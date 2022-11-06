package com.hummerrisk.oss.provider;


import com.hummerrisk.base.domain.OssBucket;
import com.hummerrisk.base.domain.OssWithBLOBs;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class AwsProvider implements OssProvider {


    @Override
    public List<OssBucket> getOssBucketList(OssWithBLOBs ossAccount) {
        List<OssBucket> resultList = new ArrayList<>();
        return resultList;
    }

}
