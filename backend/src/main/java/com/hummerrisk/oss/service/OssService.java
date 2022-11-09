package com.hummerrisk.oss.service;

import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.AccountMapper;
import com.hummerrisk.base.mapper.OssBucketMapper;
import com.hummerrisk.base.mapper.OssMapper;
import com.hummerrisk.base.mapper.ext.ExtOssMapper;
import com.hummerrisk.commons.constants.CloudAccountConstants;
import com.hummerrisk.commons.constants.ResourceTypeConstants;
import com.hummerrisk.commons.exception.HRException;
import com.hummerrisk.commons.utils.CommonThreadPool;
import com.hummerrisk.commons.utils.LogUtil;
import com.hummerrisk.controller.request.account.CloudAccountRequest;
import com.hummerrisk.i18n.Translator;
import com.hummerrisk.oss.config.OssManager;
import com.hummerrisk.oss.constants.OSSConstants;
import com.hummerrisk.oss.controller.request.OssRequest;
import com.hummerrisk.oss.dto.OssDTO;
import com.hummerrisk.oss.provider.OssProvider;
import com.hummerrisk.service.AccountService;
import com.hummerrisk.service.OperationLogService;
import kotlin.collections.ArrayDeque;
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

    public List<OssDTO> ossList (OssRequest request) {
        return extOssMapper.ossList(request);
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

    public void syncBatch(String id) throws Exception {
        OssWithBLOBs oss = ossMapper.selectByPrimaryKey(id);
        try {
            syncResource(oss);
        } catch (Exception e) {
            oss.setStatus(OSSConstants.SYNC_STATUS.ERROR.name());
            ossMapper.updateByPrimaryKeySelective(oss);
            LogUtil.error(String.format("Failed to synchronize cloud account: %s", oss.getName()), e);
        }
    }

    private void syncResource(OssWithBLOBs oss) throws Exception {
        if (OSSConstants.SYNC_STATUS.SYNC.equals(oss.getStatus())) {
            return;
        }
        if (OSSConstants.SYNC_STATUS.PENDING.equals(oss.getStatus())) {
            return;
        }
        if (!accountService.validate(oss.getId()).isFlag()) {
            oss.setStatus(OSSConstants.SYNC_STATUS.ERROR.name());
            ossMapper.updateByPrimaryKeySelective(oss);
            return;
        }
        OperationLogService.log(null, oss.getId(), oss.getName(), ResourceTypeConstants.OSS.name(), OSSConstants.SYNC_STATUS.SYNC.name(), null);
        oss.setStatus(OSSConstants.SYNC_STATUS.PENDING.name());
        ossMapper.updateByPrimaryKeySelective(oss);
        syncBucketInfo(oss);
    }

    public void syncBucketInfo(final OssWithBLOBs oss) {
        commonThreadPool.addTask(() -> {
            try {
                doSyncBucketInfo(oss);
            } catch (Exception e) {
                oss.setStatus(OSSConstants.SYNC_STATUS.ERROR.name());
                oss.setUpdateTime(System.currentTimeMillis());
                ossMapper.updateByPrimaryKeySelective(oss);
            }
        });
    }

    public void doSyncBucketInfo(OssWithBLOBs oss) throws Exception {
        oss.setStatus(OSSConstants.SYNC_STATUS.SYNC.name());
        ossMapper.updateByPrimaryKeySelective(oss);
        fetchOssBucketList(oss);
        oss.setStatus(OSSConstants.SYNC_STATUS.SUCCESS.name());
        oss.setUpdateTime(System.currentTimeMillis());
        ossMapper.updateByPrimaryKeySelective(oss);
    }

    public void fetchOssBucketList(OssWithBLOBs oss) throws Exception {
        try {
            OssProvider ossProvider = getOssProvider(oss.getPluginName());
            saveOssBuckets(ossProvider, oss);
        } catch (Exception e) {
            LogUtil.error("Failed to get the bucket information: " + oss.getName(), e);
            HRException.throwException(Translator.get("i18n_get_bucket_info_failed")+ e.getMessage());
            throw e;
        }
    }

    public OssProvider getOssProvider(String pluginName) throws Exception {
        OssProvider ossProvider = (OssProvider) OssManager.getOssProviders().get(pluginName);
        if(ossProvider == null){
            throw new Exception(String.format("Unsupported plugin: %s", pluginName));
        }
        return ossProvider;
    }

    private void saveOssBuckets(OssProvider ossProvider, OssWithBLOBs ossAccount) throws Exception{
        try{
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
                    ossBucketMapper.insertSelective(newBucket);
                }
            });

            bucketExample = new OssBucketExample();
            bucketExample.createCriteria()
                    .andOssIdEqualTo(ossAccount.getId())
                    .andSyncFlagEqualTo(true);
            ossBucketMapper.deleteByExample(bucketExample);
        }catch (Exception e){
            LogUtil.error(e.getMessage(), e);
            throw new Exception(e.getMessage());
        }

    }

}
