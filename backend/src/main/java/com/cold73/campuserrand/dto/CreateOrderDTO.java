package com.cold73.campuserrand.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 创建订单请求 DTO
 * 承载前端提交的订单信息（订单主体 + 收货信息）
 */
@Data
public class CreateOrderDTO {

    /** 下单用户ID（TODO: 后续接入 JWT 后从登录态获取，当前由前端传入） */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /** 订单标题 */
    @NotBlank(message = "订单标题不能为空")
    private String title;

    /** 订单描述 / 备注 */
    private String content;

    /** 订单类型：0-代拿快递，1-代买商品，2-代办事务，3-其他 */
    @NotNull(message = "订单类型不能为空")
    private Integer orderType;

    /** 订单总金额 */
    @NotNull(message = "订单金额不能为空")
    private BigDecimal price;

    /** 跑腿费 / 小费（可选，默认0） */
    private BigDecimal tip;

    /** 期望完成时间（可选） */
    private LocalDateTime expectFinishTime;

    /** 收货人姓名 */
    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    /** 收货人电话 */
    @NotBlank(message = "收货人电话不能为空")
    private String receiverPhone;

    /** 收货地址 */
    @NotBlank(message = "收货地址不能为空")
    private String receiverAddress;

    /** 取货地址（可选） */
    private String pickupAddress;
}
