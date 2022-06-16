package com.hummerrisk.commons.utils;

import com.google.gson.Gson;
import com.hummerrisk.proxy.Request;
import com.hummerrisk.proxy.huawei.HuaweiCloudCredential;

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
