package com.xh.ordering.controller;

import com.xh.ordering.dto.OrderCreateDTO;
import com.xh.ordering.entity.OrderItem;
import com.xh.ordering.entity.Orders;
import com.xh.ordering.service.OrderService;
import com.xh.ordering.vo.Result;
import com.xh.ordering.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 用户下单（核心接口）
     * POST /api/order/create
     */
    @PostMapping("/create")
    public Result<Orders> createOrder(@RequestBody @Validated OrderCreateDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        Orders order = orderService.createOrder(userId, dto);
        return Result.success("下单成功", order);
    }
    
    /**
     * 查询当前用户的订单列表
     * GET /api/order/user/list
     */
    @GetMapping("/user/list")
    public Result<List<Orders>> getCurrentUserOrders() {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(orderService.getUserOrders(userId));
    }
    
    /**
     * 查询指定用户的订单列表（管理员接口）
     * GET /api/order/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public Result<List<Orders>> getUserOrders(@PathVariable Long userId) {
        return Result.success(orderService.getUserOrders(userId));
    }
    
    /**
     * 查询订单详情
     * GET /api/order/{orderId}
     */
    @GetMapping("/{orderId}")
    public Result<Map<String, Object>> getOrderDetail(@PathVariable Long orderId) {
        Orders order = orderService.getOrderDetail(orderId);
        List<OrderItem> items = orderService.getOrderItems(orderId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("items", items);
        
        return Result.success(data);
    }
    
    /**
     * 管理员/后厨：查询所有订单
     * GET /api/order/admin/list
     */
    @GetMapping("/admin/list")
    public Result<List<Orders>> getAllOrders() {
        return Result.success(orderService.getAllOrders());
    }
    
    /**
     * 管理员：获取订单列表（支持分页和搜索）
     * GET /api/order/admin/list-page
     */
    @GetMapping("/admin/list-page")
    public Result<Map<String, Object>> getOrderList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String remark,
            @RequestParam(required = false) String productName) {
        Map<String, Object> result = orderService.getOrderList(
            page, pageSize, orderNo, userName, remark, productName);
        return Result.success(result);
    }
    
    /**
     * 管理员：更新订单备注
     * PUT /api/order/admin/{id}/remark
     */
    @PutMapping("/admin/{id}/remark")
    public Result<Void> updateOrderRemark(
            @PathVariable Long id,
            @RequestParam String remark) {
        orderService.updateOrderRemark(id, remark);
        return Result.success("备注更新成功", null);
    }
}

