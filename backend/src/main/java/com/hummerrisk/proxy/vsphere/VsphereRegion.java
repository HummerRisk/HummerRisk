package com.hummerrisk.proxy.vsphere;

public class VsphereRegion {
	private String name;
	private String description;
	public VsphereRegion() {}

	public VsphereRegion(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
