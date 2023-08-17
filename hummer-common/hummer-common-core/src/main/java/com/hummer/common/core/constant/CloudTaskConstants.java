package com.hummer.common.core.constant;

/**
 * @author harris
 */
public class CloudTaskConstants {

    public enum TASK_STATUS {
        DRAFT, UNCHECKED, APPROVED, FINISHED, TERMINATED, CANCELED, REJECTED, PROCESSING, ERROR, WARNING, RUNNING, PENDING, PAUSE, WAITING
    }

    public enum HISTORY_TYPE {
        Cloud, Vuln, CloudNative, Image, Code, Config
    }

    public static final String TASK_ID_PREFIX = "hr";

    public enum Type {
        CREATE, UPDATE, DELETE, SERVER_EXPIRE_UPDATE, DISK_UPDATE
    }

    public enum MODEL_ID {
        hr_create
    }

    public enum RESULT_TYPE {
        json, yaml, string
    }

    /**
     * 风险等级:
     * 正常
     * 低风险
     * 中风险
     * 高风险
     */
    public enum Severity {
        Normal, LowRisk, MediumRisk, HighRisk, CriticalRisk
    }

    public enum TaskType {
        manual, quartz
    }

    /**
     * 触发操作
     */
    public enum MessageOperation {
        PENDING, COMPLETE, TERMINATE, BUSINESS_COMPLETE, CANCEL
    }

    /**
     * 消息状态
     */
    public enum MessageStatus {
        READ, UNREAD, SUCCESS, ERROR
    }

    /**
     * 消息种类
     */
    public enum SmsType {
        EMAIL, SMS, ANNOUNCEMENT
    }

