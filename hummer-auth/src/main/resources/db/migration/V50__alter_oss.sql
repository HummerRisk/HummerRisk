
ALTER TABLE `oss` ADD `account_id` varchar(64) DEFAULT NULL COMMENT '云账号ID';

DELETE FROM `rule_group_mapping` WHERE `group_id` IS NULL OR `rule_id` IS NULL;

UPDATE `rule_group` SET `name` = 'Aliyun 最佳安全实践' WHERE `name` = 'Aliyun ACK 最佳安全实践';

UPDATE `rule_group` SET `description` = '阿里云最佳安全实践，是基于众多客户上云的成功案例萃取而成的最优化企业上云指导，管理所有阿里云资源，为您提供全方位的最佳安全实践功能。' WHERE `name` = 'Aliyun 最佳安全实践';

SELECT @groupId := (SELECT id FROM `rule_group` WHERE `name` = 'Aliyun 最佳安全实践');

SELECT @groupId1 := (SELECT id FROM `rule_group` WHERE `name` = 'Aliyun RAM 最佳安全实践');

DELETE FROM `rule_group_mapping` WHERE `group_id` = @groupId1;

DELETE FROM `rule_group` WHERE `id` = @groupId1;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('70c1e701-b87a-4e4d-8648-3db7ecc2c066', @groupId);

SELECT @groupId2 := (SELECT id FROM `rule_group` WHERE `name` = 'Aliyun OSS 最佳安全实践');

DELETE FROM `rule_group_mapping` WHERE `group_id` = @groupId2;

