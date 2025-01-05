package com.zoi.drive.entity.vo.response;

import com.zoi.drive.entity.dto.UserCheckin;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/13 2:21
 **/
@Data
public class UserInfoVO {
    private String username;
    private String phone;
    private String email;
    private String avatar;
    private List<String> role;
    private String status;
    private UserCheckin userCheckin;
    private UserDetailVO userDetail;
    private UserSettingVO userSetting;
    private LocalDate registerTime;
}
