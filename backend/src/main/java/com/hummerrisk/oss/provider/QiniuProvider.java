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
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.model.BucketInfo;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.storage.model.FileListing;
import com.qiniu.util.Auth;
import com.qiniu.util.StringUtils;
import org.ini4j.Reg;

import java.io.FilterInputStream;
import java.io.InputStream;
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
            OssBucket ossBucket = new OssBucket();
            ossBucket.setOssId(ossAccount.getId());
            ossBucket.setBucketName(bucket);
            ossBucket.setLocation(Region.autoRegion().toString());
            ossBucket.setCreateTime(System.currentTimeMillis());
            ossBucket.setExtranetEndpoint("");
            ossBucket.setIntranetEndpoint("");
            ossBucket.setStorageClass("N/A");
            ossBucket.setCannedAcl("N/A");
            ossBucket.setDomainName(Region.autoRegion().toString());
            ossBucket.setSize("0");
            ossBucket.setObjectNumber(0L);
            resultList.add(ossBucket);
        }

        return resultList;
    }

    private BucketManager getBucketManager(OssWithBLOBs ossAccount) {
        Configuration cfg = new Configuration(Region.autoRegion());
        QiniuCredential qiniuCredential = JSON.parseObject(ossAccount.getCredential(), QiniuCredential.class);
        Auth auth = Auth.create(qiniuCredential.getAccessKey(), qiniuCredential.getSecretKey());
        return new BucketManager(auth, cfg);
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
                bucketObjectDTO.setObjectName(item.key);
                bucketObjectDTO.setObjectSize(SysListener.changeFlowFormat(item.fsize));
                bucketObjectDTO.setLastModified(item.putTime);
                result.add(bucketObjectDTO);
            }
            page++;
        }

        return result;
    }

    @Override
    public FilterInputStream downloadObject(OssBucket bucket, OssWithBLOBs account, final String objectId) throws Exception {
        FilterInputStream filterInputStream = null;
        return filterInputStream;
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

    }

    @Override
    public void createDir(OssBucket bucket, OssWithBLOBs account, String dir) throws Exception {

    }

    @Override
    public void uploadFile(OssBucket bucket, OssWithBLOBs account, String dir, InputStream file, long size) throws Exception {

    }

    @Override
    public void deleteKey(OssBucket bucket, OssWithBLOBs account, String name) throws Exception {

    }

}
