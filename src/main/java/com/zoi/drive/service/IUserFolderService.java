package com.zoi.drive.service;

import com.zoi.drive.entity.dto.UserFolder;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* <p>
*  服务类
* </p>
*
* @author Yuzoi
* @since 2024-09-20
*/
public interface IUserFolderService extends IService<UserFolder> {

    List<UserFolder> listUserFolders();
}
