package com.hummerrisk.base.domain;

import java.io.Serializable;

public class TaskItem implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_item.id
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    private String id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_item.task_id
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    private String taskId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_item.source_id
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    private String sourceId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_item.rule_type
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    private String ruleType;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_item.rule_name
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    private String ruleName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_item.rule_desc
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    private String ruleDesc;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_item.status
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    private String status;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_item.icon
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    private String icon;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_item.severity
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    private String severity;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_item.account_id
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    private String accountId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_item.account_name
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    private String accountName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_item.account_type
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    private String accountType;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_item.task_order
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    private String taskOrder;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_item.create_time
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    private Long createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table task_item
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_item.id
     *
     * @return the value of task_item.id
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public String getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_item.id
     *
     * @param id the value for task_item.id
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_item.task_id
     *
     * @return the value of task_item.task_id
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_item.task_id
     *
     * @param taskId the value for task_item.task_id
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId == null ? null : taskId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_item.source_id
     *
     * @return the value of task_item.source_id
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public String getSourceId() {
        return sourceId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_item.source_id
     *
     * @param sourceId the value for task_item.source_id
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId == null ? null : sourceId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_item.rule_type
     *
     * @return the value of task_item.rule_type
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public String getRuleType() {
        return ruleType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_item.rule_type
     *
     * @param ruleType the value for task_item.rule_type
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public void setRuleType(String ruleType) {
        this.ruleType = ruleType == null ? null : ruleType.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_item.rule_name
     *
     * @return the value of task_item.rule_name
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public String getRuleName() {
        return ruleName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_item.rule_name
     *
     * @param ruleName the value for task_item.rule_name
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName == null ? null : ruleName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_item.rule_desc
     *
     * @return the value of task_item.rule_desc
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public String getRuleDesc() {
        return ruleDesc;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_item.rule_desc
     *
     * @param ruleDesc the value for task_item.rule_desc
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public void setRuleDesc(String ruleDesc) {
        this.ruleDesc = ruleDesc == null ? null : ruleDesc.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_item.status
     *
     * @return the value of task_item.status
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public String getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_item.status
     *
     * @param status the value for task_item.status
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_item.icon
     *
     * @return the value of task_item.icon
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public String getIcon() {
        return icon;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_item.icon
     *
     * @param icon the value for task_item.icon
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public void setIcon(String icon) {
        this.icon = icon == null ? null : icon.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_item.severity
     *
     * @return the value of task_item.severity
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public String getSeverity() {
        return severity;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_item.severity
     *
     * @param severity the value for task_item.severity
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public void setSeverity(String severity) {
        this.severity = severity == null ? null : severity.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_item.account_id
     *
     * @return the value of task_item.account_id
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_item.account_id
     *
     * @param accountId the value for task_item.account_id
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId == null ? null : accountId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_item.account_name
     *
     * @return the value of task_item.account_name
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_item.account_name
     *
     * @param accountName the value for task_item.account_name
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public void setAccountName(String accountName) {
        this.accountName = accountName == null ? null : accountName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_item.account_type
     *
     * @return the value of task_item.account_type
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public String getAccountType() {
        return accountType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_item.account_type
     *
     * @param accountType the value for task_item.account_type
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public void setAccountType(String accountType) {
        this.accountType = accountType == null ? null : accountType.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_item.task_order
     *
     * @return the value of task_item.task_order
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public String getTaskOrder() {
        return taskOrder;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_item.task_order
     *
     * @param taskOrder the value for task_item.task_order
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public void setTaskOrder(String taskOrder) {
        this.taskOrder = taskOrder == null ? null : taskOrder.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_item.create_time
     *
     * @return the value of task_item.create_time
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public Long getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_item.create_time
     *
     * @param createTime the value for task_item.create_time
     *
     * @mbg.generated Fri Jul 15 15:32:44 CST 2022
     */
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}