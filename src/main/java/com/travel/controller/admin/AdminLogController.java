package com.travel.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.PageResult;
import com.travel.common.Result;
import com.travel.entity.OperationLog;
import com.travel.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/log")
@RequiredArgsConstructor
@Tag(name = "后台-操作日志", description = "操作日志列表查询")
public class AdminLogController {

    private final OperationLogService logService;

    @GetMapping("/list")
    @Operation(summary = "操作日志列表")
    public Result<PageResult<OperationLog>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<OperationLog> p = logService.lambdaQuery()
                .orderByDesc(OperationLog::getCreateTime)
                .page(new Page<>(page, size));
        return Result.success(new PageResult<>(p));
    }
}
