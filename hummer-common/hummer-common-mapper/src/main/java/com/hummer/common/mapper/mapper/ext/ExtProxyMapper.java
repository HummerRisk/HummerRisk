package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.domain.Proxy;
import com.hummer.common.mapper.domain.request.proxy.ProxyRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtProxyMapper {

    List<Proxy> getProxyListWithRequest(@Param("request") ProxyRequest request);

}
