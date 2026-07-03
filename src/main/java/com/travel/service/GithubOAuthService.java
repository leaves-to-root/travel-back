package com.travel.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.travel.common.Constants;
import com.travel.entity.User;
import com.travel.utils.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * GitHub OAuth2 手动接入服务
 * <p>
 * 流程：前端跳转 authorizeUrl → GitHub 回调带 code → 换 access_token → 拉 user 信息 → 找/建本地用户 → 发 Redis Token
 * <p>
 * 用户映射：username = "gh_" + githubId，无需新增数据库字段。
 */
@Slf4j
@Service
public class GithubOAuthService {

    private static final String AUTHORIZE_URL = "https://github.com/login/oauth/authorize";
    private static final String TOKEN_URL = "https://github.com/login/oauth/access_token";
    private static final String USER_INFO_URL = "https://api.github.com/user";

    @Value("${spring.security.oauth2.client.registration.github.client-id:}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret:}")
    private String clientSecret;

    /** 后端回调地址，需在 GitHub OAuth App 中配置一致 */
    @Value("${travel.oauth2.github.callback-url:http://localhost:8080/api/auth/github/callback}")
    private String callbackUrl;

    /** 登录成功后重定向到前端的地址（带 token 参数） */
    @Value("${travel.oauth2.github.front-success-url:http://localhost:5173/login}")
    private String frontSuccessUrl;

    private final UserService userService;
    private final StringRedisTemplate redisTemplate;

    public GithubOAuthService(UserService userService, StringRedisTemplate redisTemplate) {
        this.userService = userService;
        this.redisTemplate = redisTemplate;
    }

    /** 生成 GitHub 授权页地址（带 state 防 CSRF） */
    public String buildAuthorizeUrl() {
        String state = IdUtil.fastSimpleUUID();
        redisTemplate.opsForValue().set("travel:oauth:state:" + state, "1", Duration.ofMinutes(5));
        return AUTHORIZE_URL + "?client_id=" + clientId
                + "&redirect_uri=" + callbackUrl
                + "&scope=read:user,user:email"
                + "&state=" + state;
    }

    /** 校验 state，一次性使用 */
    public boolean consumeState(String state) {
        if (StrUtil.isBlank(state)) return false;
        String key = "travel:oauth:state:" + state;
        Boolean exists = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(exists)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }

    /** GitHub 回调主流程：code → access_token → 用户信息 → 本地用户 → token */
    public String handleCallback(String code) {
        // 1. 换 access_token
        String accessToken = exchangeCodeForToken(code);
        if (accessToken == null) {
            throw new RuntimeException("获取 GitHub access_token 失败");
        }
        // 2. 拉用户信息
        Map<String, Object> githubUser = fetchUserInfo(accessToken);
        Object idObj = githubUser.get("id");
        if (idObj == null) {
            throw new RuntimeException("GitHub 返回的用户信息缺少 id");
        }
        long githubId = ((Number) idObj).longValue();
        String login = getStrOrNull(githubUser, "login");
        String name = getStrOrNull(githubUser, "name");
        String avatarUrl = getStrOrNull(githubUser, "avatar_url");
        String email = getStrOrNull(githubUser, "email");

        // 规范化：昵称为空 → 随机昵称；邮箱为空 → 空字符串
        String nickname = StrUtil.isNotBlank(name) ? name
                : (StrUtil.isNotBlank(login) ? login : "GitHub用户" + (System.currentTimeMillis() % 10000));
        email = StrUtil.isNotBlank(email) ? email : "";

        // 3. 找/建本地用户
        User user = findOrCreateUser(githubId, nickname, avatarUrl, email);
        return user.getNickname() + "|" + user.getId();
    }

    /** 用 code 换 access_token */
    private String exchangeCodeForToken(String code) {
        Map<String, Object> form = new HashMap<>();
        form.put("client_id", clientId);
        form.put("client_secret", clientSecret);
        form.put("code", code);
        form.put("redirect_uri", callbackUrl);

        try (HttpResponse resp = HttpRequest.post(TOKEN_URL)
                .header("Accept", "application/json")
                .form(form)
                .timeout(15000)
                .execute()) {
            JSONObject json = JSONUtil.parseObj(resp.body());
            return json.getStr("access_token");
        } catch (Exception e) {
            log.error("换 GitHub access_token 失败: {}", e.getMessage());
            return null;
        }
    }

    /** 拉 GitHub 用户信息 */
    private Map<String, Object> fetchUserInfo(String accessToken) {
        try (HttpResponse resp = HttpRequest.get(USER_INFO_URL)
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/vnd.github+json")
                .timeout(15000)
                .execute()) {
            return JSONUtil.parseObj(resp.body());
        } catch (Exception e) {
            log.error("拉 GitHub 用户信息失败: {}", e.getMessage());
            throw new RuntimeException("获取 GitHub 用户信息失败");
        }
    }

    /** 安全取 String：null / JSONNull → null，避免类型转换异常 */
    private String getStrOrNull(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val == null ? null : val.toString();
    }

    /** 找/建本地用户：username = "gh_" + githubId */
    private User findOrCreateUser(Long githubId, String nickname, String avatarUrl, String email) {
        String username = "gh_" + githubId;
        User user = userService.getByUsername(username);
        if (user != null) {
            // 已存在：刷新昵称/头像/邮箱
            boolean dirty = false;
            if (!nickname.equals(user.getNickname())) { user.setNickname(nickname); dirty = true; }
            if (avatarUrl != null && !avatarUrl.equals(user.getAvatar())) { user.setAvatar(avatarUrl); dirty = true; }
            if (!email.equals(user.getEmail() == null ? "" : user.getEmail())) { user.setEmail(email); dirty = true; }
            if (dirty) userService.updateById(user);
            return user;
        }
        // 新建
        user = new User();
        user.setUsername(username);
        user.setEmail(email);
        // 随机密码，GitHub 用户不会用密码登录
        user.setPassword(PasswordUtil.encode(IdUtil.fastSimpleUUID()));
        user.setNickname(nickname);
        user.setAvatar(avatarUrl);
        user.setStatus(1);
        userService.save(user);
        log.info("GitHub 用户首次登录，已创建本地账号 username={}, githubId={}", username, githubId);
        return user;
    }

    public String getFrontSuccessUrl() {
        return frontSuccessUrl;
    }
}
