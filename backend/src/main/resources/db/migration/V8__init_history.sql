CREATE TABLE IF NOT EXISTS `cloud_scan_history` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `account_id`                   varchar(50)         DEFAULT NULL COMMENT '资源ID',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `resources_sum`                bigint(13)          DEFAULT 0 COMMENT '资源总量',
    `return_sum`                   bigint(13)          DEFAULT 0 COMMENT '输出检测结果资源数',
    `scan_score`                   int(3)              DEFAULT 100 COMMENT '资源的检测评分',
    `output`                       longtext            DEFAULT NULL COMMENT '输出',
    PRIMARY KEY (`id`),
    KEY `IDX_ITEM_ID` (`account_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `cloud_scan_task_history` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `scan_id`                      int(11)             DEFAULT NULL COMMENT '历史表主键ID',
    `task_id`                      varchar(50)         DEFAULT NULL COMMENT '检测结果ID',
    `operation`                    varchar(255)        DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `resources_sum`                bigint(13)          DEFAULT 0 COMMENT '资源总量',
    `return_sum`                   bigint(13)          DEFAULT 0 COMMENT '输出检测结果资源数',
    `scan_score`                   int(3)              DEFAULT NULL COMMENT '检测评分',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;
