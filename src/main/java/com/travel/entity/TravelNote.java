package com.travel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travel.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("travel_note")
@Schema(description = "游记")
public class TravelNote extends BaseEntity {

    @Schema(description = "作者id")
    private Long userId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "正文")
    private String content;

    @Schema(description = "图片集")
    private String images;

    @Schema(description = "视频URL")
    private String video;

    @Schema(description = "关联产品")
    private Long productId;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "浏览量")
    private Integer viewCount;

    @Schema(description = "状态：0待审核 1已发布 2已下架")
    private Integer status;
}
