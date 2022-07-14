package com.hummerrisk.controller;

import com.hummerrisk.controller.handler.annotation.I18n;
import com.hummerrisk.message.MessageDetail;
import com.hummerrisk.service.NoticeService;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;

@ApiIgnore
@RestController
@RequestMapping("notice")
public class NoticeController {
    @Resource
    private NoticeService noticeService;

    @PostMapping("save/message/task")
    public void saveMessage(@RequestBody MessageDetail messageDetail) {
        noticeService.saveMessageTask(messageDetail);
    }

    @I18n
    @GetMapping("/search/message/type/{type}")
    public List<MessageDetail> searchMessage(@PathVariable String type) {
        return noticeService.searchMessageByType(type);
    }

    @I18n
    @GetMapping("/search/message/{resourceId}")
    public List<MessageDetail> searchMessageSchedule(@PathVariable String resourceId) {
        return noticeService.searchMessageByResourceId();
    }

    @GetMapping("/delete/message/{identification}")
    public int deleteMessage(@PathVariable String identification) {
        return noticeService.delMessage(identification);
    }
}

