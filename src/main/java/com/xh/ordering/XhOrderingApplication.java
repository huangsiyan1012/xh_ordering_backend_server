package com.xh.ordering;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xh.ordering.mapper")
public class XhOrderingApplication {
    public static void main(String[] args) {
        SpringApplication.run(XhOrderingApplication.class, args);
    }
}

