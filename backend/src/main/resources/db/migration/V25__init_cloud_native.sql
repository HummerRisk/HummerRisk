
CREATE TABLE IF NOT EXISTS cloud_native (
    `id`                         varchar(50)         NOT NULL COMMENT '云原生ID',
    `name`                       varchar(128)        DEFAULT NULL UNIQUE COMMENT '云原生名称',
    `credential`                 longtext            DEFAULT NULL COMMENT '云原生凭证',
    `plugin_id`                  varchar(50)         DEFAULT NULL COMMENT '插件ID',
    `plugin_name`                varchar(100)        DEFAULT NULL COMMENT '插件名称',
    `plugin_icon`                varchar(256)        DEFAULT NULL COMMENT '插件图标',
    `status`                     varchar(10)         DEFAULT NULL COMMENT '云原生状态',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `creator`                    varchar(128)        DEFAULT NULL COMMENT '创建人',
    `proxy_id`                   int(11)             DEFAULT NULL COMMENT '代理ID',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS cloud_native_source (
    `id`                         int(11)             NOT NULL AUTO_INCREMENT,
    `cloud_native_id`            varchar(50)         DEFAULT NULL COMMENT '云原生ID',
    `source_name`                varchar(128)        DEFAULT NULL COMMENT '资源名称',
    `source_namespace`           varchar(50)         DEFAULT NULL COMMENT '资源标识',
    `source_type`                varchar(50)         DEFAULT NULL COMMENT '资源类型',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `creator`                    varchar(128)        DEFAULT NULL COMMENT '创建人',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;
