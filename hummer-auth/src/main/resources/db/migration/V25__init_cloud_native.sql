
CREATE TABLE IF NOT EXISTS `cloud_native` (
    `id`                         varchar(50)         NOT NULL COMMENT '云原生ID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '云原生名称',
    `credential`                 longtext            DEFAULT NULL COMMENT '云原生凭证',
    `plugin_id`                  varchar(50)         DEFAULT NULL COMMENT '插件ID',
    `plugin_name`                varchar(100)        DEFAULT NULL COMMENT '插件名称',
    `plugin_icon`                varchar(256)        DEFAULT NULL COMMENT '插件图标',
    `status`                     varchar(10)         DEFAULT NULL COMMENT '云原生状态',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint              DEFAULT NULL COMMENT '更新时间',
    `creator`                    varchar(128)        DEFAULT NULL COMMENT '创建人',
    `proxy_id`                   int              DEFAULT NULL COMMENT '代理ID',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `cloud_native_source` (
    `id`                         int                 NOT NULL AUTO_INCREMENT,
    `cloud_native_id`            varchar(50)         DEFAULT NULL COMMENT '云原生ID',
    `source_name`                varchar(256)        DEFAULT NULL COMMENT '资源名称',
    `source_namespace`           varchar(256)        DEFAULT NULL COMMENT '资源标识',
    `source_yaml`                longtext            DEFAULT NULL COMMENT '资源yaml',
    `source_type`                varchar(50)         DEFAULT NULL COMMENT '资源类型',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint              DEFAULT NULL COMMENT '更新时间',
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
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint              DEFAULT NULL COMMENT '更新时间',
    `apply_user`                 varchar(50)         DEFAULT NULL COMMENT '创建人ID',
    `user_name`                  varchar(128)        DEFAULT NULL COMMENT '创建人名称',
    `vulnerability_report`       longtext            DEFAULT NULL COMMENT 'VulnerabilityReport',
    `config_audit_report`        longtext            DEFAULT NULL COMMENT 'ConfigAuditReport',
    `return_sum`                 bigint              DEFAULT 0 COMMENT '输出检测结果漏洞数',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `cloud_native_result_item`
(
    `id`                         varchar(50)         NOT NULL,
    `result_id`                  varchar(50)         DEFAULT NULL COMMENT '云原生检测结果ID',
    `title`                      varchar(512)        DEFAULT NULL COMMENT 'title',
    `vulnerability_id`           varchar(128)        DEFAULT NULL COMMENT 'vulnerabilityID',
    `severity`                   varchar(128)        DEFAULT NULL COMMENT 'severity',
    `score`                      varchar(128)        DEFAULT NULL COMMENT 'score',
    `target`                     varchar(256)        DEFAULT NULL COMMENT 'target',
    `primary_link`               varchar(256)        DEFAULT NULL COMMENT 'primaryLink',
    `installed_version`          varchar(128)        DEFAULT NULL COMMENT 'installedVersion',
    `fixed_version`              varchar(128)        DEFAULT NULL COMMENT 'fixedVersion',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `links`                      longtext            DEFAULT NULL COMMENT 'links',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `cloud_native_result_log` (
    `id`                           int                 NOT NULL AUTO_INCREMENT,
    `result_id`                    varchar(50)         DEFAULT NULL COMMENT '检测结果ID',
    `create_time`                  bigint              DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    mediumtext          DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint             DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_cloud_native_result`
(
    `id`                         varchar(50)         NOT NULL,
    `cloud_native_id`            varchar(50)         DEFAULT NULL COMMENT '云原生ID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '云原生名称',
    `plugin_icon`                varchar(256)        DEFAULT NULL COMMENT '插件图标',
    `result_status`              varchar(45)         DEFAULT NULL COMMENT '检测状态',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint              DEFAULT NULL COMMENT '更新时间',
    `apply_user`                 varchar(50)         DEFAULT NULL COMMENT '创建人ID',
    `user_name`                  varchar(128)        DEFAULT NULL COMMENT '创建人名称',
    `vulnerability_report`       longtext            DEFAULT NULL COMMENT 'VulnerabilityReport',
    `config_audit_report`        longtext            DEFAULT NULL COMMENT 'ConfigAuditReport',
    `return_sum`                 bigint              DEFAULT 0 COMMENT '输出检测结果漏洞数',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_cloud_native_result_item`
(
    `id`                         varchar(50)         NOT NULL,
    `result_id`                  varchar(50)         DEFAULT NULL COMMENT '云原生检测结果ID',
    `title`                      varchar(512)        DEFAULT NULL COMMENT 'title',
    `vulnerability_id`           varchar(128)        DEFAULT NULL COMMENT 'vulnerabilityID',
    `severity`                   varchar(128)        DEFAULT NULL COMMENT 'severity',
    `score`                      varchar(128)        DEFAULT NULL COMMENT 'score',
    `target`                     varchar(256)        DEFAULT NULL COMMENT 'target',
    `primary_link`               varchar(256)        DEFAULT NULL COMMENT 'primaryLink',
    `installed_version`          varchar(128)        DEFAULT NULL COMMENT 'installedVersion',
    `fixed_version`              varchar(128)        DEFAULT NULL COMMENT 'fixedVersion',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `links`                      longtext            DEFAULT NULL COMMENT 'links',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_cloud_native_result_log` (
    `id`                           int                 NOT NULL AUTO_INCREMENT,
    `result_id`                    varchar(50)         DEFAULT NULL COMMENT '检测结果ID',
    `create_time`                  bigint              DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    mediumtext          DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint             DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

ALTER TABLE `image_result` ADD `scan_type` varchar(32) DEFAULT 'grype' COMMENT '镜像检测类型';
ALTER TABLE `image_result` ADD `trivy_json` longtext DEFAULT NULL COMMENT 'trivy json';

ALTER TABLE `history_image_task` ADD `scan_type` varchar(32) DEFAULT 'grype' COMMENT '镜像检测类型';
ALTER TABLE `history_image_task` ADD `trivy_json` longtext DEFAULT NULL COMMENT 'trivy json';

CREATE TABLE IF NOT EXISTS `image_trivy_json` (
    `id`                           int                 NOT NULL AUTO_INCREMENT,
    `result_id`                    varchar(50)         NOT NULL COMMENT 'Result ID',
    `vulnerability_id`             varchar(50)         NOT NULL COMMENT 'VulnerabilityID',
    `pkg_name`                     varchar(50)         DEFAULT NULL COMMENT 'PkgName',
    `installed_version`            varchar(255)        DEFAULT NULL COMMENT 'InstalledVersion',
    `fixed_version`                varchar(255)        DEFAULT NULL COMMENT 'FixedVersion',
    `severity_source`              varchar(50)         DEFAULT NULL COMMENT 'SeveritySource',
    `primary_url`                  varchar(255)        DEFAULT NULL COMMENT 'PrimaryURL',
    `title`                        mediumtext          DEFAULT NULL COMMENT 'Title',
    `description`                  mediumtext          DEFAULT NULL COMMENT 'Description',
    `severity`                     varchar(50)         DEFAULT NULL COMMENT 'Severity',
    `published_date`               varchar(50)         DEFAULT NULL COMMENT 'PublishedDate',
    `last_modified_date`           varchar(50)         DEFAULT NULL COMMENT 'LastModifiedDate',
    `layer`                        mediumtext          DEFAULT NULL COMMENT 'Layer',
    `data_source`                  mediumtext          DEFAULT NULL COMMENT 'DataSource',
    `cwe_ids`                      mediumtext          DEFAULT NULL COMMENT 'CweIDs',
    `cvss`                         mediumtext          DEFAULT NULL COMMENT 'CVSS',
    `references`                   mediumtext          DEFAULT NULL COMMENT 'References',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `cloud_native_config` (
    `id`                         varchar(50)         NOT NULL COMMENT '云原生部署配置ID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '云原生部署配置名称',
    `config_yaml`                longtext            DEFAULT NULL COMMENT 'Config Yaml',
    `status`                     varchar(10)         DEFAULT NULL COMMENT 'yaml配置状态',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint              DEFAULT NULL COMMENT '更新时间',
    `creator`                    varchar(128)        DEFAULT NULL COMMENT '创建人',
    `proxy_id`                   int              DEFAULT NULL COMMENT '代理ID',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `cloud_native_config_result` (
    `id`                         varchar(50)         NOT NULL,
    `config_id`                  varchar(50)         DEFAULT NULL COMMENT '云原生部署配置ID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '云原生部署配置名称',
    `result_status`              varchar(45)         DEFAULT NULL COMMENT '检测状态',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint              DEFAULT NULL COMMENT '更新时间',
    `apply_user`                 varchar(50)         DEFAULT NULL COMMENT '创建人ID',
    `user_name`                  varchar(128)        DEFAULT NULL COMMENT '创建人名称',
    `result_json`                longtext            DEFAULT NULL COMMENT 'result json',
    `return_sum`                 bigint              DEFAULT 0 COMMENT '输出检测结果漏洞数',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `cloud_native_config_result_log` (
    `id`                           int                 NOT NULL AUTO_INCREMENT,
    `result_id`                    varchar(50)         DEFAULT NULL COMMENT '检测结果ID',
    `create_time`                  bigint              DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    mediumtext          DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint             DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `cloud_native_config_result_item` (
    `id`                         varchar(50)         NOT NULL,
    `result_id`                  varchar(50)         DEFAULT NULL COMMENT '检测结果ID',
    `type`                       varchar(50)         DEFAULT NULL COMMENT 'Type',
    `item_id`                    varchar(50)         DEFAULT NULL COMMENT 'ID',
    `title`                      varchar(512)        DEFAULT NULL COMMENT 'Title',
    `description`                text                DEFAULT NULL COMMENT 'Description',
    `message`                    varchar(256)        DEFAULT NULL COMMENT 'Message',
    `namespace`                  varchar(256)        DEFAULT NULL COMMENT 'Namespace',
    `query`                      varchar(256)        DEFAULT NULL COMMENT 'Query',
    `resolution`                 varchar(512)        DEFAULT NULL COMMENT 'Resolution',
    `severity`                   varchar(50)         DEFAULT NULL COMMENT 'Severity',
    `primary_url`                varchar(256)        DEFAULT NULL COMMENT 'PrimaryURL',
    `status`                     varchar(50)         DEFAULT NULL COMMENT 'Status',
    `references`                 mediumtext          DEFAULT NULL COMMENT 'References',
    `layer`                      mediumtext          DEFAULT NULL COMMENT 'Layer',
    `causemeta_data`             mediumtext          DEFAULT NULL COMMENT 'CauseMetadata',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `history_cloud_native_config_result` (
    `id`                         varchar(50)         NOT NULL,
    `config_id`                  varchar(50)         DEFAULT NULL COMMENT '云原生部署配置ID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '云原生部署配置名称',
    `result_status`              varchar(45)         DEFAULT NULL COMMENT '检测状态',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint              DEFAULT NULL COMMENT '更新时间',
    `apply_user`                 varchar(50)         DEFAULT NULL COMMENT '创建人ID',
    `user_name`                  varchar(128)        DEFAULT NULL COMMENT '创建人名称',
    `result_json`                longtext            DEFAULT NULL COMMENT 'result json',
    `return_sum`                 bigint              DEFAULT 0 COMMENT '输出检测结果漏洞数',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `history_cloud_native_config_result_log` (
    `id`                           int                 NOT NULL AUTO_INCREMENT,
    `result_id`                    varchar(50)         DEFAULT NULL COMMENT '检测结果ID',
    `create_time`                  bigint              DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    mediumtext          DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint             DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;
