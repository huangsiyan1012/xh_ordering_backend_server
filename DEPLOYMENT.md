# 部署指南

## 一、环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 8.x
- Linux服务器（推荐CentOS 7+或Ubuntu 18+）

## 二、数据库准备

### 1. 创建数据库

```sql
CREATE DATABASE IF NOT EXISTS `xh_ordering` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 执行初始化脚本

```bash
mysql -u root -p xh_ordering < sql/init.sql
```

### 3. 初始化管理员密码（重要）

由于密码已加密存储，首次部署需要手动创建管理员账号或使用工具类生成加密密码。

**方式一：使用Java工具类生成密码**

创建一个临时Java类：

```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("admin123: " + encoder.encode("admin123"));
        System.out.println("123456: " + encoder.encode("123456"));
    }
}
```

**方式二：使用在线BCrypt工具**

访问 https://www.bcrypt-generator.com/ 生成加密密码

然后更新数据库：

```sql
UPDATE admin SET password = '生成的加密密码' WHERE username = 'admin';
```

## 三、编译打包

### 1. 编译项目

```bash
mvn clean package -DskipTests
```

### 2. 打包结果

打包后的jar文件位于：`target/xh-ordering-server-1.0.0.jar`

## 四、配置文件

### 1. 生产环境配置

复制 `application-prod.yml` 并根据实际情况修改：

```yaml
spring:
  datasource:
    url: jdbc:mysql://your-db-host:3306/xh_ordering?...
    username: your_db_username
    password: ${DB_PASSWORD:your_db_password}

jwt:
  secret: ${JWT_SECRET:your-very-long-and-random-secret-key}
```

### 2. 环境变量（推荐）

使用环境变量管理敏感信息：

```bash
export DB_PASSWORD=your_db_password
export JWT_SECRET=your-very-long-and-random-secret-key
```

## 五、启动服务

### 1. 使用java命令启动

```bash
java -jar -Dspring.profiles.active=prod xh-ordering-server-1.0.0.jar
```

### 2. 使用nohup后台运行

```bash
nohup java -jar -Dspring.profiles.active=prod xh-ordering-server-1.0.0.jar > app.log 2>&1 &
```

### 3. 使用systemd服务（推荐）

创建服务文件 `/etc/systemd/system/xh-ordering.service`：

```ini
[Unit]
Description=XH Ordering Server
After=network.target

[Service]
Type=simple
User=your_user
WorkingDirectory=/path/to/app
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod /path/to/app/xh-ordering-server-1.0.0.jar
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal

Environment="DB_PASSWORD=your_db_password"
Environment="JWT_SECRET=your-very-long-and-random-secret-key"

[Install]
WantedBy=multi-user.target
```

启动服务：

```bash
sudo systemctl daemon-reload
sudo systemctl enable xh-ordering
sudo systemctl start xh-ordering
sudo systemctl status xh-ordering
```

## 六、Nginx反向代理（可选）

### 1. Nginx配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### 2. HTTPS配置（推荐）

使用Let's Encrypt免费SSL证书：

```bash
sudo certbot --nginx -d your-domain.com
```

## 七、日志管理

### 1. 日志位置

- 开发环境：`logs/xh-ordering.log`
- 生产环境：`/var/log/xh-ordering/xh-ordering.log`

### 2. 日志轮转

创建 `/etc/logrotate.d/xh-ordering`：

```
/var/log/xh-ordering/*.log {
    daily
    rotate 30
    compress
    delaycompress
    missingok
    notifempty
    create 0644 your_user your_group
}
```

## 八、监控和健康检查

### 1. 健康检查接口

```bash
curl http://localhost:8080/api/actuator/health
```

### 2. 监控指标

- 应用状态：`/actuator/health`
- 应用信息：`/actuator/info`

## 九、安全建议

### 1. 修改默认密码

部署后立即修改所有默认密码。

### 2. 配置防火墙

```bash
# 只开放必要端口
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --reload
```

### 3. 数据库安全

- 使用强密码
- 限制数据库访问IP
- 定期备份数据库

### 4. JWT密钥

- 使用足够长的随机字符串（至少32字符）
- 定期轮换密钥
- 不要将密钥提交到代码仓库

### 5. HTTPS

生产环境必须使用HTTPS，保护数据传输安全。

## 十、性能优化

### 1. JVM参数

```bash
java -Xms512m -Xmx1024m -XX:+UseG1GC -jar xh-ordering-server-1.0.0.jar
```

### 2. 数据库连接池

已在 `application-prod.yml` 中配置HikariCP连接池。

### 3. 日志级别

生产环境使用 `info` 级别，减少日志输出。

## 十一、备份和恢复

### 1. 数据库备份

```bash
# 备份
mysqldump -u root -p xh_ordering > backup_$(date +%Y%m%d).sql

# 恢复
mysql -u root -p xh_ordering < backup_20240101.sql
```

### 2. 应用备份

定期备份：
- 应用jar包
- 配置文件
- 日志文件

## 十二、故障排查

### 1. 查看日志

```bash
tail -f /var/log/xh-ordering/xh-ordering.log
```

### 2. 查看服务状态

```bash
sudo systemctl status xh-ordering
```

### 3. 检查端口占用

```bash
netstat -tlnp | grep 8080
```

### 4. 检查数据库连接

```bash
mysql -u root -p -e "USE xh_ordering; SHOW TABLES;"
```

## 十三、更新部署

### 1. 备份当前版本

```bash
cp xh-ordering-server-1.0.0.jar xh-ordering-server-1.0.0.jar.backup
```

### 2. 停止服务

```bash
sudo systemctl stop xh-ordering
```

### 3. 更新jar包

```bash
cp new-version.jar xh-ordering-server-1.0.0.jar
```

### 4. 启动服务

```bash
sudo systemctl start xh-ordering
```

### 5. 验证

```bash
curl http://localhost:8080/api/actuator/health
```

