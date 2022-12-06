
ALTER TABLE `cloud_native_source` ADD `source_node` varchar(256) DEFAULT NULL COMMENT '资源节点';

ALTER TABLE `cloud_native_result_item` ADD `resource` varchar(256) DEFAULT NULL COMMENT 'resource';

ALTER TABLE `cloud_task_item` ADD `command` varchar(1024) DEFAULT NULL COMMENT 'command';

ALTER TABLE `server_result` ADD `command` varchar(1024) DEFAULT NULL COMMENT 'command';

ALTER TABLE `cloud_native_config_result` ADD `command` varchar(1024) DEFAULT NULL COMMENT 'command';

ALTER TABLE `image_result` ADD `command` varchar(1024) DEFAULT NULL COMMENT 'command';

ALTER TABLE `code_result` ADD `command` varchar(1024) DEFAULT NULL COMMENT 'command';

ALTER TABLE `file_system_result` ADD `command` varchar(1024) DEFAULT NULL COMMENT 'command';

INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('0487f316-9cd8-46f7-86e9-31f1ab8b504a', 'server');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('0f91f89e-d39b-4a6e-8311-1070ae2bc85c', 'server');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('98122047-afd0-4b12-8bfd-7a13ee177f72', 'server');
INSERT INTO `rule_tag_mapping` (`rule_id`, `tag_key`) VALUES ('6970cdce-fd54-4853-9f89-9e70b43aa07e', 'server');

-- -----------------
-- update aws rule suggestion
-- -----------------

UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-ebs-unattached' where id = 'a90a1ba1-b392-4bf2-af31-20ecbefe5811';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-ebs-encrypted' where id = 'befc89d7-1811-404a-9226-f8ecc22820e0';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-ec2-usage' where id = 'fa5e89e3-417d-4296-9d17-ca51ed914be5';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-stopped-instance' where id = 'bd1a0479-ef54-4208-a520-50caf6acfe87';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-ec2-tagged' where id = 'b57cbdba-b4d7-4da7-84db-25b9f2d5324b';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-ec2-runnningtime' where id = '5c47228c-7fe1-484b-a5b6-7c1968074f69';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-eip-unattached' where id = '35b5c651-5bd6-44b8-85ee-ae6adfa42dc3';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-elb-sslblacklist' where id = '07eb95ef-6d78-4b9a-8555-a42b6a16de99';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-elb-unattached' where id = '0be7f5d4-766c-4b2b-910d-bec0f3aac977';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-rds-public-access' where id = '0dda84c1-794b-4977-bb66-6f12695c6c51';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-rds-encrypted' where id = 'a6c513a8-8e18-4341-94b7-b6588fdcd1f4';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-rds-unused' where id = 'b1491f69-f3b8-40ae-9659-4242dbc30a0b';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-s3-global-grants' where id = 'c30779c4-44b8-4c7b-b2ec-29ff3a96033b';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-s3-public-access' where id = 'd0f3f4b0-000a-4407-85ee-ed4a2f9dac44';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-securitygroup-check' where id = '43a1556b-5417-4efb-88fc-33e8eeb68f71';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-iam-cis' where id = 'ebc80d1e-5dd9-4b86-8037-fb7c9727084a';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-logging-cis' where id = '9929cc2a-7a9b-4643-a813-0e4359b09eb0';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-monitoring-cis' where id = '8ad73e84-141a-40c4-bd56-1813535e8e92';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-networking-cis' where id = 'ee74d071-d39e-4b0e-b4a3-e7ad24fbce12';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-cis-level1' where id = '0dc8ae24-71f8-449a-8834-d59f8fbdf991';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-cis-level2' where id = '1fbd8c76-90e7-48e4-8281-ef597bb484e8';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-cis-extras' where id = '6bb291bd-fcdf-4aa9-8fdf-69e761c9c4e0';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-forensics' where id = '350a097a-dfeb-4fd0-a3af-49049da5c025';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-gdpr' where id = 'fa60960a-0e38-4502-9834-1ab1277e9aaa';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-hippa' where id = '39c87069-a3c6-4ab3-9974-f844a89872b5';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-secrets-check' where id = '10754933-241e-42db-bb3c-42e13cb40b0f';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-api-gateway' where id = '7c37887f-0b8d-444c-bad7-e43cd2b41578';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-rds-security' where id = '197e41e7-cbc3-4eef-b5ad-5153c9232449';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-elasticseatch' where id = '4a64e90e-92ea-4d5a-a753-3c5ca92a2b95';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-pcidss' where id = '72af2230-8555-4e0c-a06a-721436a0644e';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-trustboundaries' where id = '0a5a7796-6b53-408a-ba49-7fd9e51d82f4';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-internet-exposed' where id = 'a944a483-c23f-4d62-bdd8-d2a20a4aaeca';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-iso27001' where id = '9b1d1a9c-6c7e-44db-a15c-c0c82b1040eb';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-eks-cis' where id = '0d51321e-e26c-4147-b730-5b1403384487';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-ffiec' where id = '0d6270fc-3744-42cd-a850-65ac0b8ef514';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-soc2' where id = 'c1375dfe-c0b1-4e53-9c2b-6a0f29258a08';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-sagemaker' where id = '1ab302fb-1b06-410a-b17a-fdf7843d6182';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-ens' where id = 'f38d83c9-c4b9-47b8-8135-f9bdf96cf8ec';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aws/aws-glue' where id = 'e86583b9-12dd-4316-980a-5eeeba86f9ca';

