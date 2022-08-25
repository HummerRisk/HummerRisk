
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
    `description`                varchar(255)        DEFAULT NULL COMMENT '`描述',
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
    `rule_desc`                  varchar(50)         DEFAULT NULL COMMENT '源码检测规则描述',
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
    `id`                         varchar(50)         NOT NULL COMMENT '资源ID（唯一标识）',
    `result_id`                  varchar(50)         DEFAULT NULL COMMENT 'result主键ID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '源码检测名称(别名)',
    `severity`                   varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `resource`                   longtext            DEFAULT NULL COMMENT '资源JSON',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `code_result_log` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `result_id`                    varchar(50)         DEFAULT NULL COMMENT '检测结果ID',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    varchar(1024)       DEFAULT NULL COMMENT '操作内容',
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
    `rule_desc`                  varchar(50)         DEFAULT NULL COMMENT '源码检测规则描述',
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
    `operation`                    varchar(1024)       DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint(1)          DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;
