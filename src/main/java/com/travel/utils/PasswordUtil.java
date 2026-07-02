package com.travel.utils;

import cn.hutool.crypto.digest.BCrypt;

/**
 * 密码加密工具（基于 Hutool 的 BCrypt 实现）
 */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    /** 加密明文密码 */
    public static String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    /** 校验明文密码与加密密文是否匹配 */
    public static boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}
