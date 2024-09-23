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
import com.zoi.drive.entity.dto.UserSetting;
import com.zoi.drive.entity.vo.request.AuthRequestVO;
import com.zoi.drive.entity.vo.request.RegisterVO;
import com.zoi.drive.entity.vo.request.ResetPasswordVO;
import com.zoi.drive.mapper.AccountMapper;
import com.zoi.drive.mapper.UserCheckinMapper;
import com.zoi.drive.mapper.UserDetailMapper;
import com.zoi.drive.mapper.UserSettingMapper;
import com.zoi.drive.service.IAccountService;
import com.zoi.drive.utils.Const;
import com.zoi.drive.utils.DevicesUtil;
import com.zoi.drive.utils.FlowLimitUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    private FlowLimitUtils flowLimitUtils;

    @Resource
    private AmqpTemplate amqpTemplate;

    @Resource
    private UserCheckinMapper userCheckinMapper;

    @Resource
    private UserDetailMapper userDetailMapper;

    @Resource
    private UserSettingMapper userSettingMapper;

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
        Account currentLoginUser = this.findAccountByNameOrEmail(vo.getAccount());
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
    public Result<String> updateProfile(String type, String value) {
        switch (type) {
            case "username" -> {
                if (value.length() < 3 || value.length() > 20)
                    return Result.failure(400, "用户名长度必须在3-20位之间");
                if (!value.matches("^[a-zA-Z0-9\\u4e00-\\u9fa5]+$"))
                    return Result.failure(400, "用户名只能包含中文、字母、数字");
                if (this.findAccountByNameOrEmail(value) != null)
                    return Result.failure(400, "用户名已存在");
                if (this.update().set("username", value).eq("id", StpUtil.getLoginIdAsInt()).update()) {
                    return Result.success("已更新用户名为：" + value);
                } else {
                    return Result.failure(500, "更新失败");
                }
            }
            case "email" -> {
                if (!value.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$"))
                    return Result.failure(400, "邮箱格式不正确");
                if (this.findAccountByNameOrEmail(value) != null)
                    return Result.failure(400, "邮箱已存在");
                return Result.success("email");
            }
            case "phone" -> {
                if (!value.matches("^1[3-9]\\d{9}$"))
                    return Result.failure(400, "手机号格式不正确");
                if (this.query().eq("phone", value).one() != null)
                    return Result.failure(400, "手机号已被注册");
                if (this.update().set("phone", value).eq("id", StpUtil.getLoginIdAsInt()).update()) {
                    return Result.success("已更新手机号为：" + value);
                } else {
                    return Result.failure(500, "更新失败");
                }
            }
        };
        return Result.failure(400, "未知的更新类型");
    }

    @Override
    public Result<String> deleteAccount(String token) {
        Account account = SaTempUtil.parseToken(token, Account.class);
        StpUtil.kickout(account.getId());
        // 逻辑删除用户
        if (this.removeById(account)) {
            // TODO: 删除用户数据
            SaTempUtil.deleteToken(token);
            return Result.success("已删除用户");
        }
        return Result.failure(500, "删除失败");
    }

    @Override
    public Result<String> sendDeleteEmail() {
        Account account = this.getById(StpUtil.getLoginIdAsInt());
        if (account != null) {
            if (!flowLimitUtils.checkBeforeLimit(Const.VERIFY_EMAIL_LIMIT + account.getEmail() , 30,
                    TimeUnit.SECONDS)) {
                return Result.failure(429, "操作太频繁，请稍后30秒后再试");
            }
            String token = SaTempUtil.createToken(account, 600);
            Map<String, Object> data = Map.of(
                    "email", account.getEmail(),
                    "type", "delete",
                    "token", token
            );
            amqpTemplate.convertAndSend(Const.MQ_MAIL_QUEUE, data);
            return Result.success("已发送删除用户验证邮件，请耐心等候");
        }
        return Result.failure(400, "查无此用户，请重新确认需找回的邮箱");
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
        List<String> roleList = new ArrayList<>();
        roleList.add(Const.DEFAULT_USER_ROLE);
        Account account = new Account(null, vo.getUsername(), BCrypt.hashpw(vo.getPassword(), BCrypt.gensalt()),
                null, vo.getEmail(),null,  roleList, null, null, null, null,
                LocalDate.now(), false);
        try {
            if (this.save(account)) {
                // 初始化用户详情
                UserDetail userDetail = new UserDetail(null, account.getId(), defaultStorage, BigDecimal.valueOf(0));
                // 初始化用户签到信息
                UserCheckin userCheckin = new UserCheckin(null, account.getId(), 0, null,
                        null, 0);
                // 初始化用户设置
                UserSetting userSetting = new UserSetting(null, account.getId(), false, null);

                if (userCheckinMapper.insert(userCheckin) > 0 &&
                        userDetailMapper.insert(userDetail) > 0 &&
                        userSettingMapper.insert(userSetting) > 0) {
                    account.setCheckin(userCheckin.getId());
                    account.setDetails(userDetail.getId());
                    account.setSettings(userSetting.getId());
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
                SaTempUtil.deleteToken(vo.getToken());
                return Result.success("重置密码成功");
            } else {
                return Result.failure(500, "更新密码失败，请联系管理员");
            }
        }
        return Result.failure(500, "重置密码失败");
    }
}
