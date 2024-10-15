package com.zoi.drive.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.zoi.drive.annotation.FileOpsLog;
import com.zoi.drive.entity.dto.UserFileOps;
import com.zoi.drive.mapper.UserFileOpsMapper;
import com.zoi.drive.utils.Const;
import jakarta.annotation.Resource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * @Description 记录文件操作的注解
 * @Author Yuzoi
 * @Date 2024/10/13 17:48
 **/
@Aspect
@Component
public class FileOpsLogAspect {

    @Resource
    private UserFileOpsMapper userFileOpsMapper;

    @AfterReturning(pointcut = "@annotation(com.zoi.drive.annotation.FileOpsLog)")
    private void saveFileOpsLog(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        FileOpsLog fileOpsLog = method.getAnnotation(FileOpsLog.class);
        String action = fileOpsLog.action();

        Object[] args = joinPoint.getArgs();

        UserFileOps userFileOps = new UserFileOps();
        userFileOps.setUserId(StpUtil.getLoginIdAsInt());
        userFileOps.setFileId((Integer) args[0]);
        userFileOps.setAction(action);
        userFileOps.setCreatedAt(new Date());
        userFileOps.setUuid(Const.OPS_UUID);
        userFileOpsMapper.insert(userFileOps);
    }
}
