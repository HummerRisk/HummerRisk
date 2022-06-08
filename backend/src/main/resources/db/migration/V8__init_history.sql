CREATE TABLE IF NOT EXISTS `scan_history` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `account_id`                   varchar(50)         DEFAULT NULL COMMENT '云账号ID',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `resources_sum`                bigint(13)          DEFAULT NULL COMMENT '资源总量',
    `return_sum`                   bigint(13)          DEFAULT NULL COMMENT '输出扫描结果资源数',
    `scan_score`                   int(3)              DEFAULT NULL COMMENT '云账号的扫描评分',
    `output`                       longtext            DEFAULT NULL COMMENT '输出',
    PRIMARY KEY (`id`),
    KEY `IDX_ITEM_ID` (`account_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `scan_task_history` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `scan_id`                      int(11)             DEFAULT NULL COMMENT '历史表主键ID',
    `task_id`                      varchar(50)         DEFAULT NULL COMMENT '任务ID',
    `operation`                    varchar(255)        DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `resources_sum`                bigint(13)          DEFAULT NULL COMMENT 'Task的资源总量',
    `return_sum`                   bigint(13)          DEFAULT NULL COMMENT 'Task的输出扫描结果资源数',
    `scan_score`                   int(3)              DEFAULT NULL COMMENT 'Task的扫描评分',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;
