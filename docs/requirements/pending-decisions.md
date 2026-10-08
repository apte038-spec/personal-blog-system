# 需求决策状态与剩余实施、验收细节清单

> 12.5-D当前状态：用户于2026-10-08通过ChatGPT异步正式批准79项本期需求、BLOG-NF-012后续候选及全部80项优先级（Must57 / Should22 / Could1），授权建立SRS v1.0.0。文档级正式需求基线已建立；Git固化尚未执行。需求批准不代表代码完成或测试通过，也不是教师审批或学校验收。此前v0.8及各阶段候选状态仅为历史。

文档版本：v0.9（12.5-D状态同步；对应SRS v1.0.0）
日期：2026-10-08<br>
依据：SRS/清单/追踪矩阵v1.0.0、12.5-D整体审批及A—G真实确认、影响分析、80项矩阵及当前源代码。
性质：Q01—Q20总体方向均已完成指定范围确认；本文件继续管理待设计、待补充参数与待执行验证，80项列明范围/优先级已于12.5-D获批；本文件不是代码许可或规范性SRS的替代。<br>

## 1. 阅读约定与证据

[需求清单](requirements-inventory.md)、[SRS](software-requirements-specification.md)、[评审记录](requirements-review-record.md)、[两批影响分析](confirmed-requirements-impact-analysis.md)、[追踪矩阵](requirements-traceability-matrix.md)、[角色流程](roles-and-business-flows.md)、[路线图](../project-planning/documentation-roadmap.md)为输入。E01—E17索引沿用清单，下面链接定位当前代码。本轮只静态阅读，未连接运行库、执行邮件/并发/性能测试或修改代码。

Q01—Q20共20项问题均已完成指定范围确认（2026-10-08 ChatGPT异步确认）。不存在仍未确认总体方向的Q编号；仍有待设计和待执行验证的细节；原性能正文参数已于12.5-C补齐，未发现新的量化验收阻碍。历史20项方向确认本身不等于整体批准；本轮12.5-D已另外批准80项列明范围与优先级，但不代表代码完成或测试通过，已建立v1.0.0文档级正式基线。此前子范围确认不等于整体批准；本轮另有12.5-D整体批准；代码现状、目标及验证状态分别记录。本轮未改代码、未执行测试、已建立文档级正式基线。A/B配置部分更新、缺失保留和默认分离已确认；Q19实现及旧账号验证仍待设计。Q19新密码8—64个Unicode码点、UTF-8最多72字节、超限拒绝及旧哈希登录兼容原则已确认；Q15五字段空字符串/非null、长度及email合理格式规则已确认。剩余为实现设计、正文参数已确认后的测试设计及待执行验证；复合需求只确认对应子范围。所有本轮测试未执行、代码未修复，已建立正式需求基线v1.0.0。 未将原推荐扩大为已批准技术方案。C/D已确认用户名码点计数、现有trim及受保护导航核实，登录兼容和具体刷新实现待设计，可与Q13及后续设计共同澄清，不另分或覆盖Q编号。列明范围及MoSCoW优先级已于12.5-D整体确认；额外变更另行审批。

一个Q可能包含多个子决定。12.5-B已明确批准原决策表A—G完整推荐条件，仅按其指定范围同步，不扩大到未列明方案。A/B/C是可选方案字母，分类A/B/C是问题性质，两者含义分开。影响列均是“选择推荐后可能涉及”，不是本轮改动。所有测试为拟准备用例，ID待正式登记，未执行。

## 2. 分类与决策优先级

| 类别 | 判断依据 | 问题 | 处理原则 |
| --- | --- | --- | --- |
| A 核心正确性、安全或边界 | 改变账号/身份、数据成功语义、公开范围或长期业务约束 | Q03、Q10、Q12、Q13、Q14、Q15、Q19、Q20 | 先明确政策及风险；不要求所有代码修复先于基线 |
| B 验收环境与交付质量 | 外部邮件、种子一致性、测量参数、安装升级/恢复 | Q04、Q16、Q17、Q18 | 先确认可验证范围；执行与证据在后续，未验证不可当作通过 |
| C 扩展或体验/展示口径 | 首页区域、导航体验、统计解释及同值次序、评论UI增强 | Q06、Q07、Q09、Q11 | 不阻塞无关基础功能，可明确范围后后移增强 |

共8项A、4项B、4项C，各Q只计一次。Q09的指标定义必须明确才能确认对应条目，但无须为博客添加访客分析；若选历史事件统计，其性质升为范围变更。Q11分页若数据量成为必需，则升级范围优先级。Q03服务器注销可选，但禁用后会话政策是A。问题性质不等于整个Q都阻塞。

P0优先处理安全、成功语义及验收前提；P1重要边界/体验；P2可选范围。P级是本次决策顺序建议，不改变需求Must/Should/Could。

## 3. 原16项决策总览（均已指定范围确认；完整Q01—Q20见评审记录）

