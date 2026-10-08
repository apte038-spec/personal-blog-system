# 个人博客系统（Personal Blog System）

一个面向课程设计的前后端分离个人博客系统，系统形式为 PC Web，由博客展示前台、注册用户功能和后台管理系统组成。项目用于完成文章发布与浏览、用户互动、内容审核、个人资料维护和站点管理等完整业务流程，可视为一个简化版 WordPress。

本项目已经完成主要功能开发，后续将继续补充需求分析、系统设计、测试、原型和软件开发全流程 Skill 等课程材料。

## 主要功能

### 博客前台

- 首页展示站点介绍、最新文章和热门文章。
- 已发布文章列表、关键词搜索、分类筛选、标签筛选和分页。
- 文章详情、Markdown正文展示、阅读量、上一篇和下一篇。
- 关于我、联系信息和留言板。
- 未知地址的404页面。

### 用户功能

- 普通用户注册、登录和JWT登录状态保持。
- 通过邮箱验证码重置密码。
- 文章点赞、取消点赞、发表评论和提交留言。
- 评论和留言提交后进入待审核状态。
- 个人中心查看资料和喜欢的文章。
- 修改用户名和密码。
- 从本地上传JPG、PNG或WebP头像，单张最大5 MB。
- 退出登录；禁用账号不能继续登录或使用旧Token访问受保护功能。

### 后台管理

- 文章、用户和评论等数据概览，以及近七日文章发布趋势图。
- 文章新增、编辑、发布、保存草稿和逻辑删除。
- 用户列表、角色调整以及账号启用和禁用。
- 评论和留言的分页查询、状态筛选、单条审核和批量审核。
- 文章分类和标签管理。
- 博客名称、副标题、首页简介、关于我和联系邮箱等站点配置。

## 系统角色

| 角色 | 主要权限 |
| --- | --- |
| 游客 | 浏览公开文章、关于我和已审核留言，注册、登录和重置密码 |
| 普通用户 `USER` | 在游客功能基础上点赞、评论、留言并维护个人资料 |
| 管理员 `ADMIN` | 在普通用户功能基础上进入后台管理文章、用户、审核内容和站点数据 |

## 技术栈

### 前端

- Vue 3.5
- Vite 5
- Vue Router 4
- Pinia 2
- Element Plus
- Axios
- ECharts
- marked、DOMPurify

### 后端

- Java 17
- Spring Boot 3.3.5
- Spring Security
- MyBatis Plus 3.5.8
- JJWT 0.12.6
- Spring Boot Mail
- Jakarta Validation
- Lombok
- Maven
- JUnit 5、Mockito

### 数据库

- MySQL 8
- InnoDB
- `utf8mb4`

数据库包含用户、邮箱验证码、分类、标签、文章、文章标签关联、评论、留言、点赞和站点配置等数据表。

## 项目目录

```text
codex_project/
├─ blog-web/                         # Vue 3前端项目
│  ├─ src/
│  │  ├─ api/                        # Axios接口封装
│  │  ├─ assets/                     # 全局样式
│  │  ├─ components/                 # 通用组件
│  │  ├─ layouts/                    # 前台和后台布局
│  │  ├─ router/                     # 页面路由与权限守卫
│  │  ├─ store/                      # Pinia登录状态
│  │  ├─ utils/                      # 请求和Token工具
│  │  └─ views/                      # 前台、个人中心和后台页面
│  ├─ package.json
│  └─ vite.config.js
├─ blog-server/                      # Spring Boot后端项目
│  ├─ src/main/java/com/example/blog/
│  │  ├─ common/                     # 统一响应与业务异常
│  │  ├─ config/                     # Web、MyBatis Plus、异常配置
│  │  ├─ controller/                 # REST接口层
│  │  ├─ dto/                        # 请求参数对象
│  │  ├─ entity/                     # 数据库实体
│  │  ├─ mapper/                     # MyBatis Plus Mapper
│  │  ├─ security/                   # JWT与Spring Security
│  │  ├─ service/                    # 业务服务
│  │  └─ vo/                         # 响应视图对象
│  ├─ src/main/resources/            # 配置、初始化和迁移SQL
│  ├─ src/test/                      # 后端自动化测试
│  └─ uploads/                       # 运行时上传目录，仅提交.gitkeep
├─ docs/                             # 需求、设计、开发、测试等过程文档
├─ prototype/                        # 页面原型、原型截图和说明
├─ skill/                            # 软件开发全流程Skill
├─ sql/                              # 数据库脚本索引与课程提交入口
├─ .env.example                      # 环境变量示例，不包含真实秘密
├─ .gitignore
└─ pom.xml                           # Maven聚合项目配置
```

## 环境要求

- JDK 17
- Maven 3.8或更高版本
- Node.js 18或更高版本
- npm
- MySQL 8
- IntelliJ IDEA（推荐用于运行后端）
- 现代桌面浏览器

默认开发地址：

- 前端：`http://localhost:5173`
- 后端：`http://localhost:8080`
- 数据库：`personal_blog`

## 数据库初始化

1. 启动MySQL 8。
2. 执行后端资源目录中的初始化脚本：

   ```text
   blog-server/src/main/resources/schema.sql
   ```

