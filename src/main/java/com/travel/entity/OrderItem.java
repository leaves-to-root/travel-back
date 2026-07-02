package com.travel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travel.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order_item")
@Schema(description = "订单明细")
public class OrderItem extends BaseEntity {

    @Schema(description = "订单id")
    private Long orderId;

    @Schema(description = "产品id")
    private Long productId;

    @Schema(description = "产品名快照")
    private String productName;

    @Schema(description = "产品封面快照")
    private String productCover;

    @Schema(description = "出行日期")
    private LocalDate travelDate;

    @Schema(description = "单价")
    private BigDecimal price;

    @Schema(description = "数量")
    private Integer quantity;

    @Schema(description = "小计")
    private BigDecimal subtotal;
}
