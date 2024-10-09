package com.zoi.drive.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.UserFolder;
import com.zoi.drive.mapper.UserFolderMapper;
import com.zoi.drive.service.IUserFolderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zoi.drive.utils.Const;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

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

    @Override
    @Transactional
    public Result<UserFolder> createFolder(Integer parentFolderId, String folderName) {
        Integer userId = StpUtil.getLoginIdAsInt();
        UserFolder newFolder = new UserFolder(null, userId, null, folderName, new Date());
        UserFolder parentFolder = this.getById(parentFolderId);
        if (parentFolder == null && Objects.equals(parentFolderId, Const.FOLDER_ROOT_ID)) {
            // 根目录的情况： 查不到父文件夹，且parentFolderId为0
            newFolder.setParentId(Const.FOLDER_ROOT_ID);
        }
        if (parentFolder != null && !Objects.equals(parentFolder.getAccountId(), userId)) {
            return Result.failure(400, "无权访问该父文件夹id");
        }
        newFolder.setParentId(parentFolderId);
        if (this.save(newFolder)) {
            return Result.success(newFolder);
        }
        return Result.failure(500, "创建失败");
    }
}
