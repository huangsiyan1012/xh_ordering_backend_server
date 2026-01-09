package com.xh.ordering.controller;

import com.xh.ordering.service.PointService;
import com.xh.ordering.util.SecurityUtil;
import com.xh.ordering.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 积分控制器
 */
@RestController
@RequestMapping("/point")
public class PointController {
    
    @Autowired
    private PointService pointService;
    
    /**
     * 管理员：给用户充值积分
     * POST /api/point/admin/recharge
     */
    @PostMapping("/admin/recharge")
    public Result<?> rechargePoints(@RequestParam Long userId,
                                   @RequestParam Integer amount,
                                   @RequestParam(required = false, defaultValue = "后台充值") String channel) {
        Long adminId = SecurityUtil.getCurrentUserId();
        pointService.rechargePoints(userId, amount, channel, adminId);
        return Result.success("充值成功");
    }
}

