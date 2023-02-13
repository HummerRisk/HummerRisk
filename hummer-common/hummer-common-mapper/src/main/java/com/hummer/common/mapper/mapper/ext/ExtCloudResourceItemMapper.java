package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.core.domain.CloudResourceSummary;
import com.hummer.common.core.domain.request.cloudResource.CloudResourceItemRequest;
import com.hummer.common.core.dto.CloudResourceItemDTO;
import com.hummer.common.core.dto.ResourceRuleDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCloudResourceItemMapper {

    List<CloudResourceSummary> selectResourceSummary(@Param("accountId") String accountId);

    List<CloudResourceItemDTO> selectByRequest(@Param("request") CloudResourceItemRequest request);

    List<ResourceRuleDTO> selectResourceRule(String hummerId);

    int selectResourceRiskCount(String hummerId);

    int countResourceTask(@Param("accountId")String accountId,@Param("regionId") String regionId,@Param("resourceType") String resourceType);
}
