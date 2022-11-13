package com.hummerrisk.oss.provider;


import com.hummerrisk.base.domain.OssBucket;
import com.hummerrisk.base.domain.OssWithBLOBs;
import com.hummerrisk.oss.dto.BucketObjectDTO;

import java.io.FilterInputStream;
import java.util.ArrayList;
import java.util.List;

public class QingcloudProvider implements OssProvider {


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

}
