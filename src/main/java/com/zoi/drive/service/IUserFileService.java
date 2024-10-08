package com.zoi.drive.service;

import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.UserFile;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.ServletOutputStream;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
* <p>
*  服务类
* </p>
*
* @author Yuzoi
* @since 2024-09-20
*/
public interface IUserFileService extends IService<UserFile> {
    void fetchImageFromMinio(ServletOutputStream outputStream, String imagePath) throws Exception;

    String uploadImage(MultipartFile file);

    String uploadAvatar(MultipartFile file) throws Exception;

    List<UserFile> listUserFiles();

    Result<String> manualUpload(MultipartFile[] files) throws IOException;

    Result<String> checkFileHash(String hash);
}
