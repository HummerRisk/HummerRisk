package com.hummerrisk.oss.provider;

import com.hummerrisk.base.domain.OssBucket;
import com.hummerrisk.base.domain.OssWithBLOBs;

import java.util.List;

public interface OssProvider {

    public String policyModel();

    public List<OssBucket> getOssBucketList(OssWithBLOBs ossAccount) throws Exception;

}
