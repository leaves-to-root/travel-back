package com.travel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travel.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_coupon")
@Schema(description = "用户优惠券")
public class UserCoupon extends BaseEntity {

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "优惠券id")
    private Long couponId;

    @Schema(description = "状态：0未使用 1已使用 2已过期")
    private Integer status;

    @Schema(description = "使用的订单id")
    private Long orderId;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;
}
