package com.hummer.common.core.utils;

import com.google.gson.Gson;
import com.hummer.common.core.proxy.Request;
import com.hummer.common.core.proxy.huawei.BusiRequest;
import com.hummer.common.core.proxy.huawei.HuaweiCloudCredential;
import com.hummer.common.core.proxy.huawei.IamRequest;

/**
 * @author harris
 */
public class RequestUtil {

    public static IamRequest request2IamRequest(Request request){
        String credential = request.getCredential();
        IamRequest iamRequest = new IamRequest();
        iamRequest.setCredential(credential);
        if (request.getRegionId() != null && request.getRegionId().trim().length() > 0) {
            iamRequest.setRegionId(request.getRegionId());
        }
        HuaweiCloudCredential cloudCredential = new Gson().fromJson(credential, HuaweiCloudCredential.class);
        iamRequest.setHuaweiCloudCredential(cloudCredential);
        return iamRequest;
    }

    public static BusiRequest request2BusiRequest(Request request, String busiType){
        String credential = request.getCredential();
        BusiRequest busiRequest = new BusiRequest();
        busiRequest.setCredential(credential);
        if (request.getRegionId() != null && request.getRegionId().trim().length() > 0) {
            busiRequest.setRegionId(request.getRegionId());
        }
        HuaweiCloudCredential cloudCredential = new Gson().fromJson(credential, HuaweiCloudCredential.class);
        busiRequest.setHuaweiCloudCredential(cloudCredential);
        busiRequest.setBusiType(busiType);
        return busiRequest;
    }
}
