CREATE TABLE IF NOT EXISTS `cloud_account_quartz_task` (
    `id`                            varchar(50)           NOT NULL,
    `name`                          varchar(50)           DEFAULT NULL COMMENT '名称',
    `status`                        varchar(20)           DEFAULT NULL COMMENT '状态',
    `apply_user`                    varchar(50)           DEFAULT NULL COMMENT '申请人',
    `create_time`                   bigint(13)            DEFAULT NULL COMMENT '创建时间',
    `cron`                          varchar(128)          DEFAULT NULL COMMENT 'cron 表达式',
    `cron_desc`                     varchar(128)          DEFAULT NULL COMMENT 'cron 表达式中文描述',
    `trigger_id`                    varchar(255)          DEFAULT NULL COMMENT '定时任务trigger ID',
    `prev_fire_time`                bigint(20)            DEFAULT NULL COMMENT '下一次同步时间',
    `last_fire_time`                bigint(20)            DEFAULT NULL COMMENT '最后一次同步时间',
    `qz_type`                       varchar(32)           DEFAULT NULL COMMENT '定时任务类型',
    PRIMARY KEY ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;


CREATE TABLE IF NOT EXISTS `cloud_account_quartz_task_relation` (
    `id`                            varchar(50)           NOT NULL,
    `quartz_task_id`                varchar(50)           DEFAULT NULL COMMENT '定时任务ID',
    `source_id`                     varchar(50)           DEFAULT NULL COMMENT '来源ID(accountId)',
    `create_time`                   bigint(13)            DEFAULT NULL COMMENT '创建时间',
    `qz_type`                       varchar(32)           DEFAULT NULL COMMENT '定时任务类型',
    `task_ids`                      mediumtext            DEFAULT NULL COMMENT 'taskIds',
    PRIMARY KEY ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `cloud_account_quartz_task_rela_log` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `quartz_task_id`               varchar(50)         DEFAULT NULL COMMENT '定时任务ID',
    `quartz_task_rela_id`          varchar(50)         DEFAULT NULL COMMENT '任务项ID',
    `source_id`                    varchar(50)         DEFAULT NULL COMMENT '来源ID(accountId)',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    mediumtext          DEFAULT NULL COMMENT '操作内容',
    `qz_type`                      varchar(32)         DEFAULT NULL COMMENT '定时任务类型',
    `task_ids`                     mediumtext          DEFAULT NULL COMMENT 'taskIds',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;





