package com.zoi.drive.utils;

import cn.dev33.satoken.stp.StpUtil;

import java.util.UUID;

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

    // 临时下载链接有效期
    public static final String DOWNLOAD_UUID = "file:download:uuid";

    // 邮件消息队列
    public static final String MQ_MAIL_QUEUE = "mail";

    // 离线/磁力下载消息队列
    public static final String MQ_DOWNLOAD_QUEUE = "download";

    // 邮件验证码，用于存入redis
    public static final String TOKENIZED_EMAIL_DATA = "verify:email:data";

    // 记录用户操作的UUID
    public static final String OPS_UUID = UUID.randomUUID().toString().toUpperCase();

    // 根目录id
    public static final Integer FOLDER_ROOT_ID = 0;

    // 头像文件夹id，用于分类存储
    public static final int FOLDER_AVATAR_ID = -1;

    // 创建2FA缓存Secret
    public static final String TWO_FACTOR_SECRET_KEY = "two_factor:secret:";

    // 上传后暂存文件夹路径
    public static final String USER_UPLOAD_FOLDER = "main/" + StpUtil.getLoginIdAsString() + "/";

    public static final String FILE_MERGE_LOCK = "lock:file_merge:";
}