    //此处添加类型，需要在 ExtCloudResourceSyncMapper.xml 添加资源态势类型 sql，183行
    public final static String[] ALIYUN_RESOURCE_TYPE = {"aliyun.cdn", "aliyun.disk", "aliyun.ecs", "aliyun.eip", "aliyun.mongodb", "aliyun.oss", "aliyun.polardb", "aliyun.ram", "aliyun.rds", "aliyun.redis", "aliyun.security-group", "aliyun.slb", "aliyun.nas", "aliyun.mse", "aliyun.ack", "aliyun.vpc", "aliyun.postgre-sql"};
    public final static String[] HUAWEI_RESOURCE_TYPE = {"huawei.vpc","huawei.dds", "huawei.disk", "huawei.ecs", "huawei.eip", "huawei.elb", "huawei.gaussdb", "huawei.gaussdbfornosql", "huawei.gaussdbforopengauss", "huawei.iam", "huawei.obs", "huawei.rds", "huawei.redis", "huawei.security-group"};
    public final static String[] TENCENT_RESOURCE_TYPE = {"tencent.vpc", "tencent.es", "tencent.cdb", "tencent.clb", "tencent.cos", "tencent.cvm", "tencent.disk", "tencent.eip", "tencent.mongodb", "tencent.redis", "tencent.security-group"};
    public final static String[] AWS_RESOURCE_TYPE = {"aws.account","aws.alarm","aws.ami","aws.cloudtrail","aws.codebuild","aws.codecommit","aws.codepipeline","aws.config-recorder","aws.config-rule","aws.dynamodb-backup","aws.dynamodb-stream","aws.dynamodb-table","aws.ebs","aws.ebs-snapshot","aws.ec2","aws.ec2-reserved","aws.ecr","aws.ecs","aws.ecs-container-instance"
            ,"aws.ecs-service","aws.ecs-task","aws.efs","aws.efs-mount-target","aws.eks","aws.elasticbeanstalk","aws.elasticbeanstalk-environment","aws.elasticache-group","aws.elasticsearch","aws.elb","aws.emr","aws.emr-security-configuration","aws.eni","aws.event-rule","aws.event-rule-target","aws.firehose","aws.glacier","aws.health-event","aws.healthcheck","aws.hostedzone","aws.iam-certificate","aws.iam-group","aws.iam-policy"
            ,"aws.iam-profile","aws.iam-role","aws.iam-user","aws.identity-pool","aws.internet-gateway","aws.iot","aws.kafka","aws.key-pair","aws.kinesis","aws.kinesis-analytics","aws.kms","aws.kms-key","aws.lambda","aws.lambda-layer","aws.launch-config","aws.launch-template-version","aws.log-group","aws.message-broker","aws.nat-gateway","aws.network-acl","aws.elastic-ip","aws.network-addr","aws.ops-item","aws.peering-connection","aws.rds","aws.rds-cluster","aws.rds-cluster-param-group","aws.rds-cluster-snapshot","aws.rds-param-group","aws.rds-reserved","aws.rds-snapshot","aws.rds-subnet-group","aws.rds-subscription","aws.redshift","aws.redshift-snapshot"
            ,"aws.redshift-subnet-group","aws.redshift-reserved","aws.rest-account","aws.rest-api","aws.rest-resource","aws.rest-stage","aws.rest-vpclink","aws.route-table","aws.rrset","aws.s3","aws.sagemaker-endpoint","aws.sagemaker-endpoint-config","aws.sagemaker-job","aws.sagemaker-model","aws.sagemaker-notebook","aws.sagemaker-transform-job","aws.secrets-manager","aws.security-group","aws.serverless-app","aws.snowball","aws.sns","aws.sns-subscription","aws.ssm-activation","aws.ssm-managed-instance","aws.ssm-parameter","aws.step-machine","aws.storage-gateway","aws.subnet","aws.support-case","aws.transit-attachment","aws.transit-gateway","aws.vpc","aws.vpc-endpoint","aws.vpn-gateway","aws.workspaces"};
    public final static String[] AZURE_RESOURCE_TYPE = {"azure.cosmosdb", "azure.disk", "azure.loadbalancer", "azure.networkinterface", "azure.networksecuritygroup", "azure.publicip", "azure.resourcegroup", "azure.sqldatabase", "azure.sqlserver", "azure.storage", "azure.vm", "azure.webapp", "azure.vnet"};
    public final static String[] GCP_RESOURCE_TYPE = {"gcp.vpc","gcp.app-engine-certificate", "gcp.app-engine-domain", "gcp.app-engine-firewall-ingress-rule", "gcp.dm-deployment", "gcp.dns-managed-zone", "gcp.dns-policy", "gcp.instance-template", "gcp.loadbalancer-ssl-policy", "gcp.sql-backup-run"};
    public final static String[] QINGCLOUD_RESOURCE_TYPE = {"qingcloud.ecs", "qingcloud.eip", "qingcloud.mongodb", "qingcloud.mysql"};
    public final static String[] UCLOUD_RESOURCE_TYPE = {"ucloud.eip", "ucloud.securitygroup", "ucloud.uhost", "ucloud.ulb"};
    public final static String[] K8S_RESOURCE_TYPE = {"k8s.namespace", "k8s.config-map", "k8s.daemon-set", "k8s.deployment", "k8s.node", "k8s.pod", "k8s.replica-set", "k8s.replication-controller", "k8s.secret", "k8s.service", "k8s.service-account", "k8s.stateful-set", "k8s.volume", "k8s.volume-claim", "k8s.controller-revision", "k8s.endpoint"};
    public final static String[] QINIU_RESOURCE_TYPE = {"qiniu.kodo"};
    public final static String[] JDCLOUD_RESOURCE_TYPE = {"jdcloud.vm", "jdcloud.eip", "jdcloud.cdn", "jdcloud.disk", "jdcloud.lb", "jdcloud.securitygroup", "jdcloud.oss"};
    public final static String[] KSYUN_RESOURCE_TYPE = {"ksyun.kec", "ksyun.slb", "ksyun.eip"};
    public final static String[] VOLC_RESOURCE_TYPE = {"volc.cdn", "volc.ecs", "volc.eip", "volc.mongodb", "volc.securitygroup"};
    public final static String[] VSPHERE_RESOURCE_TYPE = {"vsphere.cluster", "vsphere.datacenter", "vsphere.datastore", "vsphere.host", "vsphere.network", "vsphere.resourcepool", "vsphere.vm"};
    public final static String[] OPENSTACK_RESOURCE_TYPE = {"openstack.flavor", "openstack.image", "openstack.network", "openstack.project", "openstack.router", "openstack.security-group", "openstack.security-groups", "openstack.server", "openstack.user", "openstack.volume"};
    public final static String[] BAIDU_RESOURCE_TYPE = {"baidu.app-blb", "baidu.bbc", "baidu.blb", "baidu.cdn", "baidu.eip", "baidu.instance", "baidu.security-group", "baidu.volume"};

