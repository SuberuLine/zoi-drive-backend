package com.zoi.drive.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户保存分享文件记录表
 * </p>
 *
 * @author Yuzoi
 * @since 2024-04-02
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@TableName("db_user_share_save")
@Schema(name = "UserShareSave", description = "用户保存分享文件记录表")
public class UserShareSave implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "保存分享的用户ID")
    private Integer accountId;

    @Schema(description = "分享记录ID")
    private Integer shareId;

    @Schema(description = "目标文件夹ID")
    private Integer targetFolderId;

    @Schema(description = "保存时间")
    private Date saveTime;

    @Schema(description = "状态：0-成功，1-失败")
    private Integer status;

    @TableLogic
    private Boolean isDeleted;
} 