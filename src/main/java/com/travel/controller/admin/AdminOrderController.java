package com.travel.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.PageResult;
import com.travel.common.Result;
import com.travel.entity.Order;
import com.travel.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/order")
@RequiredArgsConstructor
@Tag(name = "后台-订单管理", description = "订单列表、确认、退款审核")
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping("/list")
    @Operation(summary = "订单列表")
    public Result<PageResult<Order>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        Page<Order> p = orderService.lambdaQuery()
                .eq(status != null, Order::getStatus, status)
                .like(keyword != null, Order::getOrderNo, keyword)
                .orderByDesc(Order::getCreateTime)
                .page(new Page<>(page, size));
        return Result.success(new PageResult<>(p));
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "订单详情")
    public Result<Order> detail(@PathVariable Long id) {
        return Result.success(orderService.getById(id));
    }

    @PostMapping("/confirm/{id}")
    @Operation(summary = "确认订单（标记已完成）")
    public Result<Void> confirm(@PathVariable Long id) {
        orderService.lambdaUpdate()
                .eq(Order::getId, id)
                .set(Order::getStatus, 2)
                .update();
        return Result.success();
    }

    @PostMapping("/refund/{id}")
    @Operation(summary = "退款审核通过")
    public Result<Void> approveRefund(@PathVariable Long id) {
        orderService.lambdaUpdate()
                .eq(Order::getId, id)
                .eq(Order::getStatus, 4)
                .set(Order::getStatus, 5)
                .update();
        return Result.success();
    }
}
