
ALTER TABLE `cloud_resource_item` ADD `hummer_name` varchar(256) DEFAULT NULL COMMENT '资源别名';

ALTER TABLE `resource_item` ADD `hummer_name` varchar(256) DEFAULT NULL COMMENT '资源别名';

CREATE TABLE IF NOT EXISTS `cloud_resource_rela` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `name`                       varchar(256)        DEFAULT NULL COMMENT '资源名称',
    `resource_item_id`           varchar(128)        DEFAULT NULL COMMENT 'cloud resource item 标识',
    `plugin_id`                  varchar(128)        DEFAULT NULL COMMENT '云类型ID',
    `account_id`                 varchar(128)        DEFAULT NULL COMMENT '云账号ID',
    `region_id`                  varchar(128)        DEFAULT NULL COMMENT '云账号区域ID',
    `resource_type`              varchar(64)         DEFAULT NULL COMMENT '资源类型',
    `hummer_id`                  varchar(128)        DEFAULT NULL COMMENT '资源ID',
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
UPDATE `rule_group` SET image_url = 'vsphere-pro.other' where level = '其他安全' and plugin_id = 'hummer-vsphere-plugin';

UPDATE `rule_group` SET image_url = 'ucloud-pro.png' where level = '等保三级' and plugin_id = 'hummer-ucloud-plugin';

UPDATE `rule_group` SET image_url = 'server-best.png' where level = '最佳实践' and plugin_id = 'hummer-server-plugin';
UPDATE `rule_group` SET level = '其他安全' where level = '服务检测' and plugin_id = 'hummer-server-plugin';
UPDATE `rule_group` SET level = '其他安全' where level = '系统检测' and plugin_id = 'hummer-server-plugin';
UPDATE `rule_group` SET level = '安全合规' where level = '网络检测' and plugin_id = 'hummer-server-plugin';
UPDATE `rule_group` SET level = '安全合规' where level = '认证检测' and plugin_id = 'hummer-server-plugin';
UPDATE `rule_group` SET image_url = 'server-safety.png' where level = '其他安全' and plugin_id = 'hummer-server-plugin';
UPDATE `rule_group` SET image_url = 'server-compliance.png' where level = '安全合规' and plugin_id = 'hummer-server-plugin';

UPDATE `rule_group` SET level = '其他安全' where level = '合规检测' and plugin_id = 'hummer-k8s-plugin';
UPDATE `rule_group` SET level = '其他安全' where level = '合规检查' and plugin_id = 'hummer-k8s-plugin';
UPDATE `rule_group` SET level = '最佳实践' where level = '安全合规' and plugin_id = 'hummer-k8s-plugin';
UPDATE `rule_group` SET image_url = 'k8s-other.png' where level = '其他安全' and plugin_id = 'hummer-k8s-plugin';
UPDATE `rule_group` SET image_url = 'k8s-best.png' where level = '最佳实践' and plugin_id = 'hummer-k8s-plugin';

UPDATE `rule_group` SET level = '其他安全' where level = '合规检测' and plugin_id = 'hummer-rancher-plugin';
UPDATE `rule_group` SET level = '其他安全' where level = '合规检查' and plugin_id = 'hummer-rancher-plugin';
UPDATE `rule_group` SET level = '最佳实践' where level = '安全合规' and plugin_id = 'hummer-rancher-plugin';
UPDATE `rule_group` SET image_url = 'rancher-other.png' where level = '其他安全' and plugin_id = 'hummer-rancher-plugin';
UPDATE `rule_group` SET image_url = 'rancher-best.png' where level = '最佳实践' and plugin_id = 'hummer-rancher-plugin';

UPDATE `rule_group` SET level = '其他安全' where level = '合规检测' and plugin_id = 'hummer-kubesphere-plugin';
UPDATE `rule_group` SET level = '其他安全' where level = '合规检查' and plugin_id = 'hummer-kubesphere-plugin';
UPDATE `rule_group` SET level = '最佳实践' where level = '安全合规' and plugin_id = 'hummer-kubesphere-plugin';
UPDATE `rule_group` SET image_url = 'kubesphere-other.png' where level = '其他安全' and plugin_id = 'hummer-kubesphere-plugin';
UPDATE `rule_group` SET image_url = 'kubesphere-best.png' where level = '最佳实践' and plugin_id = 'hummer-kubesphere-plugin';

UPDATE `rule_group` SET level = '其他安全' where level = '合规检测' and plugin_id = 'hummer-openshift-plugin';
UPDATE `rule_group` SET level = '其他安全' where level = '合规检查' and plugin_id = 'hummer-openshift-plugin';
UPDATE `rule_group` SET level = '最佳实践' where level = '安全合规' and plugin_id = 'hummer-openshift-plugin';
UPDATE `rule_group` SET image_url = 'openshift-other.png' where level = '其他安全' and plugin_id = 'hummer-openshift-plugin';
UPDATE `rule_group` SET image_url = 'openshift-best.png' where level = '最佳实践' and plugin_id = 'hummer-openshift-plugin';
