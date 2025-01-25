package com.zoi.drive.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.UserFile;
import com.zoi.drive.entity.dto.UserFolder;
import com.zoi.drive.entity.dto.UserRecycle;
import com.zoi.drive.mapper.UserFileMapper;
import com.zoi.drive.mapper.UserFolderMapper;
import com.zoi.drive.mapper.UserRecycleMapper;
import com.zoi.drive.service.IUserFolderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zoi.drive.utils.Const;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
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

    @Resource
    private UserFileMapper userFileMapper;

    @Resource
    private UserRecycleMapper userRecycleMapper;

    @Value("${server.system.default-recycle-expired}")
    int defaultRecycleExpired;

    @Override
    public List<UserFolder> listUserFolders() {
        return this.query().eq("account_id", StpUtil.getLoginIdAsInt()).list();
    }

    @Override
    @Transactional
    public Result<UserFolder> createFolder(Integer parentFolderId, String folderName) {
        Integer userId = StpUtil.getLoginIdAsInt();
        UserFolder newFolder = new UserFolder(null, userId, null, folderName, new Date(),
                false, Const.FILE_NORMALCY);
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

    @Override
    public boolean removeFolder(Integer folderId) {
        // 获取文件夹信息
        UserFolder folder = this.getById(folderId);
        if (folder == null) {
            throw new RuntimeException("文件夹不存在");
        }

        // 判断文件夹是否为空
        if (userFileMapper.selectCount(new QueryWrapper<>(UserFile.class).eq("folder_id", folderId)) > 0) {
            // 查询文件夹下的所有文件
            QueryWrapper<UserFile> userFileQueryWrapper = new QueryWrapper<>(UserFile.class)
                    .eq("folder_id", folderId);
            List<UserFile> userFiles = userFileMapper.selectList(userFileQueryWrapper);

            // 遍历文件，添加到回收站
            for (UserFile userFile : userFiles) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, defaultRecycleExpired);
                userRecycleMapper.insert(new UserRecycle(
                        null,
                        userFile.getId(),
                        userFile.getAccountId(),
                        userFile.getFilename(),
                        "folder_child_file",
                        calendar.getTime(),
                        new Date()
                ));
            }

            // 将文件夹下的所有文件的 status 设置为 0（逻辑删除）
            UserFile updateEntity = new UserFile();
            updateEntity.setStatus(0); // 假设 status 字段表示逻辑删除状态

            QueryWrapper<UserFile> updateWrapper = new QueryWrapper<>(UserFile.class)
                    .eq("folder_id", folderId);

            userFileMapper.update(updateEntity, updateWrapper);
        }

        // 将文件夹本身添加到回收站
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, defaultRecycleExpired);
        userRecycleMapper.insert(new UserRecycle(
                null,
                folderId,
                StpUtil.getLoginIdAsInt(),
                folder.getName(),
                "folder",
                calendar.getTime(),
                new Date()
        ));

        // 删除文件夹（逻辑删除或物理删除）
        return this.removeById(folderId);
    }
}
