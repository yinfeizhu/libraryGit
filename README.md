**后端服务**
一、项目概述
    本项目是基于 Spring Boot 框架开发的图书馆管理系统，旨在实现图书馆日常运营的全流程数字
化管理。 系统涵盖图书管理、用户管理、借阅还书、罚款处理、公告发布、数据统计分析等核心功能,通
过分层架构设计（Controller-Service-Mapper）保证代码可维护性，结合事务管理、AOP 日志、JWT
认证等技术确保系统稳定性与安全性。

二、技术栈与依赖管理
1. 核心框架与版本
   Spring Boot：3.5.5（基础框架，提供自动配置、依赖管理等功能）
   Java 版本：17（项目编译与运行环境）
2. 数据访问层
   MyBatis：3.0.5（持久层框架，搭配mybatis-spring-boot-starter实现数据库交互）
   MySQL：通过mysql-connector-j连接（数据库驱动，运行时依赖）
   分页插件：PageHelper 1.4.6（实现数据分页查询）
   MyBatis Generator：1.4.2（代码生成工具，搭配 Lombok 插件简化实体类生成）
3. 开发效率工具
   Lombok：简化实体类代码（消除 getter/setter 等模板代码）
   Maven：项目构建与依赖管理工具（配置见pom.xml）
4. 安全与认证
   JWT：jjwt 0.11.5（生成令牌实现身份认证，包含jjwt-api、jjwt-impl、jjwt-jackson依赖）
5. 切面与日志
   Spring AOP：通过spring-boot-starter-aop实现切面编程（用于操作日志记录）
   Logback：日志框架（配置见logback.xml，支持控制台与文件输出）
6. 文件存储
   阿里云 OSS：aliyun-sdk-oss 3.17.4（用于图书封面等文件的云存储）
   JAXB 相关依赖：适配 Java 9 + 环境的 XML 序列化 / 反序列化（jaxb-api、activation、jaxb-runtime）
7. 其他工具
   Spring Boot Actuator：应用监控与管理
   plexus-utils：测试环境通用工具类（字符串 / 集合处理等）

三、核心功能模块
1. 借阅与还书管理
   核心功能：处理图书借阅、还书操作，自动更新图书状态，逾期时生成罚款记录
   关键逻辑：
   还书流程通过@Transactional注解保证事务一致性（BorrowRecordServiceImpl.update()）
   还书时检查图书状态（仅 “借出” 状态可还书），更新为 “在架” 状态
   对比还书时间与应还时间，逾期则调用fineRecordHandler.createFineRecord()生成罚款记录
   API 接口：PUT /return/{id}（BorrowRecordController）
2. 图书管理
   核心功能：图书信息的增删改查，支持元数据（BookReader）与实物图书（BookAdmin）管理
   关键逻辑：
   添加图书时自动设置创建时间与更新时间（BookReaderServiceImpl.add()）
   图书封面支持本地存储（通过WebConfig配置访问路径）或阿里云 OSS 云存储
   API 接口：
   添加图书：POST /bookReader（BookReaderController）
   添加实物图书：POST /bookAdmin（BookAdminController）
3. 用户管理
   核心功能：用户信息维护，支持密码修改
   关键逻辑：
   修改密码时验证旧密码正确性，验证通过后更新新密码（UserServiceImpl.updatePassword()）
   API 接口：PUT /user/password（UserController）
4. 罚款管理
   核心功能：罚款记录的创建与管理，支持按借阅记录自动生成
   关键逻辑：
   罚款原因包括逾期、损坏、丢失（通过FineReasonTypeHandler映射数据库枚举）
   支持罚款状态统计（未支付、已支付、减免，见FineRecordMapper.xml）
   API 接口：添加罚款记录POST /fineRecord（FineRecordController）
5. 公告管理
   核心功能：公告的增删改查，支持获取最新 3 条公告
   关键逻辑：
   公告包含标题、内容、创建者 ID、时间等信息（Announcement实体）
   新增 / 修改公告时自动记录操作人 ID 与时间（AnnouncementServiceImpl）
   API 接口：
   新增公告：POST /announcement
   获取最新 3 条：GET /announcement/latest（AnnouncementController）
6. 数据统计与仪表盘
   核心功能：展示图书馆关键指标与统计数据
   仪表盘指标：
   图书总数、逾期借阅数、新增用户 / 图书数
   热门借阅图书（DashboardServiceImpl.getHotBookData()）
   推荐图书（基于热门图书类别，排除已推荐和热门图书本身）
   统计分析：
   用户性别分布（ReportServiceImpl.getGenderData()）
   罚款原因 / 支付状态分布（通过FineRecordMapper查询）
   API 接口：
   热门图书：GET /dashboard/hotBook（DashboardController）
   性别统计：GET /report/getGenderCount（ReportController）
