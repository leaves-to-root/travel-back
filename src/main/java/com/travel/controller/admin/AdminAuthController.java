package com.travel.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.travel.common.BizException;
import com.travel.common.Result;
import com.travel.common.ResultCode;
import com.travel.common.context.TokenService;
import com.travel.entity.Admin;
import com.travel.service.AdminService;
import com.travel.utils.PasswordUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
@Tag(name = "管理员认证", description = "管理员登录、登出、当前用户")
public class AdminAuthController {

    private final AdminService adminService;
    private final TokenService tokenService;

    @PostMapping("/login")
    @Operation(summary = "管理员登录")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            throw new BizException("请输入账号和密码");
        }
        Admin admin = adminService.getByUsername(username);
        if (admin == null || !PasswordUtil.matches(password, admin.getPassword())) {
            throw new BizException(ResultCode.LOGIN_FAILED);
        }
        if (admin.getStatus() == 0) throw new BizException(ResultCode.ACCOUNT_DISABLED);
        String token = tokenService.createAdminToken(admin.getId());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("adminId", admin.getId());
        data.put("nickname", admin.getNickname());
        return Result.success(data);
    }

    @GetMapping("/current")
    @Operation(summary = "获取当前管理员信息（手动校验）")
    public Result<Admin> current(@RequestHeader(value = "Authorization", required = false) String token) {
        Long adminId = tokenService.verifyAdminToken(token);
        if (adminId == null) return Result.error(ResultCode.UNAUTHORIZED);
        Admin admin = adminService.getById(adminId);
        if (admin == null) return Result.error("管理员不存在");
        admin.setPassword(null);
        return Result.success(admin);
    }

    @PostMapping("/logout")
    @Operation(summary = "管理员登出")
    public Result<Void> logout(@RequestHeader("Authorization") String token) {
        tokenService.removeAdminToken(token);
        return Result.success();
    }
}
