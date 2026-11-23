# 排球比赛系统 3.0

排球比赛计分、队伍管理与实时观赛系统。后端负责鉴权与计分，前端使用 Vue 3 重写，按「用户系统 / 管理员系统」划分，主题色为橙色。

- 用户系统：游客浏览公告与赛果；普通用户实时观看进行中的比赛
- 管理端：计分员创建比赛、计分、管理队伍与队员；管理员管理公告、用户、申请、系统检测
- 实时能力：WebSocket 推送比分，只有创建比赛的计分员可以修改，其他用户只读观战
- 评选规则：得分优先、最大连续得分次之、连续得分次数再次之，胜队评 MVP、负队评 SVP
- 出界得分：单独记录，不归属任何球员

## 技术栈

| 层 | 技术 |
| --- | --- |
| 后端 | Spring Boot 2.7.18（Java 8）、MyBatis、JWT、Spring WebSocket |
| 数据库 | MySQL 8；内置 H2 演示档，免安装数据库即可体验 |
| 前端 | Vue 3、Vite 5、Vue Router、Pinia、Element Plus、Axios |
| 测试 | JUnit 5、MockMvc、H2 |

## 目录结构

```text
volleyball3.0/
├─ backend/       Spring Boot 后端（REST + WebSocket + H2/MySQL）
├─ frontend/      Vue 3 前端（Vite）
├─ sql/           MySQL 初始化脚本
├─ docs/          阶段规划、需求完成清单、PingCode 导入表
├─ legacy/        原 SSM + JSP 版本，保留作参考
├─ AGENTS.md      需求说明
└─ pom.xml        聚合工程（仅包含 backend）
```

## 快速开始

### 方式一：零配置演示（H2 内存库，推荐先体验）

1. 启动后端（在项目根目录）：
   ```bash
   mvn -f backend/pom.xml spring-boot:run -Dspring-boot.run.profiles=demo
   ```
   或在 IDEA 中运行 `cn.edu.hnzj.volleyball.VolleyballApplication`，程序参数填
   `--spring.profiles.active=demo`。
2. 启动前端：
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
3. 打开 <http://localhost:5173>。前端开发服务器已将 `/api` 与 `/ws` 代理到 `localhost:8080`。

演示账号：

| 角色 | 用户名 | 密码 |
| --- | --- | --- |
| 管理员 | `admin` | `admin123` |
| 计分员 | `scorer` | `scorer123` |
| 普通用户 | `user` | `user123` |

> H2 演示数据在每次重启后重置，适合演示与联调。

### 方式二：MySQL 8

1. 导入数据库（会重建业务表，请勿在有正式数据的环境直接执行）：
   ```bash
   mysql -u root -p --default-character-set=utf8mb4 < sql/volleyball3.sql
   ```
   脚本会创建 `volleyball3` 数据库，以及 `volleyball / volleyball` 应用账号。
2. 启动后端（默认使用 MySQL，可用环境变量覆盖）：
   ```bash
   DB_USER=volleyball DB_PASSWORD=volleyball \
   mvn -f backend/pom.xml spring-boot:run
   ```
   可在 `backend/src/main/resources/application.yml` 中修改连接地址，或设置
   `DB_USER`、`DB_PASSWORD`、`JWT_SECRET`。
3. 启动前端，同上。

### 生产构建

```bash
mvn -f backend/pom.xml clean package
cd frontend && npm ci && npm run build
```

后端产物：`backend/target/volleyball-backend-3.0.0.jar`；前端产物：`frontend/dist/`。
部署时将 `dist` 交给 Nginx 等静态服务器，并将 `/api` 与 `/ws` 反向代理到后端 8080 端口即可。

## 主要规则与设计

- **计分在后端完成**：前端只发起得分请求，后端校验比赛状态、队伍归属与操作者身份，避免通过浏览器篡改比分。
- **单人计分**：只有比赛创建者可以开始比赛、计分与撤销；其他计分员、管理员只能观看。
- **队伍锁定**：队伍一旦参加进行中的比赛，队伍与队员信息不可修改；比赛结束后自动解锁。
- **六人阵容**：创建或编辑队伍必须选择 6 名不重复队员，按 1-6 号位拖拽排阵；右侧只展示未加入任何队伍的可用球员。
- **出界得分**：计分台的「出界得分」按钮位于得分方一侧，表示这一分来自对方击球出界，因此不记录本队球员。若需要改为「记录击球出界方」，可在下一阶段调整。
- **MVP / SVP**：先比个人总得分，再比最大连续得分，再比连续得分（长度 ≥ 2）的发生次数；MVP 取自胜队，SVP 取自负队。若某队无人得分，SVP 仍会从该队阵容中产生（得分、连续数据均为 0）。
- **自动公告**：比赛开始、比赛结束（含局分与 MVP/SVP）会自动生成公告。
- **局分规则**：达到目标分且领先 2 分赢下一局；决胜局使用「决胜局分数」；赢下过半局数即结束比赛。
- **撤销**：支持撤销本场最后一分，后端会重算局分与比分。

更完整的阶段划分与需求决策见 [docs/phase-plan.md](docs/phase-plan.md)，逐条完成情况见 [docs/REQUIREMENTS-CHECKLIST.md](docs/REQUIREMENTS-CHECKLIST.md)。

## 测试

```bash
mvn -f backend/pom.xml test
```

覆盖内容：登录与角色校验、申请审批、队伍 6 人校验与比赛期锁定、计分/撤销/出界、MVP/SVP 评分规则、自动公告、公开赛果、系统信息接口。

前端构建检查：

```bash
cd frontend && npm run build
```

## 接口概览

| 前缀 | 说明 |
| --- | --- |
| `/api/auth` | 注册、登录、当前用户、修改密码、资料 |
| `/api/public` | 公告、赛果列表与赛果详情（无需登录） |
| `/api/live` | 实时比赛列表与详情（需登录） |
| `/api/scorer/matches` | 比赛创建、开始、计分、撤销、删除、列表 |
| `/api/scorer/teams`、`/api/scorer/members` | 队伍与队员管理 |
| `/api/admin` | 用户、申请、公告、登录日志、系统检测（仅管理员） |
| `/ws/live?token=..&matchId=..` | 单场比赛实时比分推送 |

## 仓库

- Gitee：<https://gitee.com/huang-hao-hhao/volleyball3.0>
- GitHub：<https://github.com/hhao2002/volleyball-vue3>

原 SSM + JSP 版本位于 `legacy/`，新版本为前后端分离架构。
