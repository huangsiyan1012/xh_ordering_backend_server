package com.xh.ordering.security;

import com.xh.ordering.entity.Admin;
import com.xh.ordering.entity.User;
import com.xh.ordering.mapper.AdminMapper;
import com.xh.ordering.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户详情服务（用于Spring Security）
 * 注意：本项目主要使用JWT，此服务主要用于兼容
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private AdminMapper adminMapper;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 先查管理员
        Admin admin = adminMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Admin>()
                .eq(Admin::getUsername, username)
        );
        
        if (admin != null) {
            return org.springframework.security.core.userdetails.User.builder()
                .username(admin.getUsername())
                .password(admin.getPassword())
                .roles(admin.getRole() == 1 ? "ADMIN" : "CHEF")
                .build();
        }
        
        // 再查用户
        User user = userMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getPhone, username)
        );
        
        if (user != null) {
            return org.springframework.security.core.userdetails.User.builder()
                .username(user.getPhone())
                .password(user.getPassword())
                .roles("USER")
                .build();
        }
        
        throw new UsernameNotFoundException("用户不存在: " + username);
    }
}

