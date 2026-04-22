package com.cold73.campuserrand.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 跑腿员"我的接单"列表 VO
 * 合并 t_order 的订单主信息与 t_runner_order 的跑腿员侧时间节点
 */
@Data
@NoArgsConstructor
public class MyTaskVO {

    /** 订单ID */
    private Long id;

    /** 订单编号 */
    private String orderNo;

    /** 订单标题 */
    private String title;

    /** 订单描述 */
    private String content;

    /** 订单类型：0-代拿快递，1-代买商品，2-代办事务，3-其他 */
    private Integer orderType;

    /** 订单金额 */
    private BigDecimal price;

    /** 小费 */
    private BigDecimal tip;

    /** 订单状态：0-待接单，1-已接单，2-进行中，3-已完成，4-已取消 */
    private Integer status;

    /** 期望完成时间 */
    private LocalDateTime expectFinishTime;

    /** 订单创建时间 */
    private LocalDateTime createTime;

    /** 接单时间（来自 t_runner_order.take_time） */
    private LocalDateTime takeTime;

    /** 取货时间（来自 t_runner_order.pickup_time） */
    private LocalDateTime pickupTime;
}
