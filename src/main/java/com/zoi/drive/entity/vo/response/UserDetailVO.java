package com.zoi.drive.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class UserDetailVO {

    private Integer id;

    private Integer accountId;

    private BigDecimal totalStorage;

    private BigDecimal usedStorage;
}
