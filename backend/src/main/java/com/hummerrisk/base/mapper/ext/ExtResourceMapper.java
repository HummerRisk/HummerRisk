package com.hummerrisk.base.mapper.ext;


import com.hummerrisk.base.domain.CloudTaskItem;
import com.hummerrisk.base.domain.ResourceWithBLOBs;
import com.hummerrisk.controller.request.resource.ResourceRequest;
import com.hummerrisk.dto.ExportDTO;
import com.hummerrisk.dto.ReportDTO;
import com.hummerrisk.dto.ResourceDTO;
import com.hummerrisk.dto.SourceDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author harris
 */
public interface ExtResourceMapper {

    List<ResourceDTO> getComplianceResult(ResourceRequest resourceRequest);

    List<ReportDTO> reportList(ResourceRequest resourceRequest);

    SourceDTO source(@Param("accountId")String accountId);

    String resultPercent(@Param("accountId") String accountId, @Param("severity") String severity, @Param("taskId") String taskId);

    String sumReturnSum(@Param("accountId") String accountId);

    String sumResourcesSum(@Param("accountId") String accountId);

    Map<String, String> reportIso(@Param("accountId") String accountId, @Param("groupId") String groupId);

    List<Map<String, String>> groups(Map<String, Object> params);

    List<ExportDTO> searchExportData(ResourceRequest resourceRequest, @Param("accountIds") List<String> accountIds);

    ResourceWithBLOBs resource(CloudTaskItem cloudTaskItem);

}
