
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
    `push_time`                  varchar(50)        DEFAULT NULL COMMENT '推送时间',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;


ALTER TABLE cloud_event_sync_log ADD column exception varchar(1024) DEFAULT NULL COMMENT '异常信息';

ALTER TABLE cloud_native_config_result_item modify COLUMN `title` varchar(512) DEFAULT NULL COMMENT 'Title';
ALTER TABLE cloud_native_config_result_item modify COLUMN `namespace` varchar(256) DEFAULT NULL COMMENT 'Namespace';
ALTER TABLE cloud_native_config_result_item modify COLUMN `query` varchar(256) DEFAULT NULL COMMENT 'Query';
ALTER TABLE cloud_native_config_result_item modify COLUMN `resolution` varchar(512) DEFAULT NULL COMMENT 'Resolution';
ALTER TABLE cloud_native_config_result_item modify COLUMN `primary_url` varchar(256) DEFAULT NULL COMMENT 'PrimaryURL';

DELETE FROM plugin WHERE id = 'hummer-tsunami-plugin';

INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('c497a676-12ec-41ed-8944-eb32cc95b8a4', 'Linux 查看僵尸进程', 1, 'MediumRisk', '查看 Linux 僵尸进程', 'ps aux | awk \'{ print $8 \" \" $2 }\' | grep -w Z', '[]', concat(unix_timestamp(now()), '001'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('6f378375-bbe2-47f7-85fc-4a8e8c8193c6', 'Linux 查看磁盘使用率', 1, 'LowRisk', '检测 Linux 操作系统的磁盘使用率是否超过阈值', 'for i in $(df |grep /|tr -s \" \"|cut -d \" \" -f 5|awk -F% \'{print $1}\')\ndo \n  if [[ $i -gt 80 ]];then \n  echo \"检测磁盘使用率超过 ${{disk_use}}%，建议观察磁盘使用情况，或进行扩容\"\n  fi\ndone', '[]', concat(unix_timestamp(now()), '001'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('9eacb3a6-6129-44b9-b29a-c07ad449b063', 'Linux 消耗内存最多的进程', 1, 'MediumRisk', '查看 Linux 消耗内存最多的进程', 'ps auxf |sort -nr -k 4 |head -5', '[]', concat(unix_timestamp(now()), '001'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('fdf46bee-0ae7-416f-85c8-b17c8dfa08ba', 'Linux 查看是否存在内存溢出', 1, 'MediumRisk', '查看 Linux 系统进程是否存在内存溢出', 'grep \"Out of memory\" /var/log/messages', '[]', concat(unix_timestamp(now()), '001'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('7f742d41-d91d-4597-8462-25a89f8841e0', 'Linux 检测是否运行 SSH 远程密码登录', 1, 'LowRisk', '检测 Linux 系统是否运行 SSH 远程密码登录', 'if [[ $(grep -i \"^PasswordAuthentication\" /etc/ssh/sshd_config|awk \'{print $2}\') =~ no ]];then\n  echo ”配置正常\nelse\n  echo \"异常：当前设置运行使用密码进行远程登录，安全性较低，建议使用SSH密钥登录\"\nfi', '[]', concat(unix_timestamp(now()), '001'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('ba903623-9d68-4939-ba39-fa34628c2d1d', 'Linux 检测操作系统类型', 1, 'LowRisk', '查看 Linux 检测操作系统类型', 'OS=\"`uname`\"\ncase $OS in\n  \'Linux\')\n    OS=\'Linux\'\n    alias ls=\'ls --color=auto\'\n    ;;\n  \'FreeBSD\')\n    OS=\'FreeBSD\'\n    alias ls=\'ls -G\'\n    ;;\n  \'WindowsNT\')\n    OS=\'Windows\'\n    ;;\n  \'Darwin\') \n    OS=\'Mac\'\n    ;;\n  \'SunOS\')\n    OS=\'Solaris\'\n    ;;\n  \'AIX\') ;;\n  *) ;;\nesac', '[]', concat(unix_timestamp(now()), '001'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('f00222ed-4a39-43be-b2a8-5b5b72529296', 'Linux 查看PID资源使用情况', 1, 'LowRisk', '查看 Linux PID资源使用情况', 'use_num=$(ps -eLf | wc -l)\nsys_num=$(cat /proc/sys/kernel/pid_max)\nif [[ $use_num -gt $sys_num ]];then\n	echo \"PID 耗尽，将会影响程序正常运行\"\nelse\n  	echo \"PID 资源正常\"\nfi', '[]', concat(unix_timestamp(now()), '001'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('15370452-3ce3-429c-8743-4a344baddb26', 'Linux 查看系统密码复杂度要求是否设置', 1, 'LowRisk', '查看 Linux 系统密码复杂度要求是否设置', 'grep -i \"^password.*requisite.*pam_cracklib.so\" /etc/pam.d/system-auth ]]> /dev/null\n\nif [ $? == 0 ];then\n\necho \">>>密码复杂度:已设置\"\n\nelse\ngrep -i \"pam_pwquality\\.so\" /etc/pam.d/system-auth ]]> /dev/null\n\nif [ $? == 0 ];then\n\necho \">>>密码复杂度:已设置\"\n\nelse\necho \">>>密码复杂度:未设置,请加固密码--------[需调整]\"\n\nfi\nfi', '[]', concat(unix_timestamp(now()), '001'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('a1c9a8d3-97d2-4556-abdb-5d77ea46bad5', 'Linux 查看CPU消耗最多的进程', 1, 'MediumRisk', '查看 Linux 系统 CPU消耗最多的进程', 'ps auxf |sort -nr -k 3 |head -5', '[]', concat(unix_timestamp(now()), '001'), 1);
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`) VALUES ('5ca75854-b03c-4e9f-9ec7-4bf77f16d94d', 'Linux 查看 SSH暴力登入信息', 1, 'HighRisk', '查看 Linux 操作系统是否存在 SSH 暴力登入信息', 'echo \"ssh暴力登入信息:\"\n\nmore /var/log/secure | grep \"Failed\" ]]> /dev/null\n\nif [ $? == 1 ];then\n\necho \">>>无ssh暴力登入信息\"\n\nelse\nmore /var/log/secure|awk \'/Failed/{print $(NF-3)}\'|sort|uniq -c|awk \'{print \">>>登入失败的IP和尝试次数: \"$2\"=\"$1\"次---------[需调整]\";}\'\n\nfi', '[]', concat(unix_timestamp(now()), '001'), 1);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('c497a676-12ec-41ed-8944-eb32cc95b8a4', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('6f378375-bbe2-47f7-85fc-4a8e8c8193c6', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('9eacb3a6-6129-44b9-b29a-c07ad449b063', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('fdf46bee-0ae7-416f-85c8-b17c8dfa08ba', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('7f742d41-d91d-4597-8462-25a89f8841e0', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('ba903623-9d68-4939-ba39-fa34628c2d1d', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('f00222ed-4a39-43be-b2a8-5b5b72529296', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('15370452-3ce3-429c-8743-4a344baddb26', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('a1c9a8d3-97d2-4556-abdb-5d77ea46bad5', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('5ca75854-b03c-4e9f-9ec7-4bf77f16d94d', 'safety');

CREATE TABLE IF NOT EXISTS `package_dependency_json_item` (
    `id`                           int(11)             NOT NULL AUTO_INCREMENT,
    `result_id`                    varchar(50)         NOT NULL COMMENT 'Result ID',
    `severity`                     varchar(50)         DEFAULT NULL COMMENT 'severity',
    `name`                         varchar(50)         DEFAULT NULL COMMENT 'name',
    `description`                  text                DEFAULT NULL COMMENT 'description',
    `source`                       varchar(50)         DEFAULT NULL COMMENT 'source',
    `notes`                        text                DEFAULT NULL COMMENT 'notes',
    `cvssv3`                       mediumtext          DEFAULT NULL COMMENT 'cvssv3',
    `references`                   mediumtext          DEFAULT NULL COMMENT 'references',
    `cvssv2`                       mediumtext          DEFAULT NULL COMMENT 'cvssv2',
    `cwes`                         mediumtext          DEFAULT NULL COMMENT 'cwes',
    `vulnerable_software`          mediumtext          DEFAULT NULL COMMENT 'vulnerableSoftware',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;
