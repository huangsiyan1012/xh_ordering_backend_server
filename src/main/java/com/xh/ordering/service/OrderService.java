package com.xh.ordering.service;

import com.xh.ordering.common.ResultCode;
import com.xh.ordering.dto.OrderCreateDTO;
import com.xh.ordering.entity.OrderItem;
import com.xh.ordering.entity.Orders;
import com.xh.ordering.entity.PointRecord;
import com.xh.ordering.entity.Product;
import com.xh.ordering.exception.BusinessException;
import com.xh.ordering.mapper.OrderItemMapper;
import com.xh.ordering.mapper.OrdersMapper;
import com.xh.ordering.mapper.PointRecordMapper;
import com.xh.ordering.mapper.ProductMapper;
import com.xh.ordering.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 订单服务
 */
@Slf4j
@Service
public class OrderService {
    
    @Autowired
    private OrdersMapper ordersMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PointRecordMapper pointRecordMapper;
    
    /**
     * 创建订单（核心业务逻辑）
     * 使用事务保证：生成订单、生成订单明细、扣减积分、记录积分流水的原子性
     */
    @Transactional(rollbackFor = Exception.class)
    public Orders createOrder(Long userId, OrderCreateDTO dto) {
        // 1. 校验用户是否存在且状态正常
        com.xh.ordering.entity.User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }
        
        // 2. 查询并校验菜品信息，计算总积分
        List<Product> products = new ArrayList<>();
        Integer totalPoints = 0;
        
        for (OrderCreateDTO.OrderItemDTO itemDTO : dto.getItems()) {
            Product product = productMapper.selectById(itemDTO.getProductId());
            if (product == null) {
                throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
            }
            if (product.getStatus() == 0) {
                throw new BusinessException(ResultCode.PRODUCT_OFFLINE);
            }
            if (itemDTO.getQuantity() <= 0) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "菜品数量必须大于0");
            }
            
            products.add(product);
            totalPoints += product.getPointCost() * itemDTO.getQuantity();
        }
        
        // 3. 校验用户积分是否足够
        if (user.getPoints() < totalPoints) {
            throw new BusinessException(ResultCode.POINTS_NOT_ENOUGH, 
                "积分不足，当前积分: " + user.getPoints() + "，需要积分: " + totalPoints);
        }
        
        // 4. 生成订单号
        String orderNo = generateOrderNo();
        
        // 5. 创建订单
        Orders order = new Orders();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPoints(totalPoints);
        order.setRemark(dto.getRemark());
        order.setCreatedAt(LocalDateTime.now());
        ordersMapper.insert(order);
        
        // 6. 创建订单明细
        List<OrderItem> orderItems = new ArrayList<>();
        for (int i = 0; i < dto.getItems().size(); i++) {
            OrderCreateDTO.OrderItemDTO itemDTO = dto.getItems().get(i);
            Product product = products.get(i);
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductPoint(product.getPointCost());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setSubtotalPoint(product.getPointCost() * itemDTO.getQuantity());
            
            orderItemMapper.insert(orderItem);
            orderItems.add(orderItem);
        }
        
        // 7. 扣减用户积分（使用乐观锁保证并发安全）
        int updateCount = userMapper.deductPoints(userId, totalPoints);
        if (updateCount == 0) {
            // 扣减失败，可能是积分不足（并发情况下）
            throw new BusinessException(ResultCode.POINTS_NOT_ENOUGH, "积分不足，请刷新后重试");
        }
        
        // 8. 记录积分流水
        PointRecord pointRecord = new PointRecord();
        pointRecord.setUserId(userId);
        pointRecord.setChangeAmount(-totalPoints); // 负数表示扣除
        pointRecord.setType(2); // 2-点餐扣除
        pointRecord.setRelatedId(order.getId());
        pointRecord.setCreatedAt(LocalDateTime.now());
        pointRecordMapper.insert(pointRecord);
        
        log.info("订单创建成功，订单号: {}, 用户ID: {}, 总积分: {}", orderNo, userId, totalPoints);
        
        return order;
    }
    
    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * 查询用户的订单列表
     */
    public List<Orders> getUserOrders(Long userId) {
        return ordersMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Orders>()
                .eq(Orders::getUserId, userId)
                .orderByDesc(Orders::getCreatedAt)
        );
    }
    
    /**
     * 查询订单详情（包含订单明细）
     */
    public Orders getOrderDetail(Long orderId) {
        return ordersMapper.selectById(orderId);
    }
    
    /**
     * 查询订单明细列表
     */
    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId)
        );
    }
    
    /**
     * 查询所有订单（管理员/后厨）
     */
    public List<Orders> getAllOrders() {
        return ordersMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Orders>()
                .orderByDesc(Orders::getCreatedAt)
        );
    }
}

