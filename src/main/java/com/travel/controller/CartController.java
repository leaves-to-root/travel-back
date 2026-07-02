package com.travel.controller;

import com.travel.common.Result;
import com.travel.common.context.BaseContext;
import com.travel.entity.Cart;
import com.travel.entity.Product;
import com.travel.service.CartService;
import com.travel.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "购物车", description = "购物车增删改查、数量调整")
public class CartController {

    private final CartService cartService;
    private final ProductService productService;

    @GetMapping("/list")
    @Operation(summary = "我的购物车列表（含产品信息）")
    public Result<List<Map<String, Object>>> list() {
        Long userId = BaseContext.getCurrentId();
        List<Cart> carts = cartService.lambdaQuery()
                .eq(Cart::getUserId, userId)
                .orderByDesc(Cart::getCreateTime)
                .list();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Cart c : carts) {
            Product p = productService.getById(c.getProductId());
            Map<String, Object> item = new java.util.HashMap<>();
            item.put("cartId", c.getId());
            item.put("productId", c.getProductId());
            item.put("productTitle", p != null ? p.getTitle() : null);
            item.put("productCover", p != null ? p.getCover() : null);
            item.put("price", p != null ? p.getPrice() : null);
            item.put("travelDate", c.getTravelDate());
            item.put("quantity", c.getQuantity());
            result.add(item);
        }
        return Result.success(result);
    }

    @PostMapping("/add")
    @Operation(summary = "加入购物车")
    public Result<Void> add(@RequestBody Map<String, Object> body) {
        Long userId = BaseContext.getCurrentId();
        Long productId = Long.valueOf(body.get("productId").toString());
        String travelDate = (String) body.get("travelDate");
        Integer quantity = body.get("quantity") != null ? Integer.valueOf(body.get("quantity").toString()) : 1;
        // 检查是否已存在
        Cart exist = cartService.lambdaQuery()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getProductId, productId)
                .one();
        if (exist != null) {
            exist.setQuantity(exist.getQuantity() + quantity);
            cartService.updateById(exist);
        } else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            if (travelDate != null) cart.setTravelDate(java.time.LocalDate.parse(travelDate));
            cart.setQuantity(quantity);
            cartService.save(cart);
        }
        return Result.success();
    }

    @PostMapping("/update")
    @Operation(summary = "更新购物车数量")
    public Result<Void> update(@RequestBody Map<String, Object> body) {
        Long userId = BaseContext.getCurrentId();
        Long cartId = Long.valueOf(body.get("cartId").toString());
        Integer quantity = Integer.valueOf(body.get("quantity").toString());
        Cart cart = cartService.lambdaQuery()
                .eq(Cart::getId, cartId)
                .eq(Cart::getUserId, userId)
                .one();
        if (cart != null) {
            cart.setQuantity(quantity);
            cartService.updateById(cart);
        }
        return Result.success();
    }

    @DeleteMapping("/remove/{cartId}")
    @Operation(summary = "删除购物车项")
    public Result<Void> remove(@PathVariable Long cartId) {
        Long userId = BaseContext.getCurrentId();
        cartService.lambdaQuery()
                .eq(Cart::getId, cartId)
                .eq(Cart::getUserId, userId)
                .oneOpt()
                .ifPresent(c -> cartService.removeById(c.getId()));
        return Result.success();
    }
}
