package com.zoi.drive.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zoi.drive.entity.dto.UserFileShare;
import com.zoi.drive.entity.vo.response.ShareContentVO;
import com.zoi.drive.entity.vo.request.ShareCreateRequest;
import com.zoi.drive.entity.vo.response.ShareFileVO;
import com.zoi.drive.entity.vo.request.ShareSaveRequest;
import com.zoi.drive.entity.vo.request.ShareVerifyRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 文件分享服务接口
 */
public interface IFileShareService extends IService<UserFileShare> {
    
    /**
     * 创建文件分享
     * 
     * @param accountId 用户ID
     * @param request 创建分享请求
     * @return 分享码
     */
    String createShare(Integer accountId, ShareCreateRequest request);
    
    /**
     * 验证分享密码
     * 
     * @param request 验证请求
     * @return 是否验证成功
     */
    boolean verifySharePassword(ShareVerifyRequest request);
    
    /**
     * 获取分享信息
     * 
     * @param shareCode 分享码
     * @return 分享信息
     */
    ShareFileVO getShareInfo(String shareCode);
    
    /**
     * 获取分享内容
     * 
     * @param shareCode 分享码
     * @return 分享内容
     */
    ShareContentVO getShareContent(String shareCode);
    
    /**
     * 下载分享文件
     * 
     * @param shareCode 分享码
     * @param fileId 文件ID
     * @return 文件存储URL
     */
    String downloadShareFile(String shareCode, Integer fileId);
    
    /**
     * 保存分享文件到个人网盘
     * 
     * @param accountId 当前用户ID
     * @param request 保存请求
     * @return 是否保存成功
     */
    boolean saveShareFiles(Integer accountId, ShareSaveRequest request);
    
    /**
     * 获取用户创建的分享列表
     * 
     * @param accountId 用户ID
     * @param page 分页参数
     * @return 分享列表
     */
    Page<ShareFileVO> getUserShares(Integer accountId, Page<UserFileShare> page);
    
    /**
     * 获取用户保存的分享列表
     * 
     * @param accountId 用户ID
     * @param page 分页参数
     * @return 分享列表
     */
    Page<ShareFileVO> getUserSavedShares(Integer accountId, Page<UserFileShare> page);
    
    /**
     * 取消分享
     * 
     * @param accountId 用户ID
     * @param shareId 分享ID
     * @return 是否取消成功
     */
    boolean cancelShare(Integer accountId, Integer shareId);
    
    /**
     * 延长分享有效期
     * 
     * @param accountId 用户ID
     * @param shareId 分享ID
     * @param days 延长天数
     * @return 是否延长成功
     */
    boolean extendShareExpiration(Integer accountId, Integer shareId, Integer days);

    void downloadShareFileStream(String shareCode, Integer fileId, HttpServletResponse response) throws IOException;
} 