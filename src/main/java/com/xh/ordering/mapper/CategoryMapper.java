package com.xh.ordering.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xh.ordering.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类Mapper
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}

