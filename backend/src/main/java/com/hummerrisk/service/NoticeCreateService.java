package com.hummerrisk.service;

import com.hummer.quartz.anno.QuartzScheduled;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.*;
import com.hummerrisk.base.mapper.ext.ExtCloudTaskMapper;
import com.hummerrisk.commons.constants.CloudTaskConstants;
import com.hummerrisk.commons.constants.NoticeConstants;
import com.hummerrisk.commons.constants.ScanConstants;
import com.hummerrisk.commons.utils.BeanUtils;
import com.hummerrisk.commons.utils.CommonBeanFactory;
import com.hummerrisk.commons.utils.CommonThreadPool;
import com.hummerrisk.commons.utils.LogUtil;
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
    private AccountMapper accountMapper;
    @Resource
    private ServerService serverService;
    @Resource
    private ServerResultMapper serverResultMapper;
    @Resource
    private ServerMapper serverMapper;

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
                        LogUtil.error(e);
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
            } else if(StringUtils.equals(ScanConstants.SCAN_TYPE.SERVER.name(), scanType)) {
                ServerResult serverResult = serverResultMapper.selectByPrimaryKey(item.getTaskId());
                if (StringUtils.equalsIgnoreCase(serverResult.getResultStatus(), CloudTaskConstants.TASK_STATUS.FINISHED.name())
                        || StringUtils.equalsIgnoreCase(serverResult.getResultStatus(), CloudTaskConstants.TASK_STATUS.WARNING.name())
                        || StringUtils.equalsIgnoreCase(serverResult.getResultStatus(), CloudTaskConstants.TASK_STATUS.ERROR.name())) {
                    item.setStatus(NoticeConstants.MessageOrderStatus.FINISHED);
                    item.setSendTime(System.currentTimeMillis());
                    messageOrderItemMapper.updateByPrimaryKeySelective(item);
                } else {
                    handleMessageOrderItem(item);
                }
            } else if(StringUtils.equals(ScanConstants.SCAN_TYPE.IMAGE.name(), scanType)) {

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
        assert systemParameterService != null;
        assert noticeSendService != null;

        String successContext = "success";
        String failedContext = "failed";
        String subject = "i18n_cloud_messageorder";
        String event = NoticeConstants.Event.EXECUTE_SUCCESSFUL;

        List<CloudTask> cloudTasks = extCloudTaskMapper.getTopTasksForEmail(messageOrder);

        for (CloudTask cloudTask : cloudTasks) {
            if (cloudTask.getReturnSum() == null) {
                sendTask(messageOrder);
                return;
            }
        }

        int returnSum = 0;
        int resourcesSum = 0;
        String details = "";

        if (StringUtils.equals(ScanConstants.SCAN_TYPE.CLOUD.name(), messageOrder.getScanType())) {
            subject = "i18n_cloud_messageorder";
            returnSum = extCloudTaskMapper.getReturnSumForEmail(messageOrder);
            resourcesSum = extCloudTaskMapper.getResourcesSumForEmail(messageOrder);
            details =  "i18n_cloud_messageorder_sum" + returnSum  + "/" + resourcesSum;
        } else if(StringUtils.equals(ScanConstants.SCAN_TYPE.VULN.name(), messageOrder.getScanType())) {
            subject = "i18n_vuln_messageorder";
            returnSum = extCloudTaskMapper.getReturnSumForEmail(messageOrder);
            resourcesSum = extCloudTaskMapper.getResourcesSumForEmail(messageOrder);
            details =  "i18n_cloud_messageorder_sum" + returnSum  + "/" + resourcesSum;
        } else if(StringUtils.equals(ScanConstants.SCAN_TYPE.SERVER.name(), messageOrder.getScanType())) {
            subject = "i18n_server_messageorder";
        } else if(StringUtils.equals(ScanConstants.SCAN_TYPE.IMAGE.name(), messageOrder.getScanType())) {
            subject = "i18n_image_messageorder";
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("resources", cloudTasks);
        paramMap.put("returnSum", returnSum);
        paramMap.put("resourcesSum", resourcesSum);
        NoticeModel noticeModel = NoticeModel.builder()
                .successContext(successContext)
                .successMailTemplate("SuccessfulNotification")
                .failedContext(failedContext)
                .failedMailTemplate("FailedNotification")
                .event(event)
                .subject(subject)
                .paramMap(paramMap)
                .build();
        noticeSendService.send(noticeModel);

        LogUtil.debug(Translator.get("i18n_start_msg") + messageOrder.getAccountName());
        WebMsg msg = new WebMsg();
        msg.setStatus(false);
        msg.setType(subject);
        msg.setCreateTime(System.currentTimeMillis());
        msg.setContent(subject + "【" + messageOrder.getAccountName() + "】" +  messageOrder.getStatus() + details);
        msg.setScanType(messageOrder.getScanType());
        webMsgMapper.insertSelective(msg);
        LogUtil.debug(Translator.get("i18n_end_msg") + messageOrder.getAccountName());
    }
}
