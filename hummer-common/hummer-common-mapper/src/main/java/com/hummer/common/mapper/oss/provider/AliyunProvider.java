package com.hummer.common.mapper.oss.provider;


import com.alibaba.fastjson.JSON;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.ram.model.v20150501.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hummer.common.mapper.domain.OssBucket;
import com.hummer.common.mapper.domain.OssWithBLOBs;
import com.hummer.common.core.utils.ReadFileUtils;
import com.hummer.common.mapper.oss.constants.ObjectTypeConstants;
import com.hummer.common.mapper.oss.dto.BucketObjectDTO;
import com.hummer.common.mapper.oss.dto.OssRegion;
import com.hummer.common.mapper.proxy.aliyun.AliyunCredential;
import com.hummer.common.mapper.proxy.aliyun.AliyunRequest;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AliyunProvider implements OssProvider {

    private static final String BASE_REGION_DIC = "support/regions/";
    private static final String JSON_EXTENSION = ".json";

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

    @Override
    public FilterInputStream downloadObject(OssBucket bucket, OssWithBLOBs account, final String objectId) throws Exception {
        OSSClient ossClient = getOSSClient(account);
        ossClient.setEndpoint(bucket.getExtranetEndpoint());
        OSSObject ossObject = ossClient.getObject(bucket.getBucketName(), objectId);
        FilterInputStream in = new BufferedInputStream(ossObject.getObjectContent());
        return in;
    }

    @Override
    public boolean doesBucketExist(OssWithBLOBs ossAccount, OssBucket bucket) {
        OSSClient ossClient = getOSSClient(ossAccount);
        boolean exists = ossClient.doesBucketExist(bucket.getBucketName());
        ossClient.shutdown();
        return exists;
    }

    @Override
    public OssBucket createBucket(OssWithBLOBs ossAccount, OssBucket bucket) {
        OSSClient ossClient = getOSSClient(ossAccount);
        CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucket.getBucketName());
        // 设置存储空间的权限，默认是私有。
        createBucketRequest.setCannedACL(CannedAccessControlList.parse(bucket.getCannedAcl()));
        // 设置存储空间的存储类型，默认是标准类型。
        createBucketRequest.setStorageClass(StorageClass.parse(bucket.getStorageClass()));
        ossClient.createBucket(createBucketRequest);
        BucketInfo result = ossClient.getBucketInfo(bucket.getBucketName());
        ossClient.shutdown();
        if (result != null) {
            OssBucket bucket1 = setBucket(result.getBucket(), ossAccount, bucket.getCannedAcl(), null);
            return bucket1;
        } else {
            return null;
        }
    }

    @Override
    public void deleteBucket(OssWithBLOBs ossAccount, OssBucket bucket) throws Exception {
        OSSClient ossClient = getOSSClient(ossAccount);
        ossClient.setEndpoint(bucket.getExtranetEndpoint());
        //删除存储空间之前，必须先删除存储空间下的所有文件、LiveChannel和分片上传产生的碎片。
        // 判断是否有文件
        ObjectListing objectListing = ossClient.listObjects(bucket.getBucketName());
        if (objectListing.getObjectSummaries().size() > 0 || objectListing.getCommonPrefixes().size() > 0) {
            throw new Exception("Bucket is not empty. Please check whether the bucket contains undeleted objects!");
        }
        // 删除liveChannel
        List<LiveChannel> liveChannels = ossClient.listLiveChannels(bucket.getBucketName());
        for (LiveChannel liveChannel : liveChannels) {
            //能直接删？
            ossClient.deleteLiveChannel(bucket.getBucketName(), liveChannel.getName());
        }
        // 删除碎片
        MultipartUploadListing uploadListing = ossClient.listMultipartUploads(new ListMultipartUploadsRequest(bucket.getBucketName()));
        List<MultipartUpload> multipartUploads = uploadListing.getMultipartUploads();
        for (MultipartUpload multipartUpload : multipartUploads) {
            //不知道对不对..
            ossClient.abortMultipartUpload(new AbortMultipartUploadRequest(bucket.getBucketName(), multipartUpload.getKey(), multipartUpload.getUploadId()));
        }
        //删除桶
        ossClient.deleteBucket(bucket.getBucketName());

        ossClient.shutdown();
    }

    @Override
    public List<OssRegion> getOssRegions(OssWithBLOBs ossAccount) throws Exception {
        String result = ReadFileUtils.readConfigFile(BASE_REGION_DIC, ossAccount.getPluginId(), JSON_EXTENSION);
        return new Gson().fromJson(result, new TypeToken<ArrayList<OssRegion>>() {
        }.getType());
    }

    @Override
    public void deletetObjects(OssBucket bucket, OssWithBLOBs account, final List<String> objectIds) throws Exception {
        OSSClient ossClient = getOSSClient(account);
        ossClient.setEndpoint(bucket.getExtranetEndpoint());
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(objectIds)) {
            for (String str : objectIds) {
                if (str.endsWith("/")) {
                    deleteObj(ossClient, bucket, str);
                } else {
                    ossClient.deleteObject(bucket.getBucketName(), str);
                }
            }
        }
        ossClient.shutdown();
    }

    private void deleteObj(OSSClient ossClient, OssBucket bucket, String objectId) {
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucket.getBucketName());
        listObjectsRequest.setMaxKeys(1000);
        if (StringUtils.isNotEmpty(objectId)) {
            listObjectsRequest.setPrefix(objectId);
        }
        listObjectsRequest.setDelimiter("/");

        boolean done = false;
        while (!done) {
            ObjectListing objectListing = ossClient.listObjects(listObjectsRequest);

            for (OSSObjectSummary ossObjectSummary : objectListing.getObjectSummaries()) {
                ossClient.deleteObject(bucket.getBucketName(), ossObjectSummary.getKey());
            }

            if (objectListing.getCommonPrefixes() != null) {
                for (String commonPrefixes : objectListing.getCommonPrefixes()) {
                    deleteObj(ossClient, bucket, commonPrefixes);
                }
            } else {
                ossClient.deleteObject(bucket.getBucketName(), objectId);
            }

            if (objectListing.getNextMarker() == null) {
                done = true;
            }
        }
    }

    @Override
    public void createDir(OssBucket bucket, OssWithBLOBs account, String dir) throws Exception {
        dir = dir.endsWith("/") ? dir : dir + "/";
        OSSClient ossClient = getOSSClient(account);
        ossClient.setEndpoint(bucket.getExtranetEndpoint());
        String[] split = dir.split("/");
        String data = "";
        for (String d : split) {
            data += d + "/";
            ossClient.putObject(bucket.getBucketName(), data, new ByteArrayInputStream("".getBytes()));
        }
    }

    @Override
    public void uploadFile(OssBucket bucket, OssWithBLOBs account, String objectid, InputStream file, long size) throws Exception {
        OSSClient ossClient = getOSSClient(account);
        ossClient.setEndpoint(bucket.getExtranetEndpoint());
        ossClient.putObject(bucket.getBucketName(), objectid, file);
    }

    @Override
    public void deleteKey(OssBucket bucket, OssWithBLOBs account, String name) throws Exception {
        //删除用户、删除权限
        DefaultAcsClient client = getClient(account);
        detachPolicyFromUser(client, bucket.getBucketName(), bucket.getBucketName(), "Custom");
        deletekeys(client, bucket.getBucketName());
        deleteUser(client, bucket.getBucketName());
        deletePolicy(client, bucket.getBucketName());
    }

    private static DefaultAcsClient getClient(OssWithBLOBs account) {
        AliyunCredential aliyunCredential = JSON.parseObject(account.getCredential(), AliyunCredential.class);
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", aliyunCredential.getAccessKey(),
                aliyunCredential.getSecretKey());
        return new DefaultAcsClient(profile);
    }

    private void detachPolicyFromUser(DefaultAcsClient client, String userName, String policyName, String policyType) throws Exception {
        DetachPolicyFromUserRequest detachPolicyFromUserRequest = new DetachPolicyFromUserRequest();
        detachPolicyFromUserRequest.setUserName(userName);
        detachPolicyFromUserRequest.setPolicyName(policyName);
        detachPolicyFromUserRequest.setPolicyType(policyType);
        client.getAcsResponse(detachPolicyFromUserRequest);
    }

    private void deletekeys(DefaultAcsClient client, String userName) throws Exception {
        ListAccessKeysRequest listAccessKeysRequest = new ListAccessKeysRequest();
        listAccessKeysRequest.setUserName(userName);
        ListAccessKeysResponse listAccessKeysResponse = client.getAcsResponse(listAccessKeysRequest);
        for (ListAccessKeysResponse.AccessKey accessKey : listAccessKeysResponse.getAccessKeys()) {
            DeleteAccessKeyRequest deleteAccessKeyRequest = new DeleteAccessKeyRequest();
            deleteAccessKeyRequest.setUserName(userName);
            deleteAccessKeyRequest.setUserAccessKeyId(accessKey.getAccessKeyId());
            client.getAcsResponse(deleteAccessKeyRequest);
        }
    }

    private DeleteUserResponse deleteUser(DefaultAcsClient client, String userName) throws Exception {
        DeleteUserRequest request = new DeleteUserRequest();
        request.setUserName(userName);
        return client.getAcsResponse(request);
    }

    private DeletePolicyResponse deletePolicy(DefaultAcsClient client, String policyName) throws Exception {
        DeletePolicyRequest deletePolicyRequest = new DeletePolicyRequest();
        deletePolicyRequest.setPolicyName(policyName);
        return client.getAcsResponse(deletePolicyRequest);
    }

}
