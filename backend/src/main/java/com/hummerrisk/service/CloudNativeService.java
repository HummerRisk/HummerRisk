package com.hummerrisk.service;

import com.aliyuncs.exceptions.ClientException;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.CloudNativeMapper;
import com.hummerrisk.base.mapper.CloudNativeSourceMapper;
import com.hummerrisk.base.mapper.PluginMapper;
import com.hummerrisk.base.mapper.ProxyMapper;
import com.hummerrisk.base.mapper.ext.ExtCloudNativeMapper;
import com.hummerrisk.base.mapper.ext.ExtCloudNativeSourceMapper;
import com.hummerrisk.commons.constants.CloudAccountConstants;
import com.hummerrisk.commons.constants.ResourceOperation;
import com.hummerrisk.commons.constants.ResourceTypeConstants;
import com.hummerrisk.commons.exception.HRException;
import com.hummerrisk.commons.utils.*;
import com.hummerrisk.controller.request.cloudNative.CloudNativeRequest;
import com.hummerrisk.controller.request.cloudNative.CloudNativeSourceRequest;
import com.hummerrisk.controller.request.cloudNative.CreateCloudNativeRequest;
import com.hummerrisk.controller.request.cloudNative.UpdateCloudNativeRequest;
import com.hummerrisk.dto.CloudNativeDTO;
import com.hummerrisk.dto.CloudNativeSourceDTO;
import com.hummerrisk.dto.SituationDTO;
import com.hummerrisk.i18n.Translator;
import com.hummerrisk.proxy.k8s.K8sRequest;
import io.kubernetes.client.openapi.ApiException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CloudNativeService {

    @Resource
    private ExtCloudNativeMapper extCloudNativeMapper;
    @Resource
    private CloudNativeMapper cloudNativeMapper;
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

    public List<CloudNativeDTO> getCloudNativeList(CloudNativeRequest request) {
        return extCloudNativeMapper.getCloudNativeList(request);
    }

    public List<CloudNativeDTO> allCloudNativeList(CloudNativeRequest request) {
        return extCloudNativeMapper.getCloudNativeList(request);
    }

    public CloudNative getCloudNative(String id) {
       return cloudNativeMapper.selectByPrimaryKey(id);
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
        CloudNative cloudNative = cloudNativeMapper.selectByPrimaryKey(id);
        //检验账号的有效性
        boolean valid = validateAccount(cloudNative);
        if (valid) {
            cloudNative.setStatus(CloudAccountConstants.Status.VALID.name());
        } else {
            cloudNative.setStatus(CloudAccountConstants.Status.INVALID.name());
        }
        cloudNativeMapper.updateByPrimaryKeySelective(cloudNative);
        addCloudNativeSource(cloudNative);
        return valid;
    }

    private boolean validateAccount(CloudNative cloudNative) {
        try {
            Proxy proxy = new Proxy();
            if (cloudNative.getProxyId() != null) proxy = proxyMapper.selectByPrimaryKey(cloudNative.getProxyId());
            return PlatformUtils.validateCloudNative(cloudNative, proxy);
        } catch (Exception e) {
            LogUtil.error(String.format("HRException in verifying cloud native, cloud native: [%s], plugin: [%s], error information:%s", cloudNative.getName(), cloudNative.getPluginName(), e.getMessage()), e);
            return false;
        }
    }

    public CloudNative addCloudNative(CreateCloudNativeRequest request) {
        try{
            //参数校验
            if (StringUtils.isEmpty(request.getCredential())
                    || StringUtils.isEmpty(request.getName()) || StringUtils.isEmpty(request.getPluginId())) {
                HRException.throwException(Translator.get("i18n_ex_cloud_native_name_or_plugin"));
            }

            //云账号名称不能重复
            CloudNativeExample cloudNativeExample = new CloudNativeExample();
            cloudNativeExample.createCriteria().andNameEqualTo(request.getName());
            List<CloudNative> cloudNatives = cloudNativeMapper.selectByExampleWithBLOBs(cloudNativeExample);
            if (!CollectionUtils.isEmpty(cloudNatives)) {
                HRException.throwException(Translator.get("i18n_ex_cloud_native_name_duplicate"));
            }

            CloudNative account = new CloudNative();

            //校验云插件是否存在
            Plugin plugin = pluginMapper.selectByPrimaryKey(request.getPluginId());
            if (plugin == null) {
                HRException.throwException(Translator.get("i18n_ex_cloud_native_no_exist_plugin"));
            } else {
                BeanUtils.copyBean(account, request);
                account.setPluginIcon(Objects.requireNonNull(plugin.getIcon()));
                account.setPluginName(plugin.getName());
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
                cloudNativeMapper.insertSelective(account);
                addCloudNativeSource(account);
                OperationLogService.log(SessionUtils.getUser(), account.getId(), account.getName(), ResourceTypeConstants.CLOUD_NATIVE.name(), ResourceOperation.CREATE, "i18n_create_cloud_native");
                return getCloudNative(account.getId());
            }
        } catch (Exception e) {
            HRException.throwException(e.getMessage());
        }
        return null;
    }

    public CloudNative editCloudNative(UpdateCloudNativeRequest request) throws Exception {
        try {
            //参数校验
            if (StringUtils.isEmpty(request.getCredential())
                    || StringUtils.isEmpty(request.getId())) {
                HRException.throwException(Translator.get("i18n_ex_cloud_native_id_or_plugin"));
            }

            //云账号名称不能重复
            CloudNativeExample cloudNativeExample = new CloudNativeExample();
            cloudNativeExample.createCriteria().andNameEqualTo(request.getName()).andIdNotEqualTo(request.getId());
            List<CloudNative> cloudNatives = cloudNativeMapper.selectByExampleWithBLOBs(cloudNativeExample);
            if (!CollectionUtils.isEmpty(cloudNatives)) {
                HRException.throwException(Translator.get("i18n_ex_cloud_native_name_duplicate"));
            }

            if (cloudNativeMapper.selectByPrimaryKey(request.getId()) == null) {
                HRException.throwException(Translator.get("i18n_ex_cloud_native_no_exist_id"));
            }

            CloudNative account = new CloudNative();
            //校验云插件是否存在
            Plugin plugin = pluginMapper.selectByPrimaryKey(request.getPluginId());
            if (plugin == null) {
                HRException.throwException(Translator.get("i18n_ex_cloud_native_no_exist_plugin"));
            } else {
                BeanUtils.copyBean(account, request);
                account.setPluginIcon(plugin.getIcon());
                account.setPluginName(plugin.getName());
                account.setUpdateTime(System.currentTimeMillis());
                //检验账号的有效性
                boolean valid = validateAccount(account);
                if (valid) {
                    account.setStatus(CloudAccountConstants.Status.VALID.name());
                } else {
                    account.setStatus(CloudAccountConstants.Status.INVALID.name());
                }
                cloudNativeMapper.updateByPrimaryKeySelective(account);
                account = cloudNativeMapper.selectByPrimaryKey(account.getId());
                addCloudNativeSource(account);
                //检验账号已更新状态
                OperationLogService.log(SessionUtils.getUser(), account.getId(), account.getName(), ResourceTypeConstants.CLOUD_NATIVE.name(), ResourceOperation.UPDATE, "i18n_update_cloud_native");
                return getCloudNative(account.getId());
            }

        } catch (HRException | ClientException e) {
            HRException.throwException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return null;
    }

    public void delete(String accountId) {
        CloudNative cloudNative = cloudNativeMapper.selectByPrimaryKey(accountId);
        cloudNativeMapper.deleteByPrimaryKey(accountId);
        OperationLogService.log(SessionUtils.getUser(), accountId, cloudNative.getName(), ResourceTypeConstants.CLOUD_NATIVE.name(), ResourceOperation.DELETE, "i18n_delete_cloud_native");
    }

    public void addCloudNativeSource(CloudNative cloudNative) throws IOException, ApiException {
        commonThreadPool.addTask(() -> {
            try {
                CloudNativeSourceExample example = new CloudNativeSourceExample();
                example.createCriteria().andCloudNativeIdEqualTo(cloudNative.getId());
                cloudNativeSourceMapper.deleteByExample(example);

                List<CloudNativeSource> list = new LinkedList<>();
                K8sRequest k8sRequest = new K8sRequest();
                k8sRequest.setCredential(cloudNative.getCredential());
                list.addAll(k8sRequest.getVersion(cloudNative));
                list.addAll(k8sRequest.getNameSpace(cloudNative));
                list.addAll(k8sRequest.getNode(cloudNative));
                list.addAll(k8sRequest.getPod(cloudNative));
                list.addAll(k8sRequest.getService(cloudNative));
                list.addAll(k8sRequest.getDeployment(cloudNative));
                list.addAll(k8sRequest.getDaemonSet(cloudNative));
                list.addAll(k8sRequest.getIngress(cloudNative));
                list.addAll(k8sRequest.getRole(cloudNative));
                list.addAll(k8sRequest.getSecret(cloudNative));
                list.addAll(k8sRequest.getConfigMap(cloudNative));
                list.addAll(k8sRequest.getStatefulSet(cloudNative));
                list.addAll(k8sRequest.getCronJob(cloudNative));
                list.addAll(k8sRequest.getJob(cloudNative));
                list.addAll(k8sRequest.getPv(cloudNative));
                list.addAll(k8sRequest.getPvc(cloudNative));
                list.addAll(k8sRequest.getLease(cloudNative));
                list.addAll(k8sRequest.getEndpointSlice(cloudNative));
                list.addAll(k8sRequest.getEvent(cloudNative));
                list.addAll(k8sRequest.getNetworkPolicy(cloudNative));
                for (CloudNativeSource cloudNativeSource : list) {
                    cloudNativeSourceMapper.insertSelective(cloudNativeSource);
                }
            } catch (Exception e) {
                LogUtil.error(e);
            }
        });
    }

    public List<CloudNativeSourceDTO> getCloudNativeSourceList(CloudNativeSourceRequest request) {
        return extCloudNativeSourceMapper.getCloudNativeSourceList(request);
    }

    public SituationDTO situationInfo(Map<String, Object> params) {
        return extCloudNativeSourceMapper.situationInfo(params);
    }


}
