package com.travel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travel.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product")
@Schema(description = "旅游产品")
public class Product extends BaseEntity {

    @Schema(description = "分类id")
    private Long categoryId;

    @Schema(description = "产品标题")
    private String title;

    @Schema(description = "副标题")
    private String subtitle;

    @Schema(description = "目的地")
    private String destination;

    @Schema(description = "出发地")
    private String departure;

    @Schema(description = "现价")
    private BigDecimal price;

    @Schema(description = "市场价")
    private BigDecimal marketPrice;

    @Schema(description = "库存")
    private Integer stock;

    @Schema(description = "封面图")
    private String cover;

    @Schema(description = "图片集（JSON数组）")
    private String images;

    @Schema(description = "图文详情")
    private String detail;

    @Schema(description = "费用说明")
    private String costExplain;

    @Schema(description = "行程天数")
    private Integer days;

    @Schema(description = "标签")
    private String tags;

    @Schema(description = "评分")
    private BigDecimal score;

    @Schema(description = "销量")
    private Integer sales;

    @Schema(description = "状态：0下架 1上架")
    private Integer status;

    @Schema(description = "是否热门")
    private Integer isHot;

    @Schema(description = "是否特价")
    private Integer isSpecial;
}
