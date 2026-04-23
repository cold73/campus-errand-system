package com.cold73.campuserrand.controller;

import com.cold73.campuserrand.common.Result;
import com.cold73.campuserrand.dto.CancelOrderDTO;
import com.cold73.campuserrand.dto.CreateOrderDTO;
import com.cold73.campuserrand.dto.FinishOrderDTO;
import com.cold73.campuserrand.dto.PickupOrderDTO;
import com.cold73.campuserrand.dto.TakeOrderDTO;
import com.cold73.campuserrand.entity.Order;
import com.cold73.campuserrand.exception.BusinessException;
import com.cold73.campuserrand.service.OrderService;
import com.cold73.campuserrand.vo.MyTaskVO;
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

    /**
     * 跑腿员接单：插入接单记录 + 推进订单状态到"已接单"
     *
     * @param dto 接单参数（orderId + runnerId）
     * @return 新建接单关系记录的 ID
     */
    @PostMapping("/take")
    public Result<Long> takeOrder(@RequestBody @Valid TakeOrderDTO dto) {
        try {
            Long runnerOrderId = orderService.takeOrder(dto);
            return Result.success(runnerOrderId);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 跑腿大厅：查询所有可接订单（status=0 待接单）
     *
     * @return 可接订单列表（按创建时间倒序）
     */
    @GetMapping("/hall")
    public Result<List<Order>> hall() {
        return Result.success(orderService.listAvailableOrders());
    }

    /**
     * 跑腿员取货确认：订单 1→2（进行中），接单关系 0→1（进行中），记录取货时间
     *
     * @param dto 取货参数（orderId + runnerId）
     * @return 接单关系记录的 ID
     */
    @PostMapping("/pickup")
    public Result<Long> pickupOrder(@RequestBody @Valid PickupOrderDTO dto) {
        try {
            Long runnerOrderId = orderService.pickupOrder(dto);
            return Result.success(runnerOrderId);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 跑腿员完成订单：订单 2→3（已完成），接单关系 1→2（已完成），记录完成时间
     *
     * @param dto 完成参数（orderId + runnerId）
     * @return 接单关系记录的 ID
     */
    @PostMapping("/finish")
    public Result<Long> finishOrder(@RequestBody @Valid FinishOrderDTO dto) {
        try {
            Long runnerOrderId = orderService.finishOrder(dto);
            return Result.success(runnerOrderId);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询某跑腿员的"我的接单"列表
     *
     * @param runnerId 跑腿员ID
     * @return 合并订单主信息 + 接单时间节点的列表（按接单时间倒序）
     */
    @GetMapping("/my-tasks")
    public Result<List<MyTaskVO>> myTasks(@RequestParam Long runnerId) {
        return Result.success(orderService.listTasksByRunnerId(runnerId));
    }

    /**
     * 用户取消订单：仅允许取消自己名下、状态为"待接单"的订单
     *
     * @param dto 取消参数（orderId + userId）
     * @return 被取消订单的 ID
     */
    @PostMapping("/cancel")
    public Result<Long> cancelOrder(@RequestBody @Valid CancelOrderDTO dto) {
        try {
            Long orderId = orderService.cancelOrder(dto);
            return Result.success(orderId);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }
}
