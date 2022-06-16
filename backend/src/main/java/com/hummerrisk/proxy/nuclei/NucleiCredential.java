package com.hummerrisk.proxy.nuclei;
public class NucleiCredential {
	private String description;
	private String targetAddress;

	public NucleiCredential() {
	}

	public NucleiCredential(String description, String targetAddress) {
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
