package com.hummerrisk.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hummer.quartz.service.QuartzManageService;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.domain.Package;
import com.hummerrisk.base.mapper.*;
import com.hummerrisk.base.mapper.ext.ExtCloudTaskMapper;
import com.hummerrisk.base.mapper.ext.ExtResourceMapper;
import com.hummerrisk.commons.constants.*;
import com.hummerrisk.commons.exception.HRException;
import com.hummerrisk.commons.utils.*;
import com.hummerrisk.dto.CloudTaskCopyDTO;
import com.hummerrisk.dto.CloudTaskDTO;
import com.hummerrisk.dto.CloudTaskItemLogDTO;
import com.hummerrisk.dto.QuartzTaskDTO;
import com.hummerrisk.i18n.Translator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

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
    private ExtCloudTaskMapper extCloudTaskMapper;
    @Resource @Lazy
    private CloudTaskItemLogMapper cloudTaskItemLogMapper;
    @Resource @Lazy
    private CommonThreadPool commonThreadPool;
    @Resource @Lazy
    private CloudTaskItemResourceMapper cloudTaskItemResourceMapper;
    @Resource @Lazy
    private ResourceMapper resourceMapper;
    @Resource @Lazy
    private AccountMapper accountMapper;
    @Resource @Lazy
    private QuartzManageService quartzManageService;
    @Resource @Lazy
    private RuleMapper ruleMapper;
    @Resource @Lazy
    private ResourceRuleMapper resourceRuleMapper;
    @Resource @Lazy
    private RuleTagMappingMapper ruleTagMappingMapper;
    @Resource @Lazy
    private ExtResourceMapper extResourceMapper;
    @Resource @Lazy
    private HistoryScanMapper historyScanMapper;
    @Resource @Lazy
    private HistoryScanTaskMapper historyScanTaskMapper;

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

    public void insertTaskHistory (Object obj, Integer scanId) throws Exception {
        HistoryScan historyScan = historyScanMapper.selectByPrimaryKey(scanId);
        HistoryScanTask historyScanTask = new HistoryScanTask();
        historyScanTask.setScanId(scanId);
        historyScanTask.setTaskId(obj2Result(obj));
        historyScanTask.setAccountId(historyScan.getAccountId());
        historyScanTask.setAccountType(historyScan.getAccountType());
        historyScanTask.setStatus(TaskConstants.TASK_STATUS.APPROVED.name());
        historyScanTask.setOperation("i18n_create_scan_history");
        historyScanTaskMapper.insertSelective(historyScanTask);
    }

    public void updateTaskHistory (Object obj, HistoryScanTask historyScanTask) throws Exception {
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

            } else if(StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.imageAccount.getType())) {

            } else if(StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.packageAccount.getType())) {

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
            if (obj.getClass() == (new Account()).getClass() || obj.getClass() == (new AccountWithBLOBs()).getClass()) {
                accountId = ((Account) obj).getId();
                if (PlatformUtils.isSupportCloudAccount(((Account) obj).getPluginId())) {
                    map.put("accountType", TaskEnum.cloudAccount.getType());
                } else {
                    map.put("accountType", TaskEnum.vulnAccount.getType());
                }
            } else if(obj.getClass() == (new Server()).getClass()) {
                accountId = ((Server) obj).getId();
                map.put("accountType", TaskEnum.serverAccount.getType());
            } else if(obj.getClass() == (new Image()).getClass()) {
                accountId = ((Image) obj).getId();
                map.put("accountType", TaskEnum.imageAccount.getType());
            } else if(obj.getClass() == (new Package()).getClass()) {
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
            if (obj.getClass() == (new CloudTask()).getClass()) {
                resultId = ((CloudTask) obj).getId();
            } else if(obj.getClass() == (new ServerResult()).getClass()) {
                resultId = ((ServerResult) obj).getId();
            } else if(obj.getClass() == (new ImageResult()).getClass()) {
                resultId = ((ImageResult) obj).getId();
            } else if(obj.getClass() == (new PackageResult()).getClass()) {
                resultId = ((PackageResult) obj).getId();
            }
            return resultId;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 高：中：低 = 5 ： 3 ： 2
     * @param account
     * @return
     */
    public Integer calculateScore (Account account, CloudTask cloudTask) {
        Double highResultPercent = Double.valueOf(extResourceMapper.resultPercent(account.getId(), "HighRisk", cloudTask ==null?null: cloudTask.getId())!=null?extResourceMapper.resultPercent(account.getId(), "HighRisk", cloudTask ==null?null: cloudTask.getId()):"0.0");
        Double mediumlResultPercent = Double.valueOf(extResourceMapper.resultPercent(account.getId(), "MediumRisk", cloudTask ==null?null: cloudTask.getId())!=null?extResourceMapper.resultPercent(account.getId(), "MediumRisk", cloudTask ==null?null: cloudTask.getId()): "0.0");
        Double lowResultPercent = Double.valueOf(extResourceMapper.resultPercent(account.getId(), "LowRisk", cloudTask ==null?null: cloudTask.getId())!=null?extResourceMapper.resultPercent(account.getId(), "LowRisk", cloudTask ==null?null: cloudTask.getId()):"0.0");

        CloudTaskExample example = new CloudTaskExample();
        CloudTaskExample.Criteria criteria = example.createCriteria();
        criteria.andAccountIdEqualTo(account.getId()).andSeverityEqualTo("HighRisk");
        long high = cloudTaskMapper.countByExample(example);
        criteria.andSeverityEqualTo("MediumRisk");
        long mediuml = cloudTaskMapper.countByExample(example);
        criteria.andSeverityEqualTo("LowRisk");
        long low = cloudTaskMapper.countByExample(example);

        long sum = 5 * high + 3 * mediuml + 2 * low;
        return 100 - (int) Math.ceil(highResultPercent * (5 * high / (sum == 0 ? 1 : sum) ) * 100 + mediumlResultPercent * (3 * mediuml / (sum == 0 ? 1 : sum) ) * 100 + lowResultPercent * (2 * low / (sum == 0 ? 1 : sum) ) * 100);
    }



}
