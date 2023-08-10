
ALTER TABLE `cloud_resource_item` ADD `hummer_name` varchar(256) DEFAULT NULL COMMENT '资源别名';

ALTER TABLE `resource_item` ADD `hummer_name` varchar(256) DEFAULT NULL COMMENT '资源别名';

CREATE TABLE IF NOT EXISTS `cloud_resource_rela` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `name`                       varchar(256)        DEFAULT 'default' COMMENT '资源名称',
    `resource_item_id`           varchar(128)        DEFAULT NULL COMMENT 'cloud resource item 标识',
    `plugin_id`                  varchar(128)        DEFAULT NULL COMMENT '云类型ID',
    `account_id`                 varchar(128)        DEFAULT NULL COMMENT '云账号ID',
    `region_id`                  varchar(128)        DEFAULT NULL COMMENT '云账号区域ID',
    `resource_type`              varchar(64)         DEFAULT NULL COMMENT '资源类型',
    `hummer_id`                  varchar(128)        DEFAULT NULL COMMENT '资源ID',
    `category`                   varchar(50)         DEFAULT NULL COMMENT '资源所属类别',
    `symbol`                     varchar(50)         DEFAULT NULL COMMENT '图标地址',
    `x_axis`                     bigint              DEFAULT NULL COMMENT '横坐标',
    `y_axis`                     bigint              DEFAULT NULL COMMENT '纵坐标',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `cloud_resource_rela_link` (
    `id`                         int                 NOT NULL AUTO_INCREMENT,
    `resource_item_id`           varchar(128)        DEFAULT NULL COMMENT 'cloud resource item 标识',
    `source`                     varchar(50)         DEFAULT NULL COMMENT '资源关系ID(源)',
    `target`                     varchar(50)         DEFAULT NULL COMMENT '资源关系ID(目标)',
    `value`                      varchar(50)         DEFAULT NULL COMMENT '资源关系描述',
    `category`                   varchar(50)         DEFAULT NULL COMMENT '资源所属类别',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

ALTER TABLE `rule_group` ADD `image_url` varchar(256) DEFAULT NULL COMMENT '图片地址';

UPDATE `rule_group` SET image_url = 'aliyun-cis.png' where level = '高风险' and plugin_id = 'hummer-aliyun-plugin';
UPDATE `rule_group` SET level = 'CIS' where image_url = 'aliyun-cis.png';
UPDATE `rule_group` SET image_url = 'aliyun-best.png' where level = '最佳实践' and plugin_id = 'hummer-aliyun-plugin';
UPDATE `rule_group` SET image_url = 'aliyun-oss.png' where level = '对象存储' and plugin_id = 'hummer-aliyun-plugin';
UPDATE `rule_group` SET image_url = 'aliyun-pro.png' where level = '等保三级' and plugin_id = 'hummer-aliyun-plugin';

UPDATE `rule_group` SET level = 'HIPAA' where name = '10.0 AWS HIPAA 安全检查' and plugin_id = 'hummer-aws-plugin';
UPDATE `rule_group` SET level = 'PCI' where name = '15.0 AWS PCI-DSS v3.2.1 安全检查' and plugin_id = 'hummer-aws-plugin';
UPDATE `rule_group` SET level = 'ISO' where name = '18.0 AWS ISO 27001 安全检查' and plugin_id = 'hummer-aws-plugin';
UPDATE `rule_group` SET level = 'SOC' where name = '21.0 AWS SOC2 安全检查' and plugin_id = 'hummer-aws-plugin';
UPDATE `rule_group` SET level = 'GDPR' where name = '9.0 AWS GDPR 安全检查' and plugin_id = 'hummer-aws-plugin';
UPDATE `rule_group` SET level = '等保三级' where level = '安全合规' and plugin_id = 'hummer-aws-plugin';
UPDATE `rule_group` SET image_url = 'aws-cis.png' where level = '高风险' and plugin_id = 'hummer-aws-plugin';
UPDATE `rule_group` SET level = 'CIS' where image_url = 'aws-cis.png';
UPDATE `rule_group` SET image_url = 'aws-oss.png' where level = '对象存储' and plugin_id = 'hummer-aws-plugin';
UPDATE `rule_group` SET image_url = 'aws-cis.png' where level = 'CIS' and plugin_id = 'hummer-aws-plugin';
UPDATE `rule_group` SET image_url = 'aws-best.png' where level = '最佳实践' and plugin_id = 'hummer-aws-plugin';
UPDATE `rule_group` SET image_url = 'aws-oss.png' where level = '对象存储' and plugin_id = 'hummer-aws-plugin';
UPDATE `rule_group` SET image_url = 'aws-pro.png' where level = '等保三级' and plugin_id = 'hummer-aws-plugin';
UPDATE `rule_group` SET image_url = 'aws-hipaa.png' where level = 'HIPAA' and plugin_id = 'hummer-aws-plugin';
UPDATE `rule_group` SET image_url = 'aws-pci.png' where level = 'PCI' and plugin_id = 'hummer-aws-plugin';
UPDATE `rule_group` SET image_url = 'aws-iso.png' where level = 'ISO' and plugin_id = 'hummer-aws-plugin';
UPDATE `rule_group` SET image_url = 'aws-soc.png' where level = 'SOC' and plugin_id = 'hummer-aws-plugin';
UPDATE `rule_group` SET image_url = 'aws-gdpr.png' where level = 'GDPR' and plugin_id = 'hummer-aws-plugin';

UPDATE `rule_group` SET image_url = 'tencent-cis.png' where level = '高风险' and plugin_id = 'hummer-qcloud-plugin';
UPDATE `rule_group` SET level = 'CIS' where image_url = 'tencent-cis.png';
UPDATE `rule_group` SET image_url = 'tencent-pro.png' where level = '等保三级' and plugin_id = 'hummer-qcloud-plugin';
UPDATE `rule_group` SET image_url = 'tencent-oss.png' where level = '对象存储' and plugin_id = 'hummer-qcloud-plugin';

UPDATE `rule_group` SET image_url = 'azure-cis.png' where level = '高风险' and plugin_id = 'hummer-azure-plugin';
UPDATE `rule_group` SET level = 'CIS' where image_url = 'azure-cis.png';
UPDATE `rule_group` SET image_url = 'azure-pro.png' where level = '等保三级' and plugin_id = 'hummer-azure-plugin';

UPDATE `rule_group` SET image_url = 'baidu-pro.png' where level = '等保三级' and plugin_id = 'hummer-baidu-plugin';
UPDATE `rule_group` SET image_url = 'baidu-oss.png' where level = '对象存储' and plugin_id = 'hummer-baidu-plugin';

UPDATE `rule_group` SET image_url = 'gcp-pro.png' where level = '等保三级' and plugin_id = 'hummer-gcp-plugin';

UPDATE `rule_group` SET image_url = 'huawei-cis.png' where level = '高风险' and plugin_id = 'hummer-huawei-plugin';
UPDATE `rule_group` SET level = 'CIS' where image_url = 'huawei-cis.png';
UPDATE `rule_group` SET image_url = 'huawei-pro.png' where level = '等保三级' and plugin_id = 'hummer-huawei-plugin';
UPDATE `rule_group` SET image_url = 'huawei-oss.png' where level = '对象存储' and plugin_id = 'hummer-huawei-plugin';

UPDATE `rule_group` SET image_url = 'huoshan-pro.png' where level = '等保三级' and plugin_id = 'hummer-huoshan-plugin';

UPDATE `rule_group` SET image_url = 'jdcloud-pro.png' where level = '等保三级' and plugin_id = 'hummer-jdcloud-plugin';
UPDATE `rule_group` SET image_url = 'jdcloud-oss.png' where level = '对象存储' and plugin_id = 'hummer-jdcloud-plugin';

UPDATE `rule_group` SET image_url = 'ksyun-pro.png' where level = '等保三级' and plugin_id = 'hummer-ksyun-plugin';

UPDATE `rule_group` SET level = '其他安全' where level = '等保三级' and plugin_id = 'hummer-openstack-plugin';
UPDATE `rule_group` SET image_url = 'openstack-other.png' where level = '其他安全' and plugin_id = 'hummer-openstack-plugin';

UPDATE `rule_group` SET image_url = 'qingcloud-pro.png' where level = '等保三级' and plugin_id = 'hummer-qingcloud-plugin';
UPDATE `rule_group` SET image_url = 'qingcloud-oss.png' where level = '对象存储' and plugin_id = 'hummer-qingcloud-plugin';

UPDATE `rule_group` SET image_url = 'qiniu-oss.png' where level = '对象存储' and plugin_id = 'hummer-qiniu-plugin';

UPDATE `rule_group` SET level = '其他安全' where level = '等保三级' and plugin_id = 'hummer-vsphere-plugin';
UPDATE `rule_group` SET image_url = 'vsphere-other.png' where level = '其他安全' and plugin_id = 'hummer-vsphere-plugin';

UPDATE `rule_group` SET image_url = 'ucloud-pro.png' where level = '等保三级' and plugin_id = 'hummer-ucloud-plugin';


ALTER TABLE `oss` ADD `account_id` varchar(64) DEFAULT NULL COMMENT '云账号ID';

DELETE FROM `rule_group_mapping` WHERE `group_id` IS NULL OR `rule_id` IS NULL;

UPDATE `rule_group` SET `name` = 'Aliyun 最佳安全实践' WHERE `name` = 'Aliyun ACK 最佳安全实践';

UPDATE `rule_group` SET `description` = '阿里云最佳安全实践，是基于众多客户上云的成功案例萃取而成的最优化企业上云指导，管理所有阿里云资源，为您提供全方位的最佳安全实践功能。' WHERE `name` = 'Aliyun 最佳安全实践';

SELECT @groupId := (SELECT id FROM `rule_group` WHERE `name` = 'Aliyun 最佳安全实践');

SELECT @groupId1 := (SELECT id FROM `rule_group` WHERE `name` = 'Aliyun RAM 最佳安全实践');

DELETE FROM `rule_group_mapping` WHERE `group_id` = @groupId1;

DELETE FROM `rule_group` WHERE `id` = @groupId1;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('70c1e701-b87a-4e4d-8648-3db7ecc2c066', @groupId);

SELECT @groupId2 := (SELECT id FROM `rule_group` WHERE `name` = 'Aliyun OSS 最佳安全实践');

DELETE FROM `rule_group_mapping` WHERE `group_id` = @groupId2;

DELETE FROM `rule_group` WHERE `id` = @groupId2;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('429f8396-e04b-49d9-8b38-80647ac87e66', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8c635fda-7f89-4d5c-b0f4-2116f1b65554', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('9d94781e-922d-48c3-90a1-393dc79f2442', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d826c85d-cb42-4824-ab13-6d7a8026d9ae', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('fdef013f-ce14-468a-9af4-1c0fabc7e6e1', @groupId);

SELECT @groupId3 := (SELECT id FROM `rule_group` WHERE `name` = 'Aliyun NAS 最佳安全实践');

DELETE FROM `rule_group_mapping` WHERE `group_id` = @groupId3;

DELETE FROM `rule_group` WHERE `id` = @groupId3;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b9ff94b8-8959-4eac-86e9-983d8e6d7db6', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('deede37b-2991-40b3-b8b5-089914e4cd43', @groupId);

SELECT @groupId4 := (SELECT id FROM `rule_group` WHERE `name` = 'Aliyun SLB 最佳安全实践');

DELETE FROM `rule_group_mapping` WHERE `group_id` = @groupId4;

DELETE FROM `rule_group` WHERE `id` = @groupId4;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1299a29b-e19d-4186-93fd-a18ed1b2584a', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2274a926-ea5e-4cdc-915e-09fa6d803bff', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('339cf3fc-f9d9-457e-ac72-40d37c402bdf', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('594e7673-c0db-40a4-9a0c-f70f0e58cc62', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('88a77028-0e2a-4201-a713-ded3a94864f9', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e28786b3-f2b1-46de-b4b6-7835b42ae58b', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e2d51fc6-2ec2-4d17-bf87-13a3df90ea5d', @groupId);

SELECT @groupId5 := (SELECT id FROM `rule_group` WHERE `name` = 'Aliyun PostgreSQL 最佳安全实践');

DELETE FROM `rule_group_mapping` WHERE `group_id` = @groupId5;

DELETE FROM `rule_group` WHERE `id` = @groupId5;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0eaec886-c769-4dfb-8efa-7933328fde0d', @groupId);

SELECT @groupId6 := (SELECT id FROM `rule_group` WHERE `name` = 'Aliyun RDS 最佳安全实践');

DELETE FROM `rule_group_mapping` WHERE `group_id` = @groupId6;

DELETE FROM `rule_group` WHERE `id` = @groupId6;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('028b8362-08f2-404c-8e15-935426bb8545', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ae65e90c-124c-4a81-8081-746d47f44e8f', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('beda16d0-93fd-4366-9ebf-f5ce1360cd60', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c95fde94-53a5-4658-98a4-56a0c6d951d4', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d690be79-2e8c-4054-bbe6-496bd29e91fe', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e054787c-5826-4242-8450-b0daa926ea40', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2adbae64-6403-4dfb-92ab-637354da49f8', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c57f055e-fd84-4af3-ba97-892a8fdc1fed', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e054787c-5826-4242-8450-b0daa926ea40', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('29763f5e-ef4c-431d-b44f-39cd1b5b5363', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ff153eea-2628-440b-b054-186d6f5a7708', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('44343a84-39e2-4fbc-b8c5-d3ac06186501', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7d323895-f07b-4845-8b3d-01c78180f270', @groupId);

SELECT @groupId7 := (SELECT id FROM `rule_group` WHERE `name` = 'Aliyun Redis 最佳安全实践');

DELETE FROM `rule_group_mapping` WHERE `group_id` = @groupId7;

DELETE FROM `rule_group` WHERE `id` = @groupId7;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('29763f5e-ef4c-431d-b44f-39cd1b5b5363', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ff153eea-2628-440b-b054-186d6f5a7708', @groupId);

SELECT @groupId8 := (SELECT id FROM `rule_group` WHERE `name` = 'Aliyun PolarDB 最佳安全实践');

DELETE FROM `rule_group_mapping` WHERE `group_id` = @groupId8;

DELETE FROM `rule_group` WHERE `id` = @groupId8;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2adbae64-6403-4dfb-92ab-637354da49f8', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('4f8fa101-171a-4491-9485-e5aa091a88a4', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c57f055e-fd84-4af3-ba97-892a8fdc1fed', @groupId);

SELECT @groupId9 := (SELECT id FROM `rule_group` WHERE `name` = 'Aliyun MSE 最佳安全实践');

DELETE FROM `rule_group_mapping` WHERE `group_id` = @groupId9;

DELETE FROM `rule_group` WHERE `id` = @groupId9;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0fab953a-c392-493d-9ef8-238cf5651d40', @groupId);

SELECT @groupId10 := (SELECT id FROM `rule_group` WHERE `name` = 'Aliyun Event 最佳安全实践');

DELETE FROM `rule_group_mapping` WHERE `group_id` = @groupId10;

DELETE FROM `rule_group` WHERE `id` = @groupId10;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d76abc8d-9975-4752-8e60-709c811d44cf', @groupId);

SELECT @groupId11 := (SELECT id FROM `rule_group` WHERE `name` = 'Aliyun ECS 最佳安全实践');

DELETE FROM `rule_group_mapping` WHERE `group_id` = @groupId11;

DELETE FROM `rule_group` WHERE `id` = @groupId11;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('3e5d47ac-86b6-40d1-a191-1b2ff2496118', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('6fd132c0-b4df-4685-b132-5441d1aef2f8', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f4ebb59b-c93a-4f34-9e66-660b03943d7d', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('3002de7d-a68b-45af-933b-3d37d63e3c57', @groupId);

SELECT @groupId12 := (SELECT id FROM `rule_group` WHERE `name` = 'Aliyun MongoDB 最佳安全实践');

DELETE FROM `rule_group_mapping` WHERE `group_id` = @groupId12;

DELETE FROM `rule_group` WHERE `id` = @groupId12;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('44343a84-39e2-4fbc-b8c5-d3ac06186501', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7d323895-f07b-4845-8b3d-01c78180f270', @groupId);

ALTER TABLE `rule_inspection_report` MODIFY column `project` varchar(512) DEFAULT NULL COMMENT '检查项目描述';
ALTER TABLE `rule_inspection_report` MODIFY column `improvement` varchar(1024) DEFAULT NULL COMMENT '改进建议';

ALTER TABLE `cloud_account` ADD `check_regions` longtext DEFAULT NULL COMMENT '选中区域';

ALTER TABLE `oss` ADD `check_regions` longtext DEFAULT NULL COMMENT '选中区域';

ALTER TABLE `cloud_task` MODIFY column `resource_types` text DEFAULT NULL COMMENT '资源类型';

ALTER TABLE `history_cloud_task` MODIFY column `resource_types` text DEFAULT NULL COMMENT '资源类型';

ALTER TABLE `cloud_resource_sync` MODIFY column `resource_types` text DEFAULT NULL COMMENT '资源类型';

CREATE TABLE IF NOT EXISTS `hummer_license` (
    `id`                         varchar(50)        NOT NULL COMMENT 'ID',
    `company`                    varchar(256)       DEFAULT NULL COMMENT '公司名称',
    `edition`                    varchar(128)       DEFAULT NULL COMMENT '企业版:Enterprise',
    `expire_time`                bigint             DEFAULT NULL COMMENT '过期时间',
    `product_type`               varchar(256)       DEFAULT NULL COMMENT '产品类型:HummerRisk',
    `authorize_count`            varchar(128)       DEFAULT NULL COMMENT '订阅授权数量:100',
    `license_key`                text               DEFAULT NULL COMMENT 'license文本',
    `create_time`                bigint             DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint             DEFAULT NULL COMMENT '修改时间',
    `apply_user`                 varchar(50)        DEFAULT NULL COMMENT '申请人',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

ALTER TABLE `hummer_license` ADD `status` varchar(50) DEFAULT NULL COMMENT 'valid、invalid、expired，分别代表：有效、无效、已过期';

ALTER TABLE `hummer_license` ADD `message` text DEFAULT NULL COMMENT 'message 提示告警信息';

Alter table `cloud_resource_rela` modify COLUMN `hummer_id` varchar(1024) DEFAULT NULL COMMENT '资源ID';

Alter table `resource_item` modify COLUMN `hummer_id` varchar(1024) DEFAULT NULL COMMENT '资源ID';

Alter table `cloud_resource_item` modify COLUMN `hummer_id` varchar(1024) DEFAULT NULL COMMENT '资源ID';

ALTER TABLE `cloud_resource_item` modify COLUMN `hummer_name` varchar(1024) DEFAULT NULL COMMENT '资源别名';

ALTER TABLE `resource_item` modify COLUMN `hummer_name` varchar(1024) DEFAULT NULL COMMENT '资源别名';

ALTER TABLE `rule_group` ADD `server_type` varchar(50) DEFAULT NULL;
