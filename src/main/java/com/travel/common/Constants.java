package com.travel.common;

/**
 * 全局常量
 */
public final class Constants {

    private Constants() {
    }

    /** Redis 中用户 token 前缀 */
    public static final String USER_TOKEN_PREFIX = "travel:token:user:";
    /** Redis 中管理员 token 前缀 */
    public static final String ADMIN_TOKEN_PREFIX = "travel:token:admin:";
    /** token 有效期（秒），7天 */
    public static final long TOKEN_EXPIRE = 60 * 60 * 24 * 7;

    /** 请求头 token 字段名 */
    public static final String TOKEN_HEADER = "Authorization";

    /** 用户角色 */
    public static final String ROLE_USER = "user";
    public static final String ROLE_ADMIN = "admin";

    /** 订单状态：0待支付 1已支付/待出行 2已完成 3已取消 4退款中 5已退款 */
    public static final int ORDER_UNPAID = 0;
    public static final int ORDER_PAID = 1;
    public static final int ORDER_FINISHED = 2;
    public static final int ORDER_CANCELLED = 3;
    public static final int ORDER_REFUNDING = 4;
    public static final int ORDER_REFUNDED = 5;

    /** 产品状态：0下架 1上架 */
    public static final int PRODUCT_OFFLINE = 0;
    public static final int PRODUCT_ONLINE = 1;

    /** 用户账号状态：0禁用 1正常 */
    public static final int STATUS_DISABLED = 0;
    public static final int STATUS_NORMAL = 1;
}
