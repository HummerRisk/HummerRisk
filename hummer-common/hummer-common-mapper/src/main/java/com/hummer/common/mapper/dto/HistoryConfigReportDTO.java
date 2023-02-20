package com.hummer.common.mapper.dto;



import com.hummer.common.mapper.domain.CloudNativeConfigResultItem;
import com.hummer.common.mapper.domain.HistoryCloudNativeConfigResult;

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
