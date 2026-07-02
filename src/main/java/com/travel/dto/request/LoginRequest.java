package com.travel.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "登录请求")
public class LoginRequest {

    @Schema(description = "用户名（与手机号二选一）")
    private String username;

    @Schema(description = "手机号（与用户名二选一）")
    private String phone;

    @Schema(description = "密码")
    private String password;
}
