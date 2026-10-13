# 角色导航与访问权限矩阵

版本：v0.1（14.2权限导航设计稿，待用户检查）  
日期：2026-10-08  
依据：BLOG-REQ-BL-001、SRS v1.0.0

## 1. 依据与判断层次

参照[页面清单](page-inventory.md)、[SRS](../requirements/software-requirements-specification.md)、[角色流程](../requirements/roles-and-business-flows.md)及[追踪矩阵](../requirements/requirements-traceability-matrix.md)。源码：[路由](../../blog-web/src/router/index.js)、[PublicLayout](../../blog-web/src/layouts/PublicLayout.vue)、[AdminLayout](../../blog-web/src/layouts/AdminLayout.vue)、[Pinia](../../blog-web/src/store/auth.js)、[Axios](../../blog-web/src/utils/request.js)、[SecurityConfig](../../blog-server/src/main/java/com/example/blog/security/SecurityConfig.java)、[JwtFilter](../../blog-server/src/main/java/com/example/blog/security/JwtFilter.java)、[UserMapper](../../blog-server/src/main/java/com/example/blog/mapper/UserMapper.java)。

游客不是数据库角色；USER、ADMIN须为ACTIVE、未删除且认证有效。禁用是状态，不是第四种角色。菜单可见性、前端路由可进入、服务器允许操作是三个独立层次，隐藏菜单不能阻止伪造API请求。以下服务器要求针对数据/API，不代表Spring Security逐个保护Vue页面HTML。

## 2. 15页面访问矩阵

“允许”仅指该角色在满足数据条件后可用；公开详情仍只允许有效已发布文章。无身份保护请求401，有效USER后台请求403。前端拒绝可跳登录/首页，不必另设401或403路由。

| 页面编号、名称及路径 | 游客 | 有效USER | 有效ADMIN | 导航可见性 | 路由守卫现状 | 服务端要求 |
| --- | --- | --- | --- | --- | --- | --- |
| PAGE-001 首页 `/` | 允许 | 允许 | 允许 | 公共品牌/首页 | 无保护meta | 公开文章、配置API匿名 |
| PAGE-002 列表 `/articles` | 允许 | 允许 | 允许 | 公共“文章”、首页入口 | 无保护meta | 公开文章/分类/标签匿名 |
| PAGE-003 详情 `/articles/:id` | 可读，不能互动 | 可读可互动 | 可读可互动 | 卡片/相邻文章/喜欢列表/审核文章链接 | 页公开，按钮检查本地状态 | 详情/评论公开；点赞状态、点赞/取消、评论写入authenticated |
| PAGE-004 关于我 `/about` | 允许 | 允许 | 允许 | 公共“关于”、首页认识作者 | 无保护meta | 公开配置匿名；不开放任意敏感配置目标 |
| PAGE-005 留言 `/message` | 可读，不能提交 | 可读可提交 | 可读可提交 | 公共“留言” | 页公开，提交检查本地状态 | 公开留言匿名；提交authenticated |
| PAGE-006 登录/注册 `/login` | 允许 | guestOnly转首页 | guestOnly转后台 | 游客公共入口 | guestOnly依缓存认证状态 | 注册/登录接口permitAll，不与guestOnly混同 |
| PAGE-007 找回 `/forgot-password` | 允许入口 | guestOnly转首页 | guestOnly转后台 | 登录页忘记密码 | guestOnly依缓存认证状态 | reset接口permitAll；实际业务拒绝不存在/禁用账号等 |
| PAGE-008 个人主页 `/profile` | 登录并保留redirect | 仅本人 | 仅本人 | 已登录头像；个人中心仅ADMIN有后台按钮 | requiresAuth，当前缓存可放行 | 本人信息、改名、头像、改密、喜欢列表authenticated，ID取principal |
| PAGE-009 404 `/:pathMatch(.*)*` | 允许 | 允许 | 允许 | 未命中路由进入；返回首页 | 无保护meta | 无独立404业务API；公共布局可能读取公开配置 |
| PAGE-010 概览 `/admin` | 登录redirect | 路由回首页 | 允许 | 仅ADMIN公共后台入口、后台概览菜单 | requiresAuth及roles ADMIN | `/api/admin/**` hasRole ADMIN |
| PAGE-011 文章 `/admin/articles` | 登录redirect | 路由回首页 | 允许 | 后台文章菜单/概览文章入口 | 同上 | ADMIN |
| PAGE-012 用户 `/admin/users` | 登录redirect | 路由回首页 | 允许，禁止改自身角色/禁用自身 | 后台用户菜单 | 同上 | ADMIN及服务业务自保护 |
| PAGE-013 评论 `/admin/comments` | 登录redirect | 路由回首页 | 允许 | 后台评论菜单 | 同上 | ADMIN，审核参数/事务另校验 |
| PAGE-014 留言 `/admin/messages` | 登录redirect | 路由回首页 | 允许 | 后台留言菜单 | 同上 | ADMIN，审核参数/事务另校验 |
| PAGE-015 系统 `/admin/settings` | 登录redirect | 路由回首页 | 允许 | 后台系统菜单；内部三页签 | 同上 | ADMIN写；五业务键公开读取另走公开接口 |

后台布局内部六菜单没有逐项角色v-if，依赖整体后台路由保护；不能因此称每个菜单独立隐藏。服务端每个后台请求仍要求ADMIN。未命中的`/admin/未知路径`按当前全局通配进入公开404，不是已有受保护后台子页，也不开放任何ADMIN数据。

