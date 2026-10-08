# 已确认需求影响分析（20项问题及A—G指定范围）

> 12.5-D当前状态：用户于2026-10-08通过ChatGPT异步正式批准79项本期需求、BLOG-NF-012后续候选及全部80项优先级（Must57 / Should22 / Could1），授权建立SRS v1.0.0。文档级正式需求基线已建立；Git固化尚未执行。需求批准不代表代码完成或测试通过，也不是教师审批或学校验收。此前v0.8及各阶段候选状态仅为历史。

文档版本：v0.9（12.5-D状态同步；对应SRS v1.0.0）
日期：2026-10-08<br>
依据：[异步评审记录](requirements-review-record.md)、需求清单/SRS v1.0.0及本地HEAD `8febf5d`代码。<br>
性质：文档与代码静态影响分析；不选择最终技术方案、不修改代码/SQL、不执行测试，不是实施完成记录。

## 1. 影响分类与共同约束

确定影响表示已读代码可定位的差异；可能涉及表示后续方案可能调整；待核实表示须设计或真实测试。所有引用是存在文件；无数据库结构变更的判断基于SQL脚本，未检查运行库，不保证老师机器已有相同结构。

原四项及第二批九项目前无必须新增业务接口/表的已知需求；最终技术细节未定，后续设计仍需核实。推荐复用既有契约，改变校验或访问反馈语义；不通过放开权限解决前端体验。后续在设计/测试准备后另请授权实施，正式变更编号按真实登记产生，本轮不制造虚假CR历史。

## 2. Q01 用户名最大50字符

### 2.1 目标、现状与差异

注册、登录、改名最大50，前后端数据库一致；其他最小长度、字符计数及去空格技术细节未定；Q13已确认注册/改名去首尾空格。当前[LoginRequest](../../blog-server/src/main/java/com/example/blog/dto/LoginRequest.java)仍`@Size(max=30)`；[RegisterRequest](../../blog-server/src/main/java/com/example/blog/dto/RegisterRequest.java)、[UpdateProfileRequest](../../blog-server/src/main/java/com/example/blog/dto/UpdateProfileRequest.java)均50。[登录页](../../blog-web/src/views/admin/Login.vue)没有统一maxlength/rules；[个人中心](../../blog-web/src/views/public/Profile.vue)已有maxlength=50。[SQL](../../blog-server/src/main/resources/schema.sql)用户名VARCHAR(50)。

注册和改名服务trim用户名，登录按原字符串查询；本次不授权修改该空白政策。Java/浏览器字符串长度和MySQL字符计数对特殊Unicode可能不一致，属于待核实细节，不擅自决定计数方式或新增正则。

### 2.2 文件、数据及接口影响

| 层次 | 文件及可能工作 |
| --- | --- |
| 前端 | Login.vue统一注册/登录上限和提示；Profile.vue核对50行为；[认证API](../../blog-web/src/api/auth.js)、[个人API](../../blog-web/src/api/profile.js)作契约回归，通常无需路径修改 |
| 后端 | LoginRequest明确需调整长度及提示；RegisterRequest/UpdateProfileRequest一致性核对；[UserServiceImpl](../../blog-server/src/main/java/com/example/blog/service/impl/UserServiceImpl.java)、[AuthController](../../blog-server/src/main/java/com/example/blog/controller/AuthController.java)、[UserProfileController](../../blog-server/src/main/java/com/example/blog/controller/UserProfileController.java)、[UserMapper](../../blog-server/src/main/java/com/example/blog/mapper/UserMapper.java)检查查重、正常登录及本人限制 |
| 数据库 | 脚本列50已满足，不预计DDL或数据迁移；实际旧库列宽待核实，不在本轮连接或扩列 |
| 接口 | 注册/登录/改名路径和字段保持；登录允许31—50是兼容扩展，错误提示需统一 |

### 2.3 风险与测试

不能误增3字符最小或仅字母数字限制；不得因放宽长度跳过唯一性或禁用检查。拟新增边界测试：三入口30/31/50通过、51拒绝，注册—登录与改名—重登闭环；中文、重复、空白及密码/禁用回归；多字节/特殊Unicode语义待核实。用例ID待分配，现有[UserServiceImplTest](../../blog-server/src/test/java/com/example/blog/service/UserServiceImplTest.java)未覆盖Controller长度边界，不将其当作31—50验收证据。

## 3. Q02 公开阅读与失效Token隔离

### 3.1 目标、现状与差异

游客及Token失效用户可阅读公开文章，自动附带状态请求失败不强制登录；主动互动仍鉴权。当前[SecurityConfig](../../blog-server/src/main/java/com/example/blog/security/SecurityConfig.java)公开接口放行，[JwtFilter](../../blog-server/src/main/java/com/example/blog/security/JwtFilter.java)跳过公开路径，后端边界已存在。

[Axios](../../blog-web/src/utils/request.js)对所有非公开请求401清缓存并`window.location.assign`；[详情页](../../blog-web/src/views/public/ArticleDetail.vue)在缓存显示已登录时请求likeState，其catch只返回null，不能阻止已发生的跳转。[Store](../../blog-web/src/store/auth.js)仅Token无用户时查询me，同样可能触发全局跳转；两种情况都需验收。[路由](../../blog-web/src/router/index.js)本身允许公开页面，但catch不能撤回拦截器导航。以上是静态风险，未做浏览器复现。

### 3.2 文件、数据及接口影响

| 层次 | 文件及可能工作 |
| --- | --- |
| 前端 | request.js区分可选身份读取与主动受保护请求；ArticleDetail.vue降级并同步互动显示；auth.js Store一致清理内存和存储；router/index.js复核公开与保护导航；[blog.js](../../blog-web/src/api/blog.js)的likeState及auth API可传递错误处理选项（仅候选设计） |
| 后端 | SecurityConfig/JwtFilter复核不放宽；[InteractionController](../../blog-server/src/main/java/com/example/blog/controller/InteractionController.java)、[InteractionServiceImpl](../../blog-server/src/main/java/com/example/blog/service/impl/InteractionServiceImpl.java)鉴权与本人状态作为回归；无需为读本人点赞状态改成匿名接口 |
| 数据库 | 不预计结构或数据迁移，只有客户端处理策略与认证回归 |
| 接口 | HTTP401/403和身份要求不变；允许可选读取失败在当前公开页面降级，不能把服务端401改成成功假身份 |

### 3.3 风险与测试

全局屏蔽401会导致后台/个人中心错误保持登录，属禁止方案。缓存和Pinia应一致，不能只有存储清掉而按钮仍显示已认证。当前用户是否每次恢复查询、角色刷新频率未被本次确认，按必要设计讨论。

拟测试无Token、过期/伪造Token加用户缓存、仅Token三种公开入口；自动likeState401留在详情、正文评论可读；主动点赞评论仍401/引导登录，USER后台403，保护路由仍登录跳转；缺失文章错误与网络错误不混同身份降级。现有[SecurityFlowTest](../../blog-server/src/test/java/com/example/blog/security/SecurityFlowTest.java)只覆盖部分JWT/公开过滤，不能证明浏览器无跳转，需前端/接口端到端补测，ID待分配。

## 4. Q05 两种改密废除旧Token

### 4.1 目标、现状与差异

两条路径成功后所有此前Token失效，重新登录；具体实现由后续设计决定。UserServiceImpl.changePassword目前更新BCrypt且token_version+1，但未检查更新行数（Q12失败语义已确认、具体实现待设计）；[PasswordResetServiceImpl](../../blog-server/src/main/java/com/example/blog/service/impl/PasswordResetServiceImpl.java).resetPassword更新密码并检查行数，却未递增版本。JwtService签入tokenVersion，JwtFilter比较数据库版本，机制可复用，但本轮不将其指定为最终方案。

### 4.2 文件、数据及接口影响

| 层次 | 文件及可能工作 |
| --- | --- |
| 前端 | Profile.vue改密后的logout/login流程回归；[ForgotPassword.vue](../../blog-web/src/views/public/ForgotPassword.vue)成功返回登录；auth API、request.js和Store核对旧Token401后的状态，不新增静默刷新 |
| 后端 | UserServiceImpl/PasswordResetServiceImpl统一成功语义；JwtService/JwtFilter/[JwtPayload](../../blog-server/src/main/java/com/example/blog/security/JwtPayload.java)核对失效边界；[User](../../blog-server/src/main/java/com/example/blog/entity/User.java)、UserMapper及验证码Mapper核对原子更新、条件与并发 |
| 数据库 | schema已有token_version及v2迁移；使用现有版本方案时不预计新DDL，但成功改密需要更新安全状态；旧库是否有列须先核实，禁止重复执行v2 |
| 接口 | 路径、参数和Result不必改变；邮箱重置后已有会话不能再用，这是已确认安全行为变化，客户端需重新登录 |

### 4.3 风险与测试

数据库更新、验证码消费、失效状态必须满足成功/失败一致性，避免密码已改却会话未废或错误重置废除有效登录；不同设备多个Token、登录与改密竞争需后续设计验证。禁止改变role/status或通过重置解禁，不能将此决定扩大成Q03退出/启停政策。

拟测试两路径各持多个旧Token，成功后保护请求401、新密码新Token有效；错误当前密码、错误/过期/已用码、禁用账号、更新失败不作为成功；邮箱重置与改密回归。已有UserServiceImplTest和[PasswordResetServiceImplTest](../../blog-server/src/test/java/com/example/blog/service/PasswordResetServiceImplTest.java)提供部分线索，邮箱重置旧Token与完整过滤器链需补用例，ID待分配；真实邮件仍Q04待确认。

## 5. Q08 停用项关联政策

### 5.1 目标、现状与差异

新文章不能新增停用分类标签；编辑本文章保留原有停用关联且不变可保存，其他文章不能新增该停用关联；停用不清历史。ArticleServiceImpl.createArticle/updateArticle均调用validateSaveRequest，分类只查存在，标签全部要求ACTIVE；未传入原分类/原标签集合判断新增差集。

[ArticleManage.vue](../../blog-web/src/views/admin/ArticleManage.vue)读取后台全部分类标签，选择器不区分停用，新建默认取第一分类；原标签由文章详情回填。[AdminController](../../blog-server/src/main/java/com/example/blog/controller/AdminController.java)及[TagAdminServiceImpl](../../blog-server/src/main/java/com/example/blog/service/impl/TagAdminServiceImpl.java)启停仅改状态，没有自动清关系；这点符合历史保留目标。

### 5.2 文件、数据及接口影响

| 层次 | 文件及可能工作 |
| --- | --- |
| 前端 | ArticleManage.vue新建仅选启用，编辑显示原停用项且不能新增其他停用项，默认分类不能落到停用；[SystemManage.vue](../../blog-web/src/views/admin/SystemManage.vue)提示启停影响；blog.js和详情回填作回归 |
| 后端 | ArticleServiceImpl根据创建/更新及原关联校验；[ArticleAdminController](../../blog-server/src/main/java/com/example/blog/controller/ArticleAdminController.java)、[ArticleSaveRequest](../../blog-server/src/main/java/com/example/blog/dto/ArticleSaveRequest.java)保持契约；ArticleMapper/CategoryMapper/TagMapper/[ArticleTagMapper](../../blog-server/src/main/java/com/example/blog/mapper/ArticleTagMapper.java)读取原关系；启停Controller/Tag服务不清历史 |
| 数据库 | status及category_id、关联表已经支持，不预计DDL或数据迁移；不得清理历史停用关系，逻辑删除项不等于合法停用项 |
| 接口 | POST/PUT文章保留现有字段；新关联停用分类由可过变拒，旧停用标签保留由拒变可过；旧客户端仍由后端校验兜底 |

### 5.3 风险与测试

原关系只能从服务器读取，不能信任请求自称“原有”；不能因为别的文章有该标签就允许当前文章新增。标签整体替换前必须校验完整集合及新增差集，失败不得删原关联；分类停用与保存并发、请求遗漏tagIds是否代表删除仍需设计/核实，不新造已确认规则。

拟测试新建停用分类/标签均拒、原文分类/标签未变只改正文成功、其他文章新增均拒、启用项正常新增、混合原停用与新增启用标签、移除后再新增停用标签须按当前原关系判定、停用不删除历史、非法ID/权限/事务失败回归。现有[TagAdminServiceImplTest](../../blog-server/src/test/java/com/example/blog/service/TagAdminServiceImplTest.java)检查标签创建/引用删除，不覆盖上述文章关联政策；文章Service与数据库集成用例待新增、ID待分配。

## 6. 建议实施顺序与质量门

1. 先审阅本影响分析，确认剩余长度计数、恢复和保存语义等真正阻塞细节；四项目标不再反复当作未决定。
2. 在修改代码前建立正式测试计划/用例及预期，更新相关设计；不以本场景列表替代完整测试用例。
3. 建议Q01先处理，变动较小且修复账号可用性；再Q05账户失效；再Q02客户端降级并回归保护访问；最后Q08原关系判定与选择器联调。Q02/Q05应一起回归，顺序不是已批准排期。
4. 对每项检查范围、失败/权限/边界、真实数据影响；成功及失败分别留证，未测不标通过。
5. 同步需求矩阵、接口/设计和测试记录，经用户确认再决定基线、授权提交，不擅自Git操作。

本轮结论：目标明确，已定位主要代码差异；无必须改表的已知事项，运行库和端到端结果未核实。可以作为下一阶段设计与测试输入，不是代码修复或验收成果。


## 7. Q03 禁用用户与旧Token

