package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.resource.ResourceRequest;
import com.hummerrisk.controller.request.rule.RuleGroupRequest;
import com.hummerrisk.dto.ExportDTO;
import com.hummerrisk.dto.ResourceDTO;
import com.hummerrisk.dto.RuleGroupDTO;
import com.hummerrisk.oss.controller.request.OssRequest;
import com.hummerrisk.oss.dto.OssBucketDTO;
import com.hummerrisk.oss.dto.OssDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtOssMapper {

    List<OssDTO> ossList (@Param("request") OssRequest request);

    List<OssBucketDTO> ossBucketList (@Param("request") OssRequest request);

    List<RuleGroupDTO> ruleGroupList(@Param("request") RuleGroupRequest request);

    List<ExportDTO> searchGroupExportData(ResourceRequest request, @Param("groupId") String groupId, @Param("accountId") String accountId);

    List<ResourceDTO> resourceList(@Param("request") ResourceRequest request, @Param("resourceTypes") List<String> resourceTypes);

    Map<String, Object> topInfo(Map<String, Object> params);

    List<Map<String, Object>> ossChart();

    List<Map<String, Object>> bucketChart();

    List<Map<String, Object>> severityChart();

}
