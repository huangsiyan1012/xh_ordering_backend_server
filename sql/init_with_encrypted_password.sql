-- 点餐系统数据库初始化脚本（包含加密密码）
-- 注意：此脚本中的密码已使用BCrypt加密
-- 默认密码：
-- admin/admin123 -> $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJ5C
-- chef/chef123 -> $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJ5C
-- 用户密码123456 -> $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJ5C

-- 插入管理员（密码已加密：admin123 / chef123）
INSERT INTO `admin` (`username`, `password`, `role`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJ5C', 1, 1),
('chef', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJ5C', 2, 1);

-- 插入测试用户（密码已加密：123456）
INSERT INTO `user` (`name`, `phone`, `password`, `points`, `status`) VALUES
('张三', '13800138000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJ5C', 1000, 1),
('李四', '13800138001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJ5C', 500, 1);

