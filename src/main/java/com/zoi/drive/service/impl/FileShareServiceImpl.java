package com.zoi.drive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zoi.drive.entity.dto.Account;
import com.zoi.drive.entity.dto.UserFile;
import com.zoi.drive.entity.dto.UserFileShare;
import com.zoi.drive.entity.dto.UserFolder;
import com.zoi.drive.entity.dto.UserShareSave;
import com.zoi.drive.entity.vo.response.ShareContentVO;
import com.zoi.drive.entity.vo.request.ShareCreateRequest;
import com.zoi.drive.entity.vo.response.ShareFileVO;
import com.zoi.drive.entity.vo.request.ShareSaveRequest;
import com.zoi.drive.entity.vo.request.ShareVerifyRequest;
import com.zoi.drive.mapper.UserFileShareMapper;
import com.zoi.drive.mapper.UserShareSaveMapper;
import com.zoi.drive.service.IAccountService;
import com.zoi.drive.service.IFileShareService;
import com.zoi.drive.service.IUserFileService;
import com.zoi.drive.service.IUserFolderService;
import com.zoi.drive.utils.Const;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 文件分享服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileShareServiceImpl extends ServiceImpl<UserFileShareMapper, UserFileShare> implements IFileShareService {

    @Resource
    private UserFileShareMapper userFileShareMapper;

    @Resource
    private UserShareSaveMapper userShareSaveMapper;

    @Resource
    private IUserFileService userFileService;

    @Resource
    private IUserFolderService userFolderService;

    @Resource
    private IAccountService accountService;
    
    @Resource
    private MinioClient minioClient;
    
    @Value("${minio.bucket}")
    private String bucketName;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createShare(Integer accountId, ShareCreateRequest request) {
        // 校验文件和文件夹是否存在，且属于当前用户
        if ((request.getFileIds() == null || request.getFileIds().isEmpty()) 
                && (request.getFolderIds() == null || request.getFolderIds().isEmpty())) {
            throw new IllegalArgumentException("至少需要分享一个文件或文件夹");
        }

        // 生成分享码
        String shareCode = generateShareCode();

        // 创建分享记录
        UserFileShare userFileShare = new UserFileShare();
        userFileShare.setAccountId(accountId);
        userFileShare.setShareCode(shareCode);
        userFileShare.setFileIds(request.getFileIds());
        userFileShare.setFolderIds(request.getFolderIds());
        userFileShare.setTitle(request.getTitle());
        userFileShare.setDescription(request.getDescription());
        userFileShare.setPassword(request.getPassword());
        userFileShare.setViewCount(0);
        userFileShare.setDownloadCount(0);
        userFileShare.setSaveCount(0);
        userFileShare.setExpireTime(request.getExpireTime());
        userFileShare.setCreateTime(new Date());
        userFileShare.setUpdateTime(new Date());
        userFileShare.setStatus(0);
        userFileShare.setIsDeleted(false);

        userFileShareMapper.insert(userFileShare);

        return shareCode;
    }

    @Override
    public boolean verifySharePassword(ShareVerifyRequest request) {
        UserFileShare share = getShareByCode(request.getShareCode());
        if (share == null) {
            return false;
        }

        // 检查分享是否过期
        if (isExpired(share)) {
            return false;
        }

        // 检查密码
        if (StringUtils.hasText(share.getPassword())) {
            return Objects.equals(share.getPassword(), request.getPassword());
        }

        return true;
    }

    @Override
    public ShareFileVO getShareInfo(String shareCode) {
        UserFileShare share = getShareByCode(shareCode);
        if (share == null) {
            return null;
        }

        // 检查分享是否过期
        if (isExpired(share)) {
            return null;
        }

        // 更新查看次数
        share.setViewCount(share.getViewCount() + 1);
        userFileShareMapper.updateById(share);

        return convertToShareFileVO(share);
    }

    @Override
    public ShareContentVO getShareContent(String shareCode) {
        UserFileShare share = getShareByCode(shareCode);
        if (share == null) {
            return null;
        }

        // 检查分享是否过期
        if (isExpired(share)) {
            return null;
        }

        ShareContentVO shareContentVO = new ShareContentVO();
        shareContentVO.setShareInfo(convertToShareFileVO(share));

        // 获取文件列表
        List<ShareContentVO.FileItemVO> fileList = new ArrayList<>();
        if (share.getFileIds() != null && !share.getFileIds().isEmpty()) {
            List<UserFile> files = userFileService.listByIds(share.getFileIds());
            fileList = files.stream().map(file -> {
                ShareContentVO.FileItemVO fileItemVO = new ShareContentVO.FileItemVO();
                fileItemVO.setId(file.getId());
                fileItemVO.setFilename(file.getFilename());
                fileItemVO.setType(file.getType());
                fileItemVO.setSize(file.getSize());
                fileItemVO.setUploadAt(file.getUploadAt());
                return fileItemVO;
            }).collect(Collectors.toList());
        }
        shareContentVO.setFileList(fileList);

        // 获取文件夹列表
        List<ShareContentVO.FolderItemVO> folderList = new ArrayList<>();
        if (share.getFolderIds() != null && !share.getFolderIds().isEmpty()) {
            List<UserFolder> folders = userFolderService.listByIds(share.getFolderIds());
            folderList = folders.stream().map(folder -> {
                ShareContentVO.FolderItemVO folderItemVO = new ShareContentVO.FolderItemVO();
                folderItemVO.setId(folder.getId());
                folderItemVO.setName(folder.getName());
                folderItemVO.setCreatedAt(folder.getCreatedAt());
                return folderItemVO;
            }).collect(Collectors.toList());
        }
        shareContentVO.setFolderList(folderList);

        return shareContentVO;
    }

    @Override
    public String downloadShareFile(String shareCode, Integer fileId) {
        UserFileShare share = getShareByCode(shareCode);
        if (share == null) {
            return null;
        }

        // 检查分享是否过期
        if (isExpired(share)) {
            return null;
        }

        // 检查文件是否在分享列表中
        if (share.getFileIds() == null || !share.getFileIds().contains(fileId)) {
            return null;
        }

        // 获取文件信息
        UserFile file = userFileService.getUserFileById(fileId);
        if (file == null) {
            return null;
        }

        try {
            // 更新下载次数
            share.setDownloadCount(share.getDownloadCount() + 1);
            userFileShareMapper.updateById(share);
            
            // 生成预签名URL (60秒有效)
            String presignedUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(file.getStorageUrl())
                            .expiry(60)
                            .build());
            
            return presignedUrl;
        } catch (Exception e) {
            log.error("获取分享文件下载链接失败", e);
            return null;
        }
    }

    @Override
    public void downloadShareFileStream(String shareCode, Integer fileId, HttpServletResponse response) throws IOException {
        UserFileShare share = getShareByCode(shareCode);
        if (share == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "分享不存在");
            return;
        }

        // 检查分享是否过期
        if (isExpired(share)) {
            response.sendError(HttpServletResponse.SC_GONE, "分享已过期");
            return;
        }

        // 检查文件是否在分享列表中
        if (share.getFileIds() == null || !share.getFileIds().contains(fileId)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "文件不在分享列表中");
            return;
        }

        // 获取文件信息
        UserFile file = userFileService.getUserFileById(fileId);
        if (file == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "文件不存在");
            return;
        }

        try {
            // 更新下载次数
            share.setDownloadCount(share.getDownloadCount() + 1);
            userFileShareMapper.updateById(share);
            
            // 设置响应头
            String encodedFilename = URLEncoder.encode(file.getFilename(), StandardCharsets.UTF_8)
                    .replace("+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFilename);
            response.setContentType("application/octet-stream");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setContentLengthLong(file.getSize());
            
            // 从MinIO获取文件并写入响应
            try (InputStream is = minioClient.getObject(
                    io.minio.GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(file.getStorageUrl())
                            .build())) {
                IOUtils.copy(is, response.getOutputStream());
                response.flushBuffer();
            }
        } catch (Exception e) {
            log.error("下载分享文件失败", e);
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "下载文件失败: " + e.getMessage());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveShareFiles(Integer accountId, ShareSaveRequest request) {
        UserFileShare share = getShareByCode(request.getShareCode());
        if (share == null) {
            return false;
        }

        // 检查分享是否过期
        if (isExpired(share)) {
            return false;
        }

        // 检查密码
        if (StringUtils.hasText(share.getPassword()) && !Objects.equals(share.getPassword(), request.getPassword())) {
            return false;
        }

        // 检查文件是否在分享列表中
        if (request.getFileIds() != null && !request.getFileIds().isEmpty()) {
            for (Integer fileId : request.getFileIds()) {
                if (share.getFileIds() == null || !share.getFileIds().contains(fileId)) {
                    return false;
                }
            }
        }

        // 检查文件夹是否在分享列表中
        if (request.getFolderIds() != null && !request.getFolderIds().isEmpty()) {
            for (Integer folderId : request.getFolderIds()) {
                if (share.getFolderIds() == null || !share.getFolderIds().contains(folderId)) {
                    return false;
                }
            }
        }

        // 保存自己分享的文件
        if (Objects.equals(share.getAccountId(), accountId)) {
            return false;
        }

        UserFolder savedLocation = new UserFolder(null, accountId, Const.FOLDER_ROOT_ID, share.getTitle(), new Date(),
                false, Const.FILE_NORMALCY);

        userFolderService.save(savedLocation);


        // 复制文件和文件夹到目标文件夹
        List<Integer> shareFileIds = request.getFileIds();
        if (shareFileIds != null && !shareFileIds.isEmpty()) {
            List<UserFile> shareFileList = userFileService.listByIds(shareFileIds);
            shareFileList.forEach(shareFile -> {
                UserFile savedFile = new UserFile();
                BeanUtils.copyProperties(shareFile, savedFile);
                savedFile.setId(null);
                savedFile.setAccountId(accountId);
                savedFile.setFolderId(savedLocation.getId());
                savedFile.setUploadAt(savedLocation.getCreatedAt());
                savedFile.setViewedAt(null);
                savedFile.setIsDeleted(false);
                savedFile.setStatus(Const.FILE_NORMALCY);
                userFileService.save(savedFile);
            });
        }

        List<Integer> shareFolderIds = request.getFolderIds();
        if (shareFolderIds != null && !shareFolderIds.isEmpty()) {
            List<UserFolder> shareFolderList = userFolderService.listByIds(shareFolderIds);
            shareFolderList.forEach(shareFolder -> {
                UserFolder savedFolder = new UserFolder();
                BeanUtils.copyProperties(shareFolder, savedFolder);
                savedFolder.setId(null);
                savedFolder.setAccountId(accountId);
                savedFolder.setParentId(savedFolder.getId());
                savedFolder.setCreatedAt(savedLocation.getCreatedAt());
                savedFolder.setIsDeleted(false);
                savedFolder.setStatus(Const.FILE_NORMALCY);
                userFolderService.save(savedFolder);
            });
        }

        // 更新保存次数
        share.setSaveCount(share.getSaveCount() + 1);
        userFileShareMapper.updateById(share);

        // 记录保存操作
        UserShareSave userShareSave = new UserShareSave();
        userShareSave.setAccountId(accountId);
        userShareSave.setShareId(share.getId());
        userShareSave.setTargetFolderId(request.getTargetFolderId());
        userShareSave.setSaveTime(new Date());
        userShareSave.setStatus(0);
        userShareSave.setIsDeleted(false);
        userShareSaveMapper.insert(userShareSave);

        return true;
    }

    @Override
    public Page<ShareFileVO> getUserShares(Integer accountId, Page<UserFileShare> page) {
        // 查询用户创建的分享
        LambdaQueryWrapper<UserFileShare> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFileShare::getAccountId, accountId);
        queryWrapper.orderByDesc(UserFileShare::getCreateTime);

        Page<UserFileShare> sharePage = userFileShareMapper.selectPage(page, queryWrapper);

        // 转换为VO
        Page<ShareFileVO> result = new Page<>();
        BeanUtils.copyProperties(sharePage, result, "records");
        
        List<ShareFileVO> voList = sharePage.getRecords().stream()
                .map(share -> {
                    ShareFileVO vo = convertToShareFileVO(share);
                    
                    // 可选：进一步验证文件和文件夹是否存在
                    if (share.getFileIds() != null && !share.getFileIds().isEmpty()) {
                        List<UserFile> existingFiles = userFileService.listByIds(share.getFileIds());
                        vo.setFileCount(existingFiles.size());
                    }
                    
                    if (share.getFolderIds() != null && !share.getFolderIds().isEmpty()) {
                        List<UserFolder> existingFolders = userFolderService.listByIds(share.getFolderIds());
                        vo.setFolderCount(existingFolders.size());
                    }
                    
                    return vo;
                })
                .collect(Collectors.toList());
        
        result.setRecords(voList);
        return result;
    }

    @Override
    public Page<ShareFileVO> getUserSavedShares(Integer accountId, Page<UserFileShare> page) {
        // 查询用户保存的分享ID列表
        LambdaQueryWrapper<UserShareSave> saveQueryWrapper = new LambdaQueryWrapper<>();
        saveQueryWrapper.eq(UserShareSave::getAccountId, accountId);
        List<UserShareSave> userShareSaves = userShareSaveMapper.selectList(saveQueryWrapper);
        List<Integer> shareIds = userShareSaves.stream()
                .map(UserShareSave::getShareId)
                .collect(Collectors.toList());

        if (shareIds.isEmpty()) {
            return new Page<>();
        }

        // 查询分享信息
        LambdaQueryWrapper<UserFileShare> shareQueryWrapper = new LambdaQueryWrapper<>();
        shareQueryWrapper.in(UserFileShare::getId, shareIds);
        shareQueryWrapper.orderByDesc(UserFileShare::getCreateTime);

        Page<UserFileShare> sharePage = userFileShareMapper.selectPage(page, shareQueryWrapper);

        // 转换为VO
        Page<ShareFileVO> result = new Page<>();
        BeanUtils.copyProperties(sharePage, result, "records");
        result.setRecords(sharePage.getRecords().stream()
                .map(this::convertToShareFileVO)
                .collect(Collectors.toList()));

        return result;
    }

    @Override
    public boolean cancelShare(Integer accountId, Integer shareId) {
        UserFileShare share = userFileShareMapper.selectById(shareId);
        if (share == null || !Objects.equals(share.getAccountId(), accountId)) {
            return false;
        }

        return userFileShareMapper.deleteById(shareId) > 0;
    }

    @Override
    public boolean extendShareExpiration(Integer accountId, Integer shareId, Integer days) {
        if (days <= 0) {
            return false;
        }

        UserFileShare share = userFileShareMapper.selectById(shareId);
        if (share == null || !Objects.equals(share.getAccountId(), accountId)) {
            return false;
        }

        // 计算新的过期时间
        Date expireTime = share.getExpireTime();
        if (expireTime == null) {
            // 如果原来没有过期时间，则从当前时间开始计算
            expireTime = new Date();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(expireTime);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        share.setExpireTime(calendar.getTime());

        return userFileShareMapper.updateById(share) > 0;
    }

    /**
     * 生成唯一的分享码
     */
    private String generateShareCode() {
        // 生成一个包含时间戳和随机数的字符串
        String baseString = System.currentTimeMillis() + "_" + new Random().nextInt(10000);
        // 使用MD5生成固定长度的字符串，并截取一部分作为分享码
        String md5 = DigestUtils.md5DigestAsHex(baseString.getBytes(StandardCharsets.UTF_8));
        return md5.substring(0, 8);
    }

    /**
     * 判断分享是否过期
     */
    private boolean isExpired(UserFileShare share) {
        if (share.getExpireTime() == null) {
            return false;
        }
        return share.getExpireTime().before(new Date());
    }

    /**
     * 将分享实体转换为VO
     */
    private ShareFileVO convertToShareFileVO(UserFileShare share) {
        ShareFileVO vo = new ShareFileVO();
        vo.setId(share.getId());
        vo.setShareCode(share.getShareCode());
        vo.setTitle(share.getTitle());
        vo.setDescription(share.getDescription());
        vo.setNeedPassword(StringUtils.hasText(share.getPassword()));
        vo.setViewCount(share.getViewCount());
        vo.setDownloadCount(share.getDownloadCount());
        vo.setSaveCount(share.getSaveCount());
        vo.setCreateTime(share.getCreateTime());
        vo.setExpireTime(share.getExpireTime());
        vo.setStatus(share.getStatus());

        // 计算文件和文件夹数量
        vo.setFileCount(share.getFileIds() != null ? share.getFileIds().size() : 0);
        vo.setFolderCount(share.getFolderIds() != null ? share.getFolderIds().size() : 0);

        // 获取用户信息
        Account account = accountService.getById(share.getAccountId());
        if (account != null) {
            vo.setUsername(account.getUsername());
            vo.setAvatar(account.getAvatar());
        }

        return vo;
    }

    /**
     * 根据分享码获取分享信息
     */
    private UserFileShare getShareByCode(String shareCode) {
        return userFileShareMapper.selectByShareCode(shareCode);
    }
} 