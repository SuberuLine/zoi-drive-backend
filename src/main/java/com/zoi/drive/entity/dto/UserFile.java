package com.zoi.drive.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@AllArgsConstructor
@NoArgsConstructor
@TableName("db_user_file")
@Schema(name = "UserFile", description = "")
public class UserFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer accountId;

    private Integer folderId;

    private String filename;

    private String type;

    private Double size;

    private String hash;

    private String storageUrl;

    private Boolean isDeleted;

    private LocalDateTime uploadAt;

    private LocalDateTime viewedAt;
}