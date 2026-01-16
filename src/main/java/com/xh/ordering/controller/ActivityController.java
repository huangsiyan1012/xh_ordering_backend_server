package com.xh.ordering.controller;

import com.xh.ordering.entity.Activity;
import com.xh.ordering.service.ActivityService;
import com.xh.ordering.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 活动控制器
 */
@RestController
@RequestMapping("/activity")
public class ActivityController {
    
    @Autowired
    private ActivityService activityService;
    
    /**
     * 管理员：获取活动列表（支持分页和搜索）
     * GET /api/activity/admin/list-page
     */
    @GetMapping("/admin/list-page")
    public Result<Map<String, Object>> getActivityList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer status) {
        Map<String, Object> result = activityService.getActivityList(page, pageSize, title, status);
        return Result.success(result);
    }
    
    /**
     * 管理员：创建活动
     * POST /api/activity/admin/create
     */
    @PostMapping("/admin/create")
    public Result<Activity> createActivity(@RequestBody Activity activity) {
        activityService.createActivity(activity);
        return Result.success("创建成功", activity);
    }
    
    /**
     * 管理员：更新活动
     * PUT /api/activity/admin/{id}
     */
    @PutMapping("/admin/{id}")
    public Result<Void> updateActivity(@PathVariable Long id, @RequestBody Activity activity) {
        activityService.updateActivity(id, activity);
        return Result.success("更新成功", null);
    }
    
    /**
     * 管理员：删除活动
     * DELETE /api/activity/admin/{id}
     */
    @DeleteMapping("/admin/{id}")
    public Result<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return Result.success("删除成功", null);
    }
    
    /**
     * 管理员：更新活动状态
     * PUT /api/activity/admin/{id}/status
     */
    @PutMapping("/admin/{id}/status")
    public Result<Void> updateActivityStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        activityService.updateActivityStatus(id, status);
        return Result.success("状态更新成功", null);
    }
    
    /**
     * 管理员：获取待审核的活动列表（分页）
     * GET /api/activity/admin/review/list-page
     */
    @GetMapping("/admin/review/list-page")
    public Result<Map<String, Object>> getPendingReviewList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String activityTitle,
            @RequestParam(required = false) String userName) {
        Map<String, Object> result = activityService.getPendingReviewList(page, pageSize, activityTitle, userName);
        return Result.success(result);
    }
    
    /**
     * 管理员：审核用户活动（通过或驳回）
     * PUT /api/activity/admin/review/{userActivityId}
     */
    @PutMapping("/admin/review/{userActivityId}")
    public Result<Void> reviewUserActivity(
            @PathVariable Long userActivityId,
            @RequestParam Boolean approved,
            @RequestParam(required = false) String reviewRemark) {
        activityService.reviewUserActivity(userActivityId, approved, reviewRemark);
        String message = approved ? "审核通过" : "审核驳回";
        return Result.success(message, null);
    }
}

