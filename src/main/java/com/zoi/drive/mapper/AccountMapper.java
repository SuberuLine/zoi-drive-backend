package com.zoi.drive.mapper;

import com.zoi.drive.entity.dto.Account;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* <p>
* 用户账号表 Mapper 接口
* </p>
*
* @author Yuzoi
* @since 2024-09-14
*/
public interface AccountMapper extends BaseMapper<Account> {
    Account selectById(Integer id);
}