| 问题 | 完整名称 | 分类/建议顺序 | 原推荐/已确认范围与未定细节 | 用户决策 |
| --- | --- | --- | --- | --- |
| Q03 | 退出登录与禁用后旧会话的处理边界 | A / P0 | 禁用时此前签发Token失效；重新启用不得恢复禁用前旧Token，必须重新登录取得新Token；本期暂不增加服务器端主动注销，保留现有客户端退出。 失效机制及与登录/启停竞争的原子边界待设计；D已确认受保护路由核实及下一次鉴权生效，具体机制待设计。 | 已确认指定范围（2026-10-08）；残余细节待确认 |
| Q04 | 真实SMTP收件验收及邮件与数据库事务边界 | B / P0 | 本期验收必须覆盖请求验证码、真实SMTP发送、真实邮箱收件、输入验证码、设置新密码和新密码登录；接口成功不等于收件。错误/过期/已使用验证码必须验证；无可用环境如实记录未执行或环境受限，不免除真实收件目标，不公开凭据。 测试SMTP服务/收件邮箱、等待上限和脱敏证据形式、邮件与数据库提交失败后的重试反馈待设计/补充；不要求新增投递队列。 | 已确认指定范围（2026-10-08 ChatGPT 12.4-E），未实施/未验证 |
| Q06 | 首页独立置顶区域的范围 | C / P2 | 本期保留文章列表置顶、is_top字段及后台置顶设置；首页不新增独立置顶区，独立区域仅为后续版本候选，不纳入本期必需验收。 置顶同值次级字段随Q09设计；未批准首页新区域或删除featured接口。 | 已确认指定范围（2026-10-08 ChatGPT 12.4-D），未实施/未测试 |
| Q07 | 文章筛选与浏览器历史导航一致性 | C / P1 | 筛选控件、当前URL有效查询参数和结果一致；合法直达URL、刷新及前进/后退应恢复条件与结果；不要求每次筛选都新增历史记录。 非法/多值参数纠正、有效性判断、查询参数规范化、组合冲突边界及是否重置越界页待设计或必要确认；现有AND组合是代码证据，不将本次Q07当作新增组合规则批准。 | 已确认指定范围（2026-10-08 ChatGPT 12.4-D），未实施/未测试 |
| Q09 | 阅读量、发布时间、趋势及排序口径 | C / P1 | 保留当前阅读、趋势及热门主要指标口径；主指标相同记录补确定性次级排序；本期不新增访客去重或独立发布事件历史系统。具体次级字段未批准。 末级唯一id及其方向可作设计建议，非用户已批准字段组合；主指标不变，不将SQL默认返回顺序当稳定保证。 | 已确认指定范围（2026-10-08 ChatGPT 12.4-D），未实施/未测试 |
| Q10 | 并发互动、审核及验证码的一致性验收 | A / P0 | 在独立测试数据库对点赞/取消点赞、评论审核、同邮箱验证码发送等现有清单并发场景，每场景2个并发请求、重复10轮，验证最终状态、计数、唯一性和业务约束；不得破坏真实业务库，不称高并发性能认证。 测试用例ID待分配；数据夹具、同步起跑和邮件模拟方式待测试设计，Q04真实收件目标已确认，SMTP环境与证据待设计，未执行，锁/条件更新方案未批准。 | 已确认指定范围（2026-10-08）；残余细节待确认 |
| Q11 | 公开评论分页与互动区图片头像 | C / P2 | 本期保留公开评论展示及首字头像；评论分页、评论图片头像暂缓至后续候选；不取消个人中心头像上传或既有留言分页。 性能测试规模归Q17；首字Unicode改进未在本次批准，不把现有slice(0,1)改称码点截取。 | 已确认指定范围（2026-10-08 ChatGPT 12.4-D），未实施/未测试 |
| Q12 | 更新失败、统计错误与配置批量保存的反馈 | A / P0 | 更新/保存失败不报告成功，提供明确适当错误；合法配置批次全部成功或全部回滚；统计查询失败不得伪装真实0；HTTP请求成功不等于业务写入成功。 错误文案、具体异常映射及幂等无变更的成功判定待设计，不把所有更新返回0都机械视作失败；本次通用原则的其他写接口回归范围须测试设计。 | 已确认指定范围（2026-10-08）；残余细节待确认 |
| Q13 | 邮箱与用户名规范化及前端校验策略 | A / P1 | 新注册/真实修改邮箱入口去首尾空格并统一小写，唯一性按规范化结果判断；注册/修改用户名去首尾空格，保留大小写，不新增字符集限制，最大50沿用Q01。历史账号不自动合并、批量改值、删除或重命名；冲突先审计再提交方案。 C已确认三入口现有trim、50 Unicode码点、沿用数据库比较、邮箱先规范化及历史审计原则；实现与个案处理待设计；没有邮箱登录/改邮箱功能，本次不批准新增。 | 已确认指定范围（2026-10-08 ChatGPT 12.4-E），未实施/未验证 |
| Q14 | 文章及分类删除的历史关联保留政策 | A / P1 | 保留现有文章逻辑删除、评论/点赞历史留存及文章标签关联清理；本期不增回收站或独立物理清理。确认核实范围，不批准未知清理行为。 实际库结构/历史数据未核验，交付迁移归Q18；物理清理不在本轮批准。 | 已确认指定范围（2026-10-08 ChatGPT 12.4-D），未实施/未测试 |
| Q15 | 站点配置公开白名单与值校验 | A / P0 | 仅siteName、siteSubtitle、homeIntro、aboutContent、email五键可公开读取和由ADMIN保存，未知键拒绝写入且不得公开，五字段均拒绝null。siteName必填，保存前去除首尾空格，处理后1—50字符；siteSubtitle允许空字符串、最多100字符；homeIntro允许空字符串、最多500字符；aboutContent允许空字符串、最多5000字符；email允许空字符串，非空须符合合理邮箱格式、最多254字符，非SMTP账号。批量保存继续遵守Q12全部成功或全部回滚。 待设计或确认：A/B已确认部分更新、缺失保留、空值区分、初始化与保存分离、Unicode码点与现有trim；邮箱校验器、历史审计及失败反馈实现待设计。按A已确认的请求语义设计；其他四项未获准自动trim。 | 已确认指定范围（2026-10-08）；残余细节待确认 |
| Q16 | 初始化演示数据的冗余计数一致性 | B / P0 | 在独立干净测试数据库执行初始化并核对表、种子和冗余统计；点赞/评论数量应符合现有已确认统计口径，发现不一致后另授权修SQL，不改真实业务库。 独立库名称及防误连方案、脚本硬编码CREATE/USE隔离执行方式、夹具/查询清单及计数核验实测待设计/执行；不批准自动校准真实库。 | 已确认指定范围（2026-10-08 ChatGPT 12.4-E），未实施/未验证 |
| Q17 | 性能、桌面兼容与窄屏验收参数 | B / P0 | 本期主交付PC Web，覆盖桌面Chrome和Edge功能验收；390px等窄屏专项增强暂缓但不破坏既有响应式。必须建立可执行性能验收方案，明确环境、对象、步骤、采样和判定；G阈值、负载、工具和环境记录方式已确认；真实硬件/版本在执行前登记。 G及12.5-C已确认数据规模、接口、10客户端、20预热/100采样、计时、P95≤2秒、工具、4,096 UTF-8字节正文和桌面视口；真实版本/环境登记、夹具生成/核验、证据实现留测试设计。 | 已确认指定范围（2026-10-08 ChatGPT 12.4-E），未实施/未验证 |
| Q18 | 全新数据库安装、旧库升级与备份恢复 | B / P0 | 交付包含空库初始化、已有库升级风险与操作、备份恢复说明；新库与旧库不能无条件覆盖混用；恢复验证独立环境且同步数据库avatar路径与本地头像文件；本期不强制自动迁移框架，不声称已恢复。 旧库来源/当前结构、迁移适用判定、独立目标库与恢复机器、备份工具/一致性时点/头像快照方案、具体恢复检查和回退步骤待设计；不批准真实库执行。 | 已确认指定范围（2026-10-08 ChatGPT 12.4-E），未实施/未验证 |
| Q19 | 密码字符长度与BCrypt编码边界 | A / P0 | 新密码须同时满足8—64个Unicode码点及UTF-8编码后不超过72字节；超出任一限制明确拒绝，不静默截断。适用于注册、登录状态下修改密码及邮箱验证码重置；前后端口径一致，后端独立校验。保留已有账号及密码哈希，不批量强制重置；已有密码登录验证不得直接套用新密码设置限制，不静默修改、截断或重新编码原密码，成功登录后可建议主动更新。 待设计/核实：Java/JavaScript码点与UTF-8统计、Unicode规范化是否需处理、旧账号兼容回归、现存超限密码数量和错误逐字文案；阈值、码点口径、超限拒绝及保留旧哈希兼容原则已于12.4-C确认，不再待批准。不得自行规范化、转换、预哈希或更换算法。 | 已确认指定范围（2026-10-08）；残余细节待确认 |
| Q20 | 评论与留言永久相同内容去重政策 | A / P1 | 保留评论和留言永久去重；重复提交明确失败、不报成功；本期不增加限时去重或依据审核状态改变规则。 已有数据库约束落地情况与collation等价输入验证未执行；不批准额外文本转换或全用户共用去重。 | 已确认指定范围（2026-10-08 ChatGPT 12.4-D），未实施/未测试 |

## 4. 12.4-E及之前选项/影响（历史依据，当前确认见12.5-B）

### Q03 退出登录与禁用后旧会话的处理边界

| 项目 | 内容 |
| --- | --- |
| 完整问题 | 退出是否仅结束当前浏览器登录？账号禁用后重新启用，原未过期Token是否允许恢复有效？这两个决定必须分别回答，不能从Q05改密规则推导。 |
| 当前实际行为（确认目标尚未修复） | auth Store的logout只清本地；AdminUserServiceImpl.updateStatus只改status，不递增token_version；JwtFilter查当前ACTIVE及版本，所以重新启用后原未过期、版本未变Token可能再次有效。 |
| 代码依据 | E03、E13；[Store](../../blog-web/src/store/auth.js)、[用户管理服务](../../blog-server/src/main/java/com/example/blog/service/impl/AdminUserServiceImpl.java)、[JWT过滤器](../../blog-server/src/main/java/com/example/blog/security/JwtFilter.java)。 |
| 为什么需确认 | 会话安全承诺及退出验收不同：本地退出不能被文档写成服务器注销，启用也不能被误写成旧Token永久失效。 |
| 可选方案 | A：保留本地退出及启用后旧Token可恢复，明确风险；B：本地退出保持，禁用时永久废此前Token，启用后重新登录；C：另增服务器注销（当前或全部会话），并执行禁用失效。 |
| 原推荐（不是全量批准） | 保留下面原选项仅作评审历史；用户实际只确认：禁用时此前签发Token失效；重新启用不得恢复禁用前旧Token，必须重新登录取得新Token；本期暂不增加服务器端主动注销，保留现有客户端退出。 原建议未覆盖/未被批准部分仍待确认。 |
| 推荐理由 | 禁用后不复活旧会话更易解释；复用既有安全状态的候选设计成本低于新增会话系统，但机制仍由设计确定。 |
| 前端/后端/数据库/测试/文档影响 | 前端：退出入口可不变，启用后说明需重登；后端：状态更新与失效行为需设计，回归JWT；数据库：可复用版本列，须核对旧库，无必然新表；测试：禁用→启用旧Token401、新登录有效、本人保护与历史内容；文档：区别退出与禁用，更新F-004/035、BR-004，版本方案仅候选。 |
| 是否影响基线 | 指定目标可以作为候选，本条本期列明范围已于12.5-D批准；不是实现/验证完成；失效机制及与登录/启停竞争的原子边界待设计；D已确认受保护路由核实及下一次鉴权生效，具体机制待设计。 |
| 是否可推迟 | 服务器注销可推迟；启用后旧Token政策建议本期明确。 |
| 用户决策 | 用户已确认指定范围，2026-10-08 ChatGPT对话12.4-B；失效机制及与登录/启停竞争的原子边界待设计；D已确认受保护路由核实及下一次鉴权生效，具体机制待设计。 |

