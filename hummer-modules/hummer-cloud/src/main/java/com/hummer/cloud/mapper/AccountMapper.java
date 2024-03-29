package com.hummer.cloud.mapper;

import com.hummer.common.core.domain.Account;
import com.hummer.common.core.domain.AccountExample;
import com.hummer.common.core.domain.AccountWithBLOBs;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AccountMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_account
     *
     * @mbg.generated Wed Jul 19 22:20:28 CST 2023
     */
    long countByExample(AccountExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_account
     *
     * @mbg.generated Wed Jul 19 22:20:28 CST 2023
     */
    int deleteByExample(AccountExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_account
     *
     * @mbg.generated Wed Jul 19 22:20:28 CST 2023
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_account
     *
     * @mbg.generated Wed Jul 19 22:20:28 CST 2023
     */
    int insert(AccountWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_account
     *
     * @mbg.generated Wed Jul 19 22:20:28 CST 2023
     */
    int insertSelective(AccountWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_account
     *
     * @mbg.generated Wed Jul 19 22:20:28 CST 2023
     */
    List<AccountWithBLOBs> selectByExampleWithBLOBs(AccountExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_account
     *
     * @mbg.generated Wed Jul 19 22:20:28 CST 2023
     */
    List<Account> selectByExample(AccountExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_account
     *
     * @mbg.generated Wed Jul 19 22:20:28 CST 2023
     */
    AccountWithBLOBs selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_account
     *
     * @mbg.generated Wed Jul 19 22:20:28 CST 2023
     */
    int updateByExampleSelective(@Param("record") AccountWithBLOBs record, @Param("example") AccountExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_account
     *
     * @mbg.generated Wed Jul 19 22:20:28 CST 2023
     */
    int updateByExampleWithBLOBs(@Param("record") AccountWithBLOBs record, @Param("example") AccountExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_account
     *
     * @mbg.generated Wed Jul 19 22:20:28 CST 2023
     */
    int updateByExample(@Param("record") Account record, @Param("example") AccountExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_account
     *
     * @mbg.generated Wed Jul 19 22:20:28 CST 2023
     */
    int updateByPrimaryKeySelective(AccountWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_account
     *
     * @mbg.generated Wed Jul 19 22:20:28 CST 2023
     */
    int updateByPrimaryKeyWithBLOBs(AccountWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_account
     *
     * @mbg.generated Wed Jul 19 22:20:28 CST 2023
     */
    int updateByPrimaryKey(Account record);
}
