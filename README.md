# 点餐系统后端服务

## 项目简介

这是一个点餐系统的后台后端框架，采用前后端分离架构，用于教学/练习项目。

## 技术栈

- **框架**: Spring Boot 2.7.14
- **ORM**: MyBatis Plus 3.5.3.1
- **数据库**: MySQL 8.x
- **Java版本**: 1.8
- **安全**: Spring Security + JWT
- **密码加密**: BCrypt
- **API文档**: Swagger 3.0
- **监控**: Spring Boot Actuator

## 项目结构

```
src/main/java/com/xh/ordering/
├── XhOrderingApplication.java      # 启动类
├── controller/                      # 控制器层
│   ├── AdminController.java        # 管理员接口
│   ├── UserController.java         # 用户接口
│   ├── ProductController.java      # 菜品接口
│   ├── CategoryController.java     # 分类接口
│   ├── OrderController.java        # 订单接口（核心）
│   └── PointController.java        # 积分接口
├── service/                         # 服务层
│   ├── OrderService.java           # 订单服务（核心业务逻辑）
│   ├── ProductService.java         # 菜品服务
│   ├── CategoryService.java        # 分类服务
│   ├── UserService.java            # 用户服务
│   ├── PointService.java           # 积分服务
│   └── AdminService.java           # 管理员服务
├── mapper/                          # 数据访问层
│   ├── OrderMapper.java
│   ├── OrderItemMapper.java
│   ├── ProductMapper.java
│   ├── CategoryMapper.java
│   ├── UserMapper.java
│   ├── PointRecordMapper.java
│   ├── PointRechargeMapper.java
│   └── AdminMapper.java
├── entity/                          # 实体类
│   ├── Admin.java
│   ├── User.java
│   ├── Category.java
│   ├── Product.java
│   ├── Orders.java
│   ├── OrderItem.java
│   ├── PointRecharge.java
│   └── PointRecord.java
├── dto/                             # 数据传输对象
│   └── OrderCreateDTO.java         # 创建订单DTO
├── vo/                              # 视图对象
│   └── Result.java                 # 统一响应封装
└── exception/                       # 异常处理
    ├── BusinessException.java       # 业务异常
    └── GlobalExceptionHandler.java  # 全局异常处理器
```

## 数据库表结构

### admin（管理员表）
- id, username, password, role(1管理员 2后厨), status, created_at

### user（用户表）
- id, name, phone, password, points, status, created_at

### category（分类表）
- id, name, sort, status

### product（菜品表）
- id, category_id, name, point_cost, image, description, status, created_at

### orders（订单表）
- id, order_no, user_id, total_points, remark, created_at

### order_item（订单明细表）
- id, order_id, product_name, product_point, quantity, subtotal_point

### point_recharge（积分充值表）
- id, user_id, recharge_amount, channel, created_by, created_at

### point_record（积分流水表）
- id, user_id, change_amount, type(1充值 2点餐扣除 3系统调整), related_id, created_at

## 核心API接口

### 用户相关
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录
- `GET /api/user/info/{userId}` - 查询用户信息
- `GET /api/user/point-records/{userId}` - 查询积分流水

### 菜品相关
- `GET /api/product/list` - 查询所有上架菜品
- `GET /api/product/category/{categoryId}` - 根据分类查询菜品
- `GET /api/product/{id}` - 查询菜品详情
- `POST /api/product/admin/create` - 管理员创建菜品
- `PUT /api/product/admin/update` - 管理员更新菜品
- `PUT /api/product/admin/status` - 管理员上架/下架菜品

### 分类相关
- `GET /api/category/list` - 查询所有启用分类
- `POST /api/category/admin/create` - 管理员创建分类
- `PUT /api/category/admin/update` - 管理员更新分类
- `DELETE /api/category/admin/{id}` - 管理员删除分类

