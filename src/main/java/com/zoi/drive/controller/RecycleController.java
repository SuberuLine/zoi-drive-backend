package com.zoi.drive.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.UserDetail;
import com.zoi.drive.entity.dto.UserFile;
import com.zoi.drive.entity.dto.UserFolder;
import com.zoi.drive.entity.dto.UserRecycle;
import com.zoi.drive.entity.vo.response.UserRecycleVO;
import com.zoi.drive.service.IUserDetailService;
import com.zoi.drive.service.IUserFileService;
import com.zoi.drive.service.IUserFolderService;
import com.zoi.drive.service.IUserRecycleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recycle")
@Slf4j
public class RecycleController {

    @Resource
    private IUserRecycleService userRecycleService;

    @Resource
    private IUserFolderService userFolderService;

    @Resource
    private IUserFileService userFileService;

    @Resource
    private IUserDetailService userDetailService;

    @GetMapping("/list")
    public Result<List<UserRecycleVO>> list() {
        List<UserRecycleVO> voList = userRecycleService.list().stream()
                .map(i -> i.asViewObject(UserRecycleVO.class))
                .filter(i -> !i.getType().equals("folder_child_file"))
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    @PostMapping("/{id}/restore")
    public Result<String> restore(@PathVariable Integer id) {
        UserRecycle userRecycle = userRecycleService.getById(id);
        if (userRecycle == null) {
            return Result.failure(400, "无此文件");
        }
        if (userRecycle.getUid() != StpUtil.getLoginIdAsInt()) {
            return Result.failure(401, "您没有权限操作此文件");
        }
        if (userRecycle.getType().equals("file")) {
            UserFile target = userFileService.getById(userRecycle.getTid());
            target.setStatus(1);
            if (userFileService.updateById(target)) {
                return Result.success("已恢复文件");
            }
        }
        if (userRecycle.getType().equals("folder")) {
            // 恢复目标文件夹
            UserFolder targetFolder = userFolderService.getById(userRecycle.getTid());
            targetFolder.setStatus(1);

            // 恢复目标文件夹中的子文件
            UpdateWrapper<UserFile> updateFileWrapper = new UpdateWrapper<>();
            updateFileWrapper
                    .eq("account_id", targetFolder.getAccountId())
                    .eq("folder_id", targetFolder.getId());

            UserFile targetFile = new UserFile();
            targetFile.setStatus(1);

            if (userFolderService.updateById(targetFolder) && userFileService.update(targetFile, updateFileWrapper)) {
                return Result.success("已恢复文件夹");
            }
        }

        return Result.failure(500, "恢复失败，请联系管理员");
    }

    @DeleteMapping("/{id}/delete")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> delete(@PathVariable Integer id) {
        // 获取回收站条目
        UserRecycle userRecycle = userRecycleService.getById(id);
        if (userRecycle == null) {
            return Result.failure(404, "回收站条目不存在");
        }

        // 检查权限
        if (userRecycle.getUid() != StpUtil.getLoginIdAsInt()) {
            return Result.failure(403, "无权操作此文件");
        }

        // 获取目标 ID 和类型
        Integer targetId = userRecycle.getTid();
        String type = userRecycle.getType();

        try {
            if ("file".equals(type)) {
                // 如果是文件，逻辑删除并返还容量
                UserFile target = userFileService.getById(targetId);
                if (target != null) {
                    target.setStatus(-1); // 逻辑删除
                    userFileService.updateById(target);

                    // 返还容量
                    returnStorage(target.getSize());

                    // 删除回收站条目
                    userRecycleService.removeById(id);
                    return Result.success("成功删除文件！");
                }
            } else if ("folder".equals(type)) {
                // 如果是文件夹，逻辑删除文件夹及其子文件，并返还容量
                UserFolder target = userFolderService.getById(targetId);
                if (target != null) {
                    target.setStatus(-1); // 逻辑删除文件夹
                    userFolderService.updateById(target);

                    // 获取文件夹下的所有子文件
                    List<UserFile> folderChildList = userFileService.list(
                            new QueryWrapper<UserFile>().eq("folder_id", targetId)
                    );

                    // 计算所有子文件的总大小
                    long totalSize = folderChildList.stream()
                            .mapToLong(UserFile::getSize)
                            .sum();

                    // 返还总容量
                    returnStorage(totalSize);

                    // 逻辑删除所有子文件
                    folderChildList.forEach(i -> i.setStatus(-1));
                    userFileService.updateBatchById(folderChildList);

                    // 删除回收站条目
                    userRecycleService.removeById(id);
                    return Result.success("成功删除文件夹！");
                }
            } else {
                return Result.failure(400, "未知类型，无法删除");
            }
        } catch (Exception e) {
            log.error("删除回收站条目失败，id: {}", id, e);
            throw new RuntimeException("删除失败: " + e.getMessage());
        }

        return Result.failure(500, "删除失败，请联系管理员");
    }

    @DeleteMapping("/clear")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> clearAll() {
        int uid = StpUtil.getLoginIdAsInt();

        // 获取当前用户的所有回收站条目
        List<UserRecycle> list = userRecycleService.list(new QueryWrapper<UserRecycle>().eq("uid", uid));

        // 使用 Stream 流处理每个条目
        list.forEach(userRecycle -> {
            try {
                Integer tid = userRecycle.getTid(); // 获取 tid
                String type = userRecycle.getType(); // 获取 type

                if ("file".equals(type)) {
                    // 如果是文件，更新 userFileService 中的 status 为 -1
                    UserFile userFile = userFileService.getById(tid);
                    if (userFile != null) {
                        userFile.setStatus(-1); // 标记为已删除
                        returnStorage(userFile.getSize()); // 返还容量
                        userFileService.updateById(userFile);
                    }
                } else if ("folder".equals(type)) {
                    // 如果是文件夹，更新 userFolderService 中的 status 为 -1
                    UserFolder userFolder = userFolderService.getById(tid);
                    if (userFolder != null) {
                        userFolder.setStatus(-1); // 标记为已删除
                        userFolderService.updateById(userFolder);
                    }
                } else if ("folder_child_file".equals(type)) {
                    // 如果是文件夹的子文件，更新 userFileService 中的 status 为 -1
                    UserFile userFile = userFileService.getById(tid);
                    if (userFile != null) {
                        userFile.setStatus(-1); // 标记为已删除
                        returnStorage(userFile.getSize()); // 返还容量
                        userFileService.updateById(userFile);
                    }
                }
            } catch (Exception e) {
                log.error("处理回收站条目失败，id: {}", userRecycle.getId(), e);
                // 继续处理下一条记录
            }
        });

        // 删除该用户的所有回收站条目
        userRecycleService.remove(new QueryWrapper<UserRecycle>().eq("uid", uid));

        return Result.success("回收站已清空");
    }

    /**
     * 返还用户容量
     *
     * @param size 返还的容量大小
     * @return 是否成功
     */
    private boolean returnStorage(long size) {
        UserDetail userDetail = userDetailService.getOne(
                new QueryWrapper<UserDetail>().eq("account_id", StpUtil.getLoginIdAsInt())
        );
        if (userDetail == null) {
            throw new RuntimeException("用户详情不存在");
        }

        long currentUsedStorage = userDetail.getUsedStorage();
        if (currentUsedStorage < size) {
            throw new RuntimeException("用户已用空间不足，无法返还");
        }

        userDetail.setUsedStorage(currentUsedStorage - size);
        return userDetailService.updateById(userDetail);
    }

}
