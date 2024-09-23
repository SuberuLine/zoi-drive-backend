package com.zoi.drive.entity.vo.response;

import lombok.Data;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/24 2:45
 **/
@Data
public class UserSettingVO {
    private Integer accountId;
    private Boolean twoFactorStatus;
}
