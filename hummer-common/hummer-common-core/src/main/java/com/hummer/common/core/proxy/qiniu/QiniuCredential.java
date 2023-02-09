package com.hummer.common.core.proxy.qiniu;
public class QiniuCredential {
	private String accessKey;
	private String secretKey;
	private String bucket;

	public QiniuCredential() {
	}

	public QiniuCredential(String accessKey, String secretKey, String bucket) {
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.bucket = bucket;
	}


	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}
}
