package com.travel.controller.admin;

import com.travel.common.Result;
import com.travel.entity.Banner;
import com.travel.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/banner")
@RequiredArgsConstructor
@Tag(name = "后台-轮播图管理")
public class AdminBannerController {

    private final BannerService bannerService;

    @GetMapping("/list")
    @Operation(summary = "轮播图列表")
    public Result<List<Banner>> list() {
        List<Banner> list = bannerService.lambdaQuery()
                .orderByAsc(Banner::getSort)
                .list();
        return Result.success(list);
    }

    @PostMapping("/save")
    @Operation(summary = "新增/修改轮播图")
    public Result<Void> save(@RequestBody Banner banner) {
        if (banner.getId() == null) banner.setStatus(1);
        bannerService.saveOrUpdate(banner);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除轮播图")
    public Result<Void> delete(@PathVariable Long id) {
        bannerService.removeById(id);
        return Result.success();
    }
}
