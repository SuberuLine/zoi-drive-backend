package com.zoi.drive.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.Account;
import com.zoi.drive.entity.vo.request.RegisterVO;

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
}
