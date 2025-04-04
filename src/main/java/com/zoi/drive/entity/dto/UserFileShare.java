package com.zoi.drive.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zoi.drive.handler.type.IntegerArrayTypeHandler;
import com.zoi.drive.handler.type.StringArrayTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 文件分享表
 * </p>
 *
 * @author Yuzoi
 * @since 2024-04-02
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@TableName("db_user_file_share")
@Schema(name = "UserFileShare", description = "文件分享表")
public class UserFileShare implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "分享者ID")
    private Integer accountId;

    @Schema(description = "分享码")
    private String shareCode;

    @Schema(description = "分享的文件IDs")
    @TableField(typeHandler = IntegerArrayTypeHandler.class)
    private List<Integer> fileIds;
    
    @Schema(description = "分享的文件夹IDs")
    @TableField(typeHandler = IntegerArrayTypeHandler.class)
    private List<Integer> folderIds;

    @Schema(description = "分享标题")
    private String title;

    @Schema(description = "分享描述")
    private String description;

    @Schema(description = "分享密码，为空表示无密码")
    private String password;

    @Schema(description = "浏览次数")
    private Integer viewCount;

    @Schema(description = "下载次数")
    private Integer downloadCount;

    @Schema(description = "保存次数")
    private Integer saveCount;

    @Schema(description = "过期时间")
    private Date expireTime;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "状态：0-正常，1-已关闭")
    private Integer status;

    @TableLogic
    private Boolean isDeleted;
} 