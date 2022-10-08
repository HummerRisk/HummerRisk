-- ----------------------------
-- UCloud Rule
-- ----------------------------

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
    `create_time`                   bigint(13)            DEFAULT NULL COMMENT '创建时间',
    `account_id`                    varchar(50)           DEFAULT NULL COMMENT '云账号ID',
    `plugin_id`                     varchar(40)           DEFAULT NULL COMMENT '插件ID',
    `plugin_name`                   varchar(128)          DEFAULT NULL COMMENT '云平台名称',
    `plugin_icon`                   varchar(128)          DEFAULT NULL COMMENT '云平台图标',
    `resource_types`                varchar(256)          DEFAULT NULL COMMENT '资源类型',
    `resources_sum`                 bigint(13)            DEFAULT 0 COMMENT '资源总量',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `cloud_resource_sync_item` (
    `id`                           varchar(50)         NOT NULL,
    `sync_id`                      varchar(50)         DEFAULT NULL COMMENT '任务ID',
    `details`                      longtext            DEFAULT NULL COMMENT 'policy内容',
    `status`                       varchar(20)         DEFAULT NULL COMMENT '状态',
    `count`                        int(11)             DEFAULT '1'  COMMENT '数量',
    `resource_type`                varchar(128)        DEFAULT NULL COMMENT '资源类型',
    `region_id`                    varchar(128)        DEFAULT NULL COMMENT '区域标识',
    `region_name`                  varchar(128)        DEFAULT NULL COMMENT '区域名称',
    `account_id`                   varchar(50)         DEFAULT NULL COMMENT '云账号ID',
    `account_url`                  varchar(128)        DEFAULT NULL COMMENT '云账号图标',
    `account_label`                varchar(128)        DEFAULT NULL COMMENT '云账号名称',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `cloud_resource_sync_item_log` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `sync_item_id`                 varchar(50)         DEFAULT NULL COMMENT '任务项ID',
    `account_id`                   varchar(50)         DEFAULT NULL COMMENT '云账号ID',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    varchar(1024)       DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint(1)          DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `cloud_resource` (
    `id`                         varchar(50)         NOT NULL,
    `resource_status`            varchar(45)         DEFAULT NULL COMMENT '资源状态',
    `resource_type`              varchar(64)         DEFAULT NULL COMMENT '资源类型',
    `custodian_run_log`          longtext            DEFAULT NULL COMMENT 'custodian-run.log',
    `metadata`                   longtext            DEFAULT NULL COMMENT 'metadata.json',
    `resources`                  longtext            DEFAULT NULL COMMENT 'resources.json',
    `resources_sum`              bigint(13)          DEFAULT 0 COMMENT '资源总量',
    `plugin_id`                  varchar(40)         DEFAULT NULL COMMENT '插件名称',
    `plugin_name`                varchar(40)         DEFAULT NULL COMMENT '云平台名称',
    `plugin_icon`                varchar(128)        DEFAULT NULL COMMENT '云平台图标',
    `account_id`                 varchar(50)         DEFAULT NULL COMMENT '云账号ID',
    `region_id`                  varchar(128)        DEFAULT NULL COMMENT '区域标识',
    `region_name`                varchar(128)        DEFAULT NULL COMMENT '区域名称',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
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
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `resource`                   longtext            DEFAULT NULL COMMENT '资源JSON',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `cloud_resource_amount_history` (
    `id`                            varchar(50)           NOT NULL COMMENT 'ID',
    `sync_id`                       varchar(50)           DEFAULT NULL COMMENT '任务ID',
    `sync_time`                     bigint(13)            DEFAULT NULL COMMENT '同步时间',
    `create_time`                   bigint(13)            DEFAULT NULL COMMENT '创建时间',
    `account_id`                    varchar(50)           DEFAULT NULL COMMENT '云账号ID',
    `plugin_id`                     varchar(40)           DEFAULT NULL COMMENT '插件ID',
    `plugin_name`                   varchar(128)          DEFAULT NULL COMMENT '云平台名称',
    `plugin_icon`                   varchar(128)          DEFAULT NULL COMMENT '云平台图标',
    `resource_type`                 varchar(64)           DEFAULT NULL COMMENT '资源类型',
    `region_id`                     varchar(128)          DEFAULT NULL COMMENT '区域标识',
    `region_name`                   varchar(128)          DEFAULT NULL COMMENT '区域名称',
    `count`                         int(11)               DEFAULT 0 COMMENT '资源同步量',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;


ALTER TABLE `server_result` ADD `is_severity` tinyint(1) DEFAULT 1 COMMENT '是否有风险';

ALTER TABLE `history_server_task` ADD `is_severity` tinyint(1) DEFAULT 1 COMMENT '是否有风险';

UPDATE rule_type SET resource_type = 'huawei.obs' WHERE id = '3e50ec86-1a2a-486e-920b-39572eb8d193';

ALTER TABLE `cloud_event` ADD `id` varchar(64) NOT NULL COMMENT 'ID';

ALTER TABLE `cloud_event` ADD PRIMARY KEY ( `id` );

-- -----------------
-- Aliyun
-- -----------------

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('0fab953a-c392-493d-9ef8-238cf5651d40', 'Aliyun MSE VPC 检测', 1, 'HighRisk', 'Aliyun  账号下的 MSE 实例指定属于哪些 VPC, 符合视为“合规”，否则视为“不合规”', 'policies:\n  # 检测您账号下的Mse实例指定属于哪些VPC, 属于则合规，不属于则\"不合规\"。\n  - name: aliyun-mse-vpc-type\n    resource: aliyun.mse\n    filters:\n      - type: vpc-type\n        vpcIds: [${{value1}}, ${{value2}}]', '[{\"defaultValue\":\"\\\"vpcId1\\\"\",\"name\":\"vpcId1\",\"key\":\"value1\",\"required\":true},{\"defaultValue\":\"\\\"vpcId2\\\"\",\"name\":\"vpcId2\",\"key\":\"value2\",\"required\":true}]', 'hummer-aliyun-plugin', '阿里云', 'aliyun.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('8810fc51-d234-4454-836a-95a9b1dec196', 'Aliyun ACK 删除保护检测', 1, 'HighRisk', 'Aliyun  ACK 集群开启删除保护，符合视为“合规”，否则视为“不合规”', 'policies:\n    # ACK 集群开启删除保护，符合视为“合规”，否则视为“不合规”。\n    - name: aliyun-ack-deletion-protection\n      resource: aliyun.ack\n      filters:\n        - type: deletion-protection\n          value: ${{value}}', '[{\"defaultValue\":\"true\",\"name\":\"是否开启删除保护\",\"key\":\"value\",\"required\":true}]', 'hummer-aliyun-plugin', '阿里云', 'aliyun.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('b9ff94b8-8959-4eac-86e9-983d8e6d7db6', 'Aliyun NAS 带宽峰值检测', 1, 'HighRisk', 'Aliyun 检测您账号下的 NAS 是否达到最低带宽要求，符合视为“合规”，否则视为“不合规”', 'policies:\n    # 检测您账号下的 NAS 是否达到最低带宽要求，是视为“合规”，否则视为“不合规”\n    - name: aliyun-nas-bandwidth\n      resource: aliyun.nas\n      filters:\n        - type: bandwidth\n          value: ${{value}}', '[{\"defaultValue\":\"100\",\"name\":\"带宽\",\"key\":\"value\",\"required\":true}]', 'hummer-aliyun-plugin', '阿里云', 'aliyun.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('deede37b-2991-40b3-b8b5-089914e4cd43', 'Aliyun NAS 磁盘加密状态检测', 1, 'HighRisk', 'Aliyun 账号下 NAS 磁盘均已加密，符合视为“合规”，否则视为“不合规”', 'policies:\n    # 账号下 NAS 磁盘均已加密，视为“合规”，否则视为“不合规”\n    - name: aliyun-nas-encrypted\n      resource: aliyun.nas\n      filters:\n        - type: encrypted\n          value: ${{value}}', '[{\"defaultValue\":\"0\",\"name\":\"加密状态 0:未加密 1:NAS托管密钥 2:用户管理密钥\",\"key\":\"value\",\"required\":true}]', 'hummer-aliyun-plugin', '阿里云', 'aliyun.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('0fab953a-c392-493d-9ef8-238cf5651d40', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('8810fc51-d234-4454-836a-95a9b1dec196', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('b9ff94b8-8959-4eac-86e9-983d8e6d7db6', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('deede37b-2991-40b3-b8b5-089914e4cd43', 'safety');

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('139c93ee-c821-44d6-89fe-e41e558bee41', '8810fc51-d234-4454-836a-95a9b1dec196', 'aliyun.ack');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('143615d5-a9ca-4df3-b253-90a9f94b01f2', 'b9ff94b8-8959-4eac-86e9-983d8e6d7db6', 'aliyun.nas');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('293b16e4-a07c-4c18-b758-e6abeaa7b0df', 'deede37b-2991-40b3-b8b5-089914e4cd43', 'aliyun.nas');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('47ac4a8b-a16e-46ef-89e4-f46cfd1979cf', '0fab953a-c392-493d-9ef8-238cf5651d40', 'aliyun.mse');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0fab953a-c392-493d-9ef8-238cf5651d40', '10');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0fab953a-c392-493d-9ef8-238cf5651d40', '13');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0fab953a-c392-493d-9ef8-238cf5651d40', '92');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0fab953a-c392-493d-9ef8-238cf5651d40', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0fab953a-c392-493d-9ef8-238cf5651d40', '95');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b9ff94b8-8959-4eac-86e9-983d8e6d7db6', '2');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('deede37b-2991-40b3-b8b5-089914e4cd43', '53');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('deede37b-2991-40b3-b8b5-089914e4cd43', '94');

SELECT id INTO @groupId11 from rule_group where name = 'Aliyun 等保预检';
SELECT id INTO @groupId12 from rule_group where name = 'Aliyun MSE 最佳安全实践';
SELECT id INTO @groupId13 from rule_group where name = 'Aliyun ACK 最佳安全实践';
SELECT id INTO @groupId14 from rule_group where name = 'Aliyun NAS 最佳安全实践';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0fab953a-c392-493d-9ef8-238cf5651d40', @groupId12);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0fab953a-c392-493d-9ef8-238cf5651d40', @groupId11);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8810fc51-d234-4454-836a-95a9b1dec196', @groupId13);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8810fc51-d234-4454-836a-95a9b1dec196', @groupId11);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b9ff94b8-8959-4eac-86e9-983d8e6d7db6', @groupId14);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b9ff94b8-8959-4eac-86e9-983d8e6d7db6', @groupId11);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('deede37b-2991-40b3-b8b5-089914e4cd43', @groupId14);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('deede37b-2991-40b3-b8b5-089914e4cd43', @groupId11);