package com.zoi.drive.listener;

import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/15 19:16
 **/
@Slf4j
@Component
@RabbitListener(queues = "mail")
public class MailQueueListener {

    @Resource
    JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    String username;

    @RabbitHandler
    public void sendMail(Map<String, Object> data) {
        String type = (String) data.get("type");
        String email = (String) data.get("email");
        String token = (String) data.get("token");
        URL url = null;
        if (type.equals("register")){
            url = getClass().getClassLoader().getResource("static/register.html");
        } else if (type.equals("reset")) {
            url = getClass().getClassLoader().getResource("static/reset.html");
        }
        String content = readString(url, Charset.defaultCharset().name());
        // 替换模板内的标记
        assert content != null;
        switch (type) {
            case "register" -> sendRegisterEmail(content.replace("{{confirmRegister}}",
                    "http://localhost:9088/api/auth/confirm-register?token=" + token), email);
            case "reset" -> sendResetEmail(content.replace("{{confirmReset}}",
                    "http://localhost:5173/reset_password?token="+token+"&email="+email), email);
            default -> log.error("未知邮件类型: {}", type);
        }
    }


    private void sendRegisterEmail(String content, String email) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        try {
            mimeMessageHelper = new MimeMessageHelper(message, true);
            mimeMessageHelper.setFrom(username);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("注册邮件");
            mimeMessageHelper.setText(content, true);
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("邮件发送失败:", e);
        }
    }

    private void sendResetEmail(String content, String email) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        try {
            mimeMessageHelper = new MimeMessageHelper(message, true);
            mimeMessageHelper.setFrom(username);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("重置密码邮件");
            mimeMessageHelper.setText(content, true);
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("邮件发送失败:", e);
        }
    }

    private String readString(URL url, String charset) {
        try {
            return new String(Files.readAllBytes(Path.of(new URL(url.toString()).toURI())), charset);
        } catch (Exception e) {
            log.error("解析待发送邮件HTML失败：", e);
        }
        return null;
    }

}
