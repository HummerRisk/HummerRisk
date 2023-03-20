package com.hummer.cloud.oss.provider;

import com.alibaba.fastjson.JSON;
import com.baidubce.BceServiceException;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.model.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hummer.cloud.oss.constants.ObjectTypeConstants;
import com.hummer.cloud.oss.dto.BucketObjectDTO;
import com.hummer.common.core.domain.OssBucket;
import com.hummer.common.core.domain.OssRegion;
import com.hummer.common.core.domain.OssWithBLOBs;
import com.hummer.common.core.exception.PluginException;
import com.hummer.common.core.proxy.baidu.BaiduCredential;
import com.hummer.common.core.proxy.baidu.BaiduRequest;
import com.hummer.common.core.utils.LogUtil;
import com.hummer.common.core.utils.ReadFileUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BaiduProvider implements OssProvider {

    private static final String BASE_REGION_DIC = "support/regions/";
    private static final String JSON_EXTENSION = ".json";

    @Override
    public String policyModel() {
        return "";
    }

    @Override
    public List<OssBucket> getOssBucketList(OssWithBLOBs ossAccount) throws Exception {
        List<OssBucket> resultList = new ArrayList<>();
        try {
            BosClient bosClient = getBosClient(ossAccount);
            ListBucketsResponse list = bosClient.listBuckets();
            if (!CollectionUtils.isEmpty(list.getBuckets())) {
                for (BucketSummary bucketSummary : list.getBuckets()) {
                    resultList.add(setBucket(ossAccount, bucketSummary, null));
                }
            }
            bosClient.shutdown();
        } catch (PluginException e) {
            throw new PluginException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return resultList;
    }

    private static OssBucket setBucket(OssWithBLOBs account, BucketSummary bucketSummary, OssBucket bucket) throws Exception {
        OssBucket bucketDTO = new OssBucket();
        String bucketName = bucketSummary.getName();
        String region = bucketSummary.getLocation();
        BosClient bosClient = getBosClient(account,region);
        ListObjectsResponse listObjectsResponse = bosClient.listObjects(bucketName);
        List<BosObjectSummary> contents = listObjectsResponse.getContents();
        try {
            bucketDTO.setCreateTime(bucketSummary.getCreationDate().getTime());
            bucketDTO.setLocation(bosClient.getBucketLocation(bucketName).getLocationConstraint());
            bucketDTO.setBucketName(bucketName);
            bucketDTO.setOssId(account.getId());
            bucketDTO.setDomainName(StringUtils.joinWith(".", bucketName, bosClient.getBucketLocation(bucketName).getLocationConstraint(), "bcebos.com"));
            bucketDTO.setExtranetEndpoint(StringUtils.joinWith(".", bosClient.getBucketLocation(bucketName).getLocationConstraint(), "bcebos.com"));
            bucketDTO.setIntranetEndpoint("N/A");
            bucketDTO.setCannedAcl(chooseAccessControlList(bucket != null ? bucket.getCannedAcl() : "N/A"));
            if (contents.size() > 0) {
                bucketDTO.setStorageClass(contents.get(0).getStorageClass());
            } else {
                bucketDTO.setStorageClass(chooseStorageClassEnum(bucket != null ? bucket.getStorageClass() : "N/A"));
            }
            if (bosClient.getBosAccountOwner() != null) {
                bucketDTO.setOwnerName(bosClient.getBosAccountOwner().getDisplayName());
                bucketDTO.setOwnerId(bosClient.getBosAccountOwner().getId());
            }
            if (listObjectsResponse != null) {
                Double size = 0D;
                for (BosObjectSummary obj : contents) {
                    size += obj.getSize();
                }
                bucketDTO.setSize(SysListener.changeFlowFormat(size.longValue()));
                bucketDTO.setObjectNumber((long) contents.size() - 1);
            } else {
                bucketDTO.setSize("0");
                bucketDTO.setObjectNumber(0L);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }finally {
            bosClient.shutdown();
        }
        return bucketDTO;
    }

    private static String chooseStorageClassEnum(String storageClass) {
        // 类型不确定，先这么搁置
        if (StringUtils.equals(storageClass, "STANDARD")) {
            return "STANDARD";
        } else if (StringUtils.equals(storageClass, "STANDARD")) {
            return "STANDARD";
        } else if (StringUtils.equals(storageClass, "COLD")) {
            return "COLD";
        } else if (StringUtils.equals(storageClass, "ARCHIVE")) {
            return "ARCHIVE";
        }
        return "STANDARD";
    }

    private static String chooseAccessControlList(String cannedAcl) {
        if (StringUtils.equals(cannedAcl, CannedAccessControlList.Private.toString())) {
            return CannedAccessControlList.Private.name();
        } else if (StringUtils.equals(cannedAcl, CannedAccessControlList.PublicRead.toString())) {
            return CannedAccessControlList.PublicRead.name();
        } else if (StringUtils.equals(cannedAcl, CannedAccessControlList.PublicReadWrite.toString())) {
            return CannedAccessControlList.PublicReadWrite.name();
        }
        return "N/A";
    }

    private static BosClient getBosClient(OssWithBLOBs account) throws Exception {
        BaiduCredential baiduCredential = JSON.parseObject(account.getCredential(), BaiduCredential.class);
        BaiduRequest req = new BaiduRequest();
        req.setBaiduCredential(baiduCredential);
        return req.getClient();
    }

    private static BosClient getBosClient(OssWithBLOBs account,String region) throws Exception {
        BaiduCredential baiduCredential = JSON.parseObject(account.getCredential(), BaiduCredential.class);
        BaiduRequest req = new BaiduRequest();
        req.setBaiduCredential(baiduCredential);
        return req.getClient(region);
    }

    @Override
    public List<BucketObjectDTO> getBucketObjects(OssBucket bucket, OssWithBLOBs account, String prefix) throws Exception {
        BosClient bosClient = getBosClient(account,bucket.getLocation());
        try {
            if (bosClient.doesBucketExist(bucket.getBucketName())) {
                ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucket.getBucketName());
                listObjectsRequest.setBucketName(bucket.getBucketName());
                listObjectsRequest.setMaxKeys(1000);
                listObjectsRequest.setDelimiter("/");
                if (StringUtils.isNotEmpty(prefix)) {
                    listObjectsRequest.setPrefix(prefix);
                }
                List<BucketObjectDTO> objects = new ArrayList<>();

                ListObjectsResponse objectListing = bosClient.listObjects(listObjectsRequest);
                objects.addAll(convertToBucketFolder(bucket, objectListing.getCommonPrefixes(), prefix));
                objects.addAll(convertToBucketObject(bosClient, bucket, objectListing.getContents(), prefix));

                while (objectListing.getNextMarker() != null) {
                    objectListing = bosClient.listObjects(listObjectsRequest);
                    objects.addAll(convertToBucketObject(bosClient, bucket, objectListing.getContents(), prefix));
                    objects.addAll(convertToBucketFolder(bucket, objectListing.getCommonPrefixes(), prefix));
                }
                List<BucketObjectDTO> bucketObjectDTOS = new ArrayList<>();
                for (BucketObjectDTO object : objects) {
                    if (object.getObjectType().equals(ObjectTypeConstants.BACK.name())) {
                        bucketObjectDTOS.add(0, object);
                    } else {
                        bucketObjectDTOS.add(object);
                    }
                }
                return bucketObjectDTOS;
            } else {
                throw new Exception("no such bucket");
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            throw new Exception(e.getMessage());
        }finally {
            bosClient.shutdown();
        }
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

    private List<BucketObjectDTO> convertToBucketObject(BosClient bosClient, OssBucket bucket, List<BosObjectSummary> bosObjectSummaryList, String prefix) {
        List<BucketObjectDTO> objects = new ArrayList<>();
        for (BosObjectSummary bosObjectSummary : bosObjectSummaryList) {
            BucketObjectDTO bucketObject = new BucketObjectDTO();
            bucketObject.setBucketId(bucket.getId());

            if (StringUtils.isNotEmpty(prefix) && bosObjectSummary.getKey().equals(prefix)) {
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

            if (!bosObjectSummary.getKey().endsWith("/")) {
                bucketObject.setId(bosObjectSummary.getKey());
                if (StringUtils.isNotEmpty(prefix)) {
                    bucketObject.setObjectName(bosObjectSummary.getKey().substring(prefix.length(), bosObjectSummary.getKey().length()));
                } else {
                    bucketObject.setObjectName(bosObjectSummary.getKey());
                }
                bucketObject.setObjectType(ObjectTypeConstants.FILE.name());
                long getSize = bosObjectSummary != null ? bosObjectSummary.getSize() : 0;
                double size = ((double) getSize) / 1024 / 1024;
                DecimalFormat df = new DecimalFormat("#0.###");
                bucketObject.setObjectSize(df.format(size));
                bucketObject.setStorageClass(bosObjectSummary.getStorageClass());
                bucketObject.setLastModified(bosObjectSummary.getLastModified().getTime());
                bucketObject.setDownloadUrl(generatePresignedUrl(bosClient, bucket.getBucketName(), bosObjectSummary.getKey()).toString());
            }

            objects.add(bucketObject);
        }
        return objects;
    }

    private URL generatePresignedUrl(BosClient bosClient, String bucketName, final String objectId) {
        int expiration = 1800;
        // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
        return bosClient.generatePresignedUrl(bucketName, objectId, expiration);
    }

    @Override
    public FilterInputStream downloadObject(OssBucket bucket, OssWithBLOBs account, final String objectId) throws Exception {
        FilterInputStream input = null;
        BosClient bosClient = getBosClient(account,bucket.getLocation());
        try {
            BosObject obj = bosClient.getObject(bucket.getBucketName(), objectId);
            input = new BufferedInputStream(obj.getObjectContent());
            return input;
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public boolean doesBucketExist(OssWithBLOBs ossAccount, OssBucket bucket) throws Exception {
        boolean exists = true;
        BosClient bosClient = getBosClient(ossAccount,bucket.getLocation());
        try {
            exists = bosClient.doesBucketExist(bucket.getBucketName());
        } catch (BceServiceException be) {
            LogUtil.warn(be.getMessage(), be);
            throw new BceServiceException(be.getMessage());
        } catch (Exception e) {
            LogUtil.warn(e.getMessage(), e);
            throw new PluginException(e.getMessage());
        }finally {
            bosClient.shutdown();
        }
        return exists;
    }

    @Override
    public OssBucket createBucket(OssWithBLOBs ossAccount, OssBucket bucket) throws Exception {
        BosClient bosClient = getBosClient(ossAccount,bucket.getLocation());
        try {
            if (bosClient.doesBucketExist(bucket.getBucketName())) {
                throw new Exception("Bucket already exists");
            } else {
                CreateBucketResponse response = bosClient.createBucket(bucket.getBucketName());
                setBucketPolicy(bosClient, bucket.getBucketName(), bucket.getCannedAcl());
                BucketSummary bucketSummary = new BucketSummary();
                bucketSummary.setCreationDate(new Date());
                bucketSummary.setLocation(bucket.getLocation());
                bucketSummary.setName(bucket.getBucketName());
                OssBucket bucket1 = setBucket(ossAccount, bucketSummary, bucket);
                if (response != null) {
                    return bucket1;
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            throw new Exception(e.getMessage());
        }finally {
            bosClient.shutdown();
        }
    }

    private void setBucketPolicy(BosClient bosClient, String bucketName, String cannedAcl) throws Exception {
        if (cannedAcl == null) {
            return;
        }
        List<Grant> accessControlList = new ArrayList<Grant>();
        List<Grantee> grantees = new ArrayList<Grantee>();
        List<Permission> permissions = new ArrayList<Permission>();

        //授权给Everyone
        grantees.add(new Grantee("*"));

        //设置权限
        permissions.add(Permission.WRITE);
        permissions.add(Permission.READ);
        permissions.add(Permission.LIST);

        Grant grant = new Grant();
        grant.setGrantee(grantees);
        grant.setPermission(permissions);
        accessControlList.add(grant);
        SetBucketAclRequest request = new SetBucketAclRequest(bucketName, accessControlList);

        bosClient.setBucketAcl(request);
    }

    @Override
    public void deleteBucket(OssWithBLOBs ossAccount, OssBucket bucket) throws Exception {
        BosClient bosClient = getBosClient(ossAccount,bucket.getLocation());
        try {
            if (bosClient.doesBucketExist(bucket.getBucketName())) {
                ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucket.getBucketName());
                ListObjectsResponse objectListing = bosClient.listObjects(listObjectsRequest);
                if (ObjectUtils.isNotEmpty(objectListing.getContents()) || ObjectUtils.isNotEmpty(objectListing.getCommonPrefixes())) {
                    throw new Exception("Bucket is not empty. Please check whether the bucket contains undeleted objects!");
                }
                bosClient.deleteBucket(bucket.getBucketName());
            } else {
                throw new Exception("Bucket does not exist");
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            throw new Exception(e.getMessage());
        }finally {
            bosClient.shutdown();
        }
    }

    @Override
    public List<OssRegion> getOssRegions(OssWithBLOBs ossAccount) throws Exception {
        String result = ReadFileUtils.readConfigFile(BASE_REGION_DIC, ossAccount.getPluginId(), JSON_EXTENSION);
        return new Gson().fromJson(result, new TypeToken<ArrayList<OssRegion>>() {
        }.getType());
    }

    @Override
    public void deletetObjects(OssBucket bucket, OssWithBLOBs account, List<String> objectIds) throws Exception {
        BosClient bosClient = getBosClient(account,bucket.getLocation());
        try {
            //删除Object
            if (CollectionUtils.isNotEmpty(objectIds)) {
                for (String str : objectIds) {
                    if (str.endsWith("/")) {
                        deleteObj(bosClient, bucket, str);
                    } else {
                        bosClient.deleteObject(bucket.getBucketName(), str);
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            throw new Exception(e.getMessage());
        }finally {
            bosClient.shutdown();
        }
    }

    private void deleteObj(BosClient bosClient, OssBucket bucket, String objectId) {
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucket.getBucketName());
        listObjectsRequest.setMaxKeys(1000);
        if (StringUtils.isNotEmpty(objectId)) {
            listObjectsRequest.setPrefix(objectId);
        }
        listObjectsRequest.setDelimiter("/");

        boolean done = false;
        while (!done) {
            ListObjectsResponse listObjectsResponse = bosClient.listObjects(listObjectsRequest);

            for (BosObjectSummary bosObjectSummary : listObjectsResponse.getContents()) {
                bosClient.deleteObject(bucket.getBucketName(), bosObjectSummary.getKey());
            }

            if (listObjectsResponse.getCommonPrefixes() != null) {
                for (String commonPrefixes : listObjectsResponse.getCommonPrefixes()) {
                    deleteObj(bosClient, bucket, commonPrefixes);
                }
            } else {
                bosClient.deleteObject(bucket.getBucketName(), objectId);
            }

            if (listObjectsResponse.getNextMarker() == null) {
                done = true;
            }
        }
    }

    @Override
    public void createDir(OssBucket bucket, OssWithBLOBs account, String dir) throws Exception {
        BosClient bosClient = getBosClient(account,bucket.getLocation());
        try {
            dir = dir.endsWith("/") ? dir : dir + "/";
            String[] split = dir.split("/");
            String data = "";
            for (String d : split) {
                data += d + "/";
                if("/".equals(data)){
                    continue;
                }
                bosClient.putObject(bucket.getBucketName(), data, new ByteArrayInputStream("".getBytes()));
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }finally {
            bosClient.shutdown();
        }
    }

    @Override
    public void uploadFile(OssBucket bucket, OssWithBLOBs account, String objectid, InputStream file, long size) throws Exception {
        BosClient bosClient = getBosClient(account,bucket.getLocation());
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(size);
            bosClient.putObject(bucket.getBucketName(), objectid, file, metadata);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }finally {
            bosClient.shutdown();
        }
    }

    @Override
    public void deleteKey(OssBucket bucket, OssWithBLOBs account, String name) throws Exception {

    }

}
