
CREATE TABLE IF NOT EXISTS `image_repo_sync_log` (
    `id`                           int              NOT NULL AUTO_INCREMENT,
    `repo_id`                      varchar(50)         DEFAULT NULL COMMENT '镜像仓库ID',
    `create_time`                  bigint              DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    mediumtext          DEFAULT NULL COMMENT '操作内容',
    `sum`                          bigint              DEFAULT NULL COMMENT '同步镜像数量',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint             DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `cloud_native_source_sync_log` (
    `id`                           varchar(50)         NOT NULL COMMENT 'ID',
    `cloud_native_id`              varchar(50)         DEFAULT NULL COMMENT '云原生账号ID',
    `create_time`                  bigint              DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    mediumtext          DEFAULT NULL COMMENT '操作内容',
    `sum`                          bigint              DEFAULT NULL COMMENT '同步资源数量',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint             DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;
