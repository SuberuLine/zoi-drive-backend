package com.zoi.drive.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.UserFile;
import com.zoi.drive.entity.vo.response.FileStatisticalVO;
import com.zoi.drive.entity.vo.response.RecentFileVO;
import com.zoi.drive.entity.vo.response.RecentSavedFileVO;
import com.zoi.drive.service.IUserFileService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/statistical")
public class StatisticalController {

    @Resource
    private IUserFileService userFileService;

    /**
     * 获取最近查看的文件
     * @return 最近查看的4个文件列表
     */
    @GetMapping("/recent_view")
    public Result<List<RecentFileVO>> recentView() {
        // 获取当前登录用户ID
        Integer accountId = StpUtil.getLoginIdAsInt();
        
        List<UserFile> recentFiles = userFileService.getRecentViewedFiles(accountId, 4);
        
        // 转换为VO对象
        List<RecentFileVO> recentFileVOs = recentFiles.stream()
            .map(file -> new RecentFileVO(
                file.getId(),
                file.getFilename(),
                file.getType(),
                file.getViewedAt()
            ))
            .collect(Collectors.toList());
        
        return Result.success(recentFileVOs);
    }

    /**
     * 获取当前用户的文件统计信息
     * @return 包含文件总数和各类型文件数量的统计结果
     */
    @GetMapping("/file_stats")
    public Result<FileStatisticalVO> getFileStatistics() {
        Integer accountId = StpUtil.getLoginIdAsInt();
        
        // 获取用户所有文件（不包含已删除的）
        List<UserFile> userFiles = userFileService.listFilesByFolderId(accountId, 0L);
        
        // 统计总文件数
        long totalCount = userFiles.size();
        
        // 初始化类型统计Map
        Map<String, Long> typeDistribution = new HashMap<>();
        typeDistribution.put("image", 0L);  // 图片
        typeDistribution.put("video", 0L);  // 视频
        typeDistribution.put("audio", 0L);  // 音频
        typeDistribution.put("archive", 0L); // 压缩包
        typeDistribution.put("other", 0L);  // 其他类型
        
        // 统计各类型文件数量
        for (UserFile file : userFiles) {
            String fileType = file.getType();
            if (fileType == null) continue;
            
            fileType = fileType.toLowerCase();
            
            if (fileType.startsWith("image/") || fileType.contains("jpg") || fileType.contains("jpeg") || 
                fileType.contains("png") || fileType.contains("gif") || fileType.contains("bmp")) {
                typeDistribution.put("image", typeDistribution.get("image") + 1);
            } else if (fileType.startsWith("video/") || fileType.contains("mp4") || fileType.contains("avi") || 
                      fileType.contains("mov") || fileType.contains("mkv")) {
                typeDistribution.put("video", typeDistribution.get("video") + 1);
            } else if (fileType.startsWith("audio/") || fileType.contains("mp3") || fileType.contains("wav") || 
                      fileType.contains("ogg") || fileType.contains("flac")) {
                typeDistribution.put("audio", typeDistribution.get("audio") + 1);
            } else if (fileType.contains("zip") || fileType.contains("rar") || fileType.contains("7z") || 
                      fileType.contains("tar") || fileType.contains("gz")) {
                typeDistribution.put("archive", typeDistribution.get("archive") + 1);
            } else {
                typeDistribution.put("other", typeDistribution.get("other") + 1);
            }
        }
        
        // 创建并返回响应对象
        FileStatisticalVO statisticalVO = new FileStatisticalVO(totalCount, typeDistribution);
        return Result.success(statisticalVO);
    }

    /**
     * 获取最近保存的文件
     * @return 最近保存的4个文件列表
     */
    @GetMapping("/recent_saved")
    public Result<List<RecentSavedFileVO>> recentSaved() {
        // 获取当前登录用户ID
        Integer accountId = StpUtil.getLoginIdAsInt();
        
        List<UserFile> recentFiles = userFileService.getRecentSavedFiles(accountId, 4);
        
        // 转换为VO对象
        List<RecentSavedFileVO> recentSavedFileVOs = recentFiles.stream()
            .map(file -> new RecentSavedFileVO(
                file.getId(),
                file.getFilename(),
                file.getType(),
                file.getUploadAt()
            ))
            .collect(Collectors.toList());
        
        return Result.success(recentSavedFileVOs);
    }
}
