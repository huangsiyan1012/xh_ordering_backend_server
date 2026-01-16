package com.xh.ordering.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Slf4j
@Component
public class JwtUtil {
    
    @Value("${jwt.secret:xh-ordering-secret-key-2024-please-change-in-production-environment}")
    private String secret;
    
    @Value("${jwt.expiration:86400000}") // 默认24小时
    private Long expiration;
    
    /**
     * 生成SecretKey
     * 注意：必须与 8080 服务使用相同的密钥生成逻辑，确保 Token 可以跨服务验证
     */
    private SecretKey getSecretKey() {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        // HS256 需要 >= 256bit (32字节) key
        // 如果密钥长度 < 32 字节，填充到 32 字节（与 8080 服务保持一致）
        if (bytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(bytes, 0, padded, 0, bytes.length);
            return Keys.hmacShaKeyFor(padded);
        }
        return Keys.hmacShaKeyFor(bytes);
    }
    
    /**
     * 生成Token
     */
    public String generateToken(Long userId, String username, Integer role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * 从Token中获取Claims
     */
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("解析Token失败：{}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 从Token中获取用户ID
     * 兼容两种Token格式：
     * 1. 8080服务生成的Token：subject是userId的字符串，没有claims
     * 2. 8081服务生成的Token：claims中有userId字段
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims != null) {
            // 优先从claims中获取userId（8081格式）
            Object userId = claims.get("userId");
            if (userId != null) {
                if (userId instanceof Integer) {
                    return ((Integer) userId).longValue();
                } else if (userId instanceof Long) {
                    return (Long) userId;
                } else if (userId instanceof Number) {
                    return ((Number) userId).longValue();
                }
            }
            
            // 如果claims中没有userId，尝试从subject中解析（8080格式）
            String subject = claims.getSubject();
            if (subject != null && !subject.isEmpty()) {
                try {
                    return Long.valueOf(subject);
                } catch (NumberFormatException e) {
                    // subject不是数字，可能是username，忽略
                }
            }
        }
        return null;
    }
    
    /**
     * 从Token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims != null) {
            return claims.getSubject();
        }
        return null;
    }
    
    /**
     * 从Token中获取角色
     */
    public Integer getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims != null) {
            return (Integer) claims.get("role");
        }
        return null;
    }
    
    /**
     * 验证Token是否有效
     */
    public Boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims != null && !isTokenExpired(claims);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 判断Token是否过期
     */
    private Boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }
}

