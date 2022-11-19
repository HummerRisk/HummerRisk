package com.hummerrisk.proxy.tencent;
public class QCloudCredential {
	private String secretId;
	private String secretKey;
	private String token;
	private String APPID;

	public QCloudCredential() {
	}

	public QCloudCredential(String secretId, String secretKey) {
		this(secretId, secretKey, "");
	}

	public QCloudCredential(String secretId, String secretKey, String token) {
		this.secretId = secretId;
		this.secretKey = secretKey;
		this.token = token;
	}

	public void setSecretId(String secretId) {
		this.secretId = secretId;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getSecretId() {
		return this.secretId;
	}

	public String getSecretKey() {
		return this.secretKey;
	}

	public String getToken() {
		return this.token;
	}

	public String getAPPID() {
		return APPID;
	}

	public void setAPPID(String APPID) {
		this.APPID = APPID;
	}
}
