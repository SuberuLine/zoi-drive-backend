package com.zoi.drive.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.Account;
import com.zoi.drive.entity.dto.UserSetting;
import com.zoi.drive.mapper.AccountMapper;
import com.zoi.drive.mapper.UserSettingMapper;
import com.zoi.drive.service.ITwoFactorService;
import com.zoi.drive.utils.Const;
import com.zoi.drive.utils.TOTPUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/24 1:33
 **/
@Service
@Slf4j
public class TwoFactorServiceImpl implements ITwoFactorService {

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private UserSettingMapper userSettingMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${spring.application.name}")
    private String issuer;

    @Override
    public Result<String> generateSecretKey() {
        Account currentLoginUser = accountMapper.selectById(StpUtil.getLoginIdAsInt());
        UserSetting userSetting = userSettingMapper.selectById(currentLoginUser.getSettings());
        if (userSetting != null) {
            if (userSetting.getTwoFactorStatus()) {
                return Result.failure(400, "当前用户已开启两步验证");
            } else {
                String secretKey = TOTPUtils.generateSecretKey();
                // 缓存 用户设置信息表的id ： secretKey
                stringRedisTemplate.opsForValue().set(Const.TWO_FACTOR_SECRET_KEY + userSetting.getId(), secretKey,
                        5, TimeUnit.MINUTES);
                return Result.success(TOTPUtils.generateOTPAuthURL(secretKey, currentLoginUser.getEmail(), issuer));
            }
        }
        return Result.failure(500, "匹配用户设置出错，请联系管理员");
    }

    @Override
    public Result<String> confirmGenerate(String code) {
        Account currentLoginUser = accountMapper.selectById(StpUtil.getLoginIdAsInt());
        UserSetting userSetting = userSettingMapper.selectById(currentLoginUser.getSettings());
        Integer sid = userSetting.getId();
        String secret = stringRedisTemplate.opsForValue().get(Const.TWO_FACTOR_SECRET_KEY + sid);
        if (Boolean.TRUE.equals(isValidCode(code, secret))) {
            userSetting.setTwoFactorStatus(true);
            userSetting.setTwoFactorCode(secret);
            if (userSettingMapper.updateById(userSetting) > 0){
                stringRedisTemplate.delete(Const.TWO_FACTOR_SECRET_KEY + sid);
                return Result.success("创建2FA令牌成功");
            }
            return Result.failure(500, "创建2FA令牌出错，请联系管理员");
        }
        return Result.failure(400, "验证码错误");
    }


    @Override
    public Result<String> validateCode(String code) {
        Account currentLoginUser = accountMapper.selectById(StpUtil.getLoginIdAsInt());
        UserSetting userSetting = userSettingMapper.selectById(currentLoginUser.getSettings());
        if (Boolean.TRUE.equals(isValidCode(code, userSetting.getTwoFactorCode())) && userSetting.getTwoFactorStatus()) {
            return Result.success("验证成功");
        } else if (Boolean.FALSE.equals(isValidCode(code, userSetting.getTwoFactorCode()))) {
            return Result.failure(400, "验证码错误");
        } else {
            return Result.failure(500, "验证码验证出错，请联系管理员");
        }
    }

    @Override
    public Result<String> removeCode(String code) {
        Account currentLoginUser = accountMapper.selectById(StpUtil.getLoginIdAsInt());
        UserSetting userSetting = userSettingMapper.selectById(currentLoginUser.getSettings());
        if (Boolean.TRUE.equals(isValidCode(code, userSetting.getTwoFactorCode()))) {
            userSetting.setTwoFactorStatus(false);
            if (userSettingMapper.updateById(userSetting) > 0) {
                return Result.success("已关闭两步验证,现可以将令牌信息从手机APP上删除");
            } else {
                return Result.failure(500, "关闭2FA令牌出错，请联系管理员");
            }
        } else {
            return Result.failure(400, "验证码错误");
        }
    }

    private Boolean isValidCode(String code, String secretKey) {
        if (!code.isEmpty() && !secretKey.isEmpty()) {
            try {
                return TOTPUtils.verifyTOTP(code, secretKey);
            } catch (Exception e) {
                log.error("验证2FA发生异常：", e);
            }
        }
        return null;
    }
}
