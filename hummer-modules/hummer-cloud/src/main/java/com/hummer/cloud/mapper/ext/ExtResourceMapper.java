package com.hummer.cloud.mapper.ext;


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

    List<ResourceDTO> getComplianceResult(@Param("request") ResourceRequest request);

    List<ReportDTO> reportList(ResourceRequest resourceRequest);

    SourceDTO source(@Param("accountId")String accountId);

    SourceDTO vulnSource(@Param("accountId")String accountId);

    String resultPercentByCloud(@Param("accountId") String accountId, @Param("severity") String severity, @Param("taskId") String taskId);

    String resultPercentByVuln(@Param("accountId") String accountId, @Param("severity") String severity, @Param("taskId") String taskId);

    Integer sumReturnSum(@Param("id") Integer id);

    Integer sumResourcesSum(@Param("id") Integer id);

    Integer sumScanScore(@Param("id") Integer id);

    Map<String, String> reportIso(@Param("accountId") String accountId, @Param("groupId") String groupId);

    List<Map<String, String>> groups(Map<String, Object> params);

    List<ExportDTO> searchExportData(ResourceRequest resourceRequest, @Param("accountIds") List<String> accountIds);

    List<ExportDTO> searchGroupExportData(ResourceRequest request, @Param("groupId") String groupId, @Param("accountId") String accountId);

    ResourceWithBLOBs resource(CloudTaskItem cloudTaskItem);

    List<Map<String, Object>> regionData(Map<String, Object> map);

    List<Map<String, Object>> severityData(Map<String, Object> map);

    List<Map<String, Object>> resourceTypeData(Map<String, Object> map);

    List<Map<String, Object>> ruleData(Map<String, Object> map);

    List<RuleInspectionReport> regulation(String ruleId);

    List<RuleGroupDTO> ruleGroupList(@Param("request") RuleGroupRequest request);

}
