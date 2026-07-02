package com.travel.controller;

import cn.hutool.core.util.StrUtil;
import com.travel.common.BizException;
import com.travel.common.Result;
import com.travel.common.context.BaseContext;
import com.travel.dto.request.UpdatePasswordRequest;
import com.travel.entity.User;
import com.travel.service.UserService;
import com.travel.utils.PasswordUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户中心", description = "个人资料修改、密码修改")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "获取个人资料")
    public Result<User> profile() {
        Long userId = BaseContext.getCurrentId();
        User user = userService.getById(userId);
        if (user == null) return Result.error("用户不存在");
        user.setPassword(null);
        return Result.success(user);
    }

    @PutMapping("/profile")
    @Operation(summary = "更新个人资料")
    public Result<User> updateProfile(@RequestBody Map<String, Object> updates) {
        Long userId = BaseContext.getCurrentId();
        User user = userService.getById(userId);
        if (user == null) throw new BizException("用户不存在");
        if (updates.containsKey("nickname")) user.setNickname((String) updates.get("nickname"));
        if (updates.containsKey("gender")) user.setGender((Integer) updates.get("gender"));
        if (updates.containsKey("avatar")) user.setAvatar((String) updates.get("avatar"));
        if (updates.containsKey("email")) user.setEmail((String) updates.get("email"));
        userService.updateById(user);
        user.setPassword(null);
        return Result.success(user);
    }

    @PutMapping("/password")
    @Operation(summary = "修改密码")
    public Result<Void> changePassword(@RequestBody UpdatePasswordRequest req) {
        if (StrUtil.isBlank(req.getOldPassword()) || StrUtil.isBlank(req.getNewPassword())) {
            throw new BizException("密码不能为空");
        }
        if (req.getNewPassword().length() < 6) {
            throw new BizException("新密码不能少于6位");
        }
        Long userId = BaseContext.getCurrentId();
        User user = userService.getById(userId);
        if (!PasswordUtil.matches(req.getOldPassword(), user.getPassword())) {
            throw new BizException("旧密码错误");
        }
        user.setPassword(PasswordUtil.encode(req.getNewPassword()));
        userService.updateById(user);
        return Result.success();
    }
}
