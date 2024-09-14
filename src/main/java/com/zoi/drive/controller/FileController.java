package com.zoi.drive.controller;

import com.zoi.drive.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/13 23:31
 **/
@RestController
@RequestMapping("/api")
@Slf4j
public class FileController {

    private final String uploadDir = "C:\\Users\\Administrator\\Desktop\\drive-web";

    @PostMapping("/user/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile[] files) {
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();  // 文件名
            File dest = new File(uploadDir + '/' + fileName);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            try {
                file.transferTo(dest);
            } catch (Exception e) {
                log.error("{}", e);
                return Result.failure(500, "文件上传失败");
            }
        }
        return Result.success("文件上传成功");
    }

}
