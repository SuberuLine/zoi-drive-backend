package com.zoi.drive.entity.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 分享文件展示VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "文件分享展示VO")
public class ShareFileVO {
    
    @Schema(description = "分享ID")
    private Integer id;
    
    @Schema(description = "分享码")
    private String shareCode;
    
    @Schema(description = "分享标题")
    private String title;
    
    @Schema(description = "分享描述")
    private String description;
    
    @Schema(description = "分享者用户名")
    private String username;
    
    @Schema(description = "分享者头像")
    private String avatar;
    
    @Schema(description = "是否需要密码")
    private Boolean needPassword;
    
    @Schema(description = "浏览次数")
    private Integer viewCount;
    
    @Schema(description = "下载次数")
    private Integer downloadCount;
    
    @Schema(description = "保存次数")
    private Integer saveCount;

    @Schema(description = "文件数量")
    private Integer fileCount;
    
    @Schema(description = "文件夹数量")
    private Integer folderCount;
    
    @Schema(description = "创建时间")
    private Date createTime;
    
    @Schema(description = "过期时间")
    private Date expireTime;
    
    @Schema(description = "分享状态：0-正常，1-已关闭")
    private Integer status;
} 