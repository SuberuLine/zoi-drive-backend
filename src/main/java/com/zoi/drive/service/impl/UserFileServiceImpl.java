package com.zoi.drive.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.*;
import com.zoi.drive.entity.vo.response.FileCheckResponseVO;
import com.zoi.drive.mapper.*;
import com.zoi.drive.service.IUserFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zoi.drive.utils.Const;
import com.zoi.drive.utils.CryptUtils;
import com.zoi.drive.utils.FileUtils;
import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    // 分片上传文件信息的缓存
    private final ConcurrentHashMap<String, Chunks> chunkStore = new ConcurrentHashMap<>();
    // 长度为5的定长线程池，用于处理异步任务
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Resource
    private MinioClient minioClient;

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private UserDetailMapper userDetailMapper;

    @Resource
    private UserFolderMapper userFolderMapper;

    @Resource
    private UserFileOpsMapper userFileOpsMapper;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private AmqpTemplate amqpTemplate;

    @Resource
    private RedisTemplate<String, UserFile> redisTemplate;

    @Value("${server.system.endpoint}")
    String serverEndpoint;

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
                       file.getSize(), response.etag(), url, false, date, null, Const.FILE_NORMALCY)) ){
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
            String url = "main/" + StpUtil.getLoginIdAsString() + "/" + fileName;
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
                            null, Const.FILE_NORMALCY);
                    if (this.save(userFile)) {
                        // 增加对应已用空间
                        UserDetail currentUserDetail = userDetailMapper.selectById(StpUtil.getLoginIdAsInt());
                        long usedStorage = currentUserDetail.getUsedStorage();
                        long newStorageUsed = usedStorage + file.getSize();
                        if (newStorageUsed <= currentUserDetail.getTotalStorage()) {
                            currentUserDetail.setUsedStorage(newStorageUsed);
                            userDetailMapper.updateById(currentUserDetail);
                            return Result.success("上传文件成功！");
                        } else {
                            return Result.failure(403, "文件上传失败：空间不足");
                        }
                    } else {
                        return Result.failure(500, "保存失败，请联系管理员");
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                return Result.failure(500, "上传异常：" + e.getMessage());
            }
        }
        return Result.success("文件上传成功！");
    }

    /**
     * 分片上传的处理
     * @param file 分片文件
     * @param hash 分片md5值
     * @param chunk 当前分片
     * @param chunks 总分片数量
     * @param folderId 上传的文件夹id
     * @throws IOException
     */
    @Override
    @Transactional
    public void uploadChunk(MultipartFile file, String hash, int chunk, int chunks, Integer folderId) throws IOException {
        Integer userId = StpUtil.getLoginIdAsInt();
        String key = userId + "-" + hash;
        String chunkObjectName = String.format("temp/%d/%s/chunk_", StpUtil.getLoginIdAsInt(), hash);

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(chunkObjectName + chunk)
                            .stream(inputStream, file.getSize(), -1)
                            .build()
            );

            // compute原子操作，确保安全处理并发
            chunkStore.compute(key, (k, v) -> {
                // 如果为空，说明是第一个分片，创建新的Chunks对象
                if (v == null) {
                    v = new Chunks(hash, 0, chunks, chunkObjectName);
                }
                v.setCurrentChunk(v.getCurrentChunk() + 1);
                return v;
            });

            log.debug("Chunk {} of {} uploaded successfully for hash: {}", chunk, chunks, hash);

            if (chunkStore.get(key).getCurrentChunk().equals(chunkStore.get(key).getTotalChunks())) {
                log.info("All chunks uploaded for hash: {}. Triggering merge operation.", hash);
                UserFile uploadedFile = new UserFile(null, StpUtil.getLoginIdAsInt(), folderId, file.getOriginalFilename(),
                        FileUtils.getMimeType(file.getOriginalFilename()), -1, hash, null, false,
                        new Date(), null, Const.FILE_NORMALCY);
                this.updateById(uploadedFile);
                // 异步执行合并操作，避免阻塞上传过程
                executorService.submit(() -> completeMerge(userId, key, uploadedFile));
            }
        } catch (Exception e) {
            log.error("Failed to upload chunk {} for hash: {}", chunk, hash, e);
            throw new IOException("上传分片失败", e);
        }
    }

    /**
     * 上传成功后合并分片
     * @param userId 由于是异步调用，存在并发问题，需要使用用户id作为key
     * @param key 分片缓存键
     * @param uploadedFile 上传文件实体
     */
    private void completeMerge(Integer userId, String key, UserFile uploadedFile) {
        Chunks chunks = chunkStore.get(key);
        if (chunks == null) {
            log.error("No chunks found for key: {}", key);
            return;
        }

        UserDetail currentUserDetail = userDetailMapper.selectById(userId);

        if (currentUserDetail.getUsedStorage() + uploadedFile.getSize() > currentUserDetail.getTotalStorage()) {
            throw new RuntimeException("存储空间已满，终止合并文件");
        }

        String url = "main/" + userId + "/" + uploadedFile.getFilename();

        List<ComposeSource> sources = IntStream.range(0, chunks.getTotalChunks())
                .mapToObj(i -> ComposeSource.builder()
                        .bucket(bucketName)
                        .object(chunks.getStorageUrl() + i)
                        .build())
                .collect(Collectors.toList());

        try {
            ObjectWriteResponse response = minioClient.composeObject(
                    ComposeObjectArgs.builder()
                            .bucket(bucketName)
                            .object(url)
                            .sources(sources)
                            .build()
            );

            log.info("File merged successfully. ETag: {}", response.etag());

            deleteUploadedChunks(chunks);

            // 更新数据库
            uploadedFile.setSize(getObjectSize(url));
            uploadedFile.setStorageUrl(url);
            this.saveOrUpdate(uploadedFile);
            userFileOpsMapper.insert(new UserFileOps(null, userId, uploadedFile.getId(),
                    "上传文件", new Date(), Const.OPS_UUID));

            // 计算新增空间
            long usedStorage = currentUserDetail.getUsedStorage();
            long newStorageUsed = usedStorage + uploadedFile.getSize();

            // 检查存储空间
            if (newStorageUsed > currentUserDetail.getTotalStorage()) {
                // 空间不足，回退上传逻辑
                log.error("存储空间不足: 已用空间={}, 新增空间={}, 总空间={}", usedStorage, uploadedFile.getSize(), currentUserDetail.getTotalStorage());
                deleteUploadedChunks(chunks); // 删除已上传的分片文件
                this.removeById(uploadedFile.getId());
                throw new RuntimeException("存储空间不足，已回退上传逻辑");
            }

            // 增加对应已用空间
            currentUserDetail.setUsedStorage(newStorageUsed);
            userDetailMapper.updateById(currentUserDetail);

            chunkStore.remove(key);
        } catch (Exception e) {
            log.error("Error merging file chunks or updating database. Hash: {}", uploadedFile.getHash(), e);
            // 考虑实现重试机制或通知管理员
        }
    }

    /**
     * 删除已上传的分片文件
     */
    private void deleteUploadedChunks(Chunks chunks) {
        try {
            for (int i = 0; i < chunks.getTotalChunks(); i++) {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(chunks.getStorageUrl() + i)
                                .build()
                );
            }
            log.info("已删除分片文件: {}", chunks.getStorageUrl());
        } catch (Exception e) {
            log.error("删除分片文件失败: {}", e.getMessage());
        }
    }

    /**
     * 从Minio中获取文件大小
     * @param objectName 文件名
     * @return
     */
    private long getObjectSize(String objectName) {
        try {
            return minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build()).size();
        } catch (Exception e){
            log.error("获取文件大小失败: {}", e.getMessage());
            return 0;
        }
    }

    @Override
    public Result<FileCheckResponseVO> checkFileHash(Integer folderId, String hash) {
        // 获取当前登录用户的ID
        Integer currentUserId = StpUtil.getLoginIdAsInt();

        // 查找具有相同hash的文件
        UserFile existingFile = this.query().eq("hash", hash)
                .orderByAsc("id")
                .last("LIMIT 1")
                .one();

        if (existingFile != null) {
            if (existingFile.getAccountId().equals(currentUserId)) {
                // 情况1：文件存在，且上传者是当前用户
                return Result.success(new FileCheckResponseVO(true, existingFile.getFilename(), null));
            } else {
                // 情况2：文件存在，但上传者不是当前用户
                // 创建新的UserFile对象，复制存储位置信息和hash
                UserFile newFile = new UserFile();
                newFile.setAccountId(currentUserId);
                newFile.setFolderId(Objects.requireNonNullElse(folderId, 0)); // 可能需要根据实际情况调整
                newFile.setFilename(existingFile.getFilename());
                newFile.setType(existingFile.getType());
                newFile.setSize(existingFile.getSize());
                newFile.setHash(existingFile.getHash());
                newFile.setStorageUrl(existingFile.getStorageUrl());
                newFile.setUploadAt(new Date());

                // 保存新文件记录
                this.save(newFile);

                return Result.success(new FileCheckResponseVO(true, newFile.getFilename(), newFile.getId()));
            }
        } else {
            // 文件不存在
            return Result.success(new FileCheckResponseVO(false, null, null));
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
        try {
            String preSignedUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(file.getStorageUrl())
                            .expiry(60) // URL 有效期（秒）
                            .build()
            );

            if (preSignedUrl == null || preSignedUrl.isEmpty()) {
                return null;
            }

            return Result.success(preSignedUrl);
        } catch (Exception e) {
            log.error("获取预签名下载链接失败：", e);
            return null;
        }
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

    @Override
    public Result<String> renameFile(Integer fileId, String newName) {
        UserFile file = this.getById(fileId);
        if (file == null) return Result.failure(500, "文件不存在");
        if (file.getAccountId() != StpUtil.getLoginIdAsInt()) return Result.forbidden("无权限操作");
        return Result.success(this.update()
                .eq("id", fileId).set("filename", newName).update() ? "重命名成功" : "重命名失败");
    }

    @Override
    public Result<String> createDownloadLink(UserFile file) {
        try {
            String uuid = Const.OPS_UUID;
            long expireAt = System.currentTimeMillis() + 30 * 60 * 1000;
            String signature = CryptUtils.generateDLSignature(uuid, expireAt);
            String downloadUrl = UriComponentsBuilder.fromHttpUrl(serverEndpoint)
                    .path("/api/file/download")
                    .queryParam("UUID", uuid)
                    .queryParam("expireAt", expireAt)
                    .queryParam("signature", signature)
                    .toUriString();
            redisTemplate.opsForValue().set(Const.DOWNLOAD_UUID + uuid, file, 30, TimeUnit.MINUTES);
            return Result.success(downloadUrl);
        } catch (Exception e) {
            log.error("生成下载链接失败：", e);
            return Result.failure(500, "生成下载链接失败");
        }
    }

    @Override
    public void download(String uuid, String signature, String expireAt, HttpServletResponse response) {
        UserFile file = redisTemplate.opsForValue().get(Const.DOWNLOAD_UUID + uuid);
        if (file != null) {
            InputStream inputStream = null;
            try {
                if (signature == null || expireAt == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().println(Result.failure(400, "Require param missing"));
                    return;
                }

                long expirationTime = Long.parseLong(expireAt);
                if (System.currentTimeMillis() > expirationTime) {
                    response.setStatus(HttpServletResponse.SC_GONE);
                    response.getWriter().println(Result.failure(410, "Link expired"));
                    return;
                }

                if (!CryptUtils.isSignatureValid(uuid, expirationTime, signature)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().println(Result.failure(403, "Illegal signature"));
                    return;
                }

                String encodedFilename = URLEncoder.encode(file.getFilename(), StandardCharsets.UTF_8)
                        .replace("+", "%20");
                String contentDisposition = "attachment; filename*=UTF-8''" + encodedFilename;
                response.setHeader("Content-Disposition", contentDisposition);
                response.setContentType("application/octet-stream");
                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                response.setContentLengthLong(file.getSize());

                inputStream = minioClient.getObject(GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(file.getStorageUrl())
                        .build());

                IOUtils.copy(inputStream, response.getOutputStream());
                response.flushBuffer();

                file.setViewedAt(new Date());
                this.saveOrUpdate(file);
            } catch (Exception e) {
                log.error("下载文件失败：", e);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        } else {
            try {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().println(Result.failure(404, "Invalid UUID or File Not Found"));
            } catch (IOException e) {
                log.error("写入错误响应失败：", e);
            }
        }
    }

    @Override
    public Result<String> previewFile(UserFile file) {
        try {
            return Result.success(
                    minioClient.getPresignedObjectUrl(
                            GetPresignedObjectUrlArgs.builder()
                                    .method(Method.GET)
                                    .bucket(bucketName)
                                    .object(file.getStorageUrl())
                                    .expiry(12 * 60 * 60) // 12小时
                                    .extraQueryParams(new HashMap<>() {{
                                        put("response-content-type", file.getType());
                                    }})
                                    .build()
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