### 订单相关（核心）
- `POST /api/order/create` - 用户下单（核心接口，包含事务）
- `GET /api/order/user/{userId}` - 查询用户订单列表
- `GET /api/order/{orderId}` - 查询订单详情
- `GET /api/order/admin/list` - 管理员/后厨查询所有订单

### 积分相关
- `POST /api/point/admin/recharge` - 管理员给用户充值积分

### 管理员相关
- `POST /api/admin/login` - 管理员登录
- `GET /api/admin/info/{adminId}` - 查询管理员信息

## 核心业务逻辑

### 点餐下单流程（OrderService.createOrder）

1. **校验用户**：检查用户是否存在且状态正常
2. **校验菜品**：查询菜品信息，检查是否上架，计算总积分
3. **校验积分**：检查用户积分是否足够
4. **生成订单**：创建订单记录
5. **生成订单明细**：创建订单明细记录（保存菜品快照）
6. **扣减积分**：使用乐观锁扣减用户积分
7. **记录积分流水**：记录积分变动流水

**重要**：上述操作在 `@Transactional` 事务中执行，保证原子性。

### 积分充值流程（PointService.rechargePoints）

1. **校验参数**：检查充值金额是否大于0
2. **校验用户**：检查用户是否存在
3. **增加积分**：增加用户积分
4. **记录充值记录**：创建充值记录
5. **记录积分流水**：记录积分变动流水

**重要**：上述操作在 `@Transactional` 事务中执行，保证原子性。

## 配置说明

### application.yml

需要修改数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/xh_ordering?...
    username: root
    password: root
```

### 数据库初始化

请根据表结构创建数据库和表。建议使用MySQL 8.x。

## 运行说明

1. 确保MySQL数据库已启动并创建数据库 `xh_ordering`
2. 根据表结构创建所有表
3. 修改 `application.yml` 中的数据库连接信息
4. 运行 `XhOrderingApplication.main()` 启动项目
5. 服务将在 `http://localhost:8080/api` 启动

## 安全特性

1. **密码加密**: 使用BCrypt加密存储密码
2. **JWT认证**: 基于JWT的无状态认证机制
3. **权限控制**: Spring Security实现基于角色的访问控制
4. **跨域配置**: 支持CORS跨域请求
5. **请求日志**: 记录所有API请求日志
6. **统一异常处理**: 全局异常处理器统一处理异常
7. **参数校验**: 使用Bean Validation进行参数校验

## 环境配置

### 开发环境

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

或设置环境变量：
```bash
export SPRING_PROFILES_ACTIVE=dev
```

### 生产环境

```bash
java -jar -Dspring.profiles.active=prod xh-ordering-server-1.0.0.jar
```

详细部署说明请参考 [DEPLOYMENT.md](DEPLOYMENT.md)

## API文档

启动项目后访问：
- Swagger UI: http://localhost:8080/api/swagger-ui/index.html
- API Docs: http://localhost:8080/api/v3/api-docs

## 认证说明

### 用户认证流程

1. 用户注册/登录获取Token
2. 后续请求在Header中携带Token：`Authorization: Bearer {token}`
3. 系统自动验证Token并设置用户上下文

### Token格式

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 角色说明

- `ROLE_USER`: 普通用户
- `ROLE_ADMIN`: 管理员
- `ROLE_CHEF`: 后厨

## 注意事项

1. **生产环境部署前必须修改**：
   - JWT密钥（`jwt.secret`）
   - 数据库密码
   - 所有默认密码

2. **密码加密**: 数据库中的密码已使用BCrypt加密，不能直接使用明文密码登录

3. **Token过期**: 默认Token有效期为24小时，可在配置文件中修改

4. **日志管理**: 生产环境日志会写入文件，注意定期清理

## 扩展建议

1. 添加Redis缓存
2. 添加消息队列（RabbitMQ/Kafka）
3. 添加分布式锁
4. 添加限流和熔断
5. 添加单元测试和集成测试
6. 添加Docker容器化部署

