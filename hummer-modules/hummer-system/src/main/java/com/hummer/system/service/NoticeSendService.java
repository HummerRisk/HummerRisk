package com.hummer.system.service;

import com.hummer.common.core.constant.NoticeConstants;
import com.hummer.system.message.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NoticeSendService {
    @Autowired
    private MailNoticeSender mailNoticeSender;
    @Autowired
    private WeComNoticeSender weComNoticeSender;
    @Autowired
    private DingNoticeSender dingNoticeSender;
    @Autowired
    private WebhookNoticeSender webhookNoticeSender;
    @Autowired
    private NoticeService noticeService;

    private NoticeSender getNoticeSender(MessageDetail messageDetail) {
        NoticeSender noticeSender = null;
        switch (messageDetail.getType()) {
            case NoticeConstants.Type.EMAIL:
                noticeSender = mailNoticeSender;
                break;
            case NoticeConstants.Type.WECHAT_ROBOT:
                noticeSender = weComNoticeSender;
                break;
            case NoticeConstants.Type.NAIL_ROBOT:
                noticeSender = dingNoticeSender;
                break;
            case NoticeConstants.Type.WEBHOOK:
                noticeSender = webhookNoticeSender;
                break;
            default:
                break;
        }

        return noticeSender;
    }

    public void send(NoticeModel noticeModel) {
        List<MessageDetail> messageDetails = noticeService.searchMessageByType(NoticeConstants.TaskType.RESOURCE_TASK);
        messageDetails.forEach(messageDetail -> {
            if (StringUtils.equals(messageDetail.getEvent(), noticeModel.getEvent())) {
                this.getNoticeSender(messageDetail).send(messageDetail, noticeModel);
            }
        });
    }
}
