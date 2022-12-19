package com.hummerrisk.service;

import com.alibaba.fastjson.JSONArray;
import com.hummer.quartz.anno.QuartzScheduled;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.*;
import com.hummerrisk.base.mapper.ext.ExtCloudTaskMapper;
import com.hummerrisk.base.mapper.ext.ExtNoticeMapper;
import com.hummerrisk.commons.constants.CloudTaskConstants;
import com.hummerrisk.commons.constants.NoticeConstants;
import com.hummerrisk.commons.constants.ScanConstants;
import com.hummerrisk.commons.utils.BeanUtils;
import com.hummerrisk.commons.utils.CommonBeanFactory;
import com.hummerrisk.commons.utils.CommonThreadPool;
import com.hummerrisk.commons.utils.LogUtil;
import com.hummerrisk.dto.MetricChartDTO;
import com.hummerrisk.dto.ServerDTO;
import com.hummerrisk.dto.ServerResultDTO;
import com.hummerrisk.i18n.Translator;
import com.hummerrisk.message.NoticeModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.alibaba.fastjson.JSON.toJSONString;

/**
 * @author harris
 */
@Service
public class NoticeCreateService {
    // 只有一个任务在处理，防止超配
    private static ConcurrentHashMap<String, String> processingGroupIdMap = new ConcurrentHashMap<>();
    @Resource
    private CloudTaskMapper cloudTaskMapper;
    @Resource
    private CommonThreadPool commonThreadPool;
    @Resource
    private MessageOrderMapper messageOrderMapper;
    @Resource
    private MessageOrderItemMapper messageOrderItemMapper;
    @Resource
    private ExtCloudTaskMapper extCloudTaskMapper;
    @Resource
    private WebMsgMapper webMsgMapper;
    @Resource
    private ServerResultMapper serverResultMapper;
    @Resource
    private CodeResultMapper codeResultMapper;
    @Resource
    private ImageResultMapper imageResultMapper;
    @Resource
    private CloudNativeResultMapper cloudNativeResultMapper;
    @Resource
    private CloudNativeConfigResultMapper cloudNativeConfigResultMapper;
    @Resource
    private ExtNoticeMapper extNoticeMapper;
    @Resource
    private FileSystemResultMapper fileSystemResultMapper;
    @Resource
    private WebhookMapper webhookMapper;
    @Resource
    private AccountMapper accountMapper;
    @Resource
    private ServerMapper serverMapper;
    @Resource
    private CloudNativeMapper cloudNativeMapper;
    @Resource
    private CloudNativeConfigMapper cloudNativeConfigMapper;
    @Resource
    private ImageMapper imageMapper;
    @Resource
    private CodeMapper codeMapper;
    @Resource
    private FileSystemMapper fileSystemMapper;

    @QuartzScheduled(cron = "${cron.expression.notice}")
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
            if (!messageOrderItemList.isEmpty() && successCount == 0)
                throw new Exception("Faild to handle all messageOrderItemList, messageOrderId: " + messageOrderId);

            if (successCount != messageOrderItemList.size()) {
                return;
            }

            String status = NoticeConstants.MessageOrderStatus.FINISHED;
            messageOrder.setStatus(status);
            messageOrder.setSendTime(System.currentTimeMillis());
            messageOrderMapper.updateByPrimaryKeySelective(messageOrder);

