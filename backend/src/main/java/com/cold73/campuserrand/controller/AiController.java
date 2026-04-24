package com.cold73.campuserrand.controller;

import com.cold73.campuserrand.common.Result;
import com.cold73.campuserrand.dto.PriceSuggestRequestDTO;
import com.cold73.campuserrand.service.PriceSuggestService;
import com.cold73.campuserrand.vo.PriceSuggestVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * AI 能力入口
 *
 * 当前 v1：基于规则 + 关键词匹配的智能定价建议（RulePriceSuggestServiceImpl）
 * 未来 v2：可替换为 LLM 语义理解（deepseek via openclaw），
 *          新增 LlmPriceSuggestServiceImpl 实现 PriceSuggestService 接口并切换注入即可
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final PriceSuggestService priceSuggestService;

    /**
     * 智能定价建议
     *
     * @param dto 任务信息（content 必填，orderType/urgencyLevel 必填）
     * @return 建议价格、建议小费、分项说明
     */
    @PostMapping("/suggest-price")
    public Result<PriceSuggestVO> suggestPrice(@RequestBody @Valid PriceSuggestRequestDTO dto) {
        return Result.success(priceSuggestService.suggest(dto));
    }
}
