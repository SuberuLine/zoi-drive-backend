package com.zoi.drive.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zoi.drive.entity.dto.UserFileShare;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件分享Mapper接口
 */
@Mapper
public interface UserFileShareMapper extends BaseMapper<UserFileShare> {
    UserFileShare selectByShareCode(String shareCode);
} 