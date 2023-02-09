package com.hummer.common.core.proxy.ucloud;
public class UCloudCredential {
	private String UcloudPublicKey;
	private String UcloudPrivateKey;

	public UCloudCredential() {
	}

	public UCloudCredential(String UcloudPublicKey, String UcloudPrivateKey) {
		this.UcloudPublicKey = UcloudPublicKey;
		this.UcloudPrivateKey = UcloudPrivateKey;
	}

	public String getUcloudPublicKey() {
		return UcloudPublicKey;
	}

	public void setUcloudPublicKey(String UcloudPublicKey) {
		this.UcloudPublicKey = UcloudPublicKey;
	}

	public String getUcloudPrivateKey() {
		return UcloudPrivateKey;
	}

	public void setUcloudPrivateKey(String UcloudPrivateKey) {
		this.UcloudPrivateKey = UcloudPrivateKey;
	}

}
