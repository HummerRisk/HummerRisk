package com.hummer.common.core.domain;

import java.io.Serializable;

public class HistoryScanTask implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column history_scan_task.id
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column history_scan_task.scan_id
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    private Integer scanId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column history_scan_task.task_id
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    private String taskId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column history_scan_task.operation
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    private String operation;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column history_scan_task.resources_sum
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    private Long resourcesSum;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column history_scan_task.return_sum
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    private Long returnSum;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column history_scan_task.scan_score
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    private Integer scanScore;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column history_scan_task.account_type
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    private String accountType;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column history_scan_task.status
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    private String status;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column history_scan_task.account_id
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    private String accountId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column history_scan_task.output
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    private String output;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table history_scan_task
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column history_scan_task.id
     *
     * @return the value of history_scan_task.id
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column history_scan_task.id
     *
     * @param id the value for history_scan_task.id
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column history_scan_task.scan_id
     *
     * @return the value of history_scan_task.scan_id
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    public Integer getScanId() {
        return scanId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column history_scan_task.scan_id
     *
     * @param scanId the value for history_scan_task.scan_id
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    public void setScanId(Integer scanId) {
        this.scanId = scanId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column history_scan_task.task_id
     *
     * @return the value of history_scan_task.task_id
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column history_scan_task.task_id
     *
     * @param taskId the value for history_scan_task.task_id
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId == null ? null : taskId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column history_scan_task.operation
     *
     * @return the value of history_scan_task.operation
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    public String getOperation() {
        return operation;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column history_scan_task.operation
     *
     * @param operation the value for history_scan_task.operation
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    public void setOperation(String operation) {
        this.operation = operation == null ? null : operation.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column history_scan_task.resources_sum
     *
     * @return the value of history_scan_task.resources_sum
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    public Long getResourcesSum() {
        return resourcesSum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column history_scan_task.resources_sum
     *
     * @param resourcesSum the value for history_scan_task.resources_sum
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    public void setResourcesSum(Long resourcesSum) {
        this.resourcesSum = resourcesSum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column history_scan_task.return_sum
     *
     * @return the value of history_scan_task.return_sum
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    public Long getReturnSum() {
        return returnSum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column history_scan_task.return_sum
     *
     * @param returnSum the value for history_scan_task.return_sum
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    public void setReturnSum(Long returnSum) {
        this.returnSum = returnSum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column history_scan_task.scan_score
     *
     * @return the value of history_scan_task.scan_score
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    public Integer getScanScore() {
        return scanScore;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column history_scan_task.scan_score
     *
     * @param scanScore the value for history_scan_task.scan_score
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    public void setScanScore(Integer scanScore) {
        this.scanScore = scanScore;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column history_scan_task.account_type
     *
     * @return the value of history_scan_task.account_type
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    public String getAccountType() {
        return accountType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column history_scan_task.account_type
     *
     * @param accountType the value for history_scan_task.account_type
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    public void setAccountType(String accountType) {
        this.accountType = accountType == null ? null : accountType.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column history_scan_task.status
     *
     * @return the value of history_scan_task.status
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    public String getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column history_scan_task.status
     *
     * @param status the value for history_scan_task.status
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column history_scan_task.account_id
     *
     * @return the value of history_scan_task.account_id
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column history_scan_task.account_id
     *
     * @param accountId the value for history_scan_task.account_id
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId == null ? null : accountId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column history_scan_task.output
     *
     * @return the value of history_scan_task.output
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    public String getOutput() {
        return output;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column history_scan_task.output
     *
     * @param output the value for history_scan_task.output
     *
     * @mbg.generated Tue Jul 19 22:52:54 CST 2022
     */
    public void setOutput(String output) {
        this.output = output == null ? null : output.trim();
    }
}
