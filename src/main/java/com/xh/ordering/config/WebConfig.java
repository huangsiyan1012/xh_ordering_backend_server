package com.xh.ordering.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Web配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${file.upload.path:uploads}")
    private String uploadPath;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Swagger UI资源
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
        
        // 解析上传目录路径（如果是相对路径，则相对于项目根目录）
        Path uploadDir;
        if (Paths.get(uploadPath).isAbsolute()) {
            uploadDir = Paths.get(uploadPath);
        } else {
            // 相对于项目根目录
            String projectRoot = System.getProperty("user.dir");
            uploadDir = Paths.get(projectRoot, uploadPath);
        }
        
        // 确保目录存在
        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
        } catch (Exception e) {
            System.err.println("创建上传目录失败: " + e.getMessage());
        }
        
        // 获取绝对路径并转换为正确的文件URL格式
        String absolutePath = uploadDir.toAbsolutePath().toString();
        // Spring Boot 的 file: 协议支持 Windows 路径，但建议使用正斜杠
        // 对于 Windows 路径如 E:/xh_ordering/upload，转换为 file:E:/xh_ordering/upload/
        String normalizedPath = absolutePath.replace("\\", "/");
        String fileUrl = "file:" + normalizedPath;
        if (!fileUrl.endsWith("/")) {
            fileUrl += "/";
        }
        
        System.out.println("静态资源路径配置: /uploads/** -> " + fileUrl);
        System.out.println("上传目录绝对路径: " + absolutePath);
        
        // 静态文件资源（上传的图片）
        // 注意：因为 context-path 是 /api，所以实际访问路径是 /api/uploads/**
        // 但 addResourceHandler 会自动处理 context-path，这里配置 /uploads/** 即可
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(fileUrl)
                .setCachePeriod(3600); // 缓存1小时
    }
}

