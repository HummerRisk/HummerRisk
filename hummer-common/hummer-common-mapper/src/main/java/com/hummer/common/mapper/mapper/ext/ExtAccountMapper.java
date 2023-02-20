package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.dto.AccountDTO;
import com.hummer.common.mapper.dto.QuartzTaskDTO;
import com.hummer.common.mapper.dto.RuleDTO;
import com.hummer.common.mapper.dto.ServerDTO;
import com.hummer.common.mapper.domain.request.account.CloudAccountRequest;
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

    List<Map<String, Object>> historyList(Map<String, Object> params);

    List<Map<String, Object>> historyDiffList(Map<String, Object> params);

}
