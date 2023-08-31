
CREATE TABLE IF NOT EXISTS `cloud_project` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `status`                     varchar(20)         DEFAULT NULL COMMENT '状态',
    `account_name`               varchar(256)        DEFAULT NULL COMMENT '云账号名称',
    `account_id`                 varchar(50)         DEFAULT NULL COMMENT '云账号标识',
    `plugin_id`                  varchar(128)        DEFAULT NULL COMMENT '云类型ID',
    `plugin_name`                varchar(128)        DEFAULT NULL COMMENT '插件名称',
    `plugin_icon`                varchar(256)        DEFAULT NULL COMMENT '插件图标',
    `creator`                    varchar(50)         DEFAULT NULL COMMENT '创建人',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `resources_sum`              bigint              DEFAULT 0 COMMENT '检测结果资源总量',
    `return_sum`                 bigint              DEFAULT 0 COMMENT '检测结果风险资源数',
    `job_type`                   varchar(50)         DEFAULT 'once' COMMENT '任务类型：once/cron',
    `xxl_job_id`                 int                 DEFAULT 0 COMMENT '定时任务ID',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `cloud_project_log` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `project_id`                 varchar(128)        DEFAULT NULL COMMENT 'cloud project 标识',
    `init_time`                  varchar(50)         DEFAULT NULL COMMENT '初始化时间',
    `exec_time`                  varchar(50)         DEFAULT NULL COMMENT '执行完成时间',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `operator`                   varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                  mediumtext          DEFAULT NULL COMMENT '操作内容',
    `output`                     mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                     tinyint             DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `cloud_group` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `project_id`                 varchar(128)        DEFAULT NULL COMMENT 'cloud project 标识',
    `group_id`                   int                 DEFAULT NULL COMMENT 'rule group 标识',
    `group_name`                 varchar(256)        DEFAULT NULL COMMENT '规则组名称',
    `group_desc`                 varchar(1024)       DEFAULT NULL COMMENT '规则组描述',
    `group_level`                varchar(64)         DEFAULT NULL COMMENT '风险级别',
    `group_flag`                 tinyint             DEFAULT 0 COMMENT '是否内置',
    `status`                     varchar(20)         DEFAULT NULL COMMENT '状态',
    `account_name`               varchar(256)        DEFAULT NULL COMMENT '云账号名称',
    `account_id`                 varchar(50)         DEFAULT NULL COMMENT '云账号标识',
    `plugin_id`                  varchar(128)        DEFAULT NULL COMMENT '云类型ID',
    `plugin_name`                varchar(128)        DEFAULT NULL COMMENT '插件名称',
    `plugin_icon`                varchar(256)        DEFAULT NULL COMMENT '插件图标',
    `creator`                    varchar(50)         DEFAULT NULL COMMENT '创建人',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `resources_sum`              bigint              DEFAULT 0 COMMENT '检测结果资源总量',
    `return_sum`                 bigint              DEFAULT 0 COMMENT '检测结果风险资源数',
    `job_type`                   varchar(50)         DEFAULT 'once' COMMENT '任务类型：once/cron',
    `xxl_job_id`                 int                 DEFAULT 0 COMMENT '定时任务ID',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `cloud_group_log` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `project_id`                 varchar(128)        DEFAULT NULL COMMENT 'cloud project 标识',
    `group_id`                   varchar(128)        DEFAULT NULL COMMENT 'cloud group 标识',
    `init_time`                  varchar(50)         DEFAULT NULL COMMENT '初始化时间',
    `exec_time`                  varchar(50)         DEFAULT NULL COMMENT '执行完成时间',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `operator`                   varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                  mediumtext          DEFAULT NULL COMMENT '操作内容',
    `output`                     mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                     tinyint             DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `cloud_process` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `project_id`                 varchar(128)        DEFAULT NULL COMMENT 'cloud project 标识',
    `process_step`               int                 DEFAULT 0 COMMENT '执行初始化步骤',
    `process_order`              int                 DEFAULT 0 COMMENT '执行初始化排序',
    `process_name`               varchar(256)        DEFAULT NULL COMMENT '执行步骤名称',
    `process_rate`               int                 DEFAULT 0 COMMENT '执行进度',
    `status`                     varchar(20)         DEFAULT NULL COMMENT '状态',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `exec_time`                  int                 DEFAULT NULL COMMENT '执行完成时间(秒)',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `cloud_process_log` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `project_id`                 varchar(128)        DEFAULT NULL COMMENT 'cloud project 标识',
    `process_id`                 varchar(128)        DEFAULT NULL COMMENT 'cloud process 标识',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `operator`                   varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                  mediumtext          DEFAULT NULL COMMENT '操作内容',
    `output`                     mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                     tinyint             DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

