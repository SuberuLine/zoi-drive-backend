package com.zoi.drive.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.temp.SaTempUtil;
import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.Account;
import com.zoi.drive.entity.vo.request.AuthRequestVO;
import com.zoi.drive.entity.vo.request.RegisterVO;
import com.zoi.drive.entity.vo.request.ResetPasswordVO;
import com.zoi.drive.service.IAccountService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
    private IAccountService accountService;

    @PostMapping("/login")
    public Result<SaTokenInfo> login(@RequestBody AuthRequestVO vo, HttpServletRequest request) {
        return accountService.login(vo, request);
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody @Valid RegisterVO vo, HttpServletRequest request) {
        return accountService.register(vo, request.getRemoteHost());
    }

    @GetMapping("/confirm-register")
    public Result<String> confirmRegister(@RequestParam("token") String token) {
        RegisterVO vo = SaTempUtil.parseToken(token, RegisterVO.class);
        if (vo != null) {
            Account registerUser = accountService.createUser(vo);
            if (registerUser != null) {
                // 使用完临时token后删除
                SaTempUtil.deleteToken(token);
                return Result.success("注册成功，欢迎"+registerUser.getUsername());
            }
            return Result.failure(500, "注册失败，请联系管理员");
        }
        return Result.failure(500, "链接已过期或已使用");
    }

    @RequestMapping("/logout")
    public Result<String> logout() {
        if (StpUtil.getTokenValue() != null) {
            StpUtil.logout();
            return Result.success("已登出");
        }
        return Result.failure(401, "无效操作：未登录时尝试登出");
    }

    @GetMapping("/sendResetEmail")
    public Result<String> sendReset(@RequestParam("email") String email,
                                HttpServletRequest request){
        return accountService.resetPassword(email, request.getRemoteHost());
    }

    @PostMapping("/resetPassword")
    public Result<String> resetPassword(@RequestBody @Valid ResetPasswordVO vo) {
        return accountService.confirmReset(vo);
    }

    @DeleteMapping("/delete")
    public Result<String> deleteAccount() {
        return accountService.sendDeleteEmail();
    }

    @GetMapping("/confirm-delete")
    public Result<String> confirmDelete(@RequestParam("token") String token) {
        return accountService.deleteAccount(token);
    }

    @GetMapping("/getLoginQrCodeUrl")
    public Result<String> getLoginQrCodeUrl() {
        // 生成一个临时的登录令牌
        String loginToken = "114514";
        // 构建登录链接
        String loginUrl = "https://your-app.com/login?token=" + loginToken;
        return Result.success(loginUrl);
    }

}
