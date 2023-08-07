package com.hummer.system.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hummer.cloud.api.ICloudProviderService;
import com.hummer.common.core.constant.TaskConstants;
import com.hummer.common.core.constant.TaskEnum;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.dto.CloudTaskDTO;
import com.hummer.common.core.utils.UUIDUtil;
import com.hummer.system.mapper.*;
import com.hummer.system.mapper.ext.ExtHistoryScanMapper;
import com.hummer.system.mapper.ext.ExtResourceMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author harris
 */
@Service
public class HistoryService {

    @Autowired
    private HistoryScanMapper historyScanMapper;
    @Autowired
    private HistoryScanTaskMapper historyScanTaskMapper;
    @Autowired
    private HistoryCloudTaskMapper historyCloudTaskMapper;
    @Autowired
    private HistoryCloudTaskItemMapper historyCloudTaskItemMapper;
    @Autowired
    private HistoryCloudTaskLogMapper historyCloudTaskLogMapper;
    @Autowired
    private HistoryCloudTaskResourceMapper historyCloudTaskResourceMapper;
    @Autowired
    private ExtResourceMapper extResourceMapper;
    @Autowired
    private ExtHistoryScanMapper extHistoryScanMapper;
    @DubboReference
    private ICloudProviderService cloudProviderService;

    public Integer insertScanHistory(Object obj) throws Exception {

        Map map = obj2Account(obj);
        String accountId = map.get("accountId").toString();
        String accountType = map.get("accountType").toString();

        HistoryScan history = new HistoryScan();
        //TODO dubbo 调用没有http请求获取不到用户信息
//        history.setOperator(tokenService.getLoginUser().getUserId());
        history.setAccountId(accountId);
        history.setAccountType(accountType);
        history.setStatus(TaskConstants.TASK_STATUS.APPROVED.name());
        history.setCreateTime(System.currentTimeMillis());
        historyScanMapper.insertSelective(history);
        return history.getId();
    }

    public Integer updateScanHistory(HistoryScan historyScan) throws Exception {

        HistoryScanTaskExample historyScanTaskExample = new HistoryScanTaskExample();
        historyScanTaskExample.createCriteria().andScanIdEqualTo(historyScan.getId());
        List<HistoryScanTask> historyScanTasks = historyScanTaskMapper.selectByExampleWithBLOBs(historyScanTaskExample);
        JSONArray jsonArray = new JSONArray();
        historyScanTasks.forEach(item -> {
            String output = item.getOutput();
            Boolean flag;
            if (output != null) {
                try {
                    JSON.parse(output);
                    flag = true;
                } catch (Exception e) {
                    flag = false;
                }
                if (flag) jsonArray.addAll(JSON.parseArray(output));
            }
        });
        historyScan.setOutput(jsonArray.toJSONString());
        historyScan.setResourcesSum(Long.valueOf(extResourceMapper.sumResourcesSum(historyScan.getId())));
        historyScan.setReturnSum(Long.valueOf(extResourceMapper.sumReturnSum(historyScan.getId())));
        historyScan.setScanScore(extResourceMapper.sumScanScore(historyScan.getId()));
        historyScanMapper.updateByPrimaryKeySelective(historyScan);
        return historyScan.getId();
    }

    public void insertScanTaskHistory(Object obj, Integer scanId, String accountId, String accountType) throws Exception {
        HistoryScanTask historyScanTask = new HistoryScanTask();
        historyScanTask.setScanId(scanId);
        historyScanTask.setTaskId(obj2Result(obj));
        historyScanTask.setAccountId(accountId);
        historyScanTask.setAccountType(accountType);
        historyScanTask.setStatus(TaskConstants.TASK_STATUS.APPROVED.name());
        historyScanTask.setOperation("i18n_create_scan_history");
        historyScanTaskMapper.insertSelective(historyScanTask);
    }

