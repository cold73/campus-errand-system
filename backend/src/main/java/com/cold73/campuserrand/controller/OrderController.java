package com.cold73.campuserrand.controller;

import com.cold73.campuserrand.common.Result;
import com.cold73.campuserrand.dto.CreateOrderDTO;
import com.cold73.campuserrand.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 订单相关接口
 */
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     *
     * @param dto 订单主信息 + 收货信息
     * @return 新建订单的 ID
     */
    @PostMapping("/create")
    public Result<Long> createOrder(@RequestBody @Valid CreateOrderDTO dto) {
        Long orderId = orderService.createOrder(dto);
        return Result.success(orderId);
    }
}
