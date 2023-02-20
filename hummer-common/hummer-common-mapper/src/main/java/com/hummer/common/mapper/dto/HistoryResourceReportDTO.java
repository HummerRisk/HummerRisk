package com.hummer.common.mapper.dto;



import com.hummer.common.mapper.domain.Resource;
import com.hummer.common.mapper.domain.ResourceItem;

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
