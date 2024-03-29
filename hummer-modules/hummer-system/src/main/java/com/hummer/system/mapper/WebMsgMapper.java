package com.hummer.system.mapper;

import com.hummer.common.core.domain.WebMsg;
import com.hummer.common.core.domain.WebMsgExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WebMsgMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table web_msg
     *
     * @mbg.generated Sat Dec 03 06:51:50 CST 2022
     */
    long countByExample(WebMsgExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table web_msg
     *
     * @mbg.generated Sat Dec 03 06:51:50 CST 2022
     */
    int deleteByExample(WebMsgExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table web_msg
     *
     * @mbg.generated Sat Dec 03 06:51:50 CST 2022
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table web_msg
     *
     * @mbg.generated Sat Dec 03 06:51:50 CST 2022
     */
    int insert(WebMsg record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table web_msg
     *
     * @mbg.generated Sat Dec 03 06:51:50 CST 2022
     */
    int insertSelective(WebMsg record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table web_msg
     *
     * @mbg.generated Sat Dec 03 06:51:50 CST 2022
     */
    List<WebMsg> selectByExample(WebMsgExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table web_msg
     *
     * @mbg.generated Sat Dec 03 06:51:50 CST 2022
     */
    WebMsg selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table web_msg
     *
     * @mbg.generated Sat Dec 03 06:51:50 CST 2022
     */
    int updateByExampleSelective(@Param("record") WebMsg record, @Param("example") WebMsgExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table web_msg
     *
     * @mbg.generated Sat Dec 03 06:51:50 CST 2022
     */
    int updateByExample(@Param("record") WebMsg record, @Param("example") WebMsgExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table web_msg
     *
     * @mbg.generated Sat Dec 03 06:51:50 CST 2022
     */
    int updateByPrimaryKeySelective(WebMsg record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table web_msg
     *
     * @mbg.generated Sat Dec 03 06:51:50 CST 2022
     */
    int updateByPrimaryKey(WebMsg record);
}
