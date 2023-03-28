package com.hummer.system.service;

import com.alibaba.fastjson.JSONArray;
import com.hummer.cloud.api.ICloudProviderService;
import com.hummer.common.core.constant.TaskConstants;
import com.hummer.common.core.constant.TaskEnum;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.utils.BeanUtils;
import com.hummer.k8s.api.IK8sProviderService;
import com.hummer.system.mapper.*;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    @Autowired
    private CommonThreadPool commonThreadPool;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private TaskItemMapper taskItemMapper;
    @Autowired
    private TaskItemResourceMapper taskItemResourceMapper;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private HistoryScanMapper historyScanMapper;
    @Autowired
    private HistoryScanTaskMapper historyScanTaskMapper;
    @Autowired
    private HistoryCloudTaskMapper historyCloudTaskMapper;
    @Autowired
    private HistoryServerResultMapper historyServerResultMapper;
    @Autowired
    private HistoryImageResultMapper historyImageResultMapper;
    @Autowired
    private TaskItemResourceLogMapper taskItemResourceLogMapper;
    @Autowired
    private HistoryCodeResultMapper historyCodeResultMapper;
    @Autowired
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


}
