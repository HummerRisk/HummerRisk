
ALTER TABLE `cloud_resource_item` ADD `hummer_name` varchar(256) DEFAULT NULL COMMENT '资源别名';

ALTER TABLE `resource_item` ADD `hummer_name` varchar(256) DEFAULT NULL COMMENT '资源别名';

CREATE TABLE IF NOT EXISTS `cloud_resource_rela` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `name`                       varchar(256)        DEFAULT NULL COMMENT '资源名称',
    `resource_item_id`           varchar(128)        DEFAULT NULL COMMENT 'cloud resource item 标识',
    `plugin_id`                  varchar(128)        DEFAULT NULL COMMENT '云类型ID',
    `account_id`                 varchar(128)        DEFAULT NULL COMMENT '云账号ID',
    `region_id`                  varchar(128)        DEFAULT NULL COMMENT '云账号区域ID',
    `resource_type`              varchar(64)         DEFAULT NULL COMMENT '资源类型',
    `hummer_id`                  varchar(128)        DEFAULT NULL COMMENT '资源ID',
    `x_axis`                     bigint              DEFAULT NULL COMMENT '横坐标',
    `y_axis`                     bigint              DEFAULT NULL COMMENT '纵坐标',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `cloud_resource_rela_link` (
    `id`                         int                 NOT NULL AUTO_INCREMENT,
    `resource_item_id`           varchar(128)        DEFAULT NULL COMMENT 'cloud resource item 标识',
    `source`                     varchar(50)         DEFAULT NULL COMMENT '资源关系ID(源)',
    `target`                     varchar(50)         DEFAULT NULL COMMENT '资源关系ID(目标)',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

ALTER TABLE `rule_group` ADD `image_url` varchar(256) DEFAULT NULL COMMENT '图片地址';
