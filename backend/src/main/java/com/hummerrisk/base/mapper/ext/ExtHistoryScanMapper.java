package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.HistoryScan;

public interface ExtHistoryScanMapper {

    Integer getScanId (String accountId);

    Integer updateByExampleSelective(HistoryScan record);

}
