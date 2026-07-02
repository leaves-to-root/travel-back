package com.travel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travel.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("banner")
@Schema(description = "首页轮播图")
public class Banner extends BaseEntity {

    @Schema(description = "图片URL")
    private String image;

    @Schema(description = "点击跳转链接")
    private String link;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;
}
