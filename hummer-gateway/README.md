## 系统模块

~~~
com.hummer     
├── hummer-ui                                          // 前端框架 [80]
├── hummer-flyway                                      // 数据迁移 [9000]
├── hummer-gateway                                     // 网关模块 [8088]
├── hummer-auth                                        // 认证中心 [9200]
├── hummer-dubbo-api                                   // 接口模块
│       └── hummer-api-system                          // 系统接口
├── hummer-common                                      // 通用模块
│       └── hummer-common-core                         // 核心模块
│       └── hummer-common-datascope                    // 权限范围
│       └── hummer-common-redis                        // 缓存服务
│       └── hummer-common-security                     // 安全模块
│       └── hummer-common-swagger                      // 系统接口
├── hummer-modules                                     // 业务模块
│       └── hummer-system                              // 系统模块 [9201]
│       └── hummer-job                                 // 定时任务 [9202]
│       └── hummer-cloud                               // 混合云服务 [9203]
│       └── hummer-k8s                                 // 云原生服务 [9204]
├── hummer-visual                                      // 图形化管理模块
│       └── hummer-visual-monitor                      // 监控中心 [9100]
├──pom.xml                                             // 公共依赖
~~~

## 系统需求

- JDK >= 11 (推荐11版本)
- Mysql >= 8.0 (推荐8.0.32版本)
- Maven >= 3.6 (推荐3.6.3版本)
- Node >= 16.1 (推荐16.1.0版本)
- Redis >= 6.2 (推荐6.2.10版本)
- nacos >= 2.2 (推荐2.2.0版本)
- sentinel >= 1.6.0

## 技术选型

1、系统环境

- Java EE 17
- Servlet 3.0
- Apache Maven 3

2、主框架

- Spring Boot 3.0.0
- Spring Cloud 2022.0.1
- Spring Framework 6.0.0
- Spring Security 6.0.0

3、持久层

- Apache MyBatis 3.5.x
- Hibernate Validation 6.0.x
- Alibaba Druid 1.2.x

4、视图层

- Vue 2.6.x
- Axios 0.21.0
- Element 2.15.x
