package com.hummer.system.message;

import com.hummer.common.core.utils.LogUtil;
import com.hummer.system.service.DingtalkService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DingNoticeSender extends AbstractNoticeSender {

    @Autowired
    private DingtalkService dingtalkService;

    public void sendNailRobot(MessageDetail messageDetail, String context) {
        List<String> userIds = messageDetail.getUserIds();
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        List<String> phoneList = super.getUserPhones(userIds);
        LogUtil.info("收件人地址: " + phoneList);
        try {
            LogUtil.info("Send dingtalk：" + phoneList);
            dingtalkService.sendTextMessageToUser(context, phoneList);
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
        }
    }

    @Override
    public void send(MessageDetail messageDetail, NoticeModel noticeModel) {
        String context = super.getContext(messageDetail, noticeModel);
        sendNailRobot(messageDetail, context);
    }
}
