package com.zoi.drive.entity.vo.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PaymentStatusVO {
    private String orderNo;
    private Integer planId;
    private BigDecimal amount;
    private Integer status;
    private Date payAt;
    private String tradeNo;
}
