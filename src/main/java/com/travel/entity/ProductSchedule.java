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
@TableName("product_schedule")
@Schema(description = "产品团期")
public class ProductSchedule extends BaseEntity {

    @Schema(description = "产品id")
    private Long productId;

    @Schema(description = "出行日期")
    private LocalDate travelDate;

    @Schema(description = "当日价格")
    private BigDecimal price;

    @Schema(description = "当日库存")
    private Integer stock;
}
