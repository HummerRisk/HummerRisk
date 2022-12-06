package com.hummerrisk.oss.provider;


import com.alibaba.fastjson.JSON;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.model.BosObjectSummary;
import com.baidubce.services.bos.model.BucketSummary;
import com.baidubce.services.bos.model.ListObjectsResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hummerrisk.base.domain.Oss;
import com.hummerrisk.base.domain.OssBucket;
import com.hummerrisk.base.domain.OssWithBLOBs;
import com.hummerrisk.commons.constants.RegionsConstants;
import com.hummerrisk.commons.utils.PlatformUtils;
import com.hummerrisk.commons.utils.ReadFileUtils;
import com.hummerrisk.oss.constants.ObjectTypeConstants;
import com.hummerrisk.oss.dto.BucketObjectDTO;
import com.hummerrisk.oss.dto.OssRegion;
import com.hummerrisk.proxy.qiniu.QiniuCredential;
import com.hummerrisk.service.SysListener;
import com.qingstor.sdk.service.Bucket;
import com.qingstor.sdk.service.Types;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.*;
import com.qiniu.storage.model.*;
import com.qiniu.util.Auth;
import com.qiniu.util.StringUtils;
import org.ini4j.Reg;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class QiniuProvider implements OssProvider {

    private static final String BASE_REGION_DIC = "support/regions/";
    private static final String JSON_EXTENSION = ".json";

    @Override
    public String policyModel() {
        return "";
    }

    @Override
    public List<OssBucket> getOssBucketList(OssWithBLOBs ossAccount) throws QiniuException {
        List<OssBucket> resultList = new ArrayList<>();
        BucketManager bucketManager = getBucketManager(ossAccount);
        String[] buckets = bucketManager.buckets();
        for (String bucket : buckets) {
            BucketInfo bucketInfo = bucketManager.getBucketInfo(bucket);
            String[] domains = bucketManager.domainList(bucket);
            OssBucket ossBucket = new OssBucket();
            ossBucket.setOssId(ossAccount.getId());
            ossBucket.setBucketName(bucket);
            ossBucket.setLocation(bucketInfo.getRegion());
            ossBucket.setCreateTime(System.currentTimeMillis());
            ossBucket.setExtranetEndpoint("");
            ossBucket.setIntranetEndpoint("");
            ossBucket.setStorageClass("N/A");
            ossBucket.setCannedAcl(bucketInfo.getPrivate()==1?"private":"public");
            ossBucket.setDomainName(domains.length>0?domains[0]:"");
            ossBucket.setSize("0");
            ossBucket.setObjectNumber(0L);
            resultList.add(ossBucket);
        }

        return resultList;
    }

    private BucketManager getBucketManager(OssWithBLOBs ossAccount) {
        Configuration cfg = new Configuration(Region.autoRegion());
        Auth auth = getAuth(ossAccount);
        return new BucketManager(auth, cfg);
    }

    private UploadManager getUploadManager(OssWithBLOBs ossAccount){
        Configuration cfg = new Configuration(Region.autoRegion());
        return new UploadManager(cfg);
    }

    private Auth getAuth(OssWithBLOBs ossAccount){
        QiniuCredential qiniuCredential = JSON.parseObject(ossAccount.getCredential(), QiniuCredential.class);
        return Auth.create(qiniuCredential.getAccessKey(), qiniuCredential.getSecretKey());
    }
    private Region getRegion(String region) {
        switch (region) {
            case "z0":
                return Region.huadong();
            case "z1":
                return Region.huabei();
            case "z2":
                return Region.huanan();
            case "na0":
                return Region.beimei();
            case "as0":
                return Region.regionAs0();
            case "fog-cn-east-1":
                return Region.regionFogCnEast1();
            case "fog-cn-east-2":
                return Region.huadongZheJiang2();
        }
        return Region.autoRegion();
    }

    @Override
    public List<BucketObjectDTO> getBucketObjects(OssBucket bucket, OssWithBLOBs account, String prefix) throws QiniuException {
        BucketManager bucketManager = getBucketManager(account);
        String marker = null;
        List<BucketObjectDTO> result = new ArrayList<>();
        List<FileInfo> fileInfoList = new ArrayList<>();
        boolean isEnd = false;
        int maxPage = 10;
        int page = 1;
        while (!isEnd) {
            FileListing fileListing = bucketManager.listFilesV2(bucket.getBucketName(), prefix, marker, 1000, null);
            marker = fileListing.marker;
            if (StringUtils.isNullOrEmpty(marker) || page >= maxPage) {
                isEnd = true;
            }
            fileInfoList.addAll(Arrays.stream(fileListing.items).collect(Collectors.toList()));
            page++;
        }
        if(org.apache.commons.lang3.StringUtils.isNotBlank(prefix)&&prefix.lastIndexOf("/")>0){
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
            result.add(bucketObjectDTO);
        }
        result.addAll(convertToBucketFolder(bucket,fileInfoList,prefix));
        result.addAll(convertToBucketObject(bucket,fileInfoList,prefix));
        return result;
    }

    @Override
    public FilterInputStream downloadObject(OssBucket bucket, OssWithBLOBs account, final String objectId) throws Exception {
        String domainOfBucket = "http://"+bucket.getDomainName();
        String encodedFileName = URLEncoder.encode(objectId, "utf-8").replace("+", "%20");
        String publicUrl = String.format("%s/%s", domainOfBucket, encodedFileName);
        Auth auth = getAuth(account);
        long expireInSeconds = 300;
        String finalUrl = auth.privateDownloadUrl(publicUrl, expireInSeconds);
        return new BufferedInputStream(new URL(finalUrl).openStream());

    }

    @Override
    public boolean doesBucketExist(OssWithBLOBs ossAccount, OssBucket bucket) throws Exception {
        BucketManager bucketManager = getBucketManager(ossAccount);
        try {
            bucketManager.getBucketInfo(bucket.getBucketName());
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public OssBucket createBucket(OssWithBLOBs ossAccount, OssBucket bucket) throws Exception {
        BucketManager bucketManager = getBucketManager(ossAccount);
        Response bucket1 = bucketManager.createBucket(bucket.getBucketName(), bucket.getLocation());
        if(bucket1.statusCode==200){
            if("private".equals(bucket.getCannedAcl())){
                bucketManager.putBucketAccessMode(bucket.getBucketName(), AclType.PRIVATE);
            }
            OssBucket ossBucket = new OssBucket();
            ossBucket.setCannedAcl(bucket.getCannedAcl());
            ossBucket.setOssId(ossAccount.getId());
            ossBucket.setBucketName(bucket.getBucketName());
            ossBucket.setLocation(bucket.getLocation());
            ossBucket.setCreateTime(System.currentTimeMillis());
            ossBucket.setExtranetEndpoint("");
            ossBucket.setIntranetEndpoint("");
            ossBucket.setStorageClass("N/A");
            ossBucket.setSize("0");
            ossBucket.setObjectNumber(0L);
            return ossBucket;
        }else if(bucket1.statusCode==614){
            throw new RuntimeException("Bucket already exists");
        }else{
            throw new RuntimeException("Bucket create error");
        }
    }

    @Override
    public void deleteBucket(OssWithBLOBs ossAccount, OssBucket bucket) throws Exception {
        throw new RuntimeException("delete not support now");
    }

    @Override
    public List<OssRegion> getOssRegions(OssWithBLOBs ossAccount) throws Exception {
        String result = ReadFileUtils.readConfigFile(BASE_REGION_DIC, ossAccount.getPluginId(), JSON_EXTENSION);
        return new Gson().fromJson(result, new TypeToken<ArrayList<OssRegion>>() {
        }.getType());
    }

    @Override
    public void deletetObjects(OssBucket bucket, OssWithBLOBs account, List<String> objectIds) throws Exception {
        BucketManager bucketManager = getBucketManager(account);
        objectIds.forEach(item->{
            try {
                bucketManager.delete(bucket.getBucketName(), item);
            } catch (QiniuException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void createDir(OssBucket bucket, OssWithBLOBs account, String dir) throws Exception {
        dir = dir.endsWith("/") ? dir : dir + "/";
        String[] split = dir.split("/");
        String data = "";
        Auth auth = getAuth(account);
        String upToken = auth.uploadToken(bucket.getBucketName());
        for (String d : split) {
            if(d.length()==0){
                continue;
            }
            data += d + "/";
            Response response = getUploadManager(account).put(new ByteArrayInputStream("".getBytes()), data, upToken,null,null);
        }
    }

    @Override
    public void uploadFile(OssBucket bucket, OssWithBLOBs account, String dir, InputStream file, long size) throws Exception {
        Auth auth = getAuth(account);
        String upToken = auth.uploadToken(bucket.getBucketName());
        Response response = getUploadManager(account).put(file, dir, upToken,null,null);
    }

    @Override
    public void deleteKey(OssBucket bucket, OssWithBLOBs account, String name) throws Exception {

    }

    private List<BucketObjectDTO> convertToBucketFolder(OssBucket bucket, List<FileInfo> commonPrefixes, String prefix) {
        List<BucketObjectDTO> objects = new ArrayList<>();
        for (FileInfo keyModel: commonPrefixes) {
            if(!keyModel.key.contains("/")||!keyModel.key.endsWith("/")||keyModel.key.equals(prefix)){
                continue;
            }
            BucketObjectDTO bucketObject = new BucketObjectDTO();
            bucketObject.setBucketId(bucket.getId());
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(prefix)) {
                bucketObject.setObjectName(keyModel.key.substring(prefix.length(), keyModel.key.length()));
            } else {
                bucketObject.setObjectName(keyModel.key);
            }
            if(bucketObject.getObjectName().indexOf("/")!=bucketObject.getObjectName().lastIndexOf("/")){
                continue;
            }
            bucketObject.setId(keyModel.key);
            bucketObject.setObjectType(ObjectTypeConstants.DIR.name());
            objects.add(bucketObject);
        }
        return objects;
    }

    private List<BucketObjectDTO> convertToBucketObject(OssBucket bucket, List<FileInfo> keyModels, String prefix) {
        List<BucketObjectDTO> objects = new ArrayList<>();
        for (FileInfo keyModel : keyModels) {
            if(keyModel.key.endsWith("/")){
                continue;
            }
            BucketObjectDTO bucketObject = new BucketObjectDTO();
            bucketObject.setBucketId(bucket.getId());
            bucketObject.setId(keyModel.key);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(prefix)) {
                String objectName = keyModel.key.substring(prefix.length(), keyModel.key.length());
                bucketObject.setObjectName(objectName);
            } else {
                bucketObject.setObjectName(keyModel.key);
            }
            if(bucketObject.getObjectName().contains("/")){
                continue;
            }
            bucketObject.setObjectType(ObjectTypeConstants.FILE.name());
            long getSize = keyModel != null ? keyModel.fsize : 0;
            double size = ((double) getSize) / 1024 / 1024;
            DecimalFormat df = new DecimalFormat("#0.###");
            bucketObject.setObjectSize(df.format(size));
            bucketObject.setStorageClass("N/A");
            bucketObject.setLastModified(keyModel.putTime/10000);
            objects.add(bucketObject);
        }
        return objects;
    }
}
