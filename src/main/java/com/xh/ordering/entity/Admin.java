package com.xh.ordering.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员实体
 */
@Data
@TableName("admin")
public class Admin {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String password;
    
    /**
     * 角色：1-管理员，2-后厨
     */
    private Integer role;
    
    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;
    
    private LocalDateTime createdAt;
}

