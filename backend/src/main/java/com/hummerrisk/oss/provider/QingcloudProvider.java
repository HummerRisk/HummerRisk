package com.hummerrisk.oss.provider;


import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hummerrisk.base.domain.OssBucket;
import com.hummerrisk.base.domain.OssWithBLOBs;
import com.hummerrisk.commons.utils.ReadFileUtils;
import com.hummerrisk.oss.dto.BucketObjectDTO;
import com.hummerrisk.oss.dto.OssRegion;
import com.hummerrisk.proxy.qingcloud.QingCloudCredential;
import com.hummerrisk.service.SysListener;
import com.qingstor.sdk.config.EnvContext;
import com.qingstor.sdk.exception.QSException;
import com.qingstor.sdk.service.Bucket;
import com.qingstor.sdk.service.QingStor;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class QingcloudProvider implements OssProvider {

    private static final String BASE_REGION_DIC = "support/regions/";
    private static final String JSON_EXTENSION = ".json";

    @Override
    public String policyModel() {
        return "";
    }

    @Override
    public List<OssBucket> getOssBucketList(OssWithBLOBs ossAccount) throws QSException {
        QingStor qingStor = getStor(ossAccount);
        QingStor.ListBucketsOutput listOutput = qingStor.listBuckets(null);
        return listOutput.getBuckets().stream().map(item->{
            OssBucket ossBucket = new OssBucket();
            ossBucket.setOssId(ossAccount.getId());
            ossBucket.setBucketName(item.getName());
            ossBucket.setLocation(item.getLocation());
            ossBucket.setDomainName(item.getURL());

            return new OssBucket();
        }).collect(Collectors.toList());
    }

    private QingStor getStor(OssWithBLOBs ossAccount){
        QingCloudCredential qingCloudCredential = JSON.parseObject(ossAccount.getCredential(),QingCloudCredential.class);
        EnvContext env = new EnvContext(qingCloudCredential.getAccessKeyId(), qingCloudCredential.getSecretAccessKey());
        return new QingStor(env);
    }

    @Override
    public List<BucketObjectDTO> getBucketObjects(OssBucket bucket, OssWithBLOBs account, String prefix) throws QSException {
        QingStor qingStor = getStor(account);
        Bucket bucket1 = qingStor.getBucket(bucket.getBucketName(), bucket.getLocation());
        Bucket.ListObjectsOutput listObjectsOutput = bucket1.listObjects(null);
        return listObjectsOutput.getKeys().stream().map(item -> {
            BucketObjectDTO bucketObjectDTO = new BucketObjectDTO();
            bucketObjectDTO.setBucketId(bucket.getId());
            bucketObjectDTO.setObjectName(item.getKey());
            bucketObjectDTO.setObjectType(item.getMimeType());
            bucketObjectDTO.setObjectSize(SysListener.changeFlowFormat(item.getSize()));
            bucketObjectDTO.setLastModified((long)item.getModified());
            bucketObjectDTO.setStorageClass(item.getStorageClass());
            return bucketObjectDTO;
        }).collect(Collectors.toList());

    }

    @Override
    public FilterInputStream downloadObject(OssBucket bucket, OssWithBLOBs account, final String objectId) throws Exception{
        QingStor qingStor = getStor(account);
        Bucket bucket1 = qingStor.getBucket(bucket.getBucketName(), bucket.getLocation());
        Bucket.GetObjectOutput object = bucket1.getObject(objectId, null);
        return new BufferedInputStream(object.getBodyInputStream());
    }

    @Override
    public boolean doesBucketExist(OssWithBLOBs ossAccount, OssBucket bucket) throws Exception {
        QingStor qingStor = getStor(ossAccount);
        Bucket bucket1 = qingStor.getBucket(bucket.getBucketName(), bucket.getLocation());
        return bucket1 != null;
    }

    @Override
    public OssBucket createBucket(OssWithBLOBs ossAccount, OssBucket bucket) throws Exception {
        if(doesBucketExist(ossAccount,bucket)){
            throw new Exception("Bucket already exists");
        };
        QingStor qingStor = getStor(ossAccount);
        Bucket bucket1 = qingStor.getBucket(bucket.getBucketName(), bucket.getLocation());
        Bucket.PutBucketOutput put = bucket1.put();
        if(put.getStatueCode()==201){
            OssBucket ossBucket = new OssBucket();
            ossBucket.setOssId(ossAccount.getId());
            ossBucket.setBucketName(bucket.getBucketName());
            ossBucket.setLocation(bucket.getLocation());
            return ossBucket;
        }
        return null;
    }

    @Override
    public void deleteBucket(OssWithBLOBs ossAccount, OssBucket bucket) throws Exception {

    }

    @Override
    public List<OssRegion> getOssRegions(OssWithBLOBs ossAccount) throws Exception {
        String result = ReadFileUtils.readConfigFile(BASE_REGION_DIC, ossAccount.getPluginId(), JSON_EXTENSION);
        return new Gson().fromJson(result, new TypeToken<ArrayList<OssRegion>>() {
        }.getType());
    }

    @Override
    public void deletetObjects(OssBucket ossBucket, OssWithBLOBs account, List<String> objectIds) throws Exception {
        QingStor stor = this.getStor(account);
        Bucket bucket = stor.getBucket(ossBucket.getBucketName(), ossBucket.getLocation());
        for (String objectId : objectIds) {
            Bucket.DeleteObjectOutput deleteObjectOutput = bucket.deleteObject(objectId);
        }
    }

    @Override
    public void createDir(OssBucket bucket, OssWithBLOBs account, String dir) throws Exception {

    }

    @Override
    public void uploadFile(OssBucket ossBucket, OssWithBLOBs account, String dir, InputStream file, long size) throws Exception {
        QingStor stor = getStor(account);
        Bucket bucket = stor.getBucket(ossBucket.getBucketName(), ossBucket.getLocation());
        Bucket.PutObjectInput input = new Bucket.PutObjectInput();
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        // 这里只起示例作用, 如果 body size 有可能非常大, 按一定 size 做拆分以分段
        // 上传形式来分 part 计算 md5 上传才是比较经济的方式.
        byte[] data = file.readAllBytes();
        String contentMD5 = Base64.getEncoder().encodeToString(md5.digest(data));
        input.setContentMD5(contentMD5);
        // 此时 stream 已经被 consume 掉, 但对象存储 server side 也需要读取 body 内容,
        //  所以在 setBodyInputStream 之前,  可以通过:
        //  1. 流已经读取为 bytes array, 直接构造 ByteArrayInputStream 即可;
        // body = new ByteArrayInputStream(data);
        //  2. 如果流支持 markSupported(), 可以重设到流的开始.
        //  下面是方式 2 的示例:
        file.reset();
        input.setBodyInputStream(file);
        Bucket.PutObjectOutput putObjectOutput =bucket.putObject(dir, input);
    }

    @Override
    public void deleteKey(OssBucket bucket, OssWithBLOBs account, String name) throws Exception {

    }

}
