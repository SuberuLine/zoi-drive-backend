package com.zoi.drive.entity.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分享验证请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "分享验证请求")
public class ShareVerifyRequest {
    
    @NotNull(message = "分享码不能为空")
    @Schema(description = "分享码")
    private String shareCode;
    
    @Schema(description = "访问密码，如果分享设置了密码则必填")
    private String password;
} 