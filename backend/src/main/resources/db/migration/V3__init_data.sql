INSERT INTO `user` ( id, name, email, password, status, create_time, update_time, language, last_account_id, phone, source )
VALUES ( 'admin', '管理员', 'admin@hummercloud.com', md5('hummer'), '1', unix_timestamp() * 1000, unix_timestamp() * 1000, NULL, NULL, NULL, 'LOCAL' );

INSERT INTO user_role ( id, user_id, role_id, source_id, create_time, update_time )
VALUES ( uuid(), 'admin', 'admin', '1', unix_timestamp() * 1000, unix_timestamp() * 1000 );

INSERT INTO role ( id, name, description, type, create_time, update_time )
VALUES ( 'admin', '系统管理员', NULL, NULL, unix_timestamp() * 1000, unix_timestamp() * 1000 );
