
CREATE TABLE IF NOT EXISTS `oss`
(
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `name`                       varchar(128)        DEFAULT NULL UNIQUE COMMENT '云账号名称',
    `credential`                 longtext            DEFAULT NULL COMMENT '云账号凭证',
    `plugin_id`                  varchar(50)         DEFAULT NULL COMMENT '插件ID',
    `plugin_name`                varchar(100)        DEFAULT NULL COMMENT '插件名称',
    `plugin_icon`                varchar(256)        DEFAULT NULL COMMENT '插件图标',
    `status`                     varchar(10)         DEFAULT NULL COMMENT '同步状态',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `creator`                    varchar(128)        DEFAULT NULL COMMENT '创建人',
    `regions`                    longtext            DEFAULT NULL COMMENT '区域',
    `proxy_id`                   int(11)             DEFAULT NULL COMMENT '代理ID',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `oss_log` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `oss_id`                       varchar(50)         DEFAULT NULL COMMENT 'Oss ID',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    mediumtext          DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint(1)          DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;
