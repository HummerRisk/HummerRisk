package com.hummer.common.core.proxy.xray;
public class XrayCredential {
	private String description;
	private String targetAddress;

	public XrayCredential() {
	}

	public XrayCredential(String description, String targetAddress) {
		this.description = description;
		this.targetAddress = targetAddress;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTargetAddress() {
		return targetAddress;
	}

	public void setTargetAddress(String targetAddress) {
		this.targetAddress = targetAddress;
	}
}
