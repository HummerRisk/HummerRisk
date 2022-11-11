package com.hummerrisk.oss.provider;


import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.BucketStat;
import com.hummerrisk.base.domain.OssBucket;
import com.hummerrisk.base.domain.OssWithBLOBs;
import com.hummerrisk.proxy.aliyun.AliyunRequest;
import com.hummerrisk.service.SysListener;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class AliyunProvider implements OssProvider {

    @Override
    public String policyModel() {
        return "{\n" +
                "    \"Version\": \"1\",\n" +
                "    \"Statement\": [\n" +
                "        {\n" +
                "            \"Action\": \"oss:*\",\n" +
                "            \"Effect\": \"Allow\",\n" +
                "            \"Resource\": [\n" +
                "                \"acs:oss:*:*:BUCKET_NAME\"\n" +
                "            ],\n" +
                "            \"Condition\": {}\n" +
                "        }\n" +
                "    ]\n" +
                "}";
    }

    @Override
    public List<OssBucket> getOssBucketList(OssWithBLOBs ossWithBLOBs) {
        OSSClient ossClient = getOSSClient(ossWithBLOBs);
        List<Bucket> list = ossClient.listBuckets();
        List<OssBucket> resultList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (Bucket bucket : list) {
                ossClient.setEndpoint(bucket.getExtranetEndpoint());
                String cannedAcl = ossClient.getBucketAcl(bucket.getName()).getCannedACL().name();
                BucketStat bucketStat = ossClient.getBucketStat(bucket.getName());
                resultList.add(setBucket(bucket, ossWithBLOBs, cannedAcl, bucketStat));
            }
        }
        ossClient.shutdown();
        return resultList;
    }

    private static OssBucket setBucket(Bucket bucket, OssWithBLOBs ossWithBLOBs, String cannedAcl, BucketStat bucketStat) {
        OssBucket ossBucket = new OssBucket();
        ossBucket.setCreateTime(bucket.getCreationDate().getTime());
        if (bucket.getOwner() != null) {
            ossBucket.setOwnerName(bucket.getOwner().getDisplayName());
            ossBucket.setOwnerId(bucket.getOwner().getId());
        }
        ossBucket.setExtranetEndpoint(bucket.getExtranetEndpoint());
        ossBucket.setIntranetEndpoint(bucket.getIntranetEndpoint());
        ossBucket.setLocation(bucket.getLocation());
        ossBucket.setBucketName(bucket.getName());
        ossBucket.setStorageClass(bucket.getStorageClass().name());
        ossBucket.setOssId(ossWithBLOBs.getId());
        ossBucket.setCannedAcl(cannedAcl);
        ossBucket.setDomainName(bucket.getName() + "." + bucket.getExtranetEndpoint());
        if (bucketStat != null) {
            String size = SysListener.changeFlowFormat(bucketStat.getStorageSize());
            ossBucket.setSize(size);
            ossBucket.setObjectNumber(bucketStat.getObjectCount());
        } else {
            ossBucket.setSize("KB");
            ossBucket.setObjectNumber(0L);
        }

        return ossBucket;
    }

    private static OSSClient getOSSClient(OssWithBLOBs ossWithBLOBs) {
        AliyunRequest req = new AliyunRequest();
        req.setCredential(ossWithBLOBs.getCredential());
        return req.getAliyunOSSClient();
    }

}
