package com.hummerrisk.oss.provider;


import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import com.hummerrisk.base.domain.OssBucket;
import com.hummerrisk.base.domain.OssWithBLOBs;
import com.hummerrisk.oss.constants.ObjectTypeConstants;
import com.hummerrisk.oss.dto.BucketObjectDTO;
import com.hummerrisk.proxy.aliyun.AliyunRequest;
import com.hummerrisk.service.SysListener;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
            ossBucket.setSize("0");
            ossBucket.setObjectNumber(0L);
        }

        return ossBucket;
    }

    private static OSSClient getOSSClient(OssWithBLOBs ossWithBLOBs) {
        AliyunRequest req = new AliyunRequest();
        req.setCredential(ossWithBLOBs.getCredential());
        return req.getAliyunOSSClient();
    }

    @Override
    public List<BucketObjectDTO> getBucketObjects(OssBucket bucket, OssWithBLOBs account, String prefix) {
        OSSClient ossClient = getOSSClient(account);
        ossClient.setEndpoint(bucket.getExtranetEndpoint());
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
        listObjectsRequest.setBucketName(bucket.getBucketName());
        listObjectsRequest.setMaxKeys(1000);
        listObjectsRequest.setDelimiter("/");
        if (StringUtils.isNotEmpty(prefix)) {
            listObjectsRequest.setPrefix(prefix);
        }
        List<BucketObjectDTO> objects = new ArrayList<>();

        ObjectListing objectListing = ossClient.listObjects(listObjectsRequest);
        if (!hasPrefix(objectListing.getObjectSummaries(), objectListing.getPrefix()) && ObjectUtils.isNotEmpty(objectListing.getPrefix())) {
            OSSObjectSummary ossObjectSummary = new OSSObjectSummary();
            ossObjectSummary.setBucketName(bucket.getBucketName());
            ossObjectSummary.setKey(objectListing.getPrefix());
            objectListing.setObjectSummaries(Collections.singletonList(ossObjectSummary));
        }
        objects.addAll(convertToBucketFolder(bucket, objectListing.getCommonPrefixes(), prefix));
        objects.addAll(convertToBucketObject(ossClient, bucket, objectListing.getObjectSummaries(), prefix));

        while (objectListing.getNextMarker() != null) {
            objectListing = ossClient.listObjects(listObjectsRequest);
            if (!hasPrefix(objectListing.getObjectSummaries(), objectListing.getPrefix()) && ObjectUtils.isNotEmpty(objectListing.getPrefix())) {
                OSSObjectSummary ossObjectSummary = new OSSObjectSummary();
                ossObjectSummary.setBucketName(bucket.getBucketName());
                ossObjectSummary.setKey(objectListing.getPrefix());
                objectListing.setObjectSummaries(Collections.singletonList(ossObjectSummary));
            }
            objects.addAll(convertToBucketObject(ossClient, bucket, objectListing.getObjectSummaries(), prefix));
            objects.addAll(convertToBucketFolder(bucket, objectListing.getCommonPrefixes(), prefix));
        }
        ossClient.shutdown();
        List<BucketObjectDTO> bucketObjectDTOS = new ArrayList<>();
        for (BucketObjectDTO object : objects) {
            if (object.getObjectType().equals(ObjectTypeConstants.BACK.name())) {
                bucketObjectDTOS.add(0, object);
            } else {
                bucketObjectDTOS.add(object);
            }
        }
        return bucketObjectDTOS;
    }

    private boolean hasPrefix(List<OSSObjectSummary> ossObjectSummaries, String prefix) {
        for (OSSObjectSummary ossObjectSummary : ossObjectSummaries) {
            if (ossObjectSummary.getKey().equalsIgnoreCase(prefix)) {
                return true;
            }
        }
        return false;
    }

    private List<BucketObjectDTO> convertToBucketFolder(OssBucket bucket, List<String> commonPrefixes, String prefix) {
        List<BucketObjectDTO> objects = new ArrayList<>();
        for (String commonPrefix : commonPrefixes) {
            BucketObjectDTO bucketObject = new BucketObjectDTO();
            bucketObject.setBucketId(bucket.getId());
            if (StringUtils.isNotEmpty(prefix)) {
                bucketObject.setObjectName(commonPrefix.substring(prefix.length(), commonPrefix.length()));
            } else {
                bucketObject.setObjectName(commonPrefix);
            }
            bucketObject.setId(commonPrefix);
            bucketObject.setObjectType(ObjectTypeConstants.DIR.name());
            objects.add(bucketObject);
        }
        return objects;
    }

    private List<BucketObjectDTO> convertToBucketObject(OSSClient ossClient, OssBucket bucket, List<OSSObjectSummary> ossObjectSummaries, String prefix) {
        List<BucketObjectDTO> objects = new ArrayList<>();
        for (OSSObjectSummary ossObjectSummary : ossObjectSummaries) {
            BucketObjectDTO bucketObject = new BucketObjectDTO();
            bucketObject.setBucketId(bucket.getId());

            if (StringUtils.isNotEmpty(prefix) && ossObjectSummary.getKey().equals(prefix)) {
                String[] dirs = prefix.split("/");
                if (dirs.length == 1) {
                    bucketObject.setId("/");
                    bucketObject.setObjectName(prefix);
                } else {
                    String lastDir = dirs[dirs.length - 1];
                    bucketObject.setId(prefix.substring(0, prefix.length() - lastDir.length() - 1));
                    bucketObject.setObjectName(lastDir + "/");
                }
                bucketObject.setObjectType(ObjectTypeConstants.BACK.name());
            }

            if (!ossObjectSummary.getKey().endsWith("/")) {
                bucketObject.setId(ossObjectSummary.getKey());
                if (StringUtils.isNotEmpty(prefix)) {
                    bucketObject.setObjectName(ossObjectSummary.getKey().substring(prefix.length(), ossObjectSummary.getKey().length()));
                } else {
                    bucketObject.setObjectName(ossObjectSummary.getKey());
                }
                bucketObject.setObjectType(ObjectTypeConstants.FILE.name());
                bucketObject.setObjectSize(SysListener.changeFlowFormat(ossObjectSummary.getSize()));
                if(ossObjectSummary.getStorageClass()!=null) bucketObject.setStorageClass(ossObjectSummary.getStorageClass());
                if(ossObjectSummary.getLastModified()!=null) bucketObject.setLastModified(ossObjectSummary.getLastModified().getTime());
                bucketObject.setDownloadUrl(generatePresignedUrl(ossClient, bucket.getBucketName(), ossObjectSummary.getKey()).toString());
            }

            objects.add(bucketObject);
        }
        return objects;
    }

    private URL generatePresignedUrl(OSSClient ossClient, String bucketName, final String objectId) {
        Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
        // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
        return ossClient.generatePresignedUrl(bucketName, objectId, expiration);
    }


}
