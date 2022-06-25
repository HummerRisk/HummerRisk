package com.hummerrisk.commons.constants;

/**
 * @author harris
 */
public class CloudTaskConstants {

    public enum TASK_STATUS {
        DRAFT, UNCHECKED, APPROVED, FINISHED, TERMINATED, CANCELED, REJECTED, PROCESSING, ERROR, WARNING, RUNNING, PENDING, PAUSE
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
        Normal, LowRisk, MediumRisk, HighRisk
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

    public final static String CUSTODIAN_RUN_RESULT_FILE = "custodian-run.log";
    public final static String METADATA_RESULT_FILE = "metadata.json";
    public final static String RESOURCES_RESULT_FILE = "resources.json";
    public final static String NUCLEI_RUN_RESULT_FILE = "result.txt";
    public final static String PROWLER_RUN_RESULT_FILE = "result.txt";
    public final static String XRAY_RUN_RESULT_FILE = "result.json";

    public final static String RESULT_FILE_PATH = "/tmp/{task_id}/policy.yml";
    public final static String RESULT_FILE_PATH_PREFIX = "/tmp/";
    public final static String PROWLER_RESULT_FILE_PATH = "/prowler";//本地启动用 /tmp
    public final static String PROWLER_CONFIG_FILE_PATH = "/root/.aws";//本地启动用 ~/.aws
    public final static String XRAY_RESULT_FILE_PATH = "/opt/hummerrisk/xray/";//本地启动用 /tmp
}
