package com.hummer.cloud.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hummer.cloud.mapper.*;
import com.hummer.cloud.mapper.ext.ExtCloudTaskMapper;
import com.hummer.common.core.constant.*;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.cloudTask.ManualRequest;
import com.hummer.common.core.dto.*;
import com.hummer.common.core.exception.HRException;
import com.hummer.common.core.i18n.Translator;
import com.hummer.common.core.utils.*;
import com.hummer.common.security.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.util.*;
import java.util.stream.Collectors;

import static com.alibaba.fastjson.JSON.parseArray;

/**
 * @author harris
 */
@Service
public class CloudTaskService {

    @Autowired
    private CloudTaskMapper cloudTaskMapper;
    @Autowired
    private CloudTaskItemMapper cloudTaskItemMapper;
    @Autowired
    private ExtCloudTaskMapper extCloudTaskMapper;
    @Autowired
    private CloudTaskItemLogMapper cloudTaskItemLogMapper;
    @Autowired
    private CloudTaskItemResourceMapper cloudTaskItemResourceMapper;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ResourceRuleMapper resourceRuleMapper;
    @Autowired
    private ResourceMapper resourceMapper;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ResourceItemMapper resourceItemMapper;
    @Autowired
    private ProxyMapper proxyMapper;
    @Autowired
    private RuleMapper ruleMapper;
    @Autowired
    private ProwlerService prowlerService;
    @Autowired
    private TokenService tokenService;

    public CloudTask saveManualTask(QuartzTaskDTO quartzTaskDTO, String messageOrderId) {
        try {
            if (StringUtils.equalsIgnoreCase(quartzTaskDTO.getScanType(), ScanTypeConstants.custodian.name())) {
                this.validateYaml(quartzTaskDTO);
                return orderService.createTask(quartzTaskDTO, CloudTaskConstants.TASK_STATUS.APPROVED.name(), messageOrderId);
            } else if (StringUtils.equalsIgnoreCase(quartzTaskDTO.getScanType(), ScanTypeConstants.prowler.name())) {
                return prowlerService.createTask(quartzTaskDTO, CloudTaskConstants.TASK_STATUS.APPROVED.name(), messageOrderId);
            } else {
                return orderService.createTask(quartzTaskDTO, CloudTaskConstants.TASK_STATUS.APPROVED.name(), messageOrderId);
            }
        } catch (Exception e) {
            throw new HRException(e.getMessage());
        }
    }

    public boolean morelTask(String taskId) {
        try {
            CloudTask cloudTask = cloudTaskMapper.selectByPrimaryKey(taskId);
            if (cloudTask != null) {
                cloudTask.setStatus(CloudTaskConstants.TASK_STATUS.APPROVED.name());
                cloudTaskMapper.updateByPrimaryKeySelective(cloudTask);
            } else {
                HRException.throwException("CloudTask not found");
            }
        } catch (Exception e) {
            throw e;
        }
        return true;
    }

    public boolean validateYaml(QuartzTaskDTO quartzTaskDTO) {
        try {
            String script = quartzTaskDTO.getScript();
            JSONArray jsonArray = JSON.parseArray(quartzTaskDTO.getParameter());
            for (Object o : jsonArray) {
                JSONObject jsonObject = (JSONObject) o;
                String key = "${{" + jsonObject.getString("key") + "}}";
                if (script.contains(key)) {
                    script = script.replace(key, jsonObject.getString("defaultValue"));
                }
            }
            Yaml yaml = new Yaml();
            Map map = yaml.load(script);
            if (map != null) {
                List<Map> list = (List) map.get("policies");
                for (Map m : list) m.get("resource").toString();
            }
        } catch (Exception e) {
            HRException.throwException(Translator.get("i18n_compliance_rule_code_error"));
        }
        return true;
    }

