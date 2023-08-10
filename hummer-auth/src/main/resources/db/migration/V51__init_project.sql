
Alter table `cloud_resource_rela` modify COLUMN `hummer_id` varchar(1024) DEFAULT NULL COMMENT '资源ID';

Alter table `resource_item` modify COLUMN `hummer_id` varchar(1024) DEFAULT NULL COMMENT '资源ID';

Alter table `cloud_resource_item` modify COLUMN `hummer_id` varchar(1024) DEFAULT NULL COMMENT '资源ID';

ALTER TABLE `cloud_resource_item` modify COLUMN `hummer_name` varchar(1024) DEFAULT NULL COMMENT '资源别名';

ALTER TABLE `resource_item` modify COLUMN `hummer_name` varchar(1024) DEFAULT NULL COMMENT '资源别名';