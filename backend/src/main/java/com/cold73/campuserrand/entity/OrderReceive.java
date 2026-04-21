package com.cold73.campuserrand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 订单收货信息实体（对应 t_order_receive 表）
 * 存储订单对应的取货 / 收货人地址信息（与订单一对一）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_order_receive")
public class OrderReceive {

    /** 收货信息ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单ID（t_order.id） */
    private Long orderId;

    /** 收货人姓名 */
    private String receiverName;

    /** 收货人电话 */
    private String receiverPhone;

    /** 收货地址 */
    private String receiverAddress;

    /** 取货地址 */
    private String pickupAddress;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
