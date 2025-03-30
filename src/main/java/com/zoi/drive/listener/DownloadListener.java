package com.zoi.drive.listener;

import com.zoi.drive.entity.dto.UserDetail;
import com.zoi.drive.entity.dto.UserDownloadTask;
import com.zoi.drive.entity.dto.UserFile;
import com.zoi.drive.mapper.UserDetailMapper;
import com.zoi.drive.mapper.UserDownloadTaskMapper;
import com.zoi.drive.mapper.UserFileMapper;
import com.zoi.drive.utils.Const;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * @Description 离线下载监听器
 * @Author Yuzoi
 * @Date 2024/10/10 17:25
 **/
@Slf4j
@Component
@RabbitListener(queues = "download")
public class DownloadListener {

    @Resource
    private UserDownloadTaskMapper userDownloadTaskMapper;

    @Resource
    private UserFileMapper userFileMapper;

    @Resource
    private UserDetailMapper userDetailMapper;

    @Resource
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;
    
    // HTML文件特征正则表达式
    private static final Pattern HTML_PATTERN = Pattern.compile("<!DOCTYPE html>|<html|<head|<body", 
            Pattern.CASE_INSENSITIVE);

    @RabbitHandler
    public void handleDownloadTask(UserDownloadTask task) {
        log.info("开始处理下载任务: {}", task.getUrl());
        
        // 更新任务状态为下载中
        task.setStatus("downloading");
        task.setStartedAt(new Date());
        userDownloadTaskMapper.updateById(task);
        
        File tempFile = null;
        
        try {
            // 创建HTTP客户端
            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
            
            // 创建基本请求头
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(task.getUrl()))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            
            // 先发送HEAD请求获取文件信息
            HttpResponse<Void> headResponse;
            try {
                headResponse = client.send(
                    requestBuilder.copy().method("HEAD", HttpRequest.BodyPublishers.noBody()).build(), 
                    HttpResponse.BodyHandlers.discarding()
                );
            } catch (Exception e) {
                log.warn("HEAD请求失败，将直接发送GET请求: {}", e.getMessage());
                headResponse = null;
            }
            
            // 提取或生成文件名和内容类型
            String fileName = task.getFileName();
            String contentType = "application/octet-stream";
            
            // 从响应头中获取文件信息
            if (headResponse != null) {
                // 尝试从Content-Disposition头获取文件名
                String contentDisposition = headResponse.headers().firstValue("Content-Disposition").orElse("");
                if (contentDisposition.contains("filename=")) {
                    String[] parts = contentDisposition.split("filename=");
                    if (parts.length > 1) {
                        String extractedName = parts[1].replaceAll("\"", "").replaceAll(";.*", "");
                        if (!extractedName.isEmpty()) {
                            fileName = extractedName;
                        }
                    }
                }
                
                // 获取内容类型
                contentType = headResponse.headers().firstValue("Content-Type").orElse(contentType);
            }
            
            // 如果文件名仍为空，尝试从URL提取
            if (fileName == null || fileName.isEmpty()) {
                try {
                    String path = new URI(task.getUrl()).getPath();
                    if (path != null && !path.isEmpty()) {
                        String[] segments = path.split("/");
                        String lastSegment = segments[segments.length - 1];
                        if (lastSegment != null && !lastSegment.isEmpty()) {
                            lastSegment = lastSegment.split("\\?")[0]; // 移除查询参数
                            fileName = URLDecoder.decode(lastSegment, StandardCharsets.UTF_8);
                        }
                    }
                } catch (Exception e) {
                    log.warn("从URL提取文件名失败: {}", e.getMessage());
                }
            }
            
            // 如果仍然无法获取文件名，使用默认名称
            if (fileName == null || fileName.isEmpty()) {
                fileName = "download_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            }
            
            // 创建存储路径
            String dateFolder = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String storagePath = "downloads/" + task.getAccountId() + "/" + dateFolder + "/" + 
                    UUID.randomUUID().toString().replaceAll("-", "") + "/" + fileName;
            
            // 更新任务中的文件名
            task.setFileName(fileName);
            userDownloadTaskMapper.updateById(task);
            
            log.info("开始下载文件: {} 到临时存储", fileName);
            
            // 创建临时文件
            tempFile = File.createTempFile("download_", "_temp");
            
            // 发送GET请求下载文件
            HttpResponse<Path> response = client.send(
                requestBuilder.GET().build(), 
                HttpResponse.BodyHandlers.ofFile(tempFile.toPath())
            );
            
            // 检查状态码
            if (response.statusCode() >= 400) {
                throw new IOException("HTTP请求失败，状态码: " + response.statusCode());
            }
            
            // 获取文件大小
            long fileSize = Files.size(tempFile.toPath());
            if (fileSize <= 0) {
                throw new IOException("下载的文件为空");
            }
            
            // 检查是否为HTML文件
            if (isHtmlFile(tempFile)) {
                log.error("下载失败：下载到的是HTML文件，可能遇到反爬虫或者错误页面");
                throw new IOException("下载到的是HTML文件，而不是预期的内容");
            }
            
            log.info("文件下载完成，大小: {}, 开始上传到MinIO", fileSize);
            
            // 上传到MinIO
            try (InputStream fileInputStream = new FileInputStream(tempFile)) {
                PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(storagePath)
                        .contentType(contentType)
                        .stream(fileInputStream, fileSize, -1) // 使用已知的文件大小
                        .build();
                
                minioClient.putObject(putObjectArgs);
                
                log.info("文件已上传到MinIO: {}", storagePath);
                
                // 更新任务状态
                task.setStatus("completed");
                task.setProgress(100);
                task.setCompletedAt(new Date());
                userDownloadTaskMapper.updateById(task);
                
                // 保存文件记录
                saveUserFile(task.getAccountId(), fileName, storagePath, fileSize, contentType);
            }
            
        } catch (Exception e) {
            log.error("下载任务失败: {}", task.getUrl(), e);
            task.setStatus("failed");
            task.setCompletedAt(new Date());
            userDownloadTaskMapper.updateById(task);
        } finally {
            // 清理临时文件
            if (tempFile != null && tempFile.exists()) {
                boolean deleted = tempFile.delete();
                if (!deleted) {
                    log.warn("无法删除临时文件: {}", tempFile.getAbsolutePath());
                    tempFile.deleteOnExit();
                }
            }
        }
    }
    
