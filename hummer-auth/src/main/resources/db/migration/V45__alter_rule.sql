
ALTER TABLE `cloud_native_result` ADD `scan_groups` mediumtext DEFAULT NULL COMMENT '检测分组："vuln", "config", "kubench"';

ALTER TABLE `server_result` modify COLUMN `is_severity` varchar(64) DEFAULT 'false' COMMENT '是否有安全风险: true, false, warn';

ALTER TABLE `history_server_result` modify COLUMN `is_severity` varchar(64) DEFAULT 'false' COMMENT '是否有安全风险: true, false, warn';

ALTER TABLE `history_cloud_native_result` ADD `return_config_sum` bigint DEFAULT 0 COMMENT '输出检测结果配置审计数';

ALTER TABLE `history_cloud_native_result` ADD `scan_groups` mediumtext DEFAULT NULL COMMENT '检测分组："vuln", "config", "kubench"';
