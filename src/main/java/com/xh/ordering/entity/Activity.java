package com.xh.ordering.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动实体
 */
@Data
@TableName("activity")
public class Activity {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 活动名称
     */
    private String title;
    
    /**
     * 活动说明
     */
    private String description;
    
    /**
     * 完成活动可获得积分
     */
    private Integer rewardPoints;
    
    /**
     * 状态：0-待接收，1-进行中，2-已完成
     */
    private Integer status;
    
    /**
     * 管理员ID
     */
    private Long createdBy;
    
    private LocalDateTime createdAt;
}

