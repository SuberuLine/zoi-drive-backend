package com.zoi.drive.controller;

import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.UserFile;
import com.zoi.drive.entity.dto.UserFolder;
import com.zoi.drive.entity.vo.response.FileItemVO;
import com.zoi.drive.service.IUserFileService;
import com.zoi.drive.service.IUserFolderService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zoi.drive.utils.FileUtils.formatFileSize;

@RestController
@RequestMapping("/api/safes")
public class SafesController {

    @Resource
    IUserFileService userFileService;

    @Resource
    IUserFolderService userFolderService;

    @GetMapping("/list")
    public Result<List<FileItemVO>> fileList() {
        List<UserFile> userFileList = userFileService.listUserFiles()
                .stream().filter(item -> item.getStatus() != null && item.getStatus() == 2).toList();
        List<UserFolder> userFolderList = userFolderService.listUserFolders()
                .stream().filter(item -> item.getStatus() != null && item.getStatus() == 2).toList();
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

}
