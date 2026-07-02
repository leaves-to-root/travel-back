package com.travel.common.context;

import cn.hutool.core.util.IdUtil;
import com.travel.common.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 基于 Redis 的 Token 管理服务。
 * <p>
 * 用户 token 存储结构：travel:token:user:{token} -> LoginUser.id
 * 管理员 token 存储结构：travel:token:admin:{token} -> Admin.id
 */
@Component
@RequiredArgsConstructor
public class TokenService {

    private final StringRedisTemplate redisTemplate;

    /** 为用户生成 token 并存入 Redis */
    public String createUserToken(Long userId) {
        return createToken(Constants.USER_TOKEN_PREFIX, userId);
    }

    /** 为管理员生成 token 并存入 Redis */
    public String createAdminToken(Long adminId) {
        return createToken(Constants.ADMIN_TOKEN_PREFIX, adminId);
    }

    /** 校验用户 token，返回用户 id；无效返回 null */
    public Long verifyUserToken(String token) {
        return verifyToken(Constants.USER_TOKEN_PREFIX, token);
    }

    /** 校验管理员 token，返回管理员 id；无效返回 null */
    public Long verifyAdminToken(String token) {
        return verifyToken(Constants.ADMIN_TOKEN_PREFIX, token);
    }

    /** 用户登出 */
    public void removeUserToken(String token) {
        if (token != null) {
            redisTemplate.delete(Constants.USER_TOKEN_PREFIX + token);
        }
    }

    /** 管理员登出 */
    public void removeAdminToken(String token) {
        if (token != null) {
            redisTemplate.delete(Constants.ADMIN_TOKEN_PREFIX + token);
        }
    }

    private String createToken(String prefix, Long id) {
        String token = IdUtil.fastSimpleUUID();
        redisTemplate.opsForValue().set(prefix + token, String.valueOf(id),
                Duration.ofSeconds(Constants.TOKEN_EXPIRE));
        return token;
    }

    private Long verifyToken(String prefix, String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        String id = redisTemplate.opsForValue().get(prefix + token);
        if (id == null) {
            return null;
        }
        // 续期
        redisTemplate.expire(prefix + token, Duration.ofSeconds(Constants.TOKEN_EXPIRE));
        return Long.valueOf(id);
    }
}
