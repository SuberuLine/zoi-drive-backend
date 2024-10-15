package com.zoi.drive.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
* <p>
* 
* </p>
*
* @author Yuzoi
* @since 2024-09-20
*/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@TableName("db_user_file_ops")
@Schema(name = "UserFileOps", description = "")
public class UserFileOps implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private Integer fileId;

    private String action;

    private Date createdAt;

    private String uuid;
}