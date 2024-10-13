package com.zoi.drive.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/10/14 1:29
 **/
@Data
@AllArgsConstructor
public class FileCheckResponseVO {
    private boolean exists;
    private String filename;
    private Integer newFileId;
}
