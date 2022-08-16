
CREATE TABLE IF NOT EXISTS `cloud_native` (
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

CREATE TABLE IF NOT EXISTS `cloud_native_source` (
    `id`                         int(11)             NOT NULL AUTO_INCREMENT,
    `cloud_native_id`            varchar(50)         DEFAULT NULL COMMENT '云原生ID',
    `source_name`                varchar(128)        DEFAULT NULL COMMENT '资源名称',
    `source_namespace`           varchar(50)         DEFAULT NULL COMMENT '资源标识',
    `source_yaml`                longtext            DEFAULT NULL COMMENT '资源yaml',
    `source_type`                varchar(50)         DEFAULT NULL COMMENT '资源类型',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `creator`                    varchar(128)        DEFAULT NULL COMMENT '创建人',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `cloud_native_result`
(
    `id`                         varchar(50)         NOT NULL,
    `cloud_native_id`            varchar(50)         DEFAULT NULL COMMENT '云原生ID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '云原生名称',
    `plugin_icon`                varchar(256)        DEFAULT NULL COMMENT '插件图标',
    `result_status`              varchar(45)         DEFAULT NULL COMMENT '检测状态',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `apply_user`                 varchar(50)         DEFAULT NULL COMMENT '创建人ID',
    `user_name`                  varchar(128)        DEFAULT NULL COMMENT '创建人名称',
    `vulnerability_report`       longtext            DEFAULT NULL COMMENT 'VulnerabilityReport',
    `config_audit_report`        longtext            DEFAULT NULL COMMENT 'ConfigAuditReport',
    `return_sum`                 bigint(13)          DEFAULT 0 COMMENT '输出检测结果漏洞数',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `cloud_native_result_item`
(
    `id`                         varchar(50)         NOT NULL,
    `result_id`                  varchar(50)         DEFAULT NULL COMMENT '云原生检测结果ID',
    `title`                      varchar(128)        DEFAULT NULL COMMENT 'title',
    `vulnerability_id`           varchar(50)         DEFAULT NULL COMMENT 'vulnerabilityID',
    `severity`                   varchar(50)         DEFAULT NULL COMMENT 'severity',
    `score`                      varchar(50)         DEFAULT NULL COMMENT 'score',
    `target`                     varchar(50)         DEFAULT NULL COMMENT 'target',
    `primary_link`               varchar(50)         DEFAULT NULL COMMENT 'primaryLink',
    `installed_version`          varchar(50)         DEFAULT NULL COMMENT 'installedVersion',
    `fixed_version`              varchar(50)         DEFAULT NULL COMMENT 'fixedVersion',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `links`                      longtext            DEFAULT NULL COMMENT 'links',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `cloud_native_result_log` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `result_id`                    varchar(50)         DEFAULT NULL COMMENT '检测结果ID',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    varchar(1024)       DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint(1)          DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_cloud_native_result`
(
    `id`                         varchar(50)         NOT NULL,
    `cloud_native_id`            varchar(50)         DEFAULT NULL COMMENT '云原生ID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '云原生名称',
    `plugin_icon`                varchar(256)        DEFAULT NULL COMMENT '插件图标',
    `result_status`              varchar(45)         DEFAULT NULL COMMENT '检测状态',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `apply_user`                 varchar(50)         DEFAULT NULL COMMENT '创建人ID',
    `user_name`                  varchar(128)        DEFAULT NULL COMMENT '创建人名称',
    `vulnerability_report`       longtext            DEFAULT NULL COMMENT 'VulnerabilityReport',
    `config_audit_report`        longtext            DEFAULT NULL COMMENT 'ConfigAuditReport',
    `return_sum`                 bigint(13)          DEFAULT 0 COMMENT '输出检测结果漏洞数',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_cloud_native_result_item`
(
    `id`                         varchar(50)         NOT NULL,
    `result_id`                  varchar(50)         DEFAULT NULL COMMENT '云原生检测结果ID',
    `title`                      varchar(128)        DEFAULT NULL COMMENT 'title',
    `vulnerability_id`           varchar(50)         DEFAULT NULL COMMENT 'vulnerabilityID',
    `severity`                   varchar(50)         DEFAULT NULL COMMENT 'severity',
    `score`                      varchar(50)         DEFAULT NULL COMMENT 'score',
    `target`                     varchar(50)         DEFAULT NULL COMMENT 'target',
    `primary_link`               varchar(50)         DEFAULT NULL COMMENT 'primaryLink',
    `installed_version`          varchar(50)         DEFAULT NULL COMMENT 'installedVersion',
    `fixed_version`              varchar(50)         DEFAULT NULL COMMENT 'fixedVersion',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `links`                      longtext            DEFAULT NULL COMMENT 'links',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_cloud_native_result_log` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `result_id`                    varchar(50)         DEFAULT NULL COMMENT '检测结果ID',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    varchar(1024)       DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint(1)          DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;
