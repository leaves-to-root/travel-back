package com.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.entity.UserCoupon;
import com.travel.mapper.UserCouponMapper;
import com.travel.service.UserCouponService;
import org.springframework.stereotype.Service;

@Service
public class UserCouponServiceImpl extends ServiceImpl<UserCouponMapper, UserCoupon> implements UserCouponService {
}
