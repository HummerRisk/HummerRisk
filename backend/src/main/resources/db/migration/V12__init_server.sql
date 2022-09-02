
CREATE TABLE IF NOT EXISTS server_group (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `name`                       varchar(128)        DEFAULT NULL UNIQUE COMMENT '虚拟机分组名称',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `creator`                    varchar(128)        DEFAULT NULL COMMENT '创建人',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

INSERT INTO `server_group` (`id`, `name`, `create_time`, `update_time`, `creator`) VALUES ('d691se79-2e8c-1s54-bbe6-491sd29e91fe', 'default', concat(unix_timestamp(now()), '001'), concat(unix_timestamp(now()), '001'), 'admin');

CREATE TABLE IF NOT EXISTS server (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '虚拟机名称',
    `server_group_id`            varchar(128)        DEFAULT 'd691se79-2e8c-1s54-bbe6-491sd29e91fe' COMMENT 'Server Group ID',
    `plugin_icon`                varchar(256)        DEFAULT 'server.png' COMMENT '图标',
    `status`                     varchar(10)         DEFAULT NULL COMMENT '虚拟机状态',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `creator`                    varchar(128)        DEFAULT NULL COMMENT '创建人',
    `ip`                         varchar(128)        DEFAULT NULL COMMENT 'IP',
    `port`                       varchar(128)        DEFAULT '22' COMMENT 'Port',
    `user_name`                  varchar(128)        DEFAULT 'root' COMMENT 'UserName',
    `password`                   varchar(256)        COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'Password',
    `is_proxy`                   tinyint(1)          DEFAULT 0 COMMENT '是否启用代理',
    `proxy_id`                   int(11)             DEFAULT NULL COMMENT '代理ID',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `server_rule` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `name`                       varchar(50)         DEFAULT NULL UNIQUE COMMENT '规则名称',
    `status`                     tinyint(1)          DEFAULT 1 COMMENT '规则状态(启用1，停用0)',
    `severity`                   varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `description`                varchar(1024)       DEFAULT NULL COMMENT '`描述',
    `script`                     mediumtext          DEFAULT NULL COMMENT '脚本',
    `parameter`                  varchar(1024)       DEFAULT NULL COMMENT '参数',
    `last_modified`              bigint(14)          DEFAULT NULL COMMENT '上次更新时间',
    `flag`                       tinyint(1)          NOT NULL DEFAULT 0 COMMENT '是否内置',
    PRIMARY KEY (`id`),
    KEY `IDX_NAME` (`name`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('0cfdd71d-f4c0-42b0-a8a7-af8760640c8e', 'Linux 下获取主机名', 1, 'LowRisk', 'Linux 下获取主机名 hostname 信息', 'hostname', '[]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('10e326fb-8a12-4e4b-9f02-e2ea0e3c7e82', 'Linux 下查询进程信息', 1, 'MediumRisk', 'Linux 下查询系统所有进程信息', 'ps aux f', '[]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('17d8a321-4eb2-41cc-a466-e29293dc8755', 'Linux 下查看内存', 1, 'HighRisk', 'Linux 下查看内存使用情况', 'free -h', '[]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('63b8bb9e-02c5-48ca-a7d4-0eeb93bcfef2', 'Linux 下查看磁盘空间', 1, 'MediumRisk', 'Linux 下查看磁盘空间使用情况', 'df -hl', '[]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('73fdb432-62c4-4b08-9682-44dae7e6c312', 'Linux 下查看 CPU', 1, 'HighRisk', 'Linux 下查看 CPU 使用率', 'top -bn 1 -i -c', '[]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('77c50b70-4d5c-4231-b484-1cbcd4a7f5fe', 'Linux 下查看端口和进程', 1, 'HighRisk', 'Linux 下查看端口和进程情况', 'netstat -tunlp', '[]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('78637c3b-2206-40f2-9882-a3d29a6d2222', 'Linux 下查看网络', 1, 'LowRisk', 'Linux 下查看所有网络接口的属性', 'ifconfig', '[]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('7a9cfc62-3c78-4f08-8210-d9bbfe47980c', 'Linux 下查看防火墙规则', 1, 'HighRisk', 'Linux 下查看防火墙是否开启，以及防火墙规则', 'firewall-cmd --list-all', '[]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('8f7d6459-a421-4cd3-b1ce-c34a40c237ea', 'Linux 下查看操作系统', 1, 'LowRisk', 'Linux 下查看操作系统详情信息', 'cat /proc/version', '[]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('becf4c7c-0976-44ef-9b3e-ba93abf46f71', 'Linux 下查看系统用户', 1, 'LowRisk', 'Linux 下查看系统用户信息', 'cat /etc/passwd', '[]', concat(unix_timestamp(now()), '002'), 1);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('7a9cfc62-3c78-4f08-8210-d9bbfe47980c', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('77c50b70-4d5c-4231-b484-1cbcd4a7f5fe', 'server');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('0cfdd71d-f4c0-42b0-a8a7-af8760640c8e', 'server');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('63b8bb9e-02c5-48ca-a7d4-0eeb93bcfef2', 'cost');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('17d8a321-4eb2-41cc-a466-e29293dc8755', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('73fdb432-62c4-4b08-9682-44dae7e6c312', 'server');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('becf4c7c-0976-44ef-9b3e-ba93abf46f71', 'tagging');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('78637c3b-2206-40f2-9882-a3d29a6d2222', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('8f7d6459-a421-4cd3-b1ce-c34a40c237ea', 'server');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('10e326fb-8a12-4e4b-9f02-e2ea0e3c7e82', 'server');

CREATE TABLE IF NOT EXISTS `server_result`
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

CREATE TABLE IF NOT EXISTS `server_result_log` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `result_id`                    varchar(50)         DEFAULT NULL COMMENT '检测结果ID',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    varchar(1024)       DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint(1)          DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;
