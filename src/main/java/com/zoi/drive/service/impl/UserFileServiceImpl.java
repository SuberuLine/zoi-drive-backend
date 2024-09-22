package com.zoi.drive.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zoi.drive.entity.dto.Account;
import com.zoi.drive.entity.dto.UserFile;
import com.zoi.drive.mapper.AccountMapper;
import com.zoi.drive.mapper.UserFileMapper;
import com.zoi.drive.service.IUserFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zoi.drive.utils.Const;
import com.zoi.drive.utils.FileUtils;
import io.minio.*;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
* <p>
*  服务实现类
* </p>
*
* @author Yuzoi
* @since 2024-09-20
*/
@Service
public class UserFileServiceImpl extends ServiceImpl<UserFileMapper, UserFile> implements IUserFileService {
    @Resource
    MinioClient minioClient;

    @Resource
    AccountMapper accountMapper;

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void fetchImageFromMinio(ServletOutputStream outputStream, String imagePath) throws Exception {
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket("zoi-drive-system")
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
                .bucket("zoi-drive-system")
                .object(url)
                .contentType(type)
                .stream(file.getInputStream(), file.getSize(), -1)
                .build();
        try {
            ObjectWriteResponse response = minioClient.putObject(args);
            if (response != null) {
               if ( this.save(new UserFile(null, userId, Const.FOLDER_AVATAR_ID, imageName, type,
                       (double) file.getSize(), response.etag(), url, false, LocalDateTime.now(), null)) ){
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
            log.error("上传失败:{}", e);
            return null;
        }
        return null;
    }
}
