package com.travel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travel.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("`order`")
@Schema(description = "订单")
public class Order extends BaseEntity {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "订单总额")
    private BigDecimal totalAmount;

    @Schema(description = "实付金额")
    private BigDecimal payAmount;

    @Schema(description = "状态：0待支付 1已支付/待出行 2已完成 3已取消 4退款中 5已退款")
    private Integer status;

    @Schema(description = "支付方式")
    private String payType;

    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    @Schema(description = "出行日期")
    private LocalDate travelDate;

    @Schema(description = "联系人")
    private String contactName;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "出行人数")
    private Integer personCount;

    @Schema(description = "订单备注")
    private String remark;

    @Schema(description = "使用的优惠券id")
    private Long couponId;

    @Schema(description = "优惠金额")
    private BigDecimal couponAmount;

    @Schema(description = "退款原因")
    private String refundReason;
}
