CREATE TABLE `cloud_event_region_log` (
      `id` int(8) NOT NULL AUTO_INCREMENT COMMENT 'id',
      `log_id` int(8) DEFAULT NULL COMMENT '同步日志ID',
      `region` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '区域',
      `data_count` int(6) DEFAULT NULL COMMENT '同步数据量',
      `status` int(1) DEFAULT NULL COMMENT '状态：0 同步中，1成功，2失败',
      `start_time` datetime DEFAULT NULL COMMENT '日志实际开始时间',
      `end_time` datetime DEFAULT NULL COMMENT '日志实际结束时间',
      `create_time` datetime DEFAULT NULL COMMENT '创建时间',
      `exception` varchar(1024) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '异常信息',
      PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4;

ALTER TABLE cloud_event_sync_log MODIFY column region varchar(255) DEFAULT NULL COMMENT '区域';
ALTER TABLE cloud_event_sync_log MODIFY column `status` int(1) DEFAULT NULL COMMENT '状态：0 同步中，1成功，2失败，3告警';