package com.hummer.cloud.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hummer.cloud.mapper.*;
import com.hummer.cloud.mapper.ext.*;
import com.hummer.common.core.constant.*;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.rule.*;
import com.hummer.common.core.dto.*;
import com.hummer.common.core.exception.HRException;
import com.hummer.common.core.i18n.Translator;
import com.hummer.common.core.utils.BeanUtils;
import com.hummer.common.core.utils.LogUtil;
import com.hummer.common.core.utils.PlatformUtils;
import com.hummer.common.core.utils.UUIDUtil;
import com.hummer.k8s.api.IK8sProviderService;
import com.hummer.system.api.IOperationLogService;
import com.hummer.system.api.ISystemProviderService;
import com.hummer.system.api.model.LoginUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.Yaml;

import java.util.*;
import java.util.stream.Collectors;

import static com.alibaba.fastjson.JSON.parseArray;

/**
 * @author harris
 */
//@Service
@Service
@Transactional(rollbackFor = Exception.class)
public class RuleService {

    @Autowired
    @Lazy
    private RuleMapper ruleMapper;
    @Autowired
    @Lazy
    private RuleTagMapper ruleTagMapper;
    @Autowired
    @Lazy
    private RuleTagMappingMapper ruleTagMappingMapper;
    @Autowired
    @Lazy
    private PluginMapper pluginMapper;
    @Autowired
    @Lazy
    private ExtRuleMapper extRuleMapper;
    @Autowired
    @Lazy
    private RuleTypeMapper ruleTypeMapper;
    @Autowired
    @Lazy
    private ExtRuleTagMapper extRuleTagMapper;
    @Autowired
    @Lazy
    private ExtRuleTypeMapper extRuleTypeMapper;
    @Autowired
    @Lazy
    private CloudTaskService cloudTaskService;
    @Autowired
    @Lazy
    private ResourceRuleMapper resourceRuleMapper;
    @Autowired
    @Lazy
    private AccountMapper accountMapper;
    @Autowired
    @Lazy
    private AccountService accountService;
    @Autowired
    @Lazy
    private RuleGroupMapper ruleGroupMapper;
    @Autowired
    @Lazy
    private RuleGroupMappingMapper ruleGroupMappingMapper;
    @Autowired
    @Lazy
    private RuleInspectionReportMapper ruleInspectionReportMapper;
    @Autowired
    @Lazy
    private ExtRuleInspectionReportMapper extRuleInspectionReportMapper;
    @Autowired
    @Lazy
    private RuleInspectionReportMappingMapper ruleInspectionReportMappingMapper;
    @Autowired
    @Lazy
    private ExtRuleGroupMapper extRuleGroupMapper;
    @Autowired
    @Lazy
    private CloudTaskItemMapper cloudTaskItemMapper;
    @Autowired
    @Lazy
    private CloudTaskMapper cloudTaskMapper;
    @DubboReference
    private ISystemProviderService systemProviderService;
    @DubboReference
    private IK8sProviderService k8sProviderService;
    @DubboReference
    private IOperationLogService operationLogService;

    public List<RuleDTO> cloudList(CreateRuleRequest ruleRequest) {
        return extRuleMapper.cloudList(ruleRequest);
    }

    public List<RuleDTO> k8sList(CreateRuleRequest ruleRequest) {
        return extRuleMapper.k8sList(ruleRequest);
    }

    public List<Rule> getRulesByAccountId(String accountId) {
        RuleExample example = new RuleExample();
        example.createCriteria().andPluginIdEqualTo(accountMapper.selectByPrimaryKey(accountId).getPluginId());
        return ruleMapper.selectByExample(example);
    }

    public List<RuleTag> ruleTagList(RuleTagRequest request) {
        return extRuleTagMapper.list(request);
    }

    public List<RuleGroupDTO> ruleGroupList(RuleGroupRequest request) {
        return extRuleGroupMapper.list(request);
    }

    public List<RuleGroupDTO> allCloudRuleGroups(RuleGroupRequest request) {
        return extRuleGroupMapper.allCloudRuleGroups(request);
    }