| 分析项 | 内容 |
| --- | --- |
| 1 确认目标 | 禁用时此前签发Token失效；重新启用不得恢复禁用前旧Token，必须重新登录取得新Token；本期暂不增加服务器端主动注销，保留现有客户端退出。 |
| 2 当前实现 | updateStatus仅更新status，不改变token_version；JwtFilter查ACTIVE和版本，重新启用后未过期且版本未变的旧Token存在恢复有效的路径。客户端logout只清本地。 |
| 3 差异 | 已定位目标缺口或测试证据缺口，不将其静态推断为已复现结果；本轮未改代码。 |
| 4 真实代码文件 | [UserManage.vue](../../blog-web/src/views/admin/UserManage.vue)、[auth.js](../../blog-web/src/store/auth.js)、[index.js](../../blog-web/src/router/index.js)、[request.js](../../blog-web/src/utils/request.js)、[AdminUserServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/AdminUserServiceImpl.java)、[UserAdminController.java](../../blog-server/src/main/java/com/example/blog/controller/UserAdminController.java)、[JwtFilter.java](../../blog-server/src/main/java/com/example/blog/security/JwtFilter.java)、[JwtService.java](../../blog-server/src/main/java/com/example/blog/security/JwtService.java)、[UserMapper.java](../../blog-server/src/main/java/com/example/blog/mapper/UserMapper.java)、[User.java](../../blog-server/src/main/java/com/example/blog/entity/User.java) |
| 5 前端影响 | 前端本地退出不变；启用后提示重登、旧Token401清理内存与缓存；公开阅读按Q02仍不中断。 |
| 6 后端影响 | 后端把禁用状态与会话失效作为一致操作，候选可复用token_version，须原子递增、检查真实成功和并发登录边界。当前未指定最终方案。 |
| 7 数据库/现有数据影响 | 已有sys_user.token_version；通常可复用无需DDL，旧库有无列待核实。不得修改历史评论留言/点赞或凭空撤销全部旧数据。 |
| 8 API行为影响 | 保持PATCH /api/admin/users/{id}/status及认证路径；启用后旧Token由可能可用变401是有意安全行为变化。 |
| 9 安全/兼容/并发风险 | 启停与登录、改密的竞争可能丢失版本更新；不能用前端缓存或只查ACTIVE替代永久失效。 |
| 10 拟补测试 | 禁用前取得两个有效Token→禁用→二者保护请求401→重新启用→原两Token仍401→重新登录新Token可用；客户端退出入口及跳转保留；不要求新增服务器注销接口。 用例ID待分配，未执行。 |
| 11 建议优先级 | P0安全目标，建议与Q05会话失效及Q12写入成功统一设计。 |
| 12 未定细节 | 失效机制及与登录/启停竞争的原子边界待设计；D已确认受保护路由核实及下一次鉴权生效，具体机制待设计。 |

关联需求：BLOG-F-004、BLOG-F-035、BLOG-NF-002、BLOG-BR-004。这是指定范围的确认，不批准整条复合需求。

## 8. Q10 并发一致性与最小竞争测试

| 分析项 | 内容 |
| --- | --- |
| 1 确认目标 | 在独立测试数据库对点赞/取消点赞、评论审核、同邮箱验证码发送等现有清单并发场景，每场景2个并发请求、重复10轮，验证最终状态、计数、唯一性和业务约束；不得破坏真实业务库，不称高并发性能认证。 |
| 2 当前实现 | 点赞有联合键及事务；评论审核先读旧状态再无条件更新并计数；发送验证码先查时间再生成，未见显式同邮箱竞争控制；均不能据静态代码认定并发通过。 |
| 3 差异 | 已定位目标缺口或测试证据缺口，不将其静态推断为已复现结果；本轮未改代码。 |
| 4 真实代码文件 | [InteractionServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/InteractionServiceImpl.java)、[CommentAdminServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/CommentAdminServiceImpl.java)、[PasswordResetServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/PasswordResetServiceImpl.java)、[ArticleLikeMapper.java](../../blog-server/src/main/java/com/example/blog/mapper/ArticleLikeMapper.java)、[CommentMapper.java](../../blog-server/src/main/java/com/example/blog/mapper/CommentMapper.java)、[EmailVerificationCodeMapper.java](../../blog-server/src/main/java/com/example/blog/mapper/EmailVerificationCodeMapper.java)、[schema.sql](../../blog-server/src/main/resources/schema.sql)、[ArticleDetail.vue](../../blog-web/src/views/public/ArticleDetail.vue)、[CommentManage.vue](../../blog-web/src/views/admin/CommentManage.vue)、[ForgotPassword.vue](../../blog-web/src/views/public/ForgotPassword.vue) |
| 5 前端影响 | 前端防重点击只作辅助，不证明后端并发正确；无需本轮改变按钮。 |
| 6 后端影响 | 既有点赞TX/唯一键、审核TX先读后写、发码TX查后写分别审查；失败再评估锁/条件更新，不提前批准方案。 |
| 7 数据库/现有数据影响 | 仅独立测试库及一致初值；不得执行真实库清理。机制未定无必须DDL；Q16种子是否校准另确认，不直接拿不一致种子作为并发夹具。 |
| 8 API行为影响 | 原点赞、审核、reset-code接口不增路径；按最终状态判定，不要求两个请求都成功。重复/冲突反馈需一致。 |
| 9 安全/兼容/并发风险 | 重复审批竞争可能双增；唯一键不保证所有计数一致；两请求查码时间均通过可能多发送。数据库回滚不能撤回邮件。 |
| 10 拟补测试 | 每场景2并发×10轮；点赞关系唯一且计数等于关系数、不负；审核计数等于有效APPROVED评论数，重复通过不双计，失败批次不半写；同邮箱60秒窗口至多一个发送业务成功并保留唯一当前有效码，旧码失效；逐轮独立核对最终数据。 用例ID待分配，未执行。 |
| 11 建议优先级 | P0测试前置，先编写用例与数据隔离方案，再另获授权执行，失败再改设计/代码。 |
| 12 未定细节 | 测试用例ID待分配；数据夹具、同步起跑和邮件模拟方式待测试设计，Q04真实收件目标已确认，SMTP环境与证据待设计，未执行，锁/条件更新方案未批准。 |

关联需求：BLOG-F-006、BLOG-F-016、BLOG-F-037、BLOG-NF-007、BLOG-BR-007、BLOG-BR-018、BLOG-BR-021、BLOG-BR-022。这是指定范围的确认，不批准整条复合需求。

## 9. Q12 失败反馈、配置批量原子保存与统计异常

| 分析项 | 内容 |
| --- | --- |
| 1 确认目标 | 更新/保存失败不报告成功，提供明确适当错误；合法配置批次全部成功或全部回滚；统计查询失败不得伪装真实0；HTTP请求成功不等于业务写入成功。 |
| 2 当前实现 | changePassword未检查updateById影响行数；saveConfigs逐键无整批事务且缺失键先插入后赋值；Dashboard失败保留初始0并显示过时说明，趋势失败用空数组且未单独标注。 |
| 3 差异 | 已定位目标缺口或测试证据缺口，不将其静态推断为已复现结果；本轮未改代码。 |
| 4 真实代码文件 | [UserServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/UserServiceImpl.java)、[AdminController.java](../../blog-server/src/main/java/com/example/blog/controller/AdminController.java)、[GlobalExceptionHandler.java](../../blog-server/src/main/java/com/example/blog/config/GlobalExceptionHandler.java)、[AdminUserServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/AdminUserServiceImpl.java)、[ArticleServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/ArticleServiceImpl.java)、[TagAdminServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/TagAdminServiceImpl.java)、[CommentAdminServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/CommentAdminServiceImpl.java)、[MessageAdminServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/MessageAdminServiceImpl.java)、[AdminDashboard.vue](../../blog-web/src/views/admin/AdminDashboard.vue)、[SystemManage.vue](../../blog-web/src/views/admin/SystemManage.vue)、[Profile.vue](../../blog-web/src/views/public/Profile.vue)、[request.js](../../blog-web/src/utils/request.js) |
| 5 前端影响 | Dashboard各卡片/趋势区区分加载、失败、真实0；表单只有实际业务成功才提示。网络超时后的结果不确定需提示/核查，不假称一定回滚。 |
| 6 后端影响 | 检查预期写入行数及失败异常；配置事务边界在后续设计确定；GlobalExceptionHandler统一适当错误但不回显栈。原子性不能由前端Promise实现。 |
| 7 数据库/现有数据影响 | 现有键值表足够，原则无需DDL；新允许键先赋合法值再插入是候选修复；故障注入只在测试库。已有半写数据若有需先审计另授权，当前未读运行库。 |
| 8 API行为影响 | PUT site-config批次全成功或全拒/回滚；改密0行等不能Result.success；统计错误应错误响应，不以默认0替代。保持既有Result契约。 |
| 9 安全/兼容/并发风险 | 误把幂等未变字段的0行判失败亦有兼容风险，须区分预期写入与已达目标；可见错误不等于完整内部异常暴露。 |
| 10 拟补测试 | 预期写入未完成（包括更新0行）和数据库异常均不得成功提示；合法五键批次成功后全项可读，任一项非法或中途失败整批不变；真实空统计可为0，请求失败显示失败/未知而非正常0，部分失败分别标明。 用例ID待分配，未执行。 |
| 11 建议优先级 | P0可靠性，配置与Q15共同设计；通用失败回归覆盖既有写接口，但不扩张功能范围。 |
| 12 未定细节 | 错误文案、具体异常映射及幂等无变更的成功判定待设计，不把所有更新返回0都机械视作失败；本次通用原则的其他写接口回归范围须测试设计。 |

关联需求：BLOG-F-022、BLOG-F-025、BLOG-F-040、BLOG-NF-010、BLOG-NF-014、BLOG-BR-024。这是指定范围的确认，不批准整条复合需求。

## 10. Q15 公开配置白名单与参数校验

| 分析项 | 内容 |
| --- | --- |
| 1 确认目标 | 仅siteName、siteSubtitle、homeIntro、aboutContent、email五键可公开读取和由ADMIN保存，未知键拒绝写入且不得公开，五字段均拒绝null。siteName必填，保存前去除首尾空格，处理后1—50字符；siteSubtitle允许空字符串、最多100字符；homeIntro允许空字符串、最多500字符；aboutContent允许空字符串、最多5000字符；email允许空字符串，非空须符合合理邮箱格式、最多254字符，非SMTP账号。批量保存继续遵守Q12全部成功或全部回滚。 |
| 2 当前实现 | 页面及SQL实际五键一致；PublicController全表读取，AdminController任意Map写入且无值校验，SystemManage加载全部返回键后整体提交，数据库config_value允许NULL。 |
| 3 差异 | 已定位目标缺口或测试证据缺口，不将其静态推断为已复现结果；本轮未改代码。 |
| 4 真实代码文件 | [SystemManage.vue](../../blog-web/src/views/admin/SystemManage.vue)、[blog.js](../../blog-web/src/api/blog.js)、[PublicLayout.vue](../../blog-web/src/layouts/PublicLayout.vue)、[Home.vue](../../blog-web/src/views/public/Home.vue)、[About.vue](../../blog-web/src/views/public/About.vue)、[AdminController.java](../../blog-server/src/main/java/com/example/blog/controller/AdminController.java)、[PublicController.java](../../blog-server/src/main/java/com/example/blog/controller/PublicController.java)、[SiteConfig.java](../../blog-server/src/main/java/com/example/blog/entity/SiteConfig.java)、[SiteConfigMapper.java](../../blog-server/src/main/java/com/example/blog/mapper/SiteConfigMapper.java)、[SecurityConfig.java](../../blog-server/src/main/java/com/example/blog/security/SecurityConfig.java)、[schema.sql](../../blog-server/src/main/resources/schema.sql) |
| 5 前端影响 | SystemManage仅向编辑模型装载和提交白名单五键，不携带后台额外键；五字段空值、长度及邮箱格式规则已确认，校验实现待设计；公开前台默认回退不当保存规则。 |
| 6 后端影响 | PublicController只选/输出白名单；AdminController须校验五键白名单、字段非null、siteName去首尾空格后1—50及其余上限/邮箱格式，未知键拒绝，与Q12整批事务协同；Security写权限不放开。 |
| 7 数据库/现有数据影响 | 不需新增表，config_value可NULL只是结构现状，不代表API允许null；历史未知键仅审计、过滤，未授权删除或公开；内部安全配置不搬进此表。 |
| 8 API行为影响 | GET /api/public/site-config匿名仅五键；PUT /api/admin/site-config仅ADMIN，未知键/非法null拒绝。后台GET内部配置范围不等于全部公开，不新增私有配置平台。 |
| 9 安全/兼容/并发风险 | 收紧任意Map可能影响旧脚本客户端，需要记录兼容；白名单不可由请求任意提供；不能把联系邮箱当SMTP认证信息。 |
| 10 拟补测试 | siteName空字符串/纯空格拒绝，首尾空格去除后1—50字符合法，50边界通过、51拒绝；其他四项允许空字符串，分别超100/500/5000/254拒绝，非空email格式不合法拒绝；五项null拒绝；未知键不公开、不允许ADMIN保存，游客写401、USER写403；非法项或保存失败整批回滚不报成功。A/B已确认缺失保留、显式空值依字段规则、Unicode码点计数；验收未执行。 已有额外键不擅删。 用例ID待分配，未执行。 |
| 11 建议优先级 | P0公开数据边界；字段空值、长度及非空邮箱格式目标已确认；与Q12合并设计校验及原子保存，缺失/默认语义仍待确认。 |
| 12 未定细节 | 待设计或确认：A/B已确认部分更新、缺失保留、空值区分、初始化与保存分离、Unicode码点与现有trim；邮箱校验器、历史审计及失败反馈实现待设计。按A已确认的请求语义设计；其他四项未获准自动trim。 |

关联需求：BLOG-F-040、BLOG-NF-005、BLOG-BR-024。这是指定范围的确认，不批准整条复合需求。

## 11. Q19 密码字符长度与编码字节边界

