

ALTER TABLE `cloud_native` ADD `kubench_status` varchar(10) DEFAULT null COMMENT 'kube-bench状态';

ALTER TABLE `cloud_native_result` ADD `kube_bench` longtext DEFAULT null COMMENT 'kube bench';

ALTER TABLE `history_cloud_native_result` ADD `kube_bench` longtext DEFAULT null COMMENT 'kube bench';

CREATE TABLE IF NOT EXISTS `cloud_native_result_kubench`
(
    `id`                         varchar(50)         NOT NULL,
    `result_id`                  varchar(50)         DEFAULT NULL COMMENT '云原生检测结果ID',
    `title`                      longtext            DEFAULT NULL COMMENT 'title',
    `number`                     varchar(50)         DEFAULT NULL COMMENT 'number',
    `severity`                   varchar(128)        DEFAULT NULL COMMENT 'severity',
    `create_time`                bigint(13)          DEFAULT NULL COMMENT '创建时间',
    `description`                longtext            DEFAULT NULL COMMENT 'description',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`, `type`) VALUES ('40dc9a85-7063-4192-8ba3-bce63f49f0f4', 'Linux 密码文件检测', 1, 'LowRisk', '查看 Linux 密码文件是否正常', 'passwdCommand=`ls -l /etc/passwd 2>/dev/null |awk \'{print $1}\'`\nshadowCommand=`ls -l /etc/passwd 2>/dev/null |awk \'{print $1}\'`\n\nif [[ ${passwdCommand} == \'-rw-r--r--\' ]];then\n echo \"HummerSuccess: passwd 文件权限正常！\"\nelse\n    echo \"HummerError: passwd 文件权限变更，不为-rw-r--r--，请及时处理！\"\nfi\nif [[ ${shadowCommand} == \'-rw-r--r--\' ]];then\n echo \"HummerSuccess: shadow 文件权限正常！\"\nelse\n    echo \"HummerError: shadow 文件权限变更，不为-rw-r--r--，请及时处理！\"\nfi', '[]', concat(unix_timestamp(now()), '001'), 1, 'linux');
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`, `type`) VALUES ('f9d4db6e-210c-4984-be55-6c2aa0622c8f', 'Linux 获取用户免密登录的公钥', 1, 'LowRisk', '查看 Linux 用户免密登录的公钥是否正常', 'sshAuth=`cat /$HOME/.ssh/authorized_keys |awk \'{print $3}\'`\nif [[ ${sshAuth} != \"\" ]];then\n   echo \"HummerError: 用户 $(whoami) 存在免密登录的证书，证书客户端名称: ${sshAuth}\"\nelse\n    echo \"HummerSuccess:，ssh 密钥检测正常！\"\nfi', '[]', concat(unix_timestamp(now()), '001'), 1, 'linux');
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`, `type`) VALUES ('78fe32cb-2307-496f-8946-a81d0359a452', 'Linux 特权组检测', 1, 'MediumRisk', '查看 Linux 特权组是否正常', 'users=`cat /etc/passwd | grep \'/bin/bash\' | awk -F: \'$4==0 {print $1}\' 2>/dev/null`\nif [[ -n ${users} ]];then \n   echo \"HummerError: 存在特权组用户 $users\"\nelse\n   echo \"HummerSuccess:，特权组用户检测正常！\"\nfi', '[]', concat(unix_timestamp(now()), '001'), 1, 'linux');
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`, `type`) VALUES ('1fc49851-3f00-49e5-9be2-6f4f24e37da4', 'Linux sudo 权限异常账户检测', 1, 'MediumRisk', '查看 Linux sudo 权限异常账户', 'users=`cat /etc/sudoers 2>/dev/null |grep -v \'#\'|grep \'ALL=(ALL)\'|awk \'{print $1}\'`\nif [[ -n ${users} ]];then\n echo \"HummerError: 用户 $users 可通过 sudo 命令获取特权!\"\nelse\n echo \"HummerSuccess: 特权用户 sudo 检测正常！\"\nfi', '[]', concat(unix_timestamp(now()), '001'), 1, 'linux');
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`, `type`) VALUES ('50876ef4-9b73-4f2d-8105-ef6658fea5cc', 'Linux 检测空口令', 1, 'LowRisk', '查看 Linux 空口令是否正常', 'users=`awk -F \":\" \'($2==\"\"){print $1}\' /etc/shadow`\nif [[ -n ${users} ]];then\n echo \"HummerError: 用户 $users 未设置密码!\"\nelse\n echo \"HummerSuccess: 用户密码检测正常！\"\nfi', '[]', concat(unix_timestamp(now()), '001'), 1, 'linux');

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('50876ef4-9b73-4f2d-8105-ef6658fea5cc', 'server');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('1fc49851-3f00-49e5-9be2-6f4f24e37da4', 'server');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('78fe32cb-2307-496f-8946-a81d0359a452', 'server');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('f9d4db6e-210c-4984-be55-6c2aa0622c8f', 'server');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('40dc9a85-7063-4192-8ba3-bce63f49f0f4', 'server');