-- -----------------
-- update aliyun rule suggestion
-- -----------------
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/ack-deletion-protection' where id = '8810fc51-d234-4454-836a-95a9b1dec196';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/cdn-https' where id = 'ba1edc8f-0944-4ebb-a953-f655aa710e84';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/disk-encrypt' where id = '2533542d-5422-4bd5-8849-6a69ec05a874';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/ecs-vpc' where id = 'f4ebb59b-c93a-4f34-9e66-660b03943d7d';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/eip-bandwidth' where id = '6fd132c0-b4df-4685-b132-5441d1aef2f8';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/ecs-net-type' where id = '3e5d47ac-86b6-40d1-a191-1b2ff2496118';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/eip-bandwidth' where id = '0b2ece35-a17e-4584-ac2d-0b11483d04fb';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/event-trail' where id = 'd76abc8d-9975-4752-8e60-709c811d44cf';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/mse-vpc' where id = '0fab953a-c392-493d-9ef8-238cf5651d40';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/mongodb-public-ip' where id = '7d323895-f07b-4845-8b3d-01c78180f270';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/mse-vpc' where id = '44343a84-39e2-4fbc-b8c5-d3ac06186501';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/nas-bandwidth' where id = 'b9ff94b8-8959-4eac-86e9-983d8e6d7db6';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/nas-encrypted' where id = 'deede37b-2991-40b3-b8b5-089914e4cd43';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/oss-public-write' where id = 'd826c85d-cb42-4824-ab13-6d7a8026d9ae';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/oss-public-read' where id = '429f8396-e04b-49d9-8b38-80647ac87e66';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/oss-encrypt' where id = '9d94781e-922d-48c3-90a1-393dc79f2442';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/oss-data-redundancy-type' where id = 'fdef013f-ce14-468a-9af4-1c0fabc7e6e1';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/oss-referer' where id = '8c635fda-7f89-4d5c-b0f4-2116f1b65554';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/polardb-public-ip' where id = '2adbae64-6403-4dfb-92ab-637354da49f8';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/polardb-net-type' where id = 'c57f055e-fd84-4af3-ba97-892a8fdc1fed';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/polardb-vpc' where id = '4f8fa101-171a-4491-9485-e5aa091a88a4';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/ram' where id = '70c1e701-b87a-4e4d-8648-3db7ecc2c066';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/rds-public-ip' where id = '028b8362-08f2-404c-8e15-935426bb8545';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/rds-available-zones' where id = 'beda16d0-93fd-4366-9ebf-f5ce1360cd60';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/rds-white-list' where id = 'd690be79-2e8c-4054-bbe6-496bd29e91fe';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/rds-connection-mode' where id = 'c95fde94-53a5-4658-98a4-56a0c6d951d4';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/rds-net-type' where id = 'e054787c-5826-4242-8450-b0daa926ea40';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/rds-ha' where id = 'ae65e90c-124c-4a81-8081-746d47f44e8f';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/rds-vpc' where id = 'f3675431-730d-446f-b062-7301c5c40f23';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/redis-public-ip' where id = 'ff153eea-2628-440b-b054-186d6f5a7708';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/redis-net-type' where id = '29763f5e-ef4c-431d-b44f-39cd1b5b5363';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/slb-vpc' where id = '2274a926-ea5e-4cdc-915e-09fa6d803bff';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/slb-public-ip' where id = '88a77028-0e2a-4201-a713-ded3a94864f9';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/slb-bandwidth' where id = '594e7673-c0db-40a4-9a0c-f70f0e58cc62';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/slb-https' where id = '1299a29b-e19d-4186-93fd-a18ed1b2584a';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/slb-bind-public-ip' where id = '339cf3fc-f9d9-457e-ac72-40d37c402bdf';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/slb-bind-vpc' where id = 'e2d51fc6-2ec2-4d17-bf87-13a3df90ea5d';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/slb-listener' where id = 'e28786b3-f2b1-46de-b4b6-7835b42ae58b';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/security-group-port' where id = '4d427c93-5645-445a-93a3-0c536d6865ab';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/security-group-config' where id = '083d24e2-881f-488b-b120-8f2cd961707f';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/aliyun/security-group-high-risk-port' where id = 'df4fb45c-f9bc-4c8e-996d-036c9d2f1800';

