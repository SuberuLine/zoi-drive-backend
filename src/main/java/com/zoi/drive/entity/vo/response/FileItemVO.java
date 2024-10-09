package com.zoi.drive.entity.vo.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FileItemVO {
    private String key;
    private String name;
    private Boolean isFolder;
    private String type;
    private String size;
    private Date uploadAt;
    private List<FileItemVO> children;
}
