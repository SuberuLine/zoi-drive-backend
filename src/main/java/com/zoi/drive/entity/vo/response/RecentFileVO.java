package com.zoi.drive.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 最近查看文件响应对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentFileVO {
    // 文件ID
    private Integer id;
    
    // 文件名
    private String filename;
    
    // 文件类型
    private String type;
    
    // 查看日期
    private Date viewDate;
} 