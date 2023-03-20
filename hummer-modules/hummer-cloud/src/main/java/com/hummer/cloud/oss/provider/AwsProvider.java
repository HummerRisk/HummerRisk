package com.hummer.cloud.oss.provider;


import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hummer.cloud.oss.constants.ObjectTypeConstants;
import com.hummer.cloud.oss.dto.BucketMetric;
import com.hummer.cloud.oss.dto.BucketObjectDTO;
import com.hummer.common.core.domain.OssBucket;
import com.hummer.common.core.domain.OssRegion;
import com.hummer.common.core.domain.OssWithBLOBs;
import com.hummer.common.core.proxy.aws.AWSCredential;
import com.hummer.common.core.utils.ReadFileUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeRegionsResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.FilterInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AwsProvider implements OssProvider {

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
    public List<OssBucket> getOssBucketList(OssWithBLOBs ossAccount) {
        List<OssBucket> resultList = new ArrayList<>();
        S3Client s3 = getS3Client(ossAccount.getCredential(), "ap-northeast-1");
        try {
            ListBucketsResponse listBucketsResponse = s3.listBuckets();
            resultList = getOssBucket(ossAccount, listBucketsResponse, s3);
            s3.close();
            return resultList;
        } catch (Exception e) {
            s3.close();
            try{
                s3 = getS3Client(ossAccount.getCredential(), "cn-north-1");
                ListBucketsResponse listBucketsResponse = s3.listBuckets();
                resultList = getOssBucket(ossAccount, listBucketsResponse, s3);
                s3.close();
                return resultList;
            }catch (Exception e1){
                s3.close();
            }finally {
                s3.close();
            }
        }finally {
            s3.close();
        }
        return resultList;
    }

    private List<OssBucket> getOssBucket(OssWithBLOBs ossAccount, ListBucketsResponse listBucketsResponse, S3Client s3) {
        List<OssBucket> resultList = new ArrayList<>();
        if (listBucketsResponse != null && !CollectionUtils.isEmpty(listBucketsResponse.buckets())) {
            for (Bucket bucket : listBucketsResponse.buckets()) {
                OssBucket tmpBucket = setBucket(s3, bucket ,ossAccount);
                BucketMetric bucketMetric = getBucketMetric(tmpBucket, ossAccount);
                tmpBucket.setObjectNumber(bucketMetric.getObjectNumber());
                tmpBucket.setSize(SysListener.changeFlowFormat(bucketMetric.getSize()));
                tmpBucket.setStorageClass("N/A");
                resultList.add(tmpBucket);
            }
        }
        return resultList;
    }

    private OssBucket setBucket(S3Client s3, Bucket bucket, OssWithBLOBs account) {
        GetBucketLocationResponse getBucketLocationResponse  = s3.getBucketLocation(GetBucketLocationRequest.builder().bucket(bucket.name()).build());
        OssBucket bucketDTO = new OssBucket();
        bucketDTO.setBucketName(bucket.name());
        bucketDTO.setOssId(account.getId());
        bucketDTO.setLocation(getBucketLocationResponse.locationConstraintAsString());
        setAcl(bucketDTO, account);
        bucketDTO.setSize("0");
        bucketDTO.setObjectNumber(0L);
        return bucketDTO;
    }

    private void setAcl(OssBucket bucketDTO, OssWithBLOBs account){
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

        List<Dimension> dimensionsNumberOfObjects = new ArrayList<>();
        dimensionsNumberOfObjects.add(Dimension.builder().name("StorageType").value("AllStorageTypes").build());
        dimensionsNumberOfObjects.add(Dimension.builder().name("BucketName").value(bucket.getBucketName()).build());
        Metric NumberOfObjects = Metric.builder().metricName("NumberOfObjects").namespace("AWS/S3").dimensions(dimensionsNumberOfObjects).build();

        List<Dimension> dimensionsStandardStorage = new ArrayList<>();
        dimensionsStandardStorage.add(Dimension.builder().name("BucketName").value(bucket.getBucketName()).build());
        dimensionsStandardStorage.add(Dimension.builder().name("StorageType").value("StandardStorage").build());
        Metric StandardStorage = Metric.builder().metricName("BucketSizeBytes").namespace("AWS/S3").dimensions(dimensionsStandardStorage).build();

        List<Dimension> dimensionsStandardIAStorage = new ArrayList<>();
        dimensionsStandardIAStorage.add(Dimension.builder().name("BucketName").value(bucket.getBucketName()).build());
        dimensionsStandardIAStorage.add(Dimension.builder().name("StorageType").value("StandardIAStorage").build());
        Metric StandardIAStorage = Metric.builder().metricName("BucketSizeBytes").namespace("AWS/S3").dimensions(dimensionsStandardIAStorage).build();

        List<Dimension> dimensionsOneZoneIAStorage = new ArrayList<>();
        dimensionsOneZoneIAStorage.add(Dimension.builder().name("BucketName").value(bucket.getBucketName()).build());
        dimensionsOneZoneIAStorage.add(Dimension.builder().name("StorageType").value("OneZoneIAStorage").build());
        Metric OneZoneIAStorage = Metric.builder().metricName("BucketSizeBytes").namespace("AWS/S3").dimensions(dimensionsOneZoneIAStorage).build();

        List<Dimension> dimensionsGlacierStorage = new ArrayList<>();
        dimensionsGlacierStorage.add(Dimension.builder().name("BucketName").value(bucket.getBucketName()).build());
        dimensionsGlacierStorage.add(Dimension.builder().name("StorageType").value("GlacierStorage").build());
        Metric GlacierStorage = Metric.builder().metricName("BucketSizeBytes").namespace("AWS/S3").dimensions(dimensionsGlacierStorage).build();


        List<MetricDataQuery> metricDataQueries = new ArrayList<>();
        metricDataQueries.add(MetricDataQuery.builder().id("metric_alias0").metricStat(MetricStat.builder().stat("Average").period(300).metric(NumberOfObjects).build()).returnData(true).build());
        metricDataQueries.add(MetricDataQuery.builder().id("metric_alias1").metricStat(MetricStat.builder().stat("Average").period(300).metric(StandardStorage).build()).returnData(true).build());
        metricDataQueries.add(MetricDataQuery.builder().id("metric_alias2").metricStat(MetricStat.builder().stat("Average").period(300).metric(StandardIAStorage).build()).returnData(true).build());
        metricDataQueries.add(MetricDataQuery.builder().id("metric_alias3").metricStat(MetricStat.builder().stat("Average").period(300).metric(OneZoneIAStorage).build()).returnData(true).build());
        metricDataQueries.add(MetricDataQuery.builder().id("metric_alias4").metricStat(MetricStat.builder().stat("Average").period(300).metric(GlacierStorage).build()).returnData(true).build());
        GetMetricDataRequest getMetricDataRequest = GetMetricDataRequest.builder().startTime(Instant.now().minusMillis(TimeUnit.HOURS.toMillis(24))).endTime(Instant.now()).metricDataQueries(metricDataQueries).build();
        CloudWatchClient cloudWatchClient = getCloudWatchClient(account.getCredential(), bucket.getLocation());
        List<MetricDataResult> metricDataResults = cloudWatchClient.getMetricData(getMetricDataRequest).metricDataResults();
        BucketMetric bucketMetric = new BucketMetric();
        bucketMetric.setSize(0L);
        bucketMetric.setObjectNumber(0L);
        for (MetricDataResult metricDataResult : metricDataResults) {
            if(!metricDataResult.statusCode().equals(StatusCode.COMPLETE) || CollectionUtils.isEmpty(metricDataResult.values())){
                continue;
            }
            if(metricDataResult.id().equals("metric_alias0")){
                bucketMetric.setObjectNumber(metricDataResult.values().get(0).longValue());
                continue;
            }
            bucketMetric.setSize(bucketMetric.getSize() + metricDataResult.values().get(0).longValue()/1024/1024);
        }
        cloudWatchClient.close();
        return bucketMetric;
    }

    private CloudWatchClient getCloudWatchClient (String credential, String region){
        AWSCredential object = JSON.parseObject(credential, AWSCredential.class);
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(object.getAccessKey(), object.getSecretKey());
        return  CloudWatchClient.builder().credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials)).region(Region.of(region)).build();
    }

    private S3Client getS3Client (String credential, String region){
        AWSCredential object = JSON.parseObject(credential, AWSCredential.class);
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(object.getAccessKey(), object.getSecretKey());
        return S3Client.builder().credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials)).region(Region.of(region)).build();
    }

    @Override
    public List<BucketObjectDTO> getBucketObjects(OssBucket bucket, OssWithBLOBs account, String prefix) {
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
    public boolean doesBucketExist(OssWithBLOBs ossAccount, OssBucket bucket) {
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
        AWSCredential awsCredential = JSON.parseObject(ossAccount.getCredential(), AWSCredential.class);
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(awsCredential.getAccessKey(), awsCredential.getSecretKey());
        Ec2Client ec2Client;
        DescribeRegionsResponse describeRegionsResponse;

        try{
            ec2Client = Ec2Client.builder().region(Region.CN_NORTH_1).credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials)).build();
            describeRegionsResponse  = ec2Client.describeRegions();
        }catch (Exception e){
            ec2Client = Ec2Client.builder().region(Region.US_EAST_1).credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials)).build();
            describeRegionsResponse  = ec2Client.describeRegions();
        }

        List<software.amazon.awssdk.services.ec2.model.Region> regions = describeRegionsResponse.regions();
        String result = ReadFileUtils.readConfigFile(BASE_REGION_DIC, ossAccount.getPluginId(), JSON_EXTENSION);
        List<OssRegion> allOssRegions = new Gson().fromJson(result, new TypeToken<ArrayList<OssRegion>>() {}.getType());
        List<OssRegion> ossRegions = new ArrayList<>();
        for (software.amazon.awssdk.services.ec2.model.Region region : regions) {
            for (OssRegion ossRegion : allOssRegions) {
                if(ossRegion.getRegionId().equals(region.regionName())){
                    ossRegions.add(ossRegion);
                    break;
                }
            }
        }
        ec2Client.close();
        return ossRegions;
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
            data += d + "/";
            s3.putObject(PutObjectRequest.builder().bucket(bucket.getBucketName()).key(data).build(), RequestBody.empty());
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
