package com.xh.ordering.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xh.ordering.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单Mapper
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}

