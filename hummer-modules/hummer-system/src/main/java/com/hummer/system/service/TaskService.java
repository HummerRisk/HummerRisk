package com.hummer.system.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hummer.cloud.api.ICloudProviderService;
import com.hummer.common.core.constant.*;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.task.*;
import com.hummer.common.core.dto.*;
import com.hummer.common.core.exception.HRException;
import com.hummer.common.core.i18n.Translator;
import com.hummer.common.core.utils.BeanUtils;
import com.hummer.common.core.utils.UUIDUtil;
import com.hummer.common.security.service.TokenService;
import com.hummer.k8s.api.IK8sProviderService;
import com.hummer.system.mapper.*;
import com.hummer.system.mapper.ext.ExtTaskMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.alibaba.fastjson.JSON.parseArray;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TaskService {

    @Autowired
    private FavoriteMapper favoriteMapper;
    @Autowired
    private ExtTaskMapper extTaskMapper;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private TaskItemMapper taskItemMapper;
    @Autowired
    private TaskItemResourceMapper taskItemResourceMapper;
    @Autowired
    private TaskItemResourceLogMapper taskItemResourceLogMapper;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private OperationLogService operationLogService;
    @DubboReference
    private ICloudProviderService cloudProviderService;
    @DubboReference
    private IK8sProviderService k8sProviderService;


    public List<Favorite> listFavorites() {
        FavoriteExample example = new FavoriteExample();
        example.setOrderByClause("create_time desc");
        return favoriteMapper.selectByExample(example);
    }

    public AccountTreeDTO listAccounts() {
        AccountTreeDTO dto = new AccountTreeDTO();
        //云账号
        AccountExample accountExample = new AccountExample();
        accountExample.setOrderByClause("create_time desc");
        List<AccountVo> accounts = extTaskMapper.selectAccountByExample(accountExample);
        dto.setCloudAccount(accounts);
        //主机
        ServerExample serverExample = new ServerExample();
        serverExample.setOrderByClause("create_time desc");
        List<ServerVo> servers = extTaskMapper.selectServerByExample(serverExample);
        dto.setServerAccount(servers);
        //镜像
        ImageExample imageExample = new ImageExample();
        imageExample.setOrderByClause("create_time desc");
        List<ImageVo> images = extTaskMapper.selectImageByExample(imageExample);
        dto.setImageAccount(images);
        //源码
        CodeExample codeExample = new CodeExample();
        codeExample.setOrderByClause("create_time desc");
        List<CodeVo> codeVos = extTaskMapper.selectCodeByExample(codeExample);
        dto.setCodeAccount(codeVos);
        //文件
        FileSystemExample fileSystemExample = new FileSystemExample();
        fileSystemExample.setOrderByClause("create_time desc");
        List<FileSystemVo> fileSystemVos = extTaskMapper.selectFsByExample(fileSystemExample);
        dto.setFsAccount(fileSystemVos);
        //云原生
        CloudNativeExample cloudNativeExample = new CloudNativeExample();
        cloudNativeExample.setOrderByClause("create_time desc");
        List<K8sVo> k8sVos = extTaskMapper.selectK8sByExample(cloudNativeExample);
        dto.setK8sAccount(k8sVos);
        //部署
        CloudNativeConfigExample cloudNativeConfigExample = new CloudNativeConfigExample();
        cloudNativeConfigExample.setOrderByClause("create_time desc");
        List<ConfigVo> configVos = extTaskMapper.selectConfigByExample(cloudNativeConfigExample);
        dto.setConfigAccount(configVos);
        return dto;
    }

    public Favorite addOrDelFavorite(Favorite favorite) {
        FavoriteExample example = new FavoriteExample();
        example.createCriteria().andSourceIdEqualTo(favorite.getId());
        List<Favorite> list = favoriteMapper.selectByExample(example);
        if (list.size() > 0) {
            favoriteMapper.deleteByExample(example);
        } else {
            favorite.setSourceId(favorite.getId());
            favorite.setId(UUIDUtil.newUUID());
            favorite.setCreateTime(System.currentTimeMillis());
            favorite.setUpdateTime(System.currentTimeMillis());
            favorite.setCreator(tokenService.getLoginUser().getUserId());
            favorite.setCreatorName(tokenService.getLoginUser().getUser().getName());
            favoriteMapper.insertSelective(favorite);
        }
        return favorite;
    }

    public void deleteFavorite(String id) {
        favoriteMapper.deleteByPrimaryKey(id);
    }

    public List<RuleVo> allList(RuleVo ruleVo) {
        List<RuleVo> allList = new LinkedList<>();
        if (StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.cloudAccount.getType())) {
            allList = extTaskMapper.cloudRuleList(ruleVo);
        } else if (StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.serverAccount.getType())) {
            allList = extTaskMapper.serverRuleList(ruleVo);
        } else if (StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.k8sAccount.getType())) {
            allList = extTaskMapper.k8sRuleList(ruleVo);
        } else if (StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.configAccount.getType())) {
            allList = extTaskMapper.configRuleList(ruleVo);
        } else if (StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.imageAccount.getType())) {
            allList = extTaskMapper.imageRuleList(ruleVo);
        } else if (StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.codeAccount.getType())) {
            allList = extTaskMapper.codeRuleList(ruleVo);
        } else if (StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.fsAccount.getType())) {
            allList = extTaskMapper.fsRuleList(ruleVo);
        }
        if (ruleVo.getAccountType() != null) allList.addAll(extTaskMapper.ruleTagList(ruleVo));
        if (ruleVo.getAccountType() != null && StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.cloudAccount.getType()))
            allList.addAll(extTaskMapper.ruleGroupList(ruleVo));
        return allList;
    }

    public List<RuleVo> ruleList(RuleVo ruleVo) {
        if (StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.cloudAccount.getType())) {
            return extTaskMapper.cloudRuleList(ruleVo);
        } else if (StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.serverAccount.getType())) {
            return extTaskMapper.serverRuleList(ruleVo);
        } else if (StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.imageAccount.getType())) {
            return extTaskMapper.imageRuleList(ruleVo);
        } else if (StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.k8sAccount.getType())) {
            return extTaskMapper.k8sRuleList(ruleVo);
        } else if (StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.configAccount.getType())) {
            return extTaskMapper.configRuleList(ruleVo);
        } else if (StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.codeAccount.getType())) {
            return extTaskMapper.codeRuleList(ruleVo);
        } else if (StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.fsAccount.getType())) {
            return extTaskMapper.fsRuleList(ruleVo);
        }
        return new LinkedList<>();
    }

    public List<RuleVo> ruleTagList(RuleVo ruleVo) {
        if (ruleVo.getAccountType() != null)
            return extTaskMapper.ruleTagList(ruleVo);
        return new LinkedList<>();
    }

    public List<RuleVo> ruleGroupList(RuleVo ruleVo) {
        if (ruleVo.getAccountType() != null && StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.cloudAccount.getType()))
            return extTaskMapper.ruleGroupList(ruleVo);
        return new LinkedList<>();
    }

    public TaskRuleDTO detailRule(RuleVo ruleVo) {
        TaskRuleDTO ruleDTO = new TaskRuleDTO();
        if (StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.cloudAccount.getType())) {
            ruleDTO.setRuleDTO(extTaskMapper.cloudDetailRule(ruleVo));
        } else if (StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.serverAccount.getType())) {
            ruleDTO.setServerRuleDTO(extTaskMapper.serverDetailRule(ruleVo));
        } else if (StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.k8sAccount.getType())) {
            ruleDTO.setK8sRuleDTO(extTaskMapper.k8sDetailRule(ruleVo));
        } else if (StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.configAccount.getType())) {
            ruleDTO.setConfigRuleDTO(extTaskMapper.configDetailRule(ruleVo));
        } else if (StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.imageAccount.getType())) {
            ruleDTO.setImageRuleDTO(extTaskMapper.imageDetailRule(ruleVo));
        } else if (StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.codeAccount.getType())) {
            ruleDTO.setCodeRuleDTO(extTaskMapper.codeDetailRule(ruleVo));
        } else if (StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.fsAccount.getType())) {
            ruleDTO.setFsRuleDTO(extTaskMapper.fsDetailRule(ruleVo));
        }
        return ruleDTO;
    }

    public List<TaskTagGroupDTO> detailTag(RuleVo ruleVo) {
        return extTaskMapper.detailTag(ruleVo);
    }

    public List<TaskTagGroupDTO> detailGroup(RuleVo ruleVo) {
        return extTaskMapper.detailGroup(ruleVo);
    }

    public List<TaskVo> taskList(TaskRequest request) {
        return extTaskMapper.taskList(request);
    }

    public List<Task> allTaskList() {
        return taskMapper.selectByExample(null);
    }

    public TaskReportDTO report(String id) {
        return extTaskMapper.report(id);
    }

    public TaskVo getTask(String id) {
        TaskRequest request = new TaskRequest();
        request.setId(id);
        return extTaskMapper.taskList(request).get(0);
    }

    public int addTask(TaskDTO taskDTO) throws Exception {
        Task task = BeanUtils.copyBean(new Task(), taskDTO);
        task.setId(UUIDUtil.newUUID());
        task.setStatus(TaskConstants.TASK_STATUS.WAITING.name());
        task.setApplyUser(tokenService.getLoginUser().getUserId());
        task.setCreateTime(System.currentTimeMillis());
        task.setType(TaskConstants.TaskType.manual.name());
        int i = taskMapper.insertSelective(task);
        for (TaskItem taskItem : taskDTO.getTaskItemList()) {
            taskItem.setId(UUIDUtil.newUUID());
            taskItem.setTaskId(task.getId());
            taskItem.setStatus(TaskConstants.TASK_STATUS.WAITING.name());
            taskItem.setCreateTime(System.currentTimeMillis());
            taskItemMapper.insertSelective(taskItem);
        }
        return i;
    }

    public int editTask(TaskDTO taskDTO) throws Exception {
        this.deleteTask(taskDTO.getId());
        Task task = BeanUtils.copyBean(new Task(), taskDTO);
        task.setId(UUIDUtil.newUUID());
        task.setStatus(TaskConstants.TASK_STATUS.WAITING.name());
        task.setApplyUser(tokenService.getLoginUser().getUserId());
        task.setCreateTime(System.currentTimeMillis());
        int i = taskMapper.insertSelective(task);
        for (TaskItem taskItem : taskDTO.getTaskItemList()) {
            taskItem.setId(UUIDUtil.newUUID());
            taskItem.setTaskId(task.getId());
            taskItem.setStatus(TaskConstants.TASK_STATUS.WAITING.name());
            taskItem.setCreateTime(System.currentTimeMillis());
            taskItemMapper.insertSelective(taskItem);
        }
        return i;
    }

    public void deleteTask(String taskId) throws Exception {
        try {
            TaskItemExample example = new TaskItemExample();
            example.createCriteria().andTaskIdEqualTo(taskId);
            List<TaskItem> taskItems = taskItemMapper.selectByExample(example);
            for (TaskItem taskItem : taskItems) {
                TaskItemResourceExample taskItemResourceExample = new TaskItemResourceExample();
                taskItemResourceExample.createCriteria().andTaskIdEqualTo(taskId).andTaskItemIdEqualTo(taskItem.getId());
                taskItemResourceMapper.deleteByExample(taskItemResourceExample);
                TaskItemResourceLogExample taskItemResourceLogExample = new TaskItemResourceLogExample();
                taskItemResourceLogExample.createCriteria().andTaskItemIdEqualTo(taskItem.getId());
                taskItemResourceLogMapper.deleteByExample(taskItemResourceLogExample);
                taskItemMapper.deleteByPrimaryKey(taskItem.getId());
            }
            taskMapper.deleteByPrimaryKey(taskId);
        } catch (Exception e) {
            HRException.throwException(e.getMessage());
        }
    }

    public List<TaskItem> taskItemList(TaskRequest request) {
        TaskItemExample example = new TaskItemExample();
        example.createCriteria().andTaskIdEqualTo(request.getId());
        example.setOrderByClause("task_order");
        return taskItemMapper.selectByExample(example);
    }

    public void executeTask(String id) throws Exception {
        Task task = taskMapper.selectByPrimaryKey(id);
        TaskItemExample example = new TaskItemExample();
        example.createCriteria().andTaskIdEqualTo(id);
        example.setOrderByClause("task_order desc");
        List<TaskItem> taskItems = taskItemMapper.selectByExample(example);
        for (TaskItem taskItem : taskItems) {
            String ruleType = taskItem.getRuleType();
            if (StringUtils.equalsIgnoreCase(ruleType, TaskConstants.RuleType.rule.name())) {
                String resourceId = "", ruleId = "", ruleName = "";
                if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.cloudAccount.getType())) {
                    Rule rule = cloudProviderService.selectRule(taskItem.getSourceId());
                    ruleId = rule.getId();
                    ruleName = rule.getName();
                    resourceId = this.cloudResource(rule, taskItem.getAccountId());
                } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.vulnAccount.getType())) {
                    Rule rule = cloudProviderService.selectRule(taskItem.getSourceId());
                    ruleId = rule.getId();
                    ruleName = rule.getName();
                    resourceId = this.vulnResource(rule, taskItem.getAccountId());
                } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.serverAccount.getType())) {
                    ServerRule rule = k8sProviderService.serverRule(taskItem.getSourceId());
                    ruleId = rule.getId();
                    ruleName = rule.getName();
                    resourceId = this.serverResource(taskItem.getSourceId(), taskItem.getAccountId());
                } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.k8sAccount.getType())) {
                    CloudNativeRule rule = k8sProviderService.cloudNativeRule(taskItem.getSourceId());
                    ruleId = rule.getId();
                    ruleName = rule.getName();
                    resourceId = this.k8sResource(taskItem.getSourceId(), taskItem.getAccountId());
                } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.configAccount.getType())) {
                    CloudNativeConfigRule rule = k8sProviderService.cloudNativeConfigRule(taskItem.getSourceId());
                    ruleId = rule.getId();
                    ruleName = rule.getName();
                    resourceId = this.configResource(taskItem.getSourceId(), taskItem.getAccountId());
                } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.imageAccount.getType())) {
                    ImageRule rule = k8sProviderService.imageRule(taskItem.getSourceId());
                    ruleId = rule.getId();
                    ruleName = rule.getName();
                    resourceId = this.imageResource(taskItem.getSourceId(), taskItem.getAccountId());
                } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.codeAccount.getType())) {
                    CodeRule rule = k8sProviderService.codeRule(taskItem.getSourceId());
                    ruleId = rule.getId();
                    ruleName = rule.getName();
                    resourceId = this.codeResource(taskItem.getSourceId(), taskItem.getAccountId());
                } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.fsAccount.getType())) {
                    FileSystemRule rule = k8sProviderService.fileSystemRule(taskItem.getSourceId());
                    ruleId = rule.getId();
                    ruleName = rule.getName();
                    resourceId = this.fsResource(taskItem.getSourceId(), taskItem.getAccountId());
                }
                this.insertTaskItemResource(taskItem, ruleId, ruleName, resourceId);
            } else if (StringUtils.equalsIgnoreCase(ruleType, TaskConstants.RuleType.tag.name())) {
                String resourceId = "";
                List<RuleTagMapping> ruleTagMappings = this.ruleTagMappings(taskItem.getSourceId());
                AccountWithBLOBs accountWithBLOBs = cloudProviderService.selectAccountWithBLOBs(taskItem.getAccountId());
                if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.cloudAccount.getType())) {
                    for (RuleTagMapping ruleTagMapping : ruleTagMappings) {
                        Rule rule = cloudProviderService.selectRule(ruleTagMapping.getRuleId());
                        if (rule == null || rule.getPluginId() == null || !accountWithBLOBs.getPluginId().equals(rule.getPluginId())) {
                            continue;
                        }
                        resourceId = this.cloudResource(rule, taskItem.getAccountId());
                        if (resourceId == null) continue;
                        this.insertTaskItemResource(taskItem, rule.getId(), rule.getName(), resourceId);
                    }
                } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.vulnAccount.getType())) {
                    for (RuleTagMapping ruleTagMapping : ruleTagMappings) {
                        Rule rule = cloudProviderService.selectRule(ruleTagMapping.getRuleId());
                        if (rule == null || rule.getPluginId() == null || !accountWithBLOBs.getPluginId().equals(rule.getPluginId())) {
                            continue;
                        }
                        resourceId = this.vulnResource(rule, taskItem.getAccountId());
                        if (resourceId == null) continue;
                        this.insertTaskItemResource(taskItem, rule.getId(), rule.getName(), resourceId);
                    }
                } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.serverAccount.getType())) {
                    for (RuleTagMapping ruleTagMapping : ruleTagMappings) {
                        ServerRule serverRule = k8sProviderService.serverRule(ruleTagMapping.getRuleId());
                        if (serverRule == null) {
                            continue;
                        }
                        resourceId = this.serverResource(serverRule.getId(), taskItem.getAccountId());
                        if (resourceId == null) continue;
                        this.insertTaskItemResource(taskItem, serverRule.getId(), serverRule.getName(), resourceId);
                    }
                } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.k8sAccount.getType())) {
                    for (RuleTagMapping ruleTagMapping : ruleTagMappings) {
                        CloudNativeRule cloudNativeRule = k8sProviderService.cloudNativeRule(ruleTagMapping.getRuleId());
                        if (cloudNativeRule == null) {
                            continue;
                        }
                        resourceId = this.k8sResource(cloudNativeRule.getId(), taskItem.getAccountId());
                        if (resourceId == null) continue;
                        this.insertTaskItemResource(taskItem, cloudNativeRule.getId(), cloudNativeRule.getName(), resourceId);
                    }
                } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.configAccount.getType())) {
                    for (RuleTagMapping ruleTagMapping : ruleTagMappings) {
                        CloudNativeConfigRule cloudNativeConfigRule = k8sProviderService.cloudNativeConfigRule(ruleTagMapping.getRuleId());
                        if (cloudNativeConfigRule == null) {
                            continue;
                        }
                        resourceId = this.configResource(cloudNativeConfigRule.getId(), taskItem.getAccountId());
                        if (resourceId == null) continue;
                        this.insertTaskItemResource(taskItem, cloudNativeConfigRule.getId(), cloudNativeConfigRule.getName(), resourceId);
                    }
                } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.imageAccount.getType())) {
                    for (RuleTagMapping ruleTagMapping : ruleTagMappings) {
                        ImageRule imageRule = k8sProviderService.imageRule(ruleTagMapping.getRuleId());
                        if (imageRule == null) {
                            continue;
                        }
                        resourceId = this.imageResource(imageRule.getId(), taskItem.getAccountId());
                        if (resourceId == null) continue;
                        this.insertTaskItemResource(taskItem, imageRule.getId(), imageRule.getName(), resourceId);
                    }
                } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.codeAccount.getType())) {
                    for (RuleTagMapping ruleTagMapping : ruleTagMappings) {
                        CodeRule codeRule = k8sProviderService.codeRule(ruleTagMapping.getRuleId());
                        if (codeRule == null) {
                            continue;
                        }
                        resourceId = this.codeResource(codeRule.getId(), taskItem.getAccountId());
                        if (resourceId == null) continue;
                        this.insertTaskItemResource(taskItem, codeRule.getId(), codeRule.getName(), resourceId);
                    }
                } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.fsAccount.getType())) {
                    for (RuleTagMapping ruleTagMapping : ruleTagMappings) {
                        FileSystemRule fileSystemRule = k8sProviderService.fileSystemRule(ruleTagMapping.getRuleId());
                        if (fileSystemRule == null) {
                            continue;
                        }
                        resourceId = this.fsResource(fileSystemRule.getId(), taskItem.getAccountId());
                        if (resourceId == null) continue;
                        this.insertTaskItemResource(taskItem, fileSystemRule.getId(), fileSystemRule.getName(), resourceId);
                    }
                }
            } else if (StringUtils.equalsIgnoreCase(ruleType, TaskConstants.RuleType.group.name())) {
                String resourceId = "";
                List<RuleGroupMapping> ruleGroupMappings = this.ruleGroupMappings(taskItem.getSourceId());
                if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.cloudAccount.getType())) {
                    for (RuleGroupMapping ruleGroupMapping : ruleGroupMappings) {
                        Rule rule = cloudProviderService.selectRule(ruleGroupMapping.getRuleId());
                        resourceId = this.cloudResource(rule, taskItem.getAccountId());
                        if (resourceId == null) continue;
                        this.insertTaskItemResource(taskItem, rule.getId(), rule.getName(), resourceId);
                    }
                } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.vulnAccount.getType())) {
                    for (RuleGroupMapping ruleGroupMapping : ruleGroupMappings) {
                        Rule rule = cloudProviderService.selectRule(ruleGroupMapping.getRuleId());
                        resourceId = this.vulnResource(rule, taskItem.getAccountId());
                        if (resourceId == null) continue;
                        this.insertTaskItemResource(taskItem, rule.getId(), rule.getName(), resourceId);
                    }
                } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.serverAccount.getType())) {
                    for (RuleGroupMapping ruleGroupMapping : ruleGroupMappings) {
                        ServerRule serverRule = k8sProviderService.serverRule(ruleGroupMapping.getRuleId());
                        resourceId = this.serverResource(serverRule.getId(), taskItem.getAccountId());
                        this.insertTaskItemResource(taskItem, serverRule.getId(), serverRule.getName(), resourceId);
                    }
                } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.k8sAccount.getType())) {
                    for (RuleGroupMapping ruleGroupMapping : ruleGroupMappings) {
                        CloudNativeRule cloudNativeRule = k8sProviderService.cloudNativeRule(ruleGroupMapping.getRuleId());
                        resourceId = this.k8sResource(cloudNativeRule.getId(), taskItem.getAccountId());
                        this.insertTaskItemResource(taskItem, cloudNativeRule.getId(), cloudNativeRule.getName(), resourceId);
                    }
                } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.configAccount.getType())) {
                    for (RuleGroupMapping ruleGroupMapping : ruleGroupMappings) {
                        CloudNativeConfigRule cloudNativeConfigRule = k8sProviderService.cloudNativeConfigRule(ruleGroupMapping.getRuleId());
                        resourceId = this.configResource(cloudNativeConfigRule.getId(), taskItem.getAccountId());
                        this.insertTaskItemResource(taskItem, cloudNativeConfigRule.getId(), cloudNativeConfigRule.getName(), resourceId);
                    }
                } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.imageAccount.getType())) {
                    for (RuleGroupMapping ruleGroupMapping : ruleGroupMappings) {
                        ImageRule imageRule = k8sProviderService.imageRule(ruleGroupMapping.getRuleId());
                        resourceId = this.imageResource(imageRule.getId(), taskItem.getAccountId());
                        this.insertTaskItemResource(taskItem, imageRule.getId(), imageRule.getName(), resourceId);
                    }
                } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.codeAccount.getType())) {
                    for (RuleGroupMapping ruleGroupMapping : ruleGroupMappings) {
                        CodeRule codeRule = k8sProviderService.codeRule(ruleGroupMapping.getRuleId());
                        resourceId = this.codeResource(codeRule.getId(), taskItem.getAccountId());
                        this.insertTaskItemResource(taskItem, codeRule.getId(), codeRule.getName(), resourceId);
                    }
                } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.fsAccount.getType())) {
                    for (RuleGroupMapping ruleGroupMapping : ruleGroupMappings) {
                        FileSystemRule fileSystemRule = k8sProviderService.fileSystemRule(ruleGroupMapping.getRuleId());
                        resourceId = this.fsResource(fileSystemRule.getId(), taskItem.getAccountId());
                        this.insertTaskItemResource(taskItem, fileSystemRule.getId(), fileSystemRule.getName(), resourceId);
                    }
                }
            }
            taskItem.setStatus(TaskConstants.TASK_STATUS.APPROVED.name());
            taskItemMapper.updateByPrimaryKeySelective(taskItem);
        }
        task.setLastFireTime(System.currentTimeMillis());
        task.setStatus(TaskConstants.TASK_STATUS.APPROVED.name());
        taskMapper.updateByPrimaryKeySelective(task);
    }

    public void reExecute(String id) throws Exception {
        Task task = taskMapper.selectByPrimaryKey(id);
        TaskItemExample taskItemExample = new TaskItemExample();
        taskItemExample.createCriteria().andTaskIdEqualTo(id);
        taskItemExample.setOrderByClause("task_order desc");
        List<TaskItem> taskItems = taskItemMapper.selectByExample(taskItemExample);
        for (TaskItem taskItem : taskItems) {
            TaskItemResourceExample example = new TaskItemResourceExample();
            example.createCriteria().andTaskIdEqualTo(id).andTaskItemIdEqualTo(taskItem.getId());
            List<TaskItemResource> taskItemResources = taskItemResourceMapper.selectByExample(example);
            for (TaskItemResource taskItemResource : taskItemResources) {
                String ruleType = taskItem.getRuleType();
                if (StringUtils.equalsIgnoreCase(ruleType, TaskConstants.RuleType.rule.name())) {
                    String resourceId = "", ruleId = "";
                    if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.cloudAccount.getType())) {
                        Rule rule = cloudProviderService.selectRule(taskItem.getSourceId());
                        ruleId = rule.getId();
                        resourceId = cloudProviderService.reScan(taskItemResource.getResourceId(), taskItem.getAccountId());
                    } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.vulnAccount.getType())) {
                        Rule rule = cloudProviderService.selectRule(taskItem.getSourceId());
                        ruleId = rule.getId();
                        resourceId = cloudProviderService.reScan(taskItemResource.getResourceId(), taskItem.getAccountId());
                    } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.serverAccount.getType())) {
                        ServerRule rule = k8sProviderService.serverRule(taskItem.getSourceId());
                        ruleId = rule.getId();
                        resourceId = k8sProviderService.serverRescan(taskItemResource.getResourceId());
                    } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.k8sAccount.getType())) {
                        CloudNativeRule rule = k8sProviderService.cloudNativeRule(taskItem.getSourceId());
                        ruleId = rule.getId();
                        resourceId = k8sProviderService.k8sRescan(taskItemResource.getResourceId());
                    } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.configAccount.getType())) {
                        CloudNativeConfigRule rule = k8sProviderService.cloudNativeConfigRule(taskItem.getSourceId());
                        ruleId = rule.getId();
                        resourceId = k8sProviderService.codeRescan(taskItemResource.getResourceId());
                    } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.imageAccount.getType())) {
                        ImageRule rule = k8sProviderService.imageRule(taskItem.getSourceId());
                        ruleId = rule.getId();
                        resourceId = k8sProviderService.imageRescan(taskItemResource.getResourceId());
                    } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.codeAccount.getType())) {
                        CodeRule rule = k8sProviderService.codeRule(taskItem.getSourceId());
                        ruleId = rule.getId();
                        resourceId = k8sProviderService.codeRescan(taskItemResource.getResourceId());
                    } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.fsAccount.getType())) {
                        FileSystemRule rule = k8sProviderService.fileSystemRule(taskItem.getSourceId());
                        ruleId = rule.getId();
                        resourceId = k8sProviderService.fileSystemRescan(taskItemResource.getResourceId());
                    }
                    this.updateTaskItemResource(taskItemResource, ruleId, resourceId);
                } else if (StringUtils.equalsIgnoreCase(ruleType, TaskConstants.RuleType.tag.name())) {
                    String resourceId = "";
                    List<RuleTagMapping> ruleTagMappings = this.ruleTagMappings(taskItemResource.getResourceId());
                    if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.cloudAccount.getType())) {
                        for (RuleTagMapping ruleTagMapping : ruleTagMappings) {
                            CloudTaskExample cloudTaskExample = new CloudTaskExample();
                            cloudTaskExample.createCriteria().andRuleIdEqualTo(ruleTagMapping.getRuleId()).andAccountIdEqualTo(taskItem.getAccountId());
                            List<CloudTask> cloudTasks = cloudProviderService.selectCloudTaskList(cloudTaskExample);
                            for (CloudTask cloudTask : cloudTasks) {
                                Rule rule = cloudProviderService.selectRule(cloudTask.getRuleId());
                                resourceId = cloudProviderService.reScan(cloudTask.getId(), cloudTask.getAccountId());
                                if (resourceId == null) continue;
                                this.updateTaskItemResource(taskItemResource, rule.getId(), resourceId);
                            }
                        }
                    } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.vulnAccount.getType())) {
                        for (RuleTagMapping ruleTagMapping : ruleTagMappings) {
                            CloudTaskExample cloudTaskExample = new CloudTaskExample();
                            cloudTaskExample.createCriteria().andRuleIdEqualTo(ruleTagMapping.getRuleId()).andAccountIdEqualTo(taskItem.getAccountId());
                            List<CloudTask> cloudTasks = cloudProviderService.selectCloudTaskList(cloudTaskExample);
                            for (CloudTask cloudTask : cloudTasks) {
                                Rule rule = cloudProviderService.selectRule(cloudTask.getRuleId());
                                resourceId = cloudProviderService.reScan(cloudTask.getId(), cloudTask.getAccountId());
                                if (resourceId == null) continue;
                                this.updateTaskItemResource(taskItemResource, rule.getId(), resourceId);
                            }
                        }
                    } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.serverAccount.getType())) {
                        for (RuleTagMapping ruleTagMapping : ruleTagMappings) {
                            ServerResultExample serverResultExample = new ServerResultExample();
                            serverResultExample.createCriteria().andRuleIdEqualTo(ruleTagMapping.getRuleId());
                            List<ServerResult> serverResults = k8sProviderService.serverResults(serverResultExample);
                            for (ServerResult serverResult : serverResults) {
                                ServerRule serverRule = k8sProviderService.serverRule(serverResult.getRuleId());
                                resourceId = k8sProviderService.serverRescan(serverResult.getId());
                                if (resourceId == null) continue;
                                this.updateTaskItemResource(taskItemResource, serverRule.getId(), resourceId);
                            }
                        }
                    } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.k8sAccount.getType())) {
                        for (RuleTagMapping ruleTagMapping : ruleTagMappings) {
                            CloudNativeResultExample cloudNativeResultExample = new CloudNativeResultExample();
                            cloudNativeResultExample.createCriteria().andRuleIdEqualTo(ruleTagMapping.getRuleId());
                            List<CloudNativeResult> cloudNativeResults = k8sProviderService.cloudNativeResults(cloudNativeResultExample);
                            for (CloudNativeResult cloudNativeResult : cloudNativeResults) {
                                CloudNativeRule cloudNativeRule = k8sProviderService.cloudNativeRule(cloudNativeResult.getRuleId());
                                resourceId = k8sProviderService.k8sRescan(cloudNativeResult.getId());
                                if (resourceId == null) continue;
                                this.updateTaskItemResource(taskItemResource, cloudNativeRule.getId(), resourceId);
                            }
                        }
                    } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.configAccount.getType())) {
                        for (RuleTagMapping ruleTagMapping : ruleTagMappings) {
                            CloudNativeConfigResultExample cloudNativeConfigResultExample = new CloudNativeConfigResultExample();
                            cloudNativeConfigResultExample.createCriteria().andRuleIdEqualTo(ruleTagMapping.getRuleId());
                            List<CloudNativeConfigResult> cloudNativeConfigResults = k8sProviderService.cloudNativeConfigResults(cloudNativeConfigResultExample);
                            for (CloudNativeConfigResult cloudNativeConfigResult : cloudNativeConfigResults) {
                                CloudNativeConfigRule cloudNativeConfigRule = k8sProviderService.cloudNativeConfigRule(cloudNativeConfigResult.getRuleId());
                                resourceId = k8sProviderService.configRescan(cloudNativeConfigResult.getId());
                                if (resourceId == null) continue;
                                this.updateTaskItemResource(taskItemResource, cloudNativeConfigRule.getId(), resourceId);
                            }
                        }
                    } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.imageAccount.getType())) {
                        for (RuleTagMapping ruleTagMapping : ruleTagMappings) {
                            ImageResultExample imageResultExample = new ImageResultExample();
                            imageResultExample.createCriteria().andRuleIdEqualTo(ruleTagMapping.getRuleId());
                            List<ImageResult> imageResults = k8sProviderService.imageResults(imageResultExample);
                            for (ImageResult imageResult : imageResults) {
                                ImageRule imageRule = k8sProviderService.imageRule(imageResult.getRuleId());
                                resourceId = k8sProviderService.imageRescan(imageResult.getId());
                                if (resourceId == null) continue;
                                this.updateTaskItemResource(taskItemResource, imageRule.getId(), resourceId);
                            }
                        }
                    } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.codeAccount.getType())) {
                        for (RuleTagMapping ruleTagMapping : ruleTagMappings) {
                            CodeResultExample codeResultExample = new CodeResultExample();
                            codeResultExample.createCriteria().andRuleIdEqualTo(ruleTagMapping.getRuleId());
                            List<CodeResult> codeResults = k8sProviderService.codeResults(codeResultExample);
                            for (CodeResult codeResult : codeResults) {
                                CodeRule codeRule = k8sProviderService.codeRule(codeResult.getRuleId());
                                resourceId = k8sProviderService.codeRescan(codeRule.getId());
                                if (resourceId == null) continue;
                                this.updateTaskItemResource(taskItemResource, codeRule.getId(), resourceId);
                            }
                        }
                    } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.fsAccount.getType())) {
                        for (RuleTagMapping ruleTagMapping : ruleTagMappings) {
                            FileSystemResultExample fileSystemResultExample = new FileSystemResultExample();
                            fileSystemResultExample.createCriteria().andRuleIdEqualTo(ruleTagMapping.getRuleId());
                            List<FileSystemResult> fileSystemResults = k8sProviderService.fileSystemResults(fileSystemResultExample);
                            for (FileSystemResult fileSystemResult : fileSystemResults) {
                                FileSystemRule fileSystemRule = k8sProviderService.fileSystemRule(fileSystemResult.getRuleId());
                                resourceId = k8sProviderService.fileSystemRescan(fileSystemRule.getId());
                                if (resourceId == null) continue;
                                this.updateTaskItemResource(taskItemResource, fileSystemRule.getId(), resourceId);
                            }
                        }
                    }
                } else if (StringUtils.equalsIgnoreCase(ruleType, TaskConstants.RuleType.group.name())) {
                    String resourceId = "";
                    List<RuleGroupMapping> ruleGroupMappings = this.ruleGroupMappings(taskItemResource.getResourceId());
                    if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.cloudAccount.getType())) {
                        for (RuleGroupMapping ruleGroupMapping : ruleGroupMappings) {
                            CloudTaskExample cloudTaskExample = new CloudTaskExample();
                            cloudTaskExample.createCriteria().andRuleIdEqualTo(ruleGroupMapping.getRuleId()).andAccountIdEqualTo(taskItem.getAccountId());
                            List<CloudTask> cloudTasks = cloudProviderService.selectCloudTaskList(cloudTaskExample);
                            for (CloudTask cloudTask : cloudTasks) {
                                Rule rule = cloudProviderService.selectRule(cloudTask.getRuleId());
                                resourceId = cloudProviderService.reScan(cloudTask.getId(), cloudTask.getAccountId());
                                if (resourceId == null) continue;
                                this.updateTaskItemResource(taskItemResource, rule.getId(), resourceId);
                            }
                        }
                    } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.vulnAccount.getType())) {
                        for (RuleGroupMapping ruleGroupMapping : ruleGroupMappings) {
                            CloudTaskExample cloudTaskExample = new CloudTaskExample();
                            cloudTaskExample.createCriteria().andRuleIdEqualTo(ruleGroupMapping.getRuleId()).andAccountIdEqualTo(taskItem.getAccountId());
                            List<CloudTask> cloudTasks = cloudProviderService.selectCloudTaskList(cloudTaskExample);
                            for (CloudTask cloudTask : cloudTasks) {
                                Rule rule = cloudProviderService.selectRule(cloudTask.getRuleId());
                                resourceId = cloudProviderService.reScan(cloudTask.getId(), cloudTask.getAccountId());
                                if (resourceId == null) continue;
                                this.updateTaskItemResource(taskItemResource, rule.getId(), resourceId);
                            }
                        }
                    } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.serverAccount.getType())) {
                        for (RuleGroupMapping ruleGroupMapping : ruleGroupMappings) {
                            ServerResultExample serverResultExample = new ServerResultExample();
                            serverResultExample.createCriteria().andRuleIdEqualTo(ruleGroupMapping.getRuleId());
                            List<ServerResult> serverResults = k8sProviderService.serverResults(serverResultExample);
                            for (ServerResult serverResult : serverResults) {
                                ServerRule serverRule = k8sProviderService.serverRule(serverResult.getRuleId());
                                resourceId = k8sProviderService.serverRescan(serverResult.getId());
                                if (resourceId == null) continue;
                                this.updateTaskItemResource(taskItemResource, serverRule.getId(), resourceId);
                            }
                        }
                    } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.k8sAccount.getType())) {
                        for (RuleGroupMapping ruleGroupMapping : ruleGroupMappings) {
                            CloudNativeResultExample cloudNativeResultExample = new CloudNativeResultExample();
                            cloudNativeResultExample.createCriteria().andRuleIdEqualTo(ruleGroupMapping.getRuleId());
                            List<CloudNativeResult> cloudNativeResults = k8sProviderService.cloudNativeResults(cloudNativeResultExample);
                            for (CloudNativeResult cloudNativeResult : cloudNativeResults) {
                                CloudNativeRule cloudNativeRule = k8sProviderService.cloudNativeRule(cloudNativeResult.getRuleId());
                                resourceId = k8sProviderService.k8sRescan(cloudNativeResult.getId());
                                if (resourceId == null) continue;
                                this.updateTaskItemResource(taskItemResource, cloudNativeRule.getId(), resourceId);
                            }
                        }
                    } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.configAccount.getType())) {
                        for (RuleGroupMapping ruleGroupMapping : ruleGroupMappings) {
                            CloudNativeConfigResultExample cloudNativeConfigResultExample = new CloudNativeConfigResultExample();
                            cloudNativeConfigResultExample.createCriteria().andRuleIdEqualTo(ruleGroupMapping.getRuleId());
                            List<CloudNativeConfigResult> cloudNativeConfigResults = k8sProviderService.cloudNativeConfigResults(cloudNativeConfigResultExample);
                            for (CloudNativeConfigResult cloudNativeConfigResult : cloudNativeConfigResults) {
                                CloudNativeConfigRule cloudNativeConfigRule = k8sProviderService.cloudNativeConfigRule(cloudNativeConfigResult.getRuleId());
                                resourceId = k8sProviderService.configRescan(cloudNativeConfigResult.getId());
                                if (resourceId == null) continue;
                                this.updateTaskItemResource(taskItemResource, cloudNativeConfigRule.getId(), resourceId);
                            }
                        }
                    } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.imageAccount.getType())) {
                        for (RuleGroupMapping ruleGroupMapping : ruleGroupMappings) {
                            ImageResultExample imageResultExample = new ImageResultExample();
                            imageResultExample.createCriteria().andRuleIdEqualTo(ruleGroupMapping.getRuleId());
                            List<ImageResult> imageResults = k8sProviderService.imageResults(imageResultExample);
                            for (ImageResult imageResult : imageResults) {
                                ImageRule imageRule = k8sProviderService.imageRule(imageResult.getRuleId());
                                resourceId = k8sProviderService.imageRescan(imageResult.getId());
                                if (resourceId == null) continue;
                                this.updateTaskItemResource(taskItemResource, imageRule.getId(), resourceId);
                            }
                        }
                    } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.codeAccount.getType())) {
                        for (RuleGroupMapping ruleGroupMapping : ruleGroupMappings) {
                            CodeResultExample codeResultExample = new CodeResultExample();
                            codeResultExample.createCriteria().andRuleIdEqualTo(ruleGroupMapping.getRuleId());
                            List<CodeResult> codeResults = k8sProviderService.codeResults(codeResultExample);
                            for (CodeResult codeResult : codeResults) {
                                CodeRule codeRule = k8sProviderService.codeRule(codeResult.getRuleId());
                                resourceId = k8sProviderService.codeRescan(codeRule.getId());
                                if (resourceId == null) continue;
                                this.updateTaskItemResource(taskItemResource, codeRule.getId(), resourceId);
                            }
                        }
                    } else if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.fsAccount.getType())) {
                        for (RuleGroupMapping ruleGroupMapping : ruleGroupMappings) {
                            FileSystemResultExample fileSystemResultExample = new FileSystemResultExample();
                            fileSystemResultExample.createCriteria().andRuleIdEqualTo(ruleGroupMapping.getRuleId());
                            List<FileSystemResult> fileSystemResults = k8sProviderService.fileSystemResults(fileSystemResultExample);
                            for (FileSystemResult fileSystemResult : fileSystemResults) {
                                FileSystemRule fileSystemRule = k8sProviderService.fileSystemRule(fileSystemResult.getRuleId());
                                resourceId = k8sProviderService.fileSystemRescan(fileSystemRule.getId());
                                if (resourceId == null) continue;
                                this.updateTaskItemResource(taskItemResource, fileSystemRule.getId(), resourceId);
                            }
                        }
                    }
                }
                taskItem.setStatus(TaskConstants.TASK_STATUS.APPROVED.name());
                taskItemMapper.updateByPrimaryKeySelective(taskItem);
            }
            task.setLastFireTime(System.currentTimeMillis());
            task.setStatus(TaskConstants.TASK_STATUS.APPROVED.name());
        }
        taskMapper.updateByPrimaryKeySelective(task);
    }

    private String dealCloudTask(Rule rule, AccountWithBLOBs account, Integer scanId) {
        try {
            if (rule.getStatus()) {
                QuartzTaskDTO quartzTaskDTO = new QuartzTaskDTO();
                BeanUtils.copyBean(quartzTaskDTO, rule);
                List<SelectTag> selectTags = new LinkedList<>();
                SelectTag s = new SelectTag();
                s.setAccountId(account.getId());
                JSONArray array = parseArray(account.getRegions() != null ? account.getRegions() : account.getRegions());
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
                CloudTask cloudTask = cloudProviderService.saveManualTask(quartzTaskDTO, null);
                historyService.insertScanTaskHistory(cloudTask, scanId, cloudTask.getAccountId(), TaskEnum.cloudAccount.getType());
                return cloudTask.getId();
            } else {
                HRException.throwException(rule.getName() + ": " + Translator.get("i18n_disabled_rules_not_scanning"));
            }
        } catch (Exception e) {
            HRException.throwException(e.getMessage());
        }
        return "";
    }

    private String dealVulnTask(Rule rule, AccountWithBLOBs account, Integer scanId) {
        try {
            if (rule.getStatus()) {
                QuartzTaskDTO quartzTaskDTO = new QuartzTaskDTO();
                BeanUtils.copyBean(quartzTaskDTO, rule);
                List<SelectTag> selectTags = new LinkedList<>();
                SelectTag s = new SelectTag();
                s.setAccountId(account.getId());
                JSONArray array = parseArray(account.getRegions() != null ? account.getRegions() : account.getRegions());
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
                CloudTask cloudTask = cloudProviderService.saveManualTask(quartzTaskDTO, null);
                historyService.insertScanTaskHistory(cloudTask, scanId, cloudTask.getAccountId(), TaskEnum.vulnAccount.getType());
                return cloudTask.getId();
            } else {
                HRException.throwException(rule.getName() + ": " + Translator.get("i18n_disabled_rules_not_scanning"));
            }
        } catch (Exception e) {
            HRException.throwException(e.getMessage());
        }
        return "";
    }

    private String dealServerTask(ServerRule rule, Server server, Integer scanId) {
        try {
            if (rule.getStatus()) {
                ServerResult result = new ServerResult();
                String serverGroupName = k8sProviderService.serverGroupName(server.getServerGroupId());
                BeanUtils.copyBean(result, server);
                result.setId(UUIDUtil.newUUID());
                result.setServerId(server.getId());
                result.setServerGroupId(server.getServerGroupId());
                result.setServerGroupName(serverGroupName);
                result.setApplyUser(tokenService.getLoginUser().getUserId());
                result.setCreateTime(System.currentTimeMillis());
                result.setUpdateTime(System.currentTimeMillis());
                result.setServerName(server.getName());
                result.setRuleId(rule.getId());
                result.setRuleName(rule.getName());
                result.setRuleDesc(rule.getDescription());
                result.setResultStatus(TaskConstants.TASK_STATUS.APPROVED.toString());
                result.setSeverity(rule.getSeverity());
                ServerResultExample serverResultExample = new ServerResultExample();
                serverResultExample.createCriteria().andRuleIdEqualTo(rule.getId()).andServerIdEqualTo(server.getId());
                k8sProviderService.deleteServerResult(serverResultExample);
                k8sProviderService.insertServerResult(result);

                k8sProviderService.saveServerResultLog(result.getId(), "i18n_start_server_result", "", true);
                operationLogService.log(tokenService.getLoginUser().getUser(), result.getId(), result.getServerName(), ResourceTypeConstants.SERVER.name(), ResourceOperation.CREATE, "i18n_start_server_result");
                historyService.insertScanTaskHistory(result, scanId, result.getServerId(), TaskEnum.serverAccount.getType());

                historyService.insertHistoryServerResult(BeanUtils.copyBean(new HistoryServerResult(), result));
                return result.getId();
            } else {
                HRException.throwException(rule.getName() + ": " + Translator.get("i18n_disabled_rules_not_scanning"));
            }
        } catch (Exception e) {
            HRException.throwException(e.getMessage());
        }
        return "";
    }

    private String dealK8sTask(CloudNativeRule rule, CloudNative cloudNative, Integer scanId) {
        try {
            if (rule.getStatus()) {
                CloudNativeResultWithBLOBs result = new CloudNativeResultWithBLOBs();
                BeanUtils.copyBean(result, cloudNative);
                result.setId(UUIDUtil.newUUID());
                result.setCloudNativeId(cloudNative.getId());
                result.setApplyUser(tokenService.getLoginUser().getUserId());
                result.setCreateTime(System.currentTimeMillis());
                result.setUpdateTime(System.currentTimeMillis());
                result.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
                result.setUserName(tokenService.getLoginUser().getUser().getName());
                result.setRuleId(rule.getId());
                result.setRuleName(rule.getName());
                result.setRuleDesc(rule.getDescription());
                result.setSeverity(rule.getSeverity());
                CloudNativeResultExample cloudNativeResultExample = new CloudNativeResultExample();
                cloudNativeResultExample.createCriteria().andRuleIdEqualTo(rule.getId()).andCloudNativeIdEqualTo(cloudNative.getId());
                k8sProviderService.deleteK8sResult(cloudNativeResultExample);
                k8sProviderService.insertk8sResult(result);

                k8sProviderService.saveCloudNativeResultLog(result.getId(), "i18n_start_k8s_result", "", true);
                operationLogService.log(tokenService.getLoginUser().getUser(), result.getId(), result.getName(), ResourceTypeConstants.CLOUD_NATIVE.name(), ResourceOperation.CREATE, "i18n_start_k8s_result");
                historyService.insertScanTaskHistory(result, scanId, result.getCloudNativeId(), TaskEnum.k8sAccount.getType());

                historyService.insertHistoryServerResult(BeanUtils.copyBean(new HistoryServerResult(), result));
                return result.getId();
            } else {
                HRException.throwException(rule.getName() + ": " + Translator.get("i18n_disabled_rules_not_scanning"));
            }
        } catch (Exception e) {
            HRException.throwException(e.getMessage());
        }
        return "";
    }

    private String dealConfigTask(CloudNativeConfigRule rule, CloudNativeConfig config, Integer scanId) {
        try {
            if (rule.getStatus()) {
                CloudNativeConfigResult result = new CloudNativeConfigResult();
                BeanUtils.copyBean(result, config);
                result.setId(UUIDUtil.newUUID());
                result.setConfigId(config.getId());
                result.setApplyUser(tokenService.getLoginUser().getUserId());
                result.setCreateTime(System.currentTimeMillis());
                result.setUpdateTime(System.currentTimeMillis());
                result.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
                result.setUserName(tokenService.getLoginUser().getUser().getName());
                result.setRuleId(rule.getId());
                result.setRuleName(rule.getName());
                result.setRuleDesc(rule.getDescription());
                result.setSeverity(rule.getSeverity());
                CloudNativeConfigResultExample example = new CloudNativeConfigResultExample();
                example.createCriteria().andRuleIdEqualTo(rule.getId()).andConfigIdEqualTo(config.getId());
                k8sProviderService.deleteConfigResult(example);
                k8sProviderService.insertConfigResult(result);

                k8sProviderService.saveCloudNativeConfigResultLog(result.getId(), "i18n_start_config_result", "", true);
                operationLogService.log(tokenService.getLoginUser().getUser(), result.getId(), result.getName(), ResourceTypeConstants.CLOUD_NATIVE_CONFIG.name(), ResourceOperation.CREATE, "i18n_start_config_result");
                historyService.insertScanTaskHistory(result, scanId, result.getConfigId(), TaskEnum.configAccount.getType());

                historyService.insertHistoryServerResult(BeanUtils.copyBean(new HistoryServerResult(), result));
                return result.getId();
            } else {
                HRException.throwException(rule.getName() + ": " + Translator.get("i18n_disabled_rules_not_scanning"));
            }
        } catch (Exception e) {
            HRException.throwException(e.getMessage());
        }
        return "";
    }

    private String dealCodeTask(CodeRule rule, Code code, Integer scanId) {
        try {
            if (rule.getStatus()) {
                CodeResult result = new CodeResult();
                BeanUtils.copyBean(result, code);
                result.setId(UUIDUtil.newUUID());
                result.setCodeId(code.getId());
                result.setApplyUser(tokenService.getLoginUser().getUserId());
                result.setCreateTime(System.currentTimeMillis());
                result.setUpdateTime(System.currentTimeMillis());
                result.setRuleId(rule.getId());
                result.setRuleName(rule.getName());
                result.setRuleDesc(rule.getDescription());
                result.setResultStatus(TaskConstants.TASK_STATUS.APPROVED.toString());
                result.setSeverity(rule.getSeverity());
                result.setUserName(tokenService.getLoginUser().getUser().getName());
                CodeResultExample codeResultExample = new CodeResultExample();
                codeResultExample.createCriteria().andCodeIdEqualTo(code.getId()).andRuleIdEqualTo(rule.getId());
                k8sProviderService.deleteCodeResult(codeResultExample);
                k8sProviderService.insertCodeResult(result);

                k8sProviderService.saveCodeResultLog(result.getId(), "i18n_start_code_result", "", true);
                operationLogService.log(tokenService.getLoginUser().getUser(), result.getId(), result.getName(), ResourceTypeConstants.CODE.name(), ResourceOperation.CREATE, "i18n_start_code_result");
                historyService.insertScanTaskHistory(result, scanId, result.getCodeId(), TaskEnum.codeAccount.getType());
                historyService.insertHistoryCodeResult(BeanUtils.copyBean(new HistoryCodeResult(), result));
                return result.getId();
            } else {
                HRException.throwException(rule.getName() + ": " + Translator.get("i18n_disabled_rules_not_scanning"));
            }
        } catch (Exception e) {
            HRException.throwException(e.getMessage());
        }
        return "";
    }

    private String dealFsTask(FileSystemRule rule, FileSystem fileSystem, Integer scanId) {
        try {
            if (rule.getStatus()) {
                FileSystemResult result = new FileSystemResult();
                BeanUtils.copyBean(result, fileSystem);
                result.setId(UUIDUtil.newUUID());
                result.setFsId(fileSystem.getId());
                result.setApplyUser(tokenService.getLoginUser().getUserId());
                result.setCreateTime(System.currentTimeMillis());
                result.setUpdateTime(System.currentTimeMillis());
                result.setRuleId(rule.getId());
                result.setRuleName(rule.getName());
                result.setRuleDesc(rule.getDescription());
                result.setResultStatus(TaskConstants.TASK_STATUS.APPROVED.toString());
                result.setSeverity(rule.getSeverity());
                result.setUserName(tokenService.getLoginUser().getUser().getName());
                FileSystemResultExample fileSystemResultExample = new FileSystemResultExample();
                fileSystemResultExample.createCriteria().andFsIdEqualTo(fileSystem.getId()).andRuleIdEqualTo(rule.getId());
                k8sProviderService.deleteFileSystemResult(fileSystemResultExample);
                k8sProviderService.insertFileSystemResult(result);

                k8sProviderService.saveFsResultLog(result.getId(), "i18n_start_fs_result", "", true);
                operationLogService.log(tokenService.getLoginUser().getUser(), result.getId(), result.getName(), ResourceTypeConstants.FILE_SYSTEM.name(), ResourceOperation.CREATE, "i18n_start_fs_result");
                historyService.insertScanTaskHistory(result, scanId, result.getFsId(), TaskEnum.fsAccount.getType());
                historyService.insertHistoryFileSystemResult(BeanUtils.copyBean(new HistoryFileSystemResult(), result));
                return result.getId();
            } else {
                HRException.throwException(rule.getName() + ": " + Translator.get("i18n_disabled_rules_not_scanning"));
            }
        } catch (Exception e) {
            HRException.throwException(e.getMessage());
        }
        return "";
    }

    private String dealImageTask(ImageRule rule, Image image, Integer scanId) {
        try {
            if (rule.getStatus()) {
                ImageResultWithBLOBs result = new ImageResultWithBLOBs();
                BeanUtils.copyBean(result, image);
                result.setId(UUIDUtil.newUUID());
                result.setImageId(image.getId());
                result.setApplyUser(tokenService.getLoginUser().getUserId());
                result.setCreateTime(System.currentTimeMillis());
                result.setUpdateTime(System.currentTimeMillis());
                result.setRuleId(rule.getId());
                result.setRuleName(rule.getName());
                result.setRuleDesc(rule.getDescription());
                result.setResultStatus(TaskConstants.TASK_STATUS.APPROVED.toString());
                result.setSeverity(rule.getSeverity());
                result.setUserName(tokenService.getLoginUser().getUser().getName());
                ImageResultExample imageResultExample = new ImageResultExample();
                imageResultExample.createCriteria().andImageIdEqualTo(image.getId()).andRuleIdEqualTo(rule.getId());
                k8sProviderService.deleteImageResult(imageResultExample);
                k8sProviderService.insertImageResult(result);

                k8sProviderService.saveImageResultLog(result.getId(), "i18n_start_image_result", "", true);
                operationLogService.log(tokenService.getLoginUser().getUser(), result.getId(), result.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.CREATE, "i18n_start_image_result");
                historyService.insertScanTaskHistory(result, scanId, result.getImageId(), TaskEnum.imageAccount.getType());
                historyService.insertHistoryImageResult(BeanUtils.copyBean(new HistoryImageResultWithBLOBs(), result));
                return result.getId();
            } else {
                HRException.throwException(rule.getName() + ": " + Translator.get("i18n_disabled_rules_not_scanning"));
            }
        } catch (Exception e) {
            HRException.throwException(e.getMessage());
        }
        return "";
    }

    private String cloudResource(Rule rule, String accountId) throws Exception {
        if (rule == null) return null;
        AccountWithBLOBs account = cloudProviderService.selectAccountWithBLOBs(accountId);
        Integer scanId = historyService.insertScanHistory(account);
        return this.dealCloudTask(rule, account, scanId);
    }

    private String vulnResource(Rule rule, String accountId) throws Exception {
        if (rule == null) return null;
        AccountWithBLOBs account = cloudProviderService.selectAccountWithBLOBs(accountId);
        Integer scanId = historyService.insertScanHistory(account);
        return this.dealVulnTask(rule, account, scanId);
    }

    private String serverResource(String ruleId, String accountId) throws Exception {
        ServerRule serverRule = k8sProviderService.serverRule(ruleId);
        Server server = k8sProviderService.server(accountId);
        Integer scanId = historyService.insertScanHistory(server);
        return this.dealServerTask(serverRule, server, scanId);
    }

    private String k8sResource(String ruleId, String accountId) throws Exception {
        CloudNativeRule cloudNativeRule = k8sProviderService.cloudNativeRule(ruleId);
        CloudNative cloudNative = k8sProviderService.cloudNative(accountId);
        Integer scanId = historyService.insertScanHistory(cloudNative);
        return this.dealK8sTask(cloudNativeRule, cloudNative, scanId);
    }

    private String configResource(String ruleId, String accountId) throws Exception {
        CloudNativeConfigRule configRule = k8sProviderService.cloudNativeConfigRule(ruleId);
        CloudNativeConfig config = k8sProviderService.cloudNativeConfig(accountId);
        Integer scanId = historyService.insertScanHistory(config);
        return this.dealConfigTask(configRule, config, scanId);
    }

    private String codeResource(String ruleId, String accountId) throws Exception {
        CodeRule codeRule = k8sProviderService.codeRule(ruleId);
        Code code = k8sProviderService.code(accountId);
        Integer scanId = historyService.insertScanHistory(code);
        return this.dealCodeTask(codeRule, code, scanId);
    }

    private String fsResource(String ruleId, String accountId) throws Exception {
        FileSystemRule fsRule = k8sProviderService.fileSystemRule(ruleId);
        FileSystem fileSystem = k8sProviderService.fileSystem(accountId);
        Integer scanId = historyService.insertScanHistory(fileSystem);
        return this.dealFsTask(fsRule, fileSystem, scanId);
    }

    private String imageResource(String ruleId, String accountId) throws Exception {
        ImageRule imageRule = k8sProviderService.imageRule(ruleId);
        Image image = k8sProviderService.image(accountId);
        Integer scanId = historyService.insertScanHistory(image);
        return this.dealImageTask(imageRule, image, scanId);
    }

    private void insertTaskItemResource(TaskItem taskItem, String ruleId, String ruleName, String resourceId) throws Exception {
        TaskItemResource record = new TaskItemResource();
        BeanUtils.copyBean(record, taskItem);
        record.setResourceId(resourceId);
        record.setRuleId(ruleId);
        record.setRuleName(ruleName);
        record.setTaskItemId(taskItem.getId());
        record.setCreateTime(System.currentTimeMillis());
        taskItemResourceMapper.insertSelective(record);
        int key = record.getId();

        saveTaskItemResourceLog(record.getTaskItemId(), String.valueOf(key), resourceId, "i18n_start_task", "", true);
    }

    public void saveTaskItemResourceLog(String taskItemId, String taskItemResourceId, String resourceId, String operation, String output, boolean result) {
        TaskItemResourceLogWithBLOBs taskItemResourceLog = new TaskItemResourceLogWithBLOBs();
        String operator = "system";
        try {
            if (tokenService.getLoginUser().getUser() != null) {
                operator = tokenService.getLoginUser().getUser().getId();
            }
        } catch (Exception e) {
            //防止单元测试无session
        }
        taskItemResourceLog.setOperator(operator);
        taskItemResourceLog.setTaskItemId(taskItemId);
        taskItemResourceLog.setTaskItemResourceId(taskItemResourceId);
        taskItemResourceLog.setResourceId(resourceId);
        taskItemResourceLog.setCreateTime(System.currentTimeMillis());
        taskItemResourceLog.setOperation(operation);
        taskItemResourceLog.setOutput(output);
        taskItemResourceLog.setResult(result);
        taskItemResourceLogMapper.insertSelective(taskItemResourceLog);
    }

    private void updateTaskItemResource(TaskItemResource taskItemResource, String ruleId, String resourceId) throws Exception {
        taskItemResource.setResourceId(resourceId);
        taskItemResource.setRuleId(ruleId);
        taskItemResourceMapper.updateByPrimaryKeySelective(taskItemResource);

        saveTaskItemResourceLog(taskItemResource.getTaskItemId(), String.valueOf(taskItemResource.getId()), resourceId, "i18n_restart_task", "", true);
    }

    private List<RuleTagMapping> ruleTagMappings(String tagKey) {
        RuleTagMappingExample ruleTagMappingExample = new RuleTagMappingExample();
        ruleTagMappingExample.createCriteria().andTagKeyEqualTo(tagKey);
        List<RuleTagMapping> ruleTagMappings = cloudProviderService.ruleTagMappings(ruleTagMappingExample);
        return ruleTagMappings;
    }

    private List<RuleGroupMapping> ruleGroupMappings(String groupId) {
        RuleGroupMappingExample ruleGroupMappingExample = new RuleGroupMappingExample();
        ruleGroupMappingExample.createCriteria().andGroupIdEqualTo(groupId);
        List<RuleGroupMapping> ruleGroupMappings = cloudProviderService.ruleGroupMappings(ruleGroupMappingExample);
        return ruleGroupMappings;
    }

    public List<TaskLogVo> taskLogList(TaskRequest request) {
        List<TaskLogVo> list = extTaskMapper.taskLogList(request);
        return list;
    }

}
