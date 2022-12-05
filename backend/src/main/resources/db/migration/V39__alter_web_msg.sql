

ALTER TABLE `web_msg` ADD `result_id` longtext DEFAULT NULL COMMENT '检测结果ID';

UPDATE `rule` SET `script` = 'policies:\n    - name: qiniu-bucket-private\n      resource: qiniu.kodo\n      filters:\n        - type: private\n          value: ${{value}}' WHERE id = '1268870b-188d-41a8-a86e-0c83299dc93c';

UPDATE `rule_type` SET `resource_type` = 'qiniu.kodo' WHERE id = 'c0cd80bf-259f-4a46-a1a8-9ce7edc5e592';

