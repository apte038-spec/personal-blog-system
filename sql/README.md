# 数据库脚本说明

本目录用于统一管理面向课程提交、数据库初始化和版本迁移的 SQL 脚本。

为避免影响当前项目运行及形成两套不同步的脚本，本次整理没有移动或复制现有 SQL 文件。当前权威脚本仍位于：

- `../blog-server/src/main/resources/schema.sql`：数据库结构及初始化数据。
- `../blog-server/src/main/resources/migration-v2.sql`：第二阶段数据库迁移脚本。
- `../blog-server/src/main/resources/migration-v3-email-reset.sql`：邮箱验证码重置密码功能迁移脚本。

后续如需将脚本集中到本目录，应先同步调整项目说明和所有引用位置。
