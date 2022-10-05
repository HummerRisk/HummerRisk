-- ----------------------------
-- Huawei Rule
-- ----------------------------
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('776f0a66-25d4-4aee-bbdc-6f30a2c4652e', 'Huawei GaussDBForNoSQL实例计费类型检测', 1, 'HighRisk', 'Huawei 测您账号下GaussDBForNoSQL数据库实例计费类型信息，按需视为“合规”，否则视为“不合规”', 'policies:\n    #测您账号下GaussDBForNoSQL数据库实例计费类型信息，按需视为“合规”，否则视为“不合规”\n    - name: huawei-gaussdbfornosql-charge-info\n      resource: huawei.gaussdbfornosql\n      filters:\n        - type: charge-info\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"计费模式:“0”:按需计费/\\\"1\\\":包年包月。\",\"defaultValue\":\"\\\"0\\\"\",\"required\":true}]', 'hummer-huawei-plugin', '华为云', 'fusion.png', concat(unix_timestamp(now()), '001'), '1', 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('8e50d813-0d65-474c-951a-4abacbf381cc', 'Huawei GaussDBForNoSql实例公网访问检测', 1, 'HighRisk', 'Huawei 检测您账号下GaussDBForNoSql实例不允许任意来源公网访问，视为“合规”，否则属于“不合规”', 'policies:\n    # 检测您账号下GaussDBForNoSql实例不允许任意来源公网访问，视为“合规”\n    - name: huawei-gaussdbfornosql-internet-access\n      resource: huawei.gaussdbfornosql\n      filters:\n        - type: internet-access\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"不允许任意来源公网访问\",\"defaultValue\":\"true\",\"required\":true}]', 'hummer-huawei-plugin', '华为云', 'fusion.png', concat(unix_timestamp(now()), '001'), '1', 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('fcd416f0-e58c-417a-8795-a99748d6f4ce', 'Huawei GaussDBForNoSql实例磁盘空间检测', 1, 'HighRisk', 'Huawei 账号下GaussDBForNoSql实例磁盘空间是否已满，未满视为“合规”，否则属于“不合规”', 'policies:\n    # 账号下GaussDBForNoSql实例磁盘空间是否已满，未满视为“合规”，否则属于“不合规”。\n    - name: huawei-gaussdbfornosql-storagefull\n      resource: huawei.gaussdbfornosql\n      filters:\n        - type: storagefull', '[]', 'hummer-huawei-plugin', '华为云', 'fusion.png', concat(unix_timestamp(now()), '001'), '1', 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('4eed4751-16fc-4827-a3aa-12865b6c5c73', 'Huawei GaussDBForNoSql实例网络类型检测', 1, 'HighRisk', 'Huawei 账号下GaussDBForNoSql实例已关联到VPC；视为“合规”', 'policies:\n    # 账号下GaussDBForNoSql实例已关联到VPC；视为“合规”。\n    - name: huawei-gaussdbfornosql-instance-network-type\n      resource: huawei.gaussdbfornosql\n      filters:\n        - type: instance-network-type\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"网络类型\",\"defaultValue\":\"VPC\",\"required\":true}]', 'hummer-huawei-plugin', '华为云', 'fusion.png', concat(unix_timestamp(now()), '001'), '1', 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('77e50110-e477-4b24-a3c2-a9f5e7e41715', 'Huawei GaussDBForNoSql实例高可用状态检测', 1, 'HighRisk', 'Huawei 账号下GaussDBForNoSql实例具备高可用能力，视为“合规”，否则属于“不合规”', 'policies:\n    # 账号下GaussDBForNoSql实例具备高可用能力，视为“合规”，否则属于“不合规”。\n    - name: huawei-gaussdbfornosql-high-availability\n      resource: huawei.gaussdbfornosql\n      filters:\n        - type: high-availability', '[]', 'hummer-huawei-plugin', '华为云', 'fusion.png', concat(unix_timestamp(now()), '001'), '1', 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('8048b411-967a-47b3-b4f1-d7d7880eb79c', 'Huawei GaussDBForOpenGauss实例公网访问检测', 1, 'HighRisk', 'Huawei 检测您账号下GaussDBForOpenGauss实例不允许任意来源公网访问，视为“合规”，否则属于“不合规”', 'policies:\n    # 检测您账号下GaussDBForOpenGauss实例不允许任意来源公网访问，视为“合规”\n    - name: huawei-gaussdbforopengauss-internet-access\n      resource: huawei.gaussdbforopengauss\n      filters:\n        - type: internet-access\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"不允许任意来源公网访问\",\"defaultValue\":\"true\",\"required\":true}]', 'hummer-huawei-plugin', '华为云', 'fusion.png', concat(unix_timestamp(now()), '001'), '1', 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('86415c99-3215-4f11-a62b-fa4c7b2ac298', 'Huawei GaussDBForOpenGauss实例磁盘空间检测', 1, 'HighRisk', 'Huawei 账号下GaussDBForOpenGauss实例磁盘空间是否已满，未满视为“合规”，否则属于“不合规”', 'policies:\n    # 账号下GaussDBForOpenGauss实例磁盘空间是否已满，未满视为“合规”，否则属于“不合规”。\n    - name: huawei-gaussdbforopengauss-storagefull\n      resource: huawei.gaussdbforopengauss\n      filters:\n        - type: storagefull', '[]', 'hummer-huawei-plugin', '华为云', 'fusion.png', concat(unix_timestamp(now()), '001'), '1', 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('1abf0c53-b4ac-4b45-b528-8f47b8d7d533', 'Huawei GaussDBForOpenGauss实例网络类型检测', 1, 'HighRisk', 'Huawei 账号下GaussDBForOpenGauss实例已关联到VPC；视为“合规”', 'policies:\n    # 账号下GaussDBForOpenGauss实例已关联到；视为“合规”。\n    - name: huawei-gaussdbforopengauss-instance-network-type\n      resource: huawei.gaussdbforopengauss\n      filters:\n        - type: instance-network-type\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"网络类型\",\"defaultValue\":\"VPC\",\"required\":true}]', 'hummer-huawei-plugin', '华为云', 'fusion.png', concat(unix_timestamp(now()), '001'), '1', 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('f393ba0b-23ca-435b-b922-62c77a9437f7', 'Huawei GaussDBForOpenGauss实例计费类型检测', 1, 'HighRisk', 'Huawei 测您账号下GaussDBForOpenGauss数据库实例计费类型信息，按需视为“合规”，否则视为“不合规”', 'policies:\n    #测您账号下GaussDBForOpenGauss数据库实例计费类型信息，按需视为“合规”，否则视为“不合规”\n    - name: huawei-gaussdbforopengauss-charge-info\n      resource: huawei.gaussdbforopengauss\n      filters:\n        - type: charge-info\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"计费模式:prePaid包年包月/postPaid按需\",\"defaultValue\":\"postPaid\",\"required\":true}]', 'hummer-huawei-plugin', '华为云', 'fusion.png', concat(unix_timestamp(now()), '001'), '1', 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('86254202-388c-4c55-bba2-a3053c16fe3b', 'Huawei GaussDBForOpenGauss实例高可用状态检测', 1, 'HighRisk', 'Huawei GaussDBForOpenGauss实例高可用状态检测', 'policies:\n    # 账号下GaussDBForOpenGauss实例具备高可用能力，视为“合规”，否则属于“不合规”。\n    - name: huawei-gaussdbforopengauss-high-availability\n      resource: huawei.gaussdbforopengauss\n      filters:\n        - type: high-availability', '[]', 'hummer-huawei-plugin', '华为云', 'fusion.png', concat(unix_timestamp(now()), '001'), '1', 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('cc1f97e7-73c5-4f4f-91e9-1155d47ed24e', 'Huawei GaussDB实例公网访问检测', 1, 'HighRisk', 'Huawei 检测您账号下GaussDB实例不允许任意来源公网访问，视为“合规”，否则属于“不合规”', 'policies:\n    # 检测您账号下GaussDB实例不允许任意来源公网访问，视为“合规”\n    - name: huawei-gaussdb-internet-access\n      resource: huawei.gaussdb\n      filters:\n        - type: internet-access\n          value: ${{value}}', '[{\"defaultValue\":\"true\",\"name\":\"不允许任意来源公网访问\",\"key\":\"value\",\"required\":true}]', 'hummer-huawei-plugin', '华为云', 'fusion.png', concat(unix_timestamp(now()), '001'), '1', 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('c61ae072-e614-47d6-b0ee-07f50360032e', 'Huawei GaussDB实例磁盘空间检测', 1, 'HighRisk', 'Huawei 账号下GaussDB实例磁盘空间是否已满，未满视为“合规”，否则属于“不合规”', 'policies:\n    # 账号下GaussDB实例磁盘空间是否已满，未满视为“合规”，否则属于“不合规”。\n    - name: huawei-gaussdb-storagefull\n      resource: huawei.gaussdb\n      filters:\n        - type: storagefull', '[]', 'hummer-huawei-plugin', '华为云', 'fusion.png', concat(unix_timestamp(now()), '001'), '1', 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('fa800f05-b7ac-4009-86f9-413d26bbc368', 'Huawei GaussDB实例网络类型检测', 1, 'HighRisk', 'Huawei 账号下GaussDB实例已关联到VPC；视为“合规”', 'policies:\n    # 账号下GaussDB实例已关联到；视为“合规”。\n    - name: huawei-gaussdb-instance-network-type\n      resource: huawei.gaussdb\n      filters:\n        - type: instance-network-type\n          value: ${{value}}', '[{\"defaultValue\":\"VPC\",\"name\":\"实例的网络类型\",\"key\":\"value\",\"required\":true}]', 'hummer-huawei-plugin', '华为云', 'fusion.png', concat(unix_timestamp(now()), '001'), '1', 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('e1b193de-9b9f-41de-89d5-62a731d2f212', 'Huawei GaussDB实例计费类型检测', 1, 'HighRisk', 'Huawei 测您账号下GaussDB数据库实例计费类型信息，按需视为“合规”，否则视为“不合规”', 'policies:\n    #测您账号下GaussDB数据库实例计费类型信息，按需视为“合规”，否则视为“不合规”\n    - name: huawei-gaussdb-charge-info\n      resource: huawei.gaussdb\n      filters:\n        - type: charge-info\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"计费模式:prePaid包年包月/postPaid按需\",\"defaultValue\":\"postPaid\",\"required\":true}]', 'hummer-huawei-plugin', '华为云', 'fusion.png', concat(unix_timestamp(now()), '001'), '1', 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('b4620c46-795f-4d13-9139-2055849ec7b9', 'Huawei GaussDB实例高可用状态检测', 1, 'HighRisk', 'Huawei 账号下GaussDB实例具备高可用能力，视为“合规”，否则属于“不合规”', 'policies:\n    # 账号下GaussDB实例具备高可用能力，视为“合规”，否则属于“不合规”。\n    - name: huawei-gaussdb-high-availability\n      resource: huawei.gaussdb\n      filters:\n        - type: high-availability', '[]', 'hummer-huawei-plugin', '华为云', 'fusion.png', concat(unix_timestamp(now()), '001'), '1', 'custodian');


INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('b4620c46-795f-4d13-9139-2055849ec7b9', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('e1b193de-9b9f-41de-89d5-62a731d2f212', 'cost');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('c61ae072-e614-47d6-b0ee-07f50360032e', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('77e50110-e477-4b24-a3c2-a9f5e7e41715', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('776f0a66-25d4-4aee-bbdc-6f30a2c4652e', 'cost');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('8e50d813-0d65-474c-951a-4abacbf381cc', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('fa800f05-b7ac-4009-86f9-413d26bbc368', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('4eed4751-16fc-4827-a3aa-12865b6c5c73', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('fcd416f0-e58c-417a-8795-a99748d6f4ce', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('1abf0c53-b4ac-4b45-b528-8f47b8d7d533', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('8048b411-967a-47b3-b4f1-d7d7880eb79c', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('cc1f97e7-73c5-4f4f-91e9-1155d47ed24e', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('86415c99-3215-4f11-a62b-fa4c7b2ac298', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('f393ba0b-23ca-435b-b922-62c77a9437f7', 'cost');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('86254202-388c-4c55-bba2-a3053c16fe3b', 'safety');

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('021ec945-2d55-4985-a058-5af2e0a87ad2', '77e50110-e477-4b24-a3c2-a9f5e7e41715', 'huawei.gaussdbfornosql');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('0acaa24a-dc01-44df-9f65-9146ad46ae19', 'b4620c46-795f-4d13-9139-2055849ec7b9', 'huawei.gaussdb');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('0af32d0b-9145-4550-b296-846f2f565c09', 'cc1f97e7-73c5-4f4f-91e9-1155d47ed24e', 'huawei.gaussdb');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('0eeb2c9f-6c84-45a8-8430-af4c63a9121b', 'fa800f05-b7ac-4009-86f9-413d26bbc368', 'huawei.gaussdb');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('1f2c9982-0375-4359-a09f-a5d610a36b7d', '86415c99-3215-4f11-a62b-fa4c7b2ac298', 'huawei.gaussdbforopengauss');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('231e4274-2474-419f-9474-307aa0925cbc', 'f393ba0b-23ca-435b-b922-62c77a9437f7', 'huawei.gaussdbforopengauss');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('55683b9e-2860-497d-b5ec-c1621779af8f', 'c61ae072-e614-47d6-b0ee-07f50360032e', 'huawei.gaussdb');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('58bab82a-368f-4f33-ad3a-37b12e3d28af', '776f0a66-25d4-4aee-bbdc-6f30a2c4652e', 'huawei.gaussdbfornosql');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('5c1ef7cb-8061-4012-a81b-0886de59feb3', '1abf0c53-b4ac-4b45-b528-8f47b8d7d533', 'huawei.gaussdbforopengauss');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('5eb37609-ca7f-4f0d-9d4c-6bc91aaa63cf', 'fcd416f0-e58c-417a-8795-a99748d6f4ce', 'huawei.gaussdbfornosql');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('85ebc57d-0462-4a9a-bb82-fe62eca21bef', '8048b411-967a-47b3-b4f1-d7d7880eb79c', 'huawei.gaussdbforopengauss');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('c1bd22ca-e072-469a-b692-399d9e883f6a', '86254202-388c-4c55-bba2-a3053c16fe3b', 'huawei.gaussdbforopengauss');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('e41df014-0566-44e4-ab66-a1f67e540c0e', 'e1b193de-9b9f-41de-89d5-62a731d2f212', 'huawei.gaussdb');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('efea088d-1e3e-4bd4-90bc-74af2c626043', '8e50d813-0d65-474c-951a-4abacbf381cc', 'huawei.gaussdbfornosql');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('f58632fe-969e-4e2a-8491-584641bdb2b4', '4eed4751-16fc-4827-a3aa-12865b6c5c73', 'huawei.gaussdbfornosql');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b4620c46-795f-4d13-9139-2055849ec7b9', '2');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b4620c46-795f-4d13-9139-2055849ec7b9', '56');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b4620c46-795f-4d13-9139-2055849ec7b9', '57');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b4620c46-795f-4d13-9139-2055849ec7b9', '58');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e1b193de-9b9f-41de-89d5-62a731d2f212', '48');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c61ae072-e614-47d6-b0ee-07f50360032e', '98');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('77e50110-e477-4b24-a3c2-a9f5e7e41715', '2');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('77e50110-e477-4b24-a3c2-a9f5e7e41715', '56');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('77e50110-e477-4b24-a3c2-a9f5e7e41715', '57');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('77e50110-e477-4b24-a3c2-a9f5e7e41715', '58');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('776f0a66-25d4-4aee-bbdc-6f30a2c4652e', '48');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('8e50d813-0d65-474c-951a-4abacbf381cc', '97');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('fa800f05-b7ac-4009-86f9-413d26bbc368', '3');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('fa800f05-b7ac-4009-86f9-413d26bbc368', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('fa800f05-b7ac-4009-86f9-413d26bbc368', '5');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('fa800f05-b7ac-4009-86f9-413d26bbc368', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('fa800f05-b7ac-4009-86f9-413d26bbc368', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4eed4751-16fc-4827-a3aa-12865b6c5c73', '3');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4eed4751-16fc-4827-a3aa-12865b6c5c73', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4eed4751-16fc-4827-a3aa-12865b6c5c73', '5');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4eed4751-16fc-4827-a3aa-12865b6c5c73', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('4eed4751-16fc-4827-a3aa-12865b6c5c73', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('fcd416f0-e58c-417a-8795-a99748d6f4ce', '98');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1abf0c53-b4ac-4b45-b528-8f47b8d7d533', '3');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1abf0c53-b4ac-4b45-b528-8f47b8d7d533', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1abf0c53-b4ac-4b45-b528-8f47b8d7d533', '5');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1abf0c53-b4ac-4b45-b528-8f47b8d7d533', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1abf0c53-b4ac-4b45-b528-8f47b8d7d533', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('8048b411-967a-47b3-b4f1-d7d7880eb79c', '97');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('cc1f97e7-73c5-4f4f-91e9-1155d47ed24e', '97');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('86415c99-3215-4f11-a62b-fa4c7b2ac298', '98');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f393ba0b-23ca-435b-b922-62c77a9437f7', '48');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('86254202-388c-4c55-bba2-a3053c16fe3b', '2');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('86254202-388c-4c55-bba2-a3053c16fe3b', '56');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('86254202-388c-4c55-bba2-a3053c16fe3b', '57');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('86254202-388c-4c55-bba2-a3053c16fe3b', '58');

SELECT id INTO @groupId1 FROM rule_group WHERE name = 'Huawei 等保预检';
SELECT id INTO @groupId2 FROM rule_group WHERE name = 'Huawei CIS合规检查';

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b4620c46-795f-4d13-9139-2055849ec7b9', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b4620c46-795f-4d13-9139-2055849ec7b9', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e1b193de-9b9f-41de-89d5-62a731d2f212', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c61ae072-e614-47d6-b0ee-07f50360032e', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('77e50110-e477-4b24-a3c2-a9f5e7e41715', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('77e50110-e477-4b24-a3c2-a9f5e7e41715', @groupId2);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('776f0a66-25d4-4aee-bbdc-6f30a2c4652e', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8e50d813-0d65-474c-951a-4abacbf381cc', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('fa800f05-b7ac-4009-86f9-413d26bbc368', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('4eed4751-16fc-4827-a3aa-12865b6c5c73', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('fcd416f0-e58c-417a-8795-a99748d6f4ce', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1abf0c53-b4ac-4b45-b528-8f47b8d7d533', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8048b411-967a-47b3-b4f1-d7d7880eb79c', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('cc1f97e7-73c5-4f4f-91e9-1155d47ed24e', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('86415c99-3215-4f11-a62b-fa4c7b2ac298', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f393ba0b-23ca-435b-b922-62c77a9437f7', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('86254202-388c-4c55-bba2-a3053c16fe3b', @groupId1);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('86254202-388c-4c55-bba2-a3053c16fe3b', @groupId2);

-- ----------------------------
-- Volcengine Rule
-- ----------------------------
INSERT INTO `rule_group` (`name`, `description`, `level`, `plugin_id`, `flag`) VALUES ('Volcengine 等保预检', '等保合规检查（全称为等级保护合规检查）为您提供了全面覆盖通信网络、区域边界、计算环境和管理中心的网络安全检查。', '等保三级', 'hummer-huoshan-plugin', '1');
SELECT @groupId3 := LAST_INSERT_ID();

INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('05689a7f-f584-441a-ad6e-006df5d4cca1', 'Volcengine SecurityGroup高危安全组检测', 1, 'HighRisk', 'Volcengine  检测安全组是否开启风险端口，未开启视为“合规”，否则属于“不合规”', 'policies:\n  #检测开放以下高危端口的安全组：\n  #(20,21,22,25,80,773,765,1733,1737,3306,3389,7333,5732,5500)\n  - name: volcengine-security-group\n    resource: volc.securitygroup\n    description: |\n      Add Filter all security groups, filter ports\n      [20,21,22,25,80,773,765,1733,1737,3306,3389,7333,5732,5500]\n      on 0.0.0.0/0 or\n      [20,21,22,25,80,773,765, 1733,1737,3306,3389,7333,5732,5500]\n      on ::/0 (IPv6)\n    filters:\n        - or:\n            - type: ingress\n              IpProtocol: \"-1\"\n              Ports: ${{ipv4_port}}\n              Cidr: \"0.0.0.0/0\"\n            - type: ingress\n              IpProtocol: \"-1\"\n              Ports: ${{ipv6_port}}\n              CidrV6: \"::/0\"', '[{\"key\":\"ipv4_port\",\"name\":\"ipv4端口\",\"defaultValue\":\"[20,21,22,25,80,773,765, 1733,1737,3306,3389,7333,5732,5500]\",\"required\":true},{\"key\":\"ipv6_port\",\"name\":\"ipv6端口\",\"defaultValue\":\"[20,21,22,25,80,773,765, 1733,1737,3306,3389,7333,5732,5500]\",\"required\":true}]', 'hummer-huoshan-plugin', '火山引擎', 'huoshan2.png', concat(unix_timestamp(now()), '002'), '1', 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('0f134720-4de8-4ef0-855a-4f0b2a2017bd', 'Volcengine CDN域名HTTPS监听检测', 1, 'HighRisk', 'Volcengine  检测CDN域名是否开启HTTPS监听，若开启视为“合规”，否则视为“不合规”', 'policies:\n    # 检测CDN域名是否开启HTTPS监听，若开启视为“合规”。\n    - name: volcengine-cdn-ssl-protocol\n      resource: volc.cdn\n      filters:\n        - type: ssl-protocol', '[]', 'hummer-huoshan-plugin', '火山引擎', 'huoshan2.png', concat(unix_timestamp(now()), '002'), '1', 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('221de04f-0e7c-4bd6-9ef0-2ca34ae80767', 'Volcengine ECS实例公网IP检测', 1, 'HighRisk', 'Volcengine ECS实例未直接绑定公网IP，视为“合规”，否则属于“不合规”。该规则仅适用于 IPv4 协议', 'policies:\n    # ECS实例未直接绑定公网IP，视为“合规”。该规则仅适用于 IPv4 协议。\n    - name: volcengine-ecs-public-ipaddress\n      resource: volc.ecs\n      filters:\n        - type: public-ip-address', '[]', 'hummer-huoshan-plugin', '火山引擎', 'huoshan2.png', concat(unix_timestamp(now()), '002'), '1', 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('59898c82-c456-40df-b62d-edf140548735', 'Volcengine ECS VPC检测', 1, 'HighRisk', 'Volcengine  账号下的Ecs实例指定属于哪些VPC, 属于则合规，不属于则\"不合规\"', 'policies:\n  # 检测您账号下的Ecs实例指定属于哪些VPC, 属于则合规，不属于则\"不合规\"。\n  - name: volcengine-ecs-vpc-type\n    resource: volc.ecs\n    filters:\n      - type: vpc-type\n        vpcIds: [${{value1}}, ${{value2}}]', '[{\"key\":\"value1\",\"name\":\"vpcId1\",\"defaultValue\":\"\\\"vpc-1\\\"\",\"required\":true},{\"key\":\"value2\",\"name\":\"vpcId2\",\"defaultValue\":\"\\\"vpc-2\\\"\",\"required\":true}]', 'hummer-huoshan-plugin', '火山引擎', 'huoshan2.png', concat(unix_timestamp(now()), '002'), '1', 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('5d7e3fce-4e13-43b3-8b30-d33528fd1fbd', 'Volcengine SecurityGroup安全组端口访问检测', 1, 'HighRisk', 'Volcengine  账号下ECS安全组配置允许所有端口访问视为”不合规“，否则为”合规“', 'policies:\n  # 账号下ECS安全组配置允许所有端口访问视为”不合规“，否则为”合规“\n  - name: volcengine-sg-ports\n    resource: volc.securitygroup\n    filters:\n      - type: source-ports\n        SourceCidrIp: ${{SourceCidrIp}}\n        PortRange: ${{PortRange}}', '[{\"defaultValue\":\"\\\"0.0.0.0/0\\\"\",\"name\":\"目标IP地址段\",\"key\":\"SourceCidrIp\",\"required\":true},{\"defaultValue\":\"\\\"-1/-1\\\"\",\"name\":\"端口号\",\"key\":\"PortRange\",\"required\":true}]', 'hummer-huoshan-plugin', '火山引擎', 'huoshan2.png', concat(unix_timestamp(now()), '002'), '1', 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('6882449f-5140-4aa0-8338-ef1689cafa38', 'Volcengine MongoDB实例网络类型检测', 1, 'HighRisk', 'Volcengine  检测您账号下的MongoDB实例是否运行在VPC网络环境下，是视为“合规”，否则视为“不合规”', 'policies:\n    # 检测您账号下的MongoDB实例是否运行在VPC网络环境下。\n    - name: volcengine-mongodb-network-type\n      resource: volc.mongodb\n      filters:\n        - type: network-type', '[]', 'hummer-huoshan-plugin', '火山引擎', 'huoshan2.png', concat(unix_timestamp(now()), '002'), '1', 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('d7de14df-62e4-46b5-8bac-7dec053f84be', 'Volcengine EIP带宽峰值检测', 1, 'HighRisk', 'Volcengine  检测您账号下的弹性IP实例是否达到最低带宽要求，是视为“合规”，否则视为“不合规”', 'policies:\n    # 检测您账号下的弹性IP实例是否达到最低带宽要求。\n    - name: volcengine-eip-bandwidth\n      resource: volc.eip\n      filters:\n        - type: bandwidth\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"带宽\",\"defaultValue\":\"10\",\"required\":true}]', 'hummer-huoshan-plugin', '火山引擎', 'huoshan2.png', concat(unix_timestamp(now()), '002'), '1', 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('e0b4465d-ff7f-4bc5-946e-da723a8d0be9', 'Volcengine ECS实例网络类型检测', 1, 'HighRisk', 'Volcengine 账号下所有ECS实例已关联到VPC；若您配置阈值，则关联的VpcId需存在您列出的阈值中，视为“合规”，否则视为“不合规”', 'policies:\n    # 账号下所有ECS实例已关联到VPC；若您配置阈值，则关联的VpcId需存在您列出的阈值中，视为“合规”。\n    - name: volcengine-ecs-instance-network-type\n      resource: volc.ecs\n      filters:\n        - type: instance-network-type', '[]', 'hummer-huoshan-plugin', '火山引擎', 'huoshan2.png', concat(unix_timestamp(now()), '002'), '1', 'custodian');
INSERT INTO `rule` (`id`, `name`, `status`, `severity`, `description`, `script`, `parameter`, `plugin_id`, `plugin_name`, `plugin_icon`, `last_modified`, `flag`, `scan_type`) VALUES ('f02172dd-2514-45f5-85e1-c20c71ce19a5', 'Volcengine MongoDB实例公网访问检测', 1, 'HighRisk', 'Volcengine  账号下MongoDB实例不允许任意来源公网访问，视为“合规”，否则视为“不合规”', 'policies:\n    # 账号下MongoDB实例不允许任意来源公网访问，视为“合规”。\n    - name: volcengine-mongodb-internet-access\n      resource: volc.mongodb\n      filters:\n        - type: internet-access\n          value: ${{value}}', '[{\"key\":\"value\",\"name\":\"公网访问\",\"defaultValue\":\"true\",\"required\":true}]', 'hummer-huoshan-plugin', '火山引擎', 'huoshan2.png', concat(unix_timestamp(now()), '002'), '1', 'custodian');

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e0b4465d-ff7f-4bc5-946e-da723a8d0be9', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0f134720-4de8-4ef0-855a-4f0b2a2017bd', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('221de04f-0e7c-4bd6-9ef0-2ca34ae80767', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('59898c82-c456-40df-b62d-edf140548735', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d7de14df-62e4-46b5-8bac-7dec053f84be', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('6882449f-5140-4aa0-8338-ef1689cafa38', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f02172dd-2514-45f5-85e1-c20c71ce19a5', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('05689a7f-f584-441a-ad6e-006df5d4cca1', @groupId3);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('5d7e3fce-4e13-43b3-8b30-d33528fd1fbd', @groupId3);

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('e0b4465d-ff7f-4bc5-946e-da723a8d0be9', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('0f134720-4de8-4ef0-855a-4f0b2a2017bd', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('221de04f-0e7c-4bd6-9ef0-2ca34ae80767', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('59898c82-c456-40df-b62d-edf140548735', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('d7de14df-62e4-46b5-8bac-7dec053f84be', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('6882449f-5140-4aa0-8338-ef1689cafa38', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('f02172dd-2514-45f5-85e1-c20c71ce19a5', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('05689a7f-f584-441a-ad6e-006df5d4cca1', 'safety');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('5d7e3fce-4e13-43b3-8b30-d33528fd1fbd', 'safety');

INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('0b41796f-b471-487b-b835-bf161e308540', '5d7e3fce-4e13-43b3-8b30-d33528fd1fbd', 'volc.securitygroup');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('19232de7-6aac-4f8a-9984-0979796b0c12', 'e0b4465d-ff7f-4bc5-946e-da723a8d0be9', 'volc.ecs');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('1dc8d8e3-3f4d-454b-9985-395fc12b1753', '221de04f-0e7c-4bd6-9ef0-2ca34ae80767', 'volc.ecs');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('3bb72204-aebf-419a-a4fb-0266acaab7ef', '0f134720-4de8-4ef0-855a-4f0b2a2017bd', 'volc.cdn');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('6bbe0494-48ce-45d1-938b-2b380ee37586', '6882449f-5140-4aa0-8338-ef1689cafa38', 'volc.mongodb');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('7598d945-6ccc-4947-81a6-78c61f6e6ead', 'f02172dd-2514-45f5-85e1-c20c71ce19a5', 'volc.mongodb');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('8792365b-f1f2-47c8-a842-72b316f31bd5', '05689a7f-f584-441a-ad6e-006df5d4cca1', 'volc.securitygroup');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('b7d5e058-8bcf-47ee-99a6-c861a8ee01e3', '59898c82-c456-40df-b62d-edf140548735', 'volc.ecs');
INSERT INTO `rule_type` (`id`, `rule_id`, `resource_type`) VALUES ('fded032a-51d2-4a83-b7b8-d63ddcac2734', 'd7de14df-62e4-46b5-8bac-7dec053f84be', 'volc.eip');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e0b4465d-ff7f-4bc5-946e-da723a8d0be9', '3');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e0b4465d-ff7f-4bc5-946e-da723a8d0be9', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e0b4465d-ff7f-4bc5-946e-da723a8d0be9', '5');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e0b4465d-ff7f-4bc5-946e-da723a8d0be9', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e0b4465d-ff7f-4bc5-946e-da723a8d0be9', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0f134720-4de8-4ef0-855a-4f0b2a2017bd', '52');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('221de04f-0e7c-4bd6-9ef0-2ca34ae80767', '18');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('59898c82-c456-40df-b62d-edf140548735', '10');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('59898c82-c456-40df-b62d-edf140548735', '13');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('59898c82-c456-40df-b62d-edf140548735', '92');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('59898c82-c456-40df-b62d-edf140548735', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('59898c82-c456-40df-b62d-edf140548735', '95');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d7de14df-62e4-46b5-8bac-7dec053f84be', '2');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('6882449f-5140-4aa0-8338-ef1689cafa38', '3');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('6882449f-5140-4aa0-8338-ef1689cafa38', '4');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('6882449f-5140-4aa0-8338-ef1689cafa38', '5');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('6882449f-5140-4aa0-8338-ef1689cafa38', '6');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('6882449f-5140-4aa0-8338-ef1689cafa38', '7');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f02172dd-2514-45f5-85e1-c20c71ce19a5', '10');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f02172dd-2514-45f5-85e1-c20c71ce19a5', '13');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('05689a7f-f584-441a-ad6e-006df5d4cca1', '9');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('05689a7f-f584-441a-ad6e-006df5d4cca1', '46');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('05689a7f-f584-441a-ad6e-006df5d4cca1', '92');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('05689a7f-f584-441a-ad6e-006df5d4cca1', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('05689a7f-f584-441a-ad6e-006df5d4cca1', '95');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5d7e3fce-4e13-43b3-8b30-d33528fd1fbd', '10');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5d7e3fce-4e13-43b3-8b30-d33528fd1fbd', '13');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5d7e3fce-4e13-43b3-8b30-d33528fd1fbd', '92');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5d7e3fce-4e13-43b3-8b30-d33528fd1fbd', '93');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('5d7e3fce-4e13-43b3-8b30-d33528fd1fbd', '95');