### Q04 SMTP真实收件与密码重置闭环

| 项目 | 当前记录 |
| --- | --- |
| 用户决策 | 已确认指定范围，2026-10-08 ChatGPT对话12.4-E；原推荐不扩展为所有技术方案获批 |
| 已确认目标 | 本期验收必须覆盖请求验证码、真实SMTP发送、真实邮箱收件、输入验证码、设置新密码和新密码登录；接口成功不等于收件。错误/过期/已使用验证码必须验证；无可用环境如实记录未执行或环境受限，不免除真实收件目标，不公开凭据。 |
| 当前行为 | ForgotPassword.vue发送成功后60秒倒计时，重置后回登录；PasswordResetServiceImpl生成六位码、5分钟有效、60秒间隔，新码废旧码，条件更新used后更新BCrypt密码；EmailServiceImpl通过JavaMailSender发送。SMTP变量默认账号/授权为空，MAIL_FROM回退SMTP_USERNAME，邮件在数据库事务内发送。配置存在不证明收件；邮箱重置未递增token_version，密码校验未满足Q19码点/字节目标。 |
| 差异 | 真实投递/收件/闭环均未执行，发送成功后数据库提交失败可能产生已送达但不可用验证码，事务不能撤回邮件；继续保留Q05旧JWT失效及Q19新密码与旧哈希兼容缺口，不伪造新缺陷。 |
| 真实文件 | [ForgotPassword.vue](../../blog-web/src/views/public/ForgotPassword.vue)、[auth.js](../../blog-web/src/api/auth.js)、[AuthController.java](../../blog-server/src/main/java/com/example/blog/controller/AuthController.java)、[PasswordResetServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/PasswordResetServiceImpl.java)、[EmailServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/EmailServiceImpl.java)、[application.yml](../../blog-server/src/main/resources/application.yml)、[.env.example](../../.env.example) |
| 后续影响 | 复用sys_user和email_verification_code；后续独立测试库准备，不改真实库。 保留POST /api/auth/password/reset-code、/reset；真实收件是外部验证，不新增读取验证码接口。 SMTP受外网和服务商影响，数据库与外部邮件不具原子性；证据必须脱敏。 |
| 待设计/补充/执行 | 测试SMTP服务/收件邮箱、等待上限和脱敏证据形式、邮件与数据库提交失败后的重试反馈待设计/补充；不要求新增投递队列。 |
| 验收目标 | 独立测试库和受控真实测试邮箱：保存发送时间和脱敏收件证据，正常码完成重置并用新密码登录；错误、5分钟到期、单次消费、旧码、新旧密码、禁用状态与发送失败回归；两个路径旧Token失效按Q05、新设置按Q19。没有SMTP环境则标未执行/受限，不写通过。 正式测试用例待分配ID；未执行 |
| 是否需要实施 | 后续需真实环境验证及Q05/Q19受控修复；本轮不发邮件、不测试。 |
| 基线准备 | 指定目标可提交候选；未测不自动排除目标，但未定复合范围、验收参数和风险仍须审查 |
| 关联需求 | BLOG-F-006、BLOG-F-007、BLOG-BR-007、BLOG-BR-008；仅对应子范围 |

### Q06 首页与列表置顶范围

| 项目 | 当前记录 |
| --- | --- |
| 用户真实决策 | 2026-10-08 ChatGPT异步确认，用户12.4-D请求第三节；已确认指定范围，未建立基线 |
| 完整问题及目标 | 本期保留文章列表置顶、is_top字段及后台置顶设置；首页不新增独立置顶区，独立区域仅为后续版本候选，不纳入本期必需验收。 |
| 当前证据 | Home.vue只调用latest(5)/hot(3)；ArticleManage.vue保存isTop；pagePublicArticles按is_top DESC、published_at DESC；featured接口保留但首页未调用。 |
| 实现差异 | 范围与现有调用一致，未发现需为Q06新增首页区域的缺陷；同值排序另按Q09，不删除现有接口/字段。 |
| 代码依据 | [Home.vue](../../blog-web/src/views/public/Home.vue)、[ArticleList.vue](../../blog-web/src/views/public/ArticleList.vue)、[ArticleManage.vue](../../blog-web/src/views/admin/ArticleManage.vue)、[ArticleCard.vue](../../blog-web/src/components/ArticleCard.vue)、[ArticleServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/ArticleServiceImpl.java)、[PublicArticleController.java](../../blog-server/src/main/java/com/example/blog/controller/PublicArticleController.java)、[Article.java](../../blog-server/src/main/java/com/example/blog/entity/Article.java) |
| 影响与风险 | 保留blog_article.is_top及数据；无需为本决策DDL或清除置顶值。 GET /api/public/articles、/featured、/latest、/hot及POST/PUT /api/admin/articles契约保留，不新增首页区域接口。 不要将ArticleCard的featured样式属性误称置顶数据源；保留列表置顶不能误变为首页按置顶排序。 |
| 待设计验收 | 置顶已发布文章在列表优先，草稿及已删置顶不公开；后台置顶保存/取消仍有效；首页仍最新/热门，无独立置顶区不判失败；Q12保存失败不报成功。；用例待分配ID，未执行 |
| 本期与后续边界 | 维持现有范围，Q06本身无新增开发；Q09稳定排序需另行设计。 首页独立置顶区域本期暂缓，后续候选，非永久取消。 |
| 保留未定细节 | 置顶同值次级字段随Q09设计；未批准首页新区域或删除featured接口。 |
| 基线影响 | 已定子范围可作目标候选，未实现不自动阻塞；本条其他复合范围/优先级及残余参数未自动批准 |
| 需求关联 | BLOG-F-009、BLOG-F-030 |


### Q07 筛选、URL与浏览器历史一致性

| 项目 | 当前记录 |
| --- | --- |
| 用户真实决策 | 2026-10-08 ChatGPT异步确认，用户12.4-D请求第三节；已确认指定范围，未建立基线 |
| 完整问题及目标 | 筛选控件、当前URL有效查询参数和结果一致；合法直达URL、刷新及前进/后退应恢复条件与结果；不要求每次筛选都新增历史记录。 |
| 当前证据 | ArticleList.vue仅setup从route.query读page/categoryId/tagId/keyword；watch本地page/categoryId/tagId并router.replace、load；search也replace并load；未watch route.query。router复用/articles组件时本地状态未见回填。 |
| 实现差异 | 静态可定位同组件历史导航缺少query→控件/查询同步路径，不能将代码缺口写成已实际复现失败；首次有效URL有初始化代码。请求交错风险待验证。 |
| 代码依据 | [ArticleList.vue](../../blog-web/src/views/public/ArticleList.vue)、[index.js](../../blog-web/src/router/index.js)、[blog.js](../../blog-web/src/api/blog.js)、[PublicArticleController.java](../../blog-server/src/main/java/com/example/blog/controller/PublicArticleController.java)、[ArticleQuery.java](../../blog-server/src/main/java/com/example/blog/dto/ArticleQuery.java)、[ArticleServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/ArticleServiceImpl.java) |
| 影响与风险 | 既有blog_article/category/tag/article_tag查询复用，无DDL或数据修改要求。 GET /api/public/articles使用page、size、keyword、categoryId、tagId；前端每页8；不新增接口。 双向watch可能回环/重复加载；快速导航旧响应可能覆盖新结果；URL条件与认证redirect分开，不能因此放开保护接口。 |
| 待设计验收 | 合法带page/categoryId/tagId/keyword直达及刷新恢复控件、请求、结果；在已有不同有效URL历史项之间前进/后退正确回填并查询；清筛选恢复当前有效条件；replace可保留，不以每次产生历史项判定；结果按当前有效条件显示。；用例待分配ID，未执行 |
| 本期与后续边界 | 需后续设计query回填与请求更新策略，代码修改另获授权；不是全部筛选功能未实现。 无新延期的基础同步目标；不要求逐操作新增浏览器历史。 |
| 保留未定细节 | 非法/多值参数纠正、有效性判断、查询参数规范化、组合冲突边界及是否重置越界页待设计或必要确认；现有AND组合是代码证据，不将本次Q07当作新增组合规则批准。 |
| 基线影响 | 已定子范围可作目标候选，未实现不自动阻塞；本条其他复合范围/优先级及残余参数未自动批准 |
| 需求关联 | BLOG-F-010 |


