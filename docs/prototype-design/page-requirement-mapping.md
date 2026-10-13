# 页面与需求对应关系

版本：v0.1（原型准备映射）  
日期：2026-10-08  
需求依据：BLOG-REQ-BL-001 / SRS v1.0.0 / requirements-v1.0.0

## 1. 映射原则

本文件将已批准需求映射到当前页面及拟设计交互，不调整需求编号或范围。关联表示“该页面应承载需求”，不表示现有代码已完全满足或已验收。现有网站早于本次原型整理完成，文档用于验证已有设计及明确目标差异。

全部编号核对[正式SRS](../requirements/software-requirements-specification.md)、[需求清单](../requirements/requirements-inventory.md)和[80项追踪矩阵](../requirements/requirements-traceability-matrix.md)。页面编号和真实路径见[系统页面清单](page-inventory.md)。原型重点以本期79项为边界，不要求每项拥有独立页面。

## 2. 页面—功能需求映射

| 页面编号及名称 | 关联功能需求编号 | 主要交互目标 | 角色权限 | 原型设计重点 |
| --- | --- | --- | --- | --- |
| PAGE-001 首页 | BLOG-F-008、BLOG-F-009、BLOG-F-040 | 简介、公共导航、最新5/热门3、点击文章 | 公开 | 介绍与文章层次、数量不足/空/加载状态；不增加独立置顶区 |
| PAGE-002 文章列表 | BLOG-F-010、BLOG-F-011 | 搜索、分类/标签、清除、分页、URL恢复 | 公开 | 控件、URL、结果同步；空结果与查询失败区分 |
| PAGE-003 文章详情 | BLOG-F-005、BLOG-F-012、BLOG-F-013、BLOG-F-016、BLOG-F-018、BLOG-F-019 | 阅读、相邻导航、喜欢切换、评论提交 | 阅读公开；互动登录 | 失效登录附加请求不打断阅读；待审不入公开列表；无公开评论分页/图片头像扩展 |
| PAGE-004 关于我 | BLOG-F-014、BLOG-F-040 | 显示介绍、联系邮箱及mailto | 公开 | 内容分区及空联系邮箱；不增加站内发送邮件表单 |
| PAGE-005 留言板 | BLOG-F-005、BLOG-F-023、BLOG-F-024 | 浏览分页、输入留言、登录提示、提交待审 | 阅读公开；提交登录 | 每页10条；非空/长度、待审反馈、空/失败状态 |
| PAGE-006 登录/注册 | BLOG-F-001、BLOG-F-002、BLOG-F-003、BLOG-F-005 | 页签、注册后登录、忘记密码、返回目标路径 | 未登录入口；登录身份决定后续权限 | 用户名50码点口径、新密码双边界、重复/禁用/失败提示；不预填演示凭证 |
| PAGE-007 找回密码 | BLOG-F-006、BLOG-F-007 | 发码、60秒倒计时、验证并重置、返回登录 | 未登录入口；账号须正常 | 发送成功与失败、限流、错误/过期/已用码及新密码校验 |
| PAGE-008 个人中心 | BLOG-F-003、BLOG-F-004、BLOG-F-005、BLOG-F-017、BLOG-F-020、BLOG-F-021、BLOG-F-022 | 资料、改名、本地头像预览/保存/取消、改密、喜欢分页、退出 | 当前USER/ADMIN本人 | 头像即时同步；每页6篇喜欢；改密重新登录；退出游客首页 |
| PAGE-009 404 | BLOG-F-015 | 未知路径提示、返回首页 | 公开 | 保持公共布局，区别于文章不存在的业务错误 |
| PAGE-010 数据概览 | BLOG-F-005、BLOG-F-025 | 统计、近7日图、文章入口 | ADMIN | 真实0、加载、失败/未知与部分失败区分 |
| PAGE-011 文章管理 | BLOG-F-005、BLOG-F-026、BLOG-F-027、BLOG-F-028、BLOG-F-029、BLOG-F-030 | 查询分页、新建/编辑抽屉、状态、置顶、删除确认 | ADMIN | 分类/标签可选状态及保留历史停用关联；保存失败不成功提示；封面仍为URL |
| PAGE-012 用户管理 | BLOG-F-005、BLOG-F-033、BLOG-F-034、BLOG-F-035 | 搜索分页、角色调整、启停确认 | ADMIN；禁止改自身角色/禁用自身 | 操作后状态及权限提示；不设计服务器主动注销入口 |
| PAGE-013 评论管理 | BLOG-F-005、BLOG-F-036、BLOG-F-037 | 状态/关键词/分页、当前页多选、审核确认、所属文章 | ADMIN | 已选数量、按钮禁用、审核失败/成功、翻页/查询清选择 |
| PAGE-014 留言管理 | BLOG-F-005、BLOG-F-038、BLOG-F-039 | 状态/关键词/分页、多选审核 | ADMIN | 与评论一致的交互，但没有所属文章列或文章跳转 |
| PAGE-015 系统管理 | BLOG-F-005、BLOG-F-031、BLOG-F-032、BLOG-F-040 | 三页签、分类/标签弹窗、启停/删除、五键配置保存 | ADMIN写；公开配置另由公开页面读 | 引用删除限制、停用关联说明、五字段校验、批次原子性与失败反馈 |

