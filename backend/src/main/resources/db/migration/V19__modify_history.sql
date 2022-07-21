
RENAME TABLE cloud_scan_history TO history_scan;

RENAME TABLE cloud_scan_task_history TO history_scan_task;

ALTER TABLE history_scan ADD `status` varchar(20) DEFAULT NULL COMMENT '状态';

ALTER TABLE history_scan ADD `account_type` varchar(50) DEFAULT NULL COMMENT '资源类型：cloudAccount/vulnAccount/serverAccount/imageAccount/packageAccount';

ALTER TABLE history_scan_task ADD `status` varchar(20) DEFAULT NULL COMMENT '状态';

ALTER TABLE history_scan_task ADD `account_id` varchar(50) DEFAULT NULL COMMENT '资源ID';

ALTER TABLE history_scan_task ADD `account_type` varchar(50) DEFAULT NULL COMMENT '资源类型：cloudAccount/vulnAccount/serverAccount/imageAccount/packageAccount';

CREATE TABLE IF NOT EXISTS `history_cloud_task` (
    `id`                            varchar(50)           NOT NULL COMMENT '任务ID',
    `status`                        varchar(20)           DEFAULT NULL COMMENT '状态',
    `apply_user`                    varchar(50)           DEFAULT NULL COMMENT '申请人',
    `create_time`                   bigint(13)            DEFAULT NULL COMMENT '创建时间',
    `task_name`                     varchar(256)          DEFAULT NULL COMMENT '任务名称',
    `description`                   varchar(255)          DEFAULT NULL COMMENT '描述',
    `cron`                          varchar(128)          DEFAULT NULL COMMENT 'cron表达式',
    `trigger_id`                    varchar(255)          DEFAULT NULL COMMENT '触发ID',
    `prev_fire_time`                bigint(20)            DEFAULT NULL COMMENT '上次执行时间',
    `last_fire_time`                bigint(20)            DEFAULT NULL COMMENT '下次执行时间',
    `type`                          varchar(32)           DEFAULT NULL COMMENT '类型：manual(手动)/quartz(定时)',
    `severity`                      varchar(32)           DEFAULT NULL COMMENT '风险等级',
    `rule_id`                       varchar(50)           DEFAULT NULL COMMENT '规则ID',
    `rule_tags`                     varchar(256)          DEFAULT NULL COMMENT '标签IDs',
    `account_id`                    varchar(50)           DEFAULT NULL COMMENT '云账号ID',
    `plugin_id`                     varchar(40)           DEFAULT NULL COMMENT '插件ID',
    `plugin_name`                   varchar(128)          DEFAULT NULL COMMENT '云平台名称',
    `plugin_icon`                   varchar(128)          DEFAULT NULL COMMENT '云平台图标',
    `resource_types`                varchar(256)          DEFAULT NULL COMMENT '资源类型',
    `resources_sum`                 bigint(13)            DEFAULT 0 COMMENT '资源总量',
    `return_sum`                    bigint(13)            DEFAULT 0 COMMENT '输出检测结果资源数',
    `cron_desc`                     varchar(512)          DEFAULT NULL COMMENT '定时时间(cron中文翻译)',
    `scan_type`                     varchar(32)           DEFAULT NULL COMMENT '检测类型',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_cloud_task_item` (
    `id`                           varchar(50)         NOT NULL,
    `task_id`                      varchar(50)         DEFAULT NULL COMMENT '任务ID',
    `rule_id`                      varchar(50)         DEFAULT NULL COMMENT '规则ID',
    `details`                      longtext            DEFAULT NULL COMMENT 'policy内容,不含actions',
    `tags`                         longtext            DEFAULT NULL COMMENT '标签',
    `custom_data`                  longtext            DEFAULT NULL COMMENT 'policy内容,包含actions',
    `status`                       varchar(20)         DEFAULT NULL COMMENT '状态',
    `count`                        int(11)             DEFAULT '1'  COMMENT '数量',
    `region_id`                    varchar(128)        DEFAULT NULL COMMENT '区域标识',
    `region_name`                  varchar(128)        DEFAULT NULL COMMENT '区域名称',
    `severity`                     varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `account_id`                   varchar(50)         DEFAULT NULL COMMENT '云账号ID',
    `account_url`                  varchar(128)        DEFAULT NULL COMMENT '云账号图标',
    `account_label`                varchar(128)        DEFAULT NULL COMMENT '云账号名称',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                  bigint(13)          DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_cloud_task_log` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `task_item_id`                 varchar(50)         DEFAULT NULL COMMENT '任务项ID',
    `resource_id`                  varchar(50)         DEFAULT NULL COMMENT '资源ID',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    varchar(255)        DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint(1)          DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_cloud_task_resource`
(
    `id`                         varchar(50)         NOT NULL,
    `resource_name`              varchar(256)        DEFAULT NULL COMMENT '资源名称',
    `dir_name`                   varchar(128)        DEFAULT NULL COMMENT '目录名称',
    `resource_status`            varchar(45)         DEFAULT NULL COMMENT '资源状态',
    `resource_type`              varchar(64)         DEFAULT NULL COMMENT '资源类型',
    `custodian_run_log`          longtext            DEFAULT NULL COMMENT 'custodian-run.log',
    `metadata`                   longtext            DEFAULT NULL COMMENT 'metadata.json',
    `resources`                  longtext            DEFAULT NULL COMMENT 'resources.json',
    `resources_sum`              bigint(13)          DEFAULT 0 COMMENT '资源总量',
    `return_sum`                 bigint(13)          DEFAULT 0 COMMENT '输出检测结果资源数',
    `plugin_id`                  varchar(40)         DEFAULT NULL COMMENT '插件名称',
    `plugin_name`                varchar(40)         DEFAULT NULL COMMENT '云平台名称',
    `plugin_icon`                varchar(128)        DEFAULT NULL COMMENT '云平台图标',
    `account_id`                 varchar(50)         DEFAULT NULL COMMENT '云账号ID',
    `region_id`                  varchar(128)        DEFAULT NULL COMMENT '区域标识',
    `region_name`                varchar(128)        DEFAULT NULL COMMENT '区域名称',
    `severity`                   varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `resource_command`           longtext            DEFAULT NULL COMMENT 'policy(无actions)',
    `resource_command_action`    longtext            DEFAULT NULL COMMENT 'policy(有actions)',
    `return_html`                varchar(255)        DEFAULT ''   COMMENT 'return html地址',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_vuln_task` (
    `id`                            varchar(50)           NOT NULL COMMENT '任务ID',
    `status`                        varchar(20)           DEFAULT NULL COMMENT '状态',
    `apply_user`                    varchar(50)           DEFAULT NULL COMMENT '申请人',
    `create_time`                   bigint(13)            DEFAULT NULL COMMENT '创建时间',
    `task_name`                     varchar(256)          DEFAULT NULL COMMENT '任务名称',
    `description`                   varchar(255)          DEFAULT NULL COMMENT '描述',
    `cron`                          varchar(128)          DEFAULT NULL COMMENT 'cron表达式',
    `trigger_id`                    varchar(255)          DEFAULT NULL COMMENT '触发ID',
    `prev_fire_time`                bigint(20)            DEFAULT NULL COMMENT '上次执行时间',
    `last_fire_time`                bigint(20)            DEFAULT NULL COMMENT '下次执行时间',
    `type`                          varchar(32)           DEFAULT NULL COMMENT '类型：manual(手动)/quartz(定时)',
    `severity`                      varchar(32)           DEFAULT NULL COMMENT '风险等级',
    `rule_id`                       varchar(50)           DEFAULT NULL COMMENT '规则ID',
    `rule_tags`                     varchar(256)          DEFAULT NULL COMMENT '标签IDs',
    `account_id`                    varchar(50)           DEFAULT NULL COMMENT '漏洞检测ID',
    `plugin_id`                     varchar(40)           DEFAULT NULL COMMENT '插件ID',
    `plugin_name`                   varchar(128)          DEFAULT NULL COMMENT '漏洞检测引擎名称',
    `plugin_icon`                   varchar(128)          DEFAULT NULL COMMENT '漏洞检测引擎图标',
    `resource_types`                varchar(256)          DEFAULT NULL COMMENT '资源类型',
    `resources_sum`                 bigint(13)            DEFAULT 0 COMMENT '资源总量',
    `return_sum`                    bigint(13)            DEFAULT 0 COMMENT '输出检测结果资源数',
    `cron_desc`                     varchar(512)          DEFAULT NULL COMMENT '定时时间(cron中文翻译)',
    `scan_type`                     varchar(32)           DEFAULT NULL COMMENT '检测类型',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_vuln_task_item` (
    `id`                           varchar(50)         NOT NULL,
    `task_id`                      varchar(50)         DEFAULT NULL COMMENT '任务ID',
    `rule_id`                      varchar(50)         DEFAULT NULL COMMENT '规则ID',
    `details`                      longtext            DEFAULT NULL COMMENT 'policy内容,不含actions',
    `tags`                         longtext            DEFAULT NULL COMMENT '标签',
    `custom_data`                  longtext            DEFAULT NULL COMMENT 'policy内容,包含actions',
    `status`                       varchar(20)         DEFAULT NULL COMMENT '状态',
    `count`                        int(11)             DEFAULT '1'  COMMENT '数量',
    `region_id`                    varchar(128)        DEFAULT NULL COMMENT '区域标识',
    `region_name`                  varchar(128)        DEFAULT NULL COMMENT '区域名称',
    `severity`                     varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `account_id`                   varchar(50)         DEFAULT NULL COMMENT '漏洞检测ID',
    `account_url`                  varchar(128)        DEFAULT NULL COMMENT '漏洞检测引擎图标',
    `account_label`                varchar(128)        DEFAULT NULL COMMENT '漏洞检测名称',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                  bigint(13)          DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_vuln_task_log` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `task_item_id`                 varchar(50)         DEFAULT NULL COMMENT '任务项ID',
    `resource_id`                  varchar(50)         DEFAULT NULL COMMENT '资源ID',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    varchar(255)        DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint(1)          DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_vuln_task_resource`
(
    `id`                         varchar(50)         NOT NULL,
    `resource_name`              varchar(256)        DEFAULT NULL COMMENT '资源名称',
    `dir_name`                   varchar(128)        DEFAULT NULL COMMENT '目录名称',
    `resource_status`            varchar(45)         DEFAULT NULL COMMENT '资源状态',
    `resource_type`              varchar(64)         DEFAULT NULL COMMENT '资源类型',
    `custodian_run_log`          longtext            DEFAULT NULL COMMENT 'custodian-run.log',
    `metadata`                   longtext            DEFAULT NULL COMMENT 'metadata.json',
    `resources`                  longtext            DEFAULT NULL COMMENT 'resources.json',
    `resources_sum`              bigint(13)          DEFAULT 0 COMMENT '资源总量',
    `return_sum`                 bigint(13)          DEFAULT 0 COMMENT '输出检测结果资源数',
    `plugin_id`                  varchar(40)         DEFAULT NULL COMMENT '插件名称',
    `plugin_name`                varchar(40)         DEFAULT NULL COMMENT '漏洞检测名称',
    `plugin_icon`                varchar(128)        DEFAULT NULL COMMENT '漏洞检测图标',
    `account_id`                 varchar(50)         DEFAULT NULL COMMENT '漏洞检测ID',
    `region_id`                  varchar(128)        DEFAULT NULL COMMENT '区域标识',
    `region_name`                varchar(128)        DEFAULT NULL COMMENT '区域名称',
    `severity`                   varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `resource_command`           longtext            DEFAULT NULL COMMENT 'policy(无actions)',
    `resource_command_action`    longtext            DEFAULT NULL COMMENT 'policy(有actions)',
    `return_html`                varchar(255)        DEFAULT ''   COMMENT 'return html地址',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_server_task`
(
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `server_id`                  varchar(50)         DEFAULT NULL COMMENT 'serverID',
    `server_name`                varchar(256)        DEFAULT NULL COMMENT '虚拟机名称',
    `rule_id`                    varchar(50)         DEFAULT NULL COMMENT '虚拟机规则ID',
    `rule_name`                  varchar(50)         DEFAULT NULL COMMENT '虚拟机规则名称',
    `rule_desc`                  varchar(50)         DEFAULT NULL COMMENT '虚拟机规则描述',
    `result_status`              varchar(45)         DEFAULT NULL COMMENT '检测状态',
    `severity`                   varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `apply_user`                 varchar(50)         DEFAULT NULL COMMENT '申请人',
    `server_group_id`            varchar(128)        DEFAULT 'd691se79-2e8c-1s54-bbe6-491sd29e91fe' COMMENT 'Server Group ID',
    `server_group_name`          varchar(128)        DEFAULT NULL COMMENT '虚拟机分组名称',
    `plugin_icon`                varchar(256)        DEFAULT 'server.png' COMMENT '图标',
    `ip`                         varchar(128)        DEFAULT NULL COMMENT 'IP',
    `port`                       varchar(128)        DEFAULT '22' COMMENT 'Port',
    `user_name`                  varchar(128)        DEFAULT 'root' COMMENT 'UserName',
    `password`                   varchar(256)        COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'Password',
    `return_log`                 longtext            DEFAULT NULL COMMENT 'return log',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_server_task_log` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `result_id`                    varchar(50)         DEFAULT NULL COMMENT '检测结果ID',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    varchar(255)        DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint(1)          DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_image_task`
(
    `id`                         varchar(50)         NOT NULL,
    `image_id`                   varchar(50)         DEFAULT NULL COMMENT 'imageID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '镜像检测名称(别名)',
    `size`                       varchar(128)        DEFAULT '0M' COMMENT '镜像大小',
    `plugin_icon`                varchar(256)        DEFAULT 'docker.png' COMMENT '图标地址',
    `rule_id`                    varchar(50)         DEFAULT NULL COMMENT '镜像检测规则ID',
    `rule_name`                  varchar(50)         DEFAULT NULL COMMENT '镜像检测规则名称',
    `rule_desc`                  varchar(50)         DEFAULT NULL COMMENT '镜像检测规则描述',
    `result_status`              varchar(45)         DEFAULT NULL COMMENT '检测状态',
    `severity`                   varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `apply_user`                 varchar(50)         DEFAULT NULL COMMENT '创建人ID',
    `user_name`                  varchar(128)        DEFAULT NULL COMMENT '创建人名称',
    `return_log`                 longtext            DEFAULT NULL COMMENT 'return log',
    `grype_table`                longtext            DEFAULT NULL COMMENT 'grype table',
    `grype_json`                 longtext            DEFAULT NULL COMMENT 'grype json',
    `syft_table`                 longtext            DEFAULT NULL COMMENT 'syft table',
    `syft_json`                  longtext            DEFAULT NULL COMMENT 'syft json',
    `return_sum`                 bigint(13)          DEFAULT 0 COMMENT '输出检测结果漏洞数',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_image_task_item` (
    `id`                         varchar(50)         NOT NULL COMMENT '资源ID（唯一标识）',
    `result_id`                  varchar(50)         DEFAULT NULL COMMENT 'result主键ID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '镜像检测名称(别名)',
    `severity`                   varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `resource`                   longtext            DEFAULT NULL COMMENT '资源JSON',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_image_task_log` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `result_id`                    varchar(50)         DEFAULT NULL COMMENT '检测结果ID',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    varchar(255)        DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint(1)          DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_package_task`
(
    `id`                         varchar(50)         NOT NULL,
    `package_id`                 varchar(50)         DEFAULT NULL COMMENT 'packageID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '软件包名称(别名)',
    `package_name`               varchar(128)        DEFAULT NULL COMMENT '软件包名称',
    `size`                       varchar(128)        DEFAULT '0M' COMMENT '软件包大小',
    `plugin_icon`                varchar(256)        DEFAULT 'package.png' COMMENT '图标地址',
    `rule_id`                    varchar(50)         DEFAULT NULL COMMENT '软件包规则ID',
    `rule_name`                  varchar(50)         DEFAULT NULL COMMENT '软件包规则名称',
    `rule_desc`                  varchar(50)         DEFAULT NULL COMMENT '软件包规则描述',
    `result_status`              varchar(45)         DEFAULT NULL COMMENT '检测状态',
    `severity`                   varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `apply_user`                 varchar(50)         DEFAULT NULL COMMENT '创建人ID',
    `user_name`                  varchar(128)        DEFAULT NULL COMMENT '创建人名称',
    `resources`                  longtext            DEFAULT NULL COMMENT 'resources',
    `return_json`                longtext            DEFAULT NULL COMMENT 'return json',
    `return_html`                varchar(256)        DEFAULT '' COMMENT 'return html地址',
    `return_sum`                 bigint(13)          DEFAULT 0 COMMENT '输出检测结果依赖数',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_package_task_item` (
    `id`                         varchar(50)         NOT NULL COMMENT '资源ID（唯一标识）',
    `result_id`                  varchar(50)         DEFAULT NULL COMMENT 'result主键ID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '软件包名称(别名)',
    `severity`                   varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `resource`                   longtext            DEFAULT NULL COMMENT '资源JSON',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_package_task_log` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `result_id`                    varchar(50)         DEFAULT NULL COMMENT '检测结果ID',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    varchar(255)        DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint(1)          DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;
