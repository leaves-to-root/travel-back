package com.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.entity.Admin;
import com.travel.mapper.AdminMapper;
import com.travel.service.AdminService;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Override
    public Admin getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getUsername, username).last("LIMIT 1"));
    }
}
