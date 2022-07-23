
CREATE TABLE IF NOT EXISTS `image_grype_table` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `result_id`                    varchar(50)         NOT NULL COMMENT 'Result ID',
    `name`                         varchar(50)         DEFAULT NULL COMMENT 'NAME',
    `installed`                    varchar(50)         DEFAULT NULL COMMENT 'INSTALLED',
    `fixed_in`                     varchar(50)         DEFAULT NULL COMMENT 'FIXED-IN',
    `type`                         varchar(50)         DEFAULT NULL COMMENT 'TYPE',
    `vulnerability`                varchar(50)         DEFAULT NULL COMMENT 'VULNERABILITY',
    `severity`                     varchar(50)         DEFAULT NULL COMMENT 'SEVERITY',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `image_grype_json` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `result_id`                    varchar(50)         NOT NULL COMMENT 'Result ID',
    `name`                         varchar(50)         DEFAULT NULL COMMENT 'name',
    `version`                      varchar(50)         DEFAULT '' COMMENT 'version',
    `type`                         varchar(50)         DEFAULT NULL COMMENT 'type',
    `cve`                          varchar(50)         DEFAULT NULL COMMENT 'cve',
    `dataSource`                   varchar(50)         DEFAULT NULL COMMENT 'dataSource',
    `severity`                     varchar(50)         DEFAULT NULL COMMENT 'severity',
    `namespace`                    varchar(50)         DEFAULT NULL COMMENT 'namespace',
    `description`                  varchar(512)        DEFAULT NULL COMMENT 'description',
    `vulnerability`                longtext            DEFAULT NULL COMMENT 'vulnerability',
    `related_vulnerabilities`      longtext            DEFAULT NULL COMMENT 'relatedVulnerabilities',
    `match_details`                longtext            DEFAULT NULL COMMENT 'matchDetails',
    `artifact`                     longtext            DEFAULT NULL COMMENT 'artifact',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `image_syft_table` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `result_id`                    varchar(50)         NOT NULL COMMENT 'Result ID',
    `name`                         varchar(50)         DEFAULT NULL COMMENT 'NAME',
    `version`                      varchar(50)         DEFAULT NULL COMMENT 'VERSION',
    `type`                         varchar(50)         DEFAULT NULL COMMENT 'FIXED-IN',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `image_syft_json` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `result_id`                    varchar(50)         NOT NULL COMMENT 'Result ID',
    `name`                         varchar(50)         DEFAULT NULL COMMENT 'name',
    `version`                      varchar(50)         DEFAULT '' COMMENT 'version',
    `type`                         varchar(50)         DEFAULT NULL COMMENT 'type',
    `found_by`                     varchar(50)         DEFAULT NULL COMMENT 'foundBy',
    `language`                     varchar(50)         DEFAULT NULL COMMENT 'language',
    `purl`                         varchar(255)        DEFAULT NULL COMMENT 'purl',
    `metadata_type`                varchar(50)         DEFAULT NULL COMMENT 'metadataType',
    `metadata`                     mediumtext          DEFAULT NULL COMMENT 'metadata',
    `locations`                    mediumtext          DEFAULT NULL COMMENT 'locations',
    `licenses`                     mediumtext          DEFAULT NULL COMMENT 'licenses',
    `cpes`                         mediumtext          DEFAULT NULL COMMENT 'cpes',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `package_dependency_json` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `result_id`                    varchar(50)         NOT NULL COMMENT 'Result ID',
    `is_virtual`                   varchar(50)         DEFAULT NULL COMMENT 'isVirtual',
    `file_name`                    varchar(50)         DEFAULT NULL COMMENT 'fileName',
    `file_path`                    varchar(255)        DEFAULT NULL COMMENT 'filePath',
    `md5`                          varchar(50)         DEFAULT NULL COMMENT 'md5',
    `sha1`                         varchar(255)        DEFAULT NULL COMMENT 'sha1',
    `sha256`                       varchar(255)        DEFAULT NULL COMMENT 'sha256',
    `evidence_collected`           mediumtext          DEFAULT NULL COMMENT 'evidenceCollected',
    `projectReferences`            mediumtext          DEFAULT NULL COMMENT 'projectReferences',
    `packages`                     mediumtext          DEFAULT NULL COMMENT 'packages',
    `vulnerabilities`              mediumtext          DEFAULT NULL COMMENT 'vulnerabilities',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

DROP TABLE image_result_item;

DROP TABLE package_result_item;
