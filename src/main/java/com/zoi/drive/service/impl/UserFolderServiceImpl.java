package com.zoi.drive.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.zoi.drive.entity.dto.UserFolder;
import com.zoi.drive.mapper.UserFolderMapper;
import com.zoi.drive.service.IUserFolderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* <p>
*  服务实现类
* </p>
*
* @author Yuzoi
* @since 2024-09-20
*/
@Service
public class UserFolderServiceImpl extends ServiceImpl<UserFolderMapper, UserFolder> implements IUserFolderService {

    @Override
    public List<UserFolder> listUserFolders() {
        return this.query().eq("account_id", StpUtil.getLoginIdAsInt()).list();
    }
}
