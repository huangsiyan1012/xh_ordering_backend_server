package com.xh.ordering.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分充值记录VO（包含用户昵称）
 */
@Data
public class PointRechargeVO {
    private Long id;
    private Long userId;
    private Integer rechargeAmount;
    private String channel;
    private Long createdBy;
    private LocalDateTime createdAt;
    private String userName; // 用户昵称
}

