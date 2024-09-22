package com.zoi.drive.entity.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/22 18:08
 **/
@Data
public class UpdateProfileVO {

    @Pattern(regexp = "^(username|email|phone)")
    String type;

    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$")
    @Length(min = 3, max = 20)
    String username;

    @Email
    String email;

    @Length(min = 11, max = 11)
    String phone;

    private String value() {
        return switch (type) {
            case "username" -> username;
            case "email" -> email;
            case "phone" -> phone;
            default -> throw new IllegalArgumentException("未知的更新类型");
        };
    }
}
