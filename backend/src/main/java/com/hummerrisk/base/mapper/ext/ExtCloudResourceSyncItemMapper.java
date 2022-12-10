package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.CloudResourceSyncItemLog;
import com.hummerrisk.dto.CloudResourceSyncItemDto;

import java.util.List;
import java.util.Map;

public interface ExtCloudResourceSyncItemMapper {
    List<CloudResourceSyncItemDto> selectBySyncId(String syncId);
    List<CloudResourceSyncItemLog> selectSyncItemLogBySyncId(String syncId);
    List<Map<String,Object>> selectResourceTypeBySyncId(String syncId);
}
