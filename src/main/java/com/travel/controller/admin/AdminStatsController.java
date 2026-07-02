package com.travel.controller.admin;

import com.travel.common.Result;
import com.travel.entity.Order;
import com.travel.entity.Product;
import com.travel.entity.User;
import com.travel.service.OrderService;
import com.travel.service.ProductService;
import com.travel.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
@Tag(name = "后台-数据统计")
public class AdminStatsController {

    private final OrderService orderService;
    private final UserService userService;
    private final ProductService productService;

    @GetMapping("/dashboard")
    @Operation(summary = "管理后台仪表盘数据")
    public Result<Map<String, Object>> dashboard() {
        long totalUsers = userService.count();
        long totalOrders = orderService.count();

        long todayOrders = orderService.lambdaQuery()
                .ge(Order::getCreateTime, LocalDateTime.now().withHour(0).withMinute(0).withSecond(0))
                .count();

        BigDecimal totalRevenue = orderService.lambdaQuery()
                .in(Order::getStatus, 1, 2)
                .list()
                .stream()
                .map(o -> o.getPayAmount() != null ? o.getPayAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long newUsersToday = userService.lambdaQuery()
                .ge(User::getCreateTime, LocalDateTime.now().withHour(0).withMinute(0).withSecond(0))
                .count();

        List<Product> hotProducts = productService.lambdaQuery()
                .eq(Product::getStatus, 1)
                .orderByDesc(Product::getSales)
                .last("LIMIT 5")
                .list();

        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers", totalUsers);
        data.put("newUsersToday", newUsersToday);
        data.put("totalOrders", totalOrders);
        data.put("todayOrders", todayOrders);
        data.put("totalRevenue", totalRevenue);
        data.put("hotProducts", hotProducts);
        return Result.success(data);
    }
}
