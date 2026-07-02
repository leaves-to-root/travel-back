package com.travel.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "发布游记请求")
public class PublishNoteRequest {
    @Schema(description = "标题")
    private String title;
    @Schema(description = "正文")
    private String content;
    @Schema(description = "图片（JSON数组）")
    private String images;
    @Schema(description = "关联产品id（可选）")
    private Long productId;
}
