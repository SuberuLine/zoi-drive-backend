package com.zoi.drive.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
* <p>
* 
* </p>
*
* @author Yuzoi
* @since 2024-09-14
*/
@Getter
@Setter
@TableName("db_user_solution")
@Schema(name = "UserSolution", description = "")
public class UserSolution implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer accountId;

    @Schema(description = "客户方案")
    private String solution;

    @Schema(description = "方案开始时间")
    private Date startAt;

    @Schema(description = "方案结束时间")
    private Date endAt;
}