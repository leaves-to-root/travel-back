package com.travel.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/** 全局异常处理 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public Result<Void> handleBiz(BizException e) { log.warn("业务异常: {}", e.getMessage()); return Result.error(e.getCode(), e.getMessage()); }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValid(MethodArgumentNotValidException e) { FieldError fe = e.getBindingResult().getFieldError(); return Result.error(ResultCode.PARAM_ERROR.getCode(), fe != null ? fe.getDefaultMessage() : "参数错误"); }

    @ExceptionHandler(BindException.class)
    public Result<Void> handleBind(BindException e) { FieldError fe = e.getBindingResult().getFieldError(); return Result.error(ResultCode.PARAM_ERROR.getCode(), fe != null ? fe.getDefaultMessage() : "参数错误"); }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<Void> handleMissingParam(MissingServletRequestParameterException e) { return Result.error(ResultCode.PARAM_ERROR.getCode(), "缺少参数: " + e.getParameterName()); }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<Void> handleTypeMismatch(MethodArgumentTypeMismatchException e) { return Result.error(ResultCode.PARAM_ERROR.getCode(), "参数类型错误: " + e.getName()); }

    /** 忽略静态资源 404（如 favicon.ico）*/
    @ExceptionHandler(NoResourceFoundException.class)
    public Result<Void> handleNoResource(NoResourceFoundException e) { return Result.error(ResultCode.DATA_NOT_FOUND); }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) { log.error("系统异常", e); return Result.error("系统繁忙，请稍后再试"); }
}
