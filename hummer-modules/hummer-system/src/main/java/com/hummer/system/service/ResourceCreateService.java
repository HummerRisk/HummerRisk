package com.hummer.system.service;

import com.alibaba.fastjson.JSONArray;
import com.hummer.cloud.api.ICloudProviderService;
import com.hummer.common.core.constant.TaskConstants;
import com.hummer.common.core.constant.TaskEnum;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.utils.BeanUtils;
import com.hummer.system.mapper.HistoryCloudTaskMapper;
import com.hummer.system.mapper.HistoryScanMapper;
import com.hummer.system.mapper.HistoryScanTaskMapper;
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
    private static ConcurrentHashMap<Integer, Integer> historyIdMap = new ConcurrentHashMap<>();
    @Autowired
    private HistoryService historyService;
    @Autowired
    private HistoryScanMapper historyScanMapper;
    @Autowired
    private HistoryScanTaskMapper historyScanTaskMapper;
    @Autowired
    private HistoryCloudTaskMapper historyCloudTaskMapper;
    @DubboReference
    private ICloudProviderService cloudProviderService;

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
            JSONArray jsonArray = new JSONArray();
            for (HistoryScanTask historyScanTask : historyScanTasks) {
                if (StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.cloudAccount.getType())) {
                    CloudTask cloudTask = cloudProviderService.selectCloudTask(historyScanTask.getTaskId());
                    if (cloudTask != null) {
                        if (historyScanStatus.contains(cloudTask.getStatus())) {
                            historyScanTask.setStatus(cloudTask.getStatus());
                            historyScanTask.setResourcesSum(cloudTask.getResourcesSum()!=null? cloudTask.getResourcesSum():0);
                            historyScanTask.setReturnSum(cloudTask.getReturnSum()!=null? cloudTask.getReturnSum():0);
                            historyScanTask.setScanScore(historyService.calculateScore(cloudTask.getAccountId(), cloudTask, TaskEnum.cloudAccount.getType()));
                        } else {
                            continue;
                        }
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
            if (historyScanTasks.size() == 0) {
                historyScan.setStatus(TaskConstants.TASK_STATUS.FINISHED.name());
                historyService.updateScanHistory(historyScan);
            }
            historyIdMap.remove(historyScanToBeProceed.getId());
        }

    }

}
