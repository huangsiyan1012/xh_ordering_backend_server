package com.xh.ordering.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xh.ordering.common.ResultCode;
import com.xh.ordering.entity.Admin;
import com.xh.ordering.exception.BusinessException;
import com.xh.ordering.mapper.AdminMapper;
import com.xh.ordering.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员服务
 */
@Service
public class AdminService {
    
    @Autowired
    private AdminMapper adminMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 管理员登录
     */
    public Map<String, Object> login(String username, String password) {
        Admin admin = adminMapper.selectOne(
            new LambdaQueryWrapper<Admin>()
                .eq(Admin::getUsername, username)
        );
        
        if (admin == null) {
            throw new BusinessException(ResultCode.ADMIN_NOT_FOUND);
        }
        
        // 验证密码（明文比较）
        if (!password.equals(admin.getPassword())) {
            throw new BusinessException(ResultCode.ADMIN_PASSWORD_ERROR);
        }
        
        if (admin.getStatus() == 0) {
            throw new BusinessException(ResultCode.ADMIN_DISABLED);
        }
        
        // 生成Token
        String token = jwtUtil.generateToken(admin.getId(), admin.getUsername(), admin.getRole());
        
        Map<String, Object> result = new HashMap<>();
        result.put("admin", admin);
        result.put("token", token);
        return result;
    }
    
    /**
     * 根据ID查询管理员
     */
    public Admin getAdminById(Long id) {
        return adminMapper.selectById(id);
    }
    
    /**
     * 修改密码
     */
    public void changePassword(Long adminId, String oldPassword, String newPassword) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException(ResultCode.ADMIN_NOT_FOUND);
        }
        
        // 验证旧密码
        if (!oldPassword.equals(admin.getPassword())) {
            throw new BusinessException(ResultCode.ADMIN_PASSWORD_ERROR);
        }
        
        // 更新密码
        admin.setPassword(newPassword);
        adminMapper.updateById(admin);
    }
}

