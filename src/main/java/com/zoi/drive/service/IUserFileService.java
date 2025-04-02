package com.zoi.drive.service;

import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.UserFile;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zoi.drive.entity.vo.response.FileCheckResponseVO;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
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

    void uploadChunk(MultipartFile file, String hash, int chunk, int chunks, Integer folderId) throws IOException;

    Result<FileCheckResponseVO> checkFileHash(Integer folderId, String hash);

    Result<String> move(Integer id, Integer folderId);

    Result<String> getPreSignedLink(UserFile file) throws Exception;

    Result<String> downloadMagnetLink(String magnetLink);

    Result<String> offlineDownload(String offlineDownloadLink);

    Result<String> renameFile(Integer fileId, String newName);

    Result<String> createDownloadLink(UserFile file);

    void download(String uuid, String signature, String expireAt, HttpServletResponse response);

    Result<String> previewFile(UserFile file);

    List<UserFile> listFilesByFolderId(Integer accountId, Long folderId);

    UserFile getUserFileById(Integer fileId);

    void encryptFiles(List<Integer> fileIds);

    void decryptFiles(Integer fileIds);

    List<UserFile> getRecentViewedFiles(Integer accountId, int limit);

    /**
     * 获取用户最近保存的文件
     * 
     * @param accountId 用户ID
     * @param limit 限制返回的文件数量
     * @return 最近保存的文件列表，按创建时间降序排序
     */
    List<UserFile> getRecentSavedFiles(Integer accountId, int limit);
}
