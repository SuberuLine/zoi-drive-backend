package com.zoi.drive.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
* <p>
* 
* </p>
*
* @author Yuzoi
* @since 2024-09-20
*/
@Getter
@Setter
@TableName("db_user_file_chunk")
@Schema(name = "UserFileChunk", description = "")
public class UserFileChunk implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer fileId;

    private Integer chunkNumber;

    private Double chunkSize;

    private String storageUrl;

    private String status;

    private String hash;

    private LocalDateTime uploadAt;
}