    public void updateScanTaskHistory(HistoryScanTask historyScanTask) throws Exception {
        try {
            historyScanTaskMapper.updateByPrimaryKeySelective(historyScanTask);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void deleteScanTaskHistory(Integer scanId) throws Exception {
        try {
            historyScanTaskMapper.deleteByPrimaryKey(scanId);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }


    public void editUselessScanTaskHistory() throws Exception {
        try {
            final HistoryScanExample historyScanExample = new HistoryScanExample();
            HistoryScanExample.Criteria historyScanCriteria = historyScanExample.createCriteria();
            historyScanCriteria.andStatusEqualTo(TaskConstants.TASK_STATUS.APPROVED.toString());
            List<HistoryScan> historyScans = historyScanMapper.selectByExample(historyScanExample);

            List<String> historyScanStatus = Arrays.asList(TaskConstants.TASK_STATUS.ERROR.name(), TaskConstants.TASK_STATUS.FINISHED.name(), TaskConstants.TASK_STATUS.WARNING.name());
            for (HistoryScan historyScan : historyScans) {
                HistoryScanTaskExample historyScanTaskExample = new HistoryScanTaskExample();
                HistoryScanTaskExample.Criteria historyScanTaskCriteria = historyScanTaskExample.createCriteria();
                historyScanTaskCriteria.andScanIdEqualTo(historyScan.getId()).andStatusNotIn(historyScanStatus);
                List<HistoryScanTask> historyScanTasks = historyScanTaskMapper.selectByExample(historyScanTaskExample);
                if (historyScanTasks.size() == 0) {
                    historyScan.setStatus(TaskConstants.TASK_STATUS.ERROR.name());
                    historyScanMapper.updateByPrimaryKey(historyScan);
                }
                for (HistoryScanTask historyScanTask : historyScanTasks) {
                    if (historyScanTask.getTaskId() == null) {
                        historyScanTask.setStatus(TaskConstants.TASK_STATUS.ERROR.name());
                        historyScanTaskMapper.updateByPrimaryKeySelective(historyScanTask);
                    } else {
                        if (StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.cloudAccount.getType())) {
                            CloudTask cloudTask = cloudProviderService.selectCloudTask(historyScanTask.getTaskId());
                            Account account = cloudProviderService.selectAccount(historyScanTask.getAccountId());
                            if (cloudTask == null || account == null) {
                                historyScanTask.setStatus(TaskConstants.TASK_STATUS.ERROR.name());
                                historyScanTaskMapper.updateByPrimaryKeySelective(historyScanTask);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private boolean isJson(String str) {
        try {
            JSONObject.parseObject(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //通过对象获取资源ID
    public Map obj2Account(Object obj) throws Exception {
        Map map = new HashMap<>();
        try {
            String accountId = "";
            if (obj.getClass() == Account.class || obj.getClass() == AccountWithBLOBs.class) {
                accountId = ((Account) obj).getId();
                map.put("accountType", TaskEnum.cloudAccount.getType());
            }
            map.put("accountId", accountId);
            return map;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    //通过对象获取资源ID
    public String obj2Result(Object obj) throws Exception {
        try {
            String resultId = "";
            if (obj.getClass() == CloudTask.class || obj.getClass() == CloudTaskDTO.class) {
                resultId = ((CloudTask) obj).getId();
            } else {
                resultId = ((Map) obj).get("id").toString();
            }
            return resultId;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 计算安全检测评分
     * 高：中：低 = 5 ： 3 ： 2
     *
     * @param accountId
     * @return
     */
    public Integer calculateScore(String accountId, Object task, String accountType) {

        Integer score = 100;
        if (task == null) {
            return score;
        }
        if (StringUtils.equalsIgnoreCase(accountType, TaskEnum.cloudAccount.getType())) {

            CloudTask cloudTask = (CloudTask) task;
            Double criticalResultPercent = Double.valueOf(extResourceMapper.resultPercentByCloud(accountId, "CriticalRisk", cloudTask == null ? null : cloudTask.getId()) != null ? extResourceMapper.resultPercentByCloud(accountId, "CriticalRisk", cloudTask == null ? null : cloudTask.getId()) : "0.0");
            Double highResultPercent = Double.valueOf(extResourceMapper.resultPercentByCloud(accountId, "HighRisk", cloudTask == null ? null : cloudTask.getId()) != null ? extResourceMapper.resultPercentByCloud(accountId, "HighRisk", cloudTask == null ? null : cloudTask.getId()) : "0.0");
            Double mediumlResultPercent = Double.valueOf(extResourceMapper.resultPercentByCloud(accountId, "MediumRisk", cloudTask == null ? null : cloudTask.getId()) != null ? extResourceMapper.resultPercentByCloud(accountId, "MediumRisk", cloudTask == null ? null : cloudTask.getId()) : "0.0");
            Double lowResultPercent = Double.valueOf(extResourceMapper.resultPercentByCloud(accountId, "LowRisk", cloudTask == null ? null : cloudTask.getId()) != null ? extResourceMapper.resultPercentByCloud(accountId, "LowRisk", cloudTask == null ? null : cloudTask.getId()) : "0.0");

            HistoryCloudTaskExample example = new HistoryCloudTaskExample();
            HistoryCloudTaskExample.Criteria criteria = example.createCriteria();
            criteria.andAccountIdEqualTo(accountId).andSeverityEqualTo("CriticalRisk");
            long critical = historyCloudTaskMapper.countByExample(example);
            criteria.andAccountIdEqualTo(accountId).andSeverityEqualTo("HighRisk");
            long high = historyCloudTaskMapper.countByExample(example);
            criteria.andSeverityEqualTo("MediumRisk");
            long mediuml = historyCloudTaskMapper.countByExample(example);
            criteria.andSeverityEqualTo("LowRisk");
            long low = historyCloudTaskMapper.countByExample(example);

            long sum = 4 * critical + 3 * high + 2 * mediuml + 1 * low;
            score = 100 - (int) Math.ceil(criticalResultPercent * (4 * critical / (sum == 0 ? 1 : sum)) * 100 + highResultPercent * (3 * high / (sum == 0 ? 1 : sum)) * 100 + mediumlResultPercent * (2 * mediuml / (sum == 0 ? 1 : sum)) * 100 + lowResultPercent * (1 * low / (sum == 0 ? 1 : sum)) * 100);

        }

        return score;
    }

    public void insertHistoryCloudTask(HistoryCloudTask historyCloudTask) throws Exception {
        try {
            historyCloudTaskMapper.insertSelective(historyCloudTask);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void updateHistoryCloudTask(HistoryCloudTask historyCloudTask) throws Exception {
        try {
            historyCloudTaskMapper.updateByPrimaryKeySelective(historyCloudTask);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void insertHistoryCloudTaskItem(HistoryCloudTaskItemWithBLOBs historyCloudTaskItem) throws Exception {
        try {
            historyCloudTaskItemMapper.insertSelective(historyCloudTaskItem);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void updateHistoryCloudTaskItem(HistoryCloudTaskItemWithBLOBs historyCloudTaskItem) throws Exception {
        try {
            historyCloudTaskItemMapper.updateByPrimaryKeySelective(historyCloudTaskItem);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void insertHistoryCloudTaskLog(HistoryCloudTaskLogWithBLOBs historyCloudTaskLog) throws Exception {
        try {
            historyCloudTaskLogMapper.insertSelective(historyCloudTaskLog);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void insertHistoryCloudTaskResource(HistoryCloudTaskResourceWithBLOBs historyCloudTaskResource) throws Exception {
        try {
            historyCloudTaskResource.setId(UUIDUtil.newUUID());
            historyCloudTaskResourceMapper.insertSelective(historyCloudTaskResource);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void updateHistoryCloudTaskResource(HistoryCloudTaskResourceWithBLOBs historyCloudTaskResource) throws Exception {
        try {
            historyCloudTaskResourceMapper.updateByPrimaryKeySelective(historyCloudTaskResource);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

}
