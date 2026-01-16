package com.xh.ordering.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xh.ordering.entity.Activity;
import com.xh.ordering.entity.PointRecord;
import com.xh.ordering.entity.User;
import com.xh.ordering.entity.UserActivity;
import com.xh.ordering.mapper.ActivityMapper;
import com.xh.ordering.mapper.PointRecordMapper;
import com.xh.ordering.mapper.UserActivityMapper;
import com.xh.ordering.mapper.UserMapper;
import com.xh.ordering.util.SecurityUtil;
import com.xh.ordering.vo.ActivityReviewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 活动服务
 */
@Slf4j
@Service
public class ActivityService {
    
    @Autowired
    private ActivityMapper activityMapper;
    
    @Autowired
    private UserActivityMapper userActivityMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PointRecordMapper pointRecordMapper;
    
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
     * 如果状态更新为"已完成"，会自动给所有已领取的用户发放积分
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateActivityStatus(Long id, Integer status) {
        // 查询活动信息，获取旧状态
        Activity oldActivity = activityMapper.selectById(id);
        if (oldActivity == null) {
            throw new RuntimeException("活动不存在");
        }
        
        Integer oldStatus = oldActivity.getStatus();
        
        // 更新活动状态
        Activity activity = new Activity();
        activity.setId(id);
        activity.setStatus(status);
        activityMapper.updateById(activity);
        
        // 如果状态从非"已完成"变为"已完成"，自动发放积分
        if (oldStatus != 2 && status == 2) {
            distributePointsForCompletedActivity(id, oldActivity.getRewardPoints());
        }
    }
    
    /**
     * 活动完成时发放积分（给所有已领取该活动的用户发放积分）
     * @param activityId 活动ID
     * @param rewardPoints 奖励积分
     */
    private void distributePointsForCompletedActivity(Long activityId, Integer rewardPoints) {
        // 查询所有已领取该活动的用户ID
        List<Long> userIds = userActivityMapper.selectReceivedUserIdsByActivityId(activityId);
        
        if (userIds.isEmpty()) {
            log.info("活动 {} 没有已领取的用户，无需发放积分", activityId);
            return;
        }
        
        // 给每个已领取的用户发放积分
        for (Long userId : userIds) {
            User user = userMapper.selectById(userId);
            if (user != null) {
                // 1. 增加用户积分
                userMapper.addPoints(userId, rewardPoints);
                
                // 2. 记录积分流水
                PointRecord pointRecord = new PointRecord();
                pointRecord.setUserId(userId);
                pointRecord.setChangeAmount(rewardPoints); // 正数表示增加
                pointRecord.setType(3); // 3-系统调整（活动奖励）
                pointRecord.setRelatedId(activityId); // 关联活动ID
                pointRecord.setCreatedAt(LocalDateTime.now());
                pointRecordMapper.insert(pointRecord);
                
                log.info("活动完成，发放积分成功，用户ID: {}, 活动ID: {}, 积分: {}", userId, activityId, rewardPoints);
            }
        }
        
        log.info("活动 {} 完成，共给 {} 个用户发放了积分，每人 {} 积分", activityId, userIds.size(), rewardPoints);
    }
    
    /**
     * 根据ID查询活动
     */
    public Activity getActivityById(Long id) {
        return activityMapper.selectById(id);
    }
    
    /**
     * 获取待审核的活动列表（分页）
     * @param page 页码
     * @param pageSize 每页大小
     * @param activityTitle 活动名称（可选）
     * @param userName 用户昵称（可选）
     * @return 分页结果
     */
    public Map<String, Object> getPendingReviewList(Integer page, Integer pageSize, String activityTitle, String userName) {
        Page<ActivityReviewVO> pageParam = new Page<>(page, pageSize);
        IPage<ActivityReviewVO> pageResult = activityMapper.selectPendingReviewList(pageParam, activityTitle, userName);
        
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
     * 审核用户活动（通过或驳回）
     * @param userActivityId user_activity 表的主键ID
     * @param approved true-通过，false-驳回
     * @param reviewRemark 审核备注
     */
    @Transactional(rollbackFor = Exception.class)
    public void reviewUserActivity(Long userActivityId, Boolean approved, String reviewRemark) {
        // 查询用户活动记录
        UserActivity userActivity = userActivityMapper.selectById(userActivityId);
        if (userActivity == null) {
            throw new RuntimeException("用户活动记录不存在");
        }
        
        // 只能审核待审核状态（status = 1）的记录
        if (userActivity.getStatus() != 1) {
            throw new RuntimeException("该记录不是待审核状态，无法审核");
        }
        
        // 获取审核人ID
        Long reviewerId = SecurityUtil.getCurrentUserId();
        if (reviewerId == null) {
            throw new RuntimeException("无法获取当前登录用户信息");
        }
        
        // 审核后的状态：2-通过，3-驳回
        Integer newStatus = approved ? 2 : 3;
        
        // 执行审核
        userActivityMapper.reviewUserActivity(userActivityId, newStatus, reviewRemark, reviewerId);
        
        // 查询活动信息
        Activity activity = activityMapper.selectById(userActivity.getActivityId());
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        
        // 如果审核通过，发放积分并更新活动状态为已完成
        if (approved) {
            // 1. 更新活动表状态为已完成（status = 2）
            Activity updateActivity = new Activity();
            updateActivity.setId(activity.getId());
            updateActivity.setStatus(2); // 2-已完成
            activityMapper.updateById(updateActivity);
            
            // 2. 发放积分
            if (activity.getRewardPoints() != null && activity.getRewardPoints() > 0) {
                Long userId = userActivity.getUserId();
                Integer rewardPoints = activity.getRewardPoints();
                
                // 增加用户积分
                userMapper.addPoints(userId, rewardPoints);
                
                // 记录积分流水
                PointRecord pointRecord = new PointRecord();
                pointRecord.setUserId(userId);
                pointRecord.setChangeAmount(rewardPoints); // 正数表示增加
                pointRecord.setType(3); // 3-系统调整（活动奖励）
                pointRecord.setRelatedId(userActivity.getActivityId()); // 关联活动ID
                pointRecord.setCreatedAt(LocalDateTime.now());
                pointRecordMapper.insert(pointRecord);
                
                log.info("活动审核通过，发放积分成功，用户ID: {}, 活动ID: {}, 积分: {}", userId, userActivity.getActivityId(), rewardPoints);
            }
            
            log.info("活动审核通过，活动状态已更新为已完成，活动ID: {}", activity.getId());
        }
        
        log.info("活动审核完成，userActivityId: {}, 审核结果: {}, 审核人: {}", userActivityId, approved ? "通过" : "驳回", reviewerId);
    }
}

