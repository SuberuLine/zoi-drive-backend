package com.zoi.drive.entity.vo.response;

import lombok.Data;

import java.sql.Date;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/13 2:21
 **/
@Data
public class UserInfoVO {
    private String username;
    private String email;
    private String phone;
    private String avatar;
    private String role;
    private String details;
    private Date registerTime;
    private Boolean status;
}