            LogUtil.debug(Translator.get("i18n_start_messageorder") + messageOrder.getAccountName());
            sendTask(messageOrder);
            LogUtil.debug(Translator.get("i18n_end_messageorder") + messageOrder.getAccountName());

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
                CloudTask cloudTask = cloudTaskMapper.selectByPrimaryKey(item.getTaskId());
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
                ServerResult serverResult = serverResultMapper.selectByPrimaryKey(item.getTaskId());
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
                ImageResult imageResult = imageResultMapper.selectByPrimaryKey(item.getTaskId());
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
                CloudNativeResult cloudNativeResult = cloudNativeResultMapper.selectByPrimaryKey(item.getTaskId());
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
                CloudNativeConfigResult cloudNativeConfigResult = cloudNativeConfigResultMapper.selectByPrimaryKey(item.getTaskId());
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
                CodeResult codeResult = codeResultMapper.selectByPrimaryKey(item.getTaskId());
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
                FileSystemResult fileSystemResult = fileSystemResultMapper.selectByPrimaryKey(item.getTaskId());
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
            subject = Translator.get("i18n_cloud_messageorder");
            List<CloudTask> cloudTasks = extCloudTaskMapper.getTopTasksForEmail(messageOrder);
            MetricChartDTO metricChartDTO = extNoticeMapper.metricChartCloud(messageOrder);
            for (CloudTask cloudTask : cloudTasks) {
                if (cloudTask.getReturnSum() == null) {
                    sendTask(messageOrder);
                    return;
                }
            }

