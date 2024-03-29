package com.hummer.cloud.mapper.ext;

import com.hummer.common.core.domain.CloudResourceSyncItemLog;
import com.hummer.common.core.dto.CloudResourceSyncItemDTO;

import java.util.List;
import java.util.Map;

public interface ExtCloudResourceSyncItemMapper {

    List<CloudResourceSyncItemDTO> selectBySyncId(String syncId);

    List<CloudResourceSyncItemLog> selectSyncItemLogBySyncId(String syncId);

    List<Map<String,Object>> selectResourceTypeBySyncId(String syncId);

}
