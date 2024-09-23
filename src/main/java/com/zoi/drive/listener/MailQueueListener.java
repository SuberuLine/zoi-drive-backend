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
        switch (type) {
            case "register" -> url = getClass().getClassLoader().getResource("static/register.html");
            case "reset" -> url = getClass().getClassLoader().getResource("static/reset.html");
            case "delete" -> url = getClass().getClassLoader().getResource("static/delete.html");
            default -> log.error("未知邮件类型: {}", type);
        }
        String content = readString(url, Charset.defaultCharset().name());
        // 替换模板内的标记
        assert content != null;
        switch (type) {
            case "register" -> sendFormatEmail("注册邮件", content.replace("{{confirmRegister}}",
                    "http://localhost:9088/api/auth/confirm-register?token=" + token), email);
            case "reset" -> sendFormatEmail("重置密码邮件", content.replace("{{confirmReset}}",
                    "http://localhost:5173/reset_password?token="+token+"&email="+email), email);
            case "delete" -> sendFormatEmail("删除账户邮件", content.replace("{{confirmDelete}}",
                    "http://localhost:9088/api/auth/confirm-delete?token="+token), email);
            default -> log.error("未知邮件类型: {}", type);
        }
    }

    private void sendFormatEmail(String subject, String content, String email) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        try {
            mimeMessageHelper = new MimeMessageHelper(message, true);
            mimeMessageHelper.setFrom(username);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(subject);
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
