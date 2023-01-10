
CREATE TABLE IF NOT EXISTS `image_repo_setting` (
    `id`                         varchar(50)        NOT NULL COMMENT 'ID',
    `repo_id`                    varchar(50)        DEFAULT NULL COMMENT '镜像仓库ID',
    `repo`                       varchar(512)       DEFAULT NULL COMMENT 'repository 仓库地址',
    `repo_old`                   varchar(512)       DEFAULT NULL COMMENT 'repository 旧仓库地址',
    `create_time`                bigint(13)         DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)         DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;
