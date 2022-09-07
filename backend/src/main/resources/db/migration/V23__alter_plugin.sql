
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
UPDATE plugin SET type = 'vuln' WHERE id = 'hummer-xray-plugin';
UPDATE plugin SET type = 'vuln' WHERE id = 'hummer-nuclei-plugin';
UPDATE plugin SET type = 'vuln' WHERE id = 'hummer-tsunami-plugin';
UPDATE plugin SET type = 'native' WHERE id = 'hummer-k8s-plugin';

INSERT INTO `plugin` (`id`, `name`, `icon`, `update_time`, `scan_type`, `order_`, `type`) VALUES ('hummer-rancher-plugin', 'Rancher', 'rancher.png', concat(unix_timestamp(now()), '001'), 'native', 18, 'native');
INSERT INTO `plugin` (`id`, `name`, `icon`, `update_time`, `scan_type`, `order_`, `type`) VALUES ('hummer-openshift-plugin', 'OpenShift', 'openshift.png', concat(unix_timestamp(now()), '001'), 'native', 19, 'native');
INSERT INTO `plugin` (`id`, `name`, `icon`, `update_time`, `scan_type`, `order_`, `type`) VALUES ('hummer-kubesphere-plugin', 'KubeSphere', 'kubesphere.png', concat(unix_timestamp(now()), '001'), 'native', 20, 'native');