### Q09 现有统计口径与确定性排序

| 项目 | 当前记录 |
| --- | --- |
| 用户真实决策 | 2026-10-08 ChatGPT异步确认，用户12.4-D请求第三节；已确认指定范围，未建立基线 |
| 完整问题及目标 | 保留当前阅读、趋势及热门主要指标口径；主指标相同记录补确定性次级排序；本期不新增访客去重或独立发布事件历史系统。具体次级字段未批准。 |
| 当前证据 | getPublicArticle确认未删PUBLISHED后事务view_count+1，后台详情不计阅读；热门view_count/like_count/published_at依次降序，无周期过滤；趋势近7个日期按当前PUBLISHED且DATE(published_at)统计，逻辑删除过滤。首次发布设published_at、持续发布编辑保留、撤回置null、再次发布新时间。 |
| 实现差异 | 文章列表is_top/published_at DESC；最新/featured/喜欢按published_at DESC；相邻published_at ASC；后台updated_at DESC；这些排序末尾无唯一决胜字段，完全同值顺序未由代码确定。不宣称已有不稳定结果实测。 |
| 代码依据 | [ArticleServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/ArticleServiceImpl.java)、[AdminController.java](../../blog-server/src/main/java/com/example/blog/controller/AdminController.java)、[Article.java](../../blog-server/src/main/java/com/example/blog/entity/Article.java)、[PublicArticleController.java](../../blog-server/src/main/java/com/example/blog/controller/PublicArticleController.java)、[Home.vue](../../blog-web/src/views/public/Home.vue)、[ArticleList.vue](../../blog-web/src/views/public/ArticleList.vue)、[ArticleDetail.vue](../../blog-web/src/views/public/ArticleDetail.vue)、[Profile.vue](../../blog-web/src/views/public/Profile.vue)、[AdminDashboard.vue](../../blog-web/src/views/admin/AdminDashboard.vue)、[ArticleManage.vue](../../blog-web/src/views/admin/ArticleManage.vue) |
| 影响与风险 | 复用view_count、like_count、is_top、published_at、updated_at；不增访问去重表或发布事件表，是否需索引优化后续设计，不迁移数据。 保留文章列表/详情/latest/hot/featured/adjacent、本人likes、后台列表和/dashboard/publish-trend路径；同值顺序可能改变，返回结构不必变。 稳定排序必须与相邻顺序一致；动态数据变化本身不保证跨请求快照一致，验收固定数据集。时区/日期以现有服务LocalDate.now及SQL DATE为现状，不自行增新时区规则。Q12统计查询失败不可显示真实0。 |
| 待设计验收 | 成功有效公开详情每请求+1，列表/邻文/后台详情不+1；草稿/已删详情不计阅读；热门保留现有全站有效文章三指标顺序，无新统计周期；趋势核对当前发布集/撤回/再发；构造全部主排序指标相同数据，重复查询固定数据集及翻页/相邻导航顺序确定，不改变原主优先级；不要求唯一访客或事件历史。；用例待分配ID，未执行 |
| 本期与后续边界 | 确定性次级排序需后续设计/补齐；统计公式不重建，未修复未执行。 本期不新增访客去重、独立发布事件历史系统；不是取消阅读量/趋势。 |
| 保留未定细节 | 末级唯一id及其方向可作设计建议，非用户已批准字段组合；主指标不变，不将SQL默认返回顺序当稳定保证。 |
| 基线影响 | 已定子范围可作目标候选，未实现不自动阻塞；本条其他复合范围/优先级及残余参数未自动批准 |
| 需求关联 | BLOG-F-009、BLOG-F-010、BLOG-F-013、BLOG-F-017、BLOG-F-025、BLOG-F-026、BLOG-F-028、BLOG-F-030、BLOG-BR-013、BLOG-BR-016、BLOG-BR-017 |


### Q10 并发互动、审核及验证码的一致性验收

| 项目 | 内容 |
| --- | --- |
| 完整问题 | 是否将并发点赞/取消、同评论批量重审与发码60秒间隔纳入必要验收？测试规模和失败时处理需明确。 |
| 当前实际行为（确认目标尚未修复） | 点赞联合键及关系变动计数；CommentAdminServiceImpl先读旧状态再写，没有显式竞争控制；发码查询最新时间后生成，没有已验证并发互斥。事务标记不证明竞争安全。 |
| 代码依据 | E07/E14/E04；InteractionServiceImpl、[评论审核服务](../../blog-server/src/main/java/com/example/blog/service/impl/CommentAdminServiceImpl.java)、PasswordResetServiceImpl。 |
| 为什么需确认 | 计数与验证码身份安全属于核心规则，不能把顺序幂等作为并发验收；需决定测试范围，不预设锁方案。 |
| 可选方案 | A：本期独立库做最小竞争用例：每场景2个同时请求，重复10轮，再核对关系/状态/计数；同邮箱并发发码60秒窗口至多一个被接受；失败再授权修复；B：本期仅顺序验收，明确并发未保证与风险接受；C：更高并发压力按实际规模另定。 |
| 原推荐（不是全量批准） | 保留下面原选项仅作评审历史；用户实际只确认：在独立测试数据库对点赞/取消点赞、评论审核、同邮箱验证码发送等现有清单并发场景，每场景2个并发请求、重复10轮，验证最终状态、计数、唯一性和业务约束；不得破坏真实业务库，不称高并发性能认证。 原建议未覆盖/未被批准部分仍待确认。 |
| 推荐理由 | 两个竞争请求即可暴露典型竞态，成本低于大规模压测；不把一致性与Q17吞吐性能混同。 |
| 前端/后端/数据库/测试/文档影响 | 前端：双会话/快速点击测试，防重复按钮不代替后端；后端：失败后评估条件更新/锁等候选；数据库：测试独立库，机制决定后才判断迁移，现阶段无必须DDL；测试：核对状态与计数、不只HTTP成功，审核失败整批回滚、发码接受/实际投递区分；文档：F-016/037、NF-007、BR-007/018/021/022增并发预期，未执行。 |
| 是否影响基线 | 指定目标可以作为候选，本条本期列明范围已于12.5-D批准；不是实现/验证完成；测试用例ID待分配；数据夹具、同步起跑和邮件模拟方式待测试设计，Q04真实收件目标已确认，SMTP环境与证据待设计，未执行，锁/条件更新方案未批准。 |
| 是否可推迟 | 高并发扩展可后移；最小竞争正确性不建议以低流量为由自动忽略。 |
| 用户决策 | 用户已确认指定范围，2026-10-08 ChatGPT对话12.4-B；测试用例ID待分配；数据夹具、同步起跑和邮件模拟方式待测试设计，Q04真实收件目标已确认，SMTP环境与证据待设计，未执行，锁/条件更新方案未批准。 |

### Q11 公开评论展示与图片头像边界

