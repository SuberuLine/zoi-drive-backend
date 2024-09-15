package com.zoi.drive.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
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
* @since 2024-09-14
*/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@TableName("db_user_checkin")
@Schema(name = "UserCheckin", description = "")
public class UserCheckin implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer accountId;

    @Schema(description = "签到总计次数")
    private Integer checkinCount;

    @Schema(description = "最后一次签到")
    private Date lastCheckin;

    @Schema(description = "签到奖励")
    private BigDecimal checkinReward;

    @Schema(description = "当天为止连续签到天数")
    private Integer checkinConsecutive;

}