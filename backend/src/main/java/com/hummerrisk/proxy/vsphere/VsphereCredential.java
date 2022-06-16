package com.hummerrisk.proxy.vsphere;
public class VsphereCredential {
	private String vUserName;
	private String vPassword;
	private String vEndPoint;
	private boolean useContentLibrary = false;

	public boolean isUseContentLibrary() {
		return useContentLibrary;
	}

	public void setUseContentLibrary(boolean useContentLibrary) {
		this.useContentLibrary = useContentLibrary;
	}

	public VsphereCredential() {
	}

	public VsphereCredential(String vUserName, String vPassword, String vEndPoint) {
		this.vUserName = vUserName;
		this.vPassword = vPassword;
		this.vEndPoint = vEndPoint;
	}

	public String getvUserName() {
		return vUserName;
	}

	public void setvUserName(String vUserName) {
		this.vUserName = vUserName;
	}

	public String getvPassword() {
		return vPassword;
	}

	public void setvPassword(String vPassword) {
		this.vPassword = vPassword;
	}

	public String getvEndPoint() {
		return vEndPoint;
	}

	public void setvEndPoint(String vEndPoint) {
		this.vEndPoint = vEndPoint;
	}
}
