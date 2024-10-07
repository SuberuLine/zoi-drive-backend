package com.zoi.drive.controller;

import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.vo.response.FileItemVO;
import com.zoi.drive.service.IUserFileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/13 23:31
 **/
@RestController
@RequestMapping("/api")
@Slf4j
public class FileController {

    @Resource
    private IUserFileService userFileService;

    @PostMapping("/file/list")
    public Result<List<FileItemVO>> fileList() {
        return userFileService.listUserFiles();
    }

    @PostMapping("/file/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile[] files) throws IOException {
        for (MultipartFile file : files) {
            log.info("{} {} {}", file.getOriginalFilename(), file.getContentType(), file.getSize());
        }
        return userFileService.manualUpload(files);
    }

    @GetMapping("/file/check")
    public Result<String> check(@RequestParam("hash") String hash) {
        return userFileService.checkFileHash(hash);
    }
}
