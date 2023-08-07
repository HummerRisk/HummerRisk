-- ------------
-- 云事件表
-- ------------
CREATE TABLE IF NOT EXISTS  `cloud_event` (
    `event_id` varchar(64)  DEFAULT NULL COMMENT '事件ID',
    `cloud_account_id` varchar(64) DEFAULT NULL COMMENT '云账号ID',
    `sync_region` varchar(255)  DEFAULT NULL COMMENT '同步区域',
    `acs_region` varchar(32)  DEFAULT NULL COMMENT '事件区域',
    `event_name` varchar(64)  DEFAULT NULL COMMENT '事件名称',
    `event_type` varchar(64)  DEFAULT NULL COMMENT '事件类型',
    `event_category` varchar(64)  DEFAULT NULL COMMENT '事件类别',
    `event_version` varchar(32)  DEFAULT NULL COMMENT '事件版本',
    `event_rw` varchar(8)  DEFAULT NULL COMMENT '读写类型',
    `event_message` varchar(255)  DEFAULT NULL COMMENT '事件消息',
    `event_source` varchar(255)  DEFAULT NULL COMMENT '事件来源',
    `event_time` datetime DEFAULT NULL COMMENT '事件时间',
    `source_ip_address` varchar(16)  DEFAULT NULL COMMENT '访问源地址',
    `user_agent` varchar(255)  DEFAULT NULL COMMENT '用户代理',
    `user_identity` varchar(255)  DEFAULT NULL COMMENT '用户认证',
    `service_name` varchar(64)  DEFAULT NULL COMMENT '服务名',
    `additional_event_data` varchar(1024)  DEFAULT NULL COMMENT '额外数据',
    `request_id` varchar(255)  DEFAULT NULL COMMENT '请求ID',
    `request_parameters` varchar(1024)  DEFAULT NULL COMMENT '请求参数',
    `resource_type` varchar(64)  DEFAULT NULL COMMENT '资源类型',
    `resource_name` varchar(64)  DEFAULT NULL COMMENT '资源名称',
    `referenced_resources` varchar(1024)  DEFAULT NULL COMMENT '映射的资源',
    `api_version` varchar(16)  DEFAULT NULL COMMENT 'API版本',
    `response_elements` varchar(1024)  DEFAULT NULL COMMENT '响应值',
    `user_name` varchar(255)  DEFAULT NULL COMMENT '用户名',
    KEY `index_cloud_event_account_id` (`cloud_account_id`),
    KEY `index_cloud_event_event_time` (`event_time`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- --------------
-- 云事件同步日志
-- --------------
CREATE TABLE IF NOT EXISTS `cloud_event_sync_log` (
    `id` int    NOT NULL AUTO_INCREMENT COMMENT 'id',
    `account_id` varchar(64)  DEFAULT NULL COMMENT '云账号ID',
    `region` varchar(32)  DEFAULT NULL COMMENT '区域',
    `data_count` int    DEFAULT NULL COMMENT '同步数据量',
    `status` int    DEFAULT NULL COMMENT '状态：0 同步中，1成功，2失败',
    `start_time` datetime DEFAULT NULL COMMENT '日志实际开始时间',
    `end_time` datetime DEFAULT NULL COMMENT '日志实际结束时间',
    `request_start_time` datetime DEFAULT NULL COMMENT '请求开始时间',
    `request_end_time` datetime DEFAULT NULL COMMENT '请求结束时间',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------
-- qingcloud规则
-- ----------------

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('Qingcloud 等保预检', '等保合规检查（全称为等级保护合规检查）为您提供了全面覆盖通信网络、区域边界、计算环境和管理中心的网络安全检查。', '等保三级', 'hummer-qingcloud-plugin', '1');
SELECT @groupId1 := LAST_INSERT_ID();

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('13b6dfef-b003-4a9f-9eee-6eb30f78cd21', 'Qingcloud MySQL 实例网络类型检测', 1, 'HighRisk', 'Qingcloud 账号下 MySQL 实例已关联到 VPC；若您配置阈值，则关联的VpcId需存在您列出的阈值中，视为“合规”，否则视为“不合规”', 'policies:\n    # 账号下RDS实例已关联到VPC；若您配置阈值，则关联的VpcId需存在您列出的阈值中，视为“合规”。\n    - name: qingcloud-mysql-instance-network-type\n      resource: qingcloud.mysql\n      filters:\n        - type: network-type', '[]', 'hummer-qingcloud-plugin', '青云', 'qingcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('4223f09d-b455-4b35-9e9a-07953df5f290', 'Qingcloud ECS VPC 检测', 1, 'HighRisk', 'Qingcloud 账号下的 Ecs 实例指定属于哪些 VPC, 属于则合规，不属于则“不合规“', 'policies:\n  # 检测您账号下的Ecs实例指定属于哪些VPC, 属于则合规，不属于则“不合规“。\n  - name: qingcloud-ecs-vpc-type\n    resource: qingcloud.ecs\n    filters:\n      - type: vpc-type\n        vpcIds: [${{value1}}, ${{value2}}]', '[{\"key\":\"value1\",\"name\":\"vpcId1\",\"defaultValue\":\"\\\"vpc-1\\\"\",\"required\":true},{\"key\":\"value2\",\"name\":\"vpcId2\",\"defaultValue\":\"\\\"vpc-2\\\"\",\"required\":true}]', 'hummer-qingcloud-plugin', '青云', 'qingcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('4e60b929-2158-4459-abc8-81afb99eead9', 'Qingcloud MySQL 实例公网访问检测', 1, 'HighRisk', 'Qingcloud 检测您账号下 MySQL 实例不允许任意来源公网访问，视为“合规”，否则视为“不合规”', 'policies:\n    # 检测您账号下RDS实例不允许任意来源公网访问，视为“合规”，否则视为“不合规”。\n    - name: qingcloud-mysql-internet-access\n      resource: qingcloud.mysql\n      filters:\n        - type: internet-access\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"启用公网访问\",\"defaultValue\":\"true\",\"required\":true}]', 'hummer-qingcloud-plugin', '青云', 'qingcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('5afd9902-e319-4282-b9e7-1748ecf47c7e', 'Qingcloud MySQL 实例高可用状态检测', 1, 'HighRisk', 'Qingcloud 账号下 MySQL 实例具备高可用能力，视为“合规”，否则属于“不合规”', 'policies:\n    # 账号下MySQL实例具备高可用能力，视为“合规”，否则属于“不合规”。\n    - name: qingcloud-mysql-high-availability\n      resource: qingcloud.mysql\n      filters:\n        - type: high-availability', '[]', 'hummer-qingcloud-plugin', '青云', 'qingcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('7b8a7f85-3b68-4f94-bd1f-30ece84e1e90', 'Qingcloud ECS 实例公网 IP 检测', 1, 'HighRisk', 'Qingcloud ECS 实例未直接绑定公网IP，视为“合规”，否则属于“不合规”。该规则仅适用于 IPv4 协议', 'policies:\n    # ECS实例未直接绑定公网IP，视为“合规”。该规则仅适用于 IPv4 协议。\n    - name: qingcloud-ecs-public-ipaddress\n      resource: qingcloud.ecs\n      filters:\n        - type: public-ip-address', '[]', 'hummer-qingcloud-plugin', '青云', 'qingcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('c8977309-8b5b-48db-bf16-3380832d4ebb', 'Qingcloud EIP 带宽峰值检测', 1, 'HighRisk', 'Qingcloud 检测您账号下的弹性 IP 实例是否达到最低带宽要求，符合视为“合规”，否则视为“不合规”', 'policies:\n    # 检测您账号下的弹性IP实例是否达到最低带宽要求。\n    - name: qingcloud-eip-bandwidth\n      resource: qingcloud.eip\n      filters:\n        - type: bandwidth\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"按带宽计费的公网型实例的带宽峰值\",\"defaultValue\":\"10\",\"required\":true}]', 'hummer-qingcloud-plugin', '青云', 'qingcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('ceecc38d-5c98-4cc4-aeef-028ab653c26b', 'Qingcloud MongoDB 实例公网访问检测', 1, 'HighRisk', 'Qingcloud 账号下 MongoDB 实例不允许任意来源公网访问，视为“合规”，否则视为“不合规”', 'policies:\n    # 账号下MongoDB实例不允许任意来源公网访问，视为“合规”。\n    - name: qingcloud-mongodb-internet-access\n      resource: qingcloud.mongodb\n      filters:\n        - type: internet-access\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"公网访问\",\"defaultValue\":\"true\",\"required\":true}]', 'hummer-qingcloud-plugin', '青云', 'qingcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('d83c8434-e0ca-4250-b7c0-e5add655262d', 'Qingcloud ECS 实例网络类型检测', 1, 'HighRisk', 'Qingcloud 账号下所有 ECS 实例已关联到 VPC；若您配置阈值，则关联的VpcId需存在您列出的阈值中，视为“合规”，否则视为“不合规”', 'policies:\n    # 账号下所有ECS实例已关联到VPC；若您配置阈值，则关联的VpcId需存在您列出的阈值中，视为“合规”。\n    - name: qingcloud-ecs-instance-network-type\n      resource: qingcloud.ecs\n      filters:\n        - type: instance-network-type', '[]', 'hummer-qingcloud-plugin', '青云', 'qingcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('e8df5a37-a78d-4115-9db2-145d3ebda280', 'Qingcloud MongoDB 实例网络类型检测', 1, 'HighRisk', 'Qingcloud 检测您账号下的 MongoDB 实例是否运行在 VPC 网络环境下，符合视为“合规”，否则视为“不合规”', 'policies:\n    # 检测您账号下的MongoDB实例是否运行在VPC网络环境下。\n    - name: qingcloud-mongodb-network-type\n      resource: qingcloud.mongodb\n      filters:\n        - type: network-type', '[]', 'hummer-qingcloud-plugin', '青云', 'qingcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian');

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d83c8434-e0ca-4250-b7c0-e5add655262d', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7b8a7f85-3b68-4f94-bd1f-30ece84e1e90', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('4223f09d-b455-4b35-9e9a-07953df5f290', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c8977309-8b5b-48db-bf16-3380832d4ebb', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e8df5a37-a78d-4115-9db2-145d3ebda280', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ceecc38d-5c98-4cc4-aeef-028ab653c26b', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('4e60b929-2158-4459-abc8-81afb99eead9', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5afd9902-e319-4282-b9e7-1748ecf47c7e', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('13b6dfef-b003-4a9f-9eee-6eb30f78cd21', @groupId1);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('d83c8434-e0ca-4250-b7c0-e5add655262d', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('7b8a7f85-3b68-4f94-bd1f-30ece84e1e90', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('4223f09d-b455-4b35-9e9a-07953df5f290', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('c8977309-8b5b-48db-bf16-3380832d4ebb', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('e8df5a37-a78d-4115-9db2-145d3ebda280', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('ceecc38d-5c98-4cc4-aeef-028ab653c26b', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('4e60b929-2158-4459-abc8-81afb99eead9', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('5afd9902-e319-4282-b9e7-1748ecf47c7e', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('13b6dfef-b003-4a9f-9eee-6eb30f78cd21', 'safety');

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('3a0284ec-5c6f-4f87-8a05-15dda9035c1f', '5afd9902-e319-4282-b9e7-1748ecf47c7e', 'qingcloud.mysql');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('6c9b2cba-975e-4c94-b609-e83a8800b6d7', 'e8df5a37-a78d-4115-9db2-145d3ebda280', 'qingcloud.mongodb');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('6dbb3a3c-f1c3-47e7-b3ec-70087086da8f', '7b8a7f85-3b68-4f94-bd1f-30ece84e1e90', 'qingcloud.ecs');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('843f1780-edc0-44d7-8ef3-9d1272f4aeca', '4e60b929-2158-4459-abc8-81afb99eead9', 'qingcloud.mysql');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('8f0a0f87-a65c-4b78-bb4e-15f1375c6f77', '4223f09d-b455-4b35-9e9a-07953df5f290', 'qingcloud.ecs');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('ca72f2cd-8f2d-4c9e-948b-44070e414caa', '13b6dfef-b003-4a9f-9eee-6eb30f78cd21', 'qingcloud.mysql');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('e53d45b8-1201-4f1a-af4f-23398cc01624', 'ceecc38d-5c98-4cc4-aeef-028ab653c26b', 'qingcloud.mongodb');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('f19ef138-e405-4a89-b889-1db38cce5050', 'd83c8434-e0ca-4250-b7c0-e5add655262d', 'qingcloud.ecs');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('f53e1976-91b3-4b67-b67e-076032bbb739', 'c8977309-8b5b-48db-bf16-3380832d4ebb', 'qingcloud.eip');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d83c8434-e0ca-4250-b7c0-e5add655262d', '3');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d83c8434-e0ca-4250-b7c0-e5add655262d', '5');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d83c8434-e0ca-4250-b7c0-e5add655262d', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d83c8434-e0ca-4250-b7c0-e5add655262d', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d83c8434-e0ca-4250-b7c0-e5add655262d', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('7b8a7f85-3b68-4f94-bd1f-30ece84e1e90', '18');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4223f09d-b455-4b35-9e9a-07953df5f290', '10');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4223f09d-b455-4b35-9e9a-07953df5f290', '13');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4223f09d-b455-4b35-9e9a-07953df5f290', '92');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4223f09d-b455-4b35-9e9a-07953df5f290', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4223f09d-b455-4b35-9e9a-07953df5f290', '95');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c8977309-8b5b-48db-bf16-3380832d4ebb', '2');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e8df5a37-a78d-4115-9db2-145d3ebda280', '3');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e8df5a37-a78d-4115-9db2-145d3ebda280', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e8df5a37-a78d-4115-9db2-145d3ebda280', '5');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e8df5a37-a78d-4115-9db2-145d3ebda280', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e8df5a37-a78d-4115-9db2-145d3ebda280', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ceecc38d-5c98-4cc4-aeef-028ab653c26b', '10');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ceecc38d-5c98-4cc4-aeef-028ab653c26b', '13');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4e60b929-2158-4459-abc8-81afb99eead9', '97');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5afd9902-e319-4282-b9e7-1748ecf47c7e', '2');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5afd9902-e319-4282-b9e7-1748ecf47c7e', '56');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5afd9902-e319-4282-b9e7-1748ecf47c7e', '57');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5afd9902-e319-4282-b9e7-1748ecf47c7e', '58');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('13b6dfef-b003-4a9f-9eee-6eb30f78cd21', '3');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('13b6dfef-b003-4a9f-9eee-6eb30f78cd21', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('13b6dfef-b003-4a9f-9eee-6eb30f78cd21', '5');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('13b6dfef-b003-4a9f-9eee-6eb30f78cd21', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('13b6dfef-b003-4a9f-9eee-6eb30f78cd21', '7');

-- --------------
-- ucloud规则
-- --------------
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('UCloud 等保预检', '等保合规检查（全称为等级保护合规检查）为您提供了全面覆盖通信网络、区域边界、计算环境和管理中心的网络安全检查。', '等保三级', 'hummer-ucloud-plugin', '1');
SELECT @groupId2 := LAST_INSERT_ID();

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('001400f0-0a52-43c5-945e-d9e28c6a74f9', 'UCloud uhost 实例公网IP检测', 1, 'HighRisk', 'UCloud uhost 实例未直接绑定公网 IP，视为“合规”，否则属于“不合规”。该规则仅适用于 IPv4 协议', 'policies:\n    # UCloud uhost 实例未直接绑定公网 IP，视为“合规”，否则属于“不合规”。该规则仅适用于 IPv4 协议。\n    - name: ucloud-uhost-public-ipaddress\n      resource: ucloud.uhost\n      filters:\n        - type: public-ip-address', '[]', 'hummer-ucloud-plugin', 'UCloud 优刻得', 'ucloud.png', concat(unix_timestamp(now()), '004'), 1, 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('155f44ef-b0c6-4881-b2c2-ac1daffa04da', 'UCloud uhost VPC 检测', 1, 'HighRisk', 'UCloud 账号下的 uhost 实例指定属于哪些 VPC, 属于则合规，不属于则“不合规“', 'policies:\n  # UCloud 账号下的 uhost 实例指定属于哪些 VPC, 属于则合规，不属于则“不合规“。\n  - name: ucloud-uhost-vpc-type\n    resource: ucloud.uhost\n    filters:\n      - type: vpc-type\n        vpcIds: [${{value1}}, ${{value2}}]', '[{\"key\":\"value1\",\"name\":\"vpcId1\",\"defaultValue\":\"\\\"vpc-1\\\"\",\"required\":true},{\"key\":\"value2\",\"name\":\"vpcId2\",\"defaultValue\":\"\\\"vpc-2\\\"\",\"required\":true}]', 'hummer-ucloud-plugin', 'UCloud 优刻得', 'ucloud.png', concat(unix_timestamp(now()), '004'), 1, 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('32d7aec8-265e-4ae8-916c-85bd2798b568', 'UCloud uhost 实例网络类型检测', 1, 'HighRisk', 'UCloud 账号下所有 uhost 实例已关联到 VPC；若您配置阈值，则关联的 VpcId 需存在您列出的阈值中，视为“合规”，否则视为“不合规”', 'policies:\n    # UCloud 账号下所有 uhost 实例已关联到 VPC；若您配置阈值，则关联的 VpcId 需存在您列出的阈值中，视为“合规”，否则视为“不合规”。\n    - name: ucloud-uhost-instance-network-type\n      resource: ucloud.uhost\n      filters:\n        - type: instance-network-type', '[]', 'hummer-ucloud-plugin', 'UCloud 优刻得', 'ucloud.png', concat(unix_timestamp(now()), '004'), 1, 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('5fb3ca1d-2a8d-46c7-8549-403139a10fbd', 'UCloud EIP 带宽峰值检测', 1, 'HighRisk', 'UCloud 检测您账号下的弹性IP实例是否达到最低带宽要求，符合视为“合规”，否则视为“不合规”', 'policies:\n    # UCloud 检测您账号下的弹性IP实例是否达到最低带宽要求，符合视为“合规”，否则视为“不合规”。\n    - name: ucloud-eip-bandwidth\n      resource: ucloud.eip\n      filters:\n        - type: bandwidth\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"按带宽计费的公网型实例的带宽峰值\",\"defaultValue\":\"10\",\"required\":true}]', 'hummer-ucloud-plugin', 'UCloud 优刻得', 'ucloud.png', concat(unix_timestamp(now()), '004'), 1, 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('844fd144-0e31-419f-adf7-d6f89673faaa', 'UCloud uhost 挂载磁盘加密状态检测', 1, 'HighRisk', 'UCloud 账号下 uhost 挂载所有的磁盘均已加密；若您配置阈值，则磁盘加密的Id需存在您列出的阈值中，符合视为“合规”，否则视为“不合规”', 'policies:\n    # UCloud 账号下 uhost 挂载所有的磁盘均已加密；若您配置阈值，则磁盘加密的Id需存在您列出的阈值中，符合视为“合规”，否则视为“不合规”。\n    - name: ucloud-encrypted-disk\n      resource: ucloud.uhost\n      filters:\n        - type: disk-encrypted\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"磁盘是否加密( true/false )\",\"defaultValue\":\"true\",\"required\":true}]', 'hummer-ucloud-plugin', 'UCloud 优刻得', 'ucloud.png', concat(unix_timestamp(now()), '004'), 1, 'custodian');

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('32d7aec8-265e-4ae8-916c-85bd2798b568', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('001400f0-0a52-43c5-945e-d9e28c6a74f9', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('155f44ef-b0c6-4881-b2c2-ac1daffa04da', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('844fd144-0e31-419f-adf7-d6f89673faaa', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5fb3ca1d-2a8d-46c7-8549-403139a10fbd', @groupId2);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('32d7aec8-265e-4ae8-916c-85bd2798b568', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('001400f0-0a52-43c5-945e-d9e28c6a74f9', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('155f44ef-b0c6-4881-b2c2-ac1daffa04da', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('844fd144-0e31-419f-adf7-d6f89673faaa', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('5fb3ca1d-2a8d-46c7-8549-403139a10fbd', 'safety');

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('4c4cdd74-0377-4b77-b00a-f41c1bc9f546', '5fb3ca1d-2a8d-46c7-8549-403139a10fbd', 'ucloud.eip');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('89390548-5541-4164-bcde-f7178945d5e8', '844fd144-0e31-419f-adf7-d6f89673faaa', 'ucloud.uhost');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('ac01e108-a282-4dcc-a538-a35f9d70a405', '32d7aec8-265e-4ae8-916c-85bd2798b568', 'ucloud.uhost');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('c3006f09-dd1c-4510-9c15-90ebe4cdd8e1', '001400f0-0a52-43c5-945e-d9e28c6a74f9', 'ucloud.uhost');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('f21c2ce6-8420-429a-872f-0bdeaf252dfc', '155f44ef-b0c6-4881-b2c2-ac1daffa04da', 'ucloud.uhost');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('32d7aec8-265e-4ae8-916c-85bd2798b568', '3');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('32d7aec8-265e-4ae8-916c-85bd2798b568', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('32d7aec8-265e-4ae8-916c-85bd2798b568', '5');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('32d7aec8-265e-4ae8-916c-85bd2798b568', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('32d7aec8-265e-4ae8-916c-85bd2798b568', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('32d7aec8-265e-4ae8-916c-85bd2798b568', '91');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('001400f0-0a52-43c5-945e-d9e28c6a74f9', '18');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('155f44ef-b0c6-4881-b2c2-ac1daffa04da', '10');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('155f44ef-b0c6-4881-b2c2-ac1daffa04da', '13');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('155f44ef-b0c6-4881-b2c2-ac1daffa04da', '92');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('155f44ef-b0c6-4881-b2c2-ac1daffa04da', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('155f44ef-b0c6-4881-b2c2-ac1daffa04da', '95');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('844fd144-0e31-419f-adf7-d6f89673faaa', '53');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('844fd144-0e31-419f-adf7-d6f89673faaa', '94');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5fb3ca1d-2a8d-46c7-8549-403139a10fbd', '2');

ALTER TABLE cloud_event_sync_log ADD column exception varchar(1024) DEFAULT NULL COMMENT '异常信息';

DELETE FROM plugin WHERE id = 'hummer-tsunami-plugin';

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


-- ----------------------------
-- Table cloud_event
-- ----------------------------

ALTER TABLE `cloud_event_sync_log` MODIFY column `create_time` bigint     DEFAULT NULL COMMENT '创建时间';

ALTER TABLE `cloud_event_sync_log` ADD `proxy_id` int  DEFAULT NULL COMMENT '代理ID';

ALTER TABLE `cloud_event_sync_log` ADD `region_name` varchar(1024) DEFAULT NULL COMMENT '区域名称';

ALTER TABLE `cloud_event_region_log` ADD `region_name` varchar(64) DEFAULT NULL COMMENT '区域名称';

ALTER TABLE `cloud_event` MODIFY column `event_time` bigint     DEFAULT NULL COMMENT '事件时间';

ALTER TABLE `cloud_event` MODIFY column `source_ip_address` varchar(64) DEFAULT NULL COMMENT '访问源地址';

ALTER TABLE `cloud_event` ADD `event_rating` int    DEFAULT NULL COMMENT '事件级别 0 正常 1 警告 2 事故';

ALTER TABLE `cloud_event` ADD `resource_id` varchar(64) DEFAULT NULL COMMENT '资源id';

ALTER TABLE `cloud_event` ADD `location_info` varchar(255) DEFAULT NULL COMMENT '记录本次请求出错后，问题定位所需要的辅助信息';

ALTER TABLE `cloud_event` ADD `endpoint` varchar(64) DEFAULT NULL COMMENT '云资源的详情页面';

ALTER TABLE `cloud_event` ADD `resource_url` varchar(255) DEFAULT NULL COMMENT '云资源的详情页面的访问链接';

ALTER TABLE `cloud_event` ADD `cloud_audit_event` text DEFAULT NULL COMMENT '日志详情';

ALTER TABLE `cloud_event` ADD `region_name` varchar(255) DEFAULT NULL COMMENT '区域名称';

-- ----------------------------
-- Table rule
-- ----------------------------

ALTER TABLE `rule` ADD `suggestion` varchar(255) DEFAULT NULL COMMENT '优化建议';

ALTER TABLE `cloud_task` ADD `suggestion` varchar(255) DEFAULT NULL COMMENT '优化建议';

-- ----------------------------
-- UCloud Rule
-- ----------------------------

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('351684f0-1464-428e-a66d-4dbcfb1839b0', 'UCloud SecurityGroup 安全组配置检测', 1, 'HighRisk', 'UCloud 账号下安全组配置不为“0.0.0.0/0”，符合视为“合规”，否则属于“不合规”', 'policies:\n    # UCloud 账号下安全组配置不为“0.0.0.0/0”，符合视为“合规”，否则属于“不合规”。\n    - name: ucloud-sg-source-cidr-ip\n      resource: ucloud.securitygroup\n      filters:\n        - type: source-cidr-ip\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"目标IP地址段\",\"defaultValue\":\"\\\"0.0.0.0/0\\\"\",\"required\":true}]', 'hummer-ucloud-plugin', 'UCloud 优刻得', 'ucloud.png', concat(unix_timestamp(now()), '001'), '1', 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('3b61a7cf-e3b4-4120-86c1-ffe92c3a6d66', 'UCloud SecurityGroup 安全组端口访问检测', 1, 'HighRisk', 'UCloud 账号下安全组配置允许所有端口访问视为”不合规“，否则为”合规“', 'policies:\n  # UCloud 账号下安全组配置允许所有端口访问视为”不合规“，否则为”合规“。\n  - name: ucloud-sg-ports\n    resource: ucloud.securitygroup\n    filters:\n      - type: source-ports\n        SourceCidrIp: ${{SourceCidrIp}}\n        PortRange: ${{PortRange}}', '[{\"key\":\"SourceCidrIp\",\"name\":\"目标IP地址段\",\"defaultValue\":\"\\\"0.0.0.0/0\\\"\",\"required\":true},{\"key\":\"PortRange\",\"name\":\"端口号\",\"defaultValue\":\"”-1/-1“\",\"required\":true}]', 'hummer-ucloud-plugin', 'UCloud 优刻得', 'ucloud.png', concat(unix_timestamp(now()), '001'), '1', 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('6008cc3c-0adb-48ba-86b8-7ad2fc8efdca', 'UCloud ULB 负载均衡 HTTPS 监听检测', 1, 'HighRisk', 'UCloud ULB 负载均衡开启 HTTPS 监听，符合视为“合规”，否则属于“不合规”', 'policies:\n    # UCloud ULB 负载均衡开启 HTTPS 监听，符合视为“合规”，否则属于“不合规”。\n    - name: ucloud-ulb-listener\n      resource: ucloud.ulb\n      filters:\n        - type: listener\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"负载均衡实例前端使用的协议\",\"defaultValue\":\"HTTPS\",\"required\":true}]', 'hummer-ucloud-plugin', 'UCloud 优刻得', 'ucloud.png', concat(unix_timestamp(now()), '001'), '1', 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('6cbe7b32-d431-4fcb-9c47-4ba1411cb52c', 'UCloud SecurityGroup 高危安全组检测', 1, 'HighRisk', 'UCloud 检测安全组是否开启风险端口，未开启视为“合规”，否则属于“不合规”', 'policies:\n  # UCloud 检测安全组是否开启风险端口，未开启视为“合规”，否则属于“不合规”：\n  #(20,21,22,25,80,773,765,1733,1737,3306,3389,7333,5732,5500)\n  - name: ucloud-security-group\n    resource: ucloud.securitygroup\n    description: |\n      Add Filter all security groups, filter ports\n      [20,21,22,25,80,773,765,1733,1737,3306,3389,7333,5732,5500]\n      on 0.0.0.0/0 or\n      [20,21,22,25,80,773,765, 1733,1737,3306,3389,7333,5732,5500]\n      on ::/0 (IPv6)\n    filters:\n        - or:\n            - type: ingress\n              IpProtocol: \"-1\"\n              Ports: ${{ipv4_port}}\n              Cidr: \"0.0.0.0/0\"\n            - type: ingress\n              IpProtocol: \"-1\"\n              Ports: ${{ipv6_port}}\n              CidrV6: \"::/0\"', '[{\"key\":\"ipv4_port\",\"name\":\"ipv4端口\",\"defaultValue\":\"[20,21,22,25,80,773,765, 1733,1737,3306,3389,7333,5732,5500]\",\"required\":true},{\"key\":\"ipv6_port\",\"name\":\"ipv6端口\",\"defaultValue\":\"[20,21,22,25,80,773,765, 1733,1737,3306,3389,7333,5732,5500]\",\"required\":true}]', 'hummer-ucloud-plugin', 'UCloud 优刻得', 'ucloud.png', concat(unix_timestamp(now()), '001'), '1', 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('91bdd835-1071-4d6a-825a-4afdf58dce71', 'UCloud ULB VPC 检测', 1, 'HighRisk', 'UCloud 账号下 ULB 负载均衡实例指定属于哪些 VPC, 属于则合规，不属于则“不合规“', 'policies:\n  # UCloud 账号下 ULB 负载均衡实例指定属于哪些 VPC, 属于则合规，不属于则“不合规“。\n  - name: ucloud-ulb-vpc-type\n    resource: ucloud.ulb\n    filters:\n      - type: vpc-type\n        vpcIds: [${{value1}}, ${{value2}}]', '[{\"key\":\"value1\",\"name\":\"vpcId1\",\"defaultValue\":\"\\\"vpc-1\\\"\",\"required\":true},{\"key\":\"value2\",\"name\":\"vpcId2\",\"defaultValue\":\"\\\"vpc-2\\\"\",\"required\":true}]', 'hummer-ucloud-plugin', 'UCloud 优刻得', 'ucloud.png', concat(unix_timestamp(now()), '001'), '1', 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('b285f285-10cc-45cc-b59e-7b84fd229815', 'UCloud ULB 负载均衡监听检测', 1, 'HighRisk', 'UCloud 账号下的 ULB 负载均衡是否开启 HTTPS 或 HTTP 监听，开启视为“合规“，否则“不合规“', 'policies:\n  # UCloud 账号下的 ULB 负载均衡是否开启 HTTPS 或 HTTP 监听，开启视为“合规“，否则“不合规“。\n  - name: ucloud-ulb-listener-type\n    resource: ucloud.ulb\n    filters:\n      - type: listener-type\n        value: [${{value1}}, ${{value2}}]', '[{\"key\":\"value1\",\"name\":\"监听类型1\",\"defaultValue\":\"HTTPS\",\"required\":true},{\"key\":\"value2\",\"name\":\"监听类型2\",\"defaultValue\":\"HTTP\",\"required\":true}]', 'hummer-ucloud-plugin', 'UCloud 优刻得', 'ucloud.png', concat(unix_timestamp(now()), '001'), '1', 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('b85e5722-fd4a-471e-af94-b9d38cc23b22', 'UCloud ULB 带宽峰值检测', 1, 'HighRisk', 'UCloud 检测您账号下的负载均衡实例是否达到最低带宽要求，符合视为“合规”，，否则视为“不合规”', 'policies:\n    # UCloud 检测您账号下的负载均衡实例是否达到最低带宽要求，符合视为“合规”，，否则视为“不合规”。\n    - name: ucloud-ulb-bandwidth\n      resource: ucloud.ulb\n      filters:\n        - type: bandwidth\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"按带宽计费的公网型实例的带宽峰值\",\"defaultValue\":\"10\",\"required\":true}]', 'hummer-ucloud-plugin', 'UCloud 优刻得', 'ucloud.png', concat(unix_timestamp(now()), '001'), '1', 'custodian', NULL);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('351684f0-1464-428e-a66d-4dbcfb1839b0', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('3b61a7cf-e3b4-4120-86c1-ffe92c3a6d66', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('6008cc3c-0adb-48ba-86b8-7ad2fc8efdca', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('6cbe7b32-d431-4fcb-9c47-4ba1411cb52c', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('91bdd835-1071-4d6a-825a-4afdf58dce71', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('b285f285-10cc-45cc-b59e-7b84fd229815', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('b85e5722-fd4a-471e-af94-b9d38cc23b22', 'safety');

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('19e41041-8fad-45e7-97f3-afc4fa3b139c', 'b285f285-10cc-45cc-b59e-7b84fd229815', 'ucloud.ulb');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('20b1daa2-a11a-4dfc-83df-4eaa94d6242c', '6008cc3c-0adb-48ba-86b8-7ad2fc8efdca', 'ucloud.ulb');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('496dfb44-898c-401e-8dcc-3263e8e357f9', '351684f0-1464-428e-a66d-4dbcfb1839b0', 'ucloud.securitygroup');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('9331a5e0-177f-4432-ab82-c29b87702fea', '91bdd835-1071-4d6a-825a-4afdf58dce71', 'ucloud.ulb');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('a06fc813-f812-4f6b-88f9-a8572b01edcd', '3b61a7cf-e3b4-4120-86c1-ffe92c3a6d66', 'ucloud.securitygroup');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('ebc4b746-a4ef-409d-b7a9-ad0174106f19', 'b85e5722-fd4a-471e-af94-b9d38cc23b22', 'ucloud.ulb');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('efb63d3d-ea2e-4c5e-9d23-8851e350eef5', '6cbe7b32-d431-4fcb-9c47-4ba1411cb52c', 'ucloud.securitygroup');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('351684f0-1464-428e-a66d-4dbcfb1839b0', '13');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('3b61a7cf-e3b4-4120-86c1-ffe92c3a6d66', '10');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('3b61a7cf-e3b4-4120-86c1-ffe92c3a6d66', '92');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('3b61a7cf-e3b4-4120-86c1-ffe92c3a6d66', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('3b61a7cf-e3b4-4120-86c1-ffe92c3a6d66', '95');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('6008cc3c-0adb-48ba-86b8-7ad2fc8efdca', '52');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('6cbe7b32-d431-4fcb-9c47-4ba1411cb52c', '9');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('6cbe7b32-d431-4fcb-9c47-4ba1411cb52c', '46');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('6cbe7b32-d431-4fcb-9c47-4ba1411cb52c', '92');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('6cbe7b32-d431-4fcb-9c47-4ba1411cb52c', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('6cbe7b32-d431-4fcb-9c47-4ba1411cb52c', '95');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('91bdd835-1071-4d6a-825a-4afdf58dce71', '10');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('91bdd835-1071-4d6a-825a-4afdf58dce71', '13');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('91bdd835-1071-4d6a-825a-4afdf58dce71', '92');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('91bdd835-1071-4d6a-825a-4afdf58dce71', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('91bdd835-1071-4d6a-825a-4afdf58dce71', '95');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b285f285-10cc-45cc-b59e-7b84fd229815', '10');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b285f285-10cc-45cc-b59e-7b84fd229815', '13');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b285f285-10cc-45cc-b59e-7b84fd229815', '92');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b285f285-10cc-45cc-b59e-7b84fd229815', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b285f285-10cc-45cc-b59e-7b84fd229815', '95');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b85e5722-fd4a-471e-af94-b9d38cc23b22', '2');

SELECT id INTO @groupId1 FROM rule_group WHERE name = 'UCloud 等保预检';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('351684f0-1464-428e-a66d-4dbcfb1839b0', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('3b61a7cf-e3b4-4120-86c1-ffe92c3a6d66', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('6008cc3c-0adb-48ba-86b8-7ad2fc8efdca', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('6cbe7b32-d431-4fcb-9c47-4ba1411cb52c', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('91bdd835-1071-4d6a-825a-4afdf58dce71', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b285f285-10cc-45cc-b59e-7b84fd229815', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b85e5722-fd4a-471e-af94-b9d38cc23b22', @groupId1);

-- ----------------------------
-- Table group
-- ----------------------------

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('Aliyun RAM 最佳安全实践', '阿里云 RAM 最佳安全实践，RAM 为所有阿里云服务提供简单一致的集中式访问控制能力，管理所有阿里云资源。RAM 检测为您提供全方位的最佳安全实践功能。', '最佳实践', 'hummer-aliyun-plugin', 1);
SELECT @groupId := LAST_INSERT_ID();
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('70c1e701-b87a-4e4d-8648-3db7ecc2c066', @groupId);

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('Aliyun OSS 最佳安全实践', 'Aliyun OSS 最佳安全实践，对象存储 OSS 主要围绕数据迁移、数据备份和容灾、数据直传OSS、数据处理与分析、管理 OSS等操作。OSS 检测提供全方位的最佳实践功能，帮助您更加高效地使用OSS，满足您的业务需求。', '最佳实践', 'hummer-aliyun-plugin', 1);
SELECT @groupId2 := LAST_INSERT_ID();
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('429f8396-e04b-49d9-8b38-80647ac87e66', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8c635fda-7f89-4d5c-b0f4-2116f1b65554', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('9d94781e-922d-48c3-90a1-393dc79f2442', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d826c85d-cb42-4824-ab13-6d7a8026d9ae', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('fdef013f-ce14-468a-9af4-1c0fabc7e6e1', @groupId2);

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('Aliyun NAS 最佳安全实践', 'Aliyun NAS 最佳安全实践，文件存储 NAS 是一个可大规模共享访问，弹性扩展的高性能云原生分布式文件系统。NAS 检测为您提供全方位的最佳安全实践功能。', '最佳实践', 'hummer-aliyun-plugin', 1);

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('Aliyun SLB 最佳安全实践', 'Aliyun SLB 最佳安全实践，负载均衡 SLB 是将访问流量根据转发策略分发到后端多台云服务器的流量分发控制服务。SLB 检测为您提供全方位的最佳安全实践功能。', '最佳实践', 'hummer-aliyun-plugin', 1);
SELECT @groupId3 := LAST_INSERT_ID();
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1299a29b-e19d-4186-93fd-a18ed1b2584a', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2274a926-ea5e-4cdc-915e-09fa6d803bff', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('339cf3fc-f9d9-457e-ac72-40d37c402bdf', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('594e7673-c0db-40a4-9a0c-f70f0e58cc62', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('88a77028-0e2a-4201-a713-ded3a94864f9', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e28786b3-f2b1-46de-b4b6-7835b42ae58b', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e2d51fc6-2ec2-4d17-bf87-13a3df90ea5d', @groupId3);

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('Aliyun PostgreSQL 最佳安全实践', 'Aliyun PostgreSQL 最佳安全实践，PostgreSQL 被业界誉为“最先进的开源数据库”，主要面向企业复杂查询SQL的OLTP业务场景， 支持NoSQL数据类型。PostgreSQL 检测为您提供全方位的最佳安全实践功能。', '最佳实践', 'hummer-aliyun-plugin', 1);

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('Aliyun RDS 最佳安全实践', 'Aliyun RDS 最佳安全实践，RDS 是一种稳定可靠、可弹性伸缩的在线数据库服务。基于分布式文件系统和 SSD 盘高性能存储，提供容灾、备份、恢复、监控、迁移等解决方案。RDS 检测为您提供全方位的最佳安全实践功能。', '最佳实践', 'hummer-aliyun-plugin', 1);
SELECT @groupId4 := LAST_INSERT_ID();
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('028b8362-08f2-404c-8e15-935426bb8545', @groupId4);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ae65e90c-124c-4a81-8081-746d47f44e8f', @groupId4);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('beda16d0-93fd-4366-9ebf-f5ce1360cd60', @groupId4);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c95fde94-53a5-4658-98a4-56a0c6d951d4', @groupId4);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d690be79-2e8c-4054-bbe6-496bd29e91fe', @groupId4);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e054787c-5826-4242-8450-b0daa926ea40', @groupId4);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2adbae64-6403-4dfb-92ab-637354da49f8', @groupId4);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c57f055e-fd84-4af3-ba97-892a8fdc1fed', @groupId4);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e054787c-5826-4242-8450-b0daa926ea40', @groupId4);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('29763f5e-ef4c-431d-b44f-39cd1b5b5363', @groupId4);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ff153eea-2628-440b-b054-186d6f5a7708', @groupId4);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('44343a84-39e2-4fbc-b8c5-d3ac06186501', @groupId4);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7d323895-f07b-4845-8b3d-01c78180f270', @groupId4);

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('Aliyun ACK 最佳安全实践', 'Aliyun ACK 最佳安全实践，容器服务 Kubernetes 版（简称 ACK）提供高性能可伸缩的容器应用管理能力，支持企业级容器化应用的全生命周期管理。ACK 检测为您提供全方位的最佳安全实践功能。', '最佳实践', 'hummer-aliyun-plugin', 1);

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('Aliyun Redis 最佳安全实践', 'Aliyun Redis 最佳安全实践，数据库 Redis 提供内存加硬盘混合存储的数据库服务，基于高可靠双机热备架构及可平滑扩展的集群架构，可充分满足高吞吐、低延迟及弹性变配的业务需求。Redis 检测为您提供全方位的最佳安全实践功能。', '最佳实践', 'hummer-aliyun-plugin', 1);
SELECT @groupId5 := LAST_INSERT_ID();
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('29763f5e-ef4c-431d-b44f-39cd1b5b5363', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ff153eea-2628-440b-b054-186d6f5a7708', @groupId5);

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('Aliyun PolarDB 最佳安全实践', 'Aliyun PolarDB 最佳安全实践，PolarDB 是阿里自研的新一代云原生关系型数据库，在存储计算分离架构下，利用了软硬件结合的优势，为用户提供具备极致弹性、高性能、海量存储、安全可靠的数据库服务。PolarDB 检测为您提供全方位的最佳安全实践功能。', '最佳实践', 'hummer-aliyun-plugin', 1);
SELECT @groupId6 := LAST_INSERT_ID();
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2adbae64-6403-4dfb-92ab-637354da49f8', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('4f8fa101-171a-4491-9485-e5aa091a88a4', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c57f055e-fd84-4af3-ba97-892a8fdc1fed', @groupId6);

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('Aliyun MSE 最佳安全实践', 'Aliyun MSE 最佳安全实践，微服务引擎 MSE（Microservices Engine）是一个面向业界主流开源微服务框架Spring Cloud和Dubbo一站式微服务平台，提供治理中心、托管的注册中心和托管的配置中心。MSE 检测为您提供全方位的最佳安全实践功能。', '最佳实践', 'hummer-aliyun-plugin', 1);

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('Aliyun Event 最佳安全实践', 'Aliyun Event 最佳安全实践，操作审计帮助您监控并记录阿里云账号的活动，包括通过阿里云控制台、OpenAPI、开发者工具对云上产品和服务的访问和使用行为。您可以进行行为分析、安全分析、资源变更行为追踪和行为合规性审计等操作。操作审计检测为您提供全方位的最佳安全实践功能。', '最佳实践', 'hummer-aliyun-plugin', 1);

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('Aliyun ECS 最佳安全实践', 'Aliyun ECS 最佳安全实践，云服务器 ECS（Elastic Compute Service）是一种安全可靠、弹性可伸缩的云计算服务。ECS 检测为您提供全方位的最佳安全实践功能。', '最佳实践', 'hummer-aliyun-plugin', 1);
SELECT @groupId7 := LAST_INSERT_ID();
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('3e5d47ac-86b6-40d1-a191-1b2ff2496118', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('6fd132c0-b4df-4685-b132-5441d1aef2f8', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f4ebb59b-c93a-4f34-9e66-660b03943d7d', @groupId7);

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('Aliyun MongoDB 最佳安全实践', 'Aliyun MongoDB 最佳安全实践，云数据库 MongoDB 版（ApsaraDB for MongoDB）基于飞天分布式系统和高可靠存储引擎，提供多节点高可用架构、弹性扩容、容灾、备份恢复、性能优化等功能。MongoDB 检测为您提供全方位的最佳安全实践功能。', '最佳实践', 'hummer-aliyun-plugin', 1);
SELECT @groupId8 := LAST_INSERT_ID();
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('44343a84-39e2-4fbc-b8c5-d3ac06186501', @groupId8);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7d323895-f07b-4845-8b3d-01c78180f270', @groupId8);


CREATE TABLE IF NOT EXISTS `cloud_resource_sync` (
    `id`                            varchar(50)           NOT NULL COMMENT '任务ID',
    `status`                        varchar(20)           DEFAULT NULL COMMENT '状态',
    `apply_user`                    varchar(50)           DEFAULT NULL COMMENT '申请人',
    `create_time`                   bigint                DEFAULT NULL COMMENT '创建时间',
    `account_id`                    varchar(50)           DEFAULT NULL COMMENT '云账号ID',
    `plugin_id`                     varchar(40)           DEFAULT NULL COMMENT '插件ID',
    `plugin_name`                   varchar(128)          DEFAULT NULL COMMENT '云平台名称',
    `plugin_icon`                   varchar(128)          DEFAULT NULL COMMENT '云平台图标',
    `resource_types`                varchar(256)          DEFAULT NULL COMMENT '资源类型',
    `resources_sum`                 bigint                DEFAULT 0 COMMENT '资源总量',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `cloud_resource_sync_item` (
    `id`                           varchar(50)         NOT NULL,
    `sync_id`                      varchar(50)         DEFAULT NULL COMMENT '任务ID',
    `details`                      longtext            DEFAULT NULL COMMENT 'policy内容',
    `status`                       varchar(20)         DEFAULT NULL COMMENT '状态',
    `count`                        int              DEFAULT '1'  COMMENT '数量',
    `resource_type`                varchar(128)        DEFAULT NULL COMMENT '资源类型',
    `region_id`                    varchar(128)        DEFAULT NULL COMMENT '区域标识',
    `region_name`                  varchar(128)        DEFAULT NULL COMMENT '区域名称',
    `account_id`                   varchar(50)         DEFAULT NULL COMMENT '云账号ID',
    `account_url`                  varchar(128)        DEFAULT NULL COMMENT '云账号图标',
    `account_label`                varchar(128)        DEFAULT NULL COMMENT '云账号名称',
    `create_time`                  bigint              DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `cloud_resource_sync_item_log` (
                                                              `id`                           int                 NOT NULL AUTO_INCREMENT,
                                                              `sync_item_id`                 varchar(50)         DEFAULT NULL COMMENT '任务项ID',
    `account_id`                   varchar(50)         DEFAULT NULL COMMENT '云账号ID',
    `create_time`                  bigint              DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    mediumtext          DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint             DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `cloud_resource` (
    `id`                         varchar(50)         NOT NULL,
    `resource_status`            varchar(45)         DEFAULT NULL COMMENT '资源状态',
    `resource_type`              varchar(64)         DEFAULT NULL COMMENT '资源类型',
    `custodian_run_log`          longtext            DEFAULT NULL COMMENT 'custodian-run.log',
    `metadata`                   longtext            DEFAULT NULL COMMENT 'metadata.json',
    `resources`                  longtext            DEFAULT NULL COMMENT 'resources.json',
    `resources_sum`              bigint              DEFAULT 0 COMMENT '资源总量',
    `plugin_id`                  varchar(40)         DEFAULT NULL COMMENT '插件名称',
    `plugin_name`                varchar(40)         DEFAULT NULL COMMENT '云平台名称',
    `plugin_icon`                varchar(128)        DEFAULT NULL COMMENT '云平台图标',
    `account_id`                 varchar(50)         DEFAULT NULL COMMENT '云账号ID',
    `region_id`                  varchar(128)        DEFAULT NULL COMMENT '区域标识',
    `region_name`                varchar(128)        DEFAULT NULL COMMENT '区域名称',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint              DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `cloud_resource_item` (
    `id`                         varchar(50)         NOT NULL,
    `resource_id`                varchar(50)         DEFAULT NULL COMMENT 'resource主键ID',
    `hummer_id`                  varchar(256)        DEFAULT NULL COMMENT '资源ID（唯一标识）',
    `resource_type`              varchar(64)         DEFAULT NULL COMMENT '资源类型',
    `plugin_id`                  varchar(40)         DEFAULT NULL COMMENT '插件名称',
    `plugin_name`                varchar(40)         DEFAULT NULL COMMENT '云平台名称',
    `plugin_icon`                varchar(128)        DEFAULT NULL COMMENT '云平台图标',
    `account_id`                 varchar(50)         DEFAULT NULL COMMENT '云账号ID',
    `region_id`                  varchar(128)        DEFAULT NULL COMMENT '区域标识',
    `region_name`                varchar(128)        DEFAULT NULL COMMENT '区域名称',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint              DEFAULT NULL COMMENT '更新时间',
    `resource`                   longtext            DEFAULT NULL COMMENT '资源JSON',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `cloud_resource_amount_history` (
    `id`                            varchar(50)           NOT NULL COMMENT 'ID',
    `sync_id`                       varchar(50)           DEFAULT NULL COMMENT '任务ID',
    `sync_time`                     bigint                DEFAULT NULL COMMENT '同步时间',
    `create_time`                   bigint                DEFAULT NULL COMMENT '创建时间',
    `account_id`                    varchar(50)           DEFAULT NULL COMMENT '云账号ID',
    `plugin_id`                     varchar(40)           DEFAULT NULL COMMENT '插件ID',
    `plugin_name`                   varchar(128)          DEFAULT NULL COMMENT '云平台名称',
    `plugin_icon`                   varchar(128)          DEFAULT NULL COMMENT '云平台图标',
    `resource_type`                 varchar(64)           DEFAULT NULL COMMENT '资源类型',
    `region_id`                     varchar(128)          DEFAULT NULL COMMENT '区域标识',
    `region_name`                   varchar(128)          DEFAULT NULL COMMENT '区域名称',
    `count`                         int                DEFAULT 0 COMMENT '资源同步量',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

UPDATE rule_type SET resource_type = 'huawei.obs' WHERE id = '3e50ec86-1a2a-486e-920b-39572eb8d193';

ALTER TABLE `cloud_event` ADD `id` varchar(64) NOT NULL COMMENT 'ID';

ALTER TABLE `cloud_event` ADD PRIMARY KEY ( `id` );

DELETE FROM `cloud_account` WHERE id = '6953e0fd-57f3-4ca8-915f-e8b97725fcd7';

-- -----------------
-- Aliyun
-- -----------------

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('0fab953a-c392-493d-9ef8-238cf5651d40', 'Aliyun MSE VPC 检测', 1, 'HighRisk', 'Aliyun 账号下的 MSE 实例指定属于哪些 VPC, 符合视为“合规”，否则视为“不合规”', 'policies:\n  # 检测您账号下的Mse实例指定属于哪些VPC, 属于则合规，不属于则\"不合规\"。\n  - name: aliyun-mse-vpc-type\n    resource: aliyun.mse\n    filters:\n      - type: vpc-type\n        vpcIds: [${{value1}}, ${{value2}}]', '[{\"defaultValue\":\"\\\"vpcId1\\\"\",\"name\":\"vpcId1\",\"key\":\"value1\",\"required\":true},{\"defaultValue\":\"\\\"vpcId2\\\"\",\"name\":\"vpcId2\",\"key\":\"value2\",\"required\":true}]', 'hummer-aliyun-plugin', '阿里云', 'aliyun.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('8810fc51-d234-4454-836a-95a9b1dec196', 'Aliyun ACK 删除保护检测', 1, 'HighRisk', 'Aliyun ACK 集群开启删除保护，符合视为“合规”，否则视为“不合规”', 'policies:\n    # ACK 集群开启删除保护，符合视为“合规”，否则视为“不合规”。\n    - name: aliyun-ack-deletion-protection\n      resource: aliyun.ack\n      filters:\n        - type: deletion-protection\n          value: ${{value}}', '[{\"defaultValue\":\"true\",\"name\":\"是否开启删除保护\",\"key\":\"value\",\"required\":true}]', 'hummer-aliyun-plugin', '阿里云', 'aliyun.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('b9ff94b8-8959-4eac-86e9-983d8e6d7db6', 'Aliyun NAS 带宽峰值检测', 1, 'HighRisk', 'Aliyun 检测您账号下的 NAS 是否达到最低带宽要求，符合视为“合规”，否则视为“不合规”', 'policies:\n    # 检测您账号下的 NAS 是否达到最低带宽要求，符合视为“合规”，，否则视为“不合规”\n    - name: aliyun-nas-bandwidth\n      resource: aliyun.nas\n      filters:\n        - type: bandwidth\n          value: ${{value}}', '[{\"defaultValue\":\"100\",\"name\":\"带宽\",\"key\":\"value\",\"required\":true}]', 'hummer-aliyun-plugin', '阿里云', 'aliyun.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('deede37b-2991-40b3-b8b5-089914e4cd43', 'Aliyun NAS 磁盘加密状态检测', 1, 'HighRisk', 'Aliyun 账号下 NAS 磁盘均已加密，符合视为“合规”，否则视为“不合规”', 'policies:\n    # 账号下 NAS 磁盘均已加密，符合视为“合规”，否则视为“不合规”\n    - name: aliyun-nas-encrypted\n      resource: aliyun.nas\n      filters:\n        - type: encrypted\n          value: ${{value}}', '[{\"defaultValue\":\"0\",\"name\":\"加密状态 0:未加密 1:NAS托管密钥 2:用户管理密钥\",\"key\":\"value\",\"required\":true}]', 'hummer-aliyun-plugin', '阿里云', 'aliyun.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('d76abc8d-9975-4752-8e60-709c811d44cf', 'Aliyun Event 事件跟踪检测', 1, 'HighRisk', 'Aliyun  Event 已开启事件跟踪，符合视为“合规”，否则视为“不合规”', 'policies:\n    # Event 已开启事件跟踪，符合视为“合规”，否则视为“不合规”\n    - name: aliyun-event-trail\n      resource: aliyun.event\n      filters:\n        - type: trail-status\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"跟踪状态\",\"defaultValue\":\"Enable\",\"required\":true}]', 'hummer-aliyun-plugin', '阿里云', 'aliyun.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('0fab953a-c392-493d-9ef8-238cf5651d40', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('8810fc51-d234-4454-836a-95a9b1dec196', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('b9ff94b8-8959-4eac-86e9-983d8e6d7db6', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('deede37b-2991-40b3-b8b5-089914e4cd43', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('d76abc8d-9975-4752-8e60-709c811d44cf', 'safety');

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('139c93ee-c821-44d6-89fe-e41e558bee41', '8810fc51-d234-4454-836a-95a9b1dec196', 'aliyun.ack');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('143615d5-a9ca-4df3-b253-90a9f94b01f2', 'b9ff94b8-8959-4eac-86e9-983d8e6d7db6', 'aliyun.nas');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('293b16e4-a07c-4c18-b758-e6abeaa7b0df', 'deede37b-2991-40b3-b8b5-089914e4cd43', 'aliyun.nas');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('47ac4a8b-a16e-46ef-89e4-f46cfd1979cf', '0fab953a-c392-493d-9ef8-238cf5651d40', 'aliyun.mse');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('7fbde491-f6a2-4ce3-b442-0323c91bc477', 'd76abc8d-9975-4752-8e60-709c811d44cf', 'aliyun.event');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0fab953a-c392-493d-9ef8-238cf5651d40', '10');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0fab953a-c392-493d-9ef8-238cf5651d40', '13');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0fab953a-c392-493d-9ef8-238cf5651d40', '92');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0fab953a-c392-493d-9ef8-238cf5651d40', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0fab953a-c392-493d-9ef8-238cf5651d40', '95');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b9ff94b8-8959-4eac-86e9-983d8e6d7db6', '2');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('deede37b-2991-40b3-b8b5-089914e4cd43', '53');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('deede37b-2991-40b3-b8b5-089914e4cd43', '94');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d76abc8d-9975-4752-8e60-709c811d44cf', '26');

SELECT id INTO @groupId11 from rule_group where name = 'Aliyun 等保预检';
SELECT id INTO @groupId12 from rule_group where name = 'Aliyun MSE 最佳安全实践';
SELECT id INTO @groupId13 from rule_group where name = 'Aliyun ACK 最佳安全实践';
SELECT id INTO @groupId14 from rule_group where name = 'Aliyun NAS 最佳安全实践';
SELECT id INTO @groupId15 from rule_group where name = 'Aliyun Event 最佳安全实践';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0fab953a-c392-493d-9ef8-238cf5651d40', @groupId12);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0fab953a-c392-493d-9ef8-238cf5651d40', @groupId11);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8810fc51-d234-4454-836a-95a9b1dec196', @groupId13);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8810fc51-d234-4454-836a-95a9b1dec196', @groupId11);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b9ff94b8-8959-4eac-86e9-983d8e6d7db6', @groupId14);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b9ff94b8-8959-4eac-86e9-983d8e6d7db6', @groupId11);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('deede37b-2991-40b3-b8b5-089914e4cd43', @groupId14);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('deede37b-2991-40b3-b8b5-089914e4cd43', @groupId11);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d76abc8d-9975-4752-8e60-709c811d44cf', @groupId11);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d76abc8d-9975-4752-8e60-709c811d44cf', @groupId15);

-- -----------------
-- update aws rule suggestion
-- -----------------

UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-ebs-unattached.md' where id = 'a90a1ba1-b392-4bf2-af31-20ecbefe5811';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-ebs-encrypted.md' where id = 'befc89d7-1811-404a-9226-f8ecc22820e0';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-ec2-usage.md' where id = 'fa5e89e3-417d-4296-9d17-ca51ed914be5';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-stopped-instance.md' where id = 'bd1a0479-ef54-4208-a520-50caf6acfe87';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-ec2-tagged.md' where id = 'b57cbdba-b4d7-4da7-84db-25b9f2d5324b';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-ec2-runnningtime.md' where id = '5c47228c-7fe1-484b-a5b6-7c1968074f69';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-eip-unattached.md' where id = '35b5c651-5bd6-44b8-85ee-ae6adfa42dc3';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-elb-sslblacklist.md' where id = '07eb95ef-6d78-4b9a-8555-a42b6a16de99';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-elb-unattached.md' where id = '0be7f5d4-766c-4b2b-910d-bec0f3aac977';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-rds-public-access.md' where id = '0dda84c1-794b-4977-bb66-6f12695c6c51';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-rds-encrypted.md' where id = 'a6c513a8-8e18-4341-94b7-b6588fdcd1f4';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-rds-unused.md' where id = 'b1491f69-f3b8-40ae-9659-4242dbc30a0b';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-s3-global-grants.md' where id = 'c30779c4-44b8-4c7b-b2ec-29ff3a96033b';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-s3-public-access.md' where id = 'd0f3f4b0-000a-4407-85ee-ed4a2f9dac44';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-securitygroup-check.md' where id = '43a1556b-5417-4efb-88fc-33e8eeb68f71';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-iam-cis.md' where id = 'ebc80d1e-5dd9-4b86-8037-fb7c9727084a';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-logging-cis.md' where id = '9929cc2a-7a9b-4643-a813-0e4359b09eb0';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-monitoring-cis.md' where id = '8ad73e84-141a-40c4-bd56-1813535e8e92';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-networking-cis.md' where id = 'ee74d071-d39e-4b0e-b4a3-e7ad24fbce12';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-cis-level1.md' where id = '0dc8ae24-71f8-449a-8834-d59f8fbdf991';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-cis-level2.md' where id = '1fbd8c76-90e7-48e4-8281-ef597bb484e8';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-cis-extras.md' where id = '6bb291bd-fcdf-4aa9-8fdf-69e761c9c4e0';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-forensics.md' where id = '350a097a-dfeb-4fd0-a3af-49049da5c025';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-gdpr.md' where id = 'fa60960a-0e38-4502-9834-1ab1277e9aaa';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-hippa.md' where id = '39c87069-a3c6-4ab3-9974-f844a89872b5';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-secrets-check.md' where id = '10754933-241e-42db-bb3c-42e13cb40b0f';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-api-gateway.md' where id = '7c37887f-0b8d-444c-bad7-e43cd2b41578';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-rds-security.md' where id = '197e41e7-cbc3-4eef-b5ad-5153c9232449';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-elasticseatch.md' where id = '4a64e90e-92ea-4d5a-a753-3c5ca92a2b95';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-pcidss.md' where id = '72af2230-8555-4e0c-a06a-721436a0644e';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-trustboundaries.md' where id = '0a5a7796-6b53-408a-ba49-7fd9e51d82f4';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-internet-exposed.md' where id = 'a944a483-c23f-4d62-bdd8-d2a20a4aaeca';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-iso27001.md' where id = '9b1d1a9c-6c7e-44db-a15c-c0c82b1040eb';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-eks-cis.md' where id = '0d51321e-e26c-4147-b730-5b1403384487';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-ffiec.md' where id = '0d6270fc-3744-42cd-a850-65ac0b8ef514';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-soc2.md' where id = 'c1375dfe-c0b1-4e53-9c2b-6a0f29258a08';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-sagemaker.md' where id = '1ab302fb-1b06-410a-b17a-fdf7843d6182';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-ens.md' where id = 'f38d83c9-c4b9-47b8-8135-f9bdf96cf8ec';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aws/aws-glue.md' where id = 'e86583b9-12dd-4316-980a-5eeeba86f9ca';

-- -----------------
-- update aliyun rule suggestion
-- -----------------
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/ack-deletion-protection.md' where id = '8810fc51-d234-4454-836a-95a9b1dec196';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/cdn-https.md' where id = 'ba1edc8f-0944-4ebb-a953-f655aa710e84';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/disk-encrypt.md' where id = '2533542d-5422-4bd5-8849-6a69ec05a874';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/ecs-vpc.md' where id = 'f4ebb59b-c93a-4f34-9e66-660b03943d7d';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/eip-bandwidth.md' where id = '6fd132c0-b4df-4685-b132-5441d1aef2f8';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/ecs-net-type.md' where id = '3e5d47ac-86b6-40d1-a191-1b2ff2496118';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/eip-bandwidth.md' where id = '0b2ece35-a17e-4584-ac2d-0b11483d04fb';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/event-trail.md' where id = 'd76abc8d-9975-4752-8e60-709c811d44cf';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/mse-vpc.md' where id = '0fab953a-c392-493d-9ef8-238cf5651d40';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/mongodb-public-ip.md' where id = '7d323895-f07b-4845-8b3d-01c78180f270';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/mse-vpc.md' where id = '44343a84-39e2-4fbc-b8c5-d3ac06186501';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/nas-bandwidth.md' where id = 'b9ff94b8-8959-4eac-86e9-983d8e6d7db6';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/nas-encrypted.md' where id = 'deede37b-2991-40b3-b8b5-089914e4cd43';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/oss-public-write.md' where id = 'd826c85d-cb42-4824-ab13-6d7a8026d9ae';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/oss-public-read.md' where id = '429f8396-e04b-49d9-8b38-80647ac87e66';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/oss-encrypt.md' where id = '9d94781e-922d-48c3-90a1-393dc79f2442';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/oss-data-redundancy-type.md' where id = 'fdef013f-ce14-468a-9af4-1c0fabc7e6e1';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/oss-referer.md' where id = '8c635fda-7f89-4d5c-b0f4-2116f1b65554';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/polardb-public-ip.md' where id = '2adbae64-6403-4dfb-92ab-637354da49f8';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/polardb-net-type.md' where id = 'c57f055e-fd84-4af3-ba97-892a8fdc1fed';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/polardb-vpc.md' where id = '4f8fa101-171a-4491-9485-e5aa091a88a4';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/ram.md' where id = '70c1e701-b87a-4e4d-8648-3db7ecc2c066';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/rds-public-ip.md' where id = '028b8362-08f2-404c-8e15-935426bb8545';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/rds-available-zones.md' where id = 'beda16d0-93fd-4366-9ebf-f5ce1360cd60';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/rds-white-list.md' where id = 'd690be79-2e8c-4054-bbe6-496bd29e91fe';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/rds-connection-mode.md' where id = 'c95fde94-53a5-4658-98a4-56a0c6d951d4';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/rds-net-type.md' where id = 'e054787c-5826-4242-8450-b0daa926ea40';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/rds-ha.md' where id = 'ae65e90c-124c-4a81-8081-746d47f44e8f';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/rds-vpc.md' where id = 'f3675431-730d-446f-b062-7301c5c40f23';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/redis-public-ip.md' where id = 'ff153eea-2628-440b-b054-186d6f5a7708';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/redis-net-type.md' where id = '29763f5e-ef4c-431d-b44f-39cd1b5b5363';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/slb-vpc.md' where id = '2274a926-ea5e-4cdc-915e-09fa6d803bff';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/slb-public-ip.md' where id = '88a77028-0e2a-4201-a713-ded3a94864f9';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/slb-bandwidth.md' where id = '594e7673-c0db-40a4-9a0c-f70f0e58cc62';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/slb-https.md' where id = '1299a29b-e19d-4186-93fd-a18ed1b2584a';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/slb-bind-public-ip.md' where id = '339cf3fc-f9d9-457e-ac72-40d37c402bdf';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/slb-bind-vpc.md' where id = 'e2d51fc6-2ec2-4d17-bf87-13a3df90ea5d';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/slb-listener.md' where id = 'e28786b3-f2b1-46de-b4b6-7835b42ae58b';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/security-group-port.md' where id = '4d427c93-5645-445a-93a3-0c536d6865ab';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/security-group-config.md' where id = '083d24e2-881f-488b-b120-8f2cd961707f';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/aliyun/security-group-high-risk-port.md' where id = 'df4fb45c-f9bc-4c8e-996d-036c9d2f1800';

-- -----------------
-- update azure rule suggestion
-- -----------------
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/azure/azure-app-cors.md' where id = '6873838e-3a47-4255-b49e-af5fbf5cb454';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/azure/azure-cosmosdb.md' where id = 'dd11251a-7fd4-499c-857b-c7dcf4c98958';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/azure/azure-unused-disk.md' where id = '94d168d0-8e62-4173-96fd-326eb267273e';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/azure/azure-loadbalancer-publicip.md' where id = '618a61bb-eecc-4109-b387-4a313f10c1a9';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/azure/azure-sqlserver-usage.md' where id = 'c3259122-ac07-4256-83da-b6361c9d3abb';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/azure/azure-sql-database.md' where id = '5870d5df-7674-4f40-ae06-0408c935acd6';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/azure/azure-vm-usage.md' where id = '6967e834-ace6-48d2-9f95-f541b614810d';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/azure/azure-vm-publicip.md' where id = '4852857f-b454-4375-b405-0033fed73f09';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/azure/azure-stopped-vm.md' where id = '802fbf6c-92ac-4153-bea3-42b7720d8124';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/azure/azure-publicip-ddos.md' where id = '29ee6650-3e4f-44e3-aadd-0ae15618afa9';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/azure/azure-storage-public-access.md' where id = '77ac25d5-0a66-4890-aa7e-5b276fce299a';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/azure/azure-storage-allowip.md' where id = 'f7c03491-fc0a-4ae7-87ed-6423748b2372';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/azure/azure-network-securitygroup.md' where id = '3e324555-83e2-4ac9-8ac2-1dae350d52ff';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/azure/azure-network-interface.md' where id = 'f6c72297-33db-49dd-a106-06cf2ff47069';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/azure/azure-rg-empty.md' where id = '6a71a114-ba92-4e0b-bd9d-2f6c8ecee88d';

-- -----------------
-- update baidu rule suggestion
-- -----------------
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/baidu/bbc-public-network-ip.md' where id = 'bb8e4fea-a238-4231-87b8-3aac5c2c2323';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/baidu/bbc-bandwidth.md' where id = 'd3b291c0-e8b5-4560-84b2-4f23d0235401';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/baidu/bcc-vpc.md' where id = '98a56ff2-03fd-49a9-b52c-982ce0f04f70';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/baidu/bcc-public-network-ip-monitoring.md' where id = '2fc93189-2ebe-40c7-b5cb-61d52d9184d8';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/baidu/bcc-network-type.md' where id = '45a0e76a-2f12-4681-b955-4d0eab099dce';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/baidu/cdn-domain-https-monitoring.md' where id = '09b1030c-e094-4d23-a75b-e3d876f55a67';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/baidu/eip-bandwidth.md' where id = '77001a71-f984-4fea-9883-44c566ca13fb';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/baidu/securityGroup-port-access.md' where id = 'fe93c715-8803-4a9b-b2f0-522571b162fb';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/baidu/alb-public-network-ip.md' where id = 'ca3f9cfa-892e-43ce-abfc-4373329926f3';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/baidu/common-public-network-ip.md' where id = '73b0db2f-b946-4d03-a4bd-95ee7d4c9108';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/baidu/disk-encryption.md' where id = '862db318-ce25-41ed-85fa-fc1ea4d691ec';

-- -----------------
-- update gcp rule suggestion
-- -----------------
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/gcp/gcp-dns-managed-zones.md' where id = 'd2288b78-bab7-4195-9c16-79ae29b9f292';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/gcp/gcp-dns-policies-if-logging-disabled.md' where id = '3a1a0fc7-98f3-4f8e-a434-76460ef2009b';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/gcp/gcp-sql-backup-run.md' where id = '34c7e619-6815-4c20-af8e-8d11b16a770c';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/gcp/gcp-appengine-certificate.md' where id = 'd147a425-ddd5-4826-b685-e3b3842ae253';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/gcp/gcp-appengine-blacklist.md' where id = '6939910d-a7f1-4347-9238-abce1111f45d';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/gcp/gcp-instance-template.md' where id = '95e46474-5611-4d82-a9c5-5da3398ad3a4';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/gcp/gcp-load-balancing-ssl-policies.md' where id = '6923bc7a-fb4f-4c1c-ae70-dc6aac407e11';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/gcp/gcp-expired-deployments.md' where id = '1f3ef6ec-17ab-409d-9051-ea5c543312d1';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/gcp/gcp-appengine-firewall-ingress.md' where id = '9312ca46-1ce9-4e42-86b2-7f9b5c0db090';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/gcp/gcp-appengine-firewall-rules.md' where id = '7557ce1b-aacf-4cd7-bb19-6e411621daf3';

-- -----------------
-- update huawei rule suggestion
-- -----------------
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/dds-public-network.md' where id = 'bb878650-b62b-4980-a6aa-4d61c1a429a0';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/disk-encrypted.md' where id = '36908c29-c15e-4c73-a964-5fb97bbb27fa';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/ecs-public-ip.md' where id = '108c875b-bf3c-4034-b07d-15faa8715257';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/ecs-network-type.md' where id = 'f7e8d1c4-16f7-4079-b110-b60db0cd91bf';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/eip-bandwidth.md' where id = '3b3a76cf-78e4-4c48-84da-baf2af6be696';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/elb-bind-ecs.md' where id = '5c681b76-fea3-4cb9-b6d4-a92d0d2d44fa';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/elb-public-ip.md' where id = '7e7b3463-c7da-4545-bbc7-f97c83f429e4';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/elb-https-listener.md' where id = '3c4f6db4-6f8d-4e42-9c7e-d209fe5489fb';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/gauss-db-for-nosql-cost-type.md' where id = '776f0a66-25d4-4aee-bbdc-6f30a2c4652e';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/gauss-db-for-nosql-public-network.md' where id = '8e50d813-0d65-474c-951a-4abacbf381cc';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/gauss-db-for-nosql-disk.md' where id = 'fcd416f0-e58c-417a-8795-a99748d6f4ce';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/gauss-db-for-nosql-network-type.md' where id = '4eed4751-16fc-4827-a3aa-12865b6c5c73';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/gauss-db-for-nosql-ha.md' where id = '77e50110-e477-4b24-a3c2-a9f5e7e41715';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/gauss-db-for-open-gauss-internet-access.md' where id = '8048b411-967a-47b3-b4f1-d7d7880eb79c';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/gauss-db-for-open-gauss-disk.md' where id = '86415c99-3215-4f11-a62b-fa4c7b2ac298';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/gauss-db-for-open-gauss-network-type.md' where id = '1abf0c53-b4ac-4b45-b528-8f47b8d7d533';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/gauss-db-for-open-gauss-cost-type.md' where id = 'f393ba0b-23ca-435b-b922-62c77a9437f7';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/gauss-db-for-open-gauss-ha.md' where id = '86254202-388c-4c55-bba2-a3053c16fe3b';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/gauss-db-internet-access.md' where id = 'cc1f97e7-73c5-4f4f-91e9-1155d47ed24e';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/gauss-db-disk.md' where id = 'c61ae072-e614-47d6-b0ee-07f50360032e';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/gauss-db-network-type.md' where id = 'fa800f05-b7ac-4009-86f9-413d26bbc368';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/gauss-db-cost-type.md' where id = 'e1b193de-9b9f-41de-89d5-62a731d2f212';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/gauss-db-ha.md' where id = 'b4620c46-795f-4d13-9139-2055849ec7b9';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/iam-login.md' where id = 'e58a3395-aee8-40bb-8f2f-d7156c0435cb';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/obs-read.md' where id = '2677aa0f-6796-4138-902b-f736e0c3d8da';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/obs-write.md' where id = '968837c3-25f2-4848-8174-5413c859f1f1';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/rds-public-network.md' where id = 'b1260ecf-47fa-4613-8ed4-71417f29441b';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/rds-disk.md' where id = '33a70278-c772-4ef0-93c9-4b9d3df1e242';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/rds-network-type.md' where id = 'ce805504-de52-4ecc-a86a-4905d2a558f3';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/rds-network-type.md' where id = '332ce586-70de-4053-a922-f76d6340a03c';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/rds-ha.md' where id = 'f744fdd9-166d-40a7-ad91-9e933ad514cc';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/redis-unencrypt.md' where id = '3ce82502-1c03-4ab0-b8ed-83f631a20805';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/redis-internet-access.md' where id = '0cf1e428-3c37-4aa6-b651-acb46c4838c0';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/security-group-config.md' where id = '8f3f7596-569b-4cde-97a2-1d565b8224e1';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/huawei/security-group-high-risk-port.md' where id = '99e762e7-766f-4e55-9133-773593497b44';

-- -----------------
-- update openstack rule suggestion
-- -----------------
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/openstack/volume-encrypt.md' where id = '5673475f-c43d-432c-a6a6-f29479493b4d';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/openstack/volume-status.md' where id = '6bbd42bc-a251-498a-8b19-8de8c55e32aa';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/openstack/server-time.md' where id = '5c4d5e15-ce0a-4322-8406-47e8bcb10bf4';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/openstack/server-tag.md' where id = 'ab3306d0-e734-4977-a462-42de0e1cf263';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/openstack/server-type.md' where id = 'ccf64ae6-7f99-40fe-b45f-d86a4b63bdcc';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/openstack/server-image.md' where id = '6359bdcd-0d1d-4e43-817c-cc338f07a065';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/openstack/flavor-type.md' where id = '311daee9-3c15-4b19-8fb7-97823c57eb23';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/openstack/user-role.md' where id = '5f6c1aed-8c46-48e6-b48d-2cf06568831f';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/openstack/user-project.md' where id = '1aab5692-85a5-4151-b67c-8c2c93353916';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/openstack/network-status.md' where id = 'b8e09c5a-fecc-4400-b831-d7f75f9cee01';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/openstack/router-status.md' where id = '63dedb65-0237-4402-8a5c-1d2137840fdd';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/openstack/image-status.md' where id = 'f7b906de-63e0-479b-b766-2e50df8fbe19';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/openstack/project-member.md' where id = 'bf9a87c9-f709-4f84-8a86-5afde3eb40c3';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/openstack/security-groups-config.md' where id = 'c9162947-90f2-41c9-8df8-2d2244bb6f1d';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/openstack/security-groups-high-risk-port.md' where id = '701ae534-4c13-45a2-8a72-e78afdea4d2b';

-- -----------------
-- update qingcloud rule suggestion
-- -----------------
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/qingcloud/ecs-vpc.md' where id = '4223f09d-b455-4b35-9e9a-07953df5f290';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/qingcloud/ecs-network-type.md' where id = 'd83c8434-e0ca-4250-b7c0-e5add655262d';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/qingcloud/ecs-network-type.md' where id = 'd83c8434-e0ca-4250-b7c0-e5add655262d';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/qingcloud/eip-bandwidth.md' where id = 'c8977309-8b5b-48db-bf16-3380832d4ebb';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/suggest/qingcloud/mongodb-public-network.md' where id = 'ceecc38d-5c98-4cc4-aeef-028ab653c26b';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/qingcloud/mongodb-network-type.md' where id = 'e8df5a37-a78d-4115-9db2-145d3ebda280';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/qingcloud/mysql-public-network-access.md' where id = '4e60b929-2158-4459-abc8-81afb99eead9';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/qingcloud/mysql-network-type.md' where id = '13b6dfef-b003-4a9f-9eee-6eb30f78cd21';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/qingcloud/mysql-ha.md' where id = '5afd9902-e319-4282-b9e7-1748ecf47c7e';

-- -----------------
-- update tencent rule suggestion
-- -----------------
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/tencent/cdb-public-network-access.md' where id = 'cb9320af-4ee6-4c11-8840-8ec4b9cc8276';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/tencent/cdb-multiAZ.md' where id = '68363ad8-cce8-4458-b8f1-9bd1d1af54a7';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/tencent/cdb-network-type.md' where id = '2c4de559-7182-44e7-9657-b97412f0f0a7';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/tencent/cdb-ha-status.md' where id = '4cffcef8-be1e-44e0-8057-892e2cf588d5';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/tencent/clb-public-network-access.md' where id = '525d0b91-ca93-4d3d-8679-195aef136e0b';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/tencent/clb-bandwidth.md' where id = 'a0dca246-563a-49c3-8a43-3c537ba1fb36';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/tencent/clb-associated-with-vpc.md' where id = '59a95d30-30d7-45bf-ac47-80afed002ade';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/tencent/public-write-access-permission.md' where id = 'f8badb51-4a56-48cc-993f-6eb6698ea0ea';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/tencent/public-read-access-permission.md' where id = '77eab07c-1e3c-4713-acf1-ed1bede0531d';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/tencent/cos-bucket-encryption.md' where id = '3e66d418-3e47-475b-a863-03a9653940ed';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/tencent/cos-antileech.md' where id = 'ca991f61-49a4-49b2-9d14-16ca338d8149';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/tencent/cvm-public-network-ip.md' where id = '17dbe1a1-e9e7-4513-aa07-597ae932ff73';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/tencent/cvm-billing.md' where id = '159e19e9-e956-4c52-92fc-c9746372e448';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/tencent/disk-encryption.md' where id = 'bfbf6be1-69f8-448e-9c23-c5267530d573';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/tencent/eip-bandwidth.md' where id = '1240a2b8-f7ef-47c3-8048-2dd95d6d5b54';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/suggest/tencent/mongo-public-network.md' where id = 'c95be861-6e0e-4404-9b21-5ccfcd94ee3f';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/tencent/mongo-instance-network-type.md' where id = 'c1b41cbc-1263-4d67-a7c6-cf064999c6f8';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/tencent/redis-network-type.md' where id = '21fc2518-3b2b-475a-8398-4b1d9f68cfdb';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/tencent/securitygroup-configuration.md' where id = '2631d8a3-e5d8-4ef3-88c9-a8176fae110b';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/tencent/securitygroup-highrisk-configuration.md' where id = 'f9d3db46-d340-4487-ae9b-82b827108912';

-- -----------------
-- update ucloud rule suggestion
-- -----------------
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/ucloud/eip-bandwidth.md' where id = '5fb3ca1d-2a8d-46c7-8549-403139a10fbd';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/ucloud/sg-ports.md' where id = '3b61a7cf-e3b4-4120-86c1-ffe92c3a6d66';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/ucloud/sg-source-cidr-ip.md' where id = '351684f0-1464-428e-a66d-4dbcfb1839b0';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/ucloud/sg-high-risk-port.md' where id = '6cbe7b32-d431-4fcb-9c47-4ba1411cb52c';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/ucloud/ulb-vpc-type.md' where id = '91bdd835-1071-4d6a-825a-4afdf58dce71';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/ucloud/ulb-bandwidth.md' where id = 'b85e5722-fd4a-471e-af94-b9d38cc23b22';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/ucloud/ulb-listener.md' where id = '6008cc3c-0adb-48ba-86b8-7ad2fc8efdca';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/ucloud/ulb-listener-type.md' where id = 'b285f285-10cc-45cc-b59e-7b84fd229815';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/ucloud/uhost-vpc.md' where id = '155f44ef-b0c6-4881-b2c2-ac1daffa04da';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/ucloud/uhost-public-network-ip.md' where id = '001400f0-0a52-43c5-945e-d9e28c6a74f9';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/ucloud/uhost-network-type.md' where id = '32d7aec8-265e-4ae8-916c-85bd2798b568';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/ucloud/uhost-disk-encryption.md' where id = '844fd144-0e31-419f-adf7-d6f89673faaa';

-- -----------------
-- update vmware vsphere rule suggestion
-- -----------------
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/vsphere/cluster.md' where id = 'd004829c-b359-4993-84fd-0441c1ea9c6b';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/vsphere/datacenter.md' where id = '7b494857-7010-446f-9deb-aab10262160e';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/vsphere/datastore-available.md' where id = 'eab382c4-49db-420c-b4e1-987b47bf6661';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/vsphere/datastore.md' where id = 'e9d801ab-3790-415e-9565-bd7208cb1b54';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/vsphere/datastore-config.md' where id = 'faa53ae5-f534-4a3c-85e2-22398dadd468';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/vsphere/host.md' where id = '6503123d-6442-49f4-8118-883c354ac868';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/vsphere/host-connect-status.md' where id = '82d95569-e9be-4a26-a18e-f1590bac97fb';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/vsphere/network-status.md' where id = 'b62df3f7-581b-4591-9373-581dd3dcdd88';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/vsphere/resource-pool.md' where id = '8dccd008-f79b-4460-9b55-539f488e009a';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/vsphere/vm-status.md' where id = 'b798aa33-bd01-47ac-8cec-64f4f5d02866';

-- -----------------
-- update volcengine rule suggestion
-- -----------------
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/volc/cdn-https.md' where id = '0f134720-4de8-4ef0-855a-4f0b2a2017bd';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/volc/ecs-vpc.md' where id = '59898c82-c456-40df-b62d-edf140548735';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/volc/ecs-public-ip.md' where id = '221de04f-0e7c-4bd6-9ef0-2ca34ae80767';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/volc/ecs-net-type.md' where id = 'e0b4465d-ff7f-4bc5-946e-da723a8d0be9';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/volc/eip-bandwidth.md' where id = 'd7de14df-62e4-46b5-8bac-7dec053f84be';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/volc/mongodb-public-ip.md' where id = 'f02172dd-2514-45f5-85e1-c20c71ce19a5';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/volc/mongodb-net-type.md' where id = '6882449f-5140-4aa0-8338-ef1689cafa38';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/volc/securitygroup-port.md' where id = '5d7e3fce-4e13-43b3-8b30-d33528fd1fbd';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/v1.x/v1.x/suggest/volc/securitygroup-high-risk-port.md' where id = '05689a7f-f584-441a-ad6e-006df5d4cca1';

ALTER TABLE `cloud_task_item_log` MODIFY column `operation` mediumtext DEFAULT NULL COMMENT '操作内容';

ALTER TABLE `history_cloud_task_log` MODIFY column `operation` mediumtext DEFAULT NULL COMMENT '操作内容';

ALTER TABLE `cloud_resource_sync_item_log` MODIFY column `operation` mediumtext DEFAULT NULL COMMENT '操作内容';
