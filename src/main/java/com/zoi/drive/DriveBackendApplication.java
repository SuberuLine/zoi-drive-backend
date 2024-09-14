package com.zoi.drive;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zoi.drive.mapper")
public class DriveBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DriveBackendApplication.class, args);
    }

}
