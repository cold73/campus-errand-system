package com.cold73.campuserrand.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceSuggestVO {

    /** 建议价格 */
    private BigDecimal suggestPrice;

    /** 建议小费 */
    private BigDecimal suggestTip;

    /** 分项说明，格式：基础 3.00 + 代买商品 2.00 + ... = 10.00 */
    private String reason;
}