    public Rule saveRules(CreateRuleRequest ruleRequest, LoginUser loginUser) {
        try {
            if (StringUtils.equalsIgnoreCase(ruleRequest.getScanType(), ScanTypeConstants.custodian.toString())) {
                cloudTaskService.validateYaml(BeanUtils.copyBean(new QuartzTaskDTO(), ruleRequest));
            }
            if (StringUtils.isBlank(ruleRequest.getId())) {
                ruleRequest.setId(UUIDUtil.newUUID());
                ruleRequest.setLastModified(System.currentTimeMillis());
                ruleRequest.setType(ruleRequest.getScanType());
            }
            Rule t = ruleMapper.selectByPrimaryKey(ruleRequest.getId());
            if (t == null) {
                Plugin plugin = pluginMapper.selectByPrimaryKey(ruleRequest.getPluginId());
                ruleRequest.setPluginId(plugin.getId());
                ruleRequest.setPluginName(plugin.getName());
                ruleRequest.setPluginIcon(plugin.getIcon());
                ruleRequest.setFlag(false);
                ruleMapper.insertSelective(ruleRequest);
                operationLogService.log(loginUser, ruleRequest.getId(), ruleRequest.getName(), ResourceTypeConstants.RULE.name(), ResourceOperation.CREATE, "i18n_create_rule");
            } else {
                Plugin plugin = pluginMapper.selectByPrimaryKey(ruleRequest.getPluginId());
                ruleRequest.setPluginId(plugin.getId());
                ruleRequest.setPluginName(plugin.getName());
                ruleRequest.setPluginIcon(plugin.getIcon());
                ruleRequest.setFlag(false);
                ruleRequest.setLastModified(System.currentTimeMillis());
                ruleMapper.updateByPrimaryKeySelective(ruleRequest);
                operationLogService.log(loginUser, ruleRequest.getId(), ruleRequest.getName(), ResourceTypeConstants.RULE.name(), ResourceOperation.UPDATE, "i18n_update_rule");
            }

            saveRuleTagMapping(ruleRequest.getId(), ruleRequest.getTagKey());
            saveRuleGroupMapping(ruleRequest.getId(), ruleRequest.getRuleSets());
            saveRuleInspectionReportMapping(ruleRequest.getId(), ruleRequest.getInspectionSeports());
            saveRuleType(ruleRequest);

        } catch (Exception e) {
            HRException.throwException(e.getMessage());
        }
        return ruleRequest;
    }

    @SuppressWarnings({"unchecked"})
    public boolean saveRuleType(CreateRuleRequest ruleRequest) {
        try {
            RuleType ruleType = new RuleType();
            ruleType.setRuleId(ruleRequest.getId());
            RuleTypeExample example = new RuleTypeExample();
            example.createCriteria().andRuleIdEqualTo(ruleRequest.getId());
            ruleTypeMapper.deleteByExample(example);
            if (StringUtils.equalsIgnoreCase(ruleRequest.getScanType(), ScanTypeConstants.custodian.name())) {
                String script = ruleRequest.getScript();
                JSONArray jsonArray = parseArray(ruleRequest.getParameter());
                for (Object o : jsonArray) {
                    JSONObject jsonObject = (JSONObject) o;
                    String key = "${{" + jsonObject.getString("key") + "}}";
                    if (script.contains(key)) {
                        script = script.replace(key, jsonObject.getString("defaultValue"));
                    }
                }
                Yaml yaml = new Yaml();
                Map map = yaml.load(script);
                if (map != null) {
                    List<Map> list = (List) map.get("policies");
                    for (Map m : list) {
                        String resourceType = m.get("resource").toString();
                        example.createCriteria().andRuleIdEqualTo(ruleRequest.getId()).andResourceTypeEqualTo(resourceType);
                        List<RuleType> ruleTypes = ruleTypeMapper.selectByExample(example);
                        if (ruleTypes.isEmpty()) {
                            ruleType.setId(UUIDUtil.newUUID());
                            ruleType.setResourceType(resourceType);
                            ruleTypeMapper.insertSelective(ruleType);
                        }
                    }
                }
            } else if (StringUtils.equalsIgnoreCase(ruleRequest.getScanType(), ScanTypeConstants.prowler.name())) {
                String resourceType = "prowler";
                example.createCriteria().andRuleIdEqualTo(ruleRequest.getId()).andResourceTypeEqualTo(resourceType);
                List<RuleType> ruleTypes = ruleTypeMapper.selectByExample(example);
                if (ruleTypes.isEmpty()) {
                    ruleType.setId(UUIDUtil.newUUID());
                    ruleType.setResourceType(resourceType);
                    ruleTypeMapper.insertSelective(ruleType);
                }
            }

        } catch (Exception e) {
            LogUtil.error(Translator.get("i18n_compliance_rule_code_error") + ": " + e.getMessage());
            return false;
        }
        return true;
    }

    public Rule copyRule(CreateRuleRequest ruleRequest, LoginUser loginUser) {
        try {
            if (StringUtils.equalsIgnoreCase(ruleRequest.getScanType(), ScanTypeConstants.custodian.toString())) {
                cloudTaskService.validateYaml(BeanUtils.copyBean(new QuartzTaskDTO(), ruleRequest));
            }
            ruleRequest.setLastModified(System.currentTimeMillis());
            ruleRequest.setId(UUIDUtil.newUUID());
            ruleRequest.setFlag(false);

            Plugin plugin = pluginMapper.selectByPrimaryKey(ruleRequest.getPluginId());
            ruleRequest.setPluginName(plugin.getName());
            ruleRequest.setPluginIcon(plugin.getIcon());

            ruleMapper.insertSelective(ruleRequest);
            saveRuleTagMapping(ruleRequest.getId(), ruleRequest.getTagKey());
            saveRuleGroupMapping(ruleRequest.getId(), ruleRequest.getRuleSets());
            saveRuleInspectionReportMapping(ruleRequest.getId(), ruleRequest.getInspectionSeports());
            boolean flag = saveRuleType(ruleRequest);
            if (!flag) {
                HRException.throwException(Translator.get("i18n_compliance_rule_code_error"));
            }
            operationLogService.log(loginUser, ruleRequest.getId(), ruleRequest.getName(), ResourceTypeConstants.RULE.name(), ResourceOperation.CREATE, "i18n_copy_rule");
        } catch (Exception e) {
            HRException.throwException(e.getMessage());
        }
        return ruleRequest;
    }

