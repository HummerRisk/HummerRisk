package com.hummer.system.service;

import com.alibaba.fastjson.JSONArray;
import com.hummer.cloud.api.ICloudProviderService;
import com.hummer.common.core.constant.TaskConstants;
import com.hummer.common.core.constant.TaskEnum;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.utils.BeanUtils;
import com.hummer.common.core.utils.CommonThreadPool;
import com.hummer.k8s.api.IK8sProviderService;
import com.hummer.system.mapper.*;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author harris
 */
@Service
public class ResourceCreateService {
    // 只有一个任务在处理，防止超配
    private static ConcurrentHashMap<String, String> processingGroupIdMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, Integer> historyIdMap = new ConcurrentHashMap<>();
    @Resource
    private CommonThreadPool commonThreadPool;
    @Resource
    private TaskMapper taskMapper;
    @Resource
    private TaskItemMapper taskItemMapper;
    @Resource
    private TaskItemResourceMapper taskItemResourceMapper;
    @Resource
    private TaskService taskService;
    @Resource
    private HistoryService historyService;
    @Resource
    private HistoryScanMapper historyScanMapper;
    @Resource
    private HistoryScanTaskMapper historyScanTaskMapper;
    @Resource
    private HistoryCloudTaskMapper historyCloudTaskMapper;
    @Resource
    private HistoryVulnTaskMapper historyVulnTaskMapper;
    @Resource
    private HistoryServerResultMapper historyServerResultMapper;
    @Resource
    private HistoryImageResultMapper historyImageResultMapper;
    @Resource
    private TaskItemResourceLogMapper taskItemResourceLogMapper;
    @Resource
    private HistoryCodeResultMapper historyCodeResultMapper;
    @Resource
    private HistoryFileSystemResultMapper historyFileSystemResultMapper;
    @DubboReference
    private ICloudProviderService cloudProviderService;
    @DubboReference
    private IK8sProviderService k8sProviderService;

