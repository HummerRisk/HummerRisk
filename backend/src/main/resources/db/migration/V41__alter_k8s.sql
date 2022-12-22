

ALTER TABLE `cloud_native` ADD `kubench_status` varchar(10) DEFAULT null COMMENT 'kube-bench状态';

ALTER TABLE `cloud_native_result` ADD `kube_bench` longtext DEFAULT null COMMENT 'kube bench';