-- -----------------
-- update azure rule suggestion
-- -----------------
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/azure/azure-app-cors' where id = '6873838e-3a47-4255-b49e-af5fbf5cb454';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/azure/azure-cosmosdb' where id = 'dd11251a-7fd4-499c-857b-c7dcf4c98958';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/azure/azure-unused-disk' where id = '94d168d0-8e62-4173-96fd-326eb267273e';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/azure/azure-loadbalancer-publicip' where id = '618a61bb-eecc-4109-b387-4a313f10c1a9';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/azure/azure-sqlserver-usage' where id = 'c3259122-ac07-4256-83da-b6361c9d3abb';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/azure/azure-sql-database' where id = '5870d5df-7674-4f40-ae06-0408c935acd6';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/azure/azure-vm-usage' where id = '6967e834-ace6-48d2-9f95-f541b614810d';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/azure/azure-vm-publicip' where id = '4852857f-b454-4375-b405-0033fed73f09';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/azure/azure-stopped-vm' where id = '802fbf6c-92ac-4153-bea3-42b7720d8124';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/azure/azure-publicip-ddos' where id = '29ee6650-3e4f-44e3-aadd-0ae15618afa9';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/azure/azure-storage-public-access' where id = '77ac25d5-0a66-4890-aa7e-5b276fce299a';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/azure/azure-storage-allowip' where id = 'f7c03491-fc0a-4ae7-87ed-6423748b2372';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/azure/azure-network-securitygroup' where id = '3e324555-83e2-4ac9-8ac2-1dae350d52ff';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/azure/azure-network-interface' where id = 'f6c72297-33db-49dd-a106-06cf2ff47069';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/azure/azure-rg-empty' where id = '6a71a114-ba92-4e0b-bd9d-2f6c8ecee88d';

