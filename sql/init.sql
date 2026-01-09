-- 点餐系统数据库初始化脚本
-- 数据库：MySQL 8.x

CREATE DATABASE IF NOT EXISTS `xh_ordering` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `xh_ordering`;

-- 管理员表
CREATE TABLE IF NOT EXISTS `admin` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `role` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '角色：1-管理员，2-后厨',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `points` INT(11) NOT NULL DEFAULT 0 COMMENT '积分余额',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 分类表
CREATE TABLE IF NOT EXISTS `category` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `sort` INT(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- 菜品表
CREATE TABLE IF NOT EXISTS `product` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category_id` INT(11) NOT NULL COMMENT '分类ID',
  `name` VARCHAR(100) NOT NULL COMMENT '菜品名称',
  `point_cost` INT(11) NOT NULL COMMENT '积分价格',
  `image` VARCHAR(255) DEFAULT NULL COMMENT '图片URL',
  `description` TEXT COMMENT '描述',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：1-上架，0-下架',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品表';

-- 订单表
CREATE TABLE IF NOT EXISTS `orders` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
  `user_id` INT(11) NOT NULL COMMENT '用户ID',
  `total_points` INT(11) NOT NULL COMMENT '总积分',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单明细表
CREATE TABLE IF NOT EXISTS `order_item` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` INT(11) NOT NULL COMMENT '订单ID',
  `product_name` VARCHAR(100) NOT NULL COMMENT '菜品名称（快照）',
  `product_point` INT(11) NOT NULL COMMENT '菜品积分价格（快照）',
  `quantity` INT(11) NOT NULL COMMENT '数量',
  `subtotal_point` INT(11) NOT NULL COMMENT '小计积分',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 积分充值表
CREATE TABLE IF NOT EXISTS `point_recharge` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` INT(11) NOT NULL COMMENT '用户ID',
  `recharge_amount` INT(11) NOT NULL COMMENT '充值金额（积分）',
  `channel` VARCHAR(50) DEFAULT NULL COMMENT '充值渠道',
  `created_by` INT(11) NOT NULL COMMENT '创建人（管理员ID）',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分充值表';

-- 积分流水表
CREATE TABLE IF NOT EXISTS `point_record` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` INT(11) NOT NULL COMMENT '用户ID',
  `change_amount` INT(11) NOT NULL COMMENT '变动金额（正数为增加，负数为减少）',
  `type` TINYINT(1) NOT NULL COMMENT '类型：1-充值，2-点餐扣除，3-系统调整',
  `related_id` INT(11) DEFAULT NULL COMMENT '关联ID（订单ID或充值记录ID）',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分流水表';

-- 初始化测试数据

-- 插入管理员（密码：admin123）
INSERT INTO `admin` (`username`, `password`, `role`, `status`) VALUES
('admin', 'admin123', 1, 1),
('chef', 'chef123', 2, 1);

-- 插入测试用户（密码：123456）
INSERT INTO `user` (`name`, `phone`, `password`, `points`, `status`) VALUES
('张三', '13800138000', '123456', 1000, 1),
('李四', '13800138001', '123456', 500, 1);

-- 插入分类
INSERT INTO `category` (`name`, `sort`, `status`) VALUES
('热菜', 1, 1),
('凉菜', 2, 1),
('汤品', 3, 1),
('主食', 4, 1),
('饮品', 5, 1);

-- 插入菜品
INSERT INTO `product` (`category_id`, `name`, `point_cost`, `image`, `description`, `status`) VALUES
(1, '宫保鸡丁', 50, NULL, '经典川菜，麻辣鲜香', 1),
(1, '鱼香肉丝', 45, NULL, '酸甜可口，下饭神器', 1),
(1, '麻婆豆腐', 35, NULL, '麻辣鲜香，嫩滑爽口', 1),
(2, '凉拌黄瓜', 15, NULL, '清爽开胃', 1),
(2, '凉拌木耳', 20, NULL, '营养健康', 1),
(3, '西红柿鸡蛋汤', 25, NULL, '家常美味', 1),
(4, '米饭', 5, NULL, '香喷喷的白米饭', 1),
(5, '可乐', 10, NULL, '冰镇可乐', 1);

