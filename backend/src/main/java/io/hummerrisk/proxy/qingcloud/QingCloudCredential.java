package io.hummerrisk.proxy.qingcloud;
public class QingCloudCredential {
	private String AccessKeyId;
	private String SecretAccessKey;

	public QingCloudCredential() {
	}

	public QingCloudCredential(String AccessKeyId, String SecretAccessKey) {
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
