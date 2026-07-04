package com.travel.common.aspect;

import com.travel.common.annotation.OpLog;
import com.travel.common.context.BaseContext;
import com.travel.common.context.LoginUser;
import com.travel.entity.OperationLog;
import com.travel.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OpLogAspect {

    private final OperationLogService operationLogService;
    private final ObjectMapper objectMapper;

    @Around("@annotation(opLog)")
    public Object around(ProceedingJoinPoint joinPoint, OpLog opLog) throws Throwable {
        long start = System.currentTimeMillis();
        OperationLog operationLog = new OperationLog();

        LoginUser currentUser = BaseContext.getCurrent();
        if (currentUser != null) {
            operationLog.setAdminId(currentUser.getId());
            operationLog.setAdminName(currentUser.getNickname() != null ? currentUser.getNickname() : currentUser.getUsername());
        }

        operationLog.setModule(opLog.module());
        operationLog.setAction(opLog.action());

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        operationLog.setMethod(signature.getDeclaringType().getSimpleName() + "." + signature.getName());

        try {
            String params = objectMapper.writeValueAsString(joinPoint.getArgs());
            if (params.length() > 2000) {
                params = params.substring(0, 2000);
            }
            operationLog.setParams(params);
        } catch (Exception e) {
            operationLog.setParams("serialize failed");
        }

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Real-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            if (ip != null && ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }
            operationLog.setIp(ip);
        }

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            throw e;
        } finally {
            operationLog.setCostMs(System.currentTimeMillis() - start);
            try {
                operationLogService.save(operationLog);
            } catch (Exception e) {
                log.error("save operation log failed", e);
            }
        }

        return result;
    }
}