package com.zoi.drive.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.zoi.drive.entity.dto.UserFile;
import com.zoi.drive.entity.dto.UserFileChunk;
import com.zoi.drive.mapper.UserFileChunkMapper;
import com.zoi.drive.mapper.UserFileMapper;
import com.zoi.drive.service.IUserFileChunkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zoi.drive.utils.Const;
import com.zoi.drive.utils.FileUtils;
import io.minio.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* <p>
*  服务实现类
* </p>
*
* @author Yuzoi
* @since 2024-09-20
*/
@Service
@Slf4j
public class UserFileChunkServiceImpl extends ServiceImpl<UserFileChunkMapper, UserFileChunk> implements IUserFileChunkService {

    @Resource
    MinioClient minioClient;

    @Resource
    UserFileMapper userFileMapper;

    @Value("${minio.bucket}")
    String bucketName;

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    @Transactional
    public void uploadChunk(MultipartFile file, String hash, int chunk, int chunks, Integer folderId) throws IOException {
        // 格式化分片名
        String chunkObjectName = String.format("/temp/%d/%s/chunk_%d", StpUtil.getLoginIdAsInt(), hash, chunk);

        try (InputStream inputStream = file.getInputStream()) {

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(chunkObjectName)
                            .stream(inputStream, file.getSize(), -1)
                            .build()
            );

            UserFileChunk chunkEntity = new UserFileChunk(null, StpUtil.getLoginIdAsInt(), chunk, file.getSize(), chunkObjectName,
                    "uploading", hash, new Date(), false);

            // 直接保存每个分片信息，确保数据库中有记录
            this.save(chunkEntity);

            log.debug("Chunk {} of {} uploaded successfully for hash: {}", chunk, chunks, hash);

            // 如果是最后一个分片，可以触发合并操作
            if (chunk == chunks - 1) {
                log.info("All chunks uploaded for hash: {}. Triggering merge operation.", hash);
                completeMerge(hash, file.getOriginalFilename(), file.getSize(), folderId);
            }
        } catch (Exception e) {
            log.error("Failed to upload chunk {} for hash: {}", chunk, hash, e);
            throw new IOException("上传分片失败", e);
        }
    }

    @Transactional
    public void completeMerge(String hash, String fileName, long fileSize, Integer folderId) throws Exception {
        List<UserFileChunk> chunks = this.query().eq("hash", hash).orderByAsc("chunk_number").list();

        if (chunks.isEmpty()) {
            throw new IllegalStateException("No chunks found for hash: " + hash);
        }

        String url = Const.USER_UPLOAD_FOLDER + fileName;

        // 使用并行流创建 ComposeSource
        List<ComposeSource> sources = chunks.parallelStream().map(chunk ->
                ComposeSource.builder()
                        .bucket(bucketName)
                        .object(chunk.getStorageUrl())
                        .build()
        ).collect(Collectors.toList());

        try {
            ObjectWriteResponse response = minioClient.composeObject(
                    ComposeObjectArgs.builder()
                            .bucket(bucketName)
                            .object(url)
                            .sources(sources)
                            .build()
            );

            log.info("File merged successfully. ETag: {}", response.etag());

            // 异步清理分片和更新数据库
            asyncCleanup(chunks, hash, fileName, fileSize, url, folderId);
        } catch (Exception e) {
            log.error("Error merging file chunks. Hash: {}", hash, e);
            throw new RuntimeException("Failed to merge file chunks", e);
        }

    }

    @Async
    protected void asyncCleanup(List<UserFileChunk> chunks, String hash,
                                String fileName, long fileSize, String url, Integer folderId) {
        // 并行删除分片
        chunks.parallelStream().forEach(chunk -> {
            try {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(chunk.getStorageUrl())
                                .build()
                );
            } catch (Exception e) {
                log.error("删除分片失败: {}", e.getMessage());
            }
        });

        // 批量删除分片记录
        this.removeByIds(chunks.stream().map(UserFileChunk::getId).collect(Collectors.toList()));

        // 插入文件记录
        userFileMapper.insert(new UserFile(null, StpUtil.getLoginIdAsInt(), folderId, fileName, FileUtils.getMimeType(fileName),
                fileSize, hash, url, false, new Date(), null));
    }
}
