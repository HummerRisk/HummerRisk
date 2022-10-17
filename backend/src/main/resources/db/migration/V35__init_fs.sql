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

-- ----------------------------
-- aws rule group
-- ----------------------------
DELETE FROM `rule_group` WHERE `name` = 'AWS Prowler - AWS Security Tool';

DELETE FROM `rule` WHERE `id` in ('ebc80d1e-5dd9-4b86-8037-fb7c9727084a', '9929cc2a-7a9b-4643-a813-0e4359b09eb0', '8ad73e84-141a-40c4-bd56-1813535e8e92', 'ee74d071-d39e-4b0e-b4a3-e7ad24fbce12', '0dc8ae24-71f8-449a-8834-d59f8fbdf991',
                                '1fbd8c76-90e7-48e4-8281-ef597bb484e8', '6bb291bd-fcdf-4aa9-8fdf-69e761c9c4e0', '350a097a-dfeb-4fd0-a3af-49049da5c025', 'fa60960a-0e38-4502-9834-1ab1277e9aaa', '39c87069-a3c6-4ab3-9974-f844a89872b5',
                                '10754933-241e-42db-bb3c-42e13cb40b0f', '7c37887f-0b8d-444c-bad7-e43cd2b41578', '197e41e7-cbc3-4eef-b5ad-5153c9232449', '4a64e90e-92ea-4d5a-a753-3c5ca92a2b95', '72af2230-8555-4e0c-a06a-721436a0644e',
                                '0a5a7796-6b53-408a-ba49-7fd9e51d82f4', 'a944a483-c23f-4d62-bdd8-d2a20a4aaeca', '9b1d1a9c-6c7e-44db-a15c-c0c82b1040eb', '0d51321e-e26c-4147-b730-5b1403384487', '0d6270fc-3744-42cd-a850-65ac0b8ef514',
                                'c1375dfe-c0b1-4e53-9c2b-6a0f29258a08', '1ab302fb-1b06-410a-b17a-fdf7843d6182', 'f38d83c9-c4b9-47b8-8135-f9bdf96cf8ec', 'e86583b9-12dd-4316-980a-5eeeba86f9ca');

DELETE FROM `rule_tag_mapping` WHERE `rule_id` in ('ebc80d1e-5dd9-4b86-8037-fb7c9727084a', '9929cc2a-7a9b-4643-a813-0e4359b09eb0', '8ad73e84-141a-40c4-bd56-1813535e8e92', 'ee74d071-d39e-4b0e-b4a3-e7ad24fbce12', '0dc8ae24-71f8-449a-8834-d59f8fbdf991',
                                '1fbd8c76-90e7-48e4-8281-ef597bb484e8', '6bb291bd-fcdf-4aa9-8fdf-69e761c9c4e0', '350a097a-dfeb-4fd0-a3af-49049da5c025', 'fa60960a-0e38-4502-9834-1ab1277e9aaa', '39c87069-a3c6-4ab3-9974-f844a89872b5',
                                '10754933-241e-42db-bb3c-42e13cb40b0f', '7c37887f-0b8d-444c-bad7-e43cd2b41578', '197e41e7-cbc3-4eef-b5ad-5153c9232449', '4a64e90e-92ea-4d5a-a753-3c5ca92a2b95', '72af2230-8555-4e0c-a06a-721436a0644e',
                                '0a5a7796-6b53-408a-ba49-7fd9e51d82f4', 'a944a483-c23f-4d62-bdd8-d2a20a4aaeca', '9b1d1a9c-6c7e-44db-a15c-c0c82b1040eb', '0d51321e-e26c-4147-b730-5b1403384487', '0d6270fc-3744-42cd-a850-65ac0b8ef514',
                                'c1375dfe-c0b1-4e53-9c2b-6a0f29258a08', '1ab302fb-1b06-410a-b17a-fdf7843d6182', 'f38d83c9-c4b9-47b8-8135-f9bdf96cf8ec', 'e86583b9-12dd-4316-980a-5eeeba86f9ca');

