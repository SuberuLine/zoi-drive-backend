package com.zoi.drive.service;

import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.Account;
import com.zoi.drive.entity.dto.UserCheckin;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* <p>
*  服务类
* </p>
*
* @author Yuzoi
* @since 2024-09-14
*/
public interface IUserCheckinService extends IService<UserCheckin> {

    Result<String> dailyCheckin(Account account);
}