| 分析项 | 内容 |
| --- | --- |
| 1 确认目标 | 新密码须同时满足8—64个Unicode码点及UTF-8编码后不超过72字节；超出任一限制明确拒绝，不静默截断。适用于注册、登录状态下修改密码及邮箱验证码重置；前后端口径一致，后端独立校验。保留已有账号及密码哈希，不批量强制重置；已有密码登录验证不得直接套用新密码设置限制，不静默修改、截断或重新编码原密码，成功登录后可建议主动更新。 |
| 2 当前实现 | 当前BCryptPasswordEncoder来自spring-security-crypto 6.3.4，直接encode/matches且无UTF-8字节保护；新密码DTO按@Size 8—64，登录仅非空/max64，当前密码无Size；字符长度不等于UTF-8字节长度。 |
| 3 差异 | 已定位目标缺口或测试证据缺口，不将其静态推断为已复现结果；本轮未改代码。 |
| 4 真实代码文件 | [Login.vue](../../blog-web/src/views/admin/Login.vue)、[Profile.vue](../../blog-web/src/views/public/Profile.vue)、[ForgotPassword.vue](../../blog-web/src/views/public/ForgotPassword.vue)、[RegisterRequest.java](../../blog-server/src/main/java/com/example/blog/dto/RegisterRequest.java)、[LoginRequest.java](../../blog-server/src/main/java/com/example/blog/dto/LoginRequest.java)、[ChangePasswordRequest.java](../../blog-server/src/main/java/com/example/blog/dto/ChangePasswordRequest.java)、[ResetPasswordRequest.java](../../blog-server/src/main/java/com/example/blog/dto/ResetPasswordRequest.java)、[UserServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/UserServiceImpl.java)、[PasswordResetServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/PasswordResetServiceImpl.java)、[SecurityConfig.java](../../blog-server/src/main/java/com/example/blog/security/SecurityConfig.java)、[pom.xml](../../blog-server/pom.xml) |
| 5 前端影响 | 三密码页面需统一Unicode码点及UTF-8字节双校验反馈；Login页目前无完整rules、Forgot有8—64rules、Profile仅完整性检查。不得trim密码或自行限为ASCII。 |
| 6 后端影响 | 注册/改密/重置在新密码encode及新旧比较前独立执行新规则；登录matches及当前旧密码验证不直接套用新设置边界；crypto实际6.3.4没有主动超字节拒绝，不能只依赖DTO。 |
| 7 数据库/现有数据影响 | password摘要列足够，无必然DDL；不能由既有摘要反推密码是否曾超长。旧账号兼容/恢复是风险，不能要求用户提供原密码或批量重写摘要。 |
| 8 API行为影响 | 路径不变，新设置确认规则会明确拒绝超码点或超UTF-8字节输入；要明确登录旧密码与创建新密码策略，不能自动全入口min8。 |
| 9 安全/兼容/并发风险 | UTF-16字符串长度与UTF-8字节不等；超过72有效字节的尾部未参与摘要存在等价输入风险。升级库行为可能改变，也未获算法替换许可。 |
| 10 拟补测试 | 新密码7/65个Unicode码点拒绝；8/64码点且≤72个UTF-8字节可通过长度校验；≤64码点但>72字节拒绝；恰好72字节不因字节长度被拒绝；超限不截断，三设置入口一致且后端独立拒绝绕过前端的输入；规则上线不自动改变旧哈希或强制重置，旧密码登录兼容单独回归。均为待设计、未执行验收，不等于满足其他业务条件。 用例ID待分配，未执行。 |
| 11 建议优先级 | P0先做码点/UTF-8统一校验与旧哈希兼容设计，准备边界和回归用例；代码实施需另获授权。 |
| 12 未定细节 | 待设计/核实：Java/JavaScript码点与UTF-8统计、Unicode规范化是否需处理、旧账号兼容回归、现存超限密码数量和错误逐字文案；阈值、码点口径、超限拒绝及保留旧哈希兼容原则已于12.4-C确认，不再待批准。不得自行规范化、转换、预哈希或更换算法。 |

关联需求：BLOG-F-001、BLOG-F-002、BLOG-F-007、BLOG-F-022、BLOG-NF-001、BLOG-BR-003。这是指定范围的确认，不批准整条复合需求。

### Q10 场景及最终不变量（每一场景2并发、10轮，未执行）

| 场景 | 数据隔离与预期不变量 |
| --- | --- |
| 同用户同文章同时点赞 | 独立库初始计数和关系一致，最终联合关系至多1，like_count等于有效关系总数，无重复增计数 |
| 同时取消；点赞/取消竞争 | 初始含/不含关系分别准备；最终允许由实际提交顺序决定是否点赞，但关系唯一、计数等于实际关系数且非负，不强定哪请求先完成 |
| 同评论同时通过；重复/交叠批次审核 | 同状态不能重复改变计数；最终comment_count等于未逻辑删除APPROVED集合；合法批次整批原子，含不存在ID失败不半写 |
| 同评论通过与拒绝竞争 | 最终状态可为实际有效提交顺序的通过或拒绝，但公开可见性和计数必须与最终状态一致；不要求固定获胜者 |
| 同邮箱同时发送重置码 | 60秒窗口至多1个发送业务成功、相应当前有效码唯一，旧未用码失效；拒绝请求不能绕过间隔。邮件调用次数与数据库成功区分，真实到达仍需Q04确认 |

上述是现有清单范围内用例分解，不增加已批准功能。并发启动、10轮之间夹具/时钟重置、可控邮件替身、事务失败注入及环境隔离由后续测试计划落实；每轮保留响应与最终数据证据，不把模拟收件写成真实收件。

### Q12 通用写入回归范围

主要关联BLOG-F-022/025/040、BLOG-NF-010/014、BLOG-BR-024。通用“失败不报成功”也约束既有注册、资料、头像、文章、分类/标签、角色/状态、互动及审核写入；上述模块应在测试设计中检查。已读UserServiceImpl资料/头像及PasswordResetServiceImpl存在行数检查，不能说所有更新都缺校验；AdminUserServiceImpl角色/状态、分类Controller及评论审核有未核对写入结果的位置。涉及实际差异的文件见本节表，后续逐项确认预期更新/幂等语义，不能机械重构全部接口。

### Q15 真实五键及权限/字段细节

| 配置键 | 用途及真实前台位置 | 用户已确认的值规则 | 尚未确认的细节 |
| --- | --- | --- | --- |
| `siteName` | 博客名称；PublicLayout品牌/页脚，Home配置模型 | 必填；保存前去除首尾空格，处理后1—50字符；拒绝空字符串、纯空格、null | 去空格字符集合、长度技术计数、缺失键与默认值 |
| `siteSubtitle` | 博客副标题；PublicLayout页脚 | 允许空字符串；最多100字符；拒绝null | 长度技术计数、缺失键与默认值；未批准自动trim |
| `homeIntro` | 首页简介；Home介绍区域 | 允许空字符串；最多500字符；拒绝null | 长度技术计数、缺失键与默认值；未批准自动trim |
| `aboutContent` | 关于我内容；About正文，当前文本展示 | 允许空字符串；最多5000字符；拒绝null | 长度技术计数、缺失键与默认值；未批准自动trim或富文本扩展 |
| `email` | 公开联系邮箱；About的mailto链接 | 允许空字符串；非空须合理邮箱格式；最多254字符；拒绝null；不是SMTP账号 | 邮箱格式实现、长度技术计数、缺失键与默认值；不复用账号邮箱100上限为站点规则 |

公开GET /api/public/site-config仅返回上述白名单内已有业务配置，不返回未知键或内部秘密；某个键缺失时的返回/补值语义未确认。后台PUT /api/admin/site-config继续仅ADMIN可写，未知键拒绝，按Q12整批全部成功或全部回滚。空字符串是显式值，不等于请求未包含该键；缺失键行为须另行决定。

当前五字段校验缺失、全表读取及任意Map保存属于现状；准确使用位置和现有默认值详见本文件12.4-C补充节，不认为这些目标已实现。

### Q19 静态依赖核查与四个密码入口

| 入口 | 当前字符校验 | 当前编码/匹配 | 待确认或差异 |
| --- | --- | --- | --- |
| 注册 | RegisterRequest.password非空、@Size(8—64)；确认非空并比一致 | UserServiceImpl.register直接encode | 无UTF-8字节保护；前端无完整rules |
| 登录 | LoginRequest.password非空、@Size(max64)，没有min8 | UserServiceImpl.login直接matches | 现有max64可能阻挡部分旧输入；登录验证不套用新设置8—64码点/72字节限制，兼容未验证 |
| 登录状态改密 | ChangePasswordRequest.newPassword 8—64；currentPassword仅非空；确认一致 | changePassword先matches旧/新，再encode | 新密码执行新双边界；当前旧密码保留哈希验证兼容，行数缺口另Q12 |
| 邮箱重置 | ResetPasswordRequest.newPassword 8—64，确认非空/一致 | resetPassword先matches防旧密码再encode | 无字节保护；版本缺口另Q05；失败不能消费验证码 |

