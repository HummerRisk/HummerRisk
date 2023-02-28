package com.hummer.common.core.proxy.rancher;
public class RancherCredential {
	private String url;
	private String token;

	public RancherCredential() {
	}

	public RancherCredential(String url, String token) {
		this.url = url;
		this.token = token;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
