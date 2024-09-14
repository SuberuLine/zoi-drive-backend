package com.zoi.drive.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/13 15:28
 **/

public class DevicesUtil {

    public static String getOsName(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String os = "";
        //=================OS Info=======================
        if (userAgent.toLowerCase().contains("windows")) {
            os = "Windows";
        } else if (userAgent.toLowerCase().contains("mac")) {
            os = "Mac";
        } else if (userAgent.toLowerCase().contains("x11")) {
            os = "Unix";
        } else if (userAgent.toLowerCase().contains("android")) {
            os = "Android";
        } else if (userAgent.toLowerCase().contains("iphone")) {
            os = "IPhone";
        } else {
            os = "UnKnown, More-Info: " + userAgent;
        }
        return os;
    }

}
