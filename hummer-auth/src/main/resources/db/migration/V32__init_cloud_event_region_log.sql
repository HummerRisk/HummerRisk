CREATE TABLE `cloud_event_region_log` (
      `id` int    NOT NULL AUTO_INCREMENT COMMENT 'id',
      `log_id` int    DEFAULT NULL COMMENT '同步日志ID',
      `region` varchar(512) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '区域',
      `data_count` int    DEFAULT NULL COMMENT '同步数据量',
      `status` int    DEFAULT NULL COMMENT '状态：0 同步中，1成功，2失败',
      `start_time` datetime DEFAULT NULL COMMENT '日志实际开始时间',
      `end_time` datetime DEFAULT NULL COMMENT '日志实际结束时间',
      `create_time` datetime DEFAULT NULL COMMENT '创建时间',
      `exception` varchar(1024) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '异常信息',
      PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

ALTER TABLE `cloud_event_sync_log` MODIFY column `region` varchar(512) DEFAULT NULL COMMENT '区域';

ALTER TABLE `cloud_event_sync_log` MODIFY column `create_time` bigint     DEFAULT NULL COMMENT '创建时间';

ALTER TABLE `cloud_event` MODIFY column `event_time` bigint     DEFAULT NULL COMMENT '事件时间';

ALTER TABLE `cloud_event_sync_log` MODIFY column `status` int    DEFAULT NULL COMMENT '状态：0 同步中，1成功，2失败，3告警';

ALTER TABLE `server` MODIFY column `is_public_key` varchar(32) DEFAULT NULL COMMENT '密钥类型';

ALTER TABLE `server` ADD `auth_type` varchar(32) DEFAULT NULL COMMENT 'ssh类型';

ALTER TABLE `server` ADD `public_key_path` varchar(128) DEFAULT NULL COMMENT '密钥路径';

INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('11c940a7-24e0-4f9d-a9a9-9e6811b12ad6', 'Linux 检查是否安装了主机入侵检测软件', 1, 'MediumRisk', '检查是否安装了主机入侵检测软件', 'find / -iname ${value} -print', '[{\"defaultValue\":\"aide\",\"name\":\"主机入侵检测软件\",\"key\":\"value\",\"required\":true}]', concat(unix_timestamp(now()), '005'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('26cf695f-fca6-4097-bf02-f9cb30f79c0d', 'Linux 查看开机自启项', 1, 'LowRisk', '查看开机自启项', 'systemctl list-unit-files', '[]', concat(unix_timestamp(now()), '005'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('87ecea8f-3ba1-48d0-8fcd-b3d74e1dae60', 'Linux 查看是否对资源进行了限定', 1, 'MediumRisk', '查看是否对资源进行了限定', 'more /etc/security/limits.conf', '[]', concat(unix_timestamp(now()), '005'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('691efd98-419d-4643-a001-ed0faaa3defa', 'Linux 核查入侵和病毒行规则', 1, 'MediumRisk', '核查入侵和病毒行规则', 'crontab -l', '[]', concat(unix_timestamp(now()), '005'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('562fd271-6484-42db-b72b-08d679cb4e03', 'Linux 查看入侵检测的措施', 1, 'MediumRisk', '查看入侵检测的措施', 'more /var/log/secure | grep refused', '[]', concat(unix_timestamp(now()), '005'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('1feaf989-836f-4f90-975d-8caac4b18692', 'Linux 查看审计规则', 1, 'LowRisk', '查看服务器审计规则', 'more /etc/audit/audit.rules', '[]', concat(unix_timestamp(now()), '005'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('161b15b6-b5e3-420d-b0fc-de172553df74', 'Linux 查看audit运行状态', 1, 'MediumRisk', '查看audit运行状态', 'auditctl -s', '[]', concat(unix_timestamp(now()), '005'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('bb389762-8756-405d-b37c-fc789d1cc9cc', 'Linex 查看audit规则', 1, 'LowRisk', '查看服务器审计规则', 'auditctl -l', '[]', concat(unix_timestamp(now()), '005'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('f45cc8e2-867f-4e97-8998-ce7f0c070d2d', 'Linux 检查日志运行', 1, 'MediumRisk', '查看日志是否运行', 'systemctl status rsyslog', '[]', concat(unix_timestamp(now()), '005'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('18365786-1f24-4a42-b5a5-291b9274205a', 'Linux 查看是否开启审计日志', 1, 'MediumRisk', '查看 Linux 是否开启审计日志服务', 'service auditd status', '[]', concat(unix_timestamp(now()), '005'), 1);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('11c940a7-24e0-4f9d-a9a9-9e6811b12ad6', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('161b15b6-b5e3-420d-b0fc-de172553df74', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('18365786-1f24-4a42-b5a5-291b9274205a', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('1feaf989-836f-4f90-975d-8caac4b18692', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('26cf695f-fca6-4097-bf02-f9cb30f79c0d', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('562fd271-6484-42db-b72b-08d679cb4e03', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('691efd98-419d-4643-a001-ed0faaa3defa', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('87ecea8f-3ba1-48d0-8fcd-b3d74e1dae60', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('bb389762-8756-405d-b37c-fc789d1cc9cc', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('f45cc8e2-867f-4e97-8998-ce7f0c070d2d', 'safety');
