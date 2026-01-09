# 点餐系统 API 接口文档

## 基础信息

- **Base URL**: `http://localhost:8080/api`
- **Content-Type**: `application/json`
- **响应格式**: 统一使用 `Result<T>` 封装
- **认证方式**: JWT Token（Bearer Token）

## 认证说明

### 获取Token

用户登录或注册后，响应中会返回 `token` 字段，后续请求需要在Header中携带：

```
Authorization: Bearer {token}
```

### Token有效期

默认Token有效期为24小时，过期后需要重新登录。

### 角色说明

- `ROLE_USER`: 普通用户
- `ROLE_ADMIN`: 管理员（role=1）
- `ROLE_CHEF`: 后厨（role=2）

### 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

## 一、用户相关接口

### 1.1 用户注册
- **接口**: `POST /user/register`
- **参数**:
  - `name` (String): 姓名
  - `phone` (String): 手机号
  - `password` (String): 密码
- **响应**: 返回用户信息

### 1.2 用户登录
- **接口**: `POST /user/login`
- **参数**:
  - `phone` (String): 手机号
  - `password` (String): 密码
- **响应**: 返回用户信息和Token
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "userId": 1,
    "name": "张三",
    "phone": "13800138000",
    "points": 1000,
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### 1.3 查询当前用户信息
- **接口**: `GET /user/info`
- **认证**: 需要Token
- **响应**: 返回当前登录用户信息（不含密码）

### 1.4 查询指定用户信息（管理员接口）
- **接口**: `GET /user/info/{userId}`
- **认证**: 需要Token（管理员角色）
- **响应**: 返回指定用户信息（不含密码）

### 1.5 查询当前用户积分流水
- **接口**: `GET /user/point-records`
- **认证**: 需要Token
- **响应**: 返回当前用户的积分流水列表

### 1.6 查询指定用户积分流水（管理员接口）
- **接口**: `GET /user/point-records/{userId}`
- **认证**: 需要Token（管理员角色）
- **响应**: 返回指定用户的积分流水列表

## 二、菜品相关接口

### 2.1 查询所有上架菜品（用户端）
- **接口**: `GET /product/list`
- **响应**: 返回菜品列表

### 2.2 根据分类查询菜品
- **接口**: `GET /product/category/{categoryId}`
- **响应**: 返回该分类下的菜品列表

### 2.3 查询菜品详情
- **接口**: `GET /product/{id}`
- **响应**: 返回菜品详情

### 2.4 管理员：查询所有菜品
- **接口**: `GET /product/admin/list`
- **响应**: 返回所有菜品（包括下架的）

### 2.5 管理员：创建菜品
- **接口**: `POST /product/admin/create`
- **请求体**:
```json
{
  "categoryId": 1,
  "name": "宫保鸡丁",
  "pointCost": 50,
  "image": "http://example.com/image.jpg",
  "description": "经典川菜",
  "status": 1
}
```

### 2.6 管理员：更新菜品
- **接口**: `PUT /product/admin/update`
- **请求体**: 同创建菜品，需包含 `id`

### 2.7 管理员：上架/下架菜品
- **接口**: `PUT /product/admin/status`
- **参数**:
  - `id` (Integer): 菜品ID
  - `status` (Integer): 1-上架，0-下架

## 三、分类相关接口

### 3.1 查询所有启用分类（用户端）
- **接口**: `GET /category/list`
- **响应**: 返回分类列表（按sort排序）

### 3.2 管理员：查询所有分类
- **接口**: `GET /category/admin/list`
- **响应**: 返回所有分类

### 3.3 管理员：创建分类
- **接口**: `POST /category/admin/create`
- **请求体**:
```json
{
  "name": "热菜",
  "sort": 1,
  "status": 1
}
```

### 3.4 管理员：更新分类
- **接口**: `PUT /category/admin/update`
- **请求体**: 同创建分类，需包含 `id`

### 3.5 管理员：删除分类
- **接口**: `DELETE /category/admin/{id}`

## 四、订单相关接口（核心）

