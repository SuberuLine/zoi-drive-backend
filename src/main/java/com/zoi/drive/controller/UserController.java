package com.zoi.drive.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.Account;
import com.zoi.drive.entity.vo.response.UserInfoVO;
import com.zoi.drive.service.AccountService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/13 1:56
 **/
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private AccountService accountService;

    @GetMapping("info")
    public Result<UserInfoVO> getUserInfo() {
        Account account = accountService.getById(StpUtil.getLoginIdAsInt());
        return Result.success(account.asViewObject(UserInfoVO.class));
    }

}
