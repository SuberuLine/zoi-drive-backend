package com.zoi.drive.entity.vo.response;

import lombok.Data;

import java.util.Date;

@Data
public class UserRecycleVO {
    private int id;
    private String name;
    private String type;
    private Date expiredAt;
    private Date createAt;
}
