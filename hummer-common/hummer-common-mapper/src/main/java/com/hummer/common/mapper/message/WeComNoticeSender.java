package com.hummer.common.mapper.message;

import com.hummer.common.core.utils.LogUtil;
import com.hummer.common.mapper.service.WechatService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class WeComNoticeSender extends AbstractNoticeSender {

    @Resource
    private WechatService wechatService;

    public void sendWechatRobot(MessageDetail messageDetail, String context) {
        List<String> userIds = messageDetail.getUserIds();
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        List<String> wechatLists = super.getUserWechats(userIds);
        try {
            wechatService.sendTextMessageToUser(context, wechatLists);
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
        }
    }

    @Override
    public void send(MessageDetail messageDetail, NoticeModel noticeModel) {
        String context = super.getContext(messageDetail, noticeModel);
        sendWechatRobot(messageDetail, context);
    }
}
