package com.zoi.drive.controller;

import com.zoi.drive.entity.Result;
import com.zoi.drive.service.IUserFileService;
import io.minio.errors.ErrorResponseException;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/19 17:13
 **/
@Slf4j
@RestController
public class ResourceController {

    @Resource
    private IUserFileService userFileService;

    @GetMapping("/image/**")
    public void getImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Content-Type", "image/jpg");
        this.fetchImage(request, response);
    }

    private void fetchImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String imagePath = request.getServletPath().substring(7);
        ServletOutputStream outputStream = response.getOutputStream();

        if (imagePath.length() <= 1) {
            response.setStatus(404);
            outputStream.println(Result.failure(404, "Not Found").toString());
        } else {
            try {
                userFileService.fetchImageFromMinio(outputStream, imagePath);
                response.setHeader("Cache-Control", "max-age=2592000");
            } catch (ErrorResponseException e) {
                if (e.response().code() == 404) {
                    response.setStatus(404);
                    outputStream.println(Result.failure(404, "Not Found").toString());
                } else {
                    log.error("从Minio获取图片出现异常:" + e.getMessage(), e);
                }
            }
        }

    }

}
