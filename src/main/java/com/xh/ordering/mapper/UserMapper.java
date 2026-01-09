package com.xh.ordering.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xh.ordering.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 扣减用户积分
     */
    int deductPoints(@Param("userId") Long userId, @Param("points") Integer points);
    
    /**
     * 增加用户积分
     */
    int addPoints(@Param("userId") Long userId, @Param("points") Integer points);
}

