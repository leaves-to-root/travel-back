package com.travel.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.Constants;
import com.travel.common.PageResult;
import com.travel.common.Result;
import com.travel.entity.Product;
import com.travel.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/product")
@RequiredArgsConstructor
@Tag(name = "后台-产品管理", description = "产品CRUD、上下架")
public class AdminProductController {

    private final ProductService productService;

    @GetMapping("/list")
    @Operation(summary = "产品列表")
    public Result<PageResult<Product>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        Page<Product> p = productService.lambdaQuery()
                .like(keyword != null, Product::getTitle, keyword)
                .orderByDesc(Product::getCreateTime)
                .page(new Page<>(page, size));
        return Result.success(new PageResult<>(p));
    }

    @PostMapping("/save")
    @Operation(summary = "新增/修改产品")
    public Result<Void> save(@RequestBody Product product) {
        if (product.getId() == null) product.setStatus(Constants.PRODUCT_ONLINE);
        productService.saveOrUpdate(product);
        return Result.success();
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "产品详情")
    public Result<Product> detail(@PathVariable Long id) {
        return Result.success(productService.getById(id));
    }

    @PutMapping("/status/{id}")
    @Operation(summary = "上下架")
    public Result<Void> toggleStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        productService.lambdaUpdate()
                .eq(Product::getId, id)
                .set(Product::getStatus, body.get("status"))
                .update();
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除产品")
    public Result<Void> delete(@PathVariable Long id) {
        productService.removeById(id);
        return Result.success();
    }
}
