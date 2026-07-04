package com.travel.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.BizException;
import com.travel.common.PageResult;
import com.travel.common.Result;
import com.travel.common.annotation.OpLog;
import com.travel.entity.Coupon;
import com.travel.entity.User;
import com.travel.entity.UserCoupon;
import com.travel.service.CouponService;
import com.travel.service.UserCouponService;
import com.travel.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
@Tag(name = "后台-用户管理")
public class AdminUserController {

    private final UserService userService;
    private final CouponService couponService;
    private final UserCouponService userCouponService;

    @GetMapping("/list")
    @Operation(summary = "用户列表")
    public Result<PageResult<User>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        Page<User> p = userService.lambdaQuery()
                .like(keyword != null, User::getNickname, keyword)
                .or(keyword != null)
                .like(keyword != null, User::getPhone, keyword)
                .orderByDesc(User::getCreateTime)
                .page(new Page<>(page, size));
        p.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(new PageResult<>(p));
    }

    @PutMapping("/status/{userId}")
    @Operation(summary = "启用/禁用用户")
    @OpLog(module = "用户管理", action = "启用/禁用用户")
    public Result<Void> toggleStatus(@PathVariable Long userId, @RequestBody Map<String, Integer> body) {
        User user = userService.getById(userId);
        if (user != null) {
            user.setStatus(body.get("status"));
            userService.updateById(user);
        }
        return Result.success();
    }
    @PostMapping("/coupon/{userId}")
    @Operation(summary = "给用户分发优惠券")
    public Result<Void> distributeCoupon(@PathVariable Long userId, @RequestBody Map<String, Long> body) {
        Long couponId = body.get("couponId");
        if (couponId == null) throw new BizException("请选择优惠券");

        // 检查用户是否存在
        User user = userService.getById(userId);
        if (user == null) throw new BizException("用户不存在");

        // 检查优惠券是否存在且有库存
        Coupon coupon = couponService.getById(couponId);
        if (coupon == null || coupon.getStatus() == 0) throw new BizException("优惠券不存在或已禁用");
        if (coupon.getRemainCount() != null && coupon.getTotalCount() > 0 && coupon.getRemainCount() <= 0) {
            throw new BizException("优惠券库存不足");
        }

        // 创建用户优惠券
        UserCoupon uc = new UserCoupon();
        uc.setUserId(userId);
        uc.setCouponId(couponId);
        uc.setStatus(0); // 未使用
        uc.setExpireTime(LocalDateTime.now().plusDays(coupon.getValidDays() != null ? coupon.getValidDays() : 30));
        userCouponService.save(uc);

        // 更新优惠券库存
        if (coupon.getTotalCount() > 0) {
            couponService.lambdaUpdate()
                    .eq(Coupon::getId, couponId)
                    .setSql("remain_count = remain_count - 1")
                    .update();
        }

        return Result.success();
    }
}
