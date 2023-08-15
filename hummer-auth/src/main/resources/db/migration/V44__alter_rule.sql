
ALTER TABLE `image_repo` MODIFY column `password` mediumtext DEFAULT NULL COMMENT '仓库密码';

ALTER TABLE `rule_group` ADD `type` varchar(64) DEFAULT 'cloud' COMMENT '规则组类别';

INSERT INTO `plugin` ( `id`, `name`, `icon`, `update_time`, `scan_type`, `order_`, `type`) values ('hummer-server-plugin', '主机检测', 'server.png', concat(unix_timestamp(now()), '001'), 'server', 23, 'server');

UPDATE `rule_group` SET `type` = 'cloud';

ALTER TABLE `cloud_task_item` modify `command` mediumtext DEFAULT NULL COMMENT 'command';

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`, `type`) VALUES ('K8s 内存限制检测', '检测 K8s 资源是否限制内存大小，资源内存限额更有利于资源进行分配，保障集群运行安全。', '安全合规', 'hummer-k8s-plugin', 1, 'k8s');

SELECT LAST_INSERT_ID() INTO @groupId;

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`, `type`) VALUES ('K8s 容器的文件系统检测', '检测 K8s 容器的文件系统是否为只读，如果容器应用程序需要写入文件系统，建议为应用程序需要写访问权限的特定目录挂载辅助文件系统。', '合规检测', 'hummer-k8s-plugin', 1, 'k8s');

