-- ----------------------------
-- init fs
-- ----------------------------

CREATE TABLE IF NOT EXISTS `file_system` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '文件系统名称',
    `file_name`                  varchar(128)        DEFAULT NULL COMMENT '文件名',
    `plugin_icon`                varchar(50)         DEFAULT 'fs.png' COMMENT 'fs',
    `status`                     varchar(10)         DEFAULT 'VALID' COMMENT '状态',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `creator`                    varchar(128)        DEFAULT NULL COMMENT '创建人',
    `size`                       varchar(128)        DEFAULT NULL COMMENT '文件大小',
    `path`                       varchar(128)        DEFAULT NULL COMMENT '文件持久化存储路径/opt/hummerrisk/file/',
    `dir`                        varchar(128)        DEFAULT NULL COMMENT '目录',
    `sbom_id`                    varchar(50)         DEFAULT NULL COMMENT 'SBOM ID',
    `sbom_version_id`            varchar(50)         DEFAULT NULL COMMENT 'SBOM VERSION ID',
    `proxy_id`                   int(11)             DEFAULT NULL COMMENT '代理ID',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `file_system_rule` (
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

CREATE TABLE IF NOT EXISTS `file_system_result`
(
    `id`                         varchar(50)         NOT NULL,
    `fs_id`                      varchar(50)         DEFAULT NULL COMMENT 'fs ID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '源码名称',
    `plugin_icon`                varchar(50)         DEFAULT 'fs.png' COMMENT '图标地址',
    `rule_id`                    varchar(50)         DEFAULT NULL COMMENT '文件系统检测规则ID',
    `rule_name`                  varchar(50)         DEFAULT NULL COMMENT '文件系统检测规则名称',
    `rule_desc`                  varchar(256)        DEFAULT NULL COMMENT '文件系统检测规则描述',
    `result_status`              varchar(45)         DEFAULT NULL COMMENT '检测状态',
    `severity`                   varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `apply_user`                 varchar(50)         DEFAULT NULL COMMENT '创建人ID',
    `user_name`                  varchar(128)        DEFAULT NULL COMMENT '创建人名称',
    `return_json`                longtext            DEFAULT NULL COMMENT 'return json',
    `return_sum`                 bigint(13)          DEFAULT 0 COMMENT '输出检测结果漏洞数',
    `sbom_id`                    varchar(50)         DEFAULT NULL COMMENT 'SBOM ID',
    `sbom_version_id`            varchar(50)         DEFAULT NULL COMMENT 'SBOM VERSION ID',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `file_system_result_item` (
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

CREATE TABLE IF NOT EXISTS `file_system_result_log` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `result_id`                    varchar(50)         DEFAULT NULL COMMENT '检测结果ID',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    varchar(1024)       DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint(1)          DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_file_system_result`
(
    `id`                         varchar(50)         NOT NULL,
    `fs_id`                      varchar(50)         DEFAULT NULL COMMENT 'fs ID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '源码名称',
    `plugin_icon`                varchar(50)         DEFAULT 'fs.png' COMMENT '图标地址',
    `rule_id`                    varchar(50)         DEFAULT NULL COMMENT '文件系统检测规则ID',
    `rule_name`                  varchar(50)         DEFAULT NULL COMMENT '文件系统检测规则名称',
    `rule_desc`                  varchar(256)        DEFAULT NULL COMMENT '文件系统检测规则描述',
    `result_status`              varchar(45)         DEFAULT NULL COMMENT '检测状态',
    `severity`                   varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `apply_user`                 varchar(50)         DEFAULT NULL COMMENT '创建人ID',
    `user_name`                  varchar(128)        DEFAULT NULL COMMENT '创建人名称',
    `return_json`                longtext            DEFAULT NULL COMMENT 'return json',
    `return_sum`                 bigint(13)          DEFAULT 0 COMMENT '输出检测结果漏洞数',
    `sbom_id`                    varchar(50)         DEFAULT NULL COMMENT 'SBOM ID',
    `sbom_version_id`            varchar(50)         DEFAULT NULL COMMENT 'SBOM VERSION ID',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_file_system_result_log` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `result_id`                    varchar(50)         DEFAULT NULL COMMENT '检测结果ID',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    varchar(1024)       DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint(1)          DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

INSERT INTO `file_system_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('rtyuewq3-ef4c-4gd8-bhc5-5ej110kb0l71', '文件系统检测', 1, 'HighRisk', '文件系统漏洞检测', '全面的漏洞检测', '[]', concat(unix_timestamp(now()), '001'), 1);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('rtyuewq3-ef4c-4gd8-bhc5-5ej110kb0l71', 'safety');

ALTER TABLE `history_server_task` RENAME TO `history_server_result`;

ALTER TABLE `history_server_task_log` RENAME TO `history_server_result_log`;

ALTER TABLE `history_image_task` RENAME TO `history_image_result`;

ALTER TABLE `history_image_task_log` RENAME TO `history_image_result_log`;

ALTER TABLE `image_trivy_json` RENAME TO `image_result_item`;

ALTER TABLE `image_result` change `trivy_json` `result_json` longtext DEFAULT NULL COMMENT 'result json';

ALTER TABLE `history_image_result` change `trivy_json` `result_json` longtext DEFAULT NULL COMMENT 'result json';

ALTER TABLE `cloud_native_result` ADD `return_config_sum` bigint(13) DEFAULT 0 COMMENT '输出检测结果配置审计数';

CREATE TABLE IF NOT EXISTS `cloud_native_result_config_item`
(
    `id`                         varchar(50)         NOT NULL,
    `result_id`                  varchar(50)         DEFAULT NULL COMMENT '云原生检测结果ID',
    `title`                      varchar(512)        DEFAULT NULL COMMENT 'title',
    `category`                   varchar(128)        DEFAULT NULL COMMENT 'category',
    `severity`                   varchar(128)        DEFAULT NULL COMMENT 'severity',
    `check_id`                   varchar(128)        DEFAULT NULL COMMENT 'checkID',
    `description`                mediumtext          DEFAULT NULL COMMENT 'description',
    `success`                    varchar(128)        DEFAULT NULL COMMENT 'success',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `messages`                   mediumtext          DEFAULT NULL COMMENT 'messages',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

DROP TABLE `history_cloud_native_result_item`;

UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/tencent/mongo-public-network.md' where id = 'c95be861-6e0e-4404-9b21-5ccfcd94ee3f';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/qingcloud/mongodb-public-network.md' where id = 'ceecc38d-5c98-4cc4-aeef-028ab653c26b';
UPDATE `server_rule` SET `name` = 'Linux 僵尸进程检测', `status` = 1, `severity` = 'MediumRisk', `description` = '查看 Linux 僵尸进程', `script` = 'flag=true\nif [[ $(ps aux | awk \'{ print $8 \" \" $2 }\' | grep -w Z) == \"\" ]];then\n    echo \"HummerSuccess: 检测通过，无 Linux 僵尸进程\"\n    flag=false\n	exit 0\nfi\nif $flag ;then\n    echo \"HummerError: 检测到僵尸进程 $(ps aux | awk \'{ print $8 \" \" $2 }\' | grep -w Z)\"\nfi', `parameter` = '[]' WHERE `id` = 'c497a676-12ec-41ed-8944-eb32cc95b8a4';
