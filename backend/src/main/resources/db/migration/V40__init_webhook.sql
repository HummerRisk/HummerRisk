

CREATE TABLE IF NOT EXISTS `webhook`
(
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `name`                       varchar(128)        DEFAULT NULL UNIQUE COMMENT '名称',
    `webhook`                    varchar(1024)       DEFAULT NULL COMMENT 'webhook',
    `status`                     tinyint(1)          DEFAULT 1 COMMENT '规则状态(启用1，停用0)',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `creator`                    varchar(128)        DEFAULT NULL COMMENT '创建人',
    `proxy_id`                   int(11)             DEFAULT NULL COMMENT '代理ID',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

ALTER TABLE `server` ADD `type` varchar(64) DEFAULT 'linux' COMMENT 'type: linux/windows';

ALTER TABLE `server_rule` ADD `type` varchar(64) DEFAULT 'linux' COMMENT 'type: linux/windows';

ALTER TABLE `server_result` ADD `type` varchar(64) DEFAULT 'linux' COMMENT 'type: linux/windows';

ALTER TABLE `history_server_result` ADD `type` varchar(64) DEFAULT 'linux' COMMENT 'type: linux/windows';

UPDATE `server` SET `type` = 'linux';

UPDATE `server_rule` SET `type` = 'linux';

UPDATE `server_result` SET `type` = 'linux';

UPDATE `history_server_result` SET `type` = 'linux';

INSERT INTO `plugin` (`id`, `name`, `icon`, `update_time`, `scan_type`, `order_`, `type`) VALUES ('hummer-jdcloud-plugin', '京东云', 'jdcloud.png', concat(unix_timestamp(now()), '003'), 'custodian', 21, 'cloud');

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('JDCloud 等保预检', '等保合规检查（全称为等级保护合规检查）为您提供了全面覆盖通信网络、区域边界、计算环境和管理中心的网络安全检查。', '等保三级', 'hummer-jdcloud-plugin', 1);
SELECT @groupId3 := LAST_INSERT_ID();

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('91ca3531-1526-4ec2-ad06-ce818d1c0be9', 'JDCloud VM 实例公网 IP 检测', 1, 'HighRisk', 'JDCloud VM 实例未直接绑定公网IP，视为“合规”，否则属于“不合规”。该规则仅适用于 IPv4 协议', 'policies:\n    # VM 实例未直接绑定公网IP，视为“合规”。该规则仅适用于 IPv4 协议。\n    - name: jdcloud-cm-public-ipaddress\n      resource: jdcloud.vm\n      filters:\n        - type: public-ip-address', '[]', 'hummer-jdcloud-plugin', '京东云', 'jdcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian', NULL);

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('91ca3531-1526-4ec2-ad06-ce818d1c0be9', @groupId3);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('91ca3531-1526-4ec2-ad06-ce818d1c0be9', 'safety');

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('8a970feb-85de-462c-9aae-dce16698fb79', '91ca3531-1526-4ec2-ad06-ce818d1c0be9', 'jdcloud.vm');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('91ca3531-1526-4ec2-ad06-ce818d1c0be9', '18');


UPDATE `rule` SET `name` = 'Qiniu Kodo 公开读取访问权限检测',  `script` = 'policies:\n    - name: qiniu-kodo-private\n      resource: qiniu.kodo\n      filters:\n        - type: private\n          value: ${{value}}'  WHERE `id` = '1268870b-188d-41a8-a86e-0c83299dc93c';

DELETE FROM `rule_type` WHERE ID = 'c0cd80bf-259f-4a46-a1a8-9ce7edc5e592';

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('25a9ac83-a4c0-4524-b692-06b7d63b3516', '1268870b-188d-41a8-a86e-0c83299dc93c', 'qiniu.kodo');