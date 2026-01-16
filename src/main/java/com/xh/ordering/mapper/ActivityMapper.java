package com.xh.ordering.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xh.ordering.entity.Activity;
import com.xh.ordering.vo.ActivityReviewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 活动Mapper
 */
@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {
    
    /**
     * 分页查询待审核的活动列表（status = 1）
     */
    IPage<ActivityReviewVO> selectPendingReviewList(
        Page<ActivityReviewVO> page,
        @Param("activityTitle") String activityTitle,
        @Param("userName") String userName
    );
}

