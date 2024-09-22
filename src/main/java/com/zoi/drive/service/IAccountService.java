package com.zoi.drive.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.Account;
import com.zoi.drive.entity.vo.request.AuthRequestVO;
import com.zoi.drive.entity.vo.request.RegisterVO;
import com.zoi.drive.entity.vo.request.ResetPasswordVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
* <p>
* 用户账号表 服务类
* </p>
*
* @author Yuzoi
* @since 2024-09-14
*/
public interface IAccountService extends IService<Account> {

    Account findAccountByNameOrEmail(String text);

    Result<String> register(RegisterVO vo, String ip);

    Account createUser(RegisterVO vo);

    Result<String> resetPassword(String email, String ip);

    Result<String> confirmReset(ResetPasswordVO vo);

    Result<SaTokenInfo> login(AuthRequestVO vo, HttpServletRequest request);

    Result<String> updateProfile(String type, @Valid String value);
}
