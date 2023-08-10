package com.hummer.cloud.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.aliyuncs.exceptions.ClientException;
import com.hummer.cloud.mapper.*;
import com.hummer.cloud.mapper.ext.ExtAccountMapper;
import com.hummer.common.core.constant.CloudAccountConstants;
import com.hummer.common.core.constant.ResourceOperation;
import com.hummer.common.core.constant.ResourceTypeConstants;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.account.CloudAccountRequest;
import com.hummer.common.core.domain.request.account.CreateCloudAccountRequest;
import com.hummer.common.core.domain.request.account.UpdateCloudAccountRequest;
import com.hummer.common.core.dto.AccountDTO;
import com.hummer.common.core.dto.QuartzTaskDTO;
import com.hummer.common.core.dto.RuleDTO;
import com.hummer.common.core.dto.ValidateDTO;
import com.hummer.common.core.exception.HRException;
import com.hummer.common.core.i18n.Translator;
import com.hummer.common.core.utils.*;
import com.hummer.system.api.IOperationLogService;
import com.hummer.system.api.model.LoginUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
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
public class AccountService {

    @Autowired
    private ExtAccountMapper extAccountMapper;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private PluginMapper pluginMapper;
    @Autowired
    private RuleAccountParameterMapper ruleAccountParameterMapper;
    @Autowired
    private ProxyMapper proxyMapper;
    @Autowired
    private RuleMapper ruleMapper;
    @Autowired @Lazy
    private CloudSyncService cloudSyncService;
    @Autowired
    private CloudEventSyncLogMapper cloudEventSyncLogMapper;
    @Autowired
    private CloudEventMapper cloudEventMapper;
    @Autowired @Lazy
    private CloudEventService cloudEventService;
    @Autowired @Lazy
    private OssService ossService;
    @DubboReference
    private IOperationLogService operationLogService;

    public List<AccountDTO> getCloudAccountList(CloudAccountRequest request) {
        return extAccountMapper.getCloudAccountList(request);
    }

    public List<Account> listByGroup(String pluginId) {
        AccountExample example = new AccountExample();
        example.createCriteria().andPluginIdEqualTo(pluginId).andStatusEqualTo("VALID");
        List<Account> accounts = accountMapper.selectByExample(example);
        return accounts;
    }

    public AccountWithBLOBs getAccount(String id) {
        return accountMapper.selectByPrimaryKey(id);
    }

    public List<ValidateDTO> validate(List<String> ids) {
        List<ValidateDTO> list = new ArrayList<>();
        ids.forEach(id -> {
            try {
                AccountWithBLOBs account = accountMapper.selectByPrimaryKey(id);
                ValidateDTO validate = validateAccount(account);
                if (validate.isFlag()) {
                    account.setStatus(CloudAccountConstants.Status.VALID.name());
                } else {
                    account.setStatus(CloudAccountConstants.Status.INVALID.name());
                    list.add(validate);
                }
                accountMapper.updateByPrimaryKeySelective(account);
            } catch (Exception e) {
                throw new HRException(Translator.get("failed_cloud_account") + e.getMessage());
            }
        });
        return list;
    }


    public ValidateDTO validate(String id) {
        AccountWithBLOBs account = accountMapper.selectByPrimaryKey(id);
        //检验账号的有效性
        ValidateDTO valid = validateAccount(account);
        if (valid.isFlag()) {
            account.setStatus(CloudAccountConstants.Status.VALID.name());
        } else {
            account.setStatus(CloudAccountConstants.Status.INVALID.name());
        }
        accountMapper.updateByPrimaryKeySelective(account);
        return valid;
    }

    private ValidateDTO validateAccount(AccountWithBLOBs account) {
        ValidateDTO validateDTO = new ValidateDTO();
        validateDTO.setName(account.getName());
        try {
            Proxy proxy = new Proxy();
            if (account.getProxyId() != null) proxy = proxyMapper.selectByPrimaryKey(account.getProxyId());
            validateDTO.setFlag(PlatformUtils.validateCredential(account, proxy));
            validateDTO.setMessage(String.format("Verification cloud account status: [%s], cloud account: [%s], plugin: [%s]", validateDTO.isFlag(), account.getName(), account.getPluginName()));
            return validateDTO;
        } catch (Exception e) {
            validateDTO.setMessage(String.format("HRException in verifying cloud account, cloud account: [%s], plugin: [%s], error information:%s", account.getName(), account.getPluginName(), e.getMessage()));
            validateDTO.setFlag(false);
            LogUtil.error(String.format("HRException in verifying cloud account, cloud account: [%s], plugin: [%s], error information:%s", account.getName(), account.getPluginName(), e.getMessage()), e);
            return validateDTO;
        }
    }

