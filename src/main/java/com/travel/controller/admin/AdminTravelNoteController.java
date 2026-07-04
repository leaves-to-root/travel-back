package com.travel.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.PageResult;
import com.travel.common.Result;
import com.travel.common.annotation.OpLog;
import com.travel.entity.TravelNote;
import com.travel.service.TravelNoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/note")
@RequiredArgsConstructor
@Tag(name = "后台-游记管理", description = "游记审核、上下架、删除")
public class AdminTravelNoteController {

    private final TravelNoteService noteService;

    @GetMapping("/list")
    @Operation(summary = "游记列表")
    public Result<PageResult<TravelNote>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        Page<TravelNote> p = noteService.lambdaQuery()
                .eq(status != null, TravelNote::getStatus, status)
                .orderByDesc(TravelNote::getCreateTime)
                .page(new Page<>(page, size));
        return Result.success(new PageResult<>(p));
    }

    @PutMapping("/status/{id}")
    @Operation(summary = "审核游记状态")
    @OpLog(module = "游记管理", action = "审核游记")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        noteService.lambdaUpdate()
                .eq(TravelNote::getId, id)
                .set(TravelNote::getStatus, body.get("status"))
                .update();
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除游记")
    @OpLog(module = "游记管理", action = "删除游记")
    public Result<Void> delete(@PathVariable Long id) {
        noteService.removeById(id);
        return Result.success();
    }
}