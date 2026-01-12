package com.xh.ordering.controller;

import com.xh.ordering.service.PointRechargeService;
import com.xh.ordering.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 积分充值记录控制器
 */
@RestController
@RequestMapping("/recharge")
public class PointRechargeController {
    
    @Autowired
    private PointRechargeService pointRechargeService;
    
    /**
     * 管理员：获取充值记录列表（支持分页和搜索）
     * GET /api/recharge/admin/list-page
     */
    @GetMapping("/admin/list-page")
    public Result<Map<String, Object>> getRechargeList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String channel) {
        Map<String, Object> result = pointRechargeService.getRechargeList(
            page, pageSize, userId, channel);
        return Result.success(result);
    }
}

