
CREATE TABLE IF NOT EXISTS `oss`
(
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `name`                       varchar(128)        DEFAULT NULL UNIQUE COMMENT '云账号名称',
    `credential`                 longtext            DEFAULT NULL COMMENT '云账号凭证',
    `endpoint`                   varchar(256)        DEFAULT NULL COMMENT 'Endpoint',
    `plugin_id`                  varchar(50)         DEFAULT NULL COMMENT '插件ID',
    `plugin_name`                varchar(100)        DEFAULT NULL COMMENT '插件名称',
    `plugin_icon`                varchar(256)        DEFAULT NULL COMMENT '插件图标',
    `status`                     varchar(50)         DEFAULT 'APPROVED' COMMENT '同步状态',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `creator`                    varchar(128)        DEFAULT NULL COMMENT '创建人',
    `regions`                    longtext            DEFAULT NULL COMMENT '区域',
    `proxy_id`                   int(11)             DEFAULT NULL COMMENT '代理ID',
    `sum`                        int(11)             DEFAULT 0 COMMENT '同步bucket数',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `oss_log` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `oss_id`                       varchar(50)         DEFAULT NULL COMMENT 'Oss ID',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    mediumtext          DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint(1)          DEFAULT NULL COMMENT '结果',
    `sum`                          int(11)             DEFAULT 0 COMMENT '同步bucket数',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `oss_bucket` (
    `id`                           varchar(50)         NOT NULL COMMENT 'ID',
    `bucket_name`                  varchar(64)         DEFAULT NULL COMMENT '存储名称',
    `storage_class`                varchar(64)         DEFAULT NULL COMMENT '存储类型',
    `location`                     varchar(80)         DEFAULT NULL COMMENT '位置',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `intranet_endpoint`            varchar(256)        DEFAULT NULL COMMENT '内部节点',
    `extranet_endpoint`            varchar(256)        DEFAULT NULL COMMENT '外部节点',
    `oss_id`                       varchar(50)         DEFAULT NULL COMMENT 'Oss ID',
    `owner_id`                     varchar(80)         DEFAULT NULL COMMENT '所有者id',
    `owner_name`                   varchar(80)         DEFAULT NULL COMMENT '所有者',
    `domain_name`                  varchar(500)        DEFAULT NULL COMMENT '存储桶域名',
    `size`                         varchar(50)         DEFAULT NULL COMMENT '存储用量',
    `object_number`                bigint(20)          COLLATE utf8mb4_bin COMMENT '文件数量',
    `canned_acl`                   varchar(45)         DEFAULT NULL COMMENT '权限',
    `sync_flag`                    tinyint(1)          DEFAULT 0 COMMENT '是否启用',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

DROP TABLE `cloud_native_source`;

CREATE TABLE IF NOT EXISTS `cloud_native_source` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `cloud_native_id`            varchar(50)         DEFAULT NULL COMMENT '云原生ID',
    `source_name`                varchar(256)        DEFAULT NULL COMMENT '资源名称',
    `source_namespace`           varchar(256)        DEFAULT NULL COMMENT '资源标识',
    `source_yaml`                longtext            DEFAULT NULL COMMENT '资源yaml',
    `source_json`                longtext            DEFAULT NULL COMMENT '资源json',
    `source_type`                varchar(50)         DEFAULT NULL COMMENT '资源类型',
    `source_node`                varchar(256)        DEFAULT NULL COMMENT '资源节点',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `creator`                    varchar(128)        DEFAULT NULL COMMENT '创建人',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `cloud_native_source_image` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `source_id`                    varchar(50)         DEFAULT NULL COMMENT 'source ID',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `image`                        varchar(512)        DEFAULT NULL COMMENT 'image',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

ALTER TABLE `image_result_item` ADD `pkg_name` varchar(256) DEFAULT NULL COMMENT 'PkgName';
ALTER TABLE `cloud_native_result_item` ADD `image` varchar(512) DEFAULT NULL COMMENT 'image';
ALTER TABLE `code_result_item` ADD `pkg_name` varchar(256) DEFAULT NULL COMMENT 'PkgName';
ALTER TABLE `file_system_result_item` ADD `pkg_name` varchar(256) DEFAULT NULL COMMENT 'PkgName';
