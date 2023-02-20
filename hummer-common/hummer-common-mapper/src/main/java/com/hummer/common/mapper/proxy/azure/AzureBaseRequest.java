package com.hummer.common.mapper.proxy.azure;

import com.google.gson.Gson;
import com.hummer.common.mapper.domain.Proxy;
import com.hummer.common.core.exception.PluginException;
import com.hummer.common.mapper.proxy.Request;

public class AzureBaseRequest extends Request {
	private AzureCredential azureCredential;

	public AzureBaseRequest() {
		super("", "");
	}

	public AzureBaseRequest(Request req) {
		super(req.getCredential(), req.getRegionId());
		setCredential(req.getCredential());
		setRegionId(req.getRegionId());
	}

	public AzureClient getAzureClient(Proxy proxy) throws PluginException {
		try {
			if(azureCredential == null) {
				azureCredential = new Gson().fromJson(getCredential(), AzureCredential.class);
			}
			return new AzureClient(azureCredential, proxy);
		} catch (Exception e) {
			throw new PluginException(e);
		}
	}

}
