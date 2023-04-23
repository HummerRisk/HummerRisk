
alter table `cloud_event` modify column `resource_name` varchar(128) DEFAULT NULL  COMMENT '资源名称';

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('3002de7d-a68b-45af-933b-3d37d63e3c57', 'Aliyun ECS 访问白名单检测', 1, 'HighRisk', 'Aliyun ECS 对指定端口访问设置白名单，符合视为“合规”，否则视为“不合规”。', 'policies:\n    #Aliyun ECS 对指定端口访问设置白名单，符合视为“合规”，否则视为“不合规”。\n    - name: aliyun-ecs-white-list\n      resource: aliyun.ecs\n      filters:\n        - type: white-list\n          ip: \"${{ip}}\"\n          port: \"${{port}}\"', '[{\"defaultValue\":\" 0.0.0.0/0\",\"name\":\"白名单 IP 地址，多个 IP 用“,”号分隔\",\"key\":\"ip\",\"required\":true},{\"defaultValue\":\"22,3389\",\"name\":\"端口号，多个端口用“,”号分隔\",\"key\":\"port\",\"required\":true}]', 'hummer-aliyun-plugin', '阿里云', 'aliyun.png', concat(unix_timestamp(now()), '005'), 1, 'custodian', NULL);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('3002de7d-a68b-45af-933b-3d37d63e3c57', 'safety');

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('c04eed8c-e2f2-4c3d-8370-4814f8c42fb3', '3002de7d-a68b-45af-933b-3d37d63e3c57', 'aliyun.ecs');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('3002de7d-a68b-45af-933b-3d37d63e3c57', '13');

SELECT id INTO @groupId3 FROM rule_group WHERE name = 'Aliyun Ecs 最佳安全实践';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('3002de7d-a68b-45af-933b-3d37d63e3c57', @groupId1);
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
