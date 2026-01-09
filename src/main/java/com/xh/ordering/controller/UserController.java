package com.xh.ordering.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xh.ordering.entity.User;
import com.xh.ordering.service.UserService;
import com.xh.ordering.util.SecurityUtil;
import com.xh.ordering.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户注册
     * POST /api/user/register
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestParam String name, 
                                                 @RequestParam String phone, 
                                                 @RequestParam String password) {
        Map<String, Object> result = userService.register(name, phone, password);
        User user = (User) result.get("user");
        
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("name", user.getName());
        data.put("phone", user.getPhone());
        data.put("points", user.getPoints());
        data.put("token", result.get("token"));
        
        return Result.success("注册成功", data);
    }
    
    /**
     * 用户登录
     * POST /api/user/login
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestParam String phone, 
                                             @RequestParam String password) {
        Map<String, Object> result = userService.login(phone, password);
        User user = (User) result.get("user");
        
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("name", user.getName());
        data.put("phone", user.getPhone());
        data.put("points", user.getPoints());
        data.put("token", result.get("token"));
        
        return Result.success("登录成功", data);
    }
    
    /**
     * 查询当前用户信息（从Token中获取）
     * GET /api/user/info
     */
    @GetMapping("/info")
    public Result<User> getCurrentUserInfo() {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error(400, "用户不存在");
        }
        // 不返回密码
        user.setPassword(null);
        return Result.success(user);
    }
    
    /**
     * 查询指定用户信息（管理员接口）
     * GET /api/user/info/{userId}
     */
    @GetMapping("/info/{userId}")
    public Result<User> getUserInfo(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error(400, "用户不存在");
        }
        // 不返回密码
        user.setPassword(null);
        return Result.success(user);
    }
    
    /**
     * 查询当前用户积分流水
     * GET /api/user/point-records
     */
    @GetMapping("/point-records")
    public Result<?> getCurrentUserPointRecords() {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(userService.getUserPointRecords(userId));
    }
    
    /**
     * 查询指定用户积分流水（管理员接口）
     * GET /api/user/point-records/{userId}
     */
    @GetMapping("/point-records/{userId}")
    public Result<?> getUserPointRecords(@PathVariable Long userId) {
        return Result.success(userService.getUserPointRecords(userId));
    }
    
    /**
     * 获取用户列表（管理员接口，支持分页和搜索）
     * GET /api/user/list
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone) {
        Map<String, Object> result = userService.getUserList(page, pageSize, name, phone);
        return Result.success(result);
    }
    
    /**
     * 更新用户状态（启用/禁用）
     * PUT /api/user/{userId}/status
     */
    @PutMapping("/{userId}/status")
    public Result<Void> updateUserStatus(@PathVariable Long userId, 
                                         @RequestParam Integer status) {
        userService.updateUserStatus(userId, status);
        return Result.success(status == 1 ? "用户已启用" : "用户已禁用", null);
    }
    
    /**
     * 删除用户
     * DELETE /api/user/{userId}
     */
    @DeleteMapping("/{userId}")
    public Result<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return Result.success("用户已删除", null);
    }
    
    /**
     * 创建用户（管理员接口）
     * POST /api/user
     */
    @PostMapping("")
    public Result<User> createUser(@RequestParam String name,
                                   @RequestParam String phone,
                                   @RequestParam String password,
                                   @RequestParam(required = false, defaultValue = "1") Integer status) {
        User user = userService.createUser(name, phone, password, status);
        user.setPassword(null); // 不返回密码
        return Result.success("用户创建成功", user);
    }
    
    /**
     * 更新用户信息（管理员接口）
     * PUT /api/user/{userId}
     */
    @PutMapping("/{userId}")
    public Result<User> updateUser(@PathVariable Long userId,
                                   @RequestParam(required = false) String name,
                                   @RequestParam(required = false) String phone,
                                   @RequestParam(required = false) String password,
                                   @RequestParam(required = false) Integer status) {
        userService.updateUser(userId, name, phone, password, status);
        User user = userService.getUserById(userId);
        user.setPassword(null); // 不返回密码
        return Result.success("用户更新成功", user);
    }
}

