package com.zoi.drive.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.Account;
import com.zoi.drive.entity.dto.UserDownloadTask;
import com.zoi.drive.entity.dto.UserFile;
import com.zoi.drive.entity.dto.UserFolder;
import com.zoi.drive.mapper.AccountMapper;
import com.zoi.drive.mapper.UserDownloadTaskMapper;
import com.zoi.drive.mapper.UserFileMapper;
import com.zoi.drive.mapper.UserFolderMapper;
import com.zoi.drive.service.IUserFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zoi.drive.utils.Const;
import com.zoi.drive.utils.FileUtils;
import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
* <p>
*  服务实现类
* </p>
*
* @author Yuzoi
* @since 2024-09-20
*/
@Slf4j
@Service
public class UserFileServiceImpl extends ServiceImpl<UserFileMapper, UserFile> implements IUserFileService {

    @Resource
    MinioClient minioClient;

    @Resource
    AccountMapper accountMapper;

    @Resource
    private UserFolderMapper userFolderMapper;

    @Resource
    UserDownloadTaskMapper userDownloadTaskMapper;

    @Resource
    RestTemplate restTemplate;

    @Resource
    AmqpTemplate amqpTemplate;

    @Value("${minio.bucket}")
    String bucketName;

    @Value("${aria.url}")
    String ariaRpcUrl;

    @Value("${aria.secret}")
    String ariaSecret;

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void fetchImageFromMinio(ServletOutputStream outputStream, String imagePath) throws Exception {
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket(bucketName)
                .object("/image/" + imagePath)
                .build();
        GetObjectResponse response = minioClient.getObject(args);
        IOUtils.copy(response, outputStream);
    }

    @Override
    public String uploadImage(MultipartFile file) {
        return "";
    }

    @Override
    public String uploadAvatar(MultipartFile file) throws Exception {
        Integer userId = StpUtil.getLoginIdAsInt();
        String imageName = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        String type = FileUtils.getMimeType(file.getOriginalFilename());
        Date date = new Date();
        String url = "/image/avatar/" + format.format(date) + "/" + imageName;
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(url)
                .contentType(type)
                .stream(file.getInputStream(), file.getSize(), -1)
                .build();
        try {
            ObjectWriteResponse response = minioClient.putObject(args);
            if (response != null) {
               if ( this.save(new UserFile(null, userId, Const.FOLDER_AVATAR_ID, imageName, type,
                       file.getSize(), response.etag(), url, false, date, null)) ){
                   String avatar = accountMapper.selectById(userId).getAvatar();
                   // 逻辑删除上传的旧头像
                   if (avatar != null && !avatar.isEmpty()) {
                       this.update().eq("storage_url", avatar).set("is_deleted", true).update();
                   }
                   if (accountMapper.update(null, Wrappers.<Account>update()
                           .eq("id", userId)
                           .set("avatar", url)) > 0) {
                       return url;
                   }
               } else {
                   return "上传失败，存入数据库信息时发生错误";
               }
            }
        } catch (Exception e) {
            log.error("上传失败:", e);
            return null;
        }
        return null;
    }

    @Override
    public List<UserFile> listUserFiles() {
        return this.query()
                .eq("account_id", StpUtil.getLoginIdAsInt())
                .ge("folder_id", Const.FOLDER_ROOT_ID)
                .list();
    }

    @Override
    public Result<String> manualUpload(MultipartFile[] files) throws IOException {
        for (MultipartFile file : files) {
            String fileName = FileUtils.removeExtension(file.getOriginalFilename());
            String type = FileUtils.getMimeType(file.getOriginalFilename());
            Date date = new Date();
            String url = Const.USER_UPLOAD_FOLDER + fileName;
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(url)
                    .contentType(type)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .build();
            try {
                ObjectWriteResponse response = minioClient.putObject(args);
                if (response != null) {
                    UserFile userFile = new UserFile(null, StpUtil.getLoginIdAsInt(), 0, fileName, type,
                            file.getSize(), response.etag(), url, false, date,
                            null);
                    if (this.save(userFile)) {
                        return Result.success("上传文件成功！");
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                return Result.failure(500, "上传异常：" + e.getMessage());
            }
        }
        return Result.success("文件上传成功！");
    }

    @Override
    public Result<String> checkFileHash(String hash) {
        UserFile sameHashFile = this.query().eq("hash", hash).one();
        if (sameHashFile != null) {
            return Result.success(sameHashFile.getFilename());
        } else {
            return Result.success();
        }
    }

    @Override
    public Result<String> move(Integer id, Integer folderId) {
        UserFolder targetFolder = userFolderMapper.selectOne(
                new QueryWrapper<>(UserFolder.class)
                        .eq("account_id", StpUtil.getLoginIdAsInt())
                        .eq("id", folderId));
        if (targetFolder != null || Objects.equals(folderId, Const.FOLDER_ROOT_ID)) {
            return Result.success(this.update()
                    .eq("id", id).set("folder_id", folderId).update() ? "移动成功" : "移动失败");
        }
        return Result.failure(500, "目标文件夹不存在");
    }

    /**
     * 获取文件的预签名下载 URL
     * @param file File 对象
     * @return 预签名下载 URL
     * @throws Exception
     */
    @Override
    public Result<String> getPreSignedLink(UserFile file) throws Exception {
        String preSignedUrl = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(file.getStorageUrl())
                        .expiry(60) // URL 有效期（秒）
                        .build()
        );

        if (preSignedUrl == null || preSignedUrl.isEmpty()) {
            throw new RuntimeException("生成下载链接失败");
        }

        return Result.success(preSignedUrl);
    }

    @Override
    public Result<String> downloadMagnetLink(String magnetLink) {
        String jsonBody = "{\"jsonrpc\":\"2.0\",\"id\":\"qwer\",\"method\":\"aria2.addUri\",\"params\":[\"token:" +
                ariaSecret + "\",[\"" + magnetLink + "\"]]}";
        log.info(jsonBody);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(ariaRpcUrl, request, String.class);
            log.info(response.toString());
            if (response.getStatusCode() == HttpStatus.OK) {
                return Result.success("添加任务成功");
            } else {
                return Result.failure(500, "添加任务失败");
            }
        } catch (Exception e) {
            log.error("添加任务失败：", e);
            return Result.failure(500, "添加任务失败");
       }
    }

    @Override
    public Result<String> offlineDownload(String offlineDownloadLink) {

        String fileName = offlineDownloadLink.substring(offlineDownloadLink.lastIndexOf("/") + 1);
        UserDownloadTask task = new UserDownloadTask(null, StpUtil.getLoginIdAsInt(), offlineDownloadLink,
                "http", null, "pending", new Date(), null, null, 0);
        //userDownloadTaskMapper.insert(task);
        amqpTemplate.convertAndSend(Const.MQ_DOWNLOAD_QUEUE, task);
        // todo 上传minio
        return Result.success("下载成功");
    }

}
