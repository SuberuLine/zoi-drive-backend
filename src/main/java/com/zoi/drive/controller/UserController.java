package com.zoi.drive.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.Account;
import com.zoi.drive.entity.vo.response.UserInfoVO;
import com.zoi.drive.service.IAccountService;
import com.zoi.drive.service.IUserCheckinService;
import com.zoi.drive.service.IUserDetailService;
import com.zoi.drive.service.IUserSettingService;
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
    private IAccountService accountService;

    @Resource
    private IUserCheckinService checkinService;

    @Resource
    private IUserDetailService userDetailService;

    @Resource
    private IUserSettingService settingService;

    @GetMapping("info")
    public Result<UserInfoVO> getUserInfo() {
        Account account = accountService.getById(StpUtil.getLoginIdAsInt());
        UserInfoVO userInfoVO = account.asViewObject(UserInfoVO.class);
        userInfoVO.setUserCheckin(checkinService.getById(account.getCheckin()));
        userInfoVO.setUserDetail(userDetailService.getById(account.getDetails()));
        userInfoVO.setUserSetting(settingService.getById(account.getSettings()));
        return Result.success(userInfoVO);
    }

    @GetMapping("checkin")
    public Result<String> checkin() {
        Account account = accountService.getById(StpUtil.getLoginIdAsInt());
        return checkinService.dailyCheckin(account);
    }

}
