# personal-blog-backend

Spring Boot 3 后端模块，使用 Java 17、Maven、MyBatis Plus、MySQL 8、JWT 与 Lombok。

## 目录

```text
src/main/
├─ java/com/example/blog/
│  ├─ common/      # Result、业务异常与公共类型
│  ├─ config/      # MyBatis Plus、跨域、全局异常等基础配置
│  ├─ controller/  # HTTP 接口层（后续按 public/admin 分组）
│  ├─ dto/         # 请求参数对象
│  ├─ entity/      # 数据库实体
│  ├─ mapper/      # MyBatis Plus Mapper
│  ├─ security/    # JWT 与 Spring Security 配置
│  ├─ service/     # 业务服务接口及实现
│  └─ vo/          # 响应视图对象
└─ resources/
   ├─ application.yml
   └─ mapper/      # 复杂 SQL 的 XML Mapper（需要时新增）
```

## 配置

`application.yml` 通过环境变量读取本地连接和安全配置。`DB_PASSWORD` 与 `JWT_SECRET` 没有默认值，启动后端前必须提供。项目根目录的 `.env.example` 仅用于说明变量名称，Spring Boot 不会默认自动读取 `.env` 文件。

| 环境变量 | 默认值 |
| --- | --- |
| `SERVER_PORT` | `8080` |
| `DB_URL` | 本机 `personal_blog` 数据库连接 |
| `DB_USERNAME` | `root` |
| `DB_PASSWORD` | 无，必须通过环境变量提供 |
| `JWT_SECRET` | 无，必须通过环境变量提供安全随机值 |
| `JWT_EXPIRATION_HOURS` | `24` |
| `SMTP_HOST` | `smtp.qq.com` |
| `SMTP_PORT` | `465` |
| `SMTP_USERNAME` | 无，需填写完整发件邮箱地址 |
| `SMTP_PASSWORD` | 无，需填写邮箱 SMTP 授权码，不是邮箱登录密码 |
| `MAIL_FROM` | 默认与 `SMTP_USERNAME` 相同 |
| `SMTP_SSL_ENABLE` | `true` |
| `SMTP_STARTTLS_ENABLE` | `false` |
| `AVATAR_UPLOAD_DIR` | `uploads/avatars` |

首次启用密码重置前，先执行 `src/main/resources/migration-v3-email-reset.sql` 创建邮箱验证码表。生产或演示环境请通过环境变量传入 SMTP 账号和授权码，不要把授权码提交到代码仓库。

数据库密码、JWT密钥、SMTP账号和SMTP授权码均不得直接写入 `application.yml`、README或其他准备提交的文件。SMTP账号和授权码未配置时，密码重置邮件无法实际发送，但其他不依赖邮件的功能不受影响。

## 认证接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST` | `/api/auth/register` | 注册普通用户 |
| `POST` | `/api/auth/login` | 用户名与密码登录，返回 Bearer Token |
| `POST` | `/api/auth/password/reset-code` | 向已注册且启用的邮箱发送六位重置验证码 |
| `POST` | `/api/auth/password/reset` | 校验验证码并设置 BCrypt 加密的新密码 |
| `GET` | `/api/auth/me` | 获取当前登录用户 |
| `GET` | `/api/auth/profile` | `/me` 的兼容别名 |
| `POST` | `/api/users/me/avatar` | 登录用户上传本地头像，multipart 字段名为 `file` |

受保护请求须携带 `Authorization: Bearer <accessToken>`。普通用户无法访问 `/api/admin/**`。

## 文章接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/api/public/articles` | 已发布文章分页；支持 `keyword`、`categoryId`、`tagId`、`page`、`size` |
| `GET` | `/api/public/articles/{id}` | 文章详情，并累加阅读量 |
| `GET` | `/api/admin/articles` | 管理端文章分页；支持 `status` 及公开查询参数 |
| `GET` | `/api/admin/articles/{id}` | 管理端文章详情 |
| `POST` | `/api/admin/articles` | 发布或保存文章，需管理员权限 |
| `PUT` | `/api/admin/articles/{id}` | 修改文章，需管理员权限 |
| `DELETE` | `/api/admin/articles/{id}` | 逻辑删除文章，需管理员权限 |

文章写入请求包含 `title`、`slug`、`content`、`categoryId`、`status` 和可选的 `summary`、`coverUrl`、`isTop`、`tagIds`。`status` 仅允许 `DRAFT` 或 `PUBLISHED`。
