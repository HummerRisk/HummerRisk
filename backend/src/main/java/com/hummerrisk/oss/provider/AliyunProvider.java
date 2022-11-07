package com.hummerrisk.oss.provider;


import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.BucketStat;
import com.hummerrisk.base.domain.OssBucket;
import com.hummerrisk.base.domain.OssWithBLOBs;
import com.hummerrisk.proxy.aliyun.AliyunRequest;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class AliyunProvider implements OssProvider {

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
            Double size = (double) bucketStat.getStorageSize() / 1024 / 1024;
            ossBucket.setSize(size.longValue());
            ossBucket.setObjectNumber(bucketStat.getObjectCount());
        } else {
            ossBucket.setSize(0L);
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
