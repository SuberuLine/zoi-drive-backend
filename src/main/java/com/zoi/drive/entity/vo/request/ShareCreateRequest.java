package com.zoi.drive.entity.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 创建分享请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "创建分享请求")
public class ShareCreateRequest {
    
    @Schema(description = "分享的文件ID列表")
    private List<Integer> fileIds;
    
    @Schema(description = "分享的文件夹ID列表")
    private List<Integer> folderIds;
    
    @NotNull(message = "分享标题不能为空")
    @Schema(description = "分享标题")
    private String title;
    
    @Schema(description = "分享描述")
    private String description;
    
    @Schema(description = "分享密码，为空表示无密码")
    private String password;
    
    @Schema(description = "过期时间，不填表示永不过期")
    private Date expireTime;
} 