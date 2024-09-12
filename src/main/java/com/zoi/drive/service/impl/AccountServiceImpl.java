package com.zoi.drive.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.zoi.drive.entity.dto.Account;
import com.zoi.drive.entity.vo.request.RegisterVO;
import com.zoi.drive.mapper.AccountMapper;
import com.zoi.drive.service.AccountService;
import com.zoi.drive.utils.Const;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.sql.Date;


/**
 * 用户账号表 服务层实现。
 *
 * @author Yuzoi
 * @since 2024-09-12
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Resource
    private AccountMapper accountMapper;

    @Override
    public Account findAccountByNameOrEmail(String text) {
        QueryWrapper query = QueryWrapper.create();
        query.select().from(Account.class).where(Account::getUsername).eq(text)
                .or(Account::getEmail).eq(text);
        return accountMapper.selectOneByQuery(query);
    }

    @Override
    public Account registerUser(RegisterVO vo) {
        String email = vo.getEmail();
        String username = vo.getUsername();
        String password = BCrypt.hashpw(vo.getPassword(), BCrypt.gensalt());
        Account account = new Account(null, username, password, null, email,
                Const.DEFAULT_USER_ROLE, null,null, null,
                new Date(System.currentTimeMillis()), Boolean.TRUE);
        if (this.save(account)) {
            return account;
        }
        return null;
    }
}
