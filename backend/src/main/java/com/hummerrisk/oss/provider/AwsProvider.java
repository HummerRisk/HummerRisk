package com.hummerrisk.oss.provider;


import com.alibaba.fastjson.JSON;
import com.hummerrisk.base.domain.OssBucket;
import com.hummerrisk.base.domain.OssWithBLOBs;
import com.hummerrisk.oss.dto.BucketMetric;
import com.hummerrisk.oss.dto.BucketObjectDTO;
import com.hummerrisk.service.SysListener;
import com.tencentcloudapi.common.Credential;
import org.apache.commons.collections.CollectionUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AwsProvider implements OssProvider {

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

    private OssBucket setBucket(S3Client s3, software.amazon.awssdk.services.s3.model.Bucket bucket, OssWithBLOBs account) {
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
        Credential object = JSON.parseObject(credential, Credential.class);
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(object.getSecretId(), object.getSecretKey());
        return  CloudWatchClient.builder().credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials)).region(Region.of(region)).build();
    }

    private S3Client getS3Client (String credential, String region){
        Credential object = JSON.parseObject(credential, Credential.class);
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(object.getSecretId(), object.getSecretKey());
        return S3Client.builder().credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials)).region(Region.of(region)).build();
    }

    @Override
    public List<BucketObjectDTO> getBucketObjects(OssBucket bucket, OssWithBLOBs account, String prefix) {
        List<BucketObjectDTO> objects = new ArrayList<>();
        return objects;
    }

}
