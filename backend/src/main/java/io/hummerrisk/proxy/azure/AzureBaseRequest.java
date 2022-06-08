package io.hummerrisk.proxy.azure;

import com.google.gson.Gson;
import io.hummerrisk.base.domain.Proxy;
import io.hummerrisk.commons.exception.PluginException;
import io.hummerrisk.proxy.Request;

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
