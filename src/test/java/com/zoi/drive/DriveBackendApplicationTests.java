package com.zoi.drive;

import com.zoi.drive.utils.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Paths;

@SpringBootTest
class DriveBackendApplicationTests {

    @Test
    void contextLoads() {
        System.out.println(FileUtils.getMimeType(""));
    }

}
