package com.hummer.common.core.domain;

import org.springframework.core.io.InputStreamSource;

public class MailAttachmentInfo {
    private String attachmentName;

    private InputStreamSource inputStreamSource;

    public MailAttachmentInfo() {
    }

    public MailAttachmentInfo(String attachmentName, InputStreamSource inputStreamSource) {
        this.attachmentName = attachmentName;
        this.inputStreamSource = inputStreamSource;
    }


    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public InputStreamSource getInputStreamSource() {
        return inputStreamSource;
    }

    public void setInputStreamSource(InputStreamSource inputStreamSource) {
        this.inputStreamSource = inputStreamSource;
    }
}
