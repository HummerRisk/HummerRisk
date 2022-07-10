
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
    `size`                       varchar(128)        DEFAULT NULL COMMENT '镜像大小',
    `type`                       varchar(50)         DEFAULT 'image' COMMENT '镜像类型：镜像/image、镜像包/tar',
    `image_url`                  varchar(128)        DEFAULT NULL COMMENT '镜像名称',
    `image_tag`                  varchar(128)        DEFAULT NULL COMMENT '镜像标签',
    `path`                       varchar(128)        DEFAULT NULL COMMENT '镜像包持久化存储路径/opt/hummerrisk/image/',
    `is_image_repo`              tinyint(1)          DEFAULT 0 COMMENT '是否绑定镜像仓库',
    `is_image_icon`              tinyint(1)          DEFAULT 0 COMMENT '是否上传镜像图片',
    `is_proxy`                   tinyint(1)          DEFAULT 0 COMMENT '是否启用代理',
    `proxy_id`                   int(11)             DEFAULT NULL COMMENT '代理ID',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `image_rule` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `name`                       varchar(50)         DEFAULT NULL UNIQUE COMMENT '规则名称',
    `status`                     tinyint(1)          DEFAULT 1 COMMENT '规则状态(启用1，停用0)',
    `severity`                   varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `description`                varchar(255)        DEFAULT NULL COMMENT '`描述',
    `script`                     mediumtext          DEFAULT NULL COMMENT '脚本',
    `parameter`                  varchar(1024)       DEFAULT NULL COMMENT '参数',
    `last_modified`              bigint(14)          DEFAULT NULL COMMENT '上次更新时间',
    `flag`                       tinyint(1)          NOT NULL DEFAULT 0 COMMENT '是否内置',
    PRIMARY KEY (`id`),
    KEY `IDX_NAME` (`name`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

INSERT INTO `image_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('3095fa07-78f5-4d8d-8751-76a961bb8c21', '镜像检测', 1, 'HighRisk', '容器镜像和文件系统的漏洞检测', '扫描容器镜像或文件系统的内容以查找已知漏洞。\n查找主要操作系统包的漏洞：\n  Alpine\n  Amazon Linux\n  BusyBox\n  CentOS\n  Debian\n  Distroless\n  Oracle Linux\n  Red Hat (RHEL)\n  Ubuntu\n查找特定语言包的漏洞：\n  Ruby (Gems)\n  Java (JAR, WAR, EAR, JPI, HPI)\n  JavaScript (NPM, Yarn)\n  Python (Egg, Wheel, Poetry, requirements.txt/setup.py files)\n  Dotnet (deps.json)\n  Golang (go.mod)\n  PHP (composer.json)\n支持 Docker 和 OCI 镜像格式。\n使用 SBOM 证明。', '[]', concat(unix_timestamp(now()), '001'), 1);
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('3095fa07-78f5-4d8d-8751-76a961bb8c21', 'image');

CREATE TABLE IF NOT EXISTS `image_result`
(
    `id`                         varchar(50)         NOT NULL,
    `image_id`                   varchar(50)         DEFAULT NULL COMMENT 'imageID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '镜像检测名称(别名)',
    `size`                       varchar(128)        DEFAULT '0M' COMMENT '镜像大小',
    `plugin_icon`                varchar(256)        DEFAULT 'docker.png' COMMENT '图标地址',
    `rule_id`                    varchar(50)         DEFAULT NULL COMMENT '镜像检测规则ID',
    `rule_name`                  varchar(50)         DEFAULT NULL COMMENT '镜像检测规则名称',
    `rule_desc`                  varchar(50)         DEFAULT NULL COMMENT '镜像检测规则描述',
    `result_status`              varchar(45)         DEFAULT NULL COMMENT '检测状态',
    `severity`                   varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `apply_user`                 varchar(50)         DEFAULT NULL COMMENT '创建人ID',
    `user_name`                  varchar(128)        DEFAULT NULL COMMENT '创建人名称',
    `return_log`                 longtext            DEFAULT NULL COMMENT 'return log',
    `grype_table`                longtext            DEFAULT NULL COMMENT 'grype table',
    `grype_json`                 longtext            DEFAULT NULL COMMENT 'grype json',
    `syft_table`                 longtext            DEFAULT NULL COMMENT 'syft table',
    `syft_json`                  longtext            DEFAULT NULL COMMENT 'syft json',
    `return_sum`                 bigint(13)          DEFAULT 0 COMMENT '输出检测结果漏洞数',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `image_result_item` (
    `id`                         varchar(50)         NOT NULL COMMENT '资源ID（唯一标识）',
    `result_id`                  varchar(50)         DEFAULT NULL COMMENT 'result主键ID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '镜像检测名称(别名)',
    `severity`                   varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `resource`                   longtext            DEFAULT NULL COMMENT '资源JSON',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `image_result_log` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `result_id`                    varchar(50)         DEFAULT NULL COMMENT '检测结果ID',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    varchar(255)        DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint(1)          DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;
