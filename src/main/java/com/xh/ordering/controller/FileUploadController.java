package com.xh.ordering.controller;

import com.xh.ordering.vo.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/file")
public class FileUploadController {
    
    @Value("${file.upload.path:uploads}")
    private String uploadPath;
    
    @Value("${file.upload.url-prefix:/uploads}")
    private String urlPrefix;
    
    /**
     * 上传图片
     * POST /api/file/upload
     */
    @PostMapping("/upload")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        
        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return Result.error("文件名不能为空");
        }
        
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex);
        }
        
        // 只允许图片格式
        if (!extension.matches("\\.(?i)(jpg|jpeg|png|gif|bmp|webp)")) {
            return Result.error("只支持图片格式：jpg, jpeg, png, gif, bmp, webp");
        }
        
        try {
            // 解析上传目录路径（如果是相对路径，则相对于项目根目录）
            Path uploadDir;
            if (Paths.get(uploadPath).isAbsolute()) {
                uploadDir = Paths.get(uploadPath);
            } else {
                // 相对于项目根目录
                String projectRoot = System.getProperty("user.dir");
                uploadDir = Paths.get(projectRoot, uploadPath);
            }
            
            // 创建上传目录（如果不存在）
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            
            // 生成唯一文件名
            String fileName = UUID.randomUUID().toString() + extension;
            Path filePath = uploadDir.resolve(fileName);
            
            // 确保父目录存在
            Files.createDirectories(filePath.getParent());
            
            // 保存文件
            file.transferTo(filePath.toFile());
            
            // 返回相对路径（用于存储到数据库）
            String relativePath = urlPrefix + "/" + fileName;
            
            return Result.success("上传成功", relativePath);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
}