Alter table `cloud_resource_rela` modify COLUMN `hummer_id` varchar(1024) DEFAULT NULL COMMENT '资源ID';

Alter table `resource_item` modify COLUMN `hummer_id` varchar(1024) DEFAULT NULL COMMENT '资源ID';

Alter table `cloud_resource_item` modify COLUMN `hummer_id` varchar(1024) DEFAULT NULL COMMENT '资源ID';

ALTER TABLE `cloud_resource_item` modify COLUMN `hummer_name` varchar(1024) DEFAULT NULL COMMENT '资源别名';

ALTER TABLE `resource_item` modify COLUMN `hummer_name` varchar(1024) DEFAULT NULL COMMENT '资源别名';

ALTER TABLE `cloud_task` add `project_id` varchar(128) DEFAULT NULL COMMENT 'cloud project 标识';

ALTER TABLE `cloud_task` add `group_id` varchar(128) DEFAULT NULL COMMENT 'cloud group 标识';

ALTER TABLE `cloud_resource_item` add `resource_type_name` varchar(128) DEFAULT NULL COMMENT '资源类型中文名称';

ALTER TABLE `cloud_resource_item` add `resource_type_belong` varchar(128) DEFAULT NULL COMMENT '资源类型所属大类';

ALTER TABLE `resource_item` add `resource_type_name` varchar(128) DEFAULT NULL COMMENT '资源类型中文名称';

