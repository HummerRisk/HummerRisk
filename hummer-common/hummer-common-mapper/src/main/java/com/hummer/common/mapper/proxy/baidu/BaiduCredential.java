package com.hummer.common.mapper.proxy.baidu;
public class BaiduCredential {
	private String AccessKeyId;
	private String SecretAccessKey;
	private String Endpoint;

	public BaiduCredential() {
	}

	//目前支持“华北-北京”、“华南-广州”和“华东-苏州”三个区域。
	// 北京：https://bcc.bj.baidubce.com
	// 广州：https://bcc.gz.baidubce.com
	// 苏州：https://bcc.su.baidubce.com
	// 香港：https://bcc.hkg.baidubce.com
	// 武汉：https://bcc.fwh.baidubce.com
	// 保定：https://bcc.bd.baidubce.com
	// 新加坡：https://bcc.sin.baidubce.com
	// 上海：https://bcc.fsh.baidubce.com
	public BaiduCredential(String AccessKeyId, String SecretAccessKey, String Endpoint) {
		this.AccessKeyId = AccessKeyId;
		this.SecretAccessKey = SecretAccessKey;
		this.Endpoint = Endpoint;
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

	public String getEndpoint() {
		return Endpoint;
	}

	public void setEndpoint(String Endpoint) {
		this.Endpoint = Endpoint;
	}
}
