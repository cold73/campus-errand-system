package com.cold73.campuserrand.vo;

import com.cold73.campuserrand.entity.Order;
import com.cold73.campuserrand.entity.OrderReceive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单详情 VO
 * 聚合订单主信息（t_order）与收货信息（t_order_receive）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailVO {

    /** 订单主信息 */
    private Order order;

    /** 收货信息（可能为 null） */
    private OrderReceive receive;
}
