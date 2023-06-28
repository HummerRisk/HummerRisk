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
    public final static String[] ALIYUN_RESOURCE_TYPE = {"aliyun.cdn", "aliyun.disk", "aliyun.ecs", "aliyun.eip", "aliyun.mongodb", "aliyun.oss", "aliyun.polardb", "aliyun.ram", "aliyun.rds", "aliyun.redis", "aliyun.security-group", "aliyun.slb", "aliyun.nas", "aliyun.mse", "aliyun.ack", "aliyun.vpc", "aliyun.event", "aliyun.postgre-sql"};
    public final static String[] HUAWEI_RESOURCE_TYPE = {"huawei.dds","huawei.disk","huawei.ecs","huawei.eip","huawei.elb","huawei.gaussdb","huawei.gaussdbfornosql","huawei.gaussdbforopengauss","huawei.iam","huawei.obs","huawei.rds","huawei.redis","huawei.security-group"};
    public final static String[] TENCENT_RESOURCE_TYPE = {"tencent.vpc","tencent.es","tencent.cdb","tencent.clb","tencent.cos","tencent.cvm","tencent.disk","tencent.eip","tencent.mongodb","tencent.redis","tencent.security-group"};
    public final static String[] AWS_RESOURCE_TYPE = {"aws.ebs","aws.ec2","aws.elb","aws.network-addr","aws.rds","aws.s3","aws.security-group"};
    public final static String[] AZURE_RESOURCE_TYPE = {"azure.cosmosdb","azure.disk","azure.loadbalancer","azure.networkinterface","azure.networksecuritygroup","azure.publicip","azure.resourcegroup","azure.sqldatabase","azure.sqlserver","azure.storage","azure.vm","azure.webapp"};
    public final static String[] GCP_RESOURCE_TYPE = {"gcp.app-engine-certificate","gcp.app-engine-domain","gcp.app-engine-firewall-ingress-rule","gcp.dm-deployment","gcp.dns-managed-zone","gcp.dns-policy","gcp.instance-template","gcp.loadbalancer-ssl-policy","gcp.sql-backup-run"};
    public final static String[] QINGCLOUD_RESOURCE_TYPE = {"qingcloud.ecs","qingcloud.eip","qingcloud.mongodb","qingcloud.mysql"};
    public final static String[] UCLOUD_RESOURCE_TYPE = {"ucloud.eip","ucloud.securitygroup","ucloud.uhost","ucloud.ulb"};
    public final static String[] K8S_RESOURCE_TYPE = {"k8s.namespace","k8s.config-map","k8s.daemon-set","k8s.deployment","k8s.node","k8s.pod","k8s.replica-set","k8s.replication-controller","k8s.secret","k8s.service","k8s.service-account","k8s.stateful-set","k8s.volume","k8s.volume-claim","k8s.controller-revision","k8s.endpoint"};
    public final static String[] QINIU_RESOURCE_TYPE = {"qiniu.kodo"};
    public final static String[] JDCLOUD_RESOURCE_TYPE = {"jdcloud.vm", "jdcloud.eip", "jdcloud.cdn","jdcloud.disk","jdcloud.lb","jdcloud.securitygroup","jdcloud.oss"};
    public final static String[] KSYUN_RESOURCE_TYPE = {"ksyun.kec","ksyun.slb","ksyun.eip"};
    public final static String[] VOLC_RESOURCE_TYPE = {"volc.cdn","volc.ecs","volc.eip","volc.mongodb","volc.securitygroup"};
    public final static String[] VSPHERE_RESOURCE_TYPE = {"vsphere.cluster","vsphere.datacenter","vsphere.datastore","vsphere.host","vsphere.network","vsphere.resourcepool","vsphere.vm"};
    public final static String[] OPENSTACK_RESOURCE_TYPE = {"openstack.flavor","openstack.image","openstack.network","openstack.project","openstack.router","openstack.security-group","openstack.security-groups","openstack.server","openstack.user","openstack.volume"};
    public final static String[] BAIDU_RESOURCE_TYPE = {"baidu.app-blb","baidu.bbc","baidu.blb","baidu.cdn","baidu.eip","baidu.instance","baidu.security-group","baidu.volume"};

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
}
