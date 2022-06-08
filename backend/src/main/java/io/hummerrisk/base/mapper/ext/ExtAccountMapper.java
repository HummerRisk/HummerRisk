package io.hummerrisk.base.mapper.ext;

import io.hummerrisk.controller.request.account.CloudAccountRequest;
import io.hummerrisk.dto.AccountDTO;
import io.hummerrisk.dto.QuartzTaskDTO;
import io.hummerrisk.dto.RuleDTO;
import io.hummerrisk.dto.ServerDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtAccountMapper {

    List<AccountDTO> getCloudAccountList(@Param("request") CloudAccountRequest request);

    List<AccountDTO> getVulnList(@Param("request") CloudAccountRequest request);

    List<RuleDTO> ruleList(QuartzTaskDTO dto);

    List<Map<String, Object>> groupList(Map<String, Object> params);

    List<Map<String, Object>> reportList(Map<String, Object> params);

    List<Map<String, Object>> tagList(Map<String, Object> params);

    List<Map<String, Object>> regionsList(Map<String, Object> params);

    List<Map<String, Object>> resourceList(Map<String, Object> params);

    List<ServerDTO> getServerList(@Param("request") CloudAccountRequest request);

}
