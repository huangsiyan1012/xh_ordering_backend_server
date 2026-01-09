package com.xh.ordering.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xh.ordering.entity.Product;
import com.xh.ordering.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 菜品服务
 */
@Service
public class ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
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
}

