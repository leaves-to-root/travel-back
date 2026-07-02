package com.travel.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.BizException;
import com.travel.common.PageResult;
import com.travel.common.Result;
import com.travel.common.context.BaseContext;
import com.travel.dto.request.PublishNoteRequest;
import com.travel.entity.TravelNote;
import com.travel.service.TravelNoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/note")
@RequiredArgsConstructor
@Tag(name = "游记", description = "游记发布、浏览、我的游记")
public class TravelNoteController {

    private final TravelNoteService noteService;

    @GetMapping("/list")
    @Operation(summary = "游记列表（已发布的）")
    public Result<PageResult<TravelNote>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<TravelNote> p = noteService.lambdaQuery()
                .eq(TravelNote::getStatus, 1)
                .orderByDesc(TravelNote::getCreateTime)
                .page(new Page<>(page, size));
        return Result.success(new PageResult<>(p));
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "游记详情（增加浏览量）")
    public Result<TravelNote> detail(@PathVariable Long id) {
        TravelNote note = noteService.getById(id);
        if (note == null || note.getStatus() == 0) throw new BizException("游记不存在");
        noteService.lambdaUpdate()
                .eq(TravelNote::getId, id)
                .setSql("view_count = view_count + 1")
                .update();
        note.setViewCount(note.getViewCount() + 1);
        return Result.success(note);
    }

    @PostMapping("/publish")
    @Operation(summary = "发布游记")
    public Result<Void> publish(@RequestBody PublishNoteRequest req) {
        if (StrUtil.isBlank(req.getTitle())) throw new BizException("标题不能为空");
        TravelNote note = new TravelNote();
        note.setUserId(BaseContext.getCurrentId());
        note.setTitle(req.getTitle());
        note.setContent(req.getContent());
        note.setImages(req.getImages());
        note.setProductId(req.getProductId());
        note.setStatus(1); // 直接发布（无需审核）
        noteService.save(note);
        return Result.success();
    }

    @GetMapping("/mine")
    @Operation(summary = "我的游记")
    public Result<PageResult<TravelNote>> mine(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = BaseContext.getCurrentId();
        Page<TravelNote> p = noteService.lambdaQuery()
                .eq(TravelNote::getUserId, userId)
                .orderByDesc(TravelNote::getCreateTime)
                .page(new Page<>(page, size));
        return Result.success(new PageResult<>(p));
    }
}
