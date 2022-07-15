package com.hummerrisk.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.domain.Package;
import com.hummerrisk.base.mapper.*;
import com.hummerrisk.base.mapper.ext.ExtTaskMapper;
import com.hummerrisk.commons.constants.*;
import com.hummerrisk.commons.exception.HRException;
import com.hummerrisk.commons.utils.*;
import com.hummerrisk.controller.request.task.*;
import com.hummerrisk.dto.*;
import com.hummerrisk.i18n.Translator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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

    @Resource
    private FavoriteMapper favoriteMapper;
    @Resource
    private ExtTaskMapper extTaskMapper;
    @Resource
    private TaskMapper taskMapper;
    @Resource
    private TaskItemMapper taskItemMapper;
    @Resource
    private TaskItemResourceMapper taskItemResourceMapper;
    @Resource
    private TaskItemLogMapper taskItemLogMapper;
    @Resource
    private RuleMapper ruleMapper;
    @Resource
    private ServerRuleMapper serverRuleMapper;
    @Resource
    private ServerMapper serverMapper;
    @Resource
    private ServerGroupMapper serverGroupMapper;
    @Resource
    private ServerResultMapper serverResultMapper;
    @Resource
    private ServerService serverService;
    @Resource
    private PackageRuleMapper packageRuleMapper;
    @Resource
    private PackageMapper packageMapper;
    @Resource
    private PackageResultMapper packageResultMapper;
    @Resource
    private PackageService packageService;
    @Resource
    private ImageRuleMapper imageRuleMapper;
    @Resource
    private ImageMapper imageMapper;
    @Resource
    private ImageResultMapper imageResultMapper;
    @Resource
    private ImageRepoMapper imageRepoMapper;
    @Resource
    private ImageService imageService;
    @Resource
    private RuleTagMapper ruleTagMapper;
    @Resource
    private RuleTagMappingMapper ruleTagMappingMapper;
    @Resource
    private RuleGroupMapper ruleGroupMapper;
    @Resource
    private RuleGroupMappingMapper ruleGroupMappingMapper;
    @Resource
    private NoticeService noticeService;
    @Resource
    private AccountMapper accountMapper;
    @Resource
    private OrderService orderService;
    @Resource
    private CloudTaskService cloudTaskService;
    @Resource
    private UserMapper userMapper;

    public List<Favorite> listFavorites() {
        FavoriteExample example = new FavoriteExample();
        example.setOrderByClause("create_time desc");
        return favoriteMapper.selectByExample(example);
    }

    public AccountTreeDTO listAccounts() {
        AccountTreeDTO dto = new AccountTreeDTO();
        //云账号
        AccountExample accountExample = new AccountExample();
        accountExample.createCriteria().andPluginIdNotIn(PlatformUtils.getVulnPlugin());
        accountExample.setOrderByClause("create_time desc");
        List<AccountVo> accounts = extTaskMapper.selectAccountByExample(accountExample);
        dto.setCloudAccount(accounts);
        //漏洞
        AccountExample vulnExample = new AccountExample();
        vulnExample.createCriteria().andPluginIdIn(PlatformUtils.getVulnPlugin());
        vulnExample.setOrderByClause("create_time desc");
        List<AccountVo> vluns = extTaskMapper.selectVulnByExample(vulnExample);
        dto.setVulnAccount(vluns);
        //虚拟机
        ServerExample serverExample = new ServerExample();
        serverExample.setOrderByClause("create_time desc");
        List<ServerVo> servers = extTaskMapper.selectServerByExample(serverExample);
        dto.setServerAccount(servers);
        //镜像
        ImageExample imageExample = new ImageExample();
        imageExample.setOrderByClause("create_time desc");
        List<ImageVo> images = extTaskMapper.selectImageByExample(imageExample);
        dto.setImageAccount(images);
        //软件包
        PackageExample packageExample = new PackageExample();
        packageExample.setOrderByClause("create_time desc");
        List<PackageVo> packages = extTaskMapper.selectPackageByExample(packageExample);
        dto.setPackageAccount(packages);
        return dto;
    }

    public Favorite addOrDelFavorite(Favorite favorite) {
        FavoriteExample example = new FavoriteExample();
        example.createCriteria().andSourceIdEqualTo(favorite.getId());
        List<Favorite> list = favoriteMapper.selectByExample(example);
        if(list.size() > 0) {
            favoriteMapper.deleteByExample(example);
        } else {
            favorite.setSourceId(favorite.getId());
            favorite.setId(UUIDUtil.newUUID());
            favorite.setCreateTime(System.currentTimeMillis());
            favorite.setUpdateTime(System.currentTimeMillis());
            favorite.setCreator(SessionUtils.getUserId());
            favorite.setCreatorName(SessionUtils.getUser().getName());
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
        } else if(StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.vulnAccount.getType())) {
            allList = extTaskMapper.vulnRuleList(ruleVo);
        } else if(StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.serverAccount.getType())) {
            allList = extTaskMapper.serverRuleList(ruleVo);
        } else if(StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.imageAccount.getType())) {
            allList = extTaskMapper.imageRuleList(ruleVo);
        } else if(StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.packageAccount.getType())) {
            allList = extTaskMapper.packageRuleList(ruleVo);
        }
        if(ruleVo.getAccountType()!=null) allList.addAll(extTaskMapper.ruleTagList(ruleVo));
        if(ruleVo.getAccountType()!=null && StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.cloudAccount.getType()))
            allList.addAll(extTaskMapper.ruleGroupList(ruleVo));
        return allList;
    }

    public List<RuleVo> ruleList(RuleVo ruleVo) {
        if (StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.cloudAccount.getType())) {
            return extTaskMapper.cloudRuleList(ruleVo);
        } else if(StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.vulnAccount.getType())) {
            return extTaskMapper.vulnRuleList(ruleVo);
        } else if(StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.serverAccount.getType())) {
            return extTaskMapper.serverRuleList(ruleVo);
        } else if(StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.imageAccount.getType())) {
            return extTaskMapper.imageRuleList(ruleVo);
        } else if(StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.packageAccount.getType())) {
            return extTaskMapper.packageRuleList(ruleVo);
        }
        return new LinkedList<>();
    }

    public List<RuleVo> ruleTagList(RuleVo ruleVo) {
        if(ruleVo.getAccountType()!=null)
            return extTaskMapper.ruleTagList(ruleVo);
        return new LinkedList<>();
    }

    public List<RuleVo> ruleGroupList(RuleVo ruleVo) {
        if(ruleVo.getAccountType()!=null && StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.cloudAccount.getType()))
            return extTaskMapper.ruleGroupList(ruleVo);
        return new LinkedList<>();
    }

    public TaskRuleDTO detailRule(RuleVo ruleVo) {
        TaskRuleDTO ruleDTO = new TaskRuleDTO();
        if (StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.cloudAccount.getType())) {
            ruleDTO.setRuleDTO(extTaskMapper.cloudDetailRule(ruleVo));
        } else if(StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.vulnAccount.getType())) {
            ruleDTO.setRuleDTO(extTaskMapper.vulnDetailRule(ruleVo));
        } else if(StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.serverAccount.getType())) {
            ruleDTO.setServerRuleDTO(extTaskMapper.serverDetailRule(ruleVo));
        } else if(StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.imageAccount.getType())) {
            ruleDTO.setImageRuleDTO(extTaskMapper.imageDetailRule(ruleVo));
        } else if(StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.packageAccount.getType())) {
            ruleDTO.setPackageRuleDTO(extTaskMapper.packageDetailRule(ruleVo));
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

    public int addTask(TaskDTO taskDTO) throws Exception {
        Task task = BeanUtils.copyBean(new Task(), taskDTO);
        task.setId(UUIDUtil.newUUID());
        task.setStatus(TaskConstants.TASK_STATUS.WAITING.name());
        task.setApplyUser(SessionUtils.getUserId());
        task.setCreateTime(System.currentTimeMillis());
        task.setType(TaskConstants.TaskType.manual.name());
        int i = taskMapper.insertSelective(task);
        for (TaskItem taskItem : taskDTO.getTaskItemList()) {
            taskItem.setId(UUIDUtil.newUUID());
            taskItem.setTaskId(task.getId());
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
        task.setApplyUser(SessionUtils.getUserId());
        task.setCreateTime(System.currentTimeMillis());
        int i = taskMapper.insertSelective(task);
        for (TaskItem taskItem : taskDTO.getTaskItemList()) {
            taskItem.setId(UUIDUtil.newUUID());
            taskItem.setTaskId(task.getId());
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
                TaskItemLogExample taskItemLogExample = new TaskItemLogExample();
                taskItemLogExample.createCriteria().andTaskItemIdEqualTo(taskItem.getId());
                taskItemLogMapper.deleteByExample(taskItemLogExample);
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
        example.setOrderByClause("task_order desc");
        return taskItemMapper.selectByExample(example);
    }

    public void executeTask(String id) throws Exception{
        Task task = taskMapper.selectByPrimaryKey(id);
        TaskItemExample example = new TaskItemExample();
        example.createCriteria().andTaskIdEqualTo(id);
        example.setOrderByClause("task_order desc");
        List<TaskItem> taskItems = taskItemMapper.selectByExample(example);
        for (TaskItem taskItem : taskItems) {
            String ruleType = taskItem.getRuleType();
            if (StringUtils.equalsIgnoreCase(ruleType, TaskConstants.RuleType.rule.name())) {
                String resourceId = "";
                if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.cloudAccount.getType())) {
                    resourceId = this.cloudResource(taskItem.getSourceId(), taskItem.getAccountId());
                } else if(StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.vulnAccount.getType())) {
                    resourceId = this.vulnResource(taskItem.getSourceId(), taskItem.getAccountId());
                } else if(StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.serverAccount.getType())) {
                    resourceId = this.serverResource(taskItem.getSourceId(), taskItem.getAccountId());
                } else if(StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.imageAccount.getType())) {
                    resourceId = this.imageResource(taskItem.getSourceId(), taskItem.getAccountId());
                } else if(StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.packageAccount.getType())) {
                    resourceId = this.packageResource(taskItem.getSourceId(), taskItem.getAccountId());
                }
                this.insertTaskItemResource(taskItem, resourceId);
            } else if (StringUtils.equalsIgnoreCase(ruleType, TaskConstants.RuleType.tag.name())) {
                String resourceId = "";
                List<RuleTagMapping> ruleTagMappings = this.ruleTagMappings(taskItem.getSourceId());
                if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.cloudAccount.getType())) {
                    for (RuleTagMapping ruleTagMapping : ruleTagMappings) {
                        resourceId = this.cloudResource(ruleTagMapping.getRuleId(), taskItem.getAccountId());
                        this.insertTaskItemResource(taskItem, resourceId);
                    }
                } else if(StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.vulnAccount.getType())) {
                    for (RuleTagMapping ruleTagMapping : ruleTagMappings) {
                        resourceId = this.vulnResource(ruleTagMapping.getRuleId(), taskItem.getAccountId());
                        this.insertTaskItemResource(taskItem, resourceId);
                    }
                } else if(StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.serverAccount.getType())) {
                    for (RuleTagMapping ruleTagMapping : ruleTagMappings) {
                        resourceId = this.serverResource(ruleTagMapping.getRuleId(), taskItem.getAccountId());
                        this.insertTaskItemResource(taskItem, resourceId);
                    }
                } else if(StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.imageAccount.getType())) {
                    for (RuleTagMapping ruleTagMapping : ruleTagMappings) {
                        resourceId = this.imageResource(ruleTagMapping.getRuleId(), taskItem.getAccountId());
                        this.insertTaskItemResource(taskItem, resourceId);
                    }
                } else if(StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.packageAccount.getType())) {
                    for (RuleTagMapping ruleTagMapping : ruleTagMappings) {
                        resourceId = this.packageResource(ruleTagMapping.getRuleId(), taskItem.getAccountId());
                        this.insertTaskItemResource(taskItem, resourceId);
                    }
                }
            } else if (StringUtils.equalsIgnoreCase(ruleType, TaskConstants.RuleType.group.name())) {
                String resourceId = "";
                List<RuleGroupMapping> ruleGroupMappings = this.ruleGroupMappings(taskItem.getSourceId());
                if (StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.cloudAccount.getType())) {
                    for (RuleGroupMapping ruleGroupMapping : ruleGroupMappings) {
                        resourceId = this.cloudResource(ruleGroupMapping.getRuleId(), taskItem.getAccountId());
                        this.insertTaskItemResource(taskItem, resourceId);
                    }
                } else if(StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.vulnAccount.getType())) {
                    for (RuleGroupMapping ruleGroupMapping : ruleGroupMappings) {
                        resourceId = this.vulnResource(ruleGroupMapping.getRuleId(), taskItem.getAccountId());
                        this.insertTaskItemResource(taskItem, resourceId);
                    }
                } else if(StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.serverAccount.getType())) {
                    for (RuleGroupMapping ruleGroupMapping : ruleGroupMappings) {
                        resourceId = this.serverResource(ruleGroupMapping.getRuleId(), taskItem.getAccountId());
                        this.insertTaskItemResource(taskItem, resourceId);
                    }
                } else if(StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.imageAccount.getType())) {
                    for (RuleGroupMapping ruleGroupMapping : ruleGroupMappings) {
                        resourceId = this.imageResource(ruleGroupMapping.getRuleId(), taskItem.getAccountId());
                        this.insertTaskItemResource(taskItem, resourceId);
                    }
                } else if(StringUtils.equalsIgnoreCase(taskItem.getAccountType(), TaskEnum.packageAccount.getType())) {
                    for (RuleGroupMapping ruleGroupMapping : ruleGroupMappings) {
                        resourceId = this.packageResource(ruleGroupMapping.getRuleId(), taskItem.getAccountId());
                        this.insertTaskItemResource(taskItem, resourceId);
                    }
                }
            }
            taskItem.setStatus(TaskConstants.TASK_STATUS.APPROVED.name());
            taskItemMapper.updateByPrimaryKeySelective(taskItem);
        }
    }

    public void reExecute(String id) throws Exception{
        Task task = taskMapper.selectByPrimaryKey(id);
        TaskItemExample example = new TaskItemExample();
        example.createCriteria().andTaskIdEqualTo(id);
        example.setOrderByClause("task_order desc");
        List<TaskItem> taskItems = taskItemMapper.selectByExample(example);
    }

    private String dealCloudOrVulnTask (Rule rule, AccountWithBLOBs account, Integer scanId) {
        try {
            if (rule.getStatus()) {
                QuartzTaskDTO quartzTaskDTO = new QuartzTaskDTO();
                BeanUtils.copyBean(quartzTaskDTO, rule);
                List<SelectTag> selectTags = new LinkedList<>();
                SelectTag s = new SelectTag();
                s.setAccountId(account.getId());
                JSONArray array = parseArray(account.getRegions()!=null?account.getRegions():account.getRegions());
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
                CloudTask cloudTask = cloudTaskService.saveManualTask(quartzTaskDTO, null);
                orderService.insertTaskHistory(cloudTask, scanId);
                return cloudTask.getId();
            } else {
                LogUtil.warn(rule.getName() + ": " + Translator.get("i18n_disabled_rules_not_scanning"));
                HRException.throwException(rule.getName() + ": " + Translator.get("i18n_disabled_rules_not_scanning"));
            }
        } catch (Exception e) {
            LogUtil.error(e);
            HRException.throwException(e.getMessage());
        }
        return "";
    }

    private String dealServerTask (ServerRule rule, Server server) {
        try {
            if (rule.getStatus()) {
                ServerResult result = new ServerResult();
                String serverGroupName = serverGroupMapper.selectByPrimaryKey(server.getServerGroupId()).getName();
                BeanUtils.copyBean(result, server);
                result.setId(UUIDUtil.newUUID());
                result.setServerId(server.getId());
                result.setServerGroupId(server.getServerGroupId());
                result.setServerGroupName(serverGroupName);
                result.setApplyUser(SessionUtils.getUserId());
                result.setCreateTime(System.currentTimeMillis());
                result.setUpdateTime(System.currentTimeMillis());
                result.setServerName(server.getName());
                result.setRuleId(rule.getId());
                result.setRuleName(rule.getName());
                result.setRuleDesc(rule.getDescription());
                result.setResultStatus(TaskConstants.TASK_STATUS.APPROVED.toString());
                result.setSeverity(rule.getSeverity());
                serverResultMapper.insertSelective(result);

                serverService.saveServerResultLog(result.getId(), "i18n_start_server_result", "", true);
                return result.getId();
            } else {
                LogUtil.warn(rule.getName() + ": " + Translator.get("i18n_disabled_rules_not_scanning"));
                HRException.throwException(rule.getName() + ": " + Translator.get("i18n_disabled_rules_not_scanning"));
            }
        } catch (Exception e) {
            LogUtil.error(e);
            HRException.throwException(e.getMessage());
        }
        return "";
    }

    private String dealPackageTask (PackageRule rule, Package apackage) {
        try {
            if (rule.getStatus()) {
                PackageResultWithBLOBs result = new PackageResultWithBLOBs();
                BeanUtils.copyBean(result, apackage);
                result.setId(UUIDUtil.newUUID());
                result.setPackageId(apackage.getId());
                result.setApplyUser(SessionUtils.getUserId());
                result.setCreateTime(System.currentTimeMillis());
                result.setUpdateTime(System.currentTimeMillis());
                result.setRuleId(rule.getId());
                result.setRuleName(rule.getName());
                result.setRuleDesc(rule.getDescription());
                result.setResultStatus(TaskConstants.TASK_STATUS.APPROVED.toString());
                result.setSeverity(rule.getSeverity());
                result.setUserName(userMapper.selectByPrimaryKey(SessionUtils.getUserId()).getName());
                packageResultMapper.insertSelective(result);

                packageService.savePackageResultLog(result.getId(), "i18n_start_package_result", "", true);
                OperationLogService.log(SessionUtils.getUser(), result.getId(), result.getName(), ResourceTypeConstants.PACKAGE.name(), ResourceOperation.CREATE, "开始软件包检测");
                return result.getId();
            } else {
                LogUtil.warn(rule.getName() + ": " + Translator.get("i18n_disabled_rules_not_scanning"));
                HRException.throwException(rule.getName() + ": " + Translator.get("i18n_disabled_rules_not_scanning"));
            }
        } catch (Exception e) {
            LogUtil.error(e);
            HRException.throwException(e.getMessage());
        }
        return "";
    }

    private String dealImageTask (ImageRule rule, Image image) {
        try {
            if (rule.getStatus()) {
                ImageResultWithBLOBs result = new ImageResultWithBLOBs();
                BeanUtils.copyBean(result, image);
                result.setId(UUIDUtil.newUUID());
                result.setImageId(image.getId());
                result.setApplyUser(SessionUtils.getUserId());
                result.setCreateTime(System.currentTimeMillis());
                result.setUpdateTime(System.currentTimeMillis());
                result.setRuleId(rule.getId());
                result.setRuleName(rule.getName());
                result.setRuleDesc(rule.getDescription());
                result.setResultStatus(TaskConstants.TASK_STATUS.APPROVED.toString());
                result.setSeverity(rule.getSeverity());
                result.setUserName(userMapper.selectByPrimaryKey(SessionUtils.getUserId()).getName());
                imageResultMapper.insertSelective(result);

                imageService.saveImageResultLog(result.getId(), "i18n_start_image_result", "", true);
                OperationLogService.log(SessionUtils.getUser(), result.getId(), result.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.CREATE, "开始镜像检测");
                return result.getId();
            } else {
                LogUtil.warn(rule.getName() + ": " + Translator.get("i18n_disabled_rules_not_scanning"));
                HRException.throwException(rule.getName() + ": " + Translator.get("i18n_disabled_rules_not_scanning"));
            }
        } catch (Exception e) {
            LogUtil.error(e);
            HRException.throwException(e.getMessage());
        }
        return "";
    }

    private String cloudResource(String ruleId, String accountId) {
        Rule rule = ruleMapper.selectByPrimaryKey(ruleId);
        AccountWithBLOBs account = accountMapper.selectByPrimaryKey(accountId);
        Integer scanId = orderService.insertScanHistory(account);
        //String messageOrderId = noticeService.createMessageOrder(account);
        return this.dealCloudOrVulnTask(rule, account, scanId);
    }

    private String vulnResource(String ruleId, String accountId) {
        Rule rule = ruleMapper.selectByPrimaryKey(ruleId);
        AccountWithBLOBs account = accountMapper.selectByPrimaryKey(accountId);
        Integer scanId = orderService.insertScanHistory(account);
        return this.dealCloudOrVulnTask(rule, account, scanId);
    }

    private String serverResource(String ruleId, String accountId) {
        ServerRule serverRule = serverRuleMapper.selectByPrimaryKey(ruleId);
        Server server = serverMapper.selectByPrimaryKey(accountId);
        return this.dealServerTask(serverRule, server);
    }

    private String packageResource(String ruleId, String accountId) {
        PackageRule packageRule = packageRuleMapper.selectByPrimaryKey(ruleId);
        Package aPackage = packageMapper.selectByPrimaryKey(accountId);
        return this.dealPackageTask(packageRule, aPackage);
    }

    private String imageResource(String ruleId, String accountId) {
        ImageRule imageRule = imageRuleMapper.selectByPrimaryKey(ruleId);
        Image image = imageMapper.selectByPrimaryKey(accountId);
        return this.dealImageTask(imageRule, image);
    }

    private void insertTaskItemResource(TaskItem taskItem, String resourceId) throws Exception {
        TaskItemResource record = new TaskItemResource();
        BeanUtils.copyBean(record, taskItem);
        record.setResourceId(resourceId);
        record.setTaskItemId(taskItem.getId());
        record.setCreateTime(System.currentTimeMillis());
        taskItemResourceMapper.insertSelective(record);
    }

    private List<RuleTagMapping> ruleTagMappings (String tagKey) {
        RuleTagMappingExample ruleTagMappingExample = new RuleTagMappingExample();
        ruleTagMappingExample.createCriteria().andTagKeyEqualTo(tagKey);
        List<RuleTagMapping> ruleTagMappings = ruleTagMappingMapper.selectByExample(ruleTagMappingExample);
        return ruleTagMappings;
    }

    private List<RuleGroupMapping> ruleGroupMappings (String groupId) {
        RuleGroupMappingExample ruleGroupMappingExample = new RuleGroupMappingExample();
        ruleGroupMappingExample.createCriteria().andGroupIdEqualTo(groupId);
        List<RuleGroupMapping> ruleGroupMappings = ruleGroupMappingMapper.selectByExample(ruleGroupMappingExample);
        return ruleGroupMappings;
    }

}
