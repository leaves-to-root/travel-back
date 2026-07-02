package com.travel.controller;

import com.travel.common.Result;
import com.travel.entity.Category;
import com.travel.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
@Tag(name = "分类")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/tree")
    @Operation(summary = "获取分类树（两级）")
    public Result<List<Category>> tree() {
        List<Category> all = categoryService.lambdaQuery()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort)
                .list();
        return Result.success(all);
    }

    @GetMapping("/list")
    @Operation(summary = "获取顶级分类")
    public Result<List<Category>> list() {
        List<Category> list = categoryService.lambdaQuery()
                .eq(Category::getParentId, 0)
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort)
                .list();
        return Result.success(list);
    }
}
