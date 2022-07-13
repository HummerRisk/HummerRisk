CREATE TABLE IF NOT EXISTS `task` (
    `id`                            varchar(50)           NOT NULL COMMENT '任务ID',
    `status`                        varchar(20)           DEFAULT NULL COMMENT '状态',
    `apply_user`                    varchar(50)           DEFAULT NULL COMMENT '申请人',
    `create_time`                   bigint(13)            DEFAULT NULL COMMENT '创建时间',
    `task_name`                     varchar(256)          DEFAULT NULL COMMENT '任务名称',
    `description`                   varchar(255)          DEFAULT NULL COMMENT '描述',
    `cron`                          varchar(128)          DEFAULT NULL COMMENT 'cron表达式',
    `trigger_id`                    varchar(255)          DEFAULT NULL COMMENT '触发ID',
    `prev_fire_time`                bigint(20)            DEFAULT NULL COMMENT '上次执行时间',
    `last_fire_time`                bigint(20)            DEFAULT NULL COMMENT '下次执行时间',
    `type`                          varchar(32)           DEFAULT NULL COMMENT '类型：manual(手动)/quartz(定时)',
    `cron_desc`                     varchar(512)          DEFAULT NULL COMMENT '定时时间(cron中文翻译)',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;


CREATE TABLE IF NOT EXISTS `task_item` (
    `id`                           varchar(50)         NOT NULL,
    `task_id`                      varchar(50)         DEFAULT NULL COMMENT '任务ID',
    `source_id`                    varchar(50)         DEFAULT NULL COMMENT '规则ID',
    `rule_type`                    varchar(50)         DEFAULT NULL COMMENT '规则类型：rule/tag/group',
    `rule_name`                    varchar(128)        DEFAULT NULL COMMENT '规则名称',
    `rule_desc`                    varchar(256)        DEFAULT NULL COMMENT '规则描述',
    `status`                       varchar(20)         DEFAULT NULL COMMENT '状态',
    `icon`                         varchar(128)        DEFAULT NULL COMMENT '图标',
    `severity`                     varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `account_id`                   varchar(50)         DEFAULT NULL COMMENT '资源ID',
    `account_name`                 varchar(128)        DEFAULT NULL COMMENT '资源名称',
    `account_type`                 varchar(50)         DEFAULT NULL COMMENT '资源类型：cloudAccount/vulnAccount/serverAccount/imageAccount/packageAccount',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `task_item_resource`
(
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `task_id`                      varchar(50)         DEFAULT NULL COMMENT '任务ID',
    `task_item_id`                 varchar(50)         DEFAULT NULL COMMENT '任务项ID',
    `rule_type`                    varchar(50)         DEFAULT NULL COMMENT '规则类型：rule/tag/group',
    `rule_id`                      varchar(64)         DEFAULT NULL COMMENT '规则ID',
    `rule_name`                    varchar(128)        DEFAULT NULL COMMENT '规则名称',
    `account_id`                   varchar(50)         DEFAULT NULL COMMENT '资源ID',
    `account_name`                 varchar(128)        DEFAULT NULL COMMENT '资源名称',
    `account_type`                 varchar(50)         DEFAULT NULL COMMENT '资源类型：cloudAccount/vulnAccount/serverAccount/imageAccount/packageAccount',
    `resource_id`                  varchar(50)         DEFAULT NULL COMMENT '检测资源结果ID',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `task_item_log` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `task_item_id`                 varchar(50)         DEFAULT NULL COMMENT '任务项ID',
    `resource_id`                  varchar(50)         DEFAULT NULL COMMENT '检测资源结果ID',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    varchar(255)        DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint(1)          DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;


