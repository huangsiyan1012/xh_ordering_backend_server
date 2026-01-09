package com.xh.ordering.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xh.ordering.common.ResultCode;
import com.xh.ordering.entity.PointRecord;
import com.xh.ordering.entity.User;
import com.xh.ordering.exception.BusinessException;
import com.xh.ordering.mapper.PointRecordMapper;
import com.xh.ordering.mapper.UserMapper;
import com.xh.ordering.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户服务
 */
@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PointRecordMapper pointRecordMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 用户注册
     */
    public Map<String, Object> register(String name, String phone, String password) {
        // 检查手机号是否已存在
        User existUser = userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getPhone, phone)
        );
        if (existUser != null) {
            throw new BusinessException(ResultCode.PHONE_ALREADY_EXISTS);
        }
        
        User user = new User();
        user.setName(name);
        user.setPhone(phone);
        user.setPassword(password); // 密码明文存储
        user.setPoints(0);
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        
        userMapper.insert(user);
        
        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getPhone(), 0);
        
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("token", token);
        return result;
    }
    
    /**
     * 用户登录
     */
    public Map<String, Object> login(String phone, String password) {
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getPhone, phone)
        );
        
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        // 验证密码（明文比较）
        if (!password.equals(user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
        
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }
        
        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getPhone(), 0);
        
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("token", token);
        return result;
    }
    
    /**
     * 根据ID查询用户
     */
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }
    
    /**
     * 查询用户积分流水
     */
    public List<PointRecord> getUserPointRecords(Long userId) {
        return pointRecordMapper.selectList(
            new LambdaQueryWrapper<PointRecord>()
                .eq(PointRecord::getUserId, userId)
                .orderByDesc(PointRecord::getCreatedAt)
        );
    }
    
    /**
     * 获取用户列表（支持分页和搜索）
     */
    public Map<String, Object> getUserList(Integer page, Integer pageSize, String name, String phone) {
        Page<User> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        // 搜索条件
        if (StringUtils.hasText(name)) {
            queryWrapper.like(User::getName, name);
        }
        if (StringUtils.hasText(phone)) {
            queryWrapper.like(User::getPhone, phone);
        }
        
        // 按创建时间倒序
        queryWrapper.orderByDesc(User::getCreatedAt);
        
        IPage<User> pageResult = userMapper.selectPage(pageParam, queryWrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("page", pageResult.getCurrent());
        result.put("pageSize", pageResult.getSize());
        
        return result;
    }
    
    /**
     * 更新用户状态
     */
    public void updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }
    
    /**
     * 删除用户
     */
    public void deleteUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        userMapper.deleteById(userId);
    }
    
    /**
     * 创建用户（管理员接口）
     */
    public User createUser(String name, String phone, String password, Integer status) {
        // 检查手机号是否已存在
        User existUser = userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getPhone, phone)
        );
        if (existUser != null) {
            throw new BusinessException(ResultCode.PHONE_ALREADY_EXISTS);
        }
        
        User user = new User();
        user.setName(name);
        user.setPhone(phone);
        user.setPassword(password); // 密码明文存储
        user.setPoints(0);
        user.setStatus(status != null ? status : 1);
        user.setCreatedAt(LocalDateTime.now());
        
        userMapper.insert(user);
        return user;
    }
    
    /**
     * 更新用户信息（管理员接口）
     */
    public void updateUser(Long userId, String name, String phone, String password, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        // 如果修改了手机号，检查新手机号是否已被其他用户使用
        if (StringUtils.hasText(phone) && !phone.equals(user.getPhone())) {
            User existUser = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                    .eq(User::getPhone, phone)
                    .ne(User::getId, userId)
            );
            if (existUser != null) {
                throw new BusinessException(ResultCode.PHONE_ALREADY_EXISTS);
            }
            user.setPhone(phone);
        }
        
        if (StringUtils.hasText(name)) {
            user.setName(name);
        }
        if (StringUtils.hasText(password)) {
            user.setPassword(password); // 密码明文存储
        }
        if (status != null) {
            user.setStatus(status);
        }
        
        userMapper.updateById(user);
    }
}

