
CREATE TABLE IF NOT EXISTS `image_repo` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '镜像仓库名称',
    `status`                     varchar(10)         DEFAULT 'VALID' COMMENT '镜像仓库连接状态',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `creator`                    varchar(128)        DEFAULT NULL COMMENT '创建人',
    `repo`                       varchar(128)        DEFAULT NULL COMMENT '仓库地址',
    `user_name`                  varchar(128)        DEFAULT NULL COMMENT '仓库用户名',
    `password`                   varchar(128)        DEFAULT NULL COMMENT '仓库密码',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `image` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '镜像名称',
    `plugin_icon`                varchar(256)        DEFAULT 'docker.png' COMMENT '图标地址/opt/hummerrisk/image/',
    `status`                     varchar(10)         DEFAULT 'VALID' COMMENT '镜像状态',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `creator`                    varchar(128)        DEFAULT NULL COMMENT '创建人',
    `repo_id`                    varchar(50)         DEFAULT NULL COMMENT '镜像仓库ID',
    `size`                       varchar(128)        DEFAULT '0M' COMMENT '镜像大小',
    `type`                       varchar(50)         DEFAULT 'image' COMMENT '镜像类型：镜像/image、镜像包/tar',
    `image_url`                  varchar(128)        DEFAULT NULL COMMENT '镜像名称',
    `image_tag`                  varchar(128)        DEFAULT NULL COMMENT '镜像标签',
    `path`                       varchar(128)        DEFAULT NULL COMMENT '镜像包持久化存储路径/opt/hummerrisk/image/',
    `is_proxy`                   tinyint(1)          DEFAULT 0 COMMENT '是否启用代理',
    `proxy_id`                   int(11)             DEFAULT NULL COMMENT '代理ID',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;


