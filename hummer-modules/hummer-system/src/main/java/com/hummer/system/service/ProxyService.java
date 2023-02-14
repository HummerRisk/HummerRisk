package com.hummer.system.service;

import com.hummer.common.core.constant.ResourceOperation;
import com.hummer.common.core.constant.ResourceTypeConstants;
import com.hummer.common.core.domain.Proxy;
import com.hummer.common.core.domain.ProxyExample;
import com.hummer.common.core.domain.request.proxy.ProxyRequest;
import com.hummer.common.core.exception.HRException;
import com.hummer.common.core.i18n.Translator;
import com.hummer.common.mapper.mapper.ProxyMapper;
import com.hummer.common.mapper.mapper.ext.ExtProxyMapper;
import com.hummer.common.core.utils.SessionUtils;
import com.hummer.common.mapper.service.OperationLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ProxyService {

    @Resource
    private ProxyMapper proxyMapper;

    @Resource
    private ExtProxyMapper extProxyMapper;

    public Proxy insert(Proxy proxy) throws Exception {

        Integer id = proxy.getId();
        Proxy proxy1 = proxyMapper.selectByPrimaryKey(id);
        if (proxy1 != null) {
            HRException.throwException(Translator.get("proxy_id_already_exists"));
        } else {
            createProxy(proxy);
        }
        return proxy;
    }

    public void createProxy(Proxy proxy) throws Exception {
        proxy.setCreateTime(System.currentTimeMillis());
        proxy.setUpdateTime(System.currentTimeMillis());
        // 密码使用 MD5
        proxy.setProxyPassword(proxy.getProxyPassword());
        proxyMapper.insertSelective(proxy);
        OperationLogService.log(SessionUtils.getUser(), proxy.getProxyIp(), proxy.getProxyIp(), ResourceTypeConstants.PROXY.name(), ResourceOperation.CREATE, "i18n_create_proxy");
    }

    public List<Proxy> getProxyList() {
        ProxyExample example = new ProxyExample();
        example.setOrderByClause("update_time desc");
        return proxyMapper.selectByExample(example);
    }

    public List<Proxy> getProxyListWithRequest(ProxyRequest request) {
        return extProxyMapper.getProxyListWithRequest(request);
    }

    public void deleteProxy(int proxyId) {
        Proxy proxy = proxyMapper.selectByPrimaryKey(proxyId);
        proxyMapper.deleteByPrimaryKey(proxyId);
        OperationLogService.log(SessionUtils.getUser(), proxy.getProxyIp(), proxy.getProxyIp(), ResourceTypeConstants.PROXY.name(), ResourceOperation.DELETE, "i18n_delete_proxy");
    }

    public void updateProxy(Proxy proxy) {
        proxy.setUpdateTime(System.currentTimeMillis());
        proxyMapper.updateByPrimaryKeySelective(proxy);
        OperationLogService.log(SessionUtils.getUser(), proxy.getProxyIp(), proxy.getProxyIp(), ResourceTypeConstants.PROXY.name(), ResourceOperation.UPDATE, "i18n_update_proxy");
    }

}
