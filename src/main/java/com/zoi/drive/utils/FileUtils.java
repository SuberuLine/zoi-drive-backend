package com.zoi.drive.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
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
        if (type == null) {
            type = "application/octet-stream";
        }
        return type;
    }

    // 获取后缀
    public static String getExtension(String filename) {
        if (filename == null || filename.isBlank() || !filename.contains(".")) {
            return null;
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    // 去除后缀
    public static String removeExtension(String filename) {
        if (filename == null || filename.isBlank() || !filename.contains(".")) {
            return null;
        }
        int lastIndex = filename.lastIndexOf('.');
        return (lastIndex == -1) ? filename : filename.substring(0, lastIndex);
    }

    // 计算etag
    public static String calculateETag(String filePath) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        try (InputStream is = new FileInputStream(filePath)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) {
                md.update(buffer, 0, read);
            }
        }
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
