package com.xh.ordering.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 创建订单DTO
 */
@Data
public class OrderCreateDTO {
    /**
     * 订单明细列表
     */
    @NotEmpty(message = "订单明细不能为空")
    private List<OrderItemDTO> items;
    
    /**
     * 备注
     */
    private String remark;
    
    @Data
    public static class OrderItemDTO {
        /**
         * 菜品ID
         */
        @NotNull(message = "菜品ID不能为空")
        private Long productId;
        
        /**
         * 数量
         */
        @NotNull(message = "数量不能为空")
        private Integer quantity;
    }
}

