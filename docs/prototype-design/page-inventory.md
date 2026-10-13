# 系统页面清单

版本：v0.1（原型设计准备稿）  
整理日期：2026-10-08  
依据：BLOG-REQ-BL-001、SRS v1.0.0、当前Vue源代码  
代码检查点：`d6ceda8a36b39cf76a41c8689d12185f05ed87d0`；需求标签：`requirements-v1.0.0`

## 1. 编制说明与统计口径

本文件在网站已有实现之后补充，用于回顾页面结构并准备原型设计，不证明原型先于代码完成。结论来自源代码静态检查，未运行浏览器或功能测试。“已有”表示存在代码和路由入口，不等于已验收或完全符合基线。

以[Vue路由](../../blog-web/src/router/index.js)接入的末级页面组件为统计单位：共15个页面，包括404状态页面。动态路径中的不同文章ID不重复计数；两个布局父路由不计为页面。当前路由配置含15个末级记录和2个布局父记录。页面编号`PAGE-001`至`PAGE-015`仅是本组原型文档的索引，不是新增需求编号。

分类统计：公开内容页面5个，账号及个人中心页面3个（登录/注册、找回密码、个人主页），后台页面6个，通用404状态页面1个。按布局统计，PublicLayout下7个末级页面，独立账号页面2个，AdminLayout下6个末级页面。两种统计口径合计均为15。

## 2. 真实路由页面清单

页面组件路径均相对于项目根目录，链接指向真实源文件。权限列区分路由限制与实际操作权限。

| 编号 | 页面名称 | 路由路径 | 页面组件 | 访问角色 | 主要功能 | 当前状态 |
| --- | --- | --- | --- | --- | --- | --- |
| PAGE-001 | 首页 | `/` | [Home.vue](../../blog-web/src/views/public/Home.vue) | 游客、USER、ADMIN | 首页简介、最新5篇、热门3篇、文章及作者入口 | 已接入，使用公开查询 |
| PAGE-002 | 文章列表（页面标题“所有文章”） | `/articles` | [ArticleList.vue](../../blog-web/src/views/public/ArticleList.vue) | 全部角色 | 标题关键词、分类、标签、每页8条 | 已有组合筛选；同组件历史查询参数回填有静态缺口 |
| PAGE-003 | 文章详情 | `/articles/:id` | [ArticleDetail.vue](../../blog-web/src/views/public/ArticleDetail.vue) | 公开阅读；互动需登录 | Markdown、文章信息、相邻导航、点赞、评论 | 已有；登录附加请求401可能打断阅读 |
| PAGE-004 | 关于我 | `/about` | [About.vue](../../blog-web/src/views/public/About.vue) | 全部角色 | 作者介绍、公开联系邮箱及mailto链接 | 已接入，含本地默认介绍 |
| PAGE-005 | 留言板 | `/message` | [Message.vue](../../blog-web/src/views/public/Message.vue) | 公开阅读；提交需登录 | 已审核留言、每页10条、待审提交提示 | 已有；不是即时公开留言 |
| PAGE-006 | 用户登录与注册 | `/login` | [Login.vue](../../blog-web/src/views/admin/Login.vue) | 未登录入口 | 登录、注册页签、忘记密码、登录后返回目标地址 | 已有；虽在admin目录，并非管理员专属页 |
| PAGE-007 | 找回密码 | `/forgot-password` | [ForgotPassword.vue](../../blog-web/src/views/public/ForgotPassword.vue) | 未登录入口 | 邮箱、六位验证码、60秒倒计时、新密码确认 | 已有；长度提示与目标口径尚需同步 |
| PAGE-008 | 个人主页（个人中心） | `/profile` | [Profile.vue](../../blog-web/src/views/public/Profile.vue) | 已登录USER、ADMIN | 改名、头像预览/上传、改密、喜欢列表、退出 | 已有；不存在头像URL输入框 |
| PAGE-009 | 页面不存在 | `/:pathMatch(.*)*` | [NotFound.vue](../../blog-web/src/views/NotFound.vue) | 全部角色 | 404说明、返回首页 | 已有通配状态页；没有独立`/404`路由 |
| PAGE-010 | 数据概览 | `/admin` | [AdminDashboard.vue](../../blog-web/src/views/admin/AdminDashboard.vue) | ADMIN | 统计卡片、发布趋势、发布比例、文章入口 | 已有；失败时回退0和旧提示与基线有差异 |
| PAGE-011 | 文章管理 | `/admin/articles` | [ArticleManage.vue](../../blog-web/src/views/admin/ArticleManage.vue) | ADMIN | 搜索/状态/分页、新建编辑抽屉、草稿/发布、置顶、删除 | 已有；停用关联选择及历史编辑需改进 |
| PAGE-012 | 用户管理 | `/admin/users` | [UserManage.vue](../../blog-web/src/views/admin/UserManage.vue) | ADMIN | 搜索/分页、角色调整、启用/禁用、确认提示 | 已有；自角色/自禁用按钮禁用，旧Token目标需后端配合 |
| PAGE-013 | 评论管理 | `/admin/comments` | [CommentManage.vue](../../blog-web/src/views/admin/CommentManage.vue) | ADMIN | 三状态统计/过滤、搜索、分页、当前页多选、单条/批量审核 | 已有；无“全部评论”统计卡片；文章链接新窗口打开 |
| PAGE-014 | 留言管理 | `/admin/messages` | [MessageManage.vue](../../blog-web/src/views/admin/MessageManage.vue) | ADMIN | 三状态统计/过滤、搜索、分页、单条/批量审核 | 已有；审核后刷新及清空选择 |
| PAGE-015 | 系统管理 | `/admin/settings` | [SystemManage.vue](../../blog-web/src/views/admin/SystemManage.vue) | ADMIN | 分类、标签、站点配置三页签及维护弹窗 | 已有；五配置字段已呈现，但校验/白名单/事务尚有差异 |

