# Nacos 配置中心 - 配置文件目录

## 📁 目录说明

本目录包含所有需要导入到 Nacos 配置中心的配置文件。

---

## 📋 配置文件清单

### 1. 公共配置

#### `common-config.yml`
- **Data ID**: `common-config.yml`
- **Group**: `DEFAULT_GROUP`
- **说明**: 所有服务共享的公共配置
- **包含内容**:
  - MyBatis-Plus 配置
  - JWT 配置
  - Knife4j 配置
  - 日志配置
  - Spring 通用配置

---

### 2. 基础设施配置

#### `mysql-config.yml`
- **Data ID**: `mysql-config.yml`
- **Group**: `DEFAULT_GROUP`
- **说明**: MySQL 数据库连接配置
- **包含内容**:
  - 数据源配置
  - Druid 连接池配置
  - MyBatis-Plus mapper 配置

#### `redis-config.yml`
- **Data ID**: `redis-config.yml`
- **Group**: `DEFAULT_GROUP`
- **说明**: Redis 缓存配置
- **包含内容**:
  - Redis 连接配置
  - Lettuce 连接池配置

---

### 3. 服务专属配置

#### `murder-user.yml`
- **Data ID**: `murder-user.yml`
- **Group**: `DEFAULT_GROUP`
- **说明**: 用户服务专属配置
- **端口**: 8082
- **Redis DB**: 0
- **特殊配置**: Springdoc API 文档

#### `murder-store.yml`
- **Data ID**: `murder-store.yml`
- **Group**: `DEFAULT_GROUP`
- **说明**: 门店服务专属配置
- **端口**: 8083
- **Redis DB**: 1

#### `murder-script.yml`
- **Data ID**: `murder-script.yml`
- **Group**: `DEFAULT_GROUP`
- **说明**: 剧本服务专属配置
- **端口**: 8084
- **Redis DB**: 3
- **特殊配置**: 文件上传、MyBatis-Plus 分页

#### `murder-reservation.yml`
- **Data ID**: `murder-reservation.yml`
- **Group**: `DEFAULT_GROUP`
- **说明**: 预约服务专属配置
- **端口**: 8085
- **Redis DB**: 4
- **特殊配置**: 支付宝支付

---

## 🚀 导入方法

### 方法1：使用自动导入脚本
```bash
双击运行: ../导入Nacos配置.bat
```

### 方法2：手动导入
1. 访问 Nacos 控制台: http://localhost:8848/nacos
2. 登录（用户名/密码：nacos/nacos）
3. 进入【配置管理】→【配置列表】
4. 点击右上角【+】按钮
5. 按照上述清单，逐个创建配置：
   - Data ID: 使用上述表格中的名称
   - Group: `DEFAULT_GROUP`
   - 配置格式: `YAML`
   - 配置内容: 复制对应文件内容
   - 点击【发布】

---

## 📊 配置依赖关系

```
murder-user (8082)
├── common-config.yml  ← 公共配置
├── mysql-config.yml   ← MySQL 配置
├── redis-config.yml   ← Redis 配置
└── murder-user.yml    ← 用户服务专属配置

murder-store (8083)
├── common-config.yml
├── mysql-config.yml
├── redis-config.yml
└── murder-store.yml

murder-script (8084)
├── common-config.yml
├── mysql-config.yml
├── redis-config.yml
└── murder-script.yml

murder-reservation (8085)
├── common-config.yml
├── mysql-config.yml
├── redis-config.yml
└── murder-reservation.yml
```

---

## ⚙️ 配置优先级

当配置项冲突时，优先级从高到低：

1. **服务专属配置**（如 `murder-user.yml`）
2. **扩展配置**（`mysql-config.yml`、`redis-config.yml`）
3. **共享配置**（`common-config.yml`）
4. **本地配置**（`application.yml`）

---

## 🔒 敏感信息处理

⚠️ **注意**：当前配置文件中的敏感信息（数据库密码、JWT 密钥）为示例值。

**生产环境建议**：
1. 使用 Nacos 的加密配置功能
2. 或使用环境变量替换敏感配置
3. 或集成配置加密插件（如 Jasypt）

**示例**（使用环境变量）：
```yaml
spring:
  datasource:
    username: ${MYSQL_USERNAME:root}
    password: ${MYSQL_PASSWORD:root}
```

---

## 📝 修改配置

### 修改公共配置
修改 `common-config.yml` 会影响所有服务。

### 修改单个服务配置
只需修改对应的服务专属配置文件（如 `murder-user.yml`）。

### 配置生效
- **普通配置**: 需要重启服务
- **动态刷新**: 在代码中使用 `@RefreshScope` 注解的配置可实时生效

---

## ✅ 验证配置

导入后，可以通过以下方式验证：

1. **Nacos 控制台查看**:
   - 配置管理 → 配置列表，查看是否有 7 个配置

2. **监听查询**:
   - 配置管理 → 监听查询，输入 Data ID，查看哪些服务在监听

3. **启动服务验证**:
   - 启动任一服务，查看日志中是否有 "Located property source"

---

## 🎯 最佳实践

### ✅ 推荐做法
- 公共配置集中管理（common-config.yml）
- 基础设施配置分离（mysql-config.yml、redis-config.yml）
- 服务专属配置独立（murder-*.yml）
- 敏感信息使用加密或环境变量

### ❌ 不推荐做法
- 所有配置都放在一个文件中
- 硬编码敏感信息
- 配置文件无注释
- 不区分环境（dev、test、prod）

---

## 📞 技术支持

如有问题，请检查：
1. Nacos 服务是否正常运行（http://localhost:8848/nacos）
2. 配置文件格式是否正确（YAML 语法、缩进）
3. Data ID 和 Group 是否与 bootstrap.yml 中配置一致
4. 服务启动日志中是否有错误信息

---

## 🔗 相关文档

- [快速启用指南](../快速启用Nacos配置中心.md)
- [详细说明文档](../Nacos配置中心启用说明.md)
- [Nacos 官方文档](https://nacos.io/zh-cn/docs/what-is-nacos.html)

---

**最后更新**: 2024
**维护者**: Murder System Team
