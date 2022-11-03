package com.hummerrisk.oss.config;

import com.hummerrisk.oss.constants.OSSConstants;
import com.hummerrisk.oss.provider.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class OssManager implements ApplicationRunner {
    private static Map<String, Object> ossProviders = new HashMap<String, Object>();


    public static Map<String, Object> getOssProviders() {
        return ossProviders;
    }

    public static void setOssProviders(Map<String, Object> ossProviders) {
        OssManager.ossProviders = ossProviders;
    }

    private void init() {
        ossProviders.put(OSSConstants.aliyun, new AliyunProvider());
        ossProviders.put(OSSConstants.huawei, new HuaweiProvider());
        ossProviders.put(OSSConstants.tencent, new QcloudProvider());
        ossProviders.put(OSSConstants.aws, new AwsProvider());
        ossProviders.put(OSSConstants.qiniu, new QiniuProvider());
        ossProviders.put(OSSConstants.qingcloud, new QingcloudProvider());
        ossProviders.put(OSSConstants.ucloud, new UcloudProvider());
    }

    @Override
    public void run(ApplicationArguments args) {
        init();
    }

}
