CREATE TABLE IF NOT EXISTS `cloud_scan_history` (
    `id`                           int                 NOT NULL AUTO_INCREMENT,
    `account_id`                   varchar(50)         DEFAULT NULL COMMENT '资源ID',
    `create_time`                  bigint              DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `resources_sum`                bigint              DEFAULT 0 COMMENT '资源总量',
    `return_sum`                   bigint              DEFAULT 0 COMMENT '输出检测结果资源数',
    `scan_score`                   int                 DEFAULT 100 COMMENT '资源的检测评分',
    `output`                       longtext            DEFAULT NULL COMMENT '输出',
    PRIMARY KEY (`id`),
    KEY `IDX_ITEM_ID` (`account_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `cloud_scan_task_history` (
    `id`                           int                 NOT NULL AUTO_INCREMENT,
    `scan_id`                      int                 DEFAULT NULL COMMENT '历史表主键ID',
    `task_id`                      varchar(50)         DEFAULT NULL COMMENT '检测结果ID',
    `operation`                    mediumtext          DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `resources_sum`                bigint              DEFAULT 0 COMMENT '资源总量',
    `return_sum`                   bigint              DEFAULT 0 COMMENT '输出检测结果资源数',
    `scan_score`                   int                 DEFAULT NULL COMMENT '检测评分',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;


RENAME TABLE cloud_scan_history TO history_scan;

RENAME TABLE cloud_scan_task_history TO history_scan_task;

ALTER TABLE history_scan ADD `status` varchar(20) DEFAULT NULL COMMENT '状态';

ALTER TABLE history_scan ADD `account_type` varchar(50) DEFAULT NULL COMMENT '资源类型：cloudAccount/k8sAccount/codeAccouunt/serverAccount/imageAccount/packageAccount';

ALTER TABLE history_scan_task ADD `status` varchar(20) DEFAULT NULL COMMENT '状态';

ALTER TABLE history_scan_task ADD `account_id` varchar(50) DEFAULT NULL COMMENT '资源ID';

ALTER TABLE history_scan_task ADD `account_type` varchar(50) DEFAULT NULL COMMENT '资源类型：cloudAccount/k8sAccount/codeAccouunt/serverAccount/imageAccount/packageAccount';

CREATE TABLE IF NOT EXISTS `history_cloud_task` (
    `id`                            varchar(50)           NOT NULL COMMENT '任务ID',
    `status`                        varchar(20)           DEFAULT NULL COMMENT '状态',
    `apply_user`                    varchar(50)           DEFAULT NULL COMMENT '申请人',
    `create_time`                   bigint                DEFAULT NULL COMMENT '创建时间',
    `task_name`                     varchar(256)          DEFAULT NULL COMMENT '任务名称',
    `description`                   varchar(1024)         DEFAULT NULL COMMENT '描述',
    `trigger_id`                    varchar(255)          DEFAULT NULL COMMENT '触发ID',
    `prev_fire_time`                bigint                DEFAULT NULL COMMENT '上次执行时间',
    `last_fire_time`                bigint                DEFAULT NULL COMMENT '下次执行时间',
    `type`                          varchar(32)           DEFAULT NULL COMMENT '类型：manual(手动)/quartz(定时)',
    `severity`                      varchar(32)           DEFAULT NULL COMMENT '风险等级',
    `rule_id`                       varchar(50)           DEFAULT NULL COMMENT '规则ID',
    `rule_tags`                     varchar(256)          DEFAULT NULL COMMENT '标签IDs',
    `account_id`                    varchar(50)           DEFAULT NULL COMMENT '云账号ID',
    `plugin_id`                     varchar(40)           DEFAULT NULL COMMENT '插件ID',
    `plugin_name`                   varchar(128)          DEFAULT NULL COMMENT '云平台名称',
    `plugin_icon`                   varchar(128)          DEFAULT NULL COMMENT '云平台图标',
    `resource_types`                varchar(256)          DEFAULT NULL COMMENT '资源类型',
    `resources_sum`                 bigint                DEFAULT 0 COMMENT '资源总量',
    `return_sum`                    bigint                DEFAULT 0 COMMENT '输出检测结果资源数',
    `scan_type`                     varchar(32)           DEFAULT NULL COMMENT '检测类型',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_cloud_task_item` (
    `id`                           varchar(50)         NOT NULL,
    `task_id`                      varchar(50)         DEFAULT NULL COMMENT '任务ID',
    `rule_id`                      varchar(50)         DEFAULT NULL COMMENT '规则ID',
    `details`                      longtext            DEFAULT NULL COMMENT 'policy内容,不含actions',
    `tags`                         longtext            DEFAULT NULL COMMENT '标签',
    `custom_data`                  longtext            DEFAULT NULL COMMENT 'policy内容,包含actions',
    `status`                       varchar(20)         DEFAULT NULL COMMENT '状态',
    `count`                        int              DEFAULT '1'  COMMENT '数量',
    `region_id`                    varchar(128)        DEFAULT NULL COMMENT '区域标识',
    `region_name`                  varchar(128)        DEFAULT NULL COMMENT '区域名称',
    `severity`                     varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `account_id`                   varchar(50)         DEFAULT NULL COMMENT '云账号ID',
    `account_url`                  varchar(128)        DEFAULT NULL COMMENT '云账号图标',
    `account_label`                varchar(128)        DEFAULT NULL COMMENT '云账号名称',
    `create_time`                  bigint              DEFAULT NULL COMMENT '创建时间',
    `update_time`                  bigint              DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_cloud_task_log` (
                                                        `id`                           int                 NOT NULL AUTO_INCREMENT,
                                                        `task_item_id`                 varchar(50)         DEFAULT NULL COMMENT '任务项ID',
    `resource_id`                  varchar(50)         DEFAULT NULL COMMENT '资源ID',
    `create_time`                  bigint              DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    mediumtext          DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint             DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_cloud_task_resource`
(
    `id`                         varchar(50)         NOT NULL,
    `resource_name`              varchar(256)        DEFAULT NULL COMMENT '资源名称',
    `dir_name`                   varchar(128)        DEFAULT NULL COMMENT '目录名称',
    `resource_status`            varchar(45)         DEFAULT NULL COMMENT '资源状态',
    `resource_type`              varchar(64)         DEFAULT NULL COMMENT '资源类型',
    `custodian_run_log`          longtext            DEFAULT NULL COMMENT 'custodian-run.log',
    `metadata`                   longtext            DEFAULT NULL COMMENT 'metadata.json',
    `resources`                  longtext            DEFAULT NULL COMMENT 'resources.json',
    `resources_sum`              bigint              DEFAULT 0 COMMENT '资源总量',
    `return_sum`                 bigint              DEFAULT 0 COMMENT '输出检测结果资源数',
    `plugin_id`                  varchar(40)         DEFAULT NULL COMMENT '插件名称',
    `plugin_name`                varchar(40)         DEFAULT NULL COMMENT '云平台名称',
    `plugin_icon`                varchar(128)        DEFAULT NULL COMMENT '云平台图标',
    `account_id`                 varchar(50)         DEFAULT NULL COMMENT '云账号ID',
    `region_id`                  varchar(128)        DEFAULT NULL COMMENT '区域标识',
    `region_name`                varchar(128)        DEFAULT NULL COMMENT '区域名称',
    `severity`                   varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint              DEFAULT NULL COMMENT '更新时间',
    `resource_command`           longtext            DEFAULT NULL COMMENT 'policy(无actions)',
    `resource_command_action`    longtext            DEFAULT NULL COMMENT 'policy(有actions)',
    `return_html`                varchar(255)        DEFAULT ''   COMMENT 'return html地址',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
