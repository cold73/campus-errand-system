package com.cold73.campuserrand.controller;

import com.cold73.campuserrand.common.Result;
import com.cold73.campuserrand.dto.CreateOrderDTO;
import com.cold73.campuserrand.entity.Order;
import com.cold73.campuserrand.service.OrderService;
import com.cold73.campuserrand.vo.OrderDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

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

    /**
     * 查询指定用户的订单列表
     *
     * @param userId 用户ID
     * @return 订单列表（按 id 倒序）
     */
    @GetMapping("/list")
    public Result<List<Order>> list(@RequestParam Long userId) {
        return Result.success(orderService.listByUserId(userId));
    }

    /**
     * 查询订单详情（订单主信息 + 收货信息）
     *
     * @param id 订单ID
     * @return 订单详情
     */
    @GetMapping("/detail/{id}")
    public Result<OrderDetailVO> detail(@PathVariable Long id) {
        OrderDetailVO vo = orderService.getDetail(id);
        if (vo == null) {
            return Result.error(404, "订单不存在");
        }
        return Result.success(vo);
    }
}
