package com.travel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.entity.Admin;

public interface AdminService extends IService<Admin> {

    /** 按账号查找 */
    Admin getByUsername(String username);
}
