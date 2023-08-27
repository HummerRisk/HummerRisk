package com.hummer.common.core.utils;

import cn.ucloud.common.config.Config;
import cn.ucloud.uhost.client.UHostClient;
import cn.ucloud.uhost.models.DescribeUHostInstanceRequest;
import cn.ucloud.uhost.models.DescribeUHostInstanceResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.ecs.model.v20140526.DescribeRegionsRequest;
import com.aliyuncs.ecs.model.v20140526.DescribeRegionsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.FormatType;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bcc.BccClient;
import com.baidubce.services.bcc.BccClientConfiguration;
import com.baidubce.services.bcc.model.instance.ListInstancesResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.cloudresourcemanager.CloudResourceManager;
import com.google.api.services.cloudresourcemanager.model.Project;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.huaweicloud.sdk.iam.v3.IamClient;
import com.huaweicloud.sdk.iam.v3.model.KeystoneListProjectsRequest;
import com.huaweicloud.sdk.iam.v3.model.ProjectResult;
import com.huaweicloud.sdk.iam.v3.model.ShowCredential;
import com.hummer.common.core.constant.*;
import com.hummer.common.core.domain.AccountWithBLOBs;
import com.hummer.common.core.domain.Proxy;
import com.hummer.common.core.exception.HRException;
import com.hummer.common.core.exception.PluginException;
import com.hummer.common.core.proxy.Request;
import com.hummer.common.core.proxy.aliyun.AliyunCredential;
import com.hummer.common.core.proxy.aliyun.AliyunRequest;
import com.hummer.common.core.proxy.aws.AWSCredential;
import com.hummer.common.core.proxy.aws.AWSRequest;
import com.hummer.common.core.proxy.azure.AzureBaseRequest;
import com.hummer.common.core.proxy.azure.AzureClient;
import com.hummer.common.core.proxy.azure.AzureCredential;
import com.hummer.common.core.proxy.baidu.BaiduCredential;
import com.hummer.common.core.proxy.gcp.GcpBaseRequest;
import com.hummer.common.core.proxy.gcp.GcpClient;
import com.hummer.common.core.proxy.gcp.GcpCredential;
import com.hummer.common.core.proxy.huawei.AuthUtil;
import com.hummer.common.core.proxy.huawei.BusiRequest;
import com.hummer.common.core.proxy.huawei.ClientUtil;
import com.hummer.common.core.proxy.huawei.HuaweiCloudCredential;
import com.hummer.common.core.proxy.huoshan.HuoshanCredential;
import com.hummer.common.core.proxy.jdcloud.JDCloudCredential;
import com.hummer.common.core.proxy.jdcloud.JDRequest;
import com.hummer.common.core.proxy.ksyun.KsyunCredential;
import com.hummer.common.core.proxy.ksyun.KsyunRequest;
import com.hummer.common.core.proxy.openstack.OpenStackCredential;
import com.hummer.common.core.proxy.openstack.OpenStackRequest;
import com.hummer.common.core.proxy.openstack.OpenStackUtils;
import com.hummer.common.core.proxy.qingcloud.QingCloudCredential;
import com.hummer.common.core.proxy.qiniu.QiniuCredential;
import com.hummer.common.core.proxy.tencent.QCloudBaseRequest;
import com.hummer.common.core.proxy.ucloud.UCloudCredential;
import com.hummer.common.core.proxy.vsphere.VsphereBaseRequest;
import com.hummer.common.core.proxy.vsphere.VsphereClient;
import com.hummer.common.core.proxy.vsphere.VsphereCredential;
import com.hummer.common.core.proxy.vsphere.VsphereRegion;
import com.jdcloud.sdk.service.iam.model.DescribeGroupsRequest;
import com.jdcloud.sdk.service.iam.model.DescribeGroupsResponse;
import com.qingcloud.sdk.config.EnvContext;
import com.qingcloud.sdk.service.InstanceService;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.util.Auth;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.cvm.v20170312.CvmClient;
import com.tencentcloudapi.cvm.v20170312.models.RegionInfo;
import com.vmware.vim25.mo.Datacenter;
import com.volcengine.model.request.iam.ListUsersRequest;
import com.volcengine.model.response.iam.ListUsersResponse;
import com.volcengine.service.iam.IIamService;
import com.volcengine.service.iam.impl.IamServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.types.ServiceType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author harris
 * @desc 与云平台相关的公共方法统一在此文件
 */
public class PlatformUtils {

    //云平台插件
    public final static String aws = "hummer-aws-plugin";
    public final static String azure = "hummer-azure-plugin";
    public final static String aliyun = "hummer-aliyun-plugin";
    public final static String huawei = "hummer-huawei-plugin";
    public final static String tencent = "hummer-qcloud-plugin";
    public final static String vsphere = "hummer-vsphere-plugin";
    public final static String openstack = "hummer-openstack-plugin";
    public final static String gcp = "hummer-gcp-plugin";
    public final static String huoshan = "hummer-huoshan-plugin";
    public final static String baidu = "hummer-baidu-plugin";
    public final static String qiniu = "hummer-qiniu-plugin";
    public final static String qingcloud = "hummer-qingcloud-plugin";
    public final static String ucloud = "hummer-ucloud-plugin";
    public final static String jdcloud = "hummer-jdcloud-plugin";
    public final static String ksyun = "hummer-ksyun-plugin";
    public final static String[] userForbiddenArr = {"The IAM user is forbidden"};
    // 插件类型: 混合云、云原生
    public final static String cloud_ = "cloud";
    public final static String native_ = "native";

    /**
     * 支持的插件
     * 云平台插件: aws, azure, aliyun, huawei, tencent, vsphere, openstack, gcp, huoshan, baidu, qiniu, qingcloud, ucloud
     * 云原生检测插件: k8s, openshift, rancher, kubesphere
     */
    public final static List<String> getPlugin() {
        return Arrays.asList(aws, azure, aliyun, huawei, tencent, vsphere, openstack, gcp, huoshan, baidu, qiniu, qingcloud, ucloud,
                jdcloud, ksyun);
    }

    /**
     * 支持云平台插件
     */
    public final static List<String> getCloudPlugin() {
        return Arrays.asList(aws, azure, aliyun, huawei, tencent, vsphere, openstack, gcp, huoshan, baidu, qiniu, qingcloud, ucloud, jdcloud, ksyun);
    }

    /**
     * 是否支持云平台插件
     */
    public static boolean isSupportCloudAccount(String source) {
        // 云平台插件
        List<String> tempList = Arrays.asList(aws, azure, aliyun, huawei, tencent, vsphere, openstack, gcp, huoshan, baidu, qiniu, qingcloud, ucloud, jdcloud, ksyun);

        // 利用list的包含方法,进行判断
        return tempList.contains(source);
    }