| 项目 | 当前记录 |
| --- | --- |
| 用户真实决策 | 2026-10-08 ChatGPT异步确认，用户12.4-D请求第三节；已确认指定范围，未建立基线 |
| 完整问题及目标 | 本期保留公开评论展示及首字头像；评论分页、评论图片头像暂缓至后续候选；不取消个人中心头像上传或既有留言分页。 |
| 当前证据 | approvedComments要求发布文章，取全部有效APPROVED，created_at DESC无分页；ArticleDetail.vue渲染列表并取nickname.slice(0,1)，未用VO.avatar图片。Profile与上传接口独立存在。 |
| 实现差异 | 现状符合本期展示范围，不因没有分页/图片头像认定本期缺陷；性能风险仍需记录，不据此自动增加分页开发。 |
| 代码依据 | [ArticleDetail.vue](../../blog-web/src/views/public/ArticleDetail.vue)、[InteractionServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/InteractionServiceImpl.java)、[CommentVO.java](../../blog-server/src/main/java/com/example/blog/vo/CommentVO.java)、[Profile.vue](../../blog-web/src/views/public/Profile.vue)、[auth.js](../../blog-web/src/store/auth.js)、[UserProfileController.java](../../blog-server/src/main/java/com/example/blog/controller/UserProfileController.java)、[AvatarStorageServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/AvatarStorageServiceImpl.java) |
| 影响与风险 | blog_comment审核/逻辑删除及sys_user.avatar保留，不删除头像路径或文件；无需本决策DDL。 GET /api/public/articles/{id}/comments仍列表；POST /api/users/me/avatar不取消、不改权限；不擅自新增公开评论page/size契约。 公开评论无界返回、大量评论渲染耗时/载荷风险；Q17验收数据规模待定，不声称性能达标，也不自动增开发任务。 |
| 待设计验收 | 有效文章只显示未删APPROVED；待审/拒绝不可见，禁用用户历史通过评论保留；显示名首字方式保留；无分页/图片头像不判本期功能失败；个人中心本地头像上传、预览/取消、全局同步保持，需另按原需求回归。；用例待分配ID，未执行 |
| 本期与后续边界 | 维持现有范围，主要记录和回归；本期不开发评论分页/图片渲染。 评论区域分页与图片头像增强本期暂缓，仅后续版本候选，非永久取消。个人中心头像上传明确保留。 |
| 保留未定细节 | 性能测试规模归Q17；首字Unicode改进未在本次批准，不把现有slice(0,1)改称码点截取。 |
| 基线影响 | 已定子范围可作目标候选，未实现不自动阻塞；本条其他复合范围/优先级及残余参数未自动批准 |
| 需求关联 | BLOG-F-018、BLOG-NF-008 |


### Q12 更新失败、统计错误与配置批量保存的反馈

| 项目 | 内容 |
| --- | --- |
| 完整问题 | 改密更新0行是否算失败？统计接口失败能否显示为真实零？配置多键保存与缺失键写入是否必须原子成功？ |
| 当前实际行为（确认目标尚未修复） | UserServiceImpl.changePassword未检查updateById；概览错误文案称接口尚未接入；AdminController.saveConfigs逐键无事务，新键insert时尚未赋value，随后更新。现状风险不等于已复现失败。 |
| 代码依据 | E02/E10/E12；UserServiceImpl、[概览页](../../blog-web/src/views/admin/AdminDashboard.vue)、AdminController、PublicController。 |
| 为什么需确认 | 用户应能分辨成功/失败与暂无数据，改密失效目标依赖真正保存成功；配置半批变化影响站点一致性。 |
| 可选方案 | A：本期明确0行/数据库失败不得报成功，配置合法批次全成功或全回滚，缺失允许键先赋值后保存，统计失败标未知/失败不伪装零；B：先只修文案，其余保留限制；C：逐项分拆并由用户批准最小安全部分。 |
| 原推荐（不是全量批准） | 保留下面原选项仅作评审历史；用户实际只确认：更新/保存失败不报告成功，提供明确适当错误；合法配置批次全部成功或全部回滚；统计查询失败不得伪装真实0；HTTP请求成功不等于业务写入成功。 原建议未覆盖/未被批准部分仍待确认。 |
| 推荐理由 | 属于可靠性而非额外业务，避免基线承诺成功但实际未持久化；技术事务实现仍后续设计。 |
| 前端/后端/数据库/测试/文档影响 | 前端：错误卡片/重试、表单失败保持、不能真假零混同；后端：行数检查、输入/批量原子性设计；数据库：无必须改表，独立库/模拟失败不碰真实数据；测试：0行、DB异常、配置新键/null/中途失败、部分统计失败；文档：F-022/025/040、NF-010/014明确失败语义，与Q05/Q15协同。 |
| 是否影响基线 | 指定目标可以作为候选，本条本期列明范围已于12.5-D批准；不是实现/验证完成；错误文案、具体异常映射及幂等无变更的成功判定待设计，不把所有更新返回0都机械视作失败；本次通用原则的其他写接口回归范围须测试设计。 |
| 是否可推迟 | 非关键错误文案样式可后移，不能把失败作为成功交付。 |
| 用户决策 | 用户已确认指定范围，2026-10-08 ChatGPT对话12.4-B；错误文案、具体异常映射及幂等无变更的成功判定待设计，不把所有更新返回0都机械视作失败；本次通用原则的其他写接口回归范围须测试设计。 |

### Q13 邮箱与用户名规范化及历史账号兼容

| 项目 | 当前记录 |
| --- | --- |
| 用户决策 | 已确认指定范围，2026-10-08 ChatGPT对话12.4-E；原推荐不扩展为所有技术方案获批 |
| 已确认目标 | 新注册/真实修改邮箱入口去首尾空格并统一小写，唯一性按规范化结果判断；注册/修改用户名去首尾空格，保留大小写，不新增字符集限制，最大50沿用Q01。历史账号不自动合并、批量改值、删除或重命名；冲突先审计再提交方案。 |
| 当前行为 | UserServiceImpl.register/updateProfile已username.trim()并查询唯一性；register直接用request.email查询及保存，不trim/lowercase；login按原username查询，只有用户名登录，没有邮箱登录。PasswordResetServiceImpl邮箱trim().toLowerCase()；DTO @Email在Service规范化前执行；UserMapper SQL等值，schema两个唯一索引采用utf8mb4_unicode_ci，大小写比较受collation而非Java字符串控制。Profile修改仅username，无邮箱修改API。 |
| 差异 | 注册与找回邮箱口径不统一；前置DTO或前端邮箱校验可能先拒绝带空格输入，需设计先规范化再校验路径。用户名已trim但DTO对原始长度先校验，Q01长度计数和登录输入/历史兼容仍需设计。当前库是否冲突未审计。 |
| 真实文件 | [Login.vue](../../blog-web/src/views/admin/Login.vue)、[Profile.vue](../../blog-web/src/views/public/Profile.vue)、[ForgotPassword.vue](../../blog-web/src/views/public/ForgotPassword.vue)、[UserServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/UserServiceImpl.java)、[PasswordResetServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/PasswordResetServiceImpl.java)、[RegisterRequest.java](../../blog-server/src/main/java/com/example/blog/dto/RegisterRequest.java)、[LoginRequest.java](../../blog-server/src/main/java/com/example/blog/dto/LoginRequest.java)、[UpdateProfileRequest.java](../../blog-server/src/main/java/com/example/blog/dto/UpdateProfileRequest.java)、[UserMapper.java](../../blog-server/src/main/java/com/example/blog/mapper/UserMapper.java)、[schema.sql](../../blog-server/src/main/resources/schema.sql) |
| 后续影响 | 保留sys_user唯一索引，先设计只读规范化冲突审计；如需迁移必须另授权，不自动合并或更新。 已有注册/login、PUT /api/users/me、密码找回请求输入处理可能变化；不新增邮箱登录或修改邮箱API。 数据库大小写/重音等价与应用规范化不是同一机制；历史空格、大小写和前置校验影响兼容。 |
| 待设计/补充/执行 | C已确认三入口现有trim、50 Unicode码点、沿用数据库比较、邮箱先规范化及历史审计原则；实现与个案处理待设计；没有邮箱登录/改邮箱功能，本次不批准新增。 |
| 验收目标 | 新注册混合大小写/首尾空格邮箱保存规范值，变体重复拒绝；注册与找回相同身份邮箱路径一致；合法用户名首尾空格去除，内部字符及大小写不擅改，50上限回归；旧账号数据及哈希不自动变化，冲突仅登记/方案待批准。邮箱修改不存在，不虚构其执行测试；未来获准新增入口再适用。 正式测试用例待分配ID；未执行 |
| 是否需要实施 | 邮箱注册规范化及前后端校验顺序需后续设计实施，用户名现有trim保留。 |
| 基线准备 | 指定目标可提交候选；未测不自动排除目标，但未定复合范围、验收参数和风险仍须审查 |
| 关联需求 | BLOG-F-001、BLOG-F-002、BLOG-F-006、BLOG-F-007、BLOG-F-020、BLOG-BR-002；仅对应子范围 |

### Q14 文章逻辑删除及历史关联政策

