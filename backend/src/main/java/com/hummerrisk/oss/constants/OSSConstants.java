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
        DRAFT, UNCHECKED, APPROVED, FINISHED, TERMINATED, CANCELED, REJECTED, PROCESSING, ERROR, WARNING, RUNNING, PENDING, PAUSE, WAITING;

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

    public final static String[] SUPPORT_RESOURCE_TYPE = {"aliyun.oss", "tencent.cos", "huawei.obs", "aws.s3", "baidu.bos", "ucloud.oss", "qingcloud.qingstor", "qiniu.kodo", "vloc.oss"};

}
