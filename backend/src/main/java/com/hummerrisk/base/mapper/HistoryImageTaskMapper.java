package com.hummerrisk.base.mapper;

import com.hummerrisk.base.domain.HistoryImageTask;
import com.hummerrisk.base.domain.HistoryImageTaskExample;
import com.hummerrisk.base.domain.HistoryImageTaskWithBLOBs;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HistoryImageTaskMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_image_task
     *
     * @mbg.generated Fri Aug 19 02:23:42 CST 2022
     */
    long countByExample(HistoryImageTaskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_image_task
     *
     * @mbg.generated Fri Aug 19 02:23:42 CST 2022
     */
    int deleteByExample(HistoryImageTaskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_image_task
     *
     * @mbg.generated Fri Aug 19 02:23:42 CST 2022
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_image_task
     *
     * @mbg.generated Fri Aug 19 02:23:42 CST 2022
     */
    int insert(HistoryImageTaskWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_image_task
     *
     * @mbg.generated Fri Aug 19 02:23:42 CST 2022
     */
    int insertSelective(HistoryImageTaskWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_image_task
     *
     * @mbg.generated Fri Aug 19 02:23:42 CST 2022
     */
    List<HistoryImageTaskWithBLOBs> selectByExampleWithBLOBs(HistoryImageTaskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_image_task
     *
     * @mbg.generated Fri Aug 19 02:23:42 CST 2022
     */
    List<HistoryImageTask> selectByExample(HistoryImageTaskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_image_task
     *
     * @mbg.generated Fri Aug 19 02:23:42 CST 2022
     */
    HistoryImageTaskWithBLOBs selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_image_task
     *
     * @mbg.generated Fri Aug 19 02:23:42 CST 2022
     */
    int updateByExampleSelective(@Param("record") HistoryImageTaskWithBLOBs record, @Param("example") HistoryImageTaskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_image_task
     *
     * @mbg.generated Fri Aug 19 02:23:42 CST 2022
     */
    int updateByExampleWithBLOBs(@Param("record") HistoryImageTaskWithBLOBs record, @Param("example") HistoryImageTaskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_image_task
     *
     * @mbg.generated Fri Aug 19 02:23:42 CST 2022
     */
    int updateByExample(@Param("record") HistoryImageTask record, @Param("example") HistoryImageTaskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_image_task
     *
     * @mbg.generated Fri Aug 19 02:23:42 CST 2022
     */
    int updateByPrimaryKeySelective(HistoryImageTaskWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_image_task
     *
     * @mbg.generated Fri Aug 19 02:23:42 CST 2022
     */
    int updateByPrimaryKeyWithBLOBs(HistoryImageTaskWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table history_image_task
     *
     * @mbg.generated Fri Aug 19 02:23:42 CST 2022
     */
    int updateByPrimaryKey(HistoryImageTask record);
}
