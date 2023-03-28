package com.hummer.common.core.proxy.aws;

public class AWSCredential {
	private String accessKey;
	private String secretKey;
	private String isSessionCredential;
	private String awsSessionToken;

	public String getAwsSessionToken() {
		return awsSessionToken;
	}

	public void setAwsSessionToken(String awsSessionToken) {
		this.awsSessionToken = awsSessionToken;
	}

	public String getIsSessionCredential() {
		return isSessionCredential;
	}

	public void setIsSessionCredential(String isSessionCredential) {
		this.isSessionCredential = isSessionCredential;
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

	public Boolean checkIsSessionCredential(){
		if(this.isSessionCredential.equals("True")){
			return true;
		}else {
			return false;
		}
	}
}
