
CREATE TABLE IF NOT EXISTS `oss`
(
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `name`                       varchar(128)        DEFAULT NULL UNIQUE COMMENT '对象存储名称',
    `credential`                 longtext            DEFAULT NULL COMMENT '云账号凭证',
    `endpoint`                   varchar(256)        DEFAULT NULL COMMENT 'Endpoint',
    `plugin_id`                  varchar(50)         DEFAULT NULL COMMENT '插件ID',
    `plugin_name`                varchar(100)        DEFAULT NULL COMMENT '插件名称',
    `plugin_icon`                varchar(256)        DEFAULT NULL COMMENT '插件图标',
    `status`                     varchar(50)         DEFAULT NULL COMMENT '云账号状态',
    `sync_status`                varchar(50)         DEFAULT 'APPROVED' COMMENT '同步状态',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint              DEFAULT NULL COMMENT '更新时间',
    `creator`                    varchar(128)        DEFAULT NULL COMMENT '创建人',
    `regions`                    longtext            DEFAULT NULL COMMENT '区域',
    `proxy_id`                   int              DEFAULT NULL COMMENT '代理ID',
    `sum`                        int              DEFAULT 0 COMMENT '同步bucket数',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `oss_log` (
    `id`                           int                 NOT NULL AUTO_INCREMENT,
    `oss_id`                       varchar(50)         DEFAULT NULL COMMENT 'Oss ID',
    `create_time`                  bigint              DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    mediumtext          DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint             DEFAULT NULL COMMENT '结果',
    `sum`                          int              DEFAULT 0 COMMENT '同步bucket数',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `oss_bucket` (
    `id`                           varchar(50)         NOT NULL COMMENT 'ID',
    `bucket_name`                  varchar(64)         DEFAULT NULL COMMENT '存储名称',
    `storage_class`                varchar(64)         DEFAULT NULL COMMENT '存储类型',
    `location`                     varchar(80)         DEFAULT NULL COMMENT '位置',
    `create_time`                  bigint              DEFAULT NULL COMMENT '创建时间',
    `intranet_endpoint`            varchar(256)        DEFAULT NULL COMMENT '内部节点',
    `extranet_endpoint`            varchar(256)        DEFAULT NULL COMMENT '外部节点',
    `oss_id`                       varchar(50)         DEFAULT NULL COMMENT 'Oss ID',
    `owner_id`                     varchar(80)         DEFAULT NULL COMMENT '所有者id',
    `owner_name`                   varchar(80)         DEFAULT NULL COMMENT '所有者',
    `domain_name`                  varchar(500)        DEFAULT NULL COMMENT '存储桶域名',
    `size`                         varchar(50)         DEFAULT NULL COMMENT '存储用量',
    `object_number`                bigint              COLLATE utf8mb4_bin COMMENT '文件数量',
    `canned_acl`                   varchar(45)         DEFAULT NULL COMMENT '权限',
    `sync_flag`                    tinyint             DEFAULT 0 COMMENT '是否启用',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

UPDATE `rule_group` SET `level` = '对象存储' where `name` in ('Aliyun OSS合规基线', 'Huawei OBS合规基线', 'Tencent COS合规基线', 'AWS S3合规基线', 'Aliyun OSS 最佳安全实践');

ALTER TABLE `cloud_event` MODIFY column `user_agent` text DEFAULT NULL COMMENT '用户代理';

ALTER TABLE `cloud_event` MODIFY column `user_identity` mediumtext DEFAULT NULL COMMENT '用户认证';

ALTER TABLE `cloud_event` MODIFY column `additional_event_data` text DEFAULT NULL COMMENT '额外数据';

ALTER TABLE `cloud_event` MODIFY column `request_parameters` text DEFAULT NULL COMMENT '请求参数';

ALTER TABLE `cloud_event` MODIFY column `referenced_resources` text DEFAULT NULL COMMENT '映射的资源';

ALTER TABLE `cloud_event` MODIFY column `response_elements` text DEFAULT NULL COMMENT '响应值';

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('0eaec886-c769-4dfb-8efa-7933328fde0d', 'Aliyun PostgreSQL 实例网络类型检测', 1, 'HighRisk', 'Aliyun 账号下 PostgreSQL 实例已关联到 VPC；若您配置阈值，则关联的 VpcId 需存在您列出的阈值中，符合视为“合规”，否则视为“不合规”', 'policies:\n    # Aliyun 账号下 PostgreSQL 实例已关联到 VPC；若您配置阈值，则关联的 VpcId 需存在您列出的阈值中，符合视为“合规”，否则视为“不合规”。\n    - name: aliyun-postgre-sql-instance-network-type\n      resource: aliyun.postgre-sql\n      filters:\n        - type: instance-network-type\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"实例的网络类型:Classic经典网络/VPC网络\",\"defaultValue\":\"VPC\",\"required\":true}]', 'hummer-aliyun-plugin', '阿里云', 'aliyun.png', concat(unix_timestamp(now()), '001'), '1', 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('d0edb278-0df2-41fd-9032-7eac190416df', 'Aliyun PostgreSQL 实例公网访问检测', 1, 'HighRisk', 'Aliyun 检测您账号下 PostgreSQL 实例不允许任意来源公网访问，符合视为“合规”，否则视为“不合规”', 'policies:\n    # Aliyun 检测您账号下 PostgreSQL 实例不允许任意来源公网访问，符合视为“合规”，否则视为“不合规”。\n    - name: aliyun-postgre-sql-internet-access\n      resource: aliyun.postgre-sql\n      filters:\n        - type: internet-access\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"启用公网访问\",\"defaultValue\":\"true\",\"required\":true}]', 'hummer-aliyun-plugin', '阿里云', 'aliyun.png', concat(unix_timestamp(now()), '001'), '1', 'custodian', NULL);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('0eaec886-c769-4dfb-8efa-7933328fde0d', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('d0edb278-0df2-41fd-9032-7eac190416df', 'safety');

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('63fc2a4e-7fe6-4257-b38e-9f4927c3f8a0', 'd0edb278-0df2-41fd-9032-7eac190416df', 'aliyun.postgre-sql');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('a2cc4016-1ff7-4625-a337-67a4269d0f9f', '0eaec886-c769-4dfb-8efa-7933328fde0d', 'aliyun.postgre-sql');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0eaec886-c769-4dfb-8efa-7933328fde0d', '3');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0eaec886-c769-4dfb-8efa-7933328fde0d', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0eaec886-c769-4dfb-8efa-7933328fde0d', '5');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0eaec886-c769-4dfb-8efa-7933328fde0d', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0eaec886-c769-4dfb-8efa-7933328fde0d', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d0edb278-0df2-41fd-9032-7eac190416df', '97');

SELECT id INTO @groupId1 FROM rule_group WHERE name =  'Aliyun PostgreSQL 最佳安全实践';

SELECT id INTO @groupId2 FROM rule_group WHERE name =  'Aliyun 等保预检';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0eaec886-c769-4dfb-8efa-7933328fde0d', @groupId1);

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0eaec886-c769-4dfb-8efa-7933328fde0d', @groupId2);

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d0edb278-0df2-41fd-9032-7eac190416df', @groupId2);

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d0edb278-0df2-41fd-9032-7eac190416df', @groupId1);

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('1268870b-188d-41a8-a86e-0c83299dc93c', 'Qiniu Bucket 公开读取访问权限检测', 1, 'HighRisk', 'Qiniu 查看您的 存储桶是否不允许公开读取访问权限。如果某个存储桶策略允许公开读取访问权限，则该存储桶“不合规“', 'policies:\n    - name: qiniu-bucket-private\n      resource: qiniu.bucket\n      filters:\n        - type: private\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"私有状态：0 公开， 1 私有\",\"defaultValue\":\"1\",\"required\":true}]', 'hummer-qiniu-plugin', '七牛云', 'qiniu.png', concat(unix_timestamp(now()), '002'), '1', 'custodian', NULL);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('1268870b-188d-41a8-a86e-0c83299dc93c', 'safety');

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('c0cd80bf-259f-4a46-a1a8-9ce7edc5e592', '1268870b-188d-41a8-a86e-0c83299dc93c', 'qiniu.bucket');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1268870b-188d-41a8-a86e-0c83299dc93c', '10');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1268870b-188d-41a8-a86e-0c83299dc93c', '13');

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('Qiniu 对象存储合规基线', '对象存储合规检查为您提供全方位的对象存储资源检查功能。', '对象存储', 'hummer-qiniu-plugin', 1);

SELECT id INTO @groupId3 FROM rule_group WHERE name =  'Qiniu 对象存储合规基线';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1268870b-188d-41a8-a86e-0c83299dc93c', @groupId3);

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('accfd8f2-636a-4e01-8b15-18fc61afa7e3', 'Baidu BOS 存储桶加密检测', 1, 'HighRisk', 'Baidu 查看并确认您的 BOS 存储桶启用了默认加密，开启视为“合规”，未开启则该存储桶“不合规“', 'policies:\n    - name: baidu-bos-encryption\n      resource: baidu.bos\n      filters:\n        - type: encryption\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"加密算法\",\"defaultValue\":\"none\",\"required\":true}]', 'hummer-baidu-plugin', '百度云', 'baidu.png', concat(unix_timestamp(now()), '003'), '1', 'custodian', NULL);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('accfd8f2-636a-4e01-8b15-18fc61afa7e3', 'safety');

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('8140407a-9ef7-4042-9834-67c26b3fa8e8', 'accfd8f2-636a-4e01-8b15-18fc61afa7e3', 'baidu.bos');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('accfd8f2-636a-4e01-8b15-18fc61afa7e3', '53');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('accfd8f2-636a-4e01-8b15-18fc61afa7e3', '55');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('accfd8f2-636a-4e01-8b15-18fc61afa7e3', '96');

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('Baidu BOS 合规基线', 'BOS 合规检查为您提供全方位的对象存储资源检查功能。', '对象存储', 'hummer-baidu-plugin', 1);

SELECT id INTO @groupId4 FROM rule_group WHERE name =  'Baidu BOS 合规基线';

SELECT id INTO @groupId5 FROM rule_group WHERE name =  'Baidu 等保预检';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('accfd8f2-636a-4e01-8b15-18fc61afa7e3', @groupId4);

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('accfd8f2-636a-4e01-8b15-18fc61afa7e3', @groupId5);

ALTER TABLE `web_msg` ADD `result_id` longtext DEFAULT NULL COMMENT '检测结果ID';

UPDATE `rule` SET `script` = 'policies:\n    - name: qiniu-bucket-private\n      resource: qiniu.kodo\n      filters:\n        - type: private\n          value: ${{value}}' WHERE id = '1268870b-188d-41a8-a86e-0c83299dc93c';

UPDATE `rule_type` SET `resource_type` = 'qiniu.kodo' WHERE id = 'c0cd80bf-259f-4a46-a1a8-9ce7edc5e592';



CREATE TABLE IF NOT EXISTS `webhook`
(
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `name`                       varchar(128)        DEFAULT NULL UNIQUE COMMENT '名称',
    `webhook`                    varchar(1024)       DEFAULT NULL COMMENT 'webhook',
    `status`                     tinyint             DEFAULT 1 COMMENT '规则状态(启用1，停用0)',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint              DEFAULT NULL COMMENT '更新时间',
    `creator`                    varchar(128)        DEFAULT NULL COMMENT '创建人',
    `proxy_id`                   int              DEFAULT NULL COMMENT '代理ID',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- -----------------
-- cloud rule
-- -----------------

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

SELECT id INTO @groupId4 FROM rule_group WHERE name =  'Qingcloud 等保预检';

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('be1eddc5-9905-4280-8ae1-b841c68ce483', 'Qingcloud QingStor 公开读取访问权限检测', 1, 'HighRisk', 'Qingcloud 查看您的 存储桶是否不允许公开读取访问权限。如果某个存储桶策略允许公开读取访问权限，则该存储桶“不合规“', 'policies:\n    - name: qingcloud-qing-stor\n      resource: qingcloud.qingStor\n      filters:\n        - type: private', '[]', 'hummer-qingcloud-plugin', '青云', 'qingcloud.png',  concat(unix_timestamp(now()), '003'), 1, 'custodian', NULL);

INSERT INTO `rule_group_mapping` ( `rule_id`, `group_id`) VALUES ( 'be1eddc5-9905-4280-8ae1-b841c68ce483',  @groupId4 );

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('be1eddc5-9905-4280-8ae1-b841c68ce483', 'safety');

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('2e26fe00-d8e7-48d4-978b-c5e684b29be3', 'be1eddc5-9905-4280-8ae1-b841c68ce483', 'qingcloud.qingStor');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('be1eddc5-9905-4280-8ae1-b841c68ce483', '10');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('be1eddc5-9905-4280-8ae1-b841c68ce483', '13');

INSERT INTO `plugin` (`id`, `name`, `icon`, `update_time`, `scan_type`, `order_`, `type`) VALUES ('hummer-ksyun-plugin', '金山云', 'ksyun.png', concat(unix_timestamp(now()), '003'), 'custodian', 22, 'cloud');

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('Ksyun 等保预检', '等保合规检查（全称为等级保护合规检查）为您提供了全面覆盖通信网络、区域边界、计算环境和管理中心的网络安全检查。', '等保三级', 'hummer-ksyun-plugin', 1);
SELECT @groupId3 := LAST_INSERT_ID();

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('bf9cdcc9-0bb0-43e8-8881-70d9f65143c8', 'Ksyun EIP 带宽峰值检测', 1, 'HighRisk', 'Ksyun 检测您账号下的弹性 IP 实例是否达到最低带宽要求，符合视为“合规”，否则视为“不合规”', 'policies:\n    # 检测您账号下的弹性IP实例是否达到最低带宽要求\n    - name: ksyun-eip-bandwidth\n      resource: ksyun.eip\n      filters:\n        - type: bandwidth\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"按带宽计费的公网型实例的带宽峰值\",\"defaultValue\":\"10\",\"required\":true}]', 'hummer-ksyun-plugin', '金山云', 'ksyun.png', concat(unix_timestamp(now()), '003'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('792b3d7c-c8be-4e5f-b806-8397ce65fcb2', 'Ksyun KEC VPC 检测', 1, 'HighRisk', 'Ksyun 账号下的 KEC 实例指定属于哪些 VPC, 属于则合规，不属于则“不合规“', 'policies:\n  # 账号下的 kec 实例指定属于哪些 VPC, 属于则合规，不属于则“不合规“。\n  - name: ksyun-kec-vpc-type\n    resource: ksyun.kec\n    filters:\n      - type: vpc-type\n        vpcIds: [${{value1}}, ${{value2}}]', '[{\"key\":\"value1\",\"name\":\"vpcId1\",\"defaultValue\":\"\\\"vpc-1\\\"\",\"required\":true},{\"key\":\"value2\",\"name\":\"vpcId2\",\"defaultValue\":\"\\\"vpc-2\\\"\",\"required\":true}]', 'hummer-ksyun-plugin', '金山云', 'ksyun.png', concat(unix_timestamp(now()), '003'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('34f99c39-3043-4151-95b1-d7bc0e033840', 'Ksyun KEC 实例公网 IP 检测', 1, 'HighRisk', 'Ksyun KEC 实例未直接绑定公网IP，视为“合规”，否则属于“不合规”。该规则仅适用于 IPv4 协议', 'policies:\n    # kec 实例未直接绑定公网IP，视为“合规”。该规则仅适用于 IPv4 协议。\n    - name: ksyun-kec-public-ipaddress\n      resource: ksyun.kec\n      filters:\n        - type: public-ip-address', '[]', 'hummer-ksyun-plugin', '金山云', 'ksyun.png', concat(unix_timestamp(now()), '003'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('23a9d0d2-073c-4743-9a63-1d1546202fb8', 'Ksyun KEC 实例网络类型检测', 1, 'HighRisk', 'Ksyun 账号下所有 KEC 实例已关联到 VPC；若您配置阈值，则关联的 VpcId 需存在您列出的阈值中，视为“合规”，否则视为“不合规”', 'policies:\n    # 账号下所有 kec 实例已关联到 VPC；若您配置阈值，则关联的 VpcId 需存在您列出的阈值中，视为“合规”，否则视为“不合规”。\n    - name: ksyun-kec-instance-network-type\n      resource: ksyun.kec\n      filters:\n        - type: instance-network-type', '[]', 'hummer-ksyun-plugin', '金山云', 'ksyun.png', concat(unix_timestamp(now()), '003'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('50c41b4f-6ddd-4c4d-bcc3-4f98a30f8ca6', 'Ksyun SLB VPC 检测', 1, 'HighRisk', 'Ksyun 账号下 SLB 负载均衡实例指定属于哪些 VPC, 属于则合规，不属于则“不合规“', 'policies:\n  # 账号下 SLB 负载均衡实例指定属于哪些 VPC, 属于则合规，不属于则“不合规“。\n  - name: ksyun-slb-vpc-type\n    resource: ksyun.slb\n    filters:\n      - type: vpc-type\n        vpcIds: [${{value1}}, ${{value2}}]', '[{\"key\":\"value1\",\"name\":\"vpcId1\",\"defaultValue\":\"\\\"vpc-1\\\"\",\"required\":true},{\"key\":\"value2\",\"name\":\"vpcId2\",\"defaultValue\":\"\\\"vpc-2\\\"\",\"required\":true}]', 'hummer-ksyun-plugin', '金山云', 'ksyun.png', concat(unix_timestamp(now()), '003'), 1, 'custodian', NULL);

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('792b3d7c-c8be-4e5f-b806-8397ce65fcb2', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('23a9d0d2-073c-4743-9a63-1d1546202fb8', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('34f99c39-3043-4151-95b1-d7bc0e033840', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bf9cdcc9-0bb0-43e8-8881-70d9f65143c8', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('50c41b4f-6ddd-4c4d-bcc3-4f98a30f8ca6', @groupId3);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('792b3d7c-c8be-4e5f-b806-8397ce65fcb2', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('23a9d0d2-073c-4743-9a63-1d1546202fb8', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('34f99c39-3043-4151-95b1-d7bc0e033840', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('bf9cdcc9-0bb0-43e8-8881-70d9f65143c8', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('50c41b4f-6ddd-4c4d-bcc3-4f98a30f8ca6', 'safety');

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('55ebeb74-1a4c-48ed-bfaa-3b777dfb62c9', '50c41b4f-6ddd-4c4d-bcc3-4f98a30f8ca6', 'ksyun.slb');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('5b0278ac-4fd5-4bf7-a331-d450c4b9ad73', '23a9d0d2-073c-4743-9a63-1d1546202fb8', 'ksyun.kec');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('5d9ed0a4-020a-4330-b623-f9313a42a99f', '34f99c39-3043-4151-95b1-d7bc0e033840', 'ksyun.kec');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('9228d19e-9bf5-4fdd-a803-610d92b7434c', 'bf9cdcc9-0bb0-43e8-8881-70d9f65143c8', 'ksyun.eip');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('ecf78ce9-51f1-411e-85e3-b7c5e67c2077', '792b3d7c-c8be-4e5f-b806-8397ce65fcb2', 'ksyun.kec');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('792b3d7c-c8be-4e5f-b806-8397ce65fcb2', '10');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('792b3d7c-c8be-4e5f-b806-8397ce65fcb2', '13');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('792b3d7c-c8be-4e5f-b806-8397ce65fcb2', '92');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('792b3d7c-c8be-4e5f-b806-8397ce65fcb2', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('792b3d7c-c8be-4e5f-b806-8397ce65fcb2', '95');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('23a9d0d2-073c-4743-9a63-1d1546202fb8', '3');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('23a9d0d2-073c-4743-9a63-1d1546202fb8', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('23a9d0d2-073c-4743-9a63-1d1546202fb8', '5');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('23a9d0d2-073c-4743-9a63-1d1546202fb8', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('23a9d0d2-073c-4743-9a63-1d1546202fb8', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('23a9d0d2-073c-4743-9a63-1d1546202fb8', '91');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('34f99c39-3043-4151-95b1-d7bc0e033840', '18');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('bf9cdcc9-0bb0-43e8-8881-70d9f65143c8', '2');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('50c41b4f-6ddd-4c4d-bcc3-4f98a30f8ca6', '10');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('50c41b4f-6ddd-4c4d-bcc3-4f98a30f8ca6', '13');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('50c41b4f-6ddd-4c4d-bcc3-4f98a30f8ca6', '92');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('50c41b4f-6ddd-4c4d-bcc3-4f98a30f8ca6', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('50c41b4f-6ddd-4c4d-bcc3-4f98a30f8ca6', '95');


-- -----------------
-- JDCloud rule
-- -----------------
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('JDCloud OSS 合规基线', 'OSS 合规检查为您提供全方位的对象存储资源检查功能。', '对象存储', 'hummer-jdcloud-plugin', 1);
SELECT @groupIdJdOss := LAST_INSERT_ID();

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('QingCloud OSS 合规基线', 'OSS 合规检查为您提供全方位的对象存储资源检查功能。', '对象存储', 'hummer-qingcloud-plugin', 1);
SELECT @groupIdQingOss := LAST_INSERT_ID();

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('Qiniu OSS 合规基线', 'OSS 合规检查为您提供全方位的对象存储资源检查功能。', '对象存储', 'hummer-qiniu-plugin', 1);
SELECT @groupIdQiniuOss := LAST_INSERT_ID();

SELECT id INTO @groupIdJd FROM rule_group WHERE name = 'JDCloud 等保预检';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('be1eddc5-9905-4280-8ae1-b841c68ce483', @groupIdQingOss);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1268870b-188d-41a8-a86e-0c83299dc93c', @groupIdQiniuOss);

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('290cb102-7ce8-471e-8540-8c4e55da259b', 'JDCloud OSS 公开读取访问权限检测', 1, 'HighRisk', 'JDCloud 查看您的 存储桶是否不允许公开访问权限。如果某个存储桶策略允许公开访问权限，则该存储桶“不合规“', 'policies:\n    - name: jdcloud-oss-private\n      resource: jdcloud.oss\n      filters:\n        - type: private', '[]', 'hummer-jdcloud-plugin', '京东云', 'jdcloud.png', concat(unix_timestamp(now()), '002'), 1, 'custodian', NULL);

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('290cb102-7ce8-471e-8540-8c4e55da259b', @groupIdJd);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('290cb102-7ce8-471e-8540-8c4e55da259b', @groupIdJdOss);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('290cb102-7ce8-471e-8540-8c4e55da259b', 'safety');

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('b553c99b-eac5-413f-8e1c-f058590c58cc', '290cb102-7ce8-471e-8540-8c4e55da259b', 'jdcloud.oss');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('290cb102-7ce8-471e-8540-8c4e55da259b', '10');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('290cb102-7ce8-471e-8540-8c4e55da259b', '13');

ALTER TABLE `rule_group` ADD `type` varchar(64) DEFAULT 'cloud' COMMENT '规则组类别';

UPDATE `rule_group` SET `type` = 'cloud';

ALTER TABLE `cloud_task_item` modify `command` mediumtext DEFAULT NULL COMMENT 'command';

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

ALTER TABLE `cloud_event` modify column `resource_name` varchar(128) DEFAULT NULL  COMMENT '资源名称';

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('3002de7d-a68b-45af-933b-3d37d63e3c57', 'Aliyun ECS 访问白名单检测', 1, 'HighRisk', 'Aliyun ECS 对指定端口访问设置白名单，符合视为“合规”，否则视为“不合规”。', 'policies:\n    #Aliyun ECS 对指定端口访问设置白名单，符合视为“合规”，否则视为“不合规”。\n    - name: aliyun-ecs-white-list\n      resource: aliyun.ecs\n      filters:\n        - type: white-list\n          ip: \"${{ip}}\"\n          port: \"${{port}}\"', '[{\"defaultValue\":\" 0.0.0.0/0\",\"name\":\"白名单 IP 地址，多个 IP 用“,”号分隔\",\"key\":\"ip\",\"required\":true},{\"defaultValue\":\"22,3389\",\"name\":\"端口号，多个端口用“,”号分隔\",\"key\":\"port\",\"required\":true}]', 'hummer-aliyun-plugin', '阿里云', 'aliyun.png', concat(unix_timestamp(now()), '005'), 1, 'custodian', NULL);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('3002de7d-a68b-45af-933b-3d37d63e3c57', 'safety');

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('c04eed8c-e2f2-4c3d-8370-4814f8c42fb3', '3002de7d-a68b-45af-933b-3d37d63e3c57', 'aliyun.ecs');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('3002de7d-a68b-45af-933b-3d37d63e3c57', '13');

SELECT id INTO @groupId3 FROM rule_group WHERE name = 'Aliyun Ecs 最佳安全实践';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('3002de7d-a68b-45af-933b-3d37d63e3c57', @groupId3);

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0f2dae7b-da13-4c17-a77d-b2d677b25174', '105');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('50f5f3b4-8cc0-44fa-8e59-c84b1c4250fe', '105');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('6adf1612-6016-4a9f-a224-e04fbb78a131', '105');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('8d00a470-0a17-4eb3-908e-63e0352edec1', '105');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c3959d6f-d5c1-49d9-9f16-bd7f8e359667', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c5544cfe-084a-4947-a075-d607dc705837', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('cf01ca4c-c97a-4db6-b90a-830f6d1018ea', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('fe65831e-25b1-4c5b-a08b-e0bae0d3847b', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('988037d4-8de7-4664-8775-4fa9a4a5afca', '105');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2d08f728-f869-4b69-8772-41a8d58b5c34', '115');

update rule set severity = 'HighRisk' where id in ('2d08f728-f869-4b69-8772-41a8d58b5c34', 'c3959d6f-d5c1-49d9-9f16-bd7f8e359667', 'c5544cfe-084a-4947-a075-d607dc705837', 'cf01ca4c-c97a-4db6-b90a-830f6d1018ea', 'fe65831e-25b1-4c5b-a08b-e0bae0d3847b');

ALTER TABLE `rule` ADD `xpack_tag` tinyint DEFAULT 0 COMMENT 'xpack 规则标志';

update `rule` set `xpack_tag` = 0;

ALTER TABLE `rule_group` ADD `xpack_tag` tinyint DEFAULT 0 COMMENT 'xpack 规则标志';

update `rule_group` set `xpack_tag` = 0;

ALTER TABLE `server_rule` ADD `xpack_tag` tinyint DEFAULT 0 COMMENT 'xpack 规则标志';