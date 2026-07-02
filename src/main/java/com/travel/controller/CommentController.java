package com.travel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.BizException;
import com.travel.common.PageResult;
import com.travel.common.Result;
import com.travel.common.context.BaseContext;
import com.travel.dto.request.SubmitCommentRequest;
import com.travel.entity.Comment;
import com.travel.entity.Order;
import com.travel.service.CommentService;
import com.travel.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
@Tag(name = "评论评分")
public class CommentController {

    private final CommentService commentService;
    private final OrderService orderService;

    @GetMapping("/product/{productId}")
    @Operation(summary = "产品的评价列表")
    public Result<PageResult<Comment>> productComments(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Comment> p = commentService.lambdaQuery()
                .eq(Comment::getProductId, productId)
                .orderByDesc(Comment::getCreateTime)
                .page(new Page<>(page, size));
        return Result.success(new PageResult<>(p));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交评价")
    public Result<Void> submit(@RequestBody SubmitCommentRequest req) {
        Long userId = BaseContext.getCurrentId();
        if (req.getProductId() == null || req.getScore() == null) throw new BizException("参数不完整");
        if (req.getScore() < 1 || req.getScore() > 5) throw new BizException("评分范围1-5");
        if (req.getOrderId() != null) {
            Order order = orderService.getById(req.getOrderId());
            if (order == null || !order.getUserId().equals(userId) || order.getStatus() != 2) {
                throw new BizException("只能评价已完成的订单");
            }
            long count = commentService.lambdaQuery()
                    .eq(Comment::getOrderId, req.getOrderId())
                    .count();
            if (count > 0) throw new BizException("该订单已评价");
        }
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setProductId(req.getProductId());
        comment.setOrderId(req.getOrderId());
        comment.setScore(req.getScore());
        comment.setContent(req.getContent());
        comment.setImages(req.getImages());
        commentService.save(comment);
        return Result.success();
    }
}
