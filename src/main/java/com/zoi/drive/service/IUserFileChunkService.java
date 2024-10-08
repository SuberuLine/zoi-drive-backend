package com.zoi.drive.service;

import com.zoi.drive.entity.dto.UserFileChunk;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
* <p>
*  服务类
* </p>
*
* @author Yuzoi
* @since 2024-09-20
*/
public interface IUserFileChunkService extends IService<UserFileChunk> {
    void uploadChunk(MultipartFile file, String hash, int chunk, int chunks, Integer folderId) throws IOException;

}
