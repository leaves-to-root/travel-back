package com.travel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travel.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("category")
@Schema(description = "分类")
public class Category extends BaseEntity {

    @Schema(description = "父分类id，0为顶级")
    private Long parentId;

    @Schema(description = "分类名")
    private String name;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;
}
