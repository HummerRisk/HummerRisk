package com.hummer.common.core.dto;



import com.hummer.common.core.domain.CloudNativeConfigResultItem;
import com.hummer.common.core.domain.HistoryCloudNativeConfigResult;

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
