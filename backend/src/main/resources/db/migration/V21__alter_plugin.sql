
ALTER TABLE plugin ADD order_ bigint(13) DEFAULT 0 COMMENT '排序';

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
UPDATE plugin SET order_ = 15 WHERE id = 'hummer-xray-plugin';
UPDATE plugin SET order_ = 16 WHERE id = 'hummer-nuclei-plugin';
UPDATE plugin SET order_ = 17 WHERE id = 'hummer-tsunami-plugin';