    public void saveRuleTagMapping(String ruleId, String tagKey) {
        deleteRuleTag(null, ruleId);
        if (StringUtils.isNotEmpty(tagKey)) {
            RuleTagMapping sfRulesTagMapping = new RuleTagMapping();
            sfRulesTagMapping.setRuleId(ruleId);
            sfRulesTagMapping.setTagKey(tagKey);
            ruleTagMappingMapper.insert(sfRulesTagMapping);
        }
    }

    public void saveRuleGroupMapping(String ruleId, List<String> ruleGroups) {
        deleteRuleGroupMapping(ruleId);
        if (ruleGroups.isEmpty()) {
            return;
        }
        for (String ruleGroup : ruleGroups) {
            RuleGroupMapping ruleGroupMapping = new RuleGroupMapping();
            ruleGroupMapping.setRuleId(ruleId);
            ruleGroupMapping.setGroupId(ruleGroup);
            ruleGroupMappingMapper.insertSelective(ruleGroupMapping);
        }
    }

    public void saveRuleInspectionReportMapping(String ruleId, List<String> ruleInspectionReports) {
        deleteRuleInspectionReportMapping(ruleId);
        if (ruleInspectionReports.isEmpty()) {
            return;
        }
        for (String ruleInspectionReport : ruleInspectionReports) {
            RuleInspectionReportMapping ruleGroupMapping = new RuleInspectionReportMapping();
            ruleGroupMapping.setRuleId(ruleId);
            ruleGroupMapping.setReportId(ruleInspectionReport);
            ruleInspectionReportMappingMapper.insertSelective(ruleGroupMapping);
        }
    }

    public CloudTask runRules(RuleDTO ruleDTO, LoginUser loginUser) {
        try {
            cloudTaskService.validateYaml(BeanUtils.copyBean(new QuartzTaskDTO(), ruleDTO));
            QuartzTaskDTO quartzTaskDTO = new QuartzTaskDTO();
            BeanUtils.copyBean(quartzTaskDTO, ruleDTO);
            quartzTaskDTO.setType("manual");
            return cloudTaskService.saveManualTask(quartzTaskDTO, null, loginUser);
        } catch (Exception e) {
            throw new HRException(e.getMessage());
        }
    }

    public boolean dryRun(RuleDTO ruleDTO) throws Exception {
        QuartzTaskDTO quartzTaskDTO = new QuartzTaskDTO();
        BeanUtils.copyBean(quartzTaskDTO, ruleDTO);
        //validate && dryrun
        return cloudTaskService.ruleDryRun(quartzTaskDTO);
    }

    public void deleteRule(String id, LoginUser loginUser) {
        Rule rule = ruleMapper.selectByPrimaryKey(id);

        //内置规则不可以删除
        if(rule.getFlag()) return;

        ResourceRuleExample resourceItemRuleExample = new ResourceRuleExample();
        resourceItemRuleExample.createCriteria().andRuleIdEqualTo(id);
        List<ResourceRule> list = resourceRuleMapper.selectByExample(resourceItemRuleExample);
        if (list.isEmpty()) {
            ruleMapper.deleteByPrimaryKey(id);
            RuleTagMappingExample example = new RuleTagMappingExample();
            example.createCriteria().andRuleIdEqualTo(id);
            ruleTagMappingMapper.deleteByExample(example);
            RuleTypeExample ruleTypeExample = new RuleTypeExample();
            ruleTypeExample.createCriteria().andRuleIdEqualTo(id);
            ruleTypeMapper.deleteByExample(ruleTypeExample);
            deleteRuleInspectionReportMapping(id);
            deleteRuleGroupMapping(id);
            operationLogService.log(loginUser, id, rule.getName(), ResourceTypeConstants.RULE.name(), ResourceOperation.DELETE, "i18n_delete_rule");
        } else {
            HRException.throwException(Translator.get("i18n_compliance_rule_useage_error"));
        }
    }

