package com.zoi.drive.controller.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;
import com.zoi.drive.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/12 21:29
 **/
@Slf4j
@RestControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler(NotLoginException.class)
    public Result<String> handleNotLoginException(NotLoginException e) {
        return Result.failure(401, e.getClass().getName() + "," + e.getMessage());
    }

    @ExceptionHandler(NotPermissionException.class)
    public Result<String> handleNotPermissionException(NotPermissionException e) {
        return Result.failure(403, e.getClass().getName() + "," + e.getMessage());
    }

    @ExceptionHandler(NotRoleException.class)
    public Result<String> handleNotRoleException(NotRoleException e) {
        return Result.failure(403, e.getClass().getName() + "," + e.getMessage());
    }

    @ExceptionHandler(SaTokenException.class)
    public Result<String> handleSaTokenException(SaTokenException e) {
        return Result.failure(401, e.getClass().getName() + "," + e.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<String> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return Result.failure(405, e.getClass().getName() + "," + e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException e) {
        log.warn("Resolved [{}, {}]", e.getCause(), e.getMessage());
        return Result.failure(500, "Cause by:" + e.getCause() + "," +"message:"+e.getMessage());
    }
}
