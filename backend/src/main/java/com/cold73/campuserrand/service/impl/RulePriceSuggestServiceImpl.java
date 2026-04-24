package com.cold73.campuserrand.service.impl;

import com.cold73.campuserrand.dto.PriceSuggestRequestDTO;
import com.cold73.campuserrand.service.PriceSuggestService;
import com.cold73.campuserrand.vo.PriceSuggestVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 智能定价 v1：规则 + 关键词匹配
 *
 * 定价规则：
 *   基础价 3.00
 *   + 任务类型加价（代拿快递 +1 / 代买商品 +2 / 代办事务 +1.5 / 其他 +1）
 *   + 紧急等级加价（普通 +0 / 紧急 +2 / 超急 +4）
 *   + 语义关键词加价（排队/盖章 +2，打印/多件/代买 +1，马上/尽快/急 +1，楼上/宿舍 +0.5，可叠加）
 *   上下限保护：[3, 50]
 *
 * v2 扩展点：新增 LlmPriceSuggestServiceImpl 实现同一接口，注入时替换本类即可。
 */
@Service
public class RulePriceSuggestServiceImpl implements PriceSuggestService {

    private static final BigDecimal BASE   = new BigDecimal("3.00");
    private static final BigDecimal FLOOR  = new BigDecimal("3.00");
    private static final BigDecimal CEIL   = new BigDecimal("50.00");

    @Override
    public PriceSuggestVO suggest(PriceSuggestRequestDTO dto) {
        List<String> parts = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        // 1. 基础价
        total = total.add(BASE);
        parts.add("基础 " + fmt(BASE));

        // 2. 任务类型加价
        BigDecimal typeAdd = typePrice(dto.getOrderType());
        String typeLabel   = typeLabel(dto.getOrderType());
        total = total.add(typeAdd);
        parts.add(typeLabel + " " + fmt(typeAdd));

        // 3. 紧急等级加价
        BigDecimal urgencyAdd = urgencyPrice(dto.getUrgencyLevel());
        if (urgencyAdd.compareTo(BigDecimal.ZERO) > 0) {
            String urgencyLabel = urgencyLabel(dto.getUrgencyLevel());
            total = total.add(urgencyAdd);
            parts.add(urgencyLabel + " " + fmt(urgencyAdd));
        }

        // 4. 语义关键词加价（叠加）
        String text = (dto.getContent() == null ? "" : dto.getContent())
                    + (dto.getTitle()   == null ? "" : dto.getTitle());

        total = addKeyword(text, "排队",  new BigDecimal("2.0"), total, parts);
        total = addKeyword(text, "盖章",  new BigDecimal("2.0"), total, parts);
        total = addKeyword(text, "打印",  new BigDecimal("1.0"), total, parts);
        total = addKeyword(text, "多件",  new BigDecimal("1.0"), total, parts);
        total = addKeyword(text, "代买",  new BigDecimal("1.0"), total, parts);
        total = addKeyword(text, "马上",  new BigDecimal("1.0"), total, parts);
        total = addKeyword(text, "尽快",  new BigDecimal("1.0"), total, parts);
        total = addKeyword(text, "急",    new BigDecimal("1.0"), total, parts);
        total = addKeyword(text, "楼上",  new BigDecimal("0.5"), total, parts);
        total = addKeyword(text, "宿舍",  new BigDecimal("0.5"), total, parts);

        // 5. 上下限保护
        total = total.max(FLOOR).min(CEIL);

        // 6. 小费建议
        BigDecimal tip = tipByUrgency(dto.getUrgencyLevel());

        // 7. 分项 reason
        BigDecimal finalTotal = total.setScale(2, RoundingMode.HALF_UP);
        String reason = String.join(" + ", parts) + " = " + fmt(finalTotal);

        return new PriceSuggestVO(finalTotal, tip, reason);
    }

    // -------- 辅助方法 --------

    private BigDecimal typePrice(Integer orderType) {
        if (orderType == null) return new BigDecimal("1.0");
        switch (orderType) {
            case 0: return new BigDecimal("1.0");
            case 1: return new BigDecimal("2.0");
            case 2: return new BigDecimal("1.5");
            default: return new BigDecimal("1.0");
        }
    }

    private String typeLabel(Integer orderType) {
        if (orderType == null) return "其他";
        switch (orderType) {
            case 0: return "代拿快递";
            case 1: return "代买商品";
            case 2: return "代办事务";
            default: return "其他";
        }
    }

    private BigDecimal urgencyPrice(Integer urgencyLevel) {
        if (urgencyLevel == null) return BigDecimal.ZERO;
        switch (urgencyLevel) {
            case 1: return new BigDecimal("2.0");
            case 2: return new BigDecimal("4.0");
            default: return BigDecimal.ZERO;
        }
    }

    private String urgencyLabel(Integer urgencyLevel) {
        if (urgencyLevel == null) return "";
        switch (urgencyLevel) {
            case 1: return "紧急";
            case 2: return "超急";
            default: return "";
        }
    }

    private BigDecimal tipByUrgency(Integer urgencyLevel) {
        if (urgencyLevel == null) return BigDecimal.ZERO;
        switch (urgencyLevel) {
            case 1: return new BigDecimal("1.00");
            case 2: return new BigDecimal("2.00");
            default: return BigDecimal.ZERO;
        }
    }

    private BigDecimal addKeyword(String text, String keyword, BigDecimal add,
                                  BigDecimal current, List<String> parts) {
        if (text.contains(keyword)) {
            parts.add("关键词「" + keyword + "」" + fmt(add));
            return current.add(add);
        }
        return current;
    }

    private String fmt(BigDecimal val) {
        return val.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
