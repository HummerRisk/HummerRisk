-- ----------------------------
-- Table structure for webmsg
-- ----------------------------
DROP TABLE IF EXISTS `web_msg`;
CREATE TABLE `web_msg` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '消息主键',
  `user_id` varchar(50) DEFAULT NULL COMMENT '用户ID',
  `type` varchar(50) DEFAULT NULL COMMENT '类型',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态',
  `create_time` bigint(13) DEFAULT NULL COMMENT '发送时间',
  `read_time` bigint(13) DEFAULT NULL COMMENT '读取时间',
  `content` varchar(512) DEFAULT NULL COMMENT '消息内容',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `inx_msg_status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='消息通知表';


