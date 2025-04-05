package com.zoi.drive.service.impl;

import com.zoi.drive.entity.dto.UserReedem;
import com.zoi.drive.mapper.UserReedemMapper;
import com.zoi.drive.service.IUserReedemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* <p>
*  服务实现类
* </p>
*
* @author Yuzoi
* @since 2025-04-04
*/
@Service
public class UserReedemServiceImpl extends ServiceImpl<UserReedemMapper, UserReedem> implements IUserReedemService {

    @Override
    public String generateRedeemCode(Integer level, Integer days, Date expireAt) {
        return "";
    }

    @Override
    public UserReedem validateRedeemCode(String code) {
        return null;
    }

    @Override
    public boolean useRedeemCode(String code, Integer accountId) {
        return false;
    }
}
