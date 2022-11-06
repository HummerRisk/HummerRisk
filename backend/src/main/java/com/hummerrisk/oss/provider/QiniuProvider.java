package com.hummerrisk.oss.provider;


import com.hummerrisk.base.domain.OssBucket;
import com.hummerrisk.base.domain.OssWithBLOBs;

import java.util.ArrayList;
import java.util.List;

public class QiniuProvider implements OssProvider {




    @Override
    public List<OssBucket> getOssBucketList(OssWithBLOBs ossAccount) {
        List<OssBucket> resultList = new ArrayList<>();
        return resultList;
    }

}