DELETE FROM `rule_group_mapping` WHERE `rule_id` in ('ebc80d1e-5dd9-4b86-8037-fb7c9727084a', '9929cc2a-7a9b-4643-a813-0e4359b09eb0', '8ad73e84-141a-40c4-bd56-1813535e8e92', 'ee74d071-d39e-4b0e-b4a3-e7ad24fbce12', '0dc8ae24-71f8-449a-8834-d59f8fbdf991',
                                                   '1fbd8c76-90e7-48e4-8281-ef597bb484e8', '6bb291bd-fcdf-4aa9-8fdf-69e761c9c4e0', '350a097a-dfeb-4fd0-a3af-49049da5c025', 'fa60960a-0e38-4502-9834-1ab1277e9aaa', '39c87069-a3c6-4ab3-9974-f844a89872b5',
                                                   '10754933-241e-42db-bb3c-42e13cb40b0f', '7c37887f-0b8d-444c-bad7-e43cd2b41578', '197e41e7-cbc3-4eef-b5ad-5153c9232449', '4a64e90e-92ea-4d5a-a753-3c5ca92a2b95', '72af2230-8555-4e0c-a06a-721436a0644e',
                                                   '0a5a7796-6b53-408a-ba49-7fd9e51d82f4', 'a944a483-c23f-4d62-bdd8-d2a20a4aaeca', '9b1d1a9c-6c7e-44db-a15c-c0c82b1040eb', '0d51321e-e26c-4147-b730-5b1403384487', '0d6270fc-3744-42cd-a850-65ac0b8ef514',
                                                   'c1375dfe-c0b1-4e53-9c2b-6a0f29258a08', '1ab302fb-1b06-410a-b17a-fdf7843d6182', 'f38d83c9-c4b9-47b8-8135-f9bdf96cf8ec', 'e86583b9-12dd-4316-980a-5eeeba86f9ca');

DELETE FROM `rule_type` WHERE `rule_id` in ('ebc80d1e-5dd9-4b86-8037-fb7c9727084a', '9929cc2a-7a9b-4643-a813-0e4359b09eb0', '8ad73e84-141a-40c4-bd56-1813535e8e92', 'ee74d071-d39e-4b0e-b4a3-e7ad24fbce12', '0dc8ae24-71f8-449a-8834-d59f8fbdf991',
                                                     '1fbd8c76-90e7-48e4-8281-ef597bb484e8', '6bb291bd-fcdf-4aa9-8fdf-69e761c9c4e0', '350a097a-dfeb-4fd0-a3af-49049da5c025', 'fa60960a-0e38-4502-9834-1ab1277e9aaa', '39c87069-a3c6-4ab3-9974-f844a89872b5',
                                                     '10754933-241e-42db-bb3c-42e13cb40b0f', '7c37887f-0b8d-444c-bad7-e43cd2b41578', '197e41e7-cbc3-4eef-b5ad-5153c9232449', '4a64e90e-92ea-4d5a-a753-3c5ca92a2b95', '72af2230-8555-4e0c-a06a-721436a0644e',
                                                     '0a5a7796-6b53-408a-ba49-7fd9e51d82f4', 'a944a483-c23f-4d62-bdd8-d2a20a4aaeca', '9b1d1a9c-6c7e-44db-a15c-c0c82b1040eb', '0d51321e-e26c-4147-b730-5b1403384487', '0d6270fc-3744-42cd-a850-65ac0b8ef514',
                                                     'c1375dfe-c0b1-4e53-9c2b-6a0f29258a08', '1ab302fb-1b06-410a-b17a-fdf7843d6182', 'f38d83c9-c4b9-47b8-8135-f9bdf96cf8ec', 'e86583b9-12dd-4316-980a-5eeeba86f9ca');

