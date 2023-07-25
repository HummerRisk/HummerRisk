package com.hummer.k8s.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.cr.model.v20160607.GetRepoListRequest;
import com.aliyuncs.cr.model.v20160607.GetRepoTagsRequest;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Region;
import com.amazonaws.services.ecr.AmazonECR;
import com.amazonaws.services.ecr.AmazonECRClient;
import com.amazonaws.services.ecrpublic.AmazonECRPublic;
import com.amazonaws.services.ecrpublic.AmazonECRPublicClient;
import com.amazonaws.services.ecrpublic.model.DescribeImageTagsRequest;
import com.amazonaws.services.ecrpublic.model.DescribeImageTagsResult;
import com.amazonaws.services.ecrpublic.model.ImageTagDetail;
import com.amazonaws.services.ecrpublic.model.Repository;
import com.hummer.cloud.api.ICloudProviderService;
import com.hummer.common.core.constant.*;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.image.*;
import com.hummer.common.core.dto.*;
import com.hummer.common.core.exception.HRException;
import com.hummer.common.core.i18n.Translator;
import com.hummer.common.core.proxy.aliyun.AliyunCredential;
import com.hummer.common.core.proxy.aws.AWSCredential;
import com.hummer.common.core.proxy.tencent.QCloudCredential;
import com.hummer.common.core.utils.*;
import com.hummer.k8s.mapper.*;
import com.hummer.k8s.mapper.ext.*;
import com.hummer.system.api.IOperationLogService;
import com.hummer.system.api.ISystemProviderService;
import com.hummer.system.api.model.LoginUser;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.tcr.v20190924.TcrClient;
import com.tencentcloudapi.tcr.v20190924.models.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ImageService {

    @Autowired
    private ExtImageRepoMapper extImageRepoMapper;
    @Autowired
    private ImageMapper imageMapper;
    @Autowired
    private ImageRepoMapper imageRepoMapper;
    @Autowired
    private ExtImageMapper extImageMapper;
    @Autowired
    private ExtImageRuleMapper extImageRuleMapper;
    @Autowired
    private ImageRuleMapper imageRuleMapper;
    @Autowired
    private ExtImageResultMapper extImageResultMapper;
    @Autowired
    private ImageResultLogMapper imageResultLogMapper;
    @Autowired
    private CommonThreadPool commonThreadPool;
    @Autowired
    private ImageResultMapper imageResultMapper;
    @Autowired
    private ImageRepoItemMapper imageRepoItemMapper;
    @Autowired
    private ExtImageRepoItemMapper extImageRepoItemMapper;
    @Autowired
    private ImageRepoSyncLogMapper imageRepoSyncLogMapper;
    @Autowired
    private ImageResultItemMapper imageResultItemMapper;
    @Autowired
    private ExtImageResultItemMapper extImageResultItemMapper;
    @Autowired
    private ImageRepoSettingMapper imageRepoSettingMapper;
    @Autowired
    private SbomVersionMapper sbomVersionMapper;
    @Autowired
    private SbomMapper sbomMapper;
    @Autowired
    private ProxyMapper proxyMapper;
    @Autowired
    private PluginMapper pluginMapper;
    @Autowired
    private ImageGroupMapper imageGroupMapper;
    @DubboReference
    private ISystemProviderService systemProviderService;
    @DubboReference
    private IOperationLogService operationLogService;
    @DubboReference
    private ICloudProviderService cloudProviderService;

    public List<ImageRepo> imageRepoList(ImageRepoRequest request) {
        return extImageRepoMapper.imageRepoList(request);
    }

    public List<ImageRepo> allImageRepos() {
        ImageRepoExample example = new ImageRepoExample();
        example.setOrderByClause("update_time desc");
        return imageRepoMapper.selectByExample(example);
    }

    public List<ImageRepoItemDTO> repoItemList(ImageRepoItemRequest request) {
        List<ImageRepoItemDTO> repoItemList = extImageRepoItemMapper.repoItemList(request);
        return repoItemList;
    }

    public ImageRepo addImageRepo(ImageRepo imageRepo, LoginUser loginUser) throws Exception {
        String id = UUIDUtil.newUUID();
        imageRepo.setId(id);
        imageRepo.setCreator(loginUser.getUserId());
        imageRepo.setCreateTime(System.currentTimeMillis());
        imageRepo.setUpdateTime(System.currentTimeMillis());
        if(imageRepo.getIsBindAccount()){
            String accountId = imageRepo.getAccountId();
            AccountWithBLOBs accountWithBLOBs = cloudProviderService.selectAccountWithBLOBs(accountId);
            imageRepo.setCredential(accountWithBLOBs.getCredential());
        }
        boolean result = syncImages(imageRepo, loginUser);
        if (result) {
            imageRepo.setStatus("VALID");
        } else {
            imageRepo.setStatus("INVALID");
        }

        operationLogService.log(loginUser, imageRepo.getId(), imageRepo.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.CREATE, "i18n_create_image_repo");
        imageRepoMapper.insertSelective(imageRepo);

        return imageRepo;
    }

    public ImageRepo addImageRepoByDubbo(ImageRepo imageRepo, AccountWithBLOBs accountWithBLOBs, LoginUser loginUser) throws Exception {
        String id = UUIDUtil.newUUID();
        imageRepo.setId(id);
        imageRepo.setCreator(loginUser.getUserId());
        imageRepo.setCreateTime(System.currentTimeMillis());
        imageRepo.setUpdateTime(System.currentTimeMillis());
        if(imageRepo.getIsBindAccount()){
            imageRepo.setCredential(accountWithBLOBs.getCredential());
        }
        boolean result = syncImages(imageRepo, loginUser);
        if (result) {
            imageRepo.setStatus("VALID");
        } else {
            imageRepo.setStatus("INVALID");
        }

        operationLogService.log(loginUser, imageRepo.getId(), imageRepo.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.CREATE, "i18n_create_image_repo");
        imageRepoMapper.insertSelective(imageRepo);

        return imageRepo;
    }

    /**
     * @return 同步是否成功
     * @throws Exception
     */
    public boolean syncImages(ImageRepo imageRepo, LoginUser loginUser) throws Exception {
        long i = 0;
        ImageRepoSyncLogWithBLOBs imageRepoSyncLog = new ImageRepoSyncLogWithBLOBs();
        try {
            ImageRepoItemExample example = new ImageRepoItemExample();
            example.createCriteria().andRepoIdEqualTo(imageRepo.getId());
            imageRepoItemMapper.deleteByExample(example);
            if (StringUtils.equalsIgnoreCase(imageRepo.getPluginIcon(), "harbor.png")) {
                //* @param path harbor 地址
                // * @param username harbor 用户名
                //* @param password harbor 密码
                String path = imageRepo.getRepo();
                if (path.endsWith("/")) {
                    path = path.substring(0, path.length() - 1);
                }
                Map<String, String> header = new HashMap<>();
                header.put("Accept", CloudNativeConstants.Accept);
                header.put("Authorization", "Basic " + Base64.getUrlEncoder().encodeToString((imageRepo.getUserName() + ":" + imageRepo.getPassword()).getBytes()));
                int version = this.getHarborVersion(path);
                if (version == 1) {
                    i = this.saveHarborV1(path, header, imageRepo);
                } else if (version == 2) {
                    i = this.saveHarborV2(path, header, imageRepo);
                }

            } else if (StringUtils.equalsIgnoreCase(imageRepo.getPluginIcon(), "dockerhub.png")) {
                String loginUrl = "https://hub.docker.com/v2/users/login";
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", imageRepo.getUserName());
                jsonObject.put("password", imageRepo.getPassword());
                HttpHeaders httpHeaders = new HttpHeaders();
                // 设置请求类型
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                // 封装参数和头信息
                ResponseEntity<JSONObject> tokenResult = RestTemplateUtils.postForEntity(loginUrl, jsonObject, httpHeaders, JSONObject.class);
                if (tokenResult == null) {
                    throw new RuntimeException("dockerhub认证失败");
                }
                String token = Objects.requireNonNull(tokenResult.getBody()).getString("token");
                httpHeaders.add("Authorization", "Bearer " + token);
                ResponseEntity<JSONObject> repositoriesResult = RestTemplateUtils.getForEntity("https://hub.docker.com/v2/namespaces/" + imageRepo.getUserName() + "/repositories/", httpHeaders, JSONObject.class);
                List<JSONObject> repositories = repositoriesResult.getBody().getJSONArray("results").toJavaList(JSONObject.class);
                for (JSONObject repository : repositories) {
                    String repositoryName = repository.getString("name");
                    String namespace = repository.getString("namespace");
                    ResponseEntity<JSONObject> tagsResponse = RestTemplateUtils.getForEntity("https://hub.docker.com/v2/namespaces/" + namespace + "/repositories/" + repositoryName + "/tags", httpHeaders, JSONObject.class);
                    List<JSONObject> tags = tagsResponse.getBody().getJSONArray("results").toJavaList(JSONObject.class);
                    for (JSONObject tag : tags) {
                        JSONArray images = tag.getJSONArray("images");
                        String tagStr = tag.getString("name");
                        ImageRepoItem imageRepoItem = new ImageRepoItem();
                        imageRepoItem.setId(UUIDUtil.newUUID());
                        imageRepoItem.setProject(namespace);
                        imageRepoItem.setRepository(repositoryName);
                        imageRepoItem.setTag(tagStr);
                        if (images.size() > 0) {
                            imageRepoItem.setDigest(images.getJSONObject(0).getString("digest"));
                            imageRepoItem.setPushTime(images.getJSONObject(0).getString("last_pushed"));
                            imageRepoItem.setArch(images.getJSONObject(0).getString("architecture"));
                            imageRepoItem.setSize(changeFlowFormat(images.getJSONObject(0).getLong("size")));
                        }
                        imageRepoItem.setRepoId(imageRepo.getId());

                        imageRepoItem.setPath("docker.io" + "/" + namespace + "/" + repositoryName + ":" + tagStr);
                        imageRepoItemMapper.insertSelective(imageRepoItem);
                        i++;
                    }
                }

            } else if (StringUtils.equalsIgnoreCase(imageRepo.getPluginIcon(), "nexus.png")) {
                String url = imageRepo.getRepo();
                if (url.endsWith("/")) {
                    url = url.substring(0, url.length() - 1);
                }
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("Authorization", "Basic " + Base64.getUrlEncoder().encodeToString((imageRepo.getUserName() + ":" + imageRepo.getPassword()).getBytes()));
                ResponseEntity<JSONObject> repositoriesResponse = RestTemplateUtils.getForEntity(url + "/v2/_catalog", httpHeaders, JSONObject.class);
                List<String> repositories = repositoriesResponse.getBody().getJSONArray("repositories").toJavaList(String.class);
                for (String repository : repositories) {
                    ResponseEntity<JSONObject> tagsResponse = RestTemplateUtils.getForEntity(url + "/v2/" + repository + "/tags/list", httpHeaders, JSONObject.class);
                    List<String> tags = tagsResponse.getBody().getJSONArray("tags").toJavaList(String.class);
                    String name = tagsResponse.getBody().getString("name");
                    for (String tag : tags) {
                        String path = url.replaceAll("https://", "").replaceAll("http://", "") + "/" + name + ":" + tag;
                        ImageRepoItem imageRepoItem = new ImageRepoItem();
                        imageRepoItem.setId(UUIDUtil.newUUID());
                        imageRepoItem.setProject(name);
                        imageRepoItem.setRepository(name);
                        imageRepoItem.setTag(tag);
                        imageRepoItem.setRepoId(imageRepo.getId());
                        imageRepoItem.setPath(path);
                        imageRepoItemMapper.insertSelective(imageRepoItem);
                        i++;
                    }
                }
            } else if (StringUtils.equalsIgnoreCase(imageRepo.getPluginIcon(), "aliyun.png")) {
                try {
                    String url = imageRepo.getRepo();
                    if (url.endsWith("/")) {
                        url = url.substring(0, url.length() - 1);
                    }
                    Pattern p=Pattern.compile("(?<=registry.).*?(?=.aliyuncs)");
                    Matcher m=p.matcher( url);
                    String region = "";
                    if(m.find()){
                        region =  m.group();
                    }else{
                        p=Pattern.compile("(?<=registry.).*?(?=.cr.aliyuncs)");
                        m=p.matcher( url);
                        if(m.find()){
                            region =  m.group();
                        }else{
                            throw new RuntimeException("wrong repository address");
                        }
                    }
                    AliyunCredential aliyunCredential ;
                    if(imageRepo.getIsBindAccount()){
                        String accountId = imageRepo.getAccountId();
                        AccountWithBLOBs accountWithBLOBs = cloudProviderService.selectAccountWithBLOBs(accountId);
                        if(accountWithBLOBs == null && StringUtils.isNotBlank(imageRepo.getCredential())){
                            aliyunCredential = JSON.parseObject(imageRepo.getCredential(),AliyunCredential.class);
                        }else {
                            aliyunCredential = JSON.parseObject(accountWithBLOBs.getCredential(),AliyunCredential.class);
                        }
                    }else{
                        aliyunCredential = JSON.parseObject(imageRepo.getCredential(),AliyunCredential.class);
                    }
                    // 设置Client
                    DefaultProfile.addEndpoint(region, region, "cr", "cr."+region+".aliyuncs.com");
                    IClientProfile profile = DefaultProfile.getProfile(region, aliyunCredential.getAccessKey(), aliyunCredential.getSecretKey());
                    DefaultAcsClient client = new DefaultAcsClient(profile);
                    // 构造请求
                    GetRepoListRequest request = new GetRepoListRequest();
                    // 设置参数
                    // 发起请求
                    HttpResponse repoResponse = client.doAction(request);
                    String repoStr = new String(repoResponse.getHttpContent());
                    JSONObject repoObj = JSONObject.parseObject(repoStr);
                    JSONArray repoArr = repoObj.getJSONObject("data").getJSONArray("repos");
                    for(int j = 0;j<repoArr.size();j++){
                        JSONObject repo = repoArr.getJSONObject(j);
                        String repoNameSpace = repo.getString("repoNamespace");
                        String repoName = repo.getString("repoName");
                        GetRepoTagsRequest tagRequest = new GetRepoTagsRequest();
                        tagRequest.setRepoNamespace(repoNameSpace);
                        tagRequest.setRepoName(repoName);
                        HttpResponse tagResponse = client.doAction(tagRequest);
                        JSONObject tagResultObj = JSONObject.parseObject(new String(tagResponse.getHttpContent()));
                        JSONArray tagArr = tagResultObj.getJSONObject("data").getJSONArray("tags");
                        for(int k = 0;k < tagArr.size();k++){
                            JSONObject tag = tagArr.getJSONObject(k);
                            String path = url.replaceAll("https://", "").replaceAll("http://", "")
                                    +"/"+repoNameSpace+"/"+repoName+":"+tag.getString("tag");
                            ImageRepoItem imageRepoItem = new ImageRepoItem();
                            imageRepoItem.setId(UUIDUtil.newUUID());
                            imageRepoItem.setRepository(repoName);
                            imageRepoItem.setTag(tag.getString("tag"));
                            imageRepoItem.setRepoId(imageRepo.getId());
                            imageRepoItem.setDigest(tag.getString("digest"));
                            imageRepoItem.setProject(repoNameSpace);
                            imageRepoItem.setPath(path);
                            imageRepoItemMapper.insertSelective(imageRepoItem);
                        }
                    }
                } catch (ClientException e) {
                    System.out.println("code: " + e.getErrCode());
                    System.out.println("message: " + e.getErrMsg());
                }
            } else if (StringUtils.equalsIgnoreCase(imageRepo.getPluginIcon(), "qcloud.png")){
                //腾讯云同步
                String url = imageRepo.getRepo();
                if (url.endsWith("/")) {
                    url = url.substring(0, url.length() - 1);
                }
                QCloudCredential qCloudCredential;
                if(imageRepo.getIsBindAccount()){
                    String accountId = imageRepo.getAccountId();
                    AccountWithBLOBs accountWithBLOBs = cloudProviderService.selectAccountWithBLOBs(accountId);
                    if(accountWithBLOBs == null && StringUtils.isNotBlank(imageRepo.getCredential())) {
                        qCloudCredential = JSON.parseObject(imageRepo.getCredential(),QCloudCredential.class);
                    }else{
                        qCloudCredential = JSON.parseObject(accountWithBLOBs.getCredential(),QCloudCredential.class);
                    }
                }else{
                    qCloudCredential = JSON.parseObject(imageRepo.getCredential(),QCloudCredential.class);
                }
                Credential cred = new Credential(qCloudCredential.getSecretId(), qCloudCredential.getSecretKey());
                // 实例化一个http选项，可选的，没有特殊需求可以跳过
                HttpProfile httpProfile = new HttpProfile();
                httpProfile.setEndpoint("tcr.tencentcloudapi.com");
                // 实例化一个client选项，可选的，没有特殊需求可以跳过
                ClientProfile clientProfile = new ClientProfile();
                clientProfile.setHttpProfile(httpProfile);
                String[] supportRegions = {"ap-bangkok", "ap-beijing", "ap-chengdu", "ap-chongqing", "ap-guangzhou", "ap-hongkong", "ap-jakarta", "ap-mumbai"
                        , "ap-nanjing", "ap-seoul", "ap-shanghai", "ap-shanghai-fsi", "ap-shenzhen-fsi", "ap-singapore", "ap-tokyo", "eu-frankfurt"
                        , "na-ashburn", "na-siliconvalley", "sa-saopaulo"};
                for (String supportRegion : supportRegions) {
                    // 实例化要请求产品的client对象,clientProfile是可选的
                    TcrClient client = new TcrClient(cred, supportRegion , clientProfile);
                    // 实例化一个请求对象,每个接口都会对应一个request对象
                    DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest();
                    describeInstancesRequest.setLimit(1000L);
                    DescribeInstancesResponse describeInstancesResponse = client.DescribeInstances(describeInstancesRequest);
                    Registry[] registries = describeInstancesResponse.getRegistries();
                    for(Registry registry:registries){
                        DescribeRepositoriesRequest req = new DescribeRepositoriesRequest();
                        // 返回的resp是一个DescribeEventsResponse的实例，与请求对象对应
                        req.setLimit(1000L);
                        req.setRegistryId(registry.getRegistryId());
                        DescribeRepositoriesResponse describeRepositoriesResponse = client.DescribeRepositories(req);
                        TcrRepositoryInfo[] repositoryList = describeRepositoriesResponse.getRepositoryList();
                        for(TcrRepositoryInfo tcrRepositoryInfo:repositoryList){
                            DescribeImagesRequest describeImagesRequest = new DescribeImagesRequest();
                            describeImagesRequest.setLimit(1000L);
                            describeImagesRequest.setRegistryId(registry.getRegistryId());
                            describeImagesRequest.setNamespaceName(tcrRepositoryInfo.getNamespace());
                            describeImagesRequest.setRepositoryName(tcrRepositoryInfo.getName());
                            DescribeImagesResponse describeImagesResponse = client.DescribeImages(describeImagesRequest);
                            TcrImageInfo[] imageInfoList = describeImagesResponse.getImageInfoList();
                            for(TcrImageInfo tcrImageInfo:imageInfoList){
                                String path = url.replaceAll("https://", "").replaceAll("http://", "") + "/" + tcrRepositoryInfo.getName() + ":" + tcrImageInfo.getImageVersion();
                                ImageRepoItem imageRepoItem = new ImageRepoItem();
                                imageRepoItem.setId(UUIDUtil.newUUID());
                                imageRepoItem.setRepository(tcrRepositoryInfo.getName());
                                imageRepoItem.setTag(tcrImageInfo.getImageVersion());
                                imageRepoItem.setRepoId(imageRepo.getId());
                                imageRepoItem.setDigest(tcrImageInfo.getDigest());
                                imageRepoItem.setProject(tcrRepositoryInfo.getNamespace());
                                imageRepoItem.setSize(changeFlowFormat(tcrImageInfo.getSize()));
                                imageRepoItem.setPath(path);
                                imageRepoItemMapper.insertSelective(imageRepoItem);
                            }
                        }
                    }
                }
            }else if(StringUtils.equalsIgnoreCase(imageRepo.getPluginIcon(), "aws.png")){
                AWSCredential awsCredential = null;
                if(imageRepo.getIsBindAccount()){
                    String accountId = imageRepo.getAccountId();
                    AccountWithBLOBs accountWithBLOBs = cloudProviderService.selectAccountWithBLOBs(accountId);
                    if(accountWithBLOBs != null && StringUtils.isBlank(imageRepo.getCredential())){
                        awsCredential = JSON.parseObject(accountWithBLOBs.getCredential(),AWSCredential.class);
                    }
                }else{
                    awsCredential = JSON.parseObject(imageRepo.getCredential(),AWSCredential.class);
                }

                AWSCredentials awsCredentials = new BasicAWSCredentials(awsCredential.getAccessKey(), awsCredential.getSecretKey());
                AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
                AmazonEC2Client client = new AmazonEC2Client(awsCredentials);
                try{
                    syncAwsPublic(imageRepo,awsCredentialsProvider,"us-east-1");
                    client.setRegion(RegionUtils.getRegion("us-east-1"));
                    DescribeRegionsResult regionsResult = client.describeRegions();
                    List<Region> regions = regionsResult.getRegions();
                    regions.forEach(item->{
                        syncAwsPrivate(imageRepo,awsCredentialsProvider,item.getRegionName());
                    });
                }catch (SdkClientException e){
                    //syncAwsPublic(imageRepo,awsCredentialsProvider,"cn-north-1");
                    client.setRegion(RegionUtils.getRegion("cn-north-1"));
                    DescribeRegionsResult regionsResult = client.describeRegions();
                    List<Region> regions = regionsResult.getRegions();
                    regions.forEach(item->{
                        syncAwsPrivate(imageRepo,awsCredentialsProvider,item.getRegionName());
                    });
                }

            }else if (StringUtils.equalsIgnoreCase(imageRepo.getPluginIcon(), "other.png")) {
                return true;
            }
            imageRepoSyncLog.setRepoId(imageRepo.getId());
            imageRepoSyncLog.setCreateTime(System.currentTimeMillis());
            imageRepoSyncLog.setOperator(loginUser.getUserName());
            imageRepoSyncLog.setOperation("i18n_sync_image");
            imageRepoSyncLog.setOutput("i18n_sync_image_success");
            imageRepoSyncLog.setResult(true);
            imageRepoSyncLog.setSum(i);
            imageRepoSyncLogMapper.insertSelective(imageRepoSyncLog);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.error("镜像同步失败",e);
            imageRepoSyncLog.setRepoId(imageRepo.getId());
            imageRepoSyncLog.setCreateTime(System.currentTimeMillis());
            imageRepoSyncLog.setOperator(loginUser.getUserName());
            imageRepoSyncLog.setOperation("i18n_sync_image");
            imageRepoSyncLog.setOutput("i18n_sync_image_error: " + e.getMessage());
            imageRepoSyncLog.setResult(false);
            imageRepoSyncLog.setSum(i);
            imageRepoSyncLogMapper.insertSelective(imageRepoSyncLog);
            return false;
        }
        repoImageToImage(imageRepo, loginUser);//同步镜像到镜像管理
        return true;
    }

    private void syncAwsPrivate(ImageRepo imageRepo,AWSCredentialsProvider awsCredentialsProvider,String region){
        AmazonECR ecr = AmazonECRClient.builder().withRegion(region).withCredentials(awsCredentialsProvider).build();
        com.amazonaws.services.ecr.model.DescribeRepositoriesRequest describeRepositoriesRequest = new com.amazonaws.services.ecr.model.DescribeRepositoriesRequest();
        com.amazonaws.services.ecr.model.DescribeRepositoriesResult describeRepositoriesResult = ecr.describeRepositories(describeRepositoriesRequest);
        List<com.amazonaws.services.ecr.model.Repository> repositories = describeRepositoriesResult.getRepositories();
        AtomicReference<String> repo = new AtomicReference<>();
        repositories.forEach(repository -> {
            String repName = repository.getRepositoryName();
            String registryId = repository.getRegistryId();
            String repUri = repository.getRepositoryUri();
            if(repo.get() == null && repUri.contains("/"))
                repo.set(repUri.substring(0, repUri.indexOf("/")));
            com.amazonaws.services.ecr.model.DescribeImagesRequest describeImagesRequest = new com.amazonaws.services.ecr.model.DescribeImagesRequest();
            describeImagesRequest.setRegistryId(registryId);
            describeImagesRequest.setRepositoryName(repName);
            com.amazonaws.services.ecr.model.DescribeImagesResult describeImagesResult = ecr.describeImages(describeImagesRequest);
            List<com.amazonaws.services.ecr.model.ImageDetail> imageDetails = describeImagesResult.getImageDetails();
            imageDetails.forEach(imageTagDetail -> {
                ImageRepoItem imageRepoItem = new ImageRepoItem();
                imageRepoItem.setId(UUIDUtil.newUUID());
                imageRepoItem.setRepository(repName);
                imageRepoItem.setTag(imageTagDetail.getImageTags().size()>0?imageTagDetail.getImageTags().get(0):"");
                imageRepoItem.setRepoId(imageRepo.getId());
                imageRepoItem.setDigest(imageTagDetail.getImageDigest());
                imageRepoItem.setProject(repName.contains("/")?repName.split("/")[0]:repName);
                imageRepoItem.setSize(changeFlowFormat(imageTagDetail.getImageSizeInBytes()));
                imageRepoItem.setPath(repUri+":"+(imageTagDetail.getImageTags().size()>0?imageTagDetail.getImageTags().get(0):""));
                imageRepoItem.setPushTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS,imageTagDetail.getImagePushedAt()));
                imageRepoItemMapper.insertSelective(imageRepoItem);
            });
        });
        if(com.hummer.common.core.utils.StringUtils.isNotEmpty(repo.get()))
          imageRepo.setRepo(repo.get());
    }

    private void syncAwsPublic(ImageRepo imageRepo,AWSCredentialsProvider awsCredentialsProvider,String region){
        AmazonECRPublic ecrPublic = AmazonECRPublicClient.builder().withRegion(region).withCredentials(awsCredentialsProvider).build();
        com.amazonaws.services.ecrpublic.model.DescribeRepositoriesRequest describeRepositoriesRequest = new com.amazonaws.services.ecrpublic.model.DescribeRepositoriesRequest();
        com.amazonaws.services.ecrpublic.model.DescribeRepositoriesResult describeRepositoriesResult = ecrPublic.describeRepositories(describeRepositoriesRequest);
        List<Repository> repositories = describeRepositoriesResult.getRepositories();
        AtomicReference<String> repo = new AtomicReference<>();
        repositories.forEach(repository -> {
            String repName = repository.getRepositoryName();
            String registryId = repository.getRegistryId();
            String repUri = repository.getRepositoryUri();
            if(repo.get() == null && repUri.contains("/"))
                repo.set(repUri.substring(0, repUri.indexOf("/")));
            DescribeImageTagsRequest describeImageTagsRequest = new DescribeImageTagsRequest();
            describeImageTagsRequest.setRegistryId(registryId);
            describeImageTagsRequest.setRepositoryName(repName);
            DescribeImageTagsResult describeImageTagsResult = ecrPublic.describeImageTags(describeImageTagsRequest);
            List<ImageTagDetail> imageTagDetails = describeImageTagsResult.getImageTagDetails();
            imageTagDetails.forEach(imageTagDetail -> {
                ImageRepoItem imageRepoItem = new ImageRepoItem();
                imageRepoItem.setId(UUIDUtil.newUUID());
                imageRepoItem.setRepository(repName);
                imageRepoItem.setTag(imageTagDetail.getImageTag());
                imageRepoItem.setRepoId(imageRepo.getId());
                imageRepoItem.setDigest(imageTagDetail.getImageDetail().getImageDigest());
                imageRepoItem.setProject(repName.contains("/")?repName.split("/")[0]:repName);
                imageRepoItem.setSize(changeFlowFormat(imageTagDetail.getImageDetail().getImageSizeInBytes()));
                imageRepoItem.setPath(repUri+":"+imageTagDetail.getImageTag());
                imageRepoItem.setPushTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS,imageTagDetail.getImageDetail().getImagePushedAt()));
                imageRepoItemMapper.insertSelective(imageRepoItem);
            });
        });
        if(com.hummer.common.core.utils.StringUtils.isNotEmpty(repo.get()))
            imageRepo.setRepo(repo.get());
    }


    //插入镜像管理
    public void repoImageToImage(ImageRepo imageRepo, LoginUser loginUser) throws Exception {
        try {
            ImageRepoItemExample example = new ImageRepoItemExample();
            example.createCriteria().andRepoIdEqualTo(imageRepo.getId());
            List<ImageRepoItem> imageRepoItems = imageRepoItemMapper.selectByExample(example);
            for (ImageRepoItem item : imageRepoItems) {
                String groupName = item.getProject();
                ImageGroupExample imageGroupExample = new ImageGroupExample();
                imageGroupExample.createCriteria().andNameEqualTo(groupName);
                List<ImageGroup> imageGroups = imageGroupMapper.selectByExample(imageGroupExample);
                String groupId = "";
                if (imageGroups.size() > 0) {
                    groupId = imageGroups.get(0).getId();
                } else {
                    groupId = UUIDUtil.newUUID();
                    ImageGroup imageGroup = new ImageGroup();
                    imageGroup.setId(groupId);
                    imageGroup.setName(groupName);
                    imageGroup.setRepository(groupName);
                    imageGroup.setCreator(loginUser.getUserId());
                    imageGroup.setCreateTime(System.currentTimeMillis());
                    imageGroup.setUpdateTime(System.currentTimeMillis());
                    imageGroupMapper.insertSelective(imageGroup);
                }

                String imageName = item.getPath().replace(":" + item.getTag(), "");
                String name = "";
                if (imageName.contains("/")) {
                    String[] str = imageName.split("/");
                    name = str[str.length - 1];
                } else {
                    name = imageName;
                }

                ImageExample imageExample = new ImageExample();
                imageExample.createCriteria().andNameEqualTo(name).andGroupIdEqualTo(groupId);
                List<Image> images = imageMapper.selectByExample(imageExample);
                if (images.size() > 0) {
                    Image request = images.get(0);
                    request.setUpdateTime(System.currentTimeMillis());
                    request.setType("repo");
                    request.setGroupId(groupId);//镜像分组
                    request.setImageUrl(item.getPath().replace(":" + item.getTag(), ""));
                    request.setImageTag(item.getTag());
                    request.setSize(item.getSize());
                    request.setRepoId(imageRepo.getId());
                    request.setRepoItemId(item.getId());
                    request.setIsImageRepo(true);//是否绑定镜像仓库
                    imageMapper.updateByPrimaryKeySelective(request);
                } else {
                    Image request = new Image();
                    String id = UUIDUtil.newUUID();
                    request.setId(id);
                    request.setIsImageRepo(true);//是否绑定镜像仓库
                    request.setRepoId(imageRepo.getId());
                    request.setName(name);
                    request.setGroupId(groupId);//镜像分组
                    request.setStatus("VALID");
                    request.setCreateTime(System.currentTimeMillis());
                    request.setUpdateTime(System.currentTimeMillis());
                    request.setCreator(loginUser.getUserId());
                    request.setPluginIcon("docker.png");
                    request.setType("repo");
                    request.setImageUrl(item.getPath().replace(":" + item.getTag(), ""));
                    request.setImageTag(item.getTag());
                    request.setSize(item.getSize());
                    request.setRepoItemId(item.getId());
                    Sbom sbom = sbomMapper.selectByExample(null).get(0);
                    request.setSbomId(sbom.getId());
                    SbomVersionExample sbomVersionExample = new SbomVersionExample();
                    sbomVersionExample.createCriteria().andSbomIdEqualTo(sbom.getId());
                    SbomVersion sbomVersion = sbomVersionMapper.selectByExample(sbomVersionExample).get(0);
                    request.setSbomVersionId(sbomVersion.getId());
                    imageMapper.insertSelective(request);
                }

            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public ImageRepo editImageRepo(ImageRepo imageRepo, LoginUser loginUser) throws Exception {
        imageRepo.setUpdateTime(System.currentTimeMillis());
        if(imageRepo.getIsBindAccount()){
            String accountId = imageRepo.getAccountId();
            AccountWithBLOBs accountWithBLOBs = cloudProviderService.selectAccountWithBLOBs(accountId);
            imageRepo.setCredential(accountWithBLOBs.getCredential());
        }
        boolean result = syncImages(imageRepo, loginUser);

        if (result) {
            imageRepo.setStatus("VALID");
        } else {
            imageRepo.setStatus("INVALID");
        }

        operationLogService.log(loginUser, imageRepo.getId(), imageRepo.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.UPDATE, "i18n_update_image_repo");
        imageRepoMapper.updateByPrimaryKeySelective(imageRepo);

        return imageRepo;
    }

    public void deleteImageRepo(String id, LoginUser loginUser) throws Exception {
        imageRepoMapper.deleteByPrimaryKey(id);
        operationLogService.log(loginUser, id, id, ResourceTypeConstants.IMAGE.name(), ResourceOperation.DELETE, "i18n_delete_image_repo");
    }

    public List<ImageDTO> imageList(ImageRequest request) {
        return extImageMapper.imageList(request);
    }

    public List<Image> allBindList(String sbomVersionId) {
        ImageExample example = new ImageExample();
        example.createCriteria().andSbomVersionIdEqualTo(sbomVersionId);
        return imageMapper.selectByExample(example);
    }

    public List<Image> unBindList() {
        return imageMapper.selectByExample(null);
    }

    public Image addImage(MultipartFile tarFile, ImageRequest request, LoginUser loginUser) throws Exception {

        try {
            String id = UUIDUtil.newUUID();
            request.setId(id);
            request.setStatus("VALID");
            request.setCreateTime(System.currentTimeMillis());
            request.setUpdateTime(System.currentTimeMillis());
            request.setCreator(loginUser.getUserId());
            request.setPluginIcon("docker.png");
            if (StringUtils.equalsIgnoreCase(request.getType(), "repo")) {
                ImageRepoItem imageRepoItem = imageRepoItemMapper.selectByPrimaryKey(request.getRepoItemId());
                request.setRepoItemId(imageRepoItem.getId());
                request.setImageUrl(imageRepoItem.getPath().replace(":" + imageRepoItem.getTag(), ""));
                request.setImageTag(imageRepoItem.getTag());
                request.setSize(imageRepoItem.getSize());
            }
            if (tarFile != null) {
                String tarFilePath = upload(tarFile, ImageConstants.DEFAULT_BASE_DIR);
                request.setPath(tarFilePath);
                request.setRepoItemId("");
                request.setSize(changeFlowFormat(tarFile.getSize()));
            }

            if (!request.getIsImageRepo()) {
                request.setRepoId("");
                request.setRepoItemId("");
            }

            imageMapper.insertSelective(request);

            operationLogService.log(loginUser, request.getId(), request.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.CREATE, "i18n_create_image");

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return request;
    }

    public Image updateImage(MultipartFile tarFile, ImageRequest request, LoginUser loginUser) throws Exception {

        try {
            request.setStatus("VALID");
            request.setUpdateTime(System.currentTimeMillis());
            request.setCreator(loginUser.getUserId());
            request.setPluginIcon("docker.png");
            if (StringUtils.equalsIgnoreCase(request.getType(), "repo")) {
                ImageRepoItem imageRepoItem = imageRepoItemMapper.selectByPrimaryKey(request.getRepoItemId());
                request.setRepoItemId(imageRepoItem.getId());
                request.setImageUrl(imageRepoItem.getPath().replace(":" + imageRepoItem.getTag(), ""));
                request.setImageTag(imageRepoItem.getTag());
                request.setSize(imageRepoItem.getSize());
            }
            if (tarFile != null) {
                String tarFilePath = upload(tarFile, ImageConstants.DEFAULT_BASE_DIR);
                request.setRepoItemId("");
                request.setPath(tarFilePath);
            }
            if (!request.getIsImageRepo()) {
                request.setRepoId("");
                request.setRepoItemId("");
            }

            imageMapper.updateByPrimaryKeySelective(request);

            operationLogService.log(loginUser, request.getId(), request.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.UPDATE, "i18n_update_image");

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return request;
    }

    public void deleteImage(String id, LoginUser loginUser) throws Exception {
        imageMapper.deleteByPrimaryKey(id);
        deleteResultByImageId(id, loginUser);
        operationLogService.log(loginUser, id, id, ResourceTypeConstants.IMAGE.name(), ResourceOperation.DELETE, "i18n_delete_image");
    }

    /**
     * 以默认配置进行文件上传
     *
     * @param file 上传的文件
     * @return 文件名称
     * @throws Exception
     */
    public static final String upload(MultipartFile file, String dir) throws IOException {
        try {
            //png、html等小文件存放路径，页面需要显示，项目内目录
            //jar包等大文件存放路径，项目外目录
            return FileUploadUtils.upload(dir, file);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    public List<ImageRuleDTO> ruleList(ImageRuleRequest request) {
        return extImageRuleMapper.ruleList(request);
    }

    public int addImageRule(ImageRuleRequest request, LoginUser loginUser) throws Exception {
        ImageRule record = new ImageRule();
        BeanUtils.copyBean(record, request);
        record.setId(UUIDUtil.newUUID());
        record.setLastModified(System.currentTimeMillis());
        saveRuleTagMapping(record.getId(), request.getTagKey());
        operationLogService.log(loginUser, record.getId(), record.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.CREATE, "i18n_create_image_rule");
        return imageRuleMapper.insertSelective(record);
    }

    public void saveRuleTagMapping(String ruleId, String tagKey) {
        deleteRuleTag(null, ruleId);
        if (StringUtils.isNotEmpty(tagKey)) {
            RuleTagMapping sfRulesTagMapping = new RuleTagMapping();
            sfRulesTagMapping.setRuleId(ruleId);
            sfRulesTagMapping.setTagKey(tagKey);
            cloudProviderService.insertRuleTagMappings(sfRulesTagMapping);
        }
    }

    public void deleteRuleTag(String tagkey, String ruleId) {
        if (StringUtils.isNotBlank(tagkey)) {
            cloudProviderService.deleteRuleTag(tagkey);
        }
        if (StringUtils.isNotBlank(ruleId)) {
            RuleTagMappingExample ruleTagMappingExample = new RuleTagMappingExample();
            ruleTagMappingExample.createCriteria().andRuleIdEqualTo(ruleId);
            cloudProviderService.deleteRuleTagMappingByExample(ruleTagMappingExample);
        }
    }

    public int updateImageRule(ImageRuleRequest request, LoginUser loginUser) throws Exception {
        ImageRule record = new ImageRule();
        BeanUtils.copyBean(record, request);
        record.setLastModified(System.currentTimeMillis());
        saveRuleTagMapping(record.getId(), request.getTagKey());
        operationLogService.log(loginUser, record.getId(), record.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.UPDATE, "i18n_update_image_rule");
        return imageRuleMapper.updateByPrimaryKeySelective(record);
    }

    public void deleteImageRule(String id, LoginUser loginUser) throws Exception {
        deleteRuleTag(null, id);
        imageRuleMapper.deleteByPrimaryKey(id);
        operationLogService.log(loginUser, id, id, ResourceTypeConstants.IMAGE.name(), ResourceOperation.DELETE, "i18n_delete_image_rule");
    }

    public int changeStatus(ImageRule rule) throws Exception {
        return imageRuleMapper.updateByPrimaryKeySelective(rule);
    }

    public void scan(String id, LoginUser loginUser) throws Exception {
        Image image = imageMapper.selectByPrimaryKey(id);
        Integer scanId = systemProviderService.insertScanHistory(image);
        if (StringUtils.equalsIgnoreCase(image.getStatus(), CloudAccountConstants.Status.VALID.name())) {
            List<ImageRuleDTO> ruleList = ruleList(null);
            ImageResultWithBLOBs result = new ImageResultWithBLOBs();

            deleteRescanResultByImageId(id);

            for (ImageRuleDTO dto : ruleList) {
                BeanUtils.copyBean(result, image);
                result.setId(UUIDUtil.newUUID());
                result.setImageId(id);
                result.setApplyUser(loginUser.getUserId());
                result.setCreateTime(System.currentTimeMillis());
                result.setUpdateTime(System.currentTimeMillis());
                result.setRuleId(dto.getId());
                result.setRuleName(dto.getName());
                result.setRuleDesc(dto.getDescription());
                result.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
                result.setSeverity(dto.getSeverity());
                result.setUserName(loginUser.getUserName());
                imageResultMapper.insertSelective(result);

                saveImageResultLog(result.getId(), "i18n_start_image_result", "", true, loginUser);
                operationLogService.log(loginUser, result.getId(), result.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.SCAN, "i18n_start_image_result");

                systemProviderService.insertScanTaskHistory(result, scanId, image.getId(), TaskEnum.imageAccount.getType());

                systemProviderService.insertHistoryImageResult(BeanUtils.copyBean(new HistoryImageResultWithBLOBs(), result));
            }
        }

    }

    public void createScan(ImageResultWithBLOBs result, LoginUser loginUser) throws Exception {
        try {
            ImageRuleRequest request = new ImageRuleRequest();
            request.setId(result.getRuleId());
            ImageRuleDTO dto = ruleList(request).get(0);
            Image image = imageMapper.selectByPrimaryKey(result.getImageId());
            String resultJson = "";

            ResultDTO resultDTO = execute(image);
            String log = resultDTO.getResultStr();
            String command = resultDTO.getCommand();

            if (log.contains("docker login")) {
                throw new Exception(log);
            }
            resultJson = ReadFileUtils.readToBuffer(TrivyConstants.DEFAULT_BASE_DIR + TrivyConstants.TRIVY_JSON);

            result.setCommand(command);
            result.setReturnLog(log);
            result.setResultJson(resultJson);
            result.setRuleId(dto.getId());
            result.setRuleName(dto.getName());
            result.setRuleDesc(dto.getDescription());
            result.setUpdateTime(System.currentTimeMillis());
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.FINISHED.toString());

            long count = saveImageResultItem(result);
            result.setReturnSum(count);
            imageResultMapper.updateByPrimaryKeySelective(result);

            systemProviderService.createImageMessageOrder(result);
            saveImageResultLog(result.getId(), "i18n_end_image_result", "", true, loginUser);

            systemProviderService.updateHistoryImageResult(BeanUtils.copyBean(new HistoryImageResultWithBLOBs(), result));
        } catch (Exception e) {
            LogUtil.error("create ImageResult: " + e.getMessage());
            result.setUpdateTime(System.currentTimeMillis());
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.ERROR.toString());
            imageResultMapper.updateByPrimaryKeySelective(result);
            systemProviderService.updateHistoryImageResult(BeanUtils.copyBean(new HistoryImageResultWithBLOBs(), result));
            saveImageResultLog(result.getId(), "i18n_operation_ex" + ": " + e.getMessage(), e.getMessage(), false, loginUser);
        }
    }

    long saveImageResultItem(ImageResultWithBLOBs result) throws Exception {

        int i = 0;
        //插入resultJsons
        JSONObject jsonG = JSONObject.parseObject(result.getResultJson());
        if (jsonG != null) {
            JSONArray resultJsons = JSONArray.parseArray(jsonG.getString("Results"));
            if (resultJsons != null) {
                for (Object obj : resultJsons) {
                    JSONObject jsonObject = (JSONObject) obj;
                    JSONArray vulnJsons = JSONArray.parseArray(jsonObject.getString("Vulnerabilities"));
                    if (vulnJsons != null) {
                        for (Object o : vulnJsons) {
                            JSONObject resultObject = (JSONObject) o;
                            ImageResultItemWithBLOBs imageResultItemWithBLOBs = new ImageResultItemWithBLOBs();
                            imageResultItemWithBLOBs.setResultId(result.getId());
                            imageResultItemWithBLOBs.setVulnerabilityId(resultObject.getString("VulnerabilityID"));
                            imageResultItemWithBLOBs.setPkgName(resultObject.getString("PkgName"));
                            imageResultItemWithBLOBs.setInstalledVersion(resultObject.getString("InstalledVersion"));
                            imageResultItemWithBLOBs.setFixedVersion(resultObject.getString("FixedVersion"));
                            imageResultItemWithBLOBs.setLayer(resultObject.getString("Layer"));
                            imageResultItemWithBLOBs.setSeveritySource(resultObject.getString("SeveritySource"));
                            imageResultItemWithBLOBs.setPrimaryUrl(resultObject.getString("PrimaryURL"));
                            imageResultItemWithBLOBs.setDataSource(resultObject.getString("DataSource"));
                            imageResultItemWithBLOBs.setTitle(resultObject.getString("Title"));
                            imageResultItemWithBLOBs.setDescription(resultObject.getString("Description"));
                            imageResultItemWithBLOBs.setSeverity(resultObject.getString("Severity"));
                            imageResultItemWithBLOBs.setCweIds(resultObject.getString("CweIDs"));
                            imageResultItemWithBLOBs.setCvss(resultObject.getString("CVSS"));
                            imageResultItemWithBLOBs.setReferences(resultObject.getString("References"));
                            imageResultItemWithBLOBs.setPublishedDate(resultObject.getString("PublishedDate"));
                            imageResultItemWithBLOBs.setLastModifiedDate(resultObject.getString("LastModifiedDate"));
                            imageResultItemMapper.insertSelective(imageResultItemWithBLOBs);
                            i++;
                        }
                    }
                }
            }
        }
        return i;
    }

    public String reScan(String id, LoginUser loginUser) throws Exception {
        ImageResultWithBLOBs result = imageResultMapper.selectByPrimaryKey(id);

        result.setUpdateTime(System.currentTimeMillis());
        result.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
        result.setUserName(loginUser.getUserName());
        imageResultMapper.updateByPrimaryKeySelective(result);

        this.reScanDeleteImageResult(id);

        saveImageResultLog(result.getId(), "i18n_restart_image_result", "", true, loginUser);

        operationLogService.log(loginUser, result.getId(), result.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.RESCAN, "i18n_restart_image_result");

        systemProviderService.updateHistoryImageResult(BeanUtils.copyBean(new HistoryImageResultWithBLOBs(), result));

        return result.getId();
    }

    public void reScanDeleteImageResult(String id) throws Exception {

        ImageResultItemExample imageResultItemExample = new ImageResultItemExample();
        imageResultItemExample.createCriteria().andResultIdEqualTo(id);
        imageResultItemMapper.deleteByExample(imageResultItemExample);

    }

    public void deleteImageResult(String id, LoginUser loginUser) throws Exception {
        ImageResultLogExample logExample = new ImageResultLogExample();
        logExample.createCriteria().andResultIdEqualTo(id);
        imageResultLogMapper.deleteByExample(logExample);

        ImageResultItemExample itemExample = new ImageResultItemExample();
        itemExample.createCriteria().andResultIdEqualTo(id);
        imageResultItemMapper.deleteByExample(itemExample);

        systemProviderService.deleteHistoryImageResult(id);
        imageResultMapper.deleteByPrimaryKey(id);
        operationLogService.log(loginUser, id, id, ResourceTypeConstants.IMAGE.name(), ResourceOperation.DELETE, "i18n_delete_image_result");
    }

    public void deleteRescanResultByImageId(String id) throws Exception {
        ImageResultExample example = new ImageResultExample();
        example.createCriteria().andImageIdEqualTo(id);
        imageResultMapper.deleteByExample(example);
    }

    public void deleteResultByImageId(String id, LoginUser loginUser) throws Exception {

        ImageResultExample example = new ImageResultExample();
        example.createCriteria().andImageIdEqualTo(id);
        List<ImageResult> list = imageResultMapper.selectByExample(example);

        for (ImageResult result : list) {
            ImageResultLogExample logExample = new ImageResultLogExample();
            logExample.createCriteria().andResultIdEqualTo(result.getId());
            imageResultLogMapper.deleteByExample(logExample);

            ImageResultItemExample itemExample = new ImageResultItemExample();
            itemExample.createCriteria().andResultIdEqualTo(result.getId());
            imageResultItemMapper.deleteByExample(itemExample);

            systemProviderService.deleteHistoryImageResult(result.getId());
        }
        imageResultMapper.deleteByExample(example);
        operationLogService.log(loginUser, id, id, ResourceTypeConstants.IMAGE.name(), ResourceOperation.DELETE, "i18n_delete_image_result");

    }

    public ResultDTO execute(Image image) throws Exception {
        try {
            Proxy proxy = new Proxy();
            ImageRepo imageRepo = new ImageRepo();
            if (image.getIsProxy() && image.getProxyId() != null) {
                proxy = proxyMapper.selectByPrimaryKey(image.getProxyId());
            }
            if (image.getRepoId() != null && !StringUtils.isEmpty(image.getRepoId())) {
                imageRepo = imageRepoMapper.selectByPrimaryKey(image.getRepoId());
            }
            ScanSetting scanSetting = new ScanSetting();
            String skipDbUpdate = systemProviderService.getSystemParameterValue(ParamConstants.SCAN.SkipDbUpdate.getKey());
            String securityChecks = systemProviderService.getSystemParameterValue(ParamConstants.SCAN.SecurityChecks.getKey());
            String ignoreUnfixed = systemProviderService.getSystemParameterValue(ParamConstants.SCAN.IgnoreUnfixed.getKey());
            String offlineScan = systemProviderService.getSystemParameterValue(ParamConstants.SCAN.OfflineScan.getKey());
            String severity = systemProviderService.getSystemParameterValue(ParamConstants.SCAN.Severity.getKey());
            scanSetting.setSkipDbUpdate(skipDbUpdate);
            scanSetting.setSecurityChecks(securityChecks);
            scanSetting.setIgnoreUnfixed(ignoreUnfixed);
            scanSetting.setOfflineScan(offlineScan);
            scanSetting.setSeverity(severity);
            ResultDTO resultDTO = execute(image, proxy, imageRepo, scanSetting);
            if (resultDTO.getResultStr().contains("ERROR") || resultDTO.getResultStr().contains("error")) {
                throw new Exception(resultDTO.getResultStr());
            }
            return resultDTO;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    public ResultDTO execute(Object... obj) throws Exception {
        Image image = (Image) obj[0];
        try {
            String _proxy = "";
            String dockerLogin = "";
            if (image.getIsProxy() != null && image.getIsProxy()) {
                Proxy proxy = (Proxy) obj[1];
                _proxy = ProxyUtil.isProxy(proxy);
            }
            if (image.getRepoId() != null && !StringUtils.isEmpty(image.getRepoId())) {
                ImageRepo imageRepo = (ImageRepo) obj[2];
                String repo = imageRepo.getRepo().replace("https://", "").replace("http://", "");
                if (repo.endsWith("/")) {
                    repo = repo.substring(0, repo.length() - 1);
                }
                dockerLogin =
                        "TRIVY_USERNAME='" + imageRepo.getUserName() + "';" +
                                "TRIVY_PASSWORD='" + imageRepo.getPassword() + "';";
            }
            String fileName = "";
            if (StringUtils.equalsIgnoreCase("image", image.getType()) || StringUtils.equalsIgnoreCase("repo", image.getType())) {
                fileName = image.getImageUrl() + ":" + image.getImageTag();
            } else {
                fileName = TrivyConstants.INPUT + ImageConstants.DEFAULT_BASE_DIR + image.getPath();
            }
            ScanSetting scanSetting = (ScanSetting) obj[3];
            String str = "";
            if(scanSetting.getSkipDbUpdate() != null && StringUtils.equalsIgnoreCase(scanSetting.getSkipDbUpdate(), "true")) {
                str = str + TrivyConstants.SKIP_DB_UPDATE + TrivyConstants.SKIP_JAVA_DB_UPDATE;
            }
            if(scanSetting.getIgnoreUnfixed() != null && StringUtils.equalsIgnoreCase(scanSetting.getIgnoreUnfixed(), "true")) {
                str = str + TrivyConstants.UNFIXED;
            }
            if(scanSetting.getSecurityChecks() != null) {
                str = str + TrivyConstants.SECURITY_CHECKS + scanSetting.getSecurityChecks();
            } else {
                str = str + TrivyConstants.SECURITY_CHECKS_DEFAULT;
            }
            if(scanSetting.getOfflineScan() != null && StringUtils.equalsIgnoreCase(scanSetting.getOfflineScan(), "true")) {
                str = str + TrivyConstants.OFFLINE_SCAN;
            }
            CommandUtils.commonExecCmdWithResult(TrivyConstants.TRIVY_RM + TrivyConstants.TRIVY_JSON, TrivyConstants.DEFAULT_BASE_DIR);
            String command = _proxy + dockerLogin + TrivyConstants.TRIVY_IMAGE + str + fileName + TrivyConstants.TRIVY_TYPE + TrivyConstants.DEFAULT_BASE_DIR + TrivyConstants.TRIVY_JSON;
            LogUtil.info(image.getId() + " {Image}[command]: " + image.getName() + "   " + command);
            String resultStr = CommandUtils.commonExecCmdWithResult(command, TrivyConstants.DEFAULT_BASE_DIR);
            ResultDTO dto = new ResultDTO();
            dto.setCommand(command);
            dto.setResultStr(resultStr);
            return dto;
        } catch (Exception e) {
            throw e;
        }
    }

    public List<ImageResultWithBLOBsDTO> resultListWithBLOBs(ImageResultRequest request) {
        List<ImageResultWithBLOBsDTO> list = extImageResultMapper.resultListWithBLOBs(request);
        return list;
    }

    public List<ImageResultDTO> resultList(ImageResultRequest request) {
        List<ImageResultDTO> list = extImageResultMapper.resultList(request);
        return list;
    }

    public HistoryImageReportDTO getImageResultDto(String resultId) {
        HistoryImageReportDTO dto = systemProviderService.getImageResultDto(resultId);
        return dto;
    }

    public ImageResultDTO getImageResult(String resultId) {
        ImageResultDTO imageResult = extImageResultMapper.getImageResult(resultId);
        return imageResult;
    }

    public ImageResultWithBLOBs getImageResultWithBLOBs(String resultId) {
        ImageResultWithBLOBs imageResultWithBLOBs = imageResultMapper.selectByPrimaryKey(resultId);
        return imageResultWithBLOBs;
    }

    public List<ImageResultLogWithBLOBs> getImageResultLog(String resultId) {
        ImageResultLogExample example = new ImageResultLogExample();
        example.createCriteria().andResultIdEqualTo(resultId);
        return imageResultLogMapper.selectByExampleWithBLOBs(example);
    }

    public void saveImageResultLog(String resultId, String operation, String output, boolean result, LoginUser loginUser) throws Exception {
        ImageResultLogWithBLOBs imageResultLog = new ImageResultLogWithBLOBs();
        String operator = "system";
        try {
            if (loginUser != null) {
                operator = loginUser.getUserId();
            }
        } catch (Exception e) {
            //防止单元测试无session
        }
        imageResultLog.setOperator(operator);
        imageResultLog.setResultId(resultId);
        imageResultLog.setCreateTime(System.currentTimeMillis());
        imageResultLog.setOperation(operation);
        imageResultLog.setOutput(output);
        imageResultLog.setResult(result);
        imageResultLogMapper.insertSelective(imageResultLog);

    }

    public List<ImageResultItemWithBLOBs> resultItemList(ImageResultItem resourceRequest) {
        ImageResultItemExample example = new ImageResultItemExample();
        if (resourceRequest.getPkgName() != null && !StringUtils.isBlank(resourceRequest.getPkgName())) {
            example.createCriteria().andResultIdEqualTo(resourceRequest.getResultId()).andPkgNameLike("%" + resourceRequest.getPkgName() + "%");
        } else {
            example.createCriteria().andResultIdEqualTo(resourceRequest.getResultId());
        }
        example.setOrderByClause("FIELD(`severity`, 'CRITICAL', 'HIGH', 'MEDIUM', 'LOW', 'UNKNOWN')");
        return imageResultItemMapper.selectByExampleWithBLOBs(example);
    }

    public List<ImageResultItemWithBLOBs> resultItemListBySearch(ImageResultItemRequest request) {
        return extImageResultItemMapper.resultItemListBySearch(request);
    }

    public List<ImageRepoSyncLogWithBLOBs> repoSyncList(String id) {
        ImageRepoSyncLogExample example = new ImageRepoSyncLogExample();
        example.createCriteria().andRepoIdEqualTo(id);
        example.setOrderByClause("create_time desc");
        return imageRepoSyncLogMapper.selectByExampleWithBLOBs(example);
    }

    public void syncImage(String id, LoginUser loginUser) throws Exception {
        ImageRepo imageRepo = imageRepoMapper.selectByPrimaryKey(id);
        syncImages(imageRepo, loginUser);
    }

    /**
     * 转换流量格式为xxGBxxMBxxKB
     */
    public static String changeFlowFormat(long flows) {
        if (flows < 1024) {
            return flows + "B";
        }
        flows = flows / 1024;//默认是1024KB
        if (flows > 0 && flows < 1024) {//小于1M
            return flows + "KB";
        } else if (flows >= 1024 && flows < 1048576) {//大于1M小于1G
            int changeM = (int) Math.floor(flows / 1024);//整M数
            int surplusM = (int) Math.floor(flows % 1024);//除M后的余数
            if (surplusM > 0) {//余数大于0KB
                return changeM + 1 + "MB";
            } else {//整M，没有余数
                return changeM + "MB";
            }
        } else if (flows >= 1048576) {//大于1G
            int changeG = (int) Math.floor(flows / 1048576);//整G数
            int surplusG = (int) Math.floor(flows % 1048576);//除G后的余数
            if (surplusG >= 1024) {//余数大于大于1M
                int changeM = (int) Math.floor(surplusG / 1024);
                int surplusM = (int) Math.floor(surplusG % 1024);
                if (surplusM > 0) {//余数大于0KB
                    return changeG + 1 + "GB";
                } else {//整M，没有余数
                    return changeG + 1 + "GB";
                }
            } else if (surplusG < 1024 && surplusG > 0) {//余数小于1M，大于0K
                int surplusM = (int) Math.floor(surplusG % 1024);
                return changeG + 1 + "GB";
            } else {
                return changeG + "GB";
            }
        }
        return "i18n_no_data";
    }

    public void scanImagesRepo(List<String> ids, LoginUser loginUser) {
        ids.forEach(id -> {
            try {
                ImageRepoItem imageRepoItem = imageRepoItemMapper.selectByPrimaryKey(id);
                ScanImageRepoRequest request = BeanUtils.copyBean(new ScanImageRepoRequest(), imageRepoItem);
                Sbom sbom = sbomMapper.selectByExample(null).get(0);
                request.setSbomId(sbom.getId());
                SbomVersionExample example = new SbomVersionExample();
                example.createCriteria().andSbomIdEqualTo(sbom.getId());
                SbomVersion sbomVersion = sbomVersionMapper.selectByExample(example).get(0);
                request.setSbomVersionId(sbomVersion.getId());
                request.setName(imageRepoItem.getPath());
                scanImageRepo(request, loginUser);
            } catch (Exception e) {
                throw new HRException(e.getMessage());
            }
        });
    }

    public void scanImageRepo(ScanImageRepoRequest request, LoginUser loginUser) throws Exception {
        try {

            Image image = BeanUtils.copyBean(new Image(), request);
            image.setStatus("VALID");
            image.setCreateTime(System.currentTimeMillis());
            image.setUpdateTime(System.currentTimeMillis());
            image.setCreator(loginUser.getUserId());
            image.setType("repo");
            image.setImageUrl(request.getPath().replace(":" + request.getTag(), ""));
            image.setImageTag(request.getTag());
            image.setPluginIcon("docker.png");
            image.setIsImageRepo(true);
            image.setIsImageIcon(false);

            ImageExample example = new ImageExample();
            example.createCriteria().andImageUrlEqualTo(image.getImageUrl()).andImageTagEqualTo(image.getImageTag()).andRepoIdEqualTo(image.getRepoId());
            List<Image> list = imageMapper.selectByExample(example);
            if (list.size() == 0) {
                image.setId(UUIDUtil.newUUID());
                imageMapper.insertSelective(image);
                operationLogService.log(loginUser, image.getId(), image.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.CREATE, "i18n_create_image");

            } else {
                image.setId(list.get(0).getId());
                imageMapper.updateByPrimaryKeySelective(image);
                operationLogService.log(loginUser, image.getId(), image.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.UPDATE, "i18n_update_image_rule");

            }
            ImageResultExample imageResultExample = new ImageResultExample();
            imageResultExample.createCriteria().andImageIdEqualTo(image.getId());
            List<ImageResult> results = imageResultMapper.selectByExample(imageResultExample);
            if (results.size() > 0) {
                this.reScan(results.get(0).getId(), loginUser);
            } else {
                this.scan(image.getId(), loginUser);
            }

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public int saveHarborV2(String path, Map<String, String> header, ImageRepo imageRepo) throws Exception {
        int i = 0;
        String projectStr = HttpClientUtil.HttpGet(path + "/api/v2.0/projects/", header);
        JSONArray projects = JSON.parseArray(projectStr);
        for (Object o : projects) {
            JSONObject project = (JSONObject) o;
            String projectName = project.getString("name");
            String repositoriesStr = HttpClientUtil.HttpGet(path + "/api/v2.0/projects/" + projectName + "/repositories", header);
            JSONArray repositories = JSON.parseArray(repositoriesStr);
            for (Object repository : repositories) {
                JSONObject rep = (JSONObject) repository;
                String repName = rep.getString("name");
                if (repName.indexOf("/") > 0) {
                    repName = repName.split("/", -1)[1];
                }
                String artifactsStr = HttpClientUtil.HttpGet(path + "/api/v2.0/projects/" + projectName + "/repositories/" + repName + "/artifacts", header);
                JSONArray artifacts = JSON.parseArray(artifactsStr);
                for (Object artifact : artifacts) {
                    JSONObject arti = (JSONObject) artifact;
                    String digest = arti.getString("digest");
                    String push_time = arti.getString("push_time");
                    long size = arti.getLong("size");
                    JSONObject extra_attrs = arti.getJSONObject("extra_attrs");
                    String architecture = "";
                    if(extra_attrs != null){
                        architecture= extra_attrs.getString("architecture");
                    }
                    JSONArray tags = arti.getJSONArray("tags");
                    if(tags != null){
                        List<JSONObject> tagList = tags.toJavaList(JSONObject.class);
                        for (JSONObject tag : tagList) {
                            String tagStr = tag.getString("name");
                            ImageRepoItem imageRepoItem = new ImageRepoItem();
                            imageRepoItem.setId(UUIDUtil.newUUID());
                            imageRepoItem.setProject(projectName);
                            imageRepoItem.setRepository(repName);
                            imageRepoItem.setTag(tagStr);
                            imageRepoItem.setDigest(digest);
                            imageRepoItem.setRepoId(imageRepo.getId());
                            imageRepoItem.setPushTime(push_time);
                            imageRepoItem.setArch(architecture);
                            imageRepoItem.setSize(changeFlowFormat(size));
                            imageRepoItem.setPath(path.replaceAll("https://", "").replaceAll("http://", "") + "/" + projectName + "/" + repName + ":" + tagStr);
                            imageRepoItemMapper.insertSelective(imageRepoItem);
                            i++;
                        }
                    }
                }
            }
        }
        return i;
    }

    public int saveHarborV1(String path, Map<String, String> header, ImageRepo imageRepo) throws Exception {
        int i = 0;
        String projectStr = HttpClientUtil.HttpGet(path + "/api/projects/", header);
        if ("null".equals(projectStr)) {
            return i;
        }
        JSONArray projects = JSON.parseArray(projectStr);
        for (Object o : projects) {
            JSONObject project = (JSONObject) o;
            String projectName = project.getString("name");
            String repositoriesStr = HttpClientUtil.HttpGet(path + "/api/repositories?project_id=" + project.getString("project_id"), header);
            JSONArray repositories = JSON.parseArray(repositoriesStr);
            for (Object repository : repositories) {
                JSONObject rep = (JSONObject) repository;
                String repName = rep.getString("name");
                if (repName.indexOf("/") > 0) {
                    repName = repName.split("/", -1)[1];
                }
                String artifactsStr = HttpClientUtil.HttpGet(path + "/api/repositories/" + rep.getString("name") + "/tags", header);
                JSONArray artifacts = JSON.parseArray(artifactsStr);
                for (Object artifact : artifacts) {
                    JSONObject arti = (JSONObject) artifact;
                    String digest = arti.getString("digest");
                    String push_time = arti.getString("push_time");
                    long size = arti.getLong("size");
                    String architecture = arti.getString("architecture");
                    String tagStr = arti.getString("name");
                    ImageRepoItem imageRepoItem = new ImageRepoItem();
                    imageRepoItem.setId(UUIDUtil.newUUID());
                    imageRepoItem.setProject(projectName);
                    imageRepoItem.setRepository(repName);
                    imageRepoItem.setTag(tagStr);
                    imageRepoItem.setDigest(digest);
                    imageRepoItem.setRepoId(imageRepo.getId());
                    imageRepoItem.setPushTime(push_time);
                    imageRepoItem.setArch(architecture);
                    imageRepoItem.setSize(changeFlowFormat(size));
                    imageRepoItem.setPath(path.replaceAll("https://", "").replaceAll("http://", "") + "/" + rep.getString("name") + ":" + tagStr);
                    imageRepoItemMapper.insertSelective(imageRepoItem);
                    i++;
                }
            }
        }
        return i;
    }

    private int getHarborVersion(String path) {
        Map<String, String> header = new HashMap<>();
        header.put("Accept", CloudNativeConstants.Accept);
        try {
            HttpClientUtil.HttpGet(path + "/api/v2.0/projects/", header);
        } catch (Exception e) {
            return 1;
        }
        return 2;
    }

    public String download(Map<String, Object> map) {
        HistoryImageResultWithBLOBs imageTaskWithBLOBs = systemProviderService.imageResult(map.get("id").toString());
        String str = imageTaskWithBLOBs.getResultJson();
        return str;
    }

    public Map<String, Object> topInfo(Map<String, Object> params) {
        return extImageMapper.topInfo(params);
    }

    public List<Map<String, Object>> imageRepoChart() {
        return extImageMapper.imageRepoChart();
    }

    public List<Map<String, Object>> severityChart() {
        return extImageMapper.severityChart();
    }

    public List<Image> allList() {
        return imageMapper.selectByExample(null);
    }

    public List<HistoryImageResultDTO> history(ImageResultRequest request) {
        List<HistoryImageResultDTO> historyList = systemProviderService.imageHistory(request);
        return historyList;
    }

    public List<ImageResultItemWithBLOBs> historyResultItemList(ImageResultItem imageResultItem) {
        ImageResultItemExample example = new ImageResultItemExample();
        example.createCriteria().andResultIdEqualTo(imageResultItem.getResultId());
        example.setOrderByClause("FIELD(`severity`, 'CRITICAL', 'HIGH', 'MEDIUM', 'LOW', 'UNKNOWN')");
        return imageResultItemMapper.selectByExampleWithBLOBs(example);
    }

    public void imageRepoSetting(ImageRepoSetting imageRepoSetting) throws Exception {
        ImageRepoSettingExample example = new ImageRepoSettingExample();
        example.createCriteria().andRepoIdEqualTo(imageRepoSetting.getRepoId());
        List<ImageRepoSetting> settings = imageRepoSettingMapper.selectByExample(example);

        String old = "";
        if (settings.size() > 0) {
            ImageRepoSetting setting = settings.get(0);
            old = setting.getRepo();
            setting.setUpdateTime(System.currentTimeMillis());
            setting.setRepoOld(setting.getRepo());
            setting.setRepo(imageRepoSetting.getRepo());
            imageRepoSettingMapper.updateByPrimaryKeySelective(setting);
        } else {
            old = imageRepoSetting.getRepoOld();
            imageRepoSetting.setId(UUIDUtil.newUUID());
            imageRepoSetting.setCreateTime(System.currentTimeMillis());
            imageRepoSetting.setUpdateTime(System.currentTimeMillis());
            imageRepoSettingMapper.insertSelective(imageRepoSetting);
        }

        ImageRepoItemExample imageRepoItemExample = new ImageRepoItemExample();
        imageRepoItemExample.createCriteria().andRepoIdEqualTo(imageRepoSetting.getRepoId());
        List<ImageRepoItem> list = imageRepoItemMapper.selectByExample(imageRepoItemExample);
        for (ImageRepoItem imageRepoItem : list) {
            imageRepoItem.setPath(imageRepoItem.getPath().replace(old, imageRepoSetting.getRepo()));
            imageRepoItemMapper.updateByPrimaryKeySelective(imageRepoItem);
        }
    }

    public ImageRepoSetting getImageRepoSetting(String repoId) throws Exception {
        ImageRepoSettingExample example = new ImageRepoSettingExample();
        example.createCriteria().andRepoIdEqualTo(repoId);
        List<ImageRepoSetting> list = imageRepoSettingMapper.selectByExample(example);
        if(list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public void deleteHistoryImageResult(String id) throws Exception {
        systemProviderService.deleteHistoryImageResult(id);
    }

    public void deleteImageRepos(List<String> ids, LoginUser loginUser) throws Exception {
        ids.forEach(id -> {
            try {
                deleteImageRepo(id, loginUser);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    public void deleteImages(List<String> ids, LoginUser loginUser) throws Exception {
        ids.forEach(id -> {
            try {
                deleteImage(id, loginUser);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    public List<ImageGroup> imageGroupList() {
        return imageGroupMapper.selectByExample(null);
    }

    public int addImageGroup(ImageGroup imageGroup, LoginUser loginUser) {
        imageGroup.setId(UUIDUtil.newUUID());
        imageGroup.setCreator(loginUser.getUserId());
        imageGroup.setCreateTime(System.currentTimeMillis());
        imageGroup.setUpdateTime(System.currentTimeMillis());
        operationLogService.log(loginUser, imageGroup.getId(), imageGroup.getId(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.CREATE, "i18n_create_image_group");

        return imageGroupMapper.insertSelective(imageGroup);
    }

    public int editImageGroup(ImageGroup imageGroup, LoginUser loginUser) {
        imageGroup.setUpdateTime(System.currentTimeMillis());
        operationLogService.log(loginUser, imageGroup.getId(), imageGroup.getId(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.UPDATE, "i18n_update_image_group");
        return imageGroupMapper.updateByPrimaryKeySelective(imageGroup);
    }

    public void deleteImageGroup(ImageGroup imageGroup, LoginUser loginUser) {

        ImageExample example = new ImageExample();
        example.createCriteria().andGroupIdEqualTo(imageGroup.getId());
        List<Image> list = imageMapper.selectByExample(example);
        if (list.size() > 0) {
            HRException.throwException(Translator.get("i18n_ex_image_validate"));
        } else {
            imageGroupMapper.deleteByPrimaryKey(imageGroup.getId());
        }
        operationLogService.log(loginUser, imageGroup.getId(), imageGroup.getId(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.DELETE, "i18n_delete_image_group");

    }

    public void scanImages(List<String> ids, LoginUser loginUser) {
        ids.forEach(id -> {
            try {
                scan(id, loginUser);
            } catch (Exception e) {
                throw new HRException(e.getMessage());
            }
        });
    }

}
