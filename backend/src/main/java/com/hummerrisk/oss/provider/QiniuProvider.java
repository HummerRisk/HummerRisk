package com.hummerrisk.oss.provider;


import com.alibaba.fastjson.JSON;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.model.BosObjectSummary;
import com.baidubce.services.bos.model.BucketSummary;
import com.baidubce.services.bos.model.ListObjectsResponse;
import com.hummerrisk.base.domain.Oss;
import com.hummerrisk.base.domain.OssBucket;
import com.hummerrisk.base.domain.OssWithBLOBs;
import com.hummerrisk.commons.constants.RegionsConstants;
import com.hummerrisk.commons.utils.PlatformUtils;
import com.hummerrisk.oss.dto.BucketObjectDTO;
import com.hummerrisk.oss.dto.OssRegion;
import com.hummerrisk.proxy.qiniu.QiniuCredential;
import com.hummerrisk.service.SysListener;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.*;
import com.qiniu.storage.model.BucketInfo;
import com.qiniu.storage.model.FetchRet;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.storage.model.FileListing;
import com.qiniu.util.Auth;
import com.qiniu.util.StringUtils;
import org.ini4j.Reg;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        boolean isEnd = false;
        int maxPage = 10;
        int page = 1;
        while (!isEnd) {
            FileListing fileListing = bucketManager.listFilesV2(bucket.getBucketName(), prefix, marker, 1000, null);
            marker = fileListing.marker;
            if (StringUtils.isNullOrEmpty(marker) || page >= maxPage) {
                isEnd = true;
            }
            for (FileInfo item : fileListing.items) {
                BucketObjectDTO bucketObjectDTO = new BucketObjectDTO();
                bucketObjectDTO.setBucketId(bucket.getId());
                bucketObjectDTO.setObjectType("FILE");
                bucketObjectDTO.setId(item.key);
                bucketObjectDTO.setObjectName(item.key);
                bucketObjectDTO.setObjectSize(SysListener.changeFlowFormat(item.fsize));
                bucketObjectDTO.setLastModified(item.putTime/10000);
                result.add(bucketObjectDTO);
            }
            page++;
        }

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
        return false;
    }

    @Override
    public OssBucket createBucket(OssWithBLOBs ossAccount, OssBucket bucket) throws Exception {
        return null;
    }

    @Override
    public void deleteBucket(OssWithBLOBs ossAccount, OssBucket bucket) throws Exception {

    }

    @Override
    public List<OssRegion> getOssRegions(OssWithBLOBs ossAccount) throws Exception {
        return null;
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

}
