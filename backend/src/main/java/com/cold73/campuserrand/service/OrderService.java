package com.cold73.campuserrand.service;

import com.cold73.campuserrand.dto.CreateOrderDTO;
import com.cold73.campuserrand.dto.FinishOrderDTO;
import com.cold73.campuserrand.dto.PickupOrderDTO;
import com.cold73.campuserrand.dto.TakeOrderDTO;
import com.cold73.campuserrand.entity.Order;
import com.cold73.campuserrand.vo.MyTaskVO;
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

    /**
     * 查询跑腿大厅订单列表（status = 0 待接单，按创建时间倒序）
     *
     * @return 可接订单列表
     */
    List<Order> listAvailableOrders();

    /**
     * 跑腿员取货确认：
     * 订单状态 1(已接单) → 2(进行中)，接单关系状态 0(已接单) → 1(进行中)，记录取货时间
     * 校验失败时抛出 BusinessException
     *
     * @param dto 取货参数（orderId + runnerId）
     * @return 接单关系记录的 ID
     */
    Long pickupOrder(PickupOrderDTO dto);

    /**
     * 跑腿员完成订单：
     * 订单状态 2(进行中) → 3(已完成)，接单关系状态 1(进行中) → 2(已完成)，记录完成时间
     * 校验失败时抛出 BusinessException
     *
     * @param dto 完成参数（orderId + runnerId）
     * @return 接单关系记录的 ID
     */
    Long finishOrder(FinishOrderDTO dto);

    /**
     * 查询某跑腿员的"我的接单"列表
     * 按接单时间倒序，包含该跑腿员历史所有接过的订单
     *
     * @param runnerId 跑腿员ID
     * @return 合并订单主信息 + 接单时间节点的 VO 列表
     */
    List<MyTaskVO> listTasksByRunnerId(Long runnerId);
}
