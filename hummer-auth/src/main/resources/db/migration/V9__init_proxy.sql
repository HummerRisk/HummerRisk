CREATE TABLE IF NOT EXISTS proxy
(
    id                           int(11)             NOT NULL AUTO_INCREMENT,
    proxy_type                   varchar(50)         DEFAULT NULL COMMENT 'Proxy 类型',
    proxy_ip                     varchar(50)         DEFAULT NULL COMMENT 'Proxy IP',
    proxy_port                   varchar(50)         DEFAULT NULL COMMENT 'Proxy端口',
    proxy_name                   varchar(50)         DEFAULT NULL COMMENT 'Proxy名称',
    proxy_password               varchar(50)         DEFAULT NULL COMMENT 'Proxy密码',
    create_time                  bigint(13)          DEFAULT NULL COMMENT '创建时间',
    update_time                  bigint(13)          DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY ( id )
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

