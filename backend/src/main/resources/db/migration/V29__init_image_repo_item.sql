
CREATE TABLE IF NOT EXISTS `image_repo_item` (
    `id`                         varchar(50)        NOT NULL COMMENT 'ID',
    `repo_id`                    varchar(50)        DEFAULT NULL COMMENT '镜像仓库ID',
    `digest`                     varchar(256)       DEFAULT NULL COMMENT 'digest',
    `project`                    varchar(128)       DEFAULT NULL COMMENT 'project',
    `repository`                 varchar(128)       DEFAULT NULL COMMENT 'repository',
    `tag`                        varchar(50)        DEFAULT NULL COMMENT 'tag',
    `path`                       varchar(256)       DEFAULT NULL COMMENT '镜像地址',
    `size`						 varchar(50)		DEFAULT NULL COMMENT '大小',
    `arch`             			 varchar(64)        DEFAULT NULL COMMENT 'cpu架构',
    `push_time`                  bigint(15)         DEFAULT NULL COMMENT '推送时间',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

alter table cloud_native_config_result_item modify column `title` varchar(512) DEFAULT NULL COMMENT 'Title';
alter table cloud_native_config_result_item modify column `namespace` varchar(256) DEFAULT NULL COMMENT 'Namespace';
alter table cloud_native_config_result_item modify column `query` varchar(256) DEFAULT NULL COMMENT 'Query';
alter table cloud_native_config_result_item modify column `resolution` varchar(512) DEFAULT NULL COMMENT 'Resolution';
alter table cloud_native_config_result_item modify column `primary_url` varchar(256) DEFAULT NULL COMMENT 'PrimaryURL';
