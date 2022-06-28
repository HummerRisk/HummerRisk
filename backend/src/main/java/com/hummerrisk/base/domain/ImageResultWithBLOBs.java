package com.hummerrisk.base.domain;

import java.io.Serializable;

public class ImageResultWithBLOBs extends ImageResult implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column image_result.return_log
     *
     * @mbg.generated Tue Jun 28 11:44:06 CST 2022
     */
    private String returnLog;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column image_result.grype_table
     *
     * @mbg.generated Tue Jun 28 11:44:06 CST 2022
     */
    private String grypeTable;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column image_result.grype_json
     *
     * @mbg.generated Tue Jun 28 11:44:06 CST 2022
     */
    private String grypeJson;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column image_result.syft_table
     *
     * @mbg.generated Tue Jun 28 11:44:06 CST 2022
     */
    private String syftTable;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column image_result.syft_json
     *
     * @mbg.generated Tue Jun 28 11:44:06 CST 2022
     */
    private String syftJson;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table image_result
     *
     * @mbg.generated Tue Jun 28 11:44:06 CST 2022
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column image_result.return_log
     *
     * @return the value of image_result.return_log
     *
     * @mbg.generated Tue Jun 28 11:44:06 CST 2022
     */
    public String getReturnLog() {
        return returnLog;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column image_result.return_log
     *
     * @param returnLog the value for image_result.return_log
     *
     * @mbg.generated Tue Jun 28 11:44:06 CST 2022
     */
    public void setReturnLog(String returnLog) {
        this.returnLog = returnLog == null ? null : returnLog.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column image_result.grype_table
     *
     * @return the value of image_result.grype_table
     *
     * @mbg.generated Tue Jun 28 11:44:06 CST 2022
     */
    public String getGrypeTable() {
        return grypeTable;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column image_result.grype_table
     *
     * @param grypeTable the value for image_result.grype_table
     *
     * @mbg.generated Tue Jun 28 11:44:06 CST 2022
     */
    public void setGrypeTable(String grypeTable) {
        this.grypeTable = grypeTable == null ? null : grypeTable.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column image_result.grype_json
     *
     * @return the value of image_result.grype_json
     *
     * @mbg.generated Tue Jun 28 11:44:06 CST 2022
     */
    public String getGrypeJson() {
        return grypeJson;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column image_result.grype_json
     *
     * @param grypeJson the value for image_result.grype_json
     *
     * @mbg.generated Tue Jun 28 11:44:06 CST 2022
     */
    public void setGrypeJson(String grypeJson) {
        this.grypeJson = grypeJson == null ? null : grypeJson.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column image_result.syft_table
     *
     * @return the value of image_result.syft_table
     *
     * @mbg.generated Tue Jun 28 11:44:06 CST 2022
     */
    public String getSyftTable() {
        return syftTable;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column image_result.syft_table
     *
     * @param syftTable the value for image_result.syft_table
     *
     * @mbg.generated Tue Jun 28 11:44:06 CST 2022
     */
    public void setSyftTable(String syftTable) {
        this.syftTable = syftTable == null ? null : syftTable.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column image_result.syft_json
     *
     * @return the value of image_result.syft_json
     *
     * @mbg.generated Tue Jun 28 11:44:06 CST 2022
     */
    public String getSyftJson() {
        return syftJson;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column image_result.syft_json
     *
     * @param syftJson the value for image_result.syft_json
     *
     * @mbg.generated Tue Jun 28 11:44:06 CST 2022
     */
    public void setSyftJson(String syftJson) {
        this.syftJson = syftJson == null ? null : syftJson.trim();
    }
}