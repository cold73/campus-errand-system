package com.cold73.campuserrand.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收益明细记录 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomeRecordVO {

    /** 记录ID */
    private Long id;

    /** 关联订单ID */
    private Long orderId;

    /** 收入金额 */
    private BigDecimal amount;

    /** 结算状态：0-待结算，1-已结算 */
    private Integer status;

    /** 备注 */
    private String remark;

    /** 入账时间 */
    private LocalDateTime createTime;
}
