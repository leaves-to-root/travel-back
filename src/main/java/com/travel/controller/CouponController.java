package com.travel.controller;

import com.travel.common.Result;
import com.travel.common.context.BaseContext;
import com.travel.entity.Coupon;
import com.travel.entity.UserCoupon;
import com.travel.service.CouponService;
import com.travel.service.UserCouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
@Tag(name = "优惠券", description = "领取优惠券、我的优惠券")
public class CouponController {

    private final CouponService couponService;
    private final UserCouponService userCouponService;

    @GetMapping("/list")
    @Operation(summary = "可领优惠券列表")
    public Result<List<Coupon>> list() {
        List<Coupon> list = couponService.lambdaQuery()
                .eq(Coupon::getStatus, 1)
                .le(Coupon::getStartTime, LocalDateTime.now())
                .ge(Coupon::getEndTime, LocalDateTime.now())
                .orderByAsc(Coupon::getFaceValue)
                .list();
        return Result.success(list);
    }

    @PostMapping("/grab/{couponId}")
    @Operation(summary = "领取优惠券")
    public Result<Void> grab(@PathVariable Long couponId) {
        Long userId = BaseContext.getCurrentId();
        // 检查是否已领过
        long count = userCouponService.lambdaQuery()
                .eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getCouponId, couponId)
                .count();
        if (count > 0) throw new com.travel.common.BizException("不可重复领取");
        Coupon coupon = couponService.getById(couponId);
        if (coupon == null || coupon.getStatus() == 0) throw new com.travel.common.BizException("优惠券不存在");
        if (coupon.getRemainCount() != null && coupon.getRemainCount() <= 0) throw new com.travel.common.BizException("已领完");
        // 领取
        UserCoupon uc = new UserCoupon();
        uc.setUserId(userId);
        uc.setCouponId(couponId);
        uc.setStatus(0);
        uc.setExpireTime(LocalDateTime.now().plusDays(coupon.getValidDays() != null ? coupon.getValidDays() : 30));
        userCouponService.save(uc);
        // 扣减库存
        if (coupon.getTotalCount() > 0) {
            couponService.lambdaUpdate()
                    .eq(Coupon::getId, couponId)
                    .setSql("remain_count = remain_count - 1")
                    .update();
        }
        return Result.success();
    }

    @GetMapping("/mine")
    @Operation(summary = "我的优惠券")
    public Result<List<UserCoupon>> mine() {
        Long userId = BaseContext.getCurrentId();
        List<UserCoupon> list = userCouponService.lambdaQuery()
                .eq(UserCoupon::getUserId, userId)
                .orderByDesc(UserCoupon::getCreateTime)
                .list();
        return Result.success(list);
    }
}
