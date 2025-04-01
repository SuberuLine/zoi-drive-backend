package com.zoi.drive.service;

import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.UserDownloadTask;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* <p>
*  服务类
* </p>
*
* @author Yuzoi
* @since 2024-10-10
*/
public interface IUserDownloadTaskService extends IService<UserDownloadTask> {

    List<UserDownloadTask> listUserDownloadTasks(Integer accountId);

    Result<String> clearDownloadTask(int loginIdAsInt);
}
