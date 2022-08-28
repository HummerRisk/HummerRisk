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
    `id` int(8) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `account_id` varchar(64)  DEFAULT NULL COMMENT '云账号ID',
    `region` varchar(32)  DEFAULT NULL COMMENT '区域',
    `data_count` int(6) DEFAULT NULL COMMENT '同步数据量',
    `status` int(1) DEFAULT NULL COMMENT '状态：0 同步中，1成功，2失败',
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

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('13b6dfef-b003-4a9f-9eee-6eb30f78cd21', 'Qingcloud MySQL实例网络类型检测', 1, 'HighRisk', 'Qingcloud  账号下MySQL实例已关联到VPC；若您配置阈值，则关联的VpcId需存在您列出的阈值中，视为“合规”，否则视为“不合规”', 'policies:\n    # 账号下RDS实例已关联到VPC；若您配置阈值，则关联的VpcId需存在您列出的阈值中，视为“合规”。\n    - name: qingcloud-mysql-instance-network-type\n      resource: qingcloud.mysql\n      filters:\n        - type: network-type', '[]', 'hummer-qingcloud-plugin', '青云', 'qingcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('4223f09d-b455-4b35-9e9a-07953df5f290', 'Qingcloud ECS VPC检测', 1, 'HighRisk', 'Qingcloud  账号下的Ecs实例指定属于哪些VPC, 属于则合规，不属于则\"不合规\"', 'policies:\n  # 检测您账号下的Ecs实例指定属于哪些VPC, 属于则合规，不属于则\"不合规\"。\n  - name: qingcloud-ecs-vpc-type\n    resource: qingcloud.ecs\n    filters:\n      - type: vpc-type\n        vpcIds: [${{value1}}, ${{value2}}]', '[{\"key\":\"value1\",\"name\":\"vpcId1\",\"defaultValue\":\"\\\"vpc-1\\\"\",\"required\":true},{\"key\":\"value2\",\"name\":\"vpcId2\",\"defaultValue\":\"\\\"vpc-2\\\"\",\"required\":true}]', 'hummer-qingcloud-plugin', '青云', 'qingcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('4e60b929-2158-4459-abc8-81afb99eead9', 'Qingcloud MySQL实例公网访问检测', 1, 'HighRisk', 'Qingcloud  检测您账号下MySQL实例不允许任意来源公网访问，视为“合规”，否则视为“不合规”', 'policies:\n    # 检测您账号下RDS实例不允许任意来源公网访问，视为“合规”，否则视为“不合规”。\n    - name: qingcloud-mysql-internet-access\n      resource: qingcloud.mysql\n      filters:\n        - type: internet-access\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"启用公网访问\",\"defaultValue\":\"true\",\"required\":true}]', 'hummer-qingcloud-plugin', '青云', 'qingcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('5afd9902-e319-4282-b9e7-1748ecf47c7e', 'Qingcloud MySQL实例高可用状态检测', 1, 'HighRisk', 'Qingcloud 账号下MySQL实例具备高可用能力，视为“合规”，否则属于“不合规”', 'policies:\n    # 账号下MySQL实例具备高可用能力，视为“合规”，否则属于“不合规”。\n    - name: qingcloud-mysql-high-availability\n      resource: qingcloud.mysql\n      filters:\n        - type: high-availability', '[]', 'hummer-qingcloud-plugin', '青云', 'qingcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('7b8a7f85-3b68-4f94-bd1f-30ece84e1e90', 'Qingcloud ECS实例公网IP检测', 1, 'HighRisk', 'Qingcloud ECS实例未直接绑定公网IP，视为“合规”，否则属于“不合规”。该规则仅适用于 IPv4 协议', 'policies:\n    # ECS实例未直接绑定公网IP，视为“合规”。该规则仅适用于 IPv4 协议。\n    - name: qingcloud-ecs-public-ipaddress\n      resource: qingcloud.ecs\n      filters:\n        - type: public-ip-address', '[]', 'hummer-qingcloud-plugin', '青云', 'qingcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('c8977309-8b5b-48db-bf16-3380832d4ebb', 'Qingcloud EIP带宽峰值检测', 1, 'HighRisk', 'Qingcloud  检测您账号下的弹性IP实例是否达到最低带宽要求，是视为“合规”，否则视为“不合规”', 'policies:\n    # 检测您账号下的弹性IP实例是否达到最低带宽要求。\n    - name: qingcloud-eip-bandwidth\n      resource: qingcloud.eip\n      filters:\n        - type: bandwidth\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"按带宽计费的公网型实例的带宽峰值\",\"defaultValue\":\"10\",\"required\":true}]', 'hummer-qingcloud-plugin', '青云', 'qingcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('ceecc38d-5c98-4cc4-aeef-028ab653c26b', 'Qingcloud MongoDB实例公网访问检测', 1, 'HighRisk', 'Qingcloud  账号下MongoDB实例不允许任意来源公网访问，视为“合规”，否则视为“不合规”', 'policies:\n    # 账号下MongoDB实例不允许任意来源公网访问，视为“合规”。\n    - name: qingcloud-mongodb-internet-access\n      resource: qingcloud.mongodb\n      filters:\n        - type: internet-access\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"公网访问\",\"defaultValue\":\"true\",\"required\":true}]', 'hummer-qingcloud-plugin', '青云', 'qingcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('d83c8434-e0ca-4250-b7c0-e5add655262d', 'Qingcloud ECS实例网络类型检测', 1, 'HighRisk', 'Qingcloud 账号下所有ECS实例已关联到VPC；若您配置阈值，则关联的VpcId需存在您列出的阈值中，视为“合规”，否则视为“不合规”', 'policies:\n    # 账号下所有ECS实例已关联到VPC；若您配置阈值，则关联的VpcId需存在您列出的阈值中，视为“合规”。\n    - name: qingcloud-ecs-instance-network-type\n      resource: qingcloud.ecs\n      filters:\n        - type: instance-network-type', '[]', 'hummer-qingcloud-plugin', '青云', 'qingcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('e8df5a37-a78d-4115-9db2-145d3ebda280', 'Qingcloud MongoDB实例网络类型检测', 1, 'HighRisk', 'Qingcloud  检测您账号下的MongoDB实例是否运行在VPC网络环境下，是视为“合规”，否则视为“不合规”', 'policies:\n    # 检测您账号下的MongoDB实例是否运行在VPC网络环境下。\n    - name: qingcloud-mongodb-network-type\n      resource: qingcloud.mongodb\n      filters:\n        - type: network-type', '[]', 'hummer-qingcloud-plugin', '青云', 'qingcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian');

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

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('001400f0-0a52-43c5-945e-d9e28c6a74f9', 'UCloud uhost实例公网IP检测', 1, 'HighRisk', 'UCloud uhost实例未直接绑定公网IP，视为“合规”，否则属于“不合规”。该规则仅适用于 IPv4 协议', 'policies:\n    # uhost实例未直接绑定公网IP，视为“合规”。该规则仅适用于 IPv4 协议。\n    - name: ucloud-uhost-public-ipaddress\n      resource: ucloud.uhost\n      filters:\n        - type: public-ip-address', '[]', 'hummer-ucloud-plugin', 'UCloud 优刻得', 'ucloud.png', concat(unix_timestamp(now()), '004'), 1, 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('155f44ef-b0c6-4881-b2c2-ac1daffa04da', 'UCloud uhost VPC检测', 1, 'HighRisk', 'UCloud  账号下的uhost实例指定属于哪些VPC, 属于则合规，不属于则\"不合规\"', 'policies:\n  # 检测您账号下的uhost实例指定属于哪些VPC, 属于则合规，不属于则\"不合规\"。\n  - name: ucloud-uhost-vpc-type\n    resource: ucloud.uhost\n    filters:\n      - type: vpc-type\n        vpcIds: [${{value1}}, ${{value2}}]', '[{\"key\":\"value1\",\"name\":\"vpcId1\",\"defaultValue\":\"\\\"vpc-1\\\"\",\"required\":true},{\"key\":\"value2\",\"name\":\"vpcId2\",\"defaultValue\":\"\\\"vpc-2\\\"\",\"required\":true}]', 'hummer-ucloud-plugin', 'UCloud 优刻得', 'ucloud.png', concat(unix_timestamp(now()), '004'), 1, 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('32d7aec8-265e-4ae8-916c-85bd2798b568', 'UCloud uhost实例网络类型检测', 1, 'HighRisk', 'UCloud  账号下所有uhost实例已关联到VPC；若您配置阈值，则关联的VpcId需存在您列出的阈值中，视为“合规”，否则视为“不合规”', 'policies:\n    # 账号下所有Uhost实例已关联到VPC；若您配置阈值，则关联的VpcId需存在您列出的阈值中，视为“合规”。\n    - name: ucloud-uhost-instance-network-type\n      resource: ucloud.uhost\n      filters:\n        - type: instance-network-type', '[]', 'hummer-ucloud-plugin', 'UCloud 优刻得', 'ucloud.png', concat(unix_timestamp(now()), '004'), 1, 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('5fb3ca1d-2a8d-46c7-8549-403139a10fbd', 'UCloud EIP带宽峰值检测', 1, 'HighRisk', 'UCloud 检测您账号下的弹性IP实例是否达到最低带宽要求，是视为“合规”，否则视为“不合规”', 'policies:\n    # 检测您账号下的弹性IP实例是否达到最低带宽要求。\n    - name: ucloud-eip-bandwidth\n      resource: ucloud.eip\n      filters:\n        - type: bandwidth\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"按带宽计费的公网型实例的带宽峰值\",\"defaultValue\":\"10\",\"required\":true}]', 'hummer-ucloud-plugin', 'UCloud 优刻得', 'ucloud.png', concat(unix_timestamp(now()), '004'), 1, 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('844fd144-0e31-419f-adf7-d6f89673faaa', 'UCloud uhost挂载磁盘加密状态检测', 1, 'HighRisk', 'UCloud 账号下 uhost 挂载所有的磁盘均已加密；若您配置阈值，则磁盘加密的Id需存在您列出的阈值中，视为“合规”，否则视为“不合规”', 'policies:\n    # 账号下uhost挂载的所有的磁盘是否加密。\n    - name: ucloud-encrypted-disk\n      resource: ucloud.uhost\n      filters:\n        - type: disk-encrypted\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"磁盘是否加密( true/false )\",\"defaultValue\":\"true\",\"required\":true}]', 'hummer-ucloud-plugin', 'UCloud 优刻得', 'ucloud.png', concat(unix_timestamp(now()), '004'), 1, 'custodian');

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