
CREATE TABLE IF NOT EXISTS `code` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '源码名称',
    `plugin_icon`                varchar(50)         DEFAULT 'github.png' COMMENT 'github/gitlab',
    `status`                     varchar(10)         DEFAULT 'VALID' COMMENT '状态',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `creator`                    varchar(128)        DEFAULT NULL COMMENT '创建人',
    `credential`                 longtext            DEFAULT NULL COMMENT '源码凭证',
    `proxy_id`                   int(11)             DEFAULT NULL COMMENT '代理ID',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `code_rule` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `name`                       varchar(50)         DEFAULT NULL UNIQUE COMMENT '规则名称',
    `status`                     tinyint(1)          DEFAULT 1 COMMENT '规则状态(启用1，停用0)',
    `severity`                   varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `description`                varchar(1024)       DEFAULT NULL COMMENT '`描述',
    `script`                     mediumtext          DEFAULT NULL COMMENT '脚本',
    `parameter`                  varchar(1024)       DEFAULT NULL COMMENT '参数',
    `last_modified`              bigint(14)          DEFAULT NULL COMMENT '上次更新时间',
    `flag`                       tinyint(1)          NOT NULL DEFAULT 0 COMMENT '是否内置',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `code_result`
(
    `id`                         varchar(50)         NOT NULL,
    `code_id`                    varchar(50)         DEFAULT NULL COMMENT 'code ID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '源码名称',
    `plugin_icon`                varchar(50)         DEFAULT 'github.png' COMMENT '图标地址',
    `rule_id`                    varchar(50)         DEFAULT NULL COMMENT '源码检测规则ID',
    `rule_name`                  varchar(50)         DEFAULT NULL COMMENT '源码检测规则名称',
    `rule_desc`                  varchar(256)        DEFAULT NULL COMMENT '源码检测规则描述',
    `result_status`              varchar(45)         DEFAULT NULL COMMENT '检测状态',
    `severity`                   varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `apply_user`                 varchar(50)         DEFAULT NULL COMMENT '创建人ID',
    `user_name`                  varchar(128)        DEFAULT NULL COMMENT '创建人名称',
    `return_json`                longtext            DEFAULT NULL COMMENT 'return json',
    `return_sum`                 bigint(13)          DEFAULT 0 COMMENT '输出检测结果漏洞数',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `code_result_item` (
    `id`                           varchar(50)         NOT NULL COMMENT '资源ID（唯一标识）',
    `result_id`                    varchar(50)         DEFAULT NULL COMMENT 'result主键ID',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
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
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `code_result_log` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `result_id`                    varchar(50)         DEFAULT NULL COMMENT '检测结果ID',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    mediumtext          DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint(1)          DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_code_result`
(
    `id`                         varchar(50)         NOT NULL,
    `code_id`                    varchar(50)         DEFAULT NULL COMMENT 'code ID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '源码名称',
    `plugin_icon`                varchar(50)         DEFAULT 'github.png' COMMENT '图标地址',
    `rule_id`                    varchar(50)         DEFAULT NULL COMMENT '源码检测规则ID',
    `rule_name`                  varchar(50)         DEFAULT NULL COMMENT '源码检测规则名称',
    `rule_desc`                  varchar(256)        DEFAULT NULL COMMENT '源码检测规则描述',
    `result_status`              varchar(45)         DEFAULT NULL COMMENT '检测状态',
    `severity`                   varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `apply_user`                 varchar(50)         DEFAULT NULL COMMENT '创建人ID',
    `user_name`                  varchar(128)        DEFAULT NULL COMMENT '创建人名称',
    `return_json`                longtext            DEFAULT NULL COMMENT 'return json',
    `return_sum`                 bigint(13)          DEFAULT 0 COMMENT '输出检测结果漏洞数',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_code_result_log` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `result_id`                    varchar(50)         DEFAULT NULL COMMENT '检测结果ID',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    mediumtext          DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint(1)          DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

INSERT INTO `code_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('dcbe30ad-e74c-4fd8-b3c5-5ef1106b02f3', '源码检测', 1, 'HighRisk', 'Git 源码漏洞检测', '全面的漏洞检测', '[]', concat(unix_timestamp(now()), '001'), 1);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('dcbe30ad-e74c-4fd8-b3c5-5ef1106b02f3', 'safety');

ALTER TABLE cloud_native ADD operator_status varchar(10) DEFAULT 'VALID' COMMENT 'operator 状态';

ALTER TABLE image_repo ADD plugin_icon varchar(50) DEFAULT 'harbor.png' COMMENT '镜像仓库类型';