    /**
     * 检查文件是否为HTML
     */
    private boolean isHtmlFile(File file) {
        try {
            // 读取文件前8KB内容进行检查
            byte[] buffer = new byte[8192];
            try (FileInputStream fis = new FileInputStream(file)) {
                int bytesRead = fis.read(buffer);
                if (bytesRead > 0) {
                    String content = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8).trim();
                    return HTML_PATTERN.matcher(content).find();
                }
            }
        } catch (Exception e) {
            log.warn("检查HTML文件失败", e);
        }
        return false;
    }
    
    /**
     * 保存用户文件记录
     */
    private void saveUserFile(Integer accountId, String fileName, String storagePath, long fileSize, String contentType) {
        try {
            // 检查用户存储空间
            UserDetail userDetail = userDetailMapper.selectById(accountId);
            if (userDetail == null) {
                log.error("未找到用户信息: {}", accountId);
                return;
            }
            
            long usedStorage = userDetail.getUsedStorage();
            long totalStorage = userDetail.getTotalStorage();
            
            // 检查空间是否足够
            if (usedStorage + fileSize > totalStorage) {
                log.error("用户存储空间不足: 已用 {}, 总共 {}, 文件大小 {}", usedStorage, totalStorage, fileSize);
                return;
            }
            
            // 创建文件记录
            UserFile userFile = new UserFile();
            userFile.setAccountId(accountId);
            userFile.setFolderId(0); // 保存到根目录
            userFile.setFilename(fileName);
            userFile.setType(contentType);
            userFile.setSize(fileSize);
            userFile.setHash(""); // 可以在这里计算文件哈希
            userFile.setStorageUrl(storagePath);
            userFile.setIsDeleted(false);
            userFile.setUploadAt(new Date());
            userFile.setStatus(Const.FILE_NORMALCY);
            
            // 保存文件记录
            userFileMapper.insert(userFile);
            
            // 更新用户存储空间使用情况
            userDetail.setUsedStorage(usedStorage + fileSize);
            userDetailMapper.updateById(userDetail);
            
            log.info("文件记录已保存: {}, ID: {}", fileName, userFile.getId());
        } catch (Exception e) {
            log.error("保存文件记录失败", e);
        }
    }
}
