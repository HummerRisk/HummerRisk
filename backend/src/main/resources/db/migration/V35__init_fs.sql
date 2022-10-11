-- ----------------------------
-- init fs
-- ----------------------------

ALTER TABLE `history_server_task` RENAME TO `history_server_result`;

ALTER TABLE `history_server_task_log` RENAME TO `history_server_result_log`;

ALTER TABLE `history_image_task` RENAME TO `history_image_result`;

ALTER TABLE `history_image_task_log` RENAME TO `history_image_result_log`;

ALTER TABLE `image_trivy_json` RENAME TO `image_result_item`;
