package com.zoi.drive.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.zoi.drive.annotation.FileOpsLog;
import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.UserFile;
import com.zoi.drive.entity.dto.UserFolder;
import com.zoi.drive.entity.dto.UserRecycle;
import com.zoi.drive.entity.vo.response.FileCheckResponseVO;
import com.zoi.drive.entity.vo.response.FileItemVO;
import com.zoi.drive.service.IUserFileService;
import com.zoi.drive.service.IUserFolderService;
import com.zoi.drive.service.IUserRecycleService;
import com.zoi.drive.utils.Const;
import com.zoi.drive.utils.RegexUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.zoi.drive.utils.FileUtils.formatFileSize;


/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/13 23:31
 **/
@RestController
@RequestMapping("/api/file")
@Slf4j
public class FileController {

    @Resource
    private IUserFileService userFileService;

    @Resource
    private IUserFolderService userFolderService;

    @Resource
    private IUserRecycleService userRecycleService;

    @Value("${server.system.default-recycle-expired}")
    int defaultRecycleExpired;

    @GetMapping("/list")
    public Result<List<FileItemVO>> fileList() {
        List<UserFile> userFileList = userFileService.listUserFiles()
                .stream().filter(item -> item.getStatus() != null && item.getStatus() == 1).toList();
        List<UserFolder> userFolderList = userFolderService.listUserFolders()
                .stream().filter(item -> item.getStatus() != null && item.getStatus() == 1).toList();
        List<FileItemVO> fileListView = new ArrayList<>();

        Map<Integer, FileItemVO> folderMap = new HashMap<>();

        for (UserFolder folder : userFolderList) {
            FileItemVO folderVO = new FileItemVO();
            folderVO.setKey(String.valueOf(folder.getId()));
            folderVO.setName(folder.getName());
            folderVO.setIsFolder(true);
            folderVO.setType("folder");
            folderVO.setSize("-");
            folderVO.setUploadAt(folder.getCreatedAt());
            folderVO.setChildren(new ArrayList<>());

            folderMap.put(folder.getId(), folderVO);
        }

        for (UserFolder folder : userFolderList) {
            if (folder.getParentId() == null) {
                fileListView.add(folderMap.get(folder.getId()));
            } else {
                FileItemVO parentFolder = folderMap.get(folder.getParentId());
                if (parentFolder != null) {
                    parentFolder.getChildren().add(folderMap.get(folder.getId()));
                } else {
                    // 如果父文件夹不存在，将此文件夹添加到顶层
                    fileListView.add(folderMap.get(folder.getId()));
                }
            }
        }

        // 处理文件
        for (UserFile file : userFileList) {
            FileItemVO fileVO = new FileItemVO();
            fileVO.setKey(String.valueOf(file.getId()));
            fileVO.setName(file.getFilename());
            fileVO.setIsFolder(false);
            fileVO.setType(file.getType());
            fileVO.setSize(formatFileSize(file.getSize()));
            fileVO.setUploadAt(file.getUploadAt());

            FileItemVO parentFolder = folderMap.get(file.getFolderId());
            if (parentFolder != null) {
                parentFolder.getChildren().add(fileVO);
            } else {
                fileListView.add(fileVO);
            }
        }

        return Result.success(fileListView);
    }


    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile[] files) throws IOException {
        for (MultipartFile file : files) {
            log.info("{} {} {}", file.getOriginalFilename(), file.getContentType(), file.getSize());
        }
        return userFileService.manualUpload(files);
    }

    @FileOpsLog(action = "移动文件")
    @GetMapping("/move")
    public Result<String> move(@RequestParam("fileId") Integer fileId,
                               @RequestParam("targetFolderId") Integer targetFolderId) {
        return userFileService.move(fileId, targetFolderId);
    }

    @GetMapping("/create-folder")
    public Result<UserFolder> createFolder(@RequestParam("parentFolderId") Integer parentFolderId,
                                       @RequestParam("folderName") String folderName) {
        return userFolderService.createFolder(parentFolderId, folderName);
    }

    @GetMapping("/check")
    public Result<FileCheckResponseVO> check( @RequestParam(value = "folderId", required = false) Integer folderId,
                                              @RequestParam("hash") String hash) {
        return userFileService.checkFileHash(folderId, hash);
    }

    /**
     * 生成由后端处理的下载链接
     * @param fileId
     * @return
     */
    @FileOpsLog(action = "下载文件")
    @GetMapping("/{fileId}/download")
    public Result<String> createDownloadLink(@PathVariable("fileId") Integer fileId) {
        UserFile file = userFileService.getById(fileId);
        if (file == null) {
            return Result.failure(500, "文件不存在");
        }
        if (file.getAccountId() != StpUtil.getLoginIdAsInt()) {
            return Result.failure(500, "无该文件访问权限！");
        }
        return userFileService.createDownloadLink(file);
    }

    @GetMapping("/download")
    public void download(@RequestParam("UUID") String uuid,
                         @RequestParam("signature") String signature,
                         @RequestParam("expireAt") String expireAt,
                         HttpServletResponse response) throws IOException {
        if (uuid == null || uuid.length() != 36) {
            response.getWriter().println(Result.failure(400, "无效的UUID"));
        }
        userFileService.download(uuid, signature, expireAt, response);
    }

    @GetMapping("/{fileId}/preview")
    public Result<String> previewFile(@PathVariable("fileId") Integer fileId) {
        UserFile file = userFileService.getById(fileId);
        if (file == null) {
            return Result.failure(404, "文件不存在");
        }
        if (file.getAccountId() != StpUtil.getLoginIdAsInt()) {
            return Result.failure(500, "无该文件访问权限！");
        }
        return userFileService.previewFile(file);
    }

    @FileOpsLog(action = "预签名下载")
    @GetMapping("/{fileId}/pre-signed-link")
    public Result<String> getPreSignedLink(@PathVariable("fileId") Integer fileId) {
        UserFile file = userFileService.getById(fileId);
        if (file == null) {
            return Result.failure(500, "文件不存在");
        }
        if (file.getAccountId() != StpUtil.getLoginIdAsInt()) {
            return Result.failure(500, "无该文件访问权限！");
        }
        try {
            return userFileService.getPreSignedLink(file);
        } catch (Exception e) {
            return Result.failure(500, "获取预签名链接失败");
        }
    }

    @GetMapping("/download-magnet")
    public Result<String> downloadMagnetLink(@RequestParam("magnet") String magnetLink) {
        if (!RegexUtils.isMagnet(magnetLink)) return Result.failure(500, "非法的magnet格式");
        return userFileService.downloadMagnetLink(magnetLink);
    }

    @GetMapping("/offline-download")
    public Result<String> offlineDownload(@RequestParam("link") String offlineDownloadLink) {
        return userFileService.offlineDownload(offlineDownloadLink);
    }

    @GetMapping("/rename")
    public Result<String> rename(@RequestParam("fileId") Integer fileId,
                                 @RequestParam("newName") String newName) {
        return userFileService.renameFile(fileId, newName);
     }

    @FileOpsLog(action = "删除文件")
    @DeleteMapping("/{fileId}/delete")
    public Result<String> delete(@PathVariable("fileId") Integer fileId) {
        UserFile removeObj = userFileService.getById(fileId);
        int userId = StpUtil.getLoginIdAsInt();
        if (removeObj == null) return Result.failure(500, "文件不存在");
        if (removeObj.getAccountId() != userId) return Result.failure(500, "无权限操作");
        removeObj.setStatus(Const.FILE_RECYCLED);
        if (userFileService.updateById(removeObj)) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, defaultRecycleExpired);
            userRecycleService.saveOrUpdate(new UserRecycle(null, fileId, userId, removeObj.getFilename(),
                    "file",calendar.getTime() , new Date()));
            return Result.success("删除成功");
        }
        return Result.failure(500, "删除失败，请联系管理员");
    }

    @FileOpsLog(action = "删除文件夹")
    @DeleteMapping("/folder/{folderId}/delete")
    public Result<String> deleteFolder(@PathVariable("folderId") Integer folderId) {
        UserFolder removeObj = userFolderService.getById(folderId);
        if (removeObj == null) return Result.failure(500, "文件夹不存在");
        if (removeObj.getAccountId() != StpUtil.getLoginIdAsInt()) return Result.failure(500, "无权限操作");
        removeObj.setStatus(Const.FILE_RECYCLED);
        // TODO: 文件夹的子文件状态设置
        if (userFolderService.updateById(removeObj)) {
            return Result.success("删除成功");
        } else {
            return Result.failure(500, "删除失败，文件夹不为空");
        }
    }

    @PostMapping("/upload/chunk")
    public Result<String> uploadChunk(@RequestParam("file") MultipartFile file,
                                      @RequestParam("hash") String hash,
                                      @RequestParam("chunk") int chunk,
                                      @RequestParam("chunks") int chunks,
                                      @RequestParam("folderId") Integer folderId) {
        try {
            userFileService.uploadChunk(file, hash, chunk, chunks, folderId);
            return Result.success("分片上传成功");
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.failure(500, "上传异常：" +e.getMessage());
        }
    }

}
