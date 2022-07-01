package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.CloudScanHistory;

public interface ExtCloudScanHistoryMapper {

    Integer getScanId (String accountId);

    Integer updateByExampleSelective(CloudScanHistory record);

}
