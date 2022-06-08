package io.hummerrisk.message;

import io.hummerrisk.commons.utils.LogUtil;
import io.hummerrisk.service.DingtalkService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class DingNoticeSender extends AbstractNoticeSender {

    @Resource
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
