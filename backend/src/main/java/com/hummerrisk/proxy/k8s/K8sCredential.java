package com.hummerrisk.proxy.k8s;
public class K8sCredential {
	private String url;
	private String token;
	private String ip;
	private String port;

	public K8sCredential() {
	}

	public K8sCredential(String url, String token) {
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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
}
