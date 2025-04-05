package com.zoi.drive.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
* @since 2025-04-04
*/
@Getter
@Setter
@TableName("db_user_payment")
@Schema(name = "UserPayment", description = "")
public class UserPayment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer accountId;

    private String orderNo;

    private String tradeNo;

    private Integer planId;

    private Integer days;

    private BigDecimal amount;

    private Integer status;

    private Date createAt;

    private Date payAt;
}