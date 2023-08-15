package com.hummer.cloud.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.aliyuncs.exceptions.ClientException;
import com.hummer.cloud.mapper.*;
import com.hummer.cloud.mapper.ext.ExtAccountMapper;
import com.hummer.cloud.mapper.ext.ExtCloudProjectMapper;
import com.hummer.common.core.constant.*;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.account.CloudAccountRequest;
import com.hummer.common.core.domain.request.account.CreateCloudAccountRequest;
import com.hummer.common.core.domain.request.account.UpdateCloudAccountRequest;
import com.hummer.common.core.domain.request.project.CloudGroupRequest;
import com.hummer.common.core.domain.request.rule.ScanGroupRequest;
import com.hummer.common.core.dto.*;
import com.hummer.common.core.exception.HRException;
import com.hummer.common.core.i18n.Translator;
import com.hummer.common.core.utils.*;
import com.hummer.system.api.IOperationLogService;
import com.hummer.system.api.ISystemProviderService;
import com.hummer.system.api.model.LoginUser;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.alibaba.fastjson.JSON.parseArray;
import static com.alibaba.fastjson.JSON.parseObject;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CloudProjectService {

    @Autowired
    private ExtCloudProjectMapper extCloudProjectMapper;
    @Autowired
    private CloudProjectMapper cloudProjectMapper;
    @Autowired
    private CloudProjectLogMapper cloudProjectLogMapper;
    @Autowired
    private CloudGroupMapper cloudGroupMapper;
    @Autowired
    private CloudGroupLogMapper cloudGroupLogMapper;
    @Autowired
    private CloudProcessMapper cloudProcessMapper;
    @Autowired
    private CloudProcessLogMapper cloudProcessLogMapper;
    @Autowired
    private CloudTaskMapper cloudTaskMapper;
    @Autowired
    private CloudTaskItemMapper cloudTaskItemMapper;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private RuleGroupMapper ruleGroupMapper;
    @Autowired @Lazy
    private AccountService accountService;
    @Autowired @Lazy
    private CloudTaskService cloudTaskService;
    @Autowired
    private CommonThreadPool commonThreadPool;
    @DubboReference
    private IOperationLogService operationLogService;
    @DubboReference
    private ISystemProviderService systemProviderService;

    public List<CloudProjectDTO> getCloudProjectDTOs(CloudGroupRequest cloudGroupRequest) {
        return extCloudProjectMapper.getCloudProjectDTOs(cloudGroupRequest);
    }

    public CloudProjectDTO projectById(String projectId) {
        CloudGroupRequest cloudProject = new CloudGroupRequest();
        cloudProject.setId(projectId);
        List<CloudProjectDTO> list = extCloudProjectMapper.getCloudProjectDTOs(cloudProject);
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return new CloudProjectDTO();
        }
    }

    public void deletes(List<String> ids, LoginUser loginUser) throws Exception {
        ids.forEach(id -> {
            try {
                deleteProject(id, loginUser);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    public void deleteProject(String projectId, LoginUser loginUser) {

        CloudProject cloudProject = cloudProjectMapper.selectByPrimaryKey(projectId);
        cloudProjectMapper.deleteByPrimaryKey(projectId);

        CloudProjectLogExample cloudProjectLogExample = new CloudProjectLogExample();
        cloudProjectLogExample.createCriteria().andProjectIdEqualTo(projectId);
        cloudProjectLogMapper.deleteByExample(cloudProjectLogExample);

        CloudGroupExample cloudGroupExample = new CloudGroupExample();
        cloudGroupExample.createCriteria().andProjectIdEqualTo(projectId);
        cloudGroupMapper.deleteByExample(cloudGroupExample);

        CloudGroupLogExample cloudGroupLogExample = new CloudGroupLogExample();
        cloudGroupLogExample.createCriteria().andProjectIdEqualTo(projectId);
        cloudGroupLogMapper.deleteByExample(cloudGroupLogExample);

        CloudProcessExample cloudProcessExample = new CloudProcessExample();
        cloudProcessExample.createCriteria().andProjectIdEqualTo(projectId);
        cloudProcessMapper.deleteByExample(cloudProcessExample);

        CloudProcessLogExample cloudProcessLogExample = new CloudProcessLogExample();
        cloudProcessLogExample.createCriteria().andProjectIdEqualTo(projectId);
        cloudProcessLogMapper.deleteByExample(cloudProcessLogExample);

        operationLogService.log(loginUser, projectId, cloudProject.getAccountName(), ResourceTypeConstants.CLOUD_PROJECT.name(), ResourceOperation.DELETE, "i18n_delete_cloud_project");
    }

    public List<CloudGroupDTO> getCloudGroupDTOs(CloudGroupRequest cloudGroup) {
        return extCloudProjectMapper.getCloudGroupDTOs(cloudGroup);
    }

    public CloudGroupDTO groupById(String groupId) {
        CloudGroupRequest cloudGroup = new CloudGroupRequest();
        cloudGroup.setId(groupId);
        List<CloudGroupDTO> list = extCloudProjectMapper.getCloudGroupDTOs(cloudGroup);
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return new CloudGroupDTO();
        }
    }

    public void deleteGroups(List<String> ids, LoginUser loginUser) throws Exception {
        ids.forEach(id -> {
            try {
                deleteGroup(id, loginUser);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    public void deleteGroup(String groupId, LoginUser loginUser) {

        CloudGroup cloudGroup = cloudGroupMapper.selectByPrimaryKey(groupId);
        cloudGroupMapper.deleteByPrimaryKey(groupId);

        CloudGroupLogExample cloudGroupLogExample = new CloudGroupLogExample();
        cloudGroupLogExample.createCriteria().andGroupIdEqualTo(groupId);
        cloudGroupLogMapper.deleteByExample(cloudGroupLogExample);

        operationLogService.log(loginUser, groupId, cloudGroup.getAccountName(), ResourceTypeConstants.CLOUD_GROUP.name(), ResourceOperation.DELETE, "i18n_delete_cloud_project");
    }

    public List<CloudProcessDTO> getCloudProcessDTOs(CloudProcess cloudProcess) {
        return extCloudProjectMapper.getCloudProcessDTOs(cloudProcess);
    }

    public CloudProcessDTO processById(String processId) {
        CloudProcess cloudProcess = new CloudProcess();
        cloudProcess.setId(processId);
        List<CloudProcessDTO> list = extCloudProjectMapper.getCloudProcessDTOs(cloudProcess);
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return new CloudProcessDTO();
        }
    }

    public void scan(ScanGroupRequest request, LoginUser loginUser) throws Exception {

        commonThreadPool.addTask(() -> {
            String operation = "i18n_create_cloud_project";
            String projectId = UUIDUtil.newUUID();
            try {
                AccountWithBLOBs account = accountMapper.selectByPrimaryKey(request.getAccountId());

                Integer scanId = systemProviderService.insertScanHistory(account, loginUser);
                CloudProject cloudProject = new CloudProject();
                cloudProject.setId(projectId);
                cloudProject.setAccountId(account.getId());
                cloudProject.setAccountName(account.getName());
                cloudProject.setCreator(loginUser.getUserName());
                cloudProject.setPluginId(account.getPluginId());
                cloudProject.setPluginIcon(account.getPluginIcon());
                cloudProject.setPluginName(account.getPluginName());
                cloudProject.setCreateTime(System.currentTimeMillis());
                cloudProject.setStatus(CloudTaskConstants.TASK_STATUS.APPROVED.name());

                cloudProjectMapper.insertSelective(cloudProject);

                dealProcessStep1(projectId, loginUser);

                saveCloudProjectLog(projectId, "i18n_operation_begin" + ": " + operation, StringUtils.EMPTY, true, loginUser);

                //开始执行检测...
                //计算规则组执行过程日志（所有组时间）
                long startTime = System.currentTimeMillis();
                String processScanId = UUIDUtil.newUUID();
                String operationScan = "start_scan_task";
                saveCloudProcess(processScanId, projectId, 0, 2, 10, "start_scan_task", CloudTaskConstants.TASK_STATUS.APPROVED.name(), remainingSeconds(0));
                saveCloudProcessLog(projectId, processScanId, "i18n_operation_begin" + ": " + operationScan, StringUtils.EMPTY, true, loginUser);

                for (Integer groupId : request.getGroups()) {
                    //检测任务构建...
                    //计算规则组执行过程日志（每个规则组时间）
                    RuleGroup ruleGroup = ruleGroupMapper.selectByPrimaryKey(groupId);
                    String processGroupId = UUIDUtil.newUUID();
                    String operationGroup = "create_scan_task";
                    long startTimeGroup = System.currentTimeMillis();
                    saveCloudProcess(processGroupId, projectId, 0, 2, 8, "create_scan_task", CloudTaskConstants.TASK_STATUS.APPROVED.name(), remainingSeconds(0));
                    saveCloudProcessLog(projectId, processGroupId, "i18n_operation_begin" + ": " + operationGroup + ruleGroup.getName(), StringUtils.EMPTY, true, loginUser);
                    dealGroup(projectId, ruleGroup, account, loginUser, scanId);
                    long endTimeGroup = System.currentTimeMillis();
                    long executionTimeGroup = endTimeGroup - startTimeGroup;//执行时间：毫秒
                    saveCloudProcess(processGroupId, projectId, 100, 2, 8, "create_scan_task", CloudTaskConstants.TASK_STATUS.FINISHED.name(), remainingSeconds((int)(executionTimeGroup/1000)));
                    saveCloudProcessLog(projectId, processGroupId, "i18n_operation_init" + ": " + operation + ruleGroup.getName(), StringUtils.EMPTY, true, loginUser);
                }

                //计算规则组执行过程日志（所有组结束时间）
                long endTime = System.currentTimeMillis();
                long executionTime = endTime - startTime;//执行时间：毫秒
                saveCloudProcess(processScanId, projectId, 100, 2, 10, "start_scan_task", CloudTaskConstants.TASK_STATUS.FINISHED.name(), remainingSeconds((int)(executionTime/1000)));
                saveCloudProcessLog(projectId, processScanId, "i18n_operation_end" + ": " + operationScan, StringUtils.EMPTY, true, loginUser);

                saveCloudProjectLog(projectId, "i18n_operation_end" + ": " + operation, StringUtils.EMPTY, true, loginUser);
            } catch (Exception e) {
                try {
                    saveCloudProjectLog(projectId, "i18n_operation_ex" + ": " + e.getMessage(), e.getMessage(), false, loginUser);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                LogUtil.error("{project scan}" + projectId + ", " + e.getMessage());
            }
        });

    }

    private void dealProcessStep1(String projectId, LoginUser loginUser) throws Exception {
        String operation = "init_cloud_account_info";
        String processId = UUIDUtil.newUUID();
        int step = 1, order = 1;
        try {
            //初始化云账号信息...
            saveCloudProcess(processId, projectId, 0, step, order, "init_cloud_account_info", CloudTaskConstants.TASK_STATUS.APPROVED.name(), remainingSeconds(0));
            saveCloudProcessLog(projectId, processId, "i18n_operation_begin" + ": " + operation, StringUtils.EMPTY, true, loginUser);
            Random random = new Random();
            int randomNumber = random.nextInt(4) + 1; // 生成1-4之间的随机整数
            saveCloudProcess(processId, projectId, 100, step, order, "init_cloud_account_info", CloudTaskConstants.TASK_STATUS.FINISHED.name(), remainingSeconds(randomNumber));
            saveCloudProcessLog(projectId, processId, "i18n_operation_init" + ": " + operation, StringUtils.EMPTY, true, loginUser);
            order++;

            //初始化区域信息...
            processId = UUIDUtil.newUUID();
            operation = "init_cloud_region_info";
            saveCloudProcess(processId, projectId, 0, step, order, "init_cloud_region_info", CloudTaskConstants.TASK_STATUS.APPROVED.name(), remainingSeconds(0));
            saveCloudProcessLog(projectId, processId, "i18n_operation_begin" + ": " + operation, StringUtils.EMPTY, true, loginUser);
            randomNumber = random.nextInt(4) + 1; // 生成1-4之间的随机整数
            saveCloudProcess(processId, projectId, 100, step, order, "init_cloud_region_info", CloudTaskConstants.TASK_STATUS.FINISHED.name(), remainingSeconds(randomNumber));
            saveCloudProcessLog(projectId, processId, "i18n_operation_init" + ": " + operation, StringUtils.EMPTY, true, loginUser);
            order++;

            //初始化规则组信息...
            processId = UUIDUtil.newUUID();
            operation = "init_cloud_group_info";
            saveCloudProcess(processId, projectId, 0, step, order, "init_cloud_group_info", CloudTaskConstants.TASK_STATUS.APPROVED.name(), remainingSeconds(0));
            saveCloudProcessLog(projectId, processId, "i18n_operation_begin" + ": " + operation, StringUtils.EMPTY, true, loginUser);
            randomNumber = random.nextInt(4) + 1; // 生成1-4之间的随机整数
            saveCloudProcess(processId, projectId, 100, step, order, "init_cloud_group_info", CloudTaskConstants.TASK_STATUS.FINISHED.name(), remainingSeconds(randomNumber));
            saveCloudProcessLog(projectId, processId, "i18n_operation_init" + ": " + operation, StringUtils.EMPTY, true, loginUser);
            order++;

            //初始化规则信息...
            processId = UUIDUtil.newUUID();
            operation = "init_cloud_rule_info";
            saveCloudProcess(processId, projectId, 0, step, order, "init_cloud_rule_info", CloudTaskConstants.TASK_STATUS.APPROVED.name(), remainingSeconds(0));
            saveCloudProcessLog(projectId, processId, "i18n_operation_begin" + ": " + operation, StringUtils.EMPTY, true, loginUser);
            randomNumber = random.nextInt(4) + 1; // 生成1-4之间的随机整数
            saveCloudProcess(processId, projectId, 100, step, order, "init_cloud_rule_info", CloudTaskConstants.TASK_STATUS.FINISHED.name(), remainingSeconds(randomNumber));
            saveCloudProcessLog(projectId, processId, "i18n_operation_init" + ": " + operation, StringUtils.EMPTY, true, loginUser);
            order++;step++;

            //初始化检测环境...
            processId = UUIDUtil.newUUID();
            operation = "init_env_info";
            saveCloudProcess(processId, projectId, 0, step, order, "init_env_info", CloudTaskConstants.TASK_STATUS.APPROVED.name(), remainingSeconds(0));
            saveCloudProcessLog(projectId, processId, "i18n_operation_begin" + ": " + operation, StringUtils.EMPTY, true, loginUser);
            randomNumber = random.nextInt(10) + 1; // 生成1-10之间的随机整数
            saveCloudProcess(processId, projectId, 100, step, order, "init_env_info", CloudTaskConstants.TASK_STATUS.FINISHED.name(), remainingSeconds(randomNumber));
            saveCloudProcessLog(projectId, processId, "i18n_operation_init" + ": " + operation, StringUtils.EMPTY, true, loginUser);
            order++;

            //创建检测任务...
            processId = UUIDUtil.newUUID();
            operation = "create_scan_info";
            saveCloudProcess(processId, projectId, 0, step, order, "create_scan_info", CloudTaskConstants.TASK_STATUS.APPROVED.name(), remainingSeconds(0));
            saveCloudProcessLog(projectId, processId, "i18n_operation_begin" + ": " + operation, StringUtils.EMPTY, true, loginUser);
            randomNumber = random.nextInt(10) + 1; // 生成1-10之间的随机整数
            saveCloudProcess(processId, projectId, 100, step, order, "create_scan_info", CloudTaskConstants.TASK_STATUS.FINISHED.name(), remainingSeconds(randomNumber));
            saveCloudProcessLog(projectId, processId, "i18n_operation_init" + ": " + operation, StringUtils.EMPTY, true, loginUser);
            order++;

            //创建检测规则组...
            processId = UUIDUtil.newUUID();
            operation = "create_scan_group";
            saveCloudProcess(processId, projectId, 0, step, order, "create_scan_group", CloudTaskConstants.TASK_STATUS.APPROVED.name(), remainingSeconds(0));
            saveCloudProcessLog(projectId, processId, "i18n_operation_begin" + ": " + operation, StringUtils.EMPTY, true, loginUser);
            randomNumber = random.nextInt(20) + 1; // 生成1-20之间的随机整数
            saveCloudProcess(processId, projectId, 100, step, order, "create_scan_group", CloudTaskConstants.TASK_STATUS.FINISHED.name(), remainingSeconds(randomNumber));
            saveCloudProcessLog(projectId, processId, "i18n_operation_init" + ": " + operation, StringUtils.EMPTY, true, loginUser);
            order++;

        } catch (Exception e) {
            saveCloudProcessLog(projectId, processId, "i18n_operation_ex" + ": " + e.getMessage(), e.getMessage(), false, loginUser);
            LogUtil.error("[process scan]" + "{step: " + step + ", order: " + order + "}, " + e.getMessage());
        }
    }

    private void dealGroup(String projectId, RuleGroup ruleGroup, AccountWithBLOBs account, LoginUser loginUser, Integer scanId) throws Exception {
        String operationGroup = "i18n_create_cloud_group";
        String cloudGroupId = UUIDUtil.newUUID();
        try {
            CloudGroup cloudGroup = new CloudGroup();
            cloudGroup.setId(cloudGroupId);
            cloudGroup.setProjectId(projectId);
            cloudGroup.setAccountId(account.getId());
            cloudGroup.setAccountName(account.getName());
            cloudGroup.setPluginIcon(account.getPluginIcon());
            cloudGroup.setPluginName(account.getPluginName());
            cloudGroup.setCreateTime(System.currentTimeMillis());
            cloudGroup.setCreator(loginUser.getUserName());
            cloudGroup.setStatus(CloudTaskConstants.TASK_STATUS.APPROVED.name());
            cloudGroup.setGroupId(ruleGroup.getId());
            cloudGroup.setGroupDesc(ruleGroup.getDescription());
            cloudGroup.setGroupName(ruleGroup.getName());
            cloudGroup.setGroupFlag(ruleGroup.getFlag());
            cloudGroup.setGroupLevel(cloudGroup.getGroupLevel());

            cloudGroupMapper.insertSelective(cloudGroup);

            saveCloudGroupLog(projectId, cloudGroupId, "i18n_operation_begin" + ": " + operationGroup + "[" + ruleGroup.getName() + "]", StringUtils.EMPTY, true, loginUser);
            saveCloudProjectLog(projectId, "i18n_operation_begin" + ": " + operationGroup + "[" + ruleGroup.getName() + "]", StringUtils.EMPTY, true, loginUser);

            QuartzTaskDTO dto = new QuartzTaskDTO();
            dto.setAccountId(account.getId());
            dto.setPluginId(account.getPluginId());
            dto.setStatus(true);
            List<RuleDTO> ruleDTOS = accountService.getRules(dto);
            for (RuleDTO rule : ruleDTOS) {
                this.dealTask(rule, scanId, account, loginUser);
            }

            saveCloudGroupLog(projectId, cloudGroupId, "i18n_operation_end" + ": " + operationGroup + "[" + ruleGroup.getName() + "]", StringUtils.EMPTY, true, loginUser);
            saveCloudProjectLog(projectId, "i18n_operation_end" + ": " + operationGroup + "[" + ruleGroup.getName() + "]", StringUtils.EMPTY, true, loginUser);
        } catch (Exception e) {
            saveCloudGroupLog(projectId, cloudGroupId, "i18n_operation_ex" + ": " + e.getMessage(), e.getMessage(), false, loginUser);
            LogUtil.error("{group scan}" + cloudGroupId + ", " + e.getMessage());
        }

    }

    private String dealTask(RuleDTO rule, Integer scanId, AccountWithBLOBs account, LoginUser loginUser) {
        try {
            if (rule.getStatus() && !cloudTaskService.checkRuleTaskStatus(account.getId(), rule.getId(),
                    new String[]{CloudTaskConstants.TASK_STATUS.APPROVED.name(), CloudTaskConstants.TASK_STATUS.PROCESSING.name()})) {
                QuartzTaskDTO quartzTaskDTO = new QuartzTaskDTO();
                BeanUtils.copyBean(quartzTaskDTO, rule);
                List<SelectTag> selectTags = new LinkedList<>();
                SelectTag s = new SelectTag();
                s.setAccountId(account.getId());
                JSONArray array = parseArray(rule.getRegions() != null ? rule.getRegions() : account.getCheckRegions());
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
                CloudTask cloudTask = cloudTaskService.saveManualTask(quartzTaskDTO, loginUser);
                if (PlatformUtils.isSupportCloudAccount(cloudTask.getPluginId())) {
                    systemProviderService.insertScanTaskHistory(cloudTask, scanId, cloudTask.getAccountId(), TaskEnum.cloudAccount.getType());
                }
                return cloudTask.getId();
            }
        } catch (Exception e) {
            HRException.throwException(e.getMessage());
        }
        return "";
    }

    public void saveCloudProjectLog(String projectId, String operation, String output, boolean result, LoginUser loginUser) throws Exception {
        CloudProjectLogWithBLOBs cloudProjectLogWithBLOBs = new CloudProjectLogWithBLOBs();
        String operator = "system";
        try {
            if (loginUser != null) {
                operator = loginUser.getUserId();
            }
        } catch (Exception e) {
            //防止单元测试无session
        }
        cloudProjectLogWithBLOBs.setOperator(operator);
        cloudProjectLogWithBLOBs.setProjectId(projectId);
        cloudProjectLogWithBLOBs.setCreateTime(System.currentTimeMillis());
        cloudProjectLogWithBLOBs.setOperation(operation);
        cloudProjectLogWithBLOBs.setOutput(output);
        cloudProjectLogWithBLOBs.setResult(result);
        cloudProjectLogMapper.insertSelective(cloudProjectLogWithBLOBs);
    }

    public void saveCloudGroupLog(String projectId, String groupId, String operation, String output, boolean result, LoginUser loginUser) throws Exception {
        CloudGroupLogWithBLOBs cloudGroupLogWithBLOBs = new CloudGroupLogWithBLOBs();
        String operator = "system";
        try {
            if (loginUser != null) {
                operator = loginUser.getUserId();
            }
        } catch (Exception e) {
            //防止单元测试无session
        }
        cloudGroupLogWithBLOBs.setOperator(operator);
        cloudGroupLogWithBLOBs.setProjectId(projectId);
        cloudGroupLogWithBLOBs.setGroupId(groupId);
        cloudGroupLogWithBLOBs.setCreateTime(System.currentTimeMillis());
        cloudGroupLogWithBLOBs.setOperation(operation);
        cloudGroupLogWithBLOBs.setOutput(output);
        cloudGroupLogWithBLOBs.setResult(result);
        cloudGroupLogMapper.insertSelective(cloudGroupLogWithBLOBs);
    }

    public void saveCloudProcess(String id, String projectId, int processRate, int processStep, int processOrder, String processName, String status, String execTime) throws Exception {

        CloudProcess cloudProcess = cloudProcessMapper.selectByPrimaryKey(id);
        if (cloudProcess != null) {
            cloudProcess.setId(id);
            cloudProcess.setProjectId(projectId);
            cloudProcess.setProcessRate(processRate);
            cloudProcess.setCreateTime(System.currentTimeMillis());
            cloudProcess.setProcessStep(processStep);
            cloudProcess.setStatus(status);
            cloudProcess.setProcessOrder(processOrder);
            cloudProcess.setProcessName(processName);
            cloudProcess.setExecTime(execTime);
            cloudProcessMapper.insertSelective(cloudProcess);
        } else {
            cloudProcess.setId(id);
            cloudProcess.setProjectId(projectId);
            cloudProcess.setProcessRate(processRate);
            cloudProcess.setCreateTime(System.currentTimeMillis());
            cloudProcess.setProcessStep(processStep);
            cloudProcess.setStatus(status);
            cloudProcess.setExecTime(execTime);
            cloudProcessMapper.updateByPrimaryKey(cloudProcess);
        }
    }

    public void saveCloudProcessLog(String projectId, String processId, String operation, String output, boolean result, LoginUser loginUser) throws Exception {
        CloudProcessLogWithBLOBs cloudProcessLogWithBLOBs = new CloudProcessLogWithBLOBs();
        String operator = "system";
        try {
            if (loginUser != null) {
                operator = loginUser.getUserId();
            }
        } catch (Exception e) {
            //防止单元测试无session
        }
        cloudProcessLogWithBLOBs.setOperator(operator);
        cloudProcessLogWithBLOBs.setProjectId(projectId);
        cloudProcessLogWithBLOBs.setProcessId(processId);
        cloudProcessLogWithBLOBs.setCreateTime(System.currentTimeMillis());
        cloudProcessLogWithBLOBs.setOperation(operation);
        cloudProcessLogWithBLOBs.setOutput(output);
        cloudProcessLogWithBLOBs.setResult(result);
        cloudProcessLogMapper.insertSelective(cloudProcessLogWithBLOBs);
    }

    private String remainingSeconds(int seconds) {
        if (seconds > 60) {
            int minutes = seconds / 60; // 计算分钟数
            int remainingSeconds = seconds % 60; // 计算剩余的秒数
            return minutes + "minute_title" + remainingSeconds + "second_title";
        } else {
            return seconds + "second_title";
        }
    }

}
