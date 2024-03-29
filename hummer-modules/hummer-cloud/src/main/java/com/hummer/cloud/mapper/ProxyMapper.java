package com.hummer.cloud.mapper;

import com.hummer.common.core.domain.Proxy;
import com.hummer.common.core.domain.ProxyExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProxyMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table proxy
     *
     * @mbg.generated Fri Apr 09 11:29:36 CST 2022
     */
    long countByExample(ProxyExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table proxy
     *
     * @mbg.generated Fri Apr 09 11:29:36 CST 2022
     */
    int deleteByExample(ProxyExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table proxy
     *
     * @mbg.generated Fri Apr 09 11:29:36 CST 2022
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table proxy
     *
     * @mbg.generated Fri Apr 09 11:29:36 CST 2022
     */
    int insert(Proxy record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table proxy
     *
     * @mbg.generated Fri Apr 09 11:29:36 CST 2022
     */
    int insertSelective(Proxy record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table proxy
     *
     * @mbg.generated Fri Apr 09 11:29:36 CST 2022
     */
    List<Proxy> selectByExample(ProxyExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table proxy
     *
     * @mbg.generated Fri Apr 09 11:29:36 CST 2022
     */
    Proxy selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table proxy
     *
     * @mbg.generated Fri Apr 09 11:29:36 CST 2022
     */
    int updateByExampleSelective(@Param("record") Proxy record, @Param("example") ProxyExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table proxy
     *
     * @mbg.generated Fri Apr 09 11:29:36 CST 2022
     */
    int updateByExample(@Param("record") Proxy record, @Param("example") ProxyExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table proxy
     *
     * @mbg.generated Fri Apr 09 11:29:36 CST 2022
     */
    int updateByPrimaryKeySelective(Proxy record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table proxy
     *
     * @mbg.generated Fri Apr 09 11:29:36 CST 2022
     */
    int updateByPrimaryKey(Proxy record);
}
