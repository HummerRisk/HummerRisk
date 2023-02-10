package com.hummer.common.core.proxy.ksyun;

import client.iam.getuser.v20151101.GetUserClient;
import client.iam.listusers.v20151101.ListUsersClient;
import client.trade.describeinstances.v20200114.DescribeInstancesClient;
import com.google.gson.Gson;
import com.hummer.common.core.proxy.Request;
import common.Credential;

public class KsyunRequest extends Request {
    private KsyunCredential ksyunCredential;

    public KsyunRequest() {
        super("", "");
    }

    public KsyunRequest(Request req) {
        super(req.getCredential(), req.getRegionId());
    }
    public KsyunRequest(KsyunCredential ksyunCredential) {
        this.ksyunCredential = ksyunCredential;
    }
    public KsyunCredential getKsyunRequestCredential() {
        if (ksyunCredential == null) {
            ksyunCredential = new Gson().fromJson(getCredential(), KsyunCredential.class);
        }
        return ksyunCredential;
    }

    public DescribeInstancesClient getKESClient(){
        DescribeInstancesClient client = new DescribeInstancesClient(getKsyunCredential());
        return client;
    }

    public GetUserClient getUserClient(){
        return new GetUserClient(getKsyunCredential());
    }

    public ListUsersClient getListUserClient(){
        return new ListUsersClient(getKsyunCredential());
    }

    public Credential getKsyunCredential() {
        String accessKeyId = this.getKsyunRequestCredential().getAccessKey();
        String secretAccessKey = this.getKsyunRequestCredential().getSecretAccessKey();
        String defaultRegionId = "cn-beijing-6";
        if (getRegionId() != null && getRegionId().trim().length() > 0) {
            defaultRegionId = getRegionId();
        }
        return new Credential(accessKeyId, secretAccessKey, defaultRegionId);
    }
}
