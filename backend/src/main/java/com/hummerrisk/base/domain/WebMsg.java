package com.hummerrisk.base.domain;

import java.io.Serializable;

public class WebMsg implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column web_msg.id
     *
     * @mbg.generated Sun Jul 10 14:04:32 CST 2022
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column web_msg.user_id
     *
     * @mbg.generated Sun Jul 10 14:04:32 CST 2022
     */
    private String userId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column web_msg.type
     *
     * @mbg.generated Sun Jul 10 14:04:32 CST 2022
     */
    private String type;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column web_msg.status
     *
     * @mbg.generated Sun Jul 10 14:04:32 CST 2022
     */
    private Boolean status;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column web_msg.create_time
     *
     * @mbg.generated Sun Jul 10 14:04:32 CST 2022
     */
    private Long createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column web_msg.read_time
     *
     * @mbg.generated Sun Jul 10 14:04:32 CST 2022
     */
    private Long readTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column web_msg.content
     *
     * @mbg.generated Sun Jul 10 14:04:32 CST 2022
     */
    private String content;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column web_msg.scan_type
     *
     * @mbg.generated Sun Jul 10 14:04:32 CST 2022
     */
    private String scanType;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table web_msg
     *
     * @mbg.generated Sun Jul 10 14:04:32 CST 2022
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column web_msg.id
     *
     * @return the value of web_msg.id
     *
     * @mbg.generated Sun Jul 10 14:04:32 CST 2022
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column web_msg.id
     *
     * @param id the value for web_msg.id
     *
     * @mbg.generated Sun Jul 10 14:04:32 CST 2022
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column web_msg.user_id
     *
     * @return the value of web_msg.user_id
     *
     * @mbg.generated Sun Jul 10 14:04:32 CST 2022
     */
    public String getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column web_msg.user_id
     *
     * @param userId the value for web_msg.user_id
     *
     * @mbg.generated Sun Jul 10 14:04:32 CST 2022
     */
    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column web_msg.type
     *
     * @return the value of web_msg.type
     *
     * @mbg.generated Sun Jul 10 14:04:32 CST 2022
     */
    public String getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column web_msg.type
     *
     * @param type the value for web_msg.type
     *
     * @mbg.generated Sun Jul 10 14:04:32 CST 2022
     */
    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column web_msg.status
     *
     * @return the value of web_msg.status
     *
     * @mbg.generated Sun Jul 10 14:04:32 CST 2022
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column web_msg.status
     *
     * @param status the value for web_msg.status
     *
     * @mbg.generated Sun Jul 10 14:04:32 CST 2022
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column web_msg.create_time
     *
     * @return the value of web_msg.create_time
     *
     * @mbg.generated Sun Jul 10 14:04:32 CST 2022
     */
    public Long getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column web_msg.create_time
     *
     * @param createTime the value for web_msg.create_time
     *
     * @mbg.generated Sun Jul 10 14:04:32 CST 2022
     */
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column web_msg.read_time
     *
     * @return the value of web_msg.read_time
     *
     * @mbg.generated Sun Jul 10 14:04:32 CST 2022
     */
    public Long getReadTime() {
        return readTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column web_msg.read_time
     *
     * @param readTime the value for web_msg.read_time
     *
     * @mbg.generated Sun Jul 10 14:04:32 CST 2022
     */
    public void setReadTime(Long readTime) {
        this.readTime = readTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column web_msg.content
     *
     * @return the value of web_msg.content
     *
     * @mbg.generated Sun Jul 10 14:04:32 CST 2022
     */
    public String getContent() {
        return content;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column web_msg.content
     *
     * @param content the value for web_msg.content
     *
     * @mbg.generated Sun Jul 10 14:04:32 CST 2022
     */
    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column web_msg.scan_type
     *
     * @return the value of web_msg.scan_type
     *
     * @mbg.generated Sun Jul 10 14:04:32 CST 2022
     */
    public String getScanType() {
        return scanType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column web_msg.scan_type
     *
     * @param scanType the value for web_msg.scan_type
     *
     * @mbg.generated Sun Jul 10 14:04:32 CST 2022
     */
    public void setScanType(String scanType) {
        this.scanType = scanType == null ? null : scanType.trim();
    }
}