-- -----------------
-- update baidu rule suggestion
-- -----------------
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/baidu/bbc-public-network-ip' where id = 'bb8e4fea-a238-4231-87b8-3aac5c2c2323';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/baidu/bbc-bandwidth' where id = 'd3b291c0-e8b5-4560-84b2-4f23d0235401';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/baidu/bcc-vpc' where id = '98a56ff2-03fd-49a9-b52c-982ce0f04f70';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/baidu/bcc-public-network-ip-monitoring' where id = '2fc93189-2ebe-40c7-b5cb-61d52d9184d8';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/baidu/bcc-network-type' where id = '45a0e76a-2f12-4681-b955-4d0eab099dce';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/baidu/cdn-domain-https-monitoring' where id = '09b1030c-e094-4d23-a75b-e3d876f55a67';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/baidu/eip-bandwidth' where id = '77001a71-f984-4fea-9883-44c566ca13fb';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/baidu/securityGroup-port-access' where id = 'fe93c715-8803-4a9b-b2f0-522571b162fb';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/baidu/alb-public-network-ip' where id = 'ca3f9cfa-892e-43ce-abfc-4373329926f3';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/baidu/common-public-network-ip' where id = '73b0db2f-b946-4d03-a4bd-95ee7d4c9108';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/baidu/disk-encryption' where id = '862db318-ce25-41ed-85fa-fc1ea4d691ec';

-- -----------------
-- update gcp rule suggestion
-- -----------------
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/gcp/gcp-dns-managed-zones' where id = 'd2288b78-bab7-4195-9c16-79ae29b9f292';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/gcp/gcp-dns-policies-if-logging-disabled' where id = '3a1a0fc7-98f3-4f8e-a434-76460ef2009b';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/gcp/gcp-sql-backup-run' where id = '34c7e619-6815-4c20-af8e-8d11b16a770c';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/gcp/gcp-appengine-certificate' where id = 'd147a425-ddd5-4826-b685-e3b3842ae253';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/gcp/gcp-appengine-blacklist' where id = '6939910d-a7f1-4347-9238-abce1111f45d';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/gcp/gcp-instance-template' where id = '95e46474-5611-4d82-a9c5-5da3398ad3a4';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/gcp/gcp-load-balancing-ssl-policies' where id = '6923bc7a-fb4f-4c1c-ae70-dc6aac407e11';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/gcp/gcp-expired-deployments' where id = '1f3ef6ec-17ab-409d-9051-ea5c543312d1';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/gcp/gcp-appengine-firewall-ingress' where id = '9312ca46-1ce9-4e42-86b2-7f9b5c0db090';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/gcp/gcp-appengine-firewall-rules' where id = '7557ce1b-aacf-4cd7-bb19-6e411621daf3';

