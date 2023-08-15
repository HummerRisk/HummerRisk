package com.hummer.common.core.domain;

import java.io.Serializable;

public class CloudProjectLog implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_project_log.id
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    private String id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_project_log.project_id
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    private String projectId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_project_log.init_time
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    private String initTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_project_log.exec_time
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    private String execTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_project_log.create_time
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    private Long createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_project_log.operator
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    private String operator;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_project_log.result
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    private Boolean result;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table cloud_project_log
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cloud_project_log.id
     *
     * @return the value of cloud_project_log.id
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    public String getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cloud_project_log.id
     *
     * @param id the value for cloud_project_log.id
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cloud_project_log.project_id
     *
     * @return the value of cloud_project_log.project_id
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cloud_project_log.project_id
     *
     * @param projectId the value for cloud_project_log.project_id
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId == null ? null : projectId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cloud_project_log.init_time
     *
     * @return the value of cloud_project_log.init_time
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    public String getInitTime() {
        return initTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cloud_project_log.init_time
     *
     * @param initTime the value for cloud_project_log.init_time
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    public void setInitTime(String initTime) {
        this.initTime = initTime == null ? null : initTime.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cloud_project_log.exec_time
     *
     * @return the value of cloud_project_log.exec_time
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    public String getExecTime() {
        return execTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cloud_project_log.exec_time
     *
     * @param execTime the value for cloud_project_log.exec_time
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    public void setExecTime(String execTime) {
        this.execTime = execTime == null ? null : execTime.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cloud_project_log.create_time
     *
     * @return the value of cloud_project_log.create_time
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    public Long getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cloud_project_log.create_time
     *
     * @param createTime the value for cloud_project_log.create_time
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cloud_project_log.operator
     *
     * @return the value of cloud_project_log.operator
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    public String getOperator() {
        return operator;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cloud_project_log.operator
     *
     * @param operator the value for cloud_project_log.operator
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    public void setOperator(String operator) {
        this.operator = operator == null ? null : operator.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cloud_project_log.result
     *
     * @return the value of cloud_project_log.result
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    public Boolean getResult() {
        return result;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cloud_project_log.result
     *
     * @param result the value for cloud_project_log.result
     *
     * @mbg.generated Fri Aug 11 15:18:52 CST 2023
     */
    public void setResult(Boolean result) {
        this.result = result;
    }
}