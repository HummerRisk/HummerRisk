package com.hummer.common.core.proxy.huawei;

import com.google.gson.Gson;
import com.hummer.common.core.proxy.Request;

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
