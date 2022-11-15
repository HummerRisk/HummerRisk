package com.hummerrisk.oss.provider;


import com.hummerrisk.base.domain.OssBucket;
import com.hummerrisk.base.domain.OssWithBLOBs;
import com.hummerrisk.oss.dto.BucketObjectDTO;
import com.hummerrisk.oss.dto.OssRegion;

import java.io.FilterInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class QiniuProvider implements OssProvider {


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
