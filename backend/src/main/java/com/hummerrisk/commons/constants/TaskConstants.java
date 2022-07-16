package com.hummerrisk.commons.constants;

/**
 * @author harris
 */
public class TaskConstants {

    public enum TASK_STATUS {
        DRAFT, UNCHECKED, APPROVED, FINISHED, TERMINATED, CANCELED, REJECTED, PROCESSING, ERROR, WARNING, RUNNING, PENDING, PAUSE, WAITING
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

    public enum RuleType {
        rule, tag, group
    }

}
