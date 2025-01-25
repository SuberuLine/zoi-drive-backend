package com.zoi.drive.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.zoi.drive.entity.BaseData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
* <p>
* 
* </p>
*
* @author Yuzoi
* @since 2025-01-06
*/
@Getter
@Setter
@AllArgsConstructor
@TableName("db_user_recycle")
@Schema(name = "UserRecycle", description = "")
public class UserRecycle implements Serializable, BaseData {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer tid;

    private Integer uid;

    private String name;

    private String type;

    private Date expiredAt;

    private Date createAt;
}