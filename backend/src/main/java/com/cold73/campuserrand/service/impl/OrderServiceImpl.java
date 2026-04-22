package com.cold73.campuserrand.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cold73.campuserrand.dto.CreateOrderDTO;
import com.cold73.campuserrand.dto.FinishOrderDTO;
import com.cold73.campuserrand.dto.PickupOrderDTO;
import com.cold73.campuserrand.dto.TakeOrderDTO;
import com.cold73.campuserrand.entity.Order;
import com.cold73.campuserrand.entity.OrderReceive;
import com.cold73.campuserrand.entity.RunnerOrder;
import com.cold73.campuserrand.exception.BusinessException;
import com.cold73.campuserrand.mapper.OrderMapper;
import com.cold73.campuserrand.mapper.OrderReceiveMapper;
import com.cold73.campuserrand.mapper.RunnerOrderMapper;
import com.cold73.campuserrand.service.OrderService;
import com.cold73.campuserrand.vo.MyTaskVO;
import com.cold73.campuserrand.vo.OrderDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 订单业务 Service 实现
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderReceiveMapper orderReceiveMapper;
    private final RunnerOrderMapper runnerOrderMapper;

    private static final DateTimeFormatter ORDER_NO_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(CreateOrderDTO dto) {
        // 1. 构建订单主表数据
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(dto.getUserId());
        order.setTitle(dto.getTitle());
        order.setContent(dto.getContent());
        order.setOrderType(dto.getOrderType());
        order.setPrice(dto.getPrice());
        order.setTip(dto.getTip() != null ? dto.getTip() : BigDecimal.ZERO);
        order.setStatus(0); // 0-待接单
        order.setExpectFinishTime(dto.getExpectFinishTime());

        orderMapper.insert(order);

        // 2. 构建收货信息（orderId 取自上一步自增回填的 order.id）
        OrderReceive receive = new OrderReceive();
        receive.setOrderId(order.getId());
        receive.setReceiverName(dto.getReceiverName());
        receive.setReceiverPhone(dto.getReceiverPhone());
        receive.setReceiverAddress(dto.getReceiverAddress());
        receive.setPickupAddress(dto.getPickupAddress());

        orderReceiveMapper.insert(receive);

        return order.getId();
    }

    @Override
    public List<Order> listByUserId(Long userId) {
        return orderMapper.selectList(
                Wrappers.<Order>lambdaQuery()
                        .eq(Order::getUserId, userId)
                        .orderByDesc(Order::getId)
        );
    }

    @Override
    public OrderDetailVO getDetail(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            return null;
        }
        OrderReceive receive = orderReceiveMapper.selectOne(
                Wrappers.<OrderReceive>lambdaQuery()
                        .eq(OrderReceive::getOrderId, id)
        );
        return new OrderDetailVO(order, receive);
    }

    @Override
    public List<Order> listAvailableOrders() {
        return orderMapper.selectList(
                Wrappers.<Order>lambdaQuery()
                        .eq(Order::getStatus, 0)
                        .orderByDesc(Order::getCreateTime)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long takeOrder(TakeOrderDTO dto) {
        // 1. 校验订单存在
        Order order = orderMapper.selectById(dto.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        // 2. 校验订单处于"待接单"状态
        if (order.getStatus() == null || order.getStatus() != 0) {
            throw new BusinessException("订单当前状态不允许接单");
        }

        // 3. 插入接单关系记录
        RunnerOrder runnerOrder = new RunnerOrder();
        runnerOrder.setOrderId(dto.getOrderId());
        runnerOrder.setRunnerId(dto.getRunnerId());
        runnerOrder.setStatus(0); // 0-已接单
        runnerOrder.setTakeTime(LocalDateTime.now());
        runnerOrderMapper.insert(runnerOrder);

        // 4. 更新订单主表状态为"已接单"
        Order update = new Order();
        update.setId(dto.getOrderId());
        update.setStatus(1); // 1-已接单
        orderMapper.updateById(update);

        return runnerOrder.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long pickupOrder(PickupOrderDTO dto) {
        // 1. 校验订单存在
        Order order = orderMapper.selectById(dto.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        // 2. 校验订单处于"已接单"状态
        if (order.getStatus() == null || order.getStatus() != 1) {
            throw new BusinessException("订单当前状态不允许取货");
        }
        // 3. 校验接单关系存在且属于当前跑腿员
        RunnerOrder runnerOrder = runnerOrderMapper.selectOne(
                Wrappers.<RunnerOrder>lambdaQuery()
                        .eq(RunnerOrder::getOrderId, dto.getOrderId())
                        .eq(RunnerOrder::getRunnerId, dto.getRunnerId())
        );
        if (runnerOrder == null) {
            throw new BusinessException("该订单不属于你");
        }
        // 4. 校验接单关系处于"已接单"状态
        if (runnerOrder.getStatus() == null || runnerOrder.getStatus() != 0) {
            throw new BusinessException("接单关系当前状态不允许取货");
        }

        // 5. 更新接单关系状态为"进行中" + 记录取货时间
        RunnerOrder runnerUpdate = new RunnerOrder();
        runnerUpdate.setId(runnerOrder.getId());
        runnerUpdate.setStatus(1); // 1-进行中
        runnerUpdate.setPickupTime(LocalDateTime.now());
        runnerOrderMapper.updateById(runnerUpdate);

        // 6. 更新订单主表状态为"进行中"
        Order orderUpdate = new Order();
        orderUpdate.setId(dto.getOrderId());
        orderUpdate.setStatus(2); // 2-进行中
        orderMapper.updateById(orderUpdate);

        return runnerOrder.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long finishOrder(FinishOrderDTO dto) {
        // 1. 校验订单存在
        Order order = orderMapper.selectById(dto.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        // 2. 校验订单处于"进行中"状态
        if (order.getStatus() == null || order.getStatus() != 2) {
            throw new BusinessException("订单当前状态不允许完成");
        }
        // 3. 校验接单关系存在且属于当前跑腿员
        RunnerOrder runnerOrder = runnerOrderMapper.selectOne(
                Wrappers.<RunnerOrder>lambdaQuery()
                        .eq(RunnerOrder::getOrderId, dto.getOrderId())
                        .eq(RunnerOrder::getRunnerId, dto.getRunnerId())
        );
        if (runnerOrder == null) {
            throw new BusinessException("该订单不属于你");
        }
        // 4. 校验接单关系处于"进行中"状态
        if (runnerOrder.getStatus() == null || runnerOrder.getStatus() != 1) {
            throw new BusinessException("接单关系当前状态不允许完成");
        }

        // 5. 更新接单关系状态为"已完成" + 记录完成时间
        RunnerOrder runnerUpdate = new RunnerOrder();
        runnerUpdate.setId(runnerOrder.getId());
        runnerUpdate.setStatus(2); // 2-已完成
        runnerUpdate.setFinishTime(LocalDateTime.now());
        runnerOrderMapper.updateById(runnerUpdate);

        // 6. 更新订单主表状态为"已完成"
        Order orderUpdate = new Order();
        orderUpdate.setId(dto.getOrderId());
        orderUpdate.setStatus(3); // 3-已完成
        orderMapper.updateById(orderUpdate);

        return runnerOrder.getId();
    }

    @Override
    public List<MyTaskVO> listTasksByRunnerId(Long runnerId) {
        // 1. 按 runnerId 查接单关系表，按接单时间倒序
        List<RunnerOrder> runnerOrders = runnerOrderMapper.selectList(
                Wrappers.<RunnerOrder>lambdaQuery()
                        .eq(RunnerOrder::getRunnerId, runnerId)
                        .orderByDesc(RunnerOrder::getTakeTime)
        );
        if (runnerOrders.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 批量拉对应的订单主表
        List<Long> orderIds = runnerOrders.stream()
                .map(RunnerOrder::getOrderId)
                .collect(Collectors.toList());
        Map<Long, Order> orderMap = orderMapper.selectBatchIds(orderIds).stream()
                .collect(Collectors.toMap(Order::getId, o -> o));

        // 3. 按接单时间倒序合并为 VO
        return runnerOrders.stream()
                .map(ro -> {
                    Order o = orderMap.get(ro.getOrderId());
                    if (o == null) return null;
                    MyTaskVO vo = new MyTaskVO();
                    vo.setId(o.getId());
                    vo.setOrderNo(o.getOrderNo());
                    vo.setTitle(o.getTitle());
                    vo.setContent(o.getContent());
                    vo.setOrderType(o.getOrderType());
                    vo.setPrice(o.getPrice());
                    vo.setTip(o.getTip());
                    vo.setStatus(o.getStatus());
                    vo.setExpectFinishTime(o.getExpectFinishTime());
                    vo.setCreateTime(o.getCreateTime());
                    vo.setTakeTime(ro.getTakeTime());
                    vo.setPickupTime(ro.getPickupTime());
                    return vo;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 生成订单号：yyyyMMddHHmmss + 6位随机数
     * TODO: MVP 简单实现，后续可替换为雪花算法等分布式ID
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(ORDER_NO_FORMATTER);
        int random = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return timestamp + random;
    }
}
