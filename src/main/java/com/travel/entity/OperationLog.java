package com.travel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("operation_log")
@Schema(description = "管理员操作日志")
public class OperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long adminId;
    private String adminName;
    private String module;
    private String action;
    private String method;
    private String params;
    private String ip;
    private Long costMs;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}