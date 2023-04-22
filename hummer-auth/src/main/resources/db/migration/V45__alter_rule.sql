
ALTER TABLE `cloud_native_result` ADD `scan_groups` mediumtext DEFAULT NULL COMMENT '检测分组："vuln", "config", "kubench"';

ALTER TABLE `server_result` modify COLUMN `is_severity` varchar(64) DEFAULT 'false' COMMENT '是否有安全风险: true, false, warn';

ALTER TABLE `history_server_result` modify COLUMN `is_severity` varchar(64) DEFAULT 'false' COMMENT '是否有安全风险: true, false, warn';

ALTER TABLE `history_cloud_native_result` ADD `return_config_sum` bigint DEFAULT 0 COMMENT '输出检测结果配置审计数';

ALTER TABLE `history_cloud_native_result` ADD `scan_groups` mediumtext DEFAULT NULL COMMENT '检测分组："vuln", "config", "kubench"';

ALTER TABLE `cloud_native_result` ADD `rule_groups` mediumtext DEFAULT NULL COMMENT '检测规则组id集合';

ALTER TABLE `history_cloud_native_result` ADD `rule_groups` mediumtext DEFAULT NULL COMMENT '检测规则组id集合';

ALTER TABLE `cloud_native_result` ADD `cloud_resources_sum` bigint DEFAULT 0 COMMENT 'K8s 资源总量';

ALTER TABLE `cloud_native_result` ADD `cloud_return_sum` bigint DEFAULT 0 COMMENT '输出检测结果资源数';

ALTER TABLE `cloud_native_result` ADD `k8s_scan_status` varchar(45) DEFAULT NULL COMMENT 'K8sScan("vuln", "config", "kubench")检测状态';

ALTER TABLE `cloud_native_result` ADD `k8s_rule_status` varchar(45) DEFAULT NULL COMMENT 'K8sRuleScan 检测状态';

ALTER TABLE `history_cloud_native_result` ADD `cloud_resources_sum` bigint DEFAULT 0 COMMENT 'K8s 资源总量';

ALTER TABLE `history_cloud_native_result` ADD `cloud_return_sum` bigint DEFAULT 0 COMMENT '输出检测结果资源数';

ALTER TABLE `history_cloud_native_result` ADD `k8s_scan_status` varchar(45) DEFAULT NULL COMMENT 'K8sScan("vuln", "config", "kubench")检测状态';

ALTER TABLE `history_cloud_native_result` ADD `k8s_rule_status` varchar(45) DEFAULT NULL COMMENT 'K8sRuleScan 检测状态';

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('0e94c51b-eec8-4995-8f2e-aeff41c6047c', 'Aliyun Redis 实例白名单配置检测', 1, 'HighRisk', 'Aliyun 账号下 Redis 实例白名单不存在高危 IP，符合视为“合规”，否则视为“不合规”', 'policies:\n    #Aliyun 账号下 Redis 实例白名单是否存在高危 IP，符合视为“合规”，否则视为“不合规”\n    - name: aliyun-redis-security-ip\n      resource: aliyun.redis\n      filters:\n        - type: security-ip\n          value: \"${{value}}\"', '[{\"defaultValue\":\"0.0.0.0/0\",\"name\":\"高危IP，多个 IP 用“;”分隔\",\"key\":\"value\",\"required\":true}]', 'hummer-aliyun-plugin', '阿里云', 'aliyun.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('1246c507-716e-4700-a4b4-9b441ed543fe', 'Aliyun Redis 实例高危命令检测', 1, 'HighRisk', 'Aliyun 账号下 Redis 实例禁用高危命令，符合视为“合规”，否则视为“不合规”', 'policies:\n    #Aliyun 账号下 Redis 实例禁用高危命令，符合视为“合规”，否则视为“不合规”\n    - name: aliyun-redis-high-risk-command\n      resource: aliyun.redis\n      filters:\n        - type: high-risk-command\n          value: \"${{value}}\"', '[{\"key\":\"value\",\"name\":\"高危命令，多个命令用“,”分隔\",\"defaultValue\":\"flushdb, flushall, config,keys\",\"required\":true}]', 'hummer-aliyun-plugin', '阿里云', 'aliyun.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('5b843546-c1a5-44b0-aa4f-f38590a34e73', 'Aliyun Redis 实例 TDE 加密检测', 1, 'HighRisk', 'Aliyun 账号下 Redis 实例开启 TDE 加密，符合视为“合规”，否则视为“不合规”', 'policies:\n    #Aliyun 账号下 Redis 实例开启 TDE 加密，符合视为“合规”，否则视为“不合规”\n    - name: aliyun-redis-is-support-tde\n      resource: aliyun.redis\n      filters:\n        - type: is-support-tde\n          value: \"${{value}}\"', '[{\"defaultValue\":\"true\",\"name\":\"是否开启 TDE 加密\",\"key\":\"value\",\"required\":true}]', 'hummer-aliyun-plugin', '阿里云', 'aliyun.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('1246c507-716e-4700-a4b4-9b441ed543fe', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('5b843546-c1a5-44b0-aa4f-f38590a34e73', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('0e94c51b-eec8-4995-8f2e-aeff41c6047c', 'safety');

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('26928cd6-a567-4f32-9928-b78b77eb1497', '1246c507-716e-4700-a4b4-9b441ed543fe', 'aliyun.redis');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('28854127-9e2c-48c3-b529-bbd0d6634075', '5b843546-c1a5-44b0-aa4f-f38590a34e73', 'aliyun.redis');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('4e67e963-3256-4f30-82b1-489e47b0951e', '0e94c51b-eec8-4995-8f2e-aeff41c6047c', 'aliyun.redis');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1246c507-716e-4700-a4b4-9b441ed543fe', '45');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5b843546-c1a5-44b0-aa4f-f38590a34e73', '94');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5b843546-c1a5-44b0-aa4f-f38590a34e73', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0e94c51b-eec8-4995-8f2e-aeff41c6047c', '37');

