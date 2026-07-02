package com.travel.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.PageResult;
import com.travel.common.Result;
import com.travel.dto.request.ProductQueryRequest;
import com.travel.entity.Product;
import com.travel.entity.ProductSchedule;
import com.travel.service.ProductScheduleService;
import com.travel.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Tag(name = "旅游产品", description = "产品列表、详情、热门推荐、筛选排序")
public class ProductController {

    private final ProductService productService;
    private final ProductScheduleService scheduleService;

    @PostMapping("/list")
    @Operation(summary = "产品分页列表（含筛选）")
    public Result<PageResult<Product>> list(@RequestBody ProductQueryRequest req) {
        LambdaQueryWrapper<Product> qw = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1);
        if (req.getCategoryId() != null) {
            qw.eq(Product::getCategoryId, req.getCategoryId());
        }
        if (StrUtil.isNotBlank(req.getKeyword())) {
            qw.and(w -> w.like(Product::getTitle, req.getKeyword())
                    .or().like(Product::getDestination, req.getKeyword())
                    .or().like(Product::getTags, req.getKeyword()));
        }
        if (req.getMinPrice() != null) qw.ge(Product::getPrice, req.getMinPrice());
        if (req.getMaxPrice() != null) qw.le(Product::getPrice, req.getMaxPrice());

        // 排序
        if ("price".equals(req.getSortBy())) {
            qw.orderBy(true, "asc".equals(req.getSortDir()), Product::getPrice);
        } else if ("sales".equals(req.getSortBy())) {
            qw.orderBy(true, "desc".equals(req.getSortDir()), Product::getSales);
        } else if ("score".equals(req.getSortBy())) {
            qw.orderBy(true, "desc".equals(req.getSortDir()), Product::getScore);
        } else {
            qw.orderByDesc(Product::getSales);
        }

        Page<Product> page = productService.page(new Page<>(req.getPage(), req.getSize()), qw);
        return Result.success(new PageResult<>(page));
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "产品详情")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null || product.getStatus() == 0) {
            return Result.error("产品不存在");
        }
        List<ProductSchedule> schedules = scheduleService.lambdaQuery()
                .eq(ProductSchedule::getProductId, id)
                .ge(ProductSchedule::getTravelDate, java.time.LocalDate.now())
                .orderByAsc(ProductSchedule::getTravelDate)
                .list();

        Map<String, Object> data = new HashMap<>();
        data.put("product", product);
        data.put("schedules", schedules);
        return Result.success(data);
    }

    @GetMapping("/hot")
    @Operation(summary = "热门产品推荐")
    public Result<List<Product>> hot() {
        List<Product> list = productService.lambdaQuery()
                .eq(Product::getStatus, 1)
                .eq(Product::getIsHot, 1)
                .orderByDesc(Product::getSales)
                .last("LIMIT 6")
                .list();
        return Result.success(list);
    }
}