    public AccountWithBLOBs addAccount(CreateCloudAccountRequest request, LoginUser loginUser) throws HRException {
        try {
            //参数校验
            if (StringUtils.isEmpty(request.getCredential()) || StringUtils.isEmpty(request.getName()) || StringUtils.isEmpty(request.getPluginId())) {
                HRException.throwException(Translator.get("i18n_ex_cloud_account_name_or_plugin"));
            }

            //云账号名称不能重复
            AccountExample accountExample = new AccountExample();
            accountExample.createCriteria().andNameEqualTo(request.getName());
            List<AccountWithBLOBs> accountList = accountMapper.selectByExampleWithBLOBs(accountExample);
            if (!CollectionUtils.isEmpty(accountList)) {
                HRException.throwException(Translator.get("i18n_ex_cloud_account_name_duplicate"));
            }

            AccountWithBLOBs account = new AccountWithBLOBs();

            //校验云插件是否存在
            Plugin plugin = pluginMapper.selectByPrimaryKey(request.getPluginId());
            if (plugin == null) {
                HRException.throwException(Translator.get("i18n_ex_cloud_account_no_exist_plugin"));
            } else {
                BeanUtils.copyBean(account, request);

                account.setPluginIcon(Objects.requireNonNull(plugin.getIcon()));
                account.setPluginName(plugin.getName());
                account.setCreateTime(System.currentTimeMillis());
                account.setUpdateTime(System.currentTimeMillis());
                account.setCreator(Objects.requireNonNull(loginUser.getUser()).getId());
                account.setId(UUIDUtil.newUUID());

                ValidateDTO validate = validateAccount(account);
                if (validate.isFlag()) {
                    account.setStatus(CloudAccountConstants.Status.VALID.name());
                } else {
                    account.setStatus(CloudAccountConstants.Status.INVALID.name());
                }

                accountMapper.insertSelective(account);
                updateRegionsThrows(account);
                operationLogService.log(loginUser, account.getId(), account.getName(), ResourceTypeConstants.CLOUD_ACCOUNT.name(), ResourceOperation.CREATE, "i18n_create_cloud_account");
                if (validate.isFlag())
                    cloudSyncService.sync(account.getId(), loginUser);

                if (request.isCreateLog()) {
                    try {
                        JSONArray arr = JSONArray.parseArray(account.getCheckRegions());
                        if (arr.size() == 0) arr = JSONArray.parseArray(account.getRegions());
                        if (arr.size() > 0) {
                            String[] regions = new String[arr.size()];
                            int i = 0;
                            for (Object o : arr) {
                                JSONObject jsonObject = JSONObject.parseObject(o.toString());
                                String regionId = jsonObject.getString("regionId");
                                regions[i] = regionId;
                                i++;
                            }
                            SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
                            sdf.applyPattern("yyyy-MM-dd HH:mm:ss");//
                            Date date = new Date();// 获取当前时间
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            calendar.add(Calendar.WEEK_OF_YEAR, -1);//一周前
                            Date weekDate = calendar.getTime();

                            String startTime = sdf.format(weekDate);
                            String endTime = sdf.format(date);
                            cloudEventService.syncCloudEvents(account.getId(), regions, startTime, endTime);
                        }
                    } catch (Exception e1) {
                        LogUtil.error(e1.getMessage());
                    }

                    if (request.isCreateOss()) {
                        try {
                            OssWithBLOBs oss = new OssWithBLOBs();
                            BeanUtils.copyBean(oss, account);
                            oss.setAccountId(account.getId());
                            ossService.addOss(oss, loginUser);
                        } catch (Exception e1) {
                            LogUtil.error(e1.getMessage());
                        }
                    }

                }

                return getCloudAccountById(account.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            HRException.throwException(e.getMessage());
        }
        return null;
    }

    private AccountWithBLOBs getCloudAccountById(String id) {
        return accountMapper.selectByPrimaryKey(id);
    }

    public AccountWithBLOBs editAccount(UpdateCloudAccountRequest request, LoginUser loginUser) throws Exception {
        try {
            //参数校验
            if (StringUtils.isEmpty(request.getCredential())
                    || StringUtils.isEmpty(request.getId())) {
                HRException.throwException(Translator.get("i18n_ex_cloud_account_id_or_plugin"));
            }

            //云账号名称不能重复
            AccountExample accountExample = new AccountExample();
            accountExample.createCriteria().andNameEqualTo(request.getName()).andIdNotEqualTo(request.getId());
            List<AccountWithBLOBs> accountList = accountMapper.selectByExampleWithBLOBs(accountExample);
            if (!CollectionUtils.isEmpty(accountList)) {
                HRException.throwException(Translator.get("i18n_ex_cloud_account_name_duplicate"));
            }

            if (accountMapper.selectByPrimaryKey(request.getId()) == null) {
                HRException.throwException(Translator.get("i18n_ex_cloud_account_no_exist_id"));
            }

            AccountWithBLOBs account = new AccountWithBLOBs();
            //校验云插件是否存在
            Plugin plugin = pluginMapper.selectByPrimaryKey(request.getPluginId());
            if (plugin == null) {
                HRException.throwException(Translator.get("i18n_ex_cloud_account_no_exist_plugin"));
            } else {
                BeanUtils.copyBean(account, request);

                account.setPluginIcon(plugin.getIcon());
                account.setPluginName(plugin.getName());
                account.setUpdateTime(System.currentTimeMillis());
                accountMapper.updateByPrimaryKeySelective(account);

                ValidateDTO validate = validateAccount(account);
                if (validate.isFlag()) {
                    account.setStatus(CloudAccountConstants.Status.VALID.name());
                } else {
                    account.setStatus(CloudAccountConstants.Status.INVALID.name());
                }

                account = accountMapper.selectByPrimaryKey(account.getId());

                updateRegionsThrows(account);

                //检验账号已更新状态
                operationLogService.log(loginUser, account.getId(), account.getName(), ResourceTypeConstants.CLOUD_ACCOUNT.name(), ResourceOperation.UPDATE, "i18n_update_cloud_account");
                if (validate.isFlag()) cloudSyncService.sync(account.getId(), loginUser);
                return getCloudAccountById(account.getId());
            }

        } catch (Exception e) {
            e.printStackTrace();
            HRException.throwException(e.getMessage());
        }
        return null;
    }

    public void delete(String accountId, LoginUser loginUser) {
        AccountWithBLOBs accountWithBLOBs = accountMapper.selectByPrimaryKey(accountId);
        accountMapper.deleteByPrimaryKey(accountId);
        CloudEventExample cloudEventExample = new CloudEventExample();
        cloudEventExample.createCriteria().andCloudAccountIdEqualTo(accountId);
        cloudEventMapper.deleteByExample(cloudEventExample);
        CloudEventSyncLogExample cloudEventSyncLogExample = new CloudEventSyncLogExample();
        cloudEventSyncLogExample.createCriteria().andAccountIdEqualTo(accountId);
        cloudEventSyncLogMapper.deleteByExample(cloudEventSyncLogExample);
        operationLogService.log(loginUser, accountId, accountWithBLOBs.getName(), ResourceTypeConstants.CLOUD_ACCOUNT.name(), ResourceOperation.DELETE, "i18n_delete_cloud_account");
    }

    public void deletes(List<String> ids, LoginUser loginUser) throws Exception {
        ids.forEach(id -> {
            try {
                delete(id, loginUser);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    public Object getRegions(String id) {
        try {
            ValidateDTO flag = validate(id);
            if (!flag.isFlag()) {
                HRException.throwException(Translator.get("i18n_ex_plugin_validate"));
            }
            AccountWithBLOBs account = accountMapper.selectByPrimaryKey(id);
            String regions = account.getRegions();
            if (regions.isEmpty()) {
                Proxy proxy = new Proxy();
                if (account.getProxyId() != null) proxy = proxyMapper.selectByPrimaryKey(account.getProxyId());
                return PlatformUtils._getRegions(account, proxy, flag.isFlag());
            } else {
                return regions;
            }
        } catch (Exception e) {
            throw new HRException(e.getMessage());
        }
    }

    public void syncRegions() {
        try {
            List<AccountWithBLOBs> list = accountMapper.selectByExampleWithBLOBs(null);
            list.forEach(account -> {
                try {
                    updateRegions(account);
                } catch (ClientException e) {
                    LogUtil.error(e.getMessage());
                }
            });
        } catch (Exception e) {
            HRException.throwException(e.getMessage());
        }
    }

    public void updateRegions(AccountWithBLOBs account) throws ClientException {
        try {
            Proxy proxy = new Proxy();
            if (account.getProxyId() != null) proxy = proxyMapper.selectByPrimaryKey(account.getProxyId());
            JSONArray jsonArray = PlatformUtils._getRegions(account, proxy, validate(account.getId()).isFlag());
            if (!jsonArray.isEmpty()) {
                account.setRegions(jsonArray.toJSONString());
                accountMapper.updateByPrimaryKeySelective(account);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
    }

    public void updateRegionsThrows(AccountWithBLOBs account) throws Exception {
        try {
            Proxy proxy = new Proxy();
            if (account.getProxyId() != null) proxy = proxyMapper.selectByPrimaryKey(account.getProxyId());
            JSONArray jsonArray = PlatformUtils._getRegions(account, proxy, validate(account.getId()).isFlag());
            if (!jsonArray.isEmpty()) {
                account.setRegions(jsonArray.toJSONString());
                if (StringUtils.isEmpty(account.getCheckRegions())) account.setCheckRegions(jsonArray.toJSONString());
                accountMapper.updateByPrimaryKeySelective(account);
            }
        } catch (Exception e) {
            account.setRegions("[]");
            if (StringUtils.isEmpty(account.getCheckRegions())) account.setCheckRegions("[]");
            accountMapper.updateByPrimaryKeySelective(account);
            HRException.throwException(e.getMessage());
        }
    }

    public String string2PrettyFormat(String regions) {
        StringBuilder stringBuffer = new StringBuilder();
        JSONArray jsonArray = parseArray(regions);
        String pretty = JSON.toJSONString(jsonArray, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
        stringBuffer.append(pretty);
        return stringBuffer.toString();
    }

    public boolean cleanParameter(List<RuleAccountParameter> list, LoginUser loginUser) {
        try {
            list.forEach(rule -> {
                if (rule.getRuleId() != null) {
                    RuleAccountParameterExample example = new RuleAccountParameterExample();
                    example.createCriteria().andRuleIdEqualTo(rule.getRuleId()).andAccountIdEqualTo(rule.getAccountId());
                    ruleAccountParameterMapper.deleteByExample(example);
                }
            });
            operationLogService.log(loginUser, list.get(0).getRuleId(), accountMapper.selectByPrimaryKey(list.get(0).getAccountId()).getName(), ResourceTypeConstants.CLOUD_ACCOUNT.name(), ResourceOperation.CREATE, " i18n_clean_cloud_account");
        } catch (Exception e) {
            throw e;
        }
        return true;
    }

    public boolean saveParameter(List<QuartzTaskDTO> list, LoginUser loginUser) {
        try {
            list.forEach(this::accept);
            operationLogService.log(loginUser, list.get(0).getId(), accountMapper.selectByPrimaryKey(list.get(0).getAccountId()).getName(), ResourceTypeConstants.CLOUD_ACCOUNT.name(), ResourceOperation.CREATE, "i18n_save_cloud_account");
        } catch (Exception e) {
            throw e;
        }
        return true;
    }

    public boolean saveRegions(Map<String, String> map, LoginUser loginUser) {
        try {
            AccountWithBLOBs account = accountMapper.selectByPrimaryKey(map.get("accountId"));

            RuleExample example = new RuleExample();
            example.createCriteria().andPluginIdEqualTo(account.getPluginId());
            List<Rule> list = ruleMapper.selectByExample(example);

            for (Rule rule : list) {
                RuleAccountParameter parameter = new RuleAccountParameter();
                parameter.setAccountId(account.getId());
                parameter.setRuleId(rule.getId());
                parameter.setRegions(map.get("regions"));

                RuleAccountParameterExample ruleAccountParameterExample = new RuleAccountParameterExample();
                ruleAccountParameterExample.createCriteria().andAccountIdEqualTo(account.getId()).andRuleIdEqualTo(rule.getId());
                List<RuleAccountParameter> parameters = ruleAccountParameterMapper.selectByExample(ruleAccountParameterExample);
                if (!parameters.isEmpty()) {
                    ruleAccountParameterMapper.updateByExampleSelective(parameter, ruleAccountParameterExample);
                } else {
                    parameter.setParameter(rule.getParameter());
                    ruleAccountParameterMapper.insertSelective(parameter);
                }
            }

            operationLogService.log(loginUser, account.getId(), account.getName(), ResourceTypeConstants.CLOUD_ACCOUNT.name(), ResourceOperation.CREATE, "i18n_save_cloud_account");
        } catch (Exception e) {
            throw e;
        }
        return true;
    }

    public List<RuleDTO> getRules(QuartzTaskDTO dto) {
        return extAccountMapper.ruleList(dto);
    }

    public List<Map<String, Object>> groupList(Map<String, Object> params) {
        return extAccountMapper.groupList(params);
    }

    public List<Map<String, Object>> reportList(Map<String, Object> params) {
        return extAccountMapper.reportList(params);
    }

    public List<Map<String, Object>> tagList(Map<String, Object> params) {
        return extAccountMapper.tagList(params);
    }

    public List<Map<String, Object>> regionsList(Map<String, Object> params) {
        return extAccountMapper.regionsList(params);
    }

    public List<Map<String, Object>> resourceList(Map<String, Object> params) {
        return extAccountMapper.resourceList(params);
    }

    public String strategy(String type) throws Exception {
        String script = ReadFileUtils.readConfigFile("support/strategy/", type, ".json");
        try {
            script = this.toJSONString(script);
        } catch (Exception e) {
            script = this.toJSONString2(script);
        }
        return script;
    }

    public String toJSONString(String jsonString) {
        JSONObject object = parseObject(jsonString);
        return JSON.toJSONString(object, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
    }

    public String toJSONString2(String jsonString) {
        JSONArray jsonArray = parseArray(jsonString);
        return JSON.toJSONString(jsonArray, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
    }

    private void accept(QuartzTaskDTO quartzTaskDTO) {
        RuleAccountParameter parameter = new RuleAccountParameter();
        parameter.setAccountId(quartzTaskDTO.getAccountId());
        parameter.setRuleId(quartzTaskDTO.getId());
        parameter.setParameter(quartzTaskDTO.getParameter());
        parameter.setRegions(quartzTaskDTO.getRegions());

        RuleAccountParameterExample example = new RuleAccountParameterExample();
        example.createCriteria().andAccountIdEqualTo(quartzTaskDTO.getAccountId()).andRuleIdEqualTo(quartzTaskDTO.getId());
        List<RuleAccountParameter> parameters = ruleAccountParameterMapper.selectByExample(example);
        if (!parameters.isEmpty()) {
            ruleAccountParameterMapper.updateByExampleSelective(parameter, example);
        } else {
            ruleAccountParameterMapper.insertSelective(parameter);
        }
    }

    public List<Map<String, Object>> historyList(Map<String, Object> params) {
        List<Map<String, Object>> list = extAccountMapper.historyList(params);
        for (Map<String, Object> map : list) {
            if (map.get("rsources") != null) {
                map.put("rsources", toJSONString2(map.get("rsources").toString()));
            }
        }
        return list;
    }

    public List<Map<String, Object>> historyDiffList(Map<String, Object> params) {
        List<Map<String, Object>> list = extAccountMapper.historyDiffList(params);
        for (Map<String, Object> map : list) {
            if (map.get("rsources") != null) {
                map.put("rsources", toJSONString2(map.get("rsources").toString()));
            }
        }
        return list;
    }

    public void checkRegions(AccountWithBLOBs accountWithBLOBs, LoginUser loginUser) throws Exception {
        accountMapper.updateByPrimaryKeySelective(accountWithBLOBs);
        Map<String, String> map = new HashMap<>();
        map.put("accountId", accountWithBLOBs.getId());
        map.put("regions", accountWithBLOBs.getCheckRegions());
        saveRegions(map, loginUser);
    }

}
