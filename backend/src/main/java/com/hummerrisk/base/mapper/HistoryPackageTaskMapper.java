package com.hummerrisk.base.mapper;

import com.hummerrisk.base.domain.HistoryPackageTask;
import com.hummerrisk.base.domain.HistoryPackageTaskExample;
import com.hummerrisk.base.domain.HistoryPackageTaskWithBLOBs;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HistoryPackageTaskMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_package_task
     *
     * @mbg.generated Tue Jul 19 04:14:43 CST 2022
     */
    long countByExample(HistoryPackageTaskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_package_task
     *
     * @mbg.generated Tue Jul 19 04:14:43 CST 2022
     */
    int deleteByExample(HistoryPackageTaskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_package_task
     *
     * @mbg.generated Tue Jul 19 04:14:43 CST 2022
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_package_task
     *
     * @mbg.generated Tue Jul 19 04:14:43 CST 2022
     */
    int insert(HistoryPackageTaskWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_package_task
     *
     * @mbg.generated Tue Jul 19 04:14:43 CST 2022
     */
    int insertSelective(HistoryPackageTaskWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_package_task
     *
     * @mbg.generated Tue Jul 19 04:14:43 CST 2022
     */
    List<HistoryPackageTaskWithBLOBs> selectByExampleWithBLOBs(HistoryPackageTaskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_package_task
     *
     * @mbg.generated Tue Jul 19 04:14:43 CST 2022
     */
    List<HistoryPackageTask> selectByExample(HistoryPackageTaskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_package_task
     *
     * @mbg.generated Tue Jul 19 04:14:43 CST 2022
     */
    HistoryPackageTaskWithBLOBs selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_package_task
     *
     * @mbg.generated Tue Jul 19 04:14:43 CST 2022
     */
    int updateByExampleSelective(@Param("record") HistoryPackageTaskWithBLOBs record, @Param("example") HistoryPackageTaskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_package_task
     *
     * @mbg.generated Tue Jul 19 04:14:43 CST 2022
     */
    int updateByExampleWithBLOBs(@Param("record") HistoryPackageTaskWithBLOBs record, @Param("example") HistoryPackageTaskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_package_task
     *
     * @mbg.generated Tue Jul 19 04:14:43 CST 2022
     */
    int updateByExample(@Param("record") HistoryPackageTask record, @Param("example") HistoryPackageTaskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_package_task
     *
     * @mbg.generated Tue Jul 19 04:14:43 CST 2022
     */
    int updateByPrimaryKeySelective(HistoryPackageTaskWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_package_task
     *
     * @mbg.generated Tue Jul 19 04:14:43 CST 2022
     */
    int updateByPrimaryKeyWithBLOBs(HistoryPackageTaskWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_package_task
     *
     * @mbg.generated Tue Jul 19 04:14:43 CST 2022
     */
    int updateByPrimaryKey(HistoryPackageTask record);
}