    public void deleteManualTask(String taskId) {
        CloudTask cloudTask = cloudTaskMapper.selectByPrimaryKey(taskId);
        CloudTaskItemExample cloudTaskItemExample = new CloudTaskItemExample();
        cloudTaskItemExample.createCriteria().andTaskIdEqualTo(cloudTask.getId());
        List<CloudTaskItem> cloudTaskItemList = cloudTaskItemMapper.selectByExample(cloudTaskItemExample);
        try {
            cloudTaskItemList.forEach(taskItem -> {
                if (taskItem == null) return;
                cloudTaskItemMapper.deleteByPrimaryKey(taskItem.getId());

                CloudTaskItemLogExample cloudTaskItemLogExample = new CloudTaskItemLogExample();
                cloudTaskItemLogExample.createCriteria().andTaskItemIdEqualTo(taskItem.getId());
                cloudTaskItemLogMapper.deleteByExample(cloudTaskItemLogExample);

                CloudTaskItemResourceExample cloudTaskItemResourceExample = new CloudTaskItemResourceExample();
                cloudTaskItemResourceExample.createCriteria().andTaskItemIdEqualTo(taskItem.getId());
                List<CloudTaskItemResource> cloudTaskItemResources = cloudTaskItemResourceMapper.selectByExample(cloudTaskItemResourceExample);
                cloudTaskItemResourceMapper.deleteByExample(cloudTaskItemResourceExample);
                cloudTaskItemResources.forEach(taskItemResource -> {
                    if (taskItemResource == null) return;
                    resourceMapper.deleteByPrimaryKey(taskItemResource.getResourceId());

                    if (taskItemResource.getResourceId() != null) {
                        ResourceItemExample resourceItemExample = new ResourceItemExample();
                        resourceItemExample.createCriteria().andResourceIdEqualTo(taskItemResource.getResourceId());
                        List<ResourceItem> resourceItems = resourceItemMapper.selectByExample(resourceItemExample);
                        resourceItems.forEach(resourceItem -> {
                            ResourceRuleExample resourceRuleExample = new ResourceRuleExample();
                            if (resourceItem.getResourceId() != null) {
                                resourceRuleExample.createCriteria().andResourceIdEqualTo(resourceItem.getResourceId());
                                resourceRuleMapper.deleteByExample(resourceRuleExample);
                            }
                        });
                    }
                });

            });
            cloudTaskMapper.deleteByPrimaryKey(cloudTask.getId());
            OperationLogService.log(tokenService.getLoginUser().getUser(), taskId, cloudTask.getDescription(), ResourceTypeConstants.TASK.name(), ResourceOperation.DELETE, "i18n_delete_cloud_task");
        } catch (Exception e) {
            LogUtil.error("Delete manual cloudTask error{} " + e.getMessage());
            HRException.throwException(e.getMessage());
        }
    }

    public boolean dryRun(QuartzTaskDTO quartzTaskDTO) {
        //validate && dryrun
        String uuid = UUIDUtil.newUUID();
        try {
            String script = quartzTaskDTO.getScript();
            JSONArray jsonArray = JSON.parseArray(quartzTaskDTO.getParameter());
            String groupName = "xss";
            for (Object o : jsonArray) {
                JSONObject jsonObject = (JSONObject) o;
                String key = "${{" + jsonObject.getString("key") + "}}";
                if (script.contains(key)) {
                    script = script.replace(key, jsonObject.getString("defaultValue"));
                }
                groupName = jsonObject.getString("defaultValue");
            }
            final String finalScript = script;
            String dirPath;
            AccountExample example = new AccountExample();
            example.createCriteria().andPluginIdEqualTo(quartzTaskDTO.getPluginId()).andStatusEqualTo("VALID");
            AccountWithBLOBs account = accountMapper.selectByExampleWithBLOBs(example).get(0);
            Proxy proxy = new Proxy();
            if (account.getProxyId() != null) proxy = proxyMapper.selectByPrimaryKey(account.getProxyId());
            // 校验云账号是否有效
            Optional.ofNullable(accountService.validate(account.getId()).isFlag()).filter(Boolean::booleanValue).orElseGet(() -> {
                HRException.throwException(Translator.get("i18n_ex_plugin_validate"));
                return null;
            });
            JSONObject regionObj = Optional.ofNullable(PlatformUtils._getRegions(account, proxy, true)).filter(s -> {
                return !s.isEmpty();
            }).map(jsonArr -> {
                return (JSONObject) jsonArr.get(0);
            }).orElseGet(() -> {
                HRException.throwException(Translator.get("i18n_ex_plugin_validate"));
                return null;
            });

            Map<String, String> map = PlatformUtils.getAccount(account, regionObj.get("regionId").toString(), proxyMapper.selectByPrimaryKey(account.getProxyId()));

            String fileName = "", commandEnum = "";
            if (StringUtils.equalsIgnoreCase(quartzTaskDTO.getScanType(), ScanTypeConstants.custodian.name())) {
                fileName = "policy.yml";
                commandEnum = CommandEnum.custodian.getCommand();
            } else if (StringUtils.equalsIgnoreCase(quartzTaskDTO.getScanType(), ScanTypeConstants.prowler.name())) {
                JSONArray objects = JSONObject.parseArray(quartzTaskDTO.getParameter());
                if (objects.isEmpty()) HRException.throwException(Translator.get("error_lang_invalid"));
                fileName = objects.getJSONObject(0).getString("defaultValue");
                commandEnum = CommandEnum.prowler.getCommand();
            }

            dirPath = commandEnum.equals(CommandEnum.prowler.getCommand()) ? CloudTaskConstants.PROWLER_RESULT_FILE_PATH : CommandUtils.saveAsFile(finalScript, CloudTaskConstants.RESULT_FILE_PATH_PREFIX + uuid, fileName, false);

            String command = PlatformUtils.fixedCommand(commandEnum, CommandEnum.validate.getCommand(), dirPath, fileName, map);

            String resultStr = CommandUtils.commonExecCmdWithResult(command, dirPath);
            // 检查结果
            checkResultStr(resultStr, quartzTaskDTO.getScanType());
            String command2 = PlatformUtils.fixedCommand(commandEnum, CommandEnum.dryrun.getCommand(), dirPath, fileName, map);
            String resultStr2 = CommandUtils.commonExecCmdWithResult(command2, dirPath);
            // 结果
            checkResultStr(resultStr2, quartzTaskDTO.getScanType());
        } catch (Exception e) {
            LogUtil.error("[{}] validate && dryrun generate policy.yml file failed:{}", uuid, e.getMessage());
            HRException.throwException(e instanceof HRException ? e.getMessage() : Translator.get("i18n_compliance_rule_error"));
        }
        return true;
    }

