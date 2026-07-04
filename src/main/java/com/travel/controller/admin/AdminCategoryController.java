package com.travel.controller.admin;

import com.travel.common.Result;
import com.travel.common.annotation.OpLog;
import com.travel.entity.Category;
import com.travel.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/category")
@RequiredArgsConstructor
@Tag(name = "后台-分类管理", description = "分类增删改查")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping("/list")
    @Operation(summary = "分类列表（全部）")
    public Result<List<Category>> list() {
        List<Category> list = categoryService.lambdaQuery()
                .orderByAsc(Category::getSort)
                .list();
        return Result.success(list);
    }

    @PostMapping("/save")
    @Operation(summary = "新增/修改分类")
    @OpLog(module = "分类管理", action = "新增/修改分类")
    public Result<Void> save(@RequestBody Category category) {
        if (category.getId() == null) {
            category.setStatus(1);
        }
        categoryService.saveOrUpdate(category);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除分类")
    @OpLog(module = "分类管理", action = "删除分类")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.removeById(id);
        return Result.success();
    }
}