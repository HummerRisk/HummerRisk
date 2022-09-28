-------------------
----UCloud Rule----
-------------------

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('351684f0-1464-428e-a66d-4dbcfb1839b0', 'UCloud SecurityGroup安全组配置检测', 1, 'HighRisk', 'UCloud  账号下安全组配置不为“0.0.0.0/0”，视为“合规”，否则属于“不合规”', 'policies:\n    # 账号下安全组配置不为“0.0.0.0/0”，视为“合规”。\n    - name: ucloud-sg-source-cidr-ip\n      resource: ucloud.securitygroup\n      filters:\n        - type: source-cidr-ip\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"目标IP地址段\",\"defaultValue\":\"\\\"0.0.0.0/0\\\"\",\"required\":true}]', 'hummer-ucloud-plugin', 'UCloud 优刻得', 'ucloud.png', concat(unix_timestamp(now()), '001'), '1', 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('3b61a7cf-e3b4-4120-86c1-ffe92c3a6d66', 'UCloud SecurityGroup安全组端口访问检测', 1, 'HighRisk', 'UCloud  账号下安全组配置允许所有端口访问视为”不合规“，否则为”合规“', 'policies:\n  # 账号下安全组配置允许所有端口访问视为”不合规“，否则为”合规“\n  - name: ucloud-sg-ports\n    resource: ucloud.securitygroup\n    filters:\n      - type: source-ports\n        SourceCidrIp: ${{SourceCidrIp}}\n        PortRange: ${{PortRange}}', '[{\"key\":\"SourceCidrIp\",\"name\":\"目标IP地址段\",\"defaultValue\":\"\\\"0.0.0.0/0\\\"\",\"required\":true},{\"key\":\"PortRange\",\"name\":\"端口号\",\"defaultValue\":\"”-1/-1“\",\"required\":true}]', 'hummer-ucloud-plugin', 'UCloud 优刻得', 'ucloud.png', concat(unix_timestamp(now()), '001'), '1', 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('6008cc3c-0adb-48ba-86b8-7ad2fc8efdca', 'UCloud ULB负载均衡HTTPS监听检测', 1, 'HighRisk', 'UCloud  ULB负载均衡开启HTTPS监听，视为“合规”，否则属于“不合规”', 'policies:\n    # 负载均衡开启HTTPS监听，视为“合规”。\n    - name: ucloud-ulb-listener\n      resource: ucloud.ulb\n      filters:\n        - type: listener\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"负载均衡实例前端使用的协议\",\"defaultValue\":\"HTTPS\",\"required\":true}]', 'hummer-ucloud-plugin', 'UCloud 优刻得', 'ucloud.png', concat(unix_timestamp(now()), '001'), '1', 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('6cbe7b32-d431-4fcb-9c47-4ba1411cb52c', 'UCloud SecurityGroup高危安全组检测', 1, 'HighRisk', 'UCloud  检测安全组是否开启风险端口，未开启视为“合规”，否则属于“不合规”', 'policies:\n  #检测开放以下高危端口的安全组：\n  #(20,21,22,25,80,773,765,1733,1737,3306,3389,7333,5732,5500)\n  - name: ucloud-security-group\n    resource: ucloud.securitygroup\n    description: |\n      Add Filter all security groups, filter ports\n      [20,21,22,25,80,773,765,1733,1737,3306,3389,7333,5732,5500]\n      on 0.0.0.0/0 or\n      [20,21,22,25,80,773,765, 1733,1737,3306,3389,7333,5732,5500]\n      on ::/0 (IPv6)\n    filters:\n        - or:\n            - type: ingress\n              IpProtocol: \"-1\"\n              Ports: ${{ipv4_port}}\n              Cidr: \"0.0.0.0/0\"\n            - type: ingress\n              IpProtocol: \"-1\"\n              Ports: ${{ipv6_port}}\n              CidrV6: \"::/0\"', '[{\"key\":\"ipv4_port\",\"name\":\"ipv4端口\",\"defaultValue\":\"[20,21,22,25,80,773,765, 1733,1737,3306,3389,7333,5732,5500]\",\"required\":true},{\"key\":\"ipv6_port\",\"name\":\"ipv6端口\",\"defaultValue\":\"[20,21,22,25,80,773,765, 1733,1737,3306,3389,7333,5732,5500]\",\"required\":true}]', 'hummer-ucloud-plugin', 'UCloud 优刻得', 'ucloud.png', concat(unix_timestamp(now()), '001'), '1', 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('91bdd835-1071-4d6a-825a-4afdf58dce71', 'UCloud ULB VPC检测', 1, 'HighRisk', 'UCloud  账号下ULB负载均衡实例指定属于哪些VPC, 属于则合规，不属于则\"不合规\"', 'policies:\n  # 检测您账号下ULB负载均衡实例指定属于哪些VPC, 属于则合规，不属于则\"不合规\"。\n  - name: ucloud-ulb-vpc-type\n    resource: ucloud.ulb\n    filters:\n      - type: vpc-type\n        vpcIds: [${{value1}}, ${{value2}}]', '[{\"key\":\"value1\",\"name\":\"vpcId1\",\"defaultValue\":\"\\\"vpc-1\\\"\",\"required\":true},{\"key\":\"value2\",\"name\":\"vpcId2\",\"defaultValue\":\"\\\"vpc-2\\\"\",\"required\":true}]', 'hummer-ucloud-plugin', 'UCloud 优刻得', 'ucloud.png', concat(unix_timestamp(now()), '001'), '1', 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('b285f285-10cc-45cc-b59e-7b84fd229815', 'UCloud ULB负载均衡监听检测', 1, 'HighRisk', 'UCloud  账号下的ULB负载均衡是否开启HTTPS或HTTP监听，开启视为合规，否则不合规', 'policies:\n  # 检测您账号下的ULB负载均衡是否开启HTTPS或HTTP监听，开启视为合规，否则不合规。\n  - name: ucloud-ulb-listener-type\n    resource: ucloud.ulb\n    filters:\n      - type: listener-type\n        value: [${{value1}}, ${{value2}}]', '[{\"key\":\"value1\",\"name\":\"监听类型1\",\"defaultValue\":\"HTTPS\",\"required\":true},{\"key\":\"value2\",\"name\":\"监听类型2\",\"defaultValue\":\"HTTP\",\"required\":true}]', 'hummer-ucloud-plugin', 'UCloud 优刻得', 'ucloud.png', concat(unix_timestamp(now()), '001'), '1', 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('b85e5722-fd4a-471e-af94-b9d38cc23b22', 'UCloud ULB带宽峰值检测', 1, 'HighRisk', 'UCloud  检测您账号下的负载均衡实例是否达到最低带宽要求，是视为“合规”，否则视为“不合规”', 'policies:\n    # 检测您账号下的负载均衡实例是否达到最低带宽要求。\n    - name: ucloud-ulb-bandwidth\n      resource: ucloud.ulb\n      filters:\n        - type: bandwidth\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"按带宽计费的公网型实例的带宽峰值\",\"defaultValue\":\"10\",\"required\":true}]', 'hummer-ucloud-plugin', 'UCloud 优刻得', 'ucloud.png', concat(unix_timestamp(now()), '001'), '1', 'custodian', NULL);

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
