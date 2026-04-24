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
 * 跑腿员收益记录实体（对应 t_runner_income 表）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_runner_income")
public class RunnerIncome {

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 跑腿员ID */
    private Long runnerId;

    /** 关联订单ID */
    private Long orderId;

    /** 收入金额（price + tip） */
    private BigDecimal amount;

    /** 结算状态：0-待结算，1-已结算 */
    private Integer status;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
