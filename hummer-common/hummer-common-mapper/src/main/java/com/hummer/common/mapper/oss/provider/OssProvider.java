package com.hummer.common.mapper.oss.provider;

import com.hummer.common.mapper.domain.OssBucket;
import com.hummer.common.mapper.domain.OssWithBLOBs;
import com.hummer.common.mapper.oss.dto.BucketObjectDTO;
import com.hummer.common.mapper.oss.dto.OssRegion;

import java.io.FilterInputStream;
import java.io.InputStream;
import java.util.List;

public interface OssProvider {

    public String policyModel();

    public List<OssBucket> getOssBucketList(OssWithBLOBs ossAccount) throws Exception;

    public List<BucketObjectDTO> getBucketObjects(OssBucket bucket, OssWithBLOBs account, String prefix) throws Exception;

    public FilterInputStream downloadObject(OssBucket bucket, OssWithBLOBs account, final String objectId) throws Exception;

    public boolean doesBucketExist(OssWithBLOBs ossAccount, OssBucket bucket) throws Exception;

    public OssBucket createBucket(OssWithBLOBs ossAccount, OssBucket bucket) throws Exception;

    public void deleteBucket(OssWithBLOBs ossAccount, OssBucket bucket) throws Exception;

    public List<OssRegion> getOssRegions(OssWithBLOBs ossAccount) throws Exception;

    public void deletetObjects(OssBucket bucket, OssWithBLOBs account, final List<String> objectIds) throws Exception;

    public void createDir(OssBucket bucket, OssWithBLOBs account, String dir)throws Exception;

    public void uploadFile(OssBucket bucket, OssWithBLOBs account, String dir, InputStream file, long size)throws Exception;

    public void deleteKey(OssBucket bucket, OssWithBLOBs account, String name)throws Exception;


}
