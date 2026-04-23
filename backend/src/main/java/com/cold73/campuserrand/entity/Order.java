package com.cold73.campuserrand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体（对应 t_order 表）
 * 订单主表，存储订单基础信息与状态流转
 * 注意：表名 t_order 加反引号避免与 SQL 关键字 ORDER 冲突
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("`t_order`")
public class Order {

    /** 订单ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单编号（业务唯一） */
    private String orderNo;

    /** 下单用户ID */
    private Long userId;

    /** 订单标题 */
    private String title;

    /** 订单描述 / 备注 */
    private String content;

    /** 订单类型：0-代拿快递，1-代买商品，2-代办事务，3-其他 */
    private Integer orderType;

    /** 订单总金额 */
    private BigDecimal price;

    /** 跑腿费 / 小费 */
    private BigDecimal tip;

    /** 状态：0-待接单，1-已接单，2-进行中，3-已完成，4-已取消 */
    private Integer status;

    /** 紧急等级：0-普通，1-紧急，2-超急 */
    private Integer urgencyLevel;

    /** 期望完成时间 */
    private LocalDateTime expectFinishTime;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
