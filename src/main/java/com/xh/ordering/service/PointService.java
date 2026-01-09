package com.xh.ordering.service;

import com.xh.ordering.common.ResultCode;
import com.xh.ordering.entity.PointRecord;
import com.xh.ordering.entity.PointRecharge;
import com.xh.ordering.entity.User;
import com.xh.ordering.exception.BusinessException;
import com.xh.ordering.mapper.PointRecordMapper;
import com.xh.ordering.mapper.PointRechargeMapper;
import com.xh.ordering.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 积分服务
 */
@Slf4j
@Service
public class PointService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PointRechargeMapper pointRechargeMapper;
    
    @Autowired
    private PointRecordMapper pointRecordMapper;
    
    /**
     * 给用户充值积分（管理员操作）
     * 使用事务保证：增加积分、记录充值记录、记录积分流水的原子性
     */
    @Transactional(rollbackFor = Exception.class)
    public void rechargePoints(Long userId, Integer amount, String channel, Long adminId) {
        if (amount <= 0) {
            throw new BusinessException(ResultCode.POINTS_INVALID, "充值金额必须大于0");
        }
        
        // 1. 校验用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        // 2. 增加用户积分
        userMapper.addPoints(userId, amount);
        
        // 3. 记录充值记录
        PointRecharge recharge = new PointRecharge();
        recharge.setUserId(userId);
        recharge.setRechargeAmount(amount);
        recharge.setChannel(channel);
        recharge.setCreatedBy(adminId);
        recharge.setCreatedAt(LocalDateTime.now());
        pointRechargeMapper.insert(recharge);
        
        // 4. 记录积分流水
        PointRecord pointRecord = new PointRecord();
        pointRecord.setUserId(userId);
        pointRecord.setChangeAmount(amount); // 正数表示增加
        pointRecord.setType(1); // 1-充值
        pointRecord.setRelatedId(recharge.getId());
        pointRecord.setCreatedAt(LocalDateTime.now());
        pointRecordMapper.insert(pointRecord);
        
        log.info("积分充值成功，用户ID: {}, 充值金额: {}, 操作人: {}", userId, amount, adminId);
    }
}

