# Database Scripts

直接执行一个脚本即可：

```bash
mysql -h 103.212.49.129 -u root -p < database/init.sql
```

`init.sql` 会创建 `erss` 数据库、表结构和默认种子数据。

后续数据库改动请放在 `database/migrations/`，按文件名顺序执行增量脚本。

默认种子账号：

```text
学生号：20260001
密码：123456
角色：seller
```

不要把真实数据库密码写入脚本或提交到仓库。
