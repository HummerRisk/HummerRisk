package com.hummer.system.mapper;

import com.hummer.system.domain.ReportResultDetail;
import com.hummer.system.domain.ReportResultDetailExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ReportResultDetailMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table report_result_detail
     *
     * @mbg.generated Mon May 15 05:35:17 CST 2023
     */
    long countByExample(ReportResultDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table report_result_detail
     *
     * @mbg.generated Mon May 15 05:35:17 CST 2023
     */
    int deleteByExample(ReportResultDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table report_result_detail
     *
     * @mbg.generated Mon May 15 05:35:17 CST 2023
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table report_result_detail
     *
     * @mbg.generated Mon May 15 05:35:17 CST 2023
     */
    int insert(ReportResultDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table report_result_detail
     *
     * @mbg.generated Mon May 15 05:35:17 CST 2023
     */
    int insertSelective(ReportResultDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table report_result_detail
     *
     * @mbg.generated Mon May 15 05:35:17 CST 2023
     */
    List<ReportResultDetail> selectByExample(ReportResultDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table report_result_detail
     *
     * @mbg.generated Mon May 15 05:35:17 CST 2023
     */
    ReportResultDetail selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table report_result_detail
     *
     * @mbg.generated Mon May 15 05:35:17 CST 2023
     */
    int updateByExampleSelective(@Param("record") ReportResultDetail record, @Param("example") ReportResultDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table report_result_detail
     *
     * @mbg.generated Mon May 15 05:35:17 CST 2023
     */
    int updateByExample(@Param("record") ReportResultDetail record, @Param("example") ReportResultDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table report_result_detail
     *
     * @mbg.generated Mon May 15 05:35:17 CST 2023
     */
    int updateByPrimaryKeySelective(ReportResultDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table report_result_detail
     *
     * @mbg.generated Mon May 15 05:35:17 CST 2023
     */
    int updateByPrimaryKey(ReportResultDetail record);
}
