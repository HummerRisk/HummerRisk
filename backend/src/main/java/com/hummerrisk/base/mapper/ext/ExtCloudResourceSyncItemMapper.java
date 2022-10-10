package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.CloudResourceSyncItem;
import com.hummerrisk.base.domain.CloudResourceSyncItemExample;
import com.hummerrisk.base.domain.CloudResourceSyncItemLog;
import com.hummerrisk.dto.CloudResourceSyncItemDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCloudResourceSyncItemMapper {
    List<CloudResourceSyncItemDto> selectBySyncId(String syncId);
    List<CloudResourceSyncItemLog> selectSyncItemLogBySyncId(String syncId);
}