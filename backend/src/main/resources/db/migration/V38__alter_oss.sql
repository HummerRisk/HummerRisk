

UPDATE `rule_group` SET `level` = '对象存储' where `name` in ('Aliyun OSS合规基线', 'Huawei OBS合规基线', 'Tencent COS合规基线', 'AWS S3合规基线', 'Aliyun OSS 最佳安全实践');

ALTER TABLE `cloud_event` MODIFY column `user_agent` text DEFAULT NULL COMMENT '用户代理';

ALTER TABLE `cloud_event` MODIFY column `user_identity` mediumtext DEFAULT NULL COMMENT '用户认证';

ALTER TABLE `cloud_event` MODIFY column `additional_event_data` text DEFAULT NULL COMMENT '额外数据';

ALTER TABLE `cloud_event` MODIFY column `request_parameters` text DEFAULT NULL COMMENT '请求参数';

ALTER TABLE `cloud_event` MODIFY column `referenced_resources` text DEFAULT NULL COMMENT '映射的资源';

ALTER TABLE `cloud_event` MODIFY column `response_elements` text DEFAULT NULL COMMENT '响应值';
