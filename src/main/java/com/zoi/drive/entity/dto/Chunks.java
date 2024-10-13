package com.zoi.drive.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/10/13 22:19
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chunks {
    private String md5;
    private Integer currentChunk;
    private Integer totalChunks;
    private String storageUrl;
}
