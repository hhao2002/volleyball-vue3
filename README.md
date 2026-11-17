# 排球计分系统（volleyball-scoring-system）

基于 SSM（SpringMVC + Spring + MyBatis）的排球比赛计分系统，前端已由 JSP 逐步迁移为 Vue 3 单页应用。

## 项目结构

```
├── src/                 # 后端 SSM 工程（JSP 页面保留，兼容旧流程）
│   ├── main/java/cn/edu/hnzj/
│   │   ├── controller/  # 控制器（ApiController 为 Vue 前端提供纯 JSON 接口）
│   │   ├── service/     # 业务层
│   │   ├── dao/         # 数据访问层
│   │   └── pojo/        # 实体类
│   ├── main/resources/  # Spring/MyBatis 配置、SQL 映射
│   └── main/webapp/     # 原有 JSP 页面
├── volleyball-vue/      # Vue 3 + Vite 前端（登录 / 比赛列表 / 记分）
├── test/                # 测试工具类
└── volleyball.sql       # MySQL 建库脚本
```

## 后端运行

前置条件：JDK 1.8、Maven 3、Tomcat 8/9、MySQL 8

1. 导入数据库：

   ```sql
   mysql -u root -p
   source volleyball.sql;
   ```

2. 配置数据库连接：复制 `src/main/resources/db.properties.example` 为
   `db.properties`，填写本地 MySQL 账号密码。

3. 打包并部署到 Tomcat（或直接用 IDEA 运行）：

   ```bash
   mvn package
   # 将 target/volleyballScoringSystem.war 部署到 Tomcat webapps/
   ```

4. 默认登录账号：`admin / admin66`（管理员）

## 前端运行（Vue 3）

```bash
cd volleyball-vue
npm install
npm run dev
# 打开 http://localhost:5173
```

Vite dev server 将 `/api` 请求代理到 `http://localhost:8080/volleyballScoringSystem`，
会话 cookie 随代理透传，无需处理跨域。后端地址可用环境变量覆盖：

```bash
VITE_BACKEND=http://localhost:8080/volleyballScoringSystem npm run dev
```

## JSON 接口（/api）

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | /api/login.do | 登录（JSON：username/password） |
| POST | /api/logout.do | 退出 |
| GET | /api/currentUser.do | 当前登录用户 |
| GET | /api/matches.do | 比赛列表（含队伍与比分） |
| GET | /api/match.do?id= | 单场比赛详情 |
| GET | /api/freeTeams.do | 空闲队伍 |
| POST | /api/addMatch.do | 新建比赛 |
| POST | /api/deleteMatch.do | 删除比赛 |
| POST | /api/updateScores.do | 保存比分（scoreA/scoreB/setsA/setsB） |
| POST | /api/finishMatch.do | 结束比赛并返回 MVP |

原有 JSP 流程与 `.do` 接口保持不变，`ApiController` 为新增的纯 JSON 接口层。
