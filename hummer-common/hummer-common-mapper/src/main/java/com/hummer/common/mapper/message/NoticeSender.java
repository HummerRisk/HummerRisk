package com.hummer.common.mapper.message;

import org.springframework.scheduling.annotation.Async;

public interface NoticeSender {
    @Async
    void send(MessageDetail messageDetail, NoticeModel noticeModel);
}