-- -----------------
-- update huawei rule suggestion
-- -----------------
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/dds-public-network' where id = 'bb878650-b62b-4980-a6aa-4d61c1a429a0';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/disk-encrypted' where id = '36908c29-c15e-4c73-a964-5fb97bbb27fa';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/ecs-public-ip' where id = '108c875b-bf3c-4034-b07d-15faa8715257';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/ecs-network-type' where id = 'f7e8d1c4-16f7-4079-b110-b60db0cd91bf';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/eip-bandwidth' where id = '3b3a76cf-78e4-4c48-84da-baf2af6be696';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/elb-bind-ecs' where id = '5c681b76-fea3-4cb9-b6d4-a92d0d2d44fa';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/elb-public-ip' where id = '7e7b3463-c7da-4545-bbc7-f97c83f429e4';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/elb-https-listener' where id = '3c4f6db4-6f8d-4e42-9c7e-d209fe5489fb';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/gauss-db-for-nosql-cost-type' where id = '776f0a66-25d4-4aee-bbdc-6f30a2c4652e';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/gauss-db-for-nosql-public-network' where id = '8e50d813-0d65-474c-951a-4abacbf381cc';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/gauss-db-for-nosql-disk' where id = 'fcd416f0-e58c-417a-8795-a99748d6f4ce';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/gauss-db-for-nosql-network-type' where id = '4eed4751-16fc-4827-a3aa-12865b6c5c73';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/gauss-db-for-nosql-ha' where id = '77e50110-e477-4b24-a3c2-a9f5e7e41715';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/gauss-db-for-open-gauss-internet-access' where id = '8048b411-967a-47b3-b4f1-d7d7880eb79c';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/gauss-db-for-open-gauss-disk' where id = '86415c99-3215-4f11-a62b-fa4c7b2ac298';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/gauss-db-for-open-gauss-network-type' where id = '1abf0c53-b4ac-4b45-b528-8f47b8d7d533';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/gauss-db-for-open-gauss-cost-type' where id = 'f393ba0b-23ca-435b-b922-62c77a9437f7';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/gauss-db-for-open-gauss-ha' where id = '86254202-388c-4c55-bba2-a3053c16fe3b';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/gauss-db-internet-access' where id = 'cc1f97e7-73c5-4f4f-91e9-1155d47ed24e';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/gauss-db-disk' where id = 'c61ae072-e614-47d6-b0ee-07f50360032e';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/gauss-db-network-type' where id = 'fa800f05-b7ac-4009-86f9-413d26bbc368';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/gauss-db-cost-type' where id = 'e1b193de-9b9f-41de-89d5-62a731d2f212';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/gauss-db-ha' where id = 'b4620c46-795f-4d13-9139-2055849ec7b9';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/iam-login' where id = 'e58a3395-aee8-40bb-8f2f-d7156c0435cb';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/obs-read' where id = '2677aa0f-6796-4138-902b-f736e0c3d8da';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/obs-write' where id = '968837c3-25f2-4848-8174-5413c859f1f1';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/rds-public-network' where id = 'b1260ecf-47fa-4613-8ed4-71417f29441b';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/rds-disk' where id = '33a70278-c772-4ef0-93c9-4b9d3df1e242';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/rds-network-type' where id = 'ce805504-de52-4ecc-a86a-4905d2a558f3';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/rds-network-type' where id = '332ce586-70de-4053-a922-f76d6340a03c';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/rds-ha' where id = 'f744fdd9-166d-40a7-ad91-9e933ad514cc';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/redis-unencrypt' where id = '3ce82502-1c03-4ab0-b8ed-83f631a20805';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/redis-internet-access' where id = '0cf1e428-3c37-4aa6-b651-acb46c4838c0';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/security-group-config' where id = '8f3f7596-569b-4cde-97a2-1d565b8224e1';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/huawei/security-group-high-risk-port' where id = '99e762e7-766f-4e55-9133-773593497b44';

-- -----------------
-- update openstack rule suggestion
-- -----------------
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/openstack/volume-encrypt' where id = '5673475f-c43d-432c-a6a6-f29479493b4d';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/openstack/volume-status' where id = '6bbd42bc-a251-498a-8b19-8de8c55e32aa';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/openstack/server-time' where id = '5c4d5e15-ce0a-4322-8406-47e8bcb10bf4';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/openstack/server-tag' where id = 'ab3306d0-e734-4977-a462-42de0e1cf263';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/openstack/server-type' where id = 'ccf64ae6-7f99-40fe-b45f-d86a4b63bdcc';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/openstack/server-image' where id = '6359bdcd-0d1d-4e43-817c-cc338f07a065';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/openstack/flavor-type' where id = '311daee9-3c15-4b19-8fb7-97823c57eb23';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/openstack/user-role' where id = '5f6c1aed-8c46-48e6-b48d-2cf06568831f';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/openstack/user-project' where id = '1aab5692-85a5-4151-b67c-8c2c93353916';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/openstack/network-status' where id = 'b8e09c5a-fecc-4400-b831-d7f75f9cee01';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/openstack/router-status' where id = '63dedb65-0237-4402-8a5c-1d2137840fdd';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/openstack/image-status' where id = 'f7b906de-63e0-479b-b766-2e50df8fbe19';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/openstack/project-member' where id = 'bf9a87c9-f709-4f84-8a86-5afde3eb40c3';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/openstack/security-groups-config' where id = 'c9162947-90f2-41c9-8df8-2d2244bb6f1d';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/openstack/security-groups-high-risk-port' where id = '701ae534-4c13-45a2-8a72-e78afdea4d2b';

