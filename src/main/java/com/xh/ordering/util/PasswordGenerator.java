package com.xh.ordering.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码生成工具类（用于生成加密密码）
 * 使用方法：运行main方法，将输出的加密密码更新到数据库
 */
public class PasswordGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("=== 密码加密工具 ===");
        System.out.println();
        System.out.println("admin123: " + encoder.encode("admin123"));
        System.out.println("chef123: " + encoder.encode("chef123"));
        System.out.println("123456: " + encoder.encode("123456"));
        System.out.println();
        System.out.println("请将生成的加密密码更新到数据库中");
    }
}

