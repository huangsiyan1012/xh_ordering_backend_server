package com.xh.ordering.controller;

import com.xh.ordering.entity.Category;
import com.xh.ordering.service.CategoryService;
import com.xh.ordering.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    
    /**
     * 管理员：获取分类列表（支持分页和搜索）
     * GET /api/category/admin/list-page
     */
    @GetMapping("/admin/list-page")
    public Result<Map<String, Object>> getCategoryList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name) {
        Map<String, Object> result = categoryService.getCategoryList(page, pageSize, name);
        return Result.success(result);
    }
    
    /**
     * 管理员：更新分类状态
     * PUT /api/category/admin/{id}/status
     */
    @PutMapping("/admin/{id}/status")
    public Result<Void> updateCategoryStatus(@PathVariable Long id, 
                                            @RequestParam Integer status) {
        categoryService.updateCategoryStatus(id, status);
        return Result.success(status == 1 ? "分类已上架" : "分类已下架", null);
    }
}

