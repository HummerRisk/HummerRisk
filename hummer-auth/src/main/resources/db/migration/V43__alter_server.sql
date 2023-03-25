
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`, `type`) VALUES ('842b70d6-255d-46f3-a6da-58234f6df63f', 'Linux 检查是否禁止 root 登录 VSFTP', 1, 'HighRisk', '检查 Linux 系统中是否禁止 root 登录 VSFTP', 'if (/etc/init.d/vsftpd status 2>/dev/null | grep \"running\");then \n  if [ -s /etc/ftpusers ];then \n    ret=`cat /etc/ftpusers | grep root`\n  elif [ -s /etc/vsftpd/ftpusers ]; then \n    ret=`cat /etc/vsftpd/ftpusers | grep root`\n  fi\n  if [ -n \"$ret\" ];then \n  echo \"HummerSuccess: FTP 服务检测正常，未存在 root 用户\"\n  else \n    echo \"HummerError: FTP 服务检测异常，配置文件允许 root 用户访问FTP，易造成系统隐患\"\n  fi\nelse \n    echo \"HummerSuccess: FTP 服务检测正常，未安装 FTP 服务\"\nfi', '[]', concat(unix_timestamp(now()), '001'), 1, 'linux');
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`, `type`) VALUES ('fdf1c113-d1dc-4aea-877d-ea0001939c3c', 'Linux 检测是否存在FTP匿名用户', 1, 'HighRisk', '检测 Linux 系统中是否存在FTP匿名用户', 'if (/etc/init.d/vsftpd status 2>/dev/null | grep \"running\");then \n  if [ -s /etc/vsftpd.conf ];then \n    ret=`cat /etc/vsftpd.conf | grep -v \"^[[:space:]]*#\" | grep \"anonymous_enable=NO\"`\n  elif [ -s /etc/vsftpd/vsftpd.conf ]; then \n    ret=`cat /etc/vsftpd/vsftpd.conf | grep -v \"^[[:space:]]*#\" | grep \"anonymous_enable=NO\"`\n  fi;\n  if [ -n \"$ret\" ];then \n    echo \"HummerSuccess: FTP 服务检测正常，未存在匿名用户\"\n  else \n    echo \"HummerError: FTP 服务检测异常，未存在匿名用户${ret}\"\n  fi\nelse \n    echo \"HummerSuccess: FTP 服务检测正常，未安装 FTP 服务\"\nfi', '[]', concat(unix_timestamp(now()), '001'), 1, 'linux');
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`, `type`) VALUES ('27558ffd-a826-4b5c-bc4d-d725ec8dad58', 'Linux 查看是否开启 telnet 服务端口', 1, 'HighRisk', '查看 Linux 系统中是否开启 telnet 服务端口', 'if [[ `netstat -an | grep \":23\\b\"` == \"\" ]];then\n echo \"HummerSuccess: 未发现 Telnet 服务端口，符合要求\"\nelse\n echo \"HummerError: 发现开启 Telnet 服务端口，建议关闭\" \nfi', '[]', concat(unix_timestamp(now()), '001'), 1, 'linux');
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`, `type`) VALUES ('f7f08183-3972-4e8e-ba54-9e0cf64074d5', 'Linux 检查是否配置关闭IP伪装', 1, 'HighRisk', '检查 Linux 系统中是否配置关闭IP伪装', 'if [[ `cat /etc/host.conf |grep \"nospoof\"|awk \'{print $2}\'` == \"on\" ]];then\n  echo \"HummerSuccess: 已关闭 IP 伪装，符合要求\"\nelse \n  echo \"HummerSuccess: 未关闭 IP 伪装，存在风险\"\nfi', '[]', concat(unix_timestamp(now()), '001'), 1, 'linux');
INSERT INTO `server_rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `last_modified`, `flag`, `type`) VALUES ('e95c36e7-b968-4606-8217-a954a3ace2f5', 'Linux 查看是否有空密码账户', 1, 'HighRisk', '查看 Linux 系统中是否有空密码账户', 'users=`cat /etc/shadow | awk \'BEGIN{FS=\":\";ORS=\",\"}{if($2==\"\")print $1};\' | more`\nif [[ ${users} == \"\" ]];then\n echo \"HummerSuccess: 未发现配置为空密码的用户，符合要求\"\nelse\n echo \"HummerError: 发现存在空密码账户${users}，建议关闭\" \nfi', '[]', concat(unix_timestamp(now()), '001'), 1, 'linux');

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('e95c36e7-b968-4606-8217-a954a3ace2f5', 'server');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('f7f08183-3972-4e8e-ba54-9e0cf64074d5', 'server');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('27558ffd-a826-4b5c-bc4d-d725ec8dad58', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('fdf1c113-d1dc-4aea-877d-ea0001939c3c', 'server');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('842b70d6-255d-46f3-a6da-58234f6df63f', 'server');
