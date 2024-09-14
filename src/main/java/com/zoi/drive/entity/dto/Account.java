package com.zoi.drive.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;

import com.zoi.drive.entity.BaseData;
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

    @Schema(description = "角色")
    private String role;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "两步认证")
    private String twoFactor;

    @Schema(description = "账号其他详细信息")
    private Integer details;

    @Schema(description = "注册时间")
    private LocalDate registerTime;

    @Schema(description = "帐号状态")
    private Boolean status;

    @Schema(description = "签到")
    private Integer checkin;
}