SELECT id INTO @groupId1 FROM rule_group WHERE name = 'Aliyun 等保预检';
SELECT id INTO @groupId2 FROM rule_group WHERE name = 'Aliyun Redis 最佳安全实践';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1246c507-716e-4700-a4b4-9b441ed543fe', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1246c507-716e-4700-a4b4-9b441ed543fe', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5b843546-c1a5-44b0-aa4f-f38590a34e73', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5b843546-c1a5-44b0-aa4f-f38590a34e73', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0e94c51b-eec8-4995-8f2e-aeff41c6047c', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0e94c51b-eec8-4995-8f2e-aeff41c6047c', @groupId2);

alter table `cloud_event` modify column `resource_name` varchar(128) DEFAULT NULL  COMMENT '资源名称';

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('3002de7d-a68b-45af-933b-3d37d63e3c57', 'Aliyun ECS 访问白名单检测', 1, 'HighRisk', 'Aliyun ECS 对指定端口访问设置白名单，符合视为“合规”，否则视为“不合规”。', 'policies:\n    #Aliyun ECS 对指定端口访问设置白名单，符合视为“合规”，否则视为“不合规”。\n    - name: aliyun-ecs-white-list\n      resource: aliyun.ecs\n      filters:\n        - type: white-list\n          ip: \"${{ip}}\"\n          port: \"${{port}}\"', '[{\"defaultValue\":\" 0.0.0.0/0\",\"name\":\"白名单 IP 地址，多个 IP 用“,”号分隔\",\"key\":\"ip\",\"required\":true},{\"defaultValue\":\"22,3389\",\"name\":\"端口号，多个端口用“,”号分隔\",\"key\":\"port\",\"required\":true}]', 'hummer-aliyun-plugin', '阿里云', 'aliyun.png', concat(unix_timestamp(now()), '005'), 1, 'custodian', NULL);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('3002de7d-a68b-45af-933b-3d37d63e3c57', 'safety');

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('c04eed8c-e2f2-4c3d-8370-4814f8c42fb3', '3002de7d-a68b-45af-933b-3d37d63e3c57', 'aliyun.ecs');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('3002de7d-a68b-45af-933b-3d37d63e3c57', '13');

SELECT id INTO @groupId3 FROM rule_group WHERE name = 'Aliyun Ecs 最佳安全实践';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('3002de7d-a68b-45af-933b-3d37d63e3c57', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('3002de7d-a68b-45af-933b-3d37d63e3c57', @groupId3);