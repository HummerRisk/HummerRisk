package com.hummerrisk.service;

import com.hummerrisk.base.domain.Plugin;
import com.hummerrisk.base.domain.PluginExample;
import com.hummerrisk.base.mapper.PluginMapper;
import com.hummerrisk.commons.constants.ScanTypeConstants;
import com.hummerrisk.commons.exception.HRException;
import com.hummerrisk.commons.utils.LogUtil;
import com.hummerrisk.commons.utils.PlatformUtils;
import com.hummerrisk.commons.utils.ReadFileUtils;
import com.hummerrisk.controller.request.Plugin.PluginRequest;
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

    public List<Plugin> getAllPlugin(String scanType) {
        PluginExample example = new PluginExample();
        example.setOrderByClause("update_time");
        PluginExample.Criteria criteria = example.createCriteria();
        if (scanType!=null) {
            if (StringUtils.equalsIgnoreCase(scanType, ScanTypeConstants.prowler.name())) {
                criteria.andIdEqualTo(PlatformUtils.aws);
            } else {
                criteria.andScanTypeLike(scanType);
            }
        }
        return pluginMapper.selectByExample(example);
    }

    public List<Plugin> getCloudPlugin() {
        PluginExample example = new PluginExample();
        example.setOrderByClause("update_time");
        PluginExample.Criteria criteria = example.createCriteria();
        criteria.andIdNotIn(PlatformUtils.getVulnPlugin());
        return pluginMapper.selectByExample(example);
    }

    public List<Plugin> getVulnPlugin() {
        PluginExample example = new PluginExample();
        example.setOrderByClause("update_time");
        PluginExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(PlatformUtils.getVulnPlugin());
        return pluginMapper.selectByExample(example);
    }

    public String getCredential(String pluginId) {
        try {
            return ReadFileUtils.readConfigFile(BASE_CREDENTIAL_DIC, pluginId, JSON_EXTENSION);
        } catch (HRException e) {
            LogUtil.error("Error getting credential parameters: " + pluginId, e);
            HRException.throwException(Translator.get("i18n_ex_plugin_get"));
        } catch (Exception e) {
            LogUtil.error("Error getting credential parameters: " + pluginId, e);
            HRException.throwException(Translator.get("i18n_ex_plugin_get"));
        }
        return Translator.get("i18n_ex_plugin_get");
    }

    public List<Plugin> getPluginList(PluginRequest request) {
        PluginExample example = new PluginExample();
        example.createCriteria().andNameEqualTo(request.getName());
        return pluginMapper.selectByExample(example);
    }

}
