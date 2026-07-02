package com.travel.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "产品查询请求")
public class ProductQueryRequest {
    @Schema(description = "关键词")
    private String keyword;
    @Schema(description = "分类id")
    private Long categoryId;
    @Schema(description = "最低价")
    private java.math.BigDecimal minPrice;
    @Schema(description = "最高价")
    private java.math.BigDecimal maxPrice;
    @Schema(description = "排序字段：price/sales/score")
    private String sortBy;
    @Schema(description = "排序方向：asc/desc")
    private String sortDir;
    @Schema(description = "页码")
    private Integer page = 1;
    @Schema(description = "每页条数")
    private Integer size = 10;
}
