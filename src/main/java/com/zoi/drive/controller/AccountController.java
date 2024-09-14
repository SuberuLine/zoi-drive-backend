package com.zoi.drive.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.zoi.drive.entity.Result;
import com.zoi.drive.mapper.AccountMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/12 19:21
 **/
@RestController
@RequestMapping("/api/user")
public class AccountController {

    @Resource
    private AccountMapper accountMapper;

    @GetMapping("/check")
    public Result<String> check() {
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        if (tokenInfo.isLogin){
            return Result.success();
        } else return Result.failure(401, "未登录");
    }

}