            returnSum = extCloudTaskMapper.getReturnSumForEmail(messageOrder);
            resourcesSum = extCloudTaskMapper.getResourcesSumForEmail(messageOrder);
            details = "i18n_cloud_messageorder_sum" + returnSum + "/" + resourcesSum;
            name = accountMapper.selectByPrimaryKey(cloudTasks.get(0).getAccountId()).getName();
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

        } else if (StringUtils.equals(ScanConstants.SCAN_TYPE.VULN.name(), messageOrder.getScanType())) {
            subject = Translator.get("i18n_vuln_messageorder");

            List<CloudTask> cloudTasks = extCloudTaskMapper.getTopTasksForEmail(messageOrder);
            MetricChartDTO metricChartDTO = extNoticeMapper.metricChartVuln(messageOrder);

            for (CloudTask cloudTask : cloudTasks) {
                if (cloudTask.getReturnSum() == null) {
                    sendTask(messageOrder);
                    return;
                }
            }

            returnSum = extCloudTaskMapper.getReturnSumForEmail(messageOrder);
            resourcesSum = extCloudTaskMapper.getResourcesSumForEmail(messageOrder);
            details = "i18n_cloud_messageorder_sum" + returnSum + "/" + resourcesSum;
            name = accountMapper.selectByPrimaryKey(cloudTasks.get(0).getAccountId()).getName();
            String event = NoticeConstants.Event.EXECUTE_VULN;

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

        } else if (StringUtils.equals(ScanConstants.SCAN_TYPE.SERVER.name(), messageOrder.getScanType())) {
            List<ServerResultDTO> serverResults = extNoticeMapper.getTopServerTasksForEmail(messageOrder);
            MetricChartDTO metricChartDTO = extNoticeMapper.metricChartServer(messageOrder);

            subject = Translator.get("i18n_server_messageorder");
            returnSum = extNoticeMapper.serverSum(messageOrder);
            details = "i18n_resource_manage " + returnSum;
            Server server = serverMapper.selectByPrimaryKey(serverResults.get(0).getServerId());
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
        } else if (StringUtils.equals(ScanConstants.SCAN_TYPE.IMAGE.name(), messageOrder.getScanType())) {
            List<ImageResultItem> imageResultItems = extNoticeMapper.getTopImageTasksForEmail(messageOrder);
            MetricChartDTO metricChartDTO = extNoticeMapper.metricChartImage(messageOrder);
            ImageResultExample example = new ImageResultExample();
            example.createCriteria().andIdEqualTo(imageResultItems.get(0).getResultId());
            List<ImageResult> imageResults = imageResultMapper.selectByExample(example);

            subject = Translator.get("i18n_image_messageorder");
            returnSum = extNoticeMapper.imageSum(messageOrder);
            details = "i18n_resource_manage " + returnSum;
            name = imageMapper.selectByPrimaryKey(imageResults.get(0).getImageId()).getName();
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
        } else if (StringUtils.equals(ScanConstants.SCAN_TYPE.CODE.name(), messageOrder.getScanType())) {
            List<CodeResultItem> codeResultItems = extNoticeMapper.getTopCodeTasksForEmail(messageOrder);
            MetricChartDTO metricChartDTO = extNoticeMapper.metricChartCode(messageOrder);
            CodeResultExample example = new CodeResultExample();
            example.createCriteria().andIdEqualTo(codeResultItems.get(0).getResultId());
            List<CodeResult> codeResults = codeResultMapper.selectByExample(example);

            subject = Translator.get("i18n_code_messageorder");
            returnSum = extNoticeMapper.codeSum(messageOrder);
            details = "i18n_resource_manage " + returnSum;
            name = codeMapper.selectByPrimaryKey(codeResults.get(0).getCodeId()).getName();
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
        } else if (StringUtils.equals(ScanConstants.SCAN_TYPE.CONFIG.name(), messageOrder.getScanType())) {
            List<CloudNativeConfigResultItem> configResultItems = extNoticeMapper.getTopConfigTasksForEmail(messageOrder);
            MetricChartDTO metricChartDTO = extNoticeMapper.metricChartConfig(messageOrder);
            CloudNativeConfigResultExample example = new CloudNativeConfigResultExample();
            example.createCriteria().andIdEqualTo(configResultItems.get(0).getResultId());
            List<CloudNativeConfigResult> cloudNativeConfigResults = cloudNativeConfigResultMapper.selectByExample(example);


            subject = Translator.get("i18n_config_messageorder");
            returnSum = extNoticeMapper.configSum(messageOrder);
            details = "i18n_resource_manage " + returnSum;
            name = cloudNativeConfigMapper.selectByPrimaryKey(cloudNativeConfigResults.get(0).getConfigId()).getName();
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
        } else if (StringUtils.equals(ScanConstants.SCAN_TYPE.K8S.name(), messageOrder.getScanType())) {
            List<CloudNativeResultItem> k8sResultItems = extNoticeMapper.getTopK8sTasksForEmail(messageOrder);
            MetricChartDTO metricChartDTO = extNoticeMapper.metricChartK8s(messageOrder);
            CloudNativeResultExample example = new CloudNativeResultExample();
            example.createCriteria().andIdEqualTo(k8sResultItems.get(0).getResultId());
            List<CloudNativeResult> cloudNativeResults = cloudNativeResultMapper.selectByExample(example);

            subject = Translator.get("i18n_k8s_messageorder");
            returnSum = extNoticeMapper.k8sSum(messageOrder);
            details = "i18n_resource_manage " + returnSum;
            name = cloudNativeMapper.selectByPrimaryKey(cloudNativeResults.get(0).getCloudNativeId()).getName();
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
        } else if (StringUtils.equals(ScanConstants.SCAN_TYPE.FS.name(), messageOrder.getScanType())) {
            List<FileSystemResultItem> fsResultItems = extNoticeMapper.getTopFsTasksForEmail(messageOrder);
            MetricChartDTO metricChartDTO = extNoticeMapper.metricChartFs(messageOrder);
            FileSystemResultExample example = new FileSystemResultExample();
            example.createCriteria().andIdEqualTo(fsResultItems.get(0).getResultId());
            List<FileSystemResult> fileSystemResults = fileSystemResultMapper.selectByExample(example);

            subject = Translator.get("i18n_fs_messageorder");
            returnSum = extNoticeMapper.fsSum(messageOrder);
            details = "i18n_resource_manage " + returnSum;
            name = fileSystemMapper.selectByPrimaryKey(fileSystemResults.get(0).getFsId()).getName();
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


        LogUtil.debug(Translator.get("i18n_start_msg") + messageOrder.getAccountName());
        WebMsg msg = new WebMsg();
        MessageOrderItemExample example = new MessageOrderItemExample();
        example.createCriteria().andMessageOrderIdEqualTo(messageOrder.getId());
        List<MessageOrderItem> list = messageOrderItemMapper.selectByExample(example);
        if(list.size() > 0) {
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
        LogUtil.debug(Translator.get("i18n_end_msg") + messageOrder.getAccountName());
    }
}
