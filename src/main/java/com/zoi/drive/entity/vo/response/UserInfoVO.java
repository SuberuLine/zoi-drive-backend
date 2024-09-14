package com.zoi.drive.entity.vo.response;

import com.zoi.drive.entity.dto.UserCheckin;
import com.zoi.drive.entity.dto.UserDetail;
import lombok.Data;

import java.sql.Date;
import java.time.LocalDate;

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
    private LocalDate registerTime;
    private Boolean status;
    private UserDetail userDetail;
    private UserCheckin userCheckin;
}
