package com.travel.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "提交评价请求")
public class SubmitCommentRequest {
    @Schema(description = "产品id")
    private Long productId;
    @Schema(description = "订单id")
    private Long orderId;
    @Schema(description = "评分1-5")
    private Integer score;
    @Schema(description = "评价内容")
    private String content;
    @Schema(description = "图片（JSON数组）")
    private String images;
}
