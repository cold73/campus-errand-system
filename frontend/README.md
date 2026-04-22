# 校园跑腿系统 - 微信小程序前端

毕业设计项目「基于微信小程序的校园跑腿系统」的前端模块，使用微信小程序原生框架（JavaScript）。

## 目录结构

```
frontend/
├── app.js                  # 小程序入口（含 globalData：userId / runnerId 占位）
├── app.json                # 全局配置（页面注册、window、sitemap）
├── app.wxss                # 全局样式
├── project.config.json     # 项目配置（AppID、编译参数）
├── sitemap.json            # 页面抓取规则
├── config/
│   └── api.js              # 后端接口 baseURL + 路径常量
├── utils/
│   └── request.js          # wx.request 封装（自动解包 Result）
└── pages/
    ├── index/index         # 首页（入口聚合：跳转到各子页）
    ├── order/
    │   ├── create/create   # 发布订单
    │   ├── list/list       # 我的订单列表
    │   └── detail/detail   # 订单详情
    └── runner/
        ├── hall/hall       # 跑腿大厅（可接订单）
        └── tasks/tasks     # 我的接单（跑腿员视角）
```

## 页面说明

| 页面 | 路径 | 作用 |
|---|---|---|
| 首页 | `pages/index/index` | 入口聚合页，提供用户侧/跑腿员侧的跳转入口 |
| 发布订单 | `pages/order/create/create` | 用户填写订单信息并提交（调 `POST /api/order/create`） |
| 我的订单 | `pages/order/list/list` | 当前用户的订单列表（调 `GET /api/order/list`） |
| 订单详情 | `pages/order/detail/detail` | 订单主信息 + 收货信息（调 `GET /api/order/detail/:id`） |
| 跑腿大厅 | `pages/runner/hall/hall` | 所有可接订单（调 `GET /api/order/hall`） |
| 我的接单 | `pages/runner/tasks/tasks` | 跑腿员已接/进行中/已完成的任务列表 |

## 后端接口对接

`config/api.js` 统一管理 baseURL 与接口路径：

- 开发环境 `BASE_URL = http://localhost:8080`
- 已有接口：`ORDER_CREATE / ORDER_LIST / ORDER_DETAIL / ORDER_TAKE / ORDER_PICKUP / ORDER_FINISH / ORDER_HALL`

`utils/request.js` 提供统一的 `request({ url, method, data })` 函数：

- 自动拼接 baseURL
- 自动解包后端 `Result { code, message, data }`：`code==200` 直接 resolve `data`；否则 toast 错误并 reject
- 预留 `Authorization` header 占位，后续接入 JWT 时使用

## 启动方式

1. 打开「微信开发者工具」
2. 选择「导入项目」
3. 目录选择 `frontend/`
4. AppID 会自动从 `project.config.json` 读取（`wxc3aca17116d5eaed`）
5. 点击「确定」即可进入编辑器

> 注意：请求 `localhost:8080` 时，开发者工具「详情 → 本地设置」需勾选「不校验合法域名」。

## 当前阶段

仅完成项目骨架初始化：
- 基础配置文件
- 6 个页面占位
- 请求封装与接口地址常量预留

后续将按顺序补充：发布订单表单 → 订单列表/详情 → 跑腿大厅 → 接单/取货/完成流程 → 微信登录。
