package com.hummerrisk.oss.service;

import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.AccountMapper;
import com.hummerrisk.base.mapper.OssBucketMapper;
import com.hummerrisk.base.mapper.OssLogMapper;
import com.hummerrisk.base.mapper.OssMapper;
import com.hummerrisk.base.mapper.ext.ExtOssMapper;
import com.hummerrisk.commons.constants.CloudAccountConstants;
import com.hummerrisk.commons.constants.CloudTaskConstants;
import com.hummerrisk.commons.constants.ResourceTypeConstants;
import com.hummerrisk.commons.exception.HRException;
import com.hummerrisk.commons.utils.*;
import com.hummerrisk.i18n.Translator;
import com.hummerrisk.oss.config.OssManager;
import com.hummerrisk.oss.constants.OSSConstants;
import com.hummerrisk.oss.controller.request.OssRequest;
import com.hummerrisk.oss.dto.BucketObjectDTO;
import com.hummerrisk.oss.dto.OssBucketDTO;
import com.hummerrisk.oss.dto.OssDTO;
import com.hummerrisk.oss.provider.OssProvider;
import com.hummerrisk.service.AccountService;
import com.hummerrisk.service.OperationLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OssService {

    @Resource
    private OssMapper ossMapper;
    @Resource
    private AccountService accountService;
    @Resource
    private AccountMapper accountMapper;
    @Resource
    private ExtOssMapper extOssMapper;
    @Resource
    private OssBucketMapper ossBucketMapper;
    @Resource
    private CommonThreadPool commonThreadPool;
    @Resource
    private OssLogMapper ossLogMapper;

    private static final String BASE_CREDENTIAL_DIC = "support/credential/";
    private static final String JSON_EXTENSION = ".json";

    public List<OssDTO> ossList(OssRequest request) {
        return extOssMapper.ossList(request);
    }

    public List<OssBucketDTO> ossBucketList(OssRequest request) {
        return extOssMapper.ossBucketList(request);
    }

    public List<AccountWithBLOBs> getCloudAccountList() {
        AccountExample example = new AccountExample();
        List<String> values = new ArrayList<>();
        values.add(OSSConstants.aws);
        values.add(OSSConstants.aliyun);
        values.add(OSSConstants.baidu);
        values.add(OSSConstants.huawei);
        values.add(OSSConstants.huoshan);
        values.add(OSSConstants.tencent);
        values.add(OSSConstants.qingcloud);
        values.add(OSSConstants.qiniu);
        values.add(OSSConstants.ucloud);
        example.createCriteria().andStatusEqualTo(CloudAccountConstants.Status.VALID.name()).andPluginIdIn(values);
        return accountMapper.selectByExampleWithBLOBs(example);
    }

    public String strategy(String accountId) throws Exception {
        Account account = accountMapper.selectByPrimaryKey(accountId);
        OssProvider ossProvider = getOssProvider(account.getPluginId());
        String script = ossProvider.policyModel();
        return script;
    }

    public String getCredential(String accountId) {
        Account account = accountMapper.selectByPrimaryKey(accountId);
        try {
            return ReadFileUtils.readConfigFile(BASE_CREDENTIAL_DIC, account.getPluginId(), JSON_EXTENSION);
        } catch (Exception e) {
            LogUtil.error("Error getting credential parameters: " + account.getPluginId(), e);
            HRException.throwException(Translator.get("i18n_ex_plugin_get"));
        }
        return Translator.get("i18n_ex_plugin_get");
    }

    public OssWithBLOBs addOss(OssWithBLOBs request) throws Exception {
        OssWithBLOBs oss = ossMapper.selectByPrimaryKey(request.getId());
        if (oss != null) {
            editOss(request);
        } else {
            request.setCreateTime(System.currentTimeMillis());
            request.setUpdateTime(System.currentTimeMillis());
            request.setCreator(SessionUtils.getUserId());
            request.setStatus(CloudTaskConstants.TASK_STATUS.APPROVED.name());
            ossMapper.insertSelective(request);
        }
        return request;
    }

    public OssWithBLOBs editOss(OssWithBLOBs request) throws Exception {
        request.setCreateTime(System.currentTimeMillis());
        request.setUpdateTime(System.currentTimeMillis());
        request.setCreator(SessionUtils.getUserId());
        request.setStatus(CloudTaskConstants.TASK_STATUS.FINISHED.name());
        ossMapper.updateByPrimaryKeySelective(request);
        return request;
    }

    public void deleteOss(String ossId) {
        ossMapper.deleteByPrimaryKey(ossId);
        OssLogExample example = new OssLogExample();
        example.createCriteria().andOssIdEqualTo(ossId);
        ossLogMapper.deleteByExample(example);
    }

    public List<OssLogWithBLOBs> getLogList(String ossId) {
        OssLogExample example = new OssLogExample();
        example.createCriteria().andOssIdEqualTo(ossId);
        return ossLogMapper.selectByExampleWithBLOBs(example);
    }

    public void batch(String id) throws Exception {
        OssWithBLOBs oss = ossMapper.selectByPrimaryKey(id);
        oss.setStatus(OSSConstants.SYNC_STATUS.APPROVED.name());
        ossMapper.updateByPrimaryKeySelective(oss);
        OssLogExample example = new OssLogExample();
        example.createCriteria().andOssIdEqualTo(id);
        ossLogMapper.deleteByExample(example);
        saveLog(oss.getId(), "i18n_start_oss_sync", "", true, 0);
    }

    public void syncBatch(String id) throws Exception {
        OssWithBLOBs oss = ossMapper.selectByPrimaryKey(id);
        try {
            syncResource(oss);
        } catch (Exception e) {
            oss.setStatus(OSSConstants.SYNC_STATUS.ERROR.name());
            ossMapper.updateByPrimaryKeySelective(oss);
            saveLog(oss.getId(), "i18n_operation_ex" + ": " + e.getMessage(), e.getMessage(), false, 0);
            LogUtil.error(String.format("Failed to synchronize cloud account: %s", oss.getName()), e);
        }
    }

    private void syncResource(OssWithBLOBs oss) throws Exception {
        if (!accountService.validate(oss.getId()).isFlag()) {
            oss.setStatus(OSSConstants.SYNC_STATUS.ERROR.name());
            ossMapper.updateByPrimaryKeySelective(oss);
            saveLog(oss.getId(), "i18n_operation_ex" + ": " + "failed_oss", "failed_oss", false, 0);
            return;
        }
        OperationLogService.log(null, oss.getId(), oss.getName(), ResourceTypeConstants.OSS.name(), OSSConstants.SYNC_STATUS.APPROVED.name(), null);
        ossMapper.updateByPrimaryKeySelective(oss);
        syncBucketInfo(oss);
    }

    public void syncBucketInfo(final OssWithBLOBs oss) throws Exception {
        commonThreadPool.addTask(() -> {
            try {
                doSyncBucketInfo(oss);
            } catch (Exception e) {
                oss.setStatus(OSSConstants.SYNC_STATUS.ERROR.name());
                oss.setUpdateTime(System.currentTimeMillis());
                ossMapper.updateByPrimaryKeySelective(oss);
                saveLog(oss.getId(), "i18n_operation_ex" + ": " + e.getMessage(), e.getMessage(), false, 0);
            }
        });
    }

    public void doSyncBucketInfo(OssWithBLOBs oss) throws Exception {
        Integer sum = fetchOssBucketList(oss);
        oss.setStatus(OSSConstants.SYNC_STATUS.FINISHED.name());
        oss.setUpdateTime(System.currentTimeMillis());
        oss.setSum(sum);
        ossMapper.updateByPrimaryKeySelective(oss);
        saveLog(oss.getId(), "i18n_end_oss_sync", "", true, sum);
    }

    public Integer fetchOssBucketList(OssWithBLOBs oss) throws Exception {
        try {
            OssProvider ossProvider = getOssProvider(oss.getPluginId());
            return saveOssBuckets(ossProvider, oss);
        } catch (Exception e) {
            LogUtil.error("Failed to get the bucket information: " + oss.getName(), e);
            HRException.throwException(Translator.get("i18n_get_bucket_info_failed") + e.getMessage());
            throw e;
        }
    }

    public OssProvider getOssProvider(String pluginId) throws Exception {
        OssProvider ossProvider = (OssProvider) OssManager.getOssProviders().get(pluginId);
        if (ossProvider == null) {
            throw new Exception(String.format("Unsupported plugin: %s", pluginId));
        }
        return ossProvider;
    }

    private Integer saveOssBuckets(OssProvider ossProvider, OssWithBLOBs ossAccount) throws Exception {
        try {
            List<OssBucket> list = ossProvider.getOssBucketList(ossAccount);
            OssBucketExample bucketExample = new OssBucketExample();
            bucketExample.createCriteria().andIdEqualTo(ossAccount.getId());
            OssBucket bucket = new OssBucket();
            bucket.setSyncFlag(true);
            ossBucketMapper.updateByExampleSelective(bucket, bucketExample);
            list.forEach(newBucket -> {
                newBucket.setSyncFlag(false);
                newBucket.setCreateTime(System.currentTimeMillis());
                OssBucketExample example = new OssBucketExample();
                example.createCriteria()
                        .andOssIdEqualTo(newBucket.getOssId())
                        .andLocationEqualTo(newBucket.getLocation())
                        .andBucketNameEqualTo(newBucket.getBucketName());
                if (ossBucketMapper.selectByExample(example).size() > 0) {
                    ossBucketMapper.updateByExampleSelective(newBucket, example);
                } else {
                    newBucket.setId(UUIDUtil.newUUID());
                    ossBucketMapper.insertSelective(newBucket);
                }
            });

            bucketExample = new OssBucketExample();
            bucketExample.createCriteria()
                    .andOssIdEqualTo(ossAccount.getId())
                    .andSyncFlagEqualTo(true);
            ossBucketMapper.deleteByExample(bucketExample);
            return list.size();
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            throw new Exception(e.getMessage());
        }

    }

    void saveLog(String ossId, String operation, String output, boolean result, Integer sum) {
        OssLogWithBLOBs ossLog = new OssLogWithBLOBs();
        String operator = "system";
        try {
            if (SessionUtils.getUser() != null) {
                operator = SessionUtils.getUser().getId();
            }
        } catch (Exception e) {
            //防止单元测试无session
        }
        ossLog.setOperator(operator);
        ossLog.setOssId(ossId);
        ossLog.setCreateTime(System.currentTimeMillis());
        ossLog.setOperation(operation);
        ossLog.setOutput(output);
        ossLog.setResult(result);
        ossLog.setSum(sum);
        ossLogMapper.insertSelective(ossLog);

    }

    public List<BucketObjectDTO> getObjects(String bucketId, String prefix) throws Exception{
        OssBucket bucket = getBucketByPrimaryKey(bucketId);
        OssWithBLOBs oss = getAccountByPrimaryKey(bucket.getOssId());
        OssProvider ossProvider = (OssProvider) OssManager.getOssProviders().get(oss.getPluginId());
        return ossProvider.getBucketObjects(bucket, oss, prefix);
    }

    private OssBucket getBucketByPrimaryKey(String bucketId) throws Exception{
        OssBucket bucket = ossBucketMapper.selectByPrimaryKey(bucketId);
        if(bucket == null){
            throw new Exception("Parameter is null.");
        }
        return bucket;
    }

    private OssWithBLOBs getAccountByPrimaryKey(String ossId)throws Exception{
        OssWithBLOBs account = ossMapper.selectByPrimaryKey(ossId);
        return account;
    }

}
