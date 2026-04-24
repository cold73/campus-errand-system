package com.cold73.campuserrand.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 跑腿员收益汇总 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomeSummaryVO {

    /** 累计总收入 */
    private BigDecimal totalIncome;

    /** 已结算收入 */
    private BigDecimal settledIncome;

    /** 收入订单数量 */
    private Integer orderCount;
}
