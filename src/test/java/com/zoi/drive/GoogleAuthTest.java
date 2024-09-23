package com.zoi.drive;

import com.zoi.drive.utils.TOTPUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/23 18:45
 **/
@SpringBootTest
public class GoogleAuthTest {

    @Test
    public void generateSecretKey() {
        String secretKey = TOTPUtils.generateSecretKey();
        System.out.println(secretKey);
    }

    @Test
    public void generateTOTP() throws Exception {
        String secretKey = TOTPUtils.generateSecretKey();
        String totp = TOTPUtils.generateTOTP(secretKey);
        System.out.println(totp);
    }

    @Test
    public void verifyTOTP() throws Exception {
        String secretKey = TOTPUtils.generateSecretKey();
        System.out.println(secretKey);
        String totp = TOTPUtils.generateTOTP(secretKey);
        System.out.println(totp);
        boolean isValid = TOTPUtils.verifyTOTP(totp, secretKey);
        System.out.println(isValid);
        String url = TOTPUtils.generateOTPAuthURL(secretKey, "test", "zoi-drive");
        System.out.println(url);
    }

    @Test
    public void verifyCode() throws Exception {
        System.out.println(TOTPUtils.verifyTOTP("318128", "JAZPMJ6VJUHFJ5BM7MJZYJY7QCB72C7N"));
    }
}
