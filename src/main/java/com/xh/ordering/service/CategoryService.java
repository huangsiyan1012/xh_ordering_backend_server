package com.xh.ordering.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xh.ordering.entity.Category;
import com.xh.ordering.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}