| 项目 | 当前记录 |
| --- | --- |
| 用户真实决策 | 2026-10-08 ChatGPT异步确认，用户12.4-D请求第三节；已确认指定范围，未建立基线 |
| 完整问题及目标 | 保留现有文章逻辑删除、评论/点赞历史留存及文章标签关联清理；本期不增回收站或独立物理清理。确认核实范围，不批准未知清理行为。 |
| 当前证据 | deleteArticle事务内先删当前文章blog_article_tag，再articleMapper.deleteById；Article.deleted有@TableLogic，配置deleted=1为删除值。未删除blog_comment/blog_article_like。标准ArticleMapper查询过滤deleted，公开只PUBLISHED；本人likes关系留存但文章结果排除已删。文章统计排除已删；评论统计按有效评论本身，不联表排除已删文章。 |
| 实现差异 | 代码和SQL逻辑/物理边界一致：逻辑删不触发CASCADE，标签关系是显式物理删除。保留评论可能在后台显示“文章已删除”，不等于记录丢失。未发现需虚构回收站修复。 |
| 代码依据 | [ArticleServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/ArticleServiceImpl.java)、[Article.java](../../blog-server/src/main/java/com/example/blog/entity/Article.java)、[ArticleMapper.java](../../blog-server/src/main/java/com/example/blog/mapper/ArticleMapper.java)、[AdminController.java](../../blog-server/src/main/java/com/example/blog/controller/AdminController.java)、[CommentAdminServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/CommentAdminServiceImpl.java)、[application.yml](../../blog-server/src/main/resources/application.yml)、[schema.sql](../../blog-server/src/main/resources/schema.sql)、[ArticleManage.vue](../../blog-web/src/views/admin/ArticleManage.vue)、[CommentManage.vue](../../blog-web/src/views/admin/CommentManage.vue)、[Profile.vue](../../blog-web/src/views/public/Profile.vue) |
| 影响与风险 | blog_article标记删除；仅blog_article_tag关系显式清理；评论/点赞不物理删。分类逻辑删除检查与物理RESTRICT区别保留；不读/改真实库、不批量清历史。 DELETE /api/admin/articles/{id}及既有查询契约保留；无恢复/物理清理API。公开文章或评论读取已删文章报业务错误；不误称所有统计都排除其历史评论。 保留关系不等于可公开访问文章；已删slug唯一约束和FK继续存在。对已删文章后台评论审核更新文章计数受逻辑过滤可能0行是需Q12/Q10另设计验证的风险，不批准新清理或新审核禁令。 |
| 待设计验收 | 删除后blog_article.deleted=1，公开/后台常规文章列表、详情、热门/最新、本人喜欢结果和文章统计排除；评论/点赞行保留、标签关联清空；已删文章公开评论接口因文章不可访问而拒绝，不把历史通过评论继续公开为目标；评论总数不误要求随文章删除减少；分类删除按有效文章引用检查，物理外键RESTRICT仍有效；Q12删除失败不报成功。；用例待分配ID，未执行 |
| 本期与后续边界 | 维持现有已核实政策，准备删除回归；影响行数与失败边界由Q12，不能据保留政策宣称所有删除故障已解决。 本期不新增回收站及独立物理清理机制；未来若改变需真实需求变更。 |
| 保留未定细节 | 实际库结构/历史数据未核验，交付迁移归Q18；物理清理不在本轮批准。 |
| 基线影响 | 已定子范围可作目标候选，未实现不自动阻塞；本条其他复合范围/优先级及残余参数未自动批准 |
| 需求关联 | BLOG-F-029、BLOG-F-031、BLOG-BR-015、BLOG-BR-010、BLOG-F-012、BLOG-F-017、BLOG-F-025 |


### Q15 站点配置公开白名单与值校验

| 项目 | 内容 |
| --- | --- |
| 完整问题 | 五键、null、空值和字段上限已定；缺失键/单字段批量语义、默认策略如何决定？现有数据如何兼容？ |
| 当前实际行为（确认目标尚未修复） | AdminController接任意Map写键，PublicController全表toMap；页面只五键。config_value可空，null映射有异常风险，实际未测；公开表不应放凭证。 |
| 代码依据 | E12/E05/E16；AdminController.saveConfigs、[PublicController](../../blog-server/src/main/java/com/example/blog/controller/PublicController.java)、[系统管理页](../../blog-web/src/views/admin/SystemManage.vue)、SiteConfig。 |
| 为什么需确认 | 管理员误存密钥可能公开，必须确定公开/写入契约；白名单与Q12批量原子保存须相容，允许键缺失处理尚未定。 |
| 原可选方案（历史，不替代当前已确认目标） | A：公开读取及写入限定siteName/siteSubtitle/homeIntro/aboutContent/email，未知键拒绝且整批不变；null拒绝、空字符串是否允许按字段单独确认；B：读限定五键，后台任意键但划清不可保存秘密及值校验；C：分类公开/私有配置机制，新增设计。 |
| 原推荐（不是全量批准） | 保留下面原选项仅作评审历史；用户实际只确认：仅siteName、siteSubtitle、homeIntro、aboutContent、email五键可公开读取和由ADMIN保存，未知键拒绝写入且不得公开，五字段均拒绝null。siteName必填，保存前去除首尾空格，处理后1—50字符；siteSubtitle允许空字符串、最多100字符；homeIntro允许空字符串、最多500字符；aboutContent允许空字符串、最多5000字符；email允许空字符串，非空须符合合理邮箱格式、最多254字符，非SMTP账号。批量保存继续遵守Q12全部成功或全部回滚。 原建议未覆盖/未被批准部分仍待确认。 |
| 推荐理由 | 当前页面本来五键，白名单成本小且风险可解释；无需引入私有配置平台。 |
| 前端/后端/数据库/测试/文档影响 | 前端：五项校验；后端：白名单、null及空值处理与Q12原子性；数据库：无需改表，额外键先只读盘点，不擅删除；测试：允许键新增/修改、未知键+合法键批次全拒、null、空邮箱、公开不返回额外键；文档：F-040、BR-024、NF-005，不混联系邮箱与SMTP。 |
| 是否影响基线 | 指定目标可以作为候选，本条本期列明范围已于12.5-D批准；不是实现/验证完成；待设计或确认：A/B已确认部分更新、缺失保留、空值区分、初始化与保存分离、Unicode码点与现有trim；邮箱校验器、历史审计及失败反馈实现待设计。按A已确认的请求语义设计；其他四项未获准自动trim。 |
| 是否可推迟 | 额外/私有配置功能可后移；公开白名单不建议自动接受风险。 |
| 用户决策 | 用户已确认指定范围及字段细化，2026-10-08 ChatGPT对话12.4-B及12.4-C；待设计或确认：A/B已确认部分更新、缺失保留、空值区分、初始化与保存分离、Unicode码点与现有trim；邮箱校验器、历史审计及失败反馈实现待设计。按A已确认的请求语义设计；其他四项未获准自动trim。 |

### Q16 初始化数据与统计计数一致性

| 项目 | 当前记录 |
| --- | --- |
| 用户决策 | 已确认指定范围，2026-10-08 ChatGPT对话12.4-E；原推荐不扩展为所有技术方案获批 |
| 已确认目标 | 在独立干净测试数据库执行初始化并核对表、种子和冗余统计；点赞/评论数量应符合现有已确认统计口径，发现不一致后另授权修SQL，不改真实业务库。 |
| 当前行为 | schema.sql定义10表；种子3用户、3分类、5标签、3文章（2发布1草稿）、8条文章标签关系、3评论（2通过1待审）、3点赞、2通过留言、5配置；验证码无种子。第一篇like/comment=2/2与记录吻合，第二篇like/comment=1/1但只有1个点赞和1条PENDING评论（按已通过数应为0）。这仅是全新库静态预期，未执行SQL。 |
| 差异 | 第二篇comment_count种子与APPROVED统计存在明确脚本层面差异；实际运行库未读取，不能推断线上计数。重复执行UPSERT不等同初始化/计数自动校准，不能覆盖已有库。 |
| 真实文件 | [schema.sql](../../blog-server/src/main/resources/schema.sql)、[migration-v2.sql](../../blog-server/src/main/resources/migration-v2.sql)、[migration-v3-email-reset.sql](../../blog-server/src/main/resources/migration-v3-email-reset.sql)、[InteractionServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/InteractionServiceImpl.java)、[CommentAdminServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/CommentAdminServiceImpl.java)、[README.md](../../sql/README.md) |
| 后续影响 | 后续修正种子及独立初始化验证，不扩大为真实历史数据批量修复；本轮不改SQL。 不新增API；验证公开文章和后台统计与既定口径一致，文章删除后历史评论统计边界沿Q14。 初始化UPSERT可覆盖密码/角色/配置等演示数据；必须与旧库升级分离。 |
| 待设计/补充/执行 | 独立库名称及防误连方案、脚本硬编码CREATE/USE隔离执行方式、夹具/查询清单及计数核验实测待设计/执行；不批准自动校准真实库。 |
| 验收目标 | 独立空库执行经隔离设计的脚本，核对10表及种子数量、状态、FK/唯一约束，逐篇比对like_count与点赞行、comment_count与有效APPROVED行；留存环境/步骤/预期/实测差异；计数修正和复验需后续授权，不预填通过。 正式测试用例待分配ID；未执行 |
| 是否需要实施 | 需要后续初始化测试、最小SQL修正及回归，均未开展。 |
| 基线准备 | 指定目标可提交候选；未测不自动排除目标，但未定复合范围、验收参数和风险仍须审查 |
| 关联需求 | BLOG-NF-007、BLOG-BR-018、BLOG-BR-022；仅对应子范围 |

