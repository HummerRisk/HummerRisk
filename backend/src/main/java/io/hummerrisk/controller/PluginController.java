package io.hummerrisk.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.hummerrisk.base.domain.Plugin;
import io.hummerrisk.commons.utils.PageUtils;
import io.hummerrisk.commons.utils.Pager;
import io.hummerrisk.controller.request.Plugin.PluginRequest;
import io.hummerrisk.service.PluginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "插件")
@RestController
@RequestMapping(value = "plugin")
public class PluginController {
    @Resource
    private PluginService pluginService;

    @ApiOperation(value = "所有云账号插件")
    @GetMapping("all")
    public List<Plugin> getCloudPlugin() {
        return pluginService.getCloudPlugin();
    }

    @ApiOperation(value = "所有漏洞插件")
    @GetMapping("vuln")
    public List<Plugin> getVulnPlugin() {
        return pluginService.getVulnPlugin();
    }

    @ApiOperation(value = "扫描引擎类型过滤插件")
    @GetMapping("scan/{scanType}")
    public List<Plugin> getPlugins(@PathVariable String scanType) {
        return pluginService.getAllPlugin(scanType);
    }

    @ApiOperation(value = "插件详情")
    @GetMapping("{pluginId}")
    public String getCredential(@PathVariable String pluginId) {
        return pluginService.getCredential(pluginId);
    }

    @ApiOperation(value = "插件列表")
    @PostMapping("list/{goPage}/{pageSize}")
    public Pager<List<Plugin>> getPluginList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody PluginRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, pluginService.getPluginList(request));
    }
}
