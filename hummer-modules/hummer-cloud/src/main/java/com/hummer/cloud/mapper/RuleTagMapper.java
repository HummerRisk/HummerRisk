package com.hummer.cloud.mapper;

import com.hummer.common.core.domain.RuleTag;
import com.hummer.common.core.domain.RuleTagExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RuleTagMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table rule_tag
     *
     * @mbg.generated Tue Jan 19 17:40:09 CST 2022
     */
    long countByExample(RuleTagExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table rule_tag
     *
     * @mbg.generated Tue Jan 19 17:40:09 CST 2022
     */
    int deleteByExample(RuleTagExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table rule_tag
     *
     * @mbg.generated Tue Jan 19 17:40:09 CST 2022
     */
    int deleteByPrimaryKey(String tagKey);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table rule_tag
     *
     * @mbg.generated Tue Jan 19 17:40:09 CST 2022
     */
    int insert(RuleTag record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table rule_tag
     *
     * @mbg.generated Tue Jan 19 17:40:09 CST 2022
     */
    int insertSelective(RuleTag record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table rule_tag
     *
     * @mbg.generated Tue Jan 19 17:40:09 CST 2022
     */
    List<RuleTag> selectByExample(RuleTagExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table rule_tag
     *
     * @mbg.generated Tue Jan 19 17:40:09 CST 2022
     */
    RuleTag selectByPrimaryKey(String tagKey);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table rule_tag
     *
     * @mbg.generated Tue Jan 19 17:40:09 CST 2022
     */
    int updateByExampleSelective(@Param("record") RuleTag record, @Param("example") RuleTagExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table rule_tag
     *
     * @mbg.generated Tue Jan 19 17:40:09 CST 2022
     */
    int updateByExample(@Param("record") RuleTag record, @Param("example") RuleTagExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table rule_tag
     *
     * @mbg.generated Tue Jan 19 17:40:09 CST 2022
     */
    int updateByPrimaryKeySelective(RuleTag record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table rule_tag
     *
     * @mbg.generated Tue Jan 19 17:40:09 CST 2022
     */
    int updateByPrimaryKey(RuleTag record);
}
