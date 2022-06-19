package com.hummerrisk.proxy.tsunami;
public class TsunamiCredential {
	private String description;
	private String ip;

	public TsunamiCredential() {
	}

	public TsunamiCredential(String description, String ip) {
		this.description = description;
		this.ip = ip;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}
