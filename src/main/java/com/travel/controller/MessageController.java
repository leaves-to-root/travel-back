package com.travel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.PageResult;
import com.travel.common.Result;
import com.travel.common.context.BaseContext;
import com.travel.entity.Message;
import com.travel.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
@Tag(name = "站内消息")
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/list")
    @Operation(summary = "我的消息列表")
    public Result<PageResult<Message>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = BaseContext.getCurrentId();
        Page<Message> p = messageService.lambdaQuery()
                .eq(Message::getUserId, userId)
                .orderByDesc(Message::getCreateTime)
                .page(new Page<>(page, size));
        return Result.success(new PageResult<>(p));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "未读消息数")
    public Result<Long> unreadCount() {
        Long userId = BaseContext.getCurrentId();
        long count = messageService.lambdaQuery()
                .eq(Message::getUserId, userId)
                .eq(Message::getIsRead, 0)
                .count();
        return Result.success(count);
    }

    @PutMapping("/read/{messageId}")
    @Operation(summary = "标记已读")
    public Result<Void> markRead(@PathVariable Long messageId) {
        messageService.lambdaUpdate()
                .eq(Message::getId, messageId)
                .set(Message::getIsRead, 1)
                .update();
        return Result.success();
    }

    @PutMapping("/read-all")
    @Operation(summary = "全部标记已读")
    public Result<Void> markAllRead() {
        Long userId = BaseContext.getCurrentId();
        messageService.lambdaUpdate()
                .eq(Message::getUserId, userId)
                .eq(Message::getIsRead, 0)
                .set(Message::getIsRead, 1)
                .update();
        return Result.success();
    }
}
