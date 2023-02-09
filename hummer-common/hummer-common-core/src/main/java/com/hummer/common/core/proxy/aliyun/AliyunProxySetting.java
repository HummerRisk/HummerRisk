package com.hummer.common.core.proxy.aliyun;

public class AliyunProxySetting {
	private String httpAddr;
	private String httpsAddr;
	private String noProxy;

	public String getNoProxy() {
		return noProxy;
	}

	public void setNoProxy(String noProxy) {
		this.noProxy = noProxy;
	}

	public String getHttpAddr() {
		return httpAddr;
	}

	public void setHttpAddr(String httpAddr) {
		this.httpAddr = httpAddr;
	}

	public String getHttpsAddr() {
		return httpsAddr;
	}

	public void setHttpsAddr(String httpsAddr) {
		this.httpsAddr = httpsAddr;
	}
}
