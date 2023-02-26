
ALTER TABLE package_result DROP return_log;

UPDATE rule_group_mapping SET group_id = '1' WHERE rule_id = '2274a926-ea5e-4cdc-915e-09fa6d803bff';
