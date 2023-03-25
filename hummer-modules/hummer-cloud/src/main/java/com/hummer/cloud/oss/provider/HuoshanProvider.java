package com.hummer.cloud.oss.provider;


import com.hummer.cloud.oss.dto.BucketObjectDTO;
import com.hummer.common.core.domain.OssBucket;
import com.hummer.common.core.domain.OssRegion;
import com.hummer.common.core.domain.OssWithBLOBs;

import java.io.FilterInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HuoshanProvider implements OssProvider {

    private static final String BASE_REGION_DIC = "support/regions/";
    private static final String JSON_EXTENSION = ".json";

    @Override
    public String policyModel() {
        return "";
    }

    @Override
    public List<OssBucket> getOssBucketList(OssWithBLOBs ossAccount) {
        List<OssBucket> resultList = new ArrayList<>();
        return resultList;
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

    public boolean doesBucketExist(OssWithBLOBs ossAccount, OssBucket bucket) throws Exception{
        return true;
    }

    public OssBucket createBucket(OssWithBLOBs ossAccount, OssBucket bucket) throws Exception{
        return new OssBucket();
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
