package com.xh.ordering.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xh.ordering.entity.Category;
import com.xh.ordering.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分类服务
 */
@Service
public class CategoryService {
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    /**
     * 查询所有启用的分类
     */
    public List<Category> getAvailableCategories() {
        return categoryMapper.selectList(
            new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort)
        );
    }
    
    /**
     * 查询所有分类（管理员）
     */
    public List<Category> getAllCategories() {
        return categoryMapper.selectList(
            new LambdaQueryWrapper<Category>()
                .orderByAsc(Category::getSort)
        );
    }
    
    /**
     * 创建分类
     */
    public void createCategory(Category category) {
        categoryMapper.insert(category);
    }
    
    /**
     * 更新分类
     */
    public void updateCategory(Category category) {
        categoryMapper.updateById(category);
    }
    
    /**
     * 删除分类
     */
    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
    }
    
    /**
     * 获取分类列表（支持分页和搜索）
     */
    public Map<String, Object> getCategoryList(Integer page, Integer pageSize, String name) {
        Page<Category> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        
        // 搜索条件
        if (StringUtils.hasText(name)) {
            queryWrapper.like(Category::getName, name);
        }
        
        // 按排序字段升序
        queryWrapper.orderByAsc(Category::getSort);
        
        IPage<Category> pageResult = categoryMapper.selectPage(pageParam, queryWrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("page", pageResult.getCurrent());
        result.put("pageSize", pageResult.getSize());
        
        return result;
    }
    
    /**
     * 更新分类状态
     */
    public void updateCategoryStatus(Long id, Integer status) {
        Category category = categoryMapper.selectById(id);
        if (category != null) {
            category.setStatus(status);
            categoryMapper.updateById(category);
        }
    }
}

