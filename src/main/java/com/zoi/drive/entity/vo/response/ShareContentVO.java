package com.zoi.drive.entity.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 分享内容VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "分享内容VO")
public class ShareContentVO {
    
    @Schema(description = "分享信息")
    private ShareFileVO shareInfo;
    
    @Schema(description = "文件列表")
    private List<FileItemVO> fileList;
    
    @Schema(description = "文件夹列表")
    private List<FolderItemVO> folderList;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "文件项")
    public static class FileItemVO {
        
        @Schema(description = "文件ID")
        private Integer id;
        
        @Schema(description = "文件名")
        private String filename;
        
        @Schema(description = "文件类型")
        private String type;
        
        @Schema(description = "文件大小")
        private Long size;
        
        @Schema(description = "上传时间")
        private Date uploadAt;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "文件夹项")
    public static class FolderItemVO {
        
        @Schema(description = "文件夹ID")
        private Integer id;
        
        @Schema(description = "文件夹名称")
        private String name;
        
        @Schema(description = "创建时间")
        private Date createdAt;
    }
} 