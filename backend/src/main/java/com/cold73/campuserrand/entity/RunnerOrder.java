package com.cold73.campuserrand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 接单关系实体（对应 t_runner_order 表）
 * 跑腿员与订单的接单关系，承载状态流转与时间节点
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_runner_order")
public class RunnerOrder {

    /** 接单关系ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单ID（t_order.id） */
    private Long orderId;

    /** 跑腿员ID（t_runner.id） */
    private Long runnerId;

    /** 状态：0-已接单，1-进行中，2-已完成，3-已取消（以 prompt 约定为准） */
    private Integer status;

    /** 接单时间 */
    private LocalDateTime takeTime;

    /** 取货时间 */
    private LocalDateTime pickupTime;

    /** 完成时间 */
    private LocalDateTime finishTime;

    /** 取消时间 */
    private LocalDateTime cancelTime;

    /** 取消原因 */
    private String cancelReason;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
