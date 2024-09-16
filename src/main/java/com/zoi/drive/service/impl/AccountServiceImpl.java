package com.zoi.drive.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.temp.SaTempUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.Account;
import com.zoi.drive.entity.dto.UserCheckin;
import com.zoi.drive.entity.dto.UserDetail;
import com.zoi.drive.entity.vo.request.AuthRequestVO;
import com.zoi.drive.entity.vo.request.RegisterVO;
import com.zoi.drive.entity.vo.request.ResetPasswordVO;
import com.zoi.drive.mapper.AccountMapper;
import com.zoi.drive.mapper.UserCheckinMapper;
import com.zoi.drive.mapper.UserDetailMapper;
import com.zoi.drive.service.IAccountService;
import com.zoi.drive.utils.Const;
import com.zoi.drive.utils.DevicesUtil;
import com.zoi.drive.utils.FlowLimitUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户账号表 服务层实现。
 *
 * @author Yuzoi
 * @since 2024-09-12
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements IAccountService {

    @Resource
    private IAccountService accountService;

    @Resource
    private FlowLimitUtils flowLimitUtils;

    @Resource
    private AmqpTemplate amqpTemplate;

    @Resource
    private UserCheckinMapper userCheckinMapper;

    @Resource
    private UserDetailMapper userDetailMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${server.system.default-storage}")
    BigDecimal defaultStorage;

    @Override
    public Account findAccountByNameOrEmail(String text) {
        return this.query()
                .eq("username", text).or()
                .eq("email", text)
                .one();
    }

    @Override
    public Result<SaTokenInfo> login(AuthRequestVO vo, HttpServletRequest request) {
        Account currentLoginUser = accountService.findAccountByNameOrEmail(vo.getAccount());
        String os = DevicesUtil.getOsName(request);
        if (currentLoginUser != null) {
            if (BCrypt.checkpw(vo.getPassword(), currentLoginUser.getPassword())) {
                StpUtil.login(currentLoginUser.getId(), new SaLoginModel()
                        .setDevice(os)
                        .setIsLastingCookie(vo.getRemember()));
                SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
                return Result.success(tokenInfo);
            } else {
                return Result.failure(401, "密码错误");
            }
        }
        return Result.failure(400, "无此用户，请检查你的用户名或邮箱是否正确！");
    }

    @Override
    public Result<String> register(RegisterVO vo, String ip) {
        String email = vo.getEmail();
        if (this.findAccountByNameOrEmail(email) != null)
            return Result.failure(400, "邮箱已被注册");
        if (this.query().eq("username", vo.getUsername()).one() != null)
            return Result.failure(400, "用户名已被注册");
        if (!flowLimitUtils.checkBeforeLimit(Const.VERIFY_EMAIL_LIMIT + email, 5, TimeUnit.MINUTES)) {
            return Result.failure(429, "操作太频繁，请稍后再试");
        } else {
            String token = SaTempUtil.createToken(vo, 300);
            // stringRedisTemplate.opsForValue().set(Const.TOKENIZED_EMAIL_DATA, token, 300, TimeUnit.SECONDS);
            Map<String, Object> data = Map.of(
                    "email", email,
                    "type", "register",
                    "token", token
            );
            amqpTemplate.convertAndSend(Const.MQ_MAIL_QUEUE, data);
            return Result.success("已发送验证邮件，请耐心等候");
        }
    }

    @Override
    @Transactional
    public Account createUser(RegisterVO vo) {
        Account account = new Account(null, vo.getUsername(), BCrypt.hashpw(vo.getPassword(), BCrypt.gensalt())
                , null, vo.getEmail(), Const.DEFAULT_USER_ROLE, null, null, null
                , LocalDate.now(), true, null);
        try {
            if (this.save(account)) {
                UserDetail userDetail = new UserDetail(null, account.getId(), defaultStorage, BigDecimal.valueOf(0));  // 确保 id 为 null
                UserCheckin userCheckin = new UserCheckin(null, account.getId(), 0, null,
                        null, 0);

                if (userCheckinMapper.insert(userCheckin) > 0 && userDetailMapper.insert(userDetail) > 0) {
                    account.setCheckin(userCheckin.getId());
                    account.setDetails(userDetail.getId());
                    this.saveOrUpdate(account);
                    stringRedisTemplate.delete(Const.VERIFY_EMAIL_LIMIT + vo.getEmail());
                } else {
                    throw new RuntimeException("创建签到信息或用户详情时失败");
                }
            }
        } catch (Exception e) {
            // 回滚事务
            throw new RuntimeException("创建用户失败: " + e.getMessage(), e);
        }
        return account;
    }

    @Override
    public Result<String> resetPassword(String email, String ip) {
        if (!flowLimitUtils.checkBeforeLimit(Const.REGISTER_IP_LIMIE + ip, 30, TimeUnit.SECONDS)) {
            return Result.failure(429, "操作太频繁，请稍后30秒后再试");
        }
        if (this.query().eq("email", email).one() != null) {
            String token = SaTempUtil.createToken(email, 600);
            Map<String, Object> data = Map.of(
                    "email", email,
                    "type", "reset",
                    "token", token
            );
            amqpTemplate.convertAndSend(Const.MQ_MAIL_QUEUE, data);
            return Result.success("已发送重置密码验证邮件，请耐心等候");
        }
        return Result.failure(400, "查无此用户，请重新确认需找回的邮箱");
    }

    @Override
    public Result<String> confirmReset(ResetPasswordVO vo) {
        String resetEmail = (String) SaTempUtil.parseToken(vo.getToken());
        Account resetAccount = this.query().eq("email", resetEmail).one();
        if (resetAccount != null && vo.getPassword() != null) {
            resetAccount.setPassword(BCrypt.hashpw(vo.getPassword(), BCrypt.gensalt()));
            if (this.saveOrUpdate(resetAccount)) {
                return Result.success("重置密码成功");
            } else {
                return Result.failure(500, "更新密码失败，请联系管理员");
            }
        }
        return Result.failure(500, "重置密码失败");
    }
}
