package com.xh.ordering.controller;

import com.xh.ordering.entity.Product;
import com.xh.ordering.service.ProductService;
import com.xh.ordering.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品控制器
 */
@RestController
@RequestMapping("/product")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    /**
     * 查询所有上架的菜品（用户端）
     * GET /api/product/list
     */
    @GetMapping("/list")
    public Result<List<Product>> getAvailableProducts() {
        return Result.success(productService.getAvailableProducts());
    }
    
    /**
     * 根据分类查询菜品
     * GET /api/product/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    public Result<List<Product>> getProductsByCategory(@PathVariable Long categoryId) {
        return Result.success(productService.getProductsByCategory(categoryId));
    }
    
    /**
     * 查询菜品详情
     * GET /api/product/{id}
     */
    @GetMapping("/{id}")
    public Result<Product> getProductById(@PathVariable Long id) {
        return Result.success(productService.getProductById(id));
    }
    
    /**
     * 管理员：查询所有菜品
     * GET /api/product/admin/list
     */
    @GetMapping("/admin/list")
    public Result<List<Product>> getAllProducts() {
        return Result.success(productService.getAllProducts());
    }
    
    /**
     * 管理员：创建菜品
     * POST /api/product/admin/create
     */
    @PostMapping("/admin/create")
    public Result<Product> createProduct(@RequestBody Product product) {
        productService.createProduct(product);
        return Result.success("创建成功", product);
    }
    
    /**
     * 管理员：更新菜品
     * PUT /api/product/admin/update
     */
    @PutMapping("/admin/update")
    public Result<?> updateProduct(@RequestBody Product product) {
        productService.updateProduct(product);
        return Result.success("更新成功");
    }
    
    /**
     * 管理员：上架/下架菜品
     * PUT /api/product/admin/status
     */
    @PutMapping("/admin/status")
    public Result<?> updateProductStatus(@RequestParam Long id, 
                                        @RequestParam Integer status) {
        productService.updateProductStatus(id, status);
        return Result.success("操作成功");
    }
}

