package com.hummerrisk.service;

import com.hummerrisk.base.domain.CloudTask;
import com.hummerrisk.base.domain.CloudTaskExample;
import com.hummerrisk.base.mapper.CloudTaskMapper;
import com.hummerrisk.base.mapper.ext.ExtAccountMapper;
import com.hummerrisk.base.mapper.ext.ExtCloudTaskMapper;
import com.hummerrisk.base.mapper.ext.ExtRuleMapper;
import com.hummerrisk.commons.utils.PlatformUtils;
import com.hummerrisk.controller.request.account.CloudAccountRequest;
import com.hummerrisk.controller.request.rule.CreateRuleRequest;
import com.hummerrisk.dto.AccountDTO;
import com.hummerrisk.dto.CloudTaskDTO;
import com.hummerrisk.dto.RuleDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    private CloudTaskMapper cloudTaskMapper;
    @Resource
    private ExtCloudTaskMapper extCloudTaskMapper;

    public List<AccountDTO> getVulnList(CloudAccountRequest request) {
        return extAccountMapper.getVulnList(request);
    }

    public List<RuleDTO> vulnList(CreateRuleRequest ruleRequest) {
        return extRuleMapper.vulnList(ruleRequest);
    }

    public List<CloudTaskDTO> selectManualTasks(Map<String, Object> params) throws Exception {

        try {
            CloudTaskExample example = new CloudTaskExample();
            CloudTaskExample.Criteria criteria = example.createCriteria();
            if (params.get("name") != null && StringUtils.isNotEmpty(params.get("name").toString())) {
                criteria.andTaskNameLike("%" + params.get("name").toString() + "%");
            }
            if (params.get("type") != null && StringUtils.isNotEmpty(params.get("type").toString())) {
                criteria.andTypeEqualTo(params.get("type").toString());
            }
            if (params.get("accountId") != null && StringUtils.isNotEmpty(params.get("accountId").toString())) {
                criteria.andAccountIdEqualTo(params.get("accountId").toString());
            }
            if (params.get("cron") != null && StringUtils.isNotEmpty(params.get("cron").toString())) {
                criteria.andCronLike(params.get("cron").toString());
            }
            if (params.get("status") != null && StringUtils.isNotEmpty(params.get("status").toString())) {
                criteria.andStatusEqualTo(params.get("status").toString());
            }
            if (params.get("severity") != null && StringUtils.isNotEmpty(params.get("severity").toString())) {
                criteria.andSeverityEqualTo(params.get("severity").toString());
            }
            if (params.get("pluginName") != null && StringUtils.isNotEmpty(params.get("pluginName").toString())) {
                criteria.andPluginNameEqualTo(params.get("pluginName").toString());
            }
            if (params.get("ruleTag") != null && StringUtils.isNotEmpty(params.get("ruleTag").toString())) {
                criteria.andRuleTagsLike("%" + params.get("ruleTag").toString() + "%");
            }
            if (params.get("resourceType") != null && StringUtils.isNotEmpty(params.get("resourceType").toString())) {
                criteria.andResourceTypesLike("%" + params.get("resourceType").toString() + "%");
            }
            criteria.andPluginIdIn(PlatformUtils.getVulnPlugin());
            example.setOrderByClause("FIELD(`status`, 'PROCESSING', 'APPROVED', 'FINISHED', 'WARNING', 'ERROR'), return_sum desc, create_time desc, FIELD(`severity`, 'HighRisk', 'MediumRisk', 'LowRisk')");
            return extCloudTaskMapper.selectByExample(example);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }
}