    /**
     * 需要执行的custodian命令
     *
     * @param custodian
     * @param behavior
     * @param dirPath
     * @param fileName
     * @param params
     * @return
     */
    public final static String fixedCommand(String custodian, String behavior, String dirPath, String fileName, Map<String, String> params) throws Exception {
        String type = params.get("type");
        String region = params.get("region");
        String proxyType = params.get("proxyType");
        String proxyIp = params.get("proxyIp");
        String proxyPort = params.get("proxyPort");
        String proxyName = params.get("proxyName");
        String proxyPassword = params.get("proxyPassword");
        String pre = "";
        String _pok = " ";
        String proxy = "";
        if (StringUtils.isNotEmpty(proxyType)) {
            if (StringUtils.equalsIgnoreCase(proxyType, CloudAccountConstants.ProxyType.Http.toString())) {
                if (StringUtils.isNotEmpty(proxyName)) {
                    proxy = "export http_proxy='http://" + proxyIp + ":" + proxyPassword + "@" + proxyIp + ":" + proxyPort + "';" + "\n";
                } else {
                    proxy = "export http_proxy='http://" + proxyIp + ":" + proxyPort + "';" + "\n";
                }
            } else if (StringUtils.equalsIgnoreCase(proxyType, CloudAccountConstants.ProxyType.Https.toString())) {
                if (StringUtils.isNotEmpty(proxyName)) {
                    proxy = "export https_proxy='http://" + proxyIp + ":" + proxyPassword + "@" + proxyIp + ":" + proxyPort + "';" + "\n";
                } else {
                    proxy = "export https_proxy='http://" + proxyIp + ":" + proxyPort + "';" + "\n";
                }
            }
        } else {
            proxy = "unset http_proxy;" + "\n" +
                    "unset https_proxy;" + "\n";
        }

        switch (type) {
            case aws:
                String awsAccessKey = params.get("accessKey");
                String awsSecretKey = params.get("secretKey");
                String sessionToken = params.get("sessionToken");
                if (StringUtils.equalsIgnoreCase(custodian, ScanTypeConstants.prowler.name())) {
                    String defaultConfig = "[default]" + "\n"
                            + "region=" + region + "\n";
                    String defaultCredentials = "[default]" + "\n"
                            + "aws_access_key_id=" + awsAccessKey + "\n"
                            + "aws_secret_access_key=" + awsSecretKey + "\n";
                    CommandUtils.saveAsFile(defaultConfig, CloudTaskConstants.PROWLER_CONFIG_FILE_PATH, "config", false);
                    CommandUtils.saveAsFile(defaultCredentials, CloudTaskConstants.PROWLER_CONFIG_FILE_PATH, "credentials", false);
                    return proxy + "./prowler -c " + (StringUtils.isNotEmpty(fileName) ? fileName : "check11") + " -f " + region + " -s -M text > result.txt";
                }
                pre = "AWS_ACCESS_KEY_ID=" + awsAccessKey + " " +
                       "AWS_SECRET_ACCESS_KEY=" + awsSecretKey + " "
                        +(
                        StringUtils.isEmpty(sessionToken)?""
                                :("AWS_SESSION_TOKEN=" + sessionToken+" ")) +
                        "AWS_DEFAULT_REGION=" + region + " ";
                break;
            case azure:
                String tenant = params.get("tenant");
                String subscriptionId = params.get("subscription");
                String client = params.get("client");
                String key = params.get("key");
                pre = "AZURE_TENANT_ID=" + tenant + " " +
                        "AZURE_SUBSCRIPTION_ID=" + subscriptionId + " " +
                        "AZURE_CLIENT_ID=" + client + " " +
                        "AZURE_CLIENT_SECRET=" + key + " ";
                _pok = " --region=" + region + " ";
                break;
            case aliyun:
                String aliAccessKey = params.get("accessKey");
                String aliSecretKey = params.get("secretKey");
                pre = "ALIYUN_ACCESSKEYID=" + aliAccessKey + " " +
                        "ALIYUN_ACCESSSECRET=" + aliSecretKey + " " +
                        "ALIYUN_DEFAULT_REGION=" + region + " ";
                break;
            case huawei:
                String huaweiAccessKey = params.get("ak");
                String huaweiSecretKey = params.get("sk");
                String projectId = params.get("projectId");
                pre = "HUAWEI_AK=" + huaweiAccessKey + " " +
                        "HUAWEI_SK=" + huaweiSecretKey + " " +
                        "HUAWEI_PROJECT=" + projectId + " " +
                        "HUAWEI_DEFAULT_REGION=" + region + " ";
                break;
            case tencent:
                String qSecretId = params.get("secretId");
                String qSecretKey = params.get("secretKey");
                pre = "TENCENT_SECRETID=" + qSecretId + " " +
                        "TENCENT_SECRETKEY=" + qSecretKey + " " +
                        "TENCENT_DEFAULT_REGION=" + region + " ";
                break;
            case openstack:
                String oEndpoint = params.get("endpoint");
                String oUserName = params.get("userName");
                String oPassword = params.get("password");
                String oProjectId = params.get("projectId");
                String oDomainId = params.get("domainId");
                try {
                    String clouds =
                            "clouds:\n" +
                                    "  demo:\n" +
                                    "    region_name: " + region + "\n" +
                                    "    auth:\n" +
                                    "      username: " + oUserName + "\n" +
                                    "      password: " + oPassword + "\n" +
                                    "      project_id: " + oProjectId + "\n" +
                                    "      domain_name: " + oDomainId + "\n" +
                                    "      auth_url: " + oEndpoint + "\n";
                    CommandUtils.saveAsFile(clouds, dirPath, "clouds.yml", false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case vsphere:
                String vUserName = params.get("vUserName");
                String vPassword = params.get("vPassword");
                String vEndPoint = params.get("vEndPoint");
                pre = "VSPHERE_USERNAME=" + vUserName + " " +
                        "VSPHERE_PASSWORD=" + vPassword + " " +
                        "VSPHERE_ENDPOINT=" + vEndPoint + " " +
                        "VSPHERE_DEFAULT_REGION=" + region + " ";
                break;
            case gcp:
                String credential = params.get("credential");
                try {
                    CommandUtils.commonExecCmdWithResult("export GOOGLE_APPLICATION_CREDENTIALS='" + credential + "'", dirPath);
                    CommandUtils.saveAsFile(credential, dirPath, "google_application_credentials.json", false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                pre = "GOOGLE_CLOUD_PROJECT=" + region + " ";
                break;
            case huoshan:
                String AccessKeyId = params.get("AccessKeyId");
                String SecretAccessKey = params.get("SecretAccessKey");
                pre = "VOLC_ACCESSKEYID=" + AccessKeyId + " " +
                        "VOLC_SECRETACCESSKEY=" + SecretAccessKey + " " +
                        "VOLC_DEFAULT_REGION=" + region + " ";
                break;
            case baidu:
                String baiduAk = params.get("AccessKeyId");
                String baiduSK = params.get("SecretAccessKey");
                String baiduEp = params.get("Endpoint");
                pre = "BAIDU_ACCESSKEYID=" + baiduAk + " " +
                        "BAIDU_SECRETACCESSKEY=" + baiduSK + " " +
                        "BAIDU_ENDPOINT=" + baiduEp + " " +
                        "BAIDU_DEFAULT_REGION=" + region + " ";
                break;
            case qiniu:
                String qiniuAk = params.get("accessKey");
                String qiniuSK = params.get("secretKey");
                String qiniuBucket = params.get("bucket");
                pre = "QINIU_ACCESSKEY=" + qiniuAk + " " +
                        "QINIU_SECRETKEY=" + qiniuSK + " " +
                        "QINIU_BUCKET=" + qiniuBucket + " ";
                break;
            case qingcloud:
                String qingcloudAk = params.get("AccessKeyId");
                String qingcloudSK = params.get("SecretAccessKey");
                pre = "QINGCLOUD_ACCESSKEY=" + qingcloudAk + " " +
                        "QINGCLOUD_SECRETKEY=" + qingcloudSK + " " +
                        "QINGCLOUD_DEFAULT_REGION=" + region + " ";
                break;
            case ucloud:
                String ucloudPublicKey = params.get("UcloudPublicKey");
                String ucloudPrivateKey = params.get("UcloudPrivateKey");
                pre = "UCLOUD_PUBLICKEY=" + ucloudPublicKey + " " +
                        "UCLOUD_PRIVATEKEY=" + ucloudPrivateKey + " " +
                        "UCLOUD_DEFAULT_REGION=" + region + " ";
                break;
            case jdcloud:
                String accessKey = params.get("AccessKey");
                String secretAccessKey = params.get("SecretAccessKey");
                pre = "JDCLOUD_ACCESSKEY=" + accessKey + " " +
                        "JDCLOUD_SECRETKEY=" + secretAccessKey + " " +
                        "JDCLOUD_DEFAULT_REGION=" + region + " ";
                break;
            case ksyun:
                String ksyunAccessKey = params.get("AccessKey");
                String ksyunSecretAccessKey = params.get("SecretAccessKey");
                pre = "KSYUN_ACCESSKEY=" + ksyunAccessKey + " " +
                        "KSYUN_SECRETKEY=" + ksyunSecretAccessKey + " " +
                        "KSYUN_DEFAULT_REGION=" + region + " ";
                break;
        }
        switch (behavior) {
            case "run":
                return proxy + pre +
                        custodian + " " + behavior + " -s " + dirPath + _pok + dirPath + "/" + fileName;
            case "validate":
                return proxy + pre +
                        custodian + " " + behavior + " " + dirPath + "/" + fileName;
            case "--dryrun":
                return proxy + pre +
                        custodian + " " + CommandEnum.run.getCommand() + " " + behavior + " -s " + dirPath + " -c " + dirPath + "/" + fileName;
            case "report":
                return proxy + pre +
                        custodian + " " + behavior + " -s " + dirPath + "/" + fileName;
            default:
                throw new IllegalStateException("Unexpected value: " + behavior);
        }
    }

    /**
     * scanner
     *
     * @param params
     * @return
     */
    public final static JSONObject fixedScanner(String policy, Map<String, String> params, String plugin) throws Exception {
        String type = params.get("type");
        String region = params.get("region");
        String proxyType = params.get("proxyType");
        String proxyIp = params.get("proxyIp");
        String proxyPort = params.get("proxyPort");
        String proxyName = params.get("proxyName");
        String proxyPassword = params.get("proxyPassword");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("policies", policy);
        jsonObject.put("plugin", com.hummer.common.core.utils.StringUtils.isEmpty(plugin)?params.get("plugin"):plugin);
        if (StringUtils.isNotEmpty(proxyType)) {
            if (StringUtils.equalsIgnoreCase(proxyType, CloudAccountConstants.ProxyType.Http.toString())) {
                if (StringUtils.isNotEmpty(proxyName)) {
                    jsonObject.put("http_proxy", true);
                    jsonObject.put("proxyIp", proxyIp);
                    jsonObject.put("proxyPassword", proxyPassword);
                    jsonObject.put("proxyPort", proxyPort);
                } else {
                    jsonObject.put("http_proxy", true);
                    jsonObject.put("proxyIp", proxyIp);
                    jsonObject.put("http_proxy", "http://" + proxyIp + ":" + proxyPort);
                }
            } else if (StringUtils.equalsIgnoreCase(proxyType, CloudAccountConstants.ProxyType.Https.toString())) {
                if (StringUtils.isNotEmpty(proxyName)) {
                    jsonObject.put("https_proxy", true);
                    jsonObject.put("proxyIp", proxyIp);
                    jsonObject.put("proxyPassword", proxyPassword);
                    jsonObject.put("proxyPort", proxyPort);
                } else {
                    jsonObject.put("https_proxy", true);
                    jsonObject.put("proxyIp", proxyIp);
                    jsonObject.put("proxyPort", proxyPort);
                }
            }
        } else {
            jsonObject.put("http_proxy", false);
            jsonObject.put("https_proxy", false);
        }


        switch (type) {
            case aws:
                String awsAccessKey = params.get("accessKey");
                String awsSecretKey = params.get("secretKey");
                String sessionToken = params.get("sessionToken");
                jsonObject.put("AWS_ACCESS_KEY_ID", awsAccessKey);
                jsonObject.put("AWS_SECRET_ACCESS_KEY", awsSecretKey);
                if(StringUtils.isEmpty(sessionToken)) jsonObject.put("AWS_SESSION_TOKEN", sessionToken);
                jsonObject.put("region", region);
                break;
            case azure:
                String tenant = params.get("tenant");
                String subscriptionId = params.get("subscription");
                String client = params.get("client");
                String key = params.get("key");
                jsonObject.put("AZURE_TENANT_ID", tenant);
                jsonObject.put("AZURE_SUBSCRIPTION_ID", subscriptionId);
                jsonObject.put("AZURE_CLIENT_ID", client);
                jsonObject.put("AZURE_CLIENT_SECRET", key);
                jsonObject.put("region", region);
                break;
            case aliyun:
                String aliAccessKey = params.get("accessKey");
                String aliSecretKey = params.get("secretKey");
                jsonObject.put("ALIYUN_ACCESSKEYID", aliAccessKey);
                jsonObject.put("ALIYUN_ACCESSSECRET", aliSecretKey);
                jsonObject.put("region", region);
                break;
            case huawei:
                String huaweiAccessKey = params.get("ak");
                String huaweiSecretKey = params.get("sk");
                String projectId = params.get("projectId");
                jsonObject.put("HUAWEI_AK", huaweiAccessKey);
                jsonObject.put("HUAWEI_SK", huaweiSecretKey);
                jsonObject.put("HUAWEI_PROJECT", projectId);
                jsonObject.put("region", region);
                break;
            case tencent:
                String qSecretId = params.get("secretId");
                String qSecretKey = params.get("secretKey");
                jsonObject.put("TENCENT_SECRETID", qSecretId);
                jsonObject.put("TENCENT_SECRETKEY", qSecretKey);
                jsonObject.put("region", region);
                break;
            case openstack:
                String oEndpoint = params.get("endpoint");
                String oUserName = params.get("userName");
                String oPassword = params.get("password");
                String oProjectId = params.get("projectId");
                String oDomainId = params.get("domainId");
                jsonObject.put("username", oUserName);
                jsonObject.put("password", oPassword);
                jsonObject.put("project_id", oProjectId);
                jsonObject.put("domain_name", oDomainId);
                jsonObject.put("auth_url", oEndpoint);
                jsonObject.put("region", region);
                break;
            case vsphere:
                String vUserName = params.get("vUserName");
                String vPassword = params.get("vPassword");
                String vEndPoint = params.get("vEndPoint");
                jsonObject.put("VSPHERE_USERNAME", vUserName);
                jsonObject.put("VSPHERE_PASSWORD", vPassword);
                jsonObject.put("VSPHERE_ENDPOINT", vEndPoint);
                jsonObject.put("region", region);
                break;
            case gcp:
                String credential = params.get("credential");
                jsonObject.put("GOOGLE_APPLICATION_CREDENTIALS", credential);
                jsonObject.put("region", region);
                break;
            case huoshan:
                String AccessKeyId = params.get("AccessKeyId");
                String SecretAccessKey = params.get("SecretAccessKey");
                jsonObject.put("VOLC_ACCESSKEYID", AccessKeyId);
                jsonObject.put("VOLC_SECRETACCESSKEY", SecretAccessKey);
                jsonObject.put("region", region);
                break;
            case baidu:
                String baiduAk = params.get("AccessKeyId");
                String baiduSK = params.get("SecretAccessKey");
                String baiduEp = params.get("Endpoint");
                jsonObject.put("BAIDU_ACCESSKEYID", baiduAk);
                jsonObject.put("BAIDU_SECRETACCESSKEY", baiduSK);
                jsonObject.put("BAIDU_ENDPOINT", baiduEp);
                jsonObject.put("region", region);
                break;
            case qiniu:
                String qiniuAk = params.get("accessKey");
                String qiniuSK = params.get("secretKey");
                String qiniuBucket = params.get("bucket");
                jsonObject.put("QINIU_ACCESSKEY", qiniuAk);
                jsonObject.put("QINIU_SECRETKEY", qiniuSK);
                jsonObject.put("QINIU_BUCKET", qiniuBucket);
                break;
            case qingcloud:
                String qingcloudAk = params.get("AccessKeyId");
                String qingcloudSK = params.get("SecretAccessKey");
                jsonObject.put("QINGCLOUD_ACCESSKEY", qingcloudAk);
                jsonObject.put("QINGCLOUD_SECRETKEY", qingcloudSK);
                jsonObject.put("region", region);
                break;
            case ucloud:
                String ucloudPublicKey = params.get("UcloudPublicKey");
                String ucloudPrivateKey = params.get("UcloudPrivateKey");
                jsonObject.put("UCLOUD_PUBLICKEY", ucloudPublicKey);
                jsonObject.put("UCLOUD_PRIVATEKEY", ucloudPrivateKey);
                jsonObject.put("region", region);
                break;
            case jdcloud:
                String accessKey = params.get("AccessKey");
                String secretAccessKey = params.get("SecretAccessKey");
                jsonObject.put("JDCLOUD_ACCESSKEY", accessKey);
                jsonObject.put("JDCLOUD_SECRETKEY", secretAccessKey);
                jsonObject.put("region", region);
                break;
            case ksyun:
                String ksyunAccessKey = params.get("AccessKey");
                String ksyunSecretAccessKey = params.get("SecretAccessKey");
                jsonObject.put("KSYUN_ACCESSKEY", ksyunAccessKey);
                jsonObject.put("KSYUN_SECRETKEY", ksyunSecretAccessKey);
                jsonObject.put("region", region);
                break;
        }

        return jsonObject;

    }


    /**
     * 获取云平台相关参数
     *
     * @param account
     * @param region
     * @return
     */
    public final static Map<String, String> getAccount(AccountWithBLOBs account, String region, Proxy proxy) {
        Map<String, String> map = new HashMap<>();
        map.put("plugin", account.getPluginId());
        switch (account.getPluginId()) {
            case aws:
                map.put("type", aws);
                AWSCredential awsCredential = new Gson().fromJson(account.getCredential(), AWSCredential.class);
                map.put("accessKey", awsCredential.getAccessKey());
                map.put("secretKey", awsCredential.getSecretKey());
                map.put("sessionToken",awsCredential.getAwsSessionToken());
                map.put("region", region);
                break;
            case azure:
                map.put("type", azure);
                AzureCredential azureCredential = new Gson().fromJson(account.getCredential(), AzureCredential.class);
                map.put("client", azureCredential.getClient());
                map.put("key", azureCredential.getKey());
                map.put("subscription", azureCredential.getSubscription());
                map.put("tenant", azureCredential.getTenant());
                map.put("region", region);
                break;
            case aliyun:
                map.put("type", aliyun);
                AliyunCredential aliyunCredential = new Gson().fromJson(account.getCredential(), AliyunCredential.class);
                map.put("accessKey", aliyunCredential.getAccessKey());
                map.put("secretKey", aliyunCredential.getSecretKey());
                map.put("region", region);
                break;
            case huawei:
                map.put("type", huawei);
                HuaweiCloudCredential huaweiCloudCredential = new Gson().fromJson(account.getCredential(), HuaweiCloudCredential.class);
                map.put("ak", huaweiCloudCredential.getAk());
                map.put("sk", huaweiCloudCredential.getSk());
                JSONArray jsonArray = JSON.parseArray(account.getRegions());
                List arr = jsonArray.stream().filter(str -> StringUtils.equals(region, JSON.parseObject(str.toString()).get("regionId").toString())).collect(Collectors.toList());
                map.put("projectId", JSON.parseObject(arr.get(0).toString()).get("projectId").toString());
                map.put("region", region);
                break;
            case tencent:
                map.put("type", tencent);
                Credential tencentCredential = new Gson().fromJson(account.getCredential(), Credential.class);
                map.put("secretId", tencentCredential.getSecretId());
                map.put("secretKey", tencentCredential.getSecretKey());
                map.put("region", region);
                break;
            case openstack:
                map.put("type", openstack);
                OpenStackCredential openStackCredential = new Gson().fromJson(account.getCredential(), OpenStackCredential.class);
                map.put("endpoint", openStackCredential.getEndpoint());
                map.put("userName", openStackCredential.getUserName());
                map.put("password", openStackCredential.getPassword());
                map.put("projectId", openStackCredential.getProjectId());
                map.put("domainId", openStackCredential.getDomainId());
                map.put("region", region);
                break;
            case vsphere:
                map.put("type", vsphere);
                VsphereCredential vsphereCredential = new Gson().fromJson(account.getCredential(), VsphereCredential.class);
                map.put("vUserName", vsphereCredential.getvUserName());
                map.put("vPassword", vsphereCredential.getvPassword());
                map.put("vEndPoint", vsphereCredential.getvEndPoint());
                map.put("region", region);
                break;
            case gcp:
                map.put("type", gcp);
                GcpCredential gcpCredential = new Gson().fromJson(account.getCredential(), GcpCredential.class);
                map.put("credential", gcpCredential.getCredentials());
                map.put("region", region);
                break;
            case huoshan:
                map.put("type", huoshan);
                HuoshanCredential huoshanCredential = new Gson().fromJson(account.getCredential(), HuoshanCredential.class);
                map.put("AccessKeyId", huoshanCredential.getAccessKeyId());
                map.put("SecretAccessKey", huoshanCredential.getSecretAccessKey());
                map.put("region", region);
                break;
            case baidu:
                map.put("type", baidu);
                BaiduCredential baiduCredential = new Gson().fromJson(account.getCredential(), BaiduCredential.class);
                map.put("AccessKeyId", baiduCredential.getAccessKeyId());
                map.put("SecretAccessKey", baiduCredential.getSecretAccessKey());
                map.put("Endpoint", baiduCredential.getEndpoint());
                map.put("region", region);
                break;
            case qiniu:
                map.put("type", qiniu);
                QiniuCredential qiniuCredential = new Gson().fromJson(account.getCredential(), QiniuCredential.class);
                map.put("accessKey", qiniuCredential.getAccessKey());
                map.put("secretKey", qiniuCredential.getSecretKey());
                map.put("bucket", qiniuCredential.getBucket());
                map.put("region", region);
                break;
            case qingcloud:
                map.put("type", qingcloud);
                QingCloudCredential qingCloudCredential = new Gson().fromJson(account.getCredential(), QingCloudCredential.class);
                map.put("AccessKeyId", qingCloudCredential.getAccessKeyId());
                map.put("SecretAccessKey", qingCloudCredential.getSecretAccessKey());
                map.put("region", region);
                break;
            case ucloud:
                map.put("type", ucloud);
                UCloudCredential uCloudCredential = new Gson().fromJson(account.getCredential(), UCloudCredential.class);
                map.put("UcloudPublicKey", uCloudCredential.getUcloudPublicKey());
                map.put("UcloudPrivateKey", uCloudCredential.getUcloudPrivateKey());
                map.put("region", region);
                break;
            case jdcloud:
                map.put("type", jdcloud);
                JDCloudCredential jdCloudCredential = new Gson().fromJson(account.getCredential(), JDCloudCredential.class);
                map.put("AccessKey", jdCloudCredential.getAccessKey());
                map.put("SecretAccessKey", jdCloudCredential.getSecretAccessKey());
                map.put("region", region);
                break;
            case ksyun:
                map.put("type", ksyun);
                KsyunCredential ksyunCredential = new Gson().fromJson(account.getCredential(), KsyunCredential.class);
                map.put("AccessKey", ksyunCredential.getAccessKey());
                map.put("SecretAccessKey", ksyunCredential.getSecretAccessKey());
                map.put("region", region);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + account.getPluginId());
        }
        map.put("proxyType", proxy != null ? proxy.getProxyType() : "");
        map.put("proxyIp", proxy != null ? proxy.getProxyIp() : "");
        map.put("proxyPort", proxy != null ? proxy.getProxyPort() : "");
        map.put("proxyName", proxy != null ? proxy.getProxyName() : "");
        map.put("proxyPassword", proxy != null ? proxy.getProxyPassword() : "");
        return map;
    }

    /**
     * 获取云平台（云账号）的regions
     *
     * @param account
     * @param flag
     * @return
     * @throws ClientException
     */
    public static final JSONArray _getRegions(AccountWithBLOBs account, Proxy proxy, boolean flag) {
        try {
            JSONArray jsonArray = new JSONArray();
            if (!flag) {
                return new JSONArray();
            }
            switch (account.getPluginId()) {
                case aws:
                    AWSRequest awsReq = new AWSRequest();
                    awsReq.setCredential(account.getCredential());
                    AmazonEC2Client client = awsReq.getAmazonEC2Client(proxy);
                    DescribeRegionsResult result;
                    try {
                        client.setRegion(RegionUtils.getRegion("cn-north-1"));
                        result = client.describeRegions();
                    } catch (Exception e) {
                        client.setRegion(RegionUtils.getRegion("ap-northeast-1"));
                        result = client.describeRegions();
                    }
                    result.getRegions().forEach(region -> {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("regionId", region.getRegionName());
                        jsonObject.put("regionName", StringUtils.isNotEmpty(tranforRegionId2RegionName(region.getRegionName(), aws)) ? tranforRegionId2RegionName(region.getRegionName(), aws) : region.getRegionName());
                        if (!jsonArray.contains(jsonObject)) jsonArray.add(jsonObject);
                    });
                    break;
                case azure:
                    try {
                        AzureBaseRequest req = new AzureBaseRequest();
                        req.setCredential(account.getCredential());
                        AzureClient azureClient = req.getAzureClient(proxy);
                        //此region 不是chinanorth这种我们诠释的region，而是azure本身的中国区、国际区等概念
                        azureClient.getCloudRegions().forEach(region -> {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("regionId", region.get("regionId"));
                            jsonObject.put("regionName", StringUtils.isNotEmpty(tranforRegionId2RegionName(region.get("regionId"), azure)) ? tranforRegionId2RegionName(region.get("regionId"), azure) : region.get("regionName"));
                            if (!jsonArray.contains(jsonObject)) jsonArray.add(jsonObject);
                        });
                    } catch (Exception e) {
                        LogUtil.error(e.getMessage());
                    }
                    break;
                case aliyun:
                    AliyunRequest req = new AliyunRequest();
                    req.setCredential(account.getCredential());
                    IAcsClient aliyunClient = req.getAliyunClient(proxy);
                    DescribeRegionsRequest describeRegionsRequest = new DescribeRegionsRequest();
                    describeRegionsRequest.setAcceptFormat(FormatType.JSON);
                    try {
                        DescribeRegionsResponse describeRegionsResponse = aliyunClient.getAcsResponse(describeRegionsRequest);
                        List<DescribeRegionsResponse.Region> regions = describeRegionsResponse.getRegions();
                        regions.forEach(region -> {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("regionId", region.getRegionId());
                            jsonObject.put("regionName", StringUtils.isNotEmpty(tranforRegionId2RegionName(region.getRegionId(), aliyun)) ? tranforRegionId2RegionName(region.getRegionId(), aliyun) : region.getLocalName());
                            if (!jsonArray.contains(jsonObject)) jsonArray.add(jsonObject);
                        });
                    } catch (Exception e) {
                        LogUtil.error(e.getMessage());
                    } finally {
                        aliyunClient.shutdown();
                    }
                    break;
                case huawei:
                    try {
                        BusiRequest busiRequest = new BusiRequest();
                        busiRequest.setCredential(account.getCredential());
                        List<Map<String, String>> regions = ClientUtil.getRegions(busiRequest, proxy);
                        IamClient iamClient = ClientUtil.getIamClient(busiRequest, proxy);
                        KeystoneListProjectsRequest request = new KeystoneListProjectsRequest();
                        request.setDomainId(busiRequest.getHuaweiCloudCredential().getDomainId());
                        List<ProjectResult> projects = iamClient.keystoneListProjects(request).getProjects();
                        for (Map<String, String> region : regions) {
                            for (ProjectResult project : projects) {
                                if (project.getName().equalsIgnoreCase(region.get("key"))) {
                                    region.put("projectId", project.getId());
                                }
                            }
                        }
                        for (Map<String, String> region : regions) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("regionId", region.get("key"));
                            jsonObject.put("regionName", StringUtils.isNotEmpty(tranforRegionId2RegionName(region.get("key"), huawei)) ? tranforRegionId2RegionName(region.get("key"), huawei) : region.get("value"));
                            jsonObject.put("projectId", region.get("projectId"));
                            if (!jsonArray.contains(jsonObject)) jsonArray.add(jsonObject);
                        }
                    } catch (HRException | PluginException e) {
                        HRException.throwException(e.getMessage());
                    }
                    break;
                case tencent:
                    try {
                        QCloudBaseRequest request = new QCloudBaseRequest();
                        request.setCredential(account.getCredential());
                        CvmClient cvmClient = request.getCvmClient(proxy);
                        com.tencentcloudapi.cvm.v20170312.models.DescribeRegionsRequest regionsRequest = new com.tencentcloudapi.cvm.v20170312.models.DescribeRegionsRequest();
                        com.tencentcloudapi.cvm.v20170312.models.DescribeRegionsResponse resp = cvmClient.DescribeRegions(regionsRequest);
                        RegionInfo[] regionInfos = resp.getRegionSet();
                        for (RegionInfo regionInfo : regionInfos) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("regionId", regionInfo.getRegion());
                            jsonObject.put("regionName", StringUtils.isNotEmpty(tranforRegionId2RegionName(regionInfo.getRegion(), tencent)) ? tranforRegionId2RegionName(regionInfo.getRegion(), tencent) : regionInfo.getRegionName());
                            if (!jsonArray.contains(jsonObject)) jsonArray.add(jsonObject);
                        }
                    } catch (Exception e) {
                        LogUtil.error(e.getMessage());
                    }
                    break;
                case openstack:
                    try {
                        Request openStackReq = new Request();
                        openStackReq.setCredential(account.getCredential());
                        OpenStackRequest openstackRequest = OpenStackUtils.convert2OpenStackRequest(openStackReq);
                        OSClient.OSClientV3 osClient = openstackRequest.getOpenStackClient();
                        List<? extends org.openstack4j.model.identity.v3.Region> regions = osClient.identity().regions().list();

                        if (OpenStackUtils.isAdmin(osClient)) {
                            if (!CollectionUtils.isEmpty(regions)) {
                                if (OpenStackUtils.isSupport(osClient, ServiceType.BLOCK_STORAGE)) {
                                    regions.forEach(region -> {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("regionId", region.getId());
                                        jsonObject.put("regionName", region.getId());
                                        if (!jsonArray.contains(jsonObject)) jsonArray.add(jsonObject);
                                    });
                                } else {
                                    regions.forEach(region -> {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("regionId", region.getId());
                                        jsonObject.put("regionName", region.getId());
                                        if (!jsonArray.contains(jsonObject)) jsonArray.add(jsonObject);
                                    });
                                }
                            } else {
                                String region = OpenStackUtils.getRegion(osClient);
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("regionId", region);
                                jsonObject.put("regionName", region);
                                if (!jsonArray.contains(jsonObject)) jsonArray.add(jsonObject);
                            }
                        }
                    } catch (Exception e) {
                        throw new PluginException(e.getMessage(), e);
                    }
                    break;
                case vsphere:
                    VsphereClient vsphereClient = null;
                    try {
                        Request vsphereRequest = new Request();
                        vsphereRequest.setCredential(account.getCredential());
                        VsphereBaseRequest vsphereBaseRequest = new VsphereBaseRequest(vsphereRequest);
                        vsphereClient = vsphereBaseRequest.getVsphereClient();
                        List<Datacenter> list = vsphereClient.listDataCenters();
                        List<VsphereRegion> regions = new ArrayList<>();
                        for (Datacenter dc : list) {
                            regions.add(new VsphereRegion(dc.getName()));
                        }
                        for (VsphereRegion vsphereRegion : regions) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("regionId", vsphereRegion.getName());
                            jsonObject.put("regionName", vsphereRegion.getName());
                            if (!jsonArray.contains(jsonObject)) jsonArray.add(jsonObject);
                        }
                    } catch (Exception e) {
                        if (e instanceof PluginException) {
                            throw (PluginException) e;
                        }
                        throw new PluginException(e.getMessage(), e);
                    } finally {
                        if (vsphereClient != null) {
                            vsphereClient.closeConnection();
                        }
                    }
                    break;
                case gcp:
                    try {
                        Request gcpRequest = new Request();
                        gcpRequest.setCredential(account.getCredential());
                        GcpBaseRequest gcpBaseRequest = new GcpBaseRequest(gcpRequest);
                        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
                        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

                        InputStream inputStream = new ByteArrayInputStream(gcpBaseRequest.getGcpCredential().getCredentials().getBytes(Charset.forName("UTF-8")));
                        GoogleCredential credential = GoogleCredential.fromStream(inputStream)
                                .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
                        CloudResourceManager cloudResourceManagerService = new CloudResourceManager.Builder(httpTransport, jsonFactory, credential)
                                .setApplicationName("Google-CloudResourceManagerSample/0.1")
                                .build();
                        JSONObject gcp = JSON.parseObject(gcpBaseRequest.getGcpCredential().getCredentials());
                        String projectId = gcp.getString("quota_project_id");
                        if (projectId == null) projectId = gcp.getString("project_id");
                        CloudResourceManager.Projects.Get request =
                                cloudResourceManagerService.projects().get(projectId);
                        Project response = request.execute();
                        if (response == null) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("regionId", projectId);
                            jsonObject.put("regionName", projectId);
                            if (!jsonArray.contains(jsonObject)) jsonArray.add(jsonObject);
                            break;
                        }
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("regionId", projectId);
                        jsonObject.put("regionName", response.getName());
                        if (!jsonArray.contains(jsonObject)) jsonArray.add(jsonObject);
                    } catch (Exception e) {
                        Request gcpRequest = new Request();
                        gcpRequest.setCredential(account.getCredential());
                        GcpBaseRequest gcpBaseRequest = new GcpBaseRequest(gcpRequest);
                        JSONObject gcp = JSON.parseObject(gcpBaseRequest.getGcpCredential().getCredentials());
                        String projectId = gcp.getString("quota_project_id");
                        if (projectId == null) projectId = gcp.getString("project_id");
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("regionId", projectId);
                        jsonObject.put("regionName", projectId);
                        if (!jsonArray.contains(jsonObject)) jsonArray.add(jsonObject);
                    }
                    break;
                case huoshan:
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("regionId", "cn-beijing");
                        jsonObject.put("regionName", "北京");
                        JSONObject jsonObject2 = new JSONObject();
                        jsonObject2.put("regionId", "cn-nantong");
                        jsonObject2.put("regionName", "南通");
                        JSONObject jsonObject3 = new JSONObject();
                        jsonObject3.put("regionId", "cn-shanghai");
                        jsonObject3.put("regionName", "上海");
                        JSONObject jsonObject4 = new JSONObject();
                        jsonObject4.put("regionId", "cn-guangzhou");
                        jsonObject4.put("regionName", "广州");
                        if (!jsonArray.contains(jsonObject)) jsonArray.add(jsonObject);
                        if (!jsonArray.contains(jsonObject2)) jsonArray.add(jsonObject2);
                        if (!jsonArray.contains(jsonObject3)) jsonArray.add(jsonObject3);
                        if (!jsonArray.contains(jsonObject4)) jsonArray.add(jsonObject4);
//                        HuoshanCredential huoshanCredential = new Gson().fromJson(account.getCredential(), HuoshanCredential.class);

//                        CDNService service = CDNServiceImpl.getInstance();
//                        service.setAccessKey(huoshanCredential.getAccessKeyId());
//                        service.setSecretKey(huoshanCredential.getSecretAccessKey());
//                        CDN.DescribeCdnRegionAndIspRequest describeCdnRegionAndIspRequest = new CDN.DescribeCdnRegionAndIspRequest()
//                                .setArea("China");
//
//                        CDN.DescribeCdnRegionAndIspResponse resp = service.describeCdnRegionAndIsp(describeCdnRegionAndIspRequest);
//                        JSONArray jsons = JSON.parseArray(JSON.toJSONString(resp.getResult().getRegions()));
//
//                        for (Object obj : jsons) {
//                            JSONObject jsonObject = new JSONObject();
//                            jsonObject.put("regionId", JSONObject.parseObject(obj.toString()).getString("Code"));
//                            jsonObject.put("regionName", JSONObject.parseObject(obj.toString()).getString("Name"));
//                            if (!jsonArray.contains(jsonObject)) jsonArray.add(jsonObject);
//                        }
                    } catch (Exception e) {
                        LogUtil.error(e.getMessage());
                    }

                    break;
                case baidu:
                    for (Map.Entry<String, String> entry : RegionsConstants.BaiduMap.entrySet()) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("regionId", entry.getKey());
                        jsonObject.put("regionName", entry.getValue());
                        if (!jsonArray.contains(jsonObject)) jsonArray.add(jsonObject);
                    }
                    break;
                case qiniu:
                    for (Map.Entry<String, String> entry : RegionsConstants.QiniuMap.entrySet()) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("regionId", entry.getKey());
                        jsonObject.put("regionName", entry.getValue());
                        if (!jsonArray.contains(jsonObject)) jsonArray.add(jsonObject);
                    }
                    break;
                case qingcloud:
                    for (Map.Entry<String, String> entry : RegionsConstants.QingcloudMap.entrySet()) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("regionId", entry.getKey());
                        jsonObject.put("regionName", entry.getValue());
                        if (!jsonArray.contains(jsonObject)) jsonArray.add(jsonObject);
                    }
                    break;
                case ucloud:
                    for (Map.Entry<String, String> entry : RegionsConstants.UcloudMap.entrySet()) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("regionId", entry.getKey());
                        jsonObject.put("regionName", entry.getValue());
                        if (!jsonArray.contains(jsonObject)) jsonArray.add(jsonObject);
                    }
                    break;
                case jdcloud:
                    for (Map.Entry<String, String> entry : RegionsConstants.JdcloudMap.entrySet()) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("regionId", entry.getKey());
                        jsonObject.put("regionName", entry.getValue());
                        if (!jsonArray.contains(jsonObject)) jsonArray.add(jsonObject);
                    }
                    break;
                case ksyun:
                    for (Map.Entry<String, String> entry : RegionsConstants.KsyunMap.entrySet()) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("regionId", entry.getKey());
                        jsonObject.put("regionName", entry.getValue());
                        if (!jsonArray.contains(jsonObject)) jsonArray.add(jsonObject);
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected regions value{}: " + account.getPluginName());
            }
            return jsonArray;
        } catch (Exception e) {
            throw new HRException(e.getMessage());
        }
    }

    public static boolean validateCredential(AccountWithBLOBs account, Proxy proxy) throws Exception {
        switch (account.getPluginId()) {
            case aws:
                try {
                    AWSRequest awsReq = new AWSRequest();
                    awsReq.setCredential(account.getCredential());
                    AmazonEC2Client client = awsReq.getAmazonEC2Client(proxy);
                    String region = null;
                    if (region != null && region.trim().length() > 0) {
                        client.setRegion(RegionUtils.getRegion(region));
                    }
                    try {
                        client.describeRegions();
                    } catch (Exception e) {
                        if (region != null && region.trim().length() > 0) {
                            if (e instanceof AmazonServiceException) {
                                String errCode = ((AmazonServiceException) e).getErrorCode();
                                if ("AuthFailure".equals(errCode)) {
                                    return false;
                                }
                            } else if (e instanceof AmazonClientException) {
                                String errMsg = e.getMessage();
                                if ("Unable to execute HTTP request: Connection refused".equals(errMsg)) {
                                    return false;
                                }
                            }
                        }
                        client.setRegion(RegionUtils.getRegion("cn-north-1"));
                        client.describeRegions();
                    }
                    return true;
                } catch (Exception e) {
                    throw new Exception(String.format("HRException in verifying cloud account has an error, cloud account: [%s], plugin: [%s], error information:%s", account.getName(), account.getPluginName(), e.getMessage()));
                }
            case azure:
                try {
                    AzureBaseRequest req = new AzureBaseRequest();
                    req.setCredential(account.getCredential());
                    AzureClient azureClient = req.getAzureClient(proxy);
                    return azureClient.getCurrentSubscription() != null;
                } catch (Exception e) {
                    throw new Exception(String.format("HRException in verifying cloud account has an error, cloud account: [%s], plugin: [%s], error information:%s", account.getName(), account.getPluginName(), e.getMessage()));
                }
            case aliyun:
                AliyunRequest aliyunRequest = new AliyunRequest();
                aliyunRequest.setCredential(account.getCredential());
                IAcsClient aliyunClient = aliyunRequest.getAliyunClient(proxy);
                try {
                    DescribeRegionsRequest describeRegionsRequest = new DescribeRegionsRequest();
                    describeRegionsRequest.setAcceptFormat(FormatType.JSON);
                    DescribeRegionsResponse describeRegionsResponse = aliyunClient.getAcsResponse(describeRegionsRequest);
                    describeRegionsResponse.getRegions();
                    return true;
                } catch (Exception e) {
                    throw new Exception(String.format("HRException in verifying cloud account has an error, cloud account: [%s], plugin: [%s], error information:%s", account.getName(), account.getPluginName(), e.getMessage()));
                } finally {
                    aliyunClient.shutdown();
                }
            case huawei:
                try {
                    HuaweiCloudCredential huaweiCloudCredential = new Gson().fromJson(account.getCredential(), HuaweiCloudCredential.class);
                    IamClient iamClient = ClientUtil.getIamClient(account.getCredential(), proxy);
                    assert iamClient != null;
                    ShowCredential showCredential = AuthUtil.validate(iamClient, huaweiCloudCredential.getAk());
                    return null != showCredential;
                } catch (Exception e) {
                    throw new Exception(String.format("HRException in verifying cloud account has an error, cloud account: [%s], plugin: [%s], error information:%s", account.getName(), account.getPluginName(), e.getMessage()));
                }
            case tencent:
                com.tencentcloudapi.cvm.v20170312.models.DescribeRegionsRequest request = new com.tencentcloudapi.cvm.v20170312.models.DescribeRegionsRequest();
                QCloudBaseRequest req = new QCloudBaseRequest();
                req.setCredential(account.getCredential());
                CvmClient client = req.getCvmClient(proxy);
                try {
                    client.DescribeRegions(request);
                    return true;
                } catch (Exception e) {
                    throw new Exception(String.format("HRException in verifying cloud account has an error, cloud account: [%s], plugin: [%s], error information:%s", account.getName(), account.getPluginName(), e.getMessage()));
                }
            case openstack:
                try {
                    Request openStackReq = new Request();
                    openStackReq.setCredential(account.getCredential());
                    OpenStackRequest openStackRequest = OpenStackUtils.convert2OpenStackRequest(openStackReq);
                    return openStackRequest.getOpenStackClient() != null;
                } catch (Exception e) {
                    throw new Exception("Failed to valid credential：" + e.getMessage());
                }
            case vsphere:
                VsphereClient vsphereClient = null;
                try {
                    Request vsphereRequest = new Request();
                    vsphereRequest.setCredential(account.getCredential());
                    VsphereBaseRequest vsphereBaseRequest = new VsphereBaseRequest(vsphereRequest);
                    vsphereClient = vsphereBaseRequest.getVsphereClient();
                    if (!vsphereClient.isUseCustomSpec()) {
                        throw new Exception("This version of vCenter is not supported!");
                    }
                    return true;
                } catch (Exception e) {
                    throw new Exception(String.format("HRException in verifying cloud account has an error, cloud account: [%s], plugin: [%s], error information:%s", account.getName(), account.getPluginName(), e.getMessage()));
                } finally {
                    if (vsphereClient != null) {
                        vsphereClient.closeConnection();
                    }
                }
            case gcp:
                GcpClient gcpClient = null;
                try {
                    Request gcpRequest = new Request();
                    gcpRequest.setCredential(account.getCredential());
                    GcpBaseRequest gcpBaseRequest = new GcpBaseRequest(gcpRequest);
                    gcpClient = gcpBaseRequest.getGcpClient();
                    return gcpClient.authExplicit(gcpBaseRequest.getGcpCredential());
                } catch (Exception e) {
                    throw new Exception(String.format("HRException in verifying cloud account has an error, cloud account: [%s], plugin: [%s], error information:%s", account.getName(), account.getPluginName(), e.getMessage()));
                }
            case huoshan:
                IIamService iamService = IamServiceImpl.getInstance();
                HuoshanCredential huoshanCredential = new Gson().fromJson(account.getCredential(), HuoshanCredential.class);

                iamService.setAccessKey(huoshanCredential.getAccessKeyId());
                iamService.setSecretKey(huoshanCredential.getSecretAccessKey());
                try {
                    ListUsersRequest listUsersRequest = new ListUsersRequest();
                    listUsersRequest.setLimit(3);

                    ListUsersResponse listUsersResponse = iamService.listUsers(listUsersRequest);
                    return listUsersResponse.getResult() != null;
                } catch (Exception e) {
                    throw new Exception(String.format("HRException in verifying cloud account has an error, cloud account: [%s], plugin: [%s], error information:%s", account.getName(), account.getPluginName(), e.getMessage()));
                }
            case baidu:
                BaiduCredential baiduCredential = new Gson().fromJson(account.getCredential(), BaiduCredential.class);

                try {
                    BccClientConfiguration config = new BccClientConfiguration();
                    config.setCredentials(new DefaultBceCredentials(baiduCredential.getAccessKeyId(), baiduCredential.getSecretAccessKey()));
                    config.setEndpoint(baiduCredential.getEndpoint());
                    BccClient bccClient = new BccClient(config);
                    ListInstancesResponse listInstancesResponse = bccClient.listInstances();
                    return true;
                } catch (Exception e) {
                    throw new Exception(String.format("HRException in verifying cloud account has an error, cloud account: [%s], plugin: [%s], error information:%s", account.getName(), account.getPluginName(), e.getMessage()));
                }
            case qiniu:
                QiniuCredential qiniuCredential = new Gson().fromJson(account.getCredential(), QiniuCredential.class);

                try {
                    Auth auth = Auth.create(qiniuCredential.getAccessKey(), qiniuCredential.getSecretKey());
                    Configuration cfg = new Configuration(Region.autoRegion());
                    BucketManager bucketManager = new BucketManager(auth, cfg);
                    String[] strings = bucketManager.domainList(qiniuCredential.getBucket());
                    return true;
                } catch (Exception e) {
                    throw new Exception(String.format("HRException in verifying cloud account has an error, cloud account: [%s], plugin: [%s], error information:%s", account.getName(), account.getPluginName(), e.getMessage()));
                }
            case qingcloud:
                QingCloudCredential qingCloudCredential = new Gson().fromJson(account.getCredential(), QingCloudCredential.class);

                try {
                    EnvContext context = new EnvContext(qingCloudCredential.getAccessKeyId(), qingCloudCredential.getSecretAccessKey());
                    context.setProtocol("https");
                    context.setHost("api.qingcloud.com");
                    context.setPort("443");
                    context.setZone("pek3b");
                    context.setApiLang("zh-cn"); // optional, set return message i18n, default to us-en
                    InstanceService service = new InstanceService(context);

                    InstanceService.DescribeInstancesInput input = new InstanceService.DescribeInstancesInput();
                    input.setLimit(1);
                    InstanceService.DescribeInstancesOutput output = service.describeInstances(input);
                    return output.getRetCode() == 0 ;
                } catch (Exception e) {
                    throw new Exception(String.format("HRException in verifying cloud account has an error, cloud account: [%s], plugin: [%s], error information:%s", account.getName(), account.getPluginName(), e.getMessage()));
                }
            case ucloud:
                UCloudCredential uCloudCredential = new Gson().fromJson(account.getCredential(), UCloudCredential.class);

                try {
                    cn.ucloud.common.credential.Credential credential = new cn.ucloud.common.credential.Credential(uCloudCredential.getUcloudPublicKey(),uCloudCredential.getUcloudPrivateKey());
                    Config config = new Config();
                    config.setRegion("cn-bj2");
                    UHostClient uHostClient = new UHostClient(config, credential);
                    DescribeUHostInstanceResponse response = uHostClient.describeUHostInstance(new DescribeUHostInstanceRequest());
                    return response.getRetCode() == 0;
                } catch (Exception e) {
                    throw new Exception(String.format("HRException in verifying cloud account has an error, cloud account: [%s], plugin: [%s], error information:%s", account.getName(), account.getPluginName(), e.getMessage()));
                }
            case jdcloud:
                JDCloudCredential jdCloudCredential = new Gson().fromJson(account.getCredential(), JDCloudCredential.class);
                JDRequest jdRequest = new JDRequest(jdCloudCredential);
                try {
                    DescribeGroupsResponse describeGroupsResponse = jdRequest.getIAMClient().describeGroups(new DescribeGroupsRequest());
                    int statusCode = describeGroupsResponse.getJdcloudHttpResponse().getStatusCode();
                    return statusCode == 200;
                } catch (Exception e) {
                    throw new Exception(String.format("HRException in verifying cloud account has an error, cloud account: [%s], plugin: [%s], error information:%s", account.getName(), account.getPluginName(), e.getMessage()));
                }
            case ksyun:
                KsyunCredential ksyunCredential = new Gson().fromJson(account.getCredential(), KsyunCredential.class);
                KsyunRequest ksyunRequest = new KsyunRequest(ksyunCredential);
                try {
                    client.iam.listusers.v20151101.ListUsersResponse listUsersResponse = ksyunRequest.getListUserClient().doGet("iam.api.ksyun.com", new client.iam.listusers.v20151101.ListUsersRequest());
                    return listUsersResponse.getError() == null;
                } catch (Exception e) {
                    throw new Exception(String.format("HRException in verifying cloud account has an error, cloud account: [%s], plugin: [%s], error information:%s", account.getName(), account.getPluginName(), e.getMessage()));
                }
            default:
                throw new IllegalStateException("Unexpected value: " + account.getPluginId());
        }
    }

    public static String tranforRegionId2RegionName(String strEn, String pluginId) {
        String strCn;
        switch (pluginId) {
            case aws:
                strCn = RegionsConstants.AwsMap.get(strEn);
                break;
            case azure:
                strCn = RegionsConstants.AzureMap.get(strEn);
                break;
            case aliyun:
                strCn = RegionsConstants.AliyunMap.get(strEn);
                break;
            case huawei:
                strCn = RegionsConstants.HuaweiMap.get(strEn);
                break;
            case tencent:
                strCn = RegionsConstants.TencentMap.get(strEn);
                break;
            case vsphere:
                strCn = strEn;
                break;
            case openstack:
                strCn = strEn;
                break;
            case gcp:
                strCn = RegionsConstants.GcpMap.get(strEn);
                break;
            case huoshan:
                strCn = strEn;
                break;
            case baidu:
                strCn = RegionsConstants.BaiduMap.get(strEn);
                break;
            case qiniu:
                strCn = strEn;
                break;
            case qingcloud:
                strCn = RegionsConstants.QingcloudMap.get(strEn);
                break;
            case ucloud:
                strCn = strEn;
                break;
            case jdcloud:
                strCn = RegionsConstants.JdcloudMap.get(strEn);
                break;
            case ksyun:
                strCn = RegionsConstants.KsyunMap.get(strEn);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + pluginId);
        }
        if (StringUtils.isEmpty(strCn)) return strEn;
        return strCn;
    }

    public static boolean checkAvailableRegion(String pluginId, String resource, String region) {
        String[] stringArray;
        List<String> tempList;
        switch (pluginId) {
            case aws:
                break;
            case azure:
                break;
            case aliyun:
                if (StringUtils.contains(resource, "aliyun.polardb")) {
                    // 不支持aliyun.polardb资源的区域
                    stringArray = new String[]{"cn-wulanchabu", "cn-heyuan", "cn-guangzhou", "me-east-1",
                            "cn-nanjing", "ap-northeast-2", "ap-southeast-7", "me-central-1", "cn-fuzhou"};
                    tempList = Arrays.asList(stringArray);
                    return !tempList.contains(region);
                } else if (StringUtils.contains(resource, "aliyun.mongodb")) {
                    // 不支持aliyun.mongodb资源的区域
                    stringArray = new String[]{"cn-guangzhou", "cn-nanjing"};
                    tempList = Arrays.asList(stringArray);
                    return !tempList.contains(region);
                } else if (StringUtils.contains(resource, "aliyun.slb")) {
                    // 不支持aliyun.slb资源的区域
                    stringArray = new String[]{"cn-nanjing", "me-central-1"};
                    tempList = Arrays.asList(stringArray);
                    return !tempList.contains(region);
                } else if (StringUtils.contains(resource, "aliyun.ram")) {
                    // 因为ram是无区域资源，所以aliyun.ram资源仅绑定北京区，以防重复数据过多
                    stringArray = new String[]{"cn-beijing"};
                    tempList = Arrays.asList(stringArray);
                    return tempList.contains(region);
                } else if (StringUtils.contains(resource, "aliyun.cdn")) {
                    // 因为cdn是无区域资源，所以aliyun.cdn资源仅绑定北京区，以防重复数据过多
                    stringArray = new String[]{"cn-beijing"};
                    tempList = Arrays.asList(stringArray);
                    return tempList.contains(region);
                } else if (StringUtils.contains(resource, "aliyun.rds")) {
                    stringArray = new String[]{"cn-fuzhou"};
                    tempList = Arrays.asList(stringArray);
                    return !tempList.contains(region);
                } else if (StringUtils.contains(resource, "aliyun.redis")) {
                    stringArray = new String[]{"cn-fuzhou"};
                    tempList = Arrays.asList(stringArray);
                    return !tempList.contains(region);
                } else if (StringUtils.contains(resource, "aliyun.mse")) {
                    stringArray = new String[]{"cn-fuzhou", "cn-nanjing", "me-east-1"};
                    tempList = Arrays.asList(stringArray);
                    return !tempList.contains(region);
                } else if (StringUtils.contains(resource, "aliyun.nas")) {
                    stringArray = new String[]{"ap-southeast-7", "cn-fuzhou", "cn-nanjing"};
                    tempList = Arrays.asList(stringArray);
                    return !tempList.contains(region);
                }
                break;
            case huawei:
                if (StringUtils.contains(resource, "huawei.vpc")) {
                    stringArray = new String[]{"cn-northeast-1", "la-south-2", "sa-brazil-1", "na-mexico-1"};
                    tempList = Arrays.asList(stringArray);
                    return !tempList.contains(region);
                } else if (StringUtils.contains(resource, "huawei.security-group")) {
                    stringArray = new String[]{"cn-northeast-1", "la-south-2", "sa-brazil-1", "na-mexico-1"};
                    tempList = Arrays.asList(stringArray);
                    return !tempList.contains(region);
                } else if (StringUtils.contains(resource, "huawei.iam")) {
                    // 因为iam是无区域资源，所以huawei.iam资源仅绑定北京区，以防重复数据过多
                    stringArray = new String[]{"cn-north-4"};
                    tempList = Arrays.asList(stringArray);
                    return tempList.contains(region);
                } else if (StringUtils.contains(resource, "huawei.rds")) {
                    stringArray = new String[]{"la-south-2", "sa-brazil-1", "na-mexico-1"};
                    tempList = Arrays.asList(stringArray);
                    return !tempList.contains(region);
                } else if (StringUtils.contains(resource, "huawei.dds")) {
                    stringArray = new String[]{"la-south-2", "sa-brazil-1", "na-mexico-1"};
                    tempList = Arrays.asList(stringArray);
                    return !tempList.contains(region);
                } else if (StringUtils.contains(resource, "huawei.redis")) {
                    stringArray = new String[]{"la-south-2", "sa-brazil-1", "na-mexico-1"};
                    tempList = Arrays.asList(stringArray);
                    return !tempList.contains(region);
                } else if (StringUtils.contains(resource, "huawei.elb")) {
                    stringArray = new String[]{"la-south-2", "sa-brazil-1", "na-mexico-1"};
                    tempList = Arrays.asList(stringArray);
                    return !tempList.contains(region);
                } else if (StringUtils.contains(resource, "huawei.eip")) {
                    stringArray = new String[]{"la-south-2", "sa-brazil-1", "na-mexico-1"};
                    tempList = Arrays.asList(stringArray);
                    return !tempList.contains(region);
                } else if (StringUtils.contains(resource, "huawei.gaussdb")) {
                    stringArray = new String[]{"cn-north-1"};
                    tempList = Arrays.asList(stringArray);
                    return !tempList.contains(region);
                } else if (StringUtils.contains(resource, "huawei.gaussdbfornosql")) {
                    stringArray = new String[]{"cn-north-1"};
                    tempList = Arrays.asList(stringArray);
                    return !tempList.contains(region);
                } else if (StringUtils.contains(resource, "huawei.gaussdbforopengauss")) {
                    stringArray = new String[]{"cn-north-1"};
                    tempList = Arrays.asList(stringArray);
                    return !tempList.contains(region);
                }
                break;
            case tencent:
                if(StringUtils.contains(resource,"tencent.mongodb")){
                    stringArray = new String[]{"ap-jakarta","ap-shanghai-fsi", "ap-shenzhen-fsi"};
                }else{
                    // 不支持资源的区域
                    stringArray = new String[]{"ap-shanghai-fsi", "ap-shenzhen-fsi"};
                    // 利用list的包含方法,进行判断
                }
                tempList = Arrays.asList(stringArray);
                return !tempList.contains(region);
            case vsphere:
                break;
            case openstack:
                break;
            case gcp:
                break;
            case huoshan:
                break;
            case baidu:
                if ("hbfsg".equals(region)) {
                    return false;
                }
                if ("sin".equals(region)) {
                    return false;
                }
                break;
            case qiniu:
                break;
            case qingcloud:
                break;
            case jdcloud:
                break;
            case ksyun:
                break;
            case ucloud:
                if (StringUtils.contains(resource, "ucloud.uhost")) {
                    stringArray = new String[]{"cn-qz"};
                    tempList = Arrays.asList(stringArray);
                    // 利用list的包含方法,进行判断
                    return !tempList.contains(region);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + pluginId);
        }

        return true;
    }

    public static String[] checkoutResourceType(String pluginId) {
        switch (pluginId) {
            case aws:
                return CloudTaskConstants.AWS_RESOURCE_TYPE;
            case azure:
                return CloudTaskConstants.AZURE_RESOURCE_TYPE;
            case aliyun:
                return CloudTaskConstants.ALIYUN_RESOURCE_TYPE;
            case huawei:
                return CloudTaskConstants.HUAWEI_RESOURCE_TYPE;
            case tencent:
                return CloudTaskConstants.TENCENT_RESOURCE_TYPE;
            case vsphere:
                return CloudTaskConstants.VSPHERE_RESOURCE_TYPE;
            case openstack:
                return CloudTaskConstants.OPENSTACK_RESOURCE_TYPE;
            case gcp:
                return CloudTaskConstants.GCP_RESOURCE_TYPE;
            case huoshan:
                return CloudTaskConstants.VOLC_RESOURCE_TYPE;
            case baidu:
                return CloudTaskConstants.BAIDU_RESOURCE_TYPE;
            case qiniu:
                return CloudTaskConstants.QINIU_RESOURCE_TYPE;
            case qingcloud:
                return CloudTaskConstants.QINGCLOUD_RESOURCE_TYPE;
            case ucloud:
                return CloudTaskConstants.UCLOUD_RESOURCE_TYPE;
            case jdcloud:
                return CloudTaskConstants.JDCLOUD_RESOURCE_TYPE;
            case ksyun:
                return CloudTaskConstants.KSYUN_RESOURCE_TYPE;
            default:
                throw new IllegalStateException("Unexpected value: " + pluginId);
        }
    }

    /**
     * 检查返回值里是否包含用户被禁止的关键字
     *
     * @param result
     * @return
     */
    public static boolean isUserForbidden(String result) {
        for (String userForbiddenStr : userForbiddenArr) {
            if (result.contains(userForbiddenStr)) {
                return true;
            }
        }
        return false;
    }

    public static String tranforResourceType(String resourceType) {
        List<String> ecsTypes = Arrays.stream(CloudTaskConstants.ECS_TYPE).filter(item -> StringUtils.equals(item, resourceType)).collect(Collectors.toList());
        if (!ecsTypes.isEmpty()) {
            return "ecs";
        }
        List<String> rdsTypes = Arrays.stream(CloudTaskConstants.RDS_TYPE).filter(item -> StringUtils.equals(item, resourceType)).collect(Collectors.toList());
        if (!rdsTypes.isEmpty()) {
            return "rds";
        }
        List<String> ossTypes = Arrays.stream(CloudTaskConstants.OSS_TYPE).filter(item -> StringUtils.equals(item, resourceType)).collect(Collectors.toList());
        if (!ossTypes.isEmpty()) {
            return "oss";
        }
        List<String> diskTypes = Arrays.stream(CloudTaskConstants.DISK_TYPE).filter(item -> StringUtils.equals(item, resourceType)).collect(Collectors.toList());
        if (!diskTypes.isEmpty()) {
            return "disk";
        }
        List<String> iamTypes = Arrays.stream(CloudTaskConstants.IAM_TYPE).filter(item -> StringUtils.equals(item, resourceType)).collect(Collectors.toList());
        if (!iamTypes.isEmpty()) {
            return "iam";
        }
        List<String> eipTypes = Arrays.stream(CloudTaskConstants.EIP_TYPE).filter(item -> StringUtils.equals(item, resourceType)).collect(Collectors.toList());
        if (!eipTypes.isEmpty()) {
            return "eip";
        }
        List<String> elbTypes = Arrays.stream(CloudTaskConstants.ELB_TYPE).filter(item -> StringUtils.equals(item, resourceType)).collect(Collectors.toList());
        if (!elbTypes.isEmpty()) {
            return "elb";
        }
        List<String> sgTypes = Arrays.stream(CloudTaskConstants.SG_TYPE).filter(item -> StringUtils.equals(item, resourceType)).collect(Collectors.toList());
        if (!sgTypes.isEmpty()) {
            return "sg";
        }
        List<String> vpcTypes = Arrays.stream(CloudTaskConstants.VPC_TYPE).filter(item -> StringUtils.equals(item, resourceType)).collect(Collectors.toList());
        if (!vpcTypes.isEmpty()) {
            return "vpc";
        }
        List<String> redisTypes = Arrays.stream(CloudTaskConstants.REDIS_TYPE).filter(item -> StringUtils.equals(item, resourceType)).collect(Collectors.toList());
        if (!redisTypes.isEmpty()) {
            return "redis";
        }
        List<String> mongodbTypes = Arrays.stream(CloudTaskConstants.MONGODB_TYPE).filter(item -> StringUtils.equals(item, resourceType)).collect(Collectors.toList());
        if (!mongodbTypes.isEmpty()) {
            return "mongodb";
        }
        List<String> postgresqlTypes = Arrays.stream(CloudTaskConstants.POSTGRESQL_TYPE).filter(item -> StringUtils.equals(item, resourceType)).collect(Collectors.toList());
        if (!postgresqlTypes.isEmpty()) {
            return "postgresql";
        }
        List<String> esTypes = Arrays.stream(CloudTaskConstants.ES_TYPE).filter(item -> StringUtils.equals(item, resourceType)).collect(Collectors.toList());
        if (!esTypes.isEmpty()) {
            return "es";
        }
        List<String> iamRoleTypes = Arrays.stream(CloudTaskConstants.IAM_ROLE_TYPE).filter(item -> StringUtils.equals(item, resourceType)).collect(Collectors.toList());
        if (!iamRoleTypes.isEmpty()) {
            return "iam-role";
        }
        List<String> amiTypes = Arrays.stream(CloudTaskConstants.AMI_TYPE).filter(item -> StringUtils.equals(item, resourceType)).collect(Collectors.toList());
        if (!amiTypes.isEmpty()) {
            return "ami";
        }
        List<String> cloudtrailTypes = Arrays.stream(CloudTaskConstants.CLOUDTRAIL_TYPE).filter(item -> StringUtils.equals(item, resourceType)).collect(Collectors.toList());
        if (!cloudtrailTypes.isEmpty()) {
            return "cloudtrail";
        }
        List<String> emrTypes = Arrays.stream(CloudTaskConstants.EMR_TYPE).filter(item -> StringUtils.equals(item, resourceType)).collect(Collectors.toList());
        if (!emrTypes.isEmpty()) {
            return "emr";
        }
        List<String> glacierTypes = Arrays.stream(CloudTaskConstants.GLACIER_TYPE).filter(item -> StringUtils.equals(item, resourceType)).collect(Collectors.toList());
        if (!glacierTypes.isEmpty()) {
            return "glacier";
        }
        List<String> kmsTypes = Arrays.stream(CloudTaskConstants.KMS_TYPE).filter(item -> StringUtils.equals(item, resourceType)).collect(Collectors.toList());
        if (!kmsTypes.isEmpty()) {
            return "kms";
        }
        List<String> lambdaTypes = Arrays.stream(CloudTaskConstants.LAMBDA_TYPE).filter(item -> StringUtils.equals(item, resourceType)).collect(Collectors.toList());
        if (!lambdaTypes.isEmpty()) {
            return "lambda";
        }
        List<String> subnetTypes = Arrays.stream(CloudTaskConstants.SUBNET_TYPE).filter(item -> StringUtils.equals(item, resourceType)).collect(Collectors.toList());
        if (!subnetTypes.isEmpty()) {
            return "subnet";
        }
        List<String> redshiftTypes = Arrays.stream(CloudTaskConstants.REDSHIFT_TYPE).filter(item -> StringUtils.equals(item, resourceType)).collect(Collectors.toList());
        if (!redshiftTypes.isEmpty()) {
            return "redshift";
        }
        return "other";
    }

    public static String tranforResourceType2Name(String resourceType) throws Exception {
        String resourceTypes = ReadFileUtils.readConfigFile("resource-type", "", ".json");
        JSONObject jsonObject = JSONObject.parseObject(resourceTypes);
        String resourceEn = jsonObject.getString(resourceType);
        return !StringUtils.isEmpty(resourceEn) ? resourceEn : resourceType;
    }

}
