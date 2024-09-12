package com.zoi.drive.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.zoi.drive.entity.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/test")
    public Result<String> test(){
        return Result.success("test");
    }

}
