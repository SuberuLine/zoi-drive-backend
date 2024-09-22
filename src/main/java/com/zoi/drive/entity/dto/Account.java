package com.zoi.drive.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.zoi.drive.entity.BaseData;
import com.zoi.drive.handler.type.StringArrayTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
* <p>
* 用户账号表
* </p>
*
* @author Yuzoi
* @since 2024-09-14
*/
@Getter
@Setter
@TableName("db_account")
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Account", description = "用户账号表")
public class Account implements Serializable, BaseData {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String username;

    private String password;

    private String phone;

    private String email;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "角色")
    @TableField(typeHandler = StringArrayTypeHandler.class)
    private List<String> role;

    @Schema(description = "帐号状态")
    private String status;

    @Schema(description = "签到")
    private Integer checkin;

    @Schema(description = "账号其他详细信息")
    private Integer details;

    @Schema(description = "用户设置")
    private Integer settings;

    @Schema(description = "注册时间")
    private LocalDate registerTime;

    @Schema(description = "是否删除")
    private Boolean isDeleted;


}