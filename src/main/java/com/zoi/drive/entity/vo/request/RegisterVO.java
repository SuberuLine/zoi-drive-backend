package com.zoi.drive.entity.vo.request;

import com.zoi.drive.entity.BaseData;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/12 22:41
 **/
@Data
public class RegisterVO implements Serializable, BaseData {

    @Serial
    private static final long serialVersionUID = 1L;

    @Email
    @Length(min = 5)
    String email;

    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$")
    @Length(min = 3, max = 20)
    String username;

    @Length(min = 6, max=20)
    String password;
}
