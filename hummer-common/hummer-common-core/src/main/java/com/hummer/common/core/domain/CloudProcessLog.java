package com.hummer.common.core.domain;

import java.io.Serializable;

public class CloudProcessLog implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_process_log.id
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    private String id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_process_log.project_id
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    private String projectId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_process_log.process_id
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    private String processId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_process_log.create_time
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    private Long createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_process_log.operator
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    private String operator;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_process_log.result
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    private Boolean result;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table cloud_process_log
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cloud_process_log.id
     *
     * @return the value of cloud_process_log.id
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    public String getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cloud_process_log.id
     *
     * @param id the value for cloud_process_log.id
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cloud_process_log.project_id
     *
     * @return the value of cloud_process_log.project_id
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cloud_process_log.project_id
     *
     * @param projectId the value for cloud_process_log.project_id
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId == null ? null : projectId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cloud_process_log.process_id
     *
     * @return the value of cloud_process_log.process_id
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    public String getProcessId() {
        return processId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cloud_process_log.process_id
     *
     * @param processId the value for cloud_process_log.process_id
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    public void setProcessId(String processId) {
        this.processId = processId == null ? null : processId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cloud_process_log.create_time
     *
     * @return the value of cloud_process_log.create_time
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    public Long getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cloud_process_log.create_time
     *
     * @param createTime the value for cloud_process_log.create_time
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cloud_process_log.operator
     *
     * @return the value of cloud_process_log.operator
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    public String getOperator() {
        return operator;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cloud_process_log.operator
     *
     * @param operator the value for cloud_process_log.operator
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    public void setOperator(String operator) {
        this.operator = operator == null ? null : operator.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cloud_process_log.result
     *
     * @return the value of cloud_process_log.result
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    public Boolean getResult() {
        return result;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cloud_process_log.result
     *
     * @param result the value for cloud_process_log.result
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    public void setResult(Boolean result) {
        this.result = result;
    }
}