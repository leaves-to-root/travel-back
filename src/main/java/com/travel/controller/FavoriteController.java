package com.travel.controller;

import com.travel.common.Result;
import com.travel.common.context.BaseContext;
import com.travel.entity.Favorite;
import com.travel.entity.Product;
import com.travel.service.FavoriteService;
import com.travel.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
@Tag(name = "收藏")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final ProductService productService;

    @PostMapping("/toggle")
    @Operation(summary = "切换收藏状态")
    public Result<Map<String, Object>> toggle(@RequestBody Map<String, Long> body) {
        Long userId = BaseContext.getCurrentId();
        Long productId = body.get("productId");
        if (productId == null) return Result.error("参数错误");
        Favorite exist = favoriteService.lambdaQuery()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId)
                .one();
        boolean nowFav;
        if (exist != null) {
            favoriteService.physicalDelete(userId, productId);
            nowFav = false;
        } else {
            favoriteService.physicalDelete(userId, productId);
            Favorite fav = new Favorite();
            fav.setUserId(userId);
            fav.setProductId(productId);
            favoriteService.save(fav);
            nowFav = true;
        }
        return Result.success(Map.of("favorited", nowFav));
    }

    @GetMapping("/status/{productId}")
    @Operation(summary = "查询是否已收藏（需要登录）")
    public Result<Map<String, Object>> status(@PathVariable Long productId) {
        Long userId = BaseContext.getCurrentId();
        boolean fav = favoriteService.lambdaQuery()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId)
                .count() > 0;
        return Result.success(Map.of("favorited", fav));
    }

    @GetMapping("/list")
    @Operation(summary = "我的收藏列表")
    public Result<List<Product>> list() {
        Long userId = BaseContext.getCurrentId();
        List<Favorite> favs = favoriteService.lambdaQuery()
                .eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreateTime)
                .list();
        List<Long> productIds = favs.stream().map(Favorite::getProductId).collect(Collectors.toList());
        if (productIds.isEmpty()) return Result.success(List.of());
        List<Product> products = productService.listByIds(productIds);
        return Result.success(products);
    }
}