### 4.1 用户下单（核心接口）
- **接口**: `POST /order/create`
- **认证**: 需要Token
- **说明**: 用户ID从Token中自动获取，无需传递
- **请求体**:
```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ],
  "remark": "不要辣"
}
```
- **业务逻辑**:
  1. 校验用户状态
  2. 校验菜品是否存在且上架
  3. 计算总积分
  4. 校验用户积分是否足够
  5. 生成订单
  6. 生成订单明细（保存菜品快照）
  7. 扣减用户积分
  8. 记录积分流水
  - **注意**: 以上操作在事务中执行，保证原子性

### 4.2 查询当前用户订单列表
- **接口**: `GET /order/user/list`
- **认证**: 需要Token
- **响应**: 返回当前用户的所有订单（按创建时间倒序）

### 4.3 查询指定用户订单列表（管理员接口）
- **接口**: `GET /order/user/{userId}`
- **认证**: 需要Token（管理员角色）
- **响应**: 返回指定用户的所有订单（按创建时间倒序）

### 4.4 查询订单详情
- **接口**: `GET /order/{orderId}`
- **认证**: 需要Token
- **响应**: 返回订单信息和订单明细列表
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "order": {
      "id": 1,
      "orderNo": "ORD1234567890",
      "userId": 1,
      "totalPoints": 150,
      "remark": "不要辣",
      "createdAt": "2024-01-01 12:00:00"
    },
    "items": [
      {
        "id": 1,
        "orderId": 1,
        "productName": "宫保鸡丁",
        "productPoint": 50,
        "quantity": 2,
        "subtotalPoint": 100
      }
    ]
  }
}
```

### 4.5 管理员/后厨：查询所有订单
- **接口**: `GET /order/admin/list`
- **认证**: 需要Token（管理员或后厨角色）
- **响应**: 返回所有订单（按创建时间倒序）

## 五、积分相关接口

### 5.1 管理员：给用户充值积分
- **接口**: `POST /point/admin/recharge`
- **认证**: 需要Token（管理员角色）
- **说明**: 管理员ID从Token中自动获取，无需传递
- **参数**:
  - `userId` (Integer): 用户ID
  - `amount` (Integer): 充值金额（积分）
  - `channel` (String): 充值渠道（可选，默认"后台充值"）
- **业务逻辑**:
  1. 增加用户积分
  2. 记录充值记录
  3. 记录积分流水
  - **注意**: 以上操作在事务中执行，保证原子性

## 六、管理员相关接口

### 6.1 管理员登录
- **接口**: `POST /admin/login`
- **参数**:
  - `username` (String): 用户名
  - `password` (String): 密码
- **响应**: 返回管理员信息和Token
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "adminId": 1,
    "username": "admin",
    "role": 1,
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### 6.2 查询当前管理员信息
- **接口**: `GET /admin/info`
- **认证**: 需要Token（管理员角色）
- **响应**: 返回当前登录管理员信息（不含密码）

### 6.3 查询指定管理员信息
- **接口**: `GET /admin/info/{adminId}`
- **认证**: 需要Token（管理员角色）
- **响应**: 返回指定管理员信息（不含密码）

## 错误码说明

- `200`: 操作成功
- `400`: 参数校验失败
- `500`: 系统异常或业务异常

## 示例请求

### 用户登录示例

```bash
curl -X POST "http://localhost:8080/api/user/login" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "phone=13800138000&password=123456"
```

响应中获取token，后续请求携带token：

### 用户下单示例

```bash
curl -X POST "http://localhost:8080/api/order/create" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "items": [
      {
        "productId": 1,
        "quantity": 2
      },
      {
        "productId": 2,
        "quantity": 1
      }
    ],
    "remark": "不要辣"
  }'
```

### 管理员登录示例

```bash
curl -X POST "http://localhost:8080/api/admin/login" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=admin&password=admin123"
```

### 管理员充值积分示例

```bash
curl -X POST "http://localhost:8080/api/point/admin/recharge?userId=1&amount=500&channel=后台充值" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

