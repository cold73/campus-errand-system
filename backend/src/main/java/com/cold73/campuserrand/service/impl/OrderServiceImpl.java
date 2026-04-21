package com.cold73.campuserrand.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cold73.campuserrand.dto.CreateOrderDTO;
import com.cold73.campuserrand.entity.Order;
import com.cold73.campuserrand.entity.OrderReceive;
import com.cold73.campuserrand.mapper.OrderMapper;
import com.cold73.campuserrand.mapper.OrderReceiveMapper;
import com.cold73.campuserrand.service.OrderService;
import com.cold73.campuserrand.vo.OrderDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 订单业务 Service 实现
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderReceiveMapper orderReceiveMapper;

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
