-- ----------------------------
-- init fs
-- ----------------------------

CREATE TABLE IF NOT EXISTS `file_system` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '文件系统名称',
    `file_name`                  varchar(128)        DEFAULT NULL COMMENT '文件名',
    `plugin_icon`                varchar(50)         DEFAULT 'fs.png' COMMENT 'fs',
    `status`                     varchar(10)         DEFAULT 'VALID' COMMENT '状态',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `creator`                    varchar(128)        DEFAULT NULL COMMENT '创建人',
    `size`                       varchar(128)        DEFAULT NULL COMMENT '文件大小',
    `path`                       varchar(128)        DEFAULT NULL COMMENT '文件持久化存储路径/opt/hummerrisk/file/',
    `dir`                        varchar(128)        DEFAULT NULL COMMENT '目录',
    `sbom_id`                    varchar(50)         DEFAULT NULL COMMENT 'SBOM ID',
    `sbom_version_id`            varchar(50)         DEFAULT NULL COMMENT 'SBOM VERSION ID',
    `proxy_id`                   int(11)             DEFAULT NULL COMMENT '代理ID',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `file_system_rule` (
    `id`                         varchar(50)         NOT NULL COMMENT 'ID',
    `name`                       varchar(50)         DEFAULT NULL UNIQUE COMMENT '规则名称',
    `status`                     tinyint(1)          DEFAULT 1 COMMENT '规则状态(启用1，停用0)',
    `severity`                   varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `description`                varchar(1024)       DEFAULT NULL COMMENT '`描述',
    `script`                     mediumtext          DEFAULT NULL COMMENT '脚本',
    `parameter`                  varchar(1024)       DEFAULT NULL COMMENT '参数',
    `last_modified`              bigint(14)          DEFAULT NULL COMMENT '上次更新时间',
    `flag`                       tinyint(1)          NOT NULL DEFAULT 0 COMMENT '是否内置',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `file_system_result`
(
    `id`                         varchar(50)         NOT NULL,
    `fs_id`                      varchar(50)         DEFAULT NULL COMMENT 'fs ID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '源码名称',
    `plugin_icon`                varchar(50)         DEFAULT 'fs.png' COMMENT '图标地址',
    `rule_id`                    varchar(50)         DEFAULT NULL COMMENT '文件系统检测规则ID',
    `rule_name`                  varchar(50)         DEFAULT NULL COMMENT '文件系统检测规则名称',
    `rule_desc`                  varchar(256)        DEFAULT NULL COMMENT '文件系统检测规则描述',
    `result_status`              varchar(45)         DEFAULT NULL COMMENT '检测状态',
    `severity`                   varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `apply_user`                 varchar(50)         DEFAULT NULL COMMENT '创建人ID',
    `user_name`                  varchar(128)        DEFAULT NULL COMMENT '创建人名称',
    `return_json`                longtext            DEFAULT NULL COMMENT 'return json',
    `return_sum`                 bigint(13)          DEFAULT 0 COMMENT '输出检测结果漏洞数',
    `sbom_id`                    varchar(50)         DEFAULT NULL COMMENT 'SBOM ID',
    `sbom_version_id`            varchar(50)         DEFAULT NULL COMMENT 'SBOM VERSION ID',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `file_system_result_item` (
    `id`                           varchar(50)         NOT NULL COMMENT '资源ID（唯一标识）',
    `result_id`                    varchar(50)         DEFAULT NULL COMMENT 'result主键ID',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `vulnerability_id`             varchar(50)         NOT NULL COMMENT 'VulnerabilityID',
    `pkg_name`                     varchar(50)         DEFAULT NULL COMMENT 'PkgName',
    `installed_version`            varchar(255)        DEFAULT NULL COMMENT 'InstalledVersion',
    `fixed_version`                varchar(255)        DEFAULT NULL COMMENT 'FixedVersion',
    `severity_source`              varchar(50)         DEFAULT NULL COMMENT 'SeveritySource',
    `primary_url`                  varchar(255)        DEFAULT NULL COMMENT 'PrimaryURL',
    `title`                        mediumtext          DEFAULT NULL COMMENT 'Title',
    `description`                  mediumtext          DEFAULT NULL COMMENT 'Description',
    `severity`                     varchar(50)         DEFAULT NULL COMMENT 'Severity',
    `published_date`               varchar(50)         DEFAULT NULL COMMENT 'PublishedDate',
    `last_modified_date`           varchar(50)         DEFAULT NULL COMMENT 'LastModifiedDate',
    `layer`                        mediumtext          DEFAULT NULL COMMENT 'Layer',
    `data_source`                  mediumtext          DEFAULT NULL COMMENT 'DataSource',
    `cwe_ids`                      mediumtext          DEFAULT NULL COMMENT 'CweIDs',
    `cvss`                         mediumtext          DEFAULT NULL COMMENT 'CVSS',
    `references`                   mediumtext          DEFAULT NULL COMMENT 'References',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `file_system_result_log` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `result_id`                    varchar(50)         DEFAULT NULL COMMENT '检测结果ID',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    mediumtext          DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint(1)          DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_file_system_result`
(
    `id`                         varchar(50)         NOT NULL,
    `fs_id`                      varchar(50)         DEFAULT NULL COMMENT 'fs ID',
    `name`                       varchar(128)        DEFAULT NULL COMMENT '源码名称',
    `plugin_icon`                varchar(50)         DEFAULT 'fs.png' COMMENT '图标地址',
    `rule_id`                    varchar(50)         DEFAULT NULL COMMENT '文件系统检测规则ID',
    `rule_name`                  varchar(50)         DEFAULT NULL COMMENT '文件系统检测规则名称',
    `rule_desc`                  varchar(256)        DEFAULT NULL COMMENT '文件系统检测规则描述',
    `result_status`              varchar(45)         DEFAULT NULL COMMENT '检测状态',
    `severity`                   varchar(32)         DEFAULT NULL COMMENT '风险等级',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint(13)          DEFAULT NULL COMMENT '更新时间',
    `apply_user`                 varchar(50)         DEFAULT NULL COMMENT '创建人ID',
    `user_name`                  varchar(128)        DEFAULT NULL COMMENT '创建人名称',
    `return_json`                longtext            DEFAULT NULL COMMENT 'return json',
    `return_sum`                 bigint(13)          DEFAULT 0 COMMENT '输出检测结果漏洞数',
    `sbom_id`                    varchar(50)         DEFAULT NULL COMMENT 'SBOM ID',
    `sbom_version_id`            varchar(50)         DEFAULT NULL COMMENT 'SBOM VERSION ID',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `history_file_system_result_log` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `result_id`                    varchar(50)         DEFAULT NULL COMMENT '检测结果ID',
    `create_time`                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `operator`                     varchar(100)        DEFAULT NULL COMMENT '操作人',
    `operation`                    mediumtext          DEFAULT NULL COMMENT '操作内容',
    `output`                       mediumtext          DEFAULT NULL COMMENT '输出',
    `result`                       tinyint(1)          DEFAULT NULL COMMENT '结果',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

INSERT INTO `file_system_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('rtyuewq3-ef4c-4gd8-bhc5-5ej110kb0l71', '文件系统检测', 1, 'HighRisk', '文件系统漏洞检测', '全面的漏洞检测', '[]', concat(unix_timestamp(now()), '001'), 1);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('rtyuewq3-ef4c-4gd8-bhc5-5ej110kb0l71', 'safety');

ALTER TABLE `history_server_task` RENAME TO `history_server_result`;

ALTER TABLE `history_server_task_log` RENAME TO `history_server_result_log`;

ALTER TABLE `history_image_task` RENAME TO `history_image_result`;

ALTER TABLE `history_image_task_log` RENAME TO `history_image_result_log`;

ALTER TABLE `image_trivy_json` RENAME TO `image_result_item`;

ALTER TABLE `image_result` change `trivy_json` `result_json` longtext DEFAULT NULL COMMENT 'result json';

ALTER TABLE `history_image_result` change `trivy_json` `result_json` longtext DEFAULT NULL COMMENT 'result json';

ALTER TABLE `cloud_native_result` ADD `return_config_sum` bigint(13) DEFAULT 0 COMMENT '输出检测结果配置审计数';

ALTER TABLE `cloud_task_item_log` MODIFY column `operation` mediumtext DEFAULT NULL COMMENT '操作内容';

ALTER TABLE `cloud_account_quartz_task_rela_log` MODIFY column `operation` mediumtext DEFAULT NULL COMMENT '操作内容';

ALTER TABLE `server_result_log` MODIFY column `operation` mediumtext DEFAULT NULL COMMENT '操作内容';

ALTER TABLE `image_result_log` MODIFY column `operation` mediumtext DEFAULT NULL COMMENT '操作内容';

ALTER TABLE `task_item_resource_log` MODIFY column `operation` mediumtext DEFAULT NULL COMMENT '操作内容';

ALTER TABLE `history_cloud_task_log` MODIFY column `operation` mediumtext DEFAULT NULL COMMENT '操作内容';

ALTER TABLE `history_vuln_task_log` MODIFY column `operation` mediumtext DEFAULT NULL COMMENT '操作内容';

ALTER TABLE `cloud_native_result_log` MODIFY column `operation` mediumtext DEFAULT NULL COMMENT '操作内容';

ALTER TABLE `cloud_native_config_result_log` MODIFY column `operation` mediumtext DEFAULT NULL COMMENT '操作内容';

ALTER TABLE `code_result_log` MODIFY column `operation` mediumtext DEFAULT NULL COMMENT '操作内容';

ALTER TABLE `image_repo_sync_log` MODIFY column `operation` mediumtext DEFAULT NULL COMMENT '操作内容';

ALTER TABLE `cloud_native_source_sync_log` MODIFY column `operation` mediumtext DEFAULT NULL COMMENT '操作内容';

ALTER TABLE `cloud_resource_sync_item_log` MODIFY column `operation` mediumtext DEFAULT NULL COMMENT '操作内容';

ALTER TABLE `file_system_result_log` MODIFY column `operation` mediumtext DEFAULT NULL COMMENT '操作内容';

DROP TABLE `history_server_result_log`;

DROP TABLE `history_image_result_log`;

DROP TABLE `history_cloud_native_result_log`;

DROP TABLE `history_cloud_native_config_result_log`;

DROP TABLE `history_code_result_log`;

DROP TABLE `history_file_system_result_log`;

CREATE TABLE IF NOT EXISTS `cloud_native_result_config_item`
(
    `id`                         varchar(50)         NOT NULL,
    `result_id`                  varchar(50)         DEFAULT NULL COMMENT '云原生检测结果ID',
    `title`                      varchar(512)        DEFAULT NULL COMMENT 'title',
    `category`                   varchar(128)        DEFAULT NULL COMMENT 'category',
    `severity`                   varchar(128)        DEFAULT NULL COMMENT 'severity',
    `check_id`                   varchar(128)        DEFAULT NULL COMMENT 'checkID',
    `description`                mediumtext          DEFAULT NULL COMMENT 'description',
    `success`                    varchar(128)        DEFAULT NULL COMMENT 'success',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `messages`                   mediumtext          DEFAULT NULL COMMENT 'messages',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

DROP TABLE `history_cloud_native_result_item`;

UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/tencent/mongo-public-network.md' where id = 'c95be861-6e0e-4404-9b21-5ccfcd94ee3f';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/qingcloud/mongodb-public-network.md' where id = 'ceecc38d-5c98-4cc4-aeef-028ab653c26b';
DELETE FROM `server_rule`;
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('0487f316-9cd8-46f7-86e9-31f1ab8b504a', 'Linux 非正常系统用户检测', 1, 'MediumRisk', '查看 Linux 系统中所有非正常、不常用的系统用户，避免系统用户被恶意使用', 'users=$(echo ${{systemUsers}}|sed -e \'s/,/ /g\')\nsysUsers=$(egrep -v \'.*:\\*|:\\!\' /etc/shadow | awk -F: \'{print $1}\'|sed -e \'s/,/ /g\')\nDangerousUsers=\'\'\nflag=true\nfor i in ${users[@]};\ndo\n for z in ${sysUsers[@]};\n do\n if [[ ! $z == $i ]];then\n if [[ ! $DangerousUsers+ =~ $z ]];then\n DangerousUsers+=\"${z},\"\n fi\n fi\n done\ndone\n\nif [[ -n $DangerousUsers ]];then\n  echo \"HummerError: 存在非正常用户 ${DangerousUsers} 请及时处理！\"\nelse\n  echo \"HummerSuccess: 系统用户检测正常！\"\nfi', '[{\"defaultValue\":\"root,ubuntu,ec2-user,mysql,apache,nginx\",\"name\":\"正常系统用户名\",\"key\":\"systemUsers\",\"required\":true}]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('0cfdd71d-f4c0-42b0-a8a7-af8760640c8e', 'Linux 密码过期时间检测', 1, 'LowRisk', '查看 Linux 密码过期的时间', 'CurrentPassMaxDays=$(cat /etc/login.defs |grep -v \'^$\'|grep -v \'^#\'|grep PASS_MAX_DAYS|awk \'{print $2}\')\nflag=true\n\nif [[ ${CurrentPassMaxDays} -lt ${{PassMaxDays}}  ]];then\n    echo \"HummerSuccess: 检测通过，当前密码过期时间为 ${CurrentPassMaxDays}天 ，密码过期时间检测正常\"\n    flag=false\n	exit 0\nfi\nif $flag ;then\n    echo \"HummerError: 未设置密码过期时间，当前密码默认过期时间为 ${CurrentPassMaxDays}天 ,为避免密码泄露造成的损失，请及时处理！\"\nfi', '[{\"defaultValue\":\"90\",\"name\":\"密码过期天数\",\"key\":\"PassMaxDays\",\"required\":true}]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('0f91f89e-d39b-4a6e-8311-1070ae2bc85c', 'Linux FTP 匿名访问检测', 1, 'HighRisk', '检测 Linux 操作系统中部署的 FTP 是否开启匿名访问', 'if [[ -f ${{ftpConfig}} ]];then\necho \"已经安装 vsftpd\"\nif [[ `cat /etc/vsftpd/vsftpd.conf|grep anonymous_enable|awk -F= \'{print $2}\'` == \'YES\' ]];then\n  echo \"HummerError: FTP 允许匿名登录，存在风险！\"\nfi\nelse\n  echo \"HummerSuccess: FTP 检测正常!\"\nfi', '[{\"key\":\"ftpConfig\",\"name\":\"FTP 配置文件\",\"defaultValue\":\"/etc/vsftpd/vsftpd.conf\",\"required\":true}]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('10e326fb-8a12-4e4b-9f02-e2ea0e3c7e82', 'Linux 高危端口检测', 1, 'MediumRisk', '查看 Linux 下高风险端口', '#servicePort=\'20,21,22,25,80,773,765,1733,1737,3306,3389,7333,5732,5500\'\nflag=true\nsecurePort=\'\'\nDangerousPort=\'\'\nfor p in $(echo ${{servicePort}}|sed -e \'s/,/ /g\');\ndo\n  #echo $p\n  result=\"$(netstat -tunlp|awk \'{print $4}\'|grep \":${p}\")\"\n  #echo $result\n  if [[ -n $result ]];then\n  DangerousPort+=${p},\n  #echo \"HummerError: 存在高风险端口 ${p}，请及时处理！\"\n  flag=false\n  elif [[ x$result == \'x\' ]] && $flag;then\n  securePort+=${p},\n  #echo \"HummerSuccess: 无高风险端口！\"\n  fi\ndone\nif [[ -n $DangerousPort ]];then\n  echo \"HummerError: 存在高风险端口 ${DangerousPort}，请及时处理！\"\nelse\n  echo \"HummerSuccess: 无高风险端口！\"\nfi', '[{\"defaultValue\":\"20,21,22,25,80,773,765,1733,1737,3306,3389,7333,5732,5500\",\"name\":\"服务端口\",\"key\":\"servicePort\",\"required\":true}]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('11c940a7-24e0-4f9d-a9a9-9e6811b12ad6', 'Linux 主机入侵检测', 1, 'MediumRisk', '检查 Linux 是否安装了主机入侵检测软件', 'flag=true\nfor checkFile in `find / -iname ${value} -print`;do\nif [ -f  ${checkFile} ];then\n    echo \"HummerError: 检测到异常文件 ${checkFile},请及时处理！\"\n    flag=false\n    break\nfi\ndone\nif $flag ;then\n   echo \"HummerSuccess: 未检测到异常文件\"\nfi', '[{\"defaultValue\":\"ddgs*\",\"name\":\"主机入侵检测软件\",\"key\":\"value\",\"required\":true}]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('15370452-3ce3-429c-8743-4a344baddb26', 'Linux 系统密码复杂度检测', 1, 'LowRisk', '查看 Linux 系统密码复杂度要求是否设置', 'grep -i \"^password.*requisite.*pam_cracklib.so\" /etc/pam.d/system-auth > /dev/null\nif [ $? == 0 ];then\n	echo \"HummerSuccess: 密码复杂度:已设置\"\nelse\n	grep -i \"pam_pwquality\\.so\" /etc/pam.d/system-auth > /dev/null\nif [ $? == 0 ];then\n	echo \"HummerSuccess: 密码复杂度:已设置\"\nelse\n	echo \"HummerError: 密码复杂度:未设置,请加固密码--------[需调整]\"\nfi\nfi', '[]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('161b15b6-b5e3-420d-b0fc-de172553df74', 'Linux audit 运行状态检测', 1, 'MediumRisk', '查看 Linux audit 运行状态', 'auditctl -s\nflag=true\nif [[ $(auditctl -s|grep enable|awk \'{print $2}\') != \"1\" ]];then\n    echo \"HummerError: auditctl规则审计功能未开启，请及时处理！\"\n    flag=false\n	exit 0\nfi\nif $flag ;then\n   echo \"HummerSuccess: auditctl规则审计功能已开启\"\nfi', '[]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('17d8a321-4eb2-41cc-a466-e29293dc8755', 'Linux 内存检测', 1, 'HighRisk', 'Linux 下查看内存使用情况', 'memory_used=`head -2 /proc/meminfo | awk \'NR==1{t=$2}NR==2{f=$2;print(t-f)*100/t}\'`\nmemory_cache=`head -5 /proc/meminfo | awk \'NR==1{t=$2}NR==5{c=$2;print c*100/t}\'`\nmemory_buffer=`head -4 /proc/meminfo | awk \'NR==1{t=$2}NR==4{d=$2;print d*100/t}\'`\necho \"memory_used: \" $memory_used\necho \"memory_cache: \" $memory_cache\necho \"memory_buffer: \" $memory_buffer\nflag=true\n\nif [[ `echo ${memory_used} > ${{memoryUsed}}|bc` -eq 1 ]];then\n    echo \"HummerSuccess: 检测通过，ssh服务配置正常\"\n    flag=false\n	exit 0\nfi\nif $flag ;then\n    echo \"HummerError: 检测到内存使用率超出阈值，请及时处理！\"\nfi', '[{\"defaultValue\":\"80\",\"name\":\"内存使用率\",\"key\":\"memoryUsed\",\"required\":true}]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('18365786-1f24-4a42-b5a5-291b9274205a', 'Linux 审计日志检测', 1, 'MediumRisk', '查看 Linux 是否开启审计日志服务', 'flag=true\nif [[ $(service auditd status|grep active|awk \'{ print $2 }\') =~ \"active\" ]];then\n    echo \"HummerSuccess: auditctl规则审计功能已开启\"\n    flag=false\n	exit 0\nfi\nif $flag ;then\n    echo \"HummerError: auditctl规则审计功能未开启，请及时处理！\"\nfi', '[]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('1feaf989-836f-4f90-975d-8caac4b18692', 'Linux 审计规则检测', 1, 'LowRisk', '查看 Linux 服务器审计规则', 'flag=true\nif [[ $(more /etc/audit/audit.rules|wc -l) -gt 3 ]];then\n    echo \"HummerSuccess: auditctl 审计规则已设置\"\n    flag=false\n	exit 0\nfi\nif $flag ;then\n    echo \"HummerError: auditctl 审计规则未设置，请及时处理！\"\nfi', '[]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('20f58992-1d1b-448d-ad85-b80434490f54', 'Linux 大文件日志检测', 1, 'LowRisk', '检测 Linux 系统中占用磁盘空间很大的日志文件', '#logsDir=\"/var/log\"\n#logsSize=\"1\"\nflag=true\nIFS=$\'\\n\\n\'\nfor log in `du -sh ${{logsDir}}/* |sort -h|grep \"G\"|tr -s \" \"`\ndo\n  logSize=$(echo $log|cut -d \" \" -f 2|awk -FG \'{print $1}\')\n  logName=$(echo $log|cut -d \" \" -f 2|awk -FG \'{print $2}\')\n  if [ `echo \"$(echo $log|cut -d \" \" -f 2|awk -FG \'{print $1}\') > ${{logsSize}}\"|bc` -eq 1 ];then\n    echo \"HummerError: 检测到日志文件${logName} 占用磁盘空间过大,大小为 ${logSize} GB,建议留意日志增长情况,及时删除或处理\"\n    flag=false\n  fi\ndone\nif $flag ;then\n   echo \"HummerSuccess: 日志文件检测正常!\"\nfi', '[{\"defaultValue\":\"5\",\"name\":\"日志大小,单位 G\",\"key\":\"logsSize\",\"required\":true},{\"defaultValue\":\"/var/log\",\"name\":\"日志目录\",\"key\":\"logsDir\",\"required\":true}]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('26cf695f-fca6-4097-bf02-f9cb30f79c0d', 'Linux 开机自启项检测', 1, 'LowRisk', '查看 Linux 开机自启项', 'systemctl list-unit-files\nflag=true\nif [[ $(systemctl list-unit-files|grep ${{service}}|awk \'{print $2}\') =~ \"enabled\" ]];then\n    echo \"HummerSuccess: 检测通过，${{service}} 服务已经是开机自动启动\"\n    flag=false\n	exit 0\nfi\nif $flag ;then\n    echo \"HummerError: 检测失败，${{service}} 服务未开机自动启动，请及时处理！\"\nfi', '[{\"defaultValue\":\"sshd.service\",\"name\":\"服务名\",\"key\":\"service\",\"required\":true}]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('5319e63e-27ac-4899-94e0-7bd9ac0e8740', 'Linux Redis 密码配置检测', 1, 'HighRisk', '检测 Linux 系统中部署的 Redis 是否配置密码验证', '#redisConfig=\'/usr/local/etc/redis/redis.conf\'\nif [[ -f ${{redisConfig}} ]];then\n if [[ `cat $redisConfig|grep -v \"#^\"|grep requirepass` ]];then\n  echo \"HummerSuccess: 密码验证检测正常!\"\n else\n   \"HummerError: 未设置 redis 访问密码，容易造成数据泄露风险！\"\n fi\n else\n  echo \"HummerSuccess: 密码验证检测正常!\"\nfi', '[{\"key\":\"redisConfig\",\"name\":\"Redis 配置文件\",\"defaultValue\":\"/usr/local/etc/redis/redis.conf\",\"required\":true}]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('562fd271-6484-42db-b72b-08d679cb4e03', 'Linux 入侵检测', 1, 'MediumRisk', '查看 Linux 入侵检测的措施', 'flag=true\nif [[ $(more /var/log/secure | grep refused|wc -l) -lt 5 ]];then\n    echo \"HummerSuccess: 检测通过，没有异常登录\"\n    flag=false\n	exit 0\nfi\nif $flag ;then\n    echo \"HummerError: 存在多次登录身份验证失败，请及时处理！\"\nfi', '[]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('5ca75854-b03c-4e9f-9ec7-4bf77f16d94d', 'Linux SSH 暴力登入检测', 1, 'HighRisk', '查看 Linux 操作系统是否存在 SSH 暴力登入信息', 'flag=true\nif more /var/log/secure | grep \"Failed\" > /dev/null;then\n    more /var/log/secure|awk \'/Failed/{print $(NF-3)}\'|sort|uniq -c|awk \'{print \"HummerError: 登入失败的IP和尝试次数: \"$2\"=\"$1\"次---------[需调整]\";}\'\n    flag=false\n	exit 0\nfi\nif $flag ;then\n    echo \"HummerSuccess: 检测通过，无ssh暴力登入信息\"\nfi', '[]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('6970cdce-fd54-4853-9f89-9e70b43aa07e', 'Linux kernel 版本检测', 1, 'MediumRisk', '检测 Linux 操作系统的内核版本', 'kernelMainVersion=`uname -r|awk -F. \'{print $1}\'`\nkernelMinorVersion=`uname -r|awk -F. \'{print $2}\'`\n#kernelVersion=3.20\ntargetMainVersion=$(echo ${{kernelVersion}}|awk -F. \'{print $1}\')\ntargetMinorVersion=$(echo ${{kernelVersion}}|awk -F. \'{print $2}\')\n\nif [[ $kernelMainVersion -lt $targetMainVersion ]];then\n  echo \"HummerError: 当前内核版本为 ${kernelMainVersion}.${kernelMinorVersion} ,内核版本太低，请及时处理！\"\nelif [[ $kernelMainVersion -eq $targetMainVersion ]];then\n  if [[ $kernelMinorVersion -eq $targetMinorVersion ]];then\n  echo \"HummerSuccess: 当前内核版本为 ${kernelMainVersion}.${kernelMinorVersion} ,内核版本检查正常！\"\n  fi\n  if [[ $kernelMinorVersion -lt $targetMinorVersion ]];then\n    echo \"HummerError: 当前内核版本为 ${kernelMainVersion}.${kernelMinorVersion} ,内核版本太低，请及时处理！\"\n  fi\nelse\n  echo \"HummerSuccess: 当前内核版本为 ${kernelMainVersion}.${kernelMinorVersion} ,内核版本检查正常！\"\nfi', '[{\"defaultValue\":\"3.10\",\"name\":\"内核版本\",\"key\":\"kernelVersion\",\"required\":true}]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('6f378375-bbe2-47f7-85fc-4a8e8c8193c6', 'Linux 磁盘使用率检测', 1, 'LowRisk', '检测 Linux 操作系统的磁盘使用率是否超过阈值', 'flag=true\nIFS=$\'\\n\\n\'\nfor d in $(df |grep /|tr -s \" \");do\n  if [[ $(echo $d|cut -d \" \" -f 5|awk -F% \'{print $1}\') -gt ${{diskUse}} ]];then\n  diskName=`echo $d|awk \'{print $6}\'`\n  echo \"HummerError: 检测磁盘 ${diskName} 使用率超过 ${{diskUse}}%，建议观察磁盘使用情况，或进行扩容\"\n  flag=false\n  break\n  fi\ndone\nif $flag ;then\n   echo \"HummerSuccess: 磁盘空间检测正常\"\nfi', '[{\"defaultValue\":\"80\",\"name\":\"磁盘使用率，百分百1-100\",\"key\":\"diskUse\",\"required\":true}]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('73fdb432-62c4-4b08-9682-44dae7e6c312', 'Linux CPU 检测', 1, 'HighRisk', 'Linux 下查看 CPU 使用率', 'cpu=`top -b -n5 | fgrep \"Cpu(s)\" | tail -1 | awk -F\'id,\' \'{split($1, vs, \",\"); v=vs[length(vs)]; sub(/\\s+/, \"\", v);sub(/\\s+/, \"\", v); printf \"%d\", 100-v;}\'`\nflag=true\n\nif [[ ${cpu} -lt ${{cpuUse}}  ]];then\n    echo \"HummerSuccess: 检测通过，CPU资源空闲正常\"\n    flag=false\n	exit 0\nfi\nif $flag ;then\n    echo \"HummerError: CPU使用率过高，当前使用率 ${cpu}% ,请及时处理！\"\nfi', '[{\"defaultValue\":\"80\",\"name\":\"CPU使用率\",\"key\":\"cpuUse\",\"required\":true}]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('7a9cfc62-3c78-4f08-8210-d9bbfe47980c', 'Linux 防火墙检测', 1, 'HighRisk', 'Linux 下查看防火墙是否开启，以及防火墙规则', 'flag=true\nif [[ $(service firewalld status|grep active|awk \'{ print $2 }\') =~ \"active\" ]];then\n    echo \"HummerSuccess: firewalld 防火墙功能已开启\"\n    flag=false\n	exit 0\nfi\nif $flag ;then\n    echo \"HummerError: firewalld 功能未开启，请及时处理！\"\nfi', '[]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('7f742d41-d91d-4597-8462-25a89f8841e0', 'Linux SSH 远程密码登录检测', 1, 'LowRisk', '检测 Linux 系统是否运行 SSH 远程密码登录', 'flag=true\nif [[ $(grep -i \"^PasswordAuthentication\" /etc/ssh/sshd_config|awk \'{print $2}\') =~ no ]];then\n    echo \"HummerSuccess: 检测通过，SSH服务配置正常\"\n    flag=false\n	exit 0\nfi\nif $flag ;then\n    echo \"HummerError: 当前设置运行使用密码进行远程登录，安全性较低，建议使用SSH密钥登录，请及时处理！\"\nfi', '[]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('87ecea8f-3ba1-48d0-8fcd-b3d74e1dae60', 'Linux 资源限定检测', 1, 'MediumRisk', '查看 Linux 下是否对资源进行了限定', 'flag=true\nif [[ $(more /etc/security/limits.conf|grep -v \'^#\'|grep -v \'^$\'|wc -l) -gt 10 ]];then\n    echo \"HummerSuccess: 检测通过，已经配置资源限定规则\"\n    flag=false\n	exit 0\nfi\nif $flag ;then\n    echo \"HummerError: 未配置资源限定规则，请及时处理！\"\nfi', '[]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('98122047-afd0-4b12-8bfd-7a13ee177f72', 'Linux 磁盘 iNode 使用率检测', 1, 'HighRisk', '检测Linux操纵系统中磁盘的 iNode 使用情况', 'flag=true\nIFS=$\'\\n\\n\'\nfor d in $(df -i |grep /|tr -s \" \");do\n  if [[ $(echo $d|tr -s \' \'|cut -d\' \' -f5|awk -F% \'{print $1}\') -gt ${{diskInodeUse}} ]];then\n  diskName=`echo $d|awk \'{print $6}\'`\n  echo \"HummerError: 检测磁盘 ${diskName} iNode 使用率超过 ${{diskInodeUse}}%，建议观察磁盘使用情况，或进行扩容!\"\n  flag=false\n  break\n  fi\ndone\nif $flag ;then\n   echo \"HummerSuccess: 磁盘 inode 检测正常!\"\nfi', '[{\"defaultValue\":\"80\",\"name\":\"Disk inode使用率\",\"key\":\"diskInodeUse\",\"required\":true}]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('9eacb3a6-6129-44b9-b29a-c07ad449b063', 'Linux 消耗内存进程检测', 1, 'MediumRisk', '查看 Linux 消耗内存最多的进程', 'flag=true\nIFS=$\'\\n\\n\'\nfor d in $(ps auxf |sort -nr -k ${{taskNum}} |head -5|tr -s \" \");do\n  if [ `echo \"$(ps auxf |sort -nr -k 4 |head -5|tr -s \" \"|head -1|cut -d \" \" -f 4|awk -F% \'{print $1}\') > ${{memoryUse}}\"|bc` -eq 1 ];then\n  memName=`echo $d|awk \'{print $6}\'`\n  echo \"HummerError: 检测内存 ${memName} 使用率超过 ${{memoryUse}}%，建议观察内存使用情况，或进行扩容\"\n  flag=false\n  break\n  fi\ndone\nif $flag ;then\n   echo \"HummerSuccess: 内存检测正常\"\nfi', '[{\"defaultValue\":\"70\",\"name\":\"内存使用率%\",\"key\":\"memoryUse\",\"required\":true},{\"defaultValue\":\"5\",\"name\":\"进程数量\",\"key\":\"taskNum\",\"required\":true}]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('a1c9a8d3-97d2-4556-abdb-5d77ea46bad5', 'Linux CPU 消耗进程检测', 1, 'MediumRisk', '查看 Linux 系统 CPU消耗最多的进程', 'flag=true\nIFS=$\'\\n\\n\'\nfor d in $(ps auxf |sort -nr -k 3 |head -${{taskNum}}|tr -s \" \");do\n  if [ `echo \"$(ps auxf |sort -nr -k 3 |head -5|tr -s \" \"|head -1|cut -d \" \" -f 4|awk -F% \'{print $1}\') > ${{cpuUse}}\"|bc` -eq 1 ];then\n  cpuName=`echo $d|awk \'{print $6}\'`\n  echo \"HummerError: 检测CPU ${cpuName} 使用率超过 ${{cpuUse}}%，建议观察CPU使用情况，或进行扩容\"\n  flag=false\n  break\n  fi\ndone\nif $flag ;then\n   echo \"HummerSuccess: CPU检测正常\"\nfi', '[{\"defaultValue\":\"5\",\"name\":\"进程数量\",\"key\":\"taskNum\",\"required\":true},{\"defaultValue\":\"80\",\"name\":\"CPU使用率\",\"key\":\"cpuUse\",\"required\":true}]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('ba903623-9d68-4939-ba39-fa34628c2d1d', 'Linux 操作系统类型检测', 1, 'LowRisk', '查看 Linux 检测操作系统类型', 'OS=\"$(uname)\"\ncase $OS in\n  \"Linux\")\n    echo \"HummerSuccess: 当前系统为 Linux\";;\n  \"FreeBSD\")\n    echo \"HummerSuccess: 当前系统为 FreeBSD\";;\n  \"WindowsNT\")\n    echo \"HummerSuccess: 当前系统为 WindowsNT\";;\n  \"Darwin\")\n    echo \"HummerSuccess: 当前系统为 Darwin\";;\n  \"SunOS\")\n    echo \"HummerSuccess: 当前系统为 SunOS\";;\n  \"AIX\")\n    echo \"HummerSuccess: 当前系统为 AIX\";;\n  *)\n    echo \"HummerError: 未知的操作系统\";;\nesac', '[]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('bb389762-8756-405d-b37c-fc789d1cc9cc', 'Linux audit 规则检测', 1, 'LowRisk', '查看 Linux 系统服务器审计规则', 'flag=true\nif [[ $(auditctl -l|wc -l) -gt 1 ]];then\n    echo \"HummerSuccess: auditctl 审计规则已设置\"\n    flag=false\n	exit 0\nfi\nif $flag ;then\n    echo \"HummerError: auditctl 审计规则未设置，请及时处理！\"\nfi', '[]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('c497a676-12ec-41ed-8944-eb32cc95b8a4', 'Linux 僵尸进程检测', 1, 'MediumRisk', '查看 Linux 僵尸进程', 'flag=true\nif [[ $(ps aux | awk \'{ print $8 \" \" $2 }\' | grep -w Z) == \"\" ]];then\n    echo \"HummerSuccess: 检测通过，无 Linux 僵尸进程\"\n    flag=false\n	exit 0\nfi\nif $flag ;then\n    echo \"HummerError: 检测到僵尸进程 $(ps aux | awk \'{ print $8 \" \" $2 }\' | grep -w Z)\"\nfi', '[]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('f00222ed-4a39-43be-b2a8-5b5b72529296', 'Linux PID 检测', 1, 'LowRisk', '查看 Linux PID资源使用情况', 'use_num=$(ps -eLf | wc -l)\nsys_num=$(cat /proc/sys/kernel/pid_max)\nif [[ $use_num -gt $sys_num ]];then\n	echo \"HummerError: PID 耗尽，将会影响程序正常运行\"\nelse\n  	echo \"HummerSuccess: PID 资源正常\"\nfi', '[]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('f45cc8e2-867f-4e97-8998-ce7f0c070d2d', 'Linux 日志检测', 1, 'MediumRisk', '查看 Linux 系统日志是否运行', 'flag=true\nif [[ $(systemctl status rsyslog|grep Active|awk \'{print $2}\') =~ \"active\" ]];then\n    echo \"HummerSuccess: 检测通过，rsyslog 日志服务已经启动\"\n    flag=false\n	exit 0\nfi\nif $flag ;then\n    echo \"HummerError: 检测失败，rsyslog 日志服务未启动，请及时处理！\"\nfi', '[]', concat(unix_timestamp(now()), '002'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('fdf46bee-0ae7-416f-85c8-b17c8dfa08ba', 'Linux 内存溢出检测', 1, 'MediumRisk', '查看 Linux 系统进程是否存在内存溢出', 'flag=true\nif [[ $(grep \"Out of memory\" /var/log/messages) == \"\" ]];then\n    echo \"HummerSuccess: 检测通过,没有发现有内存溢出情况\"\n    flag=false\n	exit 0\nfi\nif $flag ;then\n    echo \"HummerError: 存在内存溢出，请及时处理！\"\n	echo \"$(grep \"Out of memory\" /var/log/messages)\"\nfi', '[]', concat(unix_timestamp(now()), '002'), 1);

-- ----------------------------
-- aws rule group
-- ----------------------------
DELETE FROM `rule_group` WHERE `name` = 'AWS Prowler - AWS Security Tool';

DELETE FROM `rule` WHERE `id` in ('ebc80d1e-5dd9-4b86-8037-fb7c9727084a', '9929cc2a-7a9b-4643-a813-0e4359b09eb0', '8ad73e84-141a-40c4-bd56-1813535e8e92', 'ee74d071-d39e-4b0e-b4a3-e7ad24fbce12', '0dc8ae24-71f8-449a-8834-d59f8fbdf991',
                                '1fbd8c76-90e7-48e4-8281-ef597bb484e8', '6bb291bd-fcdf-4aa9-8fdf-69e761c9c4e0', '350a097a-dfeb-4fd0-a3af-49049da5c025', 'fa60960a-0e38-4502-9834-1ab1277e9aaa', '39c87069-a3c6-4ab3-9974-f844a89872b5',
                                '10754933-241e-42db-bb3c-42e13cb40b0f', '7c37887f-0b8d-444c-bad7-e43cd2b41578', '197e41e7-cbc3-4eef-b5ad-5153c9232449', '4a64e90e-92ea-4d5a-a753-3c5ca92a2b95', '72af2230-8555-4e0c-a06a-721436a0644e',
                                '0a5a7796-6b53-408a-ba49-7fd9e51d82f4', 'a944a483-c23f-4d62-bdd8-d2a20a4aaeca', '9b1d1a9c-6c7e-44db-a15c-c0c82b1040eb', '0d51321e-e26c-4147-b730-5b1403384487', '0d6270fc-3744-42cd-a850-65ac0b8ef514',
                                'c1375dfe-c0b1-4e53-9c2b-6a0f29258a08', '1ab302fb-1b06-410a-b17a-fdf7843d6182', 'f38d83c9-c4b9-47b8-8135-f9bdf96cf8ec', 'e86583b9-12dd-4316-980a-5eeeba86f9ca');

DELETE FROM `rule_tag_mapping` WHERE `rule_id` in ('ebc80d1e-5dd9-4b86-8037-fb7c9727084a', '9929cc2a-7a9b-4643-a813-0e4359b09eb0', '8ad73e84-141a-40c4-bd56-1813535e8e92', 'ee74d071-d39e-4b0e-b4a3-e7ad24fbce12', '0dc8ae24-71f8-449a-8834-d59f8fbdf991',
                                '1fbd8c76-90e7-48e4-8281-ef597bb484e8', '6bb291bd-fcdf-4aa9-8fdf-69e761c9c4e0', '350a097a-dfeb-4fd0-a3af-49049da5c025', 'fa60960a-0e38-4502-9834-1ab1277e9aaa', '39c87069-a3c6-4ab3-9974-f844a89872b5',
                                '10754933-241e-42db-bb3c-42e13cb40b0f', '7c37887f-0b8d-444c-bad7-e43cd2b41578', '197e41e7-cbc3-4eef-b5ad-5153c9232449', '4a64e90e-92ea-4d5a-a753-3c5ca92a2b95', '72af2230-8555-4e0c-a06a-721436a0644e',
                                '0a5a7796-6b53-408a-ba49-7fd9e51d82f4', 'a944a483-c23f-4d62-bdd8-d2a20a4aaeca', '9b1d1a9c-6c7e-44db-a15c-c0c82b1040eb', '0d51321e-e26c-4147-b730-5b1403384487', '0d6270fc-3744-42cd-a850-65ac0b8ef514',
                                'c1375dfe-c0b1-4e53-9c2b-6a0f29258a08', '1ab302fb-1b06-410a-b17a-fdf7843d6182', 'f38d83c9-c4b9-47b8-8135-f9bdf96cf8ec', 'e86583b9-12dd-4316-980a-5eeeba86f9ca');

DELETE FROM `rule_group_mapping` WHERE `rule_id` in ('ebc80d1e-5dd9-4b86-8037-fb7c9727084a', '9929cc2a-7a9b-4643-a813-0e4359b09eb0', '8ad73e84-141a-40c4-bd56-1813535e8e92', 'ee74d071-d39e-4b0e-b4a3-e7ad24fbce12', '0dc8ae24-71f8-449a-8834-d59f8fbdf991',
                                                   '1fbd8c76-90e7-48e4-8281-ef597bb484e8', '6bb291bd-fcdf-4aa9-8fdf-69e761c9c4e0', '350a097a-dfeb-4fd0-a3af-49049da5c025', 'fa60960a-0e38-4502-9834-1ab1277e9aaa', '39c87069-a3c6-4ab3-9974-f844a89872b5',
                                                   '10754933-241e-42db-bb3c-42e13cb40b0f', '7c37887f-0b8d-444c-bad7-e43cd2b41578', '197e41e7-cbc3-4eef-b5ad-5153c9232449', '4a64e90e-92ea-4d5a-a753-3c5ca92a2b95', '72af2230-8555-4e0c-a06a-721436a0644e',
                                                   '0a5a7796-6b53-408a-ba49-7fd9e51d82f4', 'a944a483-c23f-4d62-bdd8-d2a20a4aaeca', '9b1d1a9c-6c7e-44db-a15c-c0c82b1040eb', '0d51321e-e26c-4147-b730-5b1403384487', '0d6270fc-3744-42cd-a850-65ac0b8ef514',
                                                   'c1375dfe-c0b1-4e53-9c2b-6a0f29258a08', '1ab302fb-1b06-410a-b17a-fdf7843d6182', 'f38d83c9-c4b9-47b8-8135-f9bdf96cf8ec', 'e86583b9-12dd-4316-980a-5eeeba86f9ca');

DELETE FROM `rule_type` WHERE `rule_id` in ('ebc80d1e-5dd9-4b86-8037-fb7c9727084a', '9929cc2a-7a9b-4643-a813-0e4359b09eb0', '8ad73e84-141a-40c4-bd56-1813535e8e92', 'ee74d071-d39e-4b0e-b4a3-e7ad24fbce12', '0dc8ae24-71f8-449a-8834-d59f8fbdf991',
                                                     '1fbd8c76-90e7-48e4-8281-ef597bb484e8', '6bb291bd-fcdf-4aa9-8fdf-69e761c9c4e0', '350a097a-dfeb-4fd0-a3af-49049da5c025', 'fa60960a-0e38-4502-9834-1ab1277e9aaa', '39c87069-a3c6-4ab3-9974-f844a89872b5',
                                                     '10754933-241e-42db-bb3c-42e13cb40b0f', '7c37887f-0b8d-444c-bad7-e43cd2b41578', '197e41e7-cbc3-4eef-b5ad-5153c9232449', '4a64e90e-92ea-4d5a-a753-3c5ca92a2b95', '72af2230-8555-4e0c-a06a-721436a0644e',
                                                     '0a5a7796-6b53-408a-ba49-7fd9e51d82f4', 'a944a483-c23f-4d62-bdd8-d2a20a4aaeca', '9b1d1a9c-6c7e-44db-a15c-c0c82b1040eb', '0d51321e-e26c-4147-b730-5b1403384487', '0d6270fc-3744-42cd-a850-65ac0b8ef514',
                                                     'c1375dfe-c0b1-4e53-9c2b-6a0f29258a08', '1ab302fb-1b06-410a-b17a-fdf7843d6182', 'f38d83c9-c4b9-47b8-8135-f9bdf96cf8ec', 'e86583b9-12dd-4316-980a-5eeeba86f9ca');

DELETE FROM `rule_inspection_report_mapping` WHERE `rule_id` in ('ebc80d1e-5dd9-4b86-8037-fb7c9727084a', '9929cc2a-7a9b-4643-a813-0e4359b09eb0', '8ad73e84-141a-40c4-bd56-1813535e8e92', 'ee74d071-d39e-4b0e-b4a3-e7ad24fbce12', '0dc8ae24-71f8-449a-8834-d59f8fbdf991',
                                            '1fbd8c76-90e7-48e4-8281-ef597bb484e8', '6bb291bd-fcdf-4aa9-8fdf-69e761c9c4e0', '350a097a-dfeb-4fd0-a3af-49049da5c025', 'fa60960a-0e38-4502-9834-1ab1277e9aaa', '39c87069-a3c6-4ab3-9974-f844a89872b5',
                                            '10754933-241e-42db-bb3c-42e13cb40b0f', '7c37887f-0b8d-444c-bad7-e43cd2b41578', '197e41e7-cbc3-4eef-b5ad-5153c9232449', '4a64e90e-92ea-4d5a-a753-3c5ca92a2b95', '72af2230-8555-4e0c-a06a-721436a0644e',
                                            '0a5a7796-6b53-408a-ba49-7fd9e51d82f4', 'a944a483-c23f-4d62-bdd8-d2a20a4aaeca', '9b1d1a9c-6c7e-44db-a15c-c0c82b1040eb', '0d51321e-e26c-4147-b730-5b1403384487', '0d6270fc-3744-42cd-a850-65ac0b8ef514',
                                            'c1375dfe-c0b1-4e53-9c2b-6a0f29258a08', '1ab302fb-1b06-410a-b17a-fdf7843d6182', 'f38d83c9-c4b9-47b8-8135-f9bdf96cf8ec', 'e86583b9-12dd-4316-980a-5eeeba86f9ca');

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('27.0 AWS Implementation Group 安全检查', 'AWS 基于最新的 CIS Controls v8，它们映射到多个法律、监管和政策框架并被多个框架引用，更适用于云和混合云的结构。CIS Implementation Group', 'CIS', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('26.0 AWS 服务目录安全检查', 'AWS Directory Service 提供了多种方式来结合使用 AD 与其他 AWS 服务，本规则组检测与AWS Directory Service 相关的安全问题。Directory Service related security checks', '最佳实践', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('25.0 AWS FTR 安全检查', 'AWS FTR(基础技术审查) 根据一组特定的 AWS 最佳实践评估 AWS 合作伙伴的解决方案，本规则组检测合作伙伴安全相关的问题。Amazon FTR related security checks', '最佳实践', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('24.0 AWS Glue 安全检查', 'AWS 对 Glue 相关的资源进行安全检测，发现存在的安全隐患。Glue related security checks', '最佳实践', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('23.0 AWS ENS 安全检查', 'AWS ENS (Esquema Nacional de Seguridad) 是一项西班牙认证，有出海西班牙的业务可以通过本规则组进行合规检测。ENS Esquema Nacional de Seguridad security checks', '最佳实践', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('22.0 AWS SageMaker 安全检查', 'AWS 对 SageMaker 相关资源进行安全检测，发现存在安全隐患。SageMaker related security checks', '最佳实践', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('21.0 AWS SOC2 安全检查', 'AWS 基于 SOC2 框架,主要评估与安全性、可用性、处理完整性、机密性和隐私相关的组织信息系统，可作为 SOC2 的参考资料。SOC2 Readiness - ONLY AS REFERENCE', '最佳实践', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('20.0 AWS FFIEC 网络安全检查', 'AWS 基于 FFIEC(Federal Financial Institutions Examination Council1) 网络安全评估工具域实现。FFIEC Cybersecurity Readiness', '最佳实践', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('19.0 AWS EKS 基准检查', 'AWS 基于 CIS EKS Benchmark，可帮助您准确评估 Amazon EKS 集群的安全配置，包括对节点的安全评估，以帮助满足安全性和合规性要求。CIS EKS Benchmark', 'CIS', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('18.0 AWS ISO 27001 安全检查', 'AWS ISO 27001:2013 规定了在组织范围内建立、实施、维护和持续改进信息安全管理系统的要求，本规则组围绕ISO27001，对 AWS 资源进行全的安全检测，可做为 ISO27001的参考。ISO 27001:2013 Readiness', '最佳实践', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('17.0 AWS 暴露的网络资源检查', 'AWS 检测是否资源暴露到公共网络中，针对资源、安全组、访问认证等多个方面进行检测。Find resources exposed to the internet', '最佳实践', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('16.0 AWS 跨账户信任边界', 'AWS 针对多账户使用的场景，检测在多个账号下的安全边界，发现安全风险。Find cross-account trust boundaries', '最佳实践', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('15.0 AWS PCI-DSS v3.2.1 安全检查', 'AWS 基于 PCI-DSS v3.2.1 (支付卡行业数据安全标准 3.2.1 版) 对 AWS 账号中资源进行安全检测。PCI-DSS v3.2.1 Readiness', '最佳实践', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('14.0 AWS Elasticsearch 安全检查', 'AWS 围绕 AWS Elasticsearch 相关资源进行安全检测，例如开放访问、点对点加密、开启HTTPS、认证方式等方面。Elasticsearch related security checks', '安全合规', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('13.0 AWS RDS 安全检查', 'AWS 围绕 RDS 进行安全检测，例如 RDS 的开放暴露、是否加密、自动备份开启等。RDS security checks', '安全合规', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('12.0 AWS API Gateway 安全检查', 'AWS 围绕 API Gateway 进行安全检测，包括 API 的认证、加密、开放访问、配置监控等内容。API Gateway security checks', '安全合规', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('11.0 AWS 密钥/密码安全检查', 'AWS 检测账号资源中的密码和密钥，确认没有任何潜在的安全风险，例如在配置中硬编码致使密码泄露登。Look for keys secrets or passwords around resources', '安全合规', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('10.0 AWS HIPAA 安全检查', 'AWS HIPAA 即《健康保险携带和责任法案》，是一套与医疗信息隐私和安全有关的法规。本规则组以 AWS 的 HIPAA 安全合规架构白皮书为基础，结果作为 HIPAA 合规性的参考。HIPAA Compliance - ONLY AS REFERENCE', '安全合规', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('9.0 AWS GDPR 安全检查', 'AWS《一般数据保护条例》(GDPR) 是将于 2018 年 5 月生效的一部新的欧洲数据保护法，本规则作为 GDPR 合规的参考性检测组，帮助用户快速自我检测。GDPR Readiness - ONLY AS REFERENCE', '安全合规', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('8.0 AWS Forensics 安全检查', 'AWS 对取证相关的内容进行检测。Forensics Readiness', '安全合规', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('7.0 AWS 所有非 CIS 特定检查', 'AWS 包括了全部非 CIS 检测内容的规则。Extras - all non CIS specific checks', '安全合规', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('6.0 AWS CIS Level 2', 'AWS 围绕 CIS Benchmarks Level 2 进行配置的规则组，对需要更高安全性的环境进行安全设置，可能会导致某些功能减少。CIS Level 2 - CIS only', 'CIS', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('5.0 AWS CIS Level 1', 'AWS 围绕 CIS Benchmarks Level 1 进行配置的规则组，以降低组织的攻击面，同时保持机器可用且不妨碍业务功能为目的，去进行检测。CIS Level 1 - CIS only', 'CIS', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('4.0 AWS Networking 安全检查', 'AWS 基于 CIS AWS Benchmark 中关于 Networking 的部分，对各种 AWS 网络相关内容进行安全检测。Networking - CIS only', 'CIS', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('3.0 AWS Monitoring 安全检查', 'AWS 基于 CIS AWS Benchmark 中关于 monitoring 的部分，对各种云资源监控的相关内容进行安全检测。Monitoring - CIS only', 'CIS', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('2.0 AWS Logging 安全检查', 'AWS 基于 CIS AWS Benchmark 中关于 logging的部分，对日志及监控的相关内容进行安全检测。Logging - CIS only', 'CIS', 'hummer-aws-plugin', 1);
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('1.0 AWS IAM 安全检查', 'AWS 围绕 CIS 给出的 Aamazon Web Service Benchmarks 中关于 IAM 的部分。Identity and Access Management - CIS only', 'CIS', 'hummer-aws-plugin', 1);


INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('77047e0e-88da-422e-93db-2d3a0606e961', '1.14 AWS Hardware MFA 检测', 1, 'CriticalRisk', 'AWS 确保为 root 帐户启用了硬件 MFA，Ensure hardware MFA is enabled for the root account。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.14 [check114] Ensure hardware MFA is enabled for the root account - iam [Critical]', '[{\"defaultValue\":\"check114\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('d80b3e78-6dfc-429c-bcdd-50ec212cfc42', '1.18 AWS IAM 安全联系人检测', 1, 'MediumRisk', 'AWS 确保已注册安全联系人信息，Ensure security contact information is registered。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.18 [check118] Ensure security contact information is registered - support [Medium]', '[{\"defaultValue\":\"check118\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('705c1cef-b47b-4e0b-ab42-d1b360e6502e', '1.5 AWS IAM 密码策略(大写)检测', 1, 'MediumRisk', 'AWS 确保 IAM 密码策略至少需要一个大写字母，Ensure IAM password policy requires at least one uppercase letter。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.5 [check15] Ensure IAM password policy requires at least one uppercase letter - iam [Medium]', '[{\"defaultValue\":\"check15\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('d8a2d0ca-4f5b-4809-8321-5ef485b2bf21', '1.6 AWS IAM 密码策略(小写)检测', 1, 'MediumRisk', 'AWS 确保 IAM 密码策略至少需要一个小写字母，Ensure IAM password policy require at least one lowercase letter。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.6 [check16] Ensure IAM password policy require at least one lowercase letter - iam [Medium]', '[{\"defaultValue\":\"check16\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('67a2b3ad-6974-442c-9f03-32f0a8cd4f4d', '1.8 AWS IAM 密码策略(数字)检测', 1, 'MediumRisk', 'AWS 确保 IAM 密码策略至少需要一个数字，Ensure IAM password policy require at least one number。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.8 [check18] Ensure IAM password policy require at least one number - iam [Medium]', '[{\"defaultValue\":\"check18\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('03979d46-6325-4b20-8d77-0e904a986064', '1.7 AWS IAM 密码策略(符号)检测', 1, 'MediumRisk', 'AWS 确保 IAM 密码策略至少需要一个符号，Ensure IAM password policy require at least one symbol。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.7 [check17] Ensure IAM password policy require at least one symbol - iam [Medium]', '[{\"defaultValue\":\"check17\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('bb3eed24-02b1-43d1-9057-f4f800e4660e', '1.11 AWS IAM 密码策略(过期)检测', 1, 'MediumRisk', 'AWS 确保 IAM 密码策略在 90 天或更短时间内过期，Ensure IAM password policy expires passwords within 90 days or less。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.11 [check111] Ensure IAM password policy expires passwords within 90 days or less - iam [Medium]', '[{\"defaultValue\":\"check111\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('b8844aed-9da9-469b-8ec8-a69fb1a2f7f2', '1.10 AWS IAM 密码策略(重用)检测', 1, 'MediumRisk', 'AWS 确保 IAM 密码策略防止密码重用：24 或更大，Ensure IAM password policy prevents password reuse: 24 or greater。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.10 [check110] Ensure IAM password policy prevents password reuse: 24 or greater - iam [Medium]', '[{\"defaultValue\":\"check110\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('bd18fd9b-06af-4c98-8198-26b3aa3b78bc', '1.9 AWS IAM 密码策略(长度)检测', 1, 'MediumRisk', 'AWS 确保 IAM 密码策略要求最小长度为 14 或更大，Ensure IAM password policy requires minimum length of 14 or greater。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.9 [check19] Ensure IAM password policy requires minimum length of 14 or greater - iam [Medium]', '[{\"defaultValue\":\"check19\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('f8579451-2c24-4743-87e3-0fcf7c465d08', '1.20 AWS IAM 支持角色(Support 事件)检测', 1, 'MediumRisk', 'AWS 确保已创建支持角色来管理 Support 事件，Ensure a support role has been created to manage incidents with Support。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.20 [check120] Ensure a support role has been created to manage incidents with AWS Support - iam [Medium]', '[{\"defaultValue\":\"check120\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('e3fdf46f-782c-439f-b09f-2a616d42697d', '1.16 AWS IAM 策略(组或角色)检测', 1, 'LowRisk', 'AWS 确保 IAM 策略仅附加到组或角色，Ensure security questions are registered in the AWS account。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.16 [check116] Ensure IAM policies are attached only to groups or roles - iam [Low]', '[{\"defaultValue\":\"check116\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('065d41f3-ff24-4e2e-989f-df71c45e3853', '1.17 AWS IAM 联系方式检测', 1, 'MediumRisk', 'AWS 维护当前的联系方式，Maintain current contact details。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.17 [check117] Maintain current contact details - support [Medium]', '[{\"defaultValue\":\"check117\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('71427f80-643c-4e8e-9cb8-f07f544b2825', '1.19 AWS IAM 访问实例(角色)检测', 1, 'MediumRisk', 'AWS 确保 IAM 角色从实例访问 AWS 资源，Ensure IAM instance roles are used for AWS resource access from instances。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.19 [check119] Ensure IAM instance roles are used for AWS resource access from instances - ec2 [Medium]', '[{\"defaultValue\":\"check119\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('d2628999-b60d-4e7f-812d-0cd5a94a7e38', '1.21 AWS IAM 访问密钥(初始用户设置)检测', 1, 'MediumRisk', 'AWS 在初始用户设置期间不要为所有具有控制台密码的 IAM 用户设置访问密钥。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.21 [check121] Do not setup access keys during initial user setup for all IAM users that have a console password - iam [Medium]', '[{\"defaultValue\":\"check121\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('eac1ba91-eb46-4c44-b1f4-e709ea9d0e1e', '1.22 AWS IAM 访问策略(管理权限)检测', 1, 'MediumRisk', 'AWS 确保未创建允许完全“*：*”管理权限的策略，Ensure policies that allow full \"*:*\" privileges not created。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.22 [check122] Ensure IAM policies that allow full \"*:*\" administrative privileges are not created - iam [Medium]', '[{\"defaultValue\":\"check122\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('22dd87f6-353c-42a7-a253-3957b17c1bb4', '1.13 AWS MFA 启用检测', 1, 'CriticalRisk', 'AWS 确保为 root 帐户启用 MFA，Ensure no root account access key exists。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.13 [check113] Ensure MFA is enabled for the root account - iam [Critical]', '[{\"defaultValue\":\"check113\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('48e028e7-9eff-4c98-8a08-2c696e2842f7', '1.1 AWS Root Account 检测', 1, 'HighRisk', 'AWS 避免使用 root 帐户，Avoid the use of the root account。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.1 [check11] Avoid the use of the root account - iam [High]', '[{\"key\":\"check\",\"name\":\"检测规则\",\"defaultValue\":\"check11\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('f2e017f1-70cb-4ad1-a187-8895c8040945', '1.3 AWS 凭证使用时间检测', 1, 'MediumRisk', 'AWS 确保禁用 90 天或更长时间未使用的凭据，Ensure credentials unused for 90 days or greater are disabled。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.3 [check13] Ensure credentials unused for 90 days or greater are disabled - iam [Medium]', '[{\"defaultValue\":\"check13\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('b9f4c9f9-0fb6-47d6-af8a-57e9b8902245', '1.2 AWS 多因素认证 MFA 检测', 1, 'HighRisk', 'AWS 检测是否开启 MFA，Ensure (MFA) is enabled for all IAM users that have a console password。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.2 [check12] Ensure multi-factor authentication (MFA) is enabled for all IAM users that have a console password - iam [High]', '[{\"defaultValue\":\"check12\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('c66cbddd-5846-4fb9-bedb-8a6812c51c5e', '1.4 AWS 密钥使用时间检测', 1, 'MediumRisk', 'AWS 确保访问密钥每 90 天或更短时间轮换一次，Ensure access keys are rotated every 90 days or less。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.4 [check14] Ensure access keys are rotated every 90 days or less - iam [Medium]', '[{\"defaultValue\":\"check14\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('531cefda-5723-4a81-9ec1-5eb0c797b9a0', '1.15 AWS 注册安全检测', 1, 'MediumRisk', 'AWS 确保在 AWS 账户中注册安全问题，Ensure security questions are registered in the AWS account。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.15 [check115] Ensure security questions are registered in the AWS account - support [Medium]', '[{\"defaultValue\":\"check115\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('3bd0f4f5-eb7f-48d5-b3db-1fd8cee80e07', '1.12 AWS 访问密钥检测', 1, 'CriticalRisk', 'AWS 确保不存在 root 帐户访问密钥，Ensure no root account access key exists。', '1.0 Identity and Access Management - CIS only - [group1] *********** -  []\n\n1.12 [check112] Ensure no root account access key exists - iam [Critical]', '[{\"defaultValue\":\"check112\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '002'), 1, 'prowler', NULL);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('48e028e7-9eff-4c98-8a08-2c696e2842f7', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('b9f4c9f9-0fb6-47d6-af8a-57e9b8902245', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('c66cbddd-5846-4fb9-bedb-8a6812c51c5e', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('f2e017f1-70cb-4ad1-a187-8895c8040945', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('d8a2d0ca-4f5b-4809-8321-5ef485b2bf21', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('705c1cef-b47b-4e0b-ab42-d1b360e6502e', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('03979d46-6325-4b20-8d77-0e904a986064', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('67a2b3ad-6974-442c-9f03-32f0a8cd4f4d', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('bd18fd9b-06af-4c98-8198-26b3aa3b78bc', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('b8844aed-9da9-469b-8ec8-a69fb1a2f7f2', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('bb3eed24-02b1-43d1-9057-f4f800e4660e', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('3bd0f4f5-eb7f-48d5-b3db-1fd8cee80e07', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('22dd87f6-353c-42a7-a253-3957b17c1bb4', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('77047e0e-88da-422e-93db-2d3a0606e961', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('531cefda-5723-4a81-9ec1-5eb0c797b9a0', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('e3fdf46f-782c-439f-b09f-2a616d42697d', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('065d41f3-ff24-4e2e-989f-df71c45e3853', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('d80b3e78-6dfc-429c-bcdd-50ec212cfc42', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('71427f80-643c-4e8e-9cb8-f07f544b2825', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('f8579451-2c24-4743-87e3-0fcf7c465d08', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('d2628999-b60d-4e7f-812d-0cd5a94a7e38', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('eac1ba91-eb46-4c44-b1f4-e709ea9d0e1e', 'safety');


INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b9f4c9f9-0fb6-47d6-af8a-57e9b8902245', '90');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c66cbddd-5846-4fb9-bedb-8a6812c51c5e', '114');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f2e017f1-70cb-4ad1-a187-8895c8040945', '114');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d8a2d0ca-4f5b-4809-8321-5ef485b2bf21', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('705c1cef-b47b-4e0b-ab42-d1b360e6502e', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('03979d46-6325-4b20-8d77-0e904a986064', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('67a2b3ad-6974-442c-9f03-32f0a8cd4f4d', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('bd18fd9b-06af-4c98-8198-26b3aa3b78bc', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b8844aed-9da9-469b-8ec8-a69fb1a2f7f2', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b8844aed-9da9-469b-8ec8-a69fb1a2f7f2', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b8844aed-9da9-469b-8ec8-a69fb1a2f7f2', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('bb3eed24-02b1-43d1-9057-f4f800e4660e', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('bb3eed24-02b1-43d1-9057-f4f800e4660e', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('bb3eed24-02b1-43d1-9057-f4f800e4660e', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('3bd0f4f5-eb7f-48d5-b3db-1fd8cee80e07', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('3bd0f4f5-eb7f-48d5-b3db-1fd8cee80e07', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('3bd0f4f5-eb7f-48d5-b3db-1fd8cee80e07', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('3bd0f4f5-eb7f-48d5-b3db-1fd8cee80e07', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('22dd87f6-353c-42a7-a253-3957b17c1bb4', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('22dd87f6-353c-42a7-a253-3957b17c1bb4', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('22dd87f6-353c-42a7-a253-3957b17c1bb4', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('22dd87f6-353c-42a7-a253-3957b17c1bb4', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('77047e0e-88da-422e-93db-2d3a0606e961', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('77047e0e-88da-422e-93db-2d3a0606e961', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('77047e0e-88da-422e-93db-2d3a0606e961', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('77047e0e-88da-422e-93db-2d3a0606e961', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('531cefda-5723-4a81-9ec1-5eb0c797b9a0', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('531cefda-5723-4a81-9ec1-5eb0c797b9a0', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('531cefda-5723-4a81-9ec1-5eb0c797b9a0', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('531cefda-5723-4a81-9ec1-5eb0c797b9a0', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e3fdf46f-782c-439f-b09f-2a616d42697d', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e3fdf46f-782c-439f-b09f-2a616d42697d', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e3fdf46f-782c-439f-b09f-2a616d42697d', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e3fdf46f-782c-439f-b09f-2a616d42697d', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('065d41f3-ff24-4e2e-989f-df71c45e3853', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('065d41f3-ff24-4e2e-989f-df71c45e3853', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('065d41f3-ff24-4e2e-989f-df71c45e3853', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('065d41f3-ff24-4e2e-989f-df71c45e3853', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d80b3e78-6dfc-429c-bcdd-50ec212cfc42', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d80b3e78-6dfc-429c-bcdd-50ec212cfc42', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d80b3e78-6dfc-429c-bcdd-50ec212cfc42', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d80b3e78-6dfc-429c-bcdd-50ec212cfc42', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('71427f80-643c-4e8e-9cb8-f07f544b2825', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('71427f80-643c-4e8e-9cb8-f07f544b2825', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('71427f80-643c-4e8e-9cb8-f07f544b2825', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('71427f80-643c-4e8e-9cb8-f07f544b2825', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f8579451-2c24-4743-87e3-0fcf7c465d08', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f8579451-2c24-4743-87e3-0fcf7c465d08', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f8579451-2c24-4743-87e3-0fcf7c465d08', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f8579451-2c24-4743-87e3-0fcf7c465d08', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d2628999-b60d-4e7f-812d-0cd5a94a7e38', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d2628999-b60d-4e7f-812d-0cd5a94a7e38', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d2628999-b60d-4e7f-812d-0cd5a94a7e38', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d2628999-b60d-4e7f-812d-0cd5a94a7e38', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('eac1ba91-eb46-4c44-b1f4-e709ea9d0e1e', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('eac1ba91-eb46-4c44-b1f4-e709ea9d0e1e', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('eac1ba91-eb46-4c44-b1f4-e709ea9d0e1e', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('eac1ba91-eb46-4c44-b1f4-e709ea9d0e1e', '96');

SELECT id INTO @groupId1 from rule_group where name = '1.0 AWS IAM 安全检查';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('48e028e7-9eff-4c98-8a08-2c696e2842f7', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b9f4c9f9-0fb6-47d6-af8a-57e9b8902245', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c66cbddd-5846-4fb9-bedb-8a6812c51c5e', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f2e017f1-70cb-4ad1-a187-8895c8040945', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8a2d0ca-4f5b-4809-8321-5ef485b2bf21', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('705c1cef-b47b-4e0b-ab42-d1b360e6502e', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('03979d46-6325-4b20-8d77-0e904a986064', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('67a2b3ad-6974-442c-9f03-32f0a8cd4f4d', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bd18fd9b-06af-4c98-8198-26b3aa3b78bc', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b8844aed-9da9-469b-8ec8-a69fb1a2f7f2', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bb3eed24-02b1-43d1-9057-f4f800e4660e', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('3bd0f4f5-eb7f-48d5-b3db-1fd8cee80e07', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('22dd87f6-353c-42a7-a253-3957b17c1bb4', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('77047e0e-88da-422e-93db-2d3a0606e961', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('531cefda-5723-4a81-9ec1-5eb0c797b9a0', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e3fdf46f-782c-439f-b09f-2a616d42697d', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('065d41f3-ff24-4e2e-989f-df71c45e3853', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d80b3e78-6dfc-429c-bcdd-50ec212cfc42', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('71427f80-643c-4e8e-9cb8-f07f544b2825', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f8579451-2c24-4743-87e3-0fcf7c465d08', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d2628999-b60d-4e7f-812d-0cd5a94a7e38', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('eac1ba91-eb46-4c44-b1f4-e709ea9d0e1e', @groupId1);


INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('79790c74-501c-4830-bba6-ca635c0885f5', '2.9 AWS VPC 流日志检测', 1, 'MediumRisk', 'AWS 确保在所有 VPC 中启用 VPC 流日志记录，Ensure VPC Flow Logging is Enabled in all VPCs。', '2.0 Logging - CIS only - [group2] ********************************** -  []\n\n2.9 [check29] Ensure VPC Flow Logging is Enabled in all VPCs - vpc [Medium]', '[{\"defaultValue\":\"check29\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '003'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('e7b41c37-410e-4fa8-85bd-2452c1076957', '2.8 AWS KMS CMK (轮换)检测', 1, 'MediumRisk', 'AWS 确保启用客户创建的 KMS CMK 的轮换，Ensure rotation for customer created KMS CMKs is enabled。', '2.0 Logging - CIS only - [group2] ********************************** -  []\n\n2.8 [check28] Ensure rotation for customer created KMS CMKs is enabled - kms [Medium]', '[{\"defaultValue\":\"check28\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '003'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('1ca7f983-69ea-47ed-9f9a-5a6e7317d8bd', '2.7 AWS KMS CMK 检测', 1, 'MediumRisk', 'AWS 确保使用 KMS CMK 对 CloudTrail 日志进行静态加密，Ensure CloudTrail logs are encrypted at rest using KMS CMKs。', '2.0 Logging - CIS only - [group2] ********************************** -  []\n\n2.7 [check27] Ensure CloudTrail logs are encrypted at rest using KMS CMKs - cloudtrail [Medium]', '[{\"defaultValue\":\"check27\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '003'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('79d8e033-ddca-49e6-a658-76b5c0e90e09', '2.6 AWS CloudTrail S3 检测', 1, 'MediumRisk', 'AWS 确保在 CloudTrail S3 存储桶上启用 S3 存储桶访问日志记录，Ensure S3 bucket access logging is enabled on the CloudTrail S3 bucket。', '2.0 Logging - CIS only - [group2] ********************************** -  []\n\n2.6 [check26] Ensure S3 bucket access logging is enabled on the CloudTrail S3 bucket - s3 [Medium]', '[{\"defaultValue\":\"check26\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '003'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('d8fd6c3d-5faf-4394-897d-7fd84d33550d', '2.5 AWS 启用 Config 检测', 1, 'MediumRisk', 'AWS 确保在所有区域启用 AWS Config，Ensure AWS Config is enabled in all regions。', '2.0 Logging - CIS only - [group2] ********************************** -  []\n\n2.5 [check25] Ensure AWS Config is enabled in all regions - configservice [Medium]', '[{\"defaultValue\":\"check25\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '003'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('a2df85e7-0f0a-4283-84f2-81ee336736e2', '2.4 AWS CloudTrail (CloudWatch Logs 集成)检测', 1, 'LowRisk', 'AWS 确保 CloudTrail 跟踪 CloudWatch Logs，Ensure CloudTrail trails are integrated with CloudWatch Logs。', '2.0 Logging - CIS only - [group2] ********************************** -  []\n\n2.4 [check24] Ensure CloudTrail trails are integrated with CloudWatch Logs - cloudtrail [Low]', '[{\"defaultValue\":\"check24\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '003'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('96b70632-7ccb-4fe9-af1f-58a8be26eeaf', '2.3 AWS CloudTrail 日志记录(S3 存储桶)检测', 1, 'CriticalRisk', 'AWS 确保 CloudTrail 日志记录不可公开访问 S3，Ensure the S3 bucket CloudTrail logs to is not publicly accessible。', '2.0 Logging - CIS only - [group2] ********************************** -  []\n\n2.3 [check23] Ensure the S3 bucket CloudTrail logs to is not publicly accessible - cloudtrail [Critical]', '[{\"defaultValue\":\"check23\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '003'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('e554b261-0e89-42eb-a44c-4a6d00a32f85', '2.2 AWS CloudTrail 日志文件检测', 1, 'MediumRisk', 'AWS 确保启用 CloudTrail 日志文件验证，Ensure CloudTrail log file validation is enabled。', '2.0 Logging - CIS only - [group2] ********************************** -  []\n\n2.2 [check22] Ensure CloudTrail log file validation is enabled - cloudtrail [Medium]', '[{\"defaultValue\":\"check22\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '003'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('8883c71f-7d42-4007-9152-1138c3fb7ab3', '2.1 AWS 启用 CloudTrail 检测', 1, 'HighRisk', 'AWS 确保在所有区域启用 CloudTrail，Ensure CloudTrail is enabled in all regions。', '2.0 Logging - CIS only - [group2] ********************************** -  []\n\n2.1 [check21] Ensure CloudTrail is enabled in all regions - cloudtrail [High]', '[{\"defaultValue\":\"check21\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '003'), 1, 'prowler', NULL);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('8883c71f-7d42-4007-9152-1138c3fb7ab3', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('e554b261-0e89-42eb-a44c-4a6d00a32f85', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('96b70632-7ccb-4fe9-af1f-58a8be26eeaf', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('a2df85e7-0f0a-4283-84f2-81ee336736e2', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('d8fd6c3d-5faf-4394-897d-7fd84d33550d', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('79d8e033-ddca-49e6-a658-76b5c0e90e09', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('1ca7f983-69ea-47ed-9f9a-5a6e7317d8bd', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('e7b41c37-410e-4fa8-85bd-2452c1076957', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('79790c74-501c-4830-bba6-ca635c0885f5', 'safety');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('8883c71f-7d42-4007-9152-1138c3fb7ab3', '119');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('8883c71f-7d42-4007-9152-1138c3fb7ab3', '118');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('8883c71f-7d42-4007-9152-1138c3fb7ab3', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e554b261-0e89-42eb-a44c-4a6d00a32f85', '119');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e554b261-0e89-42eb-a44c-4a6d00a32f85', '118');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e554b261-0e89-42eb-a44c-4a6d00a32f85', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('96b70632-7ccb-4fe9-af1f-58a8be26eeaf', '119');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('96b70632-7ccb-4fe9-af1f-58a8be26eeaf', '118');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('96b70632-7ccb-4fe9-af1f-58a8be26eeaf', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('a2df85e7-0f0a-4283-84f2-81ee336736e2', '119');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('a2df85e7-0f0a-4283-84f2-81ee336736e2', '118');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('a2df85e7-0f0a-4283-84f2-81ee336736e2', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d8fd6c3d-5faf-4394-897d-7fd84d33550d', '119');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d8fd6c3d-5faf-4394-897d-7fd84d33550d', '118');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d8fd6c3d-5faf-4394-897d-7fd84d33550d', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d8fd6c3d-5faf-4394-897d-7fd84d33550d', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('79d8e033-ddca-49e6-a658-76b5c0e90e09', '119');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('79d8e033-ddca-49e6-a658-76b5c0e90e09', '118');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('79d8e033-ddca-49e6-a658-76b5c0e90e09', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('79d8e033-ddca-49e6-a658-76b5c0e90e09', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1ca7f983-69ea-47ed-9f9a-5a6e7317d8bd', '119');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1ca7f983-69ea-47ed-9f9a-5a6e7317d8bd', '118');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1ca7f983-69ea-47ed-9f9a-5a6e7317d8bd', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1ca7f983-69ea-47ed-9f9a-5a6e7317d8bd', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e7b41c37-410e-4fa8-85bd-2452c1076957', '119');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e7b41c37-410e-4fa8-85bd-2452c1076957', '118');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e7b41c37-410e-4fa8-85bd-2452c1076957', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e7b41c37-410e-4fa8-85bd-2452c1076957', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('79790c74-501c-4830-bba6-ca635c0885f5', '119');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('79790c74-501c-4830-bba6-ca635c0885f5', '118');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('79790c74-501c-4830-bba6-ca635c0885f5', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('79790c74-501c-4830-bba6-ca635c0885f5', '93');

SELECT id INTO @groupId2 from rule_group where name = '2.0 AWS Logging 安全检查';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8883c71f-7d42-4007-9152-1138c3fb7ab3', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e554b261-0e89-42eb-a44c-4a6d00a32f85', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('96b70632-7ccb-4fe9-af1f-58a8be26eeaf', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a2df85e7-0f0a-4283-84f2-81ee336736e2', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8fd6c3d-5faf-4394-897d-7fd84d33550d', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('79d8e033-ddca-49e6-a658-76b5c0e90e09', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1ca7f983-69ea-47ed-9f9a-5a6e7317d8bd', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e7b41c37-410e-4fa8-85bd-2452c1076957', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('79790c74-501c-4830-bba6-ca635c0885f5', @groupId2);

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('2d040c4f-0486-413f-8738-5bc1a2a3e842', '3.14 AWS VPC 监控检测', 1, 'MediumRisk', 'AWS 确保存在针对 VPC 更改的日志指标过滤器和警报，Ensure a log metric filter and alarm exist for VPC changes。', '3.0 Monitoring - CIS only - [group3] ******************************* -  []\n\n3.14 [check314] Ensure a log metric filter and alarm exist for VPC changes - vpc [Medium]', '[{\"defaultValue\":\"check314\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '004'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('67a51dbc-16a2-4c35-a494-06f60cb7ca7e', '3.13 AWS 路由表监控检测', 1, 'MediumRisk', 'AWS 确保路由表更改存在日志指标过滤器和警报，Ensure a log metric filter and alarm exist for route table changes。', '3.0 Monitoring - CIS only - [group3] ******************************* -  []\n\n3.13 [check313] Ensure a log metric filter and alarm exist for route table changes - vpc [Medium]', '[{\"defaultValue\":\"check313\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '004'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('c4605126-0a03-420e-a9af-a808c9031138', '3.12 AWS 网络网关监控检测', 1, 'MediumRisk', 'AWS 确保存在日志指标过滤器和警报以应对网络网关的更改，Ensure a log metric filter and alarm exist for changes to network gateways。', '3.0 Monitoring - CIS only - [group3] ******************************* -  []\n\n3.12 [check312] Ensure a log metric filter and alarm exist for changes to network gateways - vpc [Medium]', '[{\"defaultValue\":\"check312\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '004'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('5578d38b-9966-498d-8c5d-f1615a817f87', '3.11 AWS 网络访问控制列表 (NACL) 监控检测', 1, 'MediumRisk', 'AWS 确保对网络访问控制列表 (NACL) 的更改存在日志指标过滤器和警报，Ensure a log metric filter and alarm exist for changes to Network Access Control Lists (NACL)。', '3.0 Monitoring - CIS only - [group3] ******************************* -  []\n\n3.11 [check311] Ensure a log metric filter and alarm exist for changes to Network Access Control Lists (NACL) - vpc [Medium]', '[{\"defaultValue\":\"check311\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '004'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('f79aada7-27fb-47c6-9827-07fa035198bc', '3.10 AWS 安全组监控检测', 1, 'MediumRisk', 'AWS 确保存在针对安全组更改的日志指标过滤器和警报，Ensure a log metric filter and alarm exist for security group changes。', '3.0 Monitoring - CIS only - [group3] ******************************* -  []\n\n3.10 [check310] Ensure a log metric filter and alarm exist for security group changes - ec2 [Medium]', '[{\"defaultValue\":\"check310\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '004'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('1a201a3a-153d-4246-834f-c1b8f247443d', '3.9 AWS Config 配置监控检测', 1, 'MediumRisk', 'AWS 确保 Config 配置更改存在日志指标过滤器和警报，Ensure a log metric filter and alarm exist for AWS Config configuration changes。', '3.0 Monitoring - CIS only - [group3] ******************************* -  []\n\n3.9 [check39] Ensure a log metric filter and alarm exist for AWS Config configuration changes - configservice [Medium]', '[{\"defaultValue\":\"check39\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '004'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('edb2c0ee-f36a-4864-b66d-bee3cd9730b0', '3.8 WS S3 存储桶策略监控检测', 1, 'MediumRisk', 'AWS 确保 S3 存储桶策略更改存在日志指标过滤器和警报，Ensure a log metric filter and alarm exist for S3 bucket policy changes。', '3.0 Monitoring - CIS only - [group3] ******************************* -  []\n\n3.8 [check38] Ensure a log metric filter and alarm exist for S3 bucket policy changes - s3 [Medium]', '[{\"defaultValue\":\"check38\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '004'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('eb0f3859-8b0d-41e1-b85f-f829ceb99801', '3.7 AWS KMS CMK 监控检测', 1, 'MediumRisk', 'AWS 确保存在日志指标过滤器和警报，以禁用或计划删除客户创建的 KMS CMK，Ensure a log metric filter and alarm exist for disabling or scheduled deletion of customer created KMS CMKs。', '3.0 Monitoring - CIS only - [group3] ******************************* -  []\n\n3.7 [check37] Ensure a log metric filter and alarm exist for disabling or scheduled deletion of customer created KMS CMKs - kms [Medium]', '[{\"defaultValue\":\"check37\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '004'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('e82e163e-4428-49d8-8986-1d06906c165c', '3.6 AWS 管理控制台身份监控检测', 1, 'MediumRisk', 'AWS 确保存在针对管理控制台身份验证失败的日志指标过滤器和警报，Ensure a log metric filter and alarm exist for AWS Management Console authentication failures。', '3.0 Monitoring - CIS only - [group3] ******************************* -  []\n\n3.6 [check36] Ensure a log metric filter and alarm exist for AWS Management Console authentication failures - iam [Medium]', '[{\"defaultValue\":\"check36\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '004'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('ed2a0376-2f23-4ca6-9746-b909db67a2ab', '3.5 AWS CloudTrail 配置监控检测', 1, 'MediumRisk', 'AWS 确保 CloudTrail 配置更改存在日志指标过滤器和警报，Ensure a log metric filter and alarm exist for CloudTrail configuration changes。', '3.0 Monitoring - CIS only - [group3] ******************************* -  []\n\n3.5 [check35] Ensure a log metric filter and alarm exist for CloudTrail configuration changes - cloudtrail [Medium]', '[{\"defaultValue\":\"check35\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '004'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('a8bb65f2-8715-4f86-97ad-0655f7271d9a', '3.4 AWS IAM 策略监控检测', 1, 'MediumRisk', 'AWS 确保 IAM 策略更改存在日志指标过滤器和警报，Ensure a log metric filter and alarm exist for IAM policy changes。', '3.0 Monitoring - CIS only - [group3] ******************************* -  []\n\n3.4 [check34] Ensure a log metric filter and alarm exist for IAM policy changes - iam [Medium]', '[{\"defaultValue\":\"check34\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '004'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('1d1974d5-3e7c-4891-ab99-e817292b73ed', '3.3 AWS root 账号监控检测', 1, 'MediumRisk', 'AWS 确保存在用于 root 帐户使用的日志指标过滤器和警报，Ensure a log metric filter and alarm exist for usage of root account。', '3.0 Monitoring - CIS only - [group3] ******************************* -  []\n\n3.3 [check33] Ensure a log metric filter and alarm exist for usage of root account - iam [Medium]', '[{\"defaultValue\":\"check33\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '004'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('14983a14-0f6b-45d8-827b-105368869ae9', '3.2 AWS 登录管理(MFA)监控检测', 1, 'MediumRisk', 'AWS 确保在没有 MFA 的情况下登录管理控制台存在日志指标过滤器和警报，Ensure a log metric filter and alarm exist for Management Console sign-in without MFA。', '3.0 Monitoring - CIS only - [group3] ******************************* -  []\n\n3.2 [check32] Ensure a log metric filter and alarm exist for Management Console sign-in without MFA - iam [Medium]', '[{\"defaultValue\":\"check32\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '004'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('f7f19d65-3561-463b-b3ca-218239268d66', '3.1 AWS API 调用监控检测', 1, 'MediumRisk', 'AWS 确保存在针对未经授权的 API 调用的日志指标过滤器和警报，Ensure a log metric filter and alarm exist for unauthorized API calls。', '3.0 Monitoring - CIS only - [group3] ******************************* -  []\n\n3.1 [check31] Ensure a log metric filter and alarm exist for unauthorized API calls - iam [Medium]', '[{\"defaultValue\":\"check31\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '004'), 1, 'prowler', NULL);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('f7f19d65-3561-463b-b3ca-218239268d66', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('14983a14-0f6b-45d8-827b-105368869ae9', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('1d1974d5-3e7c-4891-ab99-e817292b73ed', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('a8bb65f2-8715-4f86-97ad-0655f7271d9a', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('ed2a0376-2f23-4ca6-9746-b909db67a2ab', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('e82e163e-4428-49d8-8986-1d06906c165c', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('eb0f3859-8b0d-41e1-b85f-f829ceb99801', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('edb2c0ee-f36a-4864-b66d-bee3cd9730b0', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('1a201a3a-153d-4246-834f-c1b8f247443d', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('f79aada7-27fb-47c6-9827-07fa035198bc', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('5578d38b-9966-498d-8c5d-f1615a817f87', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('c4605126-0a03-420e-a9af-a808c9031138', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('67a51dbc-16a2-4c35-a494-06f60cb7ca7e', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('2d040c4f-0486-413f-8738-5bc1a2a3e842', 'safety');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f7f19d65-3561-463b-b3ca-218239268d66', '71');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f7f19d65-3561-463b-b3ca-218239268d66', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('14983a14-0f6b-45d8-827b-105368869ae9', '71');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('14983a14-0f6b-45d8-827b-105368869ae9', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1d1974d5-3e7c-4891-ab99-e817292b73ed', '71');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1d1974d5-3e7c-4891-ab99-e817292b73ed', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('a8bb65f2-8715-4f86-97ad-0655f7271d9a', '71');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('a8bb65f2-8715-4f86-97ad-0655f7271d9a', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ed2a0376-2f23-4ca6-9746-b909db67a2ab', '71');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ed2a0376-2f23-4ca6-9746-b909db67a2ab', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e82e163e-4428-49d8-8986-1d06906c165c', '71');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e82e163e-4428-49d8-8986-1d06906c165c', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('eb0f3859-8b0d-41e1-b85f-f829ceb99801', '71');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('eb0f3859-8b0d-41e1-b85f-f829ceb99801', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('edb2c0ee-f36a-4864-b66d-bee3cd9730b0', '71');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('edb2c0ee-f36a-4864-b66d-bee3cd9730b0', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('edb2c0ee-f36a-4864-b66d-bee3cd9730b0', '80');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('edb2c0ee-f36a-4864-b66d-bee3cd9730b0', '66');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1a201a3a-153d-4246-834f-c1b8f247443d', '71');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1a201a3a-153d-4246-834f-c1b8f247443d', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1a201a3a-153d-4246-834f-c1b8f247443d', '80');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1a201a3a-153d-4246-834f-c1b8f247443d', '91');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f79aada7-27fb-47c6-9827-07fa035198bc', '71');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f79aada7-27fb-47c6-9827-07fa035198bc', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f79aada7-27fb-47c6-9827-07fa035198bc', '80');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f79aada7-27fb-47c6-9827-07fa035198bc', '91');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f79aada7-27fb-47c6-9827-07fa035198bc', '69');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f79aada7-27fb-47c6-9827-07fa035198bc', '70');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f79aada7-27fb-47c6-9827-07fa035198bc', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5578d38b-9966-498d-8c5d-f1615a817f87', '71');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5578d38b-9966-498d-8c5d-f1615a817f87', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5578d38b-9966-498d-8c5d-f1615a817f87', '80');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5578d38b-9966-498d-8c5d-f1615a817f87', '91');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5578d38b-9966-498d-8c5d-f1615a817f87', '13');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5578d38b-9966-498d-8c5d-f1615a817f87', '14');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5578d38b-9966-498d-8c5d-f1615a817f87', '37');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5578d38b-9966-498d-8c5d-f1615a817f87', '76');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5578d38b-9966-498d-8c5d-f1615a817f87', '75');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5578d38b-9966-498d-8c5d-f1615a817f87', '17');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5578d38b-9966-498d-8c5d-f1615a817f87', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5578d38b-9966-498d-8c5d-f1615a817f87', '110');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c4605126-0a03-420e-a9af-a808c9031138', '71');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c4605126-0a03-420e-a9af-a808c9031138', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c4605126-0a03-420e-a9af-a808c9031138', '80');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c4605126-0a03-420e-a9af-a808c9031138', '91');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c4605126-0a03-420e-a9af-a808c9031138', '123');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c4605126-0a03-420e-a9af-a808c9031138', '102');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('67a51dbc-16a2-4c35-a494-06f60cb7ca7e', '71');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('67a51dbc-16a2-4c35-a494-06f60cb7ca7e', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('67a51dbc-16a2-4c35-a494-06f60cb7ca7e', '80');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('67a51dbc-16a2-4c35-a494-06f60cb7ca7e', '91');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('67a51dbc-16a2-4c35-a494-06f60cb7ca7e', '123');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('67a51dbc-16a2-4c35-a494-06f60cb7ca7e', '102');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2d040c4f-0486-413f-8738-5bc1a2a3e842', '71');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2d040c4f-0486-413f-8738-5bc1a2a3e842', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2d040c4f-0486-413f-8738-5bc1a2a3e842', '80');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2d040c4f-0486-413f-8738-5bc1a2a3e842', '91');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2d040c4f-0486-413f-8738-5bc1a2a3e842', '123');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2d040c4f-0486-413f-8738-5bc1a2a3e842', '102');

SELECT id INTO @groupId3 from rule_group where name = '3.0 AWS Monitoring 安全检查';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f7f19d65-3561-463b-b3ca-218239268d66', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('14983a14-0f6b-45d8-827b-105368869ae9', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1d1974d5-3e7c-4891-ab99-e817292b73ed', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a8bb65f2-8715-4f86-97ad-0655f7271d9a', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ed2a0376-2f23-4ca6-9746-b909db67a2ab', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e82e163e-4428-49d8-8986-1d06906c165c', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('eb0f3859-8b0d-41e1-b85f-f829ceb99801', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('edb2c0ee-f36a-4864-b66d-bee3cd9730b0', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1a201a3a-153d-4246-834f-c1b8f247443d', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f79aada7-27fb-47c6-9827-07fa035198bc', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5578d38b-9966-498d-8c5d-f1615a817f87', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c4605126-0a03-420e-a9af-a808c9031138', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('67a51dbc-16a2-4c35-a494-06f60cb7ca7e', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2d040c4f-0486-413f-8738-5bc1a2a3e842', @groupId3);

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('42e93327-e951-4a6e-b04d-73504f66c380', '4.4 AWS VPC (路由表)检测', 1, 'MediumRisk', 'AWS 确保 VPC 对等互连的路由表是“最少访问”，Ensure routing tables for VPC peering are \"least access\"。', '4.0 Networking - CIS only - [group4] ******************************* -  []\n\n4.4 [check44] Ensure routing tables for VPC peering are \"least access\" - vpc [Medium]', '[{\"defaultValue\":\"check44\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '005'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('ef3af2e9-ebbe-42f2-b99c-20f53caa9b2f', '4.6 AWS 网络 ACL (3389)检测', 1, 'HighRisk', 'AWS 确保没有网络 ACL 允许从 0.0.0.0/0 进入 Microsoft RDP 端口 3389，Ensure no Network ACLs allow ingress from 0.0.0.0/0 to Microsoft RDP port 3389。', '4.0 Networking - CIS only - [group4] ******************************* -  []\n\n4.6 [check46] Ensure no Network ACLs allow ingress from 0.0.0.0/0 to Microsoft RDP port 3389 - ec2 [High]', '[{\"defaultValue\":\"check46\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '005'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('7445f1b3-22dd-4359-9940-3aeedef82f05', '4.5 AWS 网络 ACL (22)检测', 1, 'HighRisk', 'AWS 确保没有网络 ACL 允许从 0.0.0.0/0 进入 SSH 端口 22，Ensure no Network ACLs allow ingress from 0.0.0.0/0 to SSH port 22。', '4.0 Networking - CIS only - [group4] ******************************* -  []\n\n4.5 [check45] Ensure no Network ACLs allow ingress from 0.0.0.0/0 to SSH port 22 - ec2 [High]', '[{\"defaultValue\":\"check45\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '005'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('d8bf919b-cf2c-4930-872b-3949143040d6', '4.2 AWS 安全组(3389)检测', 1, 'HighRisk', 'AWS 确保没有安全组允许从 0.0.0.0/0 或 ::/0 进入端口 3389，Ensure no security groups allow ingress from 0.0.0.0/0 or ::/0 to port 3389 。', '4.0 Networking - CIS only - [group4] ******************************* -  []\n\n4.2 [check42] Ensure no security groups allow ingress from 0.0.0.0/0 or ::/0 to port 3389 - ec2 [High]', '[{\"defaultValue\":\"check42\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '005'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('ef4311ae-7fd1-4866-b9f3-3e5b89975c44', '4.1 AWS 安全组(22)检测', 1, 'HighRisk', 'AWS 确保没有安全组允许从 0.0.0.0/0 或 ::/0 进入端口 22，Ensure no security groups allow ingress from 0.0.0.0/0 or ::/0 to port 22。', '4.0 Networking - CIS only - [group4] ******************************* -  []\n\n4.1 [check41] Ensure no security groups allow ingress from 0.0.0.0/0 or ::/0 to port 22 - ec2 [High]', '[{\"defaultValue\":\"check41\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '005'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('acba4a64-2bcc-4bbf-bb97-7983cc4c929b', '4.3 AWS 安全组(VPC)检测', 1, 'HighRisk', 'AWS 确保每个 VPC 的默认安全组限制所有流量，Ensure the default security group of every VPC restricts all traffic。', '4.0 Networking - CIS only - [group4] ******************************* -  []\n\n4.3 [check43] Ensure the default security group of every VPC restricts all traffic - ec2 [High]', '[{\"defaultValue\":\"check43\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '005'), 1, 'prowler', NULL);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('acba4a64-2bcc-4bbf-bb97-7983cc4c929b', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('ef4311ae-7fd1-4866-b9f3-3e5b89975c44', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('d8bf919b-cf2c-4930-872b-3949143040d6', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('7445f1b3-22dd-4359-9940-3aeedef82f05', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('ef3af2e9-ebbe-42f2-b99c-20f53caa9b2f', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('42e93327-e951-4a6e-b04d-73504f66c380', 'safety');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('acba4a64-2bcc-4bbf-bb97-7983cc4c929b', '69');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('acba4a64-2bcc-4bbf-bb97-7983cc4c929b', '70');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('acba4a64-2bcc-4bbf-bb97-7983cc4c929b', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ef4311ae-7fd1-4866-b9f3-3e5b89975c44', '69');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ef4311ae-7fd1-4866-b9f3-3e5b89975c44', '70');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ef4311ae-7fd1-4866-b9f3-3e5b89975c44', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d8bf919b-cf2c-4930-872b-3949143040d6', '69');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d8bf919b-cf2c-4930-872b-3949143040d6', '70');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d8bf919b-cf2c-4930-872b-3949143040d6', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('7445f1b3-22dd-4359-9940-3aeedef82f05', '69');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('7445f1b3-22dd-4359-9940-3aeedef82f05', '70');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('7445f1b3-22dd-4359-9940-3aeedef82f05', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ef3af2e9-ebbe-42f2-b99c-20f53caa9b2f', '69');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ef3af2e9-ebbe-42f2-b99c-20f53caa9b2f', '70');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ef3af2e9-ebbe-42f2-b99c-20f53caa9b2f', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('42e93327-e951-4a6e-b04d-73504f66c380', '24');

SELECT id INTO @groupId4 from rule_group where name = '4.0 AWS Networking 安全检查';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('acba4a64-2bcc-4bbf-bb97-7983cc4c929b', @groupId4);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ef4311ae-7fd1-4866-b9f3-3e5b89975c44', @groupId4);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8bf919b-cf2c-4930-872b-3949143040d6', @groupId4);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7445f1b3-22dd-4359-9940-3aeedef82f05', @groupId4);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ef3af2e9-ebbe-42f2-b99c-20f53caa9b2f', @groupId4);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('42e93327-e951-4a6e-b04d-73504f66c380', @groupId4);

SELECT id INTO @groupId5 from rule_group where name = '5.0 AWS CIS Level 1';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f8579451-2c24-4743-87e3-0fcf7c465d08', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('eac1ba91-eb46-4c44-b1f4-e709ea9d0e1e', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8883c71f-7d42-4007-9152-1138c3fb7ab3', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('96b70632-7ccb-4fe9-af1f-58a8be26eeaf', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a2df85e7-0f0a-4283-84f2-81ee336736e2', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8fd6c3d-5faf-4394-897d-7fd84d33550d', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('79d8e033-ddca-49e6-a658-76b5c0e90e09', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f7f19d65-3561-463b-b3ca-218239268d66', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c4605126-0a03-420e-a9af-a808c9031138', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('67a51dbc-16a2-4c35-a494-06f60cb7ca7e', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2d040c4f-0486-413f-8738-5bc1a2a3e842', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('14983a14-0f6b-45d8-827b-105368869ae9', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1d1974d5-3e7c-4891-ab99-e817292b73ed', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a8bb65f2-8715-4f86-97ad-0655f7271d9a', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ed2a0376-2f23-4ca6-9746-b909db67a2ab', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('edb2c0ee-f36a-4864-b66d-bee3cd9730b0', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ef4311ae-7fd1-4866-b9f3-3e5b89975c44', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8bf919b-cf2c-4930-872b-3949143040d6', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('48e028e7-9eff-4c98-8a08-2c696e2842f7', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b8844aed-9da9-469b-8ec8-a69fb1a2f7f2', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bb3eed24-02b1-43d1-9057-f4f800e4660e', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('3bd0f4f5-eb7f-48d5-b3db-1fd8cee80e07', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('22dd87f6-353c-42a7-a253-3957b17c1bb4', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('531cefda-5723-4a81-9ec1-5eb0c797b9a0', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e3fdf46f-782c-439f-b09f-2a616d42697d', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('065d41f3-ff24-4e2e-989f-df71c45e3853', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d80b3e78-6dfc-429c-bcdd-50ec212cfc42', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('71427f80-643c-4e8e-9cb8-f07f544b2825', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b9f4c9f9-0fb6-47d6-af8a-57e9b8902245', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f2e017f1-70cb-4ad1-a187-8895c8040945', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c66cbddd-5846-4fb9-bedb-8a6812c51c5e', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('705c1cef-b47b-4e0b-ab42-d1b360e6502e', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8a2d0ca-4f5b-4809-8321-5ef485b2bf21', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('03979d46-6325-4b20-8d77-0e904a986064', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('67a2b3ad-6974-442c-9f03-32f0a8cd4f4d', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bd18fd9b-06af-4c98-8198-26b3aa3b78bc', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f79aada7-27fb-47c6-9827-07fa035198bc', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5578d38b-9966-498d-8c5d-f1615a817f87', @groupId5);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ef3af2e9-ebbe-42f2-b99c-20f53caa9b2f', @groupId5);

SELECT id INTO @groupId6 from rule_group where name = '6.0 AWS CIS Level 2';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a8bb65f2-8715-4f86-97ad-0655f7271d9a', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('edb2c0ee-f36a-4864-b66d-bee3cd9730b0', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c4605126-0a03-420e-a9af-a808c9031138', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2d040c4f-0486-413f-8738-5bc1a2a3e842', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('67a51dbc-16a2-4c35-a494-06f60cb7ca7e', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ef4311ae-7fd1-4866-b9f3-3e5b89975c44', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8bf919b-cf2c-4930-872b-3949143040d6', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('48e028e7-9eff-4c98-8a08-2c696e2842f7', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f2e017f1-70cb-4ad1-a187-8895c8040945', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8a2d0ca-4f5b-4809-8321-5ef485b2bf21', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('705c1cef-b47b-4e0b-ab42-d1b360e6502e', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('03979d46-6325-4b20-8d77-0e904a986064', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('67a2b3ad-6974-442c-9f03-32f0a8cd4f4d', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b9f4c9f9-0fb6-47d6-af8a-57e9b8902245', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bd18fd9b-06af-4c98-8198-26b3aa3b78bc', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b8844aed-9da9-469b-8ec8-a69fb1a2f7f2', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bb3eed24-02b1-43d1-9057-f4f800e4660e', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('3bd0f4f5-eb7f-48d5-b3db-1fd8cee80e07', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('22dd87f6-353c-42a7-a253-3957b17c1bb4', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('531cefda-5723-4a81-9ec1-5eb0c797b9a0', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e3fdf46f-782c-439f-b09f-2a616d42697d', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('065d41f3-ff24-4e2e-989f-df71c45e3853', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d80b3e78-6dfc-429c-bcdd-50ec212cfc42', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('71427f80-643c-4e8e-9cb8-f07f544b2825', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f8579451-2c24-4743-87e3-0fcf7c465d08', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d2628999-b60d-4e7f-812d-0cd5a94a7e38', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('eac1ba91-eb46-4c44-b1f4-e709ea9d0e1e', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c66cbddd-5846-4fb9-bedb-8a6812c51c5e', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('77047e0e-88da-422e-93db-2d3a0606e961', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e554b261-0e89-42eb-a44c-4a6d00a32f85', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1ca7f983-69ea-47ed-9f9a-5a6e7317d8bd', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e7b41c37-410e-4fa8-85bd-2452c1076957', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('79790c74-501c-4830-bba6-ca635c0885f5', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('79d8e033-ddca-49e6-a658-76b5c0e90e09', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8883c71f-7d42-4007-9152-1138c3fb7ab3', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('96b70632-7ccb-4fe9-af1f-58a8be26eeaf', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a2df85e7-0f0a-4283-84f2-81ee336736e2', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8fd6c3d-5faf-4394-897d-7fd84d33550d', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e82e163e-4428-49d8-8986-1d06906c165c', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('eb0f3859-8b0d-41e1-b85f-f829ceb99801', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1a201a3a-153d-4246-834f-c1b8f247443d', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f79aada7-27fb-47c6-9827-07fa035198bc', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5578d38b-9966-498d-8c5d-f1615a817f87', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('acba4a64-2bcc-4bbf-bb97-7983cc4c929b', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('42e93327-e951-4a6e-b04d-73504f66c380', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f7f19d65-3561-463b-b3ca-218239268d66', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('14983a14-0f6b-45d8-827b-105368869ae9', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ed2a0376-2f23-4ca6-9746-b909db67a2ab', @groupId6);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1d1974d5-3e7c-4891-ab99-e817292b73ed', @groupId6);

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('fe94237b-eaa1-4d2f-9d87-c354b787fdc8', '7.195 AWS CodeArtifact (公共源)检测', 1, 'CriticalRisk', 'AWS 确保 CodeArtifact 内部包不允许外部公共源发布，Ensure CodeArtifact internal packages do not allow external public source publishing。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.195 [check7195] Ensure CodeArtifact internal packages do not allow external public source publishing. - codeartifact [Critical]', '[{\"defaultValue\":\"extra7195\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('3c7209df-c617-46a5-8e73-4dc0ea100f9d', '7.193 AWS AppStream 队列流实例(Internet 访问)检测', 1, 'MediumRisk', 'AWS 确保您的 Amazon AppStream 队列流实例的默认 Internet 访问应保持未选中状态。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.193 [extra7193] Ensure default Internet Access from your Amazon AppStream fleet streaming instances should remain unchecked. - appstream [Medium]', '[{\"defaultValue\":\"extra7193\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('3fa369e5-ab65-40e8-b758-1b5be10ee275', '7.192 AWS 会话空闲断开超时检测', 1, 'MediumRisk', 'AWS 确保会话空闲断开超时设置为 10 分钟或更短，Ensure session idle disconnect timeout is set to 10 minutes or less。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.192 [extra7192] Ensure session idle disconnect timeout is set to 10 minutes or less. - appstream [Medium]', '[{\"defaultValue\":\"extra7192\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('87259583-f650-48ba-a1e5-3f0f35b3dc34', '7.191 AWS 会话断开超时检测', 1, 'MediumRisk', 'AWS 确保会话断开超时设置为 5 分钟或更短，Ensure session disconnect timeout is set to 5 minutes or less。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.191 [extra7191] Ensure session disconnect timeout is set to 5 minutes or less. - appstream [Medium]', '[{\"defaultValue\":\"extra7191\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('2885b53e-12f4-4864-9dc7-79327db63604', '7.190 AWS 用户最大会话持续时间检测', 1, 'MediumRisk', 'AWS 确保用户最大会话持续时间不超过 10 小时，Ensure user maximum session duration is no longer than 10 hours。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.190 [extra7190] Ensure user maximum session duration is no longer than 10 hours. - appstream [Medium]', '[{\"defaultValue\":\"extra7190\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('8f1cd40a-bcff-4089-97e8-45337c4e8cb1', '7.189 AWS Radius 服务器(MFA)检测', 1, 'MediumRisk', 'AWS 确保在 DS 中启用了使用 Radius 服务器的多重身份验证 (MFA)，Ensure Multi-Factor Authentication (MFA) using Radius Server is enabled in DS。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.189 [extra7189] Ensure Multi-Factor Authentication (MFA) using Radius Server is enabled in DS - ds [Medium]', '[{\"defaultValue\":\"extra7189\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('978d14c5-da23-4cf8-9cd9-c0e710977250', '7.188 AWS Radius 服务器(安全协议)检测', 1, 'MediumRisk', 'AWS 确保 DS 中的 Radius 服务器使用推荐的安全协议，Ensure Radius server in DS is using the recommended security protocol。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.188 [extra7188] Ensure Radius server in DS is using the recommended security protocol - ds [Medium]', '[{\"defaultValue\":\"extra7188\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('62d16913-61e4-46fc-abd7-4174c1ab89cc', '7.187 AWS WorkSpaces 存储卷(加密)检测', 1, 'HighRisk', 'AWS 确保您的 WorkSpaces 存储卷已加密以满足安全性和合规性要求，Ensure that your WorkSpaces storage volumes are encrypted。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.187 [extra7187] Ensure that your Amazon WorkSpaces storage volumes are encrypted in order to meet security and compliance requirements - workspaces [High]', '[{\"defaultValue\":\"extra7187\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('109b39e5-6878-43cb-93ab-f19a22f2ecdd', '7.186 AWS S3 帐户(公共访问)检测', 1, 'HighRisk', 'AWS 检查 S3 帐户级别公共访问块，Check S3 Account Level Public Access Block。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.186 [extra7186] Check S3 Account Level Public Access Block - s3 [High]', '[{\"defaultValue\":\"extra7186\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('90ec24a4-79de-4911-bea9-ca795a7e6eec', '7.185 AWS IAM 策略(权限提升)检测', 1, 'HighRisk', 'AWS 确保没有客户管理的 IAM 策略允许可能导致权限提升的操作，Ensure no Customer Managed IAM policies allow actions that may lead into Privilege Escalation。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.185 [extra7185] Ensure no Customer Managed IAM policies allow actions that may lead into Privilege Escalation - iam [High]', '[{\"defaultValue\":\"extra7185\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('14f34d63-8a93-4f70-ae25-69d29b71d573', '7.184 AWS 目录服务手册快照限制检测', 1, 'LowRisk', 'AWS 目录服务手册快照限制，Directory Service Manual Snapshot Limit。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.184 [extra7184] Directory Service Manual Snapshot Limit - ds [Low]', '[{\"defaultValue\":\"extra7184\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('4420e7c7-578d-44e2-8a8a-d0e5691d76c2', '7.183 AWS 目录服务 LDAP 证书到期检测', 1, 'MediumRisk', 'AWS 目录服务 LDAP 证书到期，Directory Service LDAP Certificates expiration。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.183 [extra7183] Directory Service LDAP Certificates expiration - ds [Medium]', '[{\"defaultValue\":\"extra7183\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('a14ec927-6f85-47fa-89dc-4d5fbb0534eb', '7.182 AWS 目录服务 SNS 通知检测', 1, 'MediumRisk', 'AWS 目录服务 SNS 通知，Directory Service SNS Notifications。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.182 [extra7182] Directory Service SNS Notifications - ds [Medium]', '[{\"defaultValue\":\"extra7182\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('2d4c6320-bcd3-4599-9564-87e4e6f82eeb', '7.181 AWS CloudWatch (日志监控目录服务)检测', 1, 'MediumRisk', 'AWS 使用 CloudWatch 日志监控目录服务，Directory Service monitoring with CloudWatch logs。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.181 [extra7181] Directory Service monitoring with CloudWatch logs - ds [Medium]', '[{\"defaultValue\":\"extra7181\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('a3c12f99-237c-4dfd-b4e5-8d6d30045812', '7.180 AWS Lambda 函数 (URL CORS 配置)检测', 1, 'MediumRisk', 'AWS 检查 Lambda 函数 URL CORS 配置，Check Lambda Function URL CORS configuration。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.180 [extra7180] Check Lambda Function URL CORS configuration - lambda [Medium]', '[{\"defaultValue\":\"extra7180\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('a0964936-4dde-4fdf-b512-bb6a0fae8146', '7.179 AWS Lambda 函数 (URL)检测', 1, 'HighRisk', 'AWS 检查公共 Lambda 函数 URL，Check Public Lambda Function URL。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.179 [extra7179] Check Public Lambda Function URL - lambda [High]', '[{\"defaultValue\":\"extra7179\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('7b3df26a-1839-430d-b8b1-bec22849a62b', '7.178 AWS EMR 集群 (公共访问块)检测', 1, 'HighRisk', 'AWS 启用 EMR 帐户公共访问块，EMR Account Public Access Block enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.178 [extra7178] EMR Account Public Access Block enabled - emr [High]', '[{\"defaultValue\":\"extra7178\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('2da5e51d-889a-4dbf-a2f8-11259b8eaa56', '7.177 AWS EMR 集群 (公开访问)检测', 1, 'HighRisk', 'AWS 可公开访问的 EMR 集群，Publicly accessible EMR Cluster。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.177 [extra7177] Publicly accessible EMR Cluster - emr [High]', '[{\"defaultValue\":\"extra7177\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('d1fbc80a-78c1-4214-bf99-13ec76e78b58', '7.176 AWS EMR 集群 (公共 IP)检测', 1, 'MediumRisk', 'AWS 没有公共 IP 的 EMR 集群，EMR Cluster without Public IP。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.176 [extra7176] EMR Cluster without Public IP - emr [Medium]', '[{\"defaultValue\":\"extra7176\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('095ac637-4e0d-46ef-b658-dc2abbe0ed7e', '7.175 AWS CodeBuild 项目 (构建规范)检测', 1, 'HighRisk', 'AWS 具有用户控制的构建规范的 CodeBuild 项目，CodeBuild Project with an user controlled buildspec。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.175 [extra7175] CodeBuild Project with an user controlled buildspec - codebuild [High]', '[{\"defaultValue\":\"extra7175\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('bebe49c3-8e87-4c21-a2b3-7f5f6cc90136', '7.174 AWS CodeBuild 项目 (调用时间)检测', 1, 'HighRisk', 'AWS CodeBuild 项目上次调用时间超过 90 天，CodeBuild Project last invoked greater than 90 days。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.174 [extra7174] CodeBuild Project last invoked greater than 90 days - codebuild [High]', '[{\"defaultValue\":\"extra7174\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('dc28da88-7551-453d-a891-5c9c23781bc4', '7.173 AWS EC2 启动向导 (创建的安全组)检测', 1, 'MediumRisk', 'AWS EC2 启动向导创建的安全组，Security Groups created by EC2 Launch Wizard。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.173 [extra7173] Security Groups created by EC2 Launch Wizard - ec2 [Medium]', '[{\"defaultValue\":\"extra7173\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('87786f5c-2f7d-4836-849d-900d587c38db', '7.172 AWS S3 存储桶 (ACL)检测', 1, 'MediumRisk', 'AWS 检查 S3 存储桶是否启用了 ACL，Check if S3 buckets have ACLs enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.172 [extra7172] Check if S3 buckets have ACLs enabled - s3 [Medium]', '[{\"defaultValue\":\"extra7172\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('3d27d878-d48e-4784-bff5-b230d2d28846', '7.171 AWS 经典负载均衡器 (Shield Advanced 保护)检测', 1, 'MediumRisk', 'AWS 检查经典负载均衡器是否受 AWS Shield Advanced 保护，Check if classic load balancers are protected by AWS Shield Advanced。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.171 [extra7171] Check if classic load balancers are protected by AWS Shield Advanced - shield [Medium]', '[{\"defaultValue\":\"extra7171\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('c8926d60-6726-407d-ba1d-839effafae3c', '7.170 AWS 负载均衡器 (Shield Advanced 保护)检测', 1, 'MediumRisk', 'AWS 检查面向 Internet 的应用程序负载均衡器是否受 AWS Shield Advanced 保护，Check if internet-facing application load balancers are protected by AWS Shield Advanced。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.170 [extra7170] Check if internet-facing application load balancers are protected by AWS Shield Advanced - shield [Medium]', '[{\"defaultValue\":\"extra7170\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('75d0d479-4c86-418a-b805-e4d32d7b9583', '7.169 AWS 全球加速器 (Shield Advanced 保护)检测', 1, 'MediumRisk', 'AWS 检查全球加速器是否受 AWS Shield Advanced 的保护，Check if global accelerators are protected by AWS Shield Advanced。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.169 [extra7169] Check if global accelerators are protected by AWS Shield Advanced - shield [Medium]', '[{\"defaultValue\":\"extra7169\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('f0cfc258-0001-4423-9485-b96521535c50', '7.168 AWS Route53 托管区域 (Shield Advanced 保护)检测', 1, 'MediumRisk', 'AWS 检查 Route53 托管区域是否受 AWS Shield Advanced 保护，Check if Route53 hosted zones are protected by AWS Shield Advanced。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.168 [extra7168] Check if Route53 hosted zones are protected by AWS Shield Advanced - shield [Medium]', '[{\"defaultValue\":\"extra7168\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('8a3cd7a7-3aea-4e57-b646-f97d9952614a', '7.167 AWS Cloudfront 发行版 (Shield Advanced 保护)检测', 1, 'MediumRisk', 'AWS 检查 Cloudfront 发行版是否受 AWS Shield Advanced 的保护，Check if Cloudfront distributions are protected by AWS Shield Advanced。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.167 [extra7167] Check if Cloudfront distributions are protected by AWS Shield Advanced - shield [Medium]', '[{\"defaultValue\":\"extra7167\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('520959f9-f224-41fa-9a52-0df38e55eb8f', '7.166 AWS 弹性 IP (Shield Advanced 保护)检测', 1, 'MediumRisk', 'AWS 检查具有关联的弹性 IP 地址是否受 AWS Shield Advanced 保护，Check if Elastic IP addresses with associations are protected by AWS Shield Advanced。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.166 [extra7166] Check if Elastic IP addresses with associations are protected by AWS Shield Advanced - shield [Medium]', '[{\"defaultValue\":\"extra7166\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('8bd18f86-7b0d-4927-860d-83e295cc6b05', '7.165 AWS DynamoDB：DAX 静态加密检测', 1, 'MediumRisk', 'AWS 检查 DynamoDB：DAX 集群是否在静态加密，Check if DynamoDB: DAX Clusters are encrypted at rest。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.165 [extra7165] Check if DynamoDB: DAX Clusters are encrypted at rest - dynamodb [Medium]', '[{\"defaultValue\":\"extra7165\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('abe0b522-dabf-456f-bac6-d0d48cae4eed', '7.164 AWS CloudWatch (AWS KMS 保护)检测', 1, 'MediumRisk', 'AWS 检查 CloudWatch 日志组是否受 AWS KMS 保护，Check if CloudWatch log groups are protected by AWS KMS。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.164 [extra7164] Check if CloudWatch log groups are protected by AWS KMS  - logs [Medium]', '[{\"defaultValue\":\"extra7164\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('9ddfd1d2-810e-417a-ba5f-925365b2f9f8', '7.163 AWS Secrets Manager 密钥轮换检测', 1, 'MediumRisk', 'AWS 检查是否启用了 Secrets Manager 密钥轮换，Check if Secrets Manager key rotation is enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.163 [extra7163] Check if Secrets Manager key rotation is enabled - secretsmanager [Medium]', '[{\"defaultValue\":\"extra7163\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('41e7a337-339b-4609-8904-e362196aa9c0', '7.162 AWS CloudWatch (保留策略)检测', 1, 'MediumRisk', 'AWS 检查 CloudWatch 日志组是否具有至少 365 天的保留策略，Check if CloudWatch Log Groups have a retention policy of at least 365 days。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.162 [extra7162] Check if CloudWatch Log Groups have a retention policy of at least 365 days - cloudwatch [Medium]', '[{\"defaultValue\":\"extra7162\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('359a94bf-6423-4db2-96b0-86fd6de9bb25', '7.161 AWS EFS (敏感数据)检测', 1, 'MediumRisk', 'AWS 检查 EFS 是否通过静态加密保护敏感数据，Check if EFS protects sensitive data with encryption at rest。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.161 [extra7161] Check if EFS protects sensitive data with encryption at rest - efs [Medium]', '[{\"defaultValue\":\"extra7161\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('53569e08-86c6-43ba-99f5-64d529477bc0', '7.160 AWS Redshift (自动升级)检测', 1, 'MediumRisk', 'AWS 检查 Redshift 是否启用了自动升级，Check if Redshift has automatic upgrades enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.160 [extra7160] Check if Redshift has automatic upgrades enabled - redshift [Medium]', '[{\"defaultValue\":\"extra7160\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('f4190017-0459-4f7e-943d-5f299771da5b', '7.159 AWS ELB (监听器)检测', 1, 'MediumRisk', 'AWS 检查 ELB 下是否有侦听器，Check if ELB has listeners underneath。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.159 [extra7159] Check if ELB has listeners underneath - elb [Medium]', '[{\"defaultValue\":\"extra7159\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('9d0ca4db-2cf0-45d7-a14d-8dcd18791191', '7.158 AWS ELB V2(监听器)检测', 1, 'MediumRisk', 'AWS 检查 ELB V2 下面是否有监听器，Check if ELBV2 has listeners underneath。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.158 [extra7158] Check if ELBV2 has listeners underneath - elb [Medium]', '[{\"defaultValue\":\"extra7158\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('6ca702b2-13b2-4dfb-9a8e-2aedbfbd5252', '7.157 AWS API Gateway V2(配置授权方)检测', 1, 'MediumRisk', 'AWS 检查 API Gateway V2 是否已配置授权方，Check if API Gateway V2 has configured authorizers。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.157 [extra7157] Check if API Gateway V2 has configured authorizers - apigateway [Medium]', '[{\"defaultValue\":\"extra7157\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('e3d8f69b-a3ef-47d7-b715-235fb803e9e6', '7.156 AWS API Gateway V2(访问日志记录)检测', 1, 'MediumRisk', 'AWS 检查 API Gateway V2 是否启用了访问日志记录，Checks if API Gateway V2 has Access Logging enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.156 [extra7156] Checks if API Gateway V2 has Access Logging enabled - apigateway [Medium]', '[{\"defaultValue\":\"extra7156\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('9b174f28-9ed1-4e93-9be1-adde6619d8c3', '7.155 AWS 负载均衡器(异步缓解模式)检测', 1, 'MediumRisk', 'AWS 检查应用负载均衡器是否配置了防御性或最严格的异步缓解模式，Check whether the Application Load Balancer is configured with defensive or strictest desync mitigation mode。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.155 [extra7155] Check whether the Application Load Balancer is configured with defensive or strictest desync mitigation mode - elb [Medium]', '[{\"defaultValue\":\"extra7155\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('65dfbac5-4ee3-4405-a5d0-98e3da849831', '7.154 AWS Cloudformation(终止保护)检测', 1, 'MediumRisk', 'AWS 为 Cloudformation 堆栈启用终止保护，Enable termination protection for Cloudformation Stacks。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.154 [extra7154] Enable termination protection for Cloudformation Stacks - cloudformation [Medium]', '[{\"defaultValue\":\"extra7154\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('10751001-d9aa-4582-a96a-471813e50512', '7.153 AWS Route53 域(传输锁定)检测', 1, 'MediumRisk', 'AWS 为 Route53 域启用传输锁定（仅限 us-east-1），Enable Transfer Lock for a Route53 Domain (us-east-1 only)。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.153 [extra7153] Enable Transfer Lock for a Route53 Domain (us-east-1 only) - route53 [Medium]', '[{\"defaultValue\":\"extra7153\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('9a9eeba2-8a1c-4e3f-b486-5357a7546e08', '7.152 AWS Route53 域(隐私保护)检测', 1, 'MediumRisk', 'AWS 为 Route53 域启用隐私保护（仅限 us-east-1），Enable Privacy Protection for for a Route53 Domain (us-east-1 only) 。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.152 [extra7152] Enable Privacy Protection for for a Route53 Domain (us-east-1 only) - route53 [Medium]', '[{\"defaultValue\":\"extra7152\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('d8b035cd-6d0c-43b6-9277-e6be6fd8f8da', '7.151 AWS DynamoDB 表(PITR)检测', 1, 'MediumRisk', 'AWS 检查是否启用了 DynamoDB 表时间点恢复 (PITR)，Check if DynamoDB tables point-in-time recovery (PITR) is enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.151 [extra7151] Check if DynamoDB tables point-in-time recovery (PITR) is enabled - dynamodb [Medium]', '[{\"defaultValue\":\"extra7151\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('bf3990ee-324f-4618-8f2f-190df72aaf11', '7.150 AWS 弹性负载均衡器(删除保护)检测', 1, 'MediumRisk', 'AWS 检查弹性负载均衡器是否启用了删除保护，Check if Elastic Load Balancers have deletion protection enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.150 [extra7150] Check if Elastic Load Balancers have deletion protection enabled - elb [Medium]', '[{\"defaultValue\":\"extra7150\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('e5b42411-47db-4358-b156-1d7a56b08b10', '7.149 AWS Redshift 集群(快照)检测', 1, 'MediumRisk', 'AWS 检查 Redshift 集群是否启用了自动快照，Check if Redshift Clusters have automated snapshots enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.149 [extra7149] Check if Redshift Clusters have automated snapshots enabled - redshift [Medium]', '[{\"defaultValue\":\"extra7149\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('111fabaa-249b-4a73-b9fd-19166c9073b7', '7.148 AWS EFS 文件系统(备份)检测', 1, 'MediumRisk', 'AWS 检查 EFS 文件系统是否启用了备份，Check if EFS File systems have backup enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.148 [extra7148] Check if EFS File systems have backup enabled - efs [Medium]', '[{\"defaultValue\":\"extra7148\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('0eda2992-8173-4c3a-bd1c-e2e48e0aad1e', '7.147 AWS S3 Glacier 保险库(策略)检测', 1, 'CriticalRisk', 'AWS 检查 S3 Glacier 保险库是否具有允许所有人访问的策略，Check if S3 Glacier vaults have policies which allow access to everyone。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.147 [extra7147] Check if S3 Glacier vaults have policies which allow access to everyone - glacier [Critical]', '[{\"defaultValue\":\"extra7147\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('2198c834-ac57-4bd4-be8a-b1fa8834fc76', '7.146 AWS 弹性 IP(未分配)检测', 1, 'LowRisk', 'AWS 检查是否有任何未分配的弹性 IP，Check if there is any unassigned Elastic IP。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.146 [extra7146] Check if there is any unassigned Elastic IP - ec2 [Low]', '[{\"defaultValue\":\"extra7146\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('23aaf8a8-820b-4b2d-a2a9-e27151fdcc12', '7.145 AWS Lambda 函数(策略)检测', 1, 'CriticalRisk', 'AWS 检查 Lambda 函数是否具有允许访问任何 AWS 账户的策略，Check if Lambda functions have policies which allow access to any AWS account。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.145 [extra7145] Check if Lambda functions have policies which allow access to any AWS account - lambda [Critical]', '[{\"defaultValue\":\"extra7145\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('55e5207c-9d0e-48a5-ba68-c01ead665443', '7.144 AWS CloudWatch(共享)检测', 1, 'MediumRisk', 'AWS 检查 CloudWatch 是否允许跨账户共享，Check if CloudWatch has allowed cross-account sharing。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.144 [extra7144] Check if CloudWatch has allowed cross-account sharing - cloudwatch [Medium]', '[{\"defaultValue\":\"extra7144\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('2803b3c5-2a11-4f3d-8bd7-acaa18bd35cf', '7.143 AWS EFS(访问策略)检测', 1, 'CriticalRisk', 'AWS 检查 EFS 是否具有允许所有人访问的策略，Check if EFS have policies which allow access to everyone。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.143 [extra7143] Check if EFS have policies which allow access to everyone - efs [Critical]', '[{\"defaultValue\":\"extra7143\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('88c3427c-c668-49ee-987f-7b7da5b4de11', '7.142 AWS Application Load Balancer (HTTP)检测', 1, 'MediumRisk', 'AWS 检查 Application Load Balancer 是否正在丢弃无效数据包以防止基于标头的 HTTP 请求走私。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.142 [extra7142] Check if Application Load Balancer is dropping invalid packets to prevent header based HTTP request smuggling - elb [Medium]', '[{\"defaultValue\":\"extra7142\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('40d375c3-a1b6-4785-9325-c5c4c59bcacf', '7.141 AWS SSM 文档 (密钥)检测', 1, 'CriticalRisk', 'AWS 在 SSM 文档中查找密钥，Find secrets in SSM Documents - ssm。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.141 [extra7141] Find secrets in SSM Documents - ssm [Critical]', '[{\"defaultValue\":\"extra7141\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('8afa254e-0446-4e55-9a32-eb83bf3049ab', '7.140 AWS SSM 文档 (公共)检测', 1, 'HighRisk', 'AWS 检查是否有设置为公共的 SSM 文档，Check if there are SSM Documents set as public。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.140 [extra7140] Check if there are SSM Documents set as public - ssm [High]', '[{\"defaultValue\":\"extra7140\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('98e815f5-8b07-4717-a15c-389b34e4ebfc', '7.139 AWS GuardDuty (调查结果)检测', 1, 'HighRisk', 'AWS 有高严重性 GuardDuty 调查结果，There are High severity GuardDuty findings。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.139 [extra7139] There are High severity GuardDuty findings  - guardduty [High]', '[{\"defaultValue\":\"extra7139\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('bd5f84b8-9439-40f4-8ccd-2b544f656a91', '7.138 AWS 网络 ACL (任何端口)检测', 1, 'HighRisk', 'AWS 确保没有网络 ACL 允许从 0.0.0.0/0 进入任何端口，Ensure no Network ACLs allow ingress from 0.0.0.0/0 to any port。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.138 [extra7138] Ensure no Network ACLs allow ingress from 0.0.0.0/0 to any port - ec2 [High]', '[{\"defaultValue\":\"extra7138\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('5e5cc249-2b20-4c80-a278-a913e18a2b21', '7.137 AWS 安全组(SQL Server 端口 1433 或 1434)检测', 1, 'HighRisk', 'AWS 确保没有安全组允许从 0.0.0.0/0 或 ::/0 进入 SQL Server 端口 1433/1434，Ensure no security groups allow ingress to SQL Server ports 1433/1434。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.137 [extra7137] Ensure no security groups allow ingress from 0.0.0.0/0 or ::/0 to Windows SQL Server ports 1433 or 1434 - ec2 [High]', '[{\"defaultValue\":\"extra7137\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('a89f5a2e-eafb-4511-a71e-599a72143408', '7.136 AWS 安全组(Telnet 端口 23)检测', 1, 'HighRisk', 'AWS 确保没有安全组允许从 0.0.0.0/0 或 ::/0 进入 Telnet 端口 23，Ensure no security groups allow ingress from 0.0.0.0/0 or ::/0 to Telnet port 23。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.136 [extra7136] Ensure no security groups allow ingress from 0.0.0.0/0 or ::/0 to Telnet port 23 - ec2 [High]', '[{\"defaultValue\":\"extra7136\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('39d29fe9-74ab-450a-af0f-ae7c12896673', '7.135 AWS 安全组(Kafka 端口 9092)检测', 1, 'HighRisk', 'AWS 确保没有安全组允许从 0.0.0.0/0 或 ::/0 进入 Kafka 端口 9092，Ensure no security groups allow ingress from 0.0.0.0/0 or ::/0 to Kafka port 9092。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.135 [extra7135] Ensure no security groups allow ingress from 0.0.0.0/0 or ::/0 to Kafka port 9092  - ec2 [High]', '[{\"defaultValue\":\"extra7135\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('5f8adceb-99aa-49ac-9ed4-d86d8bd67a0d', '7.134 AWS 安全组(FTP 端口 20 或 21)检测', 1, 'HighRisk', 'AWS 确保没有安全组允许从 0.0.0.0/0 或 ::/0 进入 FTP 端口 20 或 21，Ensure no security groups allow ingress from 0.0.0.0/0 or ::/0 to FTP ports 20 or 21。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.134 [extra7134] Ensure no security groups allow ingress from 0.0.0.0/0 or ::/0 to FTP ports 20 or 21  - ec2 [High]', '[{\"defaultValue\":\"extra7134\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('7c0e6e4a-f6c4-4d11-bfdd-c7231f13a322', '7.133 AWS RDS 实例(多可用区)检测', 1, 'MediumRisk', 'AWS 检查 RDS 实例是否启用了多可用区，Check if RDS instances have multi-AZ enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.133 [extra7133] Check if RDS instances have multi-AZ enabled - rds [Medium]', '[{\"defaultValue\":\"extra7133\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('50e66a3d-13c1-403c-a7e1-3b157cda6298', '7.132 AWS RDS 实例(增强监控)检测', 1, 'LowRisk', 'AWS 检查 RDS 实例是否启用了增强监控，Check if RDS instances has enhanced monitoring enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.132 [extra7132] Check if RDS instances has enhanced monitoring enabled - rds [Low]', '[{\"defaultValue\":\"extra7132\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('aa79b399-bfea-4607-ba4b-5a3f438d3c6e', '7.131 AWS RDS 实例(版本升级)检测', 1, 'LowRisk', 'AWS 确保 RDS 实例启用了次要版本升级，Ensure RDS instances have minor version upgrade enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.131 [extra7131] Ensure RDS instances have minor version upgrade enabled - rds [Low]', '[{\"defaultValue\":\"extra7131\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('822fee45-32ac-46bb-81be-c06243e76a30', '7.130 AWS SNS 主题(加密)检测', 1, 'MediumRisk', 'AWS 确保没有未加密的 SNS 主题，Ensure there are no SNS Topics unencrypted。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.130 [extra7130] Ensure there are no SNS Topics unencrypted - sns [Medium]', '[{\"defaultValue\":\"extra7130\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('6d47ed7f-6c69-4f21-99ec-d046a0c3037c', '7.129 AWS 负载均衡器(WAF ACL)检测', 1, 'MediumRisk', 'AWS 检查应用程序负载均衡器是否附加了 WAF ACL，Check if Application Load Balancer has a WAF ACL attached。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.129 [extra7129] Check if Application Load Balancer has a WAF ACL attached - elb [Medium]', '[{\"defaultValue\":\"extra7129\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('9845da30-1314-4e12-b287-497d24c61579', '7.128 AWS DynamoDB 表(静态加密)检测', 1, 'MediumRisk', 'AWS 检查 DynamoDB 表是否使用 CMK KMS 启用了静态加密，Check if DynamoDB table has encryption at rest enabled using CMK KMS。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.128 [extra7128] Check if DynamoDB table has encryption at rest enabled using CMK KMS - dynamodb [Medium]', '[{\"defaultValue\":\"extra7128\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('616b9333-e7b5-46e5-8121-eb43314e8c71', '7.127 AWS EC2 实例修补要求检测', 1, 'HighRisk', 'AWS 检查由 Systems Manager 管理的 EC2 实例是否符合修补要求，Check if there are CMK KMS keys not used。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.127 [extra7127] Check if EC2 instances managed by Systems Manager are compliant with patching requirements - ssm [High]', '[{\"defaultValue\":\"extra7127\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('a95f4318-d629-4a04-8f42-2056d63f09cb', '7.126 AWS CMK KMS 密钥检测', 1, 'MediumRisk', 'AWS 检查是否有未使用的 CMK KMS 密钥，Check if there are CMK KMS keys not used。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.126 [extra7126] Check if there are CMK KMS keys not used - kms [Medium]', '[{\"defaultValue\":\"extra7126\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('90071ae9-ce0a-41a5-b743-9aafc6e65c08', '7.125 AWS EC2 实例(硬件 MFA)检测', 1, 'MediumRisk', 'AWS 检查 IAM 用户是否启用了硬件 MFA，Check if IAM users have Hardware MFA enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.125 [extra7125] Check if IAM users have Hardware MFA enabled. - iam [Medium]', '[{\"defaultValue\":\"extra7125\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('cf92e9e1-5d68-4bb2-85b6-ed807fdfd50f', '7.124 AWS EC2 实例(Systems Manager)检测', 1, 'MediumRisk', 'AWS 检查 EC2 实例是否由 Systems Manager 管理，Check if EC2 instances are managed by Systems Manager。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.124 [extra7124] Check if EC2 instances are managed by Systems Manager. - ssm [Medium]', '[{\"defaultValue\":\"extra7124\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('e389ad5a-660d-4f69-95cd-98dc1423be48', '7.123 AWS IAM 用户(访问密钥)检测', 1, 'MediumRisk', 'AWS 检查 IAM 用户是否有两个活动访问密钥，Check if IAM users have two active access keys。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.123 [extra7123] Check if IAM users have two active access keys - iam [Medium]', '[{\"defaultValue\":\"extra7123\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('2ad08cd2-3e23-4fde-8ffd-27174e56e579', '7.122 AWS Glue ETL 作业(书签加密)检测', 1, 'MediumRisk', 'AWS 检查 Glue ETL 作业是否启用了作业书签加密，Check if Glue ETL Jobs have Job bookmark encryption enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.122 [extra7122] Check if Glue ETL Jobs have Job bookmark encryption enabled. - glue [Medium]', '[{\"defaultValue\":\"extra7122\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('2d70fd48-f88f-45ab-878e-f9e06499b741', '7.121 AWS Glue 开发端点(书签加密)检测', 1, 'MediumRisk', 'AWS 检查 Glue 开发端点是否启用了作业书签加密，Check if Glue development endpoints have Job bookmark encryption enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.121 [extra7121] Check if Glue development endpoints have Job bookmark encryption enabled. - glue [Medium]', '[{\"defaultValue\":\"extra7121\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('4f622382-8519-4990-af03-ffa0ed9cc446', '7.120 AWS Glue ETL 作业(CloudWatch Logs 加密)检测', 1, 'MediumRisk', 'AWS 检查 Glue ETL 作业是否启用了 CloudWatch Logs 加密，Check if Glue ETL Jobs have CloudWatch Logs encryption enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.120 [extra7120] Check if Glue ETL Jobs have CloudWatch Logs encryption enabled. - glue [Medium]', '[{\"defaultValue\":\"extra7120\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('e71b1798-e390-4e05-bd39-47ccde2244c5', '7.119 AWS Glue 开发终端节点(CloudWatch)检测', 1, 'MediumRisk', 'AWS 检查 Glue 开发终端节点是否启用了 CloudWatch 日志加密，Check if Glue development endpoints have CloudWatch logs encryption enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.119 [extra7119] Check if Glue development endpoints have CloudWatch logs encryption enabled. - glue [Medium]', '[{\"defaultValue\":\"extra7119\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('e14667da-cdef-4afe-9113-4b3d21a61f11', '7.118 AWS Glue ETL 作业(S3 加密)检测', 1, 'MediumRisk', 'AWS 检查 Glue ETL 作业是否启用了 S3 加密，Check if Glue ETL Jobs have S3 encryption enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.118 [extra7118] Check if Glue ETL Jobs have S3 encryption enabled. - glue [Medium]', '[{\"defaultValue\":\"extra7118\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('c16fe4e1-3589-4f74-b3e5-64504e481d3e', '7.117 AWS Glue 数据目录(加密连接密码)检测', 1, 'MediumRisk', 'AWS 检查 Glue 数据目录设置是否启用了加密连接密码，Check if Glue data catalog settings have encrypt connection password enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.117 [extra7117] Check if Glue data catalog settings have encrypt connection password enabled. - glue [Medium]', '[{\"defaultValue\":\"extra7117\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('2bbd04a6-40af-4d74-a32b-4c31f52c7504', '7.116 AWS Glue 数据目录(元数据加密)检测', 1, 'MediumRisk', 'AWS 检查 Glue 数据目录设置是否启用了元数据加密，Check if Glue data catalog settings have metadata encryption enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.116 [extra7116] Check if Glue data catalog settings have metadata encryption enabled. - glue [Medium]', '[{\"defaultValue\":\"extra7116\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('3f8e0d58-f003-4b4a-aea4-05f40a796c6f', '7.115 AWS Glue 开发端点(SSL 连接)检测', 1, 'MediumRisk', 'AWS 检查 Glue 数据库连接是否启用了 SSL 连接，Check if Glue database connection has SSL connection enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.115 [extra7115] Check if Glue database connection has SSL connection enabled. - glue [Medium]', '[{\"defaultValue\":\"extra7115\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('d18ae6a7-a060-4ee7-a59d-3575447a1ea2', '7.114 AWS Glue 开发端点(S3 加密)检测', 1, 'MediumRisk', 'AWS 检查 Glue 开发端点是否启用了 S3 加密，Check if Glue development endpoints have S3 encryption enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.114 [extra7114] Check if Glue development endpoints have S3 encryption enabled. - glue [Medium]', '[{\"defaultValue\":\"extra7114\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('2e2816e7-85db-4903-8aaf-7708f6de4b15', '7.113 AWS RDS 实例(删除保护)检测', 1, 'MediumRisk', 'AWS 检查 RDS 实例是否启用了删除保护，Check if RDS instances have deletion protection enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.113 [extra7113] Check if RDS instances have deletion protection enabled  - rds [Medium]', '[{\"defaultValue\":\"extra7113\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('5e4678f5-378a-44b1-849e-319f3bf84faf', '7.112 AWS SageMaker 笔记本实例(数据加密)检测', 1, 'MediumRisk', 'AWS 检查 Amazon SageMaker 笔记本实例是否启用了数据加密，Check if Amazon SageMaker Notebook instances have data encryption enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.112 [extra7112] Check if Amazon SageMaker Notebook instances have data encryption enabled - sagemaker [Medium]', '[{\"defaultValue\":\"extra7112\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('05130e8d-e8f6-4446-b8d1-4e1e0a8fe813', '7.111 AWS SageMaker 笔记本实例(访问 Internet)检测', 1, 'MediumRisk', 'AWS 检查 Amazon SageMaker 笔记本实例是否可以直接访问 Internet，Check if Amazon SageMaker Notebook instances have direct internet access。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.111 [extra7111] Check if Amazon SageMaker Notebook instances have direct internet access - sagemaker [Medium]', '[{\"defaultValue\":\"extra7111\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('305e28d3-f68e-44b7-925b-6a31b92abf8a', '7.110 AWS SageMaker 训练作业(VPC 设置)检测', 1, 'MediumRisk', 'AWS 检查 Amazon SageMaker 训练作业是否配置了 VPC 设置，Check if Amazon SageMaker Training job have VPC settings configured。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.110 [extra7110] Check if Amazon SageMaker Training job have VPC settings configured. - sagemaker [Medium]', '[{\"defaultValue\":\"extra7110\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('ac04175b-feaf-4128-8cf4-b764d96c5b7b', '7.109 AWS SageMaker 训练作业(网络隔离)检测', 1, 'MediumRisk', 'AWS 检查 Amazon SageMaker 训练作业是否启用了网络隔离，Check if Amazon SageMaker Training jobs have network isolation enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.109 [extra7109] Check if Amazon SageMaker Training jobs have network isolation enabled - sagemaker [Medium]', '[{\"defaultValue\":\"extra7109\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('000348ec-9f32-4894-bff8-9e39abda83be', '7.108 AWS SageMaker 训练作业(KMS 加密)检测', 1, 'MediumRisk', 'AWS 检查 Amazon SageMaker 训练作业是否具有启用 KMS 加密的容量和输出，Check if Amazon SageMaker Training jobs have volume and output with KMS encryption enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.108 [extra7108] Check if Amazon SageMaker Training jobs have volume and output with KMS encryption enabled - sagemaker [Medium]', '[{\"defaultValue\":\"extra7108\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('8ead7b4a-5198-4feb-966b-232c0197d7bf', '7.107 AWS SageMaker 训练作业(容器间加密)检测', 1, 'MediumRisk', 'AWS 检查 Amazon SageMaker 训练作业是否启用了容器间加密，Check if Amazon SageMaker Training jobs have intercontainer encryption enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.107 [extra7107] Check if Amazon SageMaker Training jobs have intercontainer encryption enabled - sagemaker [Medium]', '[{\"defaultValue\":\"extra7107\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('ac632efc-bacc-4229-acd2-216684e36e41', '7.106 AWS SageMaker 模型(VPC 设置)检测', 1, 'MediumRisk', 'AWS 检查 Amazon SageMaker 模型是否配置了 VPC 设置，Check if Amazon SageMaker Models have VPC settings configured。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.106 [extra7106] Check if Amazon SageMaker Models have VPC settings configured - sagemaker [Medium]', '[{\"defaultValue\":\"extra7106\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('bd5566ee-6e33-4522-9ac9-18a6ba2a8a55', '7.105 AWS SageMaker 模型(网络隔离)检测', 1, 'MediumRisk', 'AWS 检查 Amazon SageMaker 模型是否启用了网络隔离，Check if Amazon SageMaker Models have network isolation enabled 。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.105 [extra7105] Check if Amazon SageMaker Models have network isolation enabled - sagemaker [Medium]', '[{\"defaultValue\":\"extra7105\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('8928f014-1d7c-4793-8c28-bd34b780b3c5', '7.104 AWS SageMaker 笔记本实例(VPC 设置)检测', 1, 'MediumRisk', 'AWS 检查 Amazon SageMaker 笔记本实例是否配置了 VPC 设置，Check if Amazon SageMaker Notebook instances have VPC settings configured。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.104 [extra7104] Check if Amazon SageMaker Notebook instances have VPC settings configured - sagemaker [Medium]', '[{\"defaultValue\":\"extra7104\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('b632c31c-91b5-42a5-bc4b-bf0a2acfbf02', '7.103 AWS SageMaker (根访问)检测', 1, 'MediumRisk', 'AWS 检查 Amazon SageMaker 笔记本实例是否禁用了根访问，Check if Amazon SageMaker Notebook instances have root access disabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.103 [extra7103] Check if Amazon SageMaker Notebook instances have root access disabled - sagemaker [Medium]', '[{\"defaultValue\":\"extra7103\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('b81b5de3-d794-4b74-bf20-36d0f9c0d74d', '7.102 AWS 弹性或公共 IP (Shodan API KEY)检测', 1, 'HighRisk', 'AWS 检查是否有任何弹性或公共 IP 在 Shodan（需要 Shodan API KEY），Check if any of the Elastic or Public IP are in Shodan (requires Shodan API KEY)。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.102 [extra7102] Check if any of the Elastic or Public IP are in Shodan (requires Shodan API KEY) - ec2 [High]', '[{\"defaultValue\":\"extra7102\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('bbbc54c2-a917-455b-9782-de74186160cb', '7.101 AWS Elasticsearch Service (审计日志)检测', 1, 'LowRisk', 'AWS 检查 Amazon Elasticsearch Service (ES) 域是否启用了审计日志记录，Check if Amazon Elasticsearch Service (ES) domains have audit logging enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.101 [extra7101] Check if Amazon Elasticsearch Service (ES) domains have audit logging enabled - es [Low]', '[{\"defaultValue\":\"extra7101\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('b1a92471-5517-48cc-8c9d-0edcb55de693', '7.100 AWS 自定义 IAM 策略检测', 1, 'CriticalRisk', 'AWS 确保不存在允许角色假设的自定义 IAM 策略（例如 sts:AssumeRole on *），Ensure that no custom IAM policies exist which allow permissive role assumption。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.100 [extra7100] Ensure that no custom IAM policies exist which allow permissive role assumption (e.g. sts:AssumeRole on *) - iam [Critical]', '[{\"defaultValue\":\"extra7100\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('05a359f2-af4e-49ca-9e48-09510dc2252a', '7.99 AWS Security Hub 检测', 1, 'HighRisk', 'AWS 检查是否启用了 Security Hub 及其标准订阅，Check if Security Hub is enabled and its standard subscriptions。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.99 [extra799] Check if Security Hub is enabled and its standard subscriptions - securityhub [High]', '[{\"defaultValue\":\"extra799\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('7fba3abd-2564-46b9-8cad-935f9f0a8683', '7.98 AWS Lambda 函数 (策略 Public) 加密检测', 1, 'CriticalRisk', 'AWS 检查 Lambda 函数是否将基于资源的策略设置为 Public，Check if Lambda functions have resource-based policy set as Public。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.98 [extra798] Check if Lambda functions have resource-based policy set as Public - lambda [Critical]', '[{\"defaultValue\":\"extra798\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('52e44c5e-2702-48b8-a3a6-e221048de715', '7.97 AWS Kubernetes (CMK) 加密检测', 1, 'MediumRisk', 'AWS 确保使用客户主密钥 (CMK) 加密 Kubernetes 机密，Ensure Kubernetes Secrets are encrypted using Customer Master Keys (CMKs)。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.97 [extra797] Ensure Kubernetes Secrets are encrypted using Customer Master Keys (CMKs) - eks [Medium]', '[{\"defaultValue\":\"extra797\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('8db50d8c-64e4-4506-a143-3a0a329492c9', '7.96 AWS EKS 控制平面端点的访问检测', 1, 'HighRisk', 'AWS 限制对 EKS 控制平面端点的访问，Restrict Access to the EKS Control Plane Endpoint。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.96 [extra796] Restrict Access to the EKS Control Plane Endpoint - eks [High]', '[{\"defaultValue\":\"extra796\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('120344f8-4544-46c5-8de9-2018416a1757', '7.95 AWS EKS 公共访问检测', 1, 'HighRisk', 'AWS 确保创建 EKS 集群时启用私有端点并禁用公共访问，Ensure EKS Clusters are created with Private Endpoint Enabled and Public Access Disabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.95 [extra795] Ensure EKS Clusters are created with Private Endpoint Enabled and Public Access Disabled - eks [High]', '[{\"defaultValue\":\"extra795\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('074a839d-a6df-4a96-adab-94c4e2869201', '7.94 AWS EKS 控制检测', 1, 'MediumRisk', 'AWS 确保为所有日志类型启用 EKS 控制平面审核日志记录，Ensure EKS Control Plane Audit Logging is enabled for all log types。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.94 [extra794] Ensure EKS Control Plane Audit Logging is enabled for all log types - eks [Medium]', '[{\"defaultValue\":\"extra794\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('e51aa0cd-7617-436f-b429-7141e123e148', '7.93 AWS 负载均衡器 (SSL 侦听器)检测', 1, 'MediumRisk', 'AWS 检查 Elastic Load Balancer 是否具有 SSL 侦听器，Check if Elastic Load Balancers have SSL listeners。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.93 [extra793] Check if Elastic Load Balancers have SSL listeners  - elb [Medium]', '[{\"defaultValue\":\"extra793\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('2bb33de6-89e2-4ab3-805a-3cc2e7cb42ed', '7.92 AWS 负载均衡器 (SSL 密码)检测', 1, 'MediumRisk', 'AWS 检查弹性负载均衡器是否具有不安全的 SSL 密码，Check if Elastic Load Balancers have insecure SSL ciphers。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.92 [extra792] Check if Elastic Load Balancers have insecure SSL ciphers  - elb [Medium]', '[{\"defaultValue\":\"extra792\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('d77e0bc8-6161-4a7a-a6f4-d4ce788e34df', '7.91 AWS CloudFront (SSL 协议)检测', 1, 'MediumRisk', 'AWS 检查 CloudFront 分配是否使用已弃用的 SSL 协议，Check if CloudFront distributions are using deprecated SSL protocols。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.91 [extra791] Check if CloudFront distributions are using deprecated SSL protocols - cloudfront [Medium]', '[{\"defaultValue\":\"extra791\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('97a3019d-5ab5-4339-9074-8a4e5c6f70f6', '7.90 AWS VPC 端点(服务白名单)检测', 1, 'MediumRisk', 'AWS 在 在 VPC 端点服务白名单原则中查找信任边界，Find trust boundaries in VPC endpoint services allowlisted principles。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.90 [extra790] Find trust boundaries in VPC endpoint services allowlisted principles - vpc [Medium]', '[{\"defaultValue\":\"extra790\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('5535a3a2-7910-4175-95b3-5fc3e2080df7', '7.89 AWS VPC 端点(信任边界)检测', 1, 'MediumRisk', 'AWS 在 VPC 端点服务连接中查找信任边界，Find trust boundaries in VPC endpoint services connections。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.89 [extra789] Find trust boundaries in VPC endpoint services connections - vpc [Medium]', '[{\"defaultValue\":\"extra789\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('0e1cd76e-2d63-499b-9e6d-a85eb00ed01a', '7.88 AWS Elasticsearch Service (ES公开)检测', 1, 'CriticalRisk', 'AWS 检查 Internet 公开的 Elasticsearch Service (ES) 域的连接和身份验证，Check connection and authentication for Internet exposed Elasticsearch Service (ES) domains。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.88 [extra788] Check connection and authentication for Internet exposed Amazon Elasticsearch Service (ES) domains - es [Critical]', '[{\"defaultValue\":\"extra788\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('0b9cbfc6-06d9-4a9c-898b-e2dccc810ad5', '7.87 AWS Elasticsearch/Kibana 端口(公开)检测', 1, 'CriticalRisk', 'AWS 检查 Internet 公开的 Elasticsearch/Kibana 端口的连接和身份验证，Check connection and authentication for Internet exposed Elasticsearch/Kibana ports。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.87 [extra787] Check connection and authentication for Internet exposed Elasticsearch/Kibana ports - es [Critical]', '[{\"defaultValue\":\"extra787\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('6f0f6236-51e1-458a-8ca3-163b70eb284b', '7.86 AWS EC2 实例 (IMDSv2) 域检测', 1, 'MediumRisk', 'AWS 检查 EC2 实例元数据服务版本 2 (IMDSv2) 是否已启用且需要，Check if EC2 Instance Metadata Service Version 2 (IMDSv2) is Enabled and Required。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.86 [extra786] Check if EC2 Instance Metadata Service Version 2 (IMDSv2) is Enabled and Required  - ec2 [Medium]', '[{\"defaultValue\":\"extra786\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('20677c87-5699-4432-ad50-82007acf0dc7', '7.85 AWS Amazon Elasticsearch Service (可用更新) 域检测', 1, 'LowRisk', 'AWS 检查 Amazon Elasticsearch Service (ES) 域是否有可用更新，Check if Amazon Elasticsearch Service (ES) domains have updates available。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.85 [extra785] Check if Amazon Elasticsearch Service (ES) domains have updates available - es [Low]', '[{\"defaultValue\":\"extra785\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('0c701e53-d5be-44ae-b89e-1cbc478f1e1c', '7.84 AWS Amazon Elasticsearch Service (内部数据库) 域检测', 1, 'MediumRisk', 'AWS 检查是否启用了 Amazon Elasticsearch Service (ES) 域内部用户数据库，Check if Amazon Elasticsearch Service (ES) domains internal user database enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.84 [extra784] Check if Amazon Elasticsearch Service (ES) domains internal user database enabled - es [Medium]', '[{\"defaultValue\":\"extra784\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('c2ed97ee-8d3c-4101-b396-211e0d9cca46', '7.83 AWS Amazon Elasticsearch Service (HTTPS) 域检测', 1, 'MediumRisk', 'AWS 检查 Amazon Elasticsearch Service (ES) 域是否已启用强制 HTTPS，Check if Amazon Elasticsearch Service (ES) domains has enforce HTTPS enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.83 [extra783] Check if Amazon Elasticsearch Service (ES) domains has enforce HTTPS enabled - es [Medium]', '[{\"defaultValue\":\"extra783\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('12d1f211-4c23-4457-9ca9-55f7c272b2ec', '7.82 AWS Amazon Elasticsearch Service (ES节点加密) 域检测', 1, 'MediumRisk', 'AWS 检查 Amazon Elasticsearch Service (ES) 域是否启用了节点到节点加密，Check if Amazon Elasticsearch Service (ES) domains has node-to-node encryption enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.82 [extra782] Check if Amazon Elasticsearch Service (ES) domains has node-to-node encryption enabled - es [Medium]', '[{\"defaultValue\":\"extra782\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('4b0dbf3a-fb62-4746-a75b-c0a43ac2bcc0', '7.81 AWS Amazon Elasticsearch Service (ES) 域检测', 1, 'MediumRisk', 'AWS 检查 Amazon Elasticsearch Service (ES) 域是否启用了静态加密，Check if Amazon Elasticsearch Service (ES) domains has encryption at-rest enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.81 [extra781] Check if Amazon Elasticsearch Service (ES) domains has encryption at-rest enabled - es [Medium]', '[{\"defaultValue\":\"extra781\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('0a0c6b87-94f9-4323-84a7-b250d870dbdf', '7.80 AWS Amazon OpenSearch Service 域检测', 1, 'HighRisk', 'AWS 检查 Amazon OpenSearch Service 域（以前称为 Elasticsearch 或 ES）是否启用了针对 Kibana 的 Amazon Cognito 身份验证或 SAML 身份验证。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.80 [extra780] Check if Amazon OpenSearch Service domains (formerly known as Elasticsearch or ES) has either Amazon Cognito authentication or SAML authentication for Kibana enabled - es [High]', '[{\"defaultValue\":\"extra780\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('d6958875-9974-4528-8f9a-9af071f46314', '7.79 AWS VPC 安全组(Elasticsearch/Kibana 端口)检测', 1, 'HighRisk', 'AWS 确保没有安全组允许从 0.0.0.0/0 或 ::/0 进入 Elasticsearch/Kibana 端口，Ensure no security groups allow ingress to Elasticsearch/Kibana ports。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.79 [extra779] Ensure no security groups allow ingress from 0.0.0.0/0 or ::/0 to Elasticsearch/Kibana ports - ec2 [High]', '[{\"defaultValue\":\"extra779\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('52639d74-170a-47bb-aeec-327c753ac2b2', '7.78 AWS VPC 安全组(公共 IPv4 CIDR 范围)检测', 1, 'MediumRisk', 'AWS 查找具有广泛开放的公共 IPv4 CIDR 范围（非 RFC1918）的 VPC 安全组，Find VPC security groups with wide-open public IPv4 CIDR ranges (non-RFC1918)。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.78 [extra778] Find VPC security groups with wide-open public IPv4 CIDR ranges (non-RFC1918)  - ec2 [Medium]', '[{\"defaultValue\":\"extra778\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('c986fdc5-aae9-4b40-a4d6-eda41636c45f', '7.77 AWS VPC 安全组(超过 50 个入口或出口规则)检测', 1, 'MediumRisk', 'AWS 查找具有超过 50 个入口或出口规则的 VPC 安全组，Find VPC security groups with more than 50 ingress or egress rules。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.77 [extra777] Find VPC security groups with more than 50 ingress or egress rules  - ec2 [Medium]', '[{\"defaultValue\":\"extra777\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('affb6d40-1958-47b6-bfe3-a717790c17ce', '7.76 AWS ECR 镜像扫描检测', 1, 'MediumRisk', 'AWS 检查 ECR 镜像扫描是否发现最新镜像版本中的漏洞，Check if ECR image scan found vulnerabilities in the newest image version。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.76 [extra776] Check if ECR image scan found vulnerabilities in the newest image version - ecr [Medium]', '[{\"defaultValue\":\"extra776\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('c283c9f4-40fe-426c-b56b-e36aca559cd7', '7.75 AWS EC2 Auto Scaling 启动配置检测', 1, 'CriticalRisk', 'AWS 在 EC2 Auto Scaling 启动配置中查找机密，Find secrets in EC2 Auto Scaling Launch Configuration。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.75 [extra775] Find secrets in EC2 Auto Scaling Launch Configuration  - autoscaling [Critical]', '[{\"defaultValue\":\"extra775\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('6f82c779-7ddc-483c-9947-9108acde05f7', '7.74 AWS 凭据 (禁用)检测', 1, 'MediumRisk', 'AWS 确保禁用 30 天或更长时间未使用的凭据，Ensure credentials unused for 30 days or greater are disabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.74 [extra774] Ensure credentials unused for 30 days or greater are disabled - iam [Medium]', '[{\"defaultValue\":\"extra774\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('bc3de762-ca52-4110-bd3e-a4a7ce279df1', '7.73 AWS CloudFront (WAF)检测', 1, 'MediumRisk', 'AWS 检查 CloudFront 分配是否使用 WAF，Check if CloudFront distributions are using WAF。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.73 [extra773] Check if CloudFront distributions are using WAF  - cloudfront [Medium]', '[{\"defaultValue\":\"extra773\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('394638cf-7baa-4073-aec3-48d1a18a705c', '7.72 AWS 弹性 IP(使用)检测', 1, 'LowRisk', 'AWS 检查弹性 IP 是否未使用，Check if elastic IPs are unused。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.72 [extra772] Check if elastic IPs are unused  - ec2 [Low]', '[{\"defaultValue\":\"extra772\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('de5b78fe-e2e5-423e-9d93-72aadbddf89d', '7.71 AWS S3 存储桶(WRITE 访问)检测', 1, 'CriticalRisk', 'AWS 检查 S3 存储桶是否具有允许 WRITE 访问的策略，Check if S3 buckets have policies which allow WRITE access。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.71 [extra771] Check if S3 buckets have policies which allow WRITE access  - s3 [Critical]', '[{\"defaultValue\":\"extra771\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('1cffb8e2-a467-4472-80b3-c44aa58b3c32', '7.70 AWS EC2 实例(实例配置文件)检测', 1, 'MediumRisk', 'AWS 检查带有附加实例配置文件的面向 Internet 的 EC2 实例，Check for internet facing EC2 instances with Instance Profiles attached。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.70 [extra770] Check for internet facing EC2 instances with Instance Profiles attached  - ec2 [Medium]', '[{\"defaultValue\":\"extra770\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('b4fd81de-1867-4416-88c2-f67fc4bdcd33', '7.69 AWS IAM 访问分析器检测', 1, 'HighRisk', 'AWS 检查是否启用了 IAM 访问分析器及其结果，Check if IAM Access Analyzer is enabled and its findings。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.69 [extra769] Check if IAM Access Analyzer is enabled and its findings  - accessanalyzer [High]', '[{\"defaultValue\":\"extra769\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('f2bba305-c147-4f92-b71c-964a3d0e9634', '7.68 AWS ECS 任务检测', 1, 'CriticalRisk', 'AWS 在 ECS 任务定义环境变量中查找密钥，Find secrets in ECS task definitions environment variables。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.68 [extra768] Find secrets in ECS task definitions environment variables  - ecs [Critical]', '[{\"defaultValue\":\"extra768\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('7c0a3a24-c4f5-432d-a53b-b643112f6512', '7.67 AWS CloudFront (字段级加密)检测', 1, 'LowRisk', 'AWS 检查 CloudFront 分配是否启用了字段级加密，Check if CloudFront distributions have Field Level Encryption enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.67 [extra767] Check if CloudFront distributions have Field Level Encryption enabled  - cloudfront [Low]', '[{\"defaultValue\":\"extra767\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('c2a0e506-c528-44a5-8e08-f7537af796cc', '7.65 AWS ECR 图像扫描检测', 1, 'MediumRisk', 'AWS 检查是否启用了推送时的 ECR 图像扫描，Check if ECR image scan on push is enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.65 [extra765] Check if ECR image scan on push is enabled  - ecr [Medium]', '[{\"defaultValue\":\"extra765\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('f3829d4f-3c1b-45a2-bbd1-f48b02b6bc6d', '7.64 AWS S3 存储桶(安全传输策略)检测', 1, 'MediumRisk', 'AWS 检查 S3 存储桶是否具有安全传输策略，Check if S3 buckets have secure transport policy。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.64 [extra764] Check if S3 buckets have secure transport policy  - s3 [Medium]', '[{\"defaultValue\":\"extra764\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('36885715-b4fe-4879-a98f-5910155dd487', '7.63 AWS S3 存储桶(对象版本控制)检测', 1, 'MediumRisk', 'AWS 检查 S3 存储桶是否启用了对象版本控制，Check if S3 buckets have object versioning enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.63 [extra763] Check if S3 buckets have object versioning enabled  - s3 [Medium]', '[{\"defaultValue\":\"extra763\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('25309b12-bac6-42f7-b8cd-f18785eb3f08', '7.62 AWS Lambda 运行时检测', 1, 'MediumRisk', 'AWS 查找过时的 Lambda 运行时，Find obsolete Lambda runtimes  - lambda。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.62 [extra762] Find obsolete Lambda runtimes  - lambda [Medium]', '[{\"defaultValue\":\"extra762\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('05903885-b7a0-4c9a-8085-691f3a5f9561', '7.61 AWS EBS 默认加密检测', 1, 'MediumRisk', 'AWS 检查 EBS 默认加密是否已激活，Check if EBS Default Encryption is activated。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.61 [extra761] Check if EBS Default Encryption is activated  - ec2 [Medium]', '[{\"defaultValue\":\"extra761\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('ccd84ca5-08e4-4512-9561-baa6896e24c0', '7.60 AWS Lambda 函数代码检测', 1, 'CriticalRisk', 'AWS 在 Lambda 函数代码中查找秘密，Find secrets in Lambda functions code。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.60 [extra760] Find secrets in Lambda functions code  - lambda [Critical]', '[{\"defaultValue\":\"extra760\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('6bbc77f9-270b-4649-91ea-111d9710aba6', '7.59 AWS Lambda 函数变量检测', 1, 'CriticalRisk', 'AWS 在 Lambda 函数变量中查找秘密，Find secrets in Lambda functions variables。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.59 [extra759] Find secrets in Lambda functions variables  - lambda [Critical]', '[{\"defaultValue\":\"extra759\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('b0a9ff2d-5211-41b6-aaaf-6e98bd2b38fe', '7.58 AWS EC2 实例(超过 12 个月)检测', 1, 'MediumRisk', 'AWS 检查超过 12 个月的 EC2 实例，Check EC2 Instances older than 12 months。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.58 [extra758] Check EC2 Instances older than 12 months  - ec2 [Medium]', '[{\"defaultValue\":\"extra758\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('3a5cf37d-6ec7-4e59-a4b4-12c02aebbca3', '7.57 AWS EC2 实例(超过 6 个月)检测', 1, 'MediumRisk', 'AWS 检查超过 6 个月的 EC2 实例，Check EC2 Instances older than 6 months。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.57 [extra757] Check EC2 Instances older than 6 months - ec2 [Medium]', '[{\"defaultValue\":\"extra757\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('c14f161a-8a1f-4056-afec-0bc3c56243bc', '7.55 AWS 安全组(11211)检测', 1, 'HighRisk', 'AWS 确保没有安全组允许从 0.0.0.0/0 或 ::/0 进入 Memcached 端口 11211，Ensure no security groups allow ingress from 0.0.0.0/0 or ::/0 to Memcached port 11211。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.55 [extra755] Ensure no security groups allow ingress from 0.0.0.0/0 or ::/0 to Memcached port 11211 - ec2 [High]', '[{\"defaultValue\":\"extra755\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('795dba0c-e2d1-40e1-9af4-2e7b99672e84', '7.54 AWS 安全组(7199 或 9160 或 8888)检测', 1, 'HighRisk', 'AWS 确保没有安全组允许 0.0.0.0/0 或 ::/0 进入 Cassandra 端口7199/9160/8888，Ensure no security groups allow ingress to Cassandra ports 7199/9160/8888。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.54 [extra754] Ensure no security groups allow ingress from 0.0.0.0/0 or ::/0 to Cassandra ports 7199 or 9160 or 8888 - ec2 [High]', '[{\"defaultValue\":\"extra754\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('ecc005d5-cf4b-496b-b423-47ef1c4fbf2b', '7.53 AWS 安全组(27017 和 27018)检测', 1, 'HighRisk', 'AWS 确保没有安全组允许从 0.0.0.0/0 或 ::/0 进入 MongoDB 端口 27017/27018，Ensure no security groups allow ingress from 0.0.0.0/0 or ::/0 to MongoDB ports 27017/27018。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.53 [extra753] Ensure no security groups allow ingress from 0.0.0.0/0 or ::/0 to MongoDB ports 27017 and 27018 - ec2 [High]', '[{\"defaultValue\":\"extra753\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('b6d9ba86-e7a2-4cb9-8a82-fadfdac75bad', '7.52 AWS 安全组(6379)检测', 1, 'HighRisk', 'AWS 确保没有安全组允许从 0.0.0.0/0 或 ::/0 进入 Redis 端口 6379，Ensure no security groups allow ingress from 0.0.0.0/0 or ::/0 to Redis port 6379。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.52 [extra752] Ensure no security groups allow ingress from 0.0.0.0/0 or ::/0 to Redis port 6379 - ec2 [High]', '[{\"defaultValue\":\"extra752\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('c7da492a-c477-4a55-a783-383a80ad62d9', '7.51 AWS 安全组(5432)检测', 1, 'HighRisk', 'AWS 确保没有安全组允许从 0.0.0.0/0 或 ::/0 进入 Postgres 端口 5432，Ensure no security groups allow ingress from 0.0.0.0/0 or ::/0 to Postgres port 5432。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.51 [extra751] Ensure no security groups allow ingress from 0.0.0.0/0 or ::/0 to Postgres port 5432 - ec2 [High]', '[{\"defaultValue\":\"extra751\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('4e4b0628-dfea-4b7e-ac5c-fd31fc109cf6', '7.50 AWS 安全组(3306)检测', 1, 'HighRisk', 'AWS 确保没有安全组允许从 0.0.0.0/0 或 ::/0 进入 MySQL 端口 3306，Ensure no security groups allow ingress from 0.0.0.0/0 or ::/0 to MySQL port 3306。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.50 [extra750] Ensure no security groups allow ingress from 0.0.0.0/0 or ::/0 to MySQL port 3306 - ec2 [High]', '[{\"defaultValue\":\"extra750\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('84a1d4fe-3d43-43dd-885d-061536e9927e', '7.49 AWS 安全组(1521 或 2483)检测', 1, 'HighRisk', 'AWS 确保没有安全组允许从 0.0.0.0/0 或 ::/0 进入 Oracle 端口 1521 或 2483，Ensure no security groups allow ingress from 0.0.0.0/0 or ::/0 to Oracle ports 1521 or 2483。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.49 [extra749] Ensure no security groups allow ingress from 0.0.0.0/0 or ::/0 to Oracle ports 1521 or 2483 - ec2 [High]', '[{\"defaultValue\":\"extra749\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('d11ce3c8-73c3-400e-b147-5ac290ebfa71', '7.48 AWS 安全组(任何端口)检测', 1, 'HighRisk', 'AWS 确保没有安全组允许从 0.0.0.0/0 或 ::/0 进入任何端口，Ensure no security groups allow ingress from 0.0.0.0/0 or ::/0 to any port。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.48 [extra748] Ensure no security groups allow ingress from 0.0.0.0/0 or ::/0 to any port - ec2 [High]', '[{\"defaultValue\":\"extra748\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('11eee3ed-6ec6-45f8-877b-f08dcd80b0aa', '7.47 AWS RDS 实例(CloudWatch Logs)检测', 1, 'MediumRisk', 'AWS 检查 RDS 实例是否与 CloudWatch Logs 集成，Check if RDS instances is integrated with CloudWatch Logs。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.47 [extra747] Check if RDS instances is integrated with CloudWatch Logs - rds [Medium]', '[{\"defaultValue\":\"extra747\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('b2d20c54-7fe2-4f35-b1f9-a2ac22ac9082', '7.46 AWS API 网关(授权)检测', 1, 'MediumRisk', 'AWS 检查 API 网关是否已配置授权方，Check if API Gateway has configured authorizers。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.46 [extra746] Check if API Gateway has configured authorizers - apigateway [Medium]', '[{\"defaultValue\":\"extra746\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('b15356d2-75c2-4f3d-a40b-807e9638deb6', '7.45 AWS API 网关(公共)检测', 1, 'MediumRisk', 'AWS 检查 API 网关端点是公共的还是私有的，Check if API Gateway endpoint is public or private。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.45 [extra745] Check if API Gateway endpoint is public or private - apigateway [Medium]', '[{\"defaultValue\":\"extra745\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('db53b3f2-6826-4852-96d5-feb54d63e451', '7.44 AWS API 网关(WAF ACL)检测', 1, 'MediumRisk', 'AWS 检查 API 网关是否附加了 WAF ACL，Check if API Gateway has a WAF ACL attached。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.44 [extra744] Check if API Gateway has a WAF ACL attached - apigateway [Medium]', '[{\"defaultValue\":\"extra744\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('67b27d8e-cd62-42a5-a065-bb4e4e1413a5', '7.43 AWS API 网关(客户端证书)检测', 1, 'MediumRisk', 'AWS 检查 API 网关是否启用了客户端证书以访问您的后端端点，Check if API Gateway has client certificate enabled to access your backend endpoint。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.43 [extra743] Check if API Gateway has client certificate enabled to access your backend endpoint - apigateway [Medium]', '[{\"defaultValue\":\"extra743\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('897d5f04-79b6-4f5a-94c1-091558714ae1', '7.42 AWS CloudFormation(密钥)检测', 1, 'CriticalRisk', 'AWS 在 CloudFormation 输出中查找密钥，Find secrets in CloudFormation outputs。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.42 [extra742] Find secrets in CloudFormation outputs - cloudformation [Critical]', '[{\"defaultValue\":\"extra742\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('fa080bcf-6efb-4091-ab4d-210ef361fc68', '7.41 AWS EC2(机密)检测', 1, 'CriticalRisk', 'AWS 在 EC2 用户数据中查找机密，Find secrets in EC2 User Data。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.41 [extra741] Find secrets in EC2 User Data - ec2 [Critical]', '[{\"defaultValue\":\"extra741\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('048200b5-ef95-4205-8479-80296d098523', '7.40 AWS EBS 快照(加密)检测', 1, 'MediumRisk', 'AWS 检查 EBS 快照是否加密，Check if EBS snapshots are encrypted。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.40 [extra740] Check if EBS snapshots are encrypted - ec2 [Medium]', '[{\"defaultValue\":\"extra740\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('678616af-c5bd-4e57-93b1-b937a8c3f6fc', '7.39 AWS RDS 实例(备份)检测', 1, 'MediumRisk', 'AWS 检查 RDS 实例是否启用了备份，Check if RDS instances have backup enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.39 [extra739] Check if RDS instances have backup enabled - rds [Medium]', '[{\"defaultValue\":\"extra739\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('7b29abcd-efec-453c-a407-cb281f7ffebc', '7.38 AWS CloudFront(HTTPS)检测', 1, 'MediumRisk', 'AWS 检查 CloudFront 分配是否设置为 HTTPS，Check if CloudFront distributions are set to HTTPS。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.38 [extra738] Check if CloudFront distributions are set to HTTPS - cloudfront [Medium]', '[{\"defaultValue\":\"extra738\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('ea90a3cf-377f-485d-b1b3-6fcf3092fcec', '7.36 AWS KMS 密钥检测', 1, 'CriticalRisk', 'AWS 检查暴露的 KMS 密钥，Check exposed KMS keys。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.36 [extra736] Check exposed KMS keys - kms [Critical]', '[{\"defaultValue\":\"extra736\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('e7db20c0-3151-4ebd-8ec5-c1f553c4c136', '7.35 AWS RDS 实例 (加密) 检测', 1, 'MediumRisk', 'AWS 检查 RDS 实例存储是否加密，Check if RDS instances storage is encrypted。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.35 [extra735] Check if RDS instances storage is encrypted - rds [Medium]', '[{\"defaultValue\":\"extra735\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('8ef1d4de-0c47-446c-a258-6c6df4e1dbe3', '7.34 AWS S3 存储桶 (SSE) 检测', 1, 'MediumRisk', 'AWS 检查 S3 存储桶是否启用了默认加密 (SSE) 或使用存储桶策略来执行它，Check if S3 buckets have default encryption (SSE) enabled or use a bucket policy to enforce it。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.34 [extra734] Check if S3 buckets have default encryption (SSE) enabled or use a bucket policy to enforce it - s3 [Medium]', '[{\"defaultValue\":\"extra734\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('94057d78-563d-4d24-870f-246c7a1ba6a0', '7.33 AWS STS (SAML) 检测', 1, 'LowRisk', 'AWS 检查是否有 SAML 提供者然后可以使用 STS，Check if there are SAML Providers then STS can be used。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.33 [extra733] Check if there are SAML Providers then STS can be used - iam [Low]', '[{\"defaultValue\":\"extra733\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('7ef05cf8-0d7d-46e0-a6c4-a3e2dfba01ef', '7.32 AWS CloudFront 分配检测', 1, 'LowRisk', 'AWS 检查是否在 CloudFront 分配中启用了地理限制，Check if Geo restrictions are enabled in CloudFront distributions。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.32 [extra732] Check if Geo restrictions are enabled in CloudFront distributions - cloudfront [Low]', '[{\"defaultValue\":\"extra732\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('16488f91-abeb-4303-9d1c-ed03e6eddb2b', '7.31 AWS SNS 主题检测', 1, 'CriticalRisk', 'AWS 检查 SNS 主题是否将策略设置为 Public，Check if SNS topics have policy set as Public。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.31 [extra731] Check if SNS topics have policy set as Public - sns [Critical]', '[{\"defaultValue\":\"extra731\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('5de4eb69-b295-4697-90ca-37f5da1c793d', '7.30 AWS ACM 证书到期检测', 1, 'HighRisk', 'AWS 检查 ACM 证书是否将在 7 天或更短时间内到期，Check if ACM Certificates are about to expire in 7 days or less。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.30 [extra730] Check if ACM Certificates are about to expire in 7 days or less - acm [High]', '[{\"defaultValue\":\"extra730\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('1a89ad74-1734-4009-80c1-08404b973793', '7.29 AWS 未加密的 EBS 卷检测', 1, 'MediumRisk', 'AWS 确保没有未加密的 EBS 卷，Ensure there are no EBS Volumes unencrypted。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.29 [extra729] Ensure there are no EBS Volumes unencrypted - ec2 [Medium]', '[{\"defaultValue\":\"extra729\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('b96f6b16-bf46-4d21-acdd-75d192b78676', '7.28 AWS SQS 队列服务器端加密检测', 1, 'MediumRisk', 'AWS 检查 SQS 队列是否启用了服务器端加密，Check if SQS queues have Server Side Encryption enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.28 [extra728] Check if SQS queues have Server Side Encryption enabled - sqs [Medium]', '[{\"defaultValue\":\"extra728\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('e6060f11-00c7-4a81-bbe2-5292b34c949f', '7.27 AWS SQS 队列策略设置检测', 1, 'CriticalRisk', 'AWS 检查 SQS 队列是否将策略设置为公共，Check if SQS queues have policy set as Public。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.27 [extra727] Check if SQS queues have policy set as Public - sqs [Critical]', '[{\"defaultValue\":\"extra727\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('4f0bcacd-4746-44c2-bc87-dc179f574d8c', '7.26 AWS Trusted Advisor 检测', 1, 'MediumRisk', 'AWS 检查 Trusted Advisor 是否有错误和警告，Check Trusted Advisor for errors and warnings。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.26 [extra726] Check Trusted Advisor for errors and warnings - trustedadvisor [Medium]', '[{\"defaultValue\":\"extra726\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('29bb51e4-b8f0-4242-83d5-4408ce757bef', '7.25 AWS S3 存储桶(CloudTrail 日志)检测', 1, 'MediumRisk', 'AWS 检查 S3 存储桶是否在 CloudTrail 中启用了对象级日志记录，Check if S3 buckets have Object-level logging enabled in CloudTrail。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.25 [extra725] Check if S3 buckets have Object-level logging enabled in CloudTrail - s3 [Medium]', '[{\"defaultValue\":\"extra725\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('7341d661-30f7-4425-a6e7-959404132ac7', '7.24 AWS ACM 证书检测', 1, 'MediumRisk', 'AWS 检查 ACM 证书是否启用了证书透明度日志记录，Check if ACM certificates have Certificate Transparency logging enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.24 [extra724] Check if ACM certificates have Certificate Transparency logging enabled - acm [Medium]', '[{\"defaultValue\":\"extra724\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('53748c53-ea71-4c56-8dd3-328d7697268b', '7.23 AWS RDS 快照和集群快照检测', 1, 'CriticalRisk', 'AWS 检查 RDS 快照和集群快照是否公开，Check if RDS Snapshots and Cluster Snapshots are public。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.23 [extra723] Check if RDS Snapshots and Cluster Snapshots are public - rds [Critical]', '[{\"defaultValue\":\"extra723\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('1d88fcff-44c5-40b5-aa7b-046dd048fa3a', '7.22 AWS API 网关 (日志记录) 检测', 1, 'MediumRisk', 'AWS 检查 API 网关是否启用了日志记录，Check if API Gateway has logging enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.22 [extra722] Check if API Gateway has logging enabled - apigateway [Medium]', '[{\"defaultValue\":\"extra722\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('5ac7447f-bb24-4bbb-9cb1-014a28d94acc', '7.21 AWS Redshift 集群 (审计日志) 检测', 1, 'MediumRisk', 'AWS 检查 Redshift 集群是否启用了审计日志记录，Check if Redshift cluster has audit logging enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.21 [extra721] Check if Redshift cluster has audit logging enabled - redshift [Medium]', '[{\"defaultValue\":\"extra721\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('9aec9ac1-71ca-442e-9678-3cdb42f16bed', '7.20 AWS CloudTrail (Lambda 函数调用 API 操作) 检测', 1, 'LowRisk', 'AWS 检查 CloudTrail 是否正在记录 Lambda 函数调用 API 操作，Check if Lambda functions invoke API operations are being recorded by CloudTrail 。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.20 [extra720] Check if Lambda functions invoke API operations are being recorded by CloudTrail - lambda [Low]', '[{\"defaultValue\":\"extra720\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('a9ac2536-088a-45f5-94e6-06abc13eea71', '7.19 AWS Route53 (CloudWatch Logs) 检测', 1, 'MediumRisk', 'AWS 检查 Route53 公共托管区域是否正在将查询记录到 CloudWatch Logs，Check if Route53 public hosted zones are logging queries to CloudWatch Logs。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.19 [extra719] Check if Route53 public hosted zones are logging queries to CloudWatch Logs - route53 [Medium]', '[{\"defaultValue\":\"extra719\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('7fdd8709-73e6-4bfd-86fe-dd52a1d00aeb', '7.18 AWS S3 存储桶 (日志) 检测', 1, 'MediumRisk', 'AWS 检查 S3 存储桶是否启用了服务器访问日志记录，Check if S3 buckets have server access logging enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.18 [extra718] Check if S3 buckets have server access logging enabled - s3 [Medium]', '[{\"defaultValue\":\"extra718\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('23985b97-2795-4867-b7af-c0b7134fcc17', '7.17 AWS Elastic Load Balancer (日志) 检测', 1, 'MediumRisk', 'AWS 检查 Elastic Load Balancer 是否启用了日志记录，Check if Elastic Load Balancers have logging enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.17 [extra717] Check if Elastic Load Balancers have logging enabled - elb [Medium]', '[{\"defaultValue\":\"extra717\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('74d3aed1-da57-43d4-bb75-2967f7398856', '7.16 AWS Elasticsearch Service (策略访问) 检测', 1, 'CriticalRisk', 'AWS 检查 Elasticsearch Service (ES) 域是否设置为公共或是否具有开放策略访问权限，Check if Elasticsearch Service (ES) domains are set as Public or if it has open policy access。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.16 [extra716] Check if Amazon Elasticsearch Service (ES) domains are set as Public or if it has open policy access - es [Critical]', '[{\"defaultValue\":\"extra716\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('ad17f911-50f4-42db-bb6f-b8c0ec651425', '7.15 AWS Elasticsearch Service (ES日志) 检测', 1, 'MediumRisk', 'AWS 检查 Amazon Elasticsearch Service (ES) 域是否启用了日志记录，Check if Amazon Elasticsearch Service (ES) domains have logging enabled 。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.15 [extra715] Check if Amazon Elasticsearch Service (ES) domains have logging enabled - es [Medium]', '[{\"defaultValue\":\"extra715\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('75676d83-dede-4a0a-93fd-b93003cfaf3b', '7.14 AWS CloudFront 分配检测', 1, 'MediumRisk', 'AWS 检查 CloudFront 分配是否启用了日志记录，Check if CloudFront distributions have logging enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.14 [extra714] Check if CloudFront distributions have logging enabled - cloudfront [Medium]', '[{\"defaultValue\":\"extra714\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('78ad5a17-0fda-4650-8bab-3217c2e3b4e0', '7.13 AWS GuardDuty 检测', 1, 'HighRisk', 'AWS 检查是否启用了 GuardDuty，Check if GuardDuty is enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.13 [extra713] Check if GuardDuty is enabled - guardduty [High]', '[{\"defaultValue\":\"extra713\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('96ccf8ea-3d58-4342-a25d-16026e655cfe', '7.12 AWS Amazon Macie 检测', 1, 'LowRisk', 'AWS 检查是否启用了 Amazon Macie，Check if Amazon Macie is enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.12 [extra712] Check if Amazon Macie is enabled - macie [Low]', '[{\"defaultValue\":\"extra712\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('9e0f8f7a-9241-4a5d-8db8-d84cd82c340f', '7.11 AWS Redshift 集群公网访问检测', 1, 'HighRisk', 'AWS 检查可公开访问的 Redshift 集群，Check for Publicly Accessible Redshift Clusters。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.11 [extra711] Check for Publicly Accessible Redshift Clusters - redshift [High]', '[{\"defaultValue\":\"extra711\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('7d09e3f5-01f8-454b-a327-34f5bb514d8b', '7.10 AWS EC2 实例公网访问检测', 1, 'MediumRisk', 'AWS 检查面向 Internet 的 EC2 实例，Check for internet facing EC2 Instances。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.10 [extra710] Check for internet facing EC2 Instances - ec2 [Medium]', '[{\"defaultValue\":\"extra710\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('189571d9-b536-4a2b-82e3-c2029285422d', '7.9 AWS 弹性负载均衡器检测', 1, 'MediumRisk', 'AWS 检查面向 Internet 的弹性负载均衡器，Check for internet facing Elastic Load Balancers。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.9 [extra79] Check for internet facing Elastic Load Balancers - elb [Medium]', '[{\"defaultValue\":\"extra79\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('c1c1dbb6-0372-4a28-a8aa-332361e0a3fd', '7.8 AWS RDS 实例检测', 1, 'CriticalRisk', 'AWS 确保没有可公开访问的 RDS 实例，Ensure there are no Public Accessible RDS instances。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.8 [extra78] Ensure there are no Public Accessible RDS instances - rds [Critical]', '[{\"defaultValue\":\"extra78\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('70320589-0b7a-4a04-b105-1da1dca0b121', '7.7 AWS ECR 存储库检测', 1, 'CriticalRisk', 'AWS 确保没有 ECR 存储库设置为公共，Ensure there are no ECR repositories set as Public。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.7 [extra77] Ensure there are no ECR repositories set as Public - ecr [Critical]', '[{\"defaultValue\":\"extra77\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('c91192cf-8a2d-49b1-99ed-d21b709d645b', '7.6 AWS EC2 AMI 检测', 1, 'CriticalRisk', 'AWS 确保没有 EC2 AMI 设置为公共，Ensure there are no EC2 AMIs set as Public。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.6 [extra76] Ensure there are no EC2 AMIs set as Public - ec2 [Critical]', '[{\"defaultValue\":\"extra76\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('e9fd1bc6-5804-4d54-b736-19deb3ce05c3', '7.5 AWS 安全组未使用检测', 1, 'HighRisk', 'AWS 确保没有未使用的安全组，Ensure there are no Security Groups not being used。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.5 [extra75] Ensure there are no Security Groups not being used - ec2 [Informational]', '[{\"defaultValue\":\"extra75\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('9382c928-b818-4f51-8585-8bd831711223', '7.4 AWS 安全组入口检测', 1, 'HighRisk', 'AWS 确保没有使用入口过滤的安全组，Ensure there are no Security Groups without ingress filtering being used。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.4 [extra74] Ensure there are no Security Groups without ingress filtering being used - ec2 [High]', '[{\"defaultValue\":\"extra74\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('6914a08d-0689-422c-a8f2-881ce9ea202c', '7.3 AWS S3 存储桶(用户开放)检测', 1, 'CriticalRisk', 'AWS 确保没有对所有人或任何 AWS 用户开放的 S3 存储桶，Ensure there are no S3 buckets open to Everyone or Any AWS user。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.3 [extra73] Ensure there are no S3 buckets open to Everyone or Any AWS user - s3 [Critical]', '[{\"defaultValue\":\"extra73\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('7cd1eee3-8f47-426d-9182-30037c3bcdac', '7.2 AWS EBS 快照检测', 1, 'CriticalRisk', 'AWS 确保没有 EBS 快照设置为公开，Ensure there are no EBS Snapshots set as Public。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.2 [extra72] Ensure there are no EBS Snapshots set as Public - ec2 [Critical]', '[{\"defaultValue\":\"extra72\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('a83198cb-b852-4dc9-a99b-c0756f825576', '7.1 AWS AdministratorAccess 策略检测', 1, 'HighRisk', 'AWS 确保具有 AdministratorAccess 策略的组的用户启用了 MFA 令牌，Ensure users of groups with AdministratorAccess policy have MFA tokens enabled。', '7.0 Extras - all non CIS specific checks - [extras] **************** -  []\n\n7.1 [extra71] Ensure users of groups with AdministratorAccess policy have MFA tokens enabled - iam [High]', '[{\"defaultValue\":\"extra71\",\"name\":\"检测规则\",\"key\":\"check\",\"required\":true}]', 'hummer-aws-plugin', 'Amazon Web Services', 'aws.png', concat(unix_timestamp(now()), '006'), 1, 'prowler', NULL);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('a83198cb-b852-4dc9-a99b-c0756f825576', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('7cd1eee3-8f47-426d-9182-30037c3bcdac', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('6914a08d-0689-422c-a8f2-881ce9ea202c', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('9382c928-b818-4f51-8585-8bd831711223', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('e9fd1bc6-5804-4d54-b736-19deb3ce05c3', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('c91192cf-8a2d-49b1-99ed-d21b709d645b', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('70320589-0b7a-4a04-b105-1da1dca0b121', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('c1c1dbb6-0372-4a28-a8aa-332361e0a3fd', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('189571d9-b536-4a2b-82e3-c2029285422d', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('7d09e3f5-01f8-454b-a327-34f5bb514d8b', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('9e0f8f7a-9241-4a5d-8db8-d84cd82c340f', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('96ccf8ea-3d58-4342-a25d-16026e655cfe', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('78ad5a17-0fda-4650-8bab-3217c2e3b4e0', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('75676d83-dede-4a0a-93fd-b93003cfaf3b', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('ad17f911-50f4-42db-bb6f-b8c0ec651425', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('74d3aed1-da57-43d4-bb75-2967f7398856', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('23985b97-2795-4867-b7af-c0b7134fcc17', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('7fdd8709-73e6-4bfd-86fe-dd52a1d00aeb', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('a9ac2536-088a-45f5-94e6-06abc13eea71', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('9aec9ac1-71ca-442e-9678-3cdb42f16bed', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('5ac7447f-bb24-4bbb-9cb1-014a28d94acc', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('1d88fcff-44c5-40b5-aa7b-046dd048fa3a', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('53748c53-ea71-4c56-8dd3-328d7697268b', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('7341d661-30f7-4425-a6e7-959404132ac7', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('29bb51e4-b8f0-4242-83d5-4408ce757bef', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('4f0bcacd-4746-44c2-bc87-dc179f574d8c', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('e6060f11-00c7-4a81-bbe2-5292b34c949f', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('b96f6b16-bf46-4d21-acdd-75d192b78676', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('1a89ad74-1734-4009-80c1-08404b973793', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('5de4eb69-b295-4697-90ca-37f5da1c793d', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('16488f91-abeb-4303-9d1c-ed03e6eddb2b', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('7ef05cf8-0d7d-46e0-a6c4-a3e2dfba01ef', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('94057d78-563d-4d24-870f-246c7a1ba6a0', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('8ef1d4de-0c47-446c-a258-6c6df4e1dbe3', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('e7db20c0-3151-4ebd-8ec5-c1f553c4c136', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('ea90a3cf-377f-485d-b1b3-6fcf3092fcec', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('7b29abcd-efec-453c-a407-cb281f7ffebc', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('678616af-c5bd-4e57-93b1-b937a8c3f6fc', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('048200b5-ef95-4205-8479-80296d098523', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('fa080bcf-6efb-4091-ab4d-210ef361fc68', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('897d5f04-79b6-4f5a-94c1-091558714ae1', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('67b27d8e-cd62-42a5-a065-bb4e4e1413a5', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('db53b3f2-6826-4852-96d5-feb54d63e451', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('b15356d2-75c2-4f3d-a40b-807e9638deb6', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('b2d20c54-7fe2-4f35-b1f9-a2ac22ac9082', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('11eee3ed-6ec6-45f8-877b-f08dcd80b0aa', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('d11ce3c8-73c3-400e-b147-5ac290ebfa71', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('84a1d4fe-3d43-43dd-885d-061536e9927e', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('4e4b0628-dfea-4b7e-ac5c-fd31fc109cf6', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('c7da492a-c477-4a55-a783-383a80ad62d9', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('b6d9ba86-e7a2-4cb9-8a82-fadfdac75bad', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('ecc005d5-cf4b-496b-b423-47ef1c4fbf2b', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('795dba0c-e2d1-40e1-9af4-2e7b99672e84', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('c14f161a-8a1f-4056-afec-0bc3c56243bc', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('3a5cf37d-6ec7-4e59-a4b4-12c02aebbca3', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('b0a9ff2d-5211-41b6-aaaf-6e98bd2b38fe', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('05903885-b7a0-4c9a-8085-691f3a5f9561', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('25309b12-bac6-42f7-b8cd-f18785eb3f08', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('36885715-b4fe-4879-a98f-5910155dd487', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('f3829d4f-3c1b-45a2-bbd1-f48b02b6bc6d', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('c2a0e506-c528-44a5-8e08-f7537af796cc', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('7c0a3a24-c4f5-432d-a53b-b643112f6512', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('f2bba305-c147-4f92-b71c-964a3d0e9634', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('b4fd81de-1867-4416-88c2-f67fc4bdcd33', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('1cffb8e2-a467-4472-80b3-c44aa58b3c32', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('de5b78fe-e2e5-423e-9d93-72aadbddf89d', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('394638cf-7baa-4073-aec3-48d1a18a705c', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('bc3de762-ca52-4110-bd3e-a4a7ce279df1', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('6f82c779-7ddc-483c-9947-9108acde05f7', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('c283c9f4-40fe-426c-b56b-e36aca559cd7', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('affb6d40-1958-47b6-bfe3-a717790c17ce', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('c986fdc5-aae9-4b40-a4d6-eda41636c45f', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('52639d74-170a-47bb-aeec-327c753ac2b2', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('d6958875-9974-4528-8f9a-9af071f46314', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('0a0c6b87-94f9-4323-84a7-b250d870dbdf', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('4b0dbf3a-fb62-4746-a75b-c0a43ac2bcc0', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('12d1f211-4c23-4457-9ca9-55f7c272b2ec', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('c2ed97ee-8d3c-4101-b396-211e0d9cca46', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('0c701e53-d5be-44ae-b89e-1cbc478f1e1c', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('20677c87-5699-4432-ad50-82007acf0dc7', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('6f0f6236-51e1-458a-8ca3-163b70eb284b', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('0b9cbfc6-06d9-4a9c-898b-e2dccc810ad5', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('0e1cd76e-2d63-499b-9e6d-a85eb00ed01a', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('d77e0bc8-6161-4a7a-a6f4-d4ce788e34df', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('2bb33de6-89e2-4ab3-805a-3cc2e7cb42ed', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('e51aa0cd-7617-436f-b429-7141e123e148', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('074a839d-a6df-4a96-adab-94c4e2869201', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('120344f8-4544-46c5-8de9-2018416a1757', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('8db50d8c-64e4-4506-a143-3a0a329492c9', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('52e44c5e-2702-48b8-a3a6-e221048de715', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('7fba3abd-2564-46b9-8cad-935f9f0a8683', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('05a359f2-af4e-49ca-9e48-09510dc2252a', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('b1a92471-5517-48cc-8c9d-0edcb55de693', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('bbbc54c2-a917-455b-9782-de74186160cb', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('b81b5de3-d794-4b74-bf20-36d0f9c0d74d', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('b632c31c-91b5-42a5-bc4b-bf0a2acfbf02', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('8928f014-1d7c-4793-8c28-bd34b780b3c5', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('bd5566ee-6e33-4522-9ac9-18a6ba2a8a55', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('ac632efc-bacc-4229-acd2-216684e36e41', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('8ead7b4a-5198-4feb-966b-232c0197d7bf', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('000348ec-9f32-4894-bff8-9e39abda83be', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('ac04175b-feaf-4128-8cf4-b764d96c5b7b', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('305e28d3-f68e-44b7-925b-6a31b92abf8a', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('05130e8d-e8f6-4446-b8d1-4e1e0a8fe813', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('5e4678f5-378a-44b1-849e-319f3bf84faf', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('2e2816e7-85db-4903-8aaf-7708f6de4b15', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('d18ae6a7-a060-4ee7-a59d-3575447a1ea2', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('3f8e0d58-f003-4b4a-aea4-05f40a796c6f', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('2bbd04a6-40af-4d74-a32b-4c31f52c7504', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('c16fe4e1-3589-4f74-b3e5-64504e481d3e', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('e14667da-cdef-4afe-9113-4b3d21a61f11', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('e71b1798-e390-4e05-bd39-47ccde2244c5', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('4f622382-8519-4990-af03-ffa0ed9cc446', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('2d70fd48-f88f-45ab-878e-f9e06499b741', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('2ad08cd2-3e23-4fde-8ffd-27174e56e579', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('e389ad5a-660d-4f69-95cd-98dc1423be48', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('cf92e9e1-5d68-4bb2-85b6-ed807fdfd50f', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('90071ae9-ce0a-41a5-b743-9aafc6e65c08', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('a95f4318-d629-4a04-8f42-2056d63f09cb', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('616b9333-e7b5-46e5-8121-eb43314e8c71', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('9845da30-1314-4e12-b287-497d24c61579', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('6d47ed7f-6c69-4f21-99ec-d046a0c3037c', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('822fee45-32ac-46bb-81be-c06243e76a30', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('aa79b399-bfea-4607-ba4b-5a3f438d3c6e', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('50e66a3d-13c1-403c-a7e1-3b157cda6298', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('7c0e6e4a-f6c4-4d11-bfdd-c7231f13a322', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('5f8adceb-99aa-49ac-9ed4-d86d8bd67a0d', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('39d29fe9-74ab-450a-af0f-ae7c12896673', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('a89f5a2e-eafb-4511-a71e-599a72143408', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('5e5cc249-2b20-4c80-a278-a913e18a2b21', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('bd5f84b8-9439-40f4-8ccd-2b544f656a91', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('98e815f5-8b07-4717-a15c-389b34e4ebfc', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('8afa254e-0446-4e55-9a32-eb83bf3049ab', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('40d375c3-a1b6-4785-9325-c5c4c59bcacf', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('88c3427c-c668-49ee-987f-7b7da5b4de11', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('2803b3c5-2a11-4f3d-8bd7-acaa18bd35cf', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('55e5207c-9d0e-48a5-ba68-c01ead665443', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('23aaf8a8-820b-4b2d-a2a9-e27151fdcc12', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('2198c834-ac57-4bd4-be8a-b1fa8834fc76', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('0eda2992-8173-4c3a-bd1c-e2e48e0aad1e', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('111fabaa-249b-4a73-b9fd-19166c9073b7', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('e5b42411-47db-4358-b156-1d7a56b08b10', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('bf3990ee-324f-4618-8f2f-190df72aaf11', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('d8b035cd-6d0c-43b6-9277-e6be6fd8f8da', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('9a9eeba2-8a1c-4e3f-b486-5357a7546e08', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('10751001-d9aa-4582-a96a-471813e50512', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('65dfbac5-4ee3-4405-a5d0-98e3da849831', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('9b174f28-9ed1-4e93-9be1-adde6619d8c3', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('e3d8f69b-a3ef-47d7-b715-235fb803e9e6', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('6ca702b2-13b2-4dfb-9a8e-2aedbfbd5252', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('9d0ca4db-2cf0-45d7-a14d-8dcd18791191', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('f4190017-0459-4f7e-943d-5f299771da5b', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('53569e08-86c6-43ba-99f5-64d529477bc0', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('359a94bf-6423-4db2-96b0-86fd6de9bb25', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('41e7a337-339b-4609-8904-e362196aa9c0', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('9ddfd1d2-810e-417a-ba5f-925365b2f9f8', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('abe0b522-dabf-456f-bac6-d0d48cae4eed', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('8bd18f86-7b0d-4927-860d-83e295cc6b05', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('520959f9-f224-41fa-9a52-0df38e55eb8f', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('8a3cd7a7-3aea-4e57-b646-f97d9952614a', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('f0cfc258-0001-4423-9485-b96521535c50', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('75d0d479-4c86-418a-b805-e4d32d7b9583', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('c8926d60-6726-407d-ba1d-839effafae3c', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('3d27d878-d48e-4784-bff5-b230d2d28846', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('87786f5c-2f7d-4836-849d-900d587c38db', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('dc28da88-7551-453d-a891-5c9c23781bc4', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('bebe49c3-8e87-4c21-a2b3-7f5f6cc90136', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('095ac637-4e0d-46ef-b658-dc2abbe0ed7e', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('d1fbc80a-78c1-4214-bf99-13ec76e78b58', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('2da5e51d-889a-4dbf-a2f8-11259b8eaa56', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('7b3df26a-1839-430d-b8b1-bec22849a62b', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('a0964936-4dde-4fdf-b512-bb6a0fae8146', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('a3c12f99-237c-4dfd-b4e5-8d6d30045812', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('2d4c6320-bcd3-4599-9564-87e4e6f82eeb', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('a14ec927-6f85-47fa-89dc-4d5fbb0534eb', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('4420e7c7-578d-44e2-8a8a-d0e5691d76c2', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('14f34d63-8a93-4f70-ae25-69d29b71d573', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('90ec24a4-79de-4911-bea9-ca795a7e6eec', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('109b39e5-6878-43cb-93ab-f19a22f2ecdd', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('62d16913-61e4-46fc-abd7-4174c1ab89cc', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('978d14c5-da23-4cf8-9cd9-c0e710977250', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('8f1cd40a-bcff-4089-97e8-45337c4e8cb1', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('2885b53e-12f4-4864-9dc7-79327db63604', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('87259583-f650-48ba-a1e5-3f0f35b3dc34', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('3fa369e5-ab65-40e8-b758-1b5be10ee275', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('3c7209df-c617-46a5-8e73-4dc0ea100f9d', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('fe94237b-eaa1-4d2f-9d87-c354b787fdc8', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('6bbc77f9-270b-4649-91ea-111d9710aba6', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('ccd84ca5-08e4-4512-9561-baa6896e24c0', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('5535a3a2-7910-4175-95b3-5fc3e2080df7', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('97a3019d-5ab5-4339-9074-8a4e5c6f70f6', 'safety');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('a83198cb-b852-4dc9-a99b-c0756f825576', '68');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('a83198cb-b852-4dc9-a99b-c0756f825576', '30');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('a83198cb-b852-4dc9-a99b-c0756f825576', '32');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('7cd1eee3-8f47-426d-9182-30037c3bcdac', '121');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('6914a08d-0689-422c-a8f2-881ce9ea202c', '121');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('9382c928-b818-4f51-8585-8bd831711223', '69');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('9382c928-b818-4f51-8585-8bd831711223', '70');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('9382c928-b818-4f51-8585-8bd831711223', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e9fd1bc6-5804-4d54-b736-19deb3ce05c3', '69');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e9fd1bc6-5804-4d54-b736-19deb3ce05c3', '70');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e9fd1bc6-5804-4d54-b736-19deb3ce05c3', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c91192cf-8a2d-49b1-99ed-d21b709d645b', '77');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c91192cf-8a2d-49b1-99ed-d21b709d645b', '83');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('70320589-0b7a-4a04-b105-1da1dca0b121', '55');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c1c1dbb6-0372-4a28-a8aa-332361e0a3fd', '38');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('189571d9-b536-4a2b-82e3-c2029285422d', '11');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('7d09e3f5-01f8-454b-a327-34f5bb514d8b', '11');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('9e0f8f7a-9241-4a5d-8db8-d84cd82c340f', '11');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('96ccf8ea-3d58-4342-a25d-16026e655cfe', '11');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('78ad5a17-0fda-4650-8bab-3217c2e3b4e0', '11');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('75676d83-dede-4a0a-93fd-b93003cfaf3b', '11');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ad17f911-50f4-42db-bb6f-b8c0ec651425', '118');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ad17f911-50f4-42db-bb6f-b8c0ec651425', '119');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ad17f911-50f4-42db-bb6f-b8c0ec651425', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('74d3aed1-da57-43d4-bb75-2967f7398856', '27');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('74d3aed1-da57-43d4-bb75-2967f7398856', '37');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('23985b97-2795-4867-b7af-c0b7134fcc17', '118');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('23985b97-2795-4867-b7af-c0b7134fcc17', '119');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('23985b97-2795-4867-b7af-c0b7134fcc17', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('7fdd8709-73e6-4bfd-86fe-dd52a1d00aeb', '118');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('7fdd8709-73e6-4bfd-86fe-dd52a1d00aeb', '119');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('7fdd8709-73e6-4bfd-86fe-dd52a1d00aeb', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('a9ac2536-088a-45f5-94e6-06abc13eea71', '118');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('a9ac2536-088a-45f5-94e6-06abc13eea71', '119');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('a9ac2536-088a-45f5-94e6-06abc13eea71', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('9aec9ac1-71ca-442e-9678-3cdb42f16bed', '118');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('9aec9ac1-71ca-442e-9678-3cdb42f16bed', '119');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('9aec9ac1-71ca-442e-9678-3cdb42f16bed', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5ac7447f-bb24-4bbb-9cb1-014a28d94acc', '118');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5ac7447f-bb24-4bbb-9cb1-014a28d94acc', '119');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5ac7447f-bb24-4bbb-9cb1-014a28d94acc', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1d88fcff-44c5-40b5-aa7b-046dd048fa3a', '118');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1d88fcff-44c5-40b5-aa7b-046dd048fa3a', '119');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1d88fcff-44c5-40b5-aa7b-046dd048fa3a', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('53748c53-ea71-4c56-8dd3-328d7697268b', '97');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('7341d661-30f7-4425-a6e7-959404132ac7', '118');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('7341d661-30f7-4425-a6e7-959404132ac7', '119');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('7341d661-30f7-4425-a6e7-959404132ac7', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('29bb51e4-b8f0-4242-83d5-4408ce757bef', '118');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('29bb51e4-b8f0-4242-83d5-4408ce757bef', '119');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('29bb51e4-b8f0-4242-83d5-4408ce757bef', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4f0bcacd-4746-44c2-bc87-dc179f574d8c', '74');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e6060f11-00c7-4a81-bbe2-5292b34c949f', '74');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e6060f11-00c7-4a81-bbe2-5292b34c949f', '37');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b96f6b16-bf46-4d21-acdd-75d192b78676', '74');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b96f6b16-bf46-4d21-acdd-75d192b78676', '37');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1a89ad74-1734-4009-80c1-08404b973793', '94');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5de4eb69-b295-4697-90ca-37f5da1c793d', '87');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('16488f91-abeb-4303-9d1c-ed03e6eddb2b', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('7ef05cf8-0d7d-46e0-a6c4-a3e2dfba01ef', '69');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('94057d78-563d-4d24-870f-246c7a1ba6a0', '124');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('8ef1d4de-0c47-446c-a258-6c6df4e1dbe3', '124');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('8ef1d4de-0c47-446c-a258-6c6df4e1dbe3', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e7db20c0-3151-4ebd-8ec5-c1f553c4c136', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e7db20c0-3151-4ebd-8ec5-c1f553c4c136', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ea90a3cf-377f-485d-b1b3-6fcf3092fcec', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ea90a3cf-377f-485d-b1b3-6fcf3092fcec', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('7b29abcd-efec-453c-a407-cb281f7ffebc', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('7b29abcd-efec-453c-a407-cb281f7ffebc', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('678616af-c5bd-4e57-93b1-b937a8c3f6fc', '109');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('678616af-c5bd-4e57-93b1-b937a8c3f6fc', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('678616af-c5bd-4e57-93b1-b937a8c3f6fc', '26');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('678616af-c5bd-4e57-93b1-b937a8c3f6fc', '42');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('678616af-c5bd-4e57-93b1-b937a8c3f6fc', '117');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('678616af-c5bd-4e57-93b1-b937a8c3f6fc', '56');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('678616af-c5bd-4e57-93b1-b937a8c3f6fc', '57');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('678616af-c5bd-4e57-93b1-b937a8c3f6fc', '81');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('048200b5-ef95-4205-8479-80296d098523', '42');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('048200b5-ef95-4205-8479-80296d098523', '117');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('048200b5-ef95-4205-8479-80296d098523', '56');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('048200b5-ef95-4205-8479-80296d098523', '57');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('048200b5-ef95-4205-8479-80296d098523', '81');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('048200b5-ef95-4205-8479-80296d098523', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('fa080bcf-6efb-4091-ab4d-210ef361fc68', '42');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('fa080bcf-6efb-4091-ab4d-210ef361fc68', '117');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('fa080bcf-6efb-4091-ab4d-210ef361fc68', '56');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('fa080bcf-6efb-4091-ab4d-210ef361fc68', '57');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('fa080bcf-6efb-4091-ab4d-210ef361fc68', '81');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('fa080bcf-6efb-4091-ab4d-210ef361fc68', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('897d5f04-79b6-4f5a-94c1-091558714ae1', '42');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('897d5f04-79b6-4f5a-94c1-091558714ae1', '117');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('897d5f04-79b6-4f5a-94c1-091558714ae1', '56');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('897d5f04-79b6-4f5a-94c1-091558714ae1', '57');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('897d5f04-79b6-4f5a-94c1-091558714ae1', '81');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('897d5f04-79b6-4f5a-94c1-091558714ae1', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('67b27d8e-cd62-42a5-a065-bb4e4e1413a5', '42');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('67b27d8e-cd62-42a5-a065-bb4e4e1413a5', '117');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('67b27d8e-cd62-42a5-a065-bb4e4e1413a5', '56');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('67b27d8e-cd62-42a5-a065-bb4e4e1413a5', '57');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('67b27d8e-cd62-42a5-a065-bb4e4e1413a5', '81');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('67b27d8e-cd62-42a5-a065-bb4e4e1413a5', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('db53b3f2-6826-4852-96d5-feb54d63e451', '42');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('db53b3f2-6826-4852-96d5-feb54d63e451', '117');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('db53b3f2-6826-4852-96d5-feb54d63e451', '56');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('db53b3f2-6826-4852-96d5-feb54d63e451', '57');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('db53b3f2-6826-4852-96d5-feb54d63e451', '81');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('db53b3f2-6826-4852-96d5-feb54d63e451', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b15356d2-75c2-4f3d-a40b-807e9638deb6', '42');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b15356d2-75c2-4f3d-a40b-807e9638deb6', '117');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b15356d2-75c2-4f3d-a40b-807e9638deb6', '56');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b15356d2-75c2-4f3d-a40b-807e9638deb6', '57');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b15356d2-75c2-4f3d-a40b-807e9638deb6', '81');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b15356d2-75c2-4f3d-a40b-807e9638deb6', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b2d20c54-7fe2-4f35-b1f9-a2ac22ac9082', '42');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b2d20c54-7fe2-4f35-b1f9-a2ac22ac9082', '117');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b2d20c54-7fe2-4f35-b1f9-a2ac22ac9082', '56');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b2d20c54-7fe2-4f35-b1f9-a2ac22ac9082', '57');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b2d20c54-7fe2-4f35-b1f9-a2ac22ac9082', '81');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b2d20c54-7fe2-4f35-b1f9-a2ac22ac9082', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('11eee3ed-6ec6-45f8-877b-f08dcd80b0aa', '97');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('11eee3ed-6ec6-45f8-877b-f08dcd80b0aa', '118');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('11eee3ed-6ec6-45f8-877b-f08dcd80b0aa', '119');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('11eee3ed-6ec6-45f8-877b-f08dcd80b0aa', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d11ce3c8-73c3-400e-b147-5ac290ebfa71', '15');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d11ce3c8-73c3-400e-b147-5ac290ebfa71', '45');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d11ce3c8-73c3-400e-b147-5ac290ebfa71', '92');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d11ce3c8-73c3-400e-b147-5ac290ebfa71', '95');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('84a1d4fe-3d43-43dd-885d-061536e9927e', '15');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('84a1d4fe-3d43-43dd-885d-061536e9927e', '45');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('84a1d4fe-3d43-43dd-885d-061536e9927e', '92');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('84a1d4fe-3d43-43dd-885d-061536e9927e', '95');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4e4b0628-dfea-4b7e-ac5c-fd31fc109cf6', '15');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4e4b0628-dfea-4b7e-ac5c-fd31fc109cf6', '45');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4e4b0628-dfea-4b7e-ac5c-fd31fc109cf6', '92');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4e4b0628-dfea-4b7e-ac5c-fd31fc109cf6', '95');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c7da492a-c477-4a55-a783-383a80ad62d9', '15');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c7da492a-c477-4a55-a783-383a80ad62d9', '45');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c7da492a-c477-4a55-a783-383a80ad62d9', '92');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c7da492a-c477-4a55-a783-383a80ad62d9', '95');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b6d9ba86-e7a2-4cb9-8a82-fadfdac75bad', '15');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b6d9ba86-e7a2-4cb9-8a82-fadfdac75bad', '45');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b6d9ba86-e7a2-4cb9-8a82-fadfdac75bad', '92');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b6d9ba86-e7a2-4cb9-8a82-fadfdac75bad', '95');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ecc005d5-cf4b-496b-b423-47ef1c4fbf2b', '15');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ecc005d5-cf4b-496b-b423-47ef1c4fbf2b', '45');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ecc005d5-cf4b-496b-b423-47ef1c4fbf2b', '92');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ecc005d5-cf4b-496b-b423-47ef1c4fbf2b', '95');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('795dba0c-e2d1-40e1-9af4-2e7b99672e84', '15');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('795dba0c-e2d1-40e1-9af4-2e7b99672e84', '45');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('795dba0c-e2d1-40e1-9af4-2e7b99672e84', '92');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('795dba0c-e2d1-40e1-9af4-2e7b99672e84', '95');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c14f161a-8a1f-4056-afec-0bc3c56243bc', '15');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c14f161a-8a1f-4056-afec-0bc3c56243bc', '45');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c14f161a-8a1f-4056-afec-0bc3c56243bc', '92');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c14f161a-8a1f-4056-afec-0bc3c56243bc', '95');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('3a5cf37d-6ec7-4e59-a4b4-12c02aebbca3', '21');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b0a9ff2d-5211-41b6-aaaf-6e98bd2b38fe', '21');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('05903885-b7a0-4c9a-8085-691f3a5f9561', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('05903885-b7a0-4c9a-8085-691f3a5f9561', '94');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('25309b12-bac6-42f7-b8cd-f18785eb3f08', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('25309b12-bac6-42f7-b8cd-f18785eb3f08', '94');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('36885715-b4fe-4879-a98f-5910155dd487', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('36885715-b4fe-4879-a98f-5910155dd487', '94');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f3829d4f-3c1b-45a2-bbd1-f48b02b6bc6d', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f3829d4f-3c1b-45a2-bbd1-f48b02b6bc6d', '94');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c2a0e506-c528-44a5-8e08-f7537af796cc', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c2a0e506-c528-44a5-8e08-f7537af796cc', '94');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('7c0a3a24-c4f5-432d-a53b-b643112f6512', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('7c0a3a24-c4f5-432d-a53b-b643112f6512', '94');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f2bba305-c147-4f92-b71c-964a3d0e9634', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f2bba305-c147-4f92-b71c-964a3d0e9634', '94');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b4fd81de-1867-4416-88c2-f67fc4bdcd33', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b4fd81de-1867-4416-88c2-f67fc4bdcd33', '94');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1cffb8e2-a467-4472-80b3-c44aa58b3c32', '96');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1cffb8e2-a467-4472-80b3-c44aa58b3c32', '94');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1cffb8e2-a467-4472-80b3-c44aa58b3c32', '28');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1cffb8e2-a467-4472-80b3-c44aa58b3c32', '55');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('de5b78fe-e2e5-423e-9d93-72aadbddf89d', '9');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('de5b78fe-e2e5-423e-9d93-72aadbddf89d', '37');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('394638cf-7baa-4073-aec3-48d1a18a705c', '9');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('394638cf-7baa-4073-aec3-48d1a18a705c', '37');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('bc3de762-ca52-4110-bd3e-a4a7ce279df1', '9');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('bc3de762-ca52-4110-bd3e-a4a7ce279df1', '37');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('6f82c779-7ddc-483c-9947-9108acde05f7', '9');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('6f82c779-7ddc-483c-9947-9108acde05f7', '37');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c283c9f4-40fe-426c-b56b-e36aca559cd7', '9');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c283c9f4-40fe-426c-b56b-e36aca559cd7', '37');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('affb6d40-1958-47b6-bfe3-a717790c17ce', '9');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('affb6d40-1958-47b6-bfe3-a717790c17ce', '37');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('affb6d40-1958-47b6-bfe3-a717790c17ce', '48');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c986fdc5-aae9-4b40-a4d6-eda41636c45f', '9');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c986fdc5-aae9-4b40-a4d6-eda41636c45f', '37');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c986fdc5-aae9-4b40-a4d6-eda41636c45f', '48');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c986fdc5-aae9-4b40-a4d6-eda41636c45f', '70');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('52639d74-170a-47bb-aeec-327c753ac2b2', '9');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('52639d74-170a-47bb-aeec-327c753ac2b2', '37');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('52639d74-170a-47bb-aeec-327c753ac2b2', '48');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('52639d74-170a-47bb-aeec-327c753ac2b2', '70');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d6958875-9974-4528-8f9a-9af071f46314', '9');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d6958875-9974-4528-8f9a-9af071f46314', '37');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d6958875-9974-4528-8f9a-9af071f46314', '48');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d6958875-9974-4528-8f9a-9af071f46314', '70');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0a0c6b87-94f9-4323-84a7-b250d870dbdf', '3');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0a0c6b87-94f9-4323-84a7-b250d870dbdf', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4b0dbf3a-fb62-4746-a75b-c0a43ac2bcc0', '3');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4b0dbf3a-fb62-4746-a75b-c0a43ac2bcc0', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('12d1f211-4c23-4457-9ca9-55f7c272b2ec', '3');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('12d1f211-4c23-4457-9ca9-55f7c272b2ec', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c2ed97ee-8d3c-4101-b396-211e0d9cca46', '3');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c2ed97ee-8d3c-4101-b396-211e0d9cca46', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0c701e53-d5be-44ae-b89e-1cbc478f1e1c', '3');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0c701e53-d5be-44ae-b89e-1cbc478f1e1c', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('20677c87-5699-4432-ad50-82007acf0dc7', '3');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('20677c87-5699-4432-ad50-82007acf0dc7', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('6f0f6236-51e1-458a-8ca3-163b70eb284b', '3');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('6f0f6236-51e1-458a-8ca3-163b70eb284b', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0b9cbfc6-06d9-4a9c-898b-e2dccc810ad5', '3');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0b9cbfc6-06d9-4a9c-898b-e2dccc810ad5', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0e1cd76e-2d63-499b-9e6d-a85eb00ed01a', '3');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0e1cd76e-2d63-499b-9e6d-a85eb00ed01a', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d77e0bc8-6161-4a7a-a6f4-d4ce788e34df', '3');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d77e0bc8-6161-4a7a-a6f4-d4ce788e34df', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2bb33de6-89e2-4ab3-805a-3cc2e7cb42ed', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e51aa0cd-7617-436f-b429-7141e123e148', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('074a839d-a6df-4a96-adab-94c4e2869201', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('120344f8-4544-46c5-8de9-2018416a1757', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('8db50d8c-64e4-4506-a143-3a0a329492c9', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('52e44c5e-2702-48b8-a3a6-e221048de715', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('7fba3abd-2564-46b9-8cad-935f9f0a8683', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('05a359f2-af4e-49ca-9e48-09510dc2252a', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b1a92471-5517-48cc-8c9d-0edcb55de693', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('bbbc54c2-a917-455b-9782-de74186160cb', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('bbbc54c2-a917-455b-9782-de74186160cb', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b81b5de3-d794-4b74-bf20-36d0f9c0d74d', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b81b5de3-d794-4b74-bf20-36d0f9c0d74d', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b632c31c-91b5-42a5-bc4b-bf0a2acfbf02', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b632c31c-91b5-42a5-bc4b-bf0a2acfbf02', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('8928f014-1d7c-4793-8c28-bd34b780b3c5', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('8928f014-1d7c-4793-8c28-bd34b780b3c5', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('bd5566ee-6e33-4522-9ac9-18a6ba2a8a55', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('bd5566ee-6e33-4522-9ac9-18a6ba2a8a55', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('bd5566ee-6e33-4522-9ac9-18a6ba2a8a55', '125');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('bd5566ee-6e33-4522-9ac9-18a6ba2a8a55', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ac632efc-bacc-4229-acd2-216684e36e41', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ac632efc-bacc-4229-acd2-216684e36e41', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ac632efc-bacc-4229-acd2-216684e36e41', '125');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ac632efc-bacc-4229-acd2-216684e36e41', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('8ead7b4a-5198-4feb-966b-232c0197d7bf', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('8ead7b4a-5198-4feb-966b-232c0197d7bf', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('8ead7b4a-5198-4feb-966b-232c0197d7bf', '125');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('8ead7b4a-5198-4feb-966b-232c0197d7bf', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('000348ec-9f32-4894-bff8-9e39abda83be', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('000348ec-9f32-4894-bff8-9e39abda83be', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('000348ec-9f32-4894-bff8-9e39abda83be', '125');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('000348ec-9f32-4894-bff8-9e39abda83be', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ac04175b-feaf-4128-8cf4-b764d96c5b7b', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ac04175b-feaf-4128-8cf4-b764d96c5b7b', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ac04175b-feaf-4128-8cf4-b764d96c5b7b', '125');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ac04175b-feaf-4128-8cf4-b764d96c5b7b', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('305e28d3-f68e-44b7-925b-6a31b92abf8a', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('305e28d3-f68e-44b7-925b-6a31b92abf8a', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('305e28d3-f68e-44b7-925b-6a31b92abf8a', '125');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('305e28d3-f68e-44b7-925b-6a31b92abf8a', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('05130e8d-e8f6-4446-b8d1-4e1e0a8fe813', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('05130e8d-e8f6-4446-b8d1-4e1e0a8fe813', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('05130e8d-e8f6-4446-b8d1-4e1e0a8fe813', '125');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('05130e8d-e8f6-4446-b8d1-4e1e0a8fe813', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5e4678f5-378a-44b1-849e-319f3bf84faf', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5e4678f5-378a-44b1-849e-319f3bf84faf', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5e4678f5-378a-44b1-849e-319f3bf84faf', '125');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5e4678f5-378a-44b1-849e-319f3bf84faf', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2e2816e7-85db-4903-8aaf-7708f6de4b15', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2e2816e7-85db-4903-8aaf-7708f6de4b15', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2e2816e7-85db-4903-8aaf-7708f6de4b15', '125');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2e2816e7-85db-4903-8aaf-7708f6de4b15', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2e2816e7-85db-4903-8aaf-7708f6de4b15', '97');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d18ae6a7-a060-4ee7-a59d-3575447a1ea2', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d18ae6a7-a060-4ee7-a59d-3575447a1ea2', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d18ae6a7-a060-4ee7-a59d-3575447a1ea2', '125');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d18ae6a7-a060-4ee7-a59d-3575447a1ea2', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('3f8e0d58-f003-4b4a-aea4-05f40a796c6f', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('3f8e0d58-f003-4b4a-aea4-05f40a796c6f', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('3f8e0d58-f003-4b4a-aea4-05f40a796c6f', '125');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('3f8e0d58-f003-4b4a-aea4-05f40a796c6f', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2bbd04a6-40af-4d74-a32b-4c31f52c7504', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2bbd04a6-40af-4d74-a32b-4c31f52c7504', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2bbd04a6-40af-4d74-a32b-4c31f52c7504', '125');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2bbd04a6-40af-4d74-a32b-4c31f52c7504', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c16fe4e1-3589-4f74-b3e5-64504e481d3e', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c16fe4e1-3589-4f74-b3e5-64504e481d3e', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c16fe4e1-3589-4f74-b3e5-64504e481d3e', '125');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c16fe4e1-3589-4f74-b3e5-64504e481d3e', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e14667da-cdef-4afe-9113-4b3d21a61f11', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e14667da-cdef-4afe-9113-4b3d21a61f11', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e14667da-cdef-4afe-9113-4b3d21a61f11', '125');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e14667da-cdef-4afe-9113-4b3d21a61f11', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e71b1798-e390-4e05-bd39-47ccde2244c5', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e71b1798-e390-4e05-bd39-47ccde2244c5', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e71b1798-e390-4e05-bd39-47ccde2244c5', '125');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e71b1798-e390-4e05-bd39-47ccde2244c5', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4f622382-8519-4990-af03-ffa0ed9cc446', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4f622382-8519-4990-af03-ffa0ed9cc446', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4f622382-8519-4990-af03-ffa0ed9cc446', '125');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4f622382-8519-4990-af03-ffa0ed9cc446', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2d70fd48-f88f-45ab-878e-f9e06499b741', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2d70fd48-f88f-45ab-878e-f9e06499b741', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2d70fd48-f88f-45ab-878e-f9e06499b741', '125');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2d70fd48-f88f-45ab-878e-f9e06499b741', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2ad08cd2-3e23-4fde-8ffd-27174e56e579', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2ad08cd2-3e23-4fde-8ffd-27174e56e579', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2ad08cd2-3e23-4fde-8ffd-27174e56e579', '125');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2ad08cd2-3e23-4fde-8ffd-27174e56e579', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e389ad5a-660d-4f69-95cd-98dc1423be48', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e389ad5a-660d-4f69-95cd-98dc1423be48', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e389ad5a-660d-4f69-95cd-98dc1423be48', '125');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e389ad5a-660d-4f69-95cd-98dc1423be48', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('cf92e9e1-5d68-4bb2-85b6-ed807fdfd50f', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('cf92e9e1-5d68-4bb2-85b6-ed807fdfd50f', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('cf92e9e1-5d68-4bb2-85b6-ed807fdfd50f', '125');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('cf92e9e1-5d68-4bb2-85b6-ed807fdfd50f', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('90071ae9-ce0a-41a5-b743-9aafc6e65c08', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('90071ae9-ce0a-41a5-b743-9aafc6e65c08', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('90071ae9-ce0a-41a5-b743-9aafc6e65c08', '125');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('90071ae9-ce0a-41a5-b743-9aafc6e65c08', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('a95f4318-d629-4a04-8f42-2056d63f09cb', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('a95f4318-d629-4a04-8f42-2056d63f09cb', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('a95f4318-d629-4a04-8f42-2056d63f09cb', '125');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('a95f4318-d629-4a04-8f42-2056d63f09cb', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('616b9333-e7b5-46e5-8121-eb43314e8c71', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('616b9333-e7b5-46e5-8121-eb43314e8c71', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('616b9333-e7b5-46e5-8121-eb43314e8c71', '125');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('616b9333-e7b5-46e5-8121-eb43314e8c71', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('9845da30-1314-4e12-b287-497d24c61579', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('9845da30-1314-4e12-b287-497d24c61579', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('9845da30-1314-4e12-b287-497d24c61579', '125');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('9845da30-1314-4e12-b287-497d24c61579', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('6d47ed7f-6c69-4f21-99ec-d046a0c3037c', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('6d47ed7f-6c69-4f21-99ec-d046a0c3037c', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('6d47ed7f-6c69-4f21-99ec-d046a0c3037c', '125');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('6d47ed7f-6c69-4f21-99ec-d046a0c3037c', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('822fee45-32ac-46bb-81be-c06243e76a30', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('822fee45-32ac-46bb-81be-c06243e76a30', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('822fee45-32ac-46bb-81be-c06243e76a30', '125');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('822fee45-32ac-46bb-81be-c06243e76a30', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('aa79b399-bfea-4607-ba4b-5a3f438d3c6e', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('aa79b399-bfea-4607-ba4b-5a3f438d3c6e', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('aa79b399-bfea-4607-ba4b-5a3f438d3c6e', '125');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('aa79b399-bfea-4607-ba4b-5a3f438d3c6e', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('50e66a3d-13c1-403c-a7e1-3b157cda6298', '115');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('50e66a3d-13c1-403c-a7e1-3b157cda6298', '120');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('6bbc77f9-270b-4649-91ea-111d9710aba6', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('ccd84ca5-08e4-4512-9561-baa6896e24c0', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5535a3a2-7910-4175-95b3-5fc3e2080df7', '9');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('97a3019d-5ab5-4339-9074-8a4e5c6f70f6', '9');

SELECT id INTO @groupId7 from rule_group where name = '7.0 AWS 所有非 CIS 特定检查';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a83198cb-b852-4dc9-a99b-c0756f825576', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7cd1eee3-8f47-426d-9182-30037c3bcdac', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('6914a08d-0689-422c-a8f2-881ce9ea202c', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('9382c928-b818-4f51-8585-8bd831711223', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e9fd1bc6-5804-4d54-b736-19deb3ce05c3', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c91192cf-8a2d-49b1-99ed-d21b709d645b', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('70320589-0b7a-4a04-b105-1da1dca0b121', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c1c1dbb6-0372-4a28-a8aa-332361e0a3fd', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('189571d9-b536-4a2b-82e3-c2029285422d', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7d09e3f5-01f8-454b-a327-34f5bb514d8b', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('9e0f8f7a-9241-4a5d-8db8-d84cd82c340f', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('96ccf8ea-3d58-4342-a25d-16026e655cfe', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('78ad5a17-0fda-4650-8bab-3217c2e3b4e0', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('75676d83-dede-4a0a-93fd-b93003cfaf3b', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ad17f911-50f4-42db-bb6f-b8c0ec651425', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('74d3aed1-da57-43d4-bb75-2967f7398856', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('23985b97-2795-4867-b7af-c0b7134fcc17', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7fdd8709-73e6-4bfd-86fe-dd52a1d00aeb', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a9ac2536-088a-45f5-94e6-06abc13eea71', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('9aec9ac1-71ca-442e-9678-3cdb42f16bed', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5ac7447f-bb24-4bbb-9cb1-014a28d94acc', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1d88fcff-44c5-40b5-aa7b-046dd048fa3a', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('53748c53-ea71-4c56-8dd3-328d7697268b', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7341d661-30f7-4425-a6e7-959404132ac7', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('29bb51e4-b8f0-4242-83d5-4408ce757bef', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('4f0bcacd-4746-44c2-bc87-dc179f574d8c', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e6060f11-00c7-4a81-bbe2-5292b34c949f', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b96f6b16-bf46-4d21-acdd-75d192b78676', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1a89ad74-1734-4009-80c1-08404b973793', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5de4eb69-b295-4697-90ca-37f5da1c793d', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('16488f91-abeb-4303-9d1c-ed03e6eddb2b', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7ef05cf8-0d7d-46e0-a6c4-a3e2dfba01ef', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('94057d78-563d-4d24-870f-246c7a1ba6a0', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8ef1d4de-0c47-446c-a258-6c6df4e1dbe3', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e7db20c0-3151-4ebd-8ec5-c1f553c4c136', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ea90a3cf-377f-485d-b1b3-6fcf3092fcec', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7b29abcd-efec-453c-a407-cb281f7ffebc', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('678616af-c5bd-4e57-93b1-b937a8c3f6fc', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('048200b5-ef95-4205-8479-80296d098523', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('fa080bcf-6efb-4091-ab4d-210ef361fc68', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('897d5f04-79b6-4f5a-94c1-091558714ae1', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('67b27d8e-cd62-42a5-a065-bb4e4e1413a5', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('db53b3f2-6826-4852-96d5-feb54d63e451', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b15356d2-75c2-4f3d-a40b-807e9638deb6', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b2d20c54-7fe2-4f35-b1f9-a2ac22ac9082', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('11eee3ed-6ec6-45f8-877b-f08dcd80b0aa', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d11ce3c8-73c3-400e-b147-5ac290ebfa71', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('84a1d4fe-3d43-43dd-885d-061536e9927e', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('4e4b0628-dfea-4b7e-ac5c-fd31fc109cf6', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c7da492a-c477-4a55-a783-383a80ad62d9', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b6d9ba86-e7a2-4cb9-8a82-fadfdac75bad', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ecc005d5-cf4b-496b-b423-47ef1c4fbf2b', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('795dba0c-e2d1-40e1-9af4-2e7b99672e84', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c14f161a-8a1f-4056-afec-0bc3c56243bc', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('3a5cf37d-6ec7-4e59-a4b4-12c02aebbca3', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b0a9ff2d-5211-41b6-aaaf-6e98bd2b38fe', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('05903885-b7a0-4c9a-8085-691f3a5f9561', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('25309b12-bac6-42f7-b8cd-f18785eb3f08', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('36885715-b4fe-4879-a98f-5910155dd487', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f3829d4f-3c1b-45a2-bbd1-f48b02b6bc6d', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c2a0e506-c528-44a5-8e08-f7537af796cc', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7c0a3a24-c4f5-432d-a53b-b643112f6512', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f2bba305-c147-4f92-b71c-964a3d0e9634', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b4fd81de-1867-4416-88c2-f67fc4bdcd33', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1cffb8e2-a467-4472-80b3-c44aa58b3c32', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('de5b78fe-e2e5-423e-9d93-72aadbddf89d', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('394638cf-7baa-4073-aec3-48d1a18a705c', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bc3de762-ca52-4110-bd3e-a4a7ce279df1', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('6f82c779-7ddc-483c-9947-9108acde05f7', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c283c9f4-40fe-426c-b56b-e36aca559cd7', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('affb6d40-1958-47b6-bfe3-a717790c17ce', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c986fdc5-aae9-4b40-a4d6-eda41636c45f', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('52639d74-170a-47bb-aeec-327c753ac2b2', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d6958875-9974-4528-8f9a-9af071f46314', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0a0c6b87-94f9-4323-84a7-b250d870dbdf', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('4b0dbf3a-fb62-4746-a75b-c0a43ac2bcc0', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('12d1f211-4c23-4457-9ca9-55f7c272b2ec', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c2ed97ee-8d3c-4101-b396-211e0d9cca46', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0c701e53-d5be-44ae-b89e-1cbc478f1e1c', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('20677c87-5699-4432-ad50-82007acf0dc7', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('6f0f6236-51e1-458a-8ca3-163b70eb284b', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0b9cbfc6-06d9-4a9c-898b-e2dccc810ad5', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0e1cd76e-2d63-499b-9e6d-a85eb00ed01a', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d77e0bc8-6161-4a7a-a6f4-d4ce788e34df', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2bb33de6-89e2-4ab3-805a-3cc2e7cb42ed', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e51aa0cd-7617-436f-b429-7141e123e148', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('074a839d-a6df-4a96-adab-94c4e2869201', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('120344f8-4544-46c5-8de9-2018416a1757', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8db50d8c-64e4-4506-a143-3a0a329492c9', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('52e44c5e-2702-48b8-a3a6-e221048de715', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7fba3abd-2564-46b9-8cad-935f9f0a8683', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('05a359f2-af4e-49ca-9e48-09510dc2252a', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b1a92471-5517-48cc-8c9d-0edcb55de693', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bbbc54c2-a917-455b-9782-de74186160cb', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b81b5de3-d794-4b74-bf20-36d0f9c0d74d', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b632c31c-91b5-42a5-bc4b-bf0a2acfbf02', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8928f014-1d7c-4793-8c28-bd34b780b3c5', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bd5566ee-6e33-4522-9ac9-18a6ba2a8a55', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ac632efc-bacc-4229-acd2-216684e36e41', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8ead7b4a-5198-4feb-966b-232c0197d7bf', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('000348ec-9f32-4894-bff8-9e39abda83be', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ac04175b-feaf-4128-8cf4-b764d96c5b7b', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('305e28d3-f68e-44b7-925b-6a31b92abf8a', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('05130e8d-e8f6-4446-b8d1-4e1e0a8fe813', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5e4678f5-378a-44b1-849e-319f3bf84faf', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2e2816e7-85db-4903-8aaf-7708f6de4b15', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d18ae6a7-a060-4ee7-a59d-3575447a1ea2', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('3f8e0d58-f003-4b4a-aea4-05f40a796c6f', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2bbd04a6-40af-4d74-a32b-4c31f52c7504', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c16fe4e1-3589-4f74-b3e5-64504e481d3e', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e14667da-cdef-4afe-9113-4b3d21a61f11', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e71b1798-e390-4e05-bd39-47ccde2244c5', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('4f622382-8519-4990-af03-ffa0ed9cc446', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2d70fd48-f88f-45ab-878e-f9e06499b741', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2ad08cd2-3e23-4fde-8ffd-27174e56e579', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e389ad5a-660d-4f69-95cd-98dc1423be48', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('cf92e9e1-5d68-4bb2-85b6-ed807fdfd50f', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('90071ae9-ce0a-41a5-b743-9aafc6e65c08', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a95f4318-d629-4a04-8f42-2056d63f09cb', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('616b9333-e7b5-46e5-8121-eb43314e8c71', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('9845da30-1314-4e12-b287-497d24c61579', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('6d47ed7f-6c69-4f21-99ec-d046a0c3037c', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('822fee45-32ac-46bb-81be-c06243e76a30', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('aa79b399-bfea-4607-ba4b-5a3f438d3c6e', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('50e66a3d-13c1-403c-a7e1-3b157cda6298', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7c0e6e4a-f6c4-4d11-bfdd-c7231f13a322', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5f8adceb-99aa-49ac-9ed4-d86d8bd67a0d', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('39d29fe9-74ab-450a-af0f-ae7c12896673', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a89f5a2e-eafb-4511-a71e-599a72143408', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5e5cc249-2b20-4c80-a278-a913e18a2b21', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bd5f84b8-9439-40f4-8ccd-2b544f656a91', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('98e815f5-8b07-4717-a15c-389b34e4ebfc', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8afa254e-0446-4e55-9a32-eb83bf3049ab', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('40d375c3-a1b6-4785-9325-c5c4c59bcacf', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('88c3427c-c668-49ee-987f-7b7da5b4de11', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2803b3c5-2a11-4f3d-8bd7-acaa18bd35cf', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('55e5207c-9d0e-48a5-ba68-c01ead665443', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('23aaf8a8-820b-4b2d-a2a9-e27151fdcc12', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2198c834-ac57-4bd4-be8a-b1fa8834fc76', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0eda2992-8173-4c3a-bd1c-e2e48e0aad1e', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('111fabaa-249b-4a73-b9fd-19166c9073b7', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e5b42411-47db-4358-b156-1d7a56b08b10', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bf3990ee-324f-4618-8f2f-190df72aaf11', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8b035cd-6d0c-43b6-9277-e6be6fd8f8da', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('9a9eeba2-8a1c-4e3f-b486-5357a7546e08', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('10751001-d9aa-4582-a96a-471813e50512', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('65dfbac5-4ee3-4405-a5d0-98e3da849831', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('9b174f28-9ed1-4e93-9be1-adde6619d8c3', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e3d8f69b-a3ef-47d7-b715-235fb803e9e6', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('6ca702b2-13b2-4dfb-9a8e-2aedbfbd5252', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('9d0ca4db-2cf0-45d7-a14d-8dcd18791191', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f4190017-0459-4f7e-943d-5f299771da5b', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('53569e08-86c6-43ba-99f5-64d529477bc0', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('359a94bf-6423-4db2-96b0-86fd6de9bb25', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('41e7a337-339b-4609-8904-e362196aa9c0', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('9ddfd1d2-810e-417a-ba5f-925365b2f9f8', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('abe0b522-dabf-456f-bac6-d0d48cae4eed', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8bd18f86-7b0d-4927-860d-83e295cc6b05', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('520959f9-f224-41fa-9a52-0df38e55eb8f', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8a3cd7a7-3aea-4e57-b646-f97d9952614a', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f0cfc258-0001-4423-9485-b96521535c50', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('75d0d479-4c86-418a-b805-e4d32d7b9583', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c8926d60-6726-407d-ba1d-839effafae3c', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('3d27d878-d48e-4784-bff5-b230d2d28846', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('87786f5c-2f7d-4836-849d-900d587c38db', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('dc28da88-7551-453d-a891-5c9c23781bc4', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bebe49c3-8e87-4c21-a2b3-7f5f6cc90136', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('095ac637-4e0d-46ef-b658-dc2abbe0ed7e', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d1fbc80a-78c1-4214-bf99-13ec76e78b58', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2da5e51d-889a-4dbf-a2f8-11259b8eaa56', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7b3df26a-1839-430d-b8b1-bec22849a62b', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a0964936-4dde-4fdf-b512-bb6a0fae8146', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a3c12f99-237c-4dfd-b4e5-8d6d30045812', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2d4c6320-bcd3-4599-9564-87e4e6f82eeb', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a14ec927-6f85-47fa-89dc-4d5fbb0534eb', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('4420e7c7-578d-44e2-8a8a-d0e5691d76c2', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('14f34d63-8a93-4f70-ae25-69d29b71d573', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('90ec24a4-79de-4911-bea9-ca795a7e6eec', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('109b39e5-6878-43cb-93ab-f19a22f2ecdd', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('62d16913-61e4-46fc-abd7-4174c1ab89cc', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('978d14c5-da23-4cf8-9cd9-c0e710977250', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8f1cd40a-bcff-4089-97e8-45337c4e8cb1', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2885b53e-12f4-4864-9dc7-79327db63604', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('87259583-f650-48ba-a1e5-3f0f35b3dc34', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('3fa369e5-ab65-40e8-b758-1b5be10ee275', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('3c7209df-c617-46a5-8e73-4dc0ea100f9d', @groupId7);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('fe94237b-eaa1-4d2f-9d87-c354b787fdc8', @groupId7);

SELECT id INTO @groupId8 from rule_group where name = '8.0 AWS Forensics 安全检查';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8883c71f-7d42-4007-9152-1138c3fb7ab3', @groupId8);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e554b261-0e89-42eb-a44c-4a6d00a32f85', @groupId8);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('96b70632-7ccb-4fe9-af1f-58a8be26eeaf', @groupId8);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a2df85e7-0f0a-4283-84f2-81ee336736e2', @groupId8);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8fd6c3d-5faf-4394-897d-7fd84d33550d', @groupId8);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('79d8e033-ddca-49e6-a658-76b5c0e90e09', @groupId8);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1ca7f983-69ea-47ed-9f9a-5a6e7317d8bd', @groupId8);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('79790c74-501c-4830-bba6-ca635c0885f5', @groupId8);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bbbc54c2-a917-455b-9782-de74186160cb', @groupId8);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('96ccf8ea-3d58-4342-a25d-16026e655cfe', @groupId8);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('78ad5a17-0fda-4650-8bab-3217c2e3b4e0', @groupId8);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('75676d83-dede-4a0a-93fd-b93003cfaf3b', @groupId8);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ad17f911-50f4-42db-bb6f-b8c0ec651425', @groupId8);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('23985b97-2795-4867-b7af-c0b7134fcc17', @groupId8);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7fdd8709-73e6-4bfd-86fe-dd52a1d00aeb', @groupId8);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a9ac2536-088a-45f5-94e6-06abc13eea71', @groupId8);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('9aec9ac1-71ca-442e-9678-3cdb42f16bed', @groupId8);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5ac7447f-bb24-4bbb-9cb1-014a28d94acc', @groupId8);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1d88fcff-44c5-40b5-aa7b-046dd048fa3a', @groupId8);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('29bb51e4-b8f0-4242-83d5-4408ce757bef', @groupId8);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('074a839d-a6df-4a96-adab-94c4e2869201', @groupId8);

SELECT id INTO @groupId9 from rule_group where name = '9.0 AWS GDPR 安全检查';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b8844aed-9da9-469b-8ec8-a69fb1a2f7f2', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bb3eed24-02b1-43d1-9057-f4f800e4660e', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('3bd0f4f5-eb7f-48d5-b3db-1fd8cee80e07', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('22dd87f6-353c-42a7-a253-3957b17c1bb4', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('77047e0e-88da-422e-93db-2d3a0606e961', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e3fdf46f-782c-439f-b09f-2a616d42697d', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b9f4c9f9-0fb6-47d6-af8a-57e9b8902245', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f8579451-2c24-4743-87e3-0fcf7c465d08', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('eac1ba91-eb46-4c44-b1f4-e709ea9d0e1e', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f2e017f1-70cb-4ad1-a187-8895c8040945', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c66cbddd-5846-4fb9-bedb-8a6812c51c5e', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('705c1cef-b47b-4e0b-ab42-d1b360e6502e', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8a2d0ca-4f5b-4809-8321-5ef485b2bf21', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('03979d46-6325-4b20-8d77-0e904a986064', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('67a2b3ad-6974-442c-9f03-32f0a8cd4f4d', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bd18fd9b-06af-4c98-8198-26b3aa3b78bc', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8883c71f-7d42-4007-9152-1138c3fb7ab3', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e554b261-0e89-42eb-a44c-4a6d00a32f85', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('96b70632-7ccb-4fe9-af1f-58a8be26eeaf', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a2df85e7-0f0a-4283-84f2-81ee336736e2', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8fd6c3d-5faf-4394-897d-7fd84d33550d', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('79d8e033-ddca-49e6-a658-76b5c0e90e09', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1ca7f983-69ea-47ed-9f9a-5a6e7317d8bd', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f7f19d65-3561-463b-b3ca-218239268d66', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f79aada7-27fb-47c6-9827-07fa035198bc', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5578d38b-9966-498d-8c5d-f1615a817f87', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c4605126-0a03-420e-a9af-a808c9031138', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('67a51dbc-16a2-4c35-a494-06f60cb7ca7e', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2d040c4f-0486-413f-8738-5bc1a2a3e842', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('14983a14-0f6b-45d8-827b-105368869ae9', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1d1974d5-3e7c-4891-ab99-e817292b73ed', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a8bb65f2-8715-4f86-97ad-0655f7271d9a', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ed2a0376-2f23-4ca6-9746-b909db67a2ab', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e82e163e-4428-49d8-8986-1d06906c165c', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('eb0f3859-8b0d-41e1-b85f-f829ceb99801', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('edb2c0ee-f36a-4864-b66d-bee3cd9730b0', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ef4311ae-7fd1-4866-b9f3-3e5b89975c44', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8bf919b-cf2c-4930-872b-3949143040d6', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('acba4a64-2bcc-4bbf-bb97-7983cc4c929b', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('9e0f8f7a-9241-4a5d-8db8-d84cd82c340f', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('75676d83-dede-4a0a-93fd-b93003cfaf3b', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ad17f911-50f4-42db-bb6f-b8c0ec651425', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('23985b97-2795-4867-b7af-c0b7134fcc17', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7fdd8709-73e6-4bfd-86fe-dd52a1d00aeb', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a9ac2536-088a-45f5-94e6-06abc13eea71', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('9aec9ac1-71ca-442e-9678-3cdb42f16bed', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5ac7447f-bb24-4bbb-9cb1-014a28d94acc', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1d88fcff-44c5-40b5-aa7b-046dd048fa3a', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('29bb51e4-b8f0-4242-83d5-4408ce757bef', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('4f0bcacd-4746-44c2-bc87-dc179f574d8c', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e6060f11-00c7-4a81-bbe2-5292b34c949f', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1a89ad74-1734-4009-80c1-08404b973793', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5de4eb69-b295-4697-90ca-37f5da1c793d', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('16488f91-abeb-4303-9d1c-ed03e6eddb2b', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7ef05cf8-0d7d-46e0-a6c4-a3e2dfba01ef', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('94057d78-563d-4d24-870f-246c7a1ba6a0', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8ef1d4de-0c47-446c-a258-6c6df4e1dbe3', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e7db20c0-3151-4ebd-8ec5-c1f553c4c136', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ea90a3cf-377f-485d-b1b3-6fcf3092fcec', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7b29abcd-efec-453c-a407-cb281f7ffebc', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('678616af-c5bd-4e57-93b1-b937a8c3f6fc', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('048200b5-ef95-4205-8479-80296d098523', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('05903885-b7a0-4c9a-8085-691f3a5f9561', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('36885715-b4fe-4879-a98f-5910155dd487', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('52639d74-170a-47bb-aeec-327c753ac2b2', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c1c1dbb6-0372-4a28-a8aa-332361e0a3fd', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2bb33de6-89e2-4ab3-805a-3cc2e7cb42ed', @groupId9);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7fba3abd-2564-46b9-8cad-935f9f0a8683', @groupId9);

SELECT id INTO @groupId10 from rule_group where name = '10.0 AWS HIPAA 安全检查';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('22dd87f6-353c-42a7-a253-3957b17c1bb4', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b9f4c9f9-0fb6-47d6-af8a-57e9b8902245', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f2e017f1-70cb-4ad1-a187-8895c8040945', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('705c1cef-b47b-4e0b-ab42-d1b360e6502e', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8a2d0ca-4f5b-4809-8321-5ef485b2bf21', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('03979d46-6325-4b20-8d77-0e904a986064', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('67a2b3ad-6974-442c-9f03-32f0a8cd4f4d', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bd18fd9b-06af-4c98-8198-26b3aa3b78bc', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8883c71f-7d42-4007-9152-1138c3fb7ab3', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('96b70632-7ccb-4fe9-af1f-58a8be26eeaf', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a2df85e7-0f0a-4283-84f2-81ee336736e2', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('79d8e033-ddca-49e6-a658-76b5c0e90e09', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1ca7f983-69ea-47ed-9f9a-5a6e7317d8bd', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e7b41c37-410e-4fa8-85bd-2452c1076957', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('79790c74-501c-4830-bba6-ca635c0885f5', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f7f19d65-3561-463b-b3ca-218239268d66', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f79aada7-27fb-47c6-9827-07fa035198bc', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5578d38b-9966-498d-8c5d-f1615a817f87', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c4605126-0a03-420e-a9af-a808c9031138', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('67a51dbc-16a2-4c35-a494-06f60cb7ca7e', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2d040c4f-0486-413f-8738-5bc1a2a3e842', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('14983a14-0f6b-45d8-827b-105368869ae9', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1d1974d5-3e7c-4891-ab99-e817292b73ed', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a8bb65f2-8715-4f86-97ad-0655f7271d9a', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ed2a0376-2f23-4ca6-9746-b909db67a2ab', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e82e163e-4428-49d8-8986-1d06906c165c', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('eb0f3859-8b0d-41e1-b85f-f829ceb99801', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('edb2c0ee-f36a-4864-b66d-bee3cd9730b0', @groupId10);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1a201a3a-153d-4246-834f-c1b8f247443d', @groupId10);

SELECT id INTO @groupId11 from rule_group where name = '11.0 AWS 密钥/密码安全检查';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('40d375c3-a1b6-4785-9325-c5c4c59bcacf', @groupId11);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('fa080bcf-6efb-4091-ab4d-210ef361fc68', @groupId11);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('897d5f04-79b6-4f5a-94c1-091558714ae1', @groupId11);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f2bba305-c147-4f92-b71c-964a3d0e9634', @groupId11);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c283c9f4-40fe-426c-b56b-e36aca559cd7', @groupId11);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('6bbc77f9-270b-4649-91ea-111d9710aba6', @groupId11);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ccd84ca5-08e4-4512-9561-baa6896e24c0', @groupId11);

SELECT id INTO @groupId12 from rule_group where name = '12.0 AWS API Gateway 安全检查';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1d88fcff-44c5-40b5-aa7b-046dd048fa3a', @groupId12);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('67b27d8e-cd62-42a5-a065-bb4e4e1413a5', @groupId12);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('db53b3f2-6826-4852-96d5-feb54d63e451', @groupId12);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b15356d2-75c2-4f3d-a40b-807e9638deb6', @groupId12);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b2d20c54-7fe2-4f35-b1f9-a2ac22ac9082', @groupId12);

SELECT id INTO @groupId13 from rule_group where name = '13.0 AWS RDS 安全检查';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2e2816e7-85db-4903-8aaf-7708f6de4b15', @groupId13);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('aa79b399-bfea-4607-ba4b-5a3f438d3c6e', @groupId13);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('50e66a3d-13c1-403c-a7e1-3b157cda6298', @groupId13);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7c0e6e4a-f6c4-4d11-bfdd-c7231f13a322', @groupId13);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('53748c53-ea71-4c56-8dd3-328d7697268b', @groupId13);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e7db20c0-3151-4ebd-8ec5-c1f553c4c136', @groupId13);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('678616af-c5bd-4e57-93b1-b937a8c3f6fc', @groupId13);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('11eee3ed-6ec6-45f8-877b-f08dcd80b0aa', @groupId13);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c1c1dbb6-0372-4a28-a8aa-332361e0a3fd', @groupId13);

SELECT id INTO @groupId14 from rule_group where name = '14.0 AWS Elasticsearch 安全检查';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bbbc54c2-a917-455b-9782-de74186160cb', @groupId14);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ad17f911-50f4-42db-bb6f-b8c0ec651425', @groupId14);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('74d3aed1-da57-43d4-bb75-2967f7398856', @groupId14);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d6958875-9974-4528-8f9a-9af071f46314', @groupId14);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0a0c6b87-94f9-4323-84a7-b250d870dbdf', @groupId14);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('4b0dbf3a-fb62-4746-a75b-c0a43ac2bcc0', @groupId14);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('12d1f211-4c23-4457-9ca9-55f7c272b2ec', @groupId14);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c2ed97ee-8d3c-4101-b396-211e0d9cca46', @groupId14);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0c701e53-d5be-44ae-b89e-1cbc478f1e1c', @groupId14);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('20677c87-5699-4432-ad50-82007acf0dc7', @groupId14);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0b9cbfc6-06d9-4a9c-898b-e2dccc810ad5', @groupId14);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0e1cd76e-2d63-499b-9e6d-a85eb00ed01a', @groupId14);

SELECT id INTO @groupId15 from rule_group where name = '15.0 AWS PCI-DSS v3.2.1 安全检查';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('48e028e7-9eff-4c98-8a08-2c696e2842f7', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b8844aed-9da9-469b-8ec8-a69fb1a2f7f2', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('3bd0f4f5-eb7f-48d5-b3db-1fd8cee80e07', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('22dd87f6-353c-42a7-a253-3957b17c1bb4', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('77047e0e-88da-422e-93db-2d3a0606e961', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e3fdf46f-782c-439f-b09f-2a616d42697d', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b9f4c9f9-0fb6-47d6-af8a-57e9b8902245', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f2e017f1-70cb-4ad1-a187-8895c8040945', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c66cbddd-5846-4fb9-bedb-8a6812c51c5e', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('705c1cef-b47b-4e0b-ab42-d1b360e6502e', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8a2d0ca-4f5b-4809-8321-5ef485b2bf21', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('03979d46-6325-4b20-8d77-0e904a986064', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('67a2b3ad-6974-442c-9f03-32f0a8cd4f4d', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bd18fd9b-06af-4c98-8198-26b3aa3b78bc', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8883c71f-7d42-4007-9152-1138c3fb7ab3', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('96b70632-7ccb-4fe9-af1f-58a8be26eeaf', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8fd6c3d-5faf-4394-897d-7fd84d33550d', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('79d8e033-ddca-49e6-a658-76b5c0e90e09', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1ca7f983-69ea-47ed-9f9a-5a6e7317d8bd', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e7b41c37-410e-4fa8-85bd-2452c1076957', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('79790c74-501c-4830-bba6-ca635c0885f5', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2d040c4f-0486-413f-8738-5bc1a2a3e842', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e82e163e-4428-49d8-8986-1d06906c165c', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('edb2c0ee-f36a-4864-b66d-bee3cd9730b0', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('acba4a64-2bcc-4bbf-bb97-7983cc4c929b', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('9e0f8f7a-9241-4a5d-8db8-d84cd82c340f', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('78ad5a17-0fda-4650-8bab-3217c2e3b4e0', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('23985b97-2795-4867-b7af-c0b7134fcc17', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7fdd8709-73e6-4bfd-86fe-dd52a1d00aeb', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7cd1eee3-8f47-426d-9182-30037c3bcdac', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1a89ad74-1734-4009-80c1-08404b973793', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e7db20c0-3151-4ebd-8ec5-c1f553c4c136', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7b29abcd-efec-453c-a407-cb281f7ffebc', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('048200b5-ef95-4205-8479-80296d098523', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('db53b3f2-6826-4852-96d5-feb54d63e451', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d11ce3c8-73c3-400e-b147-5ac290ebfa71', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e9fd1bc6-5804-4d54-b736-19deb3ce05c3', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('4e4b0628-dfea-4b7e-ac5c-fd31fc109cf6', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c7da492a-c477-4a55-a783-383a80ad62d9', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ecc005d5-cf4b-496b-b423-47ef1c4fbf2b', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('795dba0c-e2d1-40e1-9af4-2e7b99672e84', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c14f161a-8a1f-4056-afec-0bc3c56243bc', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bc3de762-ca52-4110-bd3e-a4a7ce279df1', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c1c1dbb6-0372-4a28-a8aa-332361e0a3fd', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0a0c6b87-94f9-4323-84a7-b250d870dbdf', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('4b0dbf3a-fb62-4746-a75b-c0a43ac2bcc0', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('12d1f211-4c23-4457-9ca9-55f7c272b2ec', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c2ed97ee-8d3c-4101-b396-211e0d9cca46', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0c701e53-d5be-44ae-b89e-1cbc478f1e1c', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('20677c87-5699-4432-ad50-82007acf0dc7', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0b9cbfc6-06d9-4a9c-898b-e2dccc810ad5', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0e1cd76e-2d63-499b-9e6d-a85eb00ed01a', @groupId15);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7fba3abd-2564-46b9-8cad-935f9f0a8683', @groupId15);

SELECT id INTO @groupId16 from rule_group where name = '16.0 AWS 跨账户信任边界';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5535a3a2-7910-4175-95b3-5fc3e2080df7', @groupId16);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('97a3019d-5ab5-4339-9074-8a4e5c6f70f6', @groupId16);

SELECT id INTO @groupId17 from rule_group where name = '17.0 AWS 暴露的网络资源检查';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ef4311ae-7fd1-4866-b9f3-3e5b89975c44', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8bf919b-cf2c-4930-872b-3949143040d6', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7445f1b3-22dd-4359-9940-3aeedef82f05', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ef3af2e9-ebbe-42f2-b99c-20f53caa9b2f', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7d09e3f5-01f8-454b-a327-34f5bb514d8b', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b81b5de3-d794-4b74-bf20-36d0f9c0d74d', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('9e0f8f7a-9241-4a5d-8db8-d84cd82c340f', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5f8adceb-99aa-49ac-9ed4-d86d8bd67a0d', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('39d29fe9-74ab-450a-af0f-ae7c12896673', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a89f5a2e-eafb-4511-a71e-599a72143408', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5e5cc249-2b20-4c80-a278-a913e18a2b21', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bd5f84b8-9439-40f4-8ccd-2b544f656a91', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('74d3aed1-da57-43d4-bb75-2967f7398856', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7cd1eee3-8f47-426d-9182-30037c3bcdac', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('53748c53-ea71-4c56-8dd3-328d7697268b', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e6060f11-00c7-4a81-bbe2-5292b34c949f', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('6914a08d-0689-422c-a8f2-881ce9ea202c', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('16488f91-abeb-4303-9d1c-ed03e6eddb2b', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ea90a3cf-377f-485d-b1b3-6fcf3092fcec', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7b29abcd-efec-453c-a407-cb281f7ffebc', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('9382c928-b818-4f51-8585-8bd831711223', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b15356d2-75c2-4f3d-a40b-807e9638deb6', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d11ce3c8-73c3-400e-b147-5ac290ebfa71', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('84a1d4fe-3d43-43dd-885d-061536e9927e', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('4e4b0628-dfea-4b7e-ac5c-fd31fc109cf6', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c7da492a-c477-4a55-a783-383a80ad62d9', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b6d9ba86-e7a2-4cb9-8a82-fadfdac75bad', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ecc005d5-cf4b-496b-b423-47ef1c4fbf2b', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('795dba0c-e2d1-40e1-9af4-2e7b99672e84', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c14f161a-8a1f-4056-afec-0bc3c56243bc', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c91192cf-8a2d-49b1-99ed-d21b709d645b', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('70320589-0b7a-4a04-b105-1da1dca0b121', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1cffb8e2-a467-4472-80b3-c44aa58b3c32', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('de5b78fe-e2e5-423e-9d93-72aadbddf89d', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('52639d74-170a-47bb-aeec-327c753ac2b2', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d6958875-9974-4528-8f9a-9af071f46314', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c1c1dbb6-0372-4a28-a8aa-332361e0a3fd', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0b9cbfc6-06d9-4a9c-898b-e2dccc810ad5', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0e1cd76e-2d63-499b-9e6d-a85eb00ed01a', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('189571d9-b536-4a2b-82e3-c2029285422d', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('120344f8-4544-46c5-8de9-2018416a1757', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8db50d8c-64e4-4506-a143-3a0a329492c9', @groupId17);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7fba3abd-2564-46b9-8cad-935f9f0a8683', @groupId17);

SELECT id INTO @groupId18 from rule_group where name = '18.0 AWS ISO 27001 安全检查';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('48e028e7-9eff-4c98-8a08-2c696e2842f7', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b8844aed-9da9-469b-8ec8-a69fb1a2f7f2', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bb3eed24-02b1-43d1-9057-f4f800e4660e', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('3bd0f4f5-eb7f-48d5-b3db-1fd8cee80e07', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('22dd87f6-353c-42a7-a253-3957b17c1bb4', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('77047e0e-88da-422e-93db-2d3a0606e961', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('531cefda-5723-4a81-9ec1-5eb0c797b9a0', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e3fdf46f-782c-439f-b09f-2a616d42697d', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('71427f80-643c-4e8e-9cb8-f07f544b2825', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b9f4c9f9-0fb6-47d6-af8a-57e9b8902245', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('eac1ba91-eb46-4c44-b1f4-e709ea9d0e1e', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f2e017f1-70cb-4ad1-a187-8895c8040945', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c66cbddd-5846-4fb9-bedb-8a6812c51c5e', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('705c1cef-b47b-4e0b-ab42-d1b360e6502e', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8a2d0ca-4f5b-4809-8321-5ef485b2bf21', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('03979d46-6325-4b20-8d77-0e904a986064', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('67a2b3ad-6974-442c-9f03-32f0a8cd4f4d', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bd18fd9b-06af-4c98-8198-26b3aa3b78bc', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8883c71f-7d42-4007-9152-1138c3fb7ab3', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e554b261-0e89-42eb-a44c-4a6d00a32f85', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('96b70632-7ccb-4fe9-af1f-58a8be26eeaf', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a2df85e7-0f0a-4283-84f2-81ee336736e2', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8fd6c3d-5faf-4394-897d-7fd84d33550d', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('79d8e033-ddca-49e6-a658-76b5c0e90e09', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1ca7f983-69ea-47ed-9f9a-5a6e7317d8bd', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e7b41c37-410e-4fa8-85bd-2452c1076957', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('79790c74-501c-4830-bba6-ca635c0885f5', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f7f19d65-3561-463b-b3ca-218239268d66', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f79aada7-27fb-47c6-9827-07fa035198bc', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5578d38b-9966-498d-8c5d-f1615a817f87', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c4605126-0a03-420e-a9af-a808c9031138', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('67a51dbc-16a2-4c35-a494-06f60cb7ca7e', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2d040c4f-0486-413f-8738-5bc1a2a3e842', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('14983a14-0f6b-45d8-827b-105368869ae9', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1d1974d5-3e7c-4891-ab99-e817292b73ed', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a8bb65f2-8715-4f86-97ad-0655f7271d9a', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ed2a0376-2f23-4ca6-9746-b909db67a2ab', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e82e163e-4428-49d8-8986-1d06906c165c', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('eb0f3859-8b0d-41e1-b85f-f829ceb99801', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('edb2c0ee-f36a-4864-b66d-bee3cd9730b0', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1a201a3a-153d-4246-834f-c1b8f247443d', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ef4311ae-7fd1-4866-b9f3-3e5b89975c44', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8bf919b-cf2c-4930-872b-3949143040d6', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('acba4a64-2bcc-4bbf-bb97-7983cc4c929b', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('42e93327-e951-4a6e-b04d-73504f66c380', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7d09e3f5-01f8-454b-a327-34f5bb514d8b', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b1a92471-5517-48cc-8c9d-0edcb55de693', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2e2816e7-85db-4903-8aaf-7708f6de4b15', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e389ad5a-660d-4f69-95cd-98dc1423be48', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('90071ae9-ce0a-41a5-b743-9aafc6e65c08', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a95f4318-d629-4a04-8f42-2056d63f09cb', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('9845da30-1314-4e12-b287-497d24c61579', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('6d47ed7f-6c69-4f21-99ec-d046a0c3037c', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('78ad5a17-0fda-4650-8bab-3217c2e3b4e0', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('822fee45-32ac-46bb-81be-c06243e76a30', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('75676d83-dede-4a0a-93fd-b93003cfaf3b', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7fdd8709-73e6-4bfd-86fe-dd52a1d00aeb', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a9ac2536-088a-45f5-94e6-06abc13eea71', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7cd1eee3-8f47-426d-9182-30037c3bcdac', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('9aec9ac1-71ca-442e-9678-3cdb42f16bed', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5ac7447f-bb24-4bbb-9cb1-014a28d94acc', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1d88fcff-44c5-40b5-aa7b-046dd048fa3a', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('53748c53-ea71-4c56-8dd3-328d7697268b', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7341d661-30f7-4425-a6e7-959404132ac7', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('29bb51e4-b8f0-4242-83d5-4408ce757bef', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('4f0bcacd-4746-44c2-bc87-dc179f574d8c', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e6060f11-00c7-4a81-bbe2-5292b34c949f', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b96f6b16-bf46-4d21-acdd-75d192b78676', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1a89ad74-1734-4009-80c1-08404b973793', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('16488f91-abeb-4303-9d1c-ed03e6eddb2b', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e7db20c0-3151-4ebd-8ec5-c1f553c4c136', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('678616af-c5bd-4e57-93b1-b937a8c3f6fc', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('9382c928-b818-4f51-8585-8bd831711223', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('fa080bcf-6efb-4091-ab4d-210ef361fc68', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('11eee3ed-6ec6-45f8-877b-f08dcd80b0aa', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d11ce3c8-73c3-400e-b147-5ac290ebfa71', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e9fd1bc6-5804-4d54-b736-19deb3ce05c3', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('3a5cf37d-6ec7-4e59-a4b4-12c02aebbca3', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b0a9ff2d-5211-41b6-aaaf-6e98bd2b38fe', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('6bbc77f9-270b-4649-91ea-111d9710aba6', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c91192cf-8a2d-49b1-99ed-d21b709d645b', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ccd84ca5-08e4-4512-9561-baa6896e24c0', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('05903885-b7a0-4c9a-8085-691f3a5f9561', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('25309b12-bac6-42f7-b8cd-f18785eb3f08', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('36885715-b4fe-4879-a98f-5910155dd487', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f3829d4f-3c1b-45a2-bbd1-f48b02b6bc6d', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c2a0e506-c528-44a5-8e08-f7537af796cc', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7c0a3a24-c4f5-432d-a53b-b643112f6512', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f2bba305-c147-4f92-b71c-964a3d0e9634', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b4fd81de-1867-4416-88c2-f67fc4bdcd33', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('70320589-0b7a-4a04-b105-1da1dca0b121', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('de5b78fe-e2e5-423e-9d93-72aadbddf89d', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('394638cf-7baa-4073-aec3-48d1a18a705c', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('6f82c779-7ddc-483c-9947-9108acde05f7', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('affb6d40-1958-47b6-bfe3-a717790c17ce', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c986fdc5-aae9-4b40-a4d6-eda41636c45f', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('52639d74-170a-47bb-aeec-327c753ac2b2', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c1c1dbb6-0372-4a28-a8aa-332361e0a3fd', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5535a3a2-7910-4175-95b3-5fc3e2080df7', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('189571d9-b536-4a2b-82e3-c2029285422d', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('97a3019d-5ab5-4339-9074-8a4e5c6f70f6', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2bb33de6-89e2-4ab3-805a-3cc2e7cb42ed', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e51aa0cd-7617-436f-b429-7141e123e148', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('074a839d-a6df-4a96-adab-94c4e2869201', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('120344f8-4544-46c5-8de9-2018416a1757', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8db50d8c-64e4-4506-a143-3a0a329492c9', @groupId18);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7fba3abd-2564-46b9-8cad-935f9f0a8683', @groupId18);

SELECT id INTO @groupId19 from rule_group where name = '19.0 AWS EKS 基准检查';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c2a0e506-c528-44a5-8e08-f7537af796cc', @groupId19);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('074a839d-a6df-4a96-adab-94c4e2869201', @groupId19);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('120344f8-4544-46c5-8de9-2018416a1757', @groupId19);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8db50d8c-64e4-4506-a143-3a0a329492c9', @groupId19);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('52e44c5e-2702-48b8-a3a6-e221048de715', @groupId19);

SELECT id INTO @groupId20 from rule_group where name = '20.0 AWS FFIEC 网络安全检查';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('48e028e7-9eff-4c98-8a08-2c696e2842f7', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b8844aed-9da9-469b-8ec8-a69fb1a2f7f2', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('3bd0f4f5-eb7f-48d5-b3db-1fd8cee80e07', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('22dd87f6-353c-42a7-a253-3957b17c1bb4', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e3fdf46f-782c-439f-b09f-2a616d42697d', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b9f4c9f9-0fb6-47d6-af8a-57e9b8902245', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f2e017f1-70cb-4ad1-a187-8895c8040945', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c66cbddd-5846-4fb9-bedb-8a6812c51c5e', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8a2d0ca-4f5b-4809-8321-5ef485b2bf21', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('67a2b3ad-6974-442c-9f03-32f0a8cd4f4d', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bd18fd9b-06af-4c98-8198-26b3aa3b78bc', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8883c71f-7d42-4007-9152-1138c3fb7ab3', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('96b70632-7ccb-4fe9-af1f-58a8be26eeaf', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8fd6c3d-5faf-4394-897d-7fd84d33550d', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('79790c74-501c-4830-bba6-ca635c0885f5', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f7f19d65-3561-463b-b3ca-218239268d66', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f79aada7-27fb-47c6-9827-07fa035198bc', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5578d38b-9966-498d-8c5d-f1615a817f87', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c4605126-0a03-420e-a9af-a808c9031138', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('67a51dbc-16a2-4c35-a494-06f60cb7ca7e', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2d040c4f-0486-413f-8738-5bc1a2a3e842', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('14983a14-0f6b-45d8-827b-105368869ae9', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1d1974d5-3e7c-4891-ab99-e817292b73ed', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a8bb65f2-8715-4f86-97ad-0655f7271d9a', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ed2a0376-2f23-4ca6-9746-b909db67a2ab', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e82e163e-4428-49d8-8986-1d06906c165c', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('eb0f3859-8b0d-41e1-b85f-f829ceb99801', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('edb2c0ee-f36a-4864-b66d-bee3cd9730b0', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1a201a3a-153d-4246-834f-c1b8f247443d', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ef4311ae-7fd1-4866-b9f3-3e5b89975c44', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8bf919b-cf2c-4930-872b-3949143040d6', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('acba4a64-2bcc-4bbf-bb97-7983cc4c929b', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('9e0f8f7a-9241-4a5d-8db8-d84cd82c340f', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7cd1eee3-8f47-426d-9182-30037c3bcdac', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('53748c53-ea71-4c56-8dd3-328d7697268b', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1a89ad74-1734-4009-80c1-08404b973793', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('16488f91-abeb-4303-9d1c-ed03e6eddb2b', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8ef1d4de-0c47-446c-a258-6c6df4e1dbe3', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e7db20c0-3151-4ebd-8ec5-c1f553c4c136', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c91192cf-8a2d-49b1-99ed-d21b709d645b', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('36885715-b4fe-4879-a98f-5910155dd487', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c1c1dbb6-0372-4a28-a8aa-332361e0a3fd', @groupId20);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2bb33de6-89e2-4ab3-805a-3cc2e7cb42ed', @groupId20);

SELECT id INTO @groupId21 from rule_group where name = '21.0 AWS SOC2 安全检查';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b8844aed-9da9-469b-8ec8-a69fb1a2f7f2', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bb3eed24-02b1-43d1-9057-f4f800e4660e', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('22dd87f6-353c-42a7-a253-3957b17c1bb4', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b9f4c9f9-0fb6-47d6-af8a-57e9b8902245', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('eac1ba91-eb46-4c44-b1f4-e709ea9d0e1e', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f2e017f1-70cb-4ad1-a187-8895c8040945', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('705c1cef-b47b-4e0b-ab42-d1b360e6502e', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8a2d0ca-4f5b-4809-8321-5ef485b2bf21', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('03979d46-6325-4b20-8d77-0e904a986064', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('67a2b3ad-6974-442c-9f03-32f0a8cd4f4d', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bd18fd9b-06af-4c98-8198-26b3aa3b78bc', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8883c71f-7d42-4007-9152-1138c3fb7ab3', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f7f19d65-3561-463b-b3ca-218239268d66', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f79aada7-27fb-47c6-9827-07fa035198bc', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('14983a14-0f6b-45d8-827b-105368869ae9', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1d1974d5-3e7c-4891-ab99-e817292b73ed', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a8bb65f2-8715-4f86-97ad-0655f7271d9a', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ed2a0376-2f23-4ca6-9746-b909db67a2ab', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e82e163e-4428-49d8-8986-1d06906c165c', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('eb0f3859-8b0d-41e1-b85f-f829ceb99801', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('edb2c0ee-f36a-4864-b66d-bee3cd9730b0', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1a201a3a-153d-4246-834f-c1b8f247443d', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ef4311ae-7fd1-4866-b9f3-3e5b89975c44', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8bf919b-cf2c-4930-872b-3949143040d6', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('acba4a64-2bcc-4bbf-bb97-7983cc4c929b', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('9e0f8f7a-9241-4a5d-8db8-d84cd82c340f', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7cd1eee3-8f47-426d-9182-30037c3bcdac', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('53748c53-ea71-4c56-8dd3-328d7697268b', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1a89ad74-1734-4009-80c1-08404b973793', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('16488f91-abeb-4303-9d1c-ed03e6eddb2b', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8ef1d4de-0c47-446c-a258-6c6df4e1dbe3', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e7db20c0-3151-4ebd-8ec5-c1f553c4c136', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('678616af-c5bd-4e57-93b1-b937a8c3f6fc', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c91192cf-8a2d-49b1-99ed-d21b709d645b', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c1c1dbb6-0372-4a28-a8aa-332361e0a3fd', @groupId21);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2bb33de6-89e2-4ab3-805a-3cc2e7cb42ed', @groupId21);

SELECT id INTO @groupId22 from rule_group where name = '22.0 AWS SageMaker 安全检查';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b632c31c-91b5-42a5-bc4b-bf0a2acfbf02', @groupId22);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8928f014-1d7c-4793-8c28-bd34b780b3c5', @groupId22);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bd5566ee-6e33-4522-9ac9-18a6ba2a8a55', @groupId22);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ac632efc-bacc-4229-acd2-216684e36e41', @groupId22);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8ead7b4a-5198-4feb-966b-232c0197d7bf', @groupId22);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('000348ec-9f32-4894-bff8-9e39abda83be', @groupId22);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ac04175b-feaf-4128-8cf4-b764d96c5b7b', @groupId22);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('305e28d3-f68e-44b7-925b-6a31b92abf8a', @groupId22);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('05130e8d-e8f6-4446-b8d1-4e1e0a8fe813', @groupId22);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5e4678f5-378a-44b1-849e-319f3bf84faf', @groupId22);

SELECT id INTO @groupId23 from rule_group where name = '23.0 AWS ENS 安全检查';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e3fdf46f-782c-439f-b09f-2a616d42697d', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f8579451-2c24-4743-87e3-0fcf7c465d08', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d2628999-b60d-4e7f-812d-0cd5a94a7e38', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f2e017f1-70cb-4ad1-a187-8895c8040945', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c66cbddd-5846-4fb9-bedb-8a6812c51c5e', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8883c71f-7d42-4007-9152-1138c3fb7ab3', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e554b261-0e89-42eb-a44c-4a6d00a32f85', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('96b70632-7ccb-4fe9-af1f-58a8be26eeaf', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a2df85e7-0f0a-4283-84f2-81ee336736e2', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8fd6c3d-5faf-4394-897d-7fd84d33550d', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1ca7f983-69ea-47ed-9f9a-5a6e7317d8bd', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f7f19d65-3561-463b-b3ca-218239268d66', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('14983a14-0f6b-45d8-827b-105368869ae9', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1d1974d5-3e7c-4891-ab99-e817292b73ed', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a8bb65f2-8715-4f86-97ad-0655f7271d9a', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ed2a0376-2f23-4ca6-9746-b909db67a2ab', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('eb0f3859-8b0d-41e1-b85f-f829ceb99801', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b1a92471-5517-48cc-8c9d-0edcb55de693', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e389ad5a-660d-4f69-95cd-98dc1423be48', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('cf92e9e1-5d68-4bb2-85b6-ed807fdfd50f', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('90071ae9-ce0a-41a5-b743-9aafc6e65c08', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('616b9333-e7b5-46e5-8121-eb43314e8c71', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('94057d78-563d-4d24-870f-246c7a1ba6a0', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e7b41c37-410e-4fa8-85bd-2452c1076957', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('79790c74-501c-4830-bba6-ca635c0885f5', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ef4311ae-7fd1-4866-b9f3-3e5b89975c44', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8bf919b-cf2c-4930-872b-3949143040d6', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('acba4a64-2bcc-4bbf-bb97-7983cc4c929b', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a95f4318-d629-4a04-8f42-2056d63f09cb', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('9845da30-1314-4e12-b287-497d24c61579', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('6d47ed7f-6c69-4f21-99ec-d046a0c3037c', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('78ad5a17-0fda-4650-8bab-3217c2e3b4e0', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b96f6b16-bf46-4d21-acdd-75d192b78676', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1a89ad74-1734-4009-80c1-08404b973793', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8ef1d4de-0c47-446c-a258-6c6df4e1dbe3', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e7db20c0-3151-4ebd-8ec5-c1f553c4c136', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ea90a3cf-377f-485d-b1b3-6fcf3092fcec', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7b29abcd-efec-453c-a407-cb281f7ffebc', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('9382c928-b818-4f51-8585-8bd831711223', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('048200b5-ef95-4205-8479-80296d098523', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('db53b3f2-6826-4852-96d5-feb54d63e451', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('84a1d4fe-3d43-43dd-885d-061536e9927e', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e9fd1bc6-5804-4d54-b736-19deb3ce05c3', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('4e4b0628-dfea-4b7e-ac5c-fd31fc109cf6', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c7da492a-c477-4a55-a783-383a80ad62d9', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b6d9ba86-e7a2-4cb9-8a82-fadfdac75bad', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ecc005d5-cf4b-496b-b423-47ef1c4fbf2b', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('795dba0c-e2d1-40e1-9af4-2e7b99672e84', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c14f161a-8a1f-4056-afec-0bc3c56243bc', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('05903885-b7a0-4c9a-8085-691f3a5f9561', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f3829d4f-3c1b-45a2-bbd1-f48b02b6bc6d', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bc3de762-ca52-4110-bd3e-a4a7ce279df1', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('4b0dbf3a-fb62-4746-a75b-c0a43ac2bcc0', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2bb33de6-89e2-4ab3-805a-3cc2e7cb42ed', @groupId23);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e51aa0cd-7617-436f-b429-7141e123e148', @groupId23);

SELECT id INTO @groupId24 from rule_group where name = '24.0 AWS Glue 安全检查';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d18ae6a7-a060-4ee7-a59d-3575447a1ea2', @groupId24);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('3f8e0d58-f003-4b4a-aea4-05f40a796c6f', @groupId24);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2bbd04a6-40af-4d74-a32b-4c31f52c7504', @groupId24);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c16fe4e1-3589-4f74-b3e5-64504e481d3e', @groupId24);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e14667da-cdef-4afe-9113-4b3d21a61f11', @groupId24);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e71b1798-e390-4e05-bd39-47ccde2244c5', @groupId24);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('4f622382-8519-4990-af03-ffa0ed9cc446', @groupId24);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2d70fd48-f88f-45ab-878e-f9e06499b741', @groupId24);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2ad08cd2-3e23-4fde-8ffd-27174e56e579', @groupId24);

SELECT id INTO @groupId25 from rule_group where name = '25.0 AWS FTR 安全检查';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('48e028e7-9eff-4c98-8a08-2c696e2842f7', @groupId25);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b8844aed-9da9-469b-8ec8-a69fb1a2f7f2', @groupId25);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bb3eed24-02b1-43d1-9057-f4f800e4660e', @groupId25);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('3bd0f4f5-eb7f-48d5-b3db-1fd8cee80e07', @groupId25);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('22dd87f6-353c-42a7-a253-3957b17c1bb4', @groupId25);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('065d41f3-ff24-4e2e-989f-df71c45e3853', @groupId25);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d80b3e78-6dfc-429c-bcdd-50ec212cfc42', @groupId25);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b9f4c9f9-0fb6-47d6-af8a-57e9b8902245', @groupId25);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('eac1ba91-eb46-4c44-b1f4-e709ea9d0e1e', @groupId25);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f2e017f1-70cb-4ad1-a187-8895c8040945', @groupId25);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c66cbddd-5846-4fb9-bedb-8a6812c51c5e', @groupId25);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('705c1cef-b47b-4e0b-ab42-d1b360e6502e', @groupId25);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8a2d0ca-4f5b-4809-8321-5ef485b2bf21', @groupId25);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('03979d46-6325-4b20-8d77-0e904a986064', @groupId25);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('67a2b3ad-6974-442c-9f03-32f0a8cd4f4d', @groupId25);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bd18fd9b-06af-4c98-8198-26b3aa3b78bc', @groupId25);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8883c71f-7d42-4007-9152-1138c3fb7ab3', @groupId25);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e554b261-0e89-42eb-a44c-4a6d00a32f85', @groupId25);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('40d375c3-a1b6-4785-9325-c5c4c59bcacf', @groupId25);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('6914a08d-0689-422c-a8f2-881ce9ea202c', @groupId25);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('6bbc77f9-270b-4649-91ea-111d9710aba6', @groupId25);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ccd84ca5-08e4-4512-9561-baa6896e24c0', @groupId25);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f2bba305-c147-4f92-b71c-964a3d0e9634', @groupId25);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c283c9f4-40fe-426c-b56b-e36aca559cd7', @groupId25);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('52e44c5e-2702-48b8-a3a6-e221048de715', @groupId25);

SELECT id INTO @groupId26 from rule_group where name = '26.0 AWS 服务目录安全检查';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2d4c6320-bcd3-4599-9564-87e4e6f82eeb', @groupId26);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a14ec927-6f85-47fa-89dc-4d5fbb0534eb', @groupId26);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('4420e7c7-578d-44e2-8a8a-d0e5691d76c2', @groupId26);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('14f34d63-8a93-4f70-ae25-69d29b71d573', @groupId26);

SELECT id INTO @groupId27 from rule_group where name = '27.0 AWS Implementation Group 安全检查';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b8844aed-9da9-469b-8ec8-a69fb1a2f7f2', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('22dd87f6-353c-42a7-a253-3957b17c1bb4', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('77047e0e-88da-422e-93db-2d3a0606e961', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b9f4c9f9-0fb6-47d6-af8a-57e9b8902245', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f8579451-2c24-4743-87e3-0fcf7c465d08', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d2628999-b60d-4e7f-812d-0cd5a94a7e38', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('eac1ba91-eb46-4c44-b1f4-e709ea9d0e1e', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('bd18fd9b-06af-4c98-8198-26b3aa3b78bc', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8883c71f-7d42-4007-9152-1138c3fb7ab3', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e554b261-0e89-42eb-a44c-4a6d00a32f85', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('96b70632-7ccb-4fe9-af1f-58a8be26eeaf', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a2df85e7-0f0a-4283-84f2-81ee336736e2', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8fd6c3d-5faf-4394-897d-7fd84d33550d', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('79d8e033-ddca-49e6-a658-76b5c0e90e09', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1ca7f983-69ea-47ed-9f9a-5a6e7317d8bd', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e7b41c37-410e-4fa8-85bd-2452c1076957', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('79790c74-501c-4830-bba6-ca635c0885f5', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f7f19d65-3561-463b-b3ca-218239268d66', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f79aada7-27fb-47c6-9827-07fa035198bc', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5578d38b-9966-498d-8c5d-f1615a817f87', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c4605126-0a03-420e-a9af-a808c9031138', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('67a51dbc-16a2-4c35-a494-06f60cb7ca7e', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2d040c4f-0486-413f-8738-5bc1a2a3e842', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('14983a14-0f6b-45d8-827b-105368869ae9', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1d1974d5-3e7c-4891-ab99-e817292b73ed', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('a8bb65f2-8715-4f86-97ad-0655f7271d9a', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ed2a0376-2f23-4ca6-9746-b909db67a2ab', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e82e163e-4428-49d8-8986-1d06906c165c', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('eb0f3859-8b0d-41e1-b85f-f829ceb99801', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('edb2c0ee-f36a-4864-b66d-bee3cd9730b0', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1a201a3a-153d-4246-834f-c1b8f247443d', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ef4311ae-7fd1-4866-b9f3-3e5b89975c44', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d8bf919b-cf2c-4930-872b-3949143040d6', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('acba4a64-2bcc-4bbf-bb97-7983cc4c929b', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('42e93327-e951-4a6e-b04d-73504f66c380', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7445f1b3-22dd-4359-9940-3aeedef82f05', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ef3af2e9-ebbe-42f2-b99c-20f53caa9b2f', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('aa79b399-bfea-4607-ba4b-5a3f438d3c6e', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('359a94bf-6423-4db2-96b0-86fd6de9bb25', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('109b39e5-6878-43cb-93ab-f19a22f2ecdd', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8ef1d4de-0c47-446c-a258-6c6df4e1dbe3', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e7db20c0-3151-4ebd-8ec5-c1f553c4c136', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('05903885-b7a0-4c9a-8085-691f3a5f9561', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f3829d4f-3c1b-45a2-bbd1-f48b02b6bc6d', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('6f82c779-7ddc-483c-9947-9108acde05f7', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c1c1dbb6-0372-4a28-a8aa-332361e0a3fd', @groupId27);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('05a359f2-af4e-49ca-9e48-09510dc2252a', @groupId27);

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '000348ec-9f32-4894-bff8-9e39abda83be', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '03979d46-6325-4b20-8d77-0e904a986064', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '048200b5-ef95-4205-8479-80296d098523', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '05130e8d-e8f6-4446-b8d1-4e1e0a8fe813', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '05903885-b7a0-4c9a-8085-691f3a5f9561', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '05a359f2-af4e-49ca-9e48-09510dc2252a', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '065d41f3-ff24-4e2e-989f-df71c45e3853', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '074a839d-a6df-4a96-adab-94c4e2869201', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '095ac637-4e0d-46ef-b658-dc2abbe0ed7e', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '0a0c6b87-94f9-4323-84a7-b250d870dbdf', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '0b9cbfc6-06d9-4a9c-898b-e2dccc810ad5', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '0c701e53-d5be-44ae-b89e-1cbc478f1e1c', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '0e1cd76e-2d63-499b-9e6d-a85eb00ed01a', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '0eda2992-8173-4c3a-bd1c-e2e48e0aad1e', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '10751001-d9aa-4582-a96a-471813e50512', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '109b39e5-6878-43cb-93ab-f19a22f2ecdd', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '111fabaa-249b-4a73-b9fd-19166c9073b7', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '11eee3ed-6ec6-45f8-877b-f08dcd80b0aa', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '120344f8-4544-46c5-8de9-2018416a1757', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '12d1f211-4c23-4457-9ca9-55f7c272b2ec', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '14983a14-0f6b-45d8-827b-105368869ae9', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '14f34d63-8a93-4f70-ae25-69d29b71d573', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '16488f91-abeb-4303-9d1c-ed03e6eddb2b', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '189571d9-b536-4a2b-82e3-c2029285422d', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '1a201a3a-153d-4246-834f-c1b8f247443d', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '1a89ad74-1734-4009-80c1-08404b973793', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '1ca7f983-69ea-47ed-9f9a-5a6e7317d8bd', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '1cffb8e2-a467-4472-80b3-c44aa58b3c32', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '1d1974d5-3e7c-4891-ab99-e817292b73ed', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '1d88fcff-44c5-40b5-aa7b-046dd048fa3a', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '20677c87-5699-4432-ad50-82007acf0dc7', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '2198c834-ac57-4bd4-be8a-b1fa8834fc76', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '22dd87f6-353c-42a7-a253-3957b17c1bb4', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '23985b97-2795-4867-b7af-c0b7134fcc17', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '23aaf8a8-820b-4b2d-a2a9-e27151fdcc12', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '25309b12-bac6-42f7-b8cd-f18785eb3f08', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '2803b3c5-2a11-4f3d-8bd7-acaa18bd35cf', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '2885b53e-12f4-4864-9dc7-79327db63604', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '29bb51e4-b8f0-4242-83d5-4408ce757bef', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '2ad08cd2-3e23-4fde-8ffd-27174e56e579', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '2bb33de6-89e2-4ab3-805a-3cc2e7cb42ed', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '2bbd04a6-40af-4d74-a32b-4c31f52c7504', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '2d040c4f-0486-413f-8738-5bc1a2a3e842', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '2d4c6320-bcd3-4599-9564-87e4e6f82eeb', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '2d70fd48-f88f-45ab-878e-f9e06499b741', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '2da5e51d-889a-4dbf-a2f8-11259b8eaa56', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '2e2816e7-85db-4903-8aaf-7708f6de4b15', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '305e28d3-f68e-44b7-925b-6a31b92abf8a', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '359a94bf-6423-4db2-96b0-86fd6de9bb25', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '36885715-b4fe-4879-a98f-5910155dd487', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '394638cf-7baa-4073-aec3-48d1a18a705c', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '39d29fe9-74ab-450a-af0f-ae7c12896673', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '3a5cf37d-6ec7-4e59-a4b4-12c02aebbca3', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '3bd0f4f5-eb7f-48d5-b3db-1fd8cee80e07', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '3c7209df-c617-46a5-8e73-4dc0ea100f9d', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '3d27d878-d48e-4784-bff5-b230d2d28846', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '3f8e0d58-f003-4b4a-aea4-05f40a796c6f', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '3fa369e5-ab65-40e8-b758-1b5be10ee275', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '40d375c3-a1b6-4785-9325-c5c4c59bcacf', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '41e7a337-339b-4609-8904-e362196aa9c0', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '42e93327-e951-4a6e-b04d-73504f66c380', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '4420e7c7-578d-44e2-8a8a-d0e5691d76c2', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '48e028e7-9eff-4c98-8a08-2c696e2842f7', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '4b0dbf3a-fb62-4746-a75b-c0a43ac2bcc0', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '4e4b0628-dfea-4b7e-ac5c-fd31fc109cf6', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '4f0bcacd-4746-44c2-bc87-dc179f574d8c', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '4f622382-8519-4990-af03-ffa0ed9cc446', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '50e66a3d-13c1-403c-a7e1-3b157cda6298', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '520959f9-f224-41fa-9a52-0df38e55eb8f', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '52639d74-170a-47bb-aeec-327c753ac2b2', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '52e44c5e-2702-48b8-a3a6-e221048de715', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '531cefda-5723-4a81-9ec1-5eb0c797b9a0', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '53569e08-86c6-43ba-99f5-64d529477bc0', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '53748c53-ea71-4c56-8dd3-328d7697268b', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '5535a3a2-7910-4175-95b3-5fc3e2080df7', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '5578d38b-9966-498d-8c5d-f1615a817f87', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '55e5207c-9d0e-48a5-ba68-c01ead665443', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '5ac7447f-bb24-4bbb-9cb1-014a28d94acc', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '5de4eb69-b295-4697-90ca-37f5da1c793d', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '5e4678f5-378a-44b1-849e-319f3bf84faf', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '5e5cc249-2b20-4c80-a278-a913e18a2b21', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '5f8adceb-99aa-49ac-9ed4-d86d8bd67a0d', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '616b9333-e7b5-46e5-8121-eb43314e8c71', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '62d16913-61e4-46fc-abd7-4174c1ab89cc', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '65dfbac5-4ee3-4405-a5d0-98e3da849831', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '678616af-c5bd-4e57-93b1-b937a8c3f6fc', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '67a2b3ad-6974-442c-9f03-32f0a8cd4f4d', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '67a51dbc-16a2-4c35-a494-06f60cb7ca7e', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '67b27d8e-cd62-42a5-a065-bb4e4e1413a5', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '6914a08d-0689-422c-a8f2-881ce9ea202c', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '6bbc77f9-270b-4649-91ea-111d9710aba6', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '6ca702b2-13b2-4dfb-9a8e-2aedbfbd5252', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '6d47ed7f-6c69-4f21-99ec-d046a0c3037c', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '6f0f6236-51e1-458a-8ca3-163b70eb284b', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '6f82c779-7ddc-483c-9947-9108acde05f7', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '70320589-0b7a-4a04-b105-1da1dca0b121', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '705c1cef-b47b-4e0b-ab42-d1b360e6502e', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '71427f80-643c-4e8e-9cb8-f07f544b2825', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '7341d661-30f7-4425-a6e7-959404132ac7', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '7445f1b3-22dd-4359-9940-3aeedef82f05', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '74d3aed1-da57-43d4-bb75-2967f7398856', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '75676d83-dede-4a0a-93fd-b93003cfaf3b', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '75d0d479-4c86-418a-b805-e4d32d7b9583', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '77047e0e-88da-422e-93db-2d3a0606e961', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '78ad5a17-0fda-4650-8bab-3217c2e3b4e0', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '795dba0c-e2d1-40e1-9af4-2e7b99672e84', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '79790c74-501c-4830-bba6-ca635c0885f5', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '79d8e033-ddca-49e6-a658-76b5c0e90e09', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '7b29abcd-efec-453c-a407-cb281f7ffebc', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '7b3df26a-1839-430d-b8b1-bec22849a62b', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '7c0a3a24-c4f5-432d-a53b-b643112f6512', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '7c0e6e4a-f6c4-4d11-bfdd-c7231f13a322', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '7cd1eee3-8f47-426d-9182-30037c3bcdac', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '7d09e3f5-01f8-454b-a327-34f5bb514d8b', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '7ef05cf8-0d7d-46e0-a6c4-a3e2dfba01ef', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '7fba3abd-2564-46b9-8cad-935f9f0a8683', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '7fdd8709-73e6-4bfd-86fe-dd52a1d00aeb', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '822fee45-32ac-46bb-81be-c06243e76a30', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '84a1d4fe-3d43-43dd-885d-061536e9927e', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '87259583-f650-48ba-a1e5-3f0f35b3dc34', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '87786f5c-2f7d-4836-849d-900d587c38db', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '8883c71f-7d42-4007-9152-1138c3fb7ab3', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '88c3427c-c668-49ee-987f-7b7da5b4de11', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '8928f014-1d7c-4793-8c28-bd34b780b3c5', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '897d5f04-79b6-4f5a-94c1-091558714ae1', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '8a3cd7a7-3aea-4e57-b646-f97d9952614a', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '8afa254e-0446-4e55-9a32-eb83bf3049ab', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '8bd18f86-7b0d-4927-860d-83e295cc6b05', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '8db50d8c-64e4-4506-a143-3a0a329492c9', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '8ead7b4a-5198-4feb-966b-232c0197d7bf', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '8ef1d4de-0c47-446c-a258-6c6df4e1dbe3', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '8f1cd40a-bcff-4089-97e8-45337c4e8cb1', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '90071ae9-ce0a-41a5-b743-9aafc6e65c08', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '90ec24a4-79de-4911-bea9-ca795a7e6eec', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '9382c928-b818-4f51-8585-8bd831711223', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '94057d78-563d-4d24-870f-246c7a1ba6a0', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '96b70632-7ccb-4fe9-af1f-58a8be26eeaf', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '96ccf8ea-3d58-4342-a25d-16026e655cfe', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '978d14c5-da23-4cf8-9cd9-c0e710977250', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '97a3019d-5ab5-4339-9074-8a4e5c6f70f6', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '9845da30-1314-4e12-b287-497d24c61579', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '98e815f5-8b07-4717-a15c-389b34e4ebfc', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '9a9eeba2-8a1c-4e3f-b486-5357a7546e08', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '9aec9ac1-71ca-442e-9678-3cdb42f16bed', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '9b174f28-9ed1-4e93-9be1-adde6619d8c3', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '9d0ca4db-2cf0-45d7-a14d-8dcd18791191', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '9ddfd1d2-810e-417a-ba5f-925365b2f9f8', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), '9e0f8f7a-9241-4a5d-8db8-d84cd82c340f', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'a0964936-4dde-4fdf-b512-bb6a0fae8146', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'a14ec927-6f85-47fa-89dc-4d5fbb0534eb', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'a2df85e7-0f0a-4283-84f2-81ee336736e2', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'a3c12f99-237c-4dfd-b4e5-8d6d30045812', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'a83198cb-b852-4dc9-a99b-c0756f825576', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'a89f5a2e-eafb-4511-a71e-599a72143408', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'a8bb65f2-8715-4f86-97ad-0655f7271d9a', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'a95f4318-d629-4a04-8f42-2056d63f09cb', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'a9ac2536-088a-45f5-94e6-06abc13eea71', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'aa79b399-bfea-4607-ba4b-5a3f438d3c6e', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'abe0b522-dabf-456f-bac6-d0d48cae4eed', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'ac04175b-feaf-4128-8cf4-b764d96c5b7b', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'ac632efc-bacc-4229-acd2-216684e36e41', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'acba4a64-2bcc-4bbf-bb97-7983cc4c929b', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'ad17f911-50f4-42db-bb6f-b8c0ec651425', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'affb6d40-1958-47b6-bfe3-a717790c17ce', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'b0a9ff2d-5211-41b6-aaaf-6e98bd2b38fe', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'b15356d2-75c2-4f3d-a40b-807e9638deb6', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'b1a92471-5517-48cc-8c9d-0edcb55de693', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'b2d20c54-7fe2-4f35-b1f9-a2ac22ac9082', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'b4fd81de-1867-4416-88c2-f67fc4bdcd33', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'b632c31c-91b5-42a5-bc4b-bf0a2acfbf02', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'b6d9ba86-e7a2-4cb9-8a82-fadfdac75bad', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'b81b5de3-d794-4b74-bf20-36d0f9c0d74d', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'b8844aed-9da9-469b-8ec8-a69fb1a2f7f2', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'b96f6b16-bf46-4d21-acdd-75d192b78676', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'b9f4c9f9-0fb6-47d6-af8a-57e9b8902245', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'bb3eed24-02b1-43d1-9057-f4f800e4660e', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'bbbc54c2-a917-455b-9782-de74186160cb', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'bc3de762-ca52-4110-bd3e-a4a7ce279df1', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'bd18fd9b-06af-4c98-8198-26b3aa3b78bc', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'bd5566ee-6e33-4522-9ac9-18a6ba2a8a55', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'bd5f84b8-9439-40f4-8ccd-2b544f656a91', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'bebe49c3-8e87-4c21-a2b3-7f5f6cc90136', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'bf3990ee-324f-4618-8f2f-190df72aaf11', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'c14f161a-8a1f-4056-afec-0bc3c56243bc', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'c16fe4e1-3589-4f74-b3e5-64504e481d3e', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'c1c1dbb6-0372-4a28-a8aa-332361e0a3fd', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'c283c9f4-40fe-426c-b56b-e36aca559cd7', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'c2a0e506-c528-44a5-8e08-f7537af796cc', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'c2ed97ee-8d3c-4101-b396-211e0d9cca46', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'c4605126-0a03-420e-a9af-a808c9031138', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'c66cbddd-5846-4fb9-bedb-8a6812c51c5e', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'c7da492a-c477-4a55-a783-383a80ad62d9', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'c8926d60-6726-407d-ba1d-839effafae3c', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'c91192cf-8a2d-49b1-99ed-d21b709d645b', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'c986fdc5-aae9-4b40-a4d6-eda41636c45f', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'ccd84ca5-08e4-4512-9561-baa6896e24c0', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'cf92e9e1-5d68-4bb2-85b6-ed807fdfd50f', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'd11ce3c8-73c3-400e-b147-5ac290ebfa71', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'd18ae6a7-a060-4ee7-a59d-3575447a1ea2', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'd1fbc80a-78c1-4214-bf99-13ec76e78b58', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'd2628999-b60d-4e7f-812d-0cd5a94a7e38', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'd6958875-9974-4528-8f9a-9af071f46314', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'd77e0bc8-6161-4a7a-a6f4-d4ce788e34df', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'd80b3e78-6dfc-429c-bcdd-50ec212cfc42', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'd8a2d0ca-4f5b-4809-8321-5ef485b2bf21', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'd8b035cd-6d0c-43b6-9277-e6be6fd8f8da', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'd8bf919b-cf2c-4930-872b-3949143040d6', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'd8fd6c3d-5faf-4394-897d-7fd84d33550d', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'db53b3f2-6826-4852-96d5-feb54d63e451', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'dc28da88-7551-453d-a891-5c9c23781bc4', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'de5b78fe-e2e5-423e-9d93-72aadbddf89d', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'e14667da-cdef-4afe-9113-4b3d21a61f11', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'e389ad5a-660d-4f69-95cd-98dc1423be48', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'e3d8f69b-a3ef-47d7-b715-235fb803e9e6', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'e3fdf46f-782c-439f-b09f-2a616d42697d', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'e51aa0cd-7617-436f-b429-7141e123e148', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'e554b261-0e89-42eb-a44c-4a6d00a32f85', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'e5b42411-47db-4358-b156-1d7a56b08b10', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'e6060f11-00c7-4a81-bbe2-5292b34c949f', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'e71b1798-e390-4e05-bd39-47ccde2244c5', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'e7b41c37-410e-4fa8-85bd-2452c1076957', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'e7db20c0-3151-4ebd-8ec5-c1f553c4c136', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'e82e163e-4428-49d8-8986-1d06906c165c', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'e9fd1bc6-5804-4d54-b736-19deb3ce05c3', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'ea90a3cf-377f-485d-b1b3-6fcf3092fcec', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'eac1ba91-eb46-4c44-b1f4-e709ea9d0e1e', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'eb0f3859-8b0d-41e1-b85f-f829ceb99801', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'ecc005d5-cf4b-496b-b423-47ef1c4fbf2b', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'ed2a0376-2f23-4ca6-9746-b909db67a2ab', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'edb2c0ee-f36a-4864-b66d-bee3cd9730b0', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'ef3af2e9-ebbe-42f2-b99c-20f53caa9b2f', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'ef4311ae-7fd1-4866-b9f3-3e5b89975c44', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'f0cfc258-0001-4423-9485-b96521535c50', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'f2bba305-c147-4f92-b71c-964a3d0e9634', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'f2e017f1-70cb-4ad1-a187-8895c8040945', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'f3829d4f-3c1b-45a2-bbd1-f48b02b6bc6d', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'f4190017-0459-4f7e-943d-5f299771da5b', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'f79aada7-27fb-47c6-9827-07fa035198bc', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'f7f19d65-3561-463b-b3ca-218239268d66', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'f8579451-2c24-4743-87e3-0fcf7c465d08', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'fa080bcf-6efb-4091-ab4d-210ef361fc68', 'prowler');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES (UUID(), 'fe94237b-eaa1-4d2f-9d87-c354b787fdc8', 'prowler');
