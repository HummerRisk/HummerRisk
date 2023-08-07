package com.hummer.system.mapper.ext;

import com.hummer.common.core.domain.HistoryScan;

public interface ExtHistoryScanMapper {

    Integer getScanId (String accountId);

    Integer updateByExampleSelective(HistoryScan record);

}
