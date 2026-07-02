package com.travel.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;

@Data
@Schema(description = "创建订单请求")
public class CreateOrderRequest {
    @Schema(description = "产品id")
    private Long productId;
    @Schema(description = "团期id")
    private Long scheduleId;
    @Schema(description = "出行日期")
    private String travelDate;
    @Schema(description = "联系人")
    private String contactName;
    @Schema(description = "联系电话")
    private String contactPhone;
    @Schema(description = "出行人数")
    private Integer personCount = 1;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "优惠券id（可选）")
    private Long couponId;
}
