# ERSS Backend

校园二手书交易后端，服务当前 `erss` uni-app 前端。

## 技术栈

- Java 17
- Spring Boot 3.5.x
- Sa-Token 1.45.x
- MyBatis-Plus 3.5.x
- MySQL 8
- Redis
- springdoc-openapi

## 本地启动

先准备数据库，直接执行 `database/init.sql`，后续增量脚本放在 `database/migrations/`：

```bash
mysql -h 103.212.49.129 -u root -p < database/init.sql
```

启动前设置环境变量。密码不要写进仓库：

```bash
export DB_HOST=103.212.49.129
export DB_PORT=3306
export DB_NAME=erss
export DB_USERNAME=root
export DB_PASSWORD='your-db-password'
export REDIS_HOST=103.212.49.129
export REDIS_PORT=6379
export REDIS_PASSWORD='your-redis-password'

mvn spring-boot:run
```

API 文档：

```text
http://localhost:8080/swagger-ui.html
```

## 前端对接

前端 `.env` / `.env.dev` 建议调整：

```env
VITE_API_MOCK=0
VITE_API_ORIGIN=http://localhost:8080
VITE_API_PATH=/api
VITE_API_TOKEN_KEY=Authorization
```

Sa-Token 已配置兼容 `Authorization: Bearer <token>`。
