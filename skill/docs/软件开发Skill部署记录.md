# 软件开发Skill部署记录

## 1. 部署概况

- 阶段：15.4-A；执行日期：2026年10月9日（北京时间）。
- 环境：Windows、Codex桌面应用、本地PowerShell；项目工作目录为`D:\李晓\codex_project`。
- 随应用可访问的命令行程序版本：`codex-cli 0.162.0-alpha.2`。此版本是命令行程序版本，不冒充桌面应用发行版本；未检查应用更新。
- 权威源码：`skill/software-development-workflow/`。
- 部署目录：`.agents/skills/software-development-workflow/`。
- Skill名称：`software-development-workflow`；入口正文版本：`v0.1.0`。
- 复制时间：约2026年10月9日15:31—15:32；摘要复核时间：2026年10月9日15:33:53。
- 结果：12个文件已完成单向复制与一致性核对；此结论不代表客户端发现或调用成功。

## 2. 发现机制依据与部署前检查

本轮实际读取[OpenAI官方Skill说明](https://learn.chatgpt.com/docs/build-skills)，其中仓库级位置包括项目根目录的`.agents/skills/`；显式调用支持名称选择，CLI/IDE可使用`$skill-name`。官方说明支持变更检测，必要时重启。当前桌面会话实际发现能力需单独验证，见[发现与调用验证记录](软件开发Skill发现与调用验证记录.md)。

部署前目标包不存在，检查了项目根目录及已有父目录是否为重解析点，未发现需要停止复制的链接。源码实际为1份入口和11份参考文件，没有覆盖其他Skill。源码保留不变，未使用初始化器或GitHub下载安装脚本。

现有15.2设计文档中的“尚未创建/部署”属于当时状态，不改写其历史；本记录反映15.4-A的真实新状态。

## 3. 实际复制文件与SHA-256

下表的摘要同时对应源码和部署副本，全部逐文件相等。路径为包内相对路径。

| 相对文件 | SHA-256（两侧相同） | 核对 |
| --- | --- | --- |
| `SKILL.md` | `e36cd9f58b0c62d31a8955b227bf79fb5a784fade69af8f28fb77607f1cf0e6b` | 一致 |
| `references/交付与维护.md` | `8473cdff05268dcfa7a81a4a657bb2f13ea2a23028c1867ee16bf0582520b834` | 一致 |
| `references/原型设计.md` | `847cb44eef0778aee05561f4a2ad1c0a7ce6a1a99b912418a4e5610dedf31777` | 一致 |
| `references/测试设计与验证.md` | `c1cdfe7d9e7d0f7f97f247139008b4a74a020822d517471ecf5a8a6e931bb357` | 一致 |
| `references/系统总体设计.md` | `ed8b855cab20e9cab58c8cd806e399f39911fac68ec964041addce9fb6a4b331` | 一致 |
| `references/系统详细设计.md` | `958df230cf1aaea996c8c309748e6c71fb7a0e8a342e9752e9bc320cd23f1f8b` | 一致 |
| `references/编码实现.md` | `f4066b7b3d5f23c7e84de5a11bebf9fb31873a17a6e8c8d23fc65252ed38a843` | 一致 |
| `references/质量门与确认规则.md` | `ea358abbde7ac7653bc12a3422737fb6e04c646d58567881ab79276884713d12` | 一致 |
| `references/输出模板与证据规范.md` | `d6f4d7dc431e8875b86ed11164ed003bb2c53ea5a3eb95216944945a589278bf` | 一致 |
| `references/问题修复与回归.md` | `e1f6eadac607f8a0ab00e6a9c8ffe1b4c001054e4ce3fa697f8b789739b86470` | 一致 |
| `references/需求分析与变更管理.md` | `60db970f5e241ec59966acc818e474dfb95e16d03dfda716347b66f748ee3b4f` | 一致 |
| `references/项目启动与需求理解.md` | `72209d363c8ebcfb70949e757ba82a7c085c02b1e5e4da30a798ebc084493eeb` | 一致 |

两侧文件集合均为12项，目录层次与名称一致。部署副本中的Markdown相对引用全部能够定位。两侧入口name、description及v0.1.0版本记录一致；中文路径可由当前文件读取工具正常处理。

## 4. 格式校验实际结果

读取了内置`skill-creator/scripts/quick_validate.py`，其运行依赖PyYAML。

| 环境/步骤 | 实际结果 |
| --- | --- |
| 系统Python 3.12.0隔离检查 | 未发现yaml模块 |
| Codex捆绑Python 3.12.14，以-I、-B隔离检查 | 未发现yaml模块 |
| 使用捆绑Python，以-I、-B、UTF-8模式运行quick_validate.py | 退出码1，导入阶段报ModuleNotFoundError: No module named 'yaml' |
| 独立静态自检 | 文件集合、摘要、引用及入口name/description基础检查通过；不是内置校验器通过 |

内置校验器未能进入格式校验逻辑，状态为“环境受限”，不是Skill格式被判定不合格。没有安装全局或项目依赖，没有修改Python或项目配置。后续可在用户批准且具备PyYAML的隔离环境补运行。

CLI帮助查询出现用户目录临时别名创建/旧临时目录清理的访问警告，但帮助与版本查询仍返回；没有扩大权限或清理这些目录。

## 5. 同步与后续维护

本次通过逐文件复制完成部署，未手工编辑部署内容。以后仅编辑权威源码，再检查目标既有内容、单向同步、比较相对文件集合和逐文件摘要。不自动清理未知文件，不覆盖同名用户资产。

本轮有意新增的项目成果为部署副本及两份记录；未修改Skill源码、网站代码、SRS、原型或测试，未操作数据库，未执行Git暂存、提交或推送。客户端发现和调用结果独立记录，不能由本表推断。

结束时对原有非缓存文件做摘要复核，270个文件全部未变，非缓存新增项恰为12个部署文件及2份记录。全目录观察另发现`blog-web/.npm-cache/`存在缓存新增或变化，本轮未运行npm安装、构建或网站测试，缓存变化的具体来源未核实，可能与运行环境或并行本地活动有关；不将其当作业务文件修改，也未删除或提交这些缓存。不能据此宣称整个磁盘期间没有任何其他变化。

