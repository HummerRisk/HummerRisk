package com.hummer.k8s.service;

import com.hummer.common.core.constant.CloudTaskConstants;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.utils.BeanUtils;
import com.hummer.common.core.utils.LogUtil;
import com.hummer.k8s.mapper.*;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author harris
 */
@Service
public class ResourceCreateService {
    // 只有一个任务在处理，防止超配
    private static ConcurrentHashMap<String, String> processingGroupIdMap = new ConcurrentHashMap<>();
    @Autowired
    private CommonThreadPool commonThreadPool;
    @Autowired
    private ServerResultMapper serverResultMapper;
    @Autowired
    private ServerService serverService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ImageResultMapper imageResultMapper;
    @Autowired
    private CloudNativeResultMapper cloudNativeResultMapper;
    @Autowired
    private CloudNativeConfigResultMapper cloudNativeConfigResultMapper;
    @Autowired
    private K8sService k8sService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private CodeResultMapper codeResultMapper;
    @Autowired
    private CodeService codeService;
    @Autowired
    private FileSystemResultMapper fileSystemResultMapper;
    @Autowired
    private FileSystemService fileSystemService;

    //主机检测
    @XxlJob("serverTasksJobHandler")
    public void serverTasksJobHandler() throws Exception {
        //主机检测
        final ServerResultExample serverResultExample = new ServerResultExample();
        ServerResultExample.Criteria serverCriteria = serverResultExample.createCriteria();
        serverCriteria.andResultStatusEqualTo(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
        if (CollectionUtils.isNotEmpty(processingGroupIdMap.keySet())) {
            serverCriteria.andIdNotIn(new ArrayList<>(processingGroupIdMap.keySet()));
        }
        serverResultExample.setOrderByClause("create_time limit 1");
        List<ServerResult> serverResultList = serverResultMapper.selectByExample(serverResultExample);
        if (CollectionUtils.isNotEmpty(serverResultList)) {
            Set<String> serverIds = new HashSet<>();
            serverResultList.forEach(serverResult -> {
                final ServerResult serverToBeProceed;
                try {
                    serverToBeProceed = BeanUtils.copyBean(new ServerResult(), serverResult);
                    serverIds.add(serverResult.getServerId());
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
                if (processingGroupIdMap.get(serverToBeProceed.getId()) != null) {
                    return;
                }
                processingGroupIdMap.put(serverToBeProceed.getId(), serverToBeProceed.getId());
                commonThreadPool.addTask(() -> {
                    try {
                        serverService.createScan(serverToBeProceed, null);
                    } catch (Exception e) {
                        LogUtil.error(e.getMessage());
                    } finally {
                        processingGroupIdMap.remove(serverToBeProceed.getId());
                    }
                });
            });
            serverIds.forEach(serverId -> {
                if (processingGroupIdMap.get(serverId) != null) {
                    return;
                }
                processingGroupIdMap.put(serverId, serverId);
                commonThreadPool.addTask(() -> {
                    try {
                        serverService.scanLynis(serverId, null);
                    } catch (Exception e) {
                        LogUtil.error(e.getMessage());
                    } finally {
                        processingGroupIdMap.remove(serverId);
                    }
                });
            });
        }
    }

    //K8s检测
    @XxlJob("k8sTasksJobHandler")
    public void k8sTasksJobHandler() throws Exception {
        //K8s检测
        final CloudNativeResultExample cloudNativeResultExample = new CloudNativeResultExample();
        CloudNativeResultExample.Criteria cloudNativeCriteria = cloudNativeResultExample.createCriteria();
        cloudNativeCriteria.andResultStatusEqualTo(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
        if (CollectionUtils.isNotEmpty(processingGroupIdMap.keySet())) {
            cloudNativeCriteria.andIdNotIn(new ArrayList<>(processingGroupIdMap.keySet()));
        }
        cloudNativeResultExample.setOrderByClause("create_time limit 10");
        List<CloudNativeResultWithBLOBs> cloudNativeResults = cloudNativeResultMapper.selectByExampleWithBLOBs(cloudNativeResultExample);
        if (CollectionUtils.isNotEmpty(cloudNativeResults)) {
            cloudNativeResults.forEach(cloudNativeResult -> {
                final CloudNativeResultWithBLOBs cloudNativeToBeProceed;
                try {
                    cloudNativeToBeProceed = BeanUtils.copyBean(new CloudNativeResultWithBLOBs(), cloudNativeResult);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
                if (processingGroupIdMap.get(cloudNativeToBeProceed.getId()) != null) {
                    return;
                }
                processingGroupIdMap.put(cloudNativeToBeProceed.getId(), cloudNativeToBeProceed.getId());
                commonThreadPool.addTask(() -> {
                    try {
                        k8sService.createScan(cloudNativeToBeProceed, null);
                    } catch (Exception e) {
                        LogUtil.error(e.getMessage());
                    } finally {
                        processingGroupIdMap.remove(cloudNativeToBeProceed.getId());
                    }
                });
            });
        }


    }

    //部署检测
    @XxlJob("configTasksJobHandler")
    public void configTasksJobHandler() throws Exception {
        //部署检测
        final CloudNativeConfigResultExample cloudNativeConfigResultExample = new CloudNativeConfigResultExample();
        CloudNativeConfigResultExample.Criteria cloudNativeConfigCriteria = cloudNativeConfigResultExample.createCriteria();
        cloudNativeConfigCriteria.andResultStatusEqualTo(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
        if (CollectionUtils.isNotEmpty(processingGroupIdMap.keySet())) {
            cloudNativeConfigCriteria.andIdNotIn(new ArrayList<>(processingGroupIdMap.keySet()));
        }
        cloudNativeConfigResultExample.setOrderByClause("create_time limit 10");
        List<CloudNativeConfigResult> cloudNativeConfigResults = cloudNativeConfigResultMapper.selectByExampleWithBLOBs(cloudNativeConfigResultExample);
        if (CollectionUtils.isNotEmpty(cloudNativeConfigResults)) {
            cloudNativeConfigResults.forEach(cloudNativeConfigResult -> {
                final CloudNativeConfigResult cloudNativeConfigToBeProceed;
                try {
                    cloudNativeConfigToBeProceed = BeanUtils.copyBean(new CloudNativeConfigResult(), cloudNativeConfigResult);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
                if (processingGroupIdMap.get(cloudNativeConfigToBeProceed.getId()) != null) {
                    return;
                }
                processingGroupIdMap.put(cloudNativeConfigToBeProceed.getId(), cloudNativeConfigToBeProceed.getId());
                commonThreadPool.addTask(() -> {
                    try {
                        configService.createScan(cloudNativeConfigToBeProceed, null);
                    } catch (Exception e) {
                        LogUtil.error(e.getMessage());
                    } finally {
                        processingGroupIdMap.remove(cloudNativeConfigToBeProceed.getId());
                    }
                });
            });
        }
    }

    //源码检测
    @XxlJob("codeTasksJobHandler")
    public void codeTasksJobHandler() throws Exception {
        //源码检测
        final CodeResultExample codeResultExample = new CodeResultExample();
        CodeResultExample.Criteria codeCriteria = codeResultExample.createCriteria();
        codeCriteria.andResultStatusEqualTo(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
        if (CollectionUtils.isNotEmpty(processingGroupIdMap.keySet())) {
            codeCriteria.andIdNotIn(new ArrayList<>(processingGroupIdMap.keySet()));
        }
        codeResultExample.setOrderByClause("create_time limit 10");
        List<CodeResult> codeResults = codeResultMapper.selectByExampleWithBLOBs(codeResultExample);
        if (CollectionUtils.isNotEmpty(codeResults)) {
            codeResults.forEach(codeResult -> {
                final CodeResult codeToBeProceed;
                try {
                    codeToBeProceed = BeanUtils.copyBean(new CodeResult(), codeResult);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
                if (processingGroupIdMap.get(codeToBeProceed.getId()) != null) {
                    return;
                }
                processingGroupIdMap.put(codeToBeProceed.getId(), codeToBeProceed.getId());
                commonThreadPool.addTask(() -> {
                    try {
                        codeService.createScan(codeToBeProceed, null);
                    } catch (Exception e) {
                        LogUtil.error(e.getMessage());
                    } finally {
                        processingGroupIdMap.remove(codeToBeProceed.getId());
                    }
                });
            });
        }
    }

    //文件系统检测
    @XxlJob("fsTasksJobHandler")
    public void fsTasksJobHandler() throws Exception {
        //文件系统检测
        final FileSystemResultExample fileSystemResultExample = new FileSystemResultExample();
        FileSystemResultExample.Criteria fsCriteria = fileSystemResultExample.createCriteria();
        fsCriteria.andResultStatusEqualTo(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
        if (CollectionUtils.isNotEmpty(processingGroupIdMap.keySet())) {
            fsCriteria.andIdNotIn(new ArrayList<>(processingGroupIdMap.keySet()));
        }
        fileSystemResultExample.setOrderByClause("create_time limit 10");
        List<FileSystemResult> fileSystemResults = fileSystemResultMapper.selectByExampleWithBLOBs(fileSystemResultExample);
        if (CollectionUtils.isNotEmpty(fileSystemResults)) {
            fileSystemResults.forEach(fileSystemResult -> {
                final FileSystemResult fsToBeProceed;
                try {
                    fsToBeProceed = BeanUtils.copyBean(new FileSystemResult(), fileSystemResult);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
                if (processingGroupIdMap.get(fsToBeProceed.getId()) != null) {
                    return;
                }
                processingGroupIdMap.put(fsToBeProceed.getId(), fsToBeProceed.getId());
                commonThreadPool.addTask(() -> {
                    try {
                        fileSystemService.createScan(fsToBeProceed, null);
                    } catch (Exception e) {
                        LogUtil.error(e.getMessage());
                    } finally {
                        processingGroupIdMap.remove(fsToBeProceed.getId());
                    }
                });
            });
        }
    }

    //镜像检测
    @XxlJob("imageTasksJobHandler")
    public void imageTasksJobHandler() throws Exception {
        //镜像检测
        final ImageResultExample imageResultExample = new ImageResultExample();
        ImageResultExample.Criteria imageCriteria = imageResultExample.createCriteria();
        imageCriteria.andResultStatusEqualTo(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
        if (CollectionUtils.isNotEmpty(processingGroupIdMap.keySet())) {
            imageCriteria.andIdNotIn(new ArrayList<>(processingGroupIdMap.keySet()));
        }
        imageResultExample.setOrderByClause("create_time limit 10");
        List<ImageResultWithBLOBs> imageResults = imageResultMapper.selectByExampleWithBLOBs(imageResultExample);
        if (CollectionUtils.isNotEmpty(imageResults)) {
            imageResults.forEach(imageResult -> {
                final ImageResultWithBLOBs imageToBeProceed;
                try {
                    imageToBeProceed = BeanUtils.copyBean(new ImageResultWithBLOBs(), imageResult);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
                if (processingGroupIdMap.get(imageToBeProceed.getId()) != null) {
                    return;
                }
                processingGroupIdMap.put(imageToBeProceed.getId(), imageToBeProceed.getId());
                commonThreadPool.addTask(() -> {
                    try {
                        imageService.createScan(imageToBeProceed, null);
                    } catch (Exception e) {
                        LogUtil.error(e.getMessage());
                    } finally {
                        processingGroupIdMap.remove(imageToBeProceed.getId());
                    }
                });
            });
        }
    }

}
