package com.hummerrisk.service;

import com.hummerrisk.base.domain.CloudResourceItem;
import com.hummerrisk.base.domain.CloudResourceSummary;
import com.hummerrisk.base.mapper.ext.ExtCloudResourceItemMapper;
import com.hummerrisk.controller.request.cloudResource.CloudResourceItemRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CloudResourceService {
    @Resource
    private ExtCloudResourceItemMapper extCloudResourceItemMapper;

    public List<CloudResourceSummary> getSummary(String accountId){
        return extCloudResourceItemMapper.selectResourceSummary(accountId);
    }

    public List<CloudResourceItem> getResources(CloudResourceItemRequest cloudResourceItemRequest){
        return extCloudResourceItemMapper.selectByRequest(cloudResourceItemRequest);
    }
}
