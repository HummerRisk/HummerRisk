package com.hummerrisk.service;

import com.hummer.quartz.anno.QuartzScheduled;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.MessageOrderMapper;
import com.hummerrisk.base.mapper.WebMsgMapper;
import com.hummerrisk.base.mapper.ext.ExtTaskMapper;
import com.hummerrisk.commons.constants.NoticeConstants;
import com.hummerrisk.commons.constants.TaskConstants;
import com.hummerrisk.commons.utils.BeanUtils;
import com.hummerrisk.commons.utils.CommonBeanFactory;
import com.hummerrisk.commons.utils.CommonThreadPool;
import com.hummerrisk.message.NoticeModel;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.MessageOrderItemMapper;
import com.hummerrisk.base.mapper.TaskMapper;
import com.hummerrisk.commons.utils.LogUtil;
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
    private TaskMapper taskMapper;
    @Resource
    private CommonThreadPool commonThreadPool;
    @Resource
    private MessageOrderMapper messageOrderMapper;
    @Resource
    private MessageOrderItemMapper messageOrderItemMapper;
    @Resource
    private ExtTaskMapper extTaskMapper;
    @Resource
    private WebMsgMapper webMsgMapper;

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

            LogUtil.debug("开始发送通知消息！" + messageOrder.getAccountName());
            sendTask(messageOrder);
            LogUtil.debug("结束发送通知消息！" + messageOrder.getAccountName());

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
            Task task = taskMapper.selectByPrimaryKey(item.getTaskId());

            if (task == null) {
                return false;
            } else {
                if (StringUtils.equalsIgnoreCase(task.getStatus(), TaskConstants.TASK_STATUS.FINISHED.name())
                || StringUtils.equalsIgnoreCase(task.getStatus(), TaskConstants.TASK_STATUS.WARNING.name())
                || StringUtils.equalsIgnoreCase(task.getStatus(), TaskConstants.TASK_STATUS.ERROR.name())) {
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
        assert systemParameterService != null;
        assert noticeSendService != null;

        String successContext = "success";
        String failedContext = "failed";
        String subject = "安全合规扫描结果";
        String event = NoticeConstants.Event.EXECUTE_SUCCESSFUL;

        List<Task> tasks = extTaskMapper.getTopTasksForEmail(messageOrder);

        for (Task task : tasks) {
            if (task.getReturnSum() == null) {
                sendTask(messageOrder);
                return;
            }
        }

        int returnSum = extTaskMapper.getReturnSumForEmail(messageOrder);
        int resourcesSum = extTaskMapper.getResourcesSumForEmail(messageOrder);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("resources", tasks);
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

        LogUtil.debug("开始添加站内消息！" + messageOrder.getAccountName());
        WebMsg msg = new WebMsg();
        msg.setStatus(false);
        msg.setType("扫描结果");
        msg.setCreateTime(System.currentTimeMillis());
        msg.setContent("安全合规扫描结果【" + messageOrder.getAccountName() + "】" +  messageOrder.getStatus() + "【 不合规资源/资源总数】" + returnSum  + "/" + resourcesSum);
        webMsgMapper.insertSelective(msg);
        LogUtil.debug("结束添加站内消息！" + messageOrder.getAccountName());
    }
}
