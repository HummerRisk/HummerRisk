
CREATE TABLE IF NOT EXISTS `image_group` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `name`                       varchar(256)        DEFAULT NULL COMMENT '分组名称',
    `repository`                 varchar(256)        DEFAULT NULL COMMENT 'repository',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint              DEFAULT NULL COMMENT '更新时间',
    `creator`                    varchar(128)        DEFAULT NULL COMMENT '创建人',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

INSERT INTO `image_group` (`id`, `name`, `create_time`, `update_time`, `creator`) VALUES ('d661se75-1r8c-2s54-cbe6-351sd29e91ff', 'default', concat(unix_timestamp(now()), '001'), concat(unix_timestamp(now()), '001'), 'admin');

ALTER TABLE `image` ADD `group_id` varchar(50) DEFAULT NULL COMMENT '分组Id';

UPDATE `image` SET group_id = 'd661se75-1r8c-2s54-cbe6-351sd29e91ff';

ALTER TABLE `image` ADD `repo_item_id` varchar(50) DEFAULT NULL COMMENT '镜像仓库中同步的镜像Id';

ALTER TABLE `server_result` modify COLUMN `command` mediumtext DEFAULT NULL COMMENT 'command';

CREATE TABLE IF NOT EXISTS `server_lynis_result`
(
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `server_id`                  varchar(50)         DEFAULT NULL COMMENT 'serverID',
    `server_name`                varchar(256)        DEFAULT NULL COMMENT '虚拟机名称',
    `result_status`              varchar(45)         DEFAULT NULL COMMENT '检测状态',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint              DEFAULT NULL COMMENT '更新时间',
    `apply_user`                 varchar(50)         DEFAULT NULL COMMENT '申请人',
    `plugin_icon`                varchar(256)        DEFAULT 'server.png' COMMENT '图标',
    `return_log`                 longtext            DEFAULT NULL COMMENT 'return log',
    `lynis_log`                  longtext            DEFAULT NULL COMMENT 'lynis log',
    `hardening_index`            bigint              DEFAULT NULL COMMENT 'Hardening index',
    `tests_performed`            bigint              DEFAULT NULL COMMENT 'Tests performed',
    `plugins_enabled`            bigint              DEFAULT NULL COMMENT 'Plugins enabled',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `server_lynis_result_detail` (
    `id`                           int                 NOT NULL AUTO_INCREMENT,
    `lynis_id`                     varchar(50)         DEFAULT NULL COMMENT 'lynis 检测结果ID',
    `create_time`                  bigint              DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `type`                         varchar(128)        DEFAULT NULL COMMENT '所属类型',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `status`                       varchar(100)        DEFAULT NULL COMMENT '状态',
    `order_index`                  bigint              DEFAULT NULL COMMENT '排序',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

ALTER TABLE `hummer_license` ADD `status` varchar(50) DEFAULT NULL COMMENT 'valid、invalid、expired，分别代表：有效、无效、已过期';

ALTER TABLE `hummer_license` ADD `message` text DEFAULT NULL COMMENT 'message 提示告警信息';

