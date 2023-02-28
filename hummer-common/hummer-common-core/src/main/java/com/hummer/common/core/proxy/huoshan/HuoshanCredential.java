package com.hummer.common.core.proxy.huoshan;
public class HuoshanCredential {
	private String AccessKeyId;
	private String SecretAccessKey;

	public HuoshanCredential() {
	}

	public HuoshanCredential(String AccessKeyId, String SecretAccessKey) {
		this.AccessKeyId = AccessKeyId;
		this.SecretAccessKey = SecretAccessKey;
	}

	public String getAccessKeyId() {
		return AccessKeyId;
	}

	public void setAccessKeyId(String AccessKeyId) {
		this.AccessKeyId = AccessKeyId;
	}

	public String getSecretAccessKey() {
		return SecretAccessKey;
	}

	public void setSecretAccessKey(String SecretAccessKey) {
		this.SecretAccessKey = SecretAccessKey;
	}
}