    public void deleteRules(List<String> ids, LoginUser loginUser) throws Exception {
        ids.forEach(id -> {
            try {
                deleteRule(id, loginUser);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED, rollbackFor = {RuntimeException.class, Exception.class})
    public RuleDTO getRuleById(String id) throws Exception {
        RuleDTO ruleDTO = new RuleDTO();
        Rule rule = ruleMapper.selectByPrimaryKey(id);
        BeanUtils.copyBean(ruleDTO, rule);

        //规则标签
        List<String> tags = new LinkedList<>();
        RuleTagMappingExample example = new RuleTagMappingExample();
        example.createCriteria().andRuleIdEqualTo(id);
        List<RuleTagMapping> ruleTagMappingList = ruleTagMappingMapper.selectByExample(example);
        ruleTagMappingList.forEach(obj -> tags.add(obj.getTagKey()));
        ruleDTO.setTags(tags);

        //规则组
        List<Integer> ruleSets = new ArrayList<>();
        RuleGroupMappingExample ruleGroupMappingExample = new RuleGroupMappingExample();
        ruleGroupMappingExample.createCriteria().andRuleIdEqualTo(id);
        List<RuleGroupMapping> ruleGroupMappings = ruleGroupMappingMapper.selectByExample(ruleGroupMappingExample);
        ruleGroupMappings.forEach(obj -> ruleSets.add(Integer.valueOf(obj.getGroupId())));
        ruleDTO.setRuleSets(ruleSets);

        //规则条例
        List<Integer> inspectionSeports = new ArrayList<>();
        RuleInspectionReportMappingExample ruleInspectionReportMappingExample = new RuleInspectionReportMappingExample();
        ruleInspectionReportMappingExample.createCriteria().andRuleIdEqualTo(id);
        List<RuleInspectionReportMapping> ruleInspectionReportMappings = ruleInspectionReportMappingMapper.selectByExample(ruleInspectionReportMappingExample);
        ruleInspectionReportMappings.forEach(obj -> inspectionSeports.add(Integer.valueOf(obj.getReportId())));
        ruleDTO.setInspectionSeports(inspectionSeports);

        return ruleDTO;
    }

    public RuleDTO getRuleDtoById(String ruleId, String accountId) throws Exception {
        RuleDTO ruleDTO = extRuleMapper.selectByPrimaryKey(ruleId, accountId);

        //规则标签
        List<String> tags = new LinkedList<>();
        RuleTagMappingExample example = new RuleTagMappingExample();
        example.createCriteria().andRuleIdEqualTo(ruleId);
        List<RuleTagMapping> ruleTagMappingList = ruleTagMappingMapper.selectByExample(example);
        ruleTagMappingList.forEach(obj -> tags.add(obj.getTagKey()));
        ruleDTO.setTags(tags);

        //规则组
        List<Integer> ruleSets = new ArrayList<>();
        RuleGroupMappingExample ruleGroupMappingExample = new RuleGroupMappingExample();
        ruleGroupMappingExample.createCriteria().andRuleIdEqualTo(ruleId);
        List<RuleGroupMapping> ruleGroupMappings = ruleGroupMappingMapper.selectByExample(ruleGroupMappingExample);
        ruleGroupMappings.forEach(obj -> ruleSets.add(Integer.valueOf(obj.getGroupId())));
        ruleDTO.setRuleSets(ruleSets);

        //规则条例
        List<Integer> inspectionSeports = new ArrayList<>();
        RuleInspectionReportMappingExample ruleInspectionReportMappingExample = new RuleInspectionReportMappingExample();
        ruleInspectionReportMappingExample.createCriteria().andRuleIdEqualTo(ruleId);
        List<RuleInspectionReportMapping> ruleInspectionReportMappings = ruleInspectionReportMappingMapper.selectByExample(ruleInspectionReportMappingExample);
        ruleInspectionReportMappings.forEach(obj -> inspectionSeports.add(Integer.valueOf(obj.getReportId())));
        ruleDTO.setInspectionSeports(inspectionSeports);

        return ruleDTO;
    }

    public RuleDTO getRuleByTaskId(String taskId) throws Exception {
        CloudTaskItemExample cloudTaskItemExample = new CloudTaskItemExample();
        cloudTaskItemExample.createCriteria().andTaskIdEqualTo(taskId);
        String id = cloudTaskItemMapper.selectByExample(cloudTaskItemExample).get(0).getRuleId();

        RuleDTO ruleDTO = new RuleDTO();
        Rule rule = ruleMapper.selectByPrimaryKey(id);
        BeanUtils.copyBean(ruleDTO, rule);

        //规则标签
        List<String> tags = new LinkedList<>();
        RuleTagMappingExample example = new RuleTagMappingExample();
        example.createCriteria().andRuleIdEqualTo(id);
        List<RuleTagMapping> ruleTagMappingList = ruleTagMappingMapper.selectByExample(example);
        ruleTagMappingList.forEach(obj -> tags.add(obj.getTagKey()));
        ruleDTO.setTags(tags);

        //规则组
        List<Integer> ruleSets = new ArrayList<>();
        RuleGroupMappingExample ruleGroupMappingExample = new RuleGroupMappingExample();
        ruleGroupMappingExample.createCriteria().andRuleIdEqualTo(id);
        List<RuleGroupMapping> ruleGroupMappings = ruleGroupMappingMapper.selectByExample(ruleGroupMappingExample);
        ruleGroupMappings.forEach(obj -> ruleSets.add(Integer.valueOf(obj.getGroupId())));
        ruleDTO.setRuleSets(ruleSets);

        //规则条例
        List<Integer> inspectionSeports = new ArrayList<>();
        RuleInspectionReportMappingExample ruleInspectionReportMappingExample = new RuleInspectionReportMappingExample();
        ruleInspectionReportMappingExample.createCriteria().andRuleIdEqualTo(id);
        List<RuleInspectionReportMapping> ruleInspectionReportMappings = ruleInspectionReportMappingMapper.selectByExample(ruleInspectionReportMappingExample);
        ruleInspectionReportMappings.forEach(obj -> inspectionSeports.add(Integer.valueOf(obj.getReportId())));
        ruleDTO.setInspectionSeports(inspectionSeports);

        return ruleDTO;
    }

    public boolean getRuleByName(CreateRuleRequest request) {
        RuleExample example = new RuleExample();
        RuleExample.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo(request.getName());
        List<Rule> list = ruleMapper.selectByExample(example);
        if (StringUtils.equalsIgnoreCase(request.getType(), "edit")) {
            if (!list.isEmpty() && list.size() > 1) {
                return false;
            } else if (list.size() == 1) {
                return StringUtils.equalsIgnoreCase(request.getId(), list.get(0).getId());
            }
        } else return list.isEmpty();
        return true;
    }

    public List<RuleTagDTO> getRuleTags() throws Exception {
        RuleTagExample tagExample = new RuleTagExample();
        tagExample.setOrderByClause("_index");
        List<RuleTagDTO> ruleTagDTOList = new LinkedList<>();
        for (RuleTag ruleTag : ruleTagMapper.selectByExample(tagExample)) {
            RuleTagDTO ruleTagDTO = new RuleTagDTO();
            BeanUtils.copyBean(ruleTagDTO, ruleTag);
            ruleTagDTOList.add(ruleTagDTO);
        }
        return ruleTagDTOList;
    }

    public RuleTag saveRuleTag(RuleTag ruleTag, LoginUser loginUser) {
        ruleTagMapper.insertSelective(ruleTag);
        operationLogService.log(loginUser, ruleTag.getTagKey(), ruleTag.getTagName(), ResourceTypeConstants.RULE_TAG.name(), ResourceOperation.CREATE, "i18n_create_rule_tag");
        return ruleTag;
    }

    public RuleTag updateRuleTag(RuleTag ruleTag, LoginUser loginUser) {
        ruleTagMapper.updateByPrimaryKey(ruleTag);
        operationLogService.log(loginUser, ruleTag.getTagKey(), ruleTag.getTagName(), ResourceTypeConstants.RULE_TAG.name(), ResourceOperation.UPDATE, "i18n_update_rule_tag");
        return ruleTag;
    }

    public void deleteRuleTag(String tagkey, String ruleId) {
        if (StringUtils.isNotBlank(tagkey)) {
            ruleTagMapper.deleteByPrimaryKey(tagkey);
        }
        if (StringUtils.isNotBlank(ruleId)) {
            RuleTagMappingExample ruleTagMappingExample = new RuleTagMappingExample();
            ruleTagMappingExample.createCriteria().andRuleIdEqualTo(ruleId);
            ruleTagMappingMapper.deleteByExample(ruleTagMappingExample);
        }
    }

    public void deleteRuleGroupMapping(String ruleId) {
        if (StringUtils.isNotBlank(ruleId)) {
            RuleGroupMappingExample example = new RuleGroupMappingExample();
            example.createCriteria().andRuleIdEqualTo(ruleId);
            ruleGroupMappingMapper.deleteByExample(example);
        }
    }

    public void deleteRuleInspectionReportMapping(String ruleId) {
        if (StringUtils.isNotBlank(ruleId)) {
            RuleInspectionReportMappingExample example = new RuleInspectionReportMappingExample();
            example.createCriteria().andRuleIdEqualTo(ruleId);
            ruleInspectionReportMappingMapper.deleteByExample(example);
        }
    }

    public int deleteRuleTagByTagKey(String tagkey, LoginUser loginUser) {
        RuleTag ruleTag = ruleTagMapper.selectByPrimaryKey(tagkey);
        if(ruleTag.getFlag()) return 0;
        RuleTagMappingExample example = new RuleTagMappingExample();
        example.createCriteria().andTagKeyEqualTo(tagkey);
        List<RuleTagMapping> list = ruleTagMappingMapper.selectByExample(example);
        if (!list.isEmpty()) HRException.throwException(Translator.get("i18n_not_allowed"));
        operationLogService.log(loginUser, tagkey, tagkey, ResourceTypeConstants.RULE_TAG.name(), ResourceOperation.DELETE, "i18n_delete_rule_tag");
        return ruleTagMapper.deleteByPrimaryKey(tagkey);
    }

    public void deleteRuleTags(List<String> ids, LoginUser loginUser) throws Exception {
        ids.forEach(id -> {
            try {
                deleteRuleTagByTagKey(id, loginUser);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    public List<Map<String, String>> getAllResourceTypes() {
        return extRuleTypeMapper.selectByExample();
    }

    public List<Map<String, String>> cloudResourceTypes() {
        return extRuleTypeMapper.cloudResourceTypes();
    }

    public List<RuleGroup> getRuleGroups(String pluginId) {
        RuleGroupExample example = new RuleGroupExample();
        example.createCriteria().andPluginIdEqualTo(pluginId);
        return ruleGroupMapper.selectByExample(example);
    }

    public List<RuleInspectionReport> getRuleInspectionReportList(RuleInspectionReportRequest request) {
        return extRuleInspectionReportMapper.getRuleInspectionReportList(request);
    }

    public String getResourceTypesById(String ruleId) {
        return extRuleTypeMapper.getResourceTypesById(ruleId);
    }

    public int changeStatus(Rule rule) {
        return ruleMapper.updateByPrimaryKeySelective(rule);
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED, rollbackFor = {RuntimeException.class, Exception.class})
    public void scan(ScanGroupRequest request, LoginUser loginUser) throws Exception {
        AccountWithBLOBs account = accountMapper.selectByPrimaryKey(request.getAccountId());
        Integer scanId = systemProviderService.insertScanHistory(account);
        for (Integer groupId : request.getGroups()) {
            this.scanGroups(request.getAccountId(), scanId, groupId.toString(), loginUser);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED, rollbackFor = {RuntimeException.class, Exception.class})
    public void scanK8s(ScanGroupRequest request, CloudNative cloudNative, LoginUser loginUser) throws Exception {
        Integer scanId = systemProviderService.insertScanHistory(cloudNative);
        for (Integer groupId : request.getGroups()) {
            this.scanK8sGroups(cloudNative, scanId, groupId.toString(), loginUser);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED, rollbackFor = {RuntimeException.class, Exception.class})
    public void reScans(String accountId) throws Exception {
        List<String> status = Arrays.stream(new String[]{CloudTaskConstants.TASK_STATUS.APPROVED.name(), CloudTaskConstants.TASK_STATUS.PROCESSING.name()}).collect(Collectors.toList());
        CloudTaskExample example = new CloudTaskExample();
        example.createCriteria().andAccountIdEqualTo(accountId).andStatusNotIn(status);
        List<CloudTask> cloudTaskList = cloudTaskMapper.selectByExample(example);
        for (CloudTask cloudTask : cloudTaskList) {
            cloudTask.setStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
            cloudTaskMapper.updateByPrimaryKeySelective(cloudTask);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED, rollbackFor = {RuntimeException.class, Exception.class})
    public void reScansK8s(String accountId) throws Exception {
        List<String> status = Arrays.stream(new String[]{CloudTaskConstants.TASK_STATUS.APPROVED.name(), CloudTaskConstants.TASK_STATUS.PROCESSING.name()}).collect(Collectors.toList());
        CloudTaskExample example = new CloudTaskExample();
        example.createCriteria().andAccountIdEqualTo(accountId).andStatusNotIn(status);
        List<CloudTask> cloudTaskList = cloudTaskMapper.selectByExample(example);
        for (CloudTask cloudTask : cloudTaskList) {
            cloudTask.setStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
            cloudTaskMapper.updateByPrimaryKeySelective(cloudTask);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED, rollbackFor = {RuntimeException.class, Exception.class})
    public String reScan(String taskId, String accountId, LoginUser loginUser) throws Exception {
        CloudTaskItemExample example = new CloudTaskItemExample();
        example.createCriteria().andTaskIdEqualTo(taskId);
        List<CloudTaskItem> cloudTaskItems = cloudTaskItemMapper.selectByExample(example);
        AccountWithBLOBs account = accountMapper.selectByPrimaryKey(accountId);
        RuleDTO rule = getRuleDtoById(cloudTaskItems.get(0).getRuleId(), accountId);
        if (!rule.getStatus()) HRException.throwException(Translator.get("i18n_disabled_rules_not_scanning"));
        Integer scanId = systemProviderService.insertScanHistory(account);
        return this.dealTask(rule, account, scanId, null, loginUser);
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED, rollbackFor = {RuntimeException.class, Exception.class})
    public String reScanK8s(String taskId, String accountId, LoginUser loginUser) throws Exception {
        CloudTaskItemExample example = new CloudTaskItemExample();
        example.createCriteria().andTaskIdEqualTo(taskId);
        List<CloudTaskItem> cloudTaskItems = cloudTaskItemMapper.selectByExample(example);
        CloudNative cloudNative = k8sProviderService.cloudNative(accountId);
        RuleDTO rule = getRuleDtoById(cloudTaskItems.get(0).getRuleId(), accountId);
        if (!rule.getStatus()) HRException.throwException(Translator.get("i18n_disabled_rules_not_scanning"));
        Integer scanId = systemProviderService.insertScanHistory(cloudNative);
        return this.dealK8sTask(rule, cloudNative, scanId, null, loginUser);
    }

    private void scanGroups(String accountId, Integer scanId, String groupId, LoginUser loginUser) {
        try {
            AccountWithBLOBs account = accountMapper.selectByPrimaryKey(accountId);
            String messageOrderId = systemProviderService.createMessageOrder(account);

            List<RuleDTO> ruleDTOS = extRuleGroupMapper.getRules(accountId, groupId);
            for (RuleDTO rule : ruleDTOS) {
                this.dealTask(rule, account, scanId, messageOrderId, loginUser);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
    }

    private void scanK8sGroups(CloudNative cloudNative, Integer scanId, String groupId, LoginUser loginUser) {
        try {
            String messageOrderId = systemProviderService.createK8sMessageOrder(cloudNative);

            List<RuleDTO> ruleDTOS = extRuleGroupMapper.getRules(cloudNative.getId(), groupId);
            for (RuleDTO rule : ruleDTOS) {
                this.dealK8sTask(rule, cloudNative, scanId, messageOrderId, loginUser);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
    }

    private void scan(AccountWithBLOBs account, LoginUser loginUser) throws Exception {
        Integer scanId = systemProviderService.insertScanHistory(account);

        String messageOrderId = systemProviderService.createMessageOrder(account);

        QuartzTaskDTO dto = new QuartzTaskDTO();
        dto.setAccountId(account.getId());
        dto.setPluginId(account.getPluginId());
        dto.setStatus(true);
        List<RuleDTO> ruleDTOS = accountService.getRules(dto);
        for (RuleDTO rule : ruleDTOS) {
            this.dealTask(rule, account, scanId, messageOrderId, loginUser);
        }
    }

    private String dealTask(RuleDTO rule, AccountWithBLOBs account, Integer scanId, String messageOrderId, LoginUser loginUser) {
        try {
            if (rule.getStatus() && !cloudTaskService.checkRuleTaskStatus(account.getId(), rule.getId(),
                    new String[]{CloudTaskConstants.TASK_STATUS.APPROVED.name(), CloudTaskConstants.TASK_STATUS.PROCESSING.name()})) {
                QuartzTaskDTO quartzTaskDTO = new QuartzTaskDTO();
                BeanUtils.copyBean(quartzTaskDTO, rule);
                List<SelectTag> selectTags = new LinkedList<>();
                SelectTag s = new SelectTag();
                s.setAccountId(account.getId());
                JSONArray array = parseArray(rule.getRegions() != null ? rule.getRegions() : account.getRegions());
                JSONObject object;
                List<String> regions = new ArrayList<>();
                for (int i = 0; i < array.size(); i++) {
                    try {
                        object = array.getJSONObject(i);
                        String value = object.getString("regionId");
                        regions.add(value);
                    } catch (Exception e) {
                        String value = array.get(0).toString();
                        regions.add(value);
                    }
                }
                s.setRegions(regions);
                selectTags.add(s);
                quartzTaskDTO.setSelectTags(selectTags);
                quartzTaskDTO.setType("manual");
                quartzTaskDTO.setAccountId(account.getId());
                quartzTaskDTO.setTaskName(rule.getName());
                CloudTask cloudTask = cloudTaskService.saveManualTask(quartzTaskDTO, messageOrderId, loginUser);
                if (scanId != null) {
                    if (PlatformUtils.isSupportCloudAccount(cloudTask.getPluginId())) {
                        systemProviderService.insertScanTaskHistory(cloudTask, scanId, cloudTask.getAccountId(), TaskEnum.cloudAccount.getType());
                    }
                }
                return cloudTask.getId();
            } else {
                systemProviderService.deleteScanTaskHistory(scanId);
                LogUtil.warn(rule.getName() + ": " + Translator.get("i18n_disabled_rules_not_scanning"));
            }
        } catch (Exception e) {
            HRException.throwException(e.getMessage());
        }
        return "";
    }

    private String dealK8sTask(RuleDTO rule, CloudNative cloudNative, Integer scanId, String messageOrderId, LoginUser loginUser) {
        try {
            if (rule.getStatus() && !cloudTaskService.checkRuleTaskStatus(cloudNative.getId(), rule.getId(),
                    new String[]{CloudTaskConstants.TASK_STATUS.APPROVED.name(), CloudTaskConstants.TASK_STATUS.PROCESSING.name()})) {
                QuartzTaskDTO quartzTaskDTO = new QuartzTaskDTO();
                BeanUtils.copyBean(quartzTaskDTO, rule);
                List<SelectTag> selectTags = new LinkedList<>();
                SelectTag s = new SelectTag();
                s.setAccountId(cloudNative.getId());
                List<String> regions = new ArrayList<>();
                regions.add("all-namespcace");
                s.setRegions(regions);
                selectTags.add(s);
                quartzTaskDTO.setSelectTags(selectTags);
                quartzTaskDTO.setType("manual");
                quartzTaskDTO.setAccountId(cloudNative.getId());
                quartzTaskDTO.setTaskName(rule.getName());
                CloudTask cloudTask = cloudTaskService.saveK8sManualTask(quartzTaskDTO, messageOrderId, loginUser);
                if (scanId != null) {
                    if (PlatformUtils.isSupportCloudAccount(cloudTask.getPluginId())) {
                        systemProviderService.insertScanTaskHistory(cloudTask, scanId, cloudTask.getAccountId(), TaskEnum.k8sAccount.getType());
                    }
                }
                return cloudTask.getId();
            } else {
                systemProviderService.deleteScanTaskHistory(scanId);
                LogUtil.warn(rule.getName() + ": " + Translator.get("i18n_disabled_rules_not_scanning"));
            }
        } catch (Exception e) {
            HRException.throwException(e.getMessage());
        }
        return "";
    }

    public RuleGroup saveRuleGroup(RuleGroup ruleGroup) {
        ruleGroupMapper.insertSelective(ruleGroup);
        return ruleGroup;
    }

    public RuleGroup updateRuleGroup(RuleGroup ruleGroup) {
        ruleGroupMapper.updateByPrimaryKey(ruleGroup);
        return ruleGroup;
    }

    public void deleteRuleGroupById(Integer id, LoginUser loginUser) {
        RuleGroup ruleGroup = ruleGroupMapper.selectByPrimaryKey(id);
        //内置规则组不可以被删除
        if(!ruleGroup.getFlag()) {
            ruleGroupMapper.deleteByPrimaryKey(id);
            RuleGroupMappingExample example = new RuleGroupMappingExample();
            example.createCriteria().andGroupIdEqualTo(id.toString());
            ruleGroupMappingMapper.deleteByExample(example);
            operationLogService.log(loginUser, ruleGroup.getId().toString(), ruleGroup.getName(), ResourceTypeConstants.RULE_GROUP.name(), ResourceOperation.DELETE, "i18n_delete_delete_rule_group");
        }
    }

    public void deleteGroups(List<Integer> ids, LoginUser loginUser) throws Exception {
        ids.forEach(id -> {
            try {
                deleteRuleGroupById(id, loginUser);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    public List<GroupDTO> groups(List<String> ids) {
        List<GroupDTO> groupDTOS = new LinkedList<>();
        for (String id : ids) {
            AccountWithBLOBs account = accountMapper.selectByPrimaryKey(id);
            RuleGroupRequest request = new RuleGroupRequest();
            request.setPluginId(account.getPluginId());
            GroupDTO groupDTO = new GroupDTO();
            groupDTO.setAccountWithBLOBs(account);
            groupDTO.setGroups(extRuleGroupMapper.list(request));
            groupDTOS.add(groupDTO);
        }
        return groupDTOS;
    }

    public List<RuleGroup> groupsByAccountId(String pluginId) {
        RuleGroupExample example = new RuleGroupExample();
        example.createCriteria().andPluginIdEqualTo(pluginId);
        List<RuleGroup> groups = ruleGroupMapper.selectByExample(example);
        return groups;
    }

    public List<Rule> allBindList(String id) {
        List<String> ids = new ArrayList<>();
        RuleGroupMappingExample example = new RuleGroupMappingExample();
        example.createCriteria().andGroupIdEqualTo(id);
        List<RuleGroupMapping> list = ruleGroupMappingMapper.selectByExample(example);
        for (RuleGroupMapping groupMapping : list) {
            ids.add(groupMapping.getRuleId());
        }
        RuleExample ruleExample = new RuleExample();
        if (ids.size() > 0) {
            ruleExample.createCriteria().andIdIn(ids);
            ruleExample.setOrderByClause("name");
            return ruleMapper.selectByExample(ruleExample);
        }
        return new ArrayList<>();
    }

    public List<Rule> unBindList(Integer id) {
        RuleGroup ruleGroup = ruleGroupMapper.selectByPrimaryKey(id);
        RuleExample ruleExample = new RuleExample();
        ruleExample.createCriteria().andPluginIdEqualTo(ruleGroup.getPluginId());
        ruleExample.setOrderByClause("name");
        return ruleMapper.selectByExample(ruleExample);
    }

    public void bindRule(BindRuleRequest request) throws Exception {
        String groupId = request.getGroupId();
        RuleGroupMappingExample example = new RuleGroupMappingExample();
        example.createCriteria().andGroupIdEqualTo(groupId);
        ruleGroupMappingMapper.deleteByExample(example);
        for (String id : request.getCloudValue()) {
            RuleGroupMapping record = new RuleGroupMapping();
            record.setRuleId(id);
            record.setGroupId(groupId);
            ruleGroupMappingMapper.insertSelective(record);
        }
    }

    public void scanByGroup(String groupId, String accountId, LoginUser loginUser) {
        scanGroups(accountId, null, groupId, loginUser);
    }
}
