package com.travel.controller;

import cn.hutool.core.util.StrUtil;
import com.travel.common.BizException;
import com.travel.common.Result;
import com.travel.common.context.TokenService;
import com.travel.entity.User;
import com.travel.service.GithubOAuthService;
import com.travel.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * GitHub OAuth2 登录回调控制器
 * <p>
 * /authorize  → 重定向到 GitHub 授权页
 * /callback   → GitHub 回跳，换 token、找/建用户、发 Redis token，最后重定向到前端
 */
@Slf4j
@RestController
@RequestMapping("/api/auth/github")
@RequiredArgsConstructor
@Tag(name = "GitHub 登录", description = "GitHub 第三方登录")
public class GithubAuthController {

    private final GithubOAuthService githubOAuthService;
    private final TokenService tokenService;
    private final UserService userService;

    @GetMapping("/authorize")
    @Operation(summary = "跳转到 GitHub 授权页")
    public void authorize(HttpServletResponse response) throws IOException {
        String url = githubOAuthService.buildAuthorizeUrl();
        response.sendRedirect(url);
    }

    @GetMapping("/callback")
    @Operation(summary = "GitHub 回调：换 token + 登录")
    public void callback(@RequestParam("code") String code,
                         @RequestParam(value = "state", required = false) String state,
                         HttpServletResponse response) throws IOException {
        // 1. 校验 state（CSRF 防护）
        if (!githubOAuthService.consumeState(state)) {
            response.sendRedirect(githubOAuthService.getFrontSuccessUrl()
                    + "?github_error=" + URLEncoder.encode("state 校验失败，请重试", StandardCharsets.UTF_8));
            return;
        }
        if (StrUtil.isBlank(code)) {
            response.sendRedirect(githubOAuthService.getFrontSuccessUrl()
                    + "?github_error=" + URLEncoder.encode("未收到授权码", StandardCharsets.UTF_8));
            return;
        }

        try {
            // 2. 走完 OAuth 流程，拿到 nickname|userId
            String info = githubOAuthService.handleCallback(code);
            String[] parts = info.split("\\|", 2);
            Long userId = Long.valueOf(parts[1]);

            // 3. 发 Redis token
            String token = tokenService.createUserToken(userId);
            // 4. 重定向回前端，前端捕获 token 后登录
            String redirect = githubOAuthService.getFrontSuccessUrl()
                    + "?github_token=" + token;
            response.sendRedirect(redirect);
        } catch (Exception e) {
            log.error("GitHub 登录失败", e);
            response.sendRedirect(githubOAuthService.getFrontSuccessUrl()
                    + "?github_error=" + URLEncoder.encode(e.getMessage() == null ? "登录失败" : e.getMessage(), StandardCharsets.UTF_8));
        }
    }

    @GetMapping("/bindinfo")
    @Operation(summary = "判断当前用户是否为 GitHub 账号")
    public Result<Map<String, Object>> bindInfo(@RequestParam("token") String token) {
        Long userId = tokenService.verifyUserToken(token);
        if (userId == null) throw new BizException("token 无效");
        User user = userService.getById(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("isGithub", user != null && user.getUsername() != null && user.getUsername().startsWith("gh_"));
        return Result.success(data);
    }
}
