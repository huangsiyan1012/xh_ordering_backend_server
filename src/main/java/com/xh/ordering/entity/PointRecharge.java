package com.xh.ordering.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分充值记录实体
 */
@Data
@TableName("point_recharge")
public class PointRecharge {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 充值金额（积分）
     */
    private Integer rechargeAmount;
    
    /**
     * 充值渠道
     */
    private String channel;
    
    /**
     * 创建人（管理员ID）
     */
    private Long createdBy;
    
    private LocalDateTime createdAt;
}

