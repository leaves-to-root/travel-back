package com.travel.common.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 当前登录用户信息（存入 Redis 的最小化对象）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {

    /** 主键 id（用户或管理员） */
    private Long id;

    /** 角色：user / admin */
    private String role;

    /** 用户名/账号 */
    private String username;

    /** 昵称 */
    private String nickname;
}
