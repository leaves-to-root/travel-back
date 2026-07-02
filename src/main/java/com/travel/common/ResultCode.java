package com.travel.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),

    // 鉴权相关 4xx
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    LOGIN_FAILED(401, "用户名或密码错误"),
    ACCOUNT_DISABLED(403, "账号已被禁用"),
    USER_EXISTS(400, "用户已存在"),
    USER_NOT_EXISTS(400, "用户不存在"),

    // 业务相关
    PARAM_ERROR(400, "参数错误"),
    DATA_NOT_FOUND(404, "数据不存在"),
    STOCK_NOT_ENOUGH(400, "库存不足"),
    ORDER_STATUS_ERROR(400, "订单状态异常"),
    COUPON_USED(400, "优惠券已使用"),
    COUPON_EXPIRED(400, "优惠券已过期"),
    HAS_COMMENTED(400, "已评价过该订单"),
    REPEAT_OPERATION(400, "请勿重复操作");

    private final Integer code;
    private final String msg;
}
