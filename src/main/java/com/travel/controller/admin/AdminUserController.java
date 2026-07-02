package com.travel.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.PageResult;
import com.travel.common.Result;
import com.travel.entity.User;
import com.travel.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
@Tag(name = "后台-用户管理")
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/list")
    @Operation(summary = "用户列表")
    public Result<PageResult<User>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        Page<User> p = userService.lambdaQuery()
                .like(keyword != null, User::getNickname, keyword)
                .or(keyword != null)
                .like(keyword != null, User::getPhone, keyword)
                .orderByDesc(User::getCreateTime)
                .page(new Page<>(page, size));
        p.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(new PageResult<>(p));
    }

    @PutMapping("/status/{userId}")
    @Operation(summary = "启用/禁用用户")
    public Result<Void> toggleStatus(@PathVariable Long userId, @RequestBody Map<String, Integer> body) {
        User user = userService.getById(userId);
        if (user != null) {
            user.setStatus(body.get("status"));
            userService.updateById(user);
        }
        return Result.success();
    }
}