证据：项目pom导入Spring Boot 3.3.5依赖BOM；本地缓存spring-boot-dependencies 3.3.5 POM的spring-security.version=6.3.4；实际缓存spring-security-crypto-6.3.4.jar。只读javap反汇编核对encode/matches调用链，没有运行encode、matches或测试。官方对应版本源码亦作交叉核对：[Spring Security 6.3.4 BCrypt.java](https://raw.githubusercontent.com/spring-projects/spring-security/6.3.4/crypto/src/main/java/org/springframework/security/crypto/bcrypt/BCrypt.java)（2026-10-08查阅）。

本地字节码核查：字符串转换UTF-8；密钥扩展按18个32位字读取，实际有效输入为首72字节，当前调用链无主动超长拒绝检查。超过边界的尾部不参与摘要是静态代码结论，不是本轮试验结果。默认编码器强度10；不能拿其他Spring Security版本的异常策略套用本项目。

例如25个常见汉字为25码点，通常75个UTF-8字节；单个补充平面Emoji可能为1码点、2个UTF-16单元、4个UTF-8字节；组合序列可能有多个码点。这是编码说明而非测试结果。用户已于12.4-C确认新设置8—64码点且≤72字节、超限拒绝以及旧哈希兼容；统计实现、规范化是否需处理及实际兼容结果仍待设计/验证，未批准自动转换、预哈希或更换算法。

## 12. 第二批实施准备结论

Q01—Q20共20项问题均已完成指定范围确认（2026-10-08 ChatGPT异步确认）。不存在仍未确认总体方向的Q编号；仍有待设计和待执行验证的细节；原性能正文参数已于12.5-C补齐，未发现新的量化验收阻碍。历史20项方向确认本身不等于整体批准；本轮12.5-D已另外批准80项列明范围与优先级，但不代表代码完成或测试通过，已建立v1.0.0文档级正式基线。此前子范围确认不等于整体批准；本轮另有12.5-D整体批准；代码现状、目标及验证状态分别记录。本轮未改代码、未执行测试、已建立文档级正式基线。A/B配置部分更新、缺失保留和默认分离已确认；Q19实现及旧账号验证仍待设计。Q19新密码8—64个Unicode码点、UTF-8最多72字节、超限拒绝及旧哈希登录兼容原则已确认；Q15五字段空字符串/非null、长度及email合理格式规则已确认。剩余为实现设计、正文参数已确认后的测试设计及待执行验证；复合需求只确认对应子范围。所有本轮测试未执行、代码未修复，已建立正式需求基线v1.0.0。

建议先确认Q19剩余技术政策、Q15字段语义和Q13身份规范；先设计测试与数据隔离，再设计Q03/Q05永久失效、Q12/Q15原子配置和错误反馈，按授权实施；Q10最后执行独立库竞争用例并反馈实际缺陷。顺序是建议，不是已批准排期。本轮仅文档同步，没有测试、修复、迁移或Git写操作。

### Q12 横切约束关联说明

Q12横切回归关联：BLOG-F-001、BLOG-F-006、BLOG-F-007、BLOG-F-016、BLOG-F-019、BLOG-F-020、BLOG-F-021、BLOG-F-024、BLOG-F-027、BLOG-F-028、BLOG-F-029、BLOG-F-030、BLOG-F-031、BLOG-F-032、BLOG-F-034、BLOG-F-035、BLOG-F-037、BLOG-F-039。只确认“更新/保存失败不报告成功”这一子范围；其他角色、业务状态、删除/去重等规则仍按各自确认状态。路径已有校验或影响行数检查的不等于必须修改，也不等于已验收。

> 历史记录说明：以下12.4-B/12.4-C的确认数量、版本及候选统计保留为当时事实，不作为12.4-E后的当前状态；规则未被本轮撤销。

## 12.4-C Q19与Q15细化同步（阶段历史；规则继续有效，当前状态以12.5-C为准）

### Q19细化：新密码设置与旧账号验证分离

确认来源：2026-10-08用户ChatGPT请求12.4-C第三节。新密码须同时满足8—64个Unicode码点及UTF-8编码后不超过72字节；超出任一限制明确拒绝，不静默截断。适用于注册、登录状态下修改密码及邮箱验证码重置；前后端口径一致，后端独立校验。保留已有账号及密码哈希，不批量强制重置；已有密码登录验证不得直接套用新密码设置限制，不静默修改、截断或重新编码原密码，成功登录后可建议主动更新。

“Unicode码点”不等于Java String.length()或JavaScript字符串.length的UTF-16单元，也不等于用户肉眼看到的字形数量；组合字符可能有多个码点。码点和UTF-8字节两个条件必须同时满足。本次未增加ASCII限定、密码自动trim、规范化、预哈希、算法迁移或字符组合强度规则。

已找到的新密码设置入口只有注册、登录状态改密、邮箱重置三条；SQL演示哈希初始化不是新增交互设置入口。旧账号登录及修改密码中的当前密码验证保持原哈希验证兼容，不先套用新密码设置长度条件。尤其LoginRequest现有password max64的UTF-16限制应纳入兼容设计审查，不因本次规则再收紧登录。成功登录后可建议用户更新，而非强制更新或自动重编码。

验收目标（待设计、未执行）：新密码7/65个Unicode码点拒绝；8/64码点且≤72个UTF-8字节可通过长度校验；≤64码点但>72字节拒绝；恰好72字节不因字节长度被拒绝；超限不截断，三设置入口一致且后端独立拒绝绕过前端的输入；规则上线不自动改变旧哈希或强制重置，旧密码登录兼容单独回归。均为待设计、未执行验收，不等于满足其他业务条件。
兼容测试需准备受控测试账号/旧哈希夹具，包含多字节及超过新规则的旧密码，验证原哈希保留、原输入验证兼容和新密码设置拒绝超限；不得将BCrypt自身历史等价输入误称为新设置允许截断。实际账号超限数量不能从哈希反推，本轮未调查真实库、未收集原密码。

待设计/核实：Java/JavaScript码点与UTF-8统计、Unicode规范化是否需处理、旧账号兼容回归、现存超限密码数量和错误逐字文案；阈值、码点口径、超限拒绝及保留旧哈希兼容原则已于12.4-C确认，不再待批准。不得自行规范化、转换、预哈希或更换算法。

### Q15细化：五字段规则及请求边界

确认来源：2026-10-08用户ChatGPT请求12.4-C第四节。

| 配置键 | 用途及真实前台位置 | 用户已确认的值规则 | 尚未确认的细节 |
| --- | --- | --- | --- |
| `siteName` | 博客名称；PublicLayout品牌/页脚，Home配置模型 | 必填；保存前去除首尾空格，处理后1—50字符；拒绝空字符串、纯空格、null | 去空格字符集合、长度技术计数、缺失键与默认值 |
| `siteSubtitle` | 博客副标题；PublicLayout页脚 | 允许空字符串；最多100字符；拒绝null | 长度技术计数、缺失键与默认值；未批准自动trim |
| `homeIntro` | 首页简介；Home介绍区域 | 允许空字符串；最多500字符；拒绝null | 长度技术计数、缺失键与默认值；未批准自动trim |
| `aboutContent` | 关于我内容；About正文，当前文本展示 | 允许空字符串；最多5000字符；拒绝null | 长度技术计数、缺失键与默认值；未批准自动trim或富文本扩展 |
| `email` | 公开联系邮箱；About的mailto链接 | 允许空字符串；非空须合理邮箱格式；最多254字符；拒绝null；不是SMTP账号 | 邮箱格式实现、长度技术计数、缺失键与默认值；不复用账号邮箱100上限为站点规则 |

公开GET /api/public/site-config仅返回上述白名单内已有业务配置，不返回未知键或内部秘密；某个键缺失时的返回/补值语义未确认。后台PUT /api/admin/site-config继续仅ADMIN可写，未知键拒绝，按Q12整批全部成功或全部回滚。空字符串是显式值，不等于请求未包含该键；缺失键行为须另行决定。

验收目标（待设计、未执行）：siteName空字符串/纯空格拒绝，首尾空格去除后1—50字符合法，50边界通过、51拒绝；其他四项允许空字符串，分别超100/500/5000/254拒绝，非空email格式不合法拒绝；五项null拒绝；未知键不公开、不允许ADMIN保存，游客写401、USER写403；非法项或保存失败整批回滚不报成功。缺失键处理未定，不编造其通过标准；计数口径待设计/必要确认，以上均未执行。

当前SystemManage.vue加载所有后台配置行并提交整个响应模型，无五字段rules；AdminController.saveConfigs任意Map逐项写入，未见字段校验或整批事务；PublicController全表读取。不存在专门的单字段配置更新API/DTO。现有PUT会遍历请求实际出现的键，未出现的键不参与该次循环；这是代码现状，不是已确认PATCH/完整PUT语义，也不批准缺失必填键请求自动成功。

schema.sql有五键种子值，PublicLayout、Home、About有页面展示回退值；未见统一服务端保存默认值协议。页面回退和SQL种子不等于用户批准空字符串转默认值。config_value为可NULL的TEXT，数据库不提供本次字段上限及格式校验。没有本轮读取真实数据，不能保证现存记录合法；未来可只读盘点未知键、null、空/空格siteName、超长和非法email，修数据须另获授权，不自动删键、补默认值或截断历史内容。

待设计或确认：单字段/完整批量语义、缺失键处理、缺失与空字符串区分、默认值策略、字段长度技术计数、邮箱格式实现、siteName去空格的具体字符集合、现有数据兼容及失败反馈实现。不得擅自将未定请求语义定案；其他四项未获准自动trim。

### 细化后的确认与验证状态

本次为Q15/Q19既有编号的补充，不新增Q编号或BLOG需求编号。指定确认仍9项，其他11项不批准；前两批记录保留。实现未修复，验收未执行，正式基线未建立；复合需求其他子范围、优先级和整体批准仍待确认。

本次修订版本：v0.3；SRS/清单/流程v0.4，评审/影响/矩阵/决策/基线准备v0.3。旧版本行保留为历史，不作为当前规则。

## 12.4-D 六项业务范围同步（历史快照，2026-10-08）

确认日期：2026-10-08；来源：用户ChatGPT对话12.4-D粘贴请求第三节六项明确决定。属于指定业务范围确认，不是线下会议、代码实施许可、测试通过或整体基线批准。

当前已确认15项指定范围：Q01、Q02、Q03、Q05、Q06、Q07、Q08、Q09、Q10、Q11、Q12、Q14、Q15、Q19、Q20。仍待确认5项：Q04、Q13、Q16、Q17、Q18。已确认子范围不代表80条复合需求整体或优先级获批；代码现状、目标及验证状态分别记录。本轮未改代码、未执行测试、未建立正式基线。Q15缺失键/默认等残余和Q19实现统计/兼容验证仍按12.4-C保留。

| 问题 | 用户确认的本期范围 | 当前行为与差异 | 待设计/验证及暂缓范围 |
| --- | --- | --- | --- |
| Q06 首页与列表置顶范围 | 本期保留文章列表置顶、is_top字段及后台置顶设置；首页不新增独立置顶区，独立区域仅为后续版本候选，不纳入本期必需验收。 | Home.vue只调用latest(5)/hot(3)；ArticleManage.vue保存isTop；pagePublicArticles按is_top DESC、published_at DESC；featured接口保留但首页未调用。 范围与现有调用一致，未发现需为Q06新增首页区域的缺陷；同值排序另按Q09，不删除现有接口/字段。 | 置顶同值次级字段随Q09设计；未批准首页新区域或删除featured接口。 首页独立置顶区域本期暂缓，后续候选，非永久取消。 |
| Q07 筛选、URL与浏览器历史一致性 | 筛选控件、当前URL有效查询参数和结果一致；合法直达URL、刷新及前进/后退应恢复条件与结果；不要求每次筛选都新增历史记录。 | ArticleList.vue仅setup从route.query读page/categoryId/tagId/keyword；watch本地page/categoryId/tagId并router.replace、load；search也replace并load；未watch route.query。router复用/articles组件时本地状态未见回填。 静态可定位同组件历史导航缺少query→控件/查询同步路径，不能将代码缺口写成已实际复现失败；首次有效URL有初始化代码。请求交错风险待验证。 | 非法/多值参数纠正、有效性判断、查询参数规范化、组合冲突边界及是否重置越界页待设计或必要确认；现有AND组合是代码证据，不将本次Q07当作新增组合规则批准。 无新延期的基础同步目标；不要求逐操作新增浏览器历史。 |
| Q09 现有统计口径与确定性排序 | 保留当前阅读、趋势及热门主要指标口径；主指标相同记录补确定性次级排序；本期不新增访客去重或独立发布事件历史系统。具体次级字段未批准。 | getPublicArticle确认未删PUBLISHED后事务view_count+1，后台详情不计阅读；热门view_count/like_count/published_at依次降序，无周期过滤；趋势近7个日期按当前PUBLISHED且DATE(published_at)统计，逻辑删除过滤。首次发布设published_at、持续发布编辑保留、撤回置null、再次发布新时间。 文章列表is_top/published_at DESC；最新/featured/喜欢按published_at DESC；相邻published_at ASC；后台updated_at DESC；这些排序末尾无唯一决胜字段，完全同值顺序未由代码确定。不宣称已有不稳定结果实测。 | 末级唯一id及其方向可作设计建议，非用户已批准字段组合；主指标不变，不将SQL默认返回顺序当稳定保证。 本期不新增访客去重、独立发布事件历史系统；不是取消阅读量/趋势。 |
| Q11 公开评论展示与图片头像边界 | 本期保留公开评论展示及首字头像；评论分页、评论图片头像暂缓至后续候选；不取消个人中心头像上传或既有留言分页。 | approvedComments要求发布文章，取全部有效APPROVED，created_at DESC无分页；ArticleDetail.vue渲染列表并取nickname.slice(0,1)，未用VO.avatar图片。Profile与上传接口独立存在。 现状符合本期展示范围，不因没有分页/图片头像认定本期缺陷；性能风险仍需记录，不据此自动增加分页开发。 | 性能测试规模归Q17；首字Unicode改进未在本次批准，不把现有slice(0,1)改称码点截取。 评论区域分页与图片头像增强本期暂缓，仅后续版本候选，非永久取消。个人中心头像上传明确保留。 |
| Q14 文章逻辑删除及历史关联政策 | 保留现有文章逻辑删除、评论/点赞历史留存及文章标签关联清理；本期不增回收站或独立物理清理。确认核实范围，不批准未知清理行为。 | deleteArticle事务内先删当前文章blog_article_tag，再articleMapper.deleteById；Article.deleted有@TableLogic，配置deleted=1为删除值。未删除blog_comment/blog_article_like。标准ArticleMapper查询过滤deleted，公开只PUBLISHED；本人likes关系留存但文章结果排除已删。文章统计排除已删；评论统计按有效评论本身，不联表排除已删文章。 代码和SQL逻辑/物理边界一致：逻辑删不触发CASCADE，标签关系是显式物理删除。保留评论可能在后台显示“文章已删除”，不等于记录丢失。未发现需虚构回收站修复。 | 实际库结构/历史数据未核验，交付迁移归Q18；物理清理不在本轮批准。 本期不新增回收站及独立物理清理机制；未来若改变需真实需求变更。 |
| Q20 评论留言永久去重与明确失败提示 | 保留评论和留言永久去重；重复提交明确失败、不报成功；本期不增加限时去重或依据审核状态改变规则。 | schema.sql评论唯一键(article_id,user_id,content)，留言(user_id,content)，均不含status/deleted/时间。InteractionService保存content.trim()，捕获DuplicateKeyException分别报“请勿重复提交相同评论/留言”；页面catch显示error.message，成功提示仅在await返回成功后；全局BusinessException为HTTP400/Result code4000。 代码已有永久唯一约束定义、业务异常和前端失败提示，未见需要虚构限时去重修复。运行库是否实际具有这些键本轮未查询；重复/状态边界待回归，不等于测试通过。 | 已有数据库约束落地情况与collation等价输入验证未执行；不批准额外文本转换或全用户共用去重。 不引入限时去重、按审核状态释放机制；未来调整需真实变更审批。 |

| 决策 | 完整关联需求编号（仅对应子范围） |
| --- | --- |
| Q06 | BLOG-F-009、BLOG-F-030 |
| Q07 | BLOG-F-010 |
| Q09 | BLOG-F-009、BLOG-F-010、BLOG-F-013、BLOG-F-017、BLOG-F-025、BLOG-F-026、BLOG-F-028、BLOG-F-030、BLOG-BR-013、BLOG-BR-016、BLOG-BR-017 |
| Q11 | BLOG-F-018、BLOG-NF-008 |
| Q14 | BLOG-F-029、BLOG-F-031、BLOG-BR-015、BLOG-BR-010、BLOG-F-012、BLOG-F-017、BLOG-F-025 |
| Q20 | BLOG-F-019、BLOG-F-024、BLOG-BR-020、BLOG-NF-010、BLOG-NF-014 |

### 验收准备（全部待设计、未执行）

- **Q06**：置顶已发布文章在列表优先，草稿及已删置顶不公开；后台置顶保存/取消仍有效；首页仍最新/热门，无独立置顶区不判失败；Q12保存失败不报成功。 测试用例ID待分配，未执行。
- **Q07**：合法带page/categoryId/tagId/keyword直达及刷新恢复控件、请求、结果；在已有不同有效URL历史项之间前进/后退正确回填并查询；清筛选恢复当前有效条件；replace可保留，不以每次产生历史项判定；结果按当前有效条件显示。 测试用例ID待分配，未执行。
- **Q09**：成功有效公开详情每请求+1，列表/邻文/后台详情不+1；草稿/已删详情不计阅读；热门保留现有全站有效文章三指标顺序，无新统计周期；趋势核对当前发布集/撤回/再发；构造全部主排序指标相同数据，重复查询固定数据集及翻页/相邻导航顺序确定，不改变原主优先级；不要求唯一访客或事件历史。 测试用例ID待分配，未执行。
- **Q11**：有效文章只显示未删APPROVED；待审/拒绝不可见，禁用用户历史通过评论保留；显示名首字方式保留；无分页/图片头像不判本期功能失败；个人中心本地头像上传、预览/取消、全局同步保持，需另按原需求回归。 测试用例ID待分配，未执行。
- **Q14**：删除后blog_article.deleted=1，公开/后台常规文章列表、详情、热门/最新、本人喜欢结果和文章统计排除；评论/点赞行保留、标签关联清空；已删文章公开评论接口因文章不可访问而拒绝，不把历史通过评论继续公开为目标；评论总数不误要求随文章删除减少；分类删除按有效文章引用检查，物理外键RESTRICT仍有效；Q12删除失败不报成功。 测试用例ID待分配，未执行。
- **Q20**：同用户同文章相同有效存储评论重复拒绝；同用户同内容留言重复拒绝；旧项待审/通过/拒绝/逻辑删除均不解除唯一约束；不同用户相同内容或同用户不同有效文章相同评论不因该组合键被禁止；重复失败页面明确错误、不清空为成功、不提示待审成功，不增加记录/公开评论数；与Q12保持一致。 测试用例ID待分配，未执行。

### 保留与暂缓边界

评论首字头像与个人中心本地上传独立，后者继续保留；留言分页/后台评论分页不取消。独立首页置顶、评论分页/图片、访客去重、独立发布事件历史、回收站/独立物理清理、限时或状态关联去重本期不新增，后续候选不等于永久取消。Q12实际业务失败不报告成功仍适用于所有相关保存/删除/提交。

仍待确认仅Q04/Q13/Q16/Q17/Q18；Q07非法参数和请求同步设计、Q09确定性末级字段、Q15缺失键/default等残余和Q19实现/旧哈希兼容验证分别保留，不凭本轮全量批准。


### Q06 首页与列表置顶范围：影响分析补充

| 分析项 | 本轮结论 |
| --- | --- |
| 确认目标 | 本期保留文章列表置顶、is_top字段及后台置顶设置；首页不新增独立置顶区，独立区域仅为后续版本候选，不纳入本期必需验收。 |
| 当前实现及依据 | Home.vue只调用latest(5)/hot(3)；ArticleManage.vue保存isTop；pagePublicArticles按is_top DESC、published_at DESC；featured接口保留但首页未调用。 [Home.vue](../../blog-web/src/views/public/Home.vue)、[ArticleList.vue](../../blog-web/src/views/public/ArticleList.vue)、[ArticleManage.vue](../../blog-web/src/views/admin/ArticleManage.vue)、[ArticleCard.vue](../../blog-web/src/components/ArticleCard.vue)、[ArticleServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/ArticleServiceImpl.java)、[PublicArticleController.java](../../blog-server/src/main/java/com/example/blog/controller/PublicArticleController.java)、[Article.java](../../blog-server/src/main/java/com/example/blog/entity/Article.java) |
| 当前差异 | 范围与现有调用一致，未发现需为Q06新增首页区域的缺陷；同值排序另按Q09，不删除现有接口/字段。 |
| 前端影响 | 保留现有页面与权限；按上述范围设计回归，涉及排序时只改变同值顺序，不扩大业务范围。 |
| 后端影响 | 复用当前接口和服务；维持现有范围，Q06本身无新增开发；Q09稳定排序需另行设计。 |
| 数据库影响 | 保留blog_article.is_top及数据；无需为本决策DDL或清除置顶值。 |
| 接口影响 | GET /api/public/articles、/featured、/latest、/hot及POST/PUT /api/admin/articles契约保留，不新增首页区域接口。 |
| 风险 | 不要将ArticleCard的featured样式属性误称置顶数据源；保留列表置顶不能误变为首页按置顶排序。 |
| 测试目标 | 置顶已发布文章在列表优先，草稿及已删置顶不公开；后台置顶保存/取消仍有效；首页仍最新/热门，无独立置顶区不判失败；Q12保存失败不报成功。 正式测试用例待分配ID；未编写、未执行。 |
| 建议实施顺序 | 维持现有范围，Q06本身无新增开发；Q09稳定排序需另行设计。 |
| 本期边界与待定细节 | 首页独立置顶区域本期暂缓，后续候选，非永久取消。 置顶同值次级字段随Q09设计；未批准首页新区域或删除featured接口。 |
| 关联需求 | BLOG-F-009、BLOG-F-030；仅确认上述范围，不自动批准整条复合需求。 |

### Q07 筛选、URL与浏览器历史一致性：影响分析补充

| 分析项 | 本轮结论 |
| --- | --- |
| 确认目标 | 筛选控件、当前URL有效查询参数和结果一致；合法直达URL、刷新及前进/后退应恢复条件与结果；不要求每次筛选都新增历史记录。 |
| 当前实现及依据 | ArticleList.vue仅setup从route.query读page/categoryId/tagId/keyword；watch本地page/categoryId/tagId并router.replace、load；search也replace并load；未watch route.query。router复用/articles组件时本地状态未见回填。 [ArticleList.vue](../../blog-web/src/views/public/ArticleList.vue)、[index.js](../../blog-web/src/router/index.js)、[blog.js](../../blog-web/src/api/blog.js)、[PublicArticleController.java](../../blog-server/src/main/java/com/example/blog/controller/PublicArticleController.java)、[ArticleQuery.java](../../blog-server/src/main/java/com/example/blog/dto/ArticleQuery.java)、[ArticleServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/ArticleServiceImpl.java) |
| 当前差异 | 静态可定位同组件历史导航缺少query→控件/查询同步路径，不能将代码缺口写成已实际复现失败；首次有效URL有初始化代码。请求交错风险待验证。 |
| 前端影响 | 需后续设计URL回填、查询更新与请求竞争处理；本轮不修复。 |
| 后端影响 | 复用当前接口和服务；需后续设计query回填与请求更新策略，代码修改另获授权；不是全部筛选功能未实现。 |
| 数据库影响 | 既有blog_article/category/tag/article_tag查询复用，无DDL或数据修改要求。 |
| 接口影响 | GET /api/public/articles使用page、size、keyword、categoryId、tagId；前端每页8；不新增接口。 |
| 风险 | 双向watch可能回环/重复加载；快速导航旧响应可能覆盖新结果；URL条件与认证redirect分开，不能因此放开保护接口。 |
| 测试目标 | 合法带page/categoryId/tagId/keyword直达及刷新恢复控件、请求、结果；在已有不同有效URL历史项之间前进/后退正确回填并查询；清筛选恢复当前有效条件；replace可保留，不以每次产生历史项判定；结果按当前有效条件显示。 正式测试用例待分配ID；未编写、未执行。 |
| 建议实施顺序 | 需后续设计query回填与请求更新策略，代码修改另获授权；不是全部筛选功能未实现。 |
| 本期边界与待定细节 | 无新延期的基础同步目标；不要求逐操作新增浏览器历史。 非法/多值参数纠正、有效性判断、查询参数规范化、组合冲突边界及是否重置越界页待设计或必要确认；现有AND组合是代码证据，不将本次Q07当作新增组合规则批准。 |
| 关联需求 | BLOG-F-010；仅确认上述范围，不自动批准整条复合需求。 |

### Q09 现有统计口径与确定性排序：影响分析补充

| 分析项 | 本轮结论 |
| --- | --- |
| 确认目标 | 保留当前阅读、趋势及热门主要指标口径；主指标相同记录补确定性次级排序；本期不新增访客去重或独立发布事件历史系统。具体次级字段未批准。 |
| 当前实现及依据 | getPublicArticle确认未删PUBLISHED后事务view_count+1，后台详情不计阅读；热门view_count/like_count/published_at依次降序，无周期过滤；趋势近7个日期按当前PUBLISHED且DATE(published_at)统计，逻辑删除过滤。首次发布设published_at、持续发布编辑保留、撤回置null、再次发布新时间。 [ArticleServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/ArticleServiceImpl.java)、[AdminController.java](../../blog-server/src/main/java/com/example/blog/controller/AdminController.java)、[Article.java](../../blog-server/src/main/java/com/example/blog/entity/Article.java)、[PublicArticleController.java](../../blog-server/src/main/java/com/example/blog/controller/PublicArticleController.java)、[Home.vue](../../blog-web/src/views/public/Home.vue)、[ArticleList.vue](../../blog-web/src/views/public/ArticleList.vue)、[ArticleDetail.vue](../../blog-web/src/views/public/ArticleDetail.vue)、[Profile.vue](../../blog-web/src/views/public/Profile.vue)、[AdminDashboard.vue](../../blog-web/src/views/admin/AdminDashboard.vue)、[ArticleManage.vue](../../blog-web/src/views/admin/ArticleManage.vue) |
| 当前差异 | 文章列表is_top/published_at DESC；最新/featured/喜欢按published_at DESC；相邻published_at ASC；后台updated_at DESC；这些排序末尾无唯一决胜字段，完全同值顺序未由代码确定。不宣称已有不稳定结果实测。 |
| 前端影响 | 保留现有页面与权限；按上述范围设计回归，涉及排序时只改变同值顺序，不扩大业务范围。 |
| 后端影响 | 文章相关查询需在保持主要指标的前提下补确定性次级排序；具体字段及方向待设计。 |
| 数据库影响 | 复用view_count、like_count、is_top、published_at、updated_at；不增访问去重表或发布事件表，是否需索引优化后续设计，不迁移数据。 |
| 接口影响 | 保留文章列表/详情/latest/hot/featured/adjacent、本人likes、后台列表和/dashboard/publish-trend路径；同值顺序可能改变，返回结构不必变。 |
| 风险 | 稳定排序必须与相邻顺序一致；动态数据变化本身不保证跨请求快照一致，验收固定数据集。时区/日期以现有服务LocalDate.now及SQL DATE为现状，不自行增新时区规则。Q12统计查询失败不可显示真实0。 |
| 测试目标 | 成功有效公开详情每请求+1，列表/邻文/后台详情不+1；草稿/已删详情不计阅读；热门保留现有全站有效文章三指标顺序，无新统计周期；趋势核对当前发布集/撤回/再发；构造全部主排序指标相同数据，重复查询固定数据集及翻页/相邻导航顺序确定，不改变原主优先级；不要求唯一访客或事件历史。 正式测试用例待分配ID；未编写、未执行。 |
| 建议实施顺序 | 确定性次级排序需后续设计/补齐；统计公式不重建，未修复未执行。 |
| 本期边界与待定细节 | 本期不新增访客去重、独立发布事件历史系统；不是取消阅读量/趋势。 末级唯一id及其方向可作设计建议，非用户已批准字段组合；主指标不变，不将SQL默认返回顺序当稳定保证。 |
| 关联需求 | BLOG-F-009、BLOG-F-010、BLOG-F-013、BLOG-F-017、BLOG-F-025、BLOG-F-026、BLOG-F-028、BLOG-F-030、BLOG-BR-013、BLOG-BR-016、BLOG-BR-017；仅确认上述范围，不自动批准整条复合需求。 |

### Q11 公开评论展示与图片头像边界：影响分析补充

| 分析项 | 本轮结论 |
| --- | --- |
| 确认目标 | 本期保留公开评论展示及首字头像；评论分页、评论图片头像暂缓至后续候选；不取消个人中心头像上传或既有留言分页。 |
| 当前实现及依据 | approvedComments要求发布文章，取全部有效APPROVED，created_at DESC无分页；ArticleDetail.vue渲染列表并取nickname.slice(0,1)，未用VO.avatar图片。Profile与上传接口独立存在。 [ArticleDetail.vue](../../blog-web/src/views/public/ArticleDetail.vue)、[InteractionServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/InteractionServiceImpl.java)、[CommentVO.java](../../blog-server/src/main/java/com/example/blog/vo/CommentVO.java)、[Profile.vue](../../blog-web/src/views/public/Profile.vue)、[auth.js](../../blog-web/src/store/auth.js)、[UserProfileController.java](../../blog-server/src/main/java/com/example/blog/controller/UserProfileController.java)、[AvatarStorageServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/AvatarStorageServiceImpl.java) |
| 当前差异 | 现状符合本期展示范围，不因没有分页/图片头像认定本期缺陷；性能风险仍需记录，不据此自动增加分页开发。 |
| 前端影响 | 保留现有页面与权限；按上述范围设计回归，涉及排序时只改变同值顺序，不扩大业务范围。 |
| 后端影响 | 复用当前接口和服务；维持现有范围，主要记录和回归；本期不开发评论分页/图片渲染。 |
| 数据库影响 | blog_comment审核/逻辑删除及sys_user.avatar保留，不删除头像路径或文件；无需本决策DDL。 |
| 接口影响 | GET /api/public/articles/{id}/comments仍列表；POST /api/users/me/avatar不取消、不改权限；不擅自新增公开评论page/size契约。 |
| 风险 | 公开评论无界返回、大量评论渲染耗时/载荷风险；Q17验收数据规模待定，不声称性能达标，也不自动增开发任务。 |
| 测试目标 | 有效文章只显示未删APPROVED；待审/拒绝不可见，禁用用户历史通过评论保留；显示名首字方式保留；无分页/图片头像不判本期功能失败；个人中心本地头像上传、预览/取消、全局同步保持，需另按原需求回归。 正式测试用例待分配ID；未编写、未执行。 |
| 建议实施顺序 | 维持现有范围，主要记录和回归；本期不开发评论分页/图片渲染。 |
| 本期边界与待定细节 | 评论区域分页与图片头像增强本期暂缓，仅后续版本候选，非永久取消。个人中心头像上传明确保留。 性能测试规模归Q17；首字Unicode改进未在本次批准，不把现有slice(0,1)改称码点截取。 |
| 关联需求 | BLOG-F-018、BLOG-NF-008；仅确认上述范围，不自动批准整条复合需求。 |

### Q14 文章逻辑删除及历史关联政策：影响分析补充

| 分析项 | 本轮结论 |
| --- | --- |
| 确认目标 | 保留现有文章逻辑删除、评论/点赞历史留存及文章标签关联清理；本期不增回收站或独立物理清理。确认核实范围，不批准未知清理行为。 |
| 当前实现及依据 | deleteArticle事务内先删当前文章blog_article_tag，再articleMapper.deleteById；Article.deleted有@TableLogic，配置deleted=1为删除值。未删除blog_comment/blog_article_like。标准ArticleMapper查询过滤deleted，公开只PUBLISHED；本人likes关系留存但文章结果排除已删。文章统计排除已删；评论统计按有效评论本身，不联表排除已删文章。 [ArticleServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/ArticleServiceImpl.java)、[Article.java](../../blog-server/src/main/java/com/example/blog/entity/Article.java)、[ArticleMapper.java](../../blog-server/src/main/java/com/example/blog/mapper/ArticleMapper.java)、[AdminController.java](../../blog-server/src/main/java/com/example/blog/controller/AdminController.java)、[CommentAdminServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/CommentAdminServiceImpl.java)、[application.yml](../../blog-server/src/main/resources/application.yml)、[schema.sql](../../blog-server/src/main/resources/schema.sql)、[ArticleManage.vue](../../blog-web/src/views/admin/ArticleManage.vue)、[CommentManage.vue](../../blog-web/src/views/admin/CommentManage.vue)、[Profile.vue](../../blog-web/src/views/public/Profile.vue) |
| 当前差异 | 代码和SQL逻辑/物理边界一致：逻辑删不触发CASCADE，标签关系是显式物理删除。保留评论可能在后台显示“文章已删除”，不等于记录丢失。未发现需虚构回收站修复。 |
| 前端影响 | 保留现有页面与权限；按上述范围设计回归，涉及排序时只改变同值顺序，不扩大业务范围。 |
| 后端影响 | 复用当前接口和服务；维持现有已核实政策，准备删除回归；影响行数与失败边界由Q12，不能据保留政策宣称所有删除故障已解决。 |
| 数据库影响 | blog_article标记删除；仅blog_article_tag关系显式清理；评论/点赞不物理删。分类逻辑删除检查与物理RESTRICT区别保留；不读/改真实库、不批量清历史。 |
| 接口影响 | DELETE /api/admin/articles/{id}及既有查询契约保留；无恢复/物理清理API。公开文章或评论读取已删文章报业务错误；不误称所有统计都排除其历史评论。 |
| 风险 | 保留关系不等于可公开访问文章；已删slug唯一约束和FK继续存在。对已删文章后台评论审核更新文章计数受逻辑过滤可能0行是需Q12/Q10另设计验证的风险，不批准新清理或新审核禁令。 |
| 测试目标 | 删除后blog_article.deleted=1，公开/后台常规文章列表、详情、热门/最新、本人喜欢结果和文章统计排除；评论/点赞行保留、标签关联清空；已删文章公开评论接口因文章不可访问而拒绝，不把历史通过评论继续公开为目标；评论总数不误要求随文章删除减少；分类删除按有效文章引用检查，物理外键RESTRICT仍有效；Q12删除失败不报成功。 正式测试用例待分配ID；未编写、未执行。 |
| 建议实施顺序 | 维持现有已核实政策，准备删除回归；影响行数与失败边界由Q12，不能据保留政策宣称所有删除故障已解决。 |
| 本期边界与待定细节 | 本期不新增回收站及独立物理清理机制；未来若改变需真实需求变更。 实际库结构/历史数据未核验，交付迁移归Q18；物理清理不在本轮批准。 |
| 关联需求 | BLOG-F-029、BLOG-F-031、BLOG-BR-015、BLOG-BR-010、BLOG-F-012、BLOG-F-017、BLOG-F-025；仅确认上述范围，不自动批准整条复合需求。 |

### Q20 评论留言永久去重与明确失败提示：影响分析补充

| 分析项 | 本轮结论 |
| --- | --- |
| 确认目标 | 保留评论和留言永久去重；重复提交明确失败、不报成功；本期不增加限时去重或依据审核状态改变规则。 |
| 当前实现及依据 | schema.sql评论唯一键(article_id,user_id,content)，留言(user_id,content)，均不含status/deleted/时间。InteractionService保存content.trim()，捕获DuplicateKeyException分别报“请勿重复提交相同评论/留言”；页面catch显示error.message，成功提示仅在await返回成功后；全局BusinessException为HTTP400/Result code4000。 [schema.sql](../../blog-server/src/main/resources/schema.sql)、[InteractionServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/InteractionServiceImpl.java)、[GlobalExceptionHandler.java](../../blog-server/src/main/java/com/example/blog/config/GlobalExceptionHandler.java)、[Comment.java](../../blog-server/src/main/java/com/example/blog/entity/Comment.java)、[Message.java](../../blog-server/src/main/java/com/example/blog/entity/Message.java)、[CommentMapper.java](../../blog-server/src/main/java/com/example/blog/mapper/CommentMapper.java)、[MessageMapper.java](../../blog-server/src/main/java/com/example/blog/mapper/MessageMapper.java)、[ArticleDetail.vue](../../blog-web/src/views/public/ArticleDetail.vue)、[Message.vue](../../blog-web/src/views/public/Message.vue)、[request.js](../../blog-web/src/utils/request.js) |
| 当前差异 | 代码已有永久唯一约束定义、业务异常和前端失败提示，未见需要虚构限时去重修复。运行库是否实际具有这些键本轮未查询；重复/状态边界待回归，不等于测试通过。 |
| 前端影响 | 保留现有页面与权限；按上述范围设计回归，涉及排序时只改变同值顺序，不扩大业务范围。 |
| 后端影响 | 复用当前接口和服务；维持现有实现、设计边界回归；只有后续测试暴露反馈差异才另授权修复，不凭描述断言有bug。 |
| 数据库影响 | 保留两个唯一键；不得加时间/status/deleted以释放去重。数据库按utf8mb4_unicode_ci比较，不能声称逐字节唯一，也不新增客户端规范化；不读取真实库。 |
| 接口影响 | POST /api/articles/{id}/comments、POST /api/messages及既有Result错误结构保留；错误逐字文案现有可作证据，不称用户逐字批准。 |
| 风险 | 数据库collation可能将大小写/重音等视为相同；content.trim现状保留，具体等价边界需测试设计，不擅改规则。用户无法自行编辑/删除现有内容，不新增此能力。 |
| 测试目标 | 同用户同文章相同有效存储评论重复拒绝；同用户同内容留言重复拒绝；旧项待审/通过/拒绝/逻辑删除均不解除唯一约束；不同用户相同内容或同用户不同有效文章相同评论不因该组合键被禁止；重复失败页面明确错误、不清空为成功、不提示待审成功，不增加记录/公开评论数；与Q12保持一致。 正式测试用例待分配ID；未编写、未执行。 |
| 建议实施顺序 | 维持现有实现、设计边界回归；只有后续测试暴露反馈差异才另授权修复，不凭描述断言有bug。 |
| 本期边界与待定细节 | 不引入限时去重、按审核状态释放机制；未来调整需真实变更审批。 已有数据库约束落地情况与collation等价输入验证未执行；不批准额外文本转换或全用户共用去重。 |
| 关联需求 | BLOG-F-019、BLOG-F-024、BLOG-BR-020、BLOG-NF-010、BLOG-NF-014；仅确认上述范围，不自动批准整条复合需求。 |


### 本轮修订记录

| 日期 | 版本 | 修订内容 | 状态 |
| --- | --- | --- | --- |
| 2026-10-08 | v0.4 | 同步用户异步确认的Q06、Q07、Q09、Q11、Q14、Q20；保留此前确认、未定细节及实现差异 | 评审草稿，未建立正式基线；未修改代码、未执行测试 |

## 12.4-E 最后五项指定范围确认（历史快照，2026-10-08）

确认来源：用户本次粘贴请求第三至七节，通过ChatGPT异步明确同意推荐方向及列明边界；无虚构会议、签名或审批。Q01—Q20共20项问题均已完成指定范围确认（2026-10-08 ChatGPT异步确认）。不存在仍未确认总体方向的Q编号；仍有待设计和待执行验证的细节；原性能正文参数已于12.5-C补齐，未发现新的量化验收阻碍。20项方向确认不等于80项复合需求整体获批、优先级获批、代码完成或测试通过，未建立v1.0.0正式基线。

| 问题 | 已确认目标 | 当前实现差异 | 仍需设计/补充 |
| --- | --- | --- | --- |
| Q04 SMTP真实收件与密码重置闭环 | 本期验收必须覆盖请求验证码、真实SMTP发送、真实邮箱收件、输入验证码、设置新密码和新密码登录；接口成功不等于收件。错误/过期/已使用验证码必须验证；无可用环境如实记录未执行或环境受限，不免除真实收件目标，不公开凭据。 | 真实投递/收件/闭环均未执行，发送成功后数据库提交失败可能产生已送达但不可用验证码，事务不能撤回邮件；继续保留Q05旧JWT失效及Q19新密码与旧哈希兼容缺口，不伪造新缺陷。 | 测试SMTP服务/收件邮箱、等待上限和脱敏证据形式、邮件与数据库提交失败后的重试反馈待设计/补充；不要求新增投递队列。 |
| Q13 邮箱与用户名规范化及历史账号兼容 | 新注册/真实修改邮箱入口去首尾空格并统一小写，唯一性按规范化结果判断；注册/修改用户名去首尾空格，保留大小写，不新增字符集限制，最大50沿用Q01。历史账号不自动合并、批量改值、删除或重命名；冲突先审计再提交方案。 | 注册与找回邮箱口径不统一；前置DTO或前端邮箱校验可能先拒绝带空格输入，需设计先规范化再校验路径。用户名已trim但DTO对原始长度先校验，Q01长度计数和登录输入/历史兼容仍需设计。当前库是否冲突未审计。 | 登录用户名输入处理、找回输入与校验顺序、Locale无关小写建议、去空格集合、用户名大小写唯一性和历史冲突方案待设计/必要确认；没有邮箱登录/改邮箱功能，本次不批准新增。 |
| Q16 初始化数据与统计计数一致性 | 在独立干净测试数据库执行初始化并核对表、种子和冗余统计；点赞/评论数量应符合现有已确认统计口径，发现不一致后另授权修SQL，不改真实业务库。 | 第二篇comment_count种子与APPROVED统计存在明确脚本层面差异；实际运行库未读取，不能推断线上计数。重复执行UPSERT不等同初始化/计数自动校准，不能覆盖已有库。 | 独立库名称及防误连方案、脚本硬编码CREATE/USE隔离执行方式、夹具/查询清单及计数核验实测待设计/执行；不批准自动校准真实库。 |
| Q17 PC浏览器验收与性能方案 | 本期主交付PC Web，覆盖桌面Chrome和Edge功能验收；390px等窄屏专项增强暂缓但不破坏既有响应式。必须建立可执行性能验收方案，明确环境、对象、步骤、采样和判定；具体阈值、硬件、并发、工具未批准。 | 桌面兼容方向已定但实际版本、分辨率待记录；性能环境/接口/采样/阈值需补充确认，不宣称达标；窄屏不再本期必需验收项，保留样式。 | 设备/OS/JDK/Node/MySQL实版本、桌面分辨率、数据规模、测试接口、并发、预热、计时边界、请求数/轮次、P95及失败率阈值、测试工具与报告证据待补充/批准；不强制已有草案数值。 |
| Q18 数据库初始化、升级与备份恢复交付 | 交付包含空库初始化、已有库升级风险与操作、备份恢复说明；新库与旧库不能无条件覆盖混用；恢复验证独立环境且同步数据库avatar路径与本地头像文件；本期不强制自动迁移框架，不声称已恢复。 | 已有运行/初始化概述不能替代可核对的升级检查和完整恢复文档；v2重复ADD会失败，CREATE IF NOT EXISTS不升级旧表；UPSERT可覆盖演示账号/配置，恢复仅数据库将遗漏图片。 | 旧库来源/当前结构、迁移适用判定、独立目标库与恢复机器、备份工具/一致性时点/头像快照方案、具体恢复检查和回退步骤待设计；不批准真实库执行。 |

| 问题 | 完整关联需求编号（仅对应子范围） |
| --- | --- |
| Q04 | BLOG-F-006、BLOG-F-007、BLOG-BR-007、BLOG-BR-008 |
| Q13 | BLOG-F-001、BLOG-F-002、BLOG-F-006、BLOG-F-007、BLOG-F-020、BLOG-BR-002 |
| Q16 | BLOG-NF-007、BLOG-BR-018、BLOG-BR-022 |
| Q17 | BLOG-NF-008、BLOG-NF-009、BLOG-NF-011、BLOG-NF-012 |
| Q18 | BLOG-NF-015、BLOG-NF-016 |

### 待设计验收场景

- Q04：独立测试库和受控真实测试邮箱：保存发送时间和脱敏收件证据，正常码完成重置并用新密码登录；错误、5分钟到期、单次消费、旧码、新旧密码、禁用状态与发送失败回归；两个路径旧Token失效按Q05、新设置按Q19。没有SMTP环境则标未执行/受限，不写通过。 正式用例待分配ID；未执行。
- Q13：新注册混合大小写/首尾空格邮箱保存规范值，变体重复拒绝；注册与找回相同身份邮箱路径一致；合法用户名首尾空格去除，内部字符及大小写不擅改，50上限回归；旧账号数据及哈希不自动变化，冲突仅登记/方案待批准。邮箱修改不存在，不虚构其执行测试；未来获准新增入口再适用。 正式用例待分配ID；未执行。
- Q16：独立空库执行经隔离设计的脚本，核对10表及种子数量、状态、FK/唯一约束，逐篇比对like_count与点赞行、comment_count与有效APPROVED行；留存环境/步骤/预期/实测差异；计数修正和复验需后续授权，不预填通过。 正式用例待分配ID；未执行。
- Q17：后续在记录实际版本的桌面Chrome/Edge完成注册登录、文章筛选详情、评论留言、资料/头像、后台编辑/审核等场景；性能先批准参数表再在独立数据环境采集与判定，未执行记未执行；390px专项未完成不判本期必需功能失败，不主动破坏既有布局。 正式用例待分配ID；未执行。
- Q18：后续分别编写新空库、旧库预检查/备份/适用迁移/回退、恢复说明；独立环境验证表/记录/约束/配置与头像文件可读，记录实际步骤与证据；恢复不得覆盖真实库，旧库升级先结构审计；文档完成和演练执行分别登记。 正式用例待分配ID；未执行。

Q04同时遵守Q05旧JWT失效与Q19新密码8—64个Unicode码点、UTF-8最多72字节和旧哈希兼容；Q13同时遵守Q01最大50，不将身份邮箱规范化自动扩展到site-config.email或SMTP账号。邮箱修改/邮箱登录并非现有功能，本轮不新增。

Q17量化性能参数仍未批准；1000篇/10并发/100次/P95≤2秒及两种桌面分辨率仅原草案，不是已达到结果或正式指标。Q10每场景2请求、10轮仍是最小竞争测试，不与性能验收混同。390px专项增强本期暂缓，现有响应式保留。Q18新库、旧库、恢复说明与实测分别管理，自动迁移框架非强制。Q12失败不报成功及配置原子保存继续有效。


### Q04 SMTP真实收件与密码重置闭环：影响分析

| 分析项 | 静态分析与后续工作 |
| --- | --- |
| 用户确认目标 | 本期验收必须覆盖请求验证码、真实SMTP发送、真实邮箱收件、输入验证码、设置新密码和新密码登录；接口成功不等于收件。错误/过期/已使用验证码必须验证；无可用环境如实记录未执行或环境受限，不免除真实收件目标，不公开凭据。 |
| 当前依据 | ForgotPassword.vue发送成功后60秒倒计时，重置后回登录；PasswordResetServiceImpl生成六位码、5分钟有效、60秒间隔，新码废旧码，条件更新used后更新BCrypt密码；EmailServiceImpl通过JavaMailSender发送。SMTP变量默认账号/授权为空，MAIL_FROM回退SMTP_USERNAME，邮件在数据库事务内发送。配置存在不证明收件；邮箱重置未递增token_version，密码校验未满足Q19码点/字节目标。 |
| 实现/文档差异 | 真实投递/收件/闭环均未执行，发送成功后数据库提交失败可能产生已送达但不可用验证码，事务不能撤回邮件；继续保留Q05旧JWT失效及Q19新密码与旧哈希兼容缺口，不伪造新缺陷。 |
| 真实相关文件 | [ForgotPassword.vue](../../blog-web/src/views/public/ForgotPassword.vue)、[auth.js](../../blog-web/src/api/auth.js)、[AuthController.java](../../blog-server/src/main/java/com/example/blog/controller/AuthController.java)、[PasswordResetServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/PasswordResetServiceImpl.java)、[EmailServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/EmailServiceImpl.java)、[application.yml](../../blog-server/src/main/resources/application.yml)、[.env.example](../../.env.example) |
| 数据库及现有数据 | 复用sys_user和email_verification_code；后续独立测试库准备，不改真实库。 |
| API/前端影响 | 保留POST /api/auth/password/reset-code、/reset；真实收件是外部验证，不新增读取验证码接口。 |
| 环境依赖 | DB_PASSWORD、JWT_SECRET及SMTP_HOST/PORT/USERNAME/PASSWORD、MAIL_FROM、SSL/STARTTLS；只记录变量名，不读取或记录真实值。 |
| 兼容性/数据风险 | SMTP受外网和服务商影响，数据库与外部邮件不具原子性；证据必须脱敏。 |
| 待设计测试 | 独立测试库和受控真实测试邮箱：保存发送时间和脱敏收件证据，正常码完成重置并用新密码登录；错误、5分钟到期、单次消费、旧码、新旧密码、禁用状态与发送失败回归；两个路径旧Token失效按Q05、新设置按Q19。没有SMTP环境则标未执行/受限，不写通过。 正式用例待分配ID；未执行 |
| 未定细节/参数 | 测试SMTP服务/收件邮箱、等待上限和脱敏证据形式、邮件与数据库提交失败后的重试反馈待设计/补充；不要求新增投递队列。 |
| 后续实施 | 后续需真实环境验证及Q05/Q19受控修复；本轮不发邮件、不测试。 |
| 需求关联 | BLOG-F-006、BLOG-F-007、BLOG-BR-007、BLOG-BR-008；复合需求不整体批准 |

### Q13 邮箱与用户名规范化及历史账号兼容：影响分析

| 分析项 | 静态分析与后续工作 |
| --- | --- |
| 用户确认目标 | 新注册/真实修改邮箱入口去首尾空格并统一小写，唯一性按规范化结果判断；注册/修改用户名去首尾空格，保留大小写，不新增字符集限制，最大50沿用Q01。历史账号不自动合并、批量改值、删除或重命名；冲突先审计再提交方案。 |
| 当前依据 | UserServiceImpl.register/updateProfile已username.trim()并查询唯一性；register直接用request.email查询及保存，不trim/lowercase；login按原username查询，只有用户名登录，没有邮箱登录。PasswordResetServiceImpl邮箱trim().toLowerCase()；DTO @Email在Service规范化前执行；UserMapper SQL等值，schema两个唯一索引采用utf8mb4_unicode_ci，大小写比较受collation而非Java字符串控制。Profile修改仅username，无邮箱修改API。 |
| 实现/文档差异 | 注册与找回邮箱口径不统一；前置DTO或前端邮箱校验可能先拒绝带空格输入，需设计先规范化再校验路径。用户名已trim但DTO对原始长度先校验，Q01长度计数和登录输入/历史兼容仍需设计。当前库是否冲突未审计。 |
| 真实相关文件 | [Login.vue](../../blog-web/src/views/admin/Login.vue)、[Profile.vue](../../blog-web/src/views/public/Profile.vue)、[ForgotPassword.vue](../../blog-web/src/views/public/ForgotPassword.vue)、[UserServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/UserServiceImpl.java)、[PasswordResetServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/PasswordResetServiceImpl.java)、[RegisterRequest.java](../../blog-server/src/main/java/com/example/blog/dto/RegisterRequest.java)、[LoginRequest.java](../../blog-server/src/main/java/com/example/blog/dto/LoginRequest.java)、[UpdateProfileRequest.java](../../blog-server/src/main/java/com/example/blog/dto/UpdateProfileRequest.java)、[UserMapper.java](../../blog-server/src/main/java/com/example/blog/mapper/UserMapper.java)、[schema.sql](../../blog-server/src/main/resources/schema.sql) |
| 数据库及现有数据 | 保留sys_user唯一索引，先设计只读规范化冲突审计；如需迁移必须另授权，不自动合并或更新。 |
| API/前端影响 | 已有注册/login、PUT /api/users/me、密码找回请求输入处理可能变化；不新增邮箱登录或修改邮箱API。 |
| 环境依赖 | 历史数据库collation与原始账号取值需要独立验证；不查询真实数据。 |
| 兼容性/数据风险 | 数据库大小写/重音等价与应用规范化不是同一机制；历史空格、大小写和前置校验影响兼容。 |
| 待设计测试 | 新注册混合大小写/首尾空格邮箱保存规范值，变体重复拒绝；注册与找回相同身份邮箱路径一致；合法用户名首尾空格去除，内部字符及大小写不擅改，50上限回归；旧账号数据及哈希不自动变化，冲突仅登记/方案待批准。邮箱修改不存在，不虚构其执行测试；未来获准新增入口再适用。 正式用例待分配ID；未执行 |
| 未定细节/参数 | 登录用户名输入处理、找回输入与校验顺序、Locale无关小写建议、去空格集合、用户名大小写唯一性和历史冲突方案待设计/必要确认；没有邮箱登录/改邮箱功能，本次不批准新增。 |
| 后续实施 | 邮箱注册规范化及前后端校验顺序需后续设计实施，用户名现有trim保留。 |
| 需求关联 | BLOG-F-001、BLOG-F-002、BLOG-F-006、BLOG-F-007、BLOG-F-020、BLOG-BR-002；复合需求不整体批准 |

### Q16 初始化数据与统计计数一致性：影响分析

| 分析项 | 静态分析与后续工作 |
| --- | --- |
| 用户确认目标 | 在独立干净测试数据库执行初始化并核对表、种子和冗余统计；点赞/评论数量应符合现有已确认统计口径，发现不一致后另授权修SQL，不改真实业务库。 |
| 当前依据 | schema.sql定义10表；种子3用户、3分类、5标签、3文章（2发布1草稿）、8条文章标签关系、3评论（2通过1待审）、3点赞、2通过留言、5配置；验证码无种子。第一篇like/comment=2/2与记录吻合，第二篇like/comment=1/1但只有1个点赞和1条PENDING评论（按已通过数应为0）。这仅是全新库静态预期，未执行SQL。 |
| 实现/文档差异 | 第二篇comment_count种子与APPROVED统计存在明确脚本层面差异；实际运行库未读取，不能推断线上计数。重复执行UPSERT不等同初始化/计数自动校准，不能覆盖已有库。 |
| 真实相关文件 | [schema.sql](../../blog-server/src/main/resources/schema.sql)、[migration-v2.sql](../../blog-server/src/main/resources/migration-v2.sql)、[migration-v3-email-reset.sql](../../blog-server/src/main/resources/migration-v3-email-reset.sql)、[InteractionServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/InteractionServiceImpl.java)、[CommentAdminServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/CommentAdminServiceImpl.java)、[README.md](../../sql/README.md) |
| 数据库及现有数据 | 后续修正种子及独立初始化验证，不扩大为真实历史数据批量修复；本轮不改SQL。 |
| API/前端影响 | 不新增API；验证公开文章和后台统计与既定口径一致，文章删除后历史评论统计边界沿Q14。 |
| 环境依赖 | MySQL 8独立空库，当前脚本硬编码personal_blog必须在执行前隔离，不能仅修改DB_URL便假定安全。 |
| 兼容性/数据风险 | 初始化UPSERT可覆盖密码/角色/配置等演示数据；必须与旧库升级分离。 |
| 待设计测试 | 独立空库执行经隔离设计的脚本，核对10表及种子数量、状态、FK/唯一约束，逐篇比对like_count与点赞行、comment_count与有效APPROVED行；留存环境/步骤/预期/实测差异；计数修正和复验需后续授权，不预填通过。 正式用例待分配ID；未执行 |
| 未定细节/参数 | 独立库名称及防误连方案、脚本硬编码CREATE/USE隔离执行方式、夹具/查询清单及计数核验实测待设计/执行；不批准自动校准真实库。 |
| 后续实施 | 需要后续初始化测试、最小SQL修正及回归，均未开展。 |
| 需求关联 | BLOG-NF-007、BLOG-BR-018、BLOG-BR-022；复合需求不整体批准 |

### Q17 PC浏览器验收与性能方案：影响分析

| 分析项 | 静态分析与后续工作 |
| --- | --- |
| 用户确认目标 | 本期主交付PC Web，覆盖桌面Chrome和Edge功能验收；390px等窄屏专项增强暂缓但不破坏既有响应式。必须建立可执行性能验收方案，明确环境、对象、步骤、采样和判定；具体阈值、硬件、并发、工具未批准。 |
| 当前依据 | Vue/Vite页面有响应式样式；无本轮浏览器/性能实测。SRS性能草案1000篇、10并发、每接口100次、P95≤2秒，以及1366×768/1920×1080分辨率和390px检查均为此前建议，不能因同意方向而视作参数批准。Q10两请求10轮是独立最小竞争测试，不是性能负载指标。 |
| 实现/文档差异 | 桌面兼容方向已定但实际版本、分辨率待记录；性能环境/接口/采样/阈值需补充确认，不宣称达标；窄屏不再本期必需验收项，保留样式。 |
| 真实相关文件 | [main.css](../../blog-web/src/assets/main.css)、[index.js](../../blog-web/src/router/index.js)、[ArticleList.vue](../../blog-web/src/views/public/ArticleList.vue)、[ArticleDetail.vue](../../blog-web/src/views/public/ArticleDetail.vue)、[Profile.vue](../../blog-web/src/views/public/Profile.vue)、[ArticleManage.vue](../../blog-web/src/views/admin/ArticleManage.vue)、[AdminDashboard.vue](../../blog-web/src/views/admin/AdminDashboard.vue)、[vite.config.js](../../blog-web/vite.config.js)、[ArticleServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/ArticleServiceImpl.java) |
| 数据库及现有数据 | 仅后续独立性能数据集，不注入真实库；无本轮DDL。 |
| API/前端影响 | 性能对象从实际列表/详情/热门/后台接口选取，SMTP外部延迟单独记录，不造新接口。 |
| 环境依赖 | 桌面Chrome/Edge及记录明确的测试机/数据库/网络；版本和量化参数未执行核实。 |
| 兼容性/数据风险 | 无界评论返回是风险而不是自动增加分页任务；10并发草案不得与Q10竞争目标混同。 |
| 待设计测试 | 后续在记录实际版本的桌面Chrome/Edge完成注册登录、文章筛选详情、评论留言、资料/头像、后台编辑/审核等场景；性能先批准参数表再在独立数据环境采集与判定，未执行记未执行；390px专项未完成不判本期必需功能失败，不主动破坏既有布局。 正式用例待分配ID；未执行 |
| 未定细节/参数 | 设备/OS/JDK/Node/MySQL实版本、桌面分辨率、数据规模、测试接口、并发、预热、计时边界、请求数/轮次、P95及失败率阈值、测试工具与报告证据待补充/批准；不强制已有草案数值。 |
| 后续实施 | 需编写测试计划/参数确认并真实执行；窄屏专项增强后续候选。 |
| 需求关联 | BLOG-NF-008、BLOG-NF-009、BLOG-NF-011、BLOG-NF-012；复合需求不整体批准 |

### Q18 数据库初始化、升级与备份恢复交付：影响分析

| 分析项 | 静态分析与后续工作 |
| --- | --- |
| 用户确认目标 | 交付包含空库初始化、已有库升级风险与操作、备份恢复说明；新库与旧库不能无条件覆盖混用；恢复验证独立环境且同步数据库avatar路径与本地头像文件；本期不强制自动迁移框架，不声称已恢复。 |
| 当前依据 | README及sql/README索引实际schema、migration-v2（ADD token_version、留言默认PENDING）、migration-v3（验证码建表）。Spring配置没有自动初始化SQL开关，本地环境变量需单独设置，.env不自动读取。AvatarStorageService以配置路径转绝对根目录，存UUID文件，库仅相对URL，Git忽略运行上传。尚无正式备份/恢复操作说明或独立演练证据。 |
| 实现/文档差异 | 已有运行/初始化概述不能替代可核对的升级检查和完整恢复文档；v2重复ADD会失败，CREATE IF NOT EXISTS不升级旧表；UPSERT可覆盖演示账号/配置，恢复仅数据库将遗漏图片。 |
| 真实相关文件 | [README.md](../../README.md)、[README.md](../../blog-server/README.md)、[.env.example](../../.env.example)、[application.yml](../../blog-server/src/main/resources/application.yml)、[schema.sql](../../blog-server/src/main/resources/schema.sql)、[migration-v2.sql](../../blog-server/src/main/resources/migration-v2.sql)、[migration-v3-email-reset.sql](../../blog-server/src/main/resources/migration-v3-email-reset.sql)、[AvatarStorageServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/AvatarStorageServiceImpl.java)、[WebConfig.java](../../blog-server/src/main/java/com/example/blog/config/WebConfig.java)、[README.md](../../sql/README.md) |
| 数据库及现有数据 | 保留既有三SQL文件，不虚构其他迁移；后续独立恢复与必要增量方案另授权。 |
| API/前端影响 | 无新增接口；恢复后按当前/api和/uploads/avatars映射验证应用，环境秘密不随文档公开。 |
| 环境依赖 | JDK17/MySQL8、DB_URL/DB_USERNAME/DB_PASSWORD、JWT_SECRET及实际头像绝对目录；相对目录取决于启动工作目录。 |
| 兼容性/数据风险 | 误灌旧库、重复ALTER、快照时间不一致或漏头像；邮件/JWT环境凭据只通过本地安全配置，备份需受控。 |
| 待设计测试 | 后续分别编写新空库、旧库预检查/备份/适用迁移/回退、恢复说明；独立环境验证表/记录/约束/配置与头像文件可读，记录实际步骤与证据；恢复不得覆盖真实库，旧库升级先结构审计；文档完成和演练执行分别登记。 正式用例待分配ID；未执行 |
| 未定细节/参数 | 旧库来源/当前结构、迁移适用判定、独立目标库与恢复机器、备份工具/一致性时点/头像快照方案、具体恢复检查和回退步骤待设计；不批准真实库执行。 |
| 后续实施 | 需后续交付文档与独立初始化/升级/恢复验证，自动迁移框架非本期强制。 |
| 需求关联 | BLOG-NF-015、BLOG-NF-016；复合需求不整体批准 |

### 性能验收参数建议表（供后续确认，非正式指标）

| 参数 | 当前依据/建议 | 状态与需补材料 |
| --- | --- | --- |
| 主交付与浏览器 | PC Web；桌面Chrome、Edge | 已确认方向，实际版本待记录 |
| 数据规模 | 原草案1000篇文章 | 未批准；需明确发布/草稿、评论/点赞/标签量 |
| 并发与采样 | 原草案10并发、每接口100次 | 未批准；不得替代Q10两请求10轮 |
| 响应判定 | 原草案P95≤2秒 | 未批准；需计时边界、预热、失败率及轮次 |
| 机器与网络 | 固定测试机、独立MySQL与网络 | 参数待填写；不虚构硬件配置 |
| 目标对象 | 实际列表/详情/热门/后台查询候选 | 具体接口集及工具待批准，正文读取可能增加阅读数应隔离夹具 |
| 屏幕 | 原建议1366×768、1920×1080 | 分辨率待确认；390px专项本期暂缓 |
| 证据 | 环境、参数、原始采样及判定记录 | 尚未执行/生成，不制作通过报告 |


### 12.4-E修订历史

| 日期 | 版本 | 真实修订 | 边界 |
| --- | --- | --- | --- |
| 2026-10-08 | v0.5 | 同步Q04/Q13/Q16/Q17/Q18指定范围，20个Q均完成方向确认，保留复合范围与残余细节 | 评审草稿；无代码/SQL/配置/测试修改或执行，无正式基线和Git写操作 |


## 12.5-B A—G确认同步（阶段历史，原规则继续有效）

来源：用户12.5-B粘贴请求明确批准原[决策表](baseline-final-decision-sheet.md)完整推荐条件。具体规范与完整性能参数见[SRS当前同步节](software-requirements-specification.md#125-b-ag已确认规则当前有效范围2026-10-08)。不新增Q/BLOG编号；Q01—Q20指定范围确认不变，未建立正式基线。

| 决策 | 完整关联需求编号（仅对应子范围） | 已确认目标及验收摘要（待设计、未执行） |
| --- | --- | --- |
| A 站点配置部分更新 | BLOG-F-040、BLOG-NF-007、BLOG-NF-010、BLOG-NF-014、BLOG-BR-024 | 管理员PUT /api/admin/site-config提交一个或多个五键白名单项；缺失键保留旧值，显式空字符串按字段规则，null、未知键及空对象拒绝；任一非法项或保存失败整批回滚，不出现部分保存。 验收：单键更新其余值不变；合法空字符串被保存而非默认替换；null/未知键/空对象拒绝；合法与非法项混合及中途写入失败均无部分改动；游客401、普通用户403。 |
| B 配置默认值、历史数据与码点长度 | BLOG-F-040、BLOG-F-008、BLOG-F-014、BLOG-NF-010、BLOG-NF-013、BLOG-BR-024 | 初始化种子与运行保存分离，不用默认值替换合法空字符串；历史缺失或非法值先审计再逐项批准修复，禁止擅自覆盖/删除/截断。五字段按Unicode码点计数，siteName沿现有trim后1—50且必填；其余字段不自动trim，允许空字符串，上限依次100/500/5000/254，email非空须合法；均不得null。 验收：siteName空/纯空白拒绝、trim后50码点允许及51拒绝；其他四字段空字符串保留、上限及上限+1边界；多字节/补充平面字符验证码点计数；email非法拒绝；历史值未获批准不得自动改写。 |
| C 账号身份比较与历史兼容 | BLOG-F-001、BLOG-F-002、BLOG-F-006、BLOG-F-007、BLOG-F-020、BLOG-NF-007、BLOG-BR-002 | 注册、登录、改名均先按现有trim口径去首尾空白，用户名非空、最大50 Unicode码点，保留大小写、不新增字符集限制；等值/唯一性沿现有数据库比较规则。注册及找回邮箱先trim并小写再校验/比较，规范化结果不得绕过唯一性。旧账号/哈希不自动修改或合并，冲突先审计提案；无邮箱登录/修改邮箱功能。 验收：三入口49/50/51码点边界和首尾空白一致；补充平面字符不按UTF-16单元误计；邮箱大小写/首尾空白变体同一身份且重复拒绝；保留原用户名大小写和数据库等价判断；旧账号哈希不变、兼容场景单独回归。 |
| D 登录状态和角色生效 | BLOG-F-003、BLOG-F-005、BLOG-F-016、BLOG-F-034、BLOG-F-035、BLOG-NF-002、BLOG-BR-004、BLOG-BR-006 | 状态或角色提交后开始的下一次鉴权按数据库当前状态/角色，不仅信JWT角色。客户端进入受保护路由核实用户；401清理失效身份，403纠正角色/菜单。公开页附带身份读取失败降级不强制登录；Q03启用不恢复旧Token、Q05两种改密旧Token失效继续有效。保留他人USER/ADMIN调整及禁止改本人角色；不要求跨设备实时推送或取消已完成鉴权的在途请求。 验收：变更提交后新请求判权；降权后台403并纠正菜单；缓存存在仍核实保护路由；禁用旧Token401、启用后仍401；公开详情附带like状态失败不离开文章；实际点赞/评论仍鉴权。 |
| E 分类与标签删除 | BLOG-F-031、BLOG-F-032、BLOG-NF-007、BLOG-BR-015 | 分类有未逻辑删除的有效文章引用（草稿/发布均含）则拒绝，否则可逻辑删除；标签有blog_article_tag关联则拒绝，否则可逻辑删除。不级联删除文章或互动历史，目标不存在/写入失败明确失败；不批准物理清理。Q14已删文章显式清标签关联、保留评论/点赞及分类物理外键不变。 验收：草稿/发布引用均阻删分类；仅逻辑删除文章引用不阻止分类逻辑删除；任意仍存在标签关联阻删；无引用逻辑删除；文章/评论/点赞不受清理；不存在和失败不报成功。 |
| F 邮件副作用与120秒收件 | BLOG-F-006、BLOG-F-007、BLOG-NF-010、BLOG-NF-014、BLOG-BR-007、BLOG-BR-008 | 外部邮件不能随数据库回滚撤销可接受，但发码/提交失败必须明确失败；送达不能绕过库内有效码校验，重新申请遵守60秒限流。重置写入失败不得消费验证码。真实收件从发送请求起观察120秒，未收到记该次未通过或环境受限，不是SMTP送达保证。Q04真实闭环、Q05旧JWT失效、Q19新密码及旧哈希兼容继续有效。 验收：留存发送起点及脱敏收件时间；120秒内记录是否收到并用有效码重置/新密码登录；超过窗口不报通过；邮件失败、邮件已送而库失败、错误/过期/已用码及重置回滚分别验证。 |
| G 性能及桌面验收参数 | BLOG-NF-008、BLOG-NF-009、BLOG-NF-011 | 采用原决策表完整参数包：1000文章（900发布/100草稿）、10用户、被测详情20已通过评论；每接口20次串行预热后10客户端各10次顺序请求，100测量样本；最近秩P95≤2秒且业务/HTTP/内容全部成功；独立环境、原始采样及Chrome/Edge版本/桌面视口如实记录。 验收：四接口分别100样本不混合，预热排除；P95=排序后ceil(0.95×N)样本，N=100取第95个；错误不删，缺样/Token过期轮无效且不报通过；失败留缺陷及完整复测；正文固定为每篇4,096 UTF-8字节（12.5-C已确认）。（12.5-C补充：每篇4,096 UTF-8字节，其他条件不变） |

当前代码差异保留在实现列和影响分析，不以确认推导代码符合或测试通过。正式用例ID待分配。原推荐未给固定正文UTF-8字节数；12.5-C已补齐每篇4,096字节，现不再待补充；真实硬件/软件/浏览器版本在执行前记录，不伪造。本轮未授权历史数据自动修复、运行时缺失配置自动补值或额外功能。技术实现与验证可留到设计/测试阶段，整体范围及优先级仍待最终批准。

| 日期 | 版本/阶段 | 修订 |
| --- | --- | --- |
| 2026-10-08 | 12.5-B | 记录A—G用户确认，保留实现差异、未执行验收及正文大小参数 |

### A—G对应实现文件、数据/API影响与风险

下表路径相对项目根；仅静态定位，真实运行库及历史记录未审计。不得据此执行迁移或修复。前端文件根blog-web/src，后端根blog-server/src/main/java/com/example/blog。

| 决策 | 真实代码文件 | 前后端及API影响 | 数据/兼容风险 | 待设计测试 |
| --- | --- | --- | --- | --- |
| A | blog-web/src/views/admin/SystemManage.vue、api/blog.js；blog-server/src/main/java/com/example/blog/controller/AdminController.java | PUT路径保留，Map未知键/null/空对象需拒绝；缺失不改与部分原子保存，页面失败不成功提示 | blog_site_config无需因部分语义新增表；字段更新事务需设计，运行库未知 | 单键/多键/缺失/显式空/null/未知/空对象；非法与中途失败全回滚、管理员权限 |
| B | blog-web/src/views/public/Home.vue、About.vue、layouts/PublicLayout.vue、views/admin/SystemManage.vue；后端AdminController、PublicController、entity/SiteConfig.java；blog-server/src/main/resources/schema.sql | 页面回退不是保存默认值；码点/字段校验、公开五键过滤 | TEXT可null且种子可覆盖已有值；先审计、不自动修数据；缺失展示回退待设计，不自动补值 | 五字段上限/上限+1、补充平面字符、纯空白、合法空字符串；旧值未审批不改 |
| C | blog-web/src/views/admin/Login.vue、views/public/Profile.vue、ForgotPassword.vue；后端dto/LoginRequest.java、RegisterRequest.java、UpdateProfileRequest.java、service/impl/UserServiceImpl.java、PasswordResetServiceImpl.java、mapper/UserMapper.java；schema.sql | 登录max30与目标50码点冲突；@Size/前端长度和先校验后trim路径需调整；无新邮箱登录API | VARCHAR(50)及utf8mb4_unicode_ci/唯一键保留；历史身份规范冲突先审计；不自动改哈希，真实库结构待核验 | 三入口码点边界/空白；邮箱变体重复；旧账号登录及密码兼容 |
| D | blog-web/src/router/index.js、store/auth.js、utils/request.js、views/public/ArticleDetail.vue；后端security/JwtFilter.java、JwtService.java、service/impl/AdminUserServiceImpl.java、UserServiceImpl.java、PasswordResetServiceImpl.java | JWT当前角色校验已有，缓存恢复/403纠正/公开附带401隔离有差异；启停/重置版本未递增 | sys_user.token_version现有字段可复用，版本/状态竞争待设计；不改变历史互动；不信JWT旧role | 缓存保护路由、公开降级、降权、禁用/启用及两种改密旧JWT，竞态另按Q10 |
| E | blog-web/src/views/admin/SystemManage.vue；后端controller/AdminController.java、service/impl/TagAdminServiceImpl.java、ArticleServiceImpl.java、entity/Category.java、Tag.java、mapper/ArticleTagMapper.java；schema.sql | 分类有效引用检查、标签关联检查已存在；不存在与写入失败反馈需完善、并发保护待设计 | deleted逻辑标志，blog_article_tag是物理关联；删文章显式清标签关联，分类FK仍留存；无物理清理授权 | 草稿/发布阻删、仅已删文章引用、残留标签关联、无引用逻辑删及失败无级联 |
| F | blog-web/src/views/public/ForgotPassword.vue；后端service/impl/PasswordResetServiceImpl.java、EmailServiceImpl.java、dto/ResetPasswordRequest.java；entity/EmailVerificationCode.java | TX内发邮件现状，失败反馈和120秒观察协议；重置仍需Q05版本失效设计 | email_verification_code used消费与密码更新同事务；外部邮件不能回滚，重试尊重已提交状态/限流 | 真实SMTP收件、窗口外记录、邮件/DB失败、已送但码无效、重置回滚/重复消费；ID待分配、未执行 |
| G | blog-server/src/main/java/com/example/blog/controller/PublicArticleController.java、ArticleAdminController.java、service/impl/ArticleServiceImpl.java；blog-web/src/router/index.js、views/public/ArticleList.vue、ArticleDetail.vue、views/admin/ArticleManage.vue | 四已存在查询接口无需凭空新增；测量脚本/夹具待设计，JMeter只测HTTP非渲染 | 独立测试库1000文章和约定关联数据；详情会写阅读数，严禁真实库；固定正文大小值待补 | 四接口100样本/20预热/P95第95项、业务错误不剔除、无效轮说明及两桌面浏览器（12.5-C补充：每篇4,096 UTF-8字节，其他条件不变） |

建议顺序：先补正文验收参数并完成整体范围评审；原型/详细设计及测试计划提前建立；后续另获授权实施C/D安全兼容、A/B配置事务、E失败边界、F外部副作用，最后在隔离环境完成G及回归。顺序是设计建议，不是当前代码实施批准。无本轮数据库结构修改；潜在迁移要基于运行库审计再决定。


## 12.5-C 最后性能参数与最终评审候选状态（2026-10-08，历史快照）

确认来源：用户在ChatGPT 12.5-C请求中明确指定BLOG-NF-009的性能数据集正文规格，属于既有非功能需求参数补充，不新增Q编号，不改变此前A—G确认，不是正式需求基线批准。

- 每篇纳入指定性能测试数据集的文章正文按UTF-8编码后**恰好4,096字节（4 KiB）**核验；不是4,096个字符，不能仅用Java String.length()或JavaScript字符串.length判断。
- 只限定测试夹具，不是生产文章上限，也不是用户发表文章的输入限制。正文生成、中英文混合、实际字节核验方法、插入隔离、清理及证据方式留测试设计；不能因这些技术事项拒绝范围候选。
- 此前完整G条件不变：1,000篇（900发布/100草稿）、10测试用户、被测详情20已通过评论；四指定接口各20次串行预热，随后10客户端各10次顺序测量，共100样本；最近秩第95个样本P95≤2秒、HTTP/Result及内容均正确，错误不剔除、缺样/Token过期轮不得报通过。
- 独立测试库/环境、JMeter直连完整响应时间、原始脱敏证据、失败留存及完整复测、Chrome/Edge实际版本与1366×768/1920×1080桌面视口等条件继续有效。不得将Q10的2请求×10轮最小竞争测试与性能测试混同。
- 复评为**K79 / R0 / D1**：79项可提交整体范围评审；BLOG-NF-012仅390px等窄屏专项增强/专项验收后续候选，现有响应式保留。确认前K78/R1/D1保留为历史。
- 现有MoSCoW候选优先级继续使用：Must57、Should22、Could1，全部建议、待审批，不与路线图P0—P3混用。Q01—Q20仍20项指定范围确认，A—G仍7组确认；80条复合需求整体及优先级未正式批准。
- 最终逐项评审与审批模板见[需求基线最终候选评审表](baseline-final-review.md)。未发现仍需补充决定的业务范围或量化验收阻碍；整体候选审批/风险接受/明确建立基线授权仍待用户。旧身份冲突个案及旧缺失配置展示回退等按设计阶段处理，不自动修复。
- SRS为**v0.8——需求基线最终评审候选稿，待用户批准**；没有代码修复，没有任何本轮测试结果，没有正式v1.0.0基线或Git写操作。

| 日期 | 阶段 | 真实变更 | 审批边界 |
| --- | --- | --- | --- |
| 2026-10-08 | 12.5-C | 用户确认测试正文4,096 UTF-8字节并准备最终候选评审 | 参数已确认；整体范围/优先级与建立基线授权待审批 |


## 12.5-D 正式基线同步与变更边界（2026-10-08，当前状态）

用户明确批准本期79项、BLOG-NF-012后续专项范围及80项优先级，授权SRS v1.0.0，基线编号BLOG-REQ-BL-001。此前“候选/待审批/未建基线”段落仅保留当时阶段状态，不推翻本轮正式审批；本期列明目标、规则和验收条件已批准，设计/实施/验证仍待完成。

[基线登记及变更流程](requirements-baseline-register.md)界定规范性SRS/清单、维护性追踪矩阵和审批证据。后续业务范围、规则、接口可见行为、验收或优先级变更需先登记BLOG-CR、分析影响并获用户批准；不凭批准需求直接执行数据库/编码/Git操作。文档级正式基线已建立，Git固化尚未执行。

| 日期 | 版本/阶段 | 真实变更 | 验证边界 |
| --- | --- | --- | --- |
| 2026-10-08 | 12.5-D / SRS v1.0.0 | 用户整体审批并正式登记；原80项ID及逐项优先级不变 | 只通过文档一致性检查，非功能验收；未改代码、未执行测试、未提交Git |
