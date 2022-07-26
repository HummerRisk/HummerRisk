package com.hummerrisk.dto;


import com.hummerrisk.base.domain.Resource;
import com.hummerrisk.base.domain.ResourceItem;

import java.util.List;

/**
 * @author harris
 */
public class HistoryResourceReportDTO extends Resource {

    private List<ResourceItem> resourceItemList;

    public List<ResourceItem> getResourceItemList() {
        return resourceItemList;
    }

    public void setResourceItemList(List<ResourceItem> resourceItemList) {
        this.resourceItemList = resourceItemList;
    }
}
