package com.travel.dto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "用户优惠券VO")
public class UserCouponVO {

    @Schema(description = "用户优惠券id")
    private Long id;

    @Schema(description = "优惠券id")
    private Long couponId;

    @Schema(description = "优惠券名称")
    private String name;

    @Schema(description = "优惠券类型：1满减 2折扣 3直减")
    private Integer type;

    @Schema(description = "面额/折扣率")
    private BigDecimal faceValue;

    @Schema(description = "使用门槛")
    private BigDecimal minAmount;

    @Schema(description = "状态：0未使用 1已使用 2已过期")
    private Integer status;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}