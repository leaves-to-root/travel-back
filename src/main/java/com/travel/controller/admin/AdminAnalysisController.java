package com.travel.controller.admin;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.travel.common.Result;
import com.travel.entity.BrowseHistory;
import com.travel.entity.Category;
import com.travel.entity.Product;
import com.travel.entity.User;
import com.travel.service.BrowseHistoryService;
import com.travel.service.CategoryService;
import com.travel.service.ProductService;
import com.travel.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/analysis")
@RequiredArgsConstructor
@Tag(name = "管理员-用户分析", description = "用户浏览记录、偏好分类分析")
public class AdminAnalysisController {

    private final UserService userService;
    private final BrowseHistoryService browseHistoryService;
    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping("/users")
    @Operation(summary = "用户分析列表（含浏览总数 + 浏览次数最多的前3个分类）")
    public Result<List<Map<String, Object>>> userAnalysis() {
        List<User> users = userService.list();
        if (users.isEmpty()) return Result.success(List.of());

        // 一次性查出所有浏览记录
        List<BrowseHistory> allHistory = browseHistoryService.list();
        // 按 userId 分组
        Map<Long, List<BrowseHistory>> historyByUser = allHistory.stream()
                .collect(Collectors.groupingBy(BrowseHistory::getUserId));

        // 收集所有涉及到的产品 id，批量查询
        Set<Long> productIds = allHistory.stream()
                .map(BrowseHistory::getProductId)
                .collect(Collectors.toSet());
        Map<Long, Long> productCategoryMap = new HashMap<>();
        if (!productIds.isEmpty()) {
            List<Product> products = productService.listByIds(productIds);
            for (Product p : products) {
                productCategoryMap.put(p.getId(), p.getCategoryId());
            }
        }

        // 所有分类缓存（用于解析顶级分类名）
        Map<Long, Category> categoryMap = categoryService.list().stream()
                .collect(Collectors.toMap(Category::getId, c -> c, (a, b) -> a));

        List<Map<String, Object>> result = new ArrayList<>();
        for (User user : users) {
            List<BrowseHistory> histories = historyByUser.getOrDefault(user.getId(), Collections.emptyList());

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("userId", user.getId());
            row.put("username", user.getUsername());
            row.put("nickname", user.getNickname());
            row.put("avatar", user.getAvatar());
            row.put("viewCount", histories.size());

            // 按分类聚合浏览次数
            Map<Long, Long> categoryCount = new HashMap<>();
            for (BrowseHistory h : histories) {
                Long categoryId = productCategoryMap.get(h.getProductId());
                if (categoryId == null) continue;
                // 归并到顶级分类，便于用户可读
                Long rootId = resolveRootCategoryId(categoryId, categoryMap);
                categoryCount.merge(rootId, 1L, Long::sum);
            }

            // 取浏览次数最多的前 3 个分类
            List<Map<String, Object>> topCategories = categoryCount.entrySet().stream()
                    .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                    .limit(3)
                    .map(e -> {
                        Map<String, Object> c = new LinkedHashMap<>();
                        Category cat = categoryMap.get(e.getKey());
                        c.put("categoryId", e.getKey());
                        c.put("categoryName", cat != null ? cat.getName() : "未知分类");
                        c.put("count", e.getValue());
                        return c;
                    })
                    .collect(Collectors.toList());
            row.put("topCategories", topCategories);
            result.add(row);
        }
        return Result.success(result);
    }

    @GetMapping("/user/{userId}/history")
    @Operation(summary = "查看指定用户的浏览记录")
    public Result<List<Map<String, Object>>> userHistory(@PathVariable Long userId) {
        List<BrowseHistory> histories = browseHistoryService.lambdaQuery()
                .eq(BrowseHistory::getUserId, userId)
                .orderByDesc(BrowseHistory::getCreateTime)
                .last("LIMIT 50")
                .list();
        if (histories.isEmpty()) return Result.success(List.of());

        List<Long> productIds = histories.stream()
                .map(BrowseHistory::getProductId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, Product> productMap = productService.listByIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p, (a, b) -> a));

        List<Map<String, Object>> result = new ArrayList<>();
        for (BrowseHistory h : histories) {
            Product p = productMap.get(h.getProductId());
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("historyId", h.getId());
            row.put("productId", h.getProductId());
            row.put("title", p != null ? p.getTitle() : "（产品已下架）");
            row.put("cover", p != null ? p.getCover() : null);
            row.put("price", p != null ? p.getPrice() : null);
            row.put("viewTime", h.getCreateTime());
            result.add(row);
        }
        return Result.success(result);
    }

    /** 向上追溯到顶级分类（parentId=0），找不到则返回自身 */
    private Long resolveRootCategoryId(Long categoryId, Map<Long, Category> categoryMap) {
        Long current = categoryId;
        Set<Long> visited = new HashSet<>();
        while (current != null && categoryMap.containsKey(current)) {
            if (visited.contains(current)) break; // 防御循环
            visited.add(current);
            Category c = categoryMap.get(current);
            if (c.getParentId() == null || c.getParentId() == 0L) {
                return current;
            }
            current = c.getParentId();
        }
        return categoryId;
    }
}
