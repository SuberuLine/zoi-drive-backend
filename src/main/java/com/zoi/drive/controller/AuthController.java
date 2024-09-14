package com.zoi.drive.controller;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.Account;
import com.zoi.drive.entity.vo.request.AuthRequestVO;
import com.zoi.drive.service.IAccountService;
import com.zoi.drive.utils.DevicesUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
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

    @RequestMapping("/logout")
    public Result<String> logout() {
        if (StpUtil.getTokenValue() != null) {
            StpUtil.logout();
            return Result.success("已登出");
        }
        return Result.failure(401, "无效操作：未登录时尝试登出");
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
