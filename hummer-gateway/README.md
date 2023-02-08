## 系统模块

~~~
com.hummer     
├── hummer-ui                                          // 前端框架 [80]
├── hummer-gateway                                     // 网关模块 [8080]
├── hummer-auth                                        // 认证中心 [9200]
├── hummer-api                                         // 接口模块
│       └── hummer-api-system                          // 系统接口
├── hummer-common                                      // 通用模块
│       └── hummer-common-core                         // 核心模块
│       └── hummer-common-datascope                    // 权限范围
│       └── hummer-common-datasource                   // 多数据源
│       └── hummer-common-log                          // 日志记录
│       └── hummer-common-redis                        // 缓存服务
│       └── hummer-common-seata                        // 分布式事务
│       └── hummer-common-security                     // 安全模块
│       └── hummer-common-swagger                      // 系统接口
├── hummer-modules                                     // 业务模块
│       └── hummer-system                              // 系统模块 [9201]
│       └── hummer-job                                 // 定时任务 [9202]
│       └── hummer-file                                // 文件服务 [9300]
│       └── hummer-cloud                               // 混合云服务 [9400]
│       └── hummer-k8s                                 // 云原生服务 [9500]
├── hummer-visual                                      // 图形化管理模块
│       └── hummer-visual-monitor                      // 监控中心 [9100]
├──pom.xml                                             // 公共依赖
~~~

## 系统需求

- JDK >= 11 (推荐11版本)
- Mysql >= 5.7.38 (推荐5.7版本)
- Maven >= 3.6.3 (推荐3.6版本)
- Node >= 16.1.0 (推荐16版本)
- Redis >= 3.0
- nacos >= 2.2.0 (推荐2.2.0版本)
- sentinel >= 1.6.0

## 技术选型

1、系统环境

- Java EE 11
- Servlet 3.0
- Apache Maven 3

2、主框架

- Spring Boot 3.0.2
- Spring Cloud 2022.0.0
- Spring Framework 5.2.x
- Spring Security 5.2.x

3、持久层

- Apache MyBatis 3.5.x
- Hibernate Validation 6.0.x
- Alibaba Druid 1.2.x

4、视图层

- Vue 2.6.x
- Axios 0.21.0
- Element 2.15.x
