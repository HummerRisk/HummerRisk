

ALTER TABLE `cloud_native` ADD `kubench_status` varchar(10) DEFAULT null COMMENT 'kube-bench状态';

ALTER TABLE `cloud_native_result` ADD `kube_bench` longtext DEFAULT null COMMENT 'kube bench';

ALTER TABLE `history_cloud_native_result` ADD `kube_bench` longtext DEFAULT null COMMENT 'kube bench';

CREATE TABLE IF NOT EXISTS `cloud_native_result_kubench`
(
    `id`                         varchar(50)         NOT NULL,
    `result_id`                  varchar(50)         DEFAULT NULL COMMENT '云原生检测结果ID',
    `title`                      longtext            DEFAULT NULL COMMENT 'title',
    `number`                     varchar(50)         DEFAULT NULL COMMENT 'number',
    `severity`                   varchar(128)        DEFAULT NULL COMMENT 'severity',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `description`                longtext            DEFAULT NULL COMMENT 'description',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`, `type`) VALUES ('40dc9a85-7063-4192-8ba3-bce63f49f0f4', 'Linux 密码文件检测', 1, 'LowRisk', '查看 Linux 密码文件是否正常', 'passwdCommand=`ls -l /etc/passwd 2>/dev/null |awk \'{print $1}\'`\nshadowCommand=`ls -l /etc/passwd 2>/dev/null |awk \'{print $1}\'`\n\nif [[ ${passwdCommand} == \'-rw-r--r--\' ]];then\n echo \"HummerSuccess: passwd 文件权限正常！\"\nelse\n    echo \"HummerError: passwd 文件权限变更，不为-rw-r--r--，请及时处理！\"\nfi\nif [[ ${shadowCommand} == \'-rw-r--r--\' ]];then\n echo \"HummerSuccess: shadow 文件权限正常！\"\nelse\n    echo \"HummerError: shadow 文件权限变更，不为-rw-r--r--，请及时处理！\"\nfi', '[]', concat(unix_timestamp(now()), '001'), 1, 'linux');
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`, `type`) VALUES ('f9d4db6e-210c-4984-be55-6c2aa0622c8f', 'Linux 获取用户免密登录的公钥', 1, 'LowRisk', '查看 Linux 用户免密登录的公钥是否正常', 'sshAuth=`cat /$HOME/.ssh/authorized_keys |awk \'{print $3}\'`\nif [[ ${sshAuth} != \"\" ]];then\n   echo \"HummerError: 用户 $(whoami) 存在免密登录的证书，证书客户端名称: ${sshAuth}\"\nelse\n    echo \"HummerSuccess:，ssh 密钥检测正常！\"\nfi', '[]', concat(unix_timestamp(now()), '001'), 1, 'linux');
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`, `type`) VALUES ('78fe32cb-2307-496f-8946-a81d0359a452', 'Linux 特权组检测', 1, 'MediumRisk', '查看 Linux 特权组是否正常', 'users=`cat /etc/passwd | grep \'/bin/bash\' | awk -F: \'$4==0 {print $1}\' 2>/dev/null`\nif [[ -n ${users} ]];then \n   echo \"HummerError: 存在特权组用户 $users\"\nelse\n   echo \"HummerSuccess:，特权组用户检测正常！\"\nfi', '[]', concat(unix_timestamp(now()), '001'), 1, 'linux');
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`, `type`) VALUES ('1fc49851-3f00-49e5-9be2-6f4f24e37da4', 'Linux sudo 权限异常账户检测', 1, 'MediumRisk', '查看 Linux sudo 权限异常账户', 'users=`cat /etc/sudoers 2>/dev/null |grep -v \'#\'|grep \'ALL=(ALL)\'|awk \'{print $1}\'`\nif [[ -n ${users} ]];then\n echo \"HummerError: 用户 $users 可通过 sudo 命令获取特权!\"\nelse\n echo \"HummerSuccess: 特权用户 sudo 检测正常！\"\nfi', '[]', concat(unix_timestamp(now()), '001'), 1, 'linux');
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`, `type`) VALUES ('50876ef4-9b73-4f2d-8105-ef6658fea5cc', 'Linux 检测空口令', 1, 'LowRisk', '查看 Linux 空口令是否正常', 'users=`awk -F \":\" \'($2==\"\"){print $1}\' /etc/shadow`\nif [[ -n ${users} ]];then\n echo \"HummerError: 用户 $users 未设置密码!\"\nelse\n echo \"HummerSuccess: 用户密码检测正常！\"\nfi', '[]', concat(unix_timestamp(now()), '001'), 1, 'linux');

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('50876ef4-9b73-4f2d-8105-ef6658fea5cc', 'server');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('1fc49851-3f00-49e5-9be2-6f4f24e37da4', 'server');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('78fe32cb-2307-496f-8946-a81d0359a452', 'server');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('f9d4db6e-210c-4984-be55-6c2aa0622c8f', 'server');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('40dc9a85-7063-4192-8ba3-bce63f49f0f4', 'server');

INSERT INTO `plugin` (`id`, `name`, `icon`, `update_time`, `scan_type`, `order_`, `type`) VALUES ('hummer-ksyun-plugin', '金山云', 'ksyun.png', concat(unix_timestamp(now()), '003'), 'custodian', 22, 'cloud');

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('Ksyun 等保预检', '等保合规检查（全称为等级保护合规检查）为您提供了全面覆盖通信网络、区域边界、计算环境和管理中心的网络安全检查。', '等保三级', 'hummer-ksyun-plugin', 1);
SELECT LAST_INSERT_ID() INTO @groupId3;

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
