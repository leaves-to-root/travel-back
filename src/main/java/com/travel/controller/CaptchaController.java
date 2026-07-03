package com.travel.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.travel.common.BizException;
import com.travel.common.Constants;
import com.travel.common.Result;
import com.travel.common.ResultCode;
import com.travel.service.CaptchaService;
import com.travel.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "验证码", description = "图形验证码、邮箱验证码")
public class CaptchaController {

    private final CaptchaService captchaService;
    private final EmailService emailService;
    private final StringRedisTemplate redisTemplate;

    @GetMapping("/captcha")
    @Operation(summary = "获取图形验证码")
    public Result<Map<String, String>> captcha() {
        return Result.success(captchaService.generate());
    }

    @PostMapping("/send-email-code")
    @Operation(summary = "发送邮箱验证码")
    public Result<Void> sendEmailCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (StrUtil.isBlank(email)) {
            throw new BizException("邮箱不能为空");
        }
        // 频率限制：同一邮箱 60 秒内只能发一次
        String rateKey = Constants.EMAIL_CODE_PREFIX + "rate:" + email;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(rateKey))) {
            throw new BizException("发送过于频繁，请稍后再试");
        }
        // 生成 6 位数字验证码
        String code = RandomUtil.randomNumbers(6);
        // 存入 Redis，5分钟过期
        redisTemplate.opsForValue().set(
                Constants.EMAIL_CODE_PREFIX + email, code,
                Duration.ofSeconds(Constants.CODE_EXPIRE));
        // 频率限制
        redisTemplate.opsForValue().set(rateKey, "1", Duration.ofSeconds(60));
        // 发送邮件
        try {
            emailService.sendVerifyCode(email, code);
        } catch (Exception e) {
            throw new BizException(ResultCode.EMAIL_SEND_FAIL);
        }
        return Result.success();
    }
}
