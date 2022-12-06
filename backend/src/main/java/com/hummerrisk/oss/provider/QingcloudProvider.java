package com.hummerrisk.oss.provider;


import com.alibaba.fastjson.JSON;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.model.BosObjectSummary;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hummerrisk.base.domain.OssBucket;
import com.hummerrisk.base.domain.OssWithBLOBs;
import com.hummerrisk.commons.utils.ReadFileUtils;
import com.hummerrisk.oss.constants.ObjectTypeConstants;
import com.hummerrisk.oss.dto.BucketObjectDTO;
import com.hummerrisk.oss.dto.OssRegion;
import com.hummerrisk.proxy.qingcloud.QingCloudCredential;
import com.hummerrisk.service.SysListener;
import com.qingstor.sdk.config.EnvContext;
import com.qingstor.sdk.exception.QSException;
import com.qingstor.sdk.service.Bucket;
import com.qingstor.sdk.service.QingStor;
import com.qingstor.sdk.service.Types;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.DecimalFormat;
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
            ossBucket.setDomainName(item.getURL().replaceAll("https://",""));
            ossBucket.setIntranetEndpoint("N/A");
            ossBucket.setCannedAcl("private");
            ossBucket.setStorageClass("N/A");
            ossBucket.setSize("0B");
            ossBucket.setObjectNumber(0L);
            try {
                Bucket.GetBucketACLOutput acl = qingStor.getBucket(item.getName(), item.getLocation()).getACL();
                List<Types.ACLModel> acl1 = acl.getACL();
                acl1.forEach(aclModel -> {
                    if("QS_ALL_USERS".equals(aclModel.getGrantee().getName())){
                        ossBucket.setCannedAcl("public");
                    }
                });
            } catch (QSException e) {
                throw new RuntimeException(e);
            }
            return ossBucket;
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
        Bucket.ListObjectsInput listObjectsInput = new Bucket.ListObjectsInput();
        if(StringUtils.isNotEmpty(prefix)){
            listObjectsInput.setPrefix(prefix);
        }else{
            listObjectsInput.setPrefix("");
        }
        List<BucketObjectDTO> objects = new ArrayList<>();
        if(StringUtils.isNotBlank(prefix)&&prefix.lastIndexOf("/")>0){
            BucketObjectDTO bucketObjectDTO = new BucketObjectDTO();
            bucketObjectDTO.setBucketId(bucket.getId());
            String[] prefixs = prefix.split("/");
            StringBuilder name = new StringBuilder("");
            if(prefixs.length>1){
                for(int i=0;i<prefixs.length-1;i++){
                    name.append(prefixs[i]).append("/");
                }
            }
            if(name.length()==0){
                bucketObjectDTO.setId("/");
                bucketObjectDTO.setObjectName("/");
            }else{
                bucketObjectDTO.setId(name.toString());
                bucketObjectDTO.setObjectName(name.toString());
            }
            bucketObjectDTO.setObjectType(ObjectTypeConstants.BACK.name());
            objects.add(bucketObjectDTO);
        }
        Bucket.ListObjectsOutput listObjectsOutput = bucket1.listObjects(listObjectsInput);
        objects.addAll(convertToBucketFolder(bucket,listObjectsOutput.getKeys(),prefix));
        objects.addAll(convertToBucketObject(bucket,listObjectsOutput.getKeys(),prefix));
        //List<BucketObjectDTO> bucketObjectDTOS = new ArrayList<>();
        return objects;
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
        Bucket.GetBucketPolicyOutput policy = bucket1.getPolicy();
        return policy.getStatueCode()!=404;
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
        QingStor stor = this.getStor(account);
        dir = dir.endsWith("/") ? dir : dir + "/";
        String[] split = dir.split("/");
        String data = "";
        for (String d : split) {
            if(d.length()==0){
                continue;
            }
            data += d + "/";
            Bucket storBucket = stor.getBucket(bucket.getBucketName(), bucket.getLocation());
            Bucket.PutObjectInput input = new Bucket.PutObjectInput();
            input.setBodyInputStream(new ByteArrayInputStream(new byte[0]));
            Bucket.PutObjectOutput putObjectOutput = storBucket.putObject(data, input);
            if(putObjectOutput.getStatueCode()!=201){
                throw new RuntimeException("create dir error");
            }
        }
    }

    @Override
    public void uploadFile(OssBucket ossBucket, OssWithBLOBs account, String dir, InputStream file, long size) throws Exception {
        QingStor stor = getStor(account);
        Bucket bucket = stor.getBucket(ossBucket.getBucketName(), ossBucket.getLocation());
        Bucket.PutObjectInput input = new Bucket.PutObjectInput();
        //MessageDigest md5 = MessageDigest.getInstance("MD5");
        // 这里只起示例作用, 如果 body size 有可能非常大, 按一定 size 做拆分以分段
        // 上传形式来分 part 计算 md5 上传才是比较经济的方式.
//        byte[] data = file.readAllBytes();
//        String contentMD5 = Base64.getEncoder().encodeToString(md5.digest(data));
//        input.setContentMD5(contentMD5);
        // 此时 stream 已经被 consume 掉, 但对象存储 server side 也需要读取 body 内容,
        //  所以在 setBodyInputStream 之前,  可以通过:
        //  1. 流已经读取为 bytes array, 直接构造 ByteArrayInputStream 即可;
        // body = new ByteArrayInputStream(data);
        //  2. 如果流支持 markSupported(), 可以重设到流的开始.
        //  下面是方式 2 的示例:
        //file.reset();
        input.setBodyInputStream(file);
        Bucket.PutObjectOutput putObjectOutput =bucket.putObject(dir, input);
        if(putObjectOutput.getStatueCode()!=201){
            throw new RuntimeException("file upload error");
        }
    }

    @Override
    public void deleteKey(OssBucket bucket, OssWithBLOBs account, String name) throws Exception {

    }

    private List<BucketObjectDTO> convertToBucketFolder(OssBucket bucket, List<Types.KeyModel> commonPrefixes, String prefix) {
        List<BucketObjectDTO> objects = new ArrayList<>();
        for (Types.KeyModel keyModel: commonPrefixes) {
            if(!keyModel.getKey().contains("/")||!keyModel.getKey().endsWith("/")||keyModel.getKey().equals(prefix)){
                continue;
            }
            BucketObjectDTO bucketObject = new BucketObjectDTO();
            bucketObject.setBucketId(bucket.getId());
            if (StringUtils.isNotEmpty(prefix)) {
                bucketObject.setObjectName(keyModel.getKey().substring(prefix.length(), keyModel.getKey().length()));
            } else {
                bucketObject.setObjectName(keyModel.getKey());
            }
            if(bucketObject.getObjectName().indexOf("/")!=bucketObject.getObjectName().lastIndexOf("/")){
                continue;
            }
            bucketObject.setId(keyModel.getKey());
            bucketObject.setObjectType(ObjectTypeConstants.DIR.name());
            objects.add(bucketObject);
        }
        return objects;
    }

    private List<BucketObjectDTO> convertToBucketObject(OssBucket bucket, List<Types.KeyModel> keyModels, String prefix) {
        List<BucketObjectDTO> objects = new ArrayList<>();
        for (Types.KeyModel keyModel : keyModels) {
            if(keyModel.getKey().endsWith("/")){
                continue;
            }
            BucketObjectDTO bucketObject = new BucketObjectDTO();
            bucketObject.setBucketId(bucket.getId());
            bucketObject.setId(keyModel.getKey());
            if (StringUtils.isNotEmpty(prefix)) {
                String objectName = keyModel.getKey().substring(prefix.length(), keyModel.getKey().length());
                bucketObject.setObjectName(objectName);
            } else {
                bucketObject.setObjectName(keyModel.getKey());
            }
            if(bucketObject.getObjectName().contains("/")){
                continue;
            }
            bucketObject.setObjectType(ObjectTypeConstants.FILE.name());
            long getSize = keyModel != null ? keyModel.getSize() : 0;
            double size = ((double) getSize) / 1024 / 1024;
            DecimalFormat df = new DecimalFormat("#0.###");
            bucketObject.setObjectSize(df.format(size));
            bucketObject.setStorageClass(keyModel.getStorageClass());
            bucketObject.setLastModified(keyModel.getModified()*1000L);
            objects.add(bucketObject);
        }
        return objects;
    }
}
