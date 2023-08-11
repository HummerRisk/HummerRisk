
DROP TABLE `image_grype_table`;
DROP TABLE `image_grype_json`;
DROP TABLE `image_syft_table`;
DROP TABLE `image_syft_json`;

DROP TABLE `package`;
DROP TABLE `package_rule`;
DROP TABLE `package_result`;
DROP TABLE `package_result_log`;
DROP TABLE `history_package_task`;
DROP TABLE `history_package_task_log`;
DROP TABLE `package_dependency_json`;
DROP TABLE `package_dependency_json_item`;

ALTER TABLE `image_result` DROP `grype_table`;
ALTER TABLE `image_result` DROP `grype_json`;
ALTER TABLE `image_result` DROP `syft_table`;
ALTER TABLE `image_result` DROP `syft_json`;
ALTER TABLE `image_result` DROP `scan_type`;

ALTER TABLE `history_image_task` DROP `grype_table`;
ALTER TABLE `history_image_task` DROP `grype_json`;
ALTER TABLE `history_image_task` DROP `syft_table`;
ALTER TABLE `history_image_task` DROP `syft_json`;
ALTER TABLE `history_image_task` DROP `scan_type`;

CREATE TABLE IF NOT EXISTS `cloud_native_rule` (
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

CREATE TABLE IF NOT EXISTS `cloud_native_config_rule` (
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

INSERT INTO `rule_tag` (`tag_key`, `tag_name`, `_index`, `flag`) VALUES ('cloud native', '云原生', 6, 1);

INSERT INTO `cloud_native_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('0577b102-2ef3-11ed-bd9b-3323180f04e4', 'K8s 检测', 1, 'HighRisk', 'K8s 环境检测', 'Kubernetes 容器多种安全风险检测，包括运行时威胁、漏洞、暴露和失败的合规性审计。', '[]', concat(unix_timestamp(now()), '001'), 1);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('0577b102-2ef3-11ed-bd9b-3323180f04e4', 'cloud native');

INSERT INTO `cloud_native_config_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('77c36430-2ef4-11ed-bd9b-4353680704h5', '部署检测', 1, 'HighRisk', '部署检测', 'Kubernetes 的部署 YAML 文件错误配置进行提前检测。', '[]', concat(unix_timestamp(now()), '001'), 1);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('77c36430-2ef4-11ed-bd9b-4353680704h5', 'cloud native');

ALTER TABLE `cloud_native_result` ADD `rule_id` varchar(50) DEFAULT NULL COMMENT '检测规则ID';

ALTER TABLE `cloud_native_result` ADD `rule_name` varchar(50) DEFAULT NULL COMMENT '检测规则名称';

ALTER TABLE `cloud_native_result` ADD `rule_desc` varchar(256) DEFAULT NULL COMMENT '检测规则描述';

ALTER TABLE `cloud_native_result` ADD `severity` varchar(32) DEFAULT NULL COMMENT '风险等级';

ALTER TABLE `cloud_native_config_result` ADD `rule_id` varchar(50) DEFAULT NULL COMMENT '检测规则ID';

ALTER TABLE `cloud_native_config_result` ADD `rule_name` varchar(50) DEFAULT NULL COMMENT '检测规则名称';

ALTER TABLE `cloud_native_config_result` ADD `rule_desc` varchar(256) DEFAULT NULL COMMENT '检测规则描述';

ALTER TABLE `cloud_native_config_result` ADD `severity` varchar(32) DEFAULT NULL COMMENT '风险等级';

ALTER TABLE `history_cloud_native_result` ADD `rule_id` varchar(50) DEFAULT NULL COMMENT '检测规则ID';

ALTER TABLE `history_cloud_native_result` ADD `rule_name` varchar(50) DEFAULT NULL COMMENT '检测规则名称';

ALTER TABLE `history_cloud_native_result` ADD `rule_desc` varchar(256) DEFAULT NULL COMMENT '检测规则描述';

ALTER TABLE `history_cloud_native_result` ADD `severity` varchar(32) DEFAULT NULL COMMENT '风险等级';

ALTER TABLE `history_cloud_native_config_result` ADD `rule_id` varchar(50) DEFAULT NULL COMMENT '检测规则ID';

ALTER TABLE `history_cloud_native_config_result` ADD `rule_name` varchar(50) DEFAULT NULL COMMENT '检测规则名称';

ALTER TABLE `history_cloud_native_config_result` ADD `rule_desc` varchar(256) DEFAULT NULL COMMENT '检测规则描述';

ALTER TABLE `history_cloud_native_config_result` ADD `severity` varchar(32) DEFAULT NULL COMMENT '风险等级';
