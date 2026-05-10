# Database Migrations

把后续数据库变更放在这里，每次只提交增量脚本，例如：

- `001_add_book_status.sql`
- `002_add_notification_index.sql`

新的开发机只需要先执行 `database/init.sql`，再按时间顺序执行 `database/migrations/*.sql`。
