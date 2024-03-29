package com.hummer.common.core.domain;

import java.io.Serializable;

public class CloudResourceRelaLink implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_resource_rela_link.id
     *
     * @mbg.generated Thu Jun 29 05:02:23 CST 2023
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_resource_rela_link.resource_item_id
     *
     * @mbg.generated Thu Jun 29 05:02:23 CST 2023
     */
    private String resourceItemId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_resource_rela_link.source
     *
     * @mbg.generated Thu Jun 29 05:02:23 CST 2023
     */
    private String source;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_resource_rela_link.target
     *
     * @mbg.generated Thu Jun 29 05:02:23 CST 2023
     */
    private String target;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_resource_rela_link.value
     *
     * @mbg.generated Thu Jun 29 05:02:23 CST 2023
     */
    private String value;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_resource_rela_link.category
     *
     * @mbg.generated Thu Jun 29 05:02:23 CST 2023
     */
    private String category;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_resource_rela_link.create_time
     *
     * @mbg.generated Thu Jun 29 05:02:23 CST 2023
     */
    private Long createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table cloud_resource_rela_link
     *
     * @mbg.generated Thu Jun 29 05:02:23 CST 2023
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cloud_resource_rela_link.id
     *
     * @return the value of cloud_resource_rela_link.id
     *
     * @mbg.generated Thu Jun 29 05:02:23 CST 2023
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cloud_resource_rela_link.id
     *
     * @param id the value for cloud_resource_rela_link.id
     *
     * @mbg.generated Thu Jun 29 05:02:23 CST 2023
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cloud_resource_rela_link.resource_item_id
     *
     * @return the value of cloud_resource_rela_link.resource_item_id
     *
     * @mbg.generated Thu Jun 29 05:02:23 CST 2023
     */
    public String getResourceItemId() {
        return resourceItemId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cloud_resource_rela_link.resource_item_id
     *
     * @param resourceItemId the value for cloud_resource_rela_link.resource_item_id
     *
     * @mbg.generated Thu Jun 29 05:02:23 CST 2023
     */
    public void setResourceItemId(String resourceItemId) {
        this.resourceItemId = resourceItemId == null ? null : resourceItemId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cloud_resource_rela_link.source
     *
     * @return the value of cloud_resource_rela_link.source
     *
     * @mbg.generated Thu Jun 29 05:02:23 CST 2023
     */
    public String getSource() {
        return source;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cloud_resource_rela_link.source
     *
     * @param source the value for cloud_resource_rela_link.source
     *
     * @mbg.generated Thu Jun 29 05:02:23 CST 2023
     */
    public void setSource(String source) {
        this.source = source == null ? null : source.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cloud_resource_rela_link.target
     *
     * @return the value of cloud_resource_rela_link.target
     *
     * @mbg.generated Thu Jun 29 05:02:23 CST 2023
     */
    public String getTarget() {
        return target;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cloud_resource_rela_link.target
     *
     * @param target the value for cloud_resource_rela_link.target
     *
     * @mbg.generated Thu Jun 29 05:02:23 CST 2023
     */
    public void setTarget(String target) {
        this.target = target == null ? null : target.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cloud_resource_rela_link.value
     *
     * @return the value of cloud_resource_rela_link.value
     *
     * @mbg.generated Thu Jun 29 05:02:23 CST 2023
     */
    public String getValue() {
        return value;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cloud_resource_rela_link.value
     *
     * @param value the value for cloud_resource_rela_link.value
     *
     * @mbg.generated Thu Jun 29 05:02:23 CST 2023
     */
    public void setValue(String value) {
        this.value = value == null ? null : value.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cloud_resource_rela_link.category
     *
     * @return the value of cloud_resource_rela_link.category
     *
     * @mbg.generated Thu Jun 29 05:02:23 CST 2023
     */
    public String getCategory() {
        return category;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cloud_resource_rela_link.category
     *
     * @param category the value for cloud_resource_rela_link.category
     *
     * @mbg.generated Thu Jun 29 05:02:23 CST 2023
     */
    public void setCategory(String category) {
        this.category = category == null ? null : category.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cloud_resource_rela_link.create_time
     *
     * @return the value of cloud_resource_rela_link.create_time
     *
     * @mbg.generated Thu Jun 29 05:02:23 CST 2023
     */
    public Long getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cloud_resource_rela_link.create_time
     *
     * @param createTime the value for cloud_resource_rela_link.create_time
     *
     * @mbg.generated Thu Jun 29 05:02:23 CST 2023
     */
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