### Q17 PC浏览器验收与性能方案

| 项目 | 当前记录 |
| --- | --- |
| 用户决策 | 已确认指定范围，2026-10-08 ChatGPT对话12.4-E；原推荐不扩展为所有技术方案获批 |
| 已确认目标 | 本期主交付PC Web，覆盖桌面Chrome和Edge功能验收；390px等窄屏专项增强暂缓但不破坏既有响应式。必须建立可执行性能验收方案，明确环境、对象、步骤、采样和判定；G阈值、负载、工具和环境记录方式已确认；真实硬件/版本在执行前登记。 |
| 当前行为 | Vue/Vite页面有响应式样式；无本轮浏览器/性能实测。SRS性能草案1000篇、10并发、每接口100次、P95≤2秒，以及1366×768/1920×1080分辨率和390px检查均为此前建议，不能因同意方向而视作参数批准。Q10两请求10轮是独立最小竞争测试，不是性能负载指标。 |
| 差异 | 桌面兼容方向已定但实际版本、分辨率待记录；性能环境/接口/采样/阈值需补充确认，不宣称达标；窄屏不再本期必需验收项，保留样式。 |
| 真实文件 | [main.css](../../blog-web/src/assets/main.css)、[index.js](../../blog-web/src/router/index.js)、[ArticleList.vue](../../blog-web/src/views/public/ArticleList.vue)、[ArticleDetail.vue](../../blog-web/src/views/public/ArticleDetail.vue)、[Profile.vue](../../blog-web/src/views/public/Profile.vue)、[ArticleManage.vue](../../blog-web/src/views/admin/ArticleManage.vue)、[AdminDashboard.vue](../../blog-web/src/views/admin/AdminDashboard.vue)、[vite.config.js](../../blog-web/vite.config.js)、[ArticleServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/ArticleServiceImpl.java) |
| 后续影响 | 仅后续独立性能数据集，不注入真实库；无本轮DDL。 性能对象从实际列表/详情/热门/后台接口选取，SMTP外部延迟单独记录，不造新接口。 无界评论返回是风险而不是自动增加分页任务；10并发草案不得与Q10竞争目标混同。 |
| 待设计/补充/执行 | G及12.5-C已确认数据规模、接口、10客户端、20预热/100采样、计时、P95≤2秒、工具、4,096 UTF-8字节正文和桌面视口；真实版本/环境登记、夹具生成/核验、证据实现留测试设计。 |
| 验收目标 | 后续在记录实际版本的桌面Chrome/Edge完成注册登录、文章筛选详情、评论留言、资料/头像、后台编辑/审核等场景；性能先批准参数表再在独立数据环境采集与判定，未执行记未执行；390px专项未完成不判本期必需功能失败，不主动破坏既有布局。 正式测试用例待分配ID；未执行 |
| 是否需要实施 | 需编写测试计划/参数确认并真实执行；窄屏专项增强后续候选。 |
| 基线准备 | 指定目标可提交候选；未测不自动排除目标，但未定复合范围、验收参数和风险仍须审查 |
| 关联需求 | BLOG-NF-008、BLOG-NF-009、BLOG-NF-011、BLOG-NF-012；仅对应子范围 |

### Q18 数据库初始化、升级与备份恢复交付

| 项目 | 当前记录 |
| --- | --- |
| 用户决策 | 已确认指定范围，2026-10-08 ChatGPT对话12.4-E；原推荐不扩展为所有技术方案获批 |
| 已确认目标 | 交付包含空库初始化、已有库升级风险与操作、备份恢复说明；新库与旧库不能无条件覆盖混用；恢复验证独立环境且同步数据库avatar路径与本地头像文件；本期不强制自动迁移框架，不声称已恢复。 |
| 当前行为 | README及sql/README索引实际schema、migration-v2（ADD token_version、留言默认PENDING）、migration-v3（验证码建表）。Spring配置没有自动初始化SQL开关，本地环境变量需单独设置，.env不自动读取。AvatarStorageService以配置路径转绝对根目录，存UUID文件，库仅相对URL，Git忽略运行上传。尚无正式备份/恢复操作说明或独立演练证据。 |
| 差异 | 已有运行/初始化概述不能替代可核对的升级检查和完整恢复文档；v2重复ADD会失败，CREATE IF NOT EXISTS不升级旧表；UPSERT可覆盖演示账号/配置，恢复仅数据库将遗漏图片。 |
| 真实文件 | [README.md](../../README.md)、[README.md](../../blog-server/README.md)、[.env.example](../../.env.example)、[application.yml](../../blog-server/src/main/resources/application.yml)、[schema.sql](../../blog-server/src/main/resources/schema.sql)、[migration-v2.sql](../../blog-server/src/main/resources/migration-v2.sql)、[migration-v3-email-reset.sql](../../blog-server/src/main/resources/migration-v3-email-reset.sql)、[AvatarStorageServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/AvatarStorageServiceImpl.java)、[WebConfig.java](../../blog-server/src/main/java/com/example/blog/config/WebConfig.java)、[README.md](../../sql/README.md) |
| 后续影响 | 保留既有三SQL文件，不虚构其他迁移；后续独立恢复与必要增量方案另授权。 无新增接口；恢复后按当前/api和/uploads/avatars映射验证应用，环境秘密不随文档公开。 误灌旧库、重复ALTER、快照时间不一致或漏头像；邮件/JWT环境凭据只通过本地安全配置，备份需受控。 |
| 待设计/补充/执行 | 旧库来源/当前结构、迁移适用判定、独立目标库与恢复机器、备份工具/一致性时点/头像快照方案、具体恢复检查和回退步骤待设计；不批准真实库执行。 |
| 验收目标 | 后续分别编写新空库、旧库预检查/备份/适用迁移/回退、恢复说明；独立环境验证表/记录/约束/配置与头像文件可读，记录实际步骤与证据；恢复不得覆盖真实库，旧库升级先结构审计；文档完成和演练执行分别登记。 正式测试用例待分配ID；未执行 |
| 是否需要实施 | 需后续交付文档与独立初始化/升级/恢复验证，自动迁移框架非本期强制。 |
| 基线准备 | 指定目标可提交候选；未测不自动排除目标，但未定复合范围、验收参数和风险仍须审查 |
| 关联需求 | BLOG-NF-015、BLOG-NF-016；仅对应子范围 |

### Q19 密码字符长度与BCrypt编码边界

