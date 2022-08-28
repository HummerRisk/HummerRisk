ALTER TABLE cloud_event ADD `user_name` varchar(255) DEFAULT '' COMMENT '用户名';
ALTER TABLE cloud_event MODIFY `cloud_account_id` varchar(64);