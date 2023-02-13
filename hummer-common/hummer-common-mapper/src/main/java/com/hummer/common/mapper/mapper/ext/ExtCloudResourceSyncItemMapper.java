package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.core.domain.CloudResourceSyncItemLog;
import com.hummer.common.core.dto.CloudResourceSyncItemDto;

import java.util.List;
import java.util.Map;

public interface ExtCloudResourceSyncItemMapper {

    List<CloudResourceSyncItemDto> selectBySyncId(String syncId);

    List<CloudResourceSyncItemLog> selectSyncItemLogBySyncId(String syncId);

    List<Map<String,Object>> selectResourceTypeBySyncId(String syncId);

}
