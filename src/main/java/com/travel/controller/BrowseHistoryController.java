package com.travel.controller;

import com.travel.common.Result;
import com.travel.common.context.BaseContext;
import com.travel.entity.BrowseHistory;
import com.travel.entity.Product;
import com.travel.service.BrowseHistoryService;
import com.travel.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
@Tag(name = "浏览历史", description = "记录浏览、历史列表")
public class BrowseHistoryController {

    private final BrowseHistoryService historyService;
    private final ProductService productService;

    @PostMapping("/add")
    @Operation(summary = "记录浏览（登录用户）")
    public Result<Void> add(@RequestBody Map<String, Long> body) {
        Long userId = BaseContext.getCurrentId();
        Long productId = body.get("productId");
        if (productId == null) return Result.error("参数错误");
        historyService.lambdaUpdate()
                .eq(BrowseHistory::getUserId, userId)
                .eq(BrowseHistory::getProductId, productId)
                .remove();
        BrowseHistory bh = new BrowseHistory();
        bh.setUserId(userId);
        bh.setProductId(productId);
        historyService.save(bh);
        return Result.success();
    }

    @GetMapping("/list")
    @Operation(summary = "浏览历史列表")
    public Result<List<Product>> list() {
        Long userId = BaseContext.getCurrentId();
        List<BrowseHistory> histories = historyService.lambdaQuery()
                .eq(BrowseHistory::getUserId, userId)
                .orderByDesc(BrowseHistory::getCreateTime)
                .last("LIMIT 30")
                .list();
        List<Long> productIds = histories.stream()
                .map(BrowseHistory::getProductId)
                .collect(Collectors.toList());
        if (productIds.isEmpty()) return Result.success(List.of());
        List<Product> products = productService.listByIds(productIds);
        return Result.success(products);
    }
}
