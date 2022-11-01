
ALTER TABLE `cloud_native_source` ADD `source_node` varchar(256) DEFAULT NULL COMMENT '资源节点';

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('0487f316-9cd8-46f7-86e9-31f1ab8b504a', 'server');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('0f91f89e-d39b-4a6e-8311-1070ae2bc85c', 'server');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('98122047-afd0-4b12-8bfd-7a13ee177f72', 'server');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('6970cdce-fd54-4853-9f89-9e70b43aa07e', 'server');
