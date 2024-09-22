package com.zoi.drive;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.zoi.drive.entity.dto.Account;
import com.zoi.drive.mapper.AccountMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.List;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/14 4:32
 **/
@MybatisPlusTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MybatisPlusSampleTest {

    @Resource
    AccountMapper accountMapper;

    @Test
    void testInsert() {
        Account account = accountMapper.selectById(1);
        List<String> roles = account.getRole();
        System.out.println(roles);
    }
}