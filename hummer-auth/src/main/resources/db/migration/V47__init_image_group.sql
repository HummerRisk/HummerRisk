
CREATE TABLE IF NOT EXISTS `image_group` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `name`                       varchar(256)        DEFAULT NULL COMMENT '分组名称',
    `repository`                 varchar(256)        DEFAULT NULL COMMENT 'repository',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint              DEFAULT NULL COMMENT '更新时间',
    `creator`                    varchar(128)        DEFAULT NULL COMMENT '创建人',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

INSERT INTO `image_group` (`id`, `name`, `create_time`, `update_time`, `creator`) VALUES ('d661se75-1r8c-2s54-cbe6-351sd29e91ff', 'default', concat(unix_timestamp(now()), '001'), concat(unix_timestamp(now()), '001'), 'admin');

ALTER TABLE `image` ADD `group_id` varchar(50) DEFAULT NULL COMMENT '分组Id';

UPDATE `image` SET group_id = 'd661se75-1r8c-2s54-cbe6-351sd29e91ff';
