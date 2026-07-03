package com.travel.service;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.util.IdUtil;
import com.travel.common.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 图形验证码服务（基于 Hutool + Redis）
 */
@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final StringRedisTemplate redisTemplate;

    /**
     * 生成图形验证码，返回 {captchaKey, captchaImage(base64)}
     */
    public Map<String, String> generate() {
        // 线干扰验证码：宽130 高48 字符数4 干扰线数10
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(130, 48, 4, 10);

        String code = captcha.getCode();
        String key = IdUtil.fastSimpleUUID();

        // 存入 Redis，5分钟过期
        redisTemplate.opsForValue().set(
                Constants.CAPTCHA_PREFIX + key, code.toLowerCase(),
                Duration.ofSeconds(Constants.CODE_EXPIRE));

        Map<String, String> result = new HashMap<>();
        result.put("captchaKey", key);
        // 拼接完整 data URI
        result.put("captchaImage", "data:image/png;base64," + captcha.getImageBase64());
        return result;
    }

    /**
     * 校验图形验证码（忽略大小写），校验后立即删除
     */
    public boolean verify(String key, String code) {
        if (key == null || code == null) return false;
        String redisCode = redisTemplate.opsForValue().get(Constants.CAPTCHA_PREFIX + key);
        if (redisCode == null) return false;
        boolean match = redisCode.equalsIgnoreCase(code.trim());
        if (match) {
            redisTemplate.delete(Constants.CAPTCHA_PREFIX + key); // 一次性使用
        }
        return match;
    }
}
