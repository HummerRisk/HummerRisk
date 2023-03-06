
ALTER TABLE `rule_group` ADD `type` varchar(64) DEFAULT 'cloud' COMMENT '规则组类别';

ALTER TABLE `rule_group_mapping` ADD `type` varchar(64) DEFAULT 'cloud' COMMENT '规则组类别';

UPDATE `rule_group` SET `type` = 'cloud';
