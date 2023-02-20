package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.domain.HistoryScan;

public interface ExtHistoryScanMapper {

    Integer getScanId (String accountId);

    Integer updateByExampleSelective(HistoryScan record);

}
