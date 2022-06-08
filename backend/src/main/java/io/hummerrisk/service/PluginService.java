package io.hummerrisk.service;

import io.hummerrisk.base.domain.Plugin;
import io.hummerrisk.base.domain.PluginExample;
import io.hummerrisk.base.mapper.PluginMapper;
import io.hummerrisk.commons.constants.ScanTypeConstants;
import io.hummerrisk.commons.exception.HRException;
import io.hummerrisk.commons.utils.LogUtil;
import io.hummerrisk.commons.utils.PlatformUtils;
import io.hummerrisk.commons.utils.ReadFileUtils;
import io.hummerrisk.controller.request.Plugin.PluginRequest;
import io.hummerrisk.i18n.Translator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
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
        List<String> values = new ArrayList<>();
        values.add(PlatformUtils.nuclei);
        values.add(PlatformUtils.xray);
        criteria.andIdNotIn(values);
        return pluginMapper.selectByExample(example);
    }

    public List<Plugin> getVulnPlugin() {
        PluginExample example = new PluginExample();
        example.setOrderByClause("update_time");
        PluginExample.Criteria criteria = example.createCriteria();
        List<String> values = new ArrayList<>();
        values.add(PlatformUtils.nuclei);
        values.add(PlatformUtils.xray);
        criteria.andIdIn(values);
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
