package com.travel.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.BizException;
import com.travel.common.PageResult;
import com.travel.common.Result;
import com.travel.common.context.BaseContext;
import com.travel.dto.request.CreateOrderRequest;
import com.travel.entity.*;
import com.travel.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Tag(name = "订单", description = "下单、支付、取消、退款、订单列表")
public class OrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final ProductService productService;
    private final ProductScheduleService scheduleService;
    private final CartService cartService;
    private final UserCouponService userCouponService;
    private final CouponService couponService;

    @PostMapping("/create")
    @Operation(summary = "创建订单")
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> create(@RequestBody CreateOrderRequest req) {
        Long userId = BaseContext.getCurrentId();
        if (req.getProductId() == null) throw new BizException("请选择产品");
        Product product = productService.getById(req.getProductId());
        if (product == null || product.getStatus() == 0) throw new BizException("产品不存在或已下架");

        // 检查库存（产品本身或团期库存）
        LocalDate travelDate = req.getTravelDate() != null ? LocalDate.parse(req.getTravelDate()) : null;
        BigDecimal price = product.getPrice();
        if (req.getScheduleId() != null) {
            ProductSchedule schedule = scheduleService.getById(req.getScheduleId());
            if (schedule == null || schedule.getStock() < req.getPersonCount()) {
                throw new BizException("所选团期库存不足");
            }
            price = schedule.getPrice();
        }

        // 计算优惠
        BigDecimal couponAmount = BigDecimal.ZERO;
        if (req.getCouponId() != null) {
            UserCoupon uc = userCouponService.lambdaQuery()
                    .eq(UserCoupon::getId, req.getCouponId())
                    .eq(UserCoupon::getUserId, userId)
                    .eq(UserCoupon::getStatus, 0)
                    .one();
            if (uc == null) throw new BizException("优惠券不可用");
            Coupon coupon = couponService.getById(uc.getCouponId());
            if (coupon != null) {
                BigDecimal total = price.multiply(BigDecimal.valueOf(req.getPersonCount()));
                if (total.compareTo(coupon.getMinAmount()) >= 0) {
                    if (coupon.getType() == 1 || coupon.getType() == 3) { // 满减/直减
                        couponAmount = coupon.getFaceValue().min(total);
                    }
                }
                uc.setStatus(1);
                uc.setOrderId(null); // 将在订单创建后更新
                userCouponService.updateById(uc);
            }
        }

        BigDecimal totalAmount = price.multiply(BigDecimal.valueOf(req.getPersonCount()));
        BigDecimal payAmount = totalAmount.subtract(couponAmount).max(BigDecimal.ZERO);

        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(payAmount);
        order.setStatus(0); // 待支付
        order.setTravelDate(travelDate);
        order.setContactName(req.getContactName());
        order.setContactPhone(req.getContactPhone());
        order.setPersonCount(req.getPersonCount());
        order.setRemark(req.getRemark());
        order.setCouponId(req.getCouponId());
        order.setCouponAmount(couponAmount);
        orderService.save(order);

        // 创建订单明细
        OrderItem item = new OrderItem();
        item.setOrderId(order.getId());
        item.setProductId(product.getId());
        item.setProductName(product.getTitle());
        item.setProductCover(product.getCover());
        item.setTravelDate(travelDate);
        item.setPrice(price);
        item.setQuantity(req.getPersonCount());
        item.setSubtotal(totalAmount);
        orderItemService.save(item);

        // 更新优惠券的订单号
        if (req.getCouponId() != null) {
            userCouponService.lambdaUpdate()
                    .eq(UserCoupon::getId, req.getCouponId())
                    .set(UserCoupon::getOrderId, order.getId())
                    .update();
        }

        // 扣库存
        if (req.getScheduleId() != null) {
            scheduleService.lambdaUpdate()
                    .eq(ProductSchedule::getId, req.getScheduleId())
                    .setSql("stock = stock - " + req.getPersonCount())
                    .update();
        }
        productService.lambdaUpdate()
                .eq(Product::getId, product.getId())
                .setSql("sales = sales + " + req.getPersonCount())
                .update();

        return Result.success(Map.of("orderId", order.getId(), "orderNo", order.getOrderNo()));
    }

    @GetMapping("/pay/{orderId}")
    @Operation(summary = "模拟支付（直接成功）")
    public Result<Void> pay(@PathVariable Long orderId) {
        Long userId = BaseContext.getCurrentId();
        Order order = orderService.lambdaQuery()
                .eq(Order::getId, orderId)
                .eq(Order::getUserId, userId)
                .one();
        if (order == null) throw new BizException("订单不存在");
        if (order.getStatus() != 0) throw new BizException("订单状态异常");
        order.setStatus(1); // 已支付
        order.setPayType("mock");
        order.setPayTime(LocalDateTime.now());
        orderService.updateById(order);
        return Result.success();
    }

    @GetMapping("/my")
    @Operation(summary = "我的订单列表")
    public Result<PageResult<Order>> myOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        Long userId = BaseContext.getCurrentId();
        var qw = orderService.lambdaQuery()
                .eq(Order::getUserId, userId);
        if (status != null) qw.eq(Order::getStatus, status);
        qw.orderByDesc(Order::getCreateTime);
        Page<Order> result = orderService.page(new Page<>(page, size), qw);
        return Result.success(new PageResult<>(result));
    }

    @GetMapping("/detail/{orderId}")
    @Operation(summary = "订单详情（含明细）")
    public Result<Map<String, Object>> detail(@PathVariable Long orderId) {
        Long userId = BaseContext.getCurrentId();
        Order order = orderService.lambdaQuery()
                .eq(Order::getId, orderId)
                .eq(Order::getUserId, userId)
                .one();
        if (order == null) throw new BizException("订单不存在");
        List<OrderItem> items = orderItemService.lambdaQuery()
                .eq(OrderItem::getOrderId, orderId)
                .list();
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("items", items);
        return Result.success(data);
    }

    @PostMapping("/cancel/{orderId}")
    @Operation(summary = "取消订单")
    public Result<Void> cancel(@PathVariable Long orderId) {
        Long userId = BaseContext.getCurrentId();
        Order order = orderService.lambdaQuery()
                .eq(Order::getId, orderId)
                .eq(Order::getUserId, userId)
                .one();
        if (order == null) throw new BizException("订单不存在");
        if (order.getStatus() != 0) throw new BizException("只能取消待支付订单");
        order.setStatus(3);
        orderService.updateById(order);
        return Result.success();
    }

    @PostMapping("/refund/{orderId}")
    @Operation(summary = "申请退款")
    public Result<Void> applyRefund(@PathVariable Long orderId, @RequestBody(required = false) Map<String, String> body) {
        Long userId = BaseContext.getCurrentId();
        Order order = orderService.lambdaQuery()
                .eq(Order::getId, orderId)
                .eq(Order::getUserId, userId)
                .one();
        if (order == null) throw new BizException("订单不存在");
        if (order.getStatus() != 1 && order.getStatus() != 2) throw new BizException("该订单不可申请退款");
        order.setStatus(4);
        order.setRefundReason(body != null ? body.get("reason") : null);
        orderService.updateById(order);
        return Result.success();
    }

    /** 生成订单号：时间戳 + 用户id后4位 + 随机4位 */
    private String generateOrderNo() {
        Long userId = BaseContext.getCurrentId();
        String uid = String.format("%04d", userId % 10000);
        String rand = String.format("%04d", (int) (Math.random() * 10000));
        return "T" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + uid + rand;
    }
}