    public final static String policy = "policies:\n" +
            "    - name: all-resources\n" +
            "      resource: {resourceType}";

    public final static String CUSTODIAN_RUN_RESULT_FILE = "custodian-run.log";
    public final static String METADATA_RESULT_FILE = "metadata.json";
    public final static String RESOURCES_RESULT_FILE = "resources.json";
    public final static String PROWLER_RUN_RESULT_FILE = "result.txt";
    public final static String RESULT_FILE_PATH = "/tmp/{task_id}/policy.yml";
    public final static String RESULT_FILE_PATH_PREFIX = "/tmp/";
    public final static String PROWLER_RESULT_FILE_PATH = "/prowler";//本地启动用 /tmp
    public final static String PROWLER_CONFIG_FILE_PATH = "/root/.aws";//本地启动用 ~/.aws

    public final static String[] ECS_TYPE = {"aliyun.ecs", "huawei.ecs", "tencent.cvm", "aws.ec2", "azure.vm", "qingcloud.ecs", "ucloud.uhost", "jdcloud.vm", "ksyun.kec", "volc.ecs", "vsphere.vm", "openstack.server", "baidu.bbc"};
    public final static String[] RDS_TYPE = {"aliyun.polardb", "aliyun.rds", "huawei.dds", "huawei.gaussdb", "huawei.gaussdbfornosql", "huawei.gaussdbforopengauss", "huawei.rds", "tencent.cdb", "aws.rds", "azure.cosmosdb", "azure.sqldatabase", "azure.sqlserver", "gcp.sql-backup-run", "qingcloud.mysql"};
    public final static String[] OSS_TYPE = {"aliyun.oss", "huawei.obs", "tencent.cos", "aws.s3", "qiniu.kodo", "jdcloud.oss"};
    public final static String[] DISK_TYPE = {"aliyun.disk", "huawei.disk", "tencent.disk", "aws.ebs", "azure.disk", "jdcloud.disk", "baidu.volume"};
    public final static String[] IAM_TYPE = {"huawei.iam"};
    public final static String[] EIP_TYPE = {"aliyun.eip", "huawei.eip", "tencent.eip", "aws.network-addr", "azure.publicip", "gcp.app-engine-domain", "qingcloud.eip", "ucloud.eip", "jdcloud.eip", "ksyun.eip", "volc.eip", "vsphere.host", "openstack.network", "baidu.eip"};
    public final static String[] ELB_TYPE = {"aliyun.slb", "huawei.elb", "tencent.clb", "aws.elb", "azure.loadbalancer", "ucloud.ulb", "jdcloud.lb", "ksyun.slb", "baidu.blb", "baidu.app-blb"};
    public final static String[] SG_TYPE = {"aliyun.security-group", "huawei.security-group", "tencent.security-group", "aws.security-group", "azure.networksecuritygroup", "ucloud.securitygroup", "jdcloud.securitygroup", "volc.securitygroup", "openstack.security-group", "openstack.security-groups", "baidu.security-group"};
    public final static String[] VPC_TYPE = {"aliyun.vpc", "tencent.vpc", "aws.vpc", "huawei.vpc", "gcp.vpc", "azure.vnet"};
    public final static String[] REDIS_TYPE = {"aliyun.redis", "huawei.redis", "tencent.redis"};
    public final static String[] MONGODB_TYPE = {"aliyun.mongodb", "tencent.mongodb", "qingcloud.mongodb", "volc.mongodb"};
    public final static String[] POSTGRESQL_TYPE = {"aliyun.postgre-sql"};
    public final static String[] ES_TYPE = {"tencent.es"};

}
