package com.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.entity.Coupon;
import com.travel.mapper.CouponMapper;
import com.travel.service.CouponService;
import org.springframework.stereotype.Service;

@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {
}
