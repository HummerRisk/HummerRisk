package com.hummerrisk.service;

import com.hummerrisk.base.domain.CloudResourceSummary;
import com.hummerrisk.base.domain.CloudTask;
import com.hummerrisk.base.mapper.ext.ExtCloudResourceItemMapper;
import com.hummerrisk.base.mapper.ext.ExtCloudTaskMapper;
import com.hummerrisk.controller.request.cloudResource.CloudResourceItemRequest;
import com.hummerrisk.dto.CloudResourceItemDTO;
import com.hummerrisk.dto.ResourceRuleDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CloudResourceService {
    @Resource
    private ExtCloudResourceItemMapper extCloudResourceItemMapper;
    @Resource
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

    public List<ResourceRuleDTO> getResourceRule(String hummerId){
        return extCloudResourceItemMapper.selectResourceRule(hummerId);
    }

    public List<CloudTask> getCloudTaskByHummerId(String hummerId,String regionId){
        return extCloudTaskMapper.selectByHummerId(hummerId,regionId);
    }

    public int countResourceTask(String accountId,String regionId,String resourceType){
        return extCloudResourceItemMapper.countResourceTask(accountId,regionId,resourceType);
    }
}