    /**
     * 检查返回值是否正常返回
     *
     * @param resultStr 需要检查的结果
     * @param type      检测类型
     */
    public void checkResultStr(String resultStr, String type) {
        if (type.equals(ScanTypeConstants.custodian.name())) {
            if (!resultStr.isEmpty() && !resultStr.contains("INFO")) {
                LogUtil.error(Translator.get("i18n_has_resource_failed") + " {validate}:" + resultStr);
                HRException.throwException(Translator.get("i18n_has_resource_failed"));
            }
        } else if (type.equals(ScanTypeConstants.prowler.name())) {
            if (!resultStr.isEmpty() && !resultStr.contains("INFO")) {
                LogUtil.error(Translator.get("i18n_has_resource_failed") + " {validate}:" + resultStr);
                HRException.throwException(Translator.get("i18n_has_resource_failed"));
            }
        }
    }

    public List<CloudTask> selectManualTasks(ManualRequest request) throws Exception {
        try {
            return extCloudTaskMapper.selectManualTasks(request);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    public boolean checkRuleTaskStatus(String accountId, String ruleId, String[] status) {
        CloudTaskExample cloudTaskExample = new CloudTaskExample();
        cloudTaskExample.createCriteria().andAccountIdEqualTo(accountId).andRuleIdEqualTo(ruleId).andStatusIn(Arrays.stream(status).collect(Collectors.toList()));
        long count = cloudTaskMapper.countByExample(cloudTaskExample);
        return count > 0;
    }

    public void syncTaskSum() {
        CloudTaskExample example = new CloudTaskExample();
        example.createCriteria().andStatusIn(Arrays.asList(CloudTaskConstants.TASK_STATUS.FINISHED.name(), CloudTaskConstants.TASK_STATUS.RUNNING.name()));
        List<CloudTask> cloudTasks = cloudTaskMapper.selectByExample(example);
        cloudTasks.forEach(task -> {
            if (task.getResourcesSum() != null && task.getReturnSum() != null) {
                int resourceSum = extCloudTaskMapper.getResourceSum(task.getId());
                int returnSum = extCloudTaskMapper.getReturnSum(task.getId());
                task.setResourcesSum((long) resourceSum);
                task.setReturnSum((long) returnSum);
                cloudTaskMapper.updateByPrimaryKeySelective(task);
            }
        });
    }


}
