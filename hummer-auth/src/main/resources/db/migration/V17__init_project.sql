
CREATE TABLE IF NOT EXISTS `cloud_project` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `status`                     varchar(20)         DEFAULT NULL COMMENT '状态',
    `account_name`               varchar(256)        DEFAULT NULL COMMENT '云账号名称',
    `account_id`                 varchar(50)         DEFAULT NULL COMMENT '云账号标识',
    `plugin_id`                  varchar(128)        DEFAULT NULL COMMENT '云类型ID',
    `plugin_name`                varchar(128)        DEFAULT NULL COMMENT '插件名称',
    `plugin_icon`                varchar(256)        DEFAULT NULL COMMENT '插件图标',
    `creator`                    varchar(50)         DEFAULT NULL COMMENT '创建人',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `resources_sum`              bigint              DEFAULT 0 COMMENT '检测结果资源总量',
    `return_sum`                 bigint              DEFAULT 0 COMMENT '检测结果风险资源数',
    `job_type`                   varchar(50)         DEFAULT 'once' COMMENT '任务类型：once/cron',
    `xxl_job_id`                 int                 DEFAULT 0 COMMENT '定时任务ID',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `cloud_project_log` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `project_id`                 varchar(128)        DEFAULT NULL COMMENT 'cloud project 标识',
    `init_time`                  varchar(50)         DEFAULT NULL COMMENT '初始化时间',
    `exec_time`                  varchar(50)         DEFAULT NULL COMMENT '执行完成时间',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `operator`                   varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                  mediumtext          DEFAULT NULL COMMENT '操作内容',
    `output`                     mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                     tinyint             DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `cloud_group` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `project_id`                 varchar(128)        DEFAULT NULL COMMENT 'cloud project 标识',
    `group_id`                   int                 DEFAULT NULL COMMENT 'rule group 标识',
    `group_name`                 varchar(256)        DEFAULT NULL COMMENT '规则组名称',
    `group_desc`                 varchar(1024)       DEFAULT NULL COMMENT '规则组描述',
    `group_level`                varchar(64)         DEFAULT NULL COMMENT '风险级别',
    `group_flag`                 tinyint             DEFAULT 0 COMMENT '是否内置',
    `status`                     varchar(20)         DEFAULT NULL COMMENT '状态',
    `account_name`               varchar(256)        DEFAULT NULL COMMENT '云账号名称',
    `account_id`                 varchar(50)         DEFAULT NULL COMMENT '云账号标识',
    `plugin_id`                  varchar(128)        DEFAULT NULL COMMENT '云类型ID',
    `plugin_name`                varchar(128)        DEFAULT NULL COMMENT '插件名称',
    `plugin_icon`                varchar(256)        DEFAULT NULL COMMENT '插件图标',
    `creator`                    varchar(50)         DEFAULT NULL COMMENT '创建人',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `resources_sum`              bigint              DEFAULT 0 COMMENT '检测结果资源总量',
    `return_sum`                 bigint              DEFAULT 0 COMMENT '检测结果风险资源数',
    `job_type`                   varchar(50)         DEFAULT 'once' COMMENT '任务类型：once/cron',
    `xxl_job_id`                 int                 DEFAULT 0 COMMENT '定时任务ID',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `cloud_group_log` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `project_id`                 varchar(128)        DEFAULT NULL COMMENT 'cloud project 标识',
    `group_id`                   varchar(128)        DEFAULT NULL COMMENT 'cloud group 标识',
    `init_time`                  varchar(50)         DEFAULT NULL COMMENT '初始化时间',
    `exec_time`                  varchar(50)         DEFAULT NULL COMMENT '执行完成时间',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `operator`                   varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                  mediumtext          DEFAULT NULL COMMENT '操作内容',
    `output`                     mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                     tinyint             DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `cloud_process` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `project_id`                 varchar(128)        DEFAULT NULL COMMENT 'cloud project 标识',
    `process_step`               int                 DEFAULT 0 COMMENT '执行过程步骤',
    `process_rate`               int                 DEFAULT 0 COMMENT '执行进度',
    `status`                     varchar(20)         DEFAULT NULL COMMENT '状态',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `exec_time`                  varchar(50)         DEFAULT NULL COMMENT '执行完成时间',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `cloud_process_log` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `project_id`                 varchar(128)        DEFAULT NULL COMMENT 'cloud project 标识',
    `process_id`                 varchar(128)        DEFAULT NULL COMMENT 'cloud process 标识',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `operator`                   varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                  mediumtext          DEFAULT NULL COMMENT '操作内容',
    `output`                     mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                     tinyint             DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

Alter table `cloud_resource_rela` modify COLUMN `hummer_id` varchar(1024) DEFAULT NULL COMMENT '资源ID';

Alter table `resource_item` modify COLUMN `hummer_id` varchar(1024) DEFAULT NULL COMMENT '资源ID';

Alter table `cloud_resource_item` modify COLUMN `hummer_id` varchar(1024) DEFAULT NULL COMMENT '资源ID';

ALTER TABLE `cloud_resource_item` modify COLUMN `hummer_name` varchar(1024) DEFAULT NULL COMMENT '资源别名';

ALTER TABLE `resource_item` modify COLUMN `hummer_name` varchar(1024) DEFAULT NULL COMMENT '资源别名';

