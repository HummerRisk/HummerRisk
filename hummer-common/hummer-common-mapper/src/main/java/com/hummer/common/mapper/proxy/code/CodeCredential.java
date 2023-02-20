package com.hummer.common.mapper.proxy.code;
public class CodeCredential {
	private String url;

	private String token;

	private String branch;

	private String tag;

	private String commit;

	public CodeCredential() {
	}

	public CodeCredential(String url, String token, String branch, String tag, String commit) {
		this.url = url;
		this.token = token;
		this.branch = branch;
		this.tag = tag;
		this.commit = commit;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getCommit() {
		return commit;
	}

	public void setCommit(String commit) {
		this.commit = commit;
	}
}
