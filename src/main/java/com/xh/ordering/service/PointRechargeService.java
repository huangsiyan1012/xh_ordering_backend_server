package com.xh.ordering.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xh.ordering.entity.PointRecharge;
import com.xh.ordering.entity.User;
import com.xh.ordering.mapper.PointRechargeMapper;
import com.xh.ordering.mapper.UserMapper;
import com.xh.ordering.vo.PointRechargeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 积分充值服务
 */
@Service
public class PointRechargeService {
    
    @Autowired
    private PointRechargeMapper pointRechargeMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 获取充值记录列表（支持分页和搜索，包含用户昵称）
     */
    public Map<String, Object> getRechargeList(Integer page, Integer pageSize, 
                                                Long userId, String channel) {
        Page<PointRecharge> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<PointRecharge> queryWrapper = new LambdaQueryWrapper<>();
        
        // 搜索条件
        if (userId != null) {
            queryWrapper.eq(PointRecharge::getUserId, userId);
        }
        if (StringUtils.hasText(channel)) {
            queryWrapper.eq(PointRecharge::getChannel, channel);
        }
        
        // 按创建时间降序
        queryWrapper.orderByDesc(PointRecharge::getCreatedAt);
        
        IPage<PointRecharge> pageResult = pointRechargeMapper.selectPage(pageParam, queryWrapper);
        
        // 获取所有用户信息，用于组装用户昵称
        List<User> users = userMapper.selectList(null);
        Map<Long, String> userMap = users.stream()
            .collect(Collectors.toMap(User::getId, User::getName));
        
        // 转换为PointRechargeVO，填充用户昵称
        List<PointRechargeVO> voList = pageResult.getRecords().stream().map(recharge -> {
            PointRechargeVO vo = new PointRechargeVO();
            vo.setId(recharge.getId());
            vo.setUserId(recharge.getUserId());
            vo.setRechargeAmount(recharge.getRechargeAmount());
            vo.setChannel(recharge.getChannel());
            vo.setCreatedBy(recharge.getCreatedBy());
            vo.setCreatedAt(recharge.getCreatedAt());
            vo.setUserName(userMap.get(recharge.getUserId()));
            return vo;
        }).collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", voList);
        result.put("records", voList); // 兼容字段
        result.put("total", pageResult.getTotal());
        result.put("page", pageResult.getCurrent());
        result.put("current", pageResult.getCurrent()); // 兼容字段
        result.put("pageSize", pageResult.getSize());
        
        return result;
    }
}

