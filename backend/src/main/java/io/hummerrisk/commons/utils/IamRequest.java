package io.hummerrisk.commons.utils;

import com.google.gson.Gson;
import io.hummerrisk.proxy.Request;
import io.hummerrisk.proxy.huawei.HuaweiCloudCredential;

/**
 * @Author harris
 **/
public class IamRequest extends Request {

    private HuaweiCloudCredential huaweiCloudCredential;


    public HuaweiCloudCredential getHuaweiCloudCredential() {
        if (null == huaweiCloudCredential){
            huaweiCloudCredential = new Gson().fromJson(getCredential(), HuaweiCloudCredential.class);
        }
        return huaweiCloudCredential;
    }

    public void setHuaweiCloudCredential(HuaweiCloudCredential huaweiCloudCredential) {
        this.huaweiCloudCredential = huaweiCloudCredential;
    }
}
