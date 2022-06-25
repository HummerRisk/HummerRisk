package com.hummerrisk.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.*;
import com.hummerrisk.base.mapper.ext.ExtRuleGroupMapper;
import com.hummerrisk.base.mapper.ext.ExtRuleMapper;
import com.hummerrisk.base.mapper.ext.ExtRuleTagMapper;
import com.hummerrisk.base.mapper.ext.ExtRuleTypeMapper;
import com.hummerrisk.commons.constants.CloudAccountConstants;
import com.hummerrisk.commons.constants.ResourceOperation;
import com.hummerrisk.commons.constants.ResourceTypeConstants;
import com.hummerrisk.commons.constants.ScanTypeConstants;
import com.hummerrisk.commons.exception.HRException;
import com.hummerrisk.commons.utils.*;
import com.hummerrisk.controller.request.rule.CreateRuleRequest;
import com.hummerrisk.controller.request.rule.RuleGroupRequest;
import com.hummerrisk.controller.request.rule.RuleTagRequest;
import com.hummerrisk.dto.*;
import com.hummerrisk.i18n.Translator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Resource;
import java.util.*;

import static com.alibaba.fastjson.JSON.parseArray;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RuleService {

    @Resource @Lazy
    private RuleMapper ruleMapper;
    @Resource @Lazy
    private RuleTagMapper ruleTagMapper;
    @Resource @Lazy
    private RuleTagMappingMapper ruleTagMappingMapper;
    @Resource @Lazy
    private PluginMapper pluginMapper;
    @Resource @Lazy
    private ExtRuleMapper extRuleMapper;
    @Resource @Lazy
    private RuleTypeMapper ruleTypeMapper;
    @Resource @Lazy
    private ExtRuleTagMapper extRuleTagMapper;
    @Resource @Lazy
    private ExtRuleTypeMapper extRuleTypeMapper;
    @Resource @Lazy
    private CloudTaskService cloudTaskService;
    @Resource @Lazy
    private ResourceRuleMapper resourceRuleMapper;
    @Resource @Lazy
    private CommonThreadPool commonThreadPool;
    @Resource @Lazy
    private AccountMapper accountMapper;
    @Resource @Lazy
    private AccountService accountService;
    @Resource @Lazy
    private RuleGroupMapper ruleGroupMapper;
    @Resource @Lazy
    private RuleGroupMappingMapper ruleGroupMappingMapper;
    @Resource @Lazy
    private RuleInspectionReportMapper ruleInspectionReportMapper;
    @Resource @Lazy
    private RuleInspectionReportMappingMapper ruleInspectionReportMappingMapper;
    @Resource @Lazy
    private OrderService orderService;
    @Resource @Lazy
    private ScanHistoryMapper scanHistoryMapper;
    @Resource @Lazy
    private ExtRuleGroupMapper extRuleGroupMapper;
    @Resource @Lazy
    private CloudTaskItemMapper cloudTaskItemMapper;
    @Resource @Lazy
    private NoticeService noticeService;

    public List<RuleDTO> cloudList(CreateRuleRequest ruleRequest) {
        return extRuleMapper.cloudList(ruleRequest);
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

    public Rule saveRules(CreateRuleRequest ruleRequest) {
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
            } else {
                Plugin plugin = pluginMapper.selectByPrimaryKey(ruleRequest.getPluginId());
                ruleRequest.setPluginId(plugin.getId());
                ruleRequest.setPluginName(plugin.getName());
                ruleRequest.setPluginIcon(plugin.getIcon());
                ruleRequest.setFlag(false);
                ruleRequest.setLastModified(System.currentTimeMillis());
                ruleMapper.updateByPrimaryKeySelective(ruleRequest);
            }

            saveRuleTagMapping(ruleRequest.getId(), ruleRequest.getTagKey());
            saveRuleGroupMapping(ruleRequest.getId(), ruleRequest.getRuleSets());
            saveRuleInspectionReportMapping(ruleRequest.getId(), ruleRequest.getInspectionSeports());
            saveRuleType(ruleRequest);

            OperationLogService.log(SessionUtils.getUser(), ruleRequest.getId(), ruleRequest.getName(), ResourceTypeConstants.RULE.name(), ResourceOperation.CREATE, "创建规则");
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
            } else if(StringUtils.equalsIgnoreCase(ruleRequest.getScanType(), ScanTypeConstants.nuclei.name())){
                String resourceType = "nuclei";
                example.createCriteria().andRuleIdEqualTo(ruleRequest.getId()).andResourceTypeEqualTo(resourceType);
                List<RuleType> ruleTypes = ruleTypeMapper.selectByExample(example);
                if (ruleTypes.isEmpty()) {
                    ruleType.setId(UUIDUtil.newUUID());
                    ruleType.setResourceType(resourceType);
                    ruleTypeMapper.insertSelective(ruleType);
                }
            } else if(StringUtils.equalsIgnoreCase(ruleRequest.getScanType(), ScanTypeConstants.prowler.name())){
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

    public Rule copyRule(CreateRuleRequest ruleRequest) {
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
            OperationLogService.log(SessionUtils.getUser(), ruleRequest.getId(), ruleRequest.getName(), ResourceTypeConstants.RULE.name(), ResourceOperation.CREATE, "复制规则");
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

    public Object runRules(RuleDTO ruleDTO) {
        try {
            cloudTaskService.validateYaml(BeanUtils.copyBean(new QuartzTaskDTO(), ruleDTO));
            QuartzTaskDTO quartzTaskDTO = new QuartzTaskDTO();
            BeanUtils.copyBean(quartzTaskDTO, ruleDTO);
            quartzTaskDTO.setType("manual");
            return cloudTaskService.saveManualTask(quartzTaskDTO, null);
        } catch (Exception e) {
            throw new HRException(e.getMessage());
        }
    }

    public Object dryRun(RuleDTO ruleDTO) throws Exception {
        QuartzTaskDTO quartzTaskDTO = new QuartzTaskDTO();
        BeanUtils.copyBean(quartzTaskDTO, ruleDTO);
        //validate && dryrun
        return cloudTaskService.dryRun(quartzTaskDTO);
    }

    public void deleteRule(String id) {
        Rule rule = ruleMapper.selectByPrimaryKey(id);
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
            OperationLogService.log(SessionUtils.getUser(), id, rule.getName(), ResourceTypeConstants.RULE.name(), ResourceOperation.DELETE, "删除规则");
        } else {
            HRException.throwException(Translator.get("i18n_compliance_rule_useage_error"));
        }
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

    public RuleTag saveRuleTag(RuleTag ruleTag) {
        ruleTagMapper.insertSelective(ruleTag);
        return ruleTag;
    }

    public RuleTag updateRuleTag(RuleTag ruleTag) {
        ruleTagMapper.updateByPrimaryKey(ruleTag);
        return ruleTag;
    }

    public void deleteRuleTag(String tagkey, String ruleId) {
        if (StringUtils.isNotBlank(tagkey)) {
            ruleTagMapper.deleteByPrimaryKey(tagkey);
            RuleTagExample ruleTagExample = new RuleTagExample();
            ruleTagExample.createCriteria().andTagKeyEqualTo(tagkey);
            ruleTagMapper.deleteByExample(ruleTagExample);
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

    public int deleteRuleTagByTagKey(String tagkey) {
        RuleTagMappingExample example = new RuleTagMappingExample();
        example.createCriteria().andTagKeyEqualTo(tagkey);
        List<RuleTagMapping> list = ruleTagMappingMapper.selectByExample(example);
        if (!list.isEmpty()) HRException.throwException(Translator.get("i18n_not_allowed"));
        OperationLogService.log(SessionUtils.getUser(), tagkey, tagkey, ResourceTypeConstants.RULE_TAG.name(), ResourceOperation.DELETE, "删除规则标签");
        return ruleTagMapper.deleteByPrimaryKey(tagkey);
    }

    public List<Map<String, String>> getAllResourceTypes() {
        return extRuleTypeMapper.selectByExample();
    }

    public List<RuleGroup> getRuleGroups(String pluginId) {
        RuleGroupExample example = new RuleGroupExample();
        example.createCriteria().andPluginIdEqualTo(pluginId);
        return ruleGroupMapper.selectByExample(example);
    }

    public List<RuleInspectionReport> getRuleInspectionReport(RuleInspectionReport ruleInspectionReport) {
        RuleInspectionReportExample example = new RuleInspectionReportExample();
        RuleInspectionReportExample.Criteria criteria = example.createCriteria();
        if(ruleInspectionReport.getProject()!=null) criteria.andProjectLike(ruleInspectionReport.getProject());
        if(ruleInspectionReport.getItemSortFirstLevel()!=null) criteria.andItemSortFirstLevelLike(ruleInspectionReport.getItemSortFirstLevel());
        if(ruleInspectionReport.getItemSortSecondLevel()!=null) criteria.andItemSortSecondLevelLike(ruleInspectionReport.getItemSortSecondLevel());
        if(ruleInspectionReport.getImprovement()!=null) criteria.andImprovementLike(ruleInspectionReport.getImprovement());
        return ruleInspectionReportMapper.selectByExample(example);
    }

    public String getResourceTypesById(String ruleId) {
        return extRuleTypeMapper.getResourceTypesById(ruleId);
    }

    public int changeStatus(Rule rule) {
        return ruleMapper.updateByPrimaryKeySelective(rule);
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED, rollbackFor = {RuntimeException.class, Exception.class})
    public void scan(List<String> scanCheckedGroups) {
        scanCheckedGroups.forEach(scan -> {
            String[] str = scan.split("/");
            AccountWithBLOBs account = accountMapper.selectByPrimaryKey(str[0]);
            this.scanGroups(account, str[1]);
        });
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED, rollbackFor = {RuntimeException.class, Exception.class})
    public void reScans(String accountId) {
        AccountWithBLOBs account = accountMapper.selectByPrimaryKey(accountId);
        this.scan(account);
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED, rollbackFor = {RuntimeException.class, Exception.class})
    public void reScan(String taskId, String accountId) throws Exception {
        CloudTaskItemExample example = new CloudTaskItemExample();
        example.createCriteria().andTaskIdEqualTo(taskId);
        List<CloudTaskItem> cloudTaskItems = cloudTaskItemMapper.selectByExample(example);
        AccountWithBLOBs account = accountMapper.selectByPrimaryKey(accountId);
        RuleDTO rule = getRuleDtoById(cloudTaskItems.get(0).getRuleId(), accountId);
        if (!rule.getStatus()) HRException.throwException(Translator.get("i18n_disabled_rules_not_scanning"));
        Integer scanId = orderService.insertScanHistory(account);
        this.dealTask(rule, account, scanId, null);
    }

    private void scanGroups(AccountWithBLOBs account, String groupId) {
        try {
            Integer scanId = orderService.insertScanHistory(account);

            String messageOrderId = noticeService.createMessageOrder(account);

            List<RuleDTO> ruleDTOS = extRuleGroupMapper.getRules(account.getId(), groupId);
            for (RuleDTO rule : ruleDTOS) {
                this.dealTask(rule, account, scanId, messageOrderId);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
    }

    private void scan(AccountWithBLOBs account) {
        Integer scanId = orderService.insertScanHistory(account);

        String messageOrderId = noticeService.createMessageOrder(account);

        QuartzTaskDTO dto = new QuartzTaskDTO();
        dto.setAccountId(account.getId());
        dto.setPluginId(account.getPluginId());
        dto.setStatus(true);
        List<RuleDTO> ruleDTOS = accountService.getRules(dto);
        for (RuleDTO rule : ruleDTOS) {
            this.dealTask(rule, account, scanId, messageOrderId);
        }
    }

    private void dealTask (RuleDTO rule, AccountWithBLOBs account, Integer scanId, String messageOrderId) {
        try {
            if (rule.getStatus()) {
                QuartzTaskDTO quartzTaskDTO = new QuartzTaskDTO();
                BeanUtils.copyBean(quartzTaskDTO, rule);
                List<SelectTag> selectTags = new LinkedList<>();
                SelectTag s = new SelectTag();
                s.setAccountId(account.getId());
                JSONArray array = parseArray(rule.getRegions()!=null?rule.getRegions():account.getRegions());
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
                CloudTask cloudTask = cloudTaskService.saveManualTask(quartzTaskDTO, messageOrderId);
                orderService.insertTaskHistory(cloudTask, scanId);
            } else {
                LogUtil.warn(rule.getName() + ": " + Translator.get("i18n_disabled_rules_not_scanning"));
                HRException.throwException(rule.getName() + ": " + Translator.get("i18n_disabled_rules_not_scanning"));
            }
        } catch (Exception e) {
            LogUtil.error(e);
            HRException.throwException(e.getMessage());
        }
    }

    public void insertScanHistory (String accountId) {
        orderService.insertScanHistory(accountMapper.selectByPrimaryKey(accountId));
    }

    public void syncScanHistory () {
        AccountExample accountExample = new AccountExample();
        accountExample.createCriteria().andStatusEqualTo(CloudAccountConstants.Status.VALID.name());
        List<AccountWithBLOBs> accountList = accountMapper.selectByExampleWithBLOBs(accountExample);
        accountList.forEach(account -> {
            if (PlatformUtils.isSupportCloudAccount(account.getPluginId())) {
                try {
                    RuleExample ruleExample = new RuleExample();
                    ruleExample.createCriteria().andPluginIdEqualTo(account.getPluginId());
                    List<Rule> rules = ruleMapper.selectByExample(ruleExample);
                    if (rules.isEmpty()) return;

                    long current = System.currentTimeMillis();
                    long zero = current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();//当天00点

                    ScanHistoryExample example = new ScanHistoryExample();
                    example.createCriteria().andAccountIdEqualTo(account.getId()).andCreateTimeEqualTo(zero);
                    List<ScanHistory> list = scanHistoryMapper.selectByExample(example);
                    if (!list.isEmpty()) {
                        orderService.insertScanHistory(account);
                    } else {
                        Integer scanId = orderService.insertScanHistory(account);

                        QuartzTaskDTO dto = new QuartzTaskDTO();
                        dto.setAccountId(account.getId());
                        dto.setPluginId(account.getPluginId());
                        dto.setStatus(true);
                        List<RuleDTO> ruleDTOS = accountService.getRules(dto);
                        for (RuleDTO rule : ruleDTOS) {
                            commonThreadPool.addTask(() -> {
                                try {
                                    QuartzTaskDTO quartzTaskDTO = new QuartzTaskDTO();
                                    BeanUtils.copyBean(quartzTaskDTO, rule);
                                    List<SelectTag> selectTags = new LinkedList<>();
                                    SelectTag s = new SelectTag();
                                    s.setAccountId(account.getId());
                                    JSONArray jsonArray = parseArray(account.getRegions());
                                    JSONObject object;
                                    List<String> regions = new ArrayList<>();
                                    for (int i = 0; i < jsonArray.size(); i++) {
                                        object = jsonArray.getJSONObject(i);
                                        String value = object.getString("regionId");
                                        regions.add(value);
                                    }
                                    s.setRegions(regions);
                                    selectTags.add(s);
                                    quartzTaskDTO.setSelectTags(selectTags);
                                    quartzTaskDTO.setType("manual");
                                    quartzTaskDTO.setAccountId(account.getId());
                                    quartzTaskDTO.setTaskName(rule.getName());
                                    CloudTask cloudTask = cloudTaskService.saveManualTask(quartzTaskDTO, null);
                                    orderService.insertTaskHistory(cloudTask, scanId);
                                } catch (java.lang.Exception e) {
                                    LogUtil.error(e);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    LogUtil.error(e.getMessage());
                }
            }
        });
    }

    public RuleGroup saveRuleGroup(RuleGroup ruleGroup) {
        ruleGroupMapper.insertSelective(ruleGroup);
        return ruleGroup;
    }

    public RuleGroup updateRuleGroup(RuleGroup ruleGroup) {
        ruleGroupMapper.updateByPrimaryKey(ruleGroup);
        return ruleGroup;
    }

    public int deleteRuleGroupById(Integer id) {
        return ruleGroupMapper.deleteByPrimaryKey(id);
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
}
