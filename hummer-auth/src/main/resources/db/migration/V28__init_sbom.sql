
CREATE TABLE IF NOT EXISTS `sbom` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '项目名称',
    `description`                varchar(1024)       DEFAULT NULL COMMENT '项目描述',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint              DEFAULT NULL COMMENT '更新时间',
    `creator`                    varchar(128)        DEFAULT NULL COMMENT '创建人',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `sbom_version` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `sbom_id`                    varchar(50)         NOT NULL COMMENT 'SBOM ID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '版本名称',
    `description`                varchar(1024)       DEFAULT NULL COMMENT '版本描述',
    `create_time`                bigint              DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint              DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

ALTER TABLE `code` ADD `sbom_id` varchar(50) DEFAULT NULL COMMENT 'SBOM ID';

ALTER TABLE `code` ADD `sbom_version_id` varchar(50) DEFAULT NULL COMMENT 'SBOM VERSION ID';

ALTER TABLE `image` ADD `sbom_id` varchar(50) DEFAULT NULL COMMENT 'SBOM ID';

ALTER TABLE `image` ADD `sbom_version_id` varchar(50) DEFAULT NULL COMMENT 'SBOM VERSION ID';

ALTER TABLE `package` ADD `sbom_id` varchar(50) DEFAULT NULL COMMENT 'SBOM ID';

ALTER TABLE `package` ADD `sbom_version_id` varchar(50) DEFAULT NULL COMMENT 'SBOM VERSION ID';

ALTER TABLE `code_result` ADD `sbom_id` varchar(50) DEFAULT NULL COMMENT 'SBOM ID';

ALTER TABLE `code_result` ADD `sbom_version_id` varchar(50) DEFAULT NULL COMMENT 'SBOM VERSION ID';

ALTER TABLE `image_result` ADD `sbom_id` varchar(50) DEFAULT NULL COMMENT 'SBOM ID';

ALTER TABLE `image_result` ADD `sbom_version_id` varchar(50) DEFAULT NULL COMMENT 'SBOM VERSION ID';

ALTER TABLE `package_result` ADD `sbom_id` varchar(50) DEFAULT NULL COMMENT 'SBOM ID';

ALTER TABLE `package_result` ADD `sbom_version_id` varchar(50) DEFAULT NULL COMMENT 'SBOM VERSION ID';

ALTER TABLE `history_code_result` ADD `sbom_id` varchar(50) DEFAULT NULL COMMENT 'SBOM ID';

ALTER TABLE `history_code_result` ADD `sbom_version_id` varchar(50) DEFAULT NULL COMMENT 'SBOM VERSION ID';

ALTER TABLE `history_image_task` ADD `sbom_id` varchar(50) DEFAULT NULL COMMENT 'SBOM ID';

ALTER TABLE `history_image_task` ADD `sbom_version_id` varchar(50) DEFAULT NULL COMMENT 'SBOM VERSION ID';

ALTER TABLE `history_package_task` ADD `sbom_id` varchar(50) DEFAULT NULL COMMENT 'SBOM ID';

ALTER TABLE `history_package_task` ADD `sbom_version_id` varchar(50) DEFAULT NULL COMMENT 'SBOM VERSION ID';

