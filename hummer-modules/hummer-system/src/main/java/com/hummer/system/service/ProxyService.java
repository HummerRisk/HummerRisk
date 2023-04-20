package com.hummer.system.service;

import com.hummer.common.core.constant.ResourceOperation;
import com.hummer.common.core.constant.ResourceTypeConstants;
import com.hummer.common.core.domain.Proxy;
import com.hummer.common.core.domain.ProxyExample;
import com.hummer.common.core.domain.request.proxy.ProxyRequest;
import com.hummer.common.core.exception.HRException;
import com.hummer.common.core.i18n.Translator;
import com.hummer.system.api.model.LoginUser;
import com.hummer.system.mapper.ProxyMapper;
import com.hummer.system.mapper.ext.ExtProxyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ProxyService {

    @Autowired
    private ProxyMapper proxyMapper;

    @Autowired
    private ExtProxyMapper extProxyMapper;

    @Autowired
    private OperationLogService operationLogService;

    public Proxy insert(Proxy proxy, LoginUser loginUser) throws Exception {

        Integer id = proxy.getId();
        Proxy proxy1 = proxyMapper.selectByPrimaryKey(id);
        if (proxy1 != null) {
            HRException.throwException(Translator.get("proxy_id_already_exists"));
        } else {
            createProxy(proxy, loginUser);
        }
        return proxy;
    }

    public void createProxy(Proxy proxy, LoginUser loginUser) throws Exception {
        proxy.setCreateTime(System.currentTimeMillis());
        proxy.setUpdateTime(System.currentTimeMillis());
        // 密码使用 MD5
        proxy.setProxyPassword(proxy.getProxyPassword());
        proxyMapper.insertSelective(proxy);
        operationLogService.log(loginUser, proxy.getProxyIp(), proxy.getProxyIp(), ResourceTypeConstants.PROXY.name(), ResourceOperation.CREATE, "i18n_create_proxy");
    }

    public List<Proxy> getProxyList() {
        ProxyExample example = new ProxyExample();
        example.setOrderByClause("update_time desc");
        return proxyMapper.selectByExample(example);
    }

    public List<Proxy> getProxyListWithRequest(ProxyRequest request) {
        return extProxyMapper.getProxyListWithRequest(request);
    }

    public void deleteProxy(int proxyId, LoginUser loginUser) {
        Proxy proxy = proxyMapper.selectByPrimaryKey(proxyId);
        proxyMapper.deleteByPrimaryKey(proxyId);
        operationLogService.log(loginUser, proxy.getProxyIp(), proxy.getProxyIp(), ResourceTypeConstants.PROXY.name(), ResourceOperation.DELETE, "i18n_delete_proxy");
    }

    public void updateProxy(Proxy proxy, LoginUser loginUser) {
        proxy.setUpdateTime(System.currentTimeMillis());
        proxyMapper.updateByPrimaryKeySelective(proxy);
        operationLogService.log(loginUser, proxy.getProxyIp(), proxy.getProxyIp(), ResourceTypeConstants.PROXY.name(), ResourceOperation.UPDATE, "i18n_update_proxy");
    }

    public void deleteProxys(List<Integer> ids, LoginUser loginUser) throws Exception {
        ids.forEach(id -> {
            try {
                deleteProxy(id, loginUser);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

}
