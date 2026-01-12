package com.xh.ordering.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
        
        // 静态文件资源（上传的图片）
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir.toAbsolutePath().toString() + "/");
    }
}

