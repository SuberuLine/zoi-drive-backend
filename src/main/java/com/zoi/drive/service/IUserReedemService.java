package com.zoi.drive.service;

import com.zoi.drive.entity.dto.UserReedem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;

/**
* <p>
*  服务类
* </p>
*
* @author Yuzoi
* @since 2025-04-04
*/
public interface IUserReedemService extends IService<UserReedem> {

    // 生成兑换码
    String generateRedeemCode(Integer level, Integer days, Date expireAt);

    // 验证兑换码
    UserReedem validateRedeemCode(String code);

    // 使用兑换码
    boolean useRedeemCode(String code, Integer accountId);

}
