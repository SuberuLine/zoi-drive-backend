package com.zoi.drive.controller;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.Account;
import com.zoi.drive.entity.vo.request.AuthRequestVO;
import com.zoi.drive.service.AccountService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/12 22:33
 **/

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private AccountService accountService;

    @GetMapping("/check")
    public Result<SaTokenInfo> check() {
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        if (tokenInfo.isLogin){
            return Result.success(tokenInfo);
        } else return Result.failure(401, "未登录");
    }

    @PostMapping("/login")
    public Result<SaTokenInfo> login(@RequestBody AuthRequestVO vo) {
        Account currentLoginUser = accountService.findAccountByNameOrEmail(vo.getAccount());
        if (currentLoginUser != null) {
            if (BCrypt.checkpw(vo.getPassword(), currentLoginUser.getPassword())) {
                System.out.println(vo.getRemember());
                StpUtil.login(currentLoginUser.getId(), vo.getRemember());
                SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
                return Result.success(tokenInfo);
            } else {
                return Result.failure(401, "密码错误");
            }
        }
        return Result.failure(400, "无此用户，请检查你的用户名或邮箱是否正确！");
    }

    @RequestMapping("/logout")
    public Result<String> logout() {
        if (StpUtil.getTokenValue() != null) {
            StpUtil.logout();
            return Result.success("已登出");
        }
        return Result.failure(401, "无效操作：未登录时尝试登出");
    }
}
