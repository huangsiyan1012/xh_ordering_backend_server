package com.xh.ordering.controller;

import com.xh.ordering.entity.Admin;
import com.xh.ordering.service.AdminService;
import com.xh.ordering.util.SecurityUtil;
import com.xh.ordering.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    /**
     * 管理员登录
     * POST /api/admin/login
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestParam String username, 
                                             @RequestParam String password) {
        Map<String, Object> result = adminService.login(username, password);
        Admin admin = (Admin) result.get("admin");
        
        Map<String, Object> data = new HashMap<>();
        data.put("adminId", admin.getId());
        data.put("username", admin.getUsername());
        data.put("role", admin.getRole());
        data.put("token", result.get("token"));
        
        return Result.success("登录成功", data);
    }
    
    /**
     * 查询当前管理员信息（从Token中获取）
     * GET /api/admin/info
     */
    @GetMapping("/info")
    public Result<Admin> getCurrentAdminInfo() {
        Long adminId = SecurityUtil.getCurrentUserId();
        Admin admin = adminService.getAdminById(adminId);
        if (admin == null) {
            return Result.error(400, "管理员不存在");
        }
        admin.setPassword(null); // 不返回密码
        return Result.success(admin);
    }
    
    /**
     * 查询指定管理员信息
     * GET /api/admin/info/{adminId}
     */
    @GetMapping("/info/{adminId}")
    public Result<Admin> getAdminInfo(@PathVariable Long adminId) {
        Admin admin = adminService.getAdminById(adminId);
        if (admin == null) {
            return Result.error(400, "管理员不存在");
        }
        admin.setPassword(null); // 不返回密码
        return Result.success(admin);
    }
    
    /**
     * 修改密码
     * POST /api/admin/change-password
     */
    @PostMapping("/change-password")
    public Result<Void> changePassword(@RequestParam String oldPassword,
                                      @RequestParam String newPassword) {
        Long adminId = SecurityUtil.getCurrentUserId();
        adminService.changePassword(adminId, oldPassword, newPassword);
        return Result.success("密码修改成功", null);
    }
}

