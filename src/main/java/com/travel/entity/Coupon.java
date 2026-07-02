package com.travel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travel.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("coupon")
@Schema(description = "优惠券")
public class Coupon extends BaseEntity {

    @Schema(description = "优惠券名称")
    private String name;

    @Schema(description = "类型：1满减 2折扣 3直减")
    private Integer type;

    @Schema(description = "面额")
    private BigDecimal faceValue;

    @Schema(description = "使用门槛")
    private BigDecimal minAmount;

    @Schema(description = "发放总量")
    private Integer totalCount;

    @Schema(description = "剩余数量")
    private Integer remainCount;

    @Schema(description = "领取开始时间")
    private LocalDateTime startTime;

    @Schema(description = "领取结束时间")
    private LocalDateTime endTime;

    @Schema(description = "领取后有效天数")
    private Integer validDays;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;
}
