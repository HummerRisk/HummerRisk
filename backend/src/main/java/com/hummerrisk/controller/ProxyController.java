package com.hummerrisk.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummerrisk.base.domain.Proxy;
import com.hummerrisk.commons.constants.RoleConstants;
import com.hummerrisk.commons.utils.PageUtils;
import com.hummerrisk.commons.utils.Pager;
import com.hummerrisk.controller.handler.annotation.I18n;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "代理")
@RequestMapping("proxy")
@RestController
public class ProxyController {

    @Resource
    private com.hummerrisk.service.ProxyService ProxyService;

    @I18n
    @ApiOperation(value = "添加代理")
    @PostMapping("/add")
    @RequiresRoles(RoleConstants.ADMIN)
    public Proxy insertProxy(@RequestBody Proxy Proxy) throws Exception {
        return ProxyService.insert(Proxy);
    }

    @I18n
    @ApiOperation(value = "代理列表")
    @PostMapping("/list/{goPage}/{pageSize}")
    @RequiresRoles(RoleConstants.ADMIN)
    public Pager<List<Proxy>> getProxyList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Proxy request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, ProxyService.getProxyListWithRequest(request));
    }

    @ApiOperation(value = "删除代理")
    @GetMapping("/delete/{proxyId}")
    @RequiresRoles(RoleConstants.ADMIN)
    public void deleteProxy(@PathVariable(value = "proxyId") int proxyId) {
        ProxyService.deleteProxy(proxyId);
    }

    @ApiOperation(value = "更新代理")
    @PostMapping("/update")
    @RequiresRoles(RoleConstants.ADMIN)
    public void updateProxy(@RequestBody Proxy proxy) {
        ProxyService.updateProxy(proxy);
    }

    @I18n
    @ApiOperation(value = "所有代理")
    @GetMapping("/list/all")
    public List<Proxy> getProxyList() {
        return ProxyService.getProxyList();
    }

}
