

ALTER TABLE `cloud_native` ADD `kubench_status` varchar(10) DEFAULT null COMMENT 'kube-bench状态';

ALTER TABLE `cloud_native_result` ADD `kube_bench` longtext DEFAULT null COMMENT 'kube bench';

CREATE TABLE IF NOT EXISTS `cloud_native_result_kubench`
(
    `id`                         varchar(50)         NOT NULL,
    `result_id`                  varchar(50)         DEFAULT NULL COMMENT '云原生检测结果ID',
    `title`                      longtext            DEFAULT NULL COMMENT 'title',
    `severity`                   varchar(128)        DEFAULT NULL COMMENT 'severity',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `description`                longtext            DEFAULT NULL COMMENT 'description',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;


