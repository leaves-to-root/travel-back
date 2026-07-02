package com.travel.controller;

import cn.hutool.core.util.StrUtil;
import com.travel.common.BizException;
import com.travel.common.Constants;
import com.travel.common.Result;
import com.travel.common.ResultCode;
import com.travel.common.context.TokenService;
import com.travel.dto.request.LoginRequest;
import com.travel.dto.request.RegisterRequest;
import com.travel.entity.User;
import com.travel.service.CaptchaService;
import com.travel.service.UserService;
import com.travel.utils.PasswordUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "用户认证", description = "注册、登录、登出、获取当前用户")
public class AuthController {

    private final UserService userService;
    private final TokenService tokenService;
    private final CaptchaService captchaService;
    private final StringRedisTemplate redisTemplate;

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<Map<String, Object>> register(@RequestBody RegisterRequest req) {
        // 邮箱必填
        if (StrUtil.isBlank(req.getEmail())) {
            throw new BizException("邮箱不能为空");
        }
        // 校验邮箱验证码
        String codeKey = Constants.EMAIL_CODE_PREFIX + req.getEmail();
        String cachedCode = redisTemplate.opsForValue().get(codeKey);
        if (cachedCode == null || !cachedCode.equals(req.getEmailCode())) {
            throw new BizException(ResultCode.EMAIL_CODE_ERROR);
        }
        redisTemplate.delete(codeKey); // 验证码一次性使用

        if (StrUtil.isBlank(req.getPassword()) || req.getPassword().length() < 6) {
            throw new BizException("密码不能少于6位");
        }
        // 检查重复
        if (StrUtil.isNotBlank(req.getUsername()) && userService.getByUsername(req.getUsername()) != null) {
            throw new BizException(ResultCode.USER_EXISTS);
        }
        if (userService.getByEmail(req.getEmail()) != null) {
            throw new BizException(ResultCode.EMAIL_ALREADY_EXISTS);
        }
        // 创建用户
        User user = new User();
        user.setUsername(StrUtil.isNotBlank(req.getUsername()) ? req.getUsername() : null);
        user.setEmail(req.getEmail());
        user.setPassword(PasswordUtil.encode(req.getPassword()));
        user.setNickname(StrUtil.isNotBlank(req.getNickname()) ? req.getNickname() : "用户" + System.currentTimeMillis() % 10000);
        user.setStatus(1);
        userService.save(user);
        // 生成 token
        String token = tokenService.createUserToken(user.getId());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("nickname", user.getNickname());
        return Result.success(data);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录（用户名+密码+图形验证码 / 邮箱+邮箱验证码）")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest req) {
        String loginType = StrUtil.isNotBlank(req.getLoginType()) ? req.getLoginType() : "username";
        User user;

        if ("email".equals(loginType)) {
            // 邮箱登录：校验邮箱验证码
            if (StrUtil.isBlank(req.getEmail())) {
                throw new BizException("请输入邮箱");
            }
            // 校验邮箱验证码
            String codeKey = Constants.EMAIL_CODE_PREFIX + req.getEmail();
            String cachedCode = redisTemplate.opsForValue().get(codeKey);
            if (cachedCode == null || !cachedCode.equals(req.getEmailCode())) {
                throw new BizException(ResultCode.EMAIL_CODE_ERROR);
            }
            redisTemplate.delete(codeKey);
            // 按邮箱查找用户
            user = userService.getByEmail(req.getEmail());
            if (user == null) {
                throw new BizException(ResultCode.USER_NOT_EXISTS);
            }
        } else {
            // 用户名登录：校验图形验证码 + 密码
            if (StrUtil.isBlank(req.getUsername())) {
                throw new BizException("请输入用户名");
            }
            if (StrUtil.isBlank(req.getPassword())) {
                throw new BizException("请输入密码");
            }
            // 校验图形验证码
            if (!captchaService.verify(req.getCaptchaKey(), req.getCaptchaCode())) {
                throw new BizException(ResultCode.CAPTCHA_ERROR);
            }
            // 按用户名查找
            user = userService.getByUsername(req.getUsername());
            if (user == null || !PasswordUtil.matches(req.getPassword(), user.getPassword())) {
                throw new BizException(ResultCode.LOGIN_FAILED);
            }
        }

        if (user.getStatus() == 0) {
            throw new BizException(ResultCode.ACCOUNT_DISABLED);
        }
        String token = tokenService.createUserToken(user.getId());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("nickname", user.getNickname());
        data.put("avatar", user.getAvatar());
        return Result.success(data);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public Result<Void> logout(@RequestHeader("Authorization") String token) {
        tokenService.removeUserToken(token);
        return Result.success();
    }

    @GetMapping("/current")
    @Operation(summary = "获取当前登录用户信息（手动校验 token）")
    public Result<User> current(@RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = tokenService.verifyUserToken(token);
        if (userId == null) return Result.error(ResultCode.UNAUTHORIZED);
        User user = userService.getById(userId);
        if (user == null) return Result.error(ResultCode.USER_NOT_EXISTS);
        user.setPassword(null);
        return Result.success(user);
    }
}