    //历史数据统计
    @XxlJob("historyTasksJobHandler")
    public void historyTasksJobHandler() throws Exception {
        //历史数据统计
        final HistoryScanExample historyScanExample = new HistoryScanExample();
        HistoryScanExample.Criteria historyScanCriteria = historyScanExample.createCriteria();
        historyScanCriteria.andStatusEqualTo(TaskConstants.TASK_STATUS.APPROVED.toString());
        if (CollectionUtils.isNotEmpty(historyIdMap.keySet())) {
            historyScanCriteria.andIdNotIn(new ArrayList<>(historyIdMap.keySet()));
        }
        historyScanExample.setOrderByClause("create_time limit 10");
        List<HistoryScan> historyScans = historyScanMapper.selectByExample(historyScanExample);
        List<String> historyScanStatus = Arrays.asList(TaskConstants.TASK_STATUS.ERROR.name(), TaskConstants.TASK_STATUS.FINISHED.name(), TaskConstants.TASK_STATUS.WARNING.name());
        for (HistoryScan historyScan : historyScans) {
            final HistoryScan historyScanToBeProceed;
            try {
                historyScanToBeProceed = BeanUtils.copyBean(new HistoryScan(), historyScan);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            if (historyIdMap.get(historyScanToBeProceed.getId()) != null) {
                return;
            }
            historyIdMap.put(historyScanToBeProceed.getId(), historyScanToBeProceed.getId());
            HistoryScanTaskExample historyScanTaskExample = new HistoryScanTaskExample();
            HistoryScanTaskExample.Criteria historyScanTaskCriteria = historyScanTaskExample.createCriteria();
            historyScanTaskCriteria.andScanIdEqualTo(historyScan.getId()).andStatusNotIn(historyScanStatus);
            List<HistoryScanTask> historyScanTasks = historyScanTaskMapper.selectByExample(historyScanTaskExample);
            if(historyScanTasks.size() == 0) {
                historyScan.setStatus(TaskConstants.TASK_STATUS.ERROR.name());
                historyScanMapper.updateByPrimaryKey(historyScan);
            }
            JSONArray jsonArray = new JSONArray();
            historyScanTaskCriteria.andStatusIn(historyScanStatus);
            for (HistoryScanTask historyScanTask : historyScanTasks) {
                if (StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.cloudAccount.getType())) {
                    CloudTask cloudTask = cloudProviderService.selectCloudTask(historyScanTask.getTaskId());
                    if (cloudTask != null && historyScanStatus.contains(cloudTask.getStatus())) {
                        historyScanTask.setStatus(cloudTask.getStatus());
                        historyScanTask.setResourcesSum(cloudTask.getResourcesSum()!=null? cloudTask.getResourcesSum():0);
                        historyScanTask.setReturnSum(cloudTask.getReturnSum()!=null? cloudTask.getReturnSum():0);
                        historyScanTask.setScanScore(historyService.calculateScore(cloudTask.getAccountId(), cloudTask, TaskEnum.cloudAccount.getType()));
                    } else {
                        historyScanTask.setStatus(TaskConstants.TASK_STATUS.ERROR.name());
                        historyScanTask.setResourcesSum(0L);
                        historyScanTask.setReturnSum(0L);
                        historyScanTask.setScanScore(100);
                    }
                    historyScanTask.setOutput(jsonArray.toJSONString());
                    historyService.updateScanTaskHistory(historyScanTask);
                } else if(StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.vulnAccount.getType())) {
                    CloudTask cloudTask = cloudProviderService.selectCloudTask(historyScanTask.getTaskId());
                    if (cloudTask != null && historyScanStatus.contains(cloudTask.getStatus())) {
                        historyScanTask.setStatus(cloudTask.getStatus());
                        historyScanTask.setResourcesSum(cloudTask.getResourcesSum()!=null? cloudTask.getResourcesSum():0);
                        historyScanTask.setReturnSum(cloudTask.getReturnSum()!=null? cloudTask.getReturnSum():0);
                        historyScanTask.setScanScore(historyService.calculateScore(cloudTask.getAccountId(), cloudTask, TaskEnum.vulnAccount.getType()));
                    } else {
                        historyScanTask.setStatus(TaskConstants.TASK_STATUS.ERROR.name());
                        historyScanTask.setResourcesSum(0L);
                        historyScanTask.setReturnSum(0L);
                        historyScanTask.setScanScore(100);
                    }
                    historyScanTask.setOutput(jsonArray.toJSONString());
                    historyService.updateScanTaskHistory(historyScanTask);
                } else if(StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.serverAccount.getType())) {
                    ServerResult serverResult = k8sProviderService.serverResult(historyScanTask.getTaskId());
                    if (serverResult != null && historyScanStatus.contains(serverResult.getResultStatus())) {
                        historyScanTask.setStatus(serverResult.getResultStatus());
                        historyScanTask.setResourcesSum(1L);
                        historyScanTask.setReturnSum(1L);
                        historyScanTask.setScanScore(historyService.calculateScore(historyScanTask.getAccountId(), serverResult, TaskEnum.serverAccount.getType()));
                    } else {
                        historyScanTask.setStatus(TaskConstants.TASK_STATUS.ERROR.name());
                        historyScanTask.setResourcesSum(0L);
                        historyScanTask.setReturnSum(0L);
                        historyScanTask.setScanScore(100);
                    }
                    historyScanTask.setOutput(jsonArray.toJSONString());
                    historyService.updateScanTaskHistory(historyScanTask);
                } else if(StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.imageAccount.getType())) {
                    ImageResult imageResult = k8sProviderService.imageResult(historyScanTask.getTaskId());
                    if (imageResult != null && historyScanStatus.contains(imageResult.getResultStatus())) {
                        historyScanTask.setStatus(imageResult.getResultStatus());
                        historyScanTask.setResourcesSum(imageResult.getReturnSum()!=null? imageResult.getReturnSum():0);
                        historyScanTask.setReturnSum(imageResult.getReturnSum()!=null? imageResult.getReturnSum():0);
                        historyScanTask.setScanScore(historyService.calculateScore(historyScanTask.getAccountId(), imageResult, TaskEnum.imageAccount.getType()));
                    } else {
                        historyScanTask.setStatus(TaskConstants.TASK_STATUS.ERROR.name());
                        historyScanTask.setResourcesSum(0L);
                        historyScanTask.setReturnSum(0L);
                        historyScanTask.setScanScore(100);
                    }
                    historyScanTask.setOutput(jsonArray.toJSONString());
                    historyService.updateScanTaskHistory(historyScanTask);
                } else if(StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.codeAccount.getType())) {
                    CodeResult codeResult = k8sProviderService.codeResult(historyScanTask.getTaskId());
                    if (codeResult != null && historyScanStatus.contains(codeResult.getResultStatus())) {
                        historyScanTask.setStatus(codeResult.getResultStatus());
                        historyScanTask.setResourcesSum(codeResult.getReturnSum()!=null? codeResult.getReturnSum():0);
                        historyScanTask.setReturnSum(codeResult.getReturnSum()!=null? codeResult.getReturnSum():0);
                        historyScanTask.setScanScore(historyService.calculateScore(historyScanTask.getAccountId(), codeResult, TaskEnum.codeAccount.getType()));
                    } else {
                        historyScanTask.setStatus(TaskConstants.TASK_STATUS.ERROR.name());
                        historyScanTask.setResourcesSum(0L);
                        historyScanTask.setReturnSum(0L);
                        historyScanTask.setScanScore(100);
                    }
                    historyScanTask.setOutput(jsonArray.toJSONString());
                    historyService.updateScanTaskHistory(historyScanTask);
                }  else if(StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.fsAccount.getType())) {
                    FileSystemResult fileSystemResult = k8sProviderService.fileSystemResult(historyScanTask.getTaskId());
                    if (fileSystemResult != null && historyScanStatus.contains(fileSystemResult.getResultStatus())) {
                        historyScanTask.setStatus(fileSystemResult.getResultStatus());
                        historyScanTask.setResourcesSum(fileSystemResult.getReturnSum()!=null? fileSystemResult.getReturnSum():0);
                        historyScanTask.setReturnSum(fileSystemResult.getReturnSum()!=null? fileSystemResult.getReturnSum():0);
                        historyScanTask.setScanScore(historyService.calculateScore(historyScanTask.getAccountId(), fileSystemResult, TaskEnum.fsAccount.getType()));
                    } else {
                        historyScanTask.setStatus(TaskConstants.TASK_STATUS.ERROR.name());
                        historyScanTask.setResourcesSum(0L);
                        historyScanTask.setReturnSum(0L);
                        historyScanTask.setScanScore(100);
                    }
                    historyScanTask.setOutput(jsonArray.toJSONString());
                    historyService.updateScanTaskHistory(historyScanTask);
                } else if(StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.k8sAccount.getType())) {
                    CloudNativeResult cloudNativeResult = k8sProviderService.cloudNativeResult(historyScanTask.getTaskId());
                    if (cloudNativeResult != null && historyScanStatus.contains(cloudNativeResult.getResultStatus())) {
                        historyScanTask.setStatus(cloudNativeResult.getResultStatus());
                        historyScanTask.setResourcesSum(cloudNativeResult.getReturnSum()!=null? cloudNativeResult.getReturnSum():0);
                        historyScanTask.setReturnSum(cloudNativeResult.getReturnSum()!=null? cloudNativeResult.getReturnSum():0);
                        historyScanTask.setScanScore(historyService.calculateScore(historyScanTask.getAccountId(), cloudNativeResult, TaskEnum.k8sAccount.getType()));
                    } else {
                        historyScanTask.setStatus(TaskConstants.TASK_STATUS.ERROR.name());
                        historyScanTask.setResourcesSum(0L);
                        historyScanTask.setReturnSum(0L);
                        historyScanTask.setScanScore(100);
                    }
                    historyScanTask.setOutput(jsonArray.toJSONString());
                    historyService.updateScanTaskHistory(historyScanTask);
                } else if(StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.configAccount.getType())) {
                    CloudNativeConfigResult cloudNativeConfigResult = k8sProviderService.cloudNativeConfigResult(historyScanTask.getTaskId());
                    if (cloudNativeConfigResult != null && historyScanStatus.contains(cloudNativeConfigResult.getResultStatus())) {
                        historyScanTask.setStatus(cloudNativeConfigResult.getResultStatus());
                        historyScanTask.setResourcesSum(cloudNativeConfigResult.getReturnSum()!=null? cloudNativeConfigResult.getReturnSum():0);
                        historyScanTask.setReturnSum(cloudNativeConfigResult.getReturnSum()!=null? cloudNativeConfigResult.getReturnSum():0);
                        historyScanTask.setScanScore(historyService.calculateScore(historyScanTask.getAccountId(), cloudNativeConfigResult, TaskEnum.configAccount.getType()));
                    } else {
                        historyScanTask.setStatus(TaskConstants.TASK_STATUS.ERROR.name());
                        historyScanTask.setResourcesSum(0L);
                        historyScanTask.setReturnSum(0L);
                        historyScanTask.setScanScore(100);
                    }
                    historyScanTask.setOutput(jsonArray.toJSONString());
                    historyService.updateScanTaskHistory(historyScanTask);
                }
            }
            long count = historyScanTaskMapper.countByExample(historyScanTaskExample);
            if(historyScanTasks.size() == count) {
                historyScan.setStatus(TaskConstants.TASK_STATUS.FINISHED.name());
                historyScanMapper.updateByPrimaryKeySelective(historyScan);
                historyService.updateScanHistory(historyScan);
            }
            historyIdMap.remove(historyScanToBeProceed.getId());
        }

    }

    //任务编排
    @XxlJob("taskArrangementJobHandler")
    public void taskArrangementJobHandler() throws Exception {
        //任务编排
        final TaskExample taskExample = new TaskExample();
        TaskExample.Criteria taskCriteria = taskExample.createCriteria();
        taskCriteria.andStatusEqualTo(TaskConstants.TASK_STATUS.APPROVED.toString());
        if (CollectionUtils.isNotEmpty(processingGroupIdMap.keySet())) {
            taskCriteria.andIdNotIn(new ArrayList<>(processingGroupIdMap.keySet()));
        }
        taskExample.setOrderByClause("create_time limit 10");
        List<Task> tasks = taskMapper.selectByExample(taskExample);
        List<String> status = Arrays.asList(TaskConstants.TASK_STATUS.ERROR.name(), TaskConstants.TASK_STATUS.FINISHED.name(), TaskConstants.TASK_STATUS.WARNING.name());
        for (Task task : tasks) {
            final Task taskToBeProceed;
            try {
                taskToBeProceed = BeanUtils.copyBean(new Task(), task);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            if (processingGroupIdMap.get(taskToBeProceed.getId()) != null) {
                return;
            }
            processingGroupIdMap.put(taskToBeProceed.getId(), taskToBeProceed.getId());
            TaskItemExample taskItemExample = new TaskItemExample();
            TaskItemExample.Criteria taskItemCriteria = taskItemExample.createCriteria();
            taskItemCriteria.andTaskIdEqualTo(task.getId());
            List<TaskItem> taskItems = taskItemMapper.selectByExample(taskItemExample);
            for (TaskItem taskItem : taskItems) {
                TaskItemResourceExample taskItemResourceExample = new TaskItemResourceExample();
                TaskItemResourceExample.Criteria resourceCriteria = taskItemResourceExample.createCriteria();
                resourceCriteria.andTaskItemIdEqualTo(taskItem.getId());
                long sum = taskItemResourceMapper.countByExample(taskItemResourceExample);
                long i = 0;//总数量
                List<TaskItemResource> taskItemResources = taskItemResourceMapper.selectByExample(taskItemResourceExample);
                for (TaskItemResource taskItemResource : taskItemResources) {
                    long n = 0;//已经检测完的数量
                    if (StringUtils.equalsIgnoreCase(taskItemResource.getAccountType(), TaskEnum.cloudAccount.getType())) {
                        HistoryCloudTaskExample example = new HistoryCloudTaskExample();
                        example.createCriteria().andIdEqualTo(taskItemResource.getResourceId()).andStatusIn(status);
                        n = historyCloudTaskMapper.countByExample(example);
                        i = i + n;
                    } else if(StringUtils.equalsIgnoreCase(taskItemResource.getAccountType(), TaskEnum.vulnAccount.getType())) {
                        HistoryVulnTaskExample example = new HistoryVulnTaskExample();
                        example.createCriteria().andIdEqualTo(taskItemResource.getResourceId()).andStatusIn(status);
                        n = historyVulnTaskMapper.countByExample(example);
                        i = i + n;
                    } else if(StringUtils.equalsIgnoreCase(taskItemResource.getAccountType(), TaskEnum.serverAccount.getType())) {
                        HistoryServerResultExample example = new HistoryServerResultExample();
                        example.createCriteria().andIdEqualTo(taskItemResource.getResourceId()).andResultStatusIn(status);
                        n = historyServerResultMapper.countByExample(example);
                        i = i + n;
                    } else if(StringUtils.equalsIgnoreCase(taskItemResource.getAccountType(), TaskEnum.imageAccount.getType())) {
                        HistoryImageResultExample example = new HistoryImageResultExample();
                        example.createCriteria().andIdEqualTo(taskItemResource.getResourceId()).andResultStatusIn(status);
                        n = historyImageResultMapper.countByExample(example);
                        i = i + n;
                    } else if(StringUtils.equalsIgnoreCase(taskItemResource.getAccountType(), TaskEnum.codeAccount.getType())) {
                        HistoryCodeResultExample example = new HistoryCodeResultExample();
                        example.createCriteria().andIdEqualTo(taskItemResource.getResourceId()).andResultStatusIn(status);
                        n = historyCodeResultMapper.countByExample(example);
                        i = i + n;
                    } else if(StringUtils.equalsIgnoreCase(taskItemResource.getAccountType(), TaskEnum.fsAccount.getType())) {
                        HistoryFileSystemResultExample example = new HistoryFileSystemResultExample();
                        example.createCriteria().andIdEqualTo(taskItemResource.getResourceId()).andResultStatusIn(status);
                        n = historyFileSystemResultMapper.countByExample(example);
                        i = i + n;
                    }
                    if (n > 0) {//任务结束时插入结束日志，但是只保留一条
                        TaskItemResourceLogExample taskItemResourceLogExample = new TaskItemResourceLogExample();
                        taskItemResourceLogExample.createCriteria().andTaskItemIdEqualTo(taskItemResource.getTaskItemId()).andTaskItemResourceIdEqualTo(String.valueOf(taskItemResource.getId()))
                                .andResourceIdEqualTo(taskItemResource.getResourceId()).andOperationEqualTo("i18n_end_task").andResultEqualTo(true);
                        long c = taskItemResourceLogMapper.countByExample(taskItemResourceLogExample);
                        if (c == 0) {
                            taskService.saveTaskItemResourceLog(taskItemResource.getTaskItemId(), String.valueOf(taskItemResource.getId()), taskItemResource.getResourceId(), "i18n_end_task", "", true);
                        }
                    }
                }
                if (sum == i) {//若总数与检测数相等，代表子项任务完成
                    taskItem.setStatus(TaskConstants.TASK_STATUS.FINISHED.name());
                    taskItemMapper.updateByPrimaryKeySelective(taskItem);
                }
            }
            taskItemCriteria.andStatusIn(status);
            long count = taskItemMapper.countByExample(taskItemExample);
            if(taskItems.size() == count) {//若完成状态总数与检测子项数相等，代表总任务完成
                task.setStatus(TaskConstants.TASK_STATUS.FINISHED.name());
                taskMapper.updateByPrimaryKeySelective(task);
            }
            processingGroupIdMap.remove(taskToBeProceed.getId());
        }
    }


}
