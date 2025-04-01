package com.zoi.drive.entity.vo.response;

import com.zoi.drive.entity.BaseData;
import lombok.Data;

@Data
public class DownloadProgressVO {
    private Integer accountId;
    private String fileName;
    private String status;
    private Integer progress;
}
