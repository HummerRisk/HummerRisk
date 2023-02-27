
ALTER TABLE server ADD is_public_key tinyint    DEFAULT 0 COMMENT '是否启用密钥';
ALTER TABLE server ADD public_key mediumtext DEFAULT NULL COMMENT '密钥';
