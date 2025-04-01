package com.zoi.drive.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.UserDownloadTask;
import com.zoi.drive.entity.vo.response.DownloadProgressVO;
import com.zoi.drive.service.IUserDownloadTaskService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/downloadTask")
public class DownloadTaskController {

    @Resource
    private IUserDownloadTaskService userDownloadTaskService;

    @GetMapping("/progress")
    public Result<List<DownloadProgressVO>> getDownloadProgress() {
        List<UserDownloadTask> tasks = userDownloadTaskService.listUserDownloadTasks(StpUtil.getLoginIdAsInt());
        List<DownloadProgressVO> downloadProgressList = new ArrayList<>();
        tasks.forEach(task -> {
            downloadProgressList.add(task.asViewObject(DownloadProgressVO.class));
        });
        return Result.success(downloadProgressList);
    }

    @DeleteMapping("/clear")
    public Result<String> clear() {
        return userDownloadTaskService.clearDownloadTask(StpUtil.getLoginIdAsInt());
    }

}
