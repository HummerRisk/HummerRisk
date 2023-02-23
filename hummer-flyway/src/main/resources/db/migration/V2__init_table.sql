
/******************************************/
/*   表名称 = config_info   */
/******************************************/
CREATE TABLE `config_info`
(
    `id`                    bigint(20)               NOT NULL AUTO_INCREMENT COMMENT 'id',
    `data_id`               varchar(255)             NOT NULL COMMENT 'data_id',
    `group_id`              varchar(255)             DEFAULT NULL,
    `content`               longtext                 NOT NULL COMMENT 'content',
    `md5`                   varchar(32)              DEFAULT NULL COMMENT 'md5',
    `gmt_create`            datetime                 NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`          datetime                 NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `src_user`              text                     DEFAULT NULL COMMENT 'source user',
    `src_ip`                varchar(50)              DEFAULT NULL COMMENT 'source ip',
    `app_name`              varchar(128)             DEFAULT NULL,
    `tenant_id`             varchar(128)             DEFAULT '' COMMENT '租户字段',
    `c_desc`                varchar(256)             DEFAULT NULL,
    `c_use`                 varchar(64)              DEFAULT NULL,
    `effect`                varchar(64)              DEFAULT NULL,
    `type`                  varchar(64)              DEFAULT NULL,
    `c_schema`              text                     DEFAULT NULL,
    `encrypted_data_key`    text                     DEFAULT NULL COMMENT '秘钥',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_configinfo_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info';

/******************************************/
/*   表名称 = config_info_aggr   */
/******************************************/
CREATE TABLE `config_info_aggr`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `data_id`      varchar(255) NOT NULL COMMENT 'data_id',
    `group_id`     varchar(255) NOT NULL COMMENT 'group_id',
    `datum_id`     varchar(255) NOT NULL COMMENT 'datum_id',
    `content`      longtext     NOT NULL COMMENT '内容',
    `gmt_modified` datetime     NOT NULL COMMENT '修改时间',
    `app_name`     varchar(128) DEFAULT NULL,
    `tenant_id`    varchar(128) DEFAULT '' COMMENT '租户字段',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_configinfoaggr_datagrouptenantdatum` (`data_id`,`group_id`,`tenant_id`,`datum_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='增加租户字段';


/******************************************/
/*   表名称 = config_info_beta   */
/******************************************/
CREATE TABLE `config_info_beta`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `data_id`      varchar(255) NOT NULL COMMENT 'data_id',
    `group_id`     varchar(128) NOT NULL COMMENT 'group_id',
    `app_name`     varchar(128)          DEFAULT NULL COMMENT 'app_name',
    `content`      longtext     NOT NULL COMMENT 'content',
    `beta_ips`     varchar(1024)         DEFAULT NULL COMMENT 'betaIps',
    `md5`          varchar(32)           DEFAULT NULL COMMENT 'md5',
    `gmt_create`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `src_user`     text COMMENT 'source user',
    `src_ip`       varchar(50)           DEFAULT NULL COMMENT 'source ip',
    `tenant_id`    varchar(128)          DEFAULT '' COMMENT '租户字段',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_configinfobeta_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info_beta';

/******************************************/
/*   表名称 = config_info_tag   */
/******************************************/
CREATE TABLE `config_info_tag`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `data_id`      varchar(255) NOT NULL COMMENT 'data_id',
    `group_id`     varchar(128) NOT NULL COMMENT 'group_id',
    `tenant_id`    varchar(128)          DEFAULT '' COMMENT 'tenant_id',
    `tag_id`       varchar(128) NOT NULL COMMENT 'tag_id',
    `app_name`     varchar(128)          DEFAULT NULL COMMENT 'app_name',
    `content`      longtext     NOT NULL COMMENT 'content',
    `md5`          varchar(32)           DEFAULT NULL COMMENT 'md5',
    `gmt_create`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `src_user`     text COMMENT 'source user',
    `src_ip`       varchar(50)           DEFAULT NULL COMMENT 'source ip',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_configinfotag_datagrouptenanttag` (`data_id`,`group_id`,`tenant_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info_tag';

/******************************************/
/*   表名称 = config_tags_relation   */
/******************************************/
CREATE TABLE `config_tags_relation`
(
    `id`        bigint(20) NOT NULL COMMENT 'id',
    `tag_name`  varchar(128) NOT NULL COMMENT 'tag_name',
    `tag_type`  varchar(64)  DEFAULT NULL COMMENT 'tag_type',
    `data_id`   varchar(255) NOT NULL COMMENT 'data_id',
    `group_id`  varchar(128) NOT NULL COMMENT 'group_id',
    `tenant_id` varchar(128) DEFAULT '' COMMENT 'tenant_id',
    `nid`       bigint(20) NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`nid`),
    UNIQUE KEY `uk_configtagrelation_configidtag` (`id`,`tag_name`,`tag_type`),
    KEY         `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_tag_relation';

/******************************************/
/*   表名称 = group_capacity   */
/******************************************/
CREATE TABLE `group_capacity`
(
    `id`                bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `group_id`          varchar(128) NOT NULL DEFAULT '' COMMENT 'Group ID，空字符表示整个集群',
    `quota`             int(10) unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
    `usage`             int(10) unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
    `max_size`          int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
    `max_aggr_count`    int(10) unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数，，0表示使用默认值',
    `max_aggr_size`     int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
    `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
    `gmt_create`        datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='集群、各Group容量信息表';

/******************************************/
/*   表名称 = his_config_info   */
/******************************************/
CREATE TABLE `his_config_info` (
   `id` bigint(64) unsigned NOT NULL,
   `nid` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
   `data_id` varchar(255) NOT NULL,
   `group_id` varchar(128) NOT NULL,
   `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
   `content` longtext NOT NULL,
   `md5` varchar(32) DEFAULT NULL,
   `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `src_user` text,
   `src_ip` varchar(50) DEFAULT NULL,
   `op_type` char(10) DEFAULT NULL,
   `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
   `encrypted_data_key` text COMMENT '秘钥',
   PRIMARY KEY (`nid`),
   KEY `idx_gmt_create` (`gmt_create`),
   KEY `idx_gmt_modified` (`gmt_modified`),
   KEY `idx_did` (`data_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='多租户改造';


/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = tenant_capacity   */
/******************************************/
CREATE TABLE `tenant_capacity` (
   `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
   `tenant_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'Tenant ID',
   `quota` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
   `usage` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
   `max_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
   `max_aggr_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数',
   `max_aggr_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
   `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
   `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
   PRIMARY KEY (`id`),
   UNIQUE KEY `uk_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='租户容量信息表';


CREATE TABLE `tenant_info` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
   `kp` varchar(128) NOT NULL COMMENT 'kp',
   `tenant_id` varchar(128) default '' COMMENT 'tenant_id',
   `tenant_name` varchar(128) default '' COMMENT 'tenant_name',
   `tenant_desc` varchar(256) DEFAULT NULL COMMENT 'tenant_desc',
   `create_source` varchar(32) DEFAULT NULL COMMENT 'create_source',
   `gmt_create` bigint(20) NOT NULL COMMENT '创建时间',
   `gmt_modified` bigint(20) NOT NULL COMMENT '修改时间',
   PRIMARY KEY (`id`),
   UNIQUE KEY `uk_tenant_info_kptenantid` (`kp`,`tenant_id`),
   KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='tenant_info';

CREATE TABLE `users` (
     `username` varchar(50) NOT NULL PRIMARY KEY,
     `password` varchar(500) NOT NULL,
     `enabled` boolean NOT NULL
);

CREATE TABLE `roles` (
     `username` varchar(50) NOT NULL,
     `role` varchar(50) NOT NULL,
     UNIQUE INDEX `idx_user_role` (`username` ASC, `role` ASC) USING BTREE
);

CREATE TABLE `permissions` (
   `role` varchar(50) NOT NULL,
   `resource` varchar(255) NOT NULL,
   `action` varchar(8) NOT NULL,
   UNIQUE INDEX `uk_role_permission` (`role`,`resource`,`action`) USING BTREE
);

INSERT INTO `users` (username, password, enabled) VALUES ('nacos', '$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu', TRUE);

INSERT INTO `roles` (username, role) VALUES ('nacos', 'ROLE_ADMIN');

INSERT INTO `config_info` (`id`, `data_id`, `group_id`, `content`, `md5`, `gmt_create`, `gmt_modified`, `src_user`, `src_ip`, `app_name`, `tenant_id`, `c_desc`, `c_use`, `effect`, `type`, `c_schema`, `encrypted_data_key`) VALUES (1, 'application-dev.yml', 'DEFAULT_GROUP', 'spring:\n  autoconfigure:\n    exclude: com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure\n  mvc:\n    pathmatch:\n      matching-strategy: ant_path_matcher\n\n# feign 配置\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n  compression:\n    request:\n      enabled: true\n    response:\n      enabled: true\n\n# 暴露监控端点\nmanagement:\n  endpoints:\n    web:\n      exposure:\n        include: \'*\'\n', 'aaa73b809cfd4d0058893aa13da57806', sysdate(), sysdate(), 'nacos', '0:0:0:0:0:0:0:1', '', '', '通用配置', 'null', 'null', 'yaml', NULL, '');
INSERT INTO `config_info` (`id`, `data_id`, `group_id`, `content`, `md5`, `gmt_create`, `gmt_modified`, `src_user`, `src_ip`, `app_name`, `tenant_id`, `c_desc`, `c_use`, `effect`, `type`, `c_schema`, `encrypted_data_key`) VALUES (2, 'hummer-gateway-dev.yml', 'DEFAULT_GROUP', 'spring:\n  redis:\n    host: ${HR_REDIS_HOST}\n    port: ${HR_REDIS_PORT}\n    password: ${HR_REDIS_PASSWORD}\n  cloud:\n    gateway:\n      discovery:\n        locator:\n          lowerCaseServiceId: true\n          enabled: true\n      routes:\n        # 认证中心\n        - id: hummer-auth\n          uri: lb://hummer-auth\n          predicates:\n            - Path=/auth/**\n        # 定时任务\n        - id: hummer-job\n          uri: lb://hummer-job\n          predicates:\n            - Path=/schedule/**\n          filters:\n            - StripPrefix=1\n        # 系统模块\n        - id: hummer-system\n          uri: lb://hummer-system\n          predicates:\n            - Path=/system/**\n          filters:\n            - StripPrefix=1\n        # 混合云模块\n        - id: hummer-cloud\n          uri: lb://hummer-cloud\n          predicates:\n            - Path=/cloud/**\n          filters:\n            - StripPrefix=1\n        # 云原生模块\n        - id: hummer-k8s\n          uri: lb://hummer-k8s\n          predicates:\n            - Path=/k8s/**\n          filters:\n            - StripPrefix=1\n# 安全配置\nsecurity:\n  # 防止XSS攻击\n  xss:\n    enabled: true\n    excludeUrls:\n      - /system/notice\n  # 不校验白名单\n  ignore:\n    whites:\n      - /signout\n      - /language\n', 'a9c796199990b266d8d94b582ce9e4de', sysdate(), sysdate(), 'nacos', '0:0:0:0:0:0:0:1', '', '', '网关模块', 'null', 'null', 'yaml', '', '');
INSERT INTO `config_info` (`id`, `data_id`, `group_id`, `content`, `md5`, `gmt_create`, `gmt_modified`, `src_user`, `src_ip`, `app_name`, `tenant_id`, `c_desc`, `c_use`, `effect`, `type`, `c_schema`, `encrypted_data_key`) VALUES (3, 'hummer-auth-dev.yml', 'DEFAULT_GROUP', 'spring:\n  redis:\n    host: ${HR_REDIS_HOST}\n    port: ${HR_REDIS_PORT}\n    password: ${HR_REDIS_PASSWORD}\n  # flyway enable\n  flyway:\n    # 启用或禁用 flyway\n    enabled: true\n    # 当迁移时发现目标schema非空，而且带有没有元数据的表时，是否自动执行基准迁移，默认false.\n    baseline-on-migrate: true\n    # 默认脚本加载路径：/db/migration\n    locations: classpath:db/migration\n    # 指定 baseline 的版本号,默认值为 1, 低于该版本号的 SQL 文件, migrate 时会被忽略\n    baseline-version: 0\n    # 字符编码\n    encoding: UTF-8\n    # 迁移时是否校验，默认为 true\n    validate-on-migrate: false\n    # 是否要被替换，默认 true\n    placeholder-replacement: false\n    # metadata 版本控制信息表 默认 flyway_schema_history\n    table: hummer_version\n    # 数据库配置\n    url: jdbc:mysql://${HR_MYSQL_HOST}:${HR_MYSQL_PORT}/hummer_config?autoReconnect=false&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false\n    user: root\n    password: ${HR_MYSQL_PASSWORD}\n', 'a60ce31eadb4dfc754ef7e353183cb71', sysdate(), sysdate(), 'nacos', '0:0:0:0:0:0:0:1', '', '', '认证中心', 'null', 'null', 'yaml', '', '');
INSERT INTO `config_info` (`id`, `data_id`, `group_id`, `content`, `md5`, `gmt_create`, `gmt_modified`, `src_user`, `src_ip`, `app_name`, `tenant_id`, `c_desc`, `c_use`, `effect`, `type`, `c_schema`, `encrypted_data_key`) VALUES (4, 'hummer-monitor-dev.yml', 'DEFAULT_GROUP', '# spring\nspring:\n  security:\n    user:\n      name: hummer\n      password: 123456\n  boot:\n    admin:\n      ui:\n        title: HummerRisk服务状态监控\n', 'ab1a38ac49aaf99abc136bb1079f3516', sysdate(), sysdate(), 'nacos', '0:0:0:0:0:0:0:1', '', '', '监控中心', 'null', 'null', 'yaml', '', '');
INSERT INTO `config_info` (`id`, `data_id`, `group_id`, `content`, `md5`, `gmt_create`, `gmt_modified`, `src_user`, `src_ip`, `app_name`, `tenant_id`, `c_desc`, `c_use`, `effect`, `type`, `c_schema`, `encrypted_data_key`) VALUES (5, 'hummer-system-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring:\n  redis:\n    host: ${HR_REDIS_HOST}\n    port: ${HR_REDIS_PORT}\n    password: ${HR_REDIS_PASSWORD}\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://${HR_MYSQL_HOST}:${HR_MYSQL_PORT}/${HR_MYSQL_DB}?autoReconnect=false&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false\n            username: ${HR_MYSQL_USER}\n            password: ${HR_MYSQL_PASSWORD}\n          # 从库数据源\n          # slave:\n            # username: \n            # password: \n            # url: \n            # driver-class-name: \n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.hummer.system\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By hummer\n  licenseUrl: https://hummerrisk.com', 'ed6d478e92f7ff05c42fd133b2982dbd', sysdate(), sysdate(), 'nacos', '0:0:0:0:0:0:0:1', '', '', '系统模块', 'null', 'null', 'yaml', '', '');
INSERT INTO `config_info` (`id`, `data_id`, `group_id`, `content`, `md5`, `gmt_create`, `gmt_modified`, `src_user`, `src_ip`, `app_name`, `tenant_id`, `c_desc`, `c_use`, `effect`, `type`, `c_schema`, `encrypted_data_key`) VALUES (6, 'hummer-job-dev.yml', 'DEFAULT_GROUP', '# spring配置\nspring:\n  redis:\n    host: ${HR_REDIS_HOST}\n    port: ${HR_REDIS_PORT}\n    password: ${HR_REDIS_PORT}\n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://${HR_MYSQL_HOST}:${HR_MYSQL_PORT}/${HR_MYSQL_DB}?autoReconnect=false&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false\n    username: ${HR_MYSQL_USER}\n    password: ${HR_MYSQL_PASSWORD}\n  \n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.hummer.job.domain\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 定时任务接口文档\n  license: Powered By hummer\n  licenseUrl: https://hummerrisk.com\n', '94bae56ddfc31934e55bcb8739896609', sysdate(), sysdate(), 'nacos', '0:0:0:0:0:0:0:1', '', '', '定时任务', 'null', 'null', 'yaml', '', '');
INSERT INTO `config_info` (`id`, `data_id`, `group_id`, `content`, `md5`, `gmt_create`, `gmt_modified`, `src_user`, `src_ip`, `app_name`, `tenant_id`, `c_desc`, `c_use`, `effect`, `type`, `c_schema`, `encrypted_data_key`) VALUES (7, 'hummer-cloud-dev.yml', 'DEFAULT_GROUP', '# 混合云    \n# spring配置\nspring: \n  messages:\n    basename: i18n/messages\n    encoding: UTF-8\n  redis:\n    host: ${HR_REDIS_HOST}\n    port: ${HR_REDIS_PORT}\n    password: ${HR_REDIS_PORT}\n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://${HR_MYSQL_HOST}:${HR_MYSQL_PORT}/${HR_MYSQL_DB}?autoReconnect=false&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false\n    username: ${HR_MYSQL_USER}\n    password: ${HR_MYSQL_PASSWORD}\n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.hummer.cloud.domain\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 混合云接口文档\n  license: Powered By hummerrisk\n  licenseUrl: https://hummerrisk.com\n\n\n', '5d786c886992f6a1d8bc685e8d71795f', sysdate(), sysdate(), 'nacos', '0:0:0:0:0:0:0:1', '', '', '混合云安全', 'null', 'null', 'yaml', '', '');
INSERT INTO `config_info` (`id`, `data_id`, `group_id`, `content`, `md5`, `gmt_create`, `gmt_modified`, `src_user`, `src_ip`, `app_name`, `tenant_id`, `c_desc`, `c_use`, `effect`, `type`, `c_schema`, `encrypted_data_key`) VALUES (8, 'hummer-k8s-dev.yml', 'DEFAULT_GROUP', '# 云原生    \n# spring配置\nspring: \n  messages:\n    basename: i18n/messages\n    encoding: UTF-8\n  redis:\n    host: ${HR_REDIS_HOST}\n    port: ${HR_REDIS_PORT}\n    password: ${HR_REDIS_PORT}\n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://${HR_MYSQL_HOST}:${HR_MYSQL_PORT}/${HR_MYSQL_DB}?autoReconnect=false&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false\n    username: ${HR_MYSQL_USER}\n    password: ${HR_MYSQL_PASSWORD}\n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.hummer.k8s.domain\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 云原生接口文档\n  license: Powered By hummerrisk\n  licenseUrl: https://hummerrisk.com\n\n\n', '8cd26d665949104b57e5fa4d9568ab3a', sysdate(), sysdate(), 'nacos', '0:0:0:0:0:0:0:1', '', '', '云原生安全', 'null', 'null', 'yaml', '', '');
INSERT INTO `config_info` (`id`, `data_id`, `group_id`, `content`, `md5`, `gmt_create`, `gmt_modified`, `src_user`, `src_ip`, `app_name`, `tenant_id`, `c_desc`, `c_use`, `effect`, `type`, `c_schema`, `encrypted_data_key`) VALUES (9, 'sentinel-hummer-gateway', 'DEFAULT_GROUP', '[\r\n    {\r\n        \"resource\": \"hummer-auth\",\r\n        \"count\": 500,\r\n        \"grade\": 1,\r\n        \"limitApp\": \"default\",\r\n        \"strategy\": 0,\r\n        \"controlBehavior\": 0\r\n    },\r\n	{\r\n        \"resource\": \"hummer-system\",\r\n        \"count\": 1000,\r\n        \"grade\": 1,\r\n        \"limitApp\": \"default\",\r\n        \"strategy\": 0,\r\n        \"controlBehavior\": 0\r\n    },\r\n	{\r\n        \"resource\": \"hummer-gen\",\r\n        \"count\": 200,\r\n        \"grade\": 1,\r\n        \"limitApp\": \"default\",\r\n        \"strategy\": 0,\r\n        \"controlBehavior\": 0\r\n    },\r\n	{\r\n        \"resource\": \"hummer-job\",\r\n        \"count\": 300,\r\n        \"grade\": 1,\r\n        \"limitApp\": \"default\",\r\n        \"strategy\": 0,\r\n        \"controlBehavior\": 0\r\n    }\r\n]', '9f3a3069261598f74220bc47958ec252', sysdate(), sysdate(), NULL, '0:0:0:0:0:0:0:1', '', '', '限流策略', 'null', 'null', 'json', NULL, '');
