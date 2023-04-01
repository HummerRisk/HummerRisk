package com.hummer.system.service;

import com.alibaba.fastjson.JSONArray;
import com.hummer.cloud.api.ICloudProviderService;
import com.hummer.common.core.constant.CloudTaskConstants;
import com.hummer.common.core.constant.NoticeConstants;
import com.hummer.common.core.constant.ScanConstants;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.dto.MetricChartDTO;
import com.hummer.common.core.dto.ServerResultDTO;
import com.hummer.common.core.utils.BeanUtils;
import com.hummer.common.core.utils.CommonBeanFactory;
import com.hummer.common.core.utils.LogUtil;
import com.hummer.k8s.api.IK8sProviderService;
import com.hummer.common.core.i18n.Translator;
import com.hummer.system.mapper.MessageOrderItemMapper;
import com.hummer.system.mapper.MessageOrderMapper;
import com.hummer.system.mapper.WebMsgMapper;
import com.hummer.system.mapper.WebhookMapper;
import com.hummer.system.mapper.ext.ExtNoticeMapper;
import com.hummer.system.message.NoticeModel;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.alibaba.fastjson2.JSON.toJSONString;

/**
 * @author harris
 */
@Service
public class NoticeCreateService {
    // 只有一个任务在处理，防止超配
    private static ConcurrentHashMap<String, String> processingGroupIdMap = new ConcurrentHashMap<>();

    @Autowired
    private CommonThreadPool commonThreadPool;
    @Autowired
    private MessageOrderMapper messageOrderMapper;
    @Autowired
    private MessageOrderItemMapper messageOrderItemMapper;
    @Autowired
    private WebMsgMapper webMsgMapper;
    @Autowired
    private ExtNoticeMapper extNoticeMapper;
    @Autowired
    private WebhookMapper webhookMapper;
    @DubboReference
    private ICloudProviderService cloudProviderService;
    @DubboReference
    private IK8sProviderService k8sProviderService;

