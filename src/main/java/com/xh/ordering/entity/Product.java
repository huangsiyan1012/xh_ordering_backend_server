package com.xh.ordering.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 菜品实体
 */
@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    private String name;
    
    /**
     * 积分价格
     */
    private Integer pointCost;
    
    /**
     * 图片URL
     */
    private String image;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 状态：1-上架，0-下架
     */
    private Integer status;
    
    private LocalDateTime createdAt;
}