## 3. 角色与可见入口

| 身份 | 公共顶部 | 个人中心 | 后台 |
| --- | --- | --- | --- |
| 游客 | 品牌、首页、文章、留言、关于、登录/注册 | 无头像入口，直达保护路径转登录 | 无公共后台入口，直达真实后台页转登录 |
| USER | 保留公共入口，登录/注册换为本人头像 | 可改本人资料、头像、密码、查看喜欢、退出 | 不显示公共后台入口，直达后台页面回首页，API403 |
| ADMIN | 保留公共入口，头像及管理后台入口 | 同USER，另有进入后台按钮 | 六菜单、头像下拉返回博客/退出 |

这些现有显示条件来自auth.isAuthenticated（Boolean(token)）及auth.isAdmin（缓存user.role）。目标按BLOG-F-003/005和BLOG-NF-002：进入保护路由核实身份，权限变化后纠正显示，不能将残留缓存视为有效登录。后台降权可能暂时仍显示侧栏，但服务端拒绝新后台请求；纠正UI是待开发事项。

## 4. Token、禁用和角色变化目标

| 情形 | 已批准目标 | 当前实现与差异 | 需求关联 |
| --- | --- | --- | --- |
| 缺Token访问保护页 | 登录并保存目标路径；不执行保护操作 | router已有redirect；API401 | BLOG-F-005 |
| 过期/篡改Token | 保护请求401，清失效状态并引导登录 | JwtFilter校验签名/时效/版本；Axios清存储并assign登录 | BLOG-NF-002、BLOG-BR-004 |
| 公开阅读带过期缓存 | 阅读保持；附带自动身份/like查询失败降级不强制登录 | public API不附Token且过滤器跳过；但自动likeState是保护请求，现有全局401仍跳登录 | BLOG-F-003、BLOG-F-005、BLOG-BR-004 |
| 有效USER请求后台 | 403，前端不允许进入；不把403当普通退出 | 路由回首页，后端403；Axios不清Token，但未统一刷新角色/菜单 | BLOG-F-005、BLOG-NF-014 |
| 用户禁用 | 下一次鉴权拒绝旧Token；历史通过内容及点赞关系保留 | selectActiveById排除禁用；启停仅改status，不递增版本 | BLOG-F-035、BLOG-BR-004、BLOG-BR-005 |
| 重新启用 | 禁用前旧Token仍无效，必须重新登录 | 当前只切回ACTIVE，旧未过期且版本未变Token可能恢复；目标未完整实现 | BLOG-F-035、BLOG-BR-004 |
| 角色变更提交后 | 下一次鉴权按数据库当前角色；客户端保护导航核实，403纠正菜单 | JwtFilter已查当前role而非仅JWT声明；缓存恢复/菜单纠正不完整 | BLOG-F-003、BLOG-F-034、BLOG-NF-002 |
| 两种改密成功 | 旧Token失效，新登录获得有效Token | 登录改密递增token_version；邮箱重置只改password，版本撤销有缺口 | BLOG-F-007、BLOG-F-022、BLOG-NF-006、BLOG-BR-009 |
| 本地退出 | 清本浏览器状态；个人中心首页、后台登录 | 已有两种不同跳转；无服务器主动注销，本期不增加 | BLOG-F-004 |

状态/角色变更生效边界为提交后开始的下一次鉴权，不要求跨设备实时推送或撤销已完成鉴权的在途请求。禁用用户仍能以游客阅读公开内容，不能将禁用解释为所有公开页面封锁。

服务器读取上传头像资源`/uploads/avatars/**`是匿名；上传本人头像接口仍须登录。客户端不能传任意用户ID代替principal。导航恢复/403处理具体实现待设计；本轮不实施刷新、撤销或改接口。

## 5. 统一保护与反馈约定

1. 未登录、失效身份、权限不足和服务器失败须区分；不得把所有失败归为登录问题。
2. 主动保护操作继续鉴权；公开页附带读取失败保持正文，不能将点赞/评论接口改成公开来规避401。
3. 用户名/密码返回等与敏感信息保护遵守原基线；公开配置只允许五业务键，不能把permitAll当作公开所有配置许可。
4. 角色显示及保护导航应按核实结果纠正。具体缓存刷新或请求策略是设计问题，不在本轮宣称已修复。
5. 加载、空、失败以及真实统计0的界面规则见[布局说明](layout-design.md)，不得将页面可进入、HTTP成功或菜单显示当作业务验收。

## 6. 核查结论与边界

15页面编号、路径、角色与14.1保持一致；每个页面权限必须结合操作/API判断，不把公开详情的互动权限混同为整页保护。没有新增权限状态路由，没有将BLOG-NF-012窄屏专项纳入本期。静态结论未通过浏览器/接口或数据库实验验证，不填写测试成功或原型审批。

代码差异证据：[用户启停服务](../../blog-server/src/main/java/com/example/blog/service/impl/AdminUserServiceImpl.java)、[登录改密服务](../../blog-server/src/main/java/com/example/blog/service/impl/UserServiceImpl.java)、[邮件重置服务](../../blog-server/src/main/java/com/example/blog/service/impl/PasswordResetServiceImpl.java)、[文章详情](../../blog-web/src/views/public/ArticleDetail.vue)。
