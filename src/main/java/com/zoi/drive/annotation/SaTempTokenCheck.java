package com.zoi.drive.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 临时token校验注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SaTempTokenCheck {
    /**
     * token参数名
     */
    String paramName() default "token";
    
    /**
     * 校验失败的错误信息
     */
    String errorMessage() default "临时token无效，请重新获取";
} 