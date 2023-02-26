
-- ----------------------------
-- Table cloud_event
-- ----------------------------

ALTER TABLE `cloud_event_sync_log` MODIFY column `create_time` bigint(13) DEFAULT NULL COMMENT '创建时间';

ALTER TABLE `cloud_event_sync_log` ADD `proxy_id` int(11) DEFAULT NULL COMMENT '代理ID';

ALTER TABLE `cloud_event_sync_log` ADD `region_name` varchar(1024) DEFAULT NULL COMMENT '区域名称';

ALTER TABLE `cloud_event_region_log` ADD `region_name` varchar(64) DEFAULT NULL COMMENT '区域名称';

ALTER TABLE `cloud_event` MODIFY column `event_time` bigint(13) DEFAULT NULL COMMENT '事件时间';

ALTER TABLE `cloud_event` MODIFY column `source_ip_address` varchar(64) DEFAULT NULL COMMENT '访问源地址';

ALTER TABLE `cloud_event` ADD `event_rating` int(2) DEFAULT NULL COMMENT '事件级别 0 正常 1 警告 2 事故';

ALTER TABLE `cloud_event` ADD `resource_id` varchar(64) DEFAULT NULL COMMENT '资源id';

ALTER TABLE `cloud_event` ADD `location_info` varchar(255) DEFAULT NULL COMMENT '记录本次请求出错后，问题定位所需要的辅助信息';

ALTER TABLE `cloud_event` ADD `endpoint` varchar(64) DEFAULT NULL COMMENT '云资源的详情页面';

ALTER TABLE `cloud_event` ADD `resource_url` varchar(255) DEFAULT NULL COMMENT '云资源的详情页面的访问链接';

ALTER TABLE `cloud_event` ADD `cloud_audit_event` text DEFAULT NULL COMMENT '日志详情';

ALTER TABLE `cloud_event` ADD `region_name` varchar(255) DEFAULT NULL COMMENT '区域名称';

-- ----------------------------
-- Table sbom default value
-- ----------------------------

INSERT INTO `sbom` (`id`, `name`, `description`, `create_time`, `update_time`, `creator`) VALUES ('f92948be-b15d-45a6-be6e-4ff583fb4546', 'Default', 'Default', concat(unix_timestamp(now()), '001'), concat(unix_timestamp(now()), '001'), 'admin');

INSERT INTO `sbom_version` (`id`, `sbom_id`, `name`, `description`, `create_time`, `update_time`) VALUES ('e6a4e503-fb61-4e72-ad15-ff1fbc71aff4', 'f92948be-b15d-45a6-be6e-4ff583fb4546', 'v0.1', 'v0.1', concat(unix_timestamp(now()), '001'), concat(unix_timestamp(now()), '001'));

-- ----------------------------
-- Table rule
-- ----------------------------

ALTER TABLE `rule` ADD `suggestion` varchar(255) DEFAULT NULL COMMENT '优化建议';

ALTER TABLE `cloud_task` ADD `suggestion` varchar(255) DEFAULT NULL COMMENT '优化建议';

-- ----------------------------
-- Table image
-- ----------------------------

ALTER TABLE `image` ADD `unfixed` tinyint(1) DEFAULT true COMMENT 'unfixed: true/false';

ALTER TABLE `image` ADD `serverty` varchar(255) DEFAULT '["CRITICAL", "HIGH", "MEDIUM", "LOW", "UNKNOWN"]' COMMENT 'serverty: CRITICAL,HIGH,MEDIUM,LOW,UNKNOWN';

ALTER TABLE `image_result` ADD `unfixed` tinyint(1) DEFAULT true COMMENT 'unfixed: true/false';

ALTER TABLE `image_result` ADD `serverty` varchar(255) DEFAULT '["CRITICAL", "HIGH", "MEDIUM", "LOW", "UNKNOWN"]' COMMENT 'serverty: CRITICAL,HIGH,MEDIUM,LOW,UNKNOWN';

ALTER TABLE `history_image_task` ADD `unfixed` tinyint(1) DEFAULT true COMMENT 'unfixed: true/false';

ALTER TABLE `history_image_task` ADD `serverty` varchar(255) DEFAULT '["CRITICAL", "HIGH", "MEDIUM", "LOW", "UNKNOWN"]' COMMENT 'serverty: CRITICAL,HIGH,MEDIUM,LOW,UNKNOWN';

-- ----------------------------
-- Table server
-- ----------------------------

CREATE TABLE IF NOT EXISTS `server_certificate` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `name`                       varchar(50)         DEFAULT NULL UNIQUE COMMENT '名称',
    `description`                varchar(1024)       DEFAULT NULL COMMENT '描述',
    `is_public_key`              varchar(32)         DEFAULT NULL COMMENT '密钥类型: no无密钥，file密钥文件，str密钥串',
    `password`                   varchar(256)        COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'Password',
    `public_key`                 mediumtext          DEFAULT NULL COMMENT '密钥',
    `public_key_path`            varchar(128)        DEFAULT NULL COMMENT '密钥路径',
    `creator`                    varchar(128)        DEFAULT NULL COMMENT '创建人',
    `last_modified`              bigint(14)          DEFAULT NULL COMMENT '上次更新时间',
    `proxy_id`                   int(11)             DEFAULT NULL COMMENT '代理ID',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

ALTER TABLE `server` ADD `certificate_id` varchar(50) DEFAULT NULL COMMENT 'certificate id';

ALTER TABLE `server` ADD `is_certificate` tinyint(1) DEFAULT 0 COMMENT '是否绑定凭证';

-- ----------------------------
-- Table code
-- ----------------------------

ALTER TABLE `code` ADD `serverty` varchar(255) DEFAULT '["CRITICAL", "HIGH", "MEDIUM", "LOW", "UNKNOWN"]' COMMENT 'serverty: CRITICAL,HIGH,MEDIUM,LOW,UNKNOWN';

ALTER TABLE `code_result` ADD `serverty` varchar(255) DEFAULT '["CRITICAL", "HIGH", "MEDIUM", "LOW", "UNKNOWN"]' COMMENT 'serverty: CRITICAL,HIGH,MEDIUM,LOW,UNKNOWN';

ALTER TABLE `history_code_result` ADD `serverty` varchar(255) DEFAULT '["CRITICAL", "HIGH", "MEDIUM", "LOW", "UNKNOWN"]' COMMENT 'serverty: CRITICAL,HIGH,MEDIUM,LOW,UNKNOWN';

