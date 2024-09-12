package com.zoi.drive.entity.vo.request;

import lombok.Data;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/12 22:36
 **/
@Data
public class AuthRequestVO {
    private String account;
    private String password;
    private Boolean remember;
}
