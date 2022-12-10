package com.hummerrisk.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hummerrisk.base.domain.CloudTask;
import com.hummerrisk.base.mapper.ext.ExtAccountMapper;
import com.hummerrisk.base.mapper.ext.ExtCloudTaskMapper;
import com.hummerrisk.base.mapper.ext.ExtRuleMapper;
import com.hummerrisk.base.mapper.ext.ExtVulnMapper;
import com.hummerrisk.commons.utils.DashboardTarget;
import com.hummerrisk.controller.request.account.CloudAccountRequest;
import com.hummerrisk.controller.request.cloudTask.ManualRequest;
import com.hummerrisk.controller.request.rule.CreateRuleRequest;
import com.hummerrisk.dto.AccountDTO;
import com.hummerrisk.dto.RuleDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static com.alibaba.fastjson.JSON.parseArray;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class VulnService {

    @Resource
    private ExtAccountMapper extAccountMapper;
    @Resource
    private ExtRuleMapper extRuleMapper;
    @Resource
    private ExtCloudTaskMapper extCloudTaskMapper;
    @Resource
    private ExtVulnMapper extVulnMapper;

    public List<AccountDTO> getVulnList(CloudAccountRequest request) {
        return extAccountMapper.getVulnList(request);
    }

    public List<RuleDTO> vulnList(CreateRuleRequest ruleRequest) {
        return extRuleMapper.vulnList(ruleRequest);
    }

    public List<CloudTask> selectManualTasks(ManualRequest request) throws Exception {

        try {
            return extCloudTaskMapper.selectManualVulnTasks(request);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    public List<DashboardTarget> target(Map<String, Object> params) {
        return extVulnMapper.vulnTarget(params);
    }

    public List<Map<String, Object>> groupList(Map<String, Object> params) {
        return extVulnMapper.groupList(params);
    }

    public List<Map<String, Object>> reportList(Map<String, Object> params) {
        return extVulnMapper.reportList(params);
    }

    public List<Map<String, Object>> tagList(Map<String, Object> params) {
        return extVulnMapper.tagList(params);
    }

    public List<Map<String, Object>> resourceList(Map<String, Object> params) {
        return extVulnMapper.resourceList(params);
    }

    public List<Map<String, Object>> historyList(Map<String, Object> params) {
        List<Map<String, Object>> list = extVulnMapper.historyList(params);
        for (Map<String, Object> map : list) {
            if (map.get("rsources") != null) {
                map.put("rsources", toJSONString2(map.get("rsources").toString()));
            }
        }
        return list;
    }

    public List<Map<String, Object>> historyDiffList(Map<String, Object> params) {
        List<Map<String, Object>> list = extVulnMapper.historyDiffList(params);
        for (Map<String, Object> map : list) {
            if (map.get("rsources") != null) {
                map.put("rsources", toJSONString2(map.get("rsources").toString()));
            }
        }
        return list;
    }

    public String toJSONString2(String jsonString) {
        JSONArray jsonArray = parseArray(jsonString);
        return JSON.toJSONString(jsonArray, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
    }

}
