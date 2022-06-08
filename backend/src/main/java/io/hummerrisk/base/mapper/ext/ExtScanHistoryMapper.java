package io.hummerrisk.base.mapper.ext;

import io.hummerrisk.base.domain.ScanHistory;

public interface ExtScanHistoryMapper {

    Integer getScanId (String accountId);

    Integer updateByExampleSelective(ScanHistory record);

}
