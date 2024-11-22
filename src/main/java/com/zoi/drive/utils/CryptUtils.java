package com.zoi.drive.utils;

import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class CryptUtils {

    /**
     * 生成下载链接签名
     * @param uuid
     * @param expirationTime
     * @return
     * @throws Exception
     */
    public static String generateDLSignature(String uuid, long expirationTime) throws Exception {
        String secretKey = "qwe-123-aaa";
        String data = uuid + expirationTime;
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hmacBytes = mac.doFinal(data.getBytes());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hmacBytes);
    }

    /**
     * 判断签名是否合法
     * @param uuid
     * @param expirationTime
     * @param signature
     * @return
     * @throws Exception
     */
    public static boolean isSignatureValid(String uuid, long expirationTime, String signature) throws Exception {
        String expectedSignature = generateDLSignature(uuid, expirationTime);
        return expectedSignature.equals(signature);
    }
}