3. `schema.sql`用于创建新的完整数据库，并包含课程演示数据。
4. `migration-v2.sql`和`migration-v3-email-reset.sql`用于已有旧数据库的增量升级。新建数据库已经执行完整`schema.sql`时，不需要重复执行相同结构的迁移。
5. 顶层`sql/README.md`记录当前权威SQL脚本位置，避免维护两套不同步的脚本。

使用MySQL命令行初始化时，可在项目根目录执行：

```shell
mysql -u root -p < blog-server/src/main/resources/schema.sql
```

命令执行后由MySQL交互式询问密码，不要把数据库密码写入脚本或提交到仓库。

## 环境变量配置

可提交的配置模板位于根目录`.env.example`。该文件只用于说明变量名称，Spring Boot默认不会自动读取项目根目录的`.env`文件。

### 必须配置

| 环境变量 | 说明 |
| --- | --- |
| `DB_PASSWORD` | 本机MySQL密码 |
| `JWT_SECRET` | 本机JWT HMAC签名密钥，应使用足够长的随机值 |

### 常用可选配置

| 环境变量 | 说明 | 默认行为 |
| --- | --- | --- |
| `DB_URL` | MySQL JDBC连接地址 | 连接本机`personal_blog` |
| `DB_USERNAME` | MySQL用户名 | `root` |
| `SERVER_PORT` | 后端端口 | `8080` |
| `JWT_EXPIRATION_HOURS` | JWT有效小时数 | `24` |
| `AVATAR_UPLOAD_DIR` | 头像保存目录 | `uploads/avatars` |

### 邮件配置

只有需要实际使用“忘记密码”邮件验证码时，才需要设置：

- `SMTP_USERNAME`
- `SMTP_PASSWORD`
- `MAIL_FROM`
- `SMTP_HOST`
- `SMTP_PORT`
- `SMTP_SSL_ENABLE`
- `SMTP_STARTTLS_ENABLE`

其中`SMTP_PASSWORD`应填写邮箱服务商提供的SMTP授权码，而不是把邮箱登录密码写入项目。任何真实数据库密码、JWT Secret、邮箱账号或授权码都不得写入README、`application.yml`或`.env.example`。

## 使用IntelliJ IDEA启动后端

1. 使用IntelliJ IDEA打开项目根目录。
2. 等待Maven导入完成，并确认项目使用JDK 17。
3. 打开`Run | Edit Configurations...`。
4. 创建或选择Spring Boot运行配置，主类设置为：

   ```text
   com.example.blog.BlogApplication
   ```

5. 在`Environment variables`中至少设置：

   ```text
   DB_PASSWORD=YOUR_DB_PASSWORD
   JWT_SECRET=YOUR_JWT_SECRET
   ```

6. 如需测试真实邮件，再添加SMTP相关环境变量。
7. 运行`BlogApplication`。
8. 当控制台显示Tomcat已在8080端口启动，并且数据库连接池初始化成功时，说明后端启动完成。

也可以在已经设置环境变量的终端中从项目根目录启动：

```shell
mvn -pl blog-server spring-boot:run
```

## 启动前端

在项目根目录执行：

```shell
cd blog-web
npm install
npm run dev
```

启动后访问：

```text
http://localhost:5173
```

Vite开发服务器会将`/api`和`/uploads`请求代理到本机8080端口的Spring Boot服务。

生产构建命令：

```shell
npm run build
```

## 本地课程演示账号

> 以下账号仅用于本地课程设计演示，不得用于生产环境。正式部署时必须修改默认账号和密码，也不得将其用于其他网站或真实业务系统。

| 类型 | 用户名 | 密码 |
| --- | --- | --- |
| 后台管理员 | `admin` | `password` |

演示账号密码与数据库连接密码、JWT Secret、SMTP授权码是不同类型的数据。真实运行凭据只能通过本地环境变量提供。

## 项目文档

- `docs/`：需求分析、总体设计、数据库设计、接口设计、详细设计、测试、部署和验收等软件开发过程文档。目前已包含系统用例图材料。
- `prototype/`：页面原型、原型截图、页面流程及相关说明；当前保留目录说明，后续按实际原型补充。
- `skill/`：课程要求的“软件开发全流程 Skill”；当前尚未编写正式Skill，不预先放置虚构内容。
- `sql/`：数据库脚本的课程提交入口和索引。当前权威脚本仍位于后端资源目录。

## Git版本管理约定

- 主分支使用`main`。
- 不伪造项目过去的提交历史，从当前真实状态开始管理版本。
- 每完成一个有明确意义的阶段再提交一次，不为每个小文件单独提交，也不把所有后续工作堆积到一次提交中。
- 不提交`node_modules`、`target`、`dist`、日志、IDE配置、用户上传文件和本地缓存。
- 不提交`.env`、数据库密码、JWT Secret、SMTP授权码、Token、API Key、私钥或证书。
- `.env.example`只能包含变量名、无敏感性的示例值或明显占位符。

建议的后续提交信息示例：

```text
docs: add software requirements specification
docs: add prototype design documentation
docs: add system design documentation
docs: add testing documentation
feat: add software development workflow skill
fix: resolve specific issue
docs: complete project delivery documentation
```

首次提交计划使用：

```text
chore: initialize personal blog system repository
```

## 安全说明

仓库中的`application.yml`不包含数据库密码或固定JWT密钥。启动后端前必须在本地配置相应环境变量。用户上传头像、运行日志、构建产物和本地开发环境文件已通过`.gitignore`排除。
