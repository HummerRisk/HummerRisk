package com.hummer.system.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummer.common.core.domain.Proxy;
import com.hummer.common.core.domain.request.proxy.ProxyRequest;
import com.hummer.common.core.handler.annotation.I18n;
import com.hummer.common.core.utils.PageUtils;
import com.hummer.common.core.utils.Pager;
import com.hummer.common.security.service.TokenService;
import com.hummer.system.service.ProxyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "代理")
@RequestMapping("proxy")
@RestController
public class ProxyController {

    @Autowired
    private ProxyService proxyService;
    @Autowired
    private TokenService tokenService;

    @I18n
    @Operation(summary = "添加代理")
    @PostMapping("/add")
    public Proxy insertProxy(@RequestBody Proxy Proxy) throws Exception {
        return proxyService.insert(Proxy, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "代理列表")
    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<Proxy>> getProxyList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ProxyRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, proxyService.getProxyListWithRequest(request));
    }

    @Operation(summary = "删除代理")
    @GetMapping("/delete/{proxyId}")
    public void deleteProxy(@PathVariable(value = "proxyId") int proxyId) {
        proxyService.deleteProxy(proxyId, tokenService.getLoginUser());
    }

    @Operation(summary = "更新代理")
    @PostMapping("/update")
    public void updateProxy(@RequestBody Proxy proxy) {
        proxyService.updateProxy(proxy, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "所有代理")
    @GetMapping("/list/all")
    public List<Proxy> getProxyList() {
        return proxyService.getProxyList();
    }

    @Operation(summary = "批量删除代理")
    @PostMapping("delete/proxys")
    public void deleteProxys(@RequestBody List<Integer> selectIds) throws Exception {
        proxyService.deleteProxys(selectIds, tokenService.getLoginUser());
    }

}
