package com.zoi.drive.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 文件统计响应对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileStatisticalVO {
    // 总文件数量
    private Long totalCount;
    
    // 各类型文件数量统计
    private Map<String, Long> typeDistribution;
} 