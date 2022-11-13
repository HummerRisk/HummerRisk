package com.hummerrisk.oss.provider;

import com.hummerrisk.base.domain.OssBucket;
import com.hummerrisk.base.domain.OssWithBLOBs;
import com.hummerrisk.oss.dto.BucketObjectDTO;

import java.io.FilterInputStream;
import java.util.List;

public interface OssProvider {

    public String policyModel();

    public List<OssBucket> getOssBucketList(OssWithBLOBs ossAccount) throws Exception;

    public List<BucketObjectDTO> getBucketObjects(OssBucket bucket, OssWithBLOBs account, String prefix) throws Exception;

    public FilterInputStream downloadObject(OssBucket bucket, OssWithBLOBs account, final String objectId) throws Exception;

}
