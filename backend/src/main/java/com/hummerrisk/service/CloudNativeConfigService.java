package com.hummerrisk.service;

import com.aliyuncs.exceptions.ClientException;
import com.hummerrisk.base.domain.CloudNativeConfig;
import com.hummerrisk.base.domain.CloudNativeConfigExample;
import com.hummerrisk.base.domain.Package;
import com.hummerrisk.base.domain.Proxy;
import com.hummerrisk.base.mapper.CloudNativeConfigMapper;
import com.hummerrisk.base.mapper.CloudNativeSourceMapper;
import com.hummerrisk.base.mapper.PluginMapper;
import com.hummerrisk.base.mapper.ProxyMapper;
import com.hummerrisk.base.mapper.ext.ExtCloudNativeConfigMapper;
import com.hummerrisk.base.mapper.ext.ExtCloudNativeSourceMapper;
import com.hummerrisk.commons.constants.CloudAccountConstants;
import com.hummerrisk.commons.constants.PackageConstants;
import com.hummerrisk.commons.constants.ResourceOperation;
import com.hummerrisk.commons.constants.ResourceTypeConstants;
import com.hummerrisk.commons.exception.HRException;
import com.hummerrisk.commons.utils.*;
import com.hummerrisk.controller.request.config.ConfigRequest;
import com.hummerrisk.dto.CloudNativeConfigDTO;
import com.hummerrisk.i18n.Translator;
import io.kubernetes.client.openapi.ApiException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CloudNativeConfigService {

    @Resource
    private ExtCloudNativeConfigMapper extCloudNativeConfigMapper;
    @Resource
    private CloudNativeConfigMapper cloudNativeConfigMapper;
    @Resource
    private ExtCloudNativeSourceMapper extCloudNativeSourceMapper;
    @Resource
    private CloudNativeSourceMapper cloudNativeSourceMapper;
    @Resource
    private PluginMapper pluginMapper;
    @Resource
    private ProxyMapper proxyMapper;
    @Resource
    private CommonThreadPool commonThreadPool;

    public List<CloudNativeConfigDTO> getCloudNativeConfigList(ConfigRequest request) {
        return extCloudNativeConfigMapper.getCloudNativeConfigList(request);
    }

    public boolean validate(List<String> ids) {
        ids.forEach(id -> {
            try {
                boolean validate = validate(id);
                if(!validate) throw new HRException(Translator.get("failed_cloud_native"));
            } catch (Exception e) {
                LogUtil.error(e.getMessage());
                throw new HRException(e.getMessage());
            }
        });
        return true;
    }

    public boolean validate(String id) throws IOException, ApiException {
        CloudNativeConfig cloudNativeConfig = cloudNativeConfigMapper.selectByPrimaryKey(id);
        //检验账号的有效性
        boolean valid = validateAccount(cloudNativeConfig);
        if (valid) {
            cloudNativeConfig.setStatus(CloudAccountConstants.Status.VALID.name());
        } else {
            cloudNativeConfig.setStatus(CloudAccountConstants.Status.INVALID.name());
        }
        cloudNativeConfigMapper.updateByPrimaryKeySelective(cloudNativeConfig);
        return valid;
    }

    private boolean validateAccount(CloudNativeConfig cloudNativeConfig) {
        try {
            Proxy proxy = new Proxy();//代理在校验yaml格式时暂时没用到
            if (cloudNativeConfig.getProxyId() != null) proxy = proxyMapper.selectByPrimaryKey(cloudNativeConfig.getProxyId());
            return YamlUtil.validateYaml(cloudNativeConfig.getConfigYaml()) || YamlUtil.validateJson(cloudNativeConfig.getConfigYaml());
        } catch (Exception e) {
            LogUtil.error(String.format("HRException in verifying cloud native, cloud native: [%s], error information:%s", cloudNativeConfig.getName(), e.getMessage()), e);
            return false;
        }
    }

    public CloudNativeConfig addCloudNativeConfig(CloudNativeConfig request) {
        try{
            //部署配置名称不能重复
            CloudNativeConfigExample cloudNativeConfigExample = new CloudNativeConfigExample();
            cloudNativeConfigExample.createCriteria().andNameEqualTo(request.getName());
            List<CloudNativeConfig> cloudNativeConfigs = cloudNativeConfigMapper.selectByExampleWithBLOBs(cloudNativeConfigExample);
            if (!CollectionUtils.isEmpty(cloudNativeConfigs)) {
                HRException.throwException(Translator.get("i18n_ex_cloud_native_config_name_duplicate"));
            }

            CloudNativeConfig account = new CloudNativeConfig();

            BeanUtils.copyBean(account, request);
            account.setCreateTime(System.currentTimeMillis());
            account.setUpdateTime(System.currentTimeMillis());
            account.setCreator(Objects.requireNonNull(SessionUtils.getUser()).getId());
            account.setId(UUIDUtil.newUUID());
            //检验账号的有效性
            boolean valid = validateAccount(account);
            if (valid) {
                account.setStatus(CloudAccountConstants.Status.VALID.name());
            } else {
                account.setStatus(CloudAccountConstants.Status.INVALID.name());
            }
            cloudNativeConfigMapper.insertSelective(account);
            OperationLogService.log(SessionUtils.getUser(), account.getId(), account.getName(), ResourceTypeConstants.CLOUD_NATIVE_CONFIG.name(), ResourceOperation.CREATE, "i18n_create_cloud_native_config");
            return account;
        } catch (Exception e) {
            HRException.throwException(e.getMessage());
        }
        return null;
    }

    public CloudNativeConfig editCloudNativeConfig(CloudNativeConfig request) throws Exception {
        try {
            //部署配置名称不能重复
            CloudNativeConfigExample cloudNativeConfigExample = new CloudNativeConfigExample();
            cloudNativeConfigExample.createCriteria().andNameEqualTo(request.getName()).andIdNotEqualTo(request.getId());
            List<CloudNativeConfig> cloudNativeConfigs = cloudNativeConfigMapper.selectByExampleWithBLOBs(cloudNativeConfigExample);
            if (!CollectionUtils.isEmpty(cloudNativeConfigs)) {
                HRException.throwException(Translator.get("i18n_ex_cloud_native_config_name_duplicate"));
            }

            if (cloudNativeConfigMapper.selectByPrimaryKey(request.getId()) == null) {
                HRException.throwException(Translator.get("i18n_ex_cloud_native_config_no_exist_id"));
            }

            CloudNativeConfig account = new CloudNativeConfig();

            BeanUtils.copyBean(account, request);
            account.setUpdateTime(System.currentTimeMillis());
            //检验账号的有效性
            boolean valid = validateAccount(account);
            if (valid) {
                account.setStatus(CloudAccountConstants.Status.VALID.name());
            } else {
                account.setStatus(CloudAccountConstants.Status.INVALID.name());
            }
            cloudNativeConfigMapper.updateByPrimaryKeySelective(account);
            account = cloudNativeConfigMapper.selectByPrimaryKey(account.getId());
            //检验账号已更新状态
            OperationLogService.log(SessionUtils.getUser(), account.getId(), account.getName(), ResourceTypeConstants.CLOUD_NATIVE_CONFIG.name(), ResourceOperation.UPDATE, "i18n_update_cloud_native_config");
            return account;

        } catch (HRException | ClientException e) {
            HRException.throwException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return null;
    }

    public void delete(String accountId) {
        CloudNativeConfig cloudNativeConfig = cloudNativeConfigMapper.selectByPrimaryKey(accountId);
        cloudNativeConfigMapper.deleteByPrimaryKey(accountId);
        OperationLogService.log(SessionUtils.getUser(), accountId, cloudNativeConfig.getName(), ResourceTypeConstants.CLOUD_NATIVE_CONFIG.name(), ResourceOperation.DELETE, "i18n_delete_cloud_native_config");
    }

    public String uploadYaml(MultipartFile file) throws Exception {
        String yaml = upload(file);
        return yaml;
    }

    public static final String upload(MultipartFile file) throws IOException
    {
        try
        {
            return FileUploadUtils.uploadFileToString(file);
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

}
