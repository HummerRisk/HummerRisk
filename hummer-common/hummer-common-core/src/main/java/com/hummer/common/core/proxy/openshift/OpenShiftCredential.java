package com.hummer.common.core.proxy.openshift;

public class OpenShiftCredential {
	private String url;
	private String master;
	private String userName;
	private String password;

	public OpenShiftCredential() {
	}

	public OpenShiftCredential(String url, String master, String userName, String password) {
		this.url = url;
		this.master = master;
		this.userName = userName;
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
