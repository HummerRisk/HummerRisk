package com.hummer.system.service;

import com.hummer.common.core.constant.NoticeConstants;
import com.hummer.common.core.constant.ScanConstants;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.exception.HRException;
import com.hummer.common.core.utils.UUIDUtil;
import com.hummer.common.core.i18n.Translator;
import com.hummer.system.mapper.MessageOrderItemMapper;
import com.hummer.system.mapper.MessageOrderMapper;
import com.hummer.system.mapper.MessageTaskMapper;
import com.hummer.system.message.MessageDetail;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class NoticeService {
    @Autowired
    private MessageTaskMapper messageTaskMapper;
    @Autowired
    private MessageOrderMapper messageOrderMapper;
    @Autowired
    private MessageOrderItemMapper messageOrderItemMapper;

    public void saveMessageTask(MessageDetail messageDetail) {
        MessageTaskExample example = new MessageTaskExample();
        example.createCriteria().andIdentificationEqualTo(messageDetail.getIdentification());
        List<MessageTaskWithBLOBs> messageTaskLists = messageTaskMapper.selectByExampleWithBLOBs(example);
        if (!messageTaskLists.isEmpty()) {
            delMessage(messageDetail.getIdentification());
        }
        long time = System.currentTimeMillis();
        String identification = messageDetail.getIdentification();
        if (StringUtils.isBlank(identification)) {
            identification = UUID.randomUUID().toString();
        }
        for (String userId : messageDetail.getUserIds()) {
            checkUserIdExist(userId, messageDetail);
            MessageTaskWithBLOBs messageTask = new MessageTaskWithBLOBs();
            messageTask.setEvent(messageDetail.getEvent());
            messageTask.setTaskType(messageDetail.getTaskType());
            messageTask.setUserId(userId);
            messageTask.setType(messageDetail.getType());
            messageTask.setIdentification(identification);
            messageTask.setIsSet(false);
            messageTask.setCreateTime(time);
            setTemplate(messageDetail, messageTask);
            messageTaskMapper.insertSelective(messageTask);
        }
    }

    private void setTemplate(MessageDetail messageDetail, MessageTaskWithBLOBs messageTask) {
        if (StringUtils.isNotBlank(messageDetail.getTemplate())) {
            messageTask.setTemplate(messageDetail.getTemplate());
        }
        if (StringUtils.isNotBlank(messageDetail.getTextTemplate())) {
            messageTask.setTextTemplate(messageDetail.getTextTemplate());
        }
    }

    private void checkUserIdExist(String userId, MessageDetail list) {
        MessageTaskExample example = new MessageTaskExample();
        example.createCriteria()
                .andUserIdEqualTo(userId)
                .andEventEqualTo(list.getEvent())
                .andTypeEqualTo(list.getType())
                .andTaskTypeEqualTo(list.getTaskType());
        if (messageTaskMapper.countByExample(example) > 0) {
            HRException.throwException(Translator.get("message_task_already_exists"));
        }
    }

    public List<MessageDetail> searchMessageByResourceId() {
        MessageTaskExample example = new MessageTaskExample();
        List<MessageTaskWithBLOBs> messageTaskLists = messageTaskMapper.selectByExampleWithBLOBs(example);
        List<MessageDetail> scheduleMessageTask = new ArrayList<>();
        Map<String, List<MessageTaskWithBLOBs>> MessageTaskMap = messageTaskLists.stream().collect(Collectors.groupingBy(MessageTask::getIdentification));
        MessageTaskMap.forEach((k, v) -> {
            MessageDetail messageDetail = getMessageDetail(v);
            scheduleMessageTask.add(messageDetail);
        });
        scheduleMessageTask.sort(Comparator.comparing(MessageDetail::getCreateTime, Comparator.nullsLast(Long::compareTo)).reversed());
        return scheduleMessageTask;
    }

    public List<MessageDetail> searchMessageByType(String type) {
        List<MessageDetail> messageDetails = new ArrayList<>();

        MessageTaskExample example = new MessageTaskExample();
        example.createCriteria().andTaskTypeEqualTo(type);
        List<MessageTaskWithBLOBs> messageTaskLists = messageTaskMapper.selectByExampleWithBLOBs(example);

        Map<String, List<MessageTaskWithBLOBs>> messageTaskMap = messageTaskLists.stream()
                .collect(Collectors.groupingBy(NoticeService::fetchGroupKey));
        messageTaskMap.forEach((k, v) -> {
            MessageDetail messageDetail = getMessageDetail(v);
            messageDetails.add(messageDetail);
        });

        return messageDetails.stream()
                .sorted(Comparator.comparing(MessageDetail::getCreateTime, Comparator.nullsLast(Long::compareTo)).reversed())
                .collect(Collectors.toList())
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private MessageDetail getMessageDetail(List<MessageTaskWithBLOBs> messageTasks) {
        Set<String> userIds = new HashSet<>();

        MessageDetail messageDetail = new MessageDetail();
        for (MessageTaskWithBLOBs m : messageTasks) {
            userIds.add(m.getUserId());
            messageDetail.setEvent(m.getEvent());
            messageDetail.setTaskType(m.getTaskType());
            messageDetail.setIdentification(m.getIdentification());
            messageDetail.setType(m.getType());
            messageDetail.setIsSet(m.getIsSet());
            messageDetail.setCreateTime(m.getCreateTime());
            messageDetail.setTemplate(m.getTemplate());
            messageDetail.setTextTemplate(m.getTextTemplate());
        }
        if (CollectionUtils.isNotEmpty(userIds)) {
            messageDetail.setUserIds(new ArrayList<>(userIds));
        }
        return messageDetail;
    }

    private static String fetchGroupKey(MessageTask messageTask) {
        return messageTask.getTaskType() + "#" + messageTask.getIdentification();
    }

    public int delMessage(String identification) {
        MessageTaskExample example = new MessageTaskExample();
        example.createCriteria().andIdentificationEqualTo(identification);
        return messageTaskMapper.deleteByExample(example);
    }

    public String createMessageOrder(Account account) {
        MessageOrder messageOrder = new MessageOrder();
        String uuid = UUIDUtil.newUUID();
        messageOrder.setId(uuid);
        messageOrder.setAccountId(account.getId());
        messageOrder.setAccountName(account.getName());
        messageOrder.setCreateTime(System.currentTimeMillis());
        messageOrder.setStatus(NoticeConstants.MessageOrderStatus.PROCESSING);
        messageOrder.setScanType(ScanConstants.SCAN_TYPE.CLOUD.name());
        messageOrderMapper.insertSelective(messageOrder);
        return uuid;
    }

    public void createMessageOrderItem(String messageOrderId, CloudTask cloudTask) {
        MessageOrderItem messageOrderItem = new MessageOrderItem();
        messageOrderItem.setMessageOrderId(messageOrderId);
        messageOrderItem.setTaskId(cloudTask.getId());
        messageOrderItem.setTaskName(cloudTask.getTaskName());
        messageOrderItem.setCreateTime(System.currentTimeMillis());
        messageOrderItem.setStatus(NoticeConstants.MessageOrderStatus.PROCESSING);
        messageOrderItemMapper.insertSelective(messageOrderItem);
    }

    public List<MessageTask> messageTaskList (String eventName) {
        MessageTaskExample example = new MessageTaskExample();
        example.createCriteria().andEventEqualTo(eventName);
        return messageTaskMapper.selectByExample(example);
    }


}