| 项目 | 内容 |
| --- | --- |
| 完整问题 | 新设置8—64个Unicode码点及UTF-8≤72字节已定；如何设计统计和旧账号兼容回归？ |
| 当前实际行为（确认目标尚未修复） | DTO按字符@Size(min=8,max=64)，服务直接BCrypt.encode/matches；当前没有独立UTF-8字节边界处理。本轮未执行边界试验；已静态核查crypto 6.3.4调用链的首72字节有效处理范围、无主动超长拒绝，见影响分析Q19；技术事实与业务批准需区分：12.4-C已批准新设置8—64个Unicode码点且UTF-8≤72字节，旧密码验证另保持兼容。 |
| 代码依据 | E02/E04；[RegisterRequest](../../blog-server/src/main/java/com/example/blog/dto/RegisterRequest.java)、[ChangePasswordRequest](../../blog-server/src/main/java/com/example/blog/dto/ChangePasswordRequest.java)、ResetPasswordRequest、UserServiceImpl/PasswordResetServiceImpl及SecurityConfig编码器。 |
| 为什么需确认 | 允许的密码必须注册后可登录，不能静默截断；政策影响所有密码入口及现有长密码兼容。 |
| 原可选方案（历史，不替代当前已确认目标） | A：保留8—64字符，不增加字符组合，支持多字节但在核实编码器真实接受上限后统一拒绝超字节输入且明确提示，不截断；B：只允许明确字符集以避免字节变化，须用户批准限制并评估旧账号；C：更换密码存储算法，另评估迁移，非本轮。 |
| 原推荐（不是全量批准） | 保留下面原选项仅作评审历史；用户实际只确认：新密码须同时满足8—64个Unicode码点及UTF-8编码后不超过72字节；超出任一限制明确拒绝，不静默截断。适用于注册、登录状态下修改密码及邮箱验证码重置；前后端口径一致，后端独立校验。保留已有账号及密码哈希，不批量强制重置；已有密码登录验证不得直接套用新密码设置限制，不静默修改、截断或重新编码原密码，成功登录后可建议主动更新。 原建议未覆盖/未被批准部分仍待确认。 |
| 推荐理由 | 尽量不剥夺中文密码，同时防止名义合法实际无法编码；不猜库行为，也不自行修改认证算法。 |
| 前端/后端/数据库/测试/文档影响 | 前端：与后端同规则提示，各入口一致；后端：边界及异常验证位置后续设计；数据库：通常无需DDL，旧密码摘要不能反推原字符，须兼容评估；测试：7/8/64/65个Unicode码点及72/73字节、混合多字节、相同/不同长尾密码匹配、全部入口及旧账号；文档：BR-003、关联F-001/007/022，实测前不写已达标。 |
| 是否影响基线 | 指定目标可以作为候选，本条本期列明范围已于12.5-D批准；不是实现/验证完成；待设计/核实：Java/JavaScript码点与UTF-8统计、Unicode规范化是否需处理、旧账号兼容回归、现存超限密码数量和错误逐字文案；阈值、码点口径、超限拒绝及保留旧哈希兼容原则已于12.4-C确认，不再待批准。不得自行规范化、转换、预哈希或更换算法。 |
| 是否可推迟 | 更换算法可后移；静默截断不可作为推荐策略，边界验证不宜忽略。 |
| 用户决策 | 用户已确认指定范围及密码边界/兼容细化，2026-10-08 ChatGPT对话12.4-B及12.4-C；待设计/核实：Java/JavaScript码点与UTF-8统计、Unicode规范化是否需处理、旧账号兼容回归、现存超限密码数量和错误逐字文案；阈值、码点口径、超限拒绝及保留旧哈希兼容原则已于12.4-C确认，不再待批准。不得自行规范化、转换、预哈希或更换算法。 |

### Q20 评论留言永久去重与明确失败提示

| 项目 | 当前记录 |
| --- | --- |
| 用户真实决策 | 2026-10-08 ChatGPT异步确认，用户12.4-D请求第三节；已确认指定范围，未建立基线 |
| 完整问题及目标 | 保留评论和留言永久去重；重复提交明确失败、不报成功；本期不增加限时去重或依据审核状态改变规则。 |
| 当前证据 | schema.sql评论唯一键(article_id,user_id,content)，留言(user_id,content)，均不含status/deleted/时间。InteractionService保存content.trim()，捕获DuplicateKeyException分别报“请勿重复提交相同评论/留言”；页面catch显示error.message，成功提示仅在await返回成功后；全局BusinessException为HTTP400/Result code4000。 |
| 实现差异 | 代码已有永久唯一约束定义、业务异常和前端失败提示，未见需要虚构限时去重修复。运行库是否实际具有这些键本轮未查询；重复/状态边界待回归，不等于测试通过。 |
| 代码依据 | [schema.sql](../../blog-server/src/main/resources/schema.sql)、[InteractionServiceImpl.java](../../blog-server/src/main/java/com/example/blog/service/impl/InteractionServiceImpl.java)、[GlobalExceptionHandler.java](../../blog-server/src/main/java/com/example/blog/config/GlobalExceptionHandler.java)、[Comment.java](../../blog-server/src/main/java/com/example/blog/entity/Comment.java)、[Message.java](../../blog-server/src/main/java/com/example/blog/entity/Message.java)、[CommentMapper.java](../../blog-server/src/main/java/com/example/blog/mapper/CommentMapper.java)、[MessageMapper.java](../../blog-server/src/main/java/com/example/blog/mapper/MessageMapper.java)、[ArticleDetail.vue](../../blog-web/src/views/public/ArticleDetail.vue)、[Message.vue](../../blog-web/src/views/public/Message.vue)、[request.js](../../blog-web/src/utils/request.js) |
| 影响与风险 | 保留两个唯一键；不得加时间/status/deleted以释放去重。数据库按utf8mb4_unicode_ci比较，不能声称逐字节唯一，也不新增客户端规范化；不读取真实库。 POST /api/articles/{id}/comments、POST /api/messages及既有Result错误结构保留；错误逐字文案现有可作证据，不称用户逐字批准。 数据库collation可能将大小写/重音等视为相同；content.trim现状保留，具体等价边界需测试设计，不擅改规则。用户无法自行编辑/删除现有内容，不新增此能力。 |
| 待设计验收 | 同用户同文章相同有效存储评论重复拒绝；同用户同内容留言重复拒绝；旧项待审/通过/拒绝/逻辑删除均不解除唯一约束；不同用户相同内容或同用户不同有效文章相同评论不因该组合键被禁止；重复失败页面明确错误、不清空为成功、不提示待审成功，不增加记录/公开评论数；与Q12保持一致。；用例待分配ID，未执行 |
| 本期与后续边界 | 维持现有实现、设计边界回归；只有后续测试暴露反馈差异才另授权修复，不凭描述断言有bug。 不引入限时去重、按审核状态释放机制；未来调整需真实变更审批。 |
| 保留未定细节 | 已有数据库约束落地情况与collation等价输入验证未执行；不批准额外文本转换或全用户共用去重。 |
| 基线影响 | 已定子范围可作目标候选，未实现不自动阻塞；本条其他复合范围/优先级及残余参数未自动批准 |
| 需求关联 | BLOG-F-019、BLOG-F-024、BLOG-BR-020、BLOG-NF-010、BLOG-NF-014 |


## 5. 下一小阶段：已确认规则之外的剩余事项

A—G完整推荐均已确认，不重复要求批准配置缺失/空值、用户名码点、角色边界、删除范围、120秒或已明定性能参数。此前唯一待补性能参数为正文UTF-8字节数；12.5-C已确认每篇4,096字节，现不再待补；其他实际硬件/软件版本在执行前记录。旧配置缺失展示回退、旧身份冲突个案、具体实现和测试夹具留设计，不能擅自自动修复。80项整体范围与优先级已于12.5-D正式批准并登记；实现和验收未完成。


## 6. 状态检查

Q01—Q20共20项问题均已完成指定范围确认（2026-10-08 ChatGPT异步确认）。不存在仍未确认总体方向的Q编号；仍有待设计和待执行验证的细节；原性能正文参数已于12.5-C补齐，未发现新的量化验收阻碍。历史20项方向确认本身不等于整体批准；本轮12.5-D已另外批准80项列明范围与优先级，但不代表代码完成或测试通过，已建立v1.0.0文档级正式基线。此前子范围确认不等于整体批准；本轮另有12.5-D整体批准；代码现状、目标及验证状态分别记录。本轮未改代码、未执行测试、已建立文档级正式基线。A/B配置部分更新、缺失保留和默认分离已确认；Q19实现及旧账号验证仍待设计。Q19新密码8—64个Unicode码点、UTF-8最多72字节、超限拒绝及旧哈希登录兼容原则已确认；Q15五字段空字符串/非null、长度及email合理格式规则已确认。剩余为实现设计、正文参数已确认后的测试设计及待执行验证；复合需求只确认对应子范围。所有本轮测试未执行、代码未修复，已建立正式需求基线v1.0.0。

原16项分类8A/4B/4C保留为历史性质索引；当前未确认总体方向的Q为0项；原分类只保留性质索引，残余事项按待设计/参数/执行管理。已确认项的技术/字段残余不新增Q编号。服务器注销、独立首页置顶、评论分页/图片等按各自已确认本期边界暂缓，其余增强未批准，不擅排除。

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
