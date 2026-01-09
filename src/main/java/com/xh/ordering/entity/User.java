package com.xh.ordering.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String phone;
    
    private String password;
    
    /**
     * 积分余额
     */
    private Integer points;
    
    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;
    
    private LocalDateTime createdAt;
}

