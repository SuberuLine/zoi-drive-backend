package com.zoi.drive;

import com.zoi.drive.entity.dto.UserFile;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/10/15 17:45
 **/
@SpringBootTest
public class MinioTest {

    @Value("${minio.bucket}")
    String bucket;

    @Resource
    MinioClient minioClient;

    @Test
    public void test() {
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder().bucket(bucket).object("").build()
            );
            System.out.println(stat.size());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
