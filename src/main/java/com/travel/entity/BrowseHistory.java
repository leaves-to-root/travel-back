package com.travel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travel.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("browse_history")
@Schema(description = "浏览历史")
public class BrowseHistory extends BaseEntity {

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "产品id")
    private Long productId;
}