-- -----------------
-- update qingcloud rule suggestion
-- -----------------
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/qingcloud/ecs-vpc' where id = '4223f09d-b455-4b35-9e9a-07953df5f290';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/qingcloud/ecs-network-type' where id = 'd83c8434-e0ca-4250-b7c0-e5add655262d';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/qingcloud/ecs-network-type' where id = 'd83c8434-e0ca-4250-b7c0-e5add655262d';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/qingcloud/eip-bandwidth' where id = 'c8977309-8b5b-48db-bf16-3380832d4ebb';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/qingcloud/mongodb-public-network' where id = 'ceecc38d-5c98-4cc4-aeef-028ab653c26b';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/qingcloud/mongodb-network-type' where id = 'e8df5a37-a78d-4115-9db2-145d3ebda280';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/qingcloud/mysql-public-network-access' where id = '4e60b929-2158-4459-abc8-81afb99eead9';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/qingcloud/mysql-network-type' where id = '13b6dfef-b003-4a9f-9eee-6eb30f78cd21';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/qingcloud/mysql-ha' where id = '5afd9902-e319-4282-b9e7-1748ecf47c7e';

-- -----------------
-- update tencent rule suggestion
-- -----------------
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/tencent/cdb-public-network-access' where id = 'cb9320af-4ee6-4c11-8840-8ec4b9cc8276';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/tencent/cdb-multiAZ' where id = '68363ad8-cce8-4458-b8f1-9bd1d1af54a7';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/tencent/cdb-network-type' where id = '2c4de559-7182-44e7-9657-b97412f0f0a7';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/tencent/cdb-ha-status' where id = '4cffcef8-be1e-44e0-8057-892e2cf588d5';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/tencent/clb-public-network-access' where id = '525d0b91-ca93-4d3d-8679-195aef136e0b';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/tencent/clb-bandwidth' where id = 'a0dca246-563a-49c3-8a43-3c537ba1fb36';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/tencent/clb-associated-with-vpc' where id = '59a95d30-30d7-45bf-ac47-80afed002ade';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/tencent/public-write-access-permission' where id = 'f8badb51-4a56-48cc-993f-6eb6698ea0ea';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/tencent/public-read-access-permission' where id = '77eab07c-1e3c-4713-acf1-ed1bede0531d';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/tencent/cos-bucket-encryption' where id = '3e66d418-3e47-475b-a863-03a9653940ed';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/tencent/cos-antileech' where id = 'ca991f61-49a4-49b2-9d14-16ca338d8149';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/tencent/cvm-public-network-ip' where id = '17dbe1a1-e9e7-4513-aa07-597ae932ff73';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/tencent/cvm-billing' where id = '159e19e9-e956-4c52-92fc-c9746372e448';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/tencent/disk-encryption' where id = 'bfbf6be1-69f8-448e-9c23-c5267530d573';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/tencent/eip-bandwidth' where id = '1240a2b8-f7ef-47c3-8048-2dd95d6d5b54';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/tencent/mongo-public-network' where id = 'c95be861-6e0e-4404-9b21-5ccfcd94ee3f';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/tencent/mongo-instance-network-type' where id = 'c1b41cbc-1263-4d67-a7c6-cf064999c6f8';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/tencent/redis-network-type' where id = '21fc2518-3b2b-475a-8398-4b1d9f68cfdb';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/tencent/securitygroup-configuration' where id = '2631d8a3-e5d8-4ef3-88c9-a8176fae110b';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/tencent/securitygroup-highrisk-configuration' where id = 'f9d3db46-d340-4487-ae9b-82b827108912';

