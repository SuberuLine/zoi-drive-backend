package com.zoi.drive.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/22 23:15
 **/
@Slf4j
public class FileUtils {

    /**
     * 通过文件字节计算文件的哈希值
     * @param file 文件
     * @return 计算后的hash
     * @throws Exception
     */
    public static String calculateFileHash(MultipartFile file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream fis = file.getInputStream()) {
            byte[] byteArray = new byte[1024];
            int bytesCount;
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
        }
        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String getMimeType(String filePath) {
        String type = null;
        try {
            type = Files.probeContentType(Paths.get(filePath));
        } catch (Exception e) {
            log.error("处理文件类型时出现异常:", e);
        }
        return type;
    }
}
