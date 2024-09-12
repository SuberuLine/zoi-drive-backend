package com.zoi.drive.controller.exception;

import com.zoi.drive.entity.Result;
import lombok.extern.slf4j.Slf4j;
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

    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException e) {
        log.warn("Resolve [{}, {}]", e.getCause(), e.getMessage());
        return Result.failure(500, e.getMessage());
    }

}
