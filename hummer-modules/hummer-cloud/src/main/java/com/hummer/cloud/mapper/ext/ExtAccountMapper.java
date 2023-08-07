package com.hummer.cloud.mapper.ext;

import com.hummer.common.core.domain.Account;
import com.hummer.common.core.domain.CloudTask;
import com.hummer.common.core.domain.request.account.CloudAccountRequest;
import com.hummer.common.core.dto.AccountDTO;
import com.hummer.common.core.dto.QuartzTaskDTO;
import com.hummer.common.core.dto.RuleDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtAccountMapper {

    List<AccountDTO> getCloudAccountList(@Param("request") CloudAccountRequest request);

    List<RuleDTO> ruleList(QuartzTaskDTO dto);

    List<Map<String, Object>> groupList(Map<String, Object> params);

    List<Map<String, Object>> reportList(Map<String, Object> params);

    List<Map<String, Object>> tagList(Map<String, Object> params);

    List<Map<String, Object>> regionsList(Map<String, Object> params);

    List<Map<String, Object>> resourceList(Map<String, Object> params);

    List<Map<String, Object>> historyList(Map<String, Object> params);

    List<Map<String, Object>> historyDiffList(Map<String, Object> params);

    Account account(@Param("id") String id);

    List<CloudTask> cloudTaskList(@Param("id") String id);

}
