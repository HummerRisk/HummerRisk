
ALTER TABLE `rule` ADD `xpack_tag` tinyint DEFAULT 0 COMMENT 'xpack 规则标志';

update `rule` set `xpack_tag` = 0;

ALTER TABLE `rule_group` ADD `xpack_tag` tinyint DEFAULT 0 COMMENT 'xpack 规则标志';

update `rule_group` set `xpack_tag` = 0;

ALTER TABLE `server_rule` ADD `xpack_tag` tinyint DEFAULT 0 COMMENT 'xpack 规则标志';

update `server_rule` set `xpack_tag` = 0;

update history_server_result set is_severity = 'true' where is_severity = '1';

update history_server_result set is_severity = 'false' where is_severity = '0';

delete from history_server_result where result_status = 'APPROVED';
