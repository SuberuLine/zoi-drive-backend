package com.zoi.drive.utils;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/12 23:12
 **/
public class Const {
    public static final String DEFAULT_USER_ROLE = "user";

    // Redis中存入邮箱验证码限制时长
    public static final String VERIFY_EMAIL_LIMIT = "verify:email:limit:";

    // ip地址注册冷却
    public static final String REGISTER_IP_LIMIE = "register:ip:limit:";

    // 邮件消息队列
    public static final String MQ_MAIL_QUEUE = "mail";

    // 邮件验证码，用于存入redis
    public static final String TOKENIZED_EMAIL_DATA = "verify:email:data";

    // 文件夹id，用于分类存储
    public static final int FOLDER_AVATAR_ID = 0;

    // 创建2FA缓存Secret
    public static final String TWO_FACTOR_SECRET_KEY = "two_factor:secret:";
}

