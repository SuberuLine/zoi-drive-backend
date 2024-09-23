package com.zoi.drive.controller;

import com.zoi.drive.entity.Result;
import com.zoi.drive.service.ITwoFactorService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/23 17:14
 **/
@RestController
@RequestMapping("/api/2fa")
public class TwoFactorAuthController {

    @Resource
    private ITwoFactorService twoFactorService;

    @PostMapping("/generate")
    public Result<String> generateSecretKey() {
        return twoFactorService.generateSecretKey();
    }

    @PostMapping("/confirm-generate")
    public Result<String> confirmGenerate(@RequestParam("code") String code) {
        if (code.length() != 6)
            return Result.failure(400, "验证码格式错误");
        return twoFactorService.confirmGenerate(code);
    }

    @PostMapping("/validate")
    public Result<String> validateCode(@RequestParam("code") String code) {
        if (code.length() != 6)
            return Result.failure(400, "验证码格式错误");
        return twoFactorService.validateCode(code);
    }

    @PostMapping("/remove")
    public Result<String> removeCode(@RequestParam("code") String code) {
        if (code.length() != 6)
            return Result.failure(400, "验证码格式错误");
        return twoFactorService.removeCode(code);
    }

}
