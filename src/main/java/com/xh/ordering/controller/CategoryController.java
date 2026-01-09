package com.xh.ordering.controller;

import com.xh.ordering.entity.Category;
import com.xh.ordering.service.CategoryService;
import com.xh.ordering.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    /**
     * 查询所有启用的分类（用户端）
     * GET /api/category/list
     */
    @GetMapping("/list")
    public Result<List<Category>> getAvailableCategories() {
        return Result.success(categoryService.getAvailableCategories());
    }
    
    /**
     * 管理员：查询所有分类
     * GET /api/category/admin/list
     */
    @GetMapping("/admin/list")
    public Result<List<Category>> getAllCategories() {
        return Result.success(categoryService.getAllCategories());
    }
    
    /**
     * 管理员：创建分类
     * POST /api/category/admin/create
     */
    @PostMapping("/admin/create")
    public Result<Category> createCategory(@RequestBody Category category) {
        categoryService.createCategory(category);
        return Result.success("创建成功", category);
    }
    
    /**
     * 管理员：更新分类
     * PUT /api/category/admin/update
     */
    @PutMapping("/admin/update")
    public Result<?> updateCategory(@RequestBody Category category) {
        categoryService.updateCategory(category);
        return Result.success("更新成功");
    }
    
    /**
     * 管理员：删除分类
     * DELETE /api/category/admin/{id}
     */
    @DeleteMapping("/admin/{id}")
    public Result<?> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success("删除成功");
    }
}

