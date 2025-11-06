# 🎭 Murder 剧本杀预约与门店管理系统

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.3-blue.svg)](https://spring.io/projects/spring-cloud)
[![Nacos](https://img.shields.io/badge/Nacos-2.3.2-orange.svg)](https://nacos.io/)
[![Vue](https://img.shields.io/badge/Vue-3.x-green.svg)](https://vuejs.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> 基于 Spring Cloud 微服务架构的剧本杀预约与门店管理系统，提供用户端和管理端双端应用，支持在线预约、门店管理、剧本管理、订单支付、会员系统等完整业务功能。

---

## 📑 目录

- [项目简介](#项目简介)
- [技术架构](#技术架构)
- [功能特性](#功能特性)
- [系统架构](#系统架构)
- [快速开始](#快速开始)
- [项目结构](#项目结构)
- [接口文档](#接口文档)
- [配置说明](#配置说明)
- [部署指南](#部署指南)
- [常见问题](#常见问题)
- [开发团队](#开发团队)
- [License](#license)

---

## 📖 项目简介

Murder 是一个基于 Spring Cloud 微服务架构开发的剧本杀预约与门店管理系统。系统采用前后端分离架构，后端使用 Spring Cloud 微服务技术栈，前端采用 Vue 3 框架，实现了完整的剧本杀预约业务流程。

### 🎯 适用场景

- 剧本杀门店预约管理
- 剧本信息管理与推荐
- 门店运营数据统计
- 会员积分与优惠券管理
- 订单支付与退款处理

---

## 🛠️ 技术架构

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.5 | 基础框架 |
| Spring Cloud | 2023.0.3 | 微服务框架 |
| Spring Cloud Alibaba | 2023.0.1.2 | 阿里巴巴微服务组件 |
| Nacos | 2.3.2 | 服务注册与配置中心 |
| Spring Cloud Gateway | 4.1.4 | API 网关 |
| OpenFeign | 4.1.3 | 服务间调用 |
| MyBatis-Plus | 3.5.3 | ORM 框架 |
| MySQL | 8.0+ | 数据库 |
| Redis | 6.0+ | 缓存 |
| Druid | 1.2.20 | 数据库连接池 |
| JWT | 0.12.5 | 认证授权 |
| Knife4j | 4.4.0 | API 文档 |
| WebSocket | - | 实时通知 |

### 前端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.x | 前端框架 |
| Element Plus | - | UI 组件库 |
| Vite | - | 构建工具 |
| Axios | - | HTTP 客户端 |
| Vue Router | - | 路由管理 |

---

## ✨ 功能特性

### 🔐 用户端功能

#### 用户管理
- ✅ 用户注册、登录（支持滑块验证码）
- ✅ 个人信息管理
- ✅ 收货地址管理
- ✅ 积分查询与兑换
- ✅ VIP 会员购买与管理

#### 剧本浏览
- ✅ 剧本分类浏览
- ✅ 剧本搜索与筛选
- ✅ 剧本详情查看
- ✅ 剧本收藏与评价
- ✅ 推荐剧本展示

#### 预约服务
- ✅ 在线预约创建
- ✅ 时间段选择
- ✅ 优惠券使用
- ✅ 在线支付（支付宝）
- ✅ 订单管理与退款
- ✅ 预约评价

#### 门店服务
- ✅ 门店列表查看
- ✅ 门店详情查看
- ✅ 门店评价
- ✅ 门店收藏

#### 优惠与活动
- ✅ 优惠券领取与使用
- ✅ 积分奖励
- ✅ VIP 会员折扣
- ✅ 活动推荐

#### 消息通知
- ✅ 系统消息推送
- ✅ 预约状态通知
- ✅ 优惠券到账通知
- ✅ WebSocket 实时通知

#### 社区功能
- ✅ 文章浏览
- ✅ 文章评论
- ✅ 文章点赞与收藏
- ✅ 攻略分享

---

### 🔧 管理端功能

#### 用户管理
- ✅ 用户列表查看
- ✅ 用户详情管理
- ✅ 用户状态管理
- ✅ 积分记录查询

#### 门店管理
- ✅ 门店信息管理
- ✅ 门店审核
- ✅ 房间管理
- ✅ 员工管理
- ✅ 营业数据统计

#### 剧本管理
- ✅ 剧本信息管理
- ✅ 剧本审核
- ✅ 剧本分类管理
- ✅ 角色信息管理

#### 预约管理
- ✅ 预约订单查看
- ✅ 预约状态管理
- ✅ 退款处理
- ✅ 订单统计

#### 优惠券管理
- ✅ 优惠券创建
- ✅ 优惠券发放
- ✅ 使用记录查询

#### VIP 管理
- ✅ VIP 套餐管理
- ✅ VIP 订单查询
- ✅ VIP 权益配置

#### 数据统计
- ✅ 实时数据概览
- ✅ 营收统计
- ✅ 订单统计
- ✅ 用户增长统计
- ✅ 热门剧本排行

#### 系统管理
- ✅ 消息推送
- ✅ 系统通知管理

---

## 🏗️ 系统架构

### 微服务架构图

```
┌─────────────────────────────────────────────────────────────┐
│                          用户端 / 管理端                       │
│                     (Vue 3 + Element Plus)                   │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ↓
┌─────────────────────────────────────────────────────────────┐
│                    API Gateway (8081)                        │
│              Spring Cloud Gateway + 限流                      │
└────┬──────────┬──────────┬──────────┬──────────┬───────────┘
     │          │          │          │          │
     ↓          ↓          ↓          ↓          ↓
┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐
│  User   │ │  Store  │ │ Script  │ │Reservation│ │  Auth   │
│ Service │ │ Service │ │ Service │ │ Service  │ │ Service │
│ (8082)  │ │ (8083)  │ │ (8084)  │ │ (8085)   │ │ (8086)  │
└────┬────┘ └────┬────┘ └────┬────┘ └────┬─────┘ └────┬────┘
     │           │           │           │            │
     └───────────┴───────────┴───────────┴────────────┘
                         │
                         ↓
         ┌───────────────────────────────────┐
         │  Nacos (8848)                     │
         │  - 服务注册与发现                  │
         │  - 配置中心                        │
         └───────────────────────────────────┘
                         │
         ┌───────────────┴───────────────┐
         ↓                               ↓
┌─────────────────┐            ┌─────────────────┐
│  MySQL (3306)   │            │  Redis (6379)   │
│  - 业务数据      │            │  - 缓存          │
│  - 用户信息      │            │  - Session      │
└─────────────────┘            └─────────────────┘
```

### 服务说明

| 服务名 | 端口 | 说明 |
|--------|------|------|
| murder-gateway | 8081 | API 网关，统一路由、限流、跨域处理 |
| murder-user | 8082 | 用户服务，用户管理、VIP、优惠券、积分、通知 |
| murder-store | 8083 | 门店服务，门店管理、房间管理、员工管理、统计 |
| murder-script | 8084 | 剧本服务，剧本管理、收藏、评价、角色 |
| murder-reservation | 8085 | 预约服务，预约创建、订单管理、支付、退款 |
| murder-auth | 8086 | 认证服务，用户登录、JWT 令牌管理 |
| nacos | 8848 | 服务注册中心、配置中心 |

---

## 🚀 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Node.js 16+
- Nacos 2.3.2+

### 安装步骤

#### 1. 克隆项目

```bash
git clone https://github.com/your-username/murder.git
cd murder
```

#### 2. 启动基础服务

**启动 MySQL**
```bash
# 创建数据库
CREATE DATABASE murder CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 导入数据
mysql -u root -p murder < sql/murder.sql
```

**启动 Redis**
```bash
redis-server
```

**启动 Nacos**
```bash
cd nacos/bin
# Windows
startup.cmd -m standalone
# Linux/Mac
sh startup.sh -m standalone

# 访问 Nacos 控制台: http://localhost:8848/nacos
# 用户名/密码: nacos/nacos
```

#### 3. 导入 Nacos 配置

**方式1：自动导入（推荐）**
```bash
# 双击运行
导入Nacos配置.bat
```

**方式2：手动导入**
1. 访问 Nacos 控制台：http://localhost:8848/nacos
2. 进入【配置管理】→【配置列表】
3. 依次导入 `nacos-config/` 目录下的 7 个配置文件：
   - common-config.yml
   - mysql-config.yml
   - redis-config.yml
   - murder-user.yml
   - murder-store.yml
   - murder-script.yml
   - murder-reservation.yml

#### 4. 启动后端服务

**使用 IDEA（推荐）**

依次运行以下 Application 类：
1. GatewayApplication (端口 8081)
2. UserApplication (端口 8082)
3. StoreApplication (端口 8083)
4. ScriptApplication (端口 8084)
5. ReservationApplication (端口 8085)

**使用 Maven**
```bash
# 启动网关
cd murder-gateway
mvn spring-boot:run

# 启动用户服务（新开终端）
cd murder-user
mvn spring-boot:run

# 启动门店服务（新开终端）
cd murder-store
mvn spring-boot:run

# 启动剧本服务（新开终端）
cd murder-script
mvn spring-boot:run

# 启动预约服务（新开终端）
cd murder-reservation
mvn spring-boot:run
```

#### 5. 启动前端

**用户端**
```bash
cd murder-user-frontend
npm install
npm run dev

# 访问: http://localhost:3000
```

**管理端**
```bash
cd murder-admin-frontend
npm install
npm run dev

# 访问: http://localhost:3001
```

---

## 📂 项目结构

```
murder/
├── murder-gateway/              # API 网关服务
├── murder-user/                 # 用户服务
├── murder-store/                # 门店服务
├── murder-script/               # 剧本服务
├── murder-reservation/          # 预约服务
├── murder-auth/                 # 认证服务
├── murder-common/               # 公共模块
│   ├── feign/                   # Feign 客户端
│   ├── exception/               # 统一异常
│   ├── handler/                 # 全局处理器
│   └── utils/                   # 工具类
├── murder-pojo/                 # 实体模块
│   ├── entity/                  # 实体类
│   ├── dto/                     # 数据传输对象
│   └── vo/                      # 视图对象
├── murder-user-frontend/        # 用户端前端
├── murder-admin-frontend/       # 管理端前端
├── nacos-config/                # Nacos 配置文件
├── sql/                         # 数据库脚本
├── docker-compose.yml           # Docker 编排文件
└── README.md                    # 项目说明
```

---

## 📚 接口文档

### Swagger API 文档

启动服务后，访问以下地址查看 API 文档：

- **用户服务**: http://localhost:8082/doc.html
- **门店服务**: http://localhost:8083/doc.html
- **剧本服务**: http://localhost:8084/doc.html
- **预约服务**: http://localhost:8085/doc.html

### API 访问方式

通过网关访问所有服务：

```
用户服务：http://localhost:8081/api/user/**
门店服务：http://localhost:8081/api/store/**
剧本服务：http://localhost:8081/api/script/**
预约服务：http://localhost:8081/api/reservation/**
```

---

## ⚙️ 配置说明

### Nacos 配置中心

项目使用 Nacos 作为配置中心，配置分为以下几层：

#### 公共配置（common-config.yml）
- MyBatis-Plus 配置
- JWT 配置
- Knife4j 配置
- 日志配置

#### 基础设施配置
- **mysql-config.yml**: MySQL 数据源配置
- **redis-config.yml**: Redis 连接配置

#### 服务专属配置
- **murder-user.yml**: 用户服务配置（端口、Redis DB）
- **murder-store.yml**: 门店服务配置
- **murder-script.yml**: 剧本服务配置（文件上传）
- **murder-reservation.yml**: 预约服务配置（支付宝）

### 配置优先级

```
Nacos 服务专属配置 (最高)
    ↓
Nacos 扩展配置 (mysql-config, redis-config)
    ↓
Nacos 公共配置 (common-config)
    ↓
本地 application.yml (兜底)
```

### Redis 数据库分配

| 服务 | Redis DB | 用途 |
|------|----------|------|
| Gateway | DB 2 | 限流计数器 |
| User | DB 0 | 用户缓存、Session |
| Store | DB 1 | 门店缓存 |
| Script | DB 3 | 剧本缓存 |
| Reservation | DB 4 | 预约缓存 |

---

## 🐳 部署指南

### Docker 部署

#### 1. 构建镜像

```bash
# 构建后端服务镜像
mvn clean package -DskipTests
docker build -t murder-gateway:latest ./murder-gateway
docker build -t murder-user:latest ./murder-user
docker build -t murder-store:latest ./murder-store
docker build -t murder-script:latest ./murder-script
docker build -t murder-reservation:latest ./murder-reservation

# 构建前端镜像
docker build -t murder-user-frontend:latest ./murder-user-frontend
docker build -t murder-admin-frontend:latest ./murder-admin-frontend
```

#### 2. 使用 Docker Compose 启动

```bash
docker-compose up -d
```

### 生产环境部署建议

#### 服务器配置推荐
- **CPU**: 4核+
- **内存**: 8GB+
- **磁盘**: 100GB+

#### 部署架构
```
Nginx (80/443)
    ↓
Gateway (8081) × N（负载均衡）
    ↓
业务服务 × N（水平扩展）
    ↓
MySQL 主从 + Redis 集群
```

#### 性能优化
- ✅ 使用 Nginx 反向代理
- ✅ 启用 Redis 集群
- ✅ MySQL 读写分离
- ✅ 配置 JVM 参数优化
- ✅ 开启 Druid 监控
- ✅ 配置日志分级

---

## ❓ 常见问题

### Q1: 服务启动失败，端口是 8080 而不是配置的端口？

**A**: 说明没有从 Nacos 加载配置。请检查：
1. Nacos 是否启动（http://localhost:8848/nacos）
2. Nacos 中是否已导入配置文件
3. `pom.xml` 中是否添加了 `spring-cloud-starter-bootstrap` 依赖
4. `bootstrap.yml` 配置是否正确

### Q2: 启动时报错 "Failed to configure a DataSource"？

**A**: 数据库配置未从 Nacos 加载。解决方法：
1. 确认 MySQL 已启动
2. 确认 Nacos 中 `mysql-config.yml` 已导入
3. 检查数据库连接信息是否正确
4. 确认数据库 `murder` 已创建

### Q3: 服务间调用失败？

**A**: 检查以下几点：
1. 所有服务是否都启动成功
2. 在 Nacos 控制台查看服务是否注册成功
3. OpenFeign 接口配置是否正确
4. 网络连接是否正常

### Q4: 支付功能无法使用？

**A**: 项目使用支付宝沙箱环境：
1. 需要在 Nacos 的 `murder-reservation.yml` 中配置支付宝密钥
2. 或设置 `alipay.mock-payment: true` 使用模拟支付

### Q5: WebSocket 连接失败？

**A**: 检查：
1. Gateway 中 WebSocket 路由配置
2. 前端 WebSocket 连接地址
3. 防火墙是否阻止连接

---

## 👥 开发团队

- **项目负责人**: [Your Name]
- **后端开发**: [Your Name]
- **前端开发**: [Your Name]
- **数据库设计**: [Your Name]

---

## 📄 License

本项目采用 [MIT](LICENSE) 许可证。

---

## 🔗 相关链接

- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [Spring Cloud 官方文档](https://spring.io/projects/spring-cloud)
- [Nacos 官方文档](https://nacos.io/zh-cn/docs/what-is-nacos.html)
- [MyBatis-Plus 官方文档](https://baomidou.com/)
- [Vue 3 官方文档](https://vuejs.org/)

---

## 📞 联系方式

如有问题或建议，欢迎联系：

- **邮箱**: your.email@example.com
- **GitHub**: https://github.com/your-username
- **博客**: https://your-blog.com

---

## ⭐ Star History

如果这个项目对你有帮助，欢迎 Star ⭐

---

**最后更新**: 2024年11月  
**版本**: v1.0.0
