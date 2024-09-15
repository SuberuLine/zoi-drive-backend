package com.zoi.drive;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@SpringBootApplication
@MapperScan("com.zoi.drive.mapper")
public class DriveBackendApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(DriveBackendApplication.class, args);

        try {
            Environment environment = ctx.getBean(Environment.class);
            String path = environment.getProperty("server.servlet.context-path")==null?
                    "":environment.getProperty("server.servlet.context-path");
            String port = environment.getProperty("server.port");
            String ip = InetAddress.getLocalHost().getHostAddress();
            log.info("Access URLs:\n----------------------------------------------------------\n\t"
                            + "\t\tZoi-Drive Start Success  \n\t"
                            + "前端IP: \t\thttp://127.0.0.1:5173\n\t"
                            + "服务IP: \t\thttp://127.0.0.1:{}{}\n\t"
                            + "公网IP: \t\thttp://{}:{}{}\n----------------------------------------------------------",
                    port, path, ip, port, path
            );
        } catch (UnknownHostException e) {
            log.error("启动SpringBoot失败：", e);
        }
    }

}
