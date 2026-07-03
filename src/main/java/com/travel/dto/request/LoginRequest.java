package com.travel.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "登录请求")
public class LoginRequest {

    @Schema(description = "登录方式：username=用户名登录, email=邮箱登录", example = "username")
    private String loginType;

    @Schema(description = "用户名（loginType=username 时必填）")
    private String username;

    @Schema(description = "密码（loginType=username 时必填）")
    private String password;

    @Schema(description = "图形验证码key（loginType=username 时必填）")
    private String captchaKey;

    @Schema(description = "图形验证码（loginType=username 时必填）")
    private String captchaCode;

    @Schema(description = "邮箱（loginType=email 时必填）")
    private String email;

    @Schema(description = "邮箱验证码（loginType=email 时必填）")
    private String emailCode;
}
