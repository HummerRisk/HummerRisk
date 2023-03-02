CREATE TABLE IF NOT EXISTS role (
    `id`          varchar(50)       NOT NULL COMMENT 'Role ID',
    `name`        varchar(64)       NOT NULL COMMENT 'Role name',
    `description` varchar(255)      DEFAULT NULL COMMENT 'Role description',
    `type`        varchar(50)       DEFAULT NULL COMMENT 'Role type, (system)',
    `create_time` bigint            NOT NULL COMMENT 'Create timestamp',
    `update_time` bigint            NOT NULL COMMENT 'Update timestamp',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS system_parameter (
    `param_key`   varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT 'Parameter name',
    `param_value` varchar(2046)                     DEFAULT NULL COMMENT 'Parameter value',
    `type`        varchar(100)                      NOT NULL DEFAULT 'text' COMMENT 'Parameter type',
    `sort`        int                               DEFAULT NULL COMMENT 'Sort',
    PRIMARY KEY (`param_key`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS user (
    `id`                   varchar(50)      COLLATE utf8mb4_bin NOT NULL COMMENT 'User ID',
    `name`                 varchar(64)      NOT NULL COMMENT 'User name',
    `email`                varchar(64)      NOT NULL COMMENT 'E-Mail address',
    `password`             varchar(256)     COLLATE utf8mb4_bin DEFAULT NULL,
    `status`               varchar(50)      NOT NULL COMMENT 'User status',
    `create_time`          bigint           NOT NULL COMMENT 'Create timestamp',
    `update_time`          bigint           NOT NULL COMMENT 'Update timestamp',
    `language`             varchar(30)      DEFAULT NULL,
    `last_account_id`      varchar(50)      DEFAULT NULL,
    `phone`                varchar(50)      DEFAULT NULL COMMENT 'Phone number of user',
    `wechat_account`       varchar(64)      DEFAULT NULL COMMENT 'wechat',
    `source`               varchar(50)      DEFAULT NULL COMMENT 'Source of user',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS user_role (
    `id`          varchar(50)       NOT NULL COMMENT 'ID of user''s role info',
    `user_id`     varchar(50)       NOT NULL COMMENT 'User ID of this user-role info',
    `role_id`     varchar(50)       NOT NULL COMMENT 'Role ID of this user-role info',
    `source_id`   varchar(50)       DEFAULT NULL COMMENT 'The (system) ID of this user-role info',
    `create_time` bigint            NOT NULL COMMENT 'Create timestamp',
    `update_time` bigint            NOT NULL COMMENT 'Update timestamp',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS user_key (
    `id`          varchar(50)           NOT NULL COMMENT 'user_key ID',
    `user_id`     varchar(50)           NOT NULL COMMENT '用户ID',
    `access_key`  varchar(50)           NOT NULL COMMENT 'access_key',
    `secret_key`  varchar(50)           NOT NULL COMMENT 'secret key',
    `create_time` bigint                NOT NULL COMMENT '创建时间',
    `status`      varchar(10)           DEFAULT NULL COMMENT '状态',
    PRIMARY KEY (`id`),
    UNIQUE KEY `IDX_AK` (`access_key`),
    KEY `IDX_USER_ID` (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS operation_log (
    `id`                  varchar(64)         NOT NULL,
    `resource_user_id`    varchar(64)         DEFAULT NULL COMMENT '资源拥有者ID',
    `resource_user_name`  varchar(256)        DEFAULT NULL COMMENT '资源拥有者名称',
    `resource_type`       varchar(45)         DEFAULT NULL COMMENT '资源类型',
    `resource_id`         varchar(64)         DEFAULT NULL COMMENT '资源ID',
    `resource_name`       varchar(256)        DEFAULT NULL COMMENT '资源名称',
    `operation`           varchar(256)        DEFAULT NULL COMMENT '操作',
    `time`                bigint              DEFAULT NULL COMMENT '操作时间',
    `message`             mediumtext          DEFAULT NULL COMMENT '操作信息',
    `source_ip`           varchar(50)         DEFAULT NULL COMMENT '操作方IP',
    PRIMARY KEY (`id`),
    KEY `IDX_USER_ID` (`resource_user_id`),
    KEY `IDX_OP` (`operation`),
    KEY `IDX_RES_ID` (`resource_id`),
    KEY `IDX_RES_NAME` (`resource_name`),
    KEY `IDX_USER_NAME` (`resource_user_name`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS message_task (
    `id`                  int               NOT NULL AUTO_INCREMENT,
    `type`                varchar(50)       DEFAULT NULL COMMENT '消息类型',
    `event`               varchar(255)      DEFAULT NULL COMMENT '通知事件类型',
    `user_id`             varchar(512)      DEFAULT NULL COMMENT '接收人ID',
    `task_type`           varchar(64)       DEFAULT NULL COMMENT '任务类型',
    `identification`      varchar(255)      DEFAULT NULL COMMENT '凭证',
    `template`            TEXT              DEFAULT NULL COMMENT '模板',
    `is_set`              tinyint           DEFAULT NULL COMMENT '是否设置',
    `text_template`       text              DEFAULT NULL COMMENT '是否设置',
    `create_time`         bigint            DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS message_order (
    `id`                        varchar(64)         NOT NULL,
    `account_id`                varchar(64)         DEFAULT NULL COMMENT '云账号ID',
    `account_name`              varchar(64)         DEFAULT NULL COMMENT '云账号名称',
    `status`                    varchar(64)         DEFAULT NULL COMMENT '消息通知状态',
    `create_time`               bigint              DEFAULT NULL COMMENT '创建时间',
    `send_time`                 bigint              DEFAULT NULL COMMENT '发送消息通知时间',
    `scan_type`                 varchar(32)         DEFAULT 'CLOUD' COMMENT '检测类型',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS message_order_item (
    `id`                        int                 NOT NULL AUTO_INCREMENT,
    `message_order_id`          varchar(64)         DEFAULT NULL COMMENT '消息订单ID',
    `task_id`                   varchar(64)         DEFAULT NULL COMMENT '检测任务ID',
    `task_name`                 varchar(64)         DEFAULT NULL COMMENT '检测任务名称',
    `status`                    varchar(64)         DEFAULT NULL COMMENT '消息通知状态',
    `create_time`               bigint              DEFAULT NULL COMMENT '创建时间',
    `send_time`                 bigint              DEFAULT NULL COMMENT '发送消息通知时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE `xxl_job_info` (
                `id` int(11) NOT NULL AUTO_INCREMENT,
                `job_group` int(11) NOT NULL COMMENT '执行器主键ID',
                `job_desc` varchar(255) NOT NULL,
                `add_time` datetime DEFAULT NULL,
                `update_time` datetime DEFAULT NULL,
                `author` varchar(64) DEFAULT NULL COMMENT '作者',
                `alarm_email` varchar(255) DEFAULT NULL COMMENT '报警邮件',
                `schedule_type` varchar(50) NOT NULL DEFAULT 'NONE' COMMENT '调度类型',
                `schedule_conf` varchar(128) DEFAULT NULL COMMENT '调度配置，值含义取决于调度类型',
                `misfire_strategy` varchar(50) NOT NULL DEFAULT 'DO_NOTHING' COMMENT '调度过期策略',
                `executor_route_strategy` varchar(50) DEFAULT NULL COMMENT '执行器路由策略',
                `executor_handler` varchar(255) DEFAULT NULL COMMENT '执行器任务handler',
                `executor_param` varchar(512) DEFAULT NULL COMMENT '执行器任务参数',
                `executor_block_strategy` varchar(50) DEFAULT NULL COMMENT '阻塞处理策略',
                `executor_timeout` int(11) NOT NULL DEFAULT '0' COMMENT '任务执行超时时间，单位秒',
                `executor_fail_retry_count` int(11) NOT NULL DEFAULT '0' COMMENT '失败重试次数',
                `glue_type` varchar(50) NOT NULL COMMENT 'GLUE类型',
                `glue_source` mediumtext COMMENT 'GLUE源代码',
                `glue_remark` varchar(128) DEFAULT NULL COMMENT 'GLUE备注',
                `glue_updatetime` datetime DEFAULT NULL COMMENT 'GLUE更新时间',
                `child_jobid` varchar(255) DEFAULT NULL COMMENT '子任务ID，多个逗号分隔',
                `trigger_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '调度状态：0-停止，1-运行',
                `trigger_last_time` bigint(13) NOT NULL DEFAULT '0' COMMENT '上次调度时间',
                `trigger_next_time` bigint(13) NOT NULL DEFAULT '0' COMMENT '下次调度时间',
                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `xxl_job_log` (
               `id` bigint(20) NOT NULL AUTO_INCREMENT,
               `job_group` int(11) NOT NULL COMMENT '执行器主键ID',
               `job_id` int(11) NOT NULL COMMENT '任务，主键ID',
               `executor_address` varchar(255) DEFAULT NULL COMMENT '执行器地址，本次执行的地址',
               `executor_handler` varchar(255) DEFAULT NULL COMMENT '执行器任务handler',
               `executor_param` varchar(512) DEFAULT NULL COMMENT '执行器任务参数',
               `executor_sharding_param` varchar(20) DEFAULT NULL COMMENT '执行器任务分片参数，格式如 1/2',
               `executor_fail_retry_count` int(11) NOT NULL DEFAULT '0' COMMENT '失败重试次数',
               `trigger_time` datetime DEFAULT NULL COMMENT '调度-时间',
               `trigger_code` int(11) NOT NULL COMMENT '调度-结果',
               `trigger_msg` text COMMENT '调度-日志',
               `handle_time` datetime DEFAULT NULL COMMENT '执行-时间',
               `handle_code` int(11) NOT NULL COMMENT '执行-状态',
               `handle_msg` text COMMENT '执行-日志',
               `alarm_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败',
               PRIMARY KEY (`id`),
               KEY `I_trigger_time` (`trigger_time`),
                               KEY `I_handle_code` (`handle_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `xxl_job_log_report` (
              `id` int(11) NOT NULL AUTO_INCREMENT,
              `trigger_day` datetime DEFAULT NULL COMMENT '调度-时间',
              `running_count` int(11) NOT NULL DEFAULT '0' COMMENT '运行中-日志数量',
              `suc_count` int(11) NOT NULL DEFAULT '0' COMMENT '执行成功-日志数量',
              `fail_count` int(11) NOT NULL DEFAULT '0' COMMENT '执行失败-日志数量',
              `update_time` datetime DEFAULT NULL,
              PRIMARY KEY (`id`),
              UNIQUE KEY `i_trigger_day` (`trigger_day`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `xxl_job_logglue` (
               `id` int(11) NOT NULL AUTO_INCREMENT,
               `job_id` int(11) NOT NULL COMMENT '任务，主键ID',
               `glue_type` varchar(50) DEFAULT NULL COMMENT 'GLUE类型',
               `glue_source` mediumtext COMMENT 'GLUE源代码',
               `glue_remark` varchar(128) NOT NULL COMMENT 'GLUE备注',
               `add_time` datetime DEFAULT NULL,
               `update_time` datetime DEFAULT NULL,
               PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `xxl_job_registry` (
                `id` int(11) NOT NULL AUTO_INCREMENT,
                `registry_group` varchar(50) NOT NULL,
                `registry_key` varchar(255) NOT NULL,
                `registry_value` varchar(255) NOT NULL,
                `update_time` datetime DEFAULT NULL,
                PRIMARY KEY (`id`),
                KEY `i_g_k_v` (`registry_group`,`registry_key`,`registry_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `xxl_job_group` (
                 `id` int(11) NOT NULL AUTO_INCREMENT,
                 `app_name` varchar(64) NOT NULL COMMENT '执行器AppName',
                 `title` varchar(12) NOT NULL COMMENT '执行器名称',
                 `address_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '执行器地址类型：0=自动注册、1=手动录入',
                 `address_list` text COMMENT '执行器地址列表，多地址逗号分隔',
                 `update_time` datetime DEFAULT NULL,
                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `xxl_job_user` (
                `id` int(11) NOT NULL AUTO_INCREMENT,
                `username` varchar(50) NOT NULL COMMENT '账号',
                `password` varchar(50) NOT NULL COMMENT '密码',
                `role` tinyint(4) NOT NULL COMMENT '角色：0-普通用户、1-管理员',
                `permission` varchar(255) DEFAULT NULL COMMENT '权限：执行器ID列表，多个逗号分割',
                PRIMARY KEY (`id`),
                UNIQUE KEY `i_username` (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `xxl_job_lock` (
                `lock_name` varchar(50) NOT NULL COMMENT '锁名称',
                PRIMARY KEY (`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `xxl_job_group`(`id`, `app_name`, `title`, `address_type`, `address_list`, `update_time`) VALUES (1, 'xxl-job-executor-sample', '示例执行器', 0, NULL, '2018-11-03 22:21:31' );
INSERT INTO `xxl_job_info`(`id`, `job_group`, `job_desc`, `add_time`, `update_time`, `author`, `alarm_email`, `schedule_type`, `schedule_conf`, `misfire_strategy`, `executor_route_strategy`, `executor_handler`, `executor_param`, `executor_block_strategy`, `executor_timeout`, `executor_fail_retry_count`, `glue_type`, `glue_source`, `glue_remark`, `glue_updatetime`, `child_jobid`) VALUES (1, 1, '测试任务1', '2018-11-03 22:21:31', '2018-11-03 22:21:31', 'XXL', '', 'CRON', '0 0 0 * * ? *', 'DO_NOTHING', 'FIRST', 'demoJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', '2018-11-03 22:21:31', '');
INSERT INTO `xxl_job_user`(`id`, `username`, `password`, `role`, `permission`) VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', 1, NULL);
INSERT INTO `xxl_job_lock` ( `lock_name`) VALUES ( 'schedule_lock');
