package com.hummer.k8s.mapper;

import com.hummer.common.core.domain.ImageRepoItem;
import com.hummer.common.core.domain.ImageRepoItemExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ImageRepoItemMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image_repo_item
     *
     * @mbg.generated Fri Sep 02 06:23:08 CST 2022
     */
    long countByExample(ImageRepoItemExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image_repo_item
     *
     * @mbg.generated Fri Sep 02 06:23:08 CST 2022
     */
    int deleteByExample(ImageRepoItemExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image_repo_item
     *
     * @mbg.generated Fri Sep 02 06:23:08 CST 2022
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image_repo_item
     *
     * @mbg.generated Fri Sep 02 06:23:08 CST 2022
     */
    int insert(ImageRepoItem record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image_repo_item
     *
     * @mbg.generated Fri Sep 02 06:23:08 CST 2022
     */
    int insertSelective(ImageRepoItem record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image_repo_item
     *
     * @mbg.generated Fri Sep 02 06:23:08 CST 2022
     */
    List<ImageRepoItem> selectByExample(ImageRepoItemExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image_repo_item
     *
     * @mbg.generated Fri Sep 02 06:23:08 CST 2022
     */
    ImageRepoItem selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image_repo_item
     *
     * @mbg.generated Fri Sep 02 06:23:08 CST 2022
     */
    int updateByExampleSelective(@Param("record") ImageRepoItem record, @Param("example") ImageRepoItemExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image_repo_item
     *
     * @mbg.generated Fri Sep 02 06:23:08 CST 2022
     */
    int updateByExample(@Param("record") ImageRepoItem record, @Param("example") ImageRepoItemExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image_repo_item
     *
     * @mbg.generated Fri Sep 02 06:23:08 CST 2022
     */
    int updateByPrimaryKeySelective(ImageRepoItem record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image_repo_item
     *
     * @mbg.generated Fri Sep 02 06:23:08 CST 2022
     */
    int updateByPrimaryKey(ImageRepoItem record);
}
