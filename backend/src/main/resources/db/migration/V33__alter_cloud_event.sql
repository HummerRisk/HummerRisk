
ALTER TABLE `cloud_event_sync_log` MODIFY column `create_time` bigint(13) DEFAULT NULL COMMENT '创建时间';

ALTER TABLE `cloud_event` MODIFY column `event_time` bigint(13) DEFAULT NULL COMMENT '事件时间';

ALTER TABLE `cloud_event` MODIFY column `source_ip_address` varchar(64) DEFAULT NULL COMMENT '访问源地址';

ALTER TABLE `cloud_event` ADD `event_rating` int(2) DEFAULT NULL COMMENT '事件级别 0 正常 1 警告 3 事故';

ALTER TABLE `cloud_event` ADD `resource_id` varchar(64) DEFAULT NULL COMMENT '资源id';

ALTER TABLE `cloud_event` ADD `location_info` varchar(255) DEFAULT NULL COMMENT '记录本次请求出错后，问题定位所需要的辅助信息';

ALTER TABLE `cloud_event` ADD `endpoint` varchar(64) DEFAULT NULL COMMENT '云资源的详情页面';

ALTER TABLE `cloud_event` ADD `resource_url` varchar(255) DEFAULT NULL COMMENT '云资源的详情页面的访问链接';

ALTER TABLE `cloud_event` ADD `cloud_audit_event` text DEFAULT NULL COMMENT '日志详情';