## 3. 非独立页面与共享结构

| 类型 | 实际位置 | 作用及统计处理 |
| --- | --- | --- |
| 前台布局 | [PublicLayout.vue](../../blog-web/src/layouts/PublicLayout.vue) | 品牌、首页/文章/留言/关于导航、登录或个人头像、管理员入口、页脚；作为父路由容器，不计独立页 |
| 后台布局 | [AdminLayout.vue](../../blog-web/src/layouts/AdminLayout.vue) | 6个菜单、折叠侧栏、当前页标题、返回博客及退出下拉；不计独立页 |
| 文章卡片 | [ArticleCard.vue](../../blog-web/src/components/ArticleCard.vue) | 首页、列表、喜欢列表复用；封面和标题链接详情，分类/标签文字不是独立导航页 |
| 登录/注册页签 | PAGE-006 | 同一路由内切换；注册成功切回登录，并填用户名、清密码 |
| 编辑抽屉 | PAGE-011 | 新建与编辑共用，正文为Markdown文本输入；无独立编辑页、无封面文件上传入口 |
| 分类/标签弹窗 | PAGE-015 | 在对应页签新增/编辑；删除另有确认弹窗；不计独立页 |
| 头像选择与预览 | PAGE-008 | 本地文件输入、预览、保存或取消；不计独立页 |
| 加载/空/失败/确认状态 | 各页面内部 | 是原型需要说明的交互状态，不按新页面计算 |

`src/views/`当前共18个Vue文件。以下3个没有被当前路由接入，不作为本期正式页面：`views/PlaceholderView.vue`、`views/public/Articles.vue`、`views/admin/Dashboard.vue`。不删除旧文件，也不因有旧文件就认定存在新路由。

## 4. 权限与导航事实

前台导航由PublicLayout生成；头像进入`/profile`，管理员另可进入`/admin`。文章卡片和相邻文章进入详情；未登录互动转`/login?redirect=原路径`。登录使用同一个入口，成功后优先采用合法redirect，否则ADMIN进入后台，USER进入首页。`guestOnly`使已认证状态的账号不继续停留登录/找回页。

后台菜单对应6个真实路径。系统管理的页签不会形成新的分类/标签路由。评论列表所属文章使用`target="_blank"`打开详情。个人中心退出返回`/`；后台退出当前返回`/login`，不应将两个现有行为描述为相同。

路由守卫有登录和ADMIN限制，但缓存恢复不总会查询服务器；后端仍独立判断当前用户身份与角色。因此不能把菜单隐藏、缓存角色或页面可进入当作最终授权证据。关联代码：[认证Store](../../blog-web/src/store/auth.js)、[请求拦截器](../../blog-web/src/utils/request.js)、[SecurityConfig](../../blog-server/src/main/java/com/example/blog/security/SecurityConfig.java)。

## 5. 与后续原型范围的关系

本期覆盖上述15个页面及必要局部状态，不拆出独立注册、分类文章、标签文章或头像上传页面。BLOG-NF-012的390px窄屏专项增强与专项验收为后续候选；已有响应式样式保留。当前没有绘制、截屏或评审原型。

配套文件：[页面与需求对应关系](page-requirement-mapping.md)、[原型设计计划](prototype-design-plan.md)。正式依据：[SRS](../requirements/software-requirements-specification.md)、[基线登记](../requirements/requirements-baseline-register.md)。
