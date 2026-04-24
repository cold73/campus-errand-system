package com.cold73.campuserrand.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PriceSuggestRequestDTO {

    /** 订单标题（可选，辅助语义理解） */
    private String title;

    /** 任务内容描述（必填，关键词匹配来源） */
    @NotBlank(message = "任务内容不能为空")
    private String content;

    /** 订单类型：0-代拿快递，1-代买商品，2-代办事务，3-其他 */
    @NotNull(message = "订单类型不能为空")
    private Integer orderType;

    /** 紧急等级：0-普通，1-紧急，2-超急 */
    @NotNull(message = "紧急等级不能为空")
    private Integer urgencyLevel;
}
