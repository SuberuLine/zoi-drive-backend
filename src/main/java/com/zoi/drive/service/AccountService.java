package com.zoi.drive.service;

import com.mybatisflex.core.service.IService;
import com.zoi.drive.entity.dto.Account;
import com.zoi.drive.entity.vo.request.RegisterVO;

/**
 * 用户账号表 服务层。
 *
 * @author Yuzoi
 * @since 2024-09-12
 */
public interface AccountService extends IService<Account> {
    Account findAccountByNameOrEmail(String text);

    Account registerUser(RegisterVO vo);
}
