package com.hummerrisk.oss.constants;

import org.apache.commons.lang3.StringUtils;

public interface OSSConstants {

    enum ACCOUNT_STATUS {
        VALID,
        INVALID;

        public boolean equals(String name) {
            return StringUtils.equals(this.name(), name);
        }
    }

    enum SYNC_STATUS {
        PENDING,
        SUCCESS,
        ERROR,
        SYNC;

        public boolean equals(String status) {
            return StringUtils.equals(this.name(), status);
        }
    }

    public final static String aws = "hummer-aws-plugin";
    public final static String aliyun = "hummer-aliyun-plugin";
    public final static String huawei = "hummer-huawei-plugin";
    public final static String tencent = "hummer-qcloud-plugin";
    public final static String huoshan = "hummer-huoshan-plugin";
    public final static String baidu = "hummer-baidu-plugin";
    public final static String qiniu = "hummer-qiniu-plugin";
    public final static String qingcloud = "hummer-qingcloud-plugin";
    public final static String ucloud = "hummer-ucloud-plugin";
}
