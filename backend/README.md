# 校园跑腿系统 - 后端

Spring Boot 后端服务。

## 目录说明

```
src/main/java/com/cold73/campuserrand/
├── controller/     # 接口层，接收请求并返回响应
├── service/        # 业务逻辑层
│   └── impl/       # Service 接口实现类
├── mapper/         # 数据访问层（MyBatis-Plus Mapper）
├── entity/         # 数据库实体类
├── dto/            # 数据传输对象（接收前端参数）
├── vo/             # 视图对象（返回给前端的数据）
├── config/         # 配置类（跨域、MyBatis-Plus、Security 等）
├── common/         # 通用类（统一返回结构、常量、枚举）
├── exception/      # 全局异常处理
├── utils/          # 工具类
└── interceptor/    # 拦截器（认证、日志等）

src/main/resources/
├── application.yml # 应用配置
├── mapper/         # MyBatis XML 映射文件
└── sql/            # 数据库建表脚本
```

## 启动方式

```bash
mvn spring-boot:run
```