SELECT LAST_INSERT_ID() INTO @groupId1;

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('2d08f728-f869-4b69-8772-41a8d58b5c34', 'K8s StatefulSet 内存限制检测', 1, 'MediumRisk', '检测 K8s StatefulSet 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 K8s StatefulSet 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。\n    - name: k8s-stateful-set\n      resource: k8s.stateful-set\n      filters:\n        - type: memory-limit\n          limit_min: ${{value1}}\n          limit_max: ${{value2}}', '[{\"defaultValue\":\"64\",\"name\":\"最小内存限制\",\"key\":\"value1\",\"required\":true},{\"defaultValue\":\"1024\",\"name\":\"最大内存限制\",\"key\":\"value2\",\"required\":true}]', 'hummer-k8s-plugin', 'Kubernetes', 'k8s.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('c3959d6f-d5c1-49d9-9f16-bd7f8e359667', 'K8s Pod 内存大小限制检测', 1, 'MediumRisk', '检测 K8s Pod 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 K8s Pod 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。\n    - name: k8s-pod-memory\n      resource: k8s.pod\n      filters:\n        - type: memory-limit\n          limit_min: ${{value1}}\n          limit_max: ${{value2}}', '[{\"key\":\"value1\",\"name\":\"最小内存限制\",\"defaultValue\":\"64\",\"required\":true},{\"key\":\"value2\",\"name\":\"最大内存限制\",\"defaultValue\":\"1024\",\"required\":true}]', 'hummer-k8s-plugin', 'Kubernetes', 'k8s.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('c5544cfe-084a-4947-a075-d607dc705837', 'K8s DaemonSet 内存大小限制检测', 1, 'MediumRisk', '检测 K8s DaemonSet 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 K8s DaemonSet 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。\n    - name: k8s-daemon-set-memory\n      resource: k8s.daemon-set\n      filters:\n        - type: memory-limit\n          limit_min: ${{value1}}\n          limit_max: ${{value2}}', '[{\"key\":\"value1\",\"name\":\"最小内存限制\",\"defaultValue\":\"64\",\"required\":true},{\"key\":\"value2\",\"name\":\"最大内存限制\",\"defaultValue\":\"1024\",\"required\":true}]', 'hummer-k8s-plugin', 'Kubernetes', 'k8s.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('cf01ca4c-c97a-4db6-b90a-830f6d1018ea', 'K8s Deployment 内存限制检测', 1, 'MediumRisk', '检测 K8s Deployment 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 K8s deployment 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。\n    - name: k8s-deployment-memory\n      resource: k8s.deployment\n      filters:\n        - type: memory-limit\n          limit_min: ${{value1}}\n          limit_max: ${{value2}}', '[{\"key\":\"value1\",\"name\":\"最小内存限制\",\"defaultValue\":\"64\",\"required\":true},{\"key\":\"value2\",\"name\":\"最大内存限制\",\"defaultValue\":\"1024\",\"required\":true}]', 'hummer-k8s-plugin', 'Kubernetes', 'k8s.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('fe65831e-25b1-4c5b-a08b-e0bae0d3847b', 'K8s ReplicaSet 内存大小限制检测', 1, 'MediumRisk', '检测 K8s ReplicaSet 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 K8s ReplicaSet 限制内存大小在64-1024MB，在限制大小范围内视为“合规”，否则视为“不合规”。\n    - name: k8s-replica-set-memory\n      resource: k8s.replica-set\n      filters:\n        - type: memory-limit\n          limit_min: ${{value1}}\n          limit_max: ${{value2}}', '[{\"key\":\"value1\",\"name\":\"最小内存限制\",\"defaultValue\":\"64\",\"required\":true},{\"key\":\"value2\",\"name\":\"最大内存限制\",\"defaultValue\":\"1024\",\"required\":true}]', 'hummer-k8s-plugin', 'Kubernetes', 'k8s.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('0f2dae7b-da13-4c17-a77d-b2d677b25174', 'K8s Deployment 容器的文件系统只读检测', 1, 'MediumRisk', '检测 K8s Deployment 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 K8s Deployment 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。\n    - name: k8s-deployment-filesystem\n      resource: k8s.deployment\n      filters:\n        - type: filesystem-read-only', '[]', 'hummer-k8s-plugin', 'Kubernetes', 'k8s.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('50f5f3b4-8cc0-44fa-8e59-c84b1c4250fe', 'K8s StatefulSet 容器的文件系统只读检测', 1, 'MediumRisk', '检测 K8s StatefulSet 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 K8s stateful-set 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。\n    - name: k8s-stateful-set-filesystem\n      resource: k8s.stateful-set\n      filters:\n        - type: filesystem-read-only', '[]', 'hummer-k8s-plugin', 'Kubernetes', 'k8s.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('6adf1612-6016-4a9f-a224-e04fbb78a131', 'K8s DaemonSet 容器的文件系统只读检测', 1, 'MediumRisk', '检测 K8s DaemonSet 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 K8s daemon-set 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。\n    - name: k8s-daemon-set-filesystem\n      resource: k8s.daemon-set\n      filters:\n        - type: filesystem-read-only', '[]', 'hummer-k8s-plugin', 'Kubernetes', 'k8s.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('8d00a470-0a17-4eb3-908e-63e0352edec1', 'K8s ReplicaSet 容器的文件系统只读检测', 1, 'MediumRisk', '检测 K8s ReplicaSet 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 K8s replica-set 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。\n    - name: k8s-replica-set-filesystem\n      resource: k8s.replica-set\n      filters:\n        - type: filesystem-read-only', '[]', 'hummer-k8s-plugin', 'Kubernetes', 'k8s.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('988037d4-8de7-4664-8775-4fa9a4a5afca', 'K8s Pod 容器的文件系统只读检测', 1, 'MediumRisk', '检测 K8s Pod 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。', 'policies:\n    # 检测 K8s Pod 容器的文件系统是否只读，只读视为“合规”，否则视为“不合规”。\n    - name: k8s-pod-filesystem\n      resource: k8s.pod\n      filters:\n        - type: filesystem-read-only', '[]', 'hummer-k8s-plugin', 'Kubernetes', 'k8s.png', concat(unix_timestamp(now()), '004'), 1, 'custodian', NULL);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('c3959d6f-d5c1-49d9-9f16-bd7f8e359667', 'cloud native');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('cf01ca4c-c97a-4db6-b90a-830f6d1018ea', 'cloud native');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('2d08f728-f869-4b69-8772-41a8d58b5c34', 'cloud native');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('fe65831e-25b1-4c5b-a08b-e0bae0d3847b', 'cloud native');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('c5544cfe-084a-4947-a075-d607dc705837', 'cloud native');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('988037d4-8de7-4664-8775-4fa9a4a5afca', 'cloud native');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('50f5f3b4-8cc0-44fa-8e59-c84b1c4250fe', 'cloud native');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('6adf1612-6016-4a9f-a224-e04fbb78a131', 'cloud native');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('0f2dae7b-da13-4c17-a77d-b2d677b25174', 'cloud native');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('8d00a470-0a17-4eb3-908e-63e0352edec1', 'cloud native');

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('0233018a-a9a3-4eb5-94ea-c0b1c337062f', 'c5544cfe-084a-4947-a075-d607dc705837', 'k8s.daemon-set');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('8caaeccb-1a44-44f4-84a2-8aeca4baf984', 'fe65831e-25b1-4c5b-a08b-e0bae0d3847b', 'k8s.replica-set');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('af173498-4c7b-4902-9568-faae49a1296f', '2d08f728-f869-4b69-8772-41a8d58b5c34', 'k8s.stateful-set');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('d17a7300-c0f8-4e03-8200-1e5324c13cd0', 'c3959d6f-d5c1-49d9-9f16-bd7f8e359667', 'k8s.pod');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('d6490bfc-cd35-41ac-9d7b-8723144ccc43', 'cf01ca4c-c97a-4db6-b90a-830f6d1018ea', 'k8s.deployment');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('39217066-f444-4b2f-8077-e6fa28dd69df', '8d00a470-0a17-4eb3-908e-63e0352edec1', 'k8s.replica-set');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('3c79d1e5-6ad5-4c32-a9c1-fca2cad6bfd0', '988037d4-8de7-4664-8775-4fa9a4a5afca', 'k8s.pod');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('6e9f727e-8177-4d38-b54f-b53e55024f3b', '6adf1612-6016-4a9f-a224-e04fbb78a131', 'k8s.daemon-set');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('8e6c0f41-0fd2-469f-a76d-90e448665ac7', '50f5f3b4-8cc0-44fa-8e59-c84b1c4250fe', 'k8s.stateful-set');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('a52808fa-7d43-4aff-9d05-ab66c2108b90', '0f2dae7b-da13-4c17-a77d-b2d677b25174', 'k8s.deployment');

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c3959d6f-d5c1-49d9-9f16-bd7f8e359667', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('cf01ca4c-c97a-4db6-b90a-830f6d1018ea', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2d08f728-f869-4b69-8772-41a8d58b5c34', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('fe65831e-25b1-4c5b-a08b-e0bae0d3847b', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c5544cfe-084a-4947-a075-d607dc705837', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('988037d4-8de7-4664-8775-4fa9a4a5afca', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('50f5f3b4-8cc0-44fa-8e59-c84b1c4250fe', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('6adf1612-6016-4a9f-a224-e04fbb78a131', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0f2dae7b-da13-4c17-a77d-b2d677b25174', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8d00a470-0a17-4eb3-908e-63e0352edec1', @groupId1);


INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`, `type`) VALUES ('Linux 认证相关的配置检测', '用于 Linux 认证相关的配置检测，通过认证检测可以帮助您针对 Linux 系统用户认证、应用服务认证、密码策略等进行多维度的认证安全检测。', '认证检测', 'hummer-server-plugin', 1, 'server');

SELECT LAST_INSERT_ID() INTO @groupId2;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0487f316-9cd8-46f7-86e9-31f1ab8b504a', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a9c11f7e-ba44-416e-bbfa-cd4d96f8165f', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('fdf1c113-d1dc-4aea-877d-ea0001939c3c', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c0f065e4-f205-4b1d-9d1d-a037f9e31e9d', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e95c36e7-b968-4606-8217-a954a3ace2f5', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('15370452-3ce3-429c-8743-4a344baddb26', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0cfdd71d-f4c0-42b0-a8a7-af8760640c8e', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('40dc9a85-7063-4192-8ba3-bce63f49f0f4', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('78fe32cb-2307-496f-8946-a81d0359a452', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f9d4db6e-210c-4984-be55-6c2aa0622c8f', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5319e63e-27ac-4899-94e0-7bd9ac0e8740', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('29bac3a8-6cde-49a8-968e-45745d917a08', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7f742d41-d91d-4597-8462-25a89f8841e0', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1fc49851-3f00-49e5-9be2-6f4f24e37da4', @groupId2);

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`, `type`) VALUES ('Linux 网络的配置检测', '用于 Linux 网络的配置检测，包括 DNS 、Hosts、IP 伪装、防火墙配置等进行多维度的网络安全检测，通过网络配置检测提升您主机网络的安全性。', '网络检测', 'hummer-server-plugin', 1, 'server');

SELECT LAST_INSERT_ID() INTO @groupId3;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e0ba707c-4a08-490e-93fb-1e98b1db0b3b', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c43ab09e-0c6c-4b2e-866d-3426477a6f3c', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f7f08183-3972-4e8e-ba54-9e0cf64074d5', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7a9cfc62-3c78-4f08-8210-d9bbfe47980c', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('10e326fb-8a12-4e4b-9f02-e2ea0e3c7e82', @groupId3);

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`, `type`) VALUES ('Linux 服务相关配置的检测', '用于 Linux 服务相关配置的检测，您可以使用该检测功能对一些常见服务进行端口、配置的检测，以降低系统风险。', '服务检测', 'hummer-server-plugin', 1, 'server');

SELECT LAST_INSERT_ID() INTO @groupId4;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('27558ffd-a826-4b5c-bc4d-d725ec8dad58', @groupId4);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('842b70d6-255d-46f3-a6da-58234f6df63f', @groupId4);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0f91f89e-d39b-4a6e-8311-1070ae2bc85c', @groupId4);

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`, `type`) VALUES ('Linux 系统配置的检测', '用于 Linux 系统配置的检测，通过针对系统运行状态、系统参数配置、日志、进程等进行多纬度的配置，从而提升您的系统安全性。', '系统检测', 'hummer-server-plugin', 1, 'server');

SELECT LAST_INSERT_ID() INTO @groupId5;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('73fdb432-62c4-4b08-9682-44dae7e6c312', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a1c9a8d3-97d2-4556-abdb-5d77ea46bad5', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0f91f89e-d39b-4a6e-8311-1070ae2bc85c', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f00222ed-4a39-43be-b2a8-5b5b72529296', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5319e63e-27ac-4899-94e0-7bd9ac0e8740', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5ca75854-b03c-4e9f-9ec7-4bf77f16d94d', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7f742d41-d91d-4597-8462-25a89f8841e0', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bb389762-8756-405d-b37c-fc789d1cc9cc', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('161b15b6-b5e3-420d-b0fc-de172553df74', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c43ab09e-0c6c-4b2e-866d-3426477a6f3c', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('6970cdce-fd54-4853-9f89-9e70b43aa07e', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e0ba707c-4a08-490e-93fb-1e98b1db0b3b', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1fc49851-3f00-49e5-9be2-6f4f24e37da4', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('11c940a7-24e0-4f9d-a9a9-9e6811b12ad6', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c497a676-12ec-41ed-8944-eb32cc95b8a4', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('562fd271-6484-42db-b72b-08d679cb4e03', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('17d8a321-4eb2-41cc-a466-e29293dc8755', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('fdf46bee-0ae7-416f-85c8-b17c8dfa08ba', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('20f58992-1d1b-448d-ad85-b80434490f54', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('18365786-1f24-4a42-b5a5-291b9274205a', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1feaf989-836f-4f90-975d-8caac4b18692', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('40dc9a85-7063-4192-8ba3-bce63f49f0f4', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0cfdd71d-f4c0-42b0-a8a7-af8760640c8e', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('26cf695f-fca6-4097-bf02-f9cb30f79c0d', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ba903623-9d68-4939-ba39-fa34628c2d1d', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f45cc8e2-867f-4e97-8998-ce7f0c070d2d', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('27558ffd-a826-4b5c-bc4d-d725ec8dad58', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e95c36e7-b968-4606-8217-a954a3ace2f5', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('842b70d6-255d-46f3-a6da-58234f6df63f', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f7f08183-3972-4e8e-ba54-9e0cf64074d5', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('fdf1c113-d1dc-4aea-877d-ea0001939c3c', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('50876ef4-9b73-4f2d-8105-ef6658fea5cc', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('9eacb3a6-6129-44b9-b29a-c07ad449b063', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('78fe32cb-2307-496f-8946-a81d0359a452', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('98122047-afd0-4b12-8bfd-7a13ee177f72', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('6f378375-bbe2-47f7-85fc-4a8e8c8193c6', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c0f065e4-f205-4b1d-9d1d-a037f9e31e9d', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('29bac3a8-6cde-49a8-968e-45745d917a08', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('15370452-3ce3-429c-8743-4a344baddb26', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f9d4db6e-210c-4984-be55-6c2aa0622c8f', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a9c11f7e-ba44-416e-bbfa-cd4d96f8165f', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('87ecea8f-3ba1-48d0-8fcd-b3d74e1dae60', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7a9cfc62-3c78-4f08-8210-d9bbfe47980c', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0487f316-9cd8-46f7-86e9-31f1ab8b504a', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('10e326fb-8a12-4e4b-9f02-e2ea0e3c7e82', @groupId5);

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`, `type`) VALUES ('Windows 认证相关的配置检测', '用于 Windows 认证相关的配置检测，针对 Windows 系统用户认证、应用服务认证、密码策略等进行多维度的认证安全检测。', '认证检测', 'hummer-server-plugin', 1, 'server');

SELECT LAST_INSERT_ID() INTO @groupId6;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b38f7d87-b59b-416c-9dba-dddd799c8c57', @groupId6);

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`, `type`) VALUES ('Windows 系统配置的检测', '用于 Windows 系统配置的检测，通过针对系统运行状态、系统参数配置、日志、进程等进行多纬度的配置，从而提升您主机的网络安全性。', '系统检测', 'hummer-server-plugin', 1, 'server');

SELECT LAST_INSERT_ID() INTO @groupId7;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('512c3f52-afe6-4bfe-b857-430dcb1c70de', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('733432f2-bc91-4801-8d80-fb92a4449857', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('4c58a33d-2a35-4bce-a17a-50616d3c1e38', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7b8c8fd0-d1d2-47c5-9434-3e637eb86381', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c1d590be-43df-4dbf-a103-470346f05a99', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0cb6ad8c-7e5c-4b60-9ea2-1c68b5f1f98d', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('cfeeaf24-6fa1-4335-a8c5-bbcff7a81b71', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7d78d7c2-1c84-49f1-95e7-30b62000b4ec', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('4a29c864-ca1c-468f-88a3-1b680a6d8c36', @groupId7);

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`, `type`) VALUES ('Windows 服务相关配置的检测', '用于 Windows 服务相关配置的检测，对于一些常见服务进行端口、配置的检测，以降低系统风险，从而提升您主机的网络安全性。', '服务检测', 'hummer-server-plugin', 1, 'server');

SELECT LAST_INSERT_ID() INTO @groupId8;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('6793a321-ef5b-4868-b8a6-d901fe4237c8', @groupId8);
