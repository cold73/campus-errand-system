package com.cold73.campuserrand.service;

import com.cold73.campuserrand.dto.CreateOrderDTO;
import com.cold73.campuserrand.dto.TakeOrderDTO;
import com.cold73.campuserrand.entity.Order;
import com.cold73.campuserrand.vo.OrderDetailVO;

import java.util.List;

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

    /**
     * 查询指定用户的订单列表（按 id 倒序）
     *
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> listByUserId(Long userId);

    /**
     * 查询订单详情（主信息 + 收货信息）
     *
     * @param id 订单ID
     * @return 订单详情；订单不存在时返回 null
     */
    OrderDetailVO getDetail(Long id);

    /**
     * 跑腿员接单：插入接单关系记录，并将订单状态推进到"已接单"
     * 校验失败时抛出 BusinessException
     *
     * @param dto 接单请求参数（orderId + runnerId）
     * @return 新建接单关系记录的 ID
     */
    Long takeOrder(TakeOrderDTO dto);
}
