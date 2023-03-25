
-- -----------------
-- image repo setting
-- -----------------

CREATE TABLE IF NOT EXISTS `image_repo_setting` (
    `id`                         varchar(50)        NOT NULL COMMENT 'ID',
    `repo_id`                    varchar(50)        DEFAULT NULL COMMENT '镜像仓库ID',
    `repo`                       varchar(512)       DEFAULT NULL COMMENT 'repository 仓库地址',
    `repo_old`                   varchar(512)       DEFAULT NULL COMMENT 'repository 旧仓库地址',
    `create_time`                bigint             DEFAULT NULL COMMENT '创建时间',
    `update_time`                bigint             DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

-- -----------------
-- k8s rbac
-- -----------------

CREATE TABLE IF NOT EXISTS `cloud_native_source_rbac_node` (
    `id`                         varchar(50)        NOT NULL COMMENT 'ID',
    `k8s_id`                     varchar(50)        DEFAULT NULL COMMENT 'sourceID',
    `name`                       varchar(512)       DEFAULT NULL COMMENT 'name',
    `namespace`                  varchar(512)       DEFAULT NULL COMMENT 'namespace',
    `value`                      int             DEFAULT NULL COMMENT 'value',
    `symbol`                     varchar(512)       DEFAULT NULL COMMENT 'symbol(本地图片引用地址)',
    `symbolSize`                 int             DEFAULT NULL COMMENT 'symbolSize',
    `order`                      bigint             DEFAULT NULL COMMENT 'order',
    `category`                   varchar(128)       DEFAULT NULL COMMENT 'category',
    `create_time`                bigint             DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `cloud_native_source_rbac_link` (
    `id`                         varchar(50)        NOT NULL COMMENT 'ID',
    `k8s_id`                     varchar(50)        DEFAULT NULL COMMENT 'sourceID',
    `source`                     varchar(50)        DEFAULT NULL COMMENT 'source',
    `target`                     varchar(50)        DEFAULT NULL COMMENT 'target',
    `create_time`                bigint             DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `cloud_native_source_rbac_relation` (
    `id`                         varchar(50)        NOT NULL COMMENT 'ID',
    `k8s_id`                     varchar(50)        DEFAULT NULL COMMENT 'sourceId',
    `link_id`                    varchar(50)        DEFAULT NULL COMMENT 'linkId',
    `name`                       varchar(512)       DEFAULT NULL COMMENT 'name',
    `create_time`                bigint             DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;


-- -----------------
-- server rule
-- -----------------

INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`, `type`) VALUES ('c0f065e4-f205-4b1d-9d1d-a037f9e31e9d', 'Linux 系统中隐藏异常文件检测', 1, 'HighRisk', '检测 Linux 系统中是否存在隐藏异常文件', 'hideFile=$(find / -xdev -mount \\( -name \"..*\" -o -name \"...*\" \\) 2> /dev/null)\nif [  -z \"${hideFile}\" ];then\n  echo \"HummerSuccess:不存在隐藏文件，符合要求\"\nelse\n  echo \"HummerError:${hideFile}是隐藏文件，建议审视\" \nfi', '[]', concat(unix_timestamp(now()), '001'), 1, 'linux');
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`, `type`) VALUES ('29bac3a8-6cde-49a8-968e-45745d917a08', 'Linux 系统中非 root 账户 uid 检测', 1, 'LowRisk', '查看 Linux 系统中非 root 账户 uid 为 0 的账户', 'UIDS=`awk -F[:] \'NR!=1{print $3}\' /etc/passwd`\nflag=0\nfor i in $UIDS\n\ndo\nif [ $i = 0 ];then\n\n  echo \"HummerError:存在非root账号的账号UID为0，不符合要求\"\n\nelse\n flag=1\nfi\ndone\nif [ $flag = 1 ];then\n\n  echo \"HummerSuccess:不存在非root账号的账号UID为0，符合要求\"\n\nfi', '[]', concat(unix_timestamp(now()), '001'), 1, 'linux');
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`, `type`) VALUES ('a9c11f7e-ba44-416e-bbfa-cd4d96f8165f', 'Linux 账户配置自动注销时间检测', 1, 'LowRisk', '检测 Linux 账户是否配置自动注销时间', 'checkTimeout=$(cat /etc/profile | grep TMOUT | awk -F[=] \'{print $2}\')\nif [ $? -eq 0 ];then\n  TMOUT=`cat /etc/profile | grep TMOUT | awk -F[=] \'{print $2}\'`\n\n  if [[ $checkTimeout -le 600 ]] && [[  $checkTimeout -ge 10 ]];then\n    echo \"HummerSuccess: 账号超时时间${TMOUT}秒,符合要求\"\n  else\n    echo \"HummerError:账号超时时间${TMOUT}秒,不符合要求,建议设置小于 600 秒\"\n  fi\nelse\n  echo \"HummerError:账号超时不存在自动注销,不符合要求,建议设置小于 600 秒\"\nfi', '[]', concat(unix_timestamp(now()), '001'), 1, 'linux');
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`, `type`) VALUES ('e0ba707c-4a08-490e-93fb-1e98b1db0b3b', 'Linux resolve 中 DNS 服务器检测', 1, 'MediumRisk', '检测 Linux resolve 中 DNS 服务器是否正确', 'function check_ip() {\n  IP=$1\n  VALID_CHECK=$(echo \"$IP\"|awk -F. \'$1<=255&&$2<=255&&$3<=255&&$4<=255{print \"yes\"}\')\n  if echo \"$IP\"|grep -E \"^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$\" > /dev/null;then\n    if [ ${VALID_CHECK:-no} == \"yes\" ]; then\n      echo \"HummerSuccess: DNS 服务器 $IP 有效\"\n    else\n      echo \"HummerError: DNS 服务器 $IP 是个无效IP\"\n    fi\n  else\n    echo \"HummerError: 无效IP $IP  !\"\n  fi\n}\n\nfor ip in `cat /etc/resolv.conf 2>/dev/null| grep -E -o \"([0-9]{1,3}[\\.]){3}[0-9]{1,3}\"`\ndo\ncheck_ip $ip\ndone', '[]', concat(unix_timestamp(now()), '001'), 1, 'linux');
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`, `type`) VALUES ('c43ab09e-0c6c-4b2e-866d-3426477a6f3c', 'Linux hosts 记录中服务器地址检测', 1, 'LowRisk', '检测 Linux hosts 记录中服务器地址是否正确', 'function check_ip() {\n  IP=$1\n  VALID_CHECK=$(echo \"$IP\"|awk -F. \'$1<=255&&$2<=255&&$3<=255&&$4<=255{print \"yes\"}\')\n  if echo \"$IP\"|grep -E \"^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$\" > /dev/null;then\n    if [ ${VALID_CHECK:-no} == \"yes\" ]; then\n      echo \"HummerSuccess: hosts 服务器 $IP 有效\"\n    else\n      echo \"HummerError: hosts 服务器 $IP 是个无效IP\"\n    fi\n  else\n    echo \"HummerError: 无效IP $IP  !\"\n  fi\n}\n\nfor ip in `cat /etc/hosts|awk \'{print $1}\'|grep -E \"^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$\"`\ndo\ncheck_ip $ip\ndone', '[]', concat(unix_timestamp(now()), '001'), 1, 'linux');

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('c43ab09e-0c6c-4b2e-866d-3426477a6f3c', 'server');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('e0ba707c-4a08-490e-93fb-1e98b1db0b3b', 'server');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('a9c11f7e-ba44-416e-bbfa-cd4d96f8165f', 'server');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('29bac3a8-6cde-49a8-968e-45745d917a08', 'server');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('c0f065e4-f205-4b1d-9d1d-a037f9e31e9d', 'server');

-- -----------------
-- JDCloud rule
-- -----------------
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('JDCloud OSS 合规基线', 'OSS 合规检查为您提供全方位的对象存储资源检查功能。', '对象存储', 'hummer-jdcloud-plugin', 1);
SELECT @groupIdJdOss := LAST_INSERT_ID();

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('QingCloud OSS 合规基线', 'OSS 合规检查为您提供全方位的对象存储资源检查功能。', '对象存储', 'hummer-qingcloud-plugin', 1);
SELECT @groupIdQingOss := LAST_INSERT_ID();

INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('qiniu OSS 合规基线', 'OSS 合规检查为您提供全方位的对象存储资源检查功能。', '对象存储', 'hummer-qiniu-plugin', 1);
SELECT @groupIdQiniuOss := LAST_INSERT_ID();

SELECT id INTO @groupIdJd FROM rule_group WHERE name = 'JDCloud 等保预检';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('be1eddc5-9905-4280-8ae1-b841c68ce483', @groupIdQingOss);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1268870b-188d-41a8-a86e-0c83299dc93c', @groupIdQiniuOss);

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`, `suggestion`) VALUES ('290cb102-7ce8-471e-8540-8c4e55da259b', 'JDCloud OSS 公开读取访问权限检测', 1, 'HighRisk', 'JDCloud 查看您的 存储桶是否不允许公开访问权限。如果某个存储桶策略允许公开访问权限，则该存储桶“不合规“', 'policies:\n    - name: jdcloud-oss-private\n      resource: jdcloud.oss\n      filters:\n        - type: private', '[]', 'hummer-jdcloud-plugin', '京东云', 'jdcloud.png', concat(unix_timestamp(now()), '002'), 1, 'custodian', NULL);

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('290cb102-7ce8-471e-8540-8c4e55da259b', @groupIdJd);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('290cb102-7ce8-471e-8540-8c4e55da259b', @groupIdJdOss);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('290cb102-7ce8-471e-8540-8c4e55da259b', 'safety');

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('b553c99b-eac5-413f-8e1c-f058590c58cc', '290cb102-7ce8-471e-8540-8c4e55da259b', 'jdcloud.oss');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('290cb102-7ce8-471e-8540-8c4e55da259b', '10');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('290cb102-7ce8-471e-8540-8c4e55da259b', '13');