DELETE FROM `rule_group` WHERE `id` = @groupId2;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('429f8396-e04b-49d9-8b38-80647ac87e66', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('8c635fda-7f89-4d5c-b0f4-2116f1b65554', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('9d94781e-922d-48c3-90a1-393dc79f2442', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d826c85d-cb42-4824-ab13-6d7a8026d9ae', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('fdef013f-ce14-468a-9af4-1c0fabc7e6e1', @groupId);

SELECT @groupId3 := (SELECT id FROM `rule_group` WHERE `name` = 'Aliyun NAS 最佳安全实践');

DELETE FROM `rule_group_mapping` WHERE `group_id` = @groupId3;

DELETE FROM `rule_group` WHERE `id` = @groupId3;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('b9ff94b8-8959-4eac-86e9-983d8e6d7db6', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('deede37b-2991-40b3-b8b5-089914e4cd43', @groupId);

SELECT @groupId4 := (SELECT id FROM `rule_group` WHERE `name` = 'Aliyun SLB 最佳安全实践');

DELETE FROM `rule_group_mapping` WHERE `group_id` = @groupId4;

DELETE FROM `rule_group` WHERE `id` = @groupId4;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('1299a29b-e19d-4186-93fd-a18ed1b2584a', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2274a926-ea5e-4cdc-915e-09fa6d803bff', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('339cf3fc-f9d9-457e-ac72-40d37c402bdf', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('594e7673-c0db-40a4-9a0c-f70f0e58cc62', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('88a77028-0e2a-4201-a713-ded3a94864f9', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e28786b3-f2b1-46de-b4b6-7835b42ae58b', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e2d51fc6-2ec2-4d17-bf87-13a3df90ea5d', @groupId);

SELECT @groupId5 := (SELECT id FROM `rule_group` WHERE `name` = 'Aliyun PostgreSQL 最佳安全实践');

DELETE FROM `rule_group_mapping` WHERE `group_id` = @groupId5;

DELETE FROM `rule_group` WHERE `id` = @groupId5;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0eaec886-c769-4dfb-8efa-7933328fde0d', @groupId);

SELECT @groupId6 := (SELECT id FROM `rule_group` WHERE `name` = 'Aliyun RDS 最佳安全实践');

DELETE FROM `rule_group_mapping` WHERE `group_id` = @groupId6;

DELETE FROM `rule_group` WHERE `id` = @groupId6;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('028b8362-08f2-404c-8e15-935426bb8545', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ae65e90c-124c-4a81-8081-746d47f44e8f', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('beda16d0-93fd-4366-9ebf-f5ce1360cd60', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c95fde94-53a5-4658-98a4-56a0c6d951d4', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d690be79-2e8c-4054-bbe6-496bd29e91fe', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e054787c-5826-4242-8450-b0daa926ea40', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2adbae64-6403-4dfb-92ab-637354da49f8', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c57f055e-fd84-4af3-ba97-892a8fdc1fed', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('e054787c-5826-4242-8450-b0daa926ea40', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('29763f5e-ef4c-431d-b44f-39cd1b5b5363', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ff153eea-2628-440b-b054-186d6f5a7708', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('44343a84-39e2-4fbc-b8c5-d3ac06186501', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7d323895-f07b-4845-8b3d-01c78180f270', @groupId);

SELECT @groupId7 := (SELECT id FROM `rule_group` WHERE `name` = 'Aliyun Redis 最佳安全实践');

DELETE FROM `rule_group_mapping` WHERE `group_id` = @groupId7;

DELETE FROM `rule_group` WHERE `id` = @groupId7;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('29763f5e-ef4c-431d-b44f-39cd1b5b5363', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('ff153eea-2628-440b-b054-186d6f5a7708', @groupId);

SELECT @groupId8 := (SELECT id FROM `rule_group` WHERE `name` = 'Aliyun PolarDB 最佳安全实践');

DELETE FROM `rule_group_mapping` WHERE `group_id` = @groupId8;

DELETE FROM `rule_group` WHERE `id` = @groupId8;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('2adbae64-6403-4dfb-92ab-637354da49f8', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('4f8fa101-171a-4491-9485-e5aa091a88a4', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('c57f055e-fd84-4af3-ba97-892a8fdc1fed', @groupId);

SELECT @groupId9 := (SELECT id FROM `rule_group` WHERE `name` = 'Aliyun MSE 最佳安全实践');

DELETE FROM `rule_group_mapping` WHERE `group_id` = @groupId9;

DELETE FROM `rule_group` WHERE `id` = @groupId9;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('0fab953a-c392-493d-9ef8-238cf5651d40', @groupId);

SELECT @groupId10 := (SELECT id FROM `rule_group` WHERE `name` = 'Aliyun Event 最佳安全实践');

DELETE FROM `rule_group_mapping` WHERE `group_id` = @groupId10;

DELETE FROM `rule_group` WHERE `id` = @groupId10;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('d76abc8d-9975-4752-8e60-709c811d44cf', @groupId);

SELECT @groupId11 := (SELECT id FROM `rule_group` WHERE `name` = 'Aliyun ECS 最佳安全实践');

DELETE FROM `rule_group_mapping` WHERE `group_id` = @groupId11;

DELETE FROM `rule_group` WHERE `id` = @groupId11;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('3e5d47ac-86b6-40d1-a191-1b2ff2496118', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('6fd132c0-b4df-4685-b132-5441d1aef2f8', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('f4ebb59b-c93a-4f34-9e66-660b03943d7d', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('3002de7d-a68b-45af-933b-3d37d63e3c57', @groupId);

SELECT @groupId12 := (SELECT id FROM `rule_group` WHERE `name` = 'Aliyun MongoDB 最佳安全实践');

DELETE FROM `rule_group_mapping` WHERE `group_id` = @groupId12;

DELETE FROM `rule_group` WHERE `id` = @groupId12;

INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('44343a84-39e2-4fbc-b8c5-d3ac06186501', @groupId);
INSERT INTO `rule_group_mapping` (`rule_id`, `group_id`) VALUES ('7d323895-f07b-4845-8b3d-01c78180f270', @groupId);

ALTER TABLE `rule_inspection_report` MODIFY column `project` varchar(512) DEFAULT NULL COMMENT '检查项目描述';
ALTER TABLE `rule_inspection_report` MODIFY column `improvement` varchar(1024) DEFAULT NULL COMMENT '改进建议';

INSERT INTO `rule_inspection_report` (`id`, `project`, `item_sort_first_level`, `item_sort_second_level`, `improvement`) VALUES (128, '通过设置 Deployment 资源请求（request）和资源限制（limit）来限制容器的资源使用量，包括对：cpu、memory、hugepages等资源的约束限制，从而确保容器在运行时不会过度消耗系统资源，保持整个集群的稳定性和可靠性.', 'Kubernetes 资源配置', '资源约束', '对于每个容器，您可以指定资源限制和请求，包括以下内容：\r\nspec.containers[].resources.limits.cpu\r\nspec.containers[].resources.limits.memory\r\nspec.containers[].resources.limits.hugepages-<size>\r\nspec.containers[].resources.requests.cpu\r\nspec.containers[].resources.requests.memory\r\nspec.containers[].resources.requests.hugepages-<size>\r\n您可以通过修改 deployment 添加资源限制，更多细节说明可以查阅 K8s 官方文档：https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/');
INSERT INTO `rule_inspection_report` (`id`, `project`, `item_sort_first_level`, `item_sort_second_level`, `improvement`) VALUES (129, '应遵循最小权限原则，应确保 Kubernetes 的 role 具有合理的权限，若具有对 pods/exec 资源的 create 权限，需要进行权限删除。', 'Kubernetes 权限管理', '角色权限', '1. 通过 kubectl describe role <ROLE_NAME> 查阅 role 是否有对 pods 或 pods/exec 资源的权限，\r\n2. 如果发现有对 pods/exec 的 create 权限， 通过 kubectl edit role  <ROLE_NAME> 将 对应的权限删除');
INSERT INTO `rule_inspection_report` (`id`, `project`, `item_sort_first_level`, `item_sort_second_level`, `improvement`) VALUES (130, '通过设置 ReplicaSet 资源请求（request）和资源限制（limit）来限制容器的资源使用量，包括对：cpu、memory、hugepages等资源的约束限制，从而确保容器在运行时不会过度消耗系统资源，保持整个集群的稳定性和可靠性\r\n\r\n通过设置 ReplicaSet 资源请求（request）和资源限制（limit）来限制容器的资源使用量，包括对：cpu、memory、hugepages等资源的约束限制，从而确保容器在运行时不会过度消耗系统资源，保持整个集群的稳定性和可靠性\r\n\r\n通过设置 ReplicaSet 资源请求（request）和资源限制（limit）来限制容器的资源使用量，包括对：cpu、memory、hugepages等资源的约束限制，从而确保容器在运行时不会过度消耗系统资源，保持整个集群的稳定性和可靠性。', 'Kubernetes 资源配置', '资源约束', '对于每个容器，您可以指定资源限制和请求，包括以下内容：\r\nspec.containers[].resources.limits.cpu\r\nspec.containers[].resources.limits.memory\r\nspec.containers[].resources.limits.hugepages-<size>\r\nspec.containers[].resources.requests.cpu\r\nspec.containers[].resources.requests.memory\r\nspec.containers[].resources.requests.hugepages-<size>\r\n您可以通过修改 ReplicaSet 添加资源限制，更多细节说明可以查阅 K8s 官方文档：https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/');
INSERT INTO `rule_inspection_report` (`id`, `project`, `item_sort_first_level`, `item_sort_second_level`, `improvement`) VALUES (131, '通过设置 DaemonSet 资源请求（request）和资源限制（limit）来限制容器的资源使用量，包括对：cpu、memory、hugepages等资源的约束限制，从而确保容器在运行时不会过度消耗系统资源，保持整个集群的稳定性和可靠性。', 'Kubernetes 资源配置', '资源约束', '对于每个容器，您可以指定资源限制和请求，包括以下内容：\r\nspec.containers[].resources.limits.cpu\r\nspec.containers[].resources.limits.memory\r\nspec.containers[].resources.limits.hugepages-<size>\r\nspec.containers[].resources.requests.cpu\r\nspec.containers[].resources.requests.memory\r\nspec.containers[].resources.requests.hugepages-<size>\r\n您可以通过修改 DaemonSet 添加资源限制，更多细节说明可以查阅 K8s 官方文档：https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/');
INSERT INTO `rule_inspection_report` (`id`, `project`, `item_sort_first_level`, `item_sort_second_level`, `improvement`) VALUES (132, '应遵循最小权限原则，应确保 Kubernetes 的 role 具有合理的权限，若具有 delete 权限，需要进行权限删除。', 'Kubernetes 资源配置', '资源约束', '1. 通过 kubectl describe role <ROLE_NAME> 查阅 role 的权限判定是否有 delete 权限\r\n2. 如果发现有 delete 通过 kubectl edit role  <ROLE_NAME> 将 delete 权限删除');
INSERT INTO `rule_inspection_report` (`id`, `project`, `item_sort_first_level`, `item_sort_second_level`, `improvement`) VALUES (133, '应确保容器使用非 root 用户运行，以避免潜在的安全风险。', 'Docker 容器权限管理', '权限管理', '1. 通过 kubectl exec it <POD_NAME> ps aux 查看进程是否以 root 运行\r\n2. 如果发现当前程序以 root 运行，则容易出现安全风险，需要通过修改 Dockerfile 添加非root用户，并配置程序所需要的权限，然后重新构建镜像\r\n例如：\r\nFROM ubuntu:20.04\r\nCOPY . /myapp\r\nRUN make /myapp\r\n...\r\n...\r\nRUN groupadd --gid 5000 newuser  \\\r\n    useradd --home-dir /home/newuser --create-home --uid 5000 \\\r\n     --gid 5000 --shell /bin/sh --skel /dev/null newuser\r\nUSER newuser');
INSERT INTO `rule_inspection_report` (`id`, `project`, `item_sort_first_level`, `item_sort_second_level`, `improvement`) VALUES (134, '应遵循最小权限原则，应确保 Kubernetes 的 ClusterRole 具有合理的权限，若具有 delete 权限，需要进行权限删除。', 'Kubernetes 权限管理', '角色权限', '1. 通过 kubectl describe clusterrole <CLUSTER_ROLE_NAME> 查阅 clusterrole 的权限判定是否有 delete 权限\r\n2. 如果发现有 delete 权限则通过 kubectl edit clusterrole  <CLUSTER_ROLE_NAME> 将 delete 权限删除');
INSERT INTO `rule_inspection_report` (`id`, `project`, `item_sort_first_level`, `item_sort_second_level`, `improvement`) VALUES (135, '通过设置 StatefulSet 资源请求（request）和资源限制（limit）来限制容器的资源使用量，包括对：cpu、memory、hugepages等资源的约束限制，从而确保容器在运行时不会过度消耗系统资源，保持整个集群的稳定性和可靠性\r\n\r\n通过设置 StatefulSet 资源请求（request）和资源限制（limit）来限制容器的资源使用量，包括对：cpu、memory、hugepages等资源的约束限制，从而确保容器在运行时不会过度消耗系统资源，保持整个集群的稳定性和可靠性。', '安全通信网络', '资源约束', '对于每个容器，您可以指定资源限制和请求，包括以下内容：\r\nspec.containers[].resources.limits.cpu\r\nspec.containers[].resources.limits.memory\r\nspec.containers[].resources.limits.hugepages-<size>\r\nspec.containers[].resources.requests.cpu\r\nspec.containers[].resources.requests.memory\r\nspec.containers[].resources.requests.hugepages-<size>\r\n您可以通过修改 StatefulSet 添加资源限制，更多细节说明可以查阅 K8s 官方文档：https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/');
INSERT INTO `rule_inspection_report` (`id`, `project`, `item_sort_first_level`, `item_sort_second_level`, `improvement`) VALUES (136, '应遵循最小权限原则，应确保 Kubernetes 的 ClusterRole 具有合理的权限，若具有对 pods/exec 资源的 create 权限，需要进行权限删除。', 'Kubernetes 权限管理', '角色权限', '1. 通过 kubectl describe clusterrole <ROLE_NAME> 查阅 clusterrole 是否有对 pods 或 pods/exec 资源的权限\r\n2. 如果发现有对 pods/exec 的 create 权限， 通过 kubectl edit clusterrole  <CLUSTER_ROLE_NAME> 将对应的权限删除');
INSERT INTO `rule_inspection_report` (`id`, `project`, `item_sort_first_level`, `item_sort_second_level`, `improvement`) VALUES (137, '通过设置 Pod 资源请求（request）和资源限制（limit）来限制容器的资源使用量，包括对：cpu、memory、hugepages等资源的约束限制，从而确保容器在运行时不会过度消耗系统资源，保持整个集群的稳定性和可靠性。', 'Kubernetes 资源配置', '资源约束', '对于每个容器，您可以指定资源限制和请求，包括以下内容：\r\nspec.containers[].resources.limits.cpu\r\nspec.containers[].resources.limits.memory\r\nspec.containers[].resources.limits.hugepages-<size>\r\nspec.containers[].resources.requests.cpu\r\nspec.containers[].resources.requests.memory\r\nspec.containers[].resources.requests.hugepages-<size>\r\n您可以通过修改 Pod 添加资源限制，并将其约束到合适的资源大小。更多细节说明可以查阅 K8s 官方文档：https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/');
INSERT INTO `rule_inspection_report` (`id`, `project`, `item_sort_first_level`, `item_sort_second_level`, `improvement`) VALUES (138, '通过设置 DaemonSet 资源  memory 请求（request）和资源限制（limit）来限制容器的资源内成使用量，从而确保容器在运行时不会过度消耗系统资源，保持整个集群的稳定性和可靠性。', 'Kubernetes 资源配置', '资源约束', '对于每个容器，您可以指定内存资源限制和请求，包括以下内容：\r\nspec.containers[].resources.limits.memory\r\nspec.containers[].resources.requests.memory\r\n您可以通过修改 DaemonSet 添加内存资源限制，并将其约束到合适的资源大小。更多细节说明可以查阅 K8s 官方文档：https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/');
INSERT INTO `rule_inspection_report` (`id`, `project`, `item_sort_first_level`, `item_sort_second_level`, `improvement`) VALUES (139, 'Pod 中的容器应始终使用只读根文件系统运行,避免攻击者篡改文件系统。', '容器文件系统权限管理', '文件系统权限管理', '1. 通过 kubectl describe Pod <Pod_Name> 查阅 pod 挂载的存储中， readOnlyRootFilesystem 是否为 true ，true 代表当前是只读\r\n2. 如果发现为 false （说明当前文件系统不是只读）， 通过 kubectl edit Pod  <Pod_Name> 进行修改\r\n例如：\r\nkind: Pod\r\nspec:\r\n  containers:\r\n    - name: myContainer\r\n      securityContext:\r\n        readOnlyRootFilesystem: true');
INSERT INTO `rule_inspection_report` (`id`, `project`, `item_sort_first_level`, `item_sort_second_level`, `improvement`) VALUES (140, '通过设置 StatefulSet 资源请求（request）和资源限制（limit）来限制容器的资源使用量，包括对：cpu、memory、hugepages等资源的约束限制，从而确保容器在运行时不会过度消耗系统资源，保持整个集群的稳定性和可靠性。', 'Kubernetes 资源配置', '资源约束', '对于每个容器，您可以指定资源限制和请求，包括以下内容：\r\nspec.containers[].resources.limits.cpu\r\nspec.containers[].resources.limits.memory\r\nspec.containers[].resources.limits.hugepages-<size>\r\nspec.containers[].resources.requests.cpu\r\nspec.containers[].resources.requests.memory\r\nspec.containers[].resources.requests.hugepages-<size>\r\n您可以通过修改 StatefulSet 添加资源限制，并将其约束到合适的资源大小。更多细节说明可以查阅 K8s 官方文档：https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/');
INSERT INTO `rule_inspection_report` (`id`, `project`, `item_sort_first_level`, `item_sort_second_level`, `improvement`) VALUES (141, 'Deployment 中的容器应始终使用只读根文件系统运行，避免攻击者篡改文件系统。', '容器文件系统权限管理', '文件系统权限管理', '1. 通过 kubectl describe Deployment <Deployment_Name> 查阅 pod 挂载的存储中， readOnlyRootFilesystem 是否为 true ，true 代表当前是只读\r\n2. 如果发现为 false （说明当前文件系统不是只读）， 通过 kubectl edit Deployment  <Deployment_Name> 修改\r\n例如：\r\nkind: Deployment\r\nspec:\r\n  containers:\r\n    - name: myContainer\r\n      securityContext:\r\n        readOnlyRootFilesystem: true');
INSERT INTO `rule_inspection_report` (`id`, `project`, `item_sort_first_level`, `item_sort_second_level`, `improvement`) VALUES (142, '通过设置 Pod 资源  memory 请求（request）和资源限制（limit）来限制容器的资源内成使用量，从而确保容器在运行时不会过度消耗系统资源，保持整个集群的稳定性和可靠性。', 'Kubernetes 资源配置', '资源约束', '对于每个容器，您可以指定资源限制和请求，包括以下内容：\r\nspec.containers[].resources.limits.memory\r\nspec.containers[].resources.requests.memory\r\n您可以通过修改 Pod 添加资源限制，并将其约束到合适的资源大小。更多细节说明可以查阅 K8s 官方文档：https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/');
INSERT INTO `rule_inspection_report` (`id`, `project`, `item_sort_first_level`, `item_sort_second_level`, `improvement`) VALUES (143, 'StatefulSet 中的容器应始终使用只读根文件系统运行,避免攻击者篡改文件系统。', '容器文件系统权限管理', '文件系统权限管理', '1. 通过 kubectl describe StatefulSet  <StatefulSet _Name> 查阅 StatefulSet 的配置中， readOnlyRootFilesystem 是否为 true ，true 代表当前是只读\r\n2. 如果发现为 false （说明当前文件系统不是只读）， 通过 kubectl edit StatefulSet   <StatefulSet _Name> 进行修改\r\n例如：\r\nkind: StatefulSet \r\nspec:\r\n  containers:\r\n    - name: myContainer\r\n      securityContext:\r\n        readOnlyRootFilesystem: true');
INSERT INTO `rule_inspection_report` (`id`, `project`, `item_sort_first_level`, `item_sort_second_level`, `improvement`) VALUES (144, '通过设置 ReplicaSet 资源  memory 请求（request）和资源限制（limit）来限制容器的资源内成使用量，从而确保容器在运行时不会过度消耗系统资源，保持整个集群的稳定性和可靠性。', 'Kubernetes 资源配置', '资源约束', '对于每个容器，您可以指定资源限制和请求，包括以下内容：\r\nspec.containers[].resources.limits.memory\r\nspec.containers[].resources.requests.memory\r\n您可以通过修改 ReplicaSet 添加资源限制，并将其约束到合适的资源大小。更多细节说明可以查阅 K8s 官方文档：https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/');
INSERT INTO `rule_inspection_report` (`id`, `project`, `item_sort_first_level`, `item_sort_second_level`, `improvement`) VALUES (145, '通过设置 Deployment 资源  memory 请求（request）和资源限制（limit）来限制容器的资源内成使用量，从而确保容器在运行时不会过度消耗系统资源，保持整个集群的稳定性和可靠性。', 'Kubernetes 资源配置', '资源约束', '对于每个容器，您可以指定资源限制和请求，包括以下内容：\r\nspec.containers[].resources.limits.memory\r\nspec.containers[].resources.requests.memory\r\n您可以通过修改 Deployment 添加资源限制，并将其约束到合适的资源大小。更多细节说明可以查阅 K8s 官方文档：https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/');
INSERT INTO `rule_inspection_report` (`id`, `project`, `item_sort_first_level`, `item_sort_second_level`, `improvement`) VALUES (146, 'DaemonSet 中的容器应始终使用只读根文件系统运行,避免攻击者篡改文件系统。', '容器文件系统权限管理', '文件系统权限管理', '1. 通过 kubectl describe DaemonSet <DaemonSet_Name> 查阅 pod 挂载的存储中， readOnlyRootFilesystem 是否为 true ，true 代表当前是只读\r\n2. 如果发现为 false （说明当前文件系统不是只读）， 通过 kubectl edit DaemonSet  <DaemonSet_Name> 进行修改\r\n例如：\r\nkind: DaemonSet\r\nspec:\r\n  containers:\r\n    - name: myContainer\r\n      securityContext:\r\n        readOnlyRootFilesystem: true');
INSERT INTO `rule_inspection_report` (`id`, `project`, `item_sort_first_level`, `item_sort_second_level`, `improvement`) VALUES (147, 'ReplicaSet 中的容器应始终使用只读根文件系统运行，避免攻击者篡改文件系统。', '容器文件系统权限管理', '文件系统权限管理', '1. 通过 kubectl describe ReplicaSet <ReplicaSet_Name> 查阅 pod 挂载的存储中， readOnlyRootFilesystem 是否为 true ，true 代表当前是只读\r\n2. 如果发现为 false （说明当前文件系统不是只读）， 通过 kubectl edit ReplicaSet  <ReplicaSet_Name> 修改\r\n例如：\r\nkind: ReplicaSet \r\nspec:\r\n  containers:\r\n    - name: myContainer\r\n      securityContext:\r\n        readOnlyRootFilesystem: true');

DELETE FROM `rule_inspection_report_mapping` WHERE `rule_id` in ('f00830cc-7dc5-4f6d-abcb-ad3512e23881', 'f00830cc-7dc5-4f6d-abcb-ad3512e2388c', 'q00830cc-7dc5-4f6d-abcb-ad3512e2388c', '1433cafd-25ae-4966-8079-817bf61b1a5e', '9433cafd-25ae-4966-8079-817bf61b1a51', '9433cafd-25ae-4966-8079-817bf61b1a5e',
                                                                '15a5b213-1d03-4774-878f-fb6249937047', '85a5b213-1d03-4774-878f-fb6249937041', '85a5b213-1d03-4774-878f-fb6249937047', '1af32c68-0250-4f0c-857c-a91ad525e966', '9af32c68-0250-4f0c-857c-a91ad525e961', '9af32c68-0250-4f0c-857c-a91ad525e966',
                                                                'b0583908-97bf-43e0-b5c0-3108cb74ec4e', 'c0583908-97bf-43e0-b5c0-3108cb74ec41', 'c0583908-97bf-43e0-b5c0-3108cb74ec4e', '1d40f394-ffc3-498f-b87e-c5ab51d6cc51', '1d40f394-ffc3-498f-b87e-c5ab51d6cc56', '2d40f394-ffc3-498f-b87e-c5ab51d6cc56',
                                                                'bd603fc9-a85e-428d-ab27-57bf47b2c8d4', 'cd603fc9-a85e-428d-ab27-57bf47b2c8d1', 'cd603fc9-a85e-428d-ab27-57bf47b2c8d4', 'e45e11ae-0b92-4b42-83cd-b8d6c0fca981', 'e45e11ae-0b92-4b42-83cd-b8d6c0fca98c', 't45e11ae-0b92-4b42-83cd-b8d6c0fca98c',
                                                                '19c7c58a-57ca-4142-bd10-5f36e68bdd90', '19c7c58a-57ca-4142-bd10-5f36e68bdd91', '29c7c58a-57ca-4142-bd10-5f36e68bdd90', 'b86e5e4b-a9de-415c-b5b6-061a08812d1f', 'd86e5e4b-a9de-415c-b5b6-061a08812d11', 'd86e5e4b-a9de-415c-b5b6-061a08812d1f',
                                                                'c2244cfe-074a-4947-a075-d607dc705837', 'c5544cfe-074a-4947-a075-d607dc705837', 'c5544cfe-084a-4947-a075-d607dc705837', '986637d4-8de9-4664-8775-4fa9a4a5afca', '988037d4-8de7-4664-8775-4fa9a4a5afca', '988037d4-8de9-4664-8775-4fa9a4a5afca',
                                                                '1d18f729-f869-4b69-8772-41a8d58b5c34', '2d08f728-f869-4b69-8772-41a8d58b5c34', '2d08f729-f869-4b69-8772-41a8d58b5c34', '0f2dae7b-da13-4c17-a77d-b2d677b25174', '0f2dae7b-da23-4c17-a77d-b2d677b25174', '0f4rae7b-da23-4c17-a77d-b2d677b25174',
                                                                'c1659d6f-d6c1-49d9-9f16-bd7f8e359667', 'c3959d6f-d5c1-49d9-9f16-bd7f8e359667', 'c3959d6f-d6c1-49d9-9f16-bd7f8e359667', '50f5f3b4-8cc0-44fa-8e59-c84b1c4250fe', '50f5f3b4-8cc1-44fa-8e59-c84b1c4250fe', '59t5f3b4-8cc1-44fa-8e59-c84b1c4250fe',
                                                                'fe56831e-24b1-4c5b-a08b-e0bae0d3847b', 'fe65831e-24b1-4c5b-a08b-e0bae0d3847b', 'fe65831e-25b1-4c5b-a08b-e0bae0d3847b', 'cf01ca4c-c87a-4db6-b90a-830f6d1018ea', 'cf01ca4c-c97a-4db6-b90a-830f6d1018ea', 'cf13ca4c-c87a-4db6-b90a-830f6d1018ea',
                                                                '6adf1612-6016-4a9f-a224-e04fbb78a131', '6adf1612-6017-4a9f-a224-e04fbb78a131', '6atr1612-6017-4a9f-a224-e04fbb78a131', '8d00a470-0a17-4eb3-908e-63e0352edec1', '8d00a470-0a18-4eb3-908e-63e0352edec1', '8d78a470-0a18-4eb3-908e-63e0352edec1');

INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f00830cc-7dc5-4f6d-abcb-ad3512e23881', '128');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('f00830cc-7dc5-4f6d-abcb-ad3512e2388c', '128');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('q00830cc-7dc5-4f6d-abcb-ad3512e2388c', '128');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1433cafd-25ae-4966-8079-817bf61b1a5e', '129');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('9433cafd-25ae-4966-8079-817bf61b1a51', '129');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('9433cafd-25ae-4966-8079-817bf61b1a5e', '129');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('15a5b213-1d03-4774-878f-fb6249937047', '130');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('85a5b213-1d03-4774-878f-fb6249937041', '130');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('85a5b213-1d03-4774-878f-fb6249937047', '130');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1af32c68-0250-4f0c-857c-a91ad525e966', '131');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('9af32c68-0250-4f0c-857c-a91ad525e961', '131');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('9af32c68-0250-4f0c-857c-a91ad525e966', '131');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b0583908-97bf-43e0-b5c0-3108cb74ec4e', '132');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c0583908-97bf-43e0-b5c0-3108cb74ec41', '132');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c0583908-97bf-43e0-b5c0-3108cb74ec4e', '132');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1d40f394-ffc3-498f-b87e-c5ab51d6cc51', '133');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1d40f394-ffc3-498f-b87e-c5ab51d6cc56', '133');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2d40f394-ffc3-498f-b87e-c5ab51d6cc56', '133');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('bd603fc9-a85e-428d-ab27-57bf47b2c8d4', '134');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('cd603fc9-a85e-428d-ab27-57bf47b2c8d1', '134');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('cd603fc9-a85e-428d-ab27-57bf47b2c8d4', '134');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e45e11ae-0b92-4b42-83cd-b8d6c0fca981', '135');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('e45e11ae-0b92-4b42-83cd-b8d6c0fca98c', '135');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('t45e11ae-0b92-4b42-83cd-b8d6c0fca98c', '135');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('19c7c58a-57ca-4142-bd10-5f36e68bdd90', '136');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('19c7c58a-57ca-4142-bd10-5f36e68bdd91', '136');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('29c7c58a-57ca-4142-bd10-5f36e68bdd90', '136');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('b86e5e4b-a9de-415c-b5b6-061a08812d1f', '137');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d86e5e4b-a9de-415c-b5b6-061a08812d11', '137');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('d86e5e4b-a9de-415c-b5b6-061a08812d1f', '137');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c2244cfe-074a-4947-a075-d607dc705837', '138');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c5544cfe-074a-4947-a075-d607dc705837', '138');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c5544cfe-084a-4947-a075-d607dc705837', '138');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('986637d4-8de9-4664-8775-4fa9a4a5afca', '139');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('988037d4-8de7-4664-8775-4fa9a4a5afca', '139');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('988037d4-8de9-4664-8775-4fa9a4a5afca', '139');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('1d18f729-f869-4b69-8772-41a8d58b5c34', '140');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2d08f728-f869-4b69-8772-41a8d58b5c34', '140');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('2d08f729-f869-4b69-8772-41a8d58b5c34', '140');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0f2dae7b-da13-4c17-a77d-b2d677b25174', '141');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0f2dae7b-da23-4c17-a77d-b2d677b25174', '141');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('0f4rae7b-da23-4c17-a77d-b2d677b25174', '141');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c1659d6f-d6c1-49d9-9f16-bd7f8e359667', '142');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c3959d6f-d5c1-49d9-9f16-bd7f8e359667', '142');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('c3959d6f-d6c1-49d9-9f16-bd7f8e359667', '142');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('50f5f3b4-8cc0-44fa-8e59-c84b1c4250fe', '143');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('50f5f3b4-8cc1-44fa-8e59-c84b1c4250fe', '143');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('59t5f3b4-8cc1-44fa-8e59-c84b1c4250fe', '143');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('fe56831e-24b1-4c5b-a08b-e0bae0d3847b', '144');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('fe65831e-24b1-4c5b-a08b-e0bae0d3847b', '144');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('fe65831e-25b1-4c5b-a08b-e0bae0d3847b', '144');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('cf01ca4c-c87a-4db6-b90a-830f6d1018ea', '145');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('cf01ca4c-c97a-4db6-b90a-830f6d1018ea', '145');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('cf13ca4c-c87a-4db6-b90a-830f6d1018ea', '145');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('6adf1612-6016-4a9f-a224-e04fbb78a131', '146');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('6adf1612-6017-4a9f-a224-e04fbb78a131', '146');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('6atr1612-6017-4a9f-a224-e04fbb78a131', '146');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('8d00a470-0a17-4eb3-908e-63e0352edec1', '147');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('8d00a470-0a18-4eb3-908e-63e0352edec1', '147');
INSERT INTO `rule_inspection_report_mapping` (`rule_id`, `report_id`) VALUES ('8d78a470-0a18-4eb3-908e-63e0352edec1', '147');

ALTER TABLE `cloud_account` ADD `check_regions` longtext DEFAULT NULL COMMENT '选中区域';

ALTER TABLE `oss` ADD `check_regions` longtext DEFAULT NULL COMMENT '选中区域';
