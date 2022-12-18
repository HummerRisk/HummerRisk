

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

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('33f795ca-40e4-4b6b-874c-bb0d716e3812', 'JDCloud VM VPC 检测', 1, 'HighRisk', 'JDCloud 账号下的 VM 实例指定属于哪些 VPC, 属于则合规，不属于则“不合规“', 'policies:\n  # 账号下的 vm 实例指定属于哪些 VPC, 属于则合规，不属于则“不合规“。\n  - name: jdcloud-vm-vpc-type\n    resource: jdcloud.vm\n    filters:\n      - type: vpc-type\n        vpcIds: [${{value1}}, ${{value2}}]', '[{\"key\":\"value1\",\"name\":\"vpcId1\",\"defaultValue\":\"\\\"vpc-1\\\"\",\"required\":true},{\"key\":\"value2\",\"name\":\"vpcId2\",\"defaultValue\":\"\\\"vpc-2\\\"\",\"required\":true}]', 'hummer-jdcloud-plugin', '京东云', 'jdcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('3d5dec2b-42b6-4059-b9ef-24ba86e0d9f3', 'JDCloud SecurityGroup 安全组端口访问检测', 1, 'HighRisk', 'JDCloud 账号下安全组配置允许所有端口访问视为”不合规“，否则为”合规“', 'policies:\n  # 账号下安全组配置允许所有端口访问视为”不合规“，否则为”合规“。\n  - name: jdcloud-sg-ports\n    resource: jdcloud.securitygroup\n    filters:\n      - type: source-ports\n        SourceCidrIp: ${{SourceCidrIp}}\n        PortRange: ${{PortRange}}', '[{\"key\":\"SourceCidrIp\",\"name\":\"目标IP地址段\",\"defaultValue\":\"\\\"0.0.0.0/0\\\"\",\"required\":true},{\"key\":\"PortRange\",\"name\":\"端口号\",\"defaultValue\":\"\\\"-1/-1\\\"\",\"required\":true}]', 'hummer-jdcloud-plugin', '京东云', 'jdcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('49ee3ed4-5bfc-4f10-8d3f-c5ae28301722', 'JDCloud EIP 带宽峰值检测', 1, 'HighRisk', 'JDCloud 检测您账号下的弹性 IP 实例是否达到最低带宽要求，符合视为“合规”，否则视为“不合规”', 'policies:\n    # 检测您账号下的弹性IP实例是否达到最低带宽要求\n    - name: jdcloud-eip-bandwidth\n      resource: jdcloud.eip\n      filters:\n        - type: bandwidth\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"按带宽计费的公网型实例的带宽峰值\",\"defaultValue\":\"10\",\"required\":true}]', 'hummer-jdcloud-plugin', '京东云', 'jdcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('4a6b1c6b-c2ae-48e8-be84-54b9baa7ca75', 'JDCloud VM 实例网络类型检测', 1, 'HighRisk', 'JDCloud 账号下所有 VM 实例已关联到 VPC；若您配置阈值，则关联的 VpcId 需存在您列出的阈值中，视为“合规”，否则视为“不合规”', 'policies:\n    # 账号下所有 vm 实例已关联到 VPC；若您配置阈值，则关联的 VpcId 需存在您列出的阈值中，视为“合规”，否则视为“不合规”。\n    - name: jdcloud-vm-instance-network-type\n      resource: jdcloud.vm\n      filters:\n        - type: instance-network-type', '[]', 'hummer-jdcloud-plugin', '京东云', 'jdcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('5ee975a6-0084-47db-9205-6dc3c92f33e4', 'JDCloud LB 负载均衡监听检测', 1, 'HighRisk', 'JDCloud 账号下的 LB 负载均衡是否开启 HTTPS 或 HTTP 监听，开启视为“合规“，否则“不合规“', 'policies:\n  # 账号下的 ULB 负载均衡是否开启 HTTPS 或 HTTP 监听，开启视为“合规“，否则“不合规“。\n  - name: jdcloud-lb-listener-type\n    resource: jdcloud.lb\n    filters:\n      - type: listener-type\n        value: [${{value1}}, ${{value2}}]', '[{\"key\":\"value1\",\"name\":\"监听类型1\",\"defaultValue\":\"Https\",\"required\":true},{\"key\":\"value2\",\"name\":\"监听类型2\",\"defaultValue\":\"Http\",\"required\":true}]', 'hummer-jdcloud-plugin', '京东云', 'jdcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('8c341477-d86e-47a3-85d6-3ee88dbbc032', 'JDCloud LB 负载均衡 HTTPS 监听检测', 1, 'HighRisk', 'JDCloud LB 负载均衡开启 HTTPS 监听，符合视为“合规”，否则属于“不合规”', 'policies:\n    # LB 负载均衡开启 HTTPS 监听，符合视为“合规”，否则属于“不合规”。\n    - name: jdcloud-lb-listener\n      resource: jdcloud.lb\n      filters:\n        - type: listener\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"负载均衡实例前端使用的协议\",\"defaultValue\":\"Https\",\"required\":true}]', 'hummer-jdcloud-plugin', '京东云', 'jdcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('91ca3531-1526-4ec2-ad06-ce818d1c0be9', 'JDCloud VM 实例公网 IP 检测', 1, 'HighRisk', 'JDCloud VM 实例未直接绑定公网IP，视为“合规”，否则属于“不合规”。该规则仅适用于 IPv4 协议', 'policies:\n    # VM 实例未直接绑定公网IP，视为“合规”。该规则仅适用于 IPv4 协议。\n    - name: jdcloud-cm-public-ipaddress\n      resource: jdcloud.vm\n      filters:\n        - type: public-ip-address', '[]', 'hummer-jdcloud-plugin', '京东云', 'jdcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('a999431d-bfd9-49e6-8c8e-5aa048f431ce', 'JDCloud SecurityGroup 高危安全组检测', 1, 'HighRisk', 'JDCloud 检测安全组是否开启风险端口，未开启视为“合规”，否则属于“不合规”', 'policies:\n  # 检测安全组是否开启风险端口，未开启视为“合规”，否则属于“不合规”：\n  #(20,21,22,25,80,773,765,1733,1737,3306,3389,7333,5732,5500)\n  - name: jdcloud-security-group\n    resource: jdcloud.securitygroup\n    description: |\n      Add Filter all security groups, filter ports\n      [20,21,22,25,80,773,765,1733,1737,3306,3389,7333,5732,5500]\n      on 0.0.0.0/0 or\n      [20,21,22,25,80,773,765, 1733,1737,3306,3389,7333,5732,5500]\n      on ::/0 (IPv6)\n    filters:\n        - or:\n            - type: ingress\n              IpProtocol: \"-1\"\n              Ports: ${{ipv4_port}}\n              Cidr: \"0.0.0.0/0\"\n            - type: ingress\n              IpProtocol: \"-1\"\n              Ports: ${{ipv6_port}}\n              CidrV6: \"::/0\"', '[{\"key\":\"ipv4_port\",\"name\":\"ipv4端口\",\"defaultValue\":\"[20,21,22,25,80,773,765, 1733,1737,3306,3389,7333,5732,5500]\",\"required\":true},{\"key\":\"ipv6_port\",\"name\":\"ipv6端口\",\"defaultValue\":\"[20,21,22,25,80,773,765, 1733,1737,3306,3389,7333,5732,5500]\",\"required\":true}]', 'hummer-jdcloud-plugin', '京东云', 'jdcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('ad76db29-f65f-4e42-9da2-055d993103bd', 'JDCloud CDN域名HTTPS监听检测', 1, 'HighRisk', 'JDCloud  检测CDN域名是否开启HTTPS监听，若开启视为“合规”，否则视为“不合规”', 'policies:\n    # 检测CDN域名是否开启HTTPS监听，若开启视为“合规”。\n    - name: jdcloud-cdn-ssl-protocol\n      resource: jdcloud.cdn\n      filters:\n        - type: ssl-protocol', '[]', 'hummer-jdcloud-plugin', '京东云', 'jdcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('efacc257-f6ad-45e3-941e-ce4e5febb1ee', 'JDCloud LB VPC 检测', 1, 'HighRisk', 'JDCloud 账号下 LB 负载均衡实例指定属于哪些 VPC, 属于则合规，不属于则“不合规“', 'policies:\n  # 账号下 LB 负载均衡实例指定属于哪些 VPC, 属于则合规，不属于则“不合规“。\n  - name: jdcloud-lb-vpc-type\n    resource: jdcloud.lb\n    filters:\n      - type: vpc-type\n        vpcIds: [${{value1}}, ${{value2}}]', '[{\"key\":\"value1\",\"name\":\"vpcId1\",\"defaultValue\":\"\\\"vpc-1\\\"\",\"required\":true},{\"key\":\"value2\",\"name\":\"vpcId2\",\"defaultValue\":\"\\\"vpc-2\\\"\",\"required\":true}]', 'hummer-jdcloud-plugin', '京东云', 'jdcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('f0c74c83-81ba-45f8-810e-c1ae9b06de65', 'JDCloud Disk 磁盘加密检测', 1, 'HighRisk', 'JDCloud 账号下所有的磁盘均已加密，符合视为“合规”，否则视为“不合规”', 'policies:\n    # 账号下所有的磁盘均已加密，符合视为“合规”，否则视为“不合规”\n    - name: jdcloud-disk-encrypt\n      resource: jdcloud.disk\n      filters:\n        - type: encrypted\n          value: ${{value}}', '[{\"defaultValue\":\"True\",\"name\":\"磁盘是否加密( True/False )\",\"key\":\"value\",\"required\":true}]', 'hummer-jdcloud-plugin', '京东云', 'jdcloud.png', concat(unix_timestamp(now()), '003'), 1, 'custodian', NULL);

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('91ca3531-1526-4ec2-ad06-ce818d1c0be9', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ad76db29-f65f-4e42-9da2-055d993103bd', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('49ee3ed4-5bfc-4f10-8d3f-c5ae28301722', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5ee975a6-0084-47db-9205-6dc3c92f33e4', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8c341477-d86e-47a3-85d6-3ee88dbbc032', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('efacc257-f6ad-45e3-941e-ce4e5febb1ee', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('3d5dec2b-42b6-4059-b9ef-24ba86e0d9f3', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a999431d-bfd9-49e6-8c8e-5aa048f431ce', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('4a6b1c6b-c2ae-48e8-be84-54b9baa7ca75', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('33f795ca-40e4-4b6b-874c-bb0d716e3812', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f0c74c83-81ba-45f8-810e-c1ae9b06de65', @groupId3);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('91ca3531-1526-4ec2-ad06-ce818d1c0be9', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('ad76db29-f65f-4e42-9da2-055d993103bd', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('49ee3ed4-5bfc-4f10-8d3f-c5ae28301722', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('5ee975a6-0084-47db-9205-6dc3c92f33e4', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('8c341477-d86e-47a3-85d6-3ee88dbbc032', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('efacc257-f6ad-45e3-941e-ce4e5febb1ee', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('3d5dec2b-42b6-4059-b9ef-24ba86e0d9f3', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('a999431d-bfd9-49e6-8c8e-5aa048f431ce', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('4a6b1c6b-c2ae-48e8-be84-54b9baa7ca75', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('33f795ca-40e4-4b6b-874c-bb0d716e3812', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('f0c74c83-81ba-45f8-810e-c1ae9b06de65', 'safety');

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('21522967-2016-49fd-bf54-2a41a1b16503', '3d5dec2b-42b6-4059-b9ef-24ba86e0d9f3', 'jdcloud.securitygroup');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('21da294f-51cf-42f8-aae3-42164f692240', '8c341477-d86e-47a3-85d6-3ee88dbbc032', 'jdcloud.lb');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('56448ea4-a401-4418-a4be-253df138406a', 'ad76db29-f65f-4e42-9da2-055d993103bd', 'jdcloud.cdn');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('69b96331-6763-43ed-8f01-adffba79617f', 'efacc257-f6ad-45e3-941e-ce4e5febb1ee', 'jdcloud.lb');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('72de196c-f393-40de-b86a-c5c7637bf70e', 'a999431d-bfd9-49e6-8c8e-5aa048f431ce', 'jdcloud.securitygroup');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('8a970feb-85de-462c-9aae-dce16698fb79', '91ca3531-1526-4ec2-ad06-ce818d1c0be9', 'jdcloud.vm');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('9b8638e4-1026-4def-9824-e103d0901537', '5ee975a6-0084-47db-9205-6dc3c92f33e4', 'jdcloud.lb');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('b2fda5d8-3c0f-4621-9c30-f13d8f6a58cb', '49ee3ed4-5bfc-4f10-8d3f-c5ae28301722', 'jdcloud.eip');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('d2d80c1b-b04e-4c17-bb5a-c95b0546b3bb', '4a6b1c6b-c2ae-48e8-be84-54b9baa7ca75', 'jdcloud.vm');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('d5af8c72-6e6f-4f85-96c5-17d924774010', '33f795ca-40e4-4b6b-874c-bb0d716e3812', 'jdcloud.vm');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('e31c4439-8076-49fd-9557-6a25c30daaa6', 'f0c74c83-81ba-45f8-810e-c1ae9b06de65', 'jdcloud.disk');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('91ca3531-1526-4ec2-ad06-ce818d1c0be9', '18');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ad76db29-f65f-4e42-9da2-055d993103bd', '52');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('49ee3ed4-5bfc-4f10-8d3f-c5ae28301722', '2');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5ee975a6-0084-47db-9205-6dc3c92f33e4', '10');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5ee975a6-0084-47db-9205-6dc3c92f33e4', '13');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5ee975a6-0084-47db-9205-6dc3c92f33e4', '92');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5ee975a6-0084-47db-9205-6dc3c92f33e4', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5ee975a6-0084-47db-9205-6dc3c92f33e4', '95');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('8c341477-d86e-47a3-85d6-3ee88dbbc032', '52');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('efacc257-f6ad-45e3-941e-ce4e5febb1ee', '10');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('efacc257-f6ad-45e3-941e-ce4e5febb1ee', '13');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('efacc257-f6ad-45e3-941e-ce4e5febb1ee', '92');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('efacc257-f6ad-45e3-941e-ce4e5febb1ee', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('efacc257-f6ad-45e3-941e-ce4e5febb1ee', '95');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('3d5dec2b-42b6-4059-b9ef-24ba86e0d9f3', '10');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('3d5dec2b-42b6-4059-b9ef-24ba86e0d9f3', '92');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('3d5dec2b-42b6-4059-b9ef-24ba86e0d9f3', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('3d5dec2b-42b6-4059-b9ef-24ba86e0d9f3', '95');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('a999431d-bfd9-49e6-8c8e-5aa048f431ce', '9');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('a999431d-bfd9-49e6-8c8e-5aa048f431ce', '92');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('a999431d-bfd9-49e6-8c8e-5aa048f431ce', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('a999431d-bfd9-49e6-8c8e-5aa048f431ce', '95');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('a999431d-bfd9-49e6-8c8e-5aa048f431ce', '46');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4a6b1c6b-c2ae-48e8-be84-54b9baa7ca75', '3');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4a6b1c6b-c2ae-48e8-be84-54b9baa7ca75', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4a6b1c6b-c2ae-48e8-be84-54b9baa7ca75', '5');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4a6b1c6b-c2ae-48e8-be84-54b9baa7ca75', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4a6b1c6b-c2ae-48e8-be84-54b9baa7ca75', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4a6b1c6b-c2ae-48e8-be84-54b9baa7ca75', '91');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('33f795ca-40e4-4b6b-874c-bb0d716e3812', '10');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('33f795ca-40e4-4b6b-874c-bb0d716e3812', '92');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('33f795ca-40e4-4b6b-874c-bb0d716e3812', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('33f795ca-40e4-4b6b-874c-bb0d716e3812', '95');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('33f795ca-40e4-4b6b-874c-bb0d716e3812', '13');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f0c74c83-81ba-45f8-810e-c1ae9b06de65', '53');

UPDATE `rule` SET `name` = 'Qiniu Kodo 公开读取访问权限检测',  `script` = 'policies:\n    - name: qiniu-kodo-private\n      resource: qiniu.kodo\n      filters:\n        - type: private\n          value: ${{value}}'  WHERE `id` = '1268870b-188d-41a8-a86e-0c83299dc93c';

DELETE FROM `rule_type` WHERE ID = 'c0cd80bf-259f-4a46-a1a8-9ce7edc5e592';

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('25a9ac83-a4c0-4524-b692-06b7d63b3516', '1268870b-188d-41a8-a86e-0c83299dc93c', 'qiniu.kodo');