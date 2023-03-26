package com.hummer.system.mapper.ext;


import com.hummer.common.core.domain.CloudTaskItem;
import com.hummer.common.core.domain.ResourceWithBLOBs;
import org.apache.ibatis.annotations.Param;

/**
 * @author harris
 */
public interface ExtResourceMapper {

    String resultPercentByCloud(@Param("accountId") String accountId, @Param("severity") String severity, @Param("taskId") String taskId);

    Integer sumReturnSum(@Param("id") Integer id);

    Integer sumResourcesSum(@Param("id") Integer id);

    Integer sumScanScore(@Param("id") Integer id);

    ResourceWithBLOBs resource(CloudTaskItem cloudTaskItem);


}
