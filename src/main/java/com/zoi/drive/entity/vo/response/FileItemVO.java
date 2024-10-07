package com.zoi.drive.entity.vo.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FileItemVO {
    String name;
    Boolean isFolder;
    String type;
    String size;
    Date modifiedDate;
    List<FileItemVO> children;
}
