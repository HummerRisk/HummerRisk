package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.dto.CloudResourceItemDTO;
import com.hummer.common.mapper.dto.ResourceRuleDTO;
import com.hummer.common.mapper.domain.CloudResourceSummary;
import com.hummer.common.mapper.domain.request.cloudResource.CloudResourceItemRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCloudResourceItemMapper {

    List<CloudResourceSummary> selectResourceSummary(@Param("accountId") String accountId);

    List<CloudResourceItemDTO> selectByRequest(@Param("request") CloudResourceItemRequest request);

    List<ResourceRuleDTO> selectResourceRule(String hummerId);

    int selectResourceRiskCount(String hummerId);

    int countResourceTask(@Param("accountId")String accountId,@Param("regionId") String regionId,@Param("resourceType") String resourceType);
}
