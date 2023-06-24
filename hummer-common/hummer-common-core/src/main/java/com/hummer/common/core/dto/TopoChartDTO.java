package com.hummer.common.core.dto;


import com.hummer.common.core.domain.CloudResourceRela;
import com.hummer.common.core.domain.CloudResourceRelaLink;

import java.util.List;

public class TopoChartDTO {

    private List<CloudResourceRela> cloudResourceRelaList;

    private List<CloudResourceRelaLink> cloudResourceRelaLinkList;

    public List<CloudResourceRela> getCloudResourceRelaList() {
        return cloudResourceRelaList;
    }

    public void setCloudResourceRelaList(List<CloudResourceRela> cloudResourceRelaList) {
        this.cloudResourceRelaList = cloudResourceRelaList;
    }

    public List<CloudResourceRelaLink> getCloudResourceRelaLinkList() {
        return cloudResourceRelaLinkList;
    }

    public void setCloudResourceRelaLinkList(List<CloudResourceRelaLink> cloudResourceRelaLinkList) {
        this.cloudResourceRelaLinkList = cloudResourceRelaLinkList;
    }
}
