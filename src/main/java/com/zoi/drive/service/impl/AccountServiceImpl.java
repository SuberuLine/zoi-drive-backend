package com.zoi.drive.service.impl;


import cn.dev33.satoken.secure.BCrypt;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zoi.drive.entity.dto.Account;
import com.zoi.drive.entity.vo.request.RegisterVO;
import com.zoi.drive.mapper.AccountMapper;
import com.zoi.drive.service.IAccountService;
import com.zoi.drive.utils.Const;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * 用户账号表 服务层实现。
 *
 * @author Yuzoi
 * @since 2024-09-12
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements IAccountService {

    @Resource
    private AccountMapper accountMapper;

    @Override
    public Account findAccountByNameOrEmail(String text) {
        return this.query()
                .eq("username", text).or()
                .eq("email", text)
                .one();
    }

    @Override
    public Account registerUser(RegisterVO vo) {
        String email = vo.getEmail();
        String username = vo.getUsername();
        String password = BCrypt.hashpw(vo.getPassword(), BCrypt.gensalt());
        Account account = new Account(null, username, password, null, email,
                Const.DEFAULT_USER_ROLE, null,null, null,
                LocalDate.now(), Boolean.TRUE, null);
        if (this.save(account)) {
            return account;
        }
        return null;
    }
}
