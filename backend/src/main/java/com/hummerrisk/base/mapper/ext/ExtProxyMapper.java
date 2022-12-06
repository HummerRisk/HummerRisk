package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.Proxy;
import com.hummerrisk.controller.request.proxy.ProxyRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtProxyMapper {

    List<Proxy> getProxyListWithRequest(@Param("request") ProxyRequest request);

}
