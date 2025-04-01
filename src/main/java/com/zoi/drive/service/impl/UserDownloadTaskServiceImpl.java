package com.zoi.drive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.UserDownloadTask;
import com.zoi.drive.mapper.UserDownloadTaskMapper;
import com.zoi.drive.service.IUserDownloadTaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* <p>
*  服务实现类
* </p>
*
* @author Yuzoi
* @since 2024-10-10
*/
@Service
public class UserDownloadTaskServiceImpl extends ServiceImpl<UserDownloadTaskMapper, UserDownloadTask> implements IUserDownloadTaskService {

    @Override
    public List<UserDownloadTask> listUserDownloadTasks(Integer accountId) {
        return this.query().eq("account_id", accountId).list();
    }

    @Override
    public Result<String> clearDownloadTask(int loginIdAsInt) {
        try {
            // 方案一：高效条件删除（推荐）
            LambdaQueryWrapper<UserDownloadTask> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserDownloadTask::getAccountId, loginIdAsInt) // 确保用户权限
                    .in(UserDownloadTask::getStatus, "completed", "failed"); // 状态筛选
            int count = this.remove(wrapper) ? getRemovedCount(wrapper) : 0;

            return count > 0
                    ? Result.success("成功清理" + count + "条任务")
                    : Result.success("无符合条件的数据");
        } catch (Exception e) {
            log.error("清理任务失败", e);
            return Result.failure(400, "操作失败：" + e.getMessage());
        }
    }

    // 获取实际删除数量
    private int getRemovedCount(LambdaQueryWrapper<UserDownloadTask> wrapper) {
        return baseMapper.delete(wrapper);
    }
}
