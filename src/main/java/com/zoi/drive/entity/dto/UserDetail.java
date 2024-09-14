package com.zoi.drive.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
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
@TableName("db_user_detail")
@Schema(name = "UserDetail", description = "")
public class UserDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer accountId;

    @Schema(description = "总存储空间")
    private BigDecimal totalStorage;

    @Schema(description = "已用存储空间")
    private BigDecimal usedStorage;
}