package com.hummerrisk.base.mapper;

import com.hummerrisk.base.domain.CloudNativeSourceRbacLink;
import com.hummerrisk.base.domain.CloudNativeSourceRbacLinkExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CloudNativeSourceRbacLinkMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_native_source_rbac_link
     *
     * @mbg.generated Sun Jan 15 04:51:16 CST 2023
     */
    long countByExample(CloudNativeSourceRbacLinkExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_native_source_rbac_link
     *
     * @mbg.generated Sun Jan 15 04:51:16 CST 2023
     */
    int deleteByExample(CloudNativeSourceRbacLinkExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_native_source_rbac_link
     *
     * @mbg.generated Sun Jan 15 04:51:16 CST 2023
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_native_source_rbac_link
     *
     * @mbg.generated Sun Jan 15 04:51:16 CST 2023
     */
    int insert(CloudNativeSourceRbacLink record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_native_source_rbac_link
     *
     * @mbg.generated Sun Jan 15 04:51:16 CST 2023
     */
    int insertSelective(CloudNativeSourceRbacLink record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_native_source_rbac_link
     *
     * @mbg.generated Sun Jan 15 04:51:16 CST 2023
     */
    List<CloudNativeSourceRbacLink> selectByExample(CloudNativeSourceRbacLinkExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_native_source_rbac_link
     *
     * @mbg.generated Sun Jan 15 04:51:16 CST 2023
     */
    CloudNativeSourceRbacLink selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_native_source_rbac_link
     *
     * @mbg.generated Sun Jan 15 04:51:16 CST 2023
     */
    int updateByExampleSelective(@Param("record") CloudNativeSourceRbacLink record, @Param("example") CloudNativeSourceRbacLinkExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_native_source_rbac_link
     *
     * @mbg.generated Sun Jan 15 04:51:16 CST 2023
     */
    int updateByExample(@Param("record") CloudNativeSourceRbacLink record, @Param("example") CloudNativeSourceRbacLinkExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_native_source_rbac_link
     *
     * @mbg.generated Sun Jan 15 04:51:16 CST 2023
     */
    int updateByPrimaryKeySelective(CloudNativeSourceRbacLink record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_native_source_rbac_link
     *
     * @mbg.generated Sun Jan 15 04:51:16 CST 2023
     */
    int updateByPrimaryKey(CloudNativeSourceRbacLink record);
}