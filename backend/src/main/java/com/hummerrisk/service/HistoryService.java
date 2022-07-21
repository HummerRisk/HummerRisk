package com.hummerrisk.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hummer.quartz.service.QuartzManageService;
import com.hummerrisk.base.domain.Package;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.*;
import com.hummerrisk.base.mapper.ext.ExtCloudTaskMapper;
import com.hummerrisk.base.mapper.ext.ExtResourceMapper;
import com.hummerrisk.commons.constants.TaskConstants;
import com.hummerrisk.commons.constants.TaskEnum;
import com.hummerrisk.commons.utils.BeanUtils;
import com.hummerrisk.commons.utils.CommonThreadPool;
import com.hummerrisk.commons.utils.PlatformUtils;
import com.hummerrisk.commons.utils.UUIDUtil;
import com.hummerrisk.dto.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author harris
 */
@Service
public class HistoryService {

    @Resource @Lazy
    private CloudTaskMapper cloudTaskMapper;
    @Resource @Lazy
    private CloudTaskItemMapper cloudTaskItemMapper;
    @Resource @Lazy
    private CloudTaskItemResourceMapper cloudTaskItemResourceMapper;
    @Resource @Lazy
    private ResourceMapper resourceMapper;
    @Resource @Lazy
    private AccountMapper accountMapper;
    @Resource @Lazy
    private ExtResourceMapper extResourceMapper;
    @Resource @Lazy
    private HistoryScanMapper historyScanMapper;
    @Resource @Lazy
    private HistoryScanTaskMapper historyScanTaskMapper;
    @Resource @Lazy
    private ServerMapper serverMapper;
    @Resource @Lazy
    private ServerResultMapper serverResultMapper;
    @Resource @Lazy
    private ImageMapper imageMapper;
    @Resource @Lazy
    private ImageResultMapper imageResultMapper;
    @Resource @Lazy
    private PackageMapper packageMapper;
    @Resource @Lazy
    private PackageResultMapper packageResultMapper;
    @Resource @Lazy
    private HistoryCloudTaskMapper historyCloudTaskMapper;
    @Resource @Lazy
    private HistoryCloudTaskItemMapper historyCloudTaskItemMapper;
    @Resource @Lazy
    private HistoryCloudTaskLogMapper historyCloudTaskLogMapper;
    @Resource @Lazy
    private HistoryCloudTaskResourceMapper historyCloudTaskResourceMapper;
    @Resource @Lazy
    private HistoryVulnTaskMapper historyVulnTaskMapper;
    @Resource @Lazy
    private HistoryVulnTaskItemMapper historyVulnTaskItemMapper;
    @Resource @Lazy
    private HistoryVulnTaskLogMapper historyVulnTaskLogMapper;
    @Resource @Lazy
    private HistoryVulnTaskResourceMapper historyVulnTaskResourceMapper;
    @Resource @Lazy
    private HistoryServerTaskMapper historyServerTaskMapper;
    @Resource @Lazy
    private HistoryServerTaskLogMapper historyServerTaskLogMapper;
    @Resource @Lazy
    private HistoryPackageTaskMapper historyPackageTaskMapper;
    @Resource @Lazy
    private HistoryPackageTaskItemMapper historyPackageTaskItemMapper;
    @Resource @Lazy
    private HistoryPackageTaskLogMapper historyPackageTaskLogMapper;
    @Resource @Lazy
    private HistoryImageTaskMapper historyImageTaskMapper;
    @Resource @Lazy
    private HistoryImageTaskItemMapper historyImageTaskItemMapper;
    @Resource @Lazy
    private HistoryImageTaskLogMapper historyImageTaskLogMapper;

    public Integer insertScanHistory (Object obj) throws Exception {

        String accountId = obj2Account(obj).get("accountId").toString();
        String accountType = obj2Account(obj).get("accountType").toString();

        HistoryScan history = new HistoryScan();
        history.setOperator("System");
        history.setAccountId(accountId);
        history.setAccountType(accountType);
        history.setStatus(TaskConstants.TASK_STATUS.APPROVED.name());
        history.setCreateTime(System.currentTimeMillis());
        return historyScanMapper.insertSelective(history);
    }

