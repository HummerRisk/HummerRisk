package com.hummer.cloud.service;

import com.hummer.cloud.mapper.CloudResourceItemMapper;
import com.hummer.cloud.mapper.ext.ExtCloudResourceItemMapper;
import com.hummer.cloud.mapper.ext.ExtCloudTaskMapper;
import com.hummer.common.core.domain.CloudResourceItem;
import com.hummer.common.core.domain.CloudResourceSummary;
import com.hummer.common.core.domain.CloudTask;
import com.hummer.common.core.domain.request.cloudResource.CloudResourceItemRequest;
import com.hummer.common.core.dto.CloudResourceItemDTO;
import com.hummer.common.core.dto.ResourceRuleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CloudResourceService {
    @Autowired
    private ExtCloudResourceItemMapper extCloudResourceItemMapper;
    @Autowired
    private CloudResourceItemMapper cloudResourceItemMapper;
    @Autowired
    private ExtCloudTaskMapper extCloudTaskMapper;

    public List<CloudResourceSummary> getSummary(String accountId){
        return extCloudResourceItemMapper.selectResourceSummary(accountId);
    }

    public List<CloudResourceItemDTO> getResources(CloudResourceItemRequest cloudResourceItemRequest){
        List<CloudResourceItemDTO> cloudResourceItems = extCloudResourceItemMapper.selectByRequest(cloudResourceItemRequest);
        cloudResourceItems.forEach(item -> {
            item.setRiskCount(extCloudResourceItemMapper.selectResourceRiskCount(item.getHummerId()));
        });
        return cloudResourceItems;
    }

    public String resources(String id){
        CloudResourceItem cloudResourceItem = cloudResourceItemMapper.selectByPrimaryKey(id);
        return cloudResourceItem.getResource();
    }

    public List<ResourceRuleDTO> getResourceRule(String hummerId){
        return extCloudResourceItemMapper.selectResourceRule(hummerId);
    }

    public List<CloudTask> getCloudTaskByHummerId(String hummerId, String regionId){
        return extCloudTaskMapper.selectByHummerId(hummerId,regionId);
    }

    public int countResourceTask(String accountId,String regionId,String resourceType){
        return extCloudResourceItemMapper.countResourceTask(accountId,regionId,resourceType);
    }
}
