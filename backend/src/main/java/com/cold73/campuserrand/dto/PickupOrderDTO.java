package com.cold73.campuserrand.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 取货确认请求 DTO
 */
@Data
public class PickupOrderDTO {

    /** 订单ID */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /** 跑腿员ID（TODO: 后续接入 JWT 后从登录态获取，当前由前端传入） */
    @NotNull(message = "跑腿员ID不能为空")
    private Long runnerId;
}
