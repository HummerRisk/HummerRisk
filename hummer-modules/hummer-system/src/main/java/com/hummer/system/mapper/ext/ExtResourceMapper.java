package com.hummer.system.mapper.ext;


import com.hummer.common.core.domain.CloudTaskItem;
import com.hummer.common.core.domain.ResourceWithBLOBs;
import com.hummer.common.core.domain.RuleInspectionReport;
import com.hummer.common.core.domain.request.resource.ResourceRequest;
import com.hummer.common.core.domain.request.rule.RuleGroupRequest;
import com.hummer.common.core.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

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
