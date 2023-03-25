package com.hummer.cloud.oss.provider;


import cn.ucloud.ufile.UfileClient;
import cn.ucloud.ufile.auth.BucketAuthorization;
import cn.ucloud.ufile.auth.UfileBucketLocalAuthorization;
import cn.ucloud.ufile.bean.BucketDescribeResponse;
import cn.ucloud.ufile.bean.BucketInfoBean;
import cn.ucloud.ufile.exception.UfileClientException;
import cn.ucloud.ufile.exception.UfileServerException;
import com.hummer.cloud.oss.dto.BucketObjectDTO;
import com.hummer.common.core.domain.OssBucket;
import com.hummer.common.core.domain.OssRegion;
import com.hummer.common.core.domain.OssWithBLOBs;

import java.io.FilterInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UcloudProvider implements OssProvider {

    private static final String BASE_REGION_DIC = "support/regions/";
    private static final String JSON_EXTENSION = ".json";
    @Override
    public String policyModel() {
        return "";
    }

    @Override
    public List<OssBucket> getOssBucketList(OssWithBLOBs ossAccount) throws UfileServerException, UfileClientException {
        List<OssBucket> resultList = new ArrayList<>();
        BucketAuthorization BUCKET_AUTHORIZER = new UfileBucketLocalAuthorization(
                "Your PublicKey", "Your PrivateKey");

        BucketDescribeResponse response = UfileClient.bucket(BUCKET_AUTHORIZER).describeBucket().execute();
        List<BucketInfoBean> bucketInfoList = response.getBucketInfoList();
        return bucketInfoList.stream().map(bucket->{
            OssBucket ossBucket = new OssBucket();
            ossBucket.setOssId(ossAccount.getId());
            ossBucket.setBucketName(bucket.getBucketName());
            ossBucket.setLocation(bucket.getRegion());
            ossBucket.setCreateTime(System.currentTimeMillis());
            ossBucket.setExtranetEndpoint("");
            ossBucket.setIntranetEndpoint("");
            ossBucket.setStorageClass("N/A");
            ossBucket.setCannedAcl("N/A");
            ossBucket.setDomainName(bucket.getRegion());
            ossBucket.setSize("0");
            ossBucket.setObjectNumber(0L);
            return ossBucket;
        }).collect(Collectors.toList());
    }

    @Override
    public List<BucketObjectDTO> getBucketObjects(OssBucket bucket, OssWithBLOBs account, String prefix) {
        List<BucketObjectDTO> objects = new ArrayList<>();
        return objects;
    }

    @Override
    public FilterInputStream downloadObject(OssBucket bucket, OssWithBLOBs account, final String objectId) throws Exception{
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
