package com.zoi.drive.annotation;

import java.lang.annotation.*;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/10/13 17:46
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FileOpsLog {
    String action();    // 文件操作
}
