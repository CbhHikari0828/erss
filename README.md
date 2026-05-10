# ERSS — 校园二手书交易平台

一个完整的校园二手书交易系统，包含移动端、管理后台和后端服务。

## 项目结构

```
erss/               # 移动端 (uniapp + Vue3)
vue-vben-admin/     # 管理后台 (Vben Admin + Ant Design Vue)
erss-backend/       # 后端服务 (Spring Boot 3)
```

## 技术栈

| 模块 | 技术 |
|------|------|
| 移动端 | uni-app, Vue 3, Pinia, Alova, Unocss, wot-design-uni |
| 管理后台 | Vue 3, Vite, Ant Design Vue, TypeScript, Turbo |
| 后端 | Java 17, Spring Boot 3.5, Sa-Token, MyBatis-Plus, MySQL 8, Redis |

## 快速开始

### 环境要求

- Node.js >= 20
- pnpm >= 9
- Java 17
- Maven 3.8+
- MySQL 8
- Redis

### 后端

```bash
cd erss-backend

# 初始化数据库
mysql -u root -p < database/init.sql

# 设置环境变量
export DB_HOST=your-db-host
export DB_PORT=3306
export DB_NAME=erss
export DB_USERNAME=root
export DB_PASSWORD=your-password
export REDIS_HOST=your-redis-host
export REDIS_PORT=6379
export REDIS_PASSWORD=your-redis-password

# 启动
mvn spring-boot:run
```

API 文档：http://localhost:8080/swagger-ui.html

### 移动端

```bash
cd erss
pnpm install

# H5 开发
pnpm dev:h5

# 微信小程序
pnpm dev:mp-weixin
```

### 管理后台

```bash
cd vue-vben-admin
pnpm install

# 启动开发服务器
pnpm dev:antd
```

## 功能模块

- 图书分类与搜索
- 二手书发布与交易
- 图书租赁
- 购物车与结算
- 订单管理
- 钱包与提现
- 个人店铺
- 消息通知
- 管理后台（仪表盘、财务、人员、损坏审核）

## License

MIT

## 作者

Alex <alexlee0828cbh@gmail.com>
