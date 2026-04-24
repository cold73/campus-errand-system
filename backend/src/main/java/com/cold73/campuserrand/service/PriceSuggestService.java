package com.cold73.campuserrand.service;

import com.cold73.campuserrand.dto.PriceSuggestRequestDTO;
import com.cold73.campuserrand.vo.PriceSuggestVO;

/**
 * 智能定价建议 Service 接口
 *
 * v1：规则 + 关键词匹配（当前实现：RulePriceSuggestServiceImpl）
 * v2：可替换为 LLM 语义理解，新增 LlmPriceSuggestServiceImpl 并切换注入即可
 */
public interface PriceSuggestService {

    /**
     * 根据任务信息估算建议价格
     *
     * @param dto 估价请求（orderType、urgencyLevel、content 必填）
     * @return 建议价格 + 建议小费 + 分项说明
     */
    PriceSuggestVO suggest(PriceSuggestRequestDTO dto);
}
