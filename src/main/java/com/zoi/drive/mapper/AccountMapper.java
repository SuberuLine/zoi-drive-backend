package com.zoi.drive.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.mybatisflex.core.BaseMapper;
import com.zoi.drive.entity.dto.Account;

/**
 * 用户账号表 映射层。
 *
 * @author Yuzoi
 * @since 2024-09-12
 */
@Mapper
public interface AccountMapper extends BaseMapper<Account> {

}
