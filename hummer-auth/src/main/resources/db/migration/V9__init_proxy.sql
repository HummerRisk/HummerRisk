CREATE TABLE IF NOT EXISTS proxy
(
    id                           int                 NOT NULL AUTO_INCREMENT,
    proxy_type                   varchar(50)         DEFAULT NULL COMMENT 'Proxy 类型',
    proxy_ip                     varchar(50)         DEFAULT NULL COMMENT 'Proxy IP',
    proxy_port                   varchar(50)         DEFAULT NULL COMMENT 'Proxy端口',
    proxy_name                   varchar(50)         DEFAULT NULL COMMENT 'Proxy名称',
    proxy_password               varchar(50)         DEFAULT NULL COMMENT 'Proxy密码',
    create_time                  bigint              DEFAULT NULL COMMENT '创建时间',
    update_time                  bigint              DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY ( id )
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;


ALTER TABLE plugin ADD order_ bigint     DEFAULT 0 COMMENT '排序';

UPDATE plugin SET order_ = 1 WHERE id = 'hummer-aliyun-plugin';
UPDATE plugin SET order_ = 2 WHERE id = 'hummer-huawei-plugin';
UPDATE plugin SET order_ = 3 WHERE id = 'hummer-qcloud-plugin';
UPDATE plugin SET order_ = 4 WHERE id = 'hummer-aws-plugin';
UPDATE plugin SET order_ = 5 WHERE id = 'hummer-azure-plugin';
UPDATE plugin SET order_ = 6 WHERE id = 'hummer-gcp-plugin';
UPDATE plugin SET order_ = 7 WHERE id = 'hummer-vsphere-plugin';
UPDATE plugin SET order_ = 8 WHERE id = 'hummer-openstack-plugin';
UPDATE plugin SET order_ = 9 WHERE id = 'hummer-baidu-plugin';
UPDATE plugin SET order_ = 10 WHERE id = 'hummer-huoshan-plugin';
UPDATE plugin SET order_ = 11 WHERE id = 'hummer-qingcloud-plugin';
UPDATE plugin SET order_ = 12 WHERE id = 'hummer-ucloud-plugin';
UPDATE plugin SET order_ = 13 WHERE id = 'hummer-qiniu-plugin';
UPDATE plugin SET order_ = 14 WHERE id = 'hummer-k8s-plugin';

ALTER TABLE plugin ADD type varchar(32) DEFAULT 'cloud' COMMENT '插件类型';

UPDATE plugin SET type = 'cloud' WHERE id = 'hummer-aliyun-plugin';
UPDATE plugin SET type = 'cloud' WHERE id = 'hummer-huawei-plugin';
UPDATE plugin SET type = 'cloud' WHERE id = 'hummer-qcloud-plugin';
UPDATE plugin SET type = 'cloud' WHERE id = 'hummer-aws-plugin';
UPDATE plugin SET type = 'cloud' WHERE id = 'hummer-azure-plugin';
UPDATE plugin SET type = 'cloud' WHERE id = 'hummer-gcp-plugin';
UPDATE plugin SET type = 'cloud' WHERE id = 'hummer-vsphere-plugin';
UPDATE plugin SET type = 'cloud' WHERE id = 'hummer-openstack-plugin';
UPDATE plugin SET type = 'cloud' WHERE id = 'hummer-baidu-plugin';
UPDATE plugin SET type = 'cloud' WHERE id = 'hummer-huoshan-plugin';
UPDATE plugin SET type = 'cloud' WHERE id = 'hummer-qingcloud-plugin';
UPDATE plugin SET type = 'cloud' WHERE id = 'hummer-ucloud-plugin';
UPDATE plugin SET type = 'cloud' WHERE id = 'hummer-qiniu-plugin';
UPDATE plugin SET type = 'native' WHERE id = 'hummer-k8s-plugin';

INSERT INTO `plugin` (`id`, `name`, `icon`, `update_time`, `scan_type`, `order_`, `type`) VALUES ('hummer-rancher-plugin', 'Rancher', 'rancher.png', concat(unix_timestamp(now()), '001'), 'native', 18, 'native');
INSERT INTO `plugin` (`id`, `name`, `icon`, `update_time`, `scan_type`, `order_`, `type`) VALUES ('hummer-openshift-plugin', 'OpenShift', 'openshift.png', concat(unix_timestamp(now()), '001'), 'native', 19, 'native');
INSERT INTO `plugin` (`id`, `name`, `icon`, `update_time`, `scan_type`, `order_`, `type`) VALUES ('hummer-kubesphere-plugin', 'KubeSphere', 'kubesphere.png', concat(unix_timestamp(now()), '001'), 'native', 20, 'native');