ALTER TABLE `resource_item` add `resource_type_belong` varchar(128) DEFAULT NULL COMMENT '资源类型所属大类';

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`, `xpack_tag`) VALUES ('7c08c80f-007a-4796-a5e7-0562867a5a1b', 'Aliyun SLB 实例流量监控检测', 1, 'HighRisk', 'Aliyun SLB在指定时间范围内，流量监控低于某值(单位：bit/s),符合条件视为”不合规“，否则视为”合规“', 'policies:\n  # SLB在指定时间范围内，流量监控低于某值(单位：bit/s),符合条件视为”不合规“，否则视为”合规“\n  - name: aliyun-slb-metrics\n    resource: aliyun.slb\n    filters:\n      - type: metrics\n        name: InstanceTrafficTX\n        days: ${{days}}\n        period: 86700\n        statistics: Average\n        value: ${{value}}\n        op: ${{op}}', '[{\"defaultValue\":\"7\",\"name\":\"连续n天内业务高峰\",\"key\":\"days\",\"required\":true},{\"defaultValue\":\"30000\",\"name\":\"监控值\",\"key\":\"value\",\"required\":true},{\"defaultValue\":\"less-than\",\"name\":\"低于(判断)\",\"key\":\"op\",\"required\":true}]', 'hummer-aliyun-plugin', '阿里云', 'aliyun.png', concat(unix_timestamp(now()), '001'), 1, 'custodian', NULL, 0);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`, `xpack_tag`) VALUES ('c921303f-23aa-4999-8897-472b7711223e', 'Aliyun RDS 实例 IOPS 监控检测', 1, 'HighRisk', 'Aliyun RDS实例在指定时间范围内 IOPS 监控值低于指定值(单位: 使用率%),符合条件视为”不合规“，否则视为”合规“', 'policies:\n    # RDS实例在指定时间范围内 IOPS 监控值低于指定值(单位: 使用率%),符合条件视为”不合规“，否则视为”合规“\n    # IOPS使用率:IOPSUsage\n    # 连接数使用率: ConnectionUsage\n    # 内存使用率: MemoryUsage\n    # CPU使用率: CpuUsage\n  - name: aliyun-rds-metrics\n    resource: aliyun.rds\n    filters:\n      - type: metrics\n        name: IOPSUsage\n        days: ${{days}}\n        period: 86700\n        statistics: Average\n        value: ${{value}}\n        op: ${{op}}', '[{\"defaultValue\":\"7\",\"name\":\"连续n天内业务高峰\",\"key\":\"days\",\"required\":true},{\"defaultValue\":\"30\",\"name\":\"监控值\",\"key\":\"value\",\"required\":true},{\"defaultValue\":\"less-than\",\"name\":\"低于(判断)\",\"key\":\"op\",\"required\":true}]', 'hummer-aliyun-plugin', '阿里云', 'aliyun.png', concat(unix_timestamp(now()), '001'), 1, 'custodian', NULL, 0);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`, `xpack_tag`) VALUES ('373f8917-8cbb-4b3e-b448-60ac0928efcc', 'Aliyun RDS实例CPU和内存平均使用率检测', 1, 'HighRisk', 'Aliyun RDS实例在指定时间范围内，CPU和内存平均使用率低于指定值(单位: 使用率%),符合条件视为”不合规“，否则视为”合规“', 'policies:\n  # RDS实例在指定时间范围内，CPU和内存平均使用率低于指定值(单位: 使用率%),符合条件视为”不合规“，否则视为”合规“\n  - name: aliyun-rds-metrics\n    resource: aliyun.rds\n    filters:\n      - type: metrics\n        name: CpuUsage\n        days: ${{days}}\n        period: 86700\n        statistics: Average\n        value: ${{value}}\n        op: ${{op}}\n      - type: metrics\n        name: MemoryUsage\n        days: ${{days}}\n        period: 86700\n        statistics: Average\n        value: ${{value}}\n        op: ${{op}}', '[{\"defaultValue\":\"7\",\"name\":\"连续n天内业务高峰\",\"key\":\"days\",\"required\":true},{\"defaultValue\":\"30\",\"name\":\"监控值\",\"key\":\"value\",\"required\":true},{\"defaultValue\":\"less-than\",\"name\":\"低于(判断)\",\"key\":\"op\",\"required\":true}]', 'hummer-aliyun-plugin', '阿里云', 'aliyun.png', concat(unix_timestamp(now()), '001'), 1, 'custodian', NULL, 0);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`, `xpack_tag`) VALUES ('de764892-0dd4-4cf3-ad2a-7f818f5df549', 'Aliyun ECS 公网流量和EIP 公网流量检测', 1, 'HighRisk', 'Aliyun 指定时间范围内，虚拟机公网流量（流出）或 EIP 公网流量（流出）的平均值/总带宽比值低于指定百分比,符合条件视为”不合规“，否则视为”合规“', 'policies:\n  # 指定时间范围内，虚拟机公网流量（流出）或 EIP 公网流量（流出）的平均值/总带宽比值低于指定百分比,符合条件视为”不合规“，否则视为”合规“\n  - name: aliyun-ecs-rate-metrics\n    resource: aliyun.ecs\n    filters:\n      - type: metrics\n        name: InternetOutRate_Percent\n        days: ${{days}}\n        period: 86700\n        statistics: Average\n        value: ${{value}}\n        op: ${{op}}\n      - type: metrics\n        name: VPC_PublicIP_InternetOutRate_Percent\n        days: ${{days}}\n        period: 86700\n        statistics: Average\n        value: ${{value}}\n        op: ${{op}}', '[{\"defaultValue\":\"7\",\"name\":\"连续n天内业务高峰\",\"key\":\"days\",\"required\":true},{\"defaultValue\":\"30\",\"name\":\"监控值\",\"key\":\"value\",\"required\":true},{\"defaultValue\":\"less-than\",\"name\":\"低于(判断)\",\"key\":\"op\",\"required\":true}]', 'hummer-aliyun-plugin', '阿里云', 'aliyun.png', concat(unix_timestamp(now()), '001'), 1, 'custodian', NULL, 0);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`, `xpack_tag`) VALUES ('abad943d-2928-418a-8704-0793e6aff03b', 'Aliyun Disk 磁盘读写监控检测', 1, 'HighRisk', 'Aliyun Disk 磁盘状态为未挂载，或指定时间范围内读写监控值为0（或无数据）,符合条件视为”不合规“，否则视为”合规“', 'policies:\n  # Disk磁盘状态为未挂载，或指定时间范围内读写监控值为0（或无数据）,符合条件视为”不合规“，否则视为”合规“\n  # 系统盘I/O读写总操作，单位：次/s。IOPSTotal\n  # 系统盘读写总带宽，单位：Byte/s。BPSTotal\n  - name: aliyun-disk-iopstotal-metrics\n    resource: aliyun.disk\n    filters:\n      - or:\n        - type: unused\n        - type: metrics\n          name: IOPSTotal\n          days: ${{days}}\n          #数据的精度，单位为秒。取值范围：60/600/3600,默认值：60\n          period: 3600 \n          statistics: Average\n          #监控值(单位：bit/s)\n          value: ${{value}} \n          op: ${{op}}', '[{\"defaultValue\":\"7\",\"name\":\"连续n天内业务高峰\",\"key\":\"days\",\"required\":true},{\"defaultValue\":\"0\",\"name\":\"监控值\",\"key\":\"value\",\"required\":true},{\"defaultValue\":\"eq\",\"name\":\"等于(判断)\",\"key\":\"op\",\"required\":true}]', 'hummer-aliyun-plugin', '阿里云', 'aliyun.png', concat(unix_timestamp(now()), '001'), 1, 'custodian', NULL, 0);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`, `xpack_tag`) VALUES ('66674b46-c6f1-46e0-9333-e72db63938bc', 'Aliyun ECS CPU使用率检测', 1, 'HighRisk', 'Aliyun ECS 主机连续n天内业务高峰阶段CPU平均使用率在不高于x%，符合条件视为”不合规“，否则视为”合规“', 'policies:\n  # ECS主机连续n天内业务高峰阶段CPU平均使用率在不高于x%，符合条件视为”不合规“，否则视为”合规“\n  - name: aliyun-ecs-cpu-underutilized\n    resource: aliyun.ecs\n    filters:\n      - type: metrics\n        name: CPUUtilization\n        days: ${{days}}\n        period: 86400\n        statistics: Average\n        value: ${{value}}\n        op: ${{op}}', '[{\"defaultValue\":\"7\",\"name\":\"连续n天内业务高峰\",\"key\":\"days\",\"required\":true},{\"defaultValue\":\"30\",\"name\":\"CPU平均使用率x%\",\"key\":\"value\",\"required\":true},{\"defaultValue\":\"less-than\",\"name\":\"不高于(判断)\",\"key\":\"op\",\"required\":true}]', 'hummer-aliyun-plugin', '阿里云', 'aliyun.png', concat(unix_timestamp(now()), '001'), 1, 'custodian', NULL, 0);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`, `xpack_tag`) VALUES ('28bb88eb-0ee9-47f1-abce-04c6f07cd83a', 'Aliyun ECS 内存使用率检测', 1, 'HighRisk', 'Aliyun ECS 主机处于关机状态，或指定时间范围内内存使用率为0（或无数据）,符合条件视为”不合规“，否则视为”合规“', 'policies:\n  # ECS主机处于关机状态，或指定时间范围内内存使用率为0（或无数据）,符合条件视为”不合规“，否则视为”合规“\n  - name: aliyun-ecs-memory-metrics\n    resource: aliyun.ecs\n    filters:\n      - or:\n        - type: stopped\n        - type: metrics\n          name: memory_usedutilization\n          days: ${{days}}\n          period: 86700\n          statistics: Average\n          value: ${{value}}\n          op: ${{op}}', '[{\"defaultValue\":\"7\",\"name\":\"连续n天内业务高峰\",\"key\":\"days\",\"required\":true},{\"defaultValue\":\"0\",\"name\":\"内存使用率x%\",\"key\":\"value\",\"required\":true},{\"defaultValue\":\"eq\",\"name\":\"不高于(判断)\",\"key\":\"op\",\"required\":true}]', 'hummer-aliyun-plugin', '阿里云', 'aliyun.png', concat(unix_timestamp(now()), '001'), 1, 'custodian', NULL, 0);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`, `xpack_tag`) VALUES ('4d19238e-06a6-42f3-b362-5c2578ac5fa9', 'Aliyun ECS 指定时间范围内，内存平均使用率检测', 1, 'HighRisk', 'Aliyun ECS 主机指定时间范围内，内存平均使用率低于指定值（基于agent监控）,符合条件视为”不合规“，否则视为”合规“', 'policies:\n  # ECS主机指定时间范围内，内存平均使用率低于指定值（基于agent监控）,符合条件视为”不合规“，否则视为”合规“\n  - name: aliyun-ecs-memory-metrics\n    resource: aliyun.ecs\n    filters:\n      - or:\n        - type: metrics\n          name: memory_usedutilization\n          days: ${{days}}\n          period: 86700 \n          statistics: Average\n          value: ${{value}}\n          op: ${{op}}', '[{\"defaultValue\":\"7\",\"name\":\"连续n天内业务高峰\",\"key\":\"days\",\"required\":true},{\"defaultValue\":\"30\",\"name\":\"内存平均使用率x%\",\"key\":\"value\",\"required\":true},{\"defaultValue\":\"less-than\",\"name\":\"低于(判断)\",\"key\":\"op\",\"required\":true}]', 'hummer-aliyun-plugin', '阿里云', 'aliyun.png', concat(unix_timestamp(now()), '001'), 1, 'custodian', NULL, 0);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`, `xpack_tag`) VALUES ('afacf811-76ef-49ff-8a3f-4eeede968103', 'Aliyun ECS 指定时间范围内，CPU平均使用率检测', 1, 'HighRisk', 'Aliyun ECS 主机指定时间范围内，CPU平均使用率低于指定值（基于agent监控）,符合条件视为”不合规“，否则视为”合规“', 'policies:\n  # ECS主机指定时间范围内，CPU平均使用率低于指定值（基于agent监控）,符合条件视为”不合规“，否则视为”合规“\n  - name: aliyun-ecs-cpu-metrics\n    resource: aliyun.ecs\n    filters:\n      - or:\n        - type: metrics\n          name: cpu_total\n          days: ${{days}}\n          period: 86700 \n          statistics: Average\n          value: ${{value}}\n          op: ${{op}}', '[{\"defaultValue\":\"7\",\"name\":\"连续n天内业务高峰\",\"key\":\"days\",\"required\":true},{\"defaultValue\":\"30\",\"name\":\"CPU平均使用率x%\",\"key\":\"value\",\"required\":true},{\"defaultValue\":\"less-than\",\"name\":\"低于(判断)\",\"key\":\"op\",\"required\":true}]', 'hummer-aliyun-plugin', '阿里云', 'aliyun.png', concat(unix_timestamp(now()), '001'), 1, 'custodian', NULL, 0);

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7c08c80f-007a-4796-a5e7-0562867a5a1b', '29');
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c921303f-23aa-4999-8897-472b7711223e', '29');
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('373f8917-8cbb-4b3e-b448-60ac0928efcc', '29');
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('de764892-0dd4-4cf3-ad2a-7f818f5df549', '29');
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('abad943d-2928-418a-8704-0793e6aff03b', '29');
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('66674b46-c6f1-46e0-9333-e72db63938bc', '29');
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('28bb88eb-0ee9-47f1-abce-04c6f07cd83a', '29');
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('4d19238e-06a6-42f3-b362-5c2578ac5fa9', '29');
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('afacf811-76ef-49ff-8a3f-4eeede968103', '29');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('7c08c80f-007a-4796-a5e7-0562867a5a1b', '1');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c921303f-23aa-4999-8897-472b7711223e', '1');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('373f8917-8cbb-4b3e-b448-60ac0928efcc', '1');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('de764892-0dd4-4cf3-ad2a-7f818f5df549', '2');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('abad943d-2928-418a-8704-0793e6aff03b', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('66674b46-c6f1-46e0-9333-e72db63938bc', '1');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('28bb88eb-0ee9-47f1-abce-04c6f07cd83a', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4d19238e-06a6-42f3-b362-5c2578ac5fa9', '1');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('afacf811-76ef-49ff-8a3f-4eeede968103', '1');

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('7c08c80f-007a-4796-a5e7-0562867a5a1b', 'cost');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('c921303f-23aa-4999-8897-472b7711223e', 'cost');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('373f8917-8cbb-4b3e-b448-60ac0928efcc', 'cost');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('de764892-0dd4-4cf3-ad2a-7f818f5df549', 'cost');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('abad943d-2928-418a-8704-0793e6aff03b', 'cost');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('66674b46-c6f1-46e0-9333-e72db63938bc', 'cost');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('28bb88eb-0ee9-47f1-abce-04c6f07cd83a', 'cost');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('4d19238e-06a6-42f3-b362-5c2578ac5fa9', 'cost');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('afacf811-76ef-49ff-8a3f-4eeede968103', 'cost');

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('5c26605b-2aed-48d1-b308-44164d304c7b', '7c08c80f-007a-4796-a5e7-0562867a5a1b', 'aliyun.slb');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('793f18f0-827b-4ca9-9964-26e14fac4cfb', 'c921303f-23aa-4999-8897-472b7711223e', 'aliyun.rds');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('1dd76a26-ace7-465c-b8d4-68e31a8b6298', '373f8917-8cbb-4b3e-b448-60ac0928efcc', 'aliyun.rds');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('92a72baa-132d-4241-aaed-5e7c12335781', 'de764892-0dd4-4cf3-ad2a-7f818f5df549', 'aliyun.ecs');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('f7f1c868-e897-4c27-bb57-edaf9be3fc68', 'abad943d-2928-418a-8704-0793e6aff03b', 'aliyun.disk');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('aeba93b4-e102-4630-80f2-52bbc7d9be90', '66674b46-c6f1-46e0-9333-e72db63938bc', 'aliyun.ecs');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('b7909ab9-7193-4b1a-9cfc-b5e64a273373', '28bb88eb-0ee9-47f1-abce-04c6f07cd83a', 'aliyun.ecs');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('9aff6170-c6d1-41cc-9972-200e05199574', '4d19238e-06a6-42f3-b362-5c2578ac5fa9', 'aliyun.ecs');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('d5e0a62a-df08-4e47-adc2-55eac60d54a4', 'afacf811-76ef-49ff-8a3f-4eeede968103', 'aliyun.ecs');
