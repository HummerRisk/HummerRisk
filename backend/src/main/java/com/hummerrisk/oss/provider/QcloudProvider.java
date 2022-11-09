package com.hummerrisk.oss.provider;


import com.hummerrisk.base.domain.OssBucket;
import com.hummerrisk.base.domain.OssWithBLOBs;

import java.util.ArrayList;
import java.util.List;

public class QcloudProvider implements OssProvider {

    @Override
    public String policyModel() {
        return "{\n" +
                "    \"version\": \"2.0\",\n" +
                "    \"statement\": [\n" +
                "        {\n" +
                "            \"effect\": \"allow\",\n" +
                "            \"action\": [\n" +
                "                \"name/cos:*\"\n" +
                "            ],\n" +
                "            \"resource\": [\n" +
                "                \"qcs::cos:REGION:uid/UID:BUCKET_NAME\"\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}";
    }


    @Override
    public List<OssBucket> getOssBucketList(OssWithBLOBs ossAccount) {
        List<OssBucket> resultList = new ArrayList<>();
        return resultList;
    }

}

