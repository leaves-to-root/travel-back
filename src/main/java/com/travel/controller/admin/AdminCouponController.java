package com.travel.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.PageResult;
import com.travel.common.Result;
import com.travel.entity.Coupon;
import com.travel.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/coupon")
@RequiredArgsConstructor
@Tag(name = "后台-优惠券管理", description = "优惠券CRUD、发放管理")
public class AdminCouponController {

    private final CouponService couponService;

    @GetMapping("/list")
    @Operation(summary = "优惠券列表")
    public Result<PageResult<Coupon>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Coupon> p = couponService.page(new Page<>(page, size));
        return Result.success(new PageResult<>(p));
    }

    @PostMapping("/save")
    @Operation(summary = "新增/修改优惠券")
    public Result<Void> save(@RequestBody Coupon coupon) {
        if (coupon.getId() == null) {
            coupon.setRemainCount(coupon.getTotalCount());
        }
        couponService.saveOrUpdate(coupon);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除优惠券")
    public Result<Void> delete(@PathVariable Long id) {
        couponService.removeById(id);
        return Result.success();
    }
}
