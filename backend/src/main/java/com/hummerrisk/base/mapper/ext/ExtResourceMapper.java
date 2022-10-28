package com.hummerrisk.base.mapper.ext;


import com.hummerrisk.base.domain.CloudTaskItem;
import com.hummerrisk.base.domain.ResourceWithBLOBs;
import com.hummerrisk.base.domain.RuleInspectionReport;
import com.hummerrisk.controller.request.resource.ResourceRequest;
import com.hummerrisk.controller.request.rule.RuleGroupRequest;
import com.hummerrisk.dto.*;
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

    SourceDTO vulnSource(@Param("accountId")String accountId);

    String resultPercentByCloud(@Param("accountId") String accountId, @Param("severity") String severity, @Param("taskId") String taskId);

    String resultPercentByVuln(@Param("accountId") String accountId, @Param("severity") String severity, @Param("taskId") String taskId);

    Integer sumReturnSum(@Param("id") Integer id);

    Integer sumResourcesSum(@Param("id") Integer id);

    Integer sumScanScore(@Param("id") Integer id);

    Map<String, String> reportIso(@Param("accountId") String accountId, @Param("groupId") String groupId);

    List<Map<String, String>> groups(Map<String, Object> params);

    List<ExportDTO> searchExportData(ResourceRequest resourceRequest, @Param("accountIds") List<String> accountIds);

    ResourceWithBLOBs resource(CloudTaskItem cloudTaskItem);

    List<Map<String, Object>> regionData(Map<String, Object> map);

    List<Map<String, Object>> severityData(Map<String, Object> map);

    List<Map<String, Object>> resourceTypeData(Map<String, Object> map);

    List<Map<String, Object>> ruleData(Map<String, Object> map);

    List<RuleInspectionReport> regulation(String ruleId);

    List<RuleGroupDTO> ruleGroupList(@Param("request") RuleGroupRequest request);

}
