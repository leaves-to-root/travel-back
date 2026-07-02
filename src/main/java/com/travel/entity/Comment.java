package com.travel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travel.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("comment")
@Schema(description = "评论评分")
public class Comment extends BaseEntity {

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "产品id")
    private Long productId;

    @Schema(description = "订单id")
    private Long orderId;

    @Schema(description = "评分1-5")
    private Integer score;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "评论图片")
    private String images;

    @Schema(description = "点赞数")
    private Integer likeCount;
}
