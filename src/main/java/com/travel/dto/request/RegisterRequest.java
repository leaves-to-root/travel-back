package com.travel.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "注册请求")
public class RegisterRequest {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "邮箱（必填）")
    private String email;

    @Schema(description = "邮箱验证码（必填）")
    private String emailCode;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "昵称")
    private String nickname;
}
