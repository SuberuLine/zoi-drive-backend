package com.zoi.drive.controller;

import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.UserFile;
import com.zoi.drive.entity.dto.UserFolder;
import com.zoi.drive.entity.vo.response.FileItemVO;
import com.zoi.drive.service.IUserFileChunkService;
import com.zoi.drive.service.IUserFileService;
import com.zoi.drive.service.IUserFolderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zoi.drive.utils.FileUtils.formatFileSize;


/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/13 23:31
 **/
@RestController
@RequestMapping("/api")
@Slf4j
public class FileController {

    @Resource
    private IUserFileService userFileService;

    @Resource
    private IUserFolderService userFolderService;

    @Resource
    private IUserFileChunkService userFileChunkService;

    @PostMapping("/file/list")
    public Result<List<FileItemVO>> fileList() {
        List<UserFile> userFileList = userFileService.listUserFiles();
        List<UserFolder> userFolderList = userFolderService.listUserFolders();
        List<FileItemVO> fileListView = new ArrayList<>();

        // 存储文件夹 ID 和对应的 FileItemVO
        Map<Integer, FileItemVO> folderMap = new HashMap<>();

        // 首先处理文件夹
        for (UserFolder folder : userFolderList) {
            FileItemVO folderVO = new FileItemVO();
            folderVO.setKey(String.valueOf(folder.getId()));
            folderVO.setName(folder.getName());
            folderVO.setIsFolder(true);
            folderVO.setType("folder");
            folderVO.setSize("-");
            folderVO.setUpdateAt(folder.getCreatedAt());
            folderVO.setChildren(new ArrayList<>());

            folderMap.put(folder.getId(), folderVO);

            if (folder.getParentId() == null) {
                fileListView.add(folderVO);
            } else {
                FileItemVO parentFolder = folderMap.get(folder.getParentId());
                if (parentFolder != null) {
                    parentFolder.getChildren().add(folderVO);
                }
            }
        }

        // 然后处理文件
        for (UserFile file : userFileList) {
            FileItemVO fileVO = new FileItemVO();
            fileVO.setKey(String.valueOf(file.getId()));
            fileVO.setName(file.getFilename());
            fileVO.setIsFolder(false);
            fileVO.setType(file.getType());
            fileVO.setSize(formatFileSize(file.getSize()));
            fileVO.setUpdateAt(file.getUploadAt());

            FileItemVO parentFolder = folderMap.get(file.getFolderId());
            if (parentFolder != null) {
                parentFolder.getChildren().add(fileVO);
            } else {
                fileListView.add(fileVO);
            }
        }

        return Result.success(fileListView);
    }



    @PostMapping("/file/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile[] files) throws IOException {
        for (MultipartFile file : files) {
            log.info("{} {} {}", file.getOriginalFilename(), file.getContentType(), file.getSize());
        }
        return userFileService.manualUpload(files);
    }

    @GetMapping("/file/check")
    public Result<String> check(@RequestParam("hash") String hash) {
        return userFileService.checkFileHash(hash);
    }

    @PostMapping("/file/upload/chunk")
    public Result<String> uploadChunk(@RequestParam("file") MultipartFile file,
                                      @RequestParam("hash") String hash,
                                      @RequestParam("chunk") int chunk,
                                      @RequestParam("chunks") int chunks,
                                      @RequestParam("folderId") Integer folderId) throws IOException {
        try {
            userFileChunkService.uploadChunk(file, hash, chunk, chunks, folderId);
            return Result.success("分片上传成功");
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.failure(500, "上传异常：" +e.getMessage());
        }
    }

}
