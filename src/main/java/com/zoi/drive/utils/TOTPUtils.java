package com.zoi.drive.utils;

import org.bouncycastle.util.encoders.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Instant;


/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/23 18:39
 **/
public class TOTPUtils {

    private static final int TIME_STEP = 30;  // 30秒的时间窗口
    private static final int CODE_DIGITS = 6; // 6位验证码

    // 生成随机密钥并进行 Base32 编码
    public static String generateSecretKey() {
        byte[] randomBytes = new byte[20]; // 生成20字节的密钥
        new SecureRandom().nextBytes(randomBytes);
        return new String(Base32.encode(randomBytes));
    }

    // 将 Base32 编码的密钥解码为字节数组
    public static byte[] decodeSecretKey(String secretKey) {
        return Base32.decode(secretKey.getBytes());
    }

    // 生成 TOTP 码
    public static String generateTOTP(String secretKey) throws Exception {
        byte[] decodedKey = Base32.decode(secretKey);

        // 获取当前时间步进
        long currentTimeSeconds = Instant.now().getEpochSecond();
        long timeWindow = currentTimeSeconds / TIME_STEP;

        // 将时间步进转换为字节数组
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(timeWindow);

        // 使用 HMAC-SHA1 生成动态密码
        SecretKeySpec signKey = new SecretKeySpec(decodedKey, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);

        byte[] hash = mac.doFinal(buffer.array());

        // 从 hash 中提取验证码
        int offset = hash[hash.length - 1] & 0xF;
        int binary = ((hash[offset] & 0x7F) << 24)
                | ((hash[offset + 1] & 0xFF) << 16)
                | ((hash[offset + 2] & 0xFF) << 8)
                | (hash[offset + 3] & 0xFF);

        int otp = binary % (int) Math.pow(10, CODE_DIGITS);
        return String.format("%06d", otp);  // 返回6位验证码
    }

    // 验证 TOTP 码
    public static boolean verifyTOTP(String inputCode, String secretKey) throws Exception {
        String generatedCode = generateTOTP(secretKey);
        return generatedCode.equals(inputCode);
    }

    /**
     * 生成 OTP Auth URL 提供给用户绑定 TOTP 验证程序使用.
     *
     * @param secretKey the secret key used for generating TOTP codes
     * @param accountName the name of the account associated with the OTP
     * @param issuer 应用名
     * @return a formatted OTP Auth URL string
     */
    public static String generateOTPAuthURL(String secretKey, String accountName, String issuer) {
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                issuer, accountName, secretKey, issuer
        );
    }

}
