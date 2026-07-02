package com.travel.controller;

import com.travel.common.Result;
import com.travel.entity.Banner;
import com.travel.entity.Product;
import com.travel.service.BannerService;
import com.travel.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
@Tag(name = "首页")
public class HomeController {

    private final BannerService bannerService;
    private final ProductService productService;

    @GetMapping("/data")
    @Operation(summary = "获取首页数据（轮播图+热门推荐+特价优惠）")
    public Result<Map<String, Object>> homeData() {
        List<Banner> banners = bannerService.lambdaQuery()
                .eq(Banner::getStatus, 1)
                .orderByAsc(Banner::getSort)
                .list();

        List<Product> hotProducts = productService.lambdaQuery()
                .eq(Product::getStatus, 1)
                .eq(Product::getIsHot, 1)
                .orderByDesc(Product::getSales)
                .last("LIMIT 6")
                .list();

        List<Product> specialProducts = productService.lambdaQuery()
                .eq(Product::getStatus, 1)
                .eq(Product::getIsSpecial, 1)
                .orderByAsc(Product::getPrice)
                .last("LIMIT 6")
                .list();

        Map<String, Object> data = new HashMap<>();
        data.put("banners", banners);
        data.put("hotProducts", hotProducts);
        data.put("specialProducts", specialProducts);
        return Result.success(data);
    }
}
