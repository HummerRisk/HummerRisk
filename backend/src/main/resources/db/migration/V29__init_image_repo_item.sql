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