以上15个页面覆盖40个唯一功能需求编号（BLOG-F-001—040），并不意味着40项均实现完毕。BLOG-F-004另关联后台布局的退出下拉；BLOG-F-003/005贯穿守卫和认证状态，不另设“权限页面”。BLOG-F-040同时关联公开布局的品牌和副标题。

## 3. 业务规则与交互承载

| 真实规则编号 | 页面/交互或非页面处理 | 原型需要表达的目标；不代替后端验证 |
| --- | --- | --- |
| BLOG-BR-001、BLOG-BR-002、BLOG-BR-003 | PAGE-006/007/008 | 固定注册普通账号；用户名、邮箱口径；新密码8—64 Unicode码点且UTF-8≤72字节；旧账号登录兼容不能套用新设置限制 |
| BLOG-BR-004、BLOG-BR-005、BLOG-BR-006 | 全局导航、PAGE-003/005/008/012及后台 | 公开阅读与受保护操作分离；禁用保留历史内容；自身管理限制；禁用旧Token不因启用复活属于后端状态要求 |
| BLOG-BR-007、BLOG-BR-008、BLOG-BR-009 | PAGE-007/008；认证后端 | 六位码、倒计时、失败反馈及改密后重新登录；5分钟、一次性和Token撤销由服务保证 |
| BLOG-BR-010、BLOG-BR-011、BLOG-BR-012、BLOG-BR-013 | PAGE-011与公开文章页面 | 草稿/发布可见性、必选分类/可选标签、别名校验、发布时间；实际详情按ID，不设计slug新路由 |
| BLOG-BR-014、BLOG-BR-015 | PAGE-011/015 | 停用项不能新增关联，原停用关联未改变可保存；被引用项拒绝删除 |
| BLOG-BR-016、BLOG-BR-017 | PAGE-001/002/003/008/010/011 | 现有主排序和统计口径保留，确定性次级排序待设计；阅读计数/邻文关系为数据行为 |
| BLOG-BR-018 | PAGE-003/008 | 喜欢/取消状态及数量；幂等、唯一关系、并发计数由后端及测试验证 |
| BLOG-BR-019、BLOG-BR-020 | PAGE-003/005 | 非空≤500、trim、待审提示、重复提交错误；不是承诺限时防刷 |
| BLOG-BR-021、BLOG-BR-022 | PAGE-013/014及PAGE-003 | 审核通过/拒绝、1—100去重ID、整批结果；评论计数转换和并发属于实现/测试 |
| BLOG-BR-023 | PAGE-008及公共头像导航 | JPG/JPEG/PNG/WebP、≤5MB、预览/取消、同步、默认首字；UUID与安全清理为存储设计 |
| BLOG-BR-024 | PAGE-015及公开布局/PAGE-001/004 | 五键白名单、字段规则、缺失保留、合法空字符串保留、null/未知键/空对象拒绝；不公开敏感配置 |

24项业务规则均有承载或非页面说明，但原型只能验证表达和操作路径，不能证明事务、并发、安全等后端约束成立。

## 4. 非功能要求的页面边界

| 需求编号 | 处理方式 |
| --- | --- |
| BLOG-NF-001、BLOG-NF-002、BLOG-NF-006 | 账号、改密、权限/失效状态需要原型表达；密码哈希、JWT签名、版本和当前角色为安全设计/测试事项 |
| BLOG-NF-003、BLOG-NF-004 | 正文安全和上传错误反馈分别关联详情、个人中心；净化与文件真实性由实现/测试证明 |
| BLOG-NF-007、BLOG-NF-008 | 点赞/审核结果、列表分页可见；计数一致性、返回数量、竞争测试不靠原型验收 |
| BLOG-NF-010、BLOG-NF-014 | 所有交互需区分加载、空、成功、失败、认证/权限和确认取消；Result协议由接口设计验证 |
| BLOG-NF-011 | 本期以1366×768和1920×1080 PC布局为设计检查尺寸；Chrome/Edge实际版本在后续执行时记录，不虚构已兼容 |
| BLOG-NF-005、BLOG-NF-009、BLOG-NF-013、BLOG-NF-015、BLOG-NF-016 | 环境保密、性能测量、代码可维护性、安装与备份恢复主要为非页面级需求；映射至设计/测试/交付材料，而非新页面 |
| BLOG-NF-012 | 390px窄屏专项增强和专项验收为后续候选，不纳入本期必须原型；保留既有响应式 |

