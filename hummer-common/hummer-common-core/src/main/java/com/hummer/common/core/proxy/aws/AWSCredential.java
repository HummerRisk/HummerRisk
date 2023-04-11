package com.hummer.common.core.proxy.aws;

public class AWSCredential {
	private String accessKey;
	private String secretKey;
	private Boolean isSessionCredential;
	private String awsSessionToken;

	public String getAwsSessionToken() {
		return awsSessionToken;
	}

	public void setAwsSessionToken(String awsSessionToken) {
		this.awsSessionToken = awsSessionToken;
	}

	public Boolean getSessionCredential() {
		return isSessionCredential;
	}

	public void setSessionCredential(Boolean sessionCredential) {
		isSessionCredential = sessionCredential;
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
		if(this.isSessionCredential){
			return true;
		}else {
			return false;
		}
	}
}
