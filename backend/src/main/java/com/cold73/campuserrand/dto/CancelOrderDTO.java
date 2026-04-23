package com.cold73.campuserrand.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 用户取消订单请求 DTO
 * 仅支持取消状态为 0（待接单）的订单
 */
@Data
public class CancelOrderDTO {

    /** 订单ID */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /** 下单用户ID（TODO: 后续接入 JWT 后从登录态获取，当前由前端传入） */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
}
