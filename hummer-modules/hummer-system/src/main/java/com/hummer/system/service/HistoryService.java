package com.hummer.system.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hummer.cloud.api.ICloudProviderService;
import com.hummer.common.core.constant.TaskConstants;
import com.hummer.common.core.constant.TaskEnum;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.code.CodeResultRequest;
import com.hummer.common.core.domain.request.config.ConfigResultRequest;
import com.hummer.common.core.domain.request.fs.FsResultRequest;
import com.hummer.common.core.domain.request.image.ImageResultRequest;
import com.hummer.common.core.domain.request.k8s.K8sResultRequest;
import com.hummer.common.core.domain.request.server.ServerResultRequest;
import com.hummer.common.core.dto.*;
import com.hummer.common.core.utils.UUIDUtil;
import com.hummer.k8s.api.IK8sProviderService;
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
    private HistoryServerResultMapper historyServerResultMapper;
    @Autowired
    private HistoryImageResultMapper historyImageResultMapper;
    @Autowired
    private HistoryCloudNativeResultMapper historyCloudNativeResultMapper;
    @Autowired
    private HistoryCloudNativeConfigResultMapper historyCloudNativeConfigResultMapper;
    @Autowired
    private HistoryCodeResultMapper historyCodeResultMapper;
    @Autowired
    private HistoryFileSystemResultMapper historyFileSystemResultMapper;
    @Autowired
    private ExtResourceMapper extResourceMapper;
    @Autowired
    private ExtHistoryScanMapper extHistoryScanMapper;
    @DubboReference
    private ICloudProviderService cloudProviderService;
    @DubboReference
    private IK8sProviderService k8sProviderService;

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
                        } else if (StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.serverAccount.getType())) {
                            ServerResult serverResult = k8sProviderService.serverResult(historyScanTask.getTaskId());
                            Server server = k8sProviderService.server(historyScanTask.getAccountId());
                            if (serverResult == null || server == null) {
                                historyScanTask.setStatus(TaskConstants.TASK_STATUS.ERROR.name());
                                historyScanTaskMapper.updateByPrimaryKeySelective(historyScanTask);
                            }
                        } else if (StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.imageAccount.getType())) {
                            ImageResult imageResult = k8sProviderService.imageResult(historyScanTask.getTaskId());
                            Image image = k8sProviderService.image(historyScanTask.getAccountId());
                            if (imageResult == null || image == null) {
                                historyScanTask.setStatus(TaskConstants.TASK_STATUS.ERROR.name());
                                historyScanTaskMapper.updateByPrimaryKeySelective(historyScanTask);
                            }
                        } else if (StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.codeAccount.getType())) {
                            CodeResult codeResult = k8sProviderService.codeResult(historyScanTask.getTaskId());
                            Code code = k8sProviderService.code(historyScanTask.getAccountId());
                            if (codeResult == null || code == null) {
                                historyScanTask.setStatus(TaskConstants.TASK_STATUS.ERROR.name());
                                historyScanTaskMapper.updateByPrimaryKeySelective(historyScanTask);
                            }
                        } else if (StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.fsAccount.getType())) {
                            FileSystemResult fileSystemResult = k8sProviderService.fileSystemResult(historyScanTask.getTaskId());
                            FileSystem fileSystem = k8sProviderService.fileSystem(historyScanTask.getAccountId());
                            if (fileSystemResult == null || fileSystem == null) {
                                historyScanTask.setStatus(TaskConstants.TASK_STATUS.ERROR.name());
                                historyScanTaskMapper.updateByPrimaryKeySelective(historyScanTask);
                            }
                        } else if (StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.k8sAccount.getType())) {
                            CloudNativeResult cloudNativeResult = k8sProviderService.cloudNativeResult(historyScanTask.getTaskId());
                            CloudNative cloudNative = k8sProviderService.cloudNative(historyScanTask.getAccountId());
                            if (cloudNativeResult == null || cloudNative == null) {
                                historyScanTask.setStatus(TaskConstants.TASK_STATUS.ERROR.name());
                                historyScanTaskMapper.updateByPrimaryKeySelective(historyScanTask);
                            }
                        } else if (StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.configAccount.getType())) {
                            CloudNativeConfigResult cloudNativeConfigResult = k8sProviderService.cloudNativeConfigResult(historyScanTask.getTaskId());
                            CloudNativeConfig cloudNativeConfig = k8sProviderService.cloudNativeConfig(historyScanTask.getAccountId());
                            if (cloudNativeConfigResult == null || cloudNativeConfig == null) {
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
            } else if (obj.getClass() == Server.class || obj.getClass() == ServerDTO.class) {
                accountId = ((Server) obj).getId();
                map.put("accountType", TaskEnum.serverAccount.getType());
            } else if (obj.getClass() == Image.class || obj.getClass() == ImageDTO.class) {
                accountId = ((Image) obj).getId();
                map.put("accountType", TaskEnum.imageAccount.getType());
            } else if (obj.getClass() == CloudNative.class || obj.getClass() == CloudNativeDTO.class) {
                accountId = ((CloudNative) obj).getId();
                map.put("accountType", TaskEnum.k8sAccount.getType());
            } else if (obj.getClass() == CloudNativeConfig.class || obj.getClass() == CloudNativeConfigDTO.class) {
                accountId = ((CloudNativeConfig) obj).getId();
                map.put("accountType", TaskEnum.configAccount.getType());
            } else if (obj.getClass() == Code.class || obj.getClass() == CodeDTO.class) {
                accountId = ((Code) obj).getId();
                map.put("accountType", TaskEnum.codeAccount.getType());
            } else if (obj.getClass() == FileSystem.class || obj.getClass() == FsDTO.class) {
                accountId = ((FileSystem) obj).getId();
                map.put("accountType", TaskEnum.fsAccount.getType());
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
            } else if (obj.getClass() == ServerResult.class || obj.getClass() == ServerResultLogWithBLOBs.class || obj.getClass() == ServerResultDTO.class) {
                resultId = ((ServerResult) obj).getId();
            } else if (obj.getClass() == CloudNativeResult.class || obj.getClass() == CloudNativeResultWithBLOBs.class) {
                resultId = ((CloudNativeResult) obj).getId();
            } else if (obj.getClass() == CloudNativeConfigResult.class) {
                resultId = ((CloudNativeConfigResult) obj).getId();
            } else if (obj.getClass() == CodeResult.class || obj.getClass() == CodeResultDTO.class) {
                resultId = ((CodeResult) obj).getId();
            } else if (obj.getClass() == ImageResult.class || obj.getClass() == ImageResultWithBLOBs.class || obj.getClass() == ImageResultDTO.class) {
                resultId = ((ImageResult) obj).getId();
            } else if (obj.getClass() == FileSystemResult.class) {
                resultId = ((FileSystemResult) obj).getId();
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
            Double highResultPercent = Double.valueOf(extResourceMapper.resultPercentByCloud(accountId, "HighRisk", cloudTask == null ? null : cloudTask.getId()) != null ? extResourceMapper.resultPercentByCloud(accountId, "HighRisk", cloudTask == null ? null : cloudTask.getId()) : "0.0");
            Double mediumlResultPercent = Double.valueOf(extResourceMapper.resultPercentByCloud(accountId, "MediumRisk", cloudTask == null ? null : cloudTask.getId()) != null ? extResourceMapper.resultPercentByCloud(accountId, "MediumRisk", cloudTask == null ? null : cloudTask.getId()) : "0.0");
            Double lowResultPercent = Double.valueOf(extResourceMapper.resultPercentByCloud(accountId, "LowRisk", cloudTask == null ? null : cloudTask.getId()) != null ? extResourceMapper.resultPercentByCloud(accountId, "LowRisk", cloudTask == null ? null : cloudTask.getId()) : "0.0");

            HistoryCloudTaskExample example = new HistoryCloudTaskExample();
            HistoryCloudTaskExample.Criteria criteria = example.createCriteria();
            criteria.andAccountIdEqualTo(accountId).andSeverityEqualTo("HighRisk");
            long high = historyCloudTaskMapper.countByExample(example);
            criteria.andSeverityEqualTo("MediumRisk");
            long mediuml = historyCloudTaskMapper.countByExample(example);
            criteria.andSeverityEqualTo("LowRisk");
            long low = historyCloudTaskMapper.countByExample(example);

            long sum = 5 * high + 3 * mediuml + 2 * low;
            score = 100 - (int) Math.ceil(highResultPercent * (5 * high / (sum == 0 ? 1 : sum)) * 100 + mediumlResultPercent * (3 * mediuml / (sum == 0 ? 1 : sum)) * 100 + lowResultPercent * (2 * low / (sum == 0 ? 1 : sum)) * 100);

        } else if (StringUtils.equalsIgnoreCase(accountType, TaskEnum.k8sRuleAccount.getType())) {

            CloudTask cloudTask = (CloudTask) task;
            Double highResultPercent = Double.valueOf(extResourceMapper.resultPercentByCloud(accountId, "HighRisk", cloudTask == null ? null : cloudTask.getId()) != null ? extResourceMapper.resultPercentByCloud(accountId, "HighRisk", cloudTask == null ? null : cloudTask.getId()) : "0.0");
            Double mediumlResultPercent = Double.valueOf(extResourceMapper.resultPercentByCloud(accountId, "MediumRisk", cloudTask == null ? null : cloudTask.getId()) != null ? extResourceMapper.resultPercentByCloud(accountId, "MediumRisk", cloudTask == null ? null : cloudTask.getId()) : "0.0");
            Double lowResultPercent = Double.valueOf(extResourceMapper.resultPercentByCloud(accountId, "LowRisk", cloudTask == null ? null : cloudTask.getId()) != null ? extResourceMapper.resultPercentByCloud(accountId, "LowRisk", cloudTask == null ? null : cloudTask.getId()) : "0.0");

            HistoryCloudTaskExample example = new HistoryCloudTaskExample();
            HistoryCloudTaskExample.Criteria criteria = example.createCriteria();
            criteria.andAccountIdEqualTo(accountId).andSeverityEqualTo("HighRisk");
            long high = historyCloudTaskMapper.countByExample(example);
            criteria.andSeverityEqualTo("MediumRisk");
            long mediuml = historyCloudTaskMapper.countByExample(example);
            criteria.andSeverityEqualTo("LowRisk");
            long low = historyCloudTaskMapper.countByExample(example);

            long sum = 5 * high + 3 * mediuml + 2 * low;
            score = 100 - (int) Math.ceil(highResultPercent * (5 * high / (sum == 0 ? 1 : sum)) * 100 + mediumlResultPercent * (3 * mediuml / (sum == 0 ? 1 : sum)) * 100 + lowResultPercent * (2 * low / (sum == 0 ? 1 : sum)) * 100);

        } else if (StringUtils.equalsIgnoreCase(accountType, TaskEnum.serverAccount.getType())) {
            ServerResult serverResult = (ServerResult) task;
            if (StringUtils.equalsIgnoreCase(serverResult.getSeverity(), TaskConstants.Severity.HighRisk.name())) {
                score = 100 - 20;
            } else if (StringUtils.equalsIgnoreCase(serverResult.getSeverity(), TaskConstants.Severity.MediumRisk.name())) {
                score = 100 - 10;
            } else if (StringUtils.equalsIgnoreCase(serverResult.getSeverity(), TaskConstants.Severity.LowRisk.name())) {
                score = 100 - 5;
            }
        } else if (StringUtils.equalsIgnoreCase(accountType, TaskEnum.imageAccount.getType())) {
            ImageResult imageResult = (ImageResult) task;
            if (imageResult.getReturnSum() >= 0 && imageResult.getReturnSum() < 10) {
                score = 100 - 5;
            } else if (imageResult.getReturnSum() >= 10 && imageResult.getReturnSum() < 50) {
                score = 100 - 10;
            } else if (imageResult.getReturnSum() >= 50 && imageResult.getReturnSum() < 100) {
                score = 100 - 20;
            } else if (imageResult.getReturnSum() >= 100 && imageResult.getReturnSum() < 200) {
                score = 100 - 30;
            } else if (imageResult.getReturnSum() >= 200 && imageResult.getReturnSum() < 500) {
                score = 100 - 40;
            } else {
                score = 100 - 41;
            }
        } else if (StringUtils.equalsIgnoreCase(accountType, TaskEnum.codeAccount.getType())) {
            CodeResult codeResult = (CodeResult) task;
            if (codeResult.getReturnSum() >= 0 && codeResult.getReturnSum() < 10) {
                score = 100 - 5;
            } else if (codeResult.getReturnSum() >= 10 && codeResult.getReturnSum() < 50) {
                score = 100 - 10;
            } else if (codeResult.getReturnSum() >= 50 && codeResult.getReturnSum() < 100) {
                score = 100 - 20;
            } else if (codeResult.getReturnSum() >= 100 && codeResult.getReturnSum() < 200) {
                score = 100 - 30;
            } else if (codeResult.getReturnSum() >= 200 && codeResult.getReturnSum() < 500) {
                score = 100 - 40;
            } else {
                score = 100 - 41;
            }
        } else if (StringUtils.equalsIgnoreCase(accountType, TaskEnum.fsAccount.getType())) {
            FileSystemResult fsResult = (FileSystemResult) task;
            if (fsResult.getReturnSum() >= 0 && fsResult.getReturnSum() < 10) {
                score = 100 - 5;
            } else if (fsResult.getReturnSum() >= 10 && fsResult.getReturnSum() < 50) {
                score = 100 - 10;
            } else if (fsResult.getReturnSum() >= 50 && fsResult.getReturnSum() < 100) {
                score = 100 - 20;
            } else if (fsResult.getReturnSum() >= 100 && fsResult.getReturnSum() < 200) {
                score = 100 - 30;
            } else if (fsResult.getReturnSum() >= 200 && fsResult.getReturnSum() < 500) {
                score = 100 - 40;
            } else {
                score = 100 - 41;
            }
        } else if (StringUtils.equalsIgnoreCase(accountType, TaskEnum.k8sAccount.getType())) {
            CloudNativeResult cloudNativeResult = (CloudNativeResult) task;
            if (cloudNativeResult.getReturnSum() >= 0 && cloudNativeResult.getReturnSum() < 10) {
                score = 100 - 5;
            } else if (cloudNativeResult.getReturnSum() >= 10 && cloudNativeResult.getReturnSum() < 50) {
                score = 100 - 10;
            } else if (cloudNativeResult.getReturnSum() >= 50 && cloudNativeResult.getReturnSum() < 100) {
                score = 100 - 20;
            } else if (cloudNativeResult.getReturnSum() >= 100 && cloudNativeResult.getReturnSum() < 200) {
                score = 100 - 30;
            } else if (cloudNativeResult.getReturnSum() >= 200 && cloudNativeResult.getReturnSum() < 500) {
                score = 100 - 40;
            } else {
                score = 100 - 41;
            }
        } else if (StringUtils.equalsIgnoreCase(accountType, TaskEnum.configAccount.getType())) {
            CloudNativeConfigResult cloudNativeConfigResult = (CloudNativeConfigResult) task;
            if (cloudNativeConfigResult.getReturnSum() >= 0 && cloudNativeConfigResult.getReturnSum() < 10) {
                score = 100 - 5;
            } else if (cloudNativeConfigResult.getReturnSum() >= 10 && cloudNativeConfigResult.getReturnSum() < 50) {
                score = 100 - 10;
            } else if (cloudNativeConfigResult.getReturnSum() >= 50 && cloudNativeConfigResult.getReturnSum() < 100) {
                score = 100 - 20;
            } else if (cloudNativeConfigResult.getReturnSum() >= 100 && cloudNativeConfigResult.getReturnSum() < 200) {
                score = 100 - 30;
            } else if (cloudNativeConfigResult.getReturnSum() >= 200 && cloudNativeConfigResult.getReturnSum() < 500) {
                score = 100 - 40;
            } else {
                score = 100 - 41;
            }
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

    public void insertHistoryServerResult(HistoryServerResult record) {
        historyServerResultMapper.insertSelective(record);
    }

    public void updateHistoryServerResult(HistoryServerResult record) {
        historyServerResultMapper.updateByPrimaryKeySelective(record);
    }

    public void insertHistoryImageResult(HistoryImageResultWithBLOBs record) {
        historyImageResultMapper.insertSelective(record);
    }

    public void updateHistoryImageResult(HistoryImageResultWithBLOBs record) {
        historyImageResultMapper.updateByPrimaryKeySelective(record);
    }

    public void insertHistoryCloudNativeResult(HistoryCloudNativeResultWithBLOBs record) {
        historyCloudNativeResultMapper.insertSelective(record);
    }

    public void updateHistoryCloudNativeResult(HistoryCloudNativeResultWithBLOBs record) {
        historyCloudNativeResultMapper.updateByPrimaryKeySelective(record);
    }

    public void insertHistoryCloudNativeConfigResult(HistoryCloudNativeConfigResult record) {
        historyCloudNativeConfigResultMapper.insertSelective(record);
    }

    public void updateHistoryCloudNativeConfigResult(HistoryCloudNativeConfigResult record) {
        historyCloudNativeConfigResultMapper.updateByPrimaryKeySelective(record);
    }

    public void insertHistoryCodeResult(HistoryCodeResult record) {
        historyCodeResultMapper.insertSelective(record);
    }

    public void updateHistoryCodeResult(HistoryCodeResult record) {
        historyCodeResultMapper.updateByPrimaryKeySelective(record);
    }

    public void insertHistoryFileSystemResult(HistoryFileSystemResult record) {
        historyFileSystemResultMapper.insertSelective(record);
    }

    public void updateHistoryFileSystemResult(HistoryFileSystemResult record) {
        historyFileSystemResultMapper.updateByPrimaryKeySelective(record);
    }

    public List<HistoryImageResultDTO> imageHistory(ImageResultRequest request) {
        List<HistoryImageResultDTO> historyList = extHistoryScanMapper.imageHistory(request);
        return historyList;
    }

    public List<HistoryCodeResultDTO> codeHistory(CodeResultRequest request) {
        List<HistoryCodeResultDTO> historyList = extHistoryScanMapper.codeHistory(request);
        return historyList;
    }

    public List<HistoryServerResultDTO> serverHistory(ServerResultRequest request) {
        List<HistoryServerResultDTO> historyList = extHistoryScanMapper.serverHistory(request);
        return historyList;
    }

    public List<HistoryFsResultDTO> fsHistory(FsResultRequest request) {
        List<HistoryFsResultDTO> historyList = extHistoryScanMapper.fsHistory(request);
        return historyList;
    }

    public List<HistoryCloudNativeResultDTO> k8sHistory(K8sResultRequest request) {
        List<HistoryCloudNativeResultDTO> historyList = extHistoryScanMapper.k8sHistory(request);
        return historyList;
    }

    public List<HistoryCloudNativeConfigResultDTO> configHistory(ConfigResultRequest request) {
        List<HistoryCloudNativeConfigResultDTO> historyList = extHistoryScanMapper.configHistory(request);
        return historyList;
    }

    public void serverDeleteHistoryResult(String id) throws Exception {
        historyServerResultMapper.deleteByPrimaryKey(id);
    }

    public void deleteServerHistoryResults(List<String> ids) throws Exception {
        ids.forEach(id -> {
            try {
                serverDeleteHistoryResult(id);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    public void codeDeleteHistoryResult(String id) throws Exception {
        historyCodeResultMapper.deleteByPrimaryKey(id);
    }

    public void deleteCodeHistoryResults(List<String> ids) throws Exception {
        ids.forEach(id -> {
            try {
                codeDeleteHistoryResult(id);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    public void configDeleteHistoryResult(String id) throws Exception {
        historyCloudNativeConfigResultMapper.deleteByPrimaryKey(id);
    }

    public void deleteConfigHistoryResults(List<String> ids) throws Exception {
        ids.forEach(id -> {
            try {
                configDeleteHistoryResult(id);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    public void fsDeleteHistoryResult(String id) throws Exception {
        historyFileSystemResultMapper.deleteByPrimaryKey(id);
    }

    public void deleteFsHistoryResults(List<String> ids) throws Exception {
        ids.forEach(id -> {
            try {
                fsDeleteHistoryResult(id);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    public void imageDeleteHistoryResult(String id) throws Exception {
        historyImageResultMapper.deleteByPrimaryKey(id);
    }

    public void deleteImageHistoryResults(List<String> ids) throws Exception {
        ids.forEach(id -> {
            try {
                imageDeleteHistoryResult(id);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    public void k8sDeleteHistoryResult(String id) throws Exception {
        historyCloudNativeResultMapper.deleteByPrimaryKey(id);
    }

    public void deleteK8sHistoryResults(List<String> ids) throws Exception {
        ids.forEach(id -> {
            try {
                k8sDeleteHistoryResult(id);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

}
