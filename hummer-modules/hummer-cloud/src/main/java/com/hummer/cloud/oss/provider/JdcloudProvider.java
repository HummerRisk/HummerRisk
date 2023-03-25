package com.hummer.cloud.oss.provider;

import com.alibaba.fastjson.JSON;
import com.hummer.cloud.oss.constants.ObjectTypeConstants;
import com.hummer.cloud.oss.dto.BucketMetric;
import com.hummer.cloud.oss.dto.BucketObjectDTO;
import com.hummer.common.core.constant.RegionsConstants;
import com.hummer.common.core.domain.OssBucket;
import com.hummer.common.core.domain.OssRegion;
import com.hummer.common.core.domain.OssWithBLOBs;
import com.hummer.common.core.proxy.jdcloud.JDCloudCredential;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.FilterInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JdcloudProvider implements OssProvider{
    private static final String BASE_REGION_DIC = "support/regions/";
    private static final String JSON_EXTENSION = ".json";

    @Override
    public String policyModel() {
        return "{\n" +
                "    \"Version\": \"2012-10-17\",\n" +
                "    \"Statement\": [\n" +
                "        {\n" +
                "            \"Sid\": \"VisualEditor0\",\n" +
                "            \"Effect\": \"Allow\",\n" +
                "            \"Action\": \"s3:*\",\n" +
                "            \"Resource\": \"arn:AWS_LOCATION:s3:::BUCKET_NAME\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
    }

    @Override
    public List<OssBucket> getOssBucketList(OssWithBLOBs ossAccount) throws URISyntaxException {
        List<OssBucket> resultList = new ArrayList<>();
        Set<String> regions = RegionsConstants.JdcloudMap.keySet();
        for(String region : regions){
            S3Client s3 = null;
            try{
                s3 = getS3Client(ossAccount.getCredential(), region);
                ListBucketsResponse listBucketsResponse = s3.listBuckets();
                resultList.addAll(getOssBucket(ossAccount, listBucketsResponse, s3,region));
            }catch (Exception e1){
                e1.printStackTrace();
            }finally {
                if(s3 != null){
                    s3.close();
                }
            }
        }

        return resultList;
    }

    private List<OssBucket> getOssBucket(OssWithBLOBs ossAccount, ListBucketsResponse listBucketsResponse, S3Client s3,String region) throws URISyntaxException {
        List<OssBucket> resultList = new ArrayList<>();
        if (listBucketsResponse != null && !CollectionUtils.isEmpty(listBucketsResponse.buckets())) {
            for (Bucket bucket : listBucketsResponse.buckets()) {
                OssBucket tmpBucket = setBucket(s3, bucket ,ossAccount,region);
             //   BucketMetric bucketMetric = getBucketMetric(tmpBucket, ossAccount);
              //  tmpBucket.setObjectNumber(bucketMetric.getObjectNumber());
              //  tmpBucket.setSize(SysListener.changeFlowFormat(bucketMetric.getSize()));
                tmpBucket.setStorageClass("N/A");
                resultList.add(tmpBucket);
            }
        }
        return resultList;
    }

    private OssBucket setBucket(S3Client s3, Bucket bucket, OssWithBLOBs account,String region) throws URISyntaxException {
        //GetBucketLocationResponse getBucketLocationResponse  = s3.getBucketLocation(GetBucketLocationRequest.builder().bucket(bucket.name()).build());
        OssBucket bucketDTO = new OssBucket();
        bucketDTO.setBucketName(bucket.name());
        bucketDTO.setOssId(account.getId());
        bucketDTO.setLocation(region);
        setAcl(bucketDTO, account);
        bucketDTO.setSize("0");
        bucketDTO.setObjectNumber(0L);
        return bucketDTO;
    }

    private void setAcl(OssBucket bucketDTO, OssWithBLOBs account) throws URISyntaxException {
        S3Client s3 = getS3Client(account.getCredential(), bucketDTO.getLocation());
        GetBucketAclResponse getBucketAclResponse = s3.getBucketAcl(GetBucketAclRequest.builder().bucket(bucketDTO.getBucketName()).build());
        boolean Private = true;
        boolean PublicRead = false;
        boolean PublicWrite = false;
        for (Grant grant : getBucketAclResponse.grants()) {
            if(grant.grantee().toString().contains("AllUsers") && grant.permissionAsString().equalsIgnoreCase("READ")){
                PublicRead = true;
                Private = false;
            }
            if(grant.grantee().toString().contains("AllUsers") && grant.permissionAsString().equalsIgnoreCase("WRITE")){
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
        if(PublicWrite && PublicRead){
            bucketDTO.setCannedAcl("public-read-write");
        }
    }

    public BucketMetric getBucketMetric(OssBucket bucket, OssWithBLOBs account) {

       return null;
    }



    private S3Client getS3Client (String credential, String region) throws URISyntaxException {
        JDCloudCredential object = JSON.parseObject(credential, JDCloudCredential.class);
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(object.getAccessKey(), object.getSecretAccessKey());
        return S3Client.builder().credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials)).region(Region.of(region)).endpointOverride(new URI("https://s3."+region+".jdcloud-oss.com")).build();
    }

    @Override
    public List<BucketObjectDTO> getBucketObjects(OssBucket bucket, OssWithBLOBs account, String prefix) throws URISyntaxException {
        List<BucketObjectDTO> objects = new ArrayList<>();
        S3Client s3 = getS3Client(account.getCredential(), bucket.getLocation());
        ListObjectsV2Request.Builder builder = ListObjectsV2Request.builder().delimiter("/").bucket(bucket.getBucketName());
        if(StringUtils.isNotEmpty(prefix)){
            builder.prefix(prefix);
        }
        ListObjectsV2Request listObjectsV2Request = builder.build();

        boolean done = false;
        while (!done) {
            ListObjectsV2Response listObjResponse = s3.listObjectsV2(listObjectsV2Request);
            objects.addAll(convertToBucketObject(bucket, listObjResponse.contents(), prefix));

            objects.addAll(convertToBucketFolder(bucket, listObjResponse.commonPrefixes(), prefix));

            if (listObjResponse.nextContinuationToken() == null) {
                done = true;
            }

            listObjectsV2Request = listObjectsV2Request.toBuilder()
                    .continuationToken(listObjResponse.nextContinuationToken())
                    .build();
        }
        s3.close();
        List<BucketObjectDTO> bucketObjectDTOS = new ArrayList<>();
        for (BucketObjectDTO object : objects) {
            if(object.getObjectType().equals(ObjectTypeConstants.BACK.name())){
                bucketObjectDTOS.add(0, object);
            }else {
                bucketObjectDTOS.add(object);
            }
        }
        return bucketObjectDTOS;
    }

    private List<BucketObjectDTO> convertToBucketObject(OssBucket bucket, List<S3Object> contents, final String prefix){
        List<BucketObjectDTO> objects = new ArrayList<>();
        for (S3Object s3Object : contents) {
            BucketObjectDTO bucketObject = new BucketObjectDTO();
            bucketObject.setBucketId(bucket.getId());

            if(StringUtils.isNotEmpty(prefix) && s3Object.key().equals(prefix)){
                String[] dirs = prefix.split("/");
                if(dirs.length == 1){
                    bucketObject.setId("/");
                    bucketObject.setObjectName(prefix);
                }else {
                    String lastDir = dirs[dirs.length -1];
                    bucketObject.setId(prefix.substring(0, prefix.length() - lastDir.length() - 1 ) );
                    bucketObject.setObjectName(lastDir + "/");
                }
                bucketObject.setObjectType(ObjectTypeConstants.BACK.name());
            }

            if(!s3Object.key().endsWith("/")){
                bucketObject.setObjectSize(SysListener.changeFlowFormat(s3Object.size()));
                bucketObject.setId(s3Object.key());
                if(StringUtils.isEmpty(prefix)){
                    bucketObject.setObjectName(s3Object.key());
                }else {
                    bucketObject.setObjectName(s3Object.key().substring(prefix.length(), s3Object.key().length() - 1));
                }
                bucketObject.setStorageClass(s3Object.storageClassAsString());
                bucketObject.setLastModified(s3Object.lastModified().toEpochMilli());
                bucketObject.setObjectType(ObjectTypeConstants.FILE.name());
            }
            objects.add(bucketObject);

        }
        return objects;
    }

    private List<BucketObjectDTO> convertToBucketFolder(OssBucket bucket,  List<CommonPrefix> commonPrefixes, String prefix){
        List<BucketObjectDTO> objects = new ArrayList<>();
        for (CommonPrefix commonPrefix : commonPrefixes) {
            BucketObjectDTO bucketObject = new BucketObjectDTO();
            bucketObject.setBucketId(bucket.getId());
            if(StringUtils.isNotEmpty(prefix)){
                bucketObject.setObjectName(commonPrefix.prefix().substring(prefix.length()));
            }else {
                bucketObject.setObjectName(commonPrefix.prefix());
            }
            bucketObject.setId(commonPrefix.prefix());
            bucketObject.setObjectType(ObjectTypeConstants.DIR.name());
            objects.add(bucketObject);
        }
        return objects;
    }

    @Override
    public FilterInputStream downloadObject(OssBucket bucket, OssWithBLOBs account, final String objectId) throws Exception{
        S3Client s3 = getS3Client(account.getCredential(), bucket.getLocation());

        ResponseInputStream<GetObjectResponse> responseResponseInputStream = s3.getObject(GetObjectRequest.builder().bucket(bucket.getBucketName()).key(objectId).build());
        return (FilterInputStream)responseResponseInputStream;
    }

    @Override
    public boolean doesBucketExist(OssWithBLOBs ossAccount, OssBucket bucket) throws URISyntaxException {
        boolean exits = false;
        S3Client s3;
        ListBucketsResponse listBucketsResponse;

        s3 = getS3Client(ossAccount.getCredential(), bucket.getLocation());
        listBucketsResponse = s3.listBuckets();
        s3.close();

        for (Bucket awsBucket : listBucketsResponse.buckets()) {
            if(awsBucket.name().equals(bucket.getBucketName())){
                exits = true;
            }
        }
        return exits;
    }

    @Override
    public OssBucket createBucket(OssWithBLOBs ossAccount, OssBucket bucket) throws Exception {
        S3Client s3 = getS3Client(ossAccount.getCredential(), bucket.getLocation());
        CreateBucketRequest createBucketRequest = CreateBucketRequest.builder().bucket(bucket.getBucketName())
                .acl(BucketCannedACL.fromValue(bucket.getCannedAcl())).build();
        CreateBucketResponse createBucketResponse = s3.createBucket(createBucketRequest);
        bucket.setOssId(ossAccount.getId());
        bucket.setCreateTime(System.currentTimeMillis());
        bucket.setStorageClass("N/A");
        return bucket;
    }

    @Override
    public void deleteBucket(OssWithBLOBs ossAccount, OssBucket bucket) throws Exception {
        S3Client s3 = getS3Client(ossAccount.getCredential(), bucket.getLocation());
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder().bucket(bucket.getBucketName()).build();
        ListObjectsV2Response listObjectsV2Response = s3.listObjectsV2(listObjectsV2Request);
        if(listObjectsV2Response.commonPrefixes().size() > 0 || listObjectsV2Response.commonPrefixes().size() > 0){
            s3.close();
            throw new Exception("Bucket is not empty. Please check whether the bucket contains undeleted objects!");
        }
        s3.deleteBucket(DeleteBucketRequest.builder().bucket(bucket.getBucketName()).build());
        s3.close();
    }

    @Override
    public List<OssRegion> getOssRegions(OssWithBLOBs ossAccount) throws Exception {
        return null;
    }

    @Override
    public void deletetObjects(OssBucket bucket, OssWithBLOBs account, List<String> objectIds) throws Exception {
        S3Client s3 = getS3Client(account.getCredential(), bucket.getLocation());

        if (CollectionUtils.isNotEmpty(objectIds)) {
            for (String str : objectIds) {
                if (str.endsWith("/")) {
                    deleteObj(s3, bucket, str);
                } else {
                    s3.deleteObject(DeleteObjectRequest.builder().bucket(bucket.getBucketName()).key(str).build());
                }
            }
        }
        s3.close();
    }

    private void deleteObj(S3Client s3, OssBucket bucket, String objectId) {
        ListObjectsV2Request.Builder builder = ListObjectsV2Request.builder().delimiter("/").bucket(bucket.getBucketName());
        if(StringUtils.isNotEmpty(objectId)){
            builder.prefix(objectId);
        }
        ListObjectsV2Request listObjectsV2Request = builder.build();

        boolean done = false;
        while (!done) {
            ListObjectsV2Response listObjResponse = s3.listObjectsV2(listObjectsV2Request);

            for (S3Object s3Object : listObjResponse.contents()) {
                s3.deleteObject(DeleteObjectRequest.builder().bucket(bucket.getBucketName()).key(s3Object.key()).build());
            }

            if (listObjResponse.commonPrefixes() != null) {
                for (CommonPrefix commonPrefixes : listObjResponse.commonPrefixes()) {
                    deleteObj(s3, bucket, commonPrefixes.prefix());
                }
            } else {
                s3.deleteObject(DeleteObjectRequest.builder().bucket(bucket.getBucketName()).key(objectId).build());
            }

            if (listObjResponse.nextContinuationToken() == null) {
                done = true;
            }
        }
    }

    @Override
    public void createDir(OssBucket bucket, OssWithBLOBs account, String dir) throws Exception {
        dir = dir.endsWith("/") ? dir : dir+ "/";
        S3Client s3 = getS3Client(account.getCredential(), bucket.getLocation());
        String[] split = dir.split("/");
        String data = "";
        for (String d : split) {
            if("".equals(d)){
                continue;
            }
            data += d + "/";
            if(!"/".equals(data)){
                PutObjectResponse putObjectResponse = s3.putObject(PutObjectRequest.builder().bucket(bucket.getBucketName()).key(data).build(), RequestBody.fromBytes("".getBytes()));
            }
        }
    }

    @Override
    public void uploadFile(OssBucket bucket, OssWithBLOBs account, String objectId, InputStream file, long size) throws Exception {
        S3Client s3 = getS3Client(account.getCredential(), bucket.getLocation());
        s3.putObject(PutObjectRequest.builder().bucket(bucket.getBucketName()).key(objectId).build(), RequestBody.fromInputStream(file, size));
    }

    @Override
    public void deleteKey(OssBucket bucket, OssWithBLOBs account, String name) throws Exception {

    }
}