7. 日志管理
   核心功能：记录系统操作日志，支持自动清理过期日志
   实现方式：
   基于 AOP 拦截@Log注解方法（AnnotatedOperateLogAspect）
   日志内容包含操作人、时间、目标、类型、参数、IP、耗时等
   定时任务按配置清理过期日志（schedule.log-cleanup配置）

四、系统配置
1. 数据库配置（application.yml）
   yaml
   spring:
   datasource:
   url: jdbc:mysql://localhost:3306/book_management?serverTimezone=UTC&useSSL=false
   driver-class-name: com.mysql.cj.jdbc.Driver
   username: root
   password: 123456
   mybatis:
   configuration:
   map-underscore-to-camel-case: true  # 驼峰命名映射
   log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # SQL日志输出
2. 文件存储配置
   本地存储：通过WebConfig配置/coverImage/**路径映射至本地目录
   阿里云 OSS：
   yaml
   aliyun:
   oss:
   endpoint: https://oss-cn-beijing.aliyuncs.com
   bucketName: library-web-fly
   region: cn-beijing
3. 日志配置（logback.xml）
   日志输出至控制台与文件（默认路径/usr/local/app/library-app）
   按天滚动，单文件最大 10MB，保留 60 天历史日志

五、项目特点
   分层架构：严格遵循 Controller（接口）-Service（业务逻辑）-Mapper（数据访问）分层，职责清晰
   事务保障：关键操作（如还书）通过@Transactional注解保证原子性，异常时自动回滚
   统一响应：通过Result类实现 API 响应格式统一（成功 / 错误状态、消息、数据）
   全局异常处理：GlobalExceptionHandler捕获并处理系统异常，返回标准化错误信息
   安全认证：基于 JWT 生成令牌，实现无状态身份验证（JwtUtils.generateToken()）
   操作日志：AOP 切面自动记录操作日志，支持 IP 地址识别与耗时统计
   灵活部署：支持本地文件存储与阿里云 OSS 云存储切换，适配不同环境

六、启动与部署
   启动入口：LibraryManagementApplication（main方法启动 Spring Boot 应用）
   构建方式：通过 Maven 打包（spring-boot-maven-plugin），生成可执行 JAR 包
   环境要求：JDK 17+，MySQL 5.7+（兼容 8.0）# 图书馆管理系统项目说明

**前端服务存于frontend** 

一、项目简介
本项目是一个基于 Vue 3 的图书管理系统，提供图书、用户、借阅、罚款等核心功能的管理
与统计分析能力，采用 Element Plus 作为 UI 框架，结合 ECharts 实现数据可视化报
表，适用于图书馆或相关机构进行日常运营管理。

二、技术栈
前端框架：Vue 3（使用<script setup>语法糖）
UI 组件库：Element Plus
路由管理：Vue Router
数据可视化：ECharts
HTTP 请求：Axios（封装于@/utils/request）
样式处理：CSS（支持响应式设计）

三、核心功能模块
1. 图书管理
   图书信息的增删改查（支持分页、条件搜索）
   图书分类管理（文学小说类、科学技术类等）
   图书封面图片上传功能
   图书状态跟踪（通过报表统计展示）
2. 用户管理
   用户信息管理（分页查询、新增、删除、编辑）
   读者类型与性别分布统计
   密码修改功能
3. 借阅管理
   借阅记录查询（支持按读者名、图书编号等筛选）
   图书借阅与归还操作
   逾期图书提醒（仪表盘待办事项）
4. 罚款管理
   罚款记录管理（新增、修改、删除）
   罚款原因与支付状态统计分析
   支持按读者信息、图书条码筛选罚款记录
5. 数据报表
   图书统计：种类分布（柱状图）、状态分布（饼图）
   罚款统计：罚款原因（柱状图）、支付状态（饼图）
   用户统计：性别分布、读者类型分布
   热门图书排行与推荐（仪表盘模块）
6. 系统管理
   操作日志查询与清理
   公告管理（发布、编辑、删除）
   仪表盘待办事项（逾期图书、新增用户 / 图书）

四、安装与运行
1.克隆项目到本地
2.安装需要的依赖
2.1安装基础依赖
npm install
2.2安装核心依赖
npm install axios vue-router element-plus @element-plus/icons-vue echarts
3.启动项目
npm run dev
4.访问项目
访问 http://localhost:5173 即可打开系统

五、响应式支持
系统适配多种设备屏幕：
移动端（<768px）：侧边栏可折叠，图表与布局纵向排列
平板（769px~1024px）：优化图表尺寸与间距
桌面端（>1024px）：默认横向布局，完整展示所有功能

备注：
接口请求基于 Axios 封装，后端接口地址可在@/utils/request中配置
图表数据支持动态加载与窗口大小自适应
系统操作需登录权限（登录状态通过 localStorage 存储 token 管理）

**nginx服务**
前端服务打包到了nginx1.25.2中，可以点击nginx.exe快速测试
监听本地端口为90
反向代理端口为本地的8080端口