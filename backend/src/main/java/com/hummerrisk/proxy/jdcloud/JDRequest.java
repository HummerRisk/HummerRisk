package com.hummerrisk.proxy.jdcloud;

import com.google.gson.Gson;
import com.hummerrisk.proxy.Request;
import com.jdcloud.sdk.auth.CredentialsProvider;
import com.jdcloud.sdk.auth.StaticCredentialsProvider;
import com.jdcloud.sdk.http.HttpRequestConfig;
import com.jdcloud.sdk.http.Protocol;
import com.jdcloud.sdk.service.iam.client.IamClient;
import com.jdcloud.sdk.service.vm.client.VmClient;

public class JDRequest extends Request {
    private JDCloudCredential jdCloudCredential;

    public JDRequest() {
        super("", "");
    }

    public JDRequest(Request req) {
        super(req.getCredential(), req.getRegionId());
    }
    public JDRequest(JDCloudCredential jdCloudCredential) {
        this.jdCloudCredential = jdCloudCredential;
    }
    public JDCloudCredential getJdCloudCredential() {
        if (jdCloudCredential == null) {
            jdCloudCredential = new Gson().fromJson(getCredential(), JDCloudCredential.class);
        }
        return jdCloudCredential;
    }

    public VmClient getVmJdClient(){
        String accessKeyId = this.getJdCloudCredential().getAccessKey();
        String secretAccessKey = this.getJdCloudCredential().getSecretAccessKey();
        CredentialsProvider credentialsProvider = new StaticCredentialsProvider(accessKeyId, secretAccessKey);
        //2. 创建Client
        return VmClient.builder()
                .credentialsProvider(credentialsProvider)
                .httpRequestConfig(new HttpRequestConfig.Builder().protocol(Protocol.HTTPS).build()) //默认为HTTPS
                .build();
    }

    public IamClient getIAMClient(){
        String accessKeyId = this.getJdCloudCredential().getAccessKey();
        String secretAccessKey = this.getJdCloudCredential().getSecretAccessKey();
        CredentialsProvider credentialsProvider = new StaticCredentialsProvider(accessKeyId, secretAccessKey);
        return IamClient.builder().credentialsProvider(credentialsProvider)
                .httpRequestConfig(new HttpRequestConfig.Builder().protocol(Protocol.HTTP).build()).build();
    }

}
