package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.ScanHistory;

public interface ExtScanHistoryMapper {

    Integer getScanId (String accountId);

    Integer updateByExampleSelective(ScanHistory record);

}
