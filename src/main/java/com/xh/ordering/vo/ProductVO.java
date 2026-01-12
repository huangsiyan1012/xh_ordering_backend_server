package com.xh.ordering.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 菜品视图对象（包含分类名称）
 */
@Data
public class ProductVO {
    private Long id;
    private Long categoryId;
    private String categoryName; // 分类名称
    private String name;
    private Integer pointCost;
    private String image;
    private String description;
    private Integer status;
    private LocalDateTime createdAt;
}

