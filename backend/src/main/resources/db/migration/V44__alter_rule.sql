
ALTER TABLE `rule_group` ADD `type` varchar(64) DEFAULT 'cloud' COMMENT '规则组类别';

INSERT INTO `plugin` ( `id`, `name`, `icon`, `update_time`, `scan_type`, `order_`, `type`) values ('hummer-server-plugin', '主机检测', 'server.png', concat(unix_timestamp(now()), '001'), 'server', 23, 'server');

UPDATE `rule_group` SET `type` = 'cloud';
