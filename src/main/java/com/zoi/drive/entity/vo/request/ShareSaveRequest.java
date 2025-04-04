package com.zoi.drive.entity.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 保存分享文件请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "保存分享文件请求")
public class ShareSaveRequest {
    
    @NotNull(message = "分享码不能为空")
    @Schema(description = "分享码")
    private String shareCode;
    
    @Schema(description = "密码，如果分享设置了密码则必填")
    private String password;
    
    @Schema(description = "要保存的文件ID列表")
    private List<Integer> fileIds;
    
    @Schema(description = "要保存的文件夹ID列表")
    private List<Integer> folderIds;
    
    @Schema(description = "目标文件夹ID，不填则保存到根目录")
    private Integer targetFolderId;
} 