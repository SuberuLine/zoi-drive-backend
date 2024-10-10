package com.zoi.drive.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

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
* @since 2024-10-10
*/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@TableName("db_user_download_task")
@Schema(name = "UserDownloadTask", description = "")
public class UserDownloadTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer accountId;

    private String url;

    private String taskType;

    private String fileName;

    @Schema(description = "任务状态：'pending', 'downloading', 'completed', 'failed', 'paused'")
    private String status;

    private Date createdAt;

    private Date startedAt;

    private Date completedAt;

    private Integer progress;
}