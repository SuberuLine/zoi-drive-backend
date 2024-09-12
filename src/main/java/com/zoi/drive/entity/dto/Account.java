package com.zoi.drive.entity.dto;

import com.mybatisflex.annotation.ColumnMask;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.sql.Date;

import com.mybatisflex.core.mask.Masks;
import com.zoi.drive.entity.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 用户账号表 实体类。
 *
 * @author Yuzoi
 * @since 2024-09-12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "db_account")
public class Account implements Serializable, BaseData {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private String username;

    private String password;

    @ColumnMask(Masks.MOBILE)
    private String phone;

    @ColumnMask(Masks.EMAIL)
    private String email;

    /**
     * 角色
     */
    private String role;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 两步认证
     */
    private String twoFactor;

    /**
     * 账号其他详细信息
     */
    private Integer details;

    /**
     * 注册时间
     */
    private Date registerTime;

    /**
     * 帐号状态
     */
    private Boolean status;

}
