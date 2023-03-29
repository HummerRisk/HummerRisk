package com.hummer.system.service;

import com.hummer.common.core.constant.ScanTypeConstants;
import com.hummer.common.core.domain.Plugin;
import com.hummer.common.core.domain.PluginExample;
import com.hummer.common.core.domain.request.plugin.PluginRequest;
import com.hummer.common.core.exception.HRException;
import com.hummer.common.core.utils.LogUtil;
import com.hummer.common.core.utils.PlatformUtils;
import com.hummer.common.core.utils.ReadFileUtils;
import com.hummer.system.i18n.Translator;
import com.hummer.system.mapper.PluginMapper;
import com.hummer.system.mapper.ext.ExtPluginMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author harris
 */
@Service
public class PluginService {

    private static final String BASE_CREDENTIAL_DIC = "support/credential/";
    private static final String JSON_EXTENSION = ".json";

    @Autowired
    private PluginMapper pluginMapper;

    @Autowired
    private ExtPluginMapper extPluginMapper;

    public List<Plugin> getAllPlugin(String scanType) {
        PluginExample example = new PluginExample();
        example.setOrderByClause("order_");
        PluginExample.Criteria criteria = example.createCriteria();
        if (scanType != null) {
            if (StringUtils.equalsIgnoreCase(scanType, ScanTypeConstants.prowler.name())) {
                criteria.andIdEqualTo(PlatformUtils.aws).andTypeNotEqualTo(PlatformUtils.native_);
            } else {
                criteria.andScanTypeLike(scanType);
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
