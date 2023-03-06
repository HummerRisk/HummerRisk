
ALTER TABLE `rule_group` ADD `type` varchar(64) DEFAULT 'cloud' COMMENT '规则组类别';

ALTER TABLE `server_rule` ADD `group_id` varchar(64) DEFAULT null COMMENT '规则组ID';

UPDATE `rule_group` SET `type` = 'cloud';