DELETE FROM `rule_inspection_report_mapping` WHERE `rule_id` in ('ebc80d1e-5dd9-4b86-8037-fb7c9727084a', '9929cc2a-7a9b-4643-a813-0e4359b09eb0', '8ad73e84-141a-40c4-bd56-1813535e8e92', 'ee74d071-d39e-4b0e-b4a3-e7ad24fbce12', '0dc8ae24-71f8-449a-8834-d59f8fbdf991',
                                            '1fbd8c76-90e7-48e4-8281-ef597bb484e8', '6bb291bd-fcdf-4aa9-8fdf-69e761c9c4e0', '350a097a-dfeb-4fd0-a3af-49049da5c025', 'fa60960a-0e38-4502-9834-1ab1277e9aaa', '39c87069-a3c6-4ab3-9974-f844a89872b5',
                                            '10754933-241e-42db-bb3c-42e13cb40b0f', '7c37887f-0b8d-444c-bad7-e43cd2b41578', '197e41e7-cbc3-4eef-b5ad-5153c9232449', '4a64e90e-92ea-4d5a-a753-3c5ca92a2b95', '72af2230-8555-4e0c-a06a-721436a0644e',
                                            '0a5a7796-6b53-408a-ba49-7fd9e51d82f4', 'a944a483-c23f-4d62-bdd8-d2a20a4aaeca', '9b1d1a9c-6c7e-44db-a15c-c0c82b1040eb', '0d51321e-e26c-4147-b730-5b1403384487', '0d6270fc-3744-42cd-a850-65ac0b8ef514',
                                            'c1375dfe-c0b1-4e53-9c2b-6a0f29258a08', '1ab302fb-1b06-410a-b17a-fdf7843d6182', 'f38d83c9-c4b9-47b8-8135-f9bdf96cf8ec', 'e86583b9-12dd-4316-980a-5eeeba86f9ca');

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('AWS IAM 安全检查', 'AWS Identity and Access Management', '安全合规', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('AWS ISO 27001 安全检查', 'AWS ISO 27001:2013 Readiness', '最佳实践', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('AWS API Gateway 安全检查', 'AWS API Gateway security checks', '安全合规', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('AWS CIS Level 1', 'AWS CIS Level 1', 'CIS', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('AWS CIS Level 2', 'AWS CIS Level 2', 'CIS', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('AWS EKS 基准检查', 'AWS CIS EKS Benchmark', 'CIS', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('AWS Elasticsearch 安全检查', 'AWS Elasticsearch related security checks', '安全合规', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('AWS ENS 安全检查', 'AWS ENS Esquema Nacional de Seguridad security checks', '最佳实践', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('AWS FFIEC 网络安全检查', 'AWS FFIEC Cybersecurity Readiness', '最佳实践', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('AWS Forensics 安全检查', 'AWS Forensics Readiness', '安全合规', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('AWS FTR 安全检查', 'AWS Amazon FTR related security checks', '最佳实践', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('AWS GDPR 安全检查', 'AWS GDPR Readiness', '安全合规', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('AWS Glue 安全检查', 'AWS Glue related security checks', '最佳实践', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('AWS HIPAA 安全检查', 'AWS HIPAA Compliance', '安全合规', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('AWS Implementation Group 安全检查', 'AWS CIS Implementation Group', 'CIS', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('AWS Logging 安全检查', 'AWS Logging - CIS', 'CIS', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('AWS Monitoring 安全检查', 'AWS Monitoring', '安全合规', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('AWS Networking 安全检查', 'AWS Networking', '安全合规', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('AWS PCI-DSS v3.2.1 安全检查', 'AWS PCI-DSS v3.2.1 Readiness', '最佳实践', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('AWS RDS 安全检查', 'AWS RDS security checks', '安全合规', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('AWS SageMaker 安全检查', 'AWS SageMaker related security checks', '最佳实践', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('AWS SOC2 安全检查', 'AWS SOC2 Readiness - ONLY AS REFERENCE', '最佳实践', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('AWS 密钥/密码安全检查', 'AWS Look for keys secrets or passwords around resources', '安全合规', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('AWS 所有非 CIS 特定检查', 'AWS Extras - all non CIS specific checks', '安全合规', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('AWS 暴露的网络资源检查', 'AWS Find resources exposed to the internet', '最佳实践', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('AWS 服务目录安全检查', 'AWS Directory Service related security checks', '最佳实践', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('AWS 跨账户信任边界', 'AWS Find cross-account trust boundaries', '最佳实践', 'hummer-aws-plugin', 1);


INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('77047e0e-88da-422e-93db-2d3a0606e961', 'AWS Hardware MFA 检测', 1, 'CriticalRisk', 'AWS 确保为 root 帐户启用了硬件 MFA，Ensure hardware MFA is enabled for the root account。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.14 [check114] Ensure hardware MFA is enabled for the root account - iam [Critical]', '[{\"defaultValue\":\"check114\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('d80b3e78-6dfc-429c-bcdd-50ec212cfc42', 'AWS IAM 安全联系人检测', 1, 'MediumRisk', 'AWS 确保已注册安全联系人信息，Ensure security contact information is registered。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.18 [check118] Ensure security contact information is registered - support [Medium]', '[{\"defaultValue\":\"check118\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('705c1cef-b47b-4e0b-ab42-d1b360e6502e', 'AWS IAM 密码策略(大写)检测', 1, 'MediumRisk', 'AWS 确保 IAM 密码策略至少需要一个大写字母，Ensure IAM password policy requires at least one uppercase letter。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.5 [check15] Ensure IAM password policy requires at least one uppercase letter - iam [Medium]', '[{\"defaultValue\":\"check15\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('d8a2d0ca-4f5b-4809-8321-5ef485b2bf21', 'AWS IAM 密码策略(小写)检测', 1, 'MediumRisk', 'AWS 确保 IAM 密码策略至少需要一个小写字母，Ensure IAM password policy require at least one lowercase letter。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.6 [check16] Ensure IAM password policy require at least one lowercase letter - iam [Medium]', '[{\"defaultValue\":\"check16\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('67a2b3ad-6974-442c-9f03-32f0a8cd4f4d', 'AWS IAM 密码策略(数字)检测', 1, 'MediumRisk', 'AWS 确保 IAM 密码策略至少需要一个数字，Ensure IAM password policy require at least one number。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.8 [check18] Ensure IAM password policy require at least one number - iam [Medium]', '[{\"defaultValue\":\"check18\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('03979d46-6325-4b20-8d77-0e904a986064', 'AWS IAM 密码策略(符号)检测', 1, 'MediumRisk', 'AWS 确保 IAM 密码策略至少需要一个符号，Ensure IAM password policy require at least one symbol。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.7 [check17] Ensure IAM password policy require at least one symbol - iam [Medium]', '[{\"defaultValue\":\"check17\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('bb3eed24-02b1-43d1-9057-f4f800e4660e', 'AWS IAM 密码策略(过期)检测', 1, 'MediumRisk', 'AWS 确保 IAM 密码策略在 90 天或更短时间内过期，Ensure IAM password policy expires passwords within 90 days or less。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.11 [check111] Ensure IAM password policy expires passwords within 90 days or less - iam [Medium]', '[{\"defaultValue\":\"check111\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('b8844aed-9da9-469b-8ec8-a69fb1a2f7f2', 'AWS IAM 密码策略(重用)检测', 1, 'MediumRisk', 'AWS 确保 IAM 密码策略防止密码重用：24 或更大，Ensure IAM password policy prevents password reuse: 24 or greater。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.10 [check110] Ensure IAM password policy prevents password reuse: 24 or greater - iam [Medium]', '[{\"defaultValue\":\"check110\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('bd18fd9b-06af-4c98-8198-26b3aa3b78bc', 'AWS IAM 密码策略(长度)检测', 1, 'MediumRisk', 'AWS 确保 IAM 密码策略要求最小长度为 14 或更大，Ensure IAM password policy requires minimum length of 14 or greater。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.9 [check19] Ensure IAM password policy requires minimum length of 14 or greater - iam [Medium]', '[{\"defaultValue\":\"check19\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('f8579451-2c24-4743-87e3-0fcf7c465d08', 'AWS IAM 支持角色(Support 事件)检测', 1, 'MediumRisk', 'AWS 确保已创建支持角色来管理 Support 事件，Ensure a support role has been created to manage incidents with Support。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.20 [check120] Ensure a support role has been created to manage incidents with AWS Support - iam [Medium]', '[{\"defaultValue\":\"check120\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('e3fdf46f-782c-439f-b09f-2a616d42697d', 'AWS IAM 策略(组或角色)检测', 1, 'LowRisk', 'AWS 确保 IAM 策略仅附加到组或角色，Ensure security questions are registered in the AWS account。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.16 [check116] Ensure IAM policies are attached only to groups or roles - iam [Low]', '[{\"defaultValue\":\"check116\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('065d41f3-ff24-4e2e-989f-df71c45e3853', 'AWS IAM 联系方式检测', 1, 'MediumRisk', 'AWS 维护当前的联系方式，Maintain current contact details。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.17 [check117] Maintain current contact details - support [Medium]', '[{\"defaultValue\":\"check117\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('71427f80-643c-4e8e-9cb8-f07f544b2825', 'AWS IAM 访问实例(角色)检测', 1, 'MediumRisk', 'AWS 确保 IAM 角色从实例访问 AWS 资源，Ensure IAM instance roles are used for AWS resource access from instances。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.19 [check119] Ensure IAM instance roles are used for AWS resource access from instances - ec2 [Medium]', '[{\"defaultValue\":\"check119\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('d2628999-b60d-4e7f-812d-0cd5a94a7e38', 'AWS IAM 访问密钥(初始用户设置)检测', 1, 'MediumRisk', 'AWS 在初始用户设置期间不要为所有具有控制台密码的 IAM 用户设置访问密钥。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.21 [check121] Do not setup access keys during initial user setup for all IAM users that have a console password - iam [Medium]', '[{\"defaultValue\":\"check121\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('eac1ba91-eb46-4c44-b1f4-e709ea9d0e1e', 'AWS IAM 访问策略(管理权限)检测', 1, 'MediumRisk', 'AWS 确保未创建允许完全“*：*”管理权限的策略，Ensure policies that allow full \"*:*\" privileges not created。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.22 [check122] Ensure IAM policies that allow full \"*:*\" administrative privileges are not created - iam [Medium]', '[{\"defaultValue\":\"check122\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('22dd87f6-353c-42a7-a253-3957b17c1bb4', 'AWS MFA 启用检测', 1, 'CriticalRisk', 'AWS 确保为 root 帐户启用 MFA，Ensure no root account access key exists。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.13 [check113] Ensure MFA is enabled for the root account - iam [Critical]', '[{\"defaultValue\":\"check113\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('48e028e7-9eff-4c98-8a08-2c696e2842f7', 'AWS Root Account 检测', 1, 'HighRisk', 'AWS 避免使用 root 帐户，Avoid the use of the root account。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.1 [check11] Avoid the use of the root account - iam [High]', '[{\"key\":\"check\",\"name\":\"检测规则\",\"defaultValue\":\"check11\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('f2e017f1-70cb-4ad1-a187-8895c8040945', 'AWS 凭证使用时间检测', 1, 'MediumRisk', 'AWS 确保禁用 90 天或更长时间未使用的凭据，Ensure credentials unused for 90 days or greater are disabled。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.3 [check13] Ensure credentials unused for 90 days or greater are disabled - iam [Medium]', '[{\"defaultValue\":\"check13\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('b9f4c9f9-0fb6-47d6-af8a-57e9b8902245', 'AWS 多因素认证 MFA 检测', 1, 'HighRisk', 'AWS 检测是否开启 MFA，Ensure (MFA) is enabled for all IAM users that have a console password。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.2 [check12] Ensure multi-factor authentication (MFA) is enabled for all IAM users that have a console password - iam [High]', '[{\"defaultValue\":\"check12\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('c66cbddd-5846-4fb9-bedb-8a6812c51c5e', 'AWS 密钥使用时间检测', 1, 'MediumRisk', 'AWS 确保访问密钥每 90 天或更短时间轮换一次，Ensure access keys are rotated every 90 days or less。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.4 [check14] Ensure access keys are rotated every 90 days or less - iam [Medium]', '[{\"defaultValue\":\"check14\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('531cefda-5723-4a81-9ec1-5eb0c797b9a0', 'AWS 注册安全检测', 1, 'MediumRisk', 'AWS 确保在 AWS 账户中注册安全问题，Ensure security questions are registered in the AWS account。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.15 [check115] Ensure security questions are registered in the AWS account - support [Medium]', '[{\"defaultValue\":\"check115\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('3bd0f4f5-eb7f-48d5-b3db-1fd8cee80e07', 'AWS 访问密钥检测', 1, 'CriticalRisk', 'AWS 确保不存在 root 帐户访问密钥，Ensure no root account access key exists。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.12 [check112] Ensure no root account access key exists - iam [Critical]', '[{\"defaultValue\":\"check112\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('48e028e7-9eff-4c98-8a08-2c696e2842f7', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('b9f4c9f9-0fb6-47d6-af8a-57e9b8902245', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('c66cbddd-5846-4fb9-bedb-8a6812c51c5e', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('f2e017f1-70cb-4ad1-a187-8895c8040945', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('d8a2d0ca-4f5b-4809-8321-5ef485b2bf21', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('705c1cef-b47b-4e0b-ab42-d1b360e6502e', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('03979d46-6325-4b20-8d77-0e904a986064', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('67a2b3ad-6974-442c-9f03-32f0a8cd4f4d', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('bd18fd9b-06af-4c98-8198-26b3aa3b78bc', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('b8844aed-9da9-469b-8ec8-a69fb1a2f7f2', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('bb3eed24-02b1-43d1-9057-f4f800e4660e', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('3bd0f4f5-eb7f-48d5-b3db-1fd8cee80e07', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('22dd87f6-353c-42a7-a253-3957b17c1bb4', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('77047e0e-88da-422e-93db-2d3a0606e961', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('531cefda-5723-4a81-9ec1-5eb0c797b9a0', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('e3fdf46f-782c-439f-b09f-2a616d42697d', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('065d41f3-ff24-4e2e-989f-df71c45e3853', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('d80b3e78-6dfc-429c-bcdd-50ec212cfc42', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('71427f80-643c-4e8e-9cb8-f07f544b2825', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('f8579451-2c24-4743-87e3-0fcf7c465d08', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('d2628999-b60d-4e7f-812d-0cd5a94a7e38', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('eac1ba91-eb46-4c44-b1f4-e709ea9d0e1e', 'safety');

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('222dadad-480c-4da0-92a4-31c277d08e34', 'd80b3e78-6dfc-429c-bcdd-50ec212cfc42', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('244de811-6ea6-445f-82c3-c1aacbbe6413', '03979d46-6325-4b20-8d77-0e904a986064', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('28964f45-4e5b-4239-937f-3885f80a22e3', '531cefda-5723-4a81-9ec1-5eb0c797b9a0', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('337bfdc0-ab80-40c0-873c-5a98df763e7d', '705c1cef-b47b-4e0b-ab42-d1b360e6502e', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('482dfe34-3670-4de7-b8ac-d00b2eb91bdc', 'e3fdf46f-782c-439f-b09f-2a616d42697d', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('66aa5bf8-666f-409a-a090-31a93894b00c', '71427f80-643c-4e8e-9cb8-f07f544b2825', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('72ee44c2-6d31-4b03-9bc4-0a93911f1b2e', 'f8579451-2c24-4743-87e3-0fcf7c465d08', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('7c69de34-96ed-4bcf-a28a-ba9db035fe60', 'eac1ba91-eb46-4c44-b1f4-e709ea9d0e1e', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('8470b947-d2e7-4298-adee-e8d9e7e33875', '3bd0f4f5-eb7f-48d5-b3db-1fd8cee80e07', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('8d191a69-c786-4aea-84f5-eed2ffe791fc', 'd2628999-b60d-4e7f-812d-0cd5a94a7e38', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('94aa134d-2ac3-4cf9-ba57-b47a3c4e71fe', '67a2b3ad-6974-442c-9f03-32f0a8cd4f4d', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('9bd62fd6-2804-4379-921a-312190f86910', 'c66cbddd-5846-4fb9-bedb-8a6812c51c5e', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('9dc43cc7-c2dc-4412-a257-9b5db6d12691', '065d41f3-ff24-4e2e-989f-df71c45e3853', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('a7e0eba8-1598-4a12-8306-6e2b5cd87062', 'b8844aed-9da9-469b-8ec8-a69fb1a2f7f2', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('ba32ff4f-86da-423c-9f5a-f367cd33877f', 'b9f4c9f9-0fb6-47d6-af8a-57e9b8902245', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('c299e122-8c2d-4891-8621-87826425da66', 'bd18fd9b-06af-4c98-8198-26b3aa3b78bc', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('c58df2cd-66e2-4c56-8107-4686f8c3cd2f', '48e028e7-9eff-4c98-8a08-2c696e2842f7', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('d33708e6-1fb4-4a6e-bc01-ec5acc6b5e9a', 'd8a2d0ca-4f5b-4809-8321-5ef485b2bf21', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('d61b642b-0516-46d7-b3d3-d2bcdaa22845', 'bb3eed24-02b1-43d1-9057-f4f800e4660e', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('e97f49af-4079-4a78-8f84-cdfa6f93231d', '77047e0e-88da-422e-93db-2d3a0606e961', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('ea13e3c3-35f3-4bc7-ab8f-e734e6486d96', 'f2e017f1-70cb-4ad1-a187-8895c8040945', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('ffb8880f-8162-4140-b027-d20dac6a1703', '22dd87f6-353c-42a7-a253-3957b17c1bb4', 'prowler');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b9f4c9f9-0fb6-47d6-af8a-57e9b8902245', '90');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c66cbddd-5846-4fb9-bedb-8a6812c51c5e', '114');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f2e017f1-70cb-4ad1-a187-8895c8040945', '114');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d8a2d0ca-4f5b-4809-8321-5ef485b2bf21', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('705c1cef-b47b-4e0b-ab42-d1b360e6502e', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('03979d46-6325-4b20-8d77-0e904a986064', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('67a2b3ad-6974-442c-9f03-32f0a8cd4f4d', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('bd18fd9b-06af-4c98-8198-26b3aa3b78bc', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b8844aed-9da9-469b-8ec8-a69fb1a2f7f2', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b8844aed-9da9-469b-8ec8-a69fb1a2f7f2', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b8844aed-9da9-469b-8ec8-a69fb1a2f7f2', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('bb3eed24-02b1-43d1-9057-f4f800e4660e', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('bb3eed24-02b1-43d1-9057-f4f800e4660e', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('bb3eed24-02b1-43d1-9057-f4f800e4660e', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('3bd0f4f5-eb7f-48d5-b3db-1fd8cee80e07', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('3bd0f4f5-eb7f-48d5-b3db-1fd8cee80e07', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('3bd0f4f5-eb7f-48d5-b3db-1fd8cee80e07', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('3bd0f4f5-eb7f-48d5-b3db-1fd8cee80e07', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('22dd87f6-353c-42a7-a253-3957b17c1bb4', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('22dd87f6-353c-42a7-a253-3957b17c1bb4', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('22dd87f6-353c-42a7-a253-3957b17c1bb4', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('22dd87f6-353c-42a7-a253-3957b17c1bb4', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('77047e0e-88da-422e-93db-2d3a0606e961', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('77047e0e-88da-422e-93db-2d3a0606e961', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('77047e0e-88da-422e-93db-2d3a0606e961', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('77047e0e-88da-422e-93db-2d3a0606e961', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('531cefda-5723-4a81-9ec1-5eb0c797b9a0', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('531cefda-5723-4a81-9ec1-5eb0c797b9a0', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('531cefda-5723-4a81-9ec1-5eb0c797b9a0', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('531cefda-5723-4a81-9ec1-5eb0c797b9a0', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e3fdf46f-782c-439f-b09f-2a616d42697d', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e3fdf46f-782c-439f-b09f-2a616d42697d', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e3fdf46f-782c-439f-b09f-2a616d42697d', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e3fdf46f-782c-439f-b09f-2a616d42697d', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('065d41f3-ff24-4e2e-989f-df71c45e3853', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('065d41f3-ff24-4e2e-989f-df71c45e3853', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('065d41f3-ff24-4e2e-989f-df71c45e3853', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('065d41f3-ff24-4e2e-989f-df71c45e3853', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d80b3e78-6dfc-429c-bcdd-50ec212cfc42', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d80b3e78-6dfc-429c-bcdd-50ec212cfc42', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d80b3e78-6dfc-429c-bcdd-50ec212cfc42', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d80b3e78-6dfc-429c-bcdd-50ec212cfc42', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('71427f80-643c-4e8e-9cb8-f07f544b2825', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('71427f80-643c-4e8e-9cb8-f07f544b2825', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('71427f80-643c-4e8e-9cb8-f07f544b2825', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('71427f80-643c-4e8e-9cb8-f07f544b2825', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f8579451-2c24-4743-87e3-0fcf7c465d08', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f8579451-2c24-4743-87e3-0fcf7c465d08', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f8579451-2c24-4743-87e3-0fcf7c465d08', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f8579451-2c24-4743-87e3-0fcf7c465d08', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d2628999-b60d-4e7f-812d-0cd5a94a7e38', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d2628999-b60d-4e7f-812d-0cd5a94a7e38', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d2628999-b60d-4e7f-812d-0cd5a94a7e38', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d2628999-b60d-4e7f-812d-0cd5a94a7e38', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('eac1ba91-eb46-4c44-b1f4-e709ea9d0e1e', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('eac1ba91-eb46-4c44-b1f4-e709ea9d0e1e', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('eac1ba91-eb46-4c44-b1f4-e709ea9d0e1e', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('eac1ba91-eb46-4c44-b1f4-e709ea9d0e1e', '96');

SELECT id INTO @groupId1 from rule_group where name = 'AWS IAM 安全检查';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('48e028e7-9eff-4c98-8a08-2c696e2842f7', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b9f4c9f9-0fb6-47d6-af8a-57e9b8902245', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c66cbddd-5846-4fb9-bedb-8a6812c51c5e', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f2e017f1-70cb-4ad1-a187-8895c8040945', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8a2d0ca-4f5b-4809-8321-5ef485b2bf21', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('705c1cef-b47b-4e0b-ab42-d1b360e6502e', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('03979d46-6325-4b20-8d77-0e904a986064', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('67a2b3ad-6974-442c-9f03-32f0a8cd4f4d', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bd18fd9b-06af-4c98-8198-26b3aa3b78bc', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b8844aed-9da9-469b-8ec8-a69fb1a2f7f2', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bb3eed24-02b1-43d1-9057-f4f800e4660e', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('3bd0f4f5-eb7f-48d5-b3db-1fd8cee80e07', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('22dd87f6-353c-42a7-a253-3957b17c1bb4', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('77047e0e-88da-422e-93db-2d3a0606e961', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('531cefda-5723-4a81-9ec1-5eb0c797b9a0', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e3fdf46f-782c-439f-b09f-2a616d42697d', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('065d41f3-ff24-4e2e-989f-df71c45e3853', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d80b3e78-6dfc-429c-bcdd-50ec212cfc42', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('71427f80-643c-4e8e-9cb8-f07f544b2825', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f8579451-2c24-4743-87e3-0fcf7c465d08', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d2628999-b60d-4e7f-812d-0cd5a94a7e38', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('eac1ba91-eb46-4c44-b1f4-e709ea9d0e1e', @groupId1);
