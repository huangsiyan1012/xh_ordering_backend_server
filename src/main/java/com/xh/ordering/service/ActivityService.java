package com.xh.ordering.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xh.ordering.entity.Activity;
import com.xh.ordering.mapper.ActivityMapper;
import com.xh.ordering.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 活动服务
 */
@Service
public class ActivityService {
    
    @Autowired
    private ActivityMapper activityMapper;
    
    /**
     * 获取活动列表（支持分页和搜索）
     */
    public Map<String, Object> getActivityList(Integer page, Integer pageSize, String title, Integer status) {
        Page<Activity> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Activity> queryWrapper = new LambdaQueryWrapper<>();
        
        // 搜索条件
        if (StringUtils.hasText(title)) {
            queryWrapper.like(Activity::getTitle, title);
        }
        if (status != null) {
            queryWrapper.eq(Activity::getStatus, status);
        }
        
        // 按创建时间倒序
        queryWrapper.orderByDesc(Activity::getCreatedAt);
        
        IPage<Activity> pageResult = activityMapper.selectPage(pageParam, queryWrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", pageResult.getRecords());
        result.put("records", pageResult.getRecords()); // 兼容字段
        result.put("total", pageResult.getTotal());
        result.put("page", pageResult.getCurrent());
        result.put("current", pageResult.getCurrent()); // 兼容字段
        result.put("pageSize", pageResult.getSize());
        
        return result;
    }
    
    /**
     * 创建活动
     */
    public void createActivity(Activity activity) {
        if (activity.getCreatedAt() == null) {
            activity.setCreatedAt(LocalDateTime.now());
        }
        if (activity.getStatus() == null) {
            activity.setStatus(0); // 默认待接收
        }
        // 设置创建人
        Long adminId = SecurityUtil.getCurrentUserId();
        if (adminId != null) {
            activity.setCreatedBy(adminId);
        }
        activityMapper.insert(activity);
    }
    
    /**
     * 更新活动
     */
    public void updateActivity(Long id, Activity activity) {
        activity.setId(id);
        activityMapper.updateById(activity);
    }
    
    /**
     * 删除活动
     */
    public void deleteActivity(Long id) {
        activityMapper.deleteById(id);
    }
    
    /**
     * 更新活动状态
     */
    public void updateActivityStatus(Long id, Integer status) {
        Activity activity = new Activity();
        activity.setId(id);
        activity.setStatus(status);
        activityMapper.updateById(activity);
    }
    
    /**
     * 根据ID查询活动
     */
    public Activity getActivityById(Long id) {
        return activityMapper.selectById(id);
    }
}

