package com.hummer.system.service;

import com.alibaba.fastjson.JSONArray;
import com.hummer.cloud.api.ICloudProviderService;
import com.hummer.common.core.constant.CloudTaskConstants;
import com.hummer.common.core.constant.NoticeConstants;
import com.hummer.common.core.constant.ScanConstants;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.dto.MetricChartDTO;
import com.hummer.common.core.i18n.Translator;
import com.hummer.common.core.utils.BeanUtils;
import com.hummer.common.core.utils.CommonBeanFactory;
import com.hummer.common.core.utils.LogUtil;
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

    public void handleMessageOrder(MessageOrder messageOrder) throws Exception {
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
                LogUtil.error("Faild to handle all messageOrderItemList(messageOrderItemList.isNotEmpty() && successCount == 0), messageOrderId: " + messageOrderId);
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

            if (StringUtils.equals(ScanConstants.SCAN_TYPE.CLOUD.name(), scanType)) {
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
            }

            return true;
        } catch (Exception e) {
            item.setStatus(NoticeConstants.MessageOrderStatus.ERROR);
            messageOrderItemMapper.updateByPrimaryKeySelective(item);
            LogUtil.error("handleMessageOrderItem, messageOrderItemId: " + item.getId(), e);
            return false;
        }
    }

    private void sendTask(MessageOrder messageOrder) throws Exception {
        try {
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

                } else {
                    return;
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
            msg.setContent(subject + "【" + messageOrder.getAccountName() + "】" + messageOrder.getStatus() + " " + details);
            msg.setScanType(messageOrder.getScanType());
            webMsgMapper.insertSelective(msg);
            LogUtil.warn(Translator.get("i18n_end_msg") + messageOrder.getAccountName());
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
