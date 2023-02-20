package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.dto.CloudResourceSyncItemDTO;
import com.hummer.common.mapper.domain.CloudResourceSyncItemLog;

import java.util.List;
import java.util.Map;

public interface ExtCloudResourceSyncItemMapper {

    List<CloudResourceSyncItemDTO> selectBySyncId(String syncId);

    List<CloudResourceSyncItemLog> selectSyncItemLogBySyncId(String syncId);

    List<Map<String,Object>> selectResourceTypeBySyncId(String syncId);

}
