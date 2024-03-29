package com.hummer.system.domain;

import java.io.Serializable;

public class ReportResultLog implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column report_result_log.id
     *
     * @mbg.generated Mon May 15 07:46:00 CST 2023
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column report_result_log.result_id
     *
     * @mbg.generated Mon May 15 07:46:00 CST 2023
     */
    private String resultId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column report_result_log.status
     *
     * @mbg.generated Mon May 15 07:46:00 CST 2023
     */
    private String status;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column report_result_log.create_time
     *
     * @mbg.generated Mon May 15 07:46:00 CST 2023
     */
    private Long createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column report_result_log.operator
     *
     * @mbg.generated Mon May 15 07:46:00 CST 2023
     */
    private String operator;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column report_result_log.download_number
     *
     * @mbg.generated Mon May 15 07:46:00 CST 2023
     */
    private Long downloadNumber;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column report_result_log.pdf_path
     *
     * @mbg.generated Mon May 15 07:46:00 CST 2023
     */
    private String pdfPath;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column report_result_log.pdf_log
     *
     * @mbg.generated Mon May 15 07:46:00 CST 2023
     */
    private String pdfLog;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table report_result_log
     *
     * @mbg.generated Mon May 15 07:46:00 CST 2023
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column report_result_log.id
     *
     * @return the value of report_result_log.id
     *
     * @mbg.generated Mon May 15 07:46:00 CST 2023
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column report_result_log.id
     *
     * @param id the value for report_result_log.id
     *
     * @mbg.generated Mon May 15 07:46:00 CST 2023
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column report_result_log.result_id
     *
     * @return the value of report_result_log.result_id
     *
     * @mbg.generated Mon May 15 07:46:00 CST 2023
     */
    public String getResultId() {
        return resultId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column report_result_log.result_id
     *
     * @param resultId the value for report_result_log.result_id
     *
     * @mbg.generated Mon May 15 07:46:00 CST 2023
     */
    public void setResultId(String resultId) {
        this.resultId = resultId == null ? null : resultId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column report_result_log.status
     *
     * @return the value of report_result_log.status
     *
     * @mbg.generated Mon May 15 07:46:00 CST 2023
     */
    public String getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column report_result_log.status
     *
     * @param status the value for report_result_log.status
     *
     * @mbg.generated Mon May 15 07:46:00 CST 2023
     */
    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column report_result_log.create_time
     *
     * @return the value of report_result_log.create_time
     *
     * @mbg.generated Mon May 15 07:46:00 CST 2023
     */
    public Long getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column report_result_log.create_time
     *
     * @param createTime the value for report_result_log.create_time
     *
     * @mbg.generated Mon May 15 07:46:00 CST 2023
     */
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column report_result_log.operator
     *
     * @return the value of report_result_log.operator
     *
     * @mbg.generated Mon May 15 07:46:00 CST 2023
     */
    public String getOperator() {
        return operator;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column report_result_log.operator
     *
     * @param operator the value for report_result_log.operator
     *
     * @mbg.generated Mon May 15 07:46:00 CST 2023
     */
    public void setOperator(String operator) {
        this.operator = operator == null ? null : operator.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column report_result_log.download_number
     *
     * @return the value of report_result_log.download_number
     *
     * @mbg.generated Mon May 15 07:46:00 CST 2023
     */
    public Long getDownloadNumber() {
        return downloadNumber;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column report_result_log.download_number
     *
     * @param downloadNumber the value for report_result_log.download_number
     *
     * @mbg.generated Mon May 15 07:46:00 CST 2023
     */
    public void setDownloadNumber(Long downloadNumber) {
        this.downloadNumber = downloadNumber;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column report_result_log.pdf_path
     *
     * @return the value of report_result_log.pdf_path
     *
     * @mbg.generated Mon May 15 07:46:00 CST 2023
     */
    public String getPdfPath() {
        return pdfPath;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column report_result_log.pdf_path
     *
     * @param pdfPath the value for report_result_log.pdf_path
     *
     * @mbg.generated Mon May 15 07:46:00 CST 2023
     */
    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath == null ? null : pdfPath.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column report_result_log.pdf_log
     *
     * @return the value of report_result_log.pdf_log
     *
     * @mbg.generated Mon May 15 07:46:00 CST 2023
     */
    public String getPdfLog() {
        return pdfLog;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column report_result_log.pdf_log
     *
     * @param pdfLog the value for report_result_log.pdf_log
     *
     * @mbg.generated Mon May 15 07:46:00 CST 2023
     */
    public void setPdfLog(String pdfLog) {
        this.pdfLog = pdfLog == null ? null : pdfLog.trim();
    }
}