-- -----------------
-- update ucloud rule suggestion
-- -----------------
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/ucloud/eip-bandwidth' where id = '5fb3ca1d-2a8d-46c7-8549-403139a10fbd';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/ucloud/sg-ports' where id = '3b61a7cf-e3b4-4120-86c1-ffe92c3a6d66';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/ucloud/sg-source-cidr-ip' where id = '351684f0-1464-428e-a66d-4dbcfb1839b0';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/ucloud/sg-high-risk-port' where id = '6cbe7b32-d431-4fcb-9c47-4ba1411cb52c';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/ucloud/ulb-vpc-type' where id = '91bdd835-1071-4d6a-825a-4afdf58dce71';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/ucloud/ulb-bandwidth' where id = 'b85e5722-fd4a-471e-af94-b9d38cc23b22';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/ucloud/ulb-listener' where id = '6008cc3c-0adb-48ba-86b8-7ad2fc8efdca';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/ucloud/ulb-listener-type' where id = 'b285f285-10cc-45cc-b59e-7b84fd229815';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/ucloud/uhost-vpc' where id = '155f44ef-b0c6-4881-b2c2-ac1daffa04da';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/ucloud/uhost-public-network-ip' where id = '001400f0-0a52-43c5-945e-d9e28c6a74f9';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/ucloud/uhost-network-type' where id = '32d7aec8-265e-4ae8-916c-85bd2798b568';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/ucloud/uhost-disk-encryption' where id = '844fd144-0e31-419f-adf7-d6f89673faaa';

-- -----------------
-- update vmware vsphere rule suggestion
-- -----------------
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/vsphere/cluster' where id = 'd004829c-b359-4993-84fd-0441c1ea9c6b';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/vsphere/datacenter' where id = '7b494857-7010-446f-9deb-aab10262160e';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/vsphere/datastore-available' where id = 'eab382c4-49db-420c-b4e1-987b47bf6661';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/vsphere/datastore' where id = 'e9d801ab-3790-415e-9565-bd7208cb1b54';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/vsphere/datastore-config' where id = 'faa53ae5-f534-4a3c-85e2-22398dadd468';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/vsphere/host' where id = '6503123d-6442-49f4-8118-883c354ac868';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/vsphere/host-connect-status' where id = '82d95569-e9be-4a26-a18e-f1590bac97fb';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/vsphere/network-status' where id = 'b62df3f7-581b-4591-9373-581dd3dcdd88';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/vsphere/resource-pool' where id = '8dccd008-f79b-4460-9b55-539f488e009a';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/vsphere/vm-status' where id = 'b798aa33-bd01-47ac-8cec-64f4f5d02866';

-- -----------------
-- update volcengine rule suggestion
-- -----------------
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/volc/cdn-https' where id = '0f134720-4de8-4ef0-855a-4f0b2a2017bd';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/volc/ecs-vpc' where id = '59898c82-c456-40df-b62d-edf140548735';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/volc/ecs-public-ip' where id = '221de04f-0e7c-4bd6-9ef0-2ca34ae80767';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/volc/ecs-net-type' where id = 'e0b4465d-ff7f-4bc5-946e-da723a8d0be9';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/volc/eip-bandwidth' where id = 'd7de14df-62e4-46b5-8bac-7dec053f84be';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/volc/mongodb-public-ip' where id = 'f02172dd-2514-45f5-85e1-c20c71ce19a5';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/volc/mongodb-net-type' where id = '6882449f-5140-4aa0-8338-ef1689cafa38';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/volc/securitygroup-port' where id = '5d7e3fce-4e13-43b3-8b30-d33528fd1fbd';
UPDATE `rule` SET `suggestion` = 'https://docs.hummerrisk.com/suggest/volc/securitygroup-high-risk-port' where id = '05689a7f-f584-441a-ad6e-006df5d4cca1';
