package com.hummer.cloud.mapper.ext;

import com.hummer.cloud.oss.controller.request.OssBucketRequest;
import com.hummer.cloud.oss.controller.request.OssRequest;
import com.hummer.cloud.oss.dto.OssBucketDTO;
import com.hummer.cloud.oss.dto.OssDTO;
import com.hummer.common.core.dto.ExportDTO;
import com.hummer.common.core.dto.ResourceDTO;
import com.hummer.common.core.dto.RuleGroupDTO;
import com.hummer.common.core.domain.request.resource.ResourceRequest;
import com.hummer.common.core.domain.request.rule.RuleGroupRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtOssMapper {

    List<OssDTO> ossList (@Param("request") OssRequest request);

    List<OssBucketDTO> ossBucketList (@Param("request") OssBucketRequest request);

    List<RuleGroupDTO> ruleGroupList(@Param("request") RuleGroupRequest request);

    List<ExportDTO> searchGroupExportData(ResourceRequest request, @Param("groupId") String groupId, @Param("accountId") String accountId);

    List<ResourceDTO> resourceList(@Param("request") ResourceRequest request, @Param("resourceTypes") List<String> resourceTypes);

    Map<String, Object> topInfo(Map<String, Object> params);

    List<Map<String, Object>> ossChart();

    List<Map<String, Object>> bucketChart();

    List<Map<String, Object>> severityChart();

}
