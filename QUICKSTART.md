# 快速开始指南

## 一、环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 8.x

## 二、数据库准备

1. 创建数据库：
```sql
CREATE DATABASE `xh_ordering` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行初始化脚本：
```bash
mysql -u root -p xh_ordering < sql/init.sql
```

或者直接在MySQL客户端中执行 `sql/init.sql` 文件。

## 三、配置修改

修改 `src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/xh_ordering?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root  # 修改为你的MySQL用户名
    password: root  # 修改为你的MySQL密码
```

## 四、启动项目

### 方式一：使用IDE
1. 导入项目到IDE（IntelliJ IDEA / Eclipse）
2. 等待Maven依赖下载完成
3. 运行 `XhOrderingApplication.main()` 方法

### 方式二：使用Maven命令
```bash
mvn clean install
mvn spring-boot:run
```

## 五、测试接口

项目启动后，服务地址：`http://localhost:8080/api`

### 测试用户登录
```bash
curl -X POST "http://localhost:8080/api/user/login?phone=13800138000&password=123456"
```

### 测试查询菜品
```bash
curl "http://localhost:8080/api/product/list"
```

### 测试下单（核心功能）
```bash
curl -X POST "http://localhost:8080/api/order/create?userId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {
        "productId": 1,
        "quantity": 2
      }
    ],
    "remark": "测试订单"
  }'
```

### 测试管理员登录
```bash
curl -X POST "http://localhost:8080/api/admin/login?username=admin&password=admin123"
```

### 测试充值积分
```bash
curl -X POST "http://localhost:8080/api/point/admin/recharge?userId=1&amount=1000&channel=测试充值&adminId=1"
```

## 六、测试数据说明

初始化脚本已包含以下测试数据：

### 管理员账号
- 管理员：`admin` / `admin123` (role=1)
- 后厨：`chef` / `chef123` (role=2)

### 用户账号
- 张三：`13800138000` / `123456` (积分：1000)
- 李四：`13800138001` / `123456` (积分：500)

### 分类和菜品
- 已创建5个分类（热菜、凉菜、汤品、主食、饮品）
- 已创建8个菜品

## 七、核心功能验证

### 1. 点餐下单流程验证

1. **查询用户积分**：
   ```bash
   curl "http://localhost:8080/api/user/info/1"
   ```

2. **查询菜品**：
   ```bash
   curl "http://localhost:8080/api/product/list"
   ```

3. **下单**（会扣减积分）：
   ```bash
   curl -X POST "http://localhost:8080/api/order/create?userId=1" \
     -H "Content-Type: application/json" \
     -d '{
       "items": [
         {"productId": 1, "quantity": 2}
       ]
     }'
   ```

4. **再次查询用户积分**（应该已扣减）：
   ```bash
   curl "http://localhost:8080/api/user/info/1"
   ```

5. **查询积分流水**（应该有一条扣除记录）：
   ```bash
   curl "http://localhost:8080/api/user/point-records/1"
   ```

6. **查询订单**：
   ```bash
   curl "http://localhost:8080/api/order/user/1"
   ```

### 2. 积分充值流程验证

1. **管理员登录**：
   ```bash
   curl -X POST "http://localhost:8080/api/admin/login?username=admin&password=admin123"
   ```

2. **充值积分**：
   ```bash
   curl -X POST "http://localhost:8080/api/point/admin/recharge?userId=1&amount=500&channel=测试充值&adminId=1"
   ```

3. **查询用户积分**（应该已增加）：
   ```bash
   curl "http://localhost:8080/api/user/info/1"
   ```

4. **查询积分流水**（应该有一条充值记录）：
   ```bash
   curl "http://localhost:8080/api/user/point-records/1"
   ```

## 八、常见问题

### 1. 数据库连接失败
- 检查MySQL服务是否启动
- 检查数据库连接配置是否正确
- 检查数据库用户权限

### 2. 端口被占用
- 修改 `application.yml` 中的 `server.port`

### 3. 依赖下载失败
- 检查网络连接
- 配置Maven镜像源（如阿里云镜像）

### 4. 事务不生效
- 确保使用的是 `@Transactional` 注解
- 确保异常被抛出（不要捕获异常）

## 九、下一步

1. 阅读 `README.md` 了解项目结构
2. 阅读 `API.md` 了解所有接口
3. 查看核心业务代码：
   - `OrderService.createOrder()` - 下单业务逻辑
   - `PointService.rechargePoints()` - 充值业务逻辑
4. 根据需求扩展功能