共说明16项非功能需求，其中15项本期，1项后续。结合40F/24BR，本文件对80项编号给出页面承载或非页面边界说明，不宣称完成80项设计和验收。

## 5. 静态差异及原型设计目标

| 分类 | 实际证据 | 与基线的差异/结论 | 原型阶段处理 |
| --- | --- | --- | --- |
| 已有结构主要符合 | Home、Message、Profile、两审核页 | 最新5/热门3、留言分页、头像本地预览及当前页审核已有代码 | 据实重构页面表达；保留待审、取消和空态，不宣称验证通过 |
| 已有交互需改进 | ArticleList的watch仅监听page/categoryId/tagId，route.query仅初始化 | BLOG-F-010要求既有历史项前进/后退同步，缺少同组件回填路径 | 画明直达、刷新、历史恢复后的控件/URL/结果一致状态 |
| 公开阅读需隔离附加失败 | ArticleDetail加载likeState；request统一保护请求401跳登录；restoreSession可使用缓存用户 | BLOG-F-005/BLOG-BR-004要求附带登录请求失败不打断公开阅读，当前catch不能取消全局跳转 | 保留正文；互动重新登录路径仍鉴权，不设计绕过权限 |
| 权限结构已有但不能单靠前端 | router ADMIN元数据与SecurityConfig后台hasRole；缓存角色可能陈旧 | 路由有限制，当前数据库角色判断由服务负责；不是所有角色变化都即时刷新菜单 | 明确403/登录失效、菜单更新和返回入口，实施时另设计 |
| 字段目标未完整实现 | LoginRequest上限30；ForgotPassword普通长度规则；Profile用户名maxlength50 | 用户名目标50码点，新密码码点及UTF-8边界不是现有普通长度 | 设计明确校验/错误状态；旧账号兼容不在登录强套新密码规则 |
| 已有启停UI，后端仍有差异 | AdminUserServiceImpl.updateStatus只改status | 禁用期间被拒绝不等于重新启用后旧Token永久无效 | 原型说明重新登录和历史内容保留；版本撤销属后端设计 |
| 已有重置页面，后端仍有差异 | PasswordResetServiceImpl更新密码不递增token_version | 邮箱重置旧Token失效目标未完整落实 | 保持成功返回登录；Token失效放安全设计/测试，不新增页面 |
| 已有文章编辑但停用目标不足 | ArticleManage选项未按状态区分；ArticleServiceImpl分类只查存在，标签一律要求ACTIVE | 新关联停用分类可被接受；原停用标签不变编辑也可能被拒绝 | 标识原关联与新增选项，拒绝新增停用关联、保留历史不变关联 |
| 五字段已有但保存规则不足 | SystemManage加载所有configKey形成Map后保存；AdminController/PublicController未五键白名单 | 未完整字段校验/批次原子性；读全部配置风险；目标部分更新缺失保留 | 只表达5字段、校验及批次失败；历史非法值先审计，不自动覆盖 |
| 错误状态表达不足 | AdminDashboard初始0/失败回退及“尚未接入”提示；UserManage失败列表设空并显示提示 | 统计失败可能呈现正常0；用户页错误提示虽存在，数量空态仍易误读 | 失败/未知与真实0分别表达，保留重试路径作为设计建议 |

上述差异为静态判断和后续设计依据，不是本轮复现的测试失败。未发现必须新增独立路由才能覆盖基线的功能；没有独立路由不等于没有功能入口。分类/标签、注册、文章编辑均有明确局部入口。

源码证据补充：[API封装](../../blog-web/src/api/blog.js)、[Profile API](../../blog-web/src/api/profile.js)、[认证Store](../../blog-web/src/store/auth.js)、[请求处理](../../blog-web/src/utils/request.js)、[LoginRequest](../../blog-server/src/main/java/com/example/blog/dto/LoginRequest.java)、[文章服务](../../blog-server/src/main/java/com/example/blog/service/impl/ArticleServiceImpl.java)、[用户管理服务](../../blog-server/src/main/java/com/example/blog/service/impl/AdminUserServiceImpl.java)、[重置服务](../../blog-server/src/main/java/com/example/blog/service/impl/PasswordResetServiceImpl.java)、[配置写入](../../blog-server/src/main/java/com/example/blog/controller/AdminController.java)、[公开配置](../../blog-server/src/main/java/com/example/blog/controller/PublicController.java)。
