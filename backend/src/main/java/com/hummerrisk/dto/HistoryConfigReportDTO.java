package com.hummerrisk.dto;


import com.hummerrisk.base.domain.CloudNativeConfigResultItem;
import com.hummerrisk.base.domain.HistoryCloudNativeConfigResult;

import java.util.List;

/**
 * @author harris
 */
public class HistoryConfigReportDTO extends HistoryCloudNativeConfigResult {

    private List<CloudNativeConfigResultItem> configResultItems;//结果json

    public List<CloudNativeConfigResultItem> getConfigResultItems() {
        return configResultItems;
    }

    public void setConfigResultItems(List<CloudNativeConfigResultItem> configResultItems) {
        this.configResultItems = configResultItems;
    }
}
