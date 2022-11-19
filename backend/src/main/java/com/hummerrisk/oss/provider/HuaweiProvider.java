package com.hummerrisk.oss.provider;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hummerrisk.base.domain.OssBucket;
import com.hummerrisk.base.domain.OssWithBLOBs;
import com.hummerrisk.commons.utils.ReadFileUtils;
import com.hummerrisk.oss.constants.OSSConstants;
import com.hummerrisk.oss.constants.ObjectTypeConstants;
import com.hummerrisk.oss.dto.BucketMetric;
import com.hummerrisk.oss.dto.BucketObjectDTO;
import com.hummerrisk.oss.dto.OssRegion;
import com.hummerrisk.proxy.huawei.HuaweiCloudCredential;
import com.hummerrisk.service.SysListener;
import com.obs.services.ObsClient;
import com.obs.services.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HuaweiProvider implements OssProvider {

    private static final String BASE_REGION_DIC = "support/regions/";

    private static final String BASE_STORAGE_CLASS_DIC = "support/storageClass/";
    private static final String JSON_EXTENSION = ".json";

    @Override
    public String policyModel() {
        return "";
    }

    @Override
    public List<OssBucket> getOssBucketList(OssWithBLOBs ossAccount) throws Exception {
        ListBucketsRequest request = new ListBucketsRequest();
        request.setQueryLocation(true);
        ObsClient obsClient = getObsClient(ossAccount);
        List<ObsBucket> buckets = obsClient.listBuckets(request);
        List<OssBucket> resultList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(buckets)) {
            for (ObsBucket obsBucket : buckets) {
                OssBucket bucket = setBucket(obsBucket, ossAccount);
                BucketMetric bucketMetric = getBucketMetric(obsBucket, ossAccount);
                bucket.setSize(SysListener.changeFlowFormat(bucketMetric.getSize()));
                bucket.setObjectNumber(bucketMetric.getObjectNumber());
                resultList.add(bucket);
            }
        }
        obsClient.close();
        return resultList;
    }

    public BucketMetric getBucketMetric(ObsBucket bucket, OssWithBLOBs ossAccount) throws Exception {
        ObsClient obsClient = getObsClient(ossAccount, bucket);
        BucketStorageInfo storageInfo = obsClient.getBucketStorageInfo(bucket.getBucketName());
        BucketMetric bucketMetric = new BucketMetric();
        bucketMetric.setSize(storageInfo.getSize());
        bucketMetric.setObjectNumber(storageInfo.getObjectNumber());
        obsClient.close();
        return bucketMetric;
    }

    private OssBucket setBucket(ObsBucket bucket, OssWithBLOBs ossAccount) throws Exception {
        OssBucket bucketDTO = new OssBucket();
        if (bucket.getCreationDate() != null) {
            bucketDTO.setCreateTime(bucket.getCreationDate().getTime());
        }
        bucketDTO.setBucketName(bucket.getBucketName());
        bucketDTO.setOssId(ossAccount.getId());
        setAcl(ossAccount, bucket, bucketDTO);
        BucketStoragePolicyConfiguration a = getBucketStorageClass(ossAccount, bucket);
        bucketDTO.setStorageClass(a.getBucketStorageClass().name());
        bucketDTO.setLocation(bucket.getLocation());
        bucketDTO.setDomainName(getDomainame(ossAccount, bucket.getLocation(), bucket.getBucketName()));
        bucketDTO.setSize("0");
        bucketDTO.setObjectNumber(0L);
        return bucketDTO;
    }

    private String getDomainame(OssWithBLOBs ossAccount, String region, String bucketName) throws Exception {
        List<OssRegion> ossRegions = getOssRegions(ossAccount);
        for (OssRegion ossRegion : ossRegions) {
            if (ossRegion.getRegionId().equals(region)) {
                return bucketName + "." + ossRegion.getExtranetEndpoint();
            }
        }
        return null;
    }

    public BucketStoragePolicyConfiguration getBucketStorageClass(OssWithBLOBs ossAccount, ObsBucket bucket) {
        return getObsClient(ossAccount, bucket).getBucketStoragePolicy(bucket.getBucketName());
    }

    public void setAcl(OssWithBLOBs ossAccount, ObsBucket bucket, OssBucket bucketDTO) {
        AccessControlList accessControlList = getObsClient(ossAccount, bucket).getBucketAcl(bucket.getBucketName());
        boolean Private = true;
        boolean PublicRead = false;
        boolean PublicWrite = false;

        for (GrantAndPermission grant : accessControlList.getGrants()) {
            if(grant.getPermission().toString().equalsIgnoreCase("READ") && grant.getGrantee().toString().contains("ALL_USERS")){
                PublicRead = true;
                Private = false;
            }
            if(grant.getPermission().toString().equalsIgnoreCase("WRITE") && grant.getGrantee().toString().contains("ALL_USERS")){
                PublicWrite  =true;
                Private = false;
            }
        }
        if(Private){
            bucketDTO.setCannedAcl("private");
        }
        if(PublicRead){
            bucketDTO.setCannedAcl("public-read");
        }
        if(PublicRead && PublicWrite){
            bucketDTO.setCannedAcl("public-read-write");
        }

    }

    private ObsClient getObsClient(OssWithBLOBs ossAccount, OssBucket bucket) {
        HuaweiCloudCredential object = JSON.parseObject(ossAccount.getCredential(), HuaweiCloudCredential.class);
        String endPoint = getAliEndPointByRegionId(ossAccount, bucket.getLocation());
        if (StringUtils.isNotBlank(endPoint)) {
            object.setEndPoint(endPoint);
        }
        return new ObsClient(object.getAk(), object.getSk(), object.getEndPoint());
    }

    private ObsClient getObsClient(OssWithBLOBs ossAccount, ObsBucket bucket) {
        HuaweiCloudCredential object = JSON.parseObject(ossAccount.getCredential(), HuaweiCloudCredential.class);
        String endPoint = getAliEndPointByRegionId(ossAccount, bucket.getLocation());
        if (StringUtils.isNotBlank(endPoint)) {
            object.setEndPoint(endPoint);
        }
        return new ObsClient(object.getAk(), object.getSk(), object.getEndPoint());
    }

    private String getAliEndPointByRegionId(OssWithBLOBs ossAccount, String regionId) {
        try {
            List<OssRegion> ossRegions = getOssRegions(ossAccount);
            for (OssRegion ossRegion : ossRegions) {
                if (ossRegion.getRegionId().equals(regionId)) {
                    return ossRegion.getExtranetEndpoint();
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ObsClient getObsClient(OssWithBLOBs account) {
        HuaweiCloudCredential credential = JSON.parseObject(account.getCredential(), HuaweiCloudCredential.class);
        return getObsClient(JSON.toJSONString(credential));
    }

    private ObsClient getObsClient(String credential) {
        HuaweiCloudCredential object = JSON.parseObject(credential, HuaweiCloudCredential.class);
        if (StringUtils.isBlank(object.getEndPoint())) {
            OssWithBLOBs account = new OssWithBLOBs();
            account.setPluginId(OSSConstants.huawei);
            try {
                List<OssRegion> list = getOssRegions(account);
                object.setEndPoint(list.get(0).getExtranetEndpoint());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ObsClient(object.getAk(), object.getSk(), object.getEndPoint());
    }

    public List<OssRegion> getOssRegions(OssWithBLOBs ossAccount) throws Exception {
        String result = ReadFileUtils.readConfigFile(BASE_REGION_DIC, ossAccount.getPluginId(), JSON_EXTENSION);
        return new Gson().fromJson(result, new TypeToken<ArrayList<OssRegion>>() {}.getType());
    }

    private static String getStorageClassByEnglish(String storageClassName, OssWithBLOBs ossAccount) throws Exception {
        String result = ReadFileUtils.readConfigFile(BASE_STORAGE_CLASS_DIC, ossAccount.getPluginId(), JSON_EXTENSION);
        List<Map<String, String>> list = new Gson().fromJson(result, new TypeToken<ArrayList<Map<String, String>>>() {
        }.getType());
        for (Map<String, String> map : list) {
            if (map.get("value").equals(storageClassName)) {
                return map.get("key");
            }
        }
        return storageClassName;
    }

    @Override
    public void deletetObjects(OssBucket bucket, OssWithBLOBs account, List<String> objectIds) throws Exception {
        ObsClient obsClient = getObsClient(account, bucket);
        if (CollectionUtils.isNotEmpty(objectIds)) {
            for (String str : objectIds) {
                if (str.endsWith("/")) {
                    deleteObj(obsClient, bucket, str);
                } else {
                    obsClient.deleteObject(bucket.getBucketName(), str);
                }
            }
        }
        obsClient.close();
    }

    private void deleteObj(ObsClient client, OssBucket bucket, String objectId) {
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucket.getBucketName());
        listObjectsRequest.setMaxKeys(1000);
        if (StringUtils.isNotEmpty(objectId)) {
            listObjectsRequest.setPrefix(objectId);
        }
        listObjectsRequest.setDelimiter("/");

        boolean done = false;
        while (!done) {
            ObjectListing objectListing = client.listObjects(listObjectsRequest);

            for (ObsObject obsObject : objectListing.getObjects()) {
                client.deleteObject(bucket.getBucketName(), obsObject.getObjectKey());
            }

            if (objectListing.getCommonPrefixes() != null) {
                for (String commonPrefixes : objectListing.getCommonPrefixes()) {
                    deleteObj(client, bucket, commonPrefixes);
                }
            } else {
                client.deleteObject(bucket.getBucketName(), objectId);
            }

            if (objectListing.getNextMarker() == null) {
                done = true;
            }
        }
    }

    @Override
    public void createDir(OssBucket bucket, OssWithBLOBs account, String dir) throws Exception {
        ObsClient obsClient = getObsClient(account, bucket);
        dir = dir.endsWith("/") ? dir : dir + "/";
        String[] split = dir.split("/");
        String data = "";
        for (String d : split) {
            data += d + "/";
            obsClient.putObject(bucket.getBucketName(), data, new ByteArrayInputStream(new byte[0]));
        }
    }

    @Override
    public void uploadFile(OssBucket bucket, OssWithBLOBs account, String dir, InputStream file, long size) throws Exception {
        ObsClient obsClient = getObsClient(account, bucket);
        obsClient.putObject(bucket.getBucketName(), dir, file);
    }

    @Override
    public void deleteKey(OssBucket bucket, OssWithBLOBs account, String name) throws Exception {

    }

    @Override
    public List<BucketObjectDTO> getBucketObjects(OssBucket bucket, OssWithBLOBs account, String prefix) throws IOException {
        ObsClient obsClient = getObsClient(account, bucket);
        ListObjectsRequest request = new ListObjectsRequest(bucket.getBucketName());
        request.setMaxKeys(1000);
        request.setDelimiter("/");
        if (StringUtils.isNotEmpty(prefix)) {
            request.setPrefix(prefix);
        }
        List<BucketObjectDTO> objects = new ArrayList<>();
        ObjectListing result = obsClient.listObjects(request);
        objects.addAll(convertToBucketFolder(bucket, result.getCommonPrefixes(), prefix));
        objects.addAll(convertToBucketObject(obsClient, bucket, result.getObjects(), prefix));
        while (result.getNextMarker() != null) {
            result = obsClient.listObjects(request);
            objects.addAll(convertToBucketObject(obsClient, bucket, result.getObjects(), prefix));
            objects.addAll(convertToBucketFolder(bucket, result.getCommonPrefixes(), prefix));
        }
        obsClient.close();

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

    private List<BucketObjectDTO> convertToBucketObject(ObsClient obsClient, OssBucket bucket, List<ObsObject> obsObjects, String prefix) throws IOException {
        List<BucketObjectDTO> objects = new ArrayList<>();
        for (ObsObject obsObject : obsObjects) {
            BucketObjectDTO bucketObject = new BucketObjectDTO();
            bucketObject.setBucketId(bucket.getId());

            if (StringUtils.isNotEmpty(prefix) && obsObject.getObjectKey().equals(prefix)) {
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

            if (!obsObject.getObjectKey().endsWith("/")) {
                bucketObject.setId(obsObject.getObjectKey());
                if (StringUtils.isNotEmpty(prefix)) {
                    bucketObject.setObjectName(obsObject.getObjectKey().substring(prefix.length(), obsObject.getObjectKey().length()));
                } else {
                    bucketObject.setObjectName(obsObject.getObjectKey());
                }
                bucketObject.setObjectType(ObjectTypeConstants.FILE.name());
                double size = (obsObject.getMetadata().getContentLength()) / 1024 / 1024;
                DecimalFormat df = new DecimalFormat("#0.###");
                bucketObject.setObjectSize(df.format(size));
                bucketObject.setStorageClass(obsObject.getMetadata().getObjectStorageClass().name());
                bucketObject.setLastModified(obsObject.getMetadata().getLastModified().getTime());
                bucketObject.setDownloadUrl(generatePresignedUrl(obsClient, bucket.getBucketName(), obsObject.getObjectKey()).toString());
            }

            objects.add(bucketObject);
        }
        return objects;
    }

    private URL generatePresignedUrl(ObsClient obsClient, String bucketName, String objectKey) throws MalformedURLException {
        long expireSeconds = 3600L;
        TemporarySignatureRequest request = new TemporarySignatureRequest(HttpMethodEnum.GET, expireSeconds);
        request.setBucketName(bucketName);
        request.setObjectKey(objectKey);
        TemporarySignatureResponse response = obsClient.createTemporarySignature(request);
        return new URL(response.getSignedUrl());
    }

    @Override
    public FilterInputStream downloadObject(OssBucket bucket, OssWithBLOBs account, String objectId) throws Exception {
        ObsClient obsClient = getObsClient(account, bucket);
        ObsObject obsObject = obsClient.getObject(bucket.getBucketName(), objectId);
        // 读取对象内容
        FilterInputStream in = new BufferedInputStream(obsObject.getObjectContent());
        obsClient.close();
        return in;
    }

    @Override
    public boolean doesBucketExist(OssWithBLOBs ossAccount, OssBucket bucket) throws Exception {
        ObsClient obsClient = getObsClient(ossAccount, bucket);
        boolean exists = obsClient.headBucket(bucket.getBucketName());
        obsClient.close();
        return exists;
    }

    @Override
    public OssBucket createBucket(OssWithBLOBs ossAccount, OssBucket bucket) throws Exception {
        ObsClient obsClient = getObsClient(ossAccount, bucket);
        if (!obsClient.headBucket(bucket.getBucketName())) {
            ObsBucket obsBucket = new ObsBucket();
            obsBucket.setBucketName(bucket.getBucketName());
            // 设置桶访问权限为公共读，默认是私有读写
            obsBucket.setAcl(chooseAccessControlList(bucket.getCannedAcl()));
            // 设置桶的存储类型为归档存储
            obsBucket.setBucketStorageClass(chooseStorageClassEnum(bucket.getStorageClass()));
            // 设置桶区域位置
            obsBucket.setLocation(bucket.getLocation());
            // 创建桶
            ObsBucket result = obsClient.createBucket(obsBucket);
            result.setCreationDate(new Date());
            obsClient.close();
            return setBucket(result, ossAccount);
        }
        throw new Exception("Bucket[" + bucket.getBucketName() + "] already exist");
    }

    private AccessControlList chooseAccessControlList(String cannedAcl) {
        if (cannedAcl.equals("public-read")) {
            return AccessControlList.REST_CANNED_PUBLIC_READ;
        } else if (cannedAcl.equals("public-read-write")) {
            return AccessControlList.REST_CANNED_PUBLIC_READ_WRITE;
        } else {
            return AccessControlList.REST_CANNED_PRIVATE;
        }
    }

    private StorageClassEnum chooseStorageClassEnum(String storageClass) {
        if (storageClass.equals(StorageClassEnum.COLD.name())) {
            return StorageClassEnum.COLD;
        } else if (storageClass.equals(StorageClassEnum.WARM.name())) {
            return StorageClassEnum.WARM;
        } else {
            return StorageClassEnum.STANDARD;
        }
    }

    @Override
    public void deleteBucket(OssWithBLOBs ossAccount, OssBucket bucket) throws Exception {
        ObsClient obsClient = getObsClient(ossAccount, bucket);
        BucketStorageInfo storageInfo = obsClient.getBucketStorageInfo(bucket.getBucketName());
        if (storageInfo.getSize() != 0) {
            obsClient.close();
            throw new Exception("Bucket is not empty. Please check whether the bucket contains undeleted objects!");
        }
        obsClient.deleteBucket(bucket.getBucketName());
        obsClient.close();
    }

}
