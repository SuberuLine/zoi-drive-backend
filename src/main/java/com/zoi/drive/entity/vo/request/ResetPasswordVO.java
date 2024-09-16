package com.zoi.drive.entity.vo.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class ResetPasswordVO {
    String token;
    @Length(min = 6, max = 20)
    String password;
}