    public Integer updateScanHistory (HistoryScan historyScan) throws Exception {

        HistoryScan history = new HistoryScan();
        HistoryScanTaskExample historyScanTaskExample = new HistoryScanTaskExample();
        historyScanTaskExample.createCriteria().andIdEqualTo(historyScan.getId());
        List<HistoryScanTask> historyScanTasks = historyScanTaskMapper.selectByExampleWithBLOBs(historyScanTaskExample);
        JSONArray jsonArray = new JSONArray();
        historyScanTasks.forEach(item ->{
            if(item.getOutput()!=null) jsonArray.addAll(JSON.parseArray(item.getOutput()));
        });
        history.setOutput(jsonArray.toJSONString());
        history.setResourcesSum(Long.valueOf(extResourceMapper.sumResourcesSum(historyScan.getAccountId())));
        history.setReturnSum(Long.valueOf(extResourceMapper.sumReturnSum(historyScan.getAccountId())));
        history.setScanScore(calculateScore(accountMapper.selectByPrimaryKey(historyScan.getAccountId()), null));
        historyScanMapper.updateByPrimaryKeySelective(history);
        return historyScan.getId();
    }

    public void insertScanTaskHistory (Object obj, Integer scanId, String accountId, String accountType) throws Exception {
        HistoryScanTask historyScanTask = new HistoryScanTask();
        historyScanTask.setScanId(scanId);
        historyScanTask.setTaskId(obj2Result(obj));
        historyScanTask.setAccountId(accountId);
        historyScanTask.setAccountType(accountType);
        historyScanTask.setStatus(TaskConstants.TASK_STATUS.APPROVED.name());
        historyScanTask.setOperation("i18n_create_scan_history");
        historyScanTaskMapper.insertSelective(historyScanTask);
    }

