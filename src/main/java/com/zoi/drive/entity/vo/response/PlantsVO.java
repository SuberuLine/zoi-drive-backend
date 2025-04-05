package com.zoi.drive.entity.vo.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PlantsVO {
    private Integer id;
    private String name;
    private String type;
    private BigDecimal price;
    private String duration;
    private List<String> features;
}
