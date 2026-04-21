package com.cold73.campuserrand.service;

import com.cold73.campuserrand.dto.CreateOrderDTO;

/**
 * 订单业务 Service 接口
 */
public interface OrderService {

    /**
     * 创建订单：保存订单主表 + 收货信息
     *
     * @param dto 创建订单请求参数
     * @return 新建订单的 ID
     */
    Long createOrder(CreateOrderDTO dto);
}
