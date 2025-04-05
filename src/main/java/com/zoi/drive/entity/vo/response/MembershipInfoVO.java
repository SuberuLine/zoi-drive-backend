package com.zoi.drive.entity.vo.response;

import lombok.Data;

import java.util.Date;

@Data
public class MembershipInfoVO {
    private Integer id;
    private String name;
    private String type;
    private Date startTime;
    private Date endTime;
}