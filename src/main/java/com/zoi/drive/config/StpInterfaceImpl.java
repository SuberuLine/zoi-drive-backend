package com.zoi.drive.config;

import cn.dev33.satoken.stp.StpInterface;
import com.zoi.drive.entity.dto.Account;
import com.zoi.drive.service.IAccountService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/19 23:34
 **/
@Component
@Slf4j
public class StpInterfaceImpl implements StpInterface {

    @Resource
    private IAccountService accountService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return null;
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Account account = accountService.getById(Integer.parseInt(loginId.toString()));
        return account.getRole();
    }
}
