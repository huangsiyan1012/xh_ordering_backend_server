package com.xh.ordering.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xh.ordering.entity.Category;
import com.xh.ordering.entity.Product;
import com.xh.ordering.mapper.CategoryMapper;
import com.xh.ordering.mapper.ProductMapper;
import com.xh.ordering.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜品服务
 */
@Service
public class ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    /**
     * 查询所有上架的菜品
     */
    public List<Product> getAvailableProducts() {
        return productMapper.selectList(
            new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1)
                .orderByAsc(Product::getCategoryId)
                .orderByDesc(Product::getCreatedAt)
        );
    }
    
    /**
     * 根据分类查询菜品
     */
    public List<Product> getProductsByCategory(Long categoryId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
            .eq(Product::getCategoryId, categoryId)
            .orderByDesc(Product::getCreatedAt);
        
        return productMapper.selectList(wrapper);
    }
    
    /**
     * 根据ID查询菜品
     */
    public Product getProductById(Long id) {
        return productMapper.selectById(id);
    }
    
    /**
     * 创建菜品
     */
    public void createProduct(Product product) {
        if (product.getCreatedAt() == null) {
            product.setCreatedAt(LocalDateTime.now());
        }
        productMapper.insert(product);
    }
    
    /**
     * 更新菜品
     */
    public void updateProduct(Product product) {
        productMapper.updateById(product);
    }
    
    /**
     * 上架/下架菜品
     */
    public void updateProductStatus(Long id, Integer status) {
        Product product = new Product();
        product.setId(id);
        product.setStatus(status);
        productMapper.updateById(product);
    }
    
    /**
     * 查询所有菜品（管理员）
     */
    public List<Product> getAllProducts() {
        return productMapper.selectList(
            new LambdaQueryWrapper<Product>()
                .orderByAsc(Product::getCategoryId)
                .orderByDesc(Product::getCreatedAt)
        );
    }
    
    /**
     * 获取菜品列表（支持分页和搜索，包含分类名称）
     */
    public Map<String, Object> getProductList(Integer page, Integer pageSize, String name, Long categoryId) {
        Page<Product> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        
        // 搜索条件
        if (StringUtils.hasText(name)) {
            queryWrapper.like(Product::getName, name);
        }
        if (categoryId != null) {
            queryWrapper.eq(Product::getCategoryId, categoryId);
        }
        
        // 按创建时间降序
        queryWrapper.orderByDesc(Product::getCreatedAt);
        
        IPage<Product> pageResult = productMapper.selectPage(pageParam, queryWrapper);
        
        // 获取所有分类信息，用于组装分类名称
        List<Category> categories = categoryMapper.selectList(null);
        Map<Long, String> categoryMap = categories.stream()
            .collect(Collectors.toMap(Category::getId, Category::getName));
        
        // 转换为ProductVO，填充分类名称
        List<ProductVO> voList = pageResult.getRecords().stream().map(product -> {
            ProductVO vo = new ProductVO();
            vo.setId(product.getId());
            vo.setCategoryId(product.getCategoryId());
            vo.setCategoryName(categoryMap.get(product.getCategoryId()));
            vo.setName(product.getName());
            vo.setPointCost(product.getPointCost());
            vo.setImage(product.getImage());
            vo.setDescription(product.getDescription());
            vo.setStatus(product.getStatus());
            vo.setCreatedAt(product.getCreatedAt());
            return vo;
        }).collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", voList);
        result.put("total", pageResult.getTotal());
        result.put("page", pageResult.getCurrent());
        result.put("pageSize", pageResult.getSize());
        
        return result;
    }
    
    /**
     * 删除菜品
     */
    public void deleteProduct(Long id) {
        productMapper.deleteById(id);
    }
}

