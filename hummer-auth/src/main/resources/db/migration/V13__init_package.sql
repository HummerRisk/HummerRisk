
CREATE TABLE IF NOT EXISTS `package` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '软件包名称(别名)',
    `package_name`               varchar(128)        DEFAULT NULL COMMENT '软件包名称',
    `plugin_icon`                varchar(256)        DEFAULT 'package.png' COMMENT '图标地址',
    `status`                     varchar(10)         DEFAULT 'VALID' COMMENT '软件包上传状态',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint              DEFAULT NULL COMMENT '更新时间',
    `creator`                    varchar(128)        DEFAULT NULL COMMENT '创建人',
    `size`                       varchar(128)        DEFAULT NULL COMMENT '软件包大小',
    `path`                       varchar(128)        DEFAULT NULL COMMENT '软件包持久化存储路径/opt/hummerrisk/file/',
    `is_proxy`                   tinyint             DEFAULT 0 COMMENT '是否启用代理',
    `proxy_id`                   int              DEFAULT NULL COMMENT '代理ID',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `package_rule` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `name`                       varchar(128)        DEFAULT NULL UNIQUE COMMENT '规则名称',
    `status`                     tinyint             DEFAULT 1 COMMENT '规则状态(启用1，停用0)',
    `severity`                   varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `description`                varchar(1024)       DEFAULT NULL COMMENT '`描述',
    `script`                     mediumtext          DEFAULT NULL COMMENT '脚本',
    `parameter`                  varchar(1024)       DEFAULT NULL COMMENT '参数',
    `last_modified`              bigint              DEFAULT NULL COMMENT '上次更新时间',
    `flag`                       tinyint             NOT NULL DEFAULT 0 COMMENT '是否内置',
    PRIMARY KEY (`id`),
    KEY `IDX_NAME` (`name`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

INSERT INTO `package_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('f1a15424-7175-4fbe-aa9d-8c3a51b26046', '软件包依赖漏洞检查', 1, 'HighRisk', '识别项目依赖项并检查是否存在任何已知的，公开披露的漏洞。', '软件包依赖漏洞检查，依赖NVD漏洞数据库（美国国家通用漏洞数据库）进行依赖漏洞检查（全球信息安全领域著名的漏洞数据库包括中国国家信息安全漏洞库，美国国家信息安全漏洞库NVD，赛门铁克漏洞库等等）。\nNVD评级依赖CVSS（CommonVulnerability Scoring System），即“通用漏洞评分系统”，是一个“行业公开标准，其被设计用来评测漏洞的严重程度，并帮助确定所需反应的紧急度和重要度。', '[]', concat(unix_timestamp(now()), '001'), 1);
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('f1a15424-7175-4fbe-aa9d-8c3a51b26046', 'server');

CREATE TABLE IF NOT EXISTS `package_result`
(
    `id`                         varchar(50)         NOT NULL,
    `package_id`                 varchar(50)         DEFAULT NULL COMMENT 'packageID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '软件包名称(别名)',
    `package_name`               varchar(128)        DEFAULT NULL COMMENT '软件包名称',
    `size`                       varchar(128)        DEFAULT '0M' COMMENT '软件包大小',
    `plugin_icon`                varchar(256)        DEFAULT 'package.png' COMMENT '图标地址',
    `rule_id`                    varchar(50)         DEFAULT NULL COMMENT '软件包规则ID',
    `rule_name`                  varchar(50)         DEFAULT NULL COMMENT '软件包规则名称',
    `rule_desc`                  varchar(256)        DEFAULT NULL COMMENT '软件包规则描述',
    `result_status`              varchar(45)         DEFAULT NULL COMMENT '检测状态',
    `severity`                   varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint              DEFAULT NULL COMMENT '更新时间',
    `apply_user`                 varchar(50)         DEFAULT NULL COMMENT '创建人ID',
    `user_name`                  varchar(128)        DEFAULT NULL COMMENT '创建人名称',
    `resources`                  longtext            DEFAULT NULL COMMENT 'resources',
    `return_json`                longtext            DEFAULT NULL COMMENT 'return json',
    `return_log`                 longtext            DEFAULT NULL COMMENT 'return log',
    `return_html`                varchar(256)        DEFAULT '' COMMENT 'return html地址',
    `return_sum`                 bigint              DEFAULT 0 COMMENT '输出检测结果依赖数',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `package_result_item` (
    `id`                         varchar(50)         NOT NULL COMMENT '资源ID（唯一标识）',
    `result_id`                  varchar(50)         DEFAULT NULL COMMENT 'result主键ID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '软件包名称(别名)',
    `severity`                   varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint              DEFAULT NULL COMMENT '更新时间',
    `resource`                   longtext            DEFAULT NULL COMMENT '资源JSON',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `package_result_log` (
    `id`                           int                 NOT NULL AUTO_INCREMENT,
    `result_id`                    varchar(50)         DEFAULT NULL COMMENT '检测结果ID',
    `create_time`                  bigint              DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    mediumtext          DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint             DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;
