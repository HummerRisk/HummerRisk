
alter table `cloud_event` modify column `resource_name` varchar(128) DEFAULT NULL  COMMENT '资源名称';

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


INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`, `type`) VALUES ('Rancher 内存限制检测', '检测 Rancher 资源是否限制内存大小，资源内存限额更有利于资源进行分配，保障集群运行安全。', '安全合规', 'hummer-rancher-plugin', 1, 'k8s');

SELECT LAST_INSERT_ID() INTO @groupId;

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`, `type`) VALUES ('Rancher 容器的文件系统检测', '检测 Rancher 容器的文件系统是否为只读，如果容器应用程序需要写入文件系统，建议为应用程序需要写访问权限的特定目录挂载辅助文件系统。', '合规检测', 'hummer-rancher-plugin', 1, 'k8s');

SELECT LAST_INSERT_ID() INTO @groupId1;

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('2d08f729-f869-4b69-8772-41a8d58b5c34', 'Rancher StatefulSet 内存限制检测', 1, 'HighRisk', '检测 Rancher StatefulSet 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 Rancher StatefulSet 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。\n    - name: rancher-stateful-set\n      resource: rancher.stateful-set\n      filters:\n        - type: memory-limit\n          limit_min: ${{value1}}\n          limit_max: ${{value2}}', '[{\"defaultValue\":\"64\",\"name\":\"最小内存限制\",\"key\":\"value1\",\"required\":true},{\"defaultValue\":\"1024\",\"name\":\"最大内存限制\",\"key\":\"value2\",\"required\":true}]', 'hummer-rancher-plugin', 'Rancher', 'rancher.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('c3959d6f-d6c1-49d9-9f16-bd7f8e359667', 'Rancher Pod 内存大小限制检测', 1, 'HighRisk', '检测 Rancher Pod 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 Rancher Pod 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。\n    - name: rancher-pod-memory\n      resource: rancher.pod\n      filters:\n        - type: memory-limit\n          limit_min: ${{value1}}\n          limit_max: ${{value2}}', '[{\"key\":\"value1\",\"name\":\"最小内存限制\",\"defaultValue\":\"64\",\"required\":true},{\"key\":\"value2\",\"name\":\"最大内存限制\",\"defaultValue\":\"1024\",\"required\":true}]', 'hummer-rancher-plugin', 'Rancher', 'rancher.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('c5544cfe-074a-4947-a075-d607dc705837', 'Rancher DaemonSet 内存大小限制检测', 1, 'HighRisk', '检测 Rancher DaemonSet 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 Rancher DaemonSet 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。\n    - name: rancher-daemon-set-memory\n      resource: rancher.daemon-set\n      filters:\n        - type: memory-limit\n          limit_min: ${{value1}}\n          limit_max: ${{value2}}', '[{\"key\":\"value1\",\"name\":\"最小内存限制\",\"defaultValue\":\"64\",\"required\":true},{\"key\":\"value2\",\"name\":\"最大内存限制\",\"defaultValue\":\"1024\",\"required\":true}]', 'hummer-rancher-plugin', 'Rancher', 'rancher.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('cf01ca4c-c87a-4db6-b90a-830f6d1018ea', 'Rancher Deployment 内存限制检测', 1, 'HighRisk', '检测 Rancher Deployment 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 Rancher deployment 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。\n    - name: rancher-deployment-memory\n      resource: rancher.deployment\n      filters:\n        - type: memory-limit\n          limit_min: ${{value1}}\n          limit_max: ${{value2}}', '[{\"key\":\"value1\",\"name\":\"最小内存限制\",\"defaultValue\":\"64\",\"required\":true},{\"key\":\"value2\",\"name\":\"最大内存限制\",\"defaultValue\":\"1024\",\"required\":true}]', 'hummer-rancher-plugin', 'Rancher', 'rancher.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('fe65831e-24b1-4c5b-a08b-e0bae0d3847b', 'Rancher ReplicaSet 内存大小限制检测', 1, 'HighRisk', '检测 Rancher ReplicaSet 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 Rancher ReplicaSet 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。\n    - name: rancher-replica-set-memory\n      resource: rancher.replica-set\n      filters:\n        - type: memory-limit\n          limit_min: ${{value1}}\n          limit_max: ${{value2}}', '[{\"key\":\"value1\",\"name\":\"最小内存限制\",\"defaultValue\":\"64\",\"required\":true},{\"key\":\"value2\",\"name\":\"最大内存限制\",\"defaultValue\":\"1024\",\"required\":true}]', 'hummer-rancher-plugin', 'Rancher', 'rancher.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('0f2dae7b-da23-4c17-a77d-b2d677b25174', 'Rancher Deployment 容器的文件系统只读检测', 1, 'MediumRisk', '检测 Rancher Deployment 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 Rancher Deployment 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。\n    - name: rancher-deployment-filesystem\n      resource: rancher.deployment\n      filters:\n        - type: filesystem-read-only', '[]', 'hummer-rancher-plugin', 'Rancher', 'rancher.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('50f5f3b4-8cc1-44fa-8e59-c84b1c4250fe', 'Rancher StatefulSet 容器的文件系统只读检测', 1, 'MediumRisk', '检测 Rancher StatefulSet 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 Rancher stateful-set 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。\n    - name: rancher-stateful-set-filesystem\n      resource: rancher.stateful-set\n      filters:\n        - type: filesystem-read-only', '[]', 'hummer-rancher-plugin', 'Rancher', 'rancher.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('6adf1612-6017-4a9f-a224-e04fbb78a131', 'Rancher DaemonSet 容器的文件系统只读检测', 1, 'MediumRisk', '检测 Rancher DaemonSet 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 Rancher daemon-set 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。\n    - name: rancher-daemon-set-filesystem\n      resource: rancher.daemon-set\n      filters:\n        - type: filesystem-read-only', '[]', 'hummer-rancher-plugin', 'Rancher', 'rancher.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('8d00a470-0a18-4eb3-908e-63e0352edec1', 'Rancher ReplicaSet 容器的文件系统只读检测', 1, 'MediumRisk', '检测 Rancher ReplicaSet 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 Rancher replica-set 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。\n    - name: rancher-replica-set-filesystem\n      resource: rancher.replica-set\n      filters:\n        - type: filesystem-read-only', '[]', 'hummer-rancher-plugin', 'Rancher', 'rancher.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('988037d4-8de9-4664-8775-4fa9a4a5afca', 'Rancher Pod 容器的文件系统只读检测', 1, 'MediumRisk', '检测 Rancher Pod 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 Rancher Pod 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。\n    - name: rancher-pod-filesystem\n      resource: rancher.pod\n      filters:\n        - type: filesystem-read-only', '[]', 'hummer-rancher-plugin', 'Rancher', 'rancher.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('c3959d6f-d6c1-49d9-9f16-bd7f8e359667', 'cloud native');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('cf01ca4c-c87a-4db6-b90a-830f6d1018ea', 'cloud native');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('2d08f729-f869-4b69-8772-41a8d58b5c34', 'cloud native');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('fe65831e-24b1-4c5b-a08b-e0bae0d3847b', 'cloud native');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('c5544cfe-074a-4947-a075-d607dc705837', 'cloud native');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('988037d4-8de9-4664-8775-4fa9a4a5afca', 'cloud native');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('50f5f3b4-8cc1-44fa-8e59-c84b1c4250fe', 'cloud native');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('6adf1612-6017-4a9f-a224-e04fbb78a131', 'cloud native');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('0f2dae7b-da23-4c17-a77d-b2d677b25174', 'cloud native');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('8d00a470-0a18-4eb3-908e-63e0352edec1', 'cloud native');

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('0233018a-a9a3-4eb5-94ea-c0b1c337061f', 'c5544cfe-074a-4947-a075-d607dc705837', 'rancher.daemon-set');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('8caaeccb-1a44-44f4-84a2-8aeca4baf974', 'fe65831e-24b1-4c5b-a08b-e0bae0d3847b', 'rancher.replica-set');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('af173498-4c7b-4902-9568-faae49a1295f', '2d08f729-f869-4b69-8772-41a8d58b5c34', 'rancher.stateful-set');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('d17a7300-c0f8-4e03-8200-1e5324c13cr0', 'c3959d6f-d6c1-49d9-9f16-bd7f8e359667', 'rancher.pod');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('d6490bfc-cd35-41ac-9d7b-8723144ccc73', 'cf01ca4c-c87a-4db6-b90a-830f6d1018ea', 'rancher.deployment');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('39217066-f444-4b2f-8077-e6fa28dd69rf', '8d00a470-0a18-4eb3-908e-63e0352edec1', 'rancher.replica-set');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('3c79d1e5-6ad5-4c32-a9c1-fca2cad6bfu0', '988037d4-8de9-4664-8775-4fa9a4a5afca', 'rancher.pod');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('6e9f727e-8177-4d38-b54f-b53e55024f9b', '6adf1612-6017-4a9f-a224-e04fbb78a131', 'rancher.daemon-set');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('8e6c0f41-0fd2-469f-a76d-90e448665ad7', '50f5f3b4-8cc1-44fa-8e59-c84b1c4250fe', 'rancher.stateful-set');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('a52808fa-7d43-4aff-9d05-ab66c2108b70', '0f2dae7b-da23-4c17-a77d-b2d677b25174', 'rancher.deployment');

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c3959d6f-d6c1-49d9-9f16-bd7f8e359667', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('cf01ca4c-c87a-4db6-b90a-830f6d1018ea', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2d08f729-f869-4b69-8772-41a8d58b5c34', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('fe65831e-24b1-4c5b-a08b-e0bae0d3847b', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c5544cfe-074a-4947-a075-d607dc705837', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('988037d4-8de9-4664-8775-4fa9a4a5afca', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('50f5f3b4-8cc1-44fa-8e59-c84b1c4250fe', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('6adf1612-6017-4a9f-a224-e04fbb78a131', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0f2dae7b-da23-4c17-a77d-b2d677b25174', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8d00a470-0a18-4eb3-908e-63e0352edec1', @groupId1);

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c3959d6f-d6c1-49d9-9f16-bd7f8e359667', '105');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('cf01ca4c-c87a-4db6-b90a-830f6d1018ea', '105');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2d08f729-f869-4b69-8772-41a8d58b5c34', '105');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('fe65831e-24b1-4c5b-a08b-e0bae0d3847b', '105');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c5544cfe-074a-4947-a075-d607dc705837', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('988037d4-8de9-4664-8775-4fa9a4a5afca', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('50f5f3b4-8cc1-44fa-8e59-c84b1c4250fe', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('6adf1612-6017-4a9f-a224-e04fbb78a131', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0f2dae7b-da23-4c17-a77d-b2d677b25174', '105');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('8d00a470-0a18-4eb3-908e-63e0352edec1', '115');


INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`, `type`) VALUES ('KubeSphere 内存限制检测', '检测 KubeSphere 资源是否限制内存大小，资源内存限额更有利于资源进行分配，保障集群运行安全。', '安全合规', 'hummer-kubesphere-plugin', 1, 'k8s');

SELECT LAST_INSERT_ID() INTO @groupId;

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`, `type`) VALUES ('KubeSphere 容器的文件系统检测', '检测 KubeSphere 容器的文件系统是否为只读，如果容器应用程序需要写入文件系统，建议为应用程序需要写访问权限的特定目录挂载辅助文件系统。', '合规检测', 'hummer-kubesphere-plugin', 1, 'k8s');

SELECT LAST_INSERT_ID() INTO @groupId1;

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('1d18f729-f869-4b69-8772-41a8d58b5c34', 'KubeSphere StatefulSet 内存限制检测', 1, 'HighRisk', '检测 KubeSphere StatefulSet 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 KubeSphere StatefulSet 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。\n    - name: kubesphere-stateful-set\n      resource: kubesphere.stateful-set\n      filters:\n        - type: memory-limit\n          limit_min: ${{value1}}\n          limit_max: ${{value2}}', '[{\"defaultValue\":\"64\",\"name\":\"最小内存限制\",\"key\":\"value1\",\"required\":true},{\"defaultValue\":\"1024\",\"name\":\"最大内存限制\",\"key\":\"value2\",\"required\":true}]', 'hummer-kubesphere-plugin', 'KubeSphere', 'kubesphere.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('c1659d6f-d6c1-49d9-9f16-bd7f8e359667', 'KubeSphere Pod 内存大小限制检测', 1, 'HighRisk', '检测 KubeSphere Pod 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 KubeSphere Pod 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。\n    - name: kubesphere-pod-memory\n      resource: kubesphere.pod\n      filters:\n        - type: memory-limit\n          limit_min: ${{value1}}\n          limit_max: ${{value2}}', '[{\"key\":\"value1\",\"name\":\"最小内存限制\",\"defaultValue\":\"64\",\"required\":true},{\"key\":\"value2\",\"name\":\"最大内存限制\",\"defaultValue\":\"1024\",\"required\":true}]', 'hummer-kubesphere-plugin', 'KubeSphere', 'kubesphere.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('c2244cfe-074a-4947-a075-d607dc705837', 'KubeSphere DaemonSet 内存大小限制检测', 1, 'HighRisk', '检测 KubeSphere DaemonSet 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 KubeSphere DaemonSet 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。\n    - name: kubesphere-daemon-set-memory\n      resource: kubesphere.daemon-set\n      filters:\n        - type: memory-limit\n          limit_min: ${{value1}}\n          limit_max: ${{value2}}', '[{\"key\":\"value1\",\"name\":\"最小内存限制\",\"defaultValue\":\"64\",\"required\":true},{\"key\":\"value2\",\"name\":\"最大内存限制\",\"defaultValue\":\"1024\",\"required\":true}]', 'hummer-kubesphere-plugin', 'KubeSphere', 'kubesphere.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('cf13ca4c-c87a-4db6-b90a-830f6d1018ea', 'KubeSphere Deployment 内存限制检测', 1, 'HighRisk', '检测 KubeSphere Deployment 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 KubeSphere deployment 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。\n    - name: kubesphere-deployment-memory\n      resource: kubesphere.deployment\n      filters:\n        - type: memory-limit\n          limit_min: ${{value1}}\n          limit_max: ${{value2}}', '[{\"key\":\"value1\",\"name\":\"最小内存限制\",\"defaultValue\":\"64\",\"required\":true},{\"key\":\"value2\",\"name\":\"最大内存限制\",\"defaultValue\":\"1024\",\"required\":true}]', 'hummer-kubesphere-plugin', 'KubeSphere', 'kubesphere.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('fe56831e-24b1-4c5b-a08b-e0bae0d3847b', 'KubeSphere ReplicaSet 内存大小限制检测', 1, 'HighRisk', '检测 KubeSphere ReplicaSet 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 KubeSphere ReplicaSet 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。\n    - name: kubesphere-replica-set-memory\n      resource: kubesphere.replica-set\n      filters:\n        - type: memory-limit\n          limit_min: ${{value1}}\n          limit_max: ${{value2}}', '[{\"key\":\"value1\",\"name\":\"最小内存限制\",\"defaultValue\":\"64\",\"required\":true},{\"key\":\"value2\",\"name\":\"最大内存限制\",\"defaultValue\":\"1024\",\"required\":true}]', 'hummer-kubesphere-plugin', 'KubeSphere', 'kubesphere.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('0f4rae7b-da23-4c17-a77d-b2d677b25174', 'KubeSphere Deployment 容器的文件系统只读检测', 1, 'MediumRisk', '检测 KubeSphere Deployment 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 KubeSphere Deployment 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。\n    - name: kubesphere-deployment-filesystem\n      resource: kubesphere.deployment\n      filters:\n        - type: filesystem-read-only', '[]', 'hummer-kubesphere-plugin', 'KubeSphere', 'kubesphere.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('59t5f3b4-8cc1-44fa-8e59-c84b1c4250fe', 'KubeSphere StatefulSet 容器的文件系统只读检测', 1, 'MediumRisk', '检测 KubeSphere StatefulSet 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 KubeSphere stateful-set 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。\n    - name: kubesphere-stateful-set-filesystem\n      resource: kubesphere.stateful-set\n      filters:\n        - type: filesystem-read-only', '[]', 'hummer-kubesphere-plugin', 'KubeSphere', 'kubesphere.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('6atr1612-6017-4a9f-a224-e04fbb78a131', 'KubeSphere DaemonSet 容器的文件系统只读检测', 1, 'MediumRisk', '检测 KubeSphere DaemonSet 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 KubeSphere daemon-set 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。\n    - name: kubesphere-daemon-set-filesystem\n      resource: kubesphere.daemon-set\n      filters:\n        - type: filesystem-read-only', '[]', 'hummer-kubesphere-plugin', 'KubeSphere', 'kubesphere.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('8d78a470-0a18-4eb3-908e-63e0352edec1', 'KubeSphere ReplicaSet 容器的文件系统只读检测', 1, 'MediumRisk', '检测 KubeSphere ReplicaSet 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 KubeSphere replica-set 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。\n    - name: kubesphere-replica-set-filesystem\n      resource: kubesphere.replica-set\n      filters:\n        - type: filesystem-read-only', '[]', 'hummer-kubesphere-plugin', 'KubeSphere', 'kubesphere.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('986637d4-8de9-4664-8775-4fa9a4a5afca', 'KubeSphere Pod 容器的文件系统只读检测', 1, 'MediumRisk', '检测 KubeSphere Pod 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 KubeSphere Pod 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。\n    - name: kubesphere-pod-filesystem\n      resource: kubesphere.pod\n      filters:\n        - type: filesystem-read-only', '[]', 'hummer-kubesphere-plugin', 'KubeSphere', 'kubesphere.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('c1659d6f-d6c1-49d9-9f16-bd7f8e359667', 'cloud native');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('cf13ca4c-c87a-4db6-b90a-830f6d1018ea', 'cloud native');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('1d18f729-f869-4b69-8772-41a8d58b5c34', 'cloud native');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('fe56831e-24b1-4c5b-a08b-e0bae0d3847b', 'cloud native');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('c2244cfe-074a-4947-a075-d607dc705837', 'cloud native');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('986637d4-8de9-4664-8775-4fa9a4a5afca', 'cloud native');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('59t5f3b4-8cc1-44fa-8e59-c84b1c4250fe', 'cloud native');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('6atr1612-6017-4a9f-a224-e04fbb78a131', 'cloud native');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('0f4rae7b-da23-4c17-a77d-b2d677b25174', 'cloud native');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('8d78a470-0a18-4eb3-908e-63e0352edec1', 'cloud native');

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('0453018a-a9a3-4eb5-94ea-c0b1c337061f', 'c2244cfe-074a-4947-a075-d607dc705837', 'kubesphere.daemon-set');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('8traeccb-1a44-44f4-84a2-8aeca4baf974', 'fe56831e-24b1-4c5b-a08b-e0bae0d3847b', 'kubesphere.replica-set');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('auy73498-4c7b-4902-9568-faae49a1295f', '1d18f729-f869-4b69-8772-41a8d58b5c34', 'kubesphere.stateful-set');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('d76a7300-c0f8-4e03-8200-1e5324c13cr0', 'c1659d6f-d6c1-49d9-9f16-bd7f8e359667', 'kubesphere.pod');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('d8990bfc-cd35-41ac-9d7b-8723144ccc73', 'cf13ca4c-c87a-4db6-b90a-830f6d1018ea', 'kubesphere.deployment');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('32317066-f444-4b2f-8077-e6fa28dd69rf', '8d78a470-0a18-4eb3-908e-63e0352edec1', 'kubesphere.replica-set');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('3t59d1e5-6ad5-4c32-a9c1-fca2cad6bfu0', '986637d4-8de9-4664-8775-4fa9a4a5afca', 'kubesphere.pod');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('6y6f727e-8177-4d38-b54f-b53e55024f9b', '6atr1612-6017-4a9f-a224-e04fbb78a131', 'kubesphere.daemon-set');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('8y7c0f41-0fd2-469f-a76d-90e448665ad7', '59t5f3b4-8cc1-44fa-8e59-c84b1c4250fe', 'kubesphere.stateful-set');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('a37808fa-7d43-4aff-9d05-ab66c2108b70', '0f4rae7b-da23-4c17-a77d-b2d677b25174', 'kubesphere.deployment');

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c1659d6f-d6c1-49d9-9f16-bd7f8e359667', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('cf13ca4c-c87a-4db6-b90a-830f6d1018ea', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1d18f729-f869-4b69-8772-41a8d58b5c34', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('fe56831e-24b1-4c5b-a08b-e0bae0d3847b', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c2244cfe-074a-4947-a075-d607dc705837', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('986637d4-8de9-4664-8775-4fa9a4a5afca', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('59t5f3b4-8cc1-44fa-8e59-c84b1c4250fe', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('6atr1612-6017-4a9f-a224-e04fbb78a131', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0f4rae7b-da23-4c17-a77d-b2d677b25174', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8d78a470-0a18-4eb3-908e-63e0352edec1', @groupId1);

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c1659d6f-d6c1-49d9-9f16-bd7f8e359667', '105');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('cf13ca4c-c87a-4db6-b90a-830f6d1018ea', '105');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1d18f729-f869-4b69-8772-41a8d58b5c34', '105');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('fe56831e-24b1-4c5b-a08b-e0bae0d3847b', '105');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c2244cfe-074a-4947-a075-d607dc705837', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('986637d4-8de9-4664-8775-4fa9a4a5afca', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('59t5f3b4-8cc1-44fa-8e59-c84b1c4250fe', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('6atr1612-6017-4a9f-a224-e04fbb78a131', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0f4rae7b-da23-4c17-a77d-b2d677b25174', '105');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('8d78a470-0a18-4eb3-908e-63e0352edec1', '115');
