package com.travel.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.BizException;
import com.travel.common.PageResult;
import com.travel.common.Result;
import com.travel.entity.Message;
import com.travel.entity.User;
import com.travel.service.MessageService;
import com.travel.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/message")
@RequiredArgsConstructor
@Tag(name = "管理员-系统消息", description = "发送系统消息、查看消息列表")
public class AdminMessageController {

    private final MessageService messageService;
    private final UserService userService;

    @PostMapping("/send")
    @Operation(summary = "发送系统消息（给指定用户）")
    public Result<Void> send(@RequestBody Map<String, Object> body) {
        String title = (String) body.get("title");
        String content = (String) body.get("content");
        Object userIdObj = body.get("userId");

        if (StrUtil.isBlank(title) || StrUtil.isBlank(content)) {
            throw new BizException("标题和内容不能为空");
        }

        if (userIdObj instanceof List<?> userIdList) {
            // 发送给多个用户
            for (Object id : userIdList) {
                if (id instanceof Number num) {
                    createMessage(num.longValue(), title, content);
                }
            }
        } else if (userIdObj instanceof Number num) {
            createMessage(num.longValue(), title, content);
        } else {
            throw new BizException("请指定接收用户");
        }
        return Result.success();
    }

    @PostMapping("/send-all")
    @Operation(summary = "发送系统消息（给所有用户）")
    public Result<Void> sendAll(@RequestBody Map<String, String> body) {
        String title = body.get("title");
        String content = body.get("content");
        if (StrUtil.isBlank(title) || StrUtil.isBlank(content)) {
            throw new BizException("标题和内容不能为空");
        }
        List<User> users = userService.list();
        for (User user : users) {
            createMessage(user.getId(), title, content);
        }
        return Result.success();
    }

    @GetMapping("/list")
    @Operation(summary = "系统消息列表（按发送时间倒序）")
    public Result<PageResult<Message>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Page<Message> p = messageService.lambdaQuery()
                .orderByDesc(Message::getCreateTime)
                .page(new Page<>(page, size));
        return Result.success(new PageResult<>(p));
    }

    private void createMessage(Long userId, String title, String content) {
        Message msg = new Message();
        msg.setUserId(userId);
        msg.setTitle(title);
        msg.setContent(content);
        msg.setType(1); // 系统消息
        msg.setIsRead(0);
        messageService.save(msg);
    }
}
