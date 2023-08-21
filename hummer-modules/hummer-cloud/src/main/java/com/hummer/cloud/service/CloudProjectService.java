package com.hummer.cloud.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hummer.cloud.mapper.*;
import com.hummer.cloud.mapper.ext.ExtCloudProjectMapper;
import com.hummer.common.core.constant.CloudTaskConstants;
import com.hummer.common.core.constant.ResourceOperation;
import com.hummer.common.core.constant.ResourceTypeConstants;
import com.hummer.common.core.constant.TaskEnum;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.project.CloudGroupRequest;
import com.hummer.common.core.domain.request.rule.ScanGroupRequest;
import com.hummer.common.core.dto.*;
import com.hummer.common.core.exception.HRException;
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.alibaba.fastjson.JSON.parseArray;

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
    @Autowired
    @Lazy
    private AccountService accountService;
    @Autowired
    @Lazy
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

    public void deleteGroups(List<String> ids, LoginUser loginUser) {
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

    public CloudProcessDTO getCloudProcessDTO(CloudProcess cloudProcess) {
        return extCloudProjectMapper.getCloudProcessDTO(cloudProcess);
    }

    public List<CloudProcessLogWithBLOBs> getCloudProcessLogs(CloudProcess cloudProcess) {
        CloudProcessLogExample example = new CloudProcessLogExample();
        example.createCriteria().andProjectIdEqualTo(cloudProcess.getProjectId());
        return cloudProcessLogMapper.selectByExampleWithBLOBs(example);
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED, rollbackFor = {RuntimeException.class, Exception.class})
    public String scan(ScanGroupRequest request, LoginUser loginUser) throws Exception {
        String projectId = UUIDUtil.newUUID();

        createProcess(projectId);
        saveCloudProcessLog(projectId, "", "i18n_operation_begin" + " : " + "start_init", StringUtils.EMPTY, true, loginUser);

        String operation = "i18n_create_cloud_project";

        commonThreadPool.addTask(() -> {
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

                saveCloudProjectLog(projectId, "i18n_operation_begin" + " : " + operation, StringUtils.EMPTY, true, loginUser);

                saveCloudProcessLog(projectId, "", "i18n_operation_init" + " : " + "i18n_init_task", StringUtils.EMPTY, true, loginUser);

                for (Integer groupId : request.getGroups()) {
                    //计算规则组执行初始化日志（每个规则组时间）
                    RuleGroup ruleGroup = ruleGroupMapper.selectByPrimaryKey(groupId);
                    dealGroup(projectId, ruleGroup, account, loginUser, scanId);
                }

                saveCloudProcessLog(projectId, "", "i18n_operation_end" + " : " + "start_init_finish", StringUtils.EMPTY, true, loginUser);

                saveCloudProjectLog(projectId, "i18n_operation_end" + " : " + operation, StringUtils.EMPTY, true, loginUser);
            } catch (Exception e) {
                try {
                    saveCloudProjectLog(projectId, "i18n_operation_ex" + " : " + e.getMessage(), e.getMessage(), false, loginUser);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                LogUtil.error("{project scan}" + projectId + ", " + e.getMessage());
            }
        });

        return projectId;
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

            saveCloudProcessLog(projectId, "", "i18n_operation_init" + " : " + operationGroup + "[" + ruleGroup.getName() + "]", StringUtils.EMPTY, true, loginUser);
            saveCloudGroupLog(projectId, cloudGroupId, "i18n_operation_begin" + " : " + operationGroup + "[" + ruleGroup.getName() + "]", StringUtils.EMPTY, true, loginUser);
            saveCloudProjectLog(projectId, "i18n_operation_begin" + " : " + operationGroup + "[" + ruleGroup.getName() + "]", StringUtils.EMPTY, true, loginUser);

            QuartzTaskDTO dto = new QuartzTaskDTO();
            dto.setAccountId(account.getId());
            dto.setPluginId(account.getPluginId());
            dto.setStatus(true);
            List<RuleDTO> ruleDTOS = accountService.getRules(dto);
            for (RuleDTO rule : ruleDTOS) {
                this.dealTask(rule, scanId, projectId, cloudGroupId, account, loginUser);
            }

            saveCloudGroupLog(projectId, cloudGroupId, "i18n_operation_end" + " : " + operationGroup + "[" + ruleGroup.getName() + "]", StringUtils.EMPTY, true, loginUser);
            saveCloudProjectLog(projectId, "i18n_operation_end" + " : " + operationGroup + "[" + ruleGroup.getName() + "]", StringUtils.EMPTY, true, loginUser);
        } catch (Exception e) {
            saveCloudGroupLog(projectId, cloudGroupId, "i18n_operation_ex" + " : " + e.getMessage(), e.getMessage(), false, loginUser);
            LogUtil.error("{group scan}" + cloudGroupId + ", " + e.getMessage());
        }

    }

    private void dealTask(RuleDTO rule, Integer scanId, String projectId, String cloudGroupId, AccountWithBLOBs account, LoginUser loginUser) {
        try {
            if (rule.getStatus() && !cloudTaskService.checkRuleTaskStatus(account.getId(), rule.getId(),
                    new String[]{CloudTaskConstants.TASK_STATUS.APPROVED.name(), CloudTaskConstants.TASK_STATUS.PROCESSING.name()})) {
                QuartzTaskDTO quartzTaskDTO = new QuartzTaskDTO();
                BeanUtils.copyBean(quartzTaskDTO, rule);
                quartzTaskDTO.setProjectId(projectId);
                quartzTaskDTO.setGroupId(cloudGroupId);
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
                saveCloudProcessLog(projectId, "", "i18n_operation_process" + " : " + "i18n_init_task" + "[" + rule.getName() + "]", StringUtils.EMPTY, true, loginUser);

                CloudTask cloudTask = cloudTaskService.saveManualTask(quartzTaskDTO, loginUser);
                if (PlatformUtils.isSupportCloudAccount(cloudTask.getPluginId())) {
                    try {
                        systemProviderService.insertScanTaskHistory(cloudTask, scanId, cloudTask.getAccountId(), TaskEnum.cloudAccount.getType());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (Exception e) {
            HRException.throwException(e.getMessage());
        }
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

    public void createProcess(String projectId) throws Exception {
        //初始化云账号信息...
        saveCloudProcess(UUIDUtil.newUUID(), projectId, 0, 1, 1, "init_cloud_account_info", CloudTaskConstants.TASK_STATUS.APPROVED.name(), 0);
        //初始化区域信息...
        saveCloudProcess(UUIDUtil.newUUID(), projectId, 0, 1, 2, "init_cloud_region_info", CloudTaskConstants.TASK_STATUS.APPROVED.name(), 0);
        //初始化规则组信息...
        saveCloudProcess(UUIDUtil.newUUID(), projectId, 0, 1, 3, "init_cloud_group_info", CloudTaskConstants.TASK_STATUS.APPROVED.name(), 0);
        //初始化规则信息...
        saveCloudProcess(UUIDUtil.newUUID(), projectId, 0, 1, 4, "init_cloud_rule_info", CloudTaskConstants.TASK_STATUS.APPROVED.name(), 0);
        //初始化检测环境...
        saveCloudProcess(UUIDUtil.newUUID(), projectId, 0, 2, 5, "init_env_info", CloudTaskConstants.TASK_STATUS.APPROVED.name(), 0);
        //创建检测任务...
        saveCloudProcess(UUIDUtil.newUUID(), projectId, 0, 2, 6, "create_scan_info", CloudTaskConstants.TASK_STATUS.APPROVED.name(), 0);
        //创建检测规则组...
        saveCloudProcess(UUIDUtil.newUUID(), projectId, 0, 2, 7, "create_scan_group", CloudTaskConstants.TASK_STATUS.APPROVED.name(), 0);
        //检测任务构建...
        saveCloudProcess(UUIDUtil.newUUID(), projectId, 0, 2, 8, "create_scan_task", CloudTaskConstants.TASK_STATUS.APPROVED.name(), 0);
        //开始执行检测...
        saveCloudProcess(UUIDUtil.newUUID(), projectId, 0, 3, 9, "start_scan_task", CloudTaskConstants.TASK_STATUS.APPROVED.name(), 0);
    }

    public CloudProcess updateProcess(CloudProcess cloudProcess) throws Exception {

        switch (cloudProcess.getProcessOrder()) {
            case 1 ->
                //初始化云账号信息...
                    cloudProcess = updateCloudProcess(cloudProcess.getId(), cloudProcess.getProjectId(), 100, 1, 1, "init_cloud_account_info", CloudTaskConstants.TASK_STATUS.FINISHED.name(), cloudProcess.getExecTime());
            case 2 ->
                //初始化区域信息...
                    cloudProcess = updateCloudProcess(cloudProcess.getId(), cloudProcess.getProjectId(), 100, 1, 2, "init_cloud_region_info", CloudTaskConstants.TASK_STATUS.FINISHED.name(), cloudProcess.getExecTime());
            case 3 ->
                //初始化规则组信息...
                    cloudProcess= updateCloudProcess(cloudProcess.getId(), cloudProcess.getProjectId(), 100, 1, 3, "init_cloud_group_info", CloudTaskConstants.TASK_STATUS.FINISHED.name(), cloudProcess.getExecTime());
            case 4 ->
                //初始化规则信息...
                    cloudProcess = updateCloudProcess(cloudProcess.getId(), cloudProcess.getProjectId(), 100, 1, 4, "init_cloud_rule_info", CloudTaskConstants.TASK_STATUS.FINISHED.name(), cloudProcess.getExecTime());
            case 5 ->
                //初始化检测环境...
                    cloudProcess = updateCloudProcess(cloudProcess.getId(), cloudProcess.getProjectId(), 100, 2, 5, "init_env_info", CloudTaskConstants.TASK_STATUS.FINISHED.name(), cloudProcess.getExecTime());
            case 6 ->
                //创建检测任务...
                    cloudProcess = updateCloudProcess(cloudProcess.getId(), cloudProcess.getProjectId(), 100, 2, 6, "create_scan_info", CloudTaskConstants.TASK_STATUS.FINISHED.name(), cloudProcess.getExecTime());
            case 7 ->
                //创建检测规则组...
                    cloudProcess = updateCloudProcess(cloudProcess.getId(), cloudProcess.getProjectId(), 100, 2, 7, "create_scan_group", CloudTaskConstants.TASK_STATUS.FINISHED.name(), cloudProcess.getExecTime());
            case 8 ->
                //检测任务构建...
                    cloudProcess = updateCloudProcess(cloudProcess.getId(), cloudProcess.getProjectId(), 100, 2, 8, "create_scan_task", CloudTaskConstants.TASK_STATUS.FINISHED.name(), cloudProcess.getExecTime());
            case 9 ->
                //开始执行检测...
                    cloudProcess = updateCloudProcess(cloudProcess.getId(), cloudProcess.getProjectId(), 100, 3, 9, "start_scan_task", CloudTaskConstants.TASK_STATUS.FINISHED.name(), cloudProcess.getExecTime());
        }
        return cloudProcess;
    }

    public void saveCloudProcess(String id, String projectId, int processRate, int processStep, int processOrder, String processName, String status, Integer execTime) throws Exception {

        CloudProcess cloudProcess = cloudProcessMapper.selectByPrimaryKey(id);
        if (cloudProcess == null) {
            cloudProcess = new CloudProcess();
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
            cloudProcess.setProcessStep(processStep);
            cloudProcess.setStatus(CloudTaskConstants.TASK_STATUS.FINISHED.name());
            cloudProcess.setExecTime(execTime);
            cloudProcessMapper.updateByPrimaryKey(cloudProcess);
        }
    }

    public CloudProcess updateCloudProcess(String id, String projectId, int processRate, int processStep, int processOrder, String processName, String status, Integer execTime) throws Exception {

        CloudProcess cloudProcess = cloudProcessMapper.selectByPrimaryKey(id);
        if (cloudProcess == null) {
            CloudProcessExample example = new CloudProcessExample();
            example.createCriteria().andProcessStepEqualTo(processStep).andProcessOrderEqualTo(processOrder).andProjectIdEqualTo(projectId);
            List<CloudProcess> cloudProcessList = cloudProcessMapper.selectByExample(example);
            if(!cloudProcessList.isEmpty()) {
                CloudProcess cloudProcess2 = BeanUtils.copyBean(new CloudProcess(), cloudProcessList.get(0));
                cloudProcess2.setProcessRate(processRate);
                cloudProcess2.setStatus(status);
                cloudProcess2.setExecTime(execTime);
                cloudProcessMapper.updateByPrimaryKeySelective(cloudProcess2);
                return cloudProcess2;
            }
        } else {
            cloudProcess.setId(id);
            cloudProcess.setProjectId(projectId);
            cloudProcess.setProcessRate(processRate);
            cloudProcess.setProcessStep(processStep);
            cloudProcess.setStatus(CloudTaskConstants.TASK_STATUS.FINISHED.name());
            cloudProcess.setExecTime(execTime);
            cloudProcessMapper.updateByPrimaryKeySelective(cloudProcess);
        }
        return cloudProcess;
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

}
