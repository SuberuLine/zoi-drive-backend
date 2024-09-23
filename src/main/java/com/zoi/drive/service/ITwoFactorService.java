package com.zoi.drive.service;

import com.zoi.drive.entity.Result;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/24 1:33
 **/
public interface ITwoFactorService {
    Result<String> generateSecretKey();

    Result<String> confirmGenerate(String code);

    Result<String> validateCode(String code);

    Result<String> removeCode(String code);
}
