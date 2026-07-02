package com.travel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.entity.User;

public interface UserService extends IService<User> {

    /** 按用户名查找 */
    User getByUsername(String username);

    /** 按手机号查找 */
    User getByPhone(String phone);
}
