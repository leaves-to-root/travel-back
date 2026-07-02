package com.travel.interceptor;

import com.travel.common.Constants;
import com.travel.common.Result;
import com.travel.common.context.BaseContext;
import com.travel.common.context.LoginUser;
import com.travel.common.context.TokenService;
import com.travel.entity.Admin;
import com.travel.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 管理员登录拦截器：守卫 /api/admin/** 接口
 */
@Component
@RequiredArgsConstructor
public class AdminInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;
    private final AdminService adminService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader(Constants.TOKEN_HEADER);
        Long adminId = tokenService.verifyAdminToken(token);
        if (adminId == null) {
            writeUnauthorized(response);
            return false;
        }
        Admin admin = adminService.getById(adminId);
        if (admin == null || admin.getStatus() == Constants.STATUS_DISABLED) {
            tokenService.removeAdminToken(token);
            writeUnauthorized(response);
            return false;
        }
        BaseContext.setCurrent(LoginUser.builder()
                .id(admin.getId())
                .role(Constants.ROLE_ADMIN)
                .username(admin.getUsername())
                .nickname(admin.getNickname())
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
