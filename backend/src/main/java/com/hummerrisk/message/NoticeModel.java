package com.hummerrisk.message;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public class NoticeModel {
    /**
     * 保存 资源id
     */
    private String resourceId;
    /**
     * 保存状态
     */
    private String status;
    /**
     * Event
     */
    private String event;
    /**
     * 消息主题
     */
    private String subject;
    /**
     * 消息内容
     */
    private String context;
    private String successContext;
    private String failedContext;

    /**
     * html 消息模版
     */
    private String mailTemplate;
    private String failedMailTemplate;
    private String successMailTemplate;

    /**
     * 保存特殊的用户
     */
    private List<String> relatedUsers;

    /**
     * 模版里的参数信息
     */
    private Map<String, Object> paramMap;
    /**
     * 接收人
     */
    private List<Receiver> receivers;
    /**
     * 抄送人
     */
    private List<Receiver> recipients;
    private List<String> webhookUrls;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getSuccessContext() {
        return successContext;
    }

    public void setSuccessContext(String successContext) {
        this.successContext = successContext;
    }

    public String getFailedContext() {
        return failedContext;
    }

    public void setFailedContext(String failedContext) {
        this.failedContext = failedContext;
    }

    public String getMailTemplate() {
        return mailTemplate;
    }

    public void setMailTemplate(String mailTemplate) {
        this.mailTemplate = mailTemplate;
    }

    public String getFailedMailTemplate() {
        return failedMailTemplate;
    }

    public void setFailedMailTemplate(String failedMailTemplate) {
        this.failedMailTemplate = failedMailTemplate;
    }

    public String getSuccessMailTemplate() {
        return successMailTemplate;
    }

    public void setSuccessMailTemplate(String successMailTemplate) {
        this.successMailTemplate = successMailTemplate;
    }

    public List<String> getRelatedUsers() {
        return relatedUsers;
    }

    public void setRelatedUsers(List<String> relatedUsers) {
        this.relatedUsers = relatedUsers;
    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    public List<Receiver> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<Receiver> receivers) {
        this.receivers = receivers;
    }

    public List<Receiver> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<Receiver> recipients) {
        this.recipients = recipients;
    }

    public List<String> getWebhookUrls() {
        return webhookUrls;
    }

    public void setWebhookUrls(List<String> webhookUrls) {
        this.webhookUrls = webhookUrls;
    }
}
