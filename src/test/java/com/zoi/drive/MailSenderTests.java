package com.zoi.drive;

import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/15 19:09
 **/
@SpringBootTest
public class MailSenderTests {

    @Resource
    JavaMailSender sender;

    private SimpleMailMessage createMessage() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("Test");
        message.setText("Just a test");
        message.setTo("114514@1919810.com");
        message.setFrom("Admin");
        return message;
    }

    private void createMimeMessage(String content) {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        try {
            mimeMessageHelper = new MimeMessageHelper(message, true);
            mimeMessageHelper.setFrom("Yuzoi");
            mimeMessageHelper.setTo("114514@1919810.com");
            mimeMessageHelper.setSubject("注册邮件");
            mimeMessageHelper.setText(content, true);
            sender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readString(URL url, String charset) {
        try {
            return new String(Files.readAllBytes(Path.of(new URL(url.toString()).toURI())), charset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void send() {
        URL url = getClass().getClassLoader().getResource("static/register.html");
        String content = readString(url, Charset.defaultCharset().name());
        // 替换模板内的标记
        content = content.replace("{{confirmRegister}}", "http://localhost:5173");
        this.createMimeMessage(content);
    }

    @Test
    void contextLoads() {
        send();
    }

}
