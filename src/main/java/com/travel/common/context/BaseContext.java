package com.travel.common.context;

/**
 * 当前登录用户上下文信息（基于 ThreadLocal）
 */
public class BaseContext {

    private static final ThreadLocal<LoginUser> CONTEXT = new ThreadLocal<>();

    public static void setCurrent(LoginUser user) {
        CONTEXT.set(user);
    }

    public static LoginUser getCurrent() {
        return CONTEXT.get();
    }

    public static Long getCurrentId() {
        LoginUser user = CONTEXT.get();
        return user == null ? null : user.getId();
    }

    public static void remove() {
        CONTEXT.remove();
    }
}
