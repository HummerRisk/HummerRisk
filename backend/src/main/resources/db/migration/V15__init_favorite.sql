
CREATE TABLE IF NOT EXISTS `favorite` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `resource_id`                varchar(50)         DEFAULT NULL COMMENT '资源ID',
    `resource_type`              varchar(50)         DEFAULT NULL COMMENT '资源类型',
    `resource_name`              varchar(50)         DEFAULT NULL COMMENT '资源名称',
    `resource_icon`              varchar(10)         DEFAULT NULL COMMENT '资源图标',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `creator`                    varchar(50)         DEFAULT NULL COMMENT '创建人ID',
    `creator_name`               varchar(50)         DEFAULT NULL COMMENT '创建人名称',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;
