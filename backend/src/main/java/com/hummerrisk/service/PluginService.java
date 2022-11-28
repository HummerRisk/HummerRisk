package com.hummerrisk.service;

import com.hummerrisk.base.domain.Plugin;
import com.hummerrisk.base.domain.PluginExample;
import com.hummerrisk.base.mapper.PluginMapper;
import com.hummerrisk.base.mapper.ext.ExtPluginMapper;
import com.hummerrisk.commons.constants.ScanTypeConstants;
import com.hummerrisk.commons.exception.HRException;
import com.hummerrisk.commons.utils.LogUtil;
import com.hummerrisk.commons.utils.PlatformUtils;
import com.hummerrisk.commons.utils.ReadFileUtils;
import com.hummerrisk.controller.request.plugin.PluginRequest;
import com.hummerrisk.i18n.Translator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author harris
 */
@Service
public class PluginService {

    private static final String BASE_CREDENTIAL_DIC = "support/credential/";
    private static final String JSON_EXTENSION = ".json";

    @Resource
    private PluginMapper pluginMapper;

    @Resource
    private ExtPluginMapper extPluginMapper;

    public List<Plugin> getAllPlugin(String scanType) {
        PluginExample example = new PluginExample();
        example.setOrderByClause("order_");
        PluginExample.Criteria criteria = example.createCriteria();
        if (scanType != null) {
            if (StringUtils.equalsIgnoreCase(scanType, ScanTypeConstants.prowler.name())) {
                criteria.andIdEqualTo(PlatformUtils.aws).andTypeNotEqualTo(PlatformUtils.native_);
            } else {
                criteria.andScanTypeLike(scanType).andTypeNotEqualTo(PlatformUtils.native_);
            }
        }
        return pluginMapper.selectByExample(example);
    }

    public List<Plugin> getCloudPlugin() {
        PluginExample example = new PluginExample();
        example.setOrderByClause("order_");
        PluginExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(PlatformUtils.getCloudPlugin());
        return pluginMapper.selectByExample(example);
    }

    public List<Plugin> getVulnPlugin() {
        PluginExample example = new PluginExample();
        example.setOrderByClause("order_");
        PluginExample.Criteria criteria = example.createCriteria();
        criteria.andTypeEqualTo(PlatformUtils.vuln_);
        return pluginMapper.selectByExample(example);
    }

    public List<Plugin> getNativePlugin() {
        PluginExample example = new PluginExample();
        example.setOrderByClause("order_");
        PluginExample.Criteria criteria = example.createCriteria();
        criteria.andTypeEqualTo(PlatformUtils.native_);
        return pluginMapper.selectByExample(example);
    }

    public String getCredential(String pluginId) {
        try {
            return ReadFileUtils.readConfigFile(BASE_CREDENTIAL_DIC, pluginId, JSON_EXTENSION);
        } catch (Exception e) {
            LogUtil.error("Error getting credential parameters: " + pluginId, e);
            HRException.throwException(Translator.get("i18n_ex_plugin_get"));
        }
        return Translator.get("i18n_ex_plugin_get");
    }

    public List<Plugin> getPluginList(PluginRequest request) {

        PluginExample example = new PluginExample();
        example.setOrderByClause("order_");
        if (request.getName() != null) {
            PluginExample.Criteria criteria = example.createCriteria();
            criteria.andNameLike(request.getName());
        }
        return extPluginMapper.getPluginList(request);
    }

}
