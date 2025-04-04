package com.zoi.drive.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.UserFileShare;
import com.zoi.drive.entity.vo.response.ShareContentVO;
import com.zoi.drive.entity.vo.request.ShareCreateRequest;
import com.zoi.drive.entity.vo.response.ShareFileVO;
import com.zoi.drive.entity.vo.request.ShareSaveRequest;
import com.zoi.drive.entity.vo.request.ShareVerifyRequest;
import com.zoi.drive.service.IFileShareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 文件分享控制器
 */
@Slf4j
@Tag(name = "文件分享", description = "文件分享相关接口")
@RestController
@RequestMapping("/api/share")
@RequiredArgsConstructor
@Validated
public class FileShareController {

    @Resource
    private IFileShareService fileShareService;

    @Operation(summary = "创建文件分享")
    @PostMapping("/create")
    @SaCheckLogin
    public Result<String> createShare(@RequestBody @Valid ShareCreateRequest request) {
        try {
            Integer accountId = StpUtil.getLoginIdAsInt();
            String shareCode = fileShareService.createShare(accountId, request);
            return Result.success(shareCode);
        } catch (Exception e) {
            log.error("创建分享失败", e);
            return Result.failure(400, "创建分享失败: " + e.getMessage());
        }
    }

    @Operation(summary = "验证分享密码")
    @PostMapping("/verify")
    public Result<Boolean> verifySharePassword(@RequestBody @Valid ShareVerifyRequest request) {
        try {
            boolean verified = fileShareService.verifySharePassword(request);
            return Result.success(verified);
        } catch (Exception e) {
            log.error("验证分享密码失败", e);
            return Result.failure(400, "验证分享密码失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取分享信息")
    @GetMapping("/info/{shareCode}")
    public Result<ShareFileVO> getShareInfo(@PathVariable String shareCode) {
        try {
            ShareFileVO shareInfo = fileShareService.getShareInfo(shareCode);
            if (shareInfo == null) {
                return Result.failure(400, "分享不存在或已失效");
            }
            return Result.success(shareInfo);
        } catch (Exception e) {
            log.error("获取分享信息失败", e);
            return Result.failure(400, "获取分享信息失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取分享内容")
    @GetMapping("/content/{shareCode}")
    public Result<ShareContentVO> getShareContent(
            @PathVariable String shareCode,
            @RequestParam(required = false) String password) {
        try {
            // 如果有密码，先验证密码
            if (password != null) {
                ShareVerifyRequest verifyRequest = new ShareVerifyRequest(shareCode, password);
                boolean verified = fileShareService.verifySharePassword(verifyRequest);
                if (!verified) {
                    return Result.failure(400, "密码错误");
                }
            }

            ShareContentVO shareContent = fileShareService.getShareContent(shareCode);
            if (shareContent == null) {
                return Result.failure(400, "分享不存在或已失效");
            }
            return Result.success(shareContent);
        } catch (Exception e) {
            log.error("获取分享内容失败", e);
            return Result.failure(400, "获取分享内容失败: " + e.getMessage());
        }
    }

    @Operation(summary = "下载分享文件")
    @GetMapping("/download/{shareCode}/{fileId}")
    public void downloadShareFile(
            @PathVariable String shareCode,
            @PathVariable Integer fileId,
            @RequestParam(required = false) String password,
            HttpServletResponse response) throws IOException {
        try {
            // 如果有密码，先验证密码
            if (password != null) {
                ShareVerifyRequest verifyRequest = new ShareVerifyRequest(shareCode, password);
                boolean verified = fileShareService.verifySharePassword(verifyRequest);
                if (!verified) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "密码错误");
                    return;
                }
            }

            String storageUrl = fileShareService.downloadShareFile(shareCode, fileId);
            if (storageUrl == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "文件不存在或无法下载");
                return;
            }

            // 重定向到文件下载地址
            response.sendRedirect(storageUrl);
        } catch (Exception e) {
            log.error("下载分享文件失败", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "下载文件失败: " + e.getMessage());
        }
    }

    @Operation(summary = "保存分享文件到个人网盘")
    @PostMapping("/save")
    @SaCheckLogin
    public Result<Boolean> saveShareFiles(@RequestBody @Valid ShareSaveRequest request) {
        try {
            Integer accountId = StpUtil.getLoginIdAsInt();
            boolean saved = fileShareService.saveShareFiles(accountId, request);
            if (saved) {
                return Result.success(true);
            } else {
                return Result.failure(400, "保存分享文件失败");
            }
        } catch (Exception e) {
            log.error("保存分享文件失败", e);
            return Result.failure(400, "保存分享文件失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取用户创建的分享列表")
    @GetMapping("/my/created")
    @SaCheckLogin
    public Result<Page<ShareFileVO>> getUserShares(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Integer accountId = StpUtil.getLoginIdAsInt();
            Page<UserFileShare> page = new Page<>(pageNum, pageSize);
            Page<ShareFileVO> shares = fileShareService.getUserShares(accountId, page);
            return Result.success(shares);
        } catch (Exception e) {
            log.error("获取用户分享列表失败", e);
            return Result.failure(400, "获取用户分享列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取用户保存的分享列表")
    @GetMapping("/my/saved")
    @SaCheckLogin
    public Result<Page<ShareFileVO>> getUserSavedShares(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Integer accountId = StpUtil.getLoginIdAsInt();
            Page<UserFileShare> page = new Page<>(pageNum, pageSize);
            Page<ShareFileVO> shares = fileShareService.getUserSavedShares(accountId, page);
            return Result.success(shares);
        } catch (Exception e) {
            log.error("获取用户保存的分享列表失败", e);
            return Result.failure(400, "获取用户保存的分享列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "取消分享")
    @PostMapping("/cancel/{shareId}")
    @SaCheckLogin
    public Result<Boolean> cancelShare(@PathVariable Integer shareId) {
        try {
            Integer accountId = StpUtil.getLoginIdAsInt();
            boolean canceled = fileShareService.cancelShare(accountId, shareId);
            if (canceled) {
                return Result.success(true);
            } else {
                return Result.failure(400, "取消分享失败");
            }
        } catch (Exception e) {
            log.error("取消分享失败", e);
            return Result.failure(400, "取消分享失败: " + e.getMessage());
        }
    }

    @Operation(summary = "延长分享有效期")
    @PostMapping("/extend/{shareId}")
    @SaCheckLogin
    public Result<Boolean> extendShareExpiration(
            @PathVariable Integer shareId,
            @Parameter(description = "延长天数") @RequestParam Integer days) {
        try {
            Integer accountId = StpUtil.getLoginIdAsInt();
            boolean extended = fileShareService.extendShareExpiration(accountId, shareId, days);
            if (extended) {
                return Result.success(true);
            } else {
                return Result.failure(400, "延长分享有效期失败");
            }
        } catch (Exception e) {
            log.error("延长分享有效期失败", e);
            return Result.failure(400, "延长分享有效期失败: " + e.getMessage());
        }
    }
} 