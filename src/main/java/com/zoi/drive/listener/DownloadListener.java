package com.zoi.drive.listener;

import com.zoi.drive.entity.dto.UserDownloadTask;
import com.zoi.drive.mapper.UserDownloadTaskMapper;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @Description TODO
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
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    @RabbitHandler
    public void handleDownloadTask(UserDownloadTask task) {
        log.info("received:{}", task.getFileName());

        try {
            task.setStatus("downloading");
            task.setStartedAt(new Date());
            userDownloadTaskMapper.insertOrUpdate(task);

            CompletableFuture<Boolean> downloadTask = downloadFile(task, progress -> {
                System.out.println("Progress: " + progress + "%");
            });

            downloadTask.thenAccept(success -> {
                if (success) {
                    System.out.println("离线文件成功下载到Minio");
                } else {
                    System.out.println("文件下载失败，请联系管理员");
                }
            }).exceptionally(e -> {
                System.out.println("An error occurred: " + e.getMessage());
                return null;
            });

            // 如果需要等待下载完成
            downloadTask.get(); // 阻塞，直到下载完成

        } catch (Exception e) {
            log.error("Error occurred: {}", e.getMessage());
        }
    }

    private CompletableFuture<Boolean> downloadFile(UserDownloadTask task, Consumer<Integer> progressCallback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(task.getUrl()))
                        .build();
                HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
                if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                    throw new Exception("HTTP request failed with status code: " + response.statusCode());
                }
                long totalBytes = response.headers().firstValueAsLong("Content-Length").orElse(-1);
                InputStream inputStream = response.body();
                uploadToMinioWithProgress(task, inputStream, totalBytes, progressCallback);
                return true;
            } catch (Exception e) {
                log.error("Error occurred while downloading file: {}", e.getMessage());
                return false;
            }
        });
    }

    // 上传文件流到 MinIO，并反馈进度
    private void uploadToMinioWithProgress(UserDownloadTask task, InputStream inputStream, long totalBytes,
                                           Consumer<Integer> progressCallback) throws Exception {
        long uploadedBytes = 0;
        byte[] buffer = new byte[8192];
        int bytesRead;
        int lastProgress = 0;

        try (InputStream is = inputStream) {
            // 通过流进行上传到 MinIO
            PutObjectArgs.Builder putObjectArgsBuilder = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object("temp/"+task.getAccountId()+"/"+task.getId())
                    .stream(is, totalBytes, -1);

            // 计算进度并上传
            while ((bytesRead = is.read(buffer)) != -1) {
                uploadedBytes += bytesRead;

                // 计算上传进度
                if (totalBytes > 0) {
                    int progress = (int) ((uploadedBytes * 100) / totalBytes);
                    if (progress != lastProgress) {
                        progressCallback.accept(progress); // 更新进度
                        lastProgress = progress;
                    }
                }
            }

            // 实际上传到 MinIO
            minioClient.putObject(putObjectArgsBuilder.build());
        } catch (MinioException e) {
            log.error("MinIO 上传失败: {}", e.getMessage());
            throw new Exception("上传到 MinIO 失败", e);
        }
    }
}
