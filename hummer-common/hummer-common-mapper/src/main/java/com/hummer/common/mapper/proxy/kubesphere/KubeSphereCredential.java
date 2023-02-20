package com.hummer.common.mapper.proxy.kubesphere;
public class KubeSphereCredential {
	private String url;
	private String token;

	public KubeSphereCredential() {
	}

	public KubeSphereCredential(String url, String token) {
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
