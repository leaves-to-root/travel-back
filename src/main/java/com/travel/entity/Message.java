package com.travel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travel.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("message")
@Schema(description = "站内消息")
public class Message extends BaseEntity {

    @Schema(description = "接收用户id")
    private Long userId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "类型：1系统 2订单 3互动")
    private Integer type;

    @Schema(description = "是否已读：0未读 1已读")
    private Integer isRead;
}
