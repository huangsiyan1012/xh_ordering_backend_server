package com.xh.ordering.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分流水记录实体
 */
@Data
@TableName("point_record")
public class PointRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 变动金额（正数为增加，负数为减少）
     */
    private Integer changeAmount;
    
    /**
     * 类型：1-充值，2-点餐扣除，3-系统调整
     */
    private Integer type;
    
    /**
     * 关联ID（订单ID或充值记录ID）
     */
    private Long relatedId;
    
    private LocalDateTime createdAt;
}

