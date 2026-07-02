package com.travel.interceptor;

import com.travel.common.Constants;
import com.travel.common.Result;
import com.travel.common.context.BaseContext;
import com.travel.common.context.LoginUser;
import com.travel.common.context.TokenService;
import com.travel.entity.User;
import com.travel.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用户登录拦截器：守卫需要登录的用户端接口
 */
@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader(Constants.TOKEN_HEADER);
        Long userId = tokenService.verifyUserToken(token);
        if (userId == null) {
            writeUnauthorized(response);
            return false;
        }
        User user = userService.getById(userId);
        if (user == null || user.getStatus() == Constants.STATUS_DISABLED) {
            tokenService.removeUserToken(token);
            writeUnauthorized(response);
            return false;
        }
        BaseContext.setCurrent(LoginUser.builder()
                .id(user.getId())
                .role(Constants.ROLE_USER)
                .username(user.getUsername())
                .nickname(user.getNickname())
                .build());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        BaseContext.remove();
    }

    private void writeUnauthorized(HttpServletResponse response) throws Exception {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(401, "未登录或登录已过期")));
    }
}