    //发送消息通知
    @XxlJob("noticeTasksJobHandler")
    public void handleTasks() {

        final MessageOrderExample messageOrderExample = new MessageOrderExample();
        MessageOrderExample.Criteria criteria = messageOrderExample.createCriteria();
        criteria.andStatusEqualTo(NoticeConstants.MessageOrderStatus.PROCESSING);
        if (CollectionUtils.isNotEmpty(processingGroupIdMap.keySet())) {
            criteria.andIdNotIn(new ArrayList<>(processingGroupIdMap.keySet()));
        }
        messageOrderExample.setOrderByClause("create_time limit 10");
        List<MessageOrder> messageOrderList = messageOrderMapper.selectByExample(messageOrderExample);
        if (CollectionUtils.isNotEmpty(messageOrderList)) {
            messageOrderList.forEach(messageOrder -> {
                LogUtil.info("handling messageOrder: {}", toJSONString(messageOrder));
                final MessageOrder messageOrderToBeProceed;
                try {
                    messageOrderToBeProceed = BeanUtils.copyBean(new MessageOrder(), messageOrder);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
                if (processingGroupIdMap.get(messageOrderToBeProceed.getId()) != null) {
                    return;
                }
                processingGroupIdMap.put(messageOrderToBeProceed.getId(), messageOrderToBeProceed.getId());
                commonThreadPool.addTask(() -> {
                    try {
                        handleMessageOrder(messageOrderToBeProceed);
                    } catch (Exception e) {
                        LogUtil.error(e.getMessage());
                    } finally {
                        processingGroupIdMap.remove(messageOrderToBeProceed.getId());
                    }
                });
            });
        }

    }

    public void handleMessageOrder(MessageOrder messageOrder) {
        String messageOrderId = messageOrder.getId();
        MessageOrderItemExample messageOrderItemExample = new MessageOrderItemExample();
        messageOrderItemExample.createCriteria().andMessageOrderIdEqualTo(messageOrderId);

        try {
            List<MessageOrderItem> messageOrderItemList = messageOrderItemMapper.selectByExample(messageOrderItemExample);
            if (messageOrderItemList.isEmpty()) {
                messageOrderMapper.deleteByPrimaryKey(messageOrder.getId());
                return;
            }
            int successCount = 0;
            for (MessageOrderItem item : messageOrderItemList) {
                if (LogUtil.getLogger().isDebugEnabled()) {
                    LogUtil.getLogger().debug("handling MessageOrderItem: {}", toJSONString(item));
                }
                if (handleMessageOrderItem(BeanUtils.copyBean(new MessageOrderItem(), item))) {
                    successCount++;
                }
            }
            if (!messageOrderItemList.isEmpty() && successCount == 0) {
                LogUtil.error("Faild to handle all messageOrderItemList, messageOrderId: " + messageOrderId);
                return;
            }

            if (successCount != messageOrderItemList.size()) {
                return;
            }

            String status = NoticeConstants.MessageOrderStatus.FINISHED;
            messageOrder.setStatus(status);
            messageOrder.setSendTime(System.currentTimeMillis());
            messageOrderMapper.updateByPrimaryKeySelective(messageOrder);

            LogUtil.warn(Translator.get("i18n_start_messageorder") + messageOrder.getAccountName());
            sendTask(messageOrder);
            LogUtil.warn(Translator.get("i18n_end_messageorder") + messageOrder.getAccountName());

        } catch (Exception e) {
            messageOrder.setStatus(NoticeConstants.MessageOrderStatus.ERROR);
            messageOrder.setSendTime(System.currentTimeMillis());
            sendTask(messageOrder);
            messageOrderMapper.updateByPrimaryKeySelective(messageOrder);
            LogUtil.error("handleTask, taskId: " + messageOrderId, e);

        }
    }

    private boolean handleMessageOrderItem(MessageOrderItem item) {
        try {
            MessageOrder messageOrder = messageOrderMapper.selectByPrimaryKey(item.getMessageOrderId());
            String scanType = messageOrder.getScanType();

            if (StringUtils.equals(ScanConstants.SCAN_TYPE.CLOUD.name(), scanType) || StringUtils.equals(ScanConstants.SCAN_TYPE.VULN.name(), scanType)) {
                CloudTask cloudTask = cloudProviderService.selectCloudTask(item.getTaskId());
                if (cloudTask == null) {
                    item.setStatus(NoticeConstants.MessageOrderStatus.ERROR);
                    item.setSendTime(System.currentTimeMillis());
                    messageOrderItemMapper.updateByPrimaryKeySelective(item);
                    return false;
                } else {
                    if (StringUtils.equalsIgnoreCase(cloudTask.getStatus(), CloudTaskConstants.TASK_STATUS.FINISHED.name())
                            || StringUtils.equalsIgnoreCase(cloudTask.getStatus(), CloudTaskConstants.TASK_STATUS.WARNING.name())
                            || StringUtils.equalsIgnoreCase(cloudTask.getStatus(), CloudTaskConstants.TASK_STATUS.ERROR.name())) {
                        item.setStatus(NoticeConstants.MessageOrderStatus.FINISHED);
                        item.setSendTime(System.currentTimeMillis());
                        messageOrderItemMapper.updateByPrimaryKeySelective(item);
                    } else {
                        handleMessageOrderItem(item);
                    }
                }
            } else if (StringUtils.equals(ScanConstants.SCAN_TYPE.SERVER.name(), scanType)) {
                ServerResult serverResult = k8sProviderService.serverResult(item.getTaskId());
                if (serverResult == null) {
                    item.setStatus(NoticeConstants.MessageOrderStatus.ERROR);
                    item.setSendTime(System.currentTimeMillis());
                    messageOrderItemMapper.updateByPrimaryKeySelective(item);
                    return false;
                }
                if (StringUtils.equalsIgnoreCase(serverResult.getResultStatus(), CloudTaskConstants.TASK_STATUS.FINISHED.name())
                        || StringUtils.equalsIgnoreCase(serverResult.getResultStatus(), CloudTaskConstants.TASK_STATUS.WARNING.name())
                        || StringUtils.equalsIgnoreCase(serverResult.getResultStatus(), CloudTaskConstants.TASK_STATUS.ERROR.name())) {
                    item.setStatus(NoticeConstants.MessageOrderStatus.FINISHED);
                    item.setSendTime(System.currentTimeMillis());
                    messageOrderItemMapper.updateByPrimaryKeySelective(item);
                } else {
                    handleMessageOrderItem(item);
                }
            } else if (StringUtils.equals(ScanConstants.SCAN_TYPE.IMAGE.name(), scanType)) {
                ImageResult imageResult = k8sProviderService.imageResult(item.getTaskId());
                if (imageResult == null) {
                    item.setStatus(NoticeConstants.MessageOrderStatus.ERROR);
                    item.setSendTime(System.currentTimeMillis());
                    messageOrderItemMapper.updateByPrimaryKeySelective(item);
                    return false;
                }
                if (StringUtils.equalsIgnoreCase(imageResult.getResultStatus(), CloudTaskConstants.TASK_STATUS.FINISHED.name())
                        || StringUtils.equalsIgnoreCase(imageResult.getResultStatus(), CloudTaskConstants.TASK_STATUS.WARNING.name())
                        || StringUtils.equalsIgnoreCase(imageResult.getResultStatus(), CloudTaskConstants.TASK_STATUS.ERROR.name())) {
                    item.setStatus(NoticeConstants.MessageOrderStatus.FINISHED);
                    item.setSendTime(System.currentTimeMillis());
                    messageOrderItemMapper.updateByPrimaryKeySelective(item);
                } else {
                    handleMessageOrderItem(item);
                }
            } else if (StringUtils.equals(ScanConstants.SCAN_TYPE.K8S.name(), scanType)) {
                CloudNativeResult cloudNativeResult = k8sProviderService.cloudNativeResult(item.getTaskId());
                if (cloudNativeResult == null) {
                    item.setStatus(NoticeConstants.MessageOrderStatus.ERROR);
                    item.setSendTime(System.currentTimeMillis());
                    messageOrderItemMapper.updateByPrimaryKeySelective(item);
                    return false;
                }
                if (StringUtils.equalsIgnoreCase(cloudNativeResult.getResultStatus(), CloudTaskConstants.TASK_STATUS.FINISHED.name())
                        || StringUtils.equalsIgnoreCase(cloudNativeResult.getResultStatus(), CloudTaskConstants.TASK_STATUS.WARNING.name())
                        || StringUtils.equalsIgnoreCase(cloudNativeResult.getResultStatus(), CloudTaskConstants.TASK_STATUS.ERROR.name())) {
                    item.setStatus(NoticeConstants.MessageOrderStatus.FINISHED);
                    item.setSendTime(System.currentTimeMillis());
                    messageOrderItemMapper.updateByPrimaryKeySelective(item);
                } else {
                    handleMessageOrderItem(item);
                }
            } else if (StringUtils.equals(ScanConstants.SCAN_TYPE.CONFIG.name(), scanType)) {
                CloudNativeConfigResult cloudNativeConfigResult = k8sProviderService.cloudNativeConfigResult(item.getTaskId());
                if (cloudNativeConfigResult == null) {
                    item.setStatus(NoticeConstants.MessageOrderStatus.ERROR);
                    item.setSendTime(System.currentTimeMillis());
                    messageOrderItemMapper.updateByPrimaryKeySelective(item);
                    return false;
                }
                if (StringUtils.equalsIgnoreCase(cloudNativeConfigResult.getResultStatus(), CloudTaskConstants.TASK_STATUS.FINISHED.name())
                        || StringUtils.equalsIgnoreCase(cloudNativeConfigResult.getResultStatus(), CloudTaskConstants.TASK_STATUS.WARNING.name())
                        || StringUtils.equalsIgnoreCase(cloudNativeConfigResult.getResultStatus(), CloudTaskConstants.TASK_STATUS.ERROR.name())) {
                    item.setStatus(NoticeConstants.MessageOrderStatus.FINISHED);
                    item.setSendTime(System.currentTimeMillis());
                    messageOrderItemMapper.updateByPrimaryKeySelective(item);
                } else {
                    handleMessageOrderItem(item);
                }
            } else if (StringUtils.equals(ScanConstants.SCAN_TYPE.CODE.name(), scanType)) {
                CodeResult codeResult = k8sProviderService.codeResult(item.getTaskId());
                if (codeResult == null) {
                    item.setStatus(NoticeConstants.MessageOrderStatus.ERROR);
                    item.setSendTime(System.currentTimeMillis());
                    messageOrderItemMapper.updateByPrimaryKeySelective(item);
                    return false;
                }
                if (StringUtils.equalsIgnoreCase(codeResult.getResultStatus(), CloudTaskConstants.TASK_STATUS.FINISHED.name())
                        || StringUtils.equalsIgnoreCase(codeResult.getResultStatus(), CloudTaskConstants.TASK_STATUS.WARNING.name())
                        || StringUtils.equalsIgnoreCase(codeResult.getResultStatus(), CloudTaskConstants.TASK_STATUS.ERROR.name())) {
                    item.setStatus(NoticeConstants.MessageOrderStatus.FINISHED);
                    item.setSendTime(System.currentTimeMillis());
                    messageOrderItemMapper.updateByPrimaryKeySelective(item);
                } else {
                    handleMessageOrderItem(item);
                }
            } else if (StringUtils.equals(ScanConstants.SCAN_TYPE.FS.name(), scanType)) {
                FileSystemResult fileSystemResult = k8sProviderService.fileSystemResult(item.getTaskId());
                if (fileSystemResult == null) {
                    item.setStatus(NoticeConstants.MessageOrderStatus.ERROR);
                    item.setSendTime(System.currentTimeMillis());
                    messageOrderItemMapper.updateByPrimaryKeySelective(item);
                    return false;
                }
                if (StringUtils.equalsIgnoreCase(fileSystemResult.getResultStatus(), CloudTaskConstants.TASK_STATUS.FINISHED.name())
                        || StringUtils.equalsIgnoreCase(fileSystemResult.getResultStatus(), CloudTaskConstants.TASK_STATUS.WARNING.name())
                        || StringUtils.equalsIgnoreCase(fileSystemResult.getResultStatus(), CloudTaskConstants.TASK_STATUS.ERROR.name())) {
                    item.setStatus(NoticeConstants.MessageOrderStatus.FINISHED);
                    item.setSendTime(System.currentTimeMillis());
                    messageOrderItemMapper.updateByPrimaryKeySelective(item);
                } else {
                    handleMessageOrderItem(item);
                }
            }

            return true;
        } catch (Exception e) {
            item.setStatus(NoticeConstants.MessageOrderStatus.ERROR);
            messageOrderItemMapper.updateByPrimaryKeySelective(item);
            LogUtil.error("handleMessageOrderItem, messageOrderItemId: " + item.getId(), e);
            return false;
        }
    }

    private void sendTask(MessageOrder messageOrder) {
        SystemParameterService systemParameterService = CommonBeanFactory.getBean(SystemParameterService.class);
        NoticeSendService noticeSendService = CommonBeanFactory.getBean(NoticeSendService.class);

        WebhookExample webhookExample = new WebhookExample();
        webhookExample.createCriteria().andStatusEqualTo(true);
        List<Webhook> webhooks = webhookMapper.selectByExample(webhookExample);
        List<String> webhookUrls = webhooks.stream().map(Webhook::getWebhook).collect(Collectors.toList());

        assert systemParameterService != null;
        assert noticeSendService != null;

        String successContext = "success";
        String failedContext = "failed";
        String subject = "i18n_cloud_messageorder";
        String details = "", name = "";
        int returnSum = 0, resourcesSum = 0;

        if (StringUtils.equals(ScanConstants.SCAN_TYPE.CLOUD.name(), messageOrder.getScanType())) {
            subject = "i18n_cloud_messageorder";
            List<CloudTask> cloudTasks = cloudProviderService.getTopTasksForEmail(messageOrder);
            if (cloudTasks.size() != 0) {
                MetricChartDTO metricChartDTO = extNoticeMapper.metricChartCloud(messageOrder);
                for (CloudTask cloudTask : cloudTasks) {
                    if (cloudTask.getReturnSum() == null) {
                        sendTask(messageOrder);
                        return;
                    }
                }

                returnSum = cloudProviderService.getReturnSumForEmail(messageOrder);
                resourcesSum = cloudProviderService.getResourcesSumForEmail(messageOrder);
                details = "i18n_cloud_messageorder_sum" + returnSum + "/" + resourcesSum;
                name = cloudProviderService.selectAccount(cloudTasks.get(0).getAccountId()).getName();
                String event = NoticeConstants.Event.EXECUTE_CLOUD;

                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("resources", cloudTasks);
                paramMap.put("returnSum", returnSum);
                paramMap.put("resourcesSum", resourcesSum);
                paramMap.put("name", name);
                paramMap.put("critical", metricChartDTO.getCritical());
                paramMap.put("high", metricChartDTO.getHigh());
                paramMap.put("medium", metricChartDTO.getMedium());
                paramMap.put("low", metricChartDTO.getLow());
                NoticeModel noticeModel = NoticeModel.builder()
                        .successContext(successContext)
                        .successMailTemplate("SuccessfulNotification")
                        .failedContext(failedContext)
                        .failedMailTemplate("FailedNotification")
                        .event(event)
                        .subject(subject)
                        .paramMap(paramMap)
                        .webhookUrls(webhookUrls)
                        .build();
                noticeSendService.send(noticeModel);

            }
        } else if (StringUtils.equals(ScanConstants.SCAN_TYPE.SERVER.name(), messageOrder.getScanType())) {
            List<ServerResultDTO> serverResults = extNoticeMapper.getTopServerTasksForEmail(messageOrder);
            if (serverResults.size() != 0) {
                MetricChartDTO metricChartDTO = extNoticeMapper.metricChartServer(messageOrder);

                subject = "i18n_server_messageorder";
                returnSum = extNoticeMapper.serverSum(messageOrder);
                details = "i18n_resource_manage " + returnSum;
                Server server = k8sProviderService.server(serverResults.get(0).getServerId());
                name = server.getName() + "(" + server.getIp() + ":" + server.getPort() + ")";
                String event = NoticeConstants.Event.EXECUTE_SERVER;

                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("resources", serverResults);
                paramMap.put("returnSum", returnSum);
                paramMap.put("name", name);
                paramMap.put("critical", metricChartDTO.getCritical());
                paramMap.put("high", metricChartDTO.getHigh());
                paramMap.put("medium", metricChartDTO.getMedium());
                paramMap.put("low", metricChartDTO.getLow());
                NoticeModel noticeModel = NoticeModel.builder()
                        .successContext(successContext)
                        .successMailTemplate("SuccessfulNotification")
                        .failedContext(failedContext)
                        .failedMailTemplate("FailedNotification")
                        .event(event)
                        .subject(subject)
                        .paramMap(paramMap)
                        .webhookUrls(webhookUrls)
                        .build();
                noticeSendService.send(noticeModel);
            }
        } else if (StringUtils.equals(ScanConstants.SCAN_TYPE.IMAGE.name(), messageOrder.getScanType())) {
            List<ImageResultItem> imageResultItems = extNoticeMapper.getTopImageTasksForEmail(messageOrder);
            if (imageResultItems.size() != 0) {
                MetricChartDTO metricChartDTO = extNoticeMapper.metricChartImage(messageOrder);
                ImageResultExample example = new ImageResultExample();
                example.createCriteria().andIdEqualTo(imageResultItems.get(0).getResultId());
                List<ImageResult> imageResults = k8sProviderService.imageResults(example);
                if (imageResults.size() != 0) {
                    subject = "i18n_image_messageorder";
                    returnSum = extNoticeMapper.imageSum(messageOrder);
                    details = "i18n_resource_manage " + returnSum;
                    name = k8sProviderService.image(imageResults.get(0).getImageId()).getName();
                    String event = NoticeConstants.Event.EXECUTE_IMAGE;

                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("resources", imageResultItems);
                    paramMap.put("returnSum", returnSum);
                    paramMap.put("name", name);
                    paramMap.put("critical", metricChartDTO.getCritical());
                    paramMap.put("high", metricChartDTO.getHigh());
                    paramMap.put("medium", metricChartDTO.getMedium());
                    paramMap.put("low", metricChartDTO.getLow());
                    paramMap.put("unknown", metricChartDTO.getUnknown());
                    NoticeModel noticeModel = NoticeModel.builder()
                            .successContext(successContext)
                            .successMailTemplate("SuccessfulNotification")
                            .failedContext(failedContext)
                            .failedMailTemplate("FailedNotification")
                            .event(event)
                            .subject(subject)
                            .paramMap(paramMap)
                            .webhookUrls(webhookUrls)
                            .build();
                    noticeSendService.send(noticeModel);
                }
            }
        } else if (StringUtils.equals(ScanConstants.SCAN_TYPE.CODE.name(), messageOrder.getScanType())) {
            List<CodeResultItem> codeResultItems = extNoticeMapper.getTopCodeTasksForEmail(messageOrder);
            if (codeResultItems.size() != 0) {
                MetricChartDTO metricChartDTO = extNoticeMapper.metricChartCode(messageOrder);
                CodeResultExample example = new CodeResultExample();
                example.createCriteria().andIdEqualTo(codeResultItems.get(0).getResultId());
                List<CodeResult> codeResults = k8sProviderService.codeResults(example);

                if (codeResults.size() != 0) {
                    subject = "i18n_code_messageorder";
                    returnSum = extNoticeMapper.codeSum(messageOrder);
                    details = "i18n_resource_manage " + returnSum;
                    name = k8sProviderService.code(codeResults.get(0).getCodeId()).getName();
                    String event = NoticeConstants.Event.EXECUTE_CODE;

                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("resources", codeResultItems);
                    paramMap.put("returnSum", returnSum);
                    paramMap.put("name", name);
                    paramMap.put("critical", metricChartDTO.getCritical());
                    paramMap.put("high", metricChartDTO.getHigh());
                    paramMap.put("medium", metricChartDTO.getMedium());
                    paramMap.put("low", metricChartDTO.getLow());
                    paramMap.put("unknown", metricChartDTO.getUnknown());
                    NoticeModel noticeModel = NoticeModel.builder()
                            .successContext(successContext)
                            .successMailTemplate("SuccessfulNotification")
                            .failedContext(failedContext)
                            .failedMailTemplate("FailedNotification")
                            .event(event)
                            .subject(subject)
                            .paramMap(paramMap)
                            .webhookUrls(webhookUrls)
                            .build();
                    noticeSendService.send(noticeModel);
                }

            }
        } else if (StringUtils.equals(ScanConstants.SCAN_TYPE.CONFIG.name(), messageOrder.getScanType())) {
            List<CloudNativeConfigResultItem> configResultItems = extNoticeMapper.getTopConfigTasksForEmail(messageOrder);
            if (configResultItems.size() != 0) {
                MetricChartDTO metricChartDTO = extNoticeMapper.metricChartConfig(messageOrder);
                CloudNativeConfigResultExample example = new CloudNativeConfigResultExample();
                example.createCriteria().andIdEqualTo(configResultItems.get(0).getResultId());
                List<CloudNativeConfigResult> cloudNativeConfigResults = k8sProviderService.cloudNativeConfigResults(example);

                if (cloudNativeConfigResults.size() != 0) {
                    subject = "i18n_config_messageorder";
                    returnSum = extNoticeMapper.configSum(messageOrder);
                    details = "i18n_resource_manage " + returnSum;
                    name = k8sProviderService.cloudNativeConfig(cloudNativeConfigResults.get(0).getConfigId()).getName();
                    String event = NoticeConstants.Event.EXECUTE_CONFIG;

                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("resources", configResultItems);
                    paramMap.put("returnSum", returnSum);
                    paramMap.put("name", name);
                    paramMap.put("critical", metricChartDTO.getCritical());
                    paramMap.put("high", metricChartDTO.getHigh());
                    paramMap.put("medium", metricChartDTO.getMedium());
                    paramMap.put("low", metricChartDTO.getLow());
                    paramMap.put("unknown", metricChartDTO.getUnknown());
                    NoticeModel noticeModel = NoticeModel.builder()
                            .successContext(successContext)
                            .successMailTemplate("SuccessfulNotification")
                            .failedContext(failedContext)
                            .failedMailTemplate("FailedNotification")
                            .event(event)
                            .subject(subject)
                            .paramMap(paramMap)
                            .webhookUrls(webhookUrls)
                            .build();
                    noticeSendService.send(noticeModel);
                }

            }
        } else if (StringUtils.equals(ScanConstants.SCAN_TYPE.K8S.name(), messageOrder.getScanType())) {
            List<CloudNativeResultItem> k8sResultItems = extNoticeMapper.getTopK8sTasksForEmail(messageOrder);
            if (k8sResultItems.size() != 0) {
                MetricChartDTO metricChartDTO = extNoticeMapper.metricChartK8s(messageOrder);
                CloudNativeResultExample example = new CloudNativeResultExample();
                example.createCriteria().andIdEqualTo(k8sResultItems.get(0).getResultId());
                List<CloudNativeResult> cloudNativeResults = k8sProviderService.cloudNativeResults(example);
                if (cloudNativeResults.size() != 0) {
                    subject = "i18n_k8s_messageorder";
                    returnSum = extNoticeMapper.k8sSum(messageOrder);
                    details = "i18n_resource_manage " + returnSum;
                    name = k8sProviderService.cloudNative(cloudNativeResults.get(0).getCloudNativeId()).getName();
                    String event = NoticeConstants.Event.EXECUTE_K8S;

                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("resources", k8sResultItems);
                    paramMap.put("returnSum", returnSum);
                    paramMap.put("name", name);
                    paramMap.put("critical", metricChartDTO.getCritical());
                    paramMap.put("high", metricChartDTO.getHigh());
                    paramMap.put("medium", metricChartDTO.getMedium());
                    paramMap.put("low", metricChartDTO.getLow());
                    paramMap.put("unknown", metricChartDTO.getUnknown());
                    NoticeModel noticeModel = NoticeModel.builder()
                            .successContext(successContext)
                            .successMailTemplate("SuccessfulNotification")
                            .failedContext(failedContext)
                            .failedMailTemplate("FailedNotification")
                            .event(event)
                            .subject(subject)
                            .paramMap(paramMap)
                            .webhookUrls(webhookUrls)
                            .build();
                    noticeSendService.send(noticeModel);
                }
            }
        } else if (StringUtils.equals(ScanConstants.SCAN_TYPE.FS.name(), messageOrder.getScanType())) {
            List<FileSystemResultItem> fsResultItems = extNoticeMapper.getTopFsTasksForEmail(messageOrder);
            if (fsResultItems.size() != 0) {
                MetricChartDTO metricChartDTO = extNoticeMapper.metricChartFs(messageOrder);
                FileSystemResultExample example = new FileSystemResultExample();
                example.createCriteria().andIdEqualTo(fsResultItems.get(0).getResultId());
                List<FileSystemResult> fileSystemResults = k8sProviderService.fileSystemResults(example);
                if (fileSystemResults.size() != 0) {
                    subject = "i18n_fs_messageorder";
                    returnSum = extNoticeMapper.fsSum(messageOrder);
                    details = "i18n_resource_manage " + returnSum;
                    name = k8sProviderService.fileSystem(fileSystemResults.get(0).getFsId()).getName();
                    String event = NoticeConstants.Event.EXECUTE_FS;

                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("resources", fsResultItems);
                    paramMap.put("returnSum", returnSum);
                    paramMap.put("name", name);
                    paramMap.put("critical", metricChartDTO.getCritical());
                    paramMap.put("high", metricChartDTO.getHigh());
                    paramMap.put("medium", metricChartDTO.getMedium());
                    paramMap.put("low", metricChartDTO.getLow());
                    paramMap.put("unknown", metricChartDTO.getUnknown());
                    NoticeModel noticeModel = NoticeModel.builder()
                            .successContext(successContext)
                            .successMailTemplate("SuccessfulNotification")
                            .failedContext(failedContext)
                            .failedMailTemplate("FailedNotification")
                            .event(event)
                            .subject(subject)
                            .paramMap(paramMap)
                            .webhookUrls(webhookUrls)
                            .build();
                    noticeSendService.send(noticeModel);
                }
            }

        }

        LogUtil.warn(Translator.get("i18n_start_msg") + messageOrder.getAccountName());
        WebMsg msg = new WebMsg();
        MessageOrderItemExample example = new MessageOrderItemExample();
        example.createCriteria().andMessageOrderIdEqualTo(messageOrder.getId());
        List<MessageOrderItem> list = messageOrderItemMapper.selectByExample(example);
        if (list.size() > 0) {
            JSONArray jsonArray = new JSONArray();
            for (MessageOrderItem item : list) {
                jsonArray.add(item.getTaskId());
            }
            msg.setResultId(jsonArray.toJSONString());
        }
        msg.setStatus(false);
        msg.setType(subject);
        msg.setCreateTime(System.currentTimeMillis());
        msg.setContent(subject + "【" + messageOrder.getAccountName() + "】" + messageOrder.getStatus() + details);
        msg.setScanType(messageOrder.getScanType());
        webMsgMapper.insertSelective(msg);
        LogUtil.warn(Translator.get("i18n_end_msg") + messageOrder.getAccountName());
    }
}
