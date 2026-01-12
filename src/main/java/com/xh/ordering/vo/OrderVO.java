package com.xh.ordering.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 订单视图对象（包含用户昵称）
 */
@Data
public class OrderVO {
    private Long id;
    private String orderNo;
    private Long userId;
    private String userName; // 用户昵称
    private Integer totalPoints;
    private String remark;
    private LocalDateTime createdAt;
}

