package com.zoi.drive.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.zoi.drive.entity.Result;
import com.zoi.drive.service.IAccountService;
import com.zoi.drive.service.IUserFileService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/12 19:21
 **/
@RestController
@Slf4j
@RequestMapping("/api/user")
public class AccountController {

    @Resource
    private IAccountService accountService;

    @Resource
    private IUserFileService userFileService;

    @GetMapping("/check")
    @SaCheckRole("user")
    public Result<String> check() {
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        if (tokenInfo.isLogin){
            return Result.success();
        } else return Result.failure(401, "未登录");
    }

    @PostMapping("/update-profile")
    public Result<String> updateProfile(@RequestParam("type") String type,
                                        @RequestParam("value") @Valid String value) {
        return accountService.updateProfile(type, value);
    }

    @PostMapping("/upload-image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file,
                                        HttpServletResponse response) throws IOException {
        if (file.getSize() > 1024 * 1024 * 5)
            return Result.failure(400, "图片大小不能超过5MB");
        log.info("正在进行图片上传操作...");
        String url = userFileService.uploadImage(file);
        if (url != null) {
            log.info("图片上传成功，大小：" + file.getSize());
            return Result.success(url);
        } else {
            response.setStatus(400);
            return Result.failure(400, "图片上传失败，请联系管理员");
        }
    }

    @PostMapping("/upload-avatar")
    public Result<String> uploadAvatar(@RequestParam("avatar") MultipartFile file) throws Exception {
        if (file.getSize() > 1024 * 1024 * 5)
            return Result.failure(400, "头像大小不能超过5MB");
        log.info("正在进行头像上传操作...");
        String url = userFileService.uploadAvatar(file);
        if (url != null) {
            log.info("头像上传成功，大小：{}", file.getSize());
            return Result.success(url);
        } else {
            return Result.failure(400, "头像上传失败，请联系管理员");
        }
    }

}
