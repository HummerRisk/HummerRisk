package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.CloudResourceItem;
import com.hummerrisk.base.domain.CloudResourceSummary;
import com.hummerrisk.controller.request.cloudResource.CloudResourceItemRequest;
import com.hummerrisk.dto.CloudResourceItemDTO;
import com.hummerrisk.dto.ResourceRuleDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCloudResourceItemMapper {
    List<CloudResourceSummary> selectResourceSummary(@Param("accountId") String accountId);
    List<CloudResourceItemDTO> selectByRequest(@Param("request")CloudResourceItemRequest cloudResourceItemRequest);
    List<ResourceRuleDTO> selectResourceRule(String hummerId);
}