    public void updateScanTaskHistory (Object obj, HistoryScanTask historyScanTask) throws Exception {
        try{
            String resultId = obj2Result(obj);
            JSONArray jsonArray = new JSONArray();
            if (StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.cloudAccount.getType()) ||
                    StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.vulnAccount.getType())) {
                CloudTask cloudTask = cloudTaskMapper.selectByPrimaryKey(resultId);
                CloudTaskItemResourceExample cloudTaskItemResourceExample = new CloudTaskItemResourceExample();
                cloudTaskItemResourceExample.createCriteria().andTaskIdEqualTo(cloudTask.getId());
                List<CloudTaskItemResourceWithBLOBs> taskItemResources = cloudTaskItemResourceMapper.selectByExampleWithBLOBs(cloudTaskItemResourceExample);
                taskItemResources.forEach(item ->{
                    ResourceWithBLOBs resource = resourceMapper.selectByPrimaryKey(item.getResourceId());
                    if(resource!=null) {
                        if (!resource.getResources().isEmpty()) {
                            String json = resource.getResources();
                            boolean isJson = isJson(json);
                            if (isJson) {
                                jsonArray.addAll(JSON.parseArray(json));
                            } else {
                                jsonArray.addAll(JSON.parseArray("[{'key':'" + json + "'}]"));
                            }
                        } else {
                            jsonArray.addAll(JSON.parseArray("[]"));
                        }
                    }
                });
                historyScanTask.setResourcesSum(cloudTask.getResourcesSum()!=null? cloudTask.getResourcesSum():0);
                historyScanTask.setReturnSum(cloudTask.getReturnSum()!=null? cloudTask.getReturnSum():0);
                historyScanTask.setScanScore(calculateScore(accountMapper.selectByPrimaryKey(cloudTask.getAccountId()), cloudTask));
            } else if(StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.serverAccount.getType())) {
                historyScanTask.setResourcesSum(1L);
                historyScanTask.setReturnSum(1L);
                historyScanTask.setScanScore(calculateScore(serverMapper.selectByPrimaryKey(historyScanTask.getAccountId()), serverResultMapper.selectByPrimaryKey(resultId)));
            } else if(StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.imageAccount.getType())) {
                ImageResult imageResult = imageResultMapper.selectByPrimaryKey(resultId);
                historyScanTask.setResourcesSum(imageResult.getReturnSum()!=null? imageResult.getReturnSum():0);
                historyScanTask.setReturnSum(imageResult.getReturnSum()!=null? imageResult.getReturnSum():0);
                historyScanTask.setScanScore(calculateScore(imageMapper.selectByPrimaryKey(historyScanTask.getAccountId()), imageResult));
            } else if(StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.packageAccount.getType())) {
                PackageResult packageResult = packageResultMapper.selectByPrimaryKey(resultId);
                historyScanTask.setResourcesSum(packageResult.getReturnSum()!=null? packageResult.getReturnSum():0);
                historyScanTask.setReturnSum(packageResult.getReturnSum()!=null? packageResult.getReturnSum():0);
                historyScanTask.setScanScore(calculateScore(accountMapper.selectByPrimaryKey(historyScanTask.getAccountId()), packageResult));
            }
            historyScanTask.setOperation("i18n_update_scan_history");
            historyScanTask.setOutput(jsonArray.toJSONString());
            historyScanTaskMapper.updateByPrimaryKeyWithBLOBs(historyScanTask);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    private boolean isJson(String str){
        try {
            JSONObject.parseObject(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //通过对象获取资源ID
    public Map obj2Account(Object obj) throws Exception{
        Map map = new HashMap<>();
        try {
            String accountId = "";
            if (obj.getClass() == Account.class || obj.getClass() == AccountWithBLOBs.class) {
                accountId = ((Account) obj).getId();
                if (PlatformUtils.isSupportCloudAccount(((Account) obj).getPluginId())) {
                    map.put("accountType", TaskEnum.cloudAccount.getType());
                } else {
                    map.put("accountType", TaskEnum.vulnAccount.getType());
                }
            } else if(obj.getClass() == Server.class || obj.getClass() == ServerDTO.class) {
                accountId = ((Server) obj).getId();
                map.put("accountType", TaskEnum.serverAccount.getType());
            } else if(obj.getClass() == Image.class || obj.getClass() == ImageDTO.class) {
                accountId = ((Image) obj).getId();
                map.put("accountType", TaskEnum.imageAccount.getType());
            } else if(obj.getClass() == Package.class || obj.getClass() == PackageDTO.class) {
                accountId = ((Package) obj).getId();
                map.put("accountType", TaskEnum.packageAccount.getType());
            }
            map.put("accountId", accountId);
            return map;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    //通过对象获取资源ID
    public String obj2Result(Object obj) throws Exception{
        try {
            String resultId = "";
            if (obj.getClass() == CloudTask.class || obj.getClass() == CloudTaskDTO.class) {
                resultId = ((CloudTask) obj).getId();
            } else if(obj.getClass() == ServerResult.class || obj.getClass() == ServerResultDTO.class) {
                resultId = ((ServerResult) obj).getId();
            } else if(obj.getClass() == ImageResult.class || obj.getClass() == ImageResultWithBLOBs.class || obj.getClass() == ImageResultDTO.class) {
                resultId = ((ImageResult) obj).getId();
            } else if(obj.getClass() == PackageResult.class || obj.getClass() == PackageResultWithBLOBs.class || obj.getClass() == PackageResultDTO.class) {
                resultId = ((PackageResult) obj).getId();
            }
            return resultId;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 计算安全检测评分
     * 高：中：低 = 5 ： 3 ： 2
     * @param account
     * @return
     */
    public Integer calculateScore (Object account, Object task) {

        Integer score = 100;

        if (account.getClass() == Account.class || account.getClass() == AccountWithBLOBs.class || account.getClass() == AccountDTO.class) {
            String accountId = ((Account) account).getId();
            CloudTask cloudTask =  (CloudTask) task;
            Double highResultPercent = Double.valueOf(extResourceMapper.resultPercent(accountId, "HighRisk", cloudTask ==null?null: cloudTask.getId())!=null?extResourceMapper.resultPercent(accountId, "HighRisk", cloudTask ==null?null: cloudTask.getId()):"0.0");
            Double mediumlResultPercent = Double.valueOf(extResourceMapper.resultPercent(accountId, "MediumRisk", cloudTask ==null?null: cloudTask.getId())!=null?extResourceMapper.resultPercent(accountId, "MediumRisk", cloudTask ==null?null: cloudTask.getId()): "0.0");
            Double lowResultPercent = Double.valueOf(extResourceMapper.resultPercent(accountId, "LowRisk", cloudTask ==null?null: cloudTask.getId())!=null?extResourceMapper.resultPercent(accountId, "LowRisk", cloudTask ==null?null: cloudTask.getId()):"0.0");

            CloudTaskExample example = new CloudTaskExample();
            CloudTaskExample.Criteria criteria = example.createCriteria();
            criteria.andAccountIdEqualTo(accountId).andSeverityEqualTo("HighRisk");
            long high = cloudTaskMapper.countByExample(example);
            criteria.andSeverityEqualTo("MediumRisk");
            long mediuml = cloudTaskMapper.countByExample(example);
            criteria.andSeverityEqualTo("LowRisk");
            long low = cloudTaskMapper.countByExample(example);

            long sum = 5 * high + 3 * mediuml + 2 * low;
            score = 100 - (int) Math.ceil(highResultPercent * (5 * high / (sum == 0 ? 1 : sum) ) * 100 + mediumlResultPercent * (3 * mediuml / (sum == 0 ? 1 : sum) ) * 100 + lowResultPercent * (2 * low / (sum == 0 ? 1 : sum) ) * 100);
        } else if(account.getClass() == Server.class || account.getClass() == ServerDTO.class) {
            ServerResult serverResult = (ServerResult) task;
            if (StringUtils.equalsIgnoreCase(serverResult.getSeverity(), TaskConstants.Severity.HighRisk.name())) {
                score = 100 - 20;
            } else if (StringUtils.equalsIgnoreCase(serverResult.getSeverity(), TaskConstants.Severity.MediumRisk.name())) {
                score = 100 - 10;
            } else if (StringUtils.equalsIgnoreCase(serverResult.getSeverity(), TaskConstants.Severity.LowRisk.name())) {
                score = 100 - 5;
            }
        } else if(account.getClass() == Image.class || account.getClass() == ImageDTO.class) {
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
        } else if(account.getClass() == Package.class || account.getClass() == PackageDTO.class) {
            PackageResult packageResult = (PackageResult) task;
            if (packageResult.getReturnSum() >= 0 && packageResult.getReturnSum() < 10) {
                score = 100 - 5;
            } else if (packageResult.getReturnSum() >= 10 && packageResult.getReturnSum() < 50) {
                score = 100 - 10;
            } else if (packageResult.getReturnSum() >= 50 && packageResult.getReturnSum() < 100) {
                score = 100 - 20;
            } else if (packageResult.getReturnSum() >= 100 && packageResult.getReturnSum() < 200) {
                score = 100 - 30;
            } else if (packageResult.getReturnSum() >= 200 && packageResult.getReturnSum() < 500) {
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

    public void insertHistoryCloudTaskLog(HistoryCloudTaskLog historyCloudTaskLog) throws Exception {
        try {
            historyCloudTaskLogMapper.insertSelective(historyCloudTaskLog);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void insertHistoryCloudTaskResource(HistoryCloudTaskResourceWithBLOBs historyCloudTaskResource) throws Exception {
        try {
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

    public void insertHistoryVulnTask(HistoryVulnTask historyVulnTask) throws Exception {
        try {
            historyVulnTaskMapper.insertSelective(historyVulnTask);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void updateHistoryVulnTask(HistoryVulnTask historyVulnTask) throws Exception {
        try {
            historyVulnTaskMapper.updateByPrimaryKeySelective(historyVulnTask);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void insertHistoryVulnTaskItem(HistoryVulnTaskItemWithBLOBs historyVulnTaskItem) throws Exception {
        try {
            historyVulnTaskItemMapper.insertSelective(historyVulnTaskItem);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void updateHistoryVulnTaskItem(HistoryVulnTaskItemWithBLOBs historyVulnTaskItemWithBLOBs) throws Exception {
        try {
            historyVulnTaskItemMapper.updateByPrimaryKeySelective(historyVulnTaskItemWithBLOBs);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void insertHistoryVulnTaskLog(HistoryVulnTaskLog historyVulnTaskLog) throws Exception {
        try {
            historyVulnTaskLogMapper.insertSelective(historyVulnTaskLog);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void insertHistoryVulnTaskResource(HistoryVulnTaskResourceWithBLOBs historyVulnTaskResource) throws Exception {
        try {
            historyVulnTaskResourceMapper.insertSelective(historyVulnTaskResource);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void updateHistoryVulnTaskResource(HistoryVulnTaskResourceWithBLOBs historyVulnTaskResource) throws Exception {
        try {
            historyVulnTaskResourceMapper.updateByPrimaryKeySelective(historyVulnTaskResource);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void insertHistoryServerTask(HistoryServerTask record) {
        historyServerTaskMapper.insertSelective(record);
    }

    public void updateHistoryServerTask(HistoryServerTask record) {
        historyServerTaskMapper.updateByPrimaryKeySelective(record);
    }

    public void insertHistoryServerTaskLog(HistoryServerTaskLog record) {
        historyServerTaskLogMapper.insertSelective(record);
    }

    public void insertHistoryPackageTask(HistoryPackageTaskWithBLOBs record) {
        historyPackageTaskMapper.insertSelective(record);
    }

    public void updateHistoryPackageTask(HistoryPackageTaskWithBLOBs record) {
        historyPackageTaskMapper.updateByPrimaryKeySelective(record);
    }

    public void insertHistoryPackageTaskItem(HistoryPackageTaskItem record) {
        historyPackageTaskItemMapper.insertSelective(record);
    }

    public void insertHistoryPackageTaskLog(HistoryPackageTaskLog record) {
        historyPackageTaskLogMapper.insertSelective(record);
    }

    public void insertHistoryImageTask(HistoryImageTaskWithBLOBs record) {
        historyImageTaskMapper.insertSelective(record);
    }

    public void updateHistoryImageTask(HistoryImageTaskWithBLOBs record) {
        historyImageTaskMapper.updateByPrimaryKeySelective(record);
    }

    public void insertHistoryImageTaskItem(HistoryImageTaskItem record) {
        historyImageTaskItemMapper.insertSelective(record);
    }

    public void insertHistoryImageTaskLog(HistoryImageTaskLog record) {
        historyImageTaskLogMapper.insertSelective(record);
    }

}
