package com.xh.ordering.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 订单明细实体
 */
@Data
@TableName("order_item")
public class OrderItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 菜品名称（快照）
     */
    private String productName;
    
    /**
     * 菜品积分价格（快照）
     */
    private Integer productPoint;
    
    /**
     * 数量
     */
    private Integer quantity;
    
    /**
     * 小计积分
     */
    private Integer subtotalPoint;
}

