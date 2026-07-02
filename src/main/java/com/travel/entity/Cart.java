package com.travel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travel.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cart")
@Schema(description = "购物车")
public class Cart extends BaseEntity {

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "产品id")
    private Long productId;

    @Schema(description = "团期id")
    private Long scheduleId;

    @Schema(description = "出行日期")
    private LocalDate travelDate;

    @Schema(description = "数量")
    private Integer quantity;
}
