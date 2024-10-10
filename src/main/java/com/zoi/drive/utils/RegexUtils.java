package com.zoi.drive.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/10/10 12:08
 **/
public class RegexUtils {

    // 磁力链接正则表达式
    public static final String MAGNET_REGEX = "^magnet:\\?xt=urn:btih:[a-fA-F0-9]{40}|[a-zA-Z2-7]{32}.*$";

    public static boolean isMagnet(String target) {
        return validate(MAGNET_REGEX, target);
    }

    public static boolean validate(String regex, String target) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(target);
        return matcher.matches();
